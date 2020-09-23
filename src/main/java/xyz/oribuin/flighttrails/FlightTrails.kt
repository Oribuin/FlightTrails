package xyz.oribuin.flighttrails

import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook
import xyz.oribuin.flighttrails.hook.PlaceholderExp
import xyz.oribuin.flighttrails.library.OriPlugin
import xyz.oribuin.flighttrails.listener.GeneralListener
import xyz.oribuin.flighttrails.manager.ConfigManager
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.manager.ParticleManager

/**
 * @author Oribuin
 */
class FlightTrails : OriPlugin() {
    override fun enablePlugin() {

        // Register Managers
        getManager(ConfigManager::class)
        getManager(DataManager::class)
        getManager(MessageManager::class)
        getManager(ParticleManager::class)

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
        // Unused
    }

}