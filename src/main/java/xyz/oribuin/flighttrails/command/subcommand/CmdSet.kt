package xyz.oribuin.flighttrails.command.subcommand

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.enums.ParticleItem
import xyz.oribuin.flighttrails.manager.DataManager
import xyz.oribuin.flighttrails.manager.MessageManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.StringPlaceholders
import xyz.oribuin.orilibrary.SubCommand


class CmdSet(private val plugin: FlightTrails, command: OriCommand) : SubCommand(command, "set") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)


        // If the argument length == 1 and no type or value was defined, send message


        // If the argument length == 1 and no type or value was defined, send message
        if (args.size == 2) {
            messageManager.sendMessage(sender, "set-command.no-type")
            return
        } else if (args.size == 3) {
            messageManager.sendMessage(sender, "set-command.no-value")
            return
        }

        // If the set command does not contain a username

        // If the set command does not contain a username
        if (args.size == 4) {

            // check if player
            if (sender !is Player) {
                messageManager.sendMessage(sender, "player-only")
                return
            }

            // Update sender's particle based on the arguments
            when (args[2].toLowerCase()) {
                "particle" -> {

                    // Check /trails set particle permission
                    if (!sender.hasPermission("flighttrails.set.particle")) {
                        messageManager.sendMessage(sender, "invalid-permission")
                        return
                    }
                    onSetParticle(sender, sender, args[3])
                }
                "item" -> {

                    // Check permission
                    if (!sender.hasPermission("flighttrails.set.item")) {
                        messageManager.sendMessage(sender, "invalid-permission")
                        return
                    }
                    onSetItem(sender, args[3].toUpperCase())
                }
                "block" -> {

                    // Check Permission
                    if (!sender.hasPermission("flighttrails.set.block")) {
                        messageManager.sendMessage(sender, "invalid-permission")
                        return
                    }
                    onSetBlock(sender, args[3].toUpperCase())
                }
                "color" -> {
                    // Check permission
                    if (!sender.hasPermission("flighttrails.set.color")) {
                        messageManager.sendMessage(sender, "invalid-permission")
                        return
                    }
                    onSetColor(sender, sender, args[3])
                }

                else -> {
                    messageManager.sendMessage(sender, "set-command.no-type")
                }
            }
        }

        // if the set command contains a username

        // if the set command contains a username
        if (args.size == 5) {

            // Define player's name
            val mentioned = Bukkit.getPlayer(args[4])

            // If sender has permission to change other's particles
            if (!sender.hasPermission("flighttrails.admin") && !sender.hasPermission("flighttrails.set.other")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            // Check if mentioned is null, offline, vanished
            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                // Send invalid player
                messageManager.sendMessage(sender, "invalid-player")
                return
            }
            when (args[1].toLowerCase()) {
                "particle" -> onSetParticle(sender, mentioned, args[2])
                "item" -> onSetItem(mentioned, args[2].toUpperCase())
                "block" -> onSetBlock(mentioned, args[2].toUpperCase())
                "color" ->                             // Check permission
                    onSetColor(sender, mentioned, args[2])
                else ->
                    messageManager.sendMessage(sender, "set-command.no-type")
            }

            messageManager.sendMessage(sender, "set-command.changed-other", StringPlaceholders.single("player", mentioned.name))
        }
    }

    private fun onSetParticle(sender: CommandSender, player: Player, particleValue: String) {
        // Instantiate the managers
        val messageManager = plugin.getManager(MessageManager::class.java)
        val dataManager = plugin.getManager(DataManager::class.java)

        // Check particle
        val particle: ParticleItem? = try {
            ParticleItem.valueOf(particleValue.toUpperCase())
        } catch (ex: Exception) {
            messageManager.sendMessage(player, "set-command.invalid-particle")
            return
        }

        // Check if player has permission for the particle
        if (!sender.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.particle." + particle?.particle?.name)) {
            messageManager.sendMessage(player, "invalid-permission")
            return
        }

        // Update PlayerData
        dataManager.getOrSetParticle(player, particle?.particle)
        messageManager.sendMessage(player, "set-command.particle", StringPlaceholders.single("particle", particle?.particleName))
    }

    private fun onSetItem(player: Player, itemValue: String) {
        // Instantiate the managers
        val messageManager = plugin.getManager(MessageManager::class.java)
        val dataManager = plugin.getManager(DataManager::class.java)

        // Instantiate the PlayerData and Particle
        val particle = dataManager.getOrSetParticle(player, null)

        // Check required particle
        if (particle !== Particle.ITEM_CRACK) {
            messageManager.sendMessage(player, "set-command.required-particle")
            return
        }

        // Check material included
        val material = Material.getMaterial(itemValue)
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-item")
            return
        }

        // Update ItemStack inside PlayerData
        val item = ItemStack(material)
        dataManager.getOrSetItem(player, item)

        // Send message
        messageManager.sendMessage(player, "set-command.item", StringPlaceholders.single("item", material.name.replace("_", " ").toLowerCase()))
    }

    private fun onSetBlock(player: Player, blockValue: String) {
        // Instantiate the managers
        val messageManager = plugin.getManager(MessageManager::class.java)
        val dataManager = plugin.getManager(DataManager::class.java)

        // Instantiate the PlayerData and Particle
        val particle = dataManager.getOrSetParticle(player, null)

        // Check the required particles
        if (particle !== Particle.BLOCK_CRACK && particle !== Particle.BLOCK_DUST && particle !== Particle.FALLING_DUST) {
            messageManager.sendMessage(player, "set-command.required-particle")
            return
        }

        // Check the required material
        val material = Material.getMaterial(blockValue.toUpperCase())
        if (material == null) {
            messageManager.sendMessage(player, "set-command.invalid-block")
            return
        }

        // Update PlayerData
        dataManager.getOrSetBlock(player, material)

        // Send Message
        messageManager.sendMessage(player, "set-command.block", StringPlaceholders.single("block", material.name.replace("_", " ").toLowerCase()))
    }

    private fun onSetColor(sender: CommandSender, player: Player, colorValue: String) {
        // Instantiate the managers
        val messageManager = plugin.getManager(MessageManager::class.java)
        val dataManager = plugin.getManager(DataManager::class.java)

        // Instantiate the PlayerData and Particle
        val particle = dataManager.getOrSetParticle(player, null)

        // Check required particle
        if (particle !== Particle.REDSTONE) {
            messageManager.sendMessage(player, "set-command.required-particle")
            return
        }

        // Check required Trail Color
        val color: ChatColor = try {
            ChatColor.valueOf(colorValue.toUpperCase())
        } catch (ex: Exception) {
            messageManager.sendMessage(player, "set-command.invalid-color")
            return
        }

        // Check if player has permission for the trail color
        if (!sender.hasPermission("flighttrails.admin") && !player.hasPermission("flighttrails.color." + color.name) && !player.hasPermission("flighttrails.color.custom")) {
            messageManager.sendMessage(player, "invalid-permission")
            return
        }

        // Update player data
        dataManager.getOrSetColor(player, Color.fromRGB(color.color.red, color.color.green, color.color.blue))
        // Send update message

        val rgb = color.color
        messageManager.sendMessage(player, "set-command.color", StringPlaceholders.single("color", "${rgb.red}, ${rgb.green}, ${rgb.blue}"))
    }
}