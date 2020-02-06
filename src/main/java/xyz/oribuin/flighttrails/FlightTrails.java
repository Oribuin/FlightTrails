package xyz.oribuin.flighttrails;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.oribuin.flighttrails.cmds.CmdReload;
import xyz.oribuin.flighttrails.cmds.CmdSetColor;
import xyz.oribuin.flighttrails.cmds.CmdSetColorOther;
import xyz.oribuin.flighttrails.cmds.CmdToggleTrail;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import xyz.oribuin.flighttrails.listeners.JoinNotification;
import xyz.oribuin.flighttrails.persist.Metrics;
import xyz.oribuin.flighttrails.hooks.PAPI;
import xyz.oribuin.flighttrails.hooks.ExpansionPlaceholders;
import xyz.oribuin.flighttrails.listeners.MainEvents;
import xyz.oribuin.flighttrails.persist.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlightTrails extends JavaPlugin {
    public final FileConfiguration config = this.getConfig();

    public void onEnable() {
        /*
         * Variable Defining
         */

        PluginManager pm = Bukkit.getPluginManager();
        FlyHandler flyHandler = new FlyHandler();

        /*
         * Config Checker
         */


        if (config.get("config-version") == null) {
            Bukkit.getConsoleSender().sendMessage(Chat.cl("&c[WARN] There is no configuration version defined!"));
        }

        if (config.get("config-version") != this.getDescription().getVersion()) {
            Bukkit.getConsoleSender().sendMessage(Chat.cl("&7Your configuration file is out of date, Please update your file."));
        }

        /*
         * PlaceholderAPI Stuff
         */

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            this.getServer().getConsoleSender().sendMessage(Chat.cl("&7[&bFlightTrails&7] &cPlaceholderAPI is not installed, therefor PlaceholderAPI placeholders will not work."));
        }

        if (PAPI.enabled()) {
            new ExpansionPlaceholders(this, flyHandler).register();
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
        pm.registerEvents(new JoinNotification(this), this);

        /*
         * Creation of data.yml
         */

        //copyDefaultResource("data.yml");

        /*
         * Creation of config.yml
         */

        this.saveDefaultConfig();

        /*
         * Startup Message
         */
        this.getServer().getConsoleSender().sendMessage(Chat.cl(
                "\n\n&e******************\n" +
                        "\n&6Plugin: &f" + this.getDescription().getName() +
                        "\n&6Author: &f" + this.getDescription().getAuthors().get(0) +
                        "\n&6Version: &f" + this.getDescription().getVersion() +
                        "\n&6Website: &f" + this.getDescription().getWebsite() +
                        "\n\n&e******************"
        ));
    }

    public void copyDefaultResource(String fileName) {
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) {
            try (InputStream inStream = this.getResource(fileName)) {
                Files.copy(inStream, Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
