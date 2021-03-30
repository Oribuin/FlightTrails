package xyz.oribuin.flighttrails.command

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.sub.SubToggle
import xyz.oribuin.flighttrails.enums.TrailColor
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.command.Command
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull

@Command.Info(
    name = "trails",
    description = "Main Command for Flight Trails",
    usage = "/trails",
    permission = "flighttrails.use",
    aliases = [],
    playerOnly = false
)

class CmdTrails(private val plugin: FlightTrails) : Command(plugin) {

    override fun runFunction(sender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>) {

        val msg = this.plugin.getManager(MessageManager::class.java)

        if (strings.isNotEmpty()) {
            this.runSubCommands(sender, strings, "unknown-command", "invalid-permission")
            return
        }


        SubToggle(plugin).executeArgument(sender, strings)
    }

    override fun completeString(sender: CommandSender, label: String, args: Array<String>): MutableList<String>? {
        val tabComplete = mutableListOf<String>()

        if (!sender.hasPermission("flighttrails.use")) return playerList(sender)

        when (args.size) {
            1 -> tabComplete.addAll(listOf("toggle", "reload", "set"))
            2 -> {
                if (!args[0].equals("set", true)) return null

                tabComplete.addAll(listOf("particle", "block", "item", "color", "note"))
            }

            3 -> {
                if (!args[0].equals("set", true)) return null

                when (args[1].toLowerCase()) {
                    "particle" -> tabComplete.addAll(Particle.values().map { it.name.toLowerCase() })
                    "block" -> tabComplete.addAll(Material.values().filter { it.isBlock && !it.name.endsWith("AIR") }.map { it.name.toLowerCase() })
                    "item" -> tabComplete.addAll(Material.values().filter { it.isItem && !it.name.endsWith("AIR") }.map { it.name.toLowerCase() })
                    "color", "colour" -> tabComplete.addAll(listOf("#HEX", *TrailColor.values().map { it.name.toLowerCase() }.toTypedArray()))
                    "note" -> for (i in 0..24) tabComplete.add(i.toString())
                }

            }

            4 -> {
                if (!args[0].equals("set", true)) return null

                tabComplete.addAll(playerList(sender))
            }
        }

        return tabComplete
    }

}