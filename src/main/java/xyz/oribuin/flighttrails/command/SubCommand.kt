package xyz.oribuin.flighttrails.command

import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import xyz.oribuin.flighttrails.library.OriCommand

abstract class SubCommand(private val oriCommand: OriCommand, vararg val names: String) : Listener {

    abstract fun executeArgument(sender: CommandSender, args: Array<String>)
}