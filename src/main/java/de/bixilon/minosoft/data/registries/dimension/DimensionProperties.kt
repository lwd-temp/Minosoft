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

package de.bixilon.minosoft.data.registries.dimension

import de.bixilon.kutil.cast.CastUtil.nullCast
import de.bixilon.kutil.math.interpolation.FloatInterpolation.interpolateLinear
import de.bixilon.kutil.primitive.BooleanUtil.toBoolean
import de.bixilon.kutil.primitive.FloatUtil.toFloat
import de.bixilon.kutil.primitive.IntUtil.toInt
import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.protocol.protocol.ProtocolDefinition
import de.bixilon.minosoft.util.nbt.tag.NBTUtil.get

data class DimensionProperties(
    val piglinSafe: Boolean = false,
    val natural: Boolean = true,
    val ambientLight: Float = 0.0f,
    val infiniBurn: ResourceLocation = ResourceLocation("infiniburn_overworld"),
    val respawnAnchorWorks: Boolean = false,
    val hasSkyLight: Boolean = true,
    val bedWorks: Boolean = true,
    val skyProperties: ResourceLocation = ResourceLocation("overworld"),
    val hasRaids: Boolean = true,
    val logicalHeight: Int = 256,
    val coordinateScale: Double = 0.0,
    val minY: Int = 0,
    val hasCeiling: Boolean = false,
    val ultraWarm: Boolean = false,
    @Deprecated("Height does not differ from logical height in 1.18")
    val dataHeight: Int = 256,
    val supports3DBiomes: Boolean = true,
) {
    val height = logicalHeight + minY
    val lowestSection = if (minY < 0) {
        (minY + 1) / ProtocolDefinition.SECTION_HEIGHT_Y - 1
    } else {
        minY / ProtocolDefinition.SECTION_HEIGHT_Y
    }
    val highestSection = if (height < 0) {
        (height + 1) / ProtocolDefinition.SECTION_HEIGHT_Y - 1
    } else {
        height / ProtocolDefinition.SECTION_HEIGHT_Y
    }

    val lightLevels = FloatArray(16)
    val sections = highestSection - lowestSection

    init {
        val ambientLight = 0.0f // ToDo: 0.1 in nether

        for (i in lightLevels.indices) {
            val asFloat = i / 15.0f

            lightLevels[i] = interpolateLinear(ambientLight, asFloat / (4.0f - 3.0f * asFloat), 1.0f)
        }
    }


    companion object {
        fun deserialize(data: Map<String, Any>): DimensionProperties {
            return DimensionProperties(
                piglinSafe = data["piglin_safe"]?.toBoolean() ?: false,
                natural = data["natural"]?.toBoolean() ?: false,
                ambientLight = data["ambient_light"]?.toFloat() ?: 0.0f,
                infiniBurn = ResourceLocation(data["infiniburn"].nullCast<String>() ?: "infiniburn_overworld"),
                respawnAnchorWorks = data["respawn_anchor_works"]?.toBoolean() ?: false,
                hasSkyLight = data["has_skylight", "has_sky_light"]?.toBoolean() ?: false,
                bedWorks = data["bed_works"]?.toBoolean() ?: false,
                skyProperties = ResourceLocation(data["effects"].nullCast<String>() ?: "overworld"),
                hasRaids = data["has_raids"]?.toBoolean() ?: false,
                logicalHeight = data["logical_height"]?.toInt() ?: 256,
                coordinateScale = data["coordinate_scale"].nullCast() ?: 0.0,
                minY = data["min_y"]?.toInt() ?: 0,
                hasCeiling = data["has_ceiling"]?.toBoolean() ?: false,
                ultraWarm = data["ultrawarm"]?.toBoolean() ?: false,
                dataHeight = data["height"]?.toInt() ?: 256,
                supports3DBiomes = data["supports_3d_biomes"]?.toBoolean() ?: true,
            )
        }
    }
}
