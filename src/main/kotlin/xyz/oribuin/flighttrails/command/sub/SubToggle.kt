package xyz.oribuin.flighttrails.command.sub

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.obj.TrailOptions
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull

@SubCommand.Info(
    names = ["toggle"],
    usage = "/trails toggle",
    permission = "flighttrails.use",
    command = CmdTrails::class
)
@Suppress("UNUSED")
class SubToggle(private val plugin: FlightTrails) : SubCommand(plugin) {

    override fun executeArgument(sender: @NotNull CommandSender, args: Array<String>) {
        val msg = this.plugin.getManager(MessageManager::class.java)
        val data = this.plugin.getManager(DataManager::class.java)

        // Check if sender is player
        if (sender !is Player) {
            msg.sendMessage(sender, "player-only")
            return
        }

        val options = data.getTrailOptions(sender) ?: TrailOptions(sender.uniqueId)
        options.enabled = !options.enabled
        data.saveTrailOptions(options)

        if (options.enabled) msg.sendMessage(sender, "trails-enabled")
        else msg.sendMessage(sender, "trails-disabled")
    }

}