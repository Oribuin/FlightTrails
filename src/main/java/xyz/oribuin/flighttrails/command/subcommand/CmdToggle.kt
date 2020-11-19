package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.SubCommand
import xyz.oribuin.flighttrails.library.OriCommand
import xyz.oribuin.flighttrails.library.StringPlaceholders
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager

class CmdToggle(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "toggle") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)

        if (args.size == 2) {
            val mentioned = Bukkit.getPlayer(args[1])

            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                messageManager.sendMessage(sender, "invalid-player")
                return
            }

            val data = plugin.getManager(DataManager::class)

            // Basically Data#isEnabled
            if (data.getOrSetEnabled(mentioned, null)) {
                data.getOrSetEnabled(mentioned, false)
                messageManager.sendMessage(sender, "trails-other-disabled", StringPlaceholders.single("player", mentioned.name))
            } else {
                data.getOrSetEnabled(mentioned, true)
                messageManager.sendMessage(sender, "trails-other-enabled", StringPlaceholders.single("player", mentioned.player))
            }
            return
        }

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
            data.getOrSetEnabled(sender, false)
            messageManager.sendMessage(sender, "trails-disabled")
        } else {
            data.getOrSetEnabled(sender, true)
            messageManager.sendMessage(sender, "trails-enabled")
        }

    }
}