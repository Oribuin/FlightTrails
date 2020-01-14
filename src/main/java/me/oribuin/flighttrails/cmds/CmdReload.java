package me.oribuin.flighttrails.cmds;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdReload implements CommandExecutor {
    FlightTrails plugin;

    public CmdReload(FlightTrails instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {

            FileConfiguration config = plugin.getConfig();

            Player player = (Player) sender;

            if (!player.hasPermission("flytrails.reload")) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + " " + config.getString("cmd-permission")));
                return true;
            }

            plugin.reloadConfig();
            config = plugin.getConfig();

            Bukkit.getConsoleSender().sendMessage(ColorU.cl(config.getString("prefix") + " &fReloaded " + plugin.getDescription().getName() + " (&b" + plugin.getDescription().getVersion() + "&f)"));
            player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("reload")));
        }
        return true;
    }
}