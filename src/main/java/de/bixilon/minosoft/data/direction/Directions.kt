/*
 * Minosoft
 * Copyright (C) 2021 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.direction

import de.bixilon.minosoft.data.Axes
import de.bixilon.minosoft.data.registries.blocks.properties.serializer.BlockPropertiesSerializer
import de.bixilon.minosoft.gui.rendering.block.models.BlockModelElement
import de.bixilon.minosoft.gui.rendering.block.models.FaceSize
import de.bixilon.minosoft.gui.rendering.util.VecUtil.get
import de.bixilon.minosoft.util.KUtil
import de.bixilon.minosoft.util.enum.ValuesEnum
import glm_.vec2.Vec2i
import glm_.vec3.Vec3
import glm_.vec3.Vec3d
import glm_.vec3.Vec3i
import kotlin.math.abs

enum class Directions(
    val horizontalId: Int,
    override val vector: Vec3i,
) : AbstractDirection {
    DOWN(-1, Vec3i(0, -1, 0)),
    UP(-1, Vec3i(0, 1, 0)),
    NORTH(2, Vec3i(0, 0, -1)),
    SOUTH(0, Vec3i(0, 0, 1)),
    WEST(1, Vec3i(-1, 0, 0)),
    EAST(3, Vec3i(1, 0, 0));

    override val vectorf = Vec3(vector)
    override val vectord = Vec3d(vector)

    val axis: Axes get() = Axes.get(this)

    lateinit var inverted: Directions
        private set

    private fun inverse(): Directions {
        val ordinal = ordinal
        return if (ordinal % 2 == 0) {
            byId(ordinal + 1)
        } else {
            byId(ordinal - 1)
        }
    }

    fun sidesNextTo(direction: Directions): Set<Directions> {
        return when (direction) {
            NORTH, SOUTH -> setOf(EAST, WEST)
            EAST, WEST -> setOf(NORTH, SOUTH)
            else -> emptySet()
        }
    }

    /**
     * @return the size of the face in this direction. null if the face is not touching the border (determinated by the block resolution)
     */
    fun getFaceBorderSizes(start: Vec3, end: Vec3): FaceSize? {
        // check if face is touching the border of a block

        if (!isBlockResolutionBorder(start, end)) {
            return null
        }
        return getFaceSize(start, end)
    }

    fun getFaceSize(start: Vec3, end: Vec3): FaceSize {
        return when (this) {
            DOWN, UP -> FaceSize(Vec2i(start.x, start.z), Vec2i(end.x, end.z))
            NORTH, SOUTH -> FaceSize(Vec2i(start.x, start.y), Vec2i(end.x, end.y))
            EAST, WEST -> FaceSize(Vec2i(start.y, start.z), Vec2i(end.y, end.z))
        }
    }

    private fun isBlockResolutionBorder(start: Vec3, end: Vec3): Boolean {
        return isCoordinateBorder(vector.x, start.x, end.x) || isCoordinateBorder(vector.y, start.y, end.y) || isCoordinateBorder(vector.z, start.z, end.z)
    }

    private fun isCoordinateBorder(directionValue: Int, start: Float, end: Float): Boolean {
        if (directionValue == 1) {
            return start == BlockModelElement.BLOCK_RESOLUTION_FLOAT || end == BlockModelElement.BLOCK_RESOLUTION_FLOAT
        }
        if (directionValue == -1) {
            return start == 0.0f || end == 0.0f
        }
        return false
    }

    operator fun get(axis: Axes): Int {
        return vector[axis]
    }

    fun rotateYC(): Directions {
        return when (this) {
            NORTH -> EAST
            SOUTH -> WEST
            WEST -> NORTH
            EAST -> SOUTH
            else -> TODO()
        }
    }


    companion object : BlockPropertiesSerializer, ValuesEnum<Directions> {
        override val VALUES = values()
        override val NAME_MAP: Map<String, Directions> = KUtil.getEnumValues(VALUES)
        val SIDES = arrayOf(NORTH, SOUTH, WEST, EAST)
        val PRIORITY_SIDES = arrayOf(WEST, EAST, NORTH, SOUTH)
        private val HORIZONTAL = arrayOf(SOUTH, WEST, NORTH, EAST)

        override fun deserialize(value: Any): Directions {
            return NAME_MAP[value] ?: throw IllegalArgumentException("No such property: $value")
        }

        @JvmStatic
        fun byId(id: Int): Directions {
            return VALUES[id]
        }

        private const val MIN_ERROR = 0.0001f

        fun byDirection(direction: Vec3): Directions {
            var minDirection = VALUES[0]
            var minError = 2.0f
            for (testDirection in VALUES) {
                val error = (testDirection.vectorf - direction).length()
                if (error < MIN_ERROR) {
                    return testDirection
                } else if (error < minError) {
                    minError = error
                    minDirection = testDirection
                }
            }
            return minDirection
        }

        fun byDirection(direction: Vec3d): Directions {
            return byDirection(Vec3(direction))
        }

        fun byHorizontal(value: Int): Directions {
            return HORIZONTAL[abs(value % HORIZONTAL.size)]
        }


        init {
            for (direction in VALUES) {
                direction.inverted = direction.inverse()
            }
        }
    }
}