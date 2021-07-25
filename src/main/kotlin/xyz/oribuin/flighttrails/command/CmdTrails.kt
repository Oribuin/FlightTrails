package xyz.oribuin.flighttrails.command

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.sub.SubHelp
import xyz.oribuin.flighttrails.command.sub.SubReload
import xyz.oribuin.flighttrails.command.sub.SubSet
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
    aliases = ["flighttrails"],
    playerOnly = false,
    subcommands = [SubHelp::class, SubReload::class, SubSet::class, SubToggle::class]
)

class CmdTrails(private val plugin: FlightTrails) : Command(plugin) {

    override fun runFunction(sender: @NotNull CommandSender, s: @NotNull String, strings: Array<String>) {

        if (strings.isNotEmpty()) {
            val msg = this.plugin.getManager(MessageManager::class.java)

            val prefix = msg.config.getString("prefix") ?: MessageManager.Messages.PREFIX.value

            val unknownCommand = prefix + msg.config.getString("unknown-command")
            val noPerm = prefix + msg.config.getString("invalid-permission")

            this.runSubCommands(sender, strings, unknownCommand, noPerm)
            return
        }


        SubToggle(plugin, this).executeArgument(sender, strings)
    }

    override fun completeString(sender: CommandSender, label: String, args: Array<String>): MutableList<String>? {
        val tabComplete = mutableListOf<String>()

        if (!sender.hasPermission("flighttrails.use")) return playerList(sender)

        when (args.size) {
            1 -> tabComplete.addAll(listOf("toggle", "reload", "set", "help"))
            2 -> {
                if (!args[0].equals("set", true)) return null

                tabComplete.addAll(listOf("particle", "block", "item", "color", "transition", "note"))
            }

            3 -> {
                if (!args[0].equals("set", true)) return null

                when (args[1].lowercase()) {
                    "particle" -> tabComplete.addAll(Particle.values()
                        .filter { !this.plugin.config.getStringList("disabled-particles").contains(it.name) }
                        .filter { !it.name.contains("LEGACY") }
                        .filter { !it.name.equals("VIBRATION", true) }
                        .map { it.name.lowercase() })
                    "block" -> tabComplete.addAll(Material.values().filter { it.isBlock && !it.name.endsWith("AIR") }.map { it.name.lowercase() })
                    "item" -> tabComplete.addAll(Material.values().filter { it.isItem && !it.name.endsWith("AIR") }.map { it.name.lowercase() })
                    "color", "colour", "transition" -> tabComplete.addAll(listOf("#HEX", *TrailColor.values().map { it.name.lowercase() }.toTypedArray()))
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