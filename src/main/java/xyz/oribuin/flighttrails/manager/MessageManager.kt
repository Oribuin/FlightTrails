package xyz.oribuin.flighttrails.manager

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook
import xyz.oribuin.flighttrails.library.HexUtils.colorify
import xyz.oribuin.flighttrails.library.Manager
import xyz.oribuin.flighttrails.library.StringPlaceholders
import xyz.oribuin.flighttrails.library.StringPlaceholders.Companion.empty
import xyz.oribuin.flighttrails.library.FileUtils.createFile
import java.io.File

class MessageManager(plugin: FlightTrails) : Manager(plugin) {
    lateinit var messageConfig: FileConfiguration

    override fun reload() {
        createFile(plugin, MESSAGE_CONFIG)
        messageConfig = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, MESSAGE_CONFIG))

        for (value in MsgSettings.values()) {
            if (messageConfig.get(value.key) == null) {
                messageConfig.set(value.key, value.defaultValue)
            }

            value.load(messageConfig)
        }

        messageConfig.save(File(plugin.dataFolder, MESSAGE_CONFIG))
    }


    @JvmOverloads
    fun sendMessage(sender: CommandSender, messageId: String, placeholders: StringPlaceholders = empty()) {
        if (messageConfig.getString(messageId) == null) {
            sender.spigot().sendMessage(*TextComponent.fromLegacyText(colorify("#ff4072$messageId is null in messages.yml")))
            return
        }

        if (messageConfig.getString(messageId)!!.isNotEmpty()) {
            val msg = messageConfig.getString("prefix") + placeholders.apply(messageConfig.getString(messageId)!!)
            sender.spigot().sendMessage(*TextComponent.fromLegacyText(colorify(parsePlaceholders(sender, msg))))
        }
    }

    private fun parsePlaceholders(sender: CommandSender, message: String): String {
        return if (sender is Player)
            PlaceholderAPIHook.apply(sender, message)
        else
            message
    }

    companion object {
        private const val MESSAGE_CONFIG = "messages.yml"
    }

    override fun disable() {
        // Unused
    }

    enum class MsgSettings(val key: String, val defaultValue: Any) {
        // Misc Stuff
        PREFIX("prefix", "<g:#12c2e9:#c471ed:#f64f59>FlightTrails #283048» "),

        // Toggle Command
        TRAILS_ENABLED("trails-enabled", "&bYou have enabled your flight trail."),
        TRAILS_DISABLED("trails-disabled", "&bYou have disabled your flight trail"),

        // /trails toggle <player>
        TOGGLED_OTHER_ENABLED("toggle-other-enabled", "&bYou have enabled %player%''s flight trail."),
        TOGGLED_OTHER_DISABLED("toggle-other-disabled", "&bYou have disabled %player%''s flight trail."),

        // Set command
        SET_PARTICLE("set-command.particle", "&bYour particle was changed to %particle%!"),
        SET_ITEM("set-command.item", "&bYour item was changed to %item%."),
        SET_BLOCK("set-command.block", "&bYour block was changed to %block%."),
        SET_COLOR("set-command.color", "&bYour color was changed to %color%."),
        SET_NO_TYPE("set-command.no-type", "&bYou did not provide a type to set."),
        SET_NO_VALUE("set-command.no-value", "&bYour did not provide a value to set."),
        SET_INVALID_PARTICLE("set-command.invalid-particle", "&bPlease provide a valid particle effect."),
        SET_INVALID_ITEM("set-command.invalid-item", "&bPlease provide a valid item."),
        SET_INVALID_BLOCK("set-command.invalid-block", "&bPlease provide a valid block. "),
        SET_INVALID_COLOR("set-command.invalid-color", "&bPlease provide a valid color."),
        SET_REQUIRED_PARTICLE("set-command.required-particle", "&bYou do not have the right particle for this."),
        SET_CHANGED_OTHER("set-command.changed-other", "&bYou have changed %player%''s trails."),

        // Misc
        RELOAD("reload", "&bYou have reloaded FlightTrails (&b%version%&b)."),
        INVALID_PERMISSION("invalid-permission", "&cInsufficient permission."),
        INVALID_PLAYER("invalid-player", "&cInsufficient player provided."),
        INVALID_HEX("invalid-hex", "&cInsufficient Hex Code."),
        PLAYER_ONLY("player-only", "&cOnly a player can execute this command."),
        UNKNOWN_COMMAND("unknown-command", "&cAn unknown command was entered.");


        private var value: Any? = null

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        val boolean: Boolean
            get() = value as Boolean

        /**
         * @return the setting as a String
         */
        val string: String
            get() = value as String

        /**
         * @return the setting as a string list
         */
        val stringList: List<*>
            get() = value as List<*>

        /**
         * Loads the value from the config and caches it
         */
        fun load(config: FileConfiguration) {
            value = config[key]
        }

    }
}