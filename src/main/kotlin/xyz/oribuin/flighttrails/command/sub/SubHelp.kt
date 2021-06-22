package xyz.oribuin.flighttrails.command.sub

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import xyz.oribuin.orilibrary.util.HexUtils


@SubCommand.Info(
    names = ["help"],
    permission = "flighttrails.use",
    usage = "/trails help",
    command = CmdTrails::class
)
@Suppress("UNUSED")
class SubHelp(private val plugin: FlightTrails, cmd: CmdTrails) : SubCommand(plugin, cmd) {

    override fun executeArgument(sender: @NotNull CommandSender, args: Array<String>) {
        val prefix = this.plugin.getManager(MessageManager::class.java).config.getString("prefix") ?: MessageManager.Messages.PREFIX.value

        val helpMessage = mutableListOf<String>()
        val subCommands = command.subCommands

        // Sort alphabetically
        subCommands.sortBy { it.annotation.names[0] }

        // Add all commands to help message
        for (cmd in subCommands) {
            val info = cmd.annotation

            // Check if has permission
            if (info.permission.isNotEmpty() && !sender.hasPermission(info.permission)) continue
            helpMessage.add(HexUtils.colorify("$prefix &b» &f" + info.usage))
        }

        // Send COmmand Messages
        helpMessage.forEach { sender.sendMessage(it) }
    }

}
