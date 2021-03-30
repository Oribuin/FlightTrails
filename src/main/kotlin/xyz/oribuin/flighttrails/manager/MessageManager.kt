package xyz.oribuin.flighttrails.manager

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.io.File

class MessageManager(private val plugin: FlightTrails) : Manager(plugin) {

    lateinit var config: FileConfiguration

    override fun enable() {
        FileUtils.createFile(plugin, MESSAGE_CONFIG)
        config = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, MESSAGE_CONFIG))

        for (value in MsgSettings.values()) {
            if (config.get(value.key) == null) {
                config.set(value.key, value.defaultValue)
            }
            value.load(config)
        }

        config.save(File(plugin.dataFolder, MESSAGE_CONFIG))
    }


    fun sendMessage(sender: CommandSender, messageId: String, placeholders: StringPlaceholders = StringPlaceholders.empty()) {
        if (config.getString(messageId) == null) {
            sender.sendMessage(colorify("#ff4072$messageId is null in messages.yml"))
            return
        }

        if ((config.getString(messageId) ?: return).isNotEmpty()) {
            val msg = colorify("${config.getString("prefix") ?: MsgSettings.PREFIX.defaultValue}" + placeholders.apply(config.getString(messageId) ?: "#ff4072$messageId is null in messages.yml"))
            sender.sendMessage(msg)
        }
    }

    companion object {
        private const val MESSAGE_CONFIG = "messages.yml"
    }

    override fun disable() {
        // Unused
    }

    enum class MsgSettings(val key: String, val defaultValue: Any) {
        // Misc Stuff
        PREFIX("prefix", "&b&lFlightTrails &8| &f"),
        TRAILS_ENABLED("trails-enabled", "You have enabled your flight trails."),
        TRAILS_DISABLED("trails-disabled", "You have disabled your flight trails."),
        RELOAD("reload", "You have reloaded FlightTrails (%version%&f)"),
        SET_VALUE("set-value", "You have set your trail %type% to %value%"),

        // Error Messages
        INVALID_PLAYER("invalid-player", "&cPlease provide a valid player name."),
        INVALID_PARTICLE("invalid-particle", "&cPlease provide a valid particle."),
        INVALID_COLOR("invalid-color", "&cPlease provide a valid color."),
        INVALID_BLOCK("invalid-block", "&cPlease provide a valid block."),
        INVALID_ITEM("invalid-item", "&cPlease provide a valid item."),
        INVALID_NOTE("invalid-note", "&cPlease provide a valid number between 0-24"),

        UNKNOWN_COMMAND("unknown-command", "&cPlease provide a valid command."),
        INVALID_ARGUMENTS("invalid-arguments", "&cPlease provide valid arguments. Correct usage is %usage%"),
        INVALID_PERMISSION("invalid-permission", "&cYou do not have permission for this command."),
        PLAYER_ONLY("player-only", "&cOnly a player can execute this command.");

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