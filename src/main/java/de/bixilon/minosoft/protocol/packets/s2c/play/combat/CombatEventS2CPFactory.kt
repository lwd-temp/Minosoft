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

package de.bixilon.minosoft.protocol.packets.s2c.play.combat

import de.bixilon.minosoft.protocol.packets.s2c.PlayS2CPacket
import de.bixilon.minosoft.protocol.protocol.PlayInByteBuffer
import de.bixilon.minosoft.util.enum.ValuesEnum

object CombatEventS2CPFactory {

    fun createPacket(buffer: PlayInByteBuffer): PlayS2CPacket {
        return when (CombatEvents.VALUES[buffer.readVarInt()]) {
            CombatEvents.ENTER_COMBAT -> EnterCombatEventS2CPacket()
            CombatEvents.END_COMBAT -> EndCombatEventS2CPacket(buffer)
            CombatEvents.ENTITY_DEATH -> EntityDeathCombatEventS2CPacket(buffer)
        }
    }

    enum class CombatEvents {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DEATH,
        ;

        companion object : ValuesEnum<CombatEvents> {
            override val VALUES = values()
        }
    }
}
