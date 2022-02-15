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

package de.bixilon.minosoft.gui.rendering.gui.hud.elements.chat

import de.bixilon.minosoft.config.profile.delegate.watcher.SimpleProfileDelegateWatcher.Companion.profileWatchRendering
import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.gui.rendering.gui.GUIRenderer
import de.bixilon.minosoft.gui.rendering.gui.elements.LayoutedElement
import de.bixilon.minosoft.gui.rendering.gui.elements.text.TextFlowElement
import de.bixilon.minosoft.gui.rendering.gui.hud.Initializable
import de.bixilon.minosoft.gui.rendering.gui.hud.elements.HUDBuilder
import de.bixilon.minosoft.gui.rendering.gui.hud.elements.LayoutedGUIElement
import de.bixilon.minosoft.gui.rendering.renderer.Drawable
import de.bixilon.minosoft.modding.event.events.InternalMessageReceiveEvent
import de.bixilon.minosoft.modding.event.invoker.CallbackEventInvoker
import de.bixilon.minosoft.util.KUtil.toResourceLocation
import glm_.vec2.Vec2i

class InternalMessagesElement(guiRenderer: GUIRenderer) : TextFlowElement(guiRenderer, 15000), LayoutedElement, Initializable, Drawable {
    private val connection = renderWindow.connection
    private val profile = connection.profiles.gui
    private val internalChatProfile = profile.chat.internal
    override var skipDraw: Boolean
        get() = internalChatProfile.hidden
        set(value) {
            internalChatProfile.hidden = !value
        }


    override val layoutOffset: Vec2i
        get() = super.size.let { return@let guiRenderer.scaledSize - Vec2i(it.x, it.y + ChatElement.CHAT_INPUT_HEIGHT + ChatElement.CHAT_INPUT_MARGIN * 2) }

    init {
        super.prefMaxSize = Vec2i(internalChatProfile.width, internalChatProfile.height)
        internalChatProfile::width.profileWatchRendering(this, profile = profile) { super.prefMaxSize = Vec2i(it, super.prefMaxSize.y) }
        internalChatProfile::height.profileWatchRendering(this, profile = profile) { super.prefMaxSize = Vec2i(super.prefMaxSize.x, it) }
    }


    override fun init() {
        connection.registerEvent(CallbackEventInvoker.of<InternalMessageReceiveEvent> { this += it.message })
    }


    companion object : HUDBuilder<LayoutedGUIElement<InternalMessagesElement>> {
        override val RESOURCE_LOCATION: ResourceLocation = "minosoft:internal_messages_hud".toResourceLocation()

        override fun build(guiRenderer: GUIRenderer): LayoutedGUIElement<InternalMessagesElement> {
            return LayoutedGUIElement(InternalMessagesElement(guiRenderer))
        }
    }
}
