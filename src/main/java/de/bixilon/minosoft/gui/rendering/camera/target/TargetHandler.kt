package de.bixilon.minosoft.gui.rendering.camera.target

import de.bixilon.minosoft.data.player.LocalPlayerEntity
import de.bixilon.minosoft.data.registries.VoxelShape
import de.bixilon.minosoft.data.registries.blocks.types.FluidBlock
import de.bixilon.minosoft.gui.rendering.RenderWindow
import de.bixilon.minosoft.gui.rendering.camera.Camera
import de.bixilon.minosoft.gui.rendering.camera.target.targets.BlockTarget
import de.bixilon.minosoft.gui.rendering.camera.target.targets.EntityTarget
import de.bixilon.minosoft.gui.rendering.camera.target.targets.FluidTarget
import de.bixilon.minosoft.gui.rendering.camera.target.targets.GenericTarget
import de.bixilon.minosoft.gui.rendering.util.VecUtil
import de.bixilon.minosoft.gui.rendering.util.VecUtil.floor
import de.bixilon.minosoft.gui.rendering.util.VecUtil.getWorldOffset
import de.bixilon.minosoft.gui.rendering.util.VecUtil.toVec3d
import de.bixilon.minosoft.util.KUtil.decide
import glm_.vec3.Vec3
import glm_.vec3.Vec3d

class TargetHandler(
    private val renderWindow: RenderWindow,
    private var camera: Camera,
) {
    private val connection = renderWindow.connection

    /**
     * Can ba a BlockTarget or an EntityTarget. Not a FluidTarget
     */
    var target: GenericTarget? = null
        private set
    var fluidTarget: FluidTarget? = null
        private set


    fun raycast() {
        val eyePosition = camera.matrixHandler.eyePosition.toVec3d
        val cameraFront = camera.matrixHandler.cameraFront.toVec3d

        target = raycast(eyePosition, cameraFront, blocks = true, fluids = false, entities = true)
        fluidTarget = raycast(eyePosition, cameraFront, blocks = false, fluids = true, entities = false) as FluidTarget?
    }


    private fun raycastEntity(origin: Vec3d, direction: Vec3d): EntityTarget? {
        var currentHit: EntityTarget? = null

        val originF = Vec3(origin)
        for (entity in connection.world.entities) {
            if (entity is LocalPlayerEntity) {
                continue
            }
            if ((entity.cameraPosition - originF).length2() > MAX_ENTITY_DISTANCE) {
                continue
            }
            val target = VoxelShape(entity.cameraAABB).raycast(origin, direction)
            if (!target.hit) {
                continue
            }
            if ((currentHit?.distance ?: Double.MAX_VALUE) < target.distance) {
                continue
            }
            currentHit = EntityTarget(origin + direction * target.distance, target.distance, target.direction, entity)

        }
        return currentHit
    }

    private fun raycast(origin: Vec3d, direction: Vec3d, blocks: Boolean, fluids: Boolean, entities: Boolean): GenericTarget? {
        if (!blocks && !fluids && entities) {
            // only raycast entities
            return raycastEntity(origin, direction)
        }
        val currentPosition = Vec3d(origin)

        fun getTotalDistance(): Double {
            return (origin - currentPosition).length()
        }

        var target: GenericTarget? = null
        for (i in 0..RAYCAST_MAX_STEPS) {
            val blockPosition = currentPosition.floor
            val blockState = connection.world[blockPosition]

            if (blockState == null) {
                currentPosition += direction * (VecUtil.getDistanceToNextIntegerAxisInDirection(currentPosition, direction) + 0.001)
                continue
            }
            val voxelShapeRaycastResult = (blockState.block.getOutlineShape(connection, blockState, blockPosition) + blockPosition + blockPosition.getWorldOffset(blockState.block)).raycast(currentPosition, direction)
            if (voxelShapeRaycastResult.hit) {
                val distance = getTotalDistance()
                currentPosition += direction * voxelShapeRaycastResult.distance
                currentPosition += direction * (VecUtil.getDistanceToNextIntegerAxisInDirection(currentPosition, direction) + 0.001)

                if (blockState.block is FluidBlock) {
                    if (!fluids) {
                        continue
                    }
                    target = FluidTarget(
                        currentPosition,
                        distance,
                        voxelShapeRaycastResult.direction,
                        blockState,
                        blockPosition,
                        blockState.block.fluid,
                    )
                    break
                }

                if (!blocks) {
                    continue
                }
                target = BlockTarget(
                    currentPosition,
                    distance,
                    voxelShapeRaycastResult.direction,
                    blockState,
                    blockPosition,
                )
                break
            } else {
                currentPosition += direction * (VecUtil.getDistanceToNextIntegerAxisInDirection(currentPosition, direction) + 0.001)
            }
        }

        if (entities) {
            val entityRaycastHit = raycastEntity(origin, direction) ?: return target
            target ?: return null
            return (entityRaycastHit.distance < target.distance).decide(entityRaycastHit, target)
        }

        return target
    }

    companion object {
        private const val RAYCAST_MAX_STEPS = 100
        private const val MAX_ENTITY_DISTANCE = 20.0f * 20.0f // length2 does not get the square root
    }
}