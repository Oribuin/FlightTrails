package xyz.oribuin.flighttrails.command.sub

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.util.PluginUtils
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import java.awt.Color

@SubCommand.Info(
    names = ["switch"],
    usage = "/trails switch",
    permission = "flighttrails.switch",
    command = CmdTrails::class
)
@Suppress("UNUSED")
class SubSwitch(private val plugin: FlightTrails) : SubCommand(plugin) {

    private var iterator = Particle.values().toList().listIterator()
    private var particle = iterator.next()

    override fun executeArgument(sender: @NotNull CommandSender, args: Array<String>) {
        val msg = this.plugin.getManager(MessageManager::class.java)
        val data = this.plugin.getManager(DataManager::class.java)

        // Check if sender is player
        if (sender !is Player) {
            msg.sendMessage(sender, "player-only")
            return
        }


        val options = data.getTrailOptions(sender, false) ?: return
        if (args.size == 3) {
            when (args[1].toLowerCase()) {
                "particle" -> {
                    options.particle = Particle.valueOf(args[2].toUpperCase())
                    data.saveTrailOptions(options)
                }

                "color" -> {
                    options.particleColor = PluginUtils.fromAwtColor(Color.decode(args[2]))
                    data.saveTrailOptions(options)
                }

                "block" -> {
                    options.blockData = Material.matchMaterial(args[2].toUpperCase()) ?: Material.BLACKSTONE
                    data.saveTrailOptions(options)
                }

                "item" -> {
                    options.itemData = ItemStack(Material.matchMaterial(args[2].toUpperCase()) ?: Material.BLACKSTONE)
                    data.saveTrailOptions(options)
                }

                "note" -> {
                    options.note = args[2].toIntOrNull() ?: 0
                    data.saveTrailOptions(options)
                }
            }
        }


    }

}