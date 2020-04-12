package xyz.oribuin.flighttrails.commands;

import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.listeners.DataSaving;
import xyz.oribuin.flighttrails.utilities.Color;
import xyz.oribuin.flighttrails.utilities.MessageUtils;

import java.io.File;
import java.io.IOException;

public class CmdTrails implements CommandExecutor {

    private FlightTrails plugin = FlightTrails.getInstance();
    private final File file = new File(plugin.getDataFolder(), "data.yml");
    private final FileConfiguration data = getDataConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageUtils messageUtils = new MessageUtils(sender);

        // Okay, Ignore the potential Arrow Code here :o


        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                try {
                    new DataSaving().checkData(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!player.hasPermission("flighttrails.toggle")) {
                    sender.sendMessage(messageUtils.getMessage("invalidPermission"));
                    return true;
                }

                if (getDataConfig().getBoolean(player.getUniqueId() + ".enabled", true)) {
                    data.set(player.getUniqueId() + ".enabled", false);
                    sender.sendMessage(messageUtils.getMessage("trailsDisabled"));
                } else {
                    data.set(player.getUniqueId() + ".enabled", true);
                    sender.sendMessage(messageUtils.getMessage("trailsEnabled"));
                }
                try {
                    data.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        // Reload command
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

        // Set Command
        if (args.length >= 3) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.msg("&cYou do not have access to this command."));
                return true;
            }

            Player player = (Player) sender;

            try {
                new DataSaving().checkData(player);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (args[0].equalsIgnoreCase("set")) {

                Particle particle = Particle.valueOf(getDataConfig().getString(player.getUniqueId() + ".particle"));
                switch (args[1]) {
                    case "particle":
                        if (!sender.hasPermission("flighttrails.set.particle")) {
                            sender.sendMessage(messageUtils.getMessage("invalidPermission"));
                            return true;
                        }

                        sender.sendMessage(messageUtils.getSetType("particle", args[2]));
                        data.set(player.getUniqueId() + ".particle", args[2].toUpperCase());
                        try {
                            data.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "block":
                        if (!sender.hasPermission("flighttrails.set.block")) {
                            sender.sendMessage(messageUtils.getMessage("invalidPermission"));
                            return true;
                        }

                        if (particle != Particle.FALLING_DUST) {
                            sender.sendMessage(messageUtils.getMessage("requiredParticle"));
                            return true;
                        }

                        sender.sendMessage(messageUtils.getSetType("block", args[2]));
                        data.set(player.getUniqueId() + ".block", args[2].toUpperCase());
                        try {
                            data.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "color":

                        // Set Particle
                        // Set Block
                        // Set Color
                        break;
                    default:
                        sender.sendMessage(messageUtils.getUsage("trails"));
                }
            }
        }

        return true;
    }


    private FileConfiguration getDataConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }

    private org.bukkit.Color getColor(String string) {
        switch (string) {
            case "red":
                return org.bukkit.Color.RED;
            case "orange":
                return org.bukkit.Color.ORANGE;
            case "yellow":
                return org.bukkit.Color.YELLOW;
            case "gold":
                return org.bukkit.Color.OLIVE;
            case "green":
                return org.bukkit.Color.LIME;
            case "dark_green":
                return org.bukkit.Color.GREEN;
            case "blue":
                return org.bukkit.Color.BLUE;
            case "aqua":
                return org.bukkit.Color.AQUA;
            case "dark_aqua":
                return org.bukkit.Color.TEAL;
            case "dark_blue":
                return org.bukkit.Color.NAVY;
            case "purple":
                return org.bukkit.Color.PURPLE;
            case "pink":
                return org.bukkit.Color.FUCHSIA;
            case "white":
                return org.bukkit.Color.WHITE;
            case "black":
                return org.bukkit.Color.BLACK;
            case "gray":
                return org.bukkit.Color.GRAY;
            case "light_gray":
                return org.bukkit.Color.SILVER;
            case "brown":
                return org.bukkit.Color.MAROON;
        }

        return org.bukkit.Color.WHITE;
    }
}
