package xyz.oribuin.flighttrails.library

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.event.Listener
import xyz.oribuin.flighttrails.FlightTrails

/**
 * @author Oribuin
 */
abstract class OriCommand(val plugin: FlightTrails, private val name: String) : TabExecutor, Listener {
    fun register() {
        val cmd = Bukkit.getPluginCommand(name)
        if (cmd != null) {
            cmd.setExecutor(this)
            cmd.tabCompleter = this
        }

        Bukkit.getPluginManager().registerEvents(this, plugin)
        this.addSubCommands()
    }

    /**
     * Execute the plugin's command
     *
     * @param sender The person sending the command
     * @param args   The arguments provided in the command.
     */
    abstract fun executeCommand(sender: CommandSender, args: Array<String>)

    /**
     * The tab complete for the command executed.
     *
     * @param sender The person typing the command.
     * @param args   The arguments provided by sender
     * @return List<String>?
     * */
    abstract fun tabComplete(sender: CommandSender, args: Array<String>): List<String>?

    abstract fun addSubCommands()

    // Execute the command.
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        executeCommand(sender, args)
        return true
    }


    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return tabComplete(sender, args)
    }
}