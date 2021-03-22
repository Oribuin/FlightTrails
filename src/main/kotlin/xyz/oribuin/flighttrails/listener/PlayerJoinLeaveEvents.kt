package xyz.oribuin.flighttrails.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.obj.TrailOptions

class PlayerJoinLeaveEvents(private val plugin: FlightTrails) : Listener {

    private val data = this.plugin.getManager(DataManager::class.java)

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun PlayerJoinEvent.onJoin() {
        val options = data.getTrailOptions(player, sqlOnly = false)
        // Save any trail data if it doesn't exist.
        if (options == null) data.saveTrailOptions(TrailOptions(player.uniqueId))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun PlayerQuitEvent.onQuit() {
        val options = data.getTrailOptions(player, sqlOnly = false)

        // Check if the player has any options
        if (options == null) {
            data.saveTrailOptions(TrailOptions(player.uniqueId))
            return
        }

        // Save data
        data.saveTrailOptions(options)
    }
}