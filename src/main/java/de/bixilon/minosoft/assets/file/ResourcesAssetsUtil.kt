package de.bixilon.minosoft.assets.file

import de.bixilon.minosoft.assets.AssetsManager
import de.bixilon.minosoft.assets.directory.DirectoryAssetsManager
import java.io.FileNotFoundException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


object ResourcesAssetsUtil {

    fun create(clazz: Class<*>, canUnload: Boolean = true): AssetsManager {
        val rootResources = clazz.classLoader.getResource("assets") ?: throw FileNotFoundException("Can not find assets folder in $clazz")

        return when (rootResources.protocol) {
            "file" -> DirectoryAssetsManager(rootResources.path, canUnload)// Read them directly from the folder
            "jar" -> {
                val path: String = rootResources.path
                val jarPath = path.substring(5, path.indexOf("!"))
                val zip = URLDecoder.decode(jarPath, StandardCharsets.UTF_8)
                ZipAssetsManager(zip, canUnload = canUnload)
            }
            else -> TODO("Can not read resources: $rootResources")
        }
    }
}