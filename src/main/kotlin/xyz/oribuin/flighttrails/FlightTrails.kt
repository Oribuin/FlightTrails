package xyz.oribuin.flighttrails

import org.bukkit.Location
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.OriPlugin

class FlightTrails : OriPlugin() {

    var particleLoc: Location? = null

    override fun enablePlugin() {


        // Register Commands
        CmdTrails(this).register(this.config, "player-only", "invalid-permission");

        // Register Tasks
        ParticleTask(this)
    }

    override fun disablePlugin() {

    }
}