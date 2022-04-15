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
package de.bixilon.minosoft.data.entities.entities.projectile

import de.bixilon.minosoft.data.entities.EntityDataFields
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.data.entities.entities.SynchronizedEntityData
import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.data.registries.entities.EntityFactory
import de.bixilon.minosoft.data.registries.entities.EntityType
import de.bixilon.minosoft.protocol.network.connection.play.PlayConnection
import glm_.vec3.Vec3d

class FishingBobber(connection: PlayConnection, entityType: EntityType, position: Vec3d, rotation: EntityRotation) : Projectile(connection, entityType, position, rotation) {

    @get:SynchronizedEntityData(name = "Hooked entity id")
    val hookedEntityId: Int
        get() = data.sets.getInt(EntityDataFields.FISHING_HOOK_HOOKED_ENTITY)

    @get:SynchronizedEntityData(name = "Is catchable")
    val isCatchable: Boolean
        get() = data.sets.getBoolean(EntityDataFields.FISHING_HOOK_CATCHABLE)


    companion object : EntityFactory<FishingBobber> {
        override val RESOURCE_LOCATION: ResourceLocation = ResourceLocation("fishing_bobber")

        override fun build(connection: PlayConnection, entityType: EntityType, position: Vec3d, rotation: EntityRotation): FishingBobber {
            return FishingBobber(connection, entityType, position, rotation)
        }
    }
}
