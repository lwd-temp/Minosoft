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
package de.bixilon.minosoft.data.commands.parser

import de.bixilon.minosoft.data.commands.parser.exceptions.CommandParseException
import de.bixilon.minosoft.data.commands.parser.exceptions.EnchantmentNotFoundCommandParseException
import de.bixilon.minosoft.data.commands.parser.exceptions.InvalidIdentifierCommandParseException
import de.bixilon.minosoft.data.commands.parser.exceptions.MobEffectNotFoundCommandParseException
import de.bixilon.minosoft.data.commands.parser.properties.ParserProperties
import de.bixilon.minosoft.data.mappings.ModIdentifier
import de.bixilon.minosoft.protocol.network.Connection
import de.bixilon.minosoft.util.buffers.ImprovedStringReader

class IdentifierListParser : CommandParser() {

    @Throws(CommandParseException::class)
    override fun isParsable(connection: Connection, properties: ParserProperties?, stringReader: ImprovedStringReader) {
        val argument = stringReader.readUntilNextCommandArgument()


        val identifier: ModIdentifier
        try {
            identifier = ModIdentifier.getIdentifier(argument)
        } catch (exception: IllegalArgumentException) {
            throw InvalidIdentifierCommandParseException(stringReader, argument)
        }

        if (this == ENCHANTMENT_PARSER) {
            if (!connection.mapping.doesEnchantmentExist(identifier)) {
                throw EnchantmentNotFoundCommandParseException(stringReader, argument)
            }
            return
        }
        if (this == MOB_EFFECT_PARSER) {
            if (!connection.mapping.doesMobEffectExist(identifier)) {
                throw MobEffectNotFoundCommandParseException(stringReader, argument)
            }
            return
        }
    }

    companion object {
        val ENCHANTMENT_PARSER = IdentifierListParser()
        val MOB_EFFECT_PARSER = IdentifierListParser()
    }
}
