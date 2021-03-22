package xyz.oribuin.flighttrails.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.orilibrary.command.Argument
import xyz.oribuin.orilibrary.command.Command
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.Nullable

@Command.Info(
    name = "trails",
    description = "Main Command for Flight Trails", usage = "/trails",
    permission = "",
    aliases = [], playerOnly = true
)

class CmdTrails(private val plugin: FlightTrails) : Command(plugin) {

    override fun runFunction(commandSender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>) {
        val player = commandSender as Player?
        this.plugin.particleLoc = player!!.location
    }

    override fun complete(commandSender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>): @Nullable MutableList<Argument>? {
        return null
    }
}