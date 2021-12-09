package de.bixilon.minosoft.config.profile.profiles.entity

import de.bixilon.minosoft.config.profile.ProfileManager
import de.bixilon.minosoft.config.profile.profiles.Profile
import de.bixilon.minosoft.config.profile.profiles.entity.EntityProfileManager.delegate
import de.bixilon.minosoft.config.profile.profiles.entity.EntityProfileManager.latestVersion
import de.bixilon.minosoft.config.profile.profiles.entity.hitbox.HitboxC
import de.bixilon.minosoft.util.KUtil.unsafeCast

/**
 * Profile for entity
 */
class EntityProfile(
    description: String? = null,
) : Profile {
    override val manager: ProfileManager<Profile> = EntityProfileManager.unsafeCast()
    override var initializing: Boolean = true
        private set
    override var reloading: Boolean = false
    override var saved: Boolean = true
    override var ignoreNextReload: Boolean = false
    override val version: Int = latestVersion
    override var description by delegate(description ?: "")


    val hitbox = HitboxC()

    override fun toString(): String {
        return EntityProfileManager.getName(this)
    }

    init {
        initializing = false
    }
}