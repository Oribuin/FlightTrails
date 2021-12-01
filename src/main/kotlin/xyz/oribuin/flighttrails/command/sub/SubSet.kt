package xyz.oribuin.flighttrails.command.sub

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.enums.TrailColor
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.util.fromHex
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.util.StringPlaceholders

@SubCommand.Info(
    names = ["switch", "set"],
    usage = "/trails set <block/color/particle/item/note> <value>",
    permission = "flighttrails.set"
)
class SubSet(private val plugin: FlightTrails) : SubCommand() {

    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val msg = this.plugin.getManager(MessageManager::class.java)
        val data = this.plugin.getManager(DataManager::class.java)

        // Check args
        if (args.size < 3) {
            msg.send(sender, "invalid-arguments", StringPlaceholders.single("usage", this.info.usage))
            return
        }

        // Get Player
        val player: Player = if (args.size == 4 && sender.hasPermission("flighttrails.set.other")) {

            if (Bukkit.getPlayer(args[3]) == null) {
                msg.send(sender, "invalid-player")
                return
            }

            Bukkit.getPlayer(args[3]) ?: return

        } else {

            if (sender !is Player) {
                msg.send(sender, "player-only")
                return
            }

            sender
        }

        val options = data.getTrailOptions(player) ?: return
        options.enabled = true

        when (args[1].lowercase()) {
            "particle" -> {

                val particle: Particle

                try {
                    particle = Particle.valueOf(args[2].uppercase())
                } catch (ex: Exception) {
                    msg.send(sender, "invalid-particle")
                    return
                }

                if (particle.name == "VIBRATION" || this.plugin.config.getStringList("disabled-particles").contains(particle.name)) {
                    msg.send(sender, "invalid-particle")
                    return;
                }

                options.particle = particle
                msg.send(player, "set-value", StringPlaceholders.builder("type", "particle").addPlaceholder("value", particle.name.lowercase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "color", "colour" -> {
                val color: Color

                try {
                    color = if (args[2].startsWith("#"))
                        args[2].uppercase().fromHex()
                    else
                        TrailColor.valueOf(args[2].uppercase()).color

                } catch (ex: Exception) {
                    msg.send(sender, "invalid-color")
                    return
                }

                options.particleColor = color
                options.particle = Particle.REDSTONE
                msg.send(player, "set-value", StringPlaceholders.builder("type", "color").addPlaceholder("value", args[2].replace("#", "")).build())
                data.saveTrailOptions(options)
            }

            "transition" -> {
                val color: Color

                try {
                    color = if (args[2].startsWith("#"))
                        args[2].uppercase().fromHex()
                    else
                        TrailColor.valueOf(args[2].uppercase()).color
                } catch (ex: Exception) {
                    msg.send(sender, "invalid-color")
                    return
                }

                options.transitionColor = color
                options.particle = Particle.DUST_COLOR_TRANSITION
                msg.send(player, "set-value", StringPlaceholders.builder("type", "transition").addPlaceholder("value", args[2].replace("#", "")).build())
                data.saveTrailOptions(options)
            }

            "block" -> {
                val material = Material.matchMaterial(args[2].uppercase())
                if (material == null || !material.isBlock || material.name.endsWith("AIR")) {
                    msg.send(sender, "invalid-block")
                    return
                }

                options.blockData = material
                msg.send(player, "set-value", StringPlaceholders.builder("type", "block").addPlaceholder("value", material.name.lowercase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "item" -> {
                val material = Material.matchMaterial(args[2].uppercase())
                if (material == null || !material.isItem || material.name.endsWith("AIR")) {
                    msg.send(sender, "invalid-item")
                    return
                }

                options.itemData = ItemStack(material)
                msg.send(player, "set-value", StringPlaceholders.builder("type", "item").addPlaceholder("value", material.name.lowercase().replace("_", " ")).build())

                data.saveTrailOptions(options)
            }

            "note" -> {
                val note = args[2].toIntOrNull()

                if (note == null || note < 0 || note > 24) {
                    msg.send(sender, "invalid-note")
                    return
                }

                options.note = note
                msg.send(player, "set-value", StringPlaceholders.builder("type", "note").addPlaceholder("value", note).build())
                data.saveTrailOptions(options)
            }

            else -> msg.send(sender, "invalid-arguments", StringPlaceholders.single("usage", this.info.usage))
        }


    }

}