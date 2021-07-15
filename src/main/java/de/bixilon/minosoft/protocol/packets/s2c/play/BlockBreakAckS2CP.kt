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
package de.bixilon.minosoft.protocol.packets.s2c.play

import de.bixilon.minosoft.data.registries.blocks.BlockState
import de.bixilon.minosoft.modding.event.events.BlockBreakAckEvent
import de.bixilon.minosoft.protocol.network.connection.PlayConnection
import de.bixilon.minosoft.protocol.packets.c2s.play.BlockBreakC2SP.BreakType
import de.bixilon.minosoft.protocol.packets.s2c.PlayS2CPacket
import de.bixilon.minosoft.protocol.protocol.PlayInByteBuffer
import de.bixilon.minosoft.util.logging.Log
import de.bixilon.minosoft.util.logging.LogLevels
import de.bixilon.minosoft.util.logging.LogMessageType
import glm_.vec3.Vec3i

class BlockBreakAckS2CP(buffer: PlayInByteBuffer) : PlayS2CPacket() {
    val blockPosition: Vec3i = buffer.readBlockPosition()
    val blockState: BlockState? = buffer.connection.registries.blockStateRegistry[buffer.readVarInt()]
    val breakType: BreakType = BreakType[buffer.readVarInt()]
    val successful: Boolean = buffer.readBoolean()

    override fun handle(connection: PlayConnection) {
        connection.fireEvent(BlockBreakAckEvent(connection, this))
        if (breakType == BreakType.FINISHED_DIGGING && !successful) {
            // never happens?
            connection.world[blockPosition] = blockState
        }

    }

    override fun log() {
        Log.log(LogMessageType.NETWORK_PACKETS_IN, level = LogLevels.VERBOSE) { "Block break acknowledge (blockPosition=$blockPosition, blockState=$blockState, breakType=$breakType, successful=$successful)" }
    }
}