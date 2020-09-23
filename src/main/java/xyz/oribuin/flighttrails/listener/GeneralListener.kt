package xyz.oribuin.flighttrails.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.library.Manager
import xyz.oribuin.flighttrails.manager.ConfigManager
import xyz.oribuin.flighttrails.manager.DataManager

class GeneralListener(val plugin: FlightTrails) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onJoin(event: PlayerJoinEvent) {
        val data = plugin.getManager(DataManager::class)

        if (event.player.hasPermission("flighttrails.use") && ConfigManager.Setting.PS_DEFAULT_ENABLED.boolean) {
            if (data.getConfig().get(event.player.uniqueId.toString()) == null) {
                data.setDefault(event.player)
            }
        }
    }
}