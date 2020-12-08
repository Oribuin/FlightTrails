package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.menu.MainMenu
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.SubCommand

class CmdMenu(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "menu") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        if (args.size == 2) {
            val mentioned = Bukkit.getPlayer(args[1])

            // Check Permission
            if (!sender.hasPermission("flighttrails.menu.other")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            // Check if player doesnt exist.
            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                messageManager.sendMessage(sender, "invalid-player")
                return
            }

            // Open Menu
            MainMenu(plugin, mentioned).openMenu()
            return
        }

        // Check if player
        if (sender !is Player) {
            messageManager.sendMessage(sender, "player-only")
            return
        }

        // Check permission
        if (!sender.hasPermission("flighttrails.menu")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        // Open Menu
        MainMenu(plugin, sender).openMenu()
    }
}