package xyz.oribuin.flighttrails.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

        if (args.length == 0 ) {
            if (sender instanceof Player && sender.hasPermission("flighttrails.toggle")) {

                Player player = (Player) sender;
                if (getData().getBoolean(player.getUniqueId() + ".enabled", true)) {
                    getData().set(player.getUniqueId() + ".enabled", true);
                    sender.sendMessage(messageUtils.getMessage("trailsDisabled"));
                } else {
                    getData().set(player.getUniqueId() + ".enabled", false);
                    sender.sendMessage(messageUtils.getMessage("trailsEnabled"));
                }
                try {
                    getData().save(new File(plugin.getDataFolder(), "data.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return true;
        }


        return true;
    }

    private FileConfiguration getData() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
    }

    private FileConfiguration getMsgs() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    private void saveFiles() {
        try {
            getData().save(new File(plugin.getDataFolder(), "data.yml"));
            getMsgs().save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
