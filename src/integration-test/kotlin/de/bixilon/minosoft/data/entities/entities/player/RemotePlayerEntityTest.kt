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

package de.bixilon.minosoft.data.entities.entities.player

import de.bixilon.kotlinglm.vec3.Vec3d
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.data.entities.data.EntityData
import de.bixilon.minosoft.data.entities.entities.player.additional.PlayerAdditional
import de.bixilon.minosoft.gui.rendering.util.vec.vec3.Vec3dUtil.EMPTY
import de.bixilon.minosoft.physics.entities.living.player.PlayerPhysics
import de.bixilon.minosoft.protocol.network.connection.play.ConnectionTestUtil.createConnection
import de.bixilon.minosoft.test.IT
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

@Test(groups = ["entities"])
class RemotePlayerEntityTest {

    private fun create(): RemotePlayerEntity {
        val connection = createConnection()
        return RemotePlayerEntity(connection, IT.REGISTRIES.entityType[PlayerEntity]!!, EntityData(connection), Vec3d.EMPTY, EntityRotation.EMPTY, PlayerAdditional("Dummy")).apply { init() }
    }

    fun `check correct physics`() {
        val player = create()
        assertTrue(player.physics is PlayerPhysics)
    }
}
