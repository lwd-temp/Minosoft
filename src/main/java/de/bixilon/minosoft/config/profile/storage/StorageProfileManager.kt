/*
 * Minosoft
 * Copyright (C) 2020-2023 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.config.profile.storage

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.node.ObjectNode
import de.bixilon.kutil.cast.CastUtil.nullCast
import de.bixilon.kutil.cast.CastUtil.unsafeCast
import de.bixilon.kutil.cast.CastUtil.unsafeNull
import de.bixilon.kutil.collections.CollectionUtil.mutableBiMapOf
import de.bixilon.kutil.collections.map.bi.AbstractMutableBiMap
import de.bixilon.kutil.concurrent.lock.simple.SimpleLock
import de.bixilon.kutil.exception.Broken
import de.bixilon.kutil.file.watcher.FileWatcherService
import de.bixilon.kutil.observer.DataObserver.Companion.observed
import de.bixilon.kutil.observer.map.bi.BiMapObserver.Companion.observedBiMap
import de.bixilon.minosoft.assets.util.FileUtil.mkdirParent
import de.bixilon.minosoft.config.profile.ProfileType
import de.bixilon.minosoft.config.profile.profiles.Profile
import de.bixilon.minosoft.config.profile.storage.ProfileIOUtil.isValidName
import de.bixilon.minosoft.data.registries.identified.Identified
import de.bixilon.minosoft.gui.eros.crash.ErosCrashReport.Companion.crash
import de.bixilon.minosoft.protocol.ProtocolUtil.encodeNetwork
import de.bixilon.minosoft.terminal.RunConfiguration
import de.bixilon.minosoft.util.json.Jackson
import de.bixilon.minosoft.util.logging.Log
import de.bixilon.minosoft.util.logging.LogLevels
import de.bixilon.minosoft.util.logging.LogMessageType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY


abstract class StorageProfileManager<P : Profile> : Iterable<P>, Identified {
    private val jacksonType by lazy { Jackson.MAPPER.typeFactory.constructType(type.clazz) }
    private val reader by lazy { Jackson.MAPPER.readerFor(jacksonType) }
    override val identifier get() = type.identifier


    abstract val latestVersion: Int
    abstract val type: ProfileType<P>


    private val lock = SimpleLock()
    val profiles: AbstractMutableBiMap<String, P> by observedBiMap(mutableBiMapOf())
    var selected: P by observed(unsafeNull())


    open fun migrate(version: Int, data: ObjectNode) = Unit
    open fun migrate(data: ObjectNode): Int {
        val version = data["version"]?.intValue() ?: throw IllegalArgumentException("Data has no version set!")
        when {
            version == latestVersion -> return -1
            version > latestVersion -> throw IllegalArgumentException("Profile was created with a newer version!")
            version < latestVersion -> {
                for (version in version until latestVersion) {
                    migrate(version, data)
                }
                return version
            }
        }
        Broken()
    }

    private fun createDefault() {
        val default = create(DEFAULT_NAME)
        selected = default
    }

    private fun load(name: String, path: File): P {
        val stream = FileInputStream(path)
        val content = Jackson.MAPPER.readTree(stream).unsafeCast<ObjectNode>()
        stream.close()
        val storage = FileStorage(name, this, path)
        Log.log(LogMessageType.PROFILES, LogLevels.VERBOSE) { "Loading profile from $path" }
        return load(storage, content)
    }

    open fun load() {
        val root = RunConfiguration.CONFIG_DIRECTORY.resolve(identifier.namespace).resolve(identifier.path).toFile()
        if (!root.exists()) {
            root.mkdirs()
            return createDefault()
        }
        var selected = DEFAULT_NAME // root.resolve("selected") TODO
        if (!selected.isValidName()) selected = DEFAULT_NAME
        val files = root.listFiles() ?: return createDefault()

        for (file in files) {
            if (!file.name.endsWith(".json")) continue
            val name = file.name.removeSuffix(".json")
            if (!name.isValidName()) {
                Log.log(LogMessageType.PROFILES, LogLevels.WARN) { "Not loading $file: Invalid name!" }
                continue
            }
            lock.lock()
            try {
                val profile = load(name, file)
                profiles[name] = profile
            } catch (error: Throwable) {
                Log.log(LogMessageType.PROFILES, LogLevels.FATAL) { error }
                error.crash()
            } finally {
                lock.unlock()
            }
        }

        this.selected = this[selected] ?: create(selected)

        observe(root.toPath())
    }

    private fun observe(root: Path) {
        FileWatcherService.watchAsync(root, setOf(ENTRY_MODIFY, ENTRY_CREATE)) { _, path ->
            val filename = path.fileName.toString()
            if (!filename.endsWith(".json")) return@watchAsync
            val profile = this[filename.removeSuffix(".json")] ?: return@watchAsync
            val storage = profile.storage?.nullCast<FileStorage>() ?: return@watchAsync
            if (storage.path.toPath() != path) return@watchAsync
            ProfileIOManager.reload(storage)
        }
    }

    fun load(storage: FileStorage, data: ObjectNode): P {
        val profile = type.create(storage)
        storage.profile = profile
        update(profile, data)
        return profile
    }

    fun update(profile: P, data: ObjectNode) {
        val storage = profile.storage.nullCast<FileStorage>() ?: throw IllegalArgumentException("Storage not set!")
        val migrated = migrate(data)
        if (migrated >= 0) {
            Log.log(LogMessageType.PROFILES, LogLevels.INFO) { "Profile ${storage.name} (type=$identifier) was migrated from version $migrated to $latestVersion" }
            storage.invalidate()
        }
        profile.lock.lock()
        storage.updating = true

        val injectable = InjectableValues.Std()
        injectable.addValue(type.clazz, profile)
        reader
            .withValueToUpdate(profile)
            .with(injectable)
            .readValue<P>(data)

        storage.updating = false
        // storage.invalid = false
        profile.lock.unlock()
    }

    fun create(name: String): P {
        if (!name.isValidName()) throw IllegalArgumentException("Invalid profile name!")
        val path = RunConfiguration.CONFIG_DIRECTORY.resolve(identifier.namespace).resolve(identifier.path).resolve("$name.json")
        val storage = FileStorage(name, this, path.toFile())
        val profile = type.create(storage)
        storage.profile = profile
        this.profiles[name] = profile

        storage.invalidate()

        return profile
    }

    fun save(profile: P) {
        val storage = profile.storage?.nullCast<FileStorage>() ?: throw IllegalArgumentException("Storage unset!")
        if (!storage.invalid) return
        val path = storage.path
        path.mkdirParent()

        Log.log(LogMessageType.PROFILES, LogLevels.VERBOSE) { "Saving profile to $path" }
        profile.lock.acquire()
        storage.saved++
        val node = Jackson.MAPPER.valueToTree<ObjectNode>(profile) // TODO: cache jacksonType
        node.put("version", latestVersion)
        val string = Jackson.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node)
        val stream = FileOutputStream(path)
        stream.write(string.encodeNetwork())
        stream.close()

        storage.invalid = false

        profile.lock.release()
    }

    fun delete(name: String) {
        delete(this[name] ?: return)
    }

    fun delete(profile: P) {
        val storage = profile.storage?.nullCast<FileStorage>() ?: throw IllegalArgumentException("Not storage set!")
        lock.lock()
        profile.storage = null
        this.profiles.remove(storage.name)
        lock.unlock()
        ProfileIOManager.delete(storage)
    }

    operator fun get(name: String): P? {
        lock.acquire()
        val profile = profiles[name]
        lock.release()
        return profile
    }

    override fun iterator(): Iterator<P> {
        return profiles.values.iterator()
    }

    fun init() {
        reader
    }

    companion object {
        const val DEFAULT_NAME = "Default"
    }
}
