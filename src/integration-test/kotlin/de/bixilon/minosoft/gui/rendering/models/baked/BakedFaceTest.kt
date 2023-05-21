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

package de.bixilon.minosoft.gui.rendering.models.baked

import de.bixilon.kutil.collections.primitive.floats.HeapArrayFloatList
import de.bixilon.kutil.reflection.ReflectionUtil.forceSet
import de.bixilon.minosoft.gui.rendering.models.block.state.baked.BakedFace
import de.bixilon.minosoft.gui.rendering.system.base.MeshUtil.buffer
import de.bixilon.minosoft.gui.rendering.system.base.buffer.vertex.PrimitiveTypes
import de.bixilon.minosoft.gui.rendering.system.base.texture.texture.AbstractTexture
import de.bixilon.minosoft.gui.rendering.world.mesh.SingleWorldMesh
import de.bixilon.minosoft.gui.rendering.world.mesh.WorldMesh
import de.bixilon.minosoft.test.IT.OBJENESIS
import de.bixilon.minosoft.util.KUtil.toResourceLocation
import org.testng.Assert.assertEquals
import org.testng.annotations.Test

@Test(groups = ["models"])
class BakedFaceTest {
    private val texture = "block/test"

    private fun texture(): AbstractTexture {
        val manager = BakedModelTestUtil.createTextureManager(texture)
        return manager.staticTextures.createTexture(texture.toResourceLocation())
    }

    private fun singleMesh(): SingleWorldMesh {
        val mesh = OBJENESIS.newInstance(SingleWorldMesh::class.java)
        mesh::quadType.forceSet(PrimitiveTypes.QUAD)
        mesh::order.forceSet(SingleWorldMesh.QUAD_ORDER)

        mesh.data = HeapArrayFloatList(1000)

        mesh::initialCacheSize.forceSet(1000)

        return mesh
    }

    private fun mesh(): WorldMesh {
        val mesh = OBJENESIS.newInstance(WorldMesh::class.java)
        mesh::opaqueMesh.forceSet(singleMesh())

        return mesh

    }

    fun renderFull() {
        val face = BakedFace(floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f), floatArrayOf(-1f, -2f, -3f, -4f, -5f, -6f, -7f, -8f), 1.0f, -1, null, texture())

        val mesh = mesh()

        face.render(floatArrayOf(0.0f, 0.0f, 0.0f), mesh, byteArrayOf(0, 0, 0, 0, 0, 0, 0), null)

        val texture = 0.buffer()
        val lightTint = 0xFFFFFF.buffer()

        val data = mesh.opaqueMesh!!.data.toArray()
        val expected = floatArrayOf(
            0f, 1f, 2f, -7f, -8f, texture, lightTint,
            9f, 10f, 11f, -1f, -2f, texture, lightTint,
            6f, 7f, 8f, -3f, -4f, texture, lightTint,
            3f, 4f, 5f, -5f, -6f, texture, lightTint,
        )


        assertEquals(data, expected)
    }
}
