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

package de.bixilon.minosoft.protocol.packets.s2c.play;

import de.bixilon.minosoft.data.commands.CommandNode;
import de.bixilon.minosoft.data.commands.CommandRootNode;
import de.bixilon.minosoft.protocol.network.connection.play.PlayConnection;
import de.bixilon.minosoft.protocol.packets.s2c.PlayS2CPacket;
import de.bixilon.minosoft.protocol.protocol.PlayInByteBuffer;
import de.bixilon.minosoft.util.logging.Log;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class PacketDeclareCommands implements PlayS2CPacket {
    private final CommandRootNode rootNode;

    public PacketDeclareCommands(PlayInByteBuffer buffer) {
        CommandNode[] nodes = buffer.readCommandNodeArray();
        this.rootNode = (CommandRootNode) nodes[buffer.readVarInt()];
    }

    @Override
    public void handle(PlayConnection connection) {
        connection.setCommandRootNode(getRootNode());
    }

    public CommandRootNode getRootNode() {
        return this.rootNode;
    }

    @Override
    public void log(boolean reducedLog) {
        Log.protocol("Received declare commands packets");
    }

    @Override
    public void check(@NotNull PlayConnection connection) {

    }
}
