package xyz.oribuin.flighttrails.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.library.OriCommand
import xyz.oribuin.flighttrails.menu.ColorMenu

class CmdTrails(plugin: FlightTrails) : OriCommand(plugin, "trails") {
    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        if (sender is Player) {
            ColorMenu(plugin as FlightTrails, sender).openMenu()
            return
        }

        this.plugin.reload()
        this.plugin.reloadConfig()
        println("Reloaded plugin.")
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }

}