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
        FileConfiguration config = plugin.getConfig();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("flytrails.reload")) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + " " + config.getString("cmd-permission")));
                return true;
            }

            // Tell the player it was reloaded.
            player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("reload")));
        }

        // Get the config
        config = plugin.getConfig();
        // Reload Config
        plugin.reloadConfig();

        // Notify Console plugin was reloaded
        Bukkit.getConsoleSender().sendMessage(ColorU.cl(config.getString("prefix") + " &fReloaded " + plugin.getDescription().getName() + " (&b" + plugin.getDescription().getVersion() + "&f)"));
        return true;
    }
}