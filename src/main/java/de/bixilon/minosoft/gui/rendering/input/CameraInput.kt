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

package de.bixilon.minosoft.gui.rendering.input

import de.bixilon.kotlinglm.GLM
import de.bixilon.kotlinglm.vec2.Vec2d
import de.bixilon.kotlinglm.vec3.Vec3
import de.bixilon.minosoft.config.key.KeyActions
import de.bixilon.minosoft.config.key.KeyBinding
import de.bixilon.minosoft.config.key.KeyCodes
import de.bixilon.minosoft.data.entities.EntityRotation
import de.bixilon.minosoft.gui.rendering.RenderWindow
import de.bixilon.minosoft.gui.rendering.camera.MatrixHandler
import de.bixilon.minosoft.gui.rendering.input.camera.MovementInput
import de.bixilon.minosoft.gui.rendering.util.vec.vec3.Vec3Util.EMPTY
import de.bixilon.minosoft.util.KUtil.toResourceLocation

class CameraInput(
    private val renderWindow: RenderWindow,
    val matrixHandler: MatrixHandler,
) {
    private val connection = renderWindow.connection
    private val player = connection.player
    private val controlsProfile = connection.profiles.controls

    private val ignoreInput: Boolean
        get() {
            val entity = matrixHandler.entity
            if (entity != player) {
                return true
            }
            if (renderWindow.camera.debugView) {
                return true
            }

            return false
        }

    private fun registerKeyBindings() {
        renderWindow.inputHandler.registerCheckCallback(
            MOVE_SPRINT_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_LEFT_CONTROL),
                ),
            ),
            MOVE_FORWARDS_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_W),
                ),
            ),
            MOVE_BACKWARDS_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_S),
                ),
            ),
            MOVE_LEFT_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_A),
                ),
            ),
            MOVE_RIGHT_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_D),
                ),
            ),
            FLY_UP_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_SPACE),
                ),
            ),
            FLY_DOWN_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_LEFT_SHIFT),
                ),
            ),
            JUMP_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_SPACE),
                ),
            ),
            SNEAK_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.CHANGE to setOf(KeyCodes.KEY_LEFT_SHIFT),
                ),
            ),
            TOGGLE_FLY_KEYBINDING to KeyBinding(
                mapOf(
                    KeyActions.DOUBLE_PRESS to setOf(KeyCodes.KEY_SPACE),
                ),
            ),
        )


        renderWindow.inputHandler.registerKeyCallback(ZOOM_KEYBINDING, KeyBinding(
            mapOf(
                KeyActions.CHANGE to setOf(KeyCodes.KEY_C),
            ),
        )) { matrixHandler.zoom = if (it) 2.0f else 0.0f }
    }

    fun init() {
        registerKeyBindings()
    }

    private fun updateDebugPosition(input: MovementInput, delta: Double) {
        val cameraFront = renderWindow.camera.matrixHandler.debugRotation.front
        val speedMultiplier = if (input.sprinting) 25 else 10
        val movement = Vec3.EMPTY
        if (input.movementForward != 0.0f) {
            movement += cameraFront * input.movementForward
        }
        if (input.movementSideways != 0.0f) {
            val cameraRight = (MatrixHandler.CAMERA_UP_VEC3 cross cameraFront).normalize()
            movement += cameraRight * input.movementSideways
        }
        renderWindow.camera.matrixHandler.debugPosition = renderWindow.camera.matrixHandler.debugPosition + (if (movement.length2() != 0.0f) movement.normalize() else return) * delta * speedMultiplier
    }

    fun update(delta: Double) {
        val input = MovementInput(
            pressingForward = renderWindow.inputHandler.isKeyBindingDown(MOVE_FORWARDS_KEYBINDING),
            pressingBack = renderWindow.inputHandler.isKeyBindingDown(MOVE_BACKWARDS_KEYBINDING),
            pressingLeft = renderWindow.inputHandler.isKeyBindingDown(MOVE_LEFT_KEYBINDING),
            pressingRight = renderWindow.inputHandler.isKeyBindingDown(MOVE_RIGHT_KEYBINDING),
            jumping = renderWindow.inputHandler.isKeyBindingDown(JUMP_KEYBINDING),
            sneaking = renderWindow.inputHandler.isKeyBindingDown(SNEAK_KEYBINDING),
            sprinting = renderWindow.inputHandler.isKeyBindingDown(MOVE_SPRINT_KEYBINDING),
            flyDown = renderWindow.inputHandler.isKeyBindingDown(FLY_DOWN_KEYBINDING),
            flyUp = renderWindow.inputHandler.isKeyBindingDown(FLY_UP_KEYBINDING),
            toggleFlyDown = renderWindow.inputHandler.isKeyBindingDown(TOGGLE_FLY_KEYBINDING),
        )
        if (renderWindow.camera.debugView) {
            updateDebugPosition(input, delta)
        }
        connection.player.input = if (ignoreInput || renderWindow.camera.debugView) MovementInput() else input
    }

    fun mouseCallback(delta: Vec2d) {
        if (renderWindow.camera.debugView) {
            matrixHandler.debugRotation = mouseCallback(delta, matrixHandler.debugRotation)
        } else if (!ignoreInput) {
            player.rotation = mouseCallback(delta, player.rotation)
        }
    }

    private fun mouseCallback(delta: Vec2d, rotation: EntityRotation): EntityRotation {
        delta *= 0.1f * controlsProfile.mouse.sensitivity
        var yaw = delta.x + rotation.yaw
        if (yaw > 180) {
            yaw -= 360
        } else if (yaw < -180) {
            yaw += 360
        }
        yaw %= 180
        val pitch = GLM.clamp(delta.y + rotation.pitch, -89.9, 89.9)
        return EntityRotation(yaw, pitch)
    }

    private companion object {
        private val MOVE_SPRINT_KEYBINDING = "minosoft:move_sprint".toResourceLocation()
        private val MOVE_FORWARDS_KEYBINDING = "minosoft:move_forward".toResourceLocation()
        private val MOVE_BACKWARDS_KEYBINDING = "minosoft:move_backwards".toResourceLocation()
        private val MOVE_LEFT_KEYBINDING = "minosoft:move_left".toResourceLocation()
        private val MOVE_RIGHT_KEYBINDING = "minosoft:move_right".toResourceLocation()

        private val SNEAK_KEYBINDING = "minosoft:move_sneak".toResourceLocation()
        private val JUMP_KEYBINDING = "minosoft:move_jump".toResourceLocation()

        private val TOGGLE_FLY_KEYBINDING = "minosoft:move_toggle_fly".toResourceLocation()
        private val FLY_UP_KEYBINDING = "minosoft:move_fly_up".toResourceLocation()
        private val FLY_DOWN_KEYBINDING = "minosoft:move_fly_down".toResourceLocation()

        private val ZOOM_KEYBINDING = "minosoft:zoom".toResourceLocation()
    }
}
