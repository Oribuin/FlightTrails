package xyz.oribuin.flighttrails.command.subcommand

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.command.OriCommand
import xyz.oribuin.orilibrary.util.StringPlaceholders
import xyz.oribuin.orilibrary.command.SubCommand

class CmdReload(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "reload") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        if (!sender.hasPermission("flighttrails.reload")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        this.plugin.reload()
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))


    }
}