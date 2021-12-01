package xyz.oribuin.flighttrails.command.sub

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.command.Command
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.util.HexUtils


@SubCommand.Info(
    names = ["help"],
    permission = "flighttrails.use",
    usage = "/trails help"
)
class SubHelp(private val plugin: FlightTrails) : SubCommand() {

    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val prefix = this.plugin.getManager(MessageManager::class.java).config.getString("prefix") ?: MessageManager.Messages.PREFIX.value

        val helpMessage = mutableListOf<String>()
        val info = (CmdTrails::class.java).getAnnotation(Command.Info::class.java)

        val subCommands = info.subCommands.map { it.java.getAnnotation(Info::class.java) }

        // Sort alphabetically
        subCommands.sortedBy { it.names[0] }

        // Add all commands to help message
        for (cmd in subCommands) {

            // Check if has permission
            if (info.permission.isNotEmpty() && !sender.hasPermission(info.permission)) continue
            helpMessage.add(HexUtils.colorify("$prefix &b» &f" + info.usage))
        }

        // Send Command Messages
        helpMessage.forEach { sender.sendMessage(it) }
    }

}
