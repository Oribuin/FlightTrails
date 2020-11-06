package xyz.oribuin.flighttrails.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.subcommand.*
import xyz.oribuin.flighttrails.library.OriCommand
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager

class CmdTrails(plugin: FlightTrails) : OriCommand(plugin, "trails") {
    private val subcommands = mutableListOf<SubCommand>()

    private val messageManager = plugin.getManager(MessageManager::class)

    override fun executeCommand(sender: CommandSender, args: Array<String>) {

        if (sender is Player && plugin.getManager(DataManager::class).getPlayer(sender) == null) {
            plugin.getManager(DataManager::class).setDefault(sender)
        }

        for (cmd in subcommands) {
            if (args.isEmpty()) {
                messageManager.sendMessage(sender, "unknown-command")
                break
            }

            if (args.isNotEmpty() && cmd.names.contains(args[0].toLowerCase())) {
                cmd.executeArgument(sender, args)
                break
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val suggestions = mutableListOf<String>()
        if (args.isEmpty() || args.size == 1) {
            val subCommand = if (args.isEmpty()) "" else args[0]

            val commands = mutableListOf<String>()

            if (sender.hasPermission("flighttrails.help"))
                commands.add("help")

            if (sender.hasPermission("flighttrails.reload"))
                commands.add("reload")

            if (sender.hasPermission("flighttrails.menu"))
                commands.add("menu")

            if (sender.hasPermission("flighttrails.toggle"))
                commands.add("toggle")


            StringUtil.copyPartialMatches(subCommand, commands, suggestions)
        } else if (args.size == 2) {
            if (args[0].toLowerCase() == "menu" && sender.hasPermission("flighttrails.menu.other")) {
                val players = mutableListOf<String>()
                Bukkit.getOnlinePlayers().stream().filter { player -> !player.hasMetadata("vanished") }.forEach { player -> players.add(player.name) }

                StringUtil.copyPartialMatches(args[1].toLowerCase(), players, suggestions)

            }
        } else {
            return null
        }
        return suggestions
    }

    override fun addSubCommands() {
        subcommands.addAll(listOf(CmdMenu(plugin, this), CmdReload(plugin, this), CmdToggle(plugin, this)))
    }

}