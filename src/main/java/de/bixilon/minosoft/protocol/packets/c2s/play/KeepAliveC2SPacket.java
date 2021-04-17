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

import static de.bixilon.minosoft.protocol.protocol.ProtocolVersions.V_14W31A;
import static de.bixilon.minosoft.protocol.protocol.ProtocolVersions.V_1_12_2_PRE2;

public class KeepAliveC2SPacket implements PlayC2SPacket {
    private final long id;

    public KeepAliveC2SPacket(long id) {
        this.id = id;
    }

    public KeepAliveC2SPacket(int id) {
        this.id = id;
    }

    @Override
    public void write(PlayOutByteBuffer buffer) {
        if (buffer.getVersionId() < V_14W31A) {
            buffer.writeInt((int) this.id);
        } else if (buffer.getVersionId() < V_1_12_2_PRE2) {
            buffer.writeVarInt((int) this.id);
        } else {
            buffer.writeLong(this.id);
        }
    }

    @Override
    public void log() {
        Log.protocol(String.format("[OUT] Sending keep alive back (%d)", this.id));
    }
}
