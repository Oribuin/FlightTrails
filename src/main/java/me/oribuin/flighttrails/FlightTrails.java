package me.oribuin.flighttrails;

import me.oribuin.flighttrails.cmds.CmdReload;
import me.oribuin.flighttrails.cmds.CmdGuiOpen;
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
        ColorSelector menu = ColorSelector.getInstance(flyHandler, this);
        getCommand("ft").setExecutor(new CmdToggleTrail(flyHandler, this));

        getCommand("ftcolor").setExecutor(new CmdGuiOpen(flyHandler, this));
        getCommand("ftreload").setExecutor(new CmdReload(this));

        pm.registerEvents(menu, this);
        pm.registerEvents(new MainEvents(flyHandler), this);

        this.saveDefaultConfig();

        this.getServer().getConsoleSender().sendMessage(ColorU.cl(
                "\n\n&9******************\n" +
                        "\n&bPlugin: &f" + this.getDescription().getName() +
                        "\n&bAuthor: &f" + this.getDescription().getAuthors() +
                        "\n&bVersion: &f" + this.getDescription().getVersion() +
                        "\n\n&9******************"
        ));
    }

    public void onDisable() {

    }
}
