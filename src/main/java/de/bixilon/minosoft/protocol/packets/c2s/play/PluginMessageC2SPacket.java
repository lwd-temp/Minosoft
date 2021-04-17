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

package de.bixilon.minosoft.protocol.packets.c2s.play;

import de.bixilon.minosoft.protocol.packets.c2s.PlayC2SPacket;
import de.bixilon.minosoft.protocol.protocol.PlayOutByteBuffer;
import de.bixilon.minosoft.util.logging.Log;

import static de.bixilon.minosoft.protocol.protocol.ProtocolVersions.V_14W29A;
import static de.bixilon.minosoft.protocol.protocol.ProtocolVersions.V_14W31A;

public class PluginMessageC2SPacket implements PlayC2SPacket {

    public final String channel;
    public final byte[] data;

    public PluginMessageC2SPacket(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void write(PlayOutByteBuffer buffer) {
        buffer.writeString(this.channel);

        if (buffer.getVersionId() < V_14W29A) {
            buffer.writeShort((short) this.data.length);
        } else if (buffer.getVersionId() < V_14W31A) {
            buffer.writeVarInt(this.data.length);
        }

        buffer.writeByteArray(this.data);
    }

    @Override
    public void log() {
        Log.protocol(String.format("[OUT] Sending data in plugin channel \"%s\" with a length of %d bytes", this.channel, this.data.length));
    }
}
