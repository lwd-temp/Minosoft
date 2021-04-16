/*
 * Minosoft
 * Copyright (C) 2021 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.entities.block

import de.bixilon.minosoft.data.mappings.ResourceLocation
import de.bixilon.minosoft.data.text.ChatComponent
import de.bixilon.minosoft.gui.rendering.RenderConstants
import de.bixilon.minosoft.protocol.network.connection.PlayConnection
import de.bixilon.minosoft.util.nbt.tag.CompoundTag

class SignBlockEntity(connection: PlayConnection) : BlockEntity(connection) {
    var lines: Array<ChatComponent> = Array(RenderConstants.SIGN_LINES) { ChatComponent.valueOf(raw = "") }


    override fun updateNBT(nbt: CompoundTag) {
        for (i in 0 until RenderConstants.SIGN_LINES) {
            val tag = nbt.getStringTag("Text$i") ?: continue

            lines[i] = ChatComponent.valueOf(translator = connection.version.localeManager, raw = tag.value)
        }
    }

    companion object : BlockEntityFactory<SignBlockEntity> {
        override val RESOURCE_LOCATION: ResourceLocation = ResourceLocation("minecraft:sign")

        override fun build(connection: PlayConnection): SignBlockEntity {
            return SignBlockEntity(connection)
        }
    }
}
