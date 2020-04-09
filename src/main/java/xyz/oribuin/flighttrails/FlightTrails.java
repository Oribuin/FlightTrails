package xyz.oribuin.flighttrails;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.flighttrails.commands.CmdTrails;
import xyz.oribuin.flighttrails.listeners.DataSaving;
import xyz.oribuin.flighttrails.listeners.ParticleSpawn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FlightTrails extends JavaPlugin {

    private static FlightTrails instance;

    public static FlightTrails getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null)
            getServer().getConsoleSender().sendMessage("[FlightTrails] No PlaceholderAPI, Placeholders will not work.");

        getCommand("trails").setExecutor(new CmdTrails());
        getServer().getPluginManager().registerEvents(new ParticleSpawn(), this);
        getServer().getPluginManager().registerEvents(new DataSaving(), this);

        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.saveDefaultConfig();
        createFile("messages.yml");
        createFile("data.yml");
    }

    private void createFile(String fileName) {
        File file = new File(this.getDataFolder(), fileName);

        if (!file.exists()) {
            try (InputStream inStream = this.getResource(fileName)) {
                Files.copy(inStream, Paths.get(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateConfig() throws IOException {
        FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));

        if (getConfig().get("file-version") == null || !getConfig().getString("file-version").equals(getDescription().getVersion())) {
            System.out.println("» ------------- «");
            System.out.println(" ");
            System.out.println("Updated Configuration File");
            System.out.println("Restart the server to set all the changes");
            System.out.println(" ");
            System.out.println("» ------------- «");

            if (getConfig().getDefaults() != null)
                getConfig().setDefaults(getConfig().getDefaults());

            if (msgConfig.getDefaults() != null)
                msgConfig.setDefaults(msgConfig.getDefaults());

            msgConfig.save(new File(getDataFolder(), "messages.yml"));
            saveConfig();

        }
    }
}
