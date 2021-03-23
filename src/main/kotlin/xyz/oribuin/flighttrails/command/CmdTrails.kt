package xyz.oribuin.flighttrails.command

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.sub.SubToggle
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.command.Argument
import xyz.oribuin.orilibrary.command.Command
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.Nullable

@Command.Info(
    name = "trails",
    description = "Main Command for Flight Trails",
    usage = "/trails",
    permission = "",
    aliases = [],
    playerOnly = false
)

class CmdTrails(private val plugin: FlightTrails) : Command(plugin) {

    override fun runFunction(sender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>) {

        val msg = this.plugin.getManager(MessageManager::class.java)

        if (strings.isNotEmpty()) {
            this.runSubCommands(sender, strings, "unknown-command", "invalid-permission")
            return
        }

        SubToggle(plugin).executeArgument(sender, strings)
    }

    override fun complete(commandSender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>): @Nullable MutableList<Argument> {
        return mutableListOf(Argument(0, arrayOf("reload", "toggle")))
    }
}