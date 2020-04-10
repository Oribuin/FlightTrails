package xyz.oribuin.flighttrails.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.utilities.Color;
import xyz.oribuin.flighttrails.utilities.MessageUtils;

import java.io.File;
import java.io.IOException;

public class CmdTrails implements CommandExecutor {

    private FlightTrails plugin = FlightTrails.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageUtils messageUtils = new MessageUtils(sender);

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!player.hasPermission("flighttrails.toggle")) {
                    sender.sendMessage(messageUtils.getMessage("invalidPermission"));
                    return true;
                }

                if (getDataConfig().getBoolean(player.getUniqueId() + ".enabled", true)) {
                    getDataConfig().set(player.getUniqueId() + ".enabled", false);
                    sender.sendMessage(messageUtils.getMessage("trailsDisabled"));
                } else {
                    getDataConfig().set(player.getUniqueId() + ".enabled", false);
                    sender.sendMessage(messageUtils.getMessage("trailsEnabled"));
                }

                saveData();
            }
            return true;
        }

        if (args.length == 1) {
            if ("reload".equals(args[0])) {
                if (!sender.hasPermission("flighttrails.reload")) {
                    sender.sendMessage(messageUtils.getMessage("invalidPermission"));
                    return true;
                }

                plugin.reloadConfig();
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
                YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
                sender.sendMessage(messageUtils.getMessage("reload"));
            }
        }

        return true;
    }


    private FileConfiguration getDataConfig() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
    }

    private void saveData() {
        try {
            getDataConfig().save(new File(plugin.getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
