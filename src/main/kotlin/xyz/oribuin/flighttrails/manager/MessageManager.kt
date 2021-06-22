package xyz.oribuin.flighttrails.manager

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import xyz.oribuin.flighttrails.FlightTrails
import xyz.oribuin.orilibrary.manager.Manager
import xyz.oribuin.orilibrary.util.FileUtils
import xyz.oribuin.orilibrary.util.HexUtils.colorify
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.io.File

class MessageManager(private val plugin: FlightTrails) : Manager(plugin) {

    lateinit var config: FileConfiguration

    override fun enable() {
        this.config = YamlConfiguration.loadConfiguration(FileUtils.createFile(this.plugin, "messages.yml"))

        // Set any values that dont exist.
        for (msg in Messages.values()) {

            val key = msg.name.lowercase().replace("_", "-")

            if (config.get(key) == null) {
                config.set(key, msg.value)
            }

        }

        config.save(File(plugin.dataFolder, "messages.yml"))

    }

    /**
     * Send a configuration message without any placeholders
     *
     * @param receiver  The CommandSender who receives the message.
     * @param messageId The message path
     */
    fun send(receiver: CommandSender, messageId: String) {
        this.send(receiver, messageId, StringPlaceholders.empty())
    }

    /**
     * Send a configuration messageId with placeholders.
     *
     * @param receiver     The CommandSender who receives the messageId.
     * @param messageId    The messageId path
     * @param placeholders The Placeholders
     */
    fun send(receiver: CommandSender, messageId: String, placeholders: StringPlaceholders) {
        val msg = this.config.getString(messageId)

        if (msg == null) {
            receiver.sendMessage(colorify("&c&lError &8| &fThis is an invalid message in the messages file, Please contact the server owner about this issue. (Id: $messageId)"))
            return
        }

        receiver.sendMessage(colorify(apply(receiver, placeholders.apply(msg))))
    }

    /**
     * Send a raw message to the receiver without any placeholders
     *
     *
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver The message receiver
     * @param message  The raw message
     */
    fun sendRaw(receiver: CommandSender, message: String?) {
        this.sendRaw(receiver, message, StringPlaceholders.empty())
    }

    /**
     * Send a raw message to the receiver with placeholders.
     *
     *
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver     The message receiver
     * @param message      The message
     * @param placeholders Message Placeholders.
     */
    fun sendRaw(receiver: CommandSender, message: String?, placeholders: StringPlaceholders) {
        receiver.sendMessage(colorify(apply(receiver, placeholders.apply(message))))
    }

    fun apply(sender: CommandSender, text: String): String {
        return if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            text
        else PlaceholderAPI.setPlaceholders(if (sender is Player) sender else null, text)
    }

    override fun disable() {

    }

    /**
     * Define all the configuration values.
     */
    enum class Messages(val value: String) {

        // Misc Stuff
        PREFIX("&b&lFlightTrails &8| &f"),
        TRAILS_ENABLED("You have enabled your flight trails."),
        TRAILS_DISABLED("You have disabled your flight trails."),
        RELOAD("You have reloaded FlightTrails (%version%&f)"),
        SET_VALUE("You have set your trail %type% to %value%"),

        // Error Messages
        INVALID_PLAYER("&cPlease provide a valid player name."),
        INVALID_PARTICLE("&cPlease provide a valid particle."),
        INVALID_COLOR("&cPlease provide a valid color."),
        INVALID_BLOCK("&cPlease provide a valid block."),
        INVALID_ITEM("&cPlease provide a valid item."),
        INVALID_NOTE("&cPlease provide a valid number between 0-24"),

        UNKNOWN_COMMAND("&cPlease provide a valid command."),
        INVALID_ARGUMENTS("&cPlease provide valid arguments. Correct usage is %usage%"),
        INVALID_PERMISSION("&cYou do not have permission for this command."),
        PLAYER_ONLY("&cOnly a player can execute this command.");
    }

}