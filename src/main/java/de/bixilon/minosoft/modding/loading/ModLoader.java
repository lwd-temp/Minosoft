/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.modding.loading;

import de.bixilon.minosoft.Config;
import de.bixilon.minosoft.Minosoft;
import de.bixilon.minosoft.logging.Log;
import de.bixilon.minosoft.modding.MinosoftMod;
import de.bixilon.minosoft.util.CountUpAndDownLatch;
import de.bixilon.minosoft.util.Util;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.zip.ZipFile;

public class ModLoader {
    static final LinkedList<MinosoftMod> mods = new LinkedList<>();

    public static void loadMods(CountUpAndDownLatch progress) throws Exception {
        Log.verbose("Start loading mods...");
        // load all jars, parse the mod.json
        // sort the list and prioritize
        // load all lists and dependencies async
        HashSet<Callable<MinosoftMod>> callables = new HashSet<>();
        File[] files = new File(Config.homeDir + "mods").listFiles();
        if (files == null) {
            // no mods to load
            return;
        }
        for (File modFile : files) {
            if (modFile.isDirectory()) {
                continue;
            }
            callables.add(() -> {
                MinosoftMod mod = loadMod(progress, modFile);
                if (mod != null) {
                    mods.add(mod);
                }
                return mod;
            });
        }

        Util.executeInThreadPool("ModLoader", callables);

        progress.addCount(mods.size() * ModPhases.values().length); // count * mod phases

        mods.sort((a, b) -> {
            if (a == null || b == null) {
                return 0;
            }
            return -(getLoadingPriorityOrDefault(b.getInfo()).ordinal() - getLoadingPriorityOrDefault(a.getInfo()).ordinal());
        });
        for (ModPhases phase : ModPhases.values()) {
            Log.verbose(String.format("Map loading phase changed: %s", phase));
            HashSet<Callable<MinosoftMod>> phaseLoaderCallables = new HashSet<>();
            mods.forEach((instance) -> phaseLoaderCallables.add(() -> {
                if (!instance.isEnabled()) {
                    return instance;
                }
                if (!instance.start(phase)) {
                    Log.warn(String.format("An error occurred while loading %s", instance.getInfo()));
                    instance.setEnabled(false);
                }
                progress.countDown();
                return instance;
            }));
            Util.executeInThreadPool("ModLoader", phaseLoaderCallables);
        }
        mods.forEach((instance) -> {
            if (instance.isEnabled()) {
                Minosoft.eventManagers.add(instance.getEventManager());
            } else {
                mods.remove(instance);
                Log.warn(String.format("An error occurred while loading %s", instance.getInfo()));
            }
        });
        Log.verbose("Loading all mods finished!");
    }

    public static MinosoftMod loadMod(CountUpAndDownLatch progress, File file) {
        try {
            Log.verbose(String.format("[MOD] Loading file %s", file.getAbsolutePath()));
            progress.countUp();
            ZipFile zipFile = new ZipFile(file);
            ModInfo modInfo = new ModInfo(Util.readJsonFromZip("mod.json", zipFile));
            if (isModLoaded(modInfo)) {
                Log.warn(String.format("Mod %s:%d (uuid=%s) is loaded multiple times! Skipping", modInfo.getName(), modInfo.getVersionId(), modInfo.getUUID()));
                return null;
            }
            JarClassLoader jcl = new JarClassLoader();
            jcl.add(file.getAbsolutePath());
            JclObjectFactory factory = JclObjectFactory.getInstance();

            MinosoftMod instance = (MinosoftMod) factory.create(jcl, modInfo.getMainClass());
            instance.setInfo(modInfo);
            Log.verbose(String.format("[MOD] Mod file loaded and added to classpath (%s)", modInfo));
            zipFile.close();
            progress.countDown();
            return instance;
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn(String.format("Could not load mod: %s", file.getAbsolutePath()));
        }
        progress.countDown(); // failed
        return null;
    }

    private static Priorities getLoadingPriorityOrDefault(ModInfo info) {
        if (info.getLoadingInfo() != null && info.getLoadingInfo().getLoadingPriority() != null) {
            return info.getLoadingInfo().getLoadingPriority();
        }
        return Priorities.NORMAL;
    }

    static boolean isModLoaded(ModInfo info) {
        for (MinosoftMod instance : mods) {
            if (instance.getInfo().getUUID().equals(info.getUUID())) {
                return true;
            }
        }
        return false;
    }
}