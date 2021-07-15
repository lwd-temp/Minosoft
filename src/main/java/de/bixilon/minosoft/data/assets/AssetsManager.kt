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

package de.bixilon.minosoft.data.assets

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.data.text.RGBColor
import de.bixilon.minosoft.util.KUtil.fromJson
import de.bixilon.minosoft.util.Util
import de.bixilon.minosoft.util.nbt.tag.NBTUtil.asCompound
import de.matthiasmann.twl.utils.PNGDecoder
import org.lwjgl.BufferUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels

interface AssetsManager {

    val namespaces: Set<String>

    fun getAssetURL(resourceLocation: ResourceLocation): URL

    fun getAssetSize(resourceLocation: ResourceLocation): Long

    fun readAssetAsStream(resourceLocation: ResourceLocation): InputStream

    fun readAssetAsReader(resourceLocation: ResourceLocation): BufferedReader {
        return BufferedReader(InputStreamReader(readAssetAsStream(resourceLocation)))
    }

    @Deprecated(message = "Will be removed...")
    fun readLegacyJsonAsset(resourceLocation: ResourceLocation): JsonObject {
        val reader = readAssetAsReader(resourceLocation)
        val json = JsonParser.parseReader(reader).asJsonObject
        reader.close()
        return json
    }

    fun readJsonAsset(resourceLocation: ResourceLocation): Map<String, Any> {
        return readStringAsset(resourceLocation).fromJson().asCompound()
    }

    fun readStringAsset(resourceLocation: ResourceLocation): String {
        return Util.readReader(readAssetAsReader(resourceLocation), true)
    }

    fun readPixelArrayAsset(resourceLocation: ResourceLocation): Array<RGBColor> {
        val decoder = PNGDecoder(readAssetAsStream(resourceLocation))

        val buffer = BufferUtils.createByteBuffer(decoder.width * decoder.height * PNGDecoder.Format.RGBA.numComponents)
        decoder.decode(buffer, decoder.width * PNGDecoder.Format.RGBA.numComponents, PNGDecoder.Format.RGBA)
        buffer.rewind()

        val colors: MutableList<RGBColor> = mutableListOf()
        while (buffer.hasRemaining()) {
            colors.add(RGBColor(buffer.get(), buffer.get(), buffer.get(), buffer.get()))
        }
        return colors.toTypedArray()
    }

    fun readByteAsset(resourceLocation: ResourceLocation): ByteBuffer {
        val buffer = BufferUtils.createByteBuffer(getAssetSize(resourceLocation).toInt())
        val inputStream = readAssetAsStream(resourceLocation)
        val byteChannel = Channels.newChannel(inputStream)
        while (true) {
            val bytes: Int = byteChannel.read(buffer)
            if (bytes <= 0) {
                break
            }
        }
        byteChannel.close()
        inputStream.close()

        buffer.flip()
        return buffer
    }
}