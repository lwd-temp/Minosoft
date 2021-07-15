/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.world

import de.bixilon.minosoft.data.entities.block.BlockEntity
import de.bixilon.minosoft.data.registries.blocks.BlockState
import de.bixilon.minosoft.data.world.block.entities.ArrayBlockEntityProvider
import de.bixilon.minosoft.data.world.block.entities.BlockEntityProvider
import de.bixilon.minosoft.data.world.block.entities.MapBlockEntityProvider
import de.bixilon.minosoft.protocol.network.connection.PlayConnection
import de.bixilon.minosoft.protocol.protocol.ProtocolDefinition
import glm_.vec3.Vec3i

/**
 * Collection of 16x16x16 blocks
 */
class ChunkSection(
    var blocks: Array<BlockState?> = arrayOfNulls(ProtocolDefinition.BLOCKS_PER_SECTION),
    private var blockEntities: BlockEntityProvider = MapBlockEntityProvider(),
) {

    fun getBlockState(inChunkSectionPositions: Vec3i): BlockState? {
        return blocks[inChunkSectionPositions.index]
    }

    fun setBlockState(inChunkSectionPositions: Vec3i, blockState: BlockState?) {
        blocks[inChunkSectionPositions.index] = blockState
    }

    fun setData(chunkSection: ChunkSection) {
        blocks = chunkSection.blocks.clone()
        blockEntities = chunkSection.blockEntities.clone()
    }

    fun getBlockEntity(inChunkSectionPositions: Vec3i): BlockEntity? {
        return blockEntities[inChunkSectionPositions]
    }

    fun setBlockEntity(inChunkSectionPositions: Vec3i, blockEntity: BlockEntity?) {
        blockEntities[inChunkSectionPositions] = blockEntity
        val blockEntities = blockEntities
        if (blockEntities.size > BlockEntityProvider.BLOCK_ENTITY_MAP_LIMIT_UP && blockEntities is MapBlockEntityProvider) {
            this.blockEntities = ArrayBlockEntityProvider(blockEntities)
        } else if (blockEntities.size <= BlockEntityProvider.BLOCK_ENTITY_MAP_LIMIT_DOWN && blockEntities is ArrayBlockEntityProvider) {
            this.blockEntities = MapBlockEntityProvider(blockEntities)
        }
    }

    fun realTick(connection: PlayConnection, chunkSectionPosition: Vec3i) {
        blockEntities.forEach { entity, inChunkSectionPosition ->
            entity.realTick(connection, blocks[inChunkSectionPosition.index]!!, chunkSectionPosition + inChunkSectionPosition)
        }
    }

    companion object {
        val Vec3i.index: Int
            get() = getIndex(x, y, z)

        val Int.indexPosition: Vec3i
            get() {
                return Vec3i(this and 0x0F, (this shr 8) and 0x0F, (this shr 4) and 0x0F)
            }

        fun getIndex(x: Int, y: Int, z: Int): Int {
            return y shl 8 or (z shl 4) or x
        }
    }
}