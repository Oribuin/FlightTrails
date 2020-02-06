package xyz.oribuin.flighttrails.cmds;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.persist.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CmdReload implements CommandExecutor {

    FlightTrails plugin;

    public CmdReload(FlightTrails plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (sender instanceof Player) {
            Player player = (Player) sender;


            /*
             * If the user does not have permission to use the reload command.
             * Print "no-permission" message to the player.
             */

            if (!player.hasPermission("flytrails.reload")) {
                player.sendMessage(Chat.cl(config.getString("no-permission")));
                return true;
            }
        }

        // Reload the configuration
        plugin.reloadConfig();

        sender.sendMessage(Chat.cl(config.getString("reload").replaceAll("\\{version}", plugin.getDescription().getVersion())));
        // Notify Console that the plugin was reloaded.
        Bukkit.getConsoleSender().sendMessage(Chat.cl("&bReloaded " + plugin.getDescription().getName() + " &f(&b" + plugin.getDescription().getVersion() + "&f)"));
        return true;
    }
}
