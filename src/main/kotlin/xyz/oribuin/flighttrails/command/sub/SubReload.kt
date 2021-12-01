package xyz.oribuin.flighttrails.command.sub

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.command.CmdTrails
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.flighttrails.task.ParticleTask
import xyz.oribuin.orilibrary.command.SubCommand
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.util.concurrent.CompletableFuture

@SubCommand.Info(
    names = ["reload"],
    usage = "/trails reload",
    permission = "flighttrails.reload"
)
class SubReload(private val plugin: FlightTrails) : SubCommand() {

    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val msg = this.plugin.getManager(MessageManager::class.java)
        // Reload the plugin
        this.plugin.reload()


        // Cancel Task & Restart it.
        this.plugin.server.scheduler.cancelTasks(this.plugin)
        ParticleTask(this.plugin)

        // Send Message
        msg.send(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))


    }

}