package me.oribuin.flighttrails.cmds;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.menus.ColorSelector;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CmdGuiOpen implements CommandExecutor {
    public FlightTrails plugin;
    public FlyHandler flyHandler;

    public CmdGuiOpen(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            FileConfiguration config = plugin.getConfig();

            /*
             If you does not have permission
             return No Permission Message
            */
            if (!player.hasPermission("flytrails.color")) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + config.getString("cmd-permission")));
                return true;
            }

            // Get the ColorSelector Instance.
            ColorSelector colorSelector = ColorSelector.getInstance(plugin, flyHandler);

            // Initialize Items
            colorSelector.guiItems();
            // Open Inventory
            colorSelector.onInventory(player);
        }
        return true;
    }
}