/*
 * Minosoft
 * Copyright (C) 2020-2022 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft

import de.bixilon.kutil.latch.CountUpAndDownLatch
import de.bixilon.minosoft.assets.properties.version.AssetsVersionProperties
import de.bixilon.minosoft.config.profile.profiles.resources.ResourcesProfile
import de.bixilon.minosoft.config.profile.profiles.resources.ResourcesProfileManager
import de.bixilon.minosoft.data.registries.DefaultRegistries
import de.bixilon.minosoft.data.registries.versions.Versions
import de.bixilon.minosoft.protocol.packets.factory.PacketTypeRegistry
import de.bixilon.minosoft.util.logging.Log
import de.bixilon.minosoft.util.logging.LogLevels
import de.bixilon.minosoft.util.logging.LogMessageType
import org.testng.annotations.BeforeGroups
import org.testng.annotations.BeforeTest


internal object MinosoftSIT {

    @BeforeTest
    @BeforeGroups(groups = ["block"])
    fun setup() {
        disableGC()
        initAssetsManager()
        setupPacketRegistry()
        loadVersionsJson()
        loadAssetsProperties()
        loadDefaultRegistries()
        loadPixlyzerData()
        Log.log(LogMessageType.OTHER, LogLevels.INFO) { "Integration tests setup successfully!" }
    }


    fun disableGC() {
        Thread {
            val references = IT.references
            // basically while (true)
            for (i in 0 until Int.MAX_VALUE) {
                Thread.sleep(100000L)
            }
            references.hashCode()
        }.start()
    }

    fun initAssetsManager() {
        Minosoft.MINOSOFT_ASSETS_MANAGER.load(CountUpAndDownLatch(0))
    }

    fun setupPacketRegistry() {
        PacketTypeRegistry.init(CountUpAndDownLatch(0))
    }

    fun loadVersionsJson() {
        Versions.load(CountUpAndDownLatch(0))
    }

    fun loadAssetsProperties() {
        AssetsVersionProperties.load(CountUpAndDownLatch(0))
    }

    fun loadDefaultRegistries() {
        DefaultRegistries.load(CountUpAndDownLatch(0))
    }

    private fun createResourcesProfile(): ResourcesProfile {
        ResourcesProfileManager.currentLoadingPath = "dummy"
        val profile = ResourcesProfile()
        ResourcesProfileManager.currentLoadingPath = null
        return profile
    }

    fun loadPixlyzerData() {
        val version = Versions[IT.VERSION_NAME]!!
        IT.VERSION = version

        version.load(createResourcesProfile(), CountUpAndDownLatch(0))
    }
}