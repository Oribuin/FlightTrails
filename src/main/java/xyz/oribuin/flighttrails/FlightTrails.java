package xyz.oribuin.flighttrails;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.flighttrails.commands.CmdTrails;
import xyz.oribuin.flighttrails.listeners.DataSaving;

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
        getServer().getPluginManager().registerEvents(new DataSaving(), this);

        try {
            updateConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.saveDefaultConfig();
        createFile("messages.yml");
        createFile("data.yml");


        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasMetadata("vanished"))
                    return;

                if (!player.hasPermission("flighttrails.use"))
                    return;

                if (getDataConfig().getBoolean(player.getUniqueId() + ".enabled", true)) {
                    if (!getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
                        if (getConfig().getBoolean("conditions.creative-fly", true) && player.isFlying())
                            spawnParticles(player);
                        else if (getConfig().getBoolean("conditions.elytra", true) && player.isGliding())
                            spawnParticles(player);
                    }
                }
            }
        }, 0, 1);
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

    private FileConfiguration getDataConfig() {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
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


    public void spawnParticles(Player player) {

        // Particle Type
        Particle particle = Particle.valueOf(getDataConfig().getString(player.getUniqueId() + ".particle"));
        // Particle Settings
        int particleCount = getConfig().getInt("particle-settings.count");
        int particleSize = getConfig().getInt("particle-settings.size");

        // Particle Data
        Particle.DustOptions color = new Particle.DustOptions(getDataConfig().getColor(player.getUniqueId() + ".color"), particleSize);
        ItemStack itemStack = getDataConfig().getItemStack(player.getUniqueId() + ".item");
        BlockData blockData = Material.valueOf(getDataConfig().getString(player.getUniqueId() + ".block")).createBlockData();

        // I feel like this is not the right way to do it, Deal with it
        switch (particle) {
            case REDSTONE:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, color);
                break;
            case FALLING_DUST:
            case BLOCK_CRACK:
            case BLOCK_DUST:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, blockData);
                break;
            case ITEM_CRACK:
                if (itemStack != null)
                    player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, itemStack);
                break;
            default:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, 0);
        }
    }
}
