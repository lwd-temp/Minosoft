package de.bixilon.minosoft.config.profile.profiles.entity

import com.google.common.collect.HashBiMap
import de.bixilon.minosoft.config.profile.GlobalProfileManager
import de.bixilon.minosoft.config.profile.ProfileManager
import de.bixilon.minosoft.modding.event.master.GlobalEventMaster
import de.bixilon.minosoft.util.KUtil.toResourceLocation
import de.bixilon.minosoft.util.KUtil.unsafeCast
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import java.util.concurrent.locks.ReentrantLock

object EntityProfileManager : ProfileManager<EntityProfile> {
    override val namespace = "minosoft:entity".toResourceLocation()
    override val latestVersion = 1
    override val saveLock = ReentrantLock()
    override val profileClass = EntityProfile::class.java
    override val icon = FontAwesomeSolid.SKULL


    override var currentLoadingPath: String? = null
    override val profiles: HashBiMap<String, EntityProfile> = HashBiMap.create()

    override var selected: EntityProfile = null.unsafeCast()
        set(value) {
            field = value
            GlobalProfileManager.selectProfile(this, value)
            GlobalEventMaster.fireEvent(EntityProfileSelectEvent(value))
        }

    override fun createProfile(name: String, description: String?): EntityProfile {
        currentLoadingPath = name
        val profile = EntityProfile(description ?: "Default entity profile")
        currentLoadingPath = null
        profiles[name] = profile

        return profile
    }
}