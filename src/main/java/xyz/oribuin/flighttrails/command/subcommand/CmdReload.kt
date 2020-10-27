package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.SubCommand
import xyz.oribuin.flighttrails.library.OriCommand
import xyz.oribuin.flighttrails.library.StringPlaceholders
import xyz.oribuin.flighttrails.manager.MessageManager

class CmdReload(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "reload") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)

        if (!sender.hasPermission("flighttrails.reload")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        this.plugin.reload()
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))


    }
}