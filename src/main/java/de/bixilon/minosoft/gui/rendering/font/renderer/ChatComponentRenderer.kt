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

package de.bixilon.minosoft.gui.rendering.font.renderer

import de.bixilon.minosoft.data.text.BaseComponent
import de.bixilon.minosoft.data.text.ChatComponent
import de.bixilon.minosoft.data.text.TextComponent
import de.bixilon.minosoft.gui.rendering.RenderWindow
import de.bixilon.minosoft.gui.rendering.gui.elements.Element
import de.bixilon.minosoft.gui.rendering.gui.mesh.GUIVertexConsumer
import de.bixilon.minosoft.gui.rendering.gui.mesh.GUIVertexOptions
import glm_.vec2.Vec2i

interface ChatComponentRenderer<T : ChatComponent> {

    /**
     * Returns true if the text exceeded the maximum size
     */
    fun render(initialOffset: Vec2i, offset: Vec2i, size: Vec2i, z: Int, element: Element, renderWindow: RenderWindow, consumer: GUIVertexConsumer?, options: GUIVertexOptions?, renderInfo: TextRenderInfo, text: T): Boolean


    companion object : ChatComponentRenderer<ChatComponent> {

        override fun render(initialOffset: Vec2i, offset: Vec2i, size: Vec2i, z: Int, element: Element, renderWindow: RenderWindow, consumer: GUIVertexConsumer?, options: GUIVertexOptions?, renderInfo: TextRenderInfo, text: ChatComponent): Boolean {
            return when (text) {
                is BaseComponent -> BaseComponentRenderer.render(initialOffset, offset, size, z, element, renderWindow, consumer, options, renderInfo, text)
                is TextComponent -> TextComponentRenderer.render(initialOffset, offset, size, z, element, renderWindow, consumer, options, renderInfo, text)
                else -> TODO("Don't know how to render ${text::class.java}")
            }
        }
    }
}