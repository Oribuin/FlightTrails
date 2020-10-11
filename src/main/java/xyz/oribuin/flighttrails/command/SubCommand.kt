package xyz.oribuin.flighttrails.command

import org.bukkit.command.CommandSender
import xyz.oribuin.flighttrails.library.OriCommand

abstract class SubCommand(private val oriCommand: OriCommand, vararg val names: String) {

    abstract fun executeArgument(sender: CommandSender, args: Array<String>)
}