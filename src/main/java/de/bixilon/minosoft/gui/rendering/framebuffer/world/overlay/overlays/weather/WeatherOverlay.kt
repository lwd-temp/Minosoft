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

package de.bixilon.minosoft.gui.rendering.framebuffer.world.overlay.overlays.weather

import de.bixilon.kotlinglm.vec2.Vec2
import de.bixilon.kotlinglm.vec3.Vec3
import de.bixilon.kutil.time.TimeUtil.millis
import de.bixilon.minosoft.data.registries.biomes.BiomePrecipitation
import de.bixilon.minosoft.gui.rendering.RenderWindow
import de.bixilon.minosoft.gui.rendering.framebuffer.world.overlay.Overlay
import de.bixilon.minosoft.gui.rendering.framebuffer.world.overlay.OverlayFactory
import de.bixilon.minosoft.gui.rendering.system.base.shader.Shader
import de.bixilon.minosoft.gui.rendering.system.base.texture.texture.AbstractTexture
import de.bixilon.minosoft.gui.rendering.textures.TextureUtil.texture
import de.bixilon.minosoft.gui.rendering.util.mesh.Mesh
import de.bixilon.minosoft.gui.rendering.util.vec.vec2.Vec2Util.EMPTY
import de.bixilon.minosoft.util.KUtil.minosoft
import de.bixilon.minosoft.util.KUtil.nextFloat
import de.bixilon.minosoft.util.KUtil.toResourceLocation
import java.util.*

class WeatherOverlay(private val renderWindow: RenderWindow, private val z: Float) : Overlay {
    private val world = renderWindow.connection.world
    private val config = renderWindow.connection.profiles.rendering.overlay.weather
    private val rain = renderWindow.textureManager.staticTextures.createTexture(RAIN)
    private val snow = renderWindow.textureManager.staticTextures.createTexture(SNOW)
    private val precipitation get() = renderWindow.connection.player.positionInfo.biome?.precipitation ?: BiomePrecipitation.NONE
    override val render: Boolean
        get() = world.dimension?.effects?.weather == true && world.weather.raining && when (precipitation) { // ToDo: Check if exposed to the sky
            BiomePrecipitation.NONE -> false
            BiomePrecipitation.RAIN -> config.rain
            BiomePrecipitation.SNOW -> config.snow
        }
    private val texture: AbstractTexture?
        get() = when (precipitation) {
            BiomePrecipitation.NONE -> null
            BiomePrecipitation.RAIN -> rain
            BiomePrecipitation.SNOW -> snow
        }

    private val shader: Shader = renderWindow.renderSystem.createShader(minosoft("weather/overlay"))
    private var mesh = WeatherOverlayMesh(renderWindow)
    private var windowSize = Vec2.EMPTY


    private fun updateMesh(windowSize: Vec2) {
        if (mesh.state == Mesh.MeshStates.LOADED) {
            mesh.unload()
        }
        mesh = WeatherOverlayMesh(renderWindow)

        val texture = texture!!
        val scale = windowSize.y / texture.size.y
        val step = texture.size.x * scale
        var offset = 0.0f
        val random = Random()
        while (true) {
            val timeOffset = random.nextFloat(0.0f, 1.0f)
            val offsetMultiplicator = random.nextFloat(0.8f, 1.2f)
            val alpha = random.nextFloat(0.8f, 1.0f)
            mesh.addZQuad(
                Vec2(offset, 0), z, Vec2(offset + step, windowSize.y), Vec2(0.0f), texture.textureArrayUV
            ) { position, uv ->
                val transformed = Vec2()
                transformed.x = position.x / (windowSize.x / 2) - 1.0f
                transformed.y = position.y / (windowSize.y / 2) - 1.0f
                mesh.addVertex(Vec3(transformed.x, transformed.y, z), uv, timeOffset, offsetMultiplicator, alpha)
            }
            offset += step
            if (offset > windowSize.x) {
                break
            }
        }
        this.windowSize = windowSize
        mesh.load()
    }

    override fun init() {
        shader.load()
    }

    override fun postInit() {
        shader.use()
        renderWindow.textureManager.staticTextures.use(shader)
    }

    private fun updateShader() {
        shader.setFloat("uIntensity", world.weather.rain)
        val offset = (millis() % 500L) / 500.0f
        shader.setFloat("uOffset", -offset)
        shader.setUInt("uIndexLayer", texture!!.shaderId)
    }

    override fun draw() {
        renderWindow.renderSystem.reset(blending = true)
        val windowSize = renderWindow.window.sizef
        if (this.windowSize != windowSize) {
            updateMesh(windowSize)
        }
        shader.use()
        updateShader()
        mesh.draw()
    }

    companion object : OverlayFactory<WeatherOverlay> {
        private val RAIN = "environment/rain".toResourceLocation().texture()
        private val SNOW = "environment/snow".toResourceLocation().texture()

        override fun build(renderWindow: RenderWindow, z: Float): WeatherOverlay {
            return WeatherOverlay(renderWindow, z)
        }
    }
}