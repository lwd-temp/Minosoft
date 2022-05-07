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

package de.bixilon.minosoft.data.registries.blocks.types.entity.sign

import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.data.registries.blocks.BlockFactory
import de.bixilon.minosoft.data.registries.factory.clazz.MultiClassFactory
import de.bixilon.minosoft.data.registries.registries.Registries

open class StandingSignBlock(resourceLocation: ResourceLocation, registries: Registries, data: Map<String, Any>) : SignBlock(resourceLocation, registries, data) {

    companion object : BlockFactory<StandingSignBlock>, MultiClassFactory<StandingSignBlock> {
        override val ALIASES: Set<String> = setOf("SignBlock")

        override fun build(resourceLocation: ResourceLocation, registries: Registries, data: Map<String, Any>): StandingSignBlock {
            return StandingSignBlock(resourceLocation, registries, data)
        }
    }
}
