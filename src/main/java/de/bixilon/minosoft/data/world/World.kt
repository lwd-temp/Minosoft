/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.world

import com.google.common.collect.HashBiMap
import de.bixilon.minosoft.data.entities.block.BlockEntityMetaData
import de.bixilon.minosoft.data.entities.entities.Entity
import de.bixilon.minosoft.data.mappings.Dimension
import de.bixilon.minosoft.data.mappings.blocks.BlockState
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Collection of chunks
 */
class World {
    val chunks = ConcurrentHashMap<ChunkLocation, Chunk>()
    val entityIdMap = HashBiMap.create<Int, Entity>()
    val entityUUIDMap = HashBiMap.create<UUID, Entity>()
    var isHardcore = false
    var isRaining = false
    var dimension: Dimension? = null


    fun getBlockInfo(blockPosition: BlockPosition): BlockInfo? {
        val chunkLocation = blockPosition.getChunkLocation()
        return chunks[chunkLocation]?.getBlockInfo(blockPosition.getInChunkLocation())
    }

    fun getChunk(loc: ChunkLocation): Chunk? {
        return chunks[loc]
    }

    fun getOrCreateChunk(chunkLocation: ChunkLocation): Chunk {
        return chunks[chunkLocation] ?: run {
            val chunk = Chunk()
            chunks[chunkLocation] = chunk
            chunk
        }
    }

    fun setBlock(blockPosition: BlockPosition, block: BlockState?) {
        chunks[blockPosition.getChunkLocation()]?.setRawBlock(blockPosition.getInChunkLocation(), block)
    }

    fun unloadChunk(location: ChunkLocation) {
        chunks.remove(location)
    }

    fun replaceChunk(location: ChunkLocation, chunk: Chunk) {
        chunks[location] = chunk
    }

    fun replaceChunks(chunkMap: HashMap<ChunkLocation, Chunk>) {
        for ((chunkLocation, chunk) in chunkMap) {
            chunks[chunkLocation] = chunk
        }
    }

    fun addEntity(entity: Entity) {
        entityIdMap[entity.entityId] = entity
        entityUUIDMap[entity.uuid] = entity
    }

    fun getEntity(id: Int): Entity? {
        return entityIdMap[id]
    }

    fun getEntity(uuid: UUID): Entity? {
        return entityUUIDMap[uuid]
    }

    fun removeEntity(entity: Entity) {
        entityIdMap.inverse().remove(entity)
        entityUUIDMap.inverse().remove(entity)
    }

    fun removeEntity(entityId: Int) {
        entityIdMap[entityId]?.let { removeEntity(it) }
    }

    fun removeEntity(entityUUID: UUID) {
        entityUUIDMap[entityUUID]?.let { removeEntity(it) }
    }

    fun setBlockEntityData(position: BlockPosition, data: BlockEntityMetaData?) {
        chunks[position.getChunkLocation()]?.sections?.get(position.getSectionHeight())?.getBlockInfo(position.getInChunkSectionLocation())?.metaData = data
    }

    fun setBlockEntityData(blockEntities: HashMap<BlockPosition, BlockEntityMetaData>) {
        for ((blockPosition, entityMetaData) in blockEntities) {
            setBlockEntityData(blockPosition, entityMetaData)
        }
    }
}
