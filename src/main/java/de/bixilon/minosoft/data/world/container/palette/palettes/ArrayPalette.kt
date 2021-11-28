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

package de.bixilon.minosoft.data.world.container.palette.palettes

import de.bixilon.minosoft.data.registries.registries.registry.AbstractRegistry
import de.bixilon.minosoft.protocol.protocol.PlayInByteBuffer

class ArrayPalette<T>(private val registry: AbstractRegistry<T>, override val bits: Int) : Palette<T> {
    private var array: Array<Any?> = arrayOfNulls(0)

    override fun read(buffer: PlayInByteBuffer) {
        array = arrayOfNulls(buffer.readVarInt())
        for (i in array.indices) {
            array[i] = registry[buffer.readVarInt()]
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T {
        return array[index] as T
    }
}