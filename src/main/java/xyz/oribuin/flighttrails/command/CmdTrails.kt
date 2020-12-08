package xyz.oribuin.flighttrails.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.subcommand.CmdMenu
import xyz.oribuin.flighttrails.command.subcommand.CmdReload
import xyz.oribuin.flighttrails.command.subcommand.CmdSet
import xyz.oribuin.flighttrails.command.subcommand.CmdToggle
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.SubCommand

class CmdTrails(plugin: FlightTrails) : OriCommand(plugin, "trails") {
    private val subcommands = mutableListOf<SubCommand>()

    private val messageManager = plugin.getManager(MessageManager::class.java)

    override fun executeCommand(sender: CommandSender, args: Array<String>) {

        if (sender is Player && plugin.getManager(DataManager::class.java).getPlayer(sender) == null) {
            plugin.getManager(DataManager::class.java).setDefault(sender)
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
        val flightTrails = plugin as FlightTrails
        subcommands.addAll(
            listOf(
                CmdMenu(flightTrails, this),
                CmdReload(flightTrails, this),
                CmdSet(flightTrails, this),
                CmdToggle(flightTrails, this)
            )
        )
    }

}