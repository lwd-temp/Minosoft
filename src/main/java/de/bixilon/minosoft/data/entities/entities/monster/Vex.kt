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
package de.bixilon.minosoft.data.entities.entities.monster

import de.bixilon.minosoft.data.entities.EntityDataFields
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.data.entities.entities.SynchronizedEntityData
import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.data.registries.entities.EntityFactory
import de.bixilon.minosoft.data.registries.entities.EntityType
import de.bixilon.minosoft.protocol.network.connection.play.PlayConnection
import glm_.vec3.Vec3d

class Vex(connection: PlayConnection, entityType: EntityType, position: Vec3d, rotation: EntityRotation) : Monster(connection, entityType, position, rotation) {

    private fun getVexFlag(bitMask: Int): Boolean {
        return data.sets.getBitMask(EntityDataFields.VEX_FLAGS, bitMask)
    }

    @get:SynchronizedEntityData(name = "Is attacking")
    val isAttacking: Boolean
        get() = getVexFlag(0x01)


    companion object : EntityFactory<Vex> {
        override val RESOURCE_LOCATION: ResourceLocation = ResourceLocation("vex")

        override fun build(connection: PlayConnection, entityType: EntityType, position: Vec3d, rotation: EntityRotation): Vex {
            return Vex(connection, entityType, position, rotation)
        }
    }
}
