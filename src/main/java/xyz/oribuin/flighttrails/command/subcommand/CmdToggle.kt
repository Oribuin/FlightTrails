package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.StringPlaceholders
import xyz.oribuin.orilibrary.SubCommand

class CmdToggle(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "toggle") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        if (args.size == 2) {
            val mentioned = Bukkit.getPlayer(args[1])

            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                messageManager.sendMessage(sender, "invalid-player")
                return
            }

            val data = plugin.getManager(DataManager::class.java)

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

        val data = plugin.getManager(DataManager::class.java)

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