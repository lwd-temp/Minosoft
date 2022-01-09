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

package de.bixilon.minosoft.protocol.network.network.client.pipeline.encoding

import de.bixilon.kutil.cast.CastUtil.unsafeCast
import de.bixilon.minosoft.data.registries.versions.Version
import de.bixilon.minosoft.protocol.network.connection.play.PlayConnection
import de.bixilon.minosoft.protocol.network.network.client.NettyClient
import de.bixilon.minosoft.protocol.network.network.client.exceptions.NetworkException
import de.bixilon.minosoft.protocol.network.network.client.exceptions.PacketBufferUnderflowException
import de.bixilon.minosoft.protocol.network.network.client.exceptions.PacketReadException
import de.bixilon.minosoft.protocol.network.network.client.exceptions.ciritical.UnknownPacketIdException
import de.bixilon.minosoft.protocol.network.network.client.exceptions.implementation.S2CPacketNotImplementedException
import de.bixilon.minosoft.protocol.packets.factory.S2CPacketType
import de.bixilon.minosoft.protocol.packets.s2c.S2CPacket
import de.bixilon.minosoft.protocol.protocol.InByteBuffer
import de.bixilon.minosoft.protocol.protocol.PlayInByteBuffer
import de.bixilon.minosoft.protocol.protocol.Protocol
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder

class PacketDecoder(
    private val client: NettyClient,
) : MessageToMessageDecoder<ByteArray>() {
    private val version: Version? = client.connection.version

    override fun decode(context: ChannelHandlerContext, array: ByteArray, out: MutableList<Any>) {
        val buffer = InByteBuffer(array)
        val packetId = buffer.readVarInt()
        val data = buffer.readRest()

        val state = client.state

        val packetType = version?.s2cPackets?.get(state)?.getKey(packetId) ?: Protocol.S2C_PACKET_MAPPING[state]?.getKey(packetId) ?: throw UnknownPacketIdException(packetId, state, version)

        val packet = try {
            readPacket(packetType, data)
        } catch (exception: NetworkException) {
            packetType.onError(exception, client.connection)
            throw exception
        } catch (error: Throwable) {
            packetType.onError(error, client.connection)
            throw PacketReadException(error)
        }

        out += packet
    }

    private fun readPacket(type: S2CPacketType, data: ByteArray): S2CPacket {
        val buffer: InByteBuffer = if (client.connection is PlayConnection) {
            PlayInByteBuffer(data, client.connection)
        } else {
            InByteBuffer(data)
        }
        val packet = type.factory?.createPacket(buffer) ?: throw S2CPacketNotImplementedException(type)
        if (buffer.pointer < buffer.size) {
            throw PacketBufferUnderflowException(type, buffer.size, buffer.pointer)
        }
        return packet.unsafeCast()
    }


    companion object {
        const val NAME = "packet_decoder"
    }
}
