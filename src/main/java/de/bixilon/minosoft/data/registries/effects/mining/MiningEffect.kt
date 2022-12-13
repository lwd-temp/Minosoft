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

package de.bixilon.minosoft.data.registries.effects.mining

import de.bixilon.minosoft.data.registries.CompanionResourceLocation
import de.bixilon.minosoft.data.registries.effects.StatusEffectType
import de.bixilon.minosoft.data.registries.effects.properties.categories.BeneficalEffect
import de.bixilon.minosoft.data.registries.effects.properties.categories.HarmfulEffect
import de.bixilon.minosoft.data.text.formatting.color.Colored
import de.bixilon.minosoft.data.text.formatting.color.RGBColor.Companion.asRGBColor
import de.bixilon.minosoft.util.KUtil.minecraft

interface MiningEffect {


    object Haste : StatusEffectType(), MiningEffect, CompanionResourceLocation, Colored, BeneficalEffect {
        override val RESOURCE_LOCATION = minecraft("haste")
        override val color = 0xD9C043.asRGBColor()
    }

    object MiningFatigue : StatusEffectType(), MiningEffect, CompanionResourceLocation, Colored, HarmfulEffect {
        override val RESOURCE_LOCATION = minecraft("mining_fatigue")
        override val color = 0x4A4217.asRGBColor()
    }
}