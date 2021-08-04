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

package de.bixilon.minosoft.gui.rendering.gui.mesh

import de.bixilon.minosoft.data.text.RGBColor
import de.bixilon.minosoft.gui.rendering.system.base.texture.texture.AbstractTexture
import glm_.vec2.Vec2
import glm_.vec2.Vec2i

interface FontVertexConsumer {

    fun addVertex(position: Vec2i, texture: AbstractTexture, uv: Vec2, tint: RGBColor)

    fun addQuad(start: Vec2i, end: Vec2i, texture: AbstractTexture, uvStart: Vec2, uvEnd: Vec2, tint: RGBColor)
}
