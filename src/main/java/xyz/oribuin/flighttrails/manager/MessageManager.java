package xyz.oribuin.flighttrails.manager;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.util.FileUtils;
import xyz.oribuin.flighttrails.util.StringPlaceholders;

public class MessageManager extends Manager {

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
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messageConfig.getString("prefix") + placeholders.apply(this.messageConfig.getString(messageId))));
    }

}
