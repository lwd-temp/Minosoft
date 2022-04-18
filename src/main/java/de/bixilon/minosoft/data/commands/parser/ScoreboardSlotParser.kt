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
package de.bixilon.minosoft.data.commands.parser

import de.bixilon.minosoft.data.commands.CommandStringReader
import de.bixilon.minosoft.data.commands.parser.exceptions.ColorNotFoundCommandParseException
import de.bixilon.minosoft.data.commands.parser.exceptions.UnknownOperationCommandParseException
import de.bixilon.minosoft.data.commands.parser.properties.ParserProperties
import de.bixilon.minosoft.data.text.ChatCode
import de.bixilon.minosoft.protocol.network.connection.play.PlayConnection

object ScoreboardSlotParser : CommandParser() {
    private val SCOREBOARD_SLOTS = setOf("list", "sidebar", "belowName")

    override fun parse(connection: PlayConnection, properties: ParserProperties?, stringReader: CommandStringReader): Any? {
        val slot = stringReader.readUnquotedString()

        if (slot.startsWith("sidebar.team.")) {
            val color = slot.substring("sidebar.team.".length)
            try {
                return ChatCode[color]
            } catch (exception: IllegalArgumentException) {
                throw ColorNotFoundCommandParseException(stringReader, color)
            }
        }

        if (!SCOREBOARD_SLOTS.contains(slot)) {
            throw UnknownOperationCommandParseException(stringReader, slot)
        }
        return slot

    }
}