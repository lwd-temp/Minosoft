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

package de.bixilon.minosoft.commands.parser.minecraft.target.targets.selector.properties.rotation

import de.bixilon.minosoft.commands.parser.minecraft.range._float.FloatRange
import de.bixilon.minosoft.commands.parser.minecraft.range._float.FloatRangeParser
import de.bixilon.minosoft.commands.parser.minecraft.target.targets.selector.properties.TargetPropertyFactory
import de.bixilon.minosoft.commands.util.CommandReader
import de.bixilon.minosoft.data.entities.EntityRotation

class PitchRotation(
    override val range: FloatRange,
) : RotationProperty {

    override fun getValue(rotation: EntityRotation): Double {
        return rotation.pitch
    }


    companion object : TargetPropertyFactory<YawRotation> {
        const val MIN = -90.0f
        const val MAX = 90.0f
        override val name: String = "x_rotation"
        private val parser = FloatRangeParser(false)

        override fun read(reader: CommandReader): YawRotation {
            val range = reader.readResult { parser.parse(reader) }
            if (range.result.min < MIN || range.result.max > MAX) {
                throw RotationOutOfRangeError(reader, range)
            }
            return YawRotation(range.result)
        }
    }
}
