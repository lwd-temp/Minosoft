/*
 * Minosoft
 * Copyright (C) 2022 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.text.events.click

import de.bixilon.kutil.json.JsonObject
import de.bixilon.kutil.url.URLUtil.checkWeb
import de.bixilon.kutil.url.URLUtil.toURL
import de.bixilon.minosoft.data.text.events.hover.HoverEvent.Companion.data
import de.bixilon.minosoft.gui.eros.util.JavaFXUtil.hyperlink
import javafx.scene.text.Text
import java.net.URL

class OpenURLClickEvent(
    val url: URL,
) : ClickEvent {

    override fun applyJavaFX(text: Text) {
        text.hyperlink(url.toString())
    }

    companion object : ClickEventFactory<OpenURLClickEvent> {
        override val name: String = "open_url"

        override fun build(json: JsonObject, restrictedMode: Boolean): OpenURLClickEvent {
            val url = json.data.toString().toURL()
            if (restrictedMode) {
                url.checkWeb()
            }
            return OpenURLClickEvent(url)
        }
    }
}
