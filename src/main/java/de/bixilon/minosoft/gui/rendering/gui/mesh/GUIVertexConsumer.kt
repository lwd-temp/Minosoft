/*
 * Minosoft
 * Copyright (C) 2020-2023 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.gui.rendering.gui.mesh

import de.bixilon.kotlinglm.vec2.Vec2
import de.bixilon.minosoft.data.text.formatting.color.RGBColor
import de.bixilon.minosoft.gui.rendering.font.renderer.properties.FontProperties
import de.bixilon.minosoft.gui.rendering.font.renderer.properties.FormattingProperties
import de.bixilon.minosoft.gui.rendering.system.base.texture.TexturePart
import de.bixilon.minosoft.gui.rendering.system.base.texture.shader.ShaderTexture
import de.bixilon.minosoft.gui.rendering.system.base.texture.texture.Texture

interface GUIVertexConsumer {
    val order: IntArray

    fun addVertex(position: Vec2, texture: ShaderTexture?, uv: Vec2, tint: RGBColor, options: GUIVertexOptions?)

    fun addQuad(start: Vec2, end: Vec2, texture: ShaderTexture?, uvStart: Vec2 = UV_START, uvEnd: Vec2 = UV_END, tint: RGBColor, options: GUIVertexOptions?) {
        val uvStart = texture?.transformUV(uvStart) ?: uvStart
        val uvEnd = texture?.transformUV(uvEnd) ?: uvEnd

        val positions = arrayOf(
            start,
            Vec2(end.x, start.y),
            end,
            Vec2(start.x, end.y),
        )
        val texturePositions = arrayOf(
            Vec2(uvEnd.x, uvStart.y),
            uvStart,
            Vec2(uvStart.x, uvEnd.y),
            uvEnd,
        )

        for (index in 0 until order.size step 2) {
            addVertex(positions[order[index]], texture, texturePositions[order[index + 1]], tint, options)
        }
    }

    fun addQuad(start: Vec2, end: Vec2, texture: TexturePart, tint: RGBColor, options: GUIVertexOptions?) {
        addQuad(start, end, texture.texture, texture.uvStart, texture.uvEnd, tint, options)
    }

    fun addQuad(start: Vec2, end: Vec2, tint: RGBColor, options: GUIVertexOptions?) {
        addQuad(start, end, null, tint = tint, options = options)
    }


    fun addChar(start: Vec2, end: Vec2, texture: Texture?, uvStart: Vec2, uvEnd: Vec2, italic: Boolean, tint: RGBColor, options: GUIVertexOptions?) {
        val topOffset = if (italic) (end.y - start.y) / FontProperties.CHAR_BASE_HEIGHT * FormattingProperties.ITALIC_OFFSET else 0.0f

        val positions = arrayOf(
            Vec2(start.x + topOffset, start.y),
            Vec2(end.x + topOffset, start.y),
            end,
            Vec2(start.x, end.y),
        )
        val texturePositions = arrayOf(
            Vec2(uvEnd.x, uvStart.y),
            uvStart,
            Vec2(uvStart.x, uvEnd.y),
            uvEnd,
        )

        for (index in 0 until order.size step 2) {
            addVertex(positions[order[index]], texture, texturePositions[order[index + 1]], tint, options)
        }
    }

    fun addCache(cache: GUIMeshCache)

    fun ensureSize(size: Int)

    companion object {
        val UV_START = Vec2(0.0f, 0.0f)
        val UV_END = Vec2(1.0f, 1.0f)
    }
}
