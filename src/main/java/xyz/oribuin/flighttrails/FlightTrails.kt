package xyz.oribuin.flighttrails

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook
import xyz.oribuin.flighttrails.hook.PlaceholderExp
import xyz.oribuin.flighttrails.library.OriPlugin
import xyz.oribuin.flighttrails.listener.GeneralListener
import xyz.oribuin.flighttrails.manager.ConfigManager
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.manager.ParticleManager
import java.io.File

/**
 * @author Oribuin
 */
class FlightTrails : OriPlugin() {
    override fun enablePlugin() {
        Bukkit.getOnlinePlayers().forEach { player -> player.closeInventory() }


        // Register Managers
        getManager(ConfigManager::class)
        getManager(DataManager::class)
        getManager(MessageManager::class)
        getManager(ParticleManager::class)

        val config = YamlConfiguration.loadConfiguration(File("t"))

        // Register PlaceholderAPI
        if (PlaceholderAPIHook.enabled()) {
            PlaceholderExp(this).register()
        }

        // Register Commands
        registerCommands(CmdTrails(this))

        // Register Listeners
        registerListeners(GeneralListener(this))
    }

    override fun disablePlugin() {
    }

}