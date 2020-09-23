package xyz.oribuin.flighttrails.menu

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.util.FileUtils.createMenuFile
import java.io.File

abstract class Menu(val plugin: FlightTrails, private val guiName: String) : Listener {
    val menuConfig: FileConfiguration

    init {
        createMenuFile(plugin, guiName)
        menuConfig = YamlConfiguration.loadConfiguration(menuFile)
    }

    fun reload() {
        createMenuFile(plugin, guiName)
        YamlConfiguration.loadConfiguration(menuFile)
    }

    fun getGuiName(): String {
        return guiName.toLowerCase()
    }

    private val menuFile: File
        get() = File("${plugin.dataFolder}${File.separator}menus", getGuiName() + ".yml")
}

