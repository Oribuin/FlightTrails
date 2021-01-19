package xyz.oribuin.flighttrails

import org.bukkit.Bukkit
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.hook.PAPIHook
import xyz.oribuin.flighttrails.hook.PlaceholderExp
import xyz.oribuin.flighttrails.listener.GeneralListener
import xyz.oribuin.flighttrails.manager.ConfigManager
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.manager.ParticleManager
import xyz.oribuin.orilibrary.OriPlugin

/**
 * @author Oribuin
 */
class FlightTrails : OriPlugin() {

    override fun enablePlugin() {
        Bukkit.getOnlinePlayers().forEach { player -> player.closeInventory() }

        // Register Managers
        getManager(ConfigManager::class.java)
        getManager(DataManager::class.java)
        getManager(MessageManager::class.java)
        getManager(ParticleManager::class.java)

        // Register PlaceholderAPI
        if (PAPIHook.enabled()) {
            PlaceholderExp(this).register()
        }

        // Register Commands
        CmdTrails(this).register()

        Bukkit.getPluginManager().registerEvents(GeneralListener(this), this)
    }


    override fun disablePlugin() {

    }

}