package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.SubCommand
import xyz.oribuin.flighttrails.library.OriCommand
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager

class CmdToggle(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "toggle") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)

        if (sender !is Player) {
            messageManager.sendMessage(sender, "player-only")
            return
        }

        if (!sender.hasPermission("flighttrails.toggle")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }


        val data = plugin.getManager(DataManager::class)
        // Basically Data#isEnabled
        if (data.getOrSetEnabled(sender, null)) {
            messageManager.sendMessage(sender, "trails-disabled")
            data.getOrSetEnabled(sender, false)
        } else {
            messageManager.sendMessage(sender, "trails-enabled")
            data.getOrSetEnabled(sender, true)
        }

    }
}