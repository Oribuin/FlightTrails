package me.oribuin.flighttrails;

import me.oribuin.flighttrails.cmds.CmdReload;
import me.oribuin.flighttrails.cmds.CmdGuiOpen;
import me.oribuin.flighttrails.cmds.CmdSetColor;
import me.oribuin.flighttrails.cmds.CmdToggleTrail;
import me.oribuin.flighttrails.handlers.FlyHandler;
import me.oribuin.flighttrails.listeners.MainEvents;
import me.oribuin.flighttrails.menus.ColorSelector;
import me.oribuin.flighttrails.persist.ColorU;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FlightTrails extends JavaPlugin {
    public final FileConfiguration config = this.getConfig();

    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();

        FlyHandler flyHandler = new FlyHandler();
        ColorSelector menu = ColorSelector.getInstance(this, flyHandler);

        getCommand("ftrail").setExecutor(new CmdToggleTrail(flyHandler, this));
        getCommand("ftcolor").setExecutor(new CmdGuiOpen( this, flyHandler));
        getCommand("ftreload").setExecutor(new CmdReload(this));
        getCommand("ftset").setExecutor(new CmdSetColor(flyHandler, this));

        pm.registerEvents(menu, this);
        pm.registerEvents(new MainEvents(this, flyHandler), this);

        this.saveDefaultConfig();

        this.getServer().getConsoleSender().sendMessage(ColorU.cl(
                "\n\n&e******************\n" +
                        "\n&6Plugin: &f" + this.getDescription().getName() +
                        "\n&6Author: &f" + this.getDescription().getAuthors() +
                        "\n&6Version: &f" + this.getDescription().getVersion() +
                        "\n\n&e******************"
        ));
    }

    public void onDisable() {

    }
}
