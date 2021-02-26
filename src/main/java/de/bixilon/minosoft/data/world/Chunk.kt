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

import de.bixilon.minosoft.data.mappings.blocks.BlockState
import de.bixilon.minosoft.data.world.biome.BiomeAccessor
import de.bixilon.minosoft.data.world.light.LightAccessor
import java.util.*

/**
 * Collection of chunks sections (allocated in y)
 */
class Chunk(
    var sections: MutableMap<Int, ChunkSection>? = null,
    var biomeAccessor: BiomeAccessor? = null,
    var lightAccessor: LightAccessor? = null,
) {
    private val lock = Object()
    val isFullyLoaded: Boolean
        get() {
            return sections != null && biomeAccessor != null && lightAccessor != null
        }

    fun getBlockInfo(location: InChunkLocation): BlockInfo? {
        return sections?.get(location.getSectionHeight())?.getBlockInfo(location.getInChunkSectionLocation())
    }

    fun getBlockInfo(x: Int, y: Int, z: Int): BlockInfo? {
        return getBlockInfo(InChunkLocation(x, y, z))
    }

    fun setBlocks(blocks: HashMap<InChunkLocation, BlockInfo?>) {
        for ((location, blockInfo) in blocks) {
            setBlock(location, blockInfo)
        }
    }

    fun setData(data: ChunkData, merge: Boolean = false) {
        synchronized(lock) {
            data.blocks?.let {
                if (sections == null) {
                    sections = mutableMapOf()
                }
                if (!merge) {
                    sections?.clear()
                }
                // replace all chunk sections
                for ((sectionHeight, chunkSection) in it) {
                    getSectionOrCreate(sectionHeight).setData(chunkSection, merge)
                }
            }
            data.biomeAccessor?.let {
                this.biomeAccessor = it
            }
            data.lightAccessor?.let {
                this.lightAccessor = it
            }
        }
    }

    fun setRawBlocks(blocks: HashMap<InChunkLocation, BlockState?>) {
        for ((location, blockInfo) in blocks) {
            setRawBlock(location, blockInfo)
        }
    }

    fun setBlock(location: InChunkLocation, block: BlockInfo?) {
        getSectionOrCreate(location.getSectionHeight()).setBlockInfo(location.getInChunkSectionLocation(), block)
    }

    fun setRawBlock(location: InChunkLocation, block: BlockState?) {
        getSectionOrCreate(location.getSectionHeight()).let {
            val inChunkSectionLocation = location.getInChunkSectionLocation()
            if (block == null) {
                it.blocks.remove(inChunkSectionLocation)
                return
            }
            it.setBlockInfo(inChunkSectionLocation, BlockInfo(block))
        }

    }

    fun getSectionOrCreate(sectionHeight: Int): ChunkSection {
        if (sections == null) {
            throw IllegalStateException("Chunk not received/initialized yet!")
        }
        return sections!![sectionHeight].let {
            var section = it
            if (section == null) {
                section = ChunkSection()
                sections!![sectionHeight] = section
            }
            section
        }
    }
}
