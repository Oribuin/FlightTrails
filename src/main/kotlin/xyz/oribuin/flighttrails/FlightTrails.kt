package xyz.oribuin.flighttrails

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.hook.Expansion
import xyz.oribuin.flighttrails.listener.PlayerJoinLeaveEvents
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.OriPlugin
import java.io.File

class FlightTrails : OriPlugin() {

    var flag: StateFlag? = null

    override fun enablePlugin() {

        // Check if outdated plugin.
        if (this.isOutdated()) return

        // Load Data Manager Asynchronously.
        this.server.scheduler.runTaskAsynchronously(this, Runnable { this.getManager(DataManager::class.java) })

        // Load messages.yml
        val msg = this.getManager(MessageManager::class.java)
        val prefix = msg.config.getString("prefix") ?: MessageManager.MsgSettings.PREFIX.defaultValue.toString()

        // Register Commands
        CmdTrails(this).register(prefix + msg.config.getString("player-only"), prefix + msg.config.getString("invalid-permission"))

        if (this.server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            this.logger.info("Detected PlaceholderAPI... Registering Expansion")
            Expansion(this).register()
        }

        // Register Listeners.
        this.server.pluginManager.registerEvents(PlayerJoinLeaveEvents(this), this)

        // Register Tasks
        ParticleTask(this)
    }

    override fun disablePlugin() {

    }

    override fun onLoad() {

        val plugin = this.server.pluginManager.getPlugin("WorldGuard")

        if (plugin != null && plugin.isEnabled) {
            this.logger.info("Detected WorldGuard... Registering 'flighttrails-trail' flag!")
            val registry = WorldGuard.getInstance().flagRegistry

            val flag = StateFlag("flighttrails-trails", true)
            this.flag = flag
            registry.register(flag)

            return
        }

        this.logger.warning("Failed to detect WorldGuard... Region support has not been enabled.")

    }

    private fun isOutdated(): Boolean {
        val file = File(this.dataFolder, "data.yml")

        if (file.exists()) {
            this.logger.severe("Please reset your FlightTrails Folder for the new update! Disabling plugin... ")
            this.server.pluginManager.disablePlugin(this)
            return true
        }

        return false
    }

}