package xyz.oribuin.flighttrails

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.listener.PlayerJoinLeaveEvents
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.OriPlugin
import java.util.*

class FlightTrails : OriPlugin() {

    var toggleList = mutableListOf<UUID>()
    lateinit var flag: StateFlag


    override fun enablePlugin() {

        // Load managers asynchronously
        this.server.scheduler.runTaskAsynchronously(this, Runnable {
            this.getManager(DataManager::class.java)
            this.getManager(MessageManager::class.java)
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

    override fun onLoad() {

        this.server.pluginManager.getPlugin("WorldGuard") ?: return

        val registry = WorldGuard.getInstance().flagRegistry

        val flag = StateFlag("flighttrails-trails", true)
        registry.register(flag)
        this.flag = flag

    }

}