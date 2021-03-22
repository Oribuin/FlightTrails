package xyz.oribuin.flighttrails

import org.bukkit.Location
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.listener.PlayerJoinLeaveEvents
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.OriPlugin
import java.util.*

class FlightTrails : OriPlugin() {

    var toggleList = mutableListOf<UUID>()
    var particleLoc: Location? = null

    override fun enablePlugin() {


        // Load managers asynchronously
        this.server.scheduler.runTaskAsynchronously(this, Runnable {
            this.getManager(DataManager::class.java)
        })

        // Register Commands
        CmdTrails(this).register(this.config, "player-only", "invalid-permission");

        // Register Listeners.
        this.server.pluginManager.registerEvents(PlayerJoinLeaveEvents(this), this)

        // Register Tasks
        ParticleTask(this)
    }

    override fun disablePlugin() {
        this.toggleList.clear()
    }
}