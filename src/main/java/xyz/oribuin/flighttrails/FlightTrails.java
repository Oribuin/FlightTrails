package xyz.oribuin.flighttrails;

import xyz.oribuin.flighttrails.cmds.CmdReload;
import xyz.oribuin.flighttrails.cmds.CmdSetColor;
import xyz.oribuin.flighttrails.cmds.CmdSetColorOther;
import xyz.oribuin.flighttrails.cmds.CmdToggleTrail;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import xyz.oribuin.flighttrails.hooks.Metrics;
import xyz.oribuin.flighttrails.hooks.PlaceholderAPIHook;
import xyz.oribuin.flighttrails.hooks.TrailsPlaceholderExpansion;
import xyz.oribuin.flighttrails.listeners.MainEvents;
import xyz.oribuin.flighttrails.persist.ColorU;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FlightTrails extends JavaPlugin {
    public final FileConfiguration config = this.getConfig();

    public void onEnable() {
        /*
         * Variable Defining
         */

        PluginManager pm = Bukkit.getPluginManager();
        FlyHandler flyHandler = new FlyHandler();
        CmdSetColor cmdSetColor = new CmdSetColor(this, flyHandler);

        /*
         * PlaceholderAPI Stuff
         */

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getServer().getConsoleSender().sendMessage(ColorU.cl("&7[&bFlightTrails&7] &cPlaceholderAPI is not installed, therefor PlaceholderAPI placeholders will not work."));
        }

        if (PlaceholderAPIHook.enabled()) {
            new TrailsPlaceholderExpansion(this, flyHandler, cmdSetColor).register();
        }

        /*
         * BStats Metrics
         */

        int pluginId = 6324;
        Metrics metrics = new Metrics(this, pluginId);

        /*
         * Command Registering
         */

        getCommand("ftrail").setExecutor(new CmdToggleTrail(this, flyHandler));
        getCommand("ftreload").setExecutor(new CmdReload(this));
        getCommand("ftset").setExecutor(new CmdSetColor(this, flyHandler));
        getCommand("ftoset").setExecutor(new CmdSetColorOther(this, flyHandler));

        /*
         * Event Registering
         */

        pm.registerEvents(new MainEvents(this, flyHandler), this);

        /*
         * Creation of data.yml
         */

        /*
        FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
        this.saveResource("data.yml", false);
        data.createSection(Data.dustOptionsMap.toString());
         */

        /*
         * Creation of config.yml
         */

        this.saveDefaultConfig();

        /*
         * Startup Message
         */
        this.getServer().getConsoleSender().sendMessage(ColorU.cl(
                "\n\n&e******************\n" +
                        "\n&6Plugin: &f" + this.getDescription().getName() +
                        "\n&6Author: &f" + this.getDescription().getAuthors() +
                        "\n&6Version: &f" + this.getDescription().getVersion() +
                        "\n&6Website: &f" + this.getDescription().getWebsite() +
                        "\n\n&e******************"
        ));
    }
}
