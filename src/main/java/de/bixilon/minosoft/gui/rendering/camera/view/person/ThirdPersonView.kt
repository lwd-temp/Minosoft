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

package de.bixilon.minosoft.gui.rendering.camera.view.person

import de.bixilon.kotlinglm.vec2.Vec2d
import de.bixilon.kotlinglm.vec3.Vec3
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.gui.rendering.RenderContext
import de.bixilon.minosoft.gui.rendering.camera.Camera
import de.bixilon.minosoft.gui.rendering.camera.view.CameraView
import de.bixilon.minosoft.gui.rendering.input.camera.MovementInput
import de.bixilon.minosoft.gui.rendering.util.VecUtil.toVec3d
import de.bixilon.minosoft.gui.rendering.util.vec.vec3.Vec3Util.EMPTY

// TODO: handle block changes
class ThirdPersonView(override val camera: Camera) : PersonView {
    override val context: RenderContext get() = camera.context

    override var eyePosition: Vec3 = Vec3.EMPTY

    override var rotation = EntityRotation.EMPTY
    override var front = Vec3.EMPTY

    override fun onInput(input: MovementInput, delta: Double) {
        super.onInput(input, delta)
        update(eyePosition, front)
    }


    override fun onMouse(delta: Vec2d) {
        val rotation = super.handleMouse(delta) ?: return
        this.rotation = rotation
        this.front = rotation.front
        update(eyePosition, rotation.front)
    }

    private fun update() {
        val entity = camera.matrixHandler.entity
        this.rotation = entity.rotation
        this.front = rotation.front
        update(entity.eyePosition, front)
    }

    private fun update(position: Vec3, front: Vec3) {
        val cast = camera.targetHandler.raycast(position.toVec3d, (-front).toVec3d, blocks = true, fluids = false, entities = true)
        val distance = cast?.distance?.let { minOf(it, MAX_DISTANCE) } ?: MAX_DISTANCE

        this.eyePosition = position + (-front * distance)
    }

    override fun onAttach(previous: CameraView?) {
        update()
    }

    override fun draw() {
        update()
    }

    companion object {
        const val MAX_DISTANCE = 3.0
    }
}
