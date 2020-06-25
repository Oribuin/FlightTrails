package xyz.oribuin.flighttrails.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.hook.PlaceholderAPIHook;
import xyz.oribuin.flighttrails.util.FileUtils;
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
        final String msg = this.messageConfig.getString("prefix") + placeholders.apply(this.messageConfig.getString(messageId));

        if (Bukkit.getServer().getClass().getPackage().getName().contains("1.16")) {
            sender.spigot().sendMessage(TextComponent.fromLegacyText((this.parseColors(this.parsePlaceholders(sender, msg)))));
        } else {
            sender.sendMessage(this.parseColors(this.parsePlaceholders(sender, msg)));
        }
    }


    // Thank you esophose for saving me here
    private String parseColors(String message) {
        String parsed = message;

        if (Bukkit.getServer().getClass().getPackage().getName().contains("1.16")) {
            Matcher matcher = HEX_PATTERN.matcher(parsed);
            while(matcher.find()) {
                String hexString = matcher.group();
                hexString = hexString.substring(1, hexString.length() - 1);
                ChatColor hexColor = ChatColor.of(hexString);
                String before = parsed.substring(0, matcher.start());
                String after = parsed.substring(matcher.end());
                parsed = before + hexColor + after;
            }
        }

        return ChatColor.translateAlternateColorCodes('&', parsed);
    }

    private String parsePlaceholders(CommandSender sender, String message) {
        if (sender instanceof Player)
            return PlaceholderAPIHook.apply((Player) sender, message);
        return message;
    }

}
