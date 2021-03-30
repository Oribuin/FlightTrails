package xyz.oribuin.flighttrails.command.sub

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.enums.TrailColor
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.flighttrails.util.PluginUtils
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import xyz.oribuin.orilibrary.util.HexUtils
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.lang.Exception

@SubCommand.Info(
    names = ["switch", "set"],
    usage = "/trails set <block/color/particle/item> <value> [Player]",
    permission = "flighttrails.set",
    command = CmdTrails::class
)
@Suppress("UNUSED")
class SubSet(private val plugin: FlightTrails) : SubCommand(plugin) {

    override fun executeArgument(sender: @NotNull CommandSender, args: Array<String>) {
        val msg = this.plugin.getManager(MessageManager::class.java)
        val data = this.plugin.getManager(DataManager::class.java)

        // Check args
        if (args.size < 3) {
            msg.sendMessage(sender, "invalid-arguments", StringPlaceholders.single("usage", this.annotation.usage))
            return
        }

        // Get Player
        val player: Player = if (args.size == 4 && sender.hasPermission("flighttrails.set.other")) {

            if (Bukkit.getPlayer(args[3]) == null) {
                msg.sendMessage(sender, "invalid-player")
                return
            }

            Bukkit.getPlayer(args[3]) ?: return

        } else {

            if (sender !is Player) {
                msg.sendMessage(sender, "player-only")
                return
            }

            sender
        }

        if (!this.plugin.toggleList.contains(player.uniqueId)) {
            msg.sendMessage(sender, "trails-enabled")
            this.plugin.toggleList.add(player.uniqueId)
        }

        val options = data.getTrailOptions(player, false) ?: return

        when (args[1].toLowerCase()) {
            "particle" -> {

                val particle: Particle

                try {
                    particle = Particle.valueOf(args[2].toUpperCase())
                } catch (ex: Exception) {
                    msg.sendMessage(sender, "invalid-particle")
                    return
                }

                if (this.plugin.config.getStringList("disabled-particles").contains(particle.name)) {
                    msg.sendMessage(sender, "invalid-particle")
                    return;
                }

                options.particle = particle
                msg.sendMessage(player, "set-value", StringPlaceholders.builder("type", "particle").addPlaceholder("value", particle.name.toLowerCase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "color" -> {
                val color: Color

                try {
                    color = if (args[2].startsWith("#")) PluginUtils.fromAwtColor(java.awt.Color.decode(args[2].toUpperCase())) else TrailColor.valueOf(args[2].toUpperCase()).color
                } catch (ex: Exception) {
                    msg.sendMessage(sender, "invalid-color")
                    return
                }
                
                options.particleColor = color
                msg.sendMessage(player, "set-value", StringPlaceholders.builder("type", "color").addPlaceholder("value", args[2].replace("#", "")).build())
                data.saveTrailOptions(options)
            }

            "block" -> {
                val material = Material.matchMaterial(args[2].toUpperCase())
                if (material == null || !material.isBlock || material.name.endsWith("AIR")) {
                    msg.sendMessage(sender, "invalid-block")
                    return
                }

                options.blockData = material
                msg.sendMessage(player, "set-value", StringPlaceholders.builder("type", "block").addPlaceholder("value", material.name.toLowerCase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "item" -> {
                val material = Material.matchMaterial(args[2].toUpperCase())
                if (material == null || !material.isItem || material.name.endsWith("AIR")) {
                    msg.sendMessage(sender, "invalid-item")
                    return
                }

                options.itemData = ItemStack(material)
                msg.sendMessage(player, "set-value", StringPlaceholders.builder("type", "item").addPlaceholder("value", material.name.toLowerCase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "note" -> {
                val note = args[2].toIntOrNull()

                if (note == null || note < 0 || note > 24) {
                    msg.sendMessage(sender, "invalid-note")
                    return
                }

                options.note = note
                msg.sendMessage(player, "set-value", StringPlaceholders.builder("type", "note").addPlaceholder("value", note).build())
                data.saveTrailOptions(options)
            }

            else -> msg.sendMessage(sender, "invalid-arguments", StringPlaceholders.single("usage", this.annotation.usage))
        }


    }

}