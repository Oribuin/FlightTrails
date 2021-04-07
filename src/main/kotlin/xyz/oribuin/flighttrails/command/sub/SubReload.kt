package xyz.oribuin.flighttrails.command.sub

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.libs.jetbrains.annotations.NotNull
import xyz.oribuin.orilibrary.util.StringPlaceholders

@SubCommand.Info(
    names = ["reload"],
    usage = "/trails reload",
    permission = "flighttrails.reload",
    command = CmdTrails::class
)
@Suppress("UNUSED")
class SubReload(private val plugin: FlightTrails) : SubCommand(plugin) {

    override fun executeArgument(sender: @NotNull CommandSender, args: Array<String>) {

        this.plugin.logger.warning("Reloading Plugin!")
        // Reload Configs
        this.plugin.reloadConfig()
        this.plugin.reload()

        // Cancel Task & Restart it.
        this.plugin.server.scheduler.cancelTasks(this.plugin)
        ParticleTask(this.plugin)

        // Send Message
        this.plugin.getManager(MessageManager::class.java).sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))


    }

}