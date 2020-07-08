package xyz.oribuin.flighttrails.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook;
import xyz.oribuin.flighttrails.util.FileUtils;
import xyz.oribuin.flighttrails.util.HexUtils;
import xyz.oribuin.flighttrails.util.NMSUtil;
import xyz.oribuin.flighttrails.util.StringPlaceholders;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager extends Manager {
    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#([A-Fa-f0-9]){6}}");

    private final static String MESSAGE_CONFIG = "messages.yml";

    private FileConfiguration messageConfig;

    public MessageManager(FlightTrails plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        FileUtils.createFile(this.plugin, MESSAGE_CONFIG);
        this.messageConfig = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), MESSAGE_CONFIG));
    }

    public void sendMessage(CommandSender sender, String messageId) {
        this.sendMessage(sender, messageId, StringPlaceholders.empty());
    }

    public void sendMessage(CommandSender sender, String messageId, StringPlaceholders placeholders) {

        if (messageConfig.getString(messageId) == null) {
            sender.spigot().sendMessage(TextComponent.fromLegacyText(HexUtils.colorify("{#ff4072}" + messageId + " is null in messages.yml")));
            return;
        }

        if (!messageConfig.getString(messageId).isEmpty()) {
            final String msg = messageConfig.getString("prefix") + placeholders.apply(messageConfig.getString(messageId));

            sender.spigot().sendMessage(TextComponent.fromLegacyText(HexUtils.colorify(this.parsePlaceholders(sender, msg))));
        }
    }

    private String parsePlaceholders(CommandSender sender, String message) {
        if (sender instanceof Player)
            return PlaceholderAPIHook.apply((Player) sender, message);
        return message;
    }

}
