package xyz.oribuin.flighttrails.library

import org.bukkit.plugin.Plugin
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object FileUtils {
    /**
     * Creates a file on disk from a file located in the jar
     *
     * @param plugin The plugin the file is being created in
     * @param fileName The name of the file to create
     */
    @JvmStatic
    fun createFile(plugin: Plugin, fileName: String) {
        val file = File(plugin.dataFolder, fileName)

        if (!file.exists()) {
            plugin.getResource(fileName).use { inStream ->
                if (inStream == null) {
                    file.createNewFile()
                    return
                }

                Files.copy(inStream, Paths.get(file.absolutePath))
            }
        }
    }

    /**
     * Creates a menu file on disk from a file located in the jar
     *
     * @param plugin The plugin the file is being created in
     * @param fileName The name of the file to create
     */
    @JvmStatic
    fun createMenuFile(plugin: Plugin, fileName: String) {
        val dir = File(plugin.dataFolder, "menus")
        val file = File(dir, "$fileName.yml")

        if (!dir.exists())
            dir.mkdirs()

        if (!file.exists()) {
            plugin.getResource("menus/${fileName}.yml").use { inputStream ->
                if (inputStream == null) {
                    file.createNewFile()
                    return
                }

                Files.copy(inputStream, Paths.get(file.absolutePath))
            }
        }
    }
}