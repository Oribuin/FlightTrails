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

    public CmdGuiOpen(FlyHandler flyHandler, FlightTrails instance) {
        this.flyHandler = flyHandler;
        this.plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            FileConfiguration config = plugin.getConfig();

            if (!player.hasPermission("flytrails.fly.gui")) {
                player.sendMessage(ColorU.cl(config.getString("prefix") + "cmd-permission"));
            }

            ColorSelector colorSelector = ColorSelector.getInstance(flyHandler, plugin);

            colorSelector.guiItems();
            colorSelector.onInventory(player);
        }
        return true;
    }

}