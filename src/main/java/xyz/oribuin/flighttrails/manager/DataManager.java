package xyz.oribuin.flighttrails.manager;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.data.PlayerData;
import xyz.oribuin.flighttrails.util.FileUtils;
import xyz.oribuin.flighttrails.util.TrailColor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Esophose
 */

public class DataManager extends Manager implements Listener {

    // Define data.yml
    private final static String DATA_CONFIG = "data.yml";

    private FileConfiguration dataConfig;
    private final Map<UUID, PlayerData> playerData;

    public DataManager(FlightTrails plugin) {
        super(plugin);
        this.playerData = new HashMap<>();

        // Register the events
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void reload() {
        // Check file
        FileUtils.createFile(this.plugin, DATA_CONFIG);

        // Load configuration
        this.dataConfig = YamlConfiguration.loadConfiguration(this.getDataFile());

        // Clear PlayerData HashMap
        this.playerData.clear();

        // Load all players data
        for (Player player : Bukkit.getOnlinePlayers())
            this.getPlayerData(player, false);
    }

    // Load PlayerData on PlayerJoin
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getPlayerData(event.getPlayer(), false);
    }

    // Remove the player from hashMap when they leave
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerData.remove(event.getPlayer().getUniqueId());
    }

    // Get the player's Data
    public PlayerData getPlayerData(Player player, boolean create) {
        // Get the PlayerData from hashmap
        PlayerData playerData = this.playerData.get(player.getUniqueId());

        // Check if PlayerData is null
        if (playerData == null) {

            // Define PlayerUUID
            String key = player.getUniqueId().toString();

            // Get the Player from data.yml
            ConfigurationSection playerDataSection = this.dataConfig.getConfigurationSection(key);
            if (playerDataSection == null) {
                if (!create)
                    return null;

                // Get default values
                playerData = PlayerData.getDefault(player);

                // Save default values
                this.savePlayerData(playerData);
            } else {
                playerData = new PlayerData(player, playerDataSection);
            }

            // Add playerdata to HashMap
            this.playerData.put(player.getUniqueId(), playerData);
        }

        // Return PlayerData
        return playerData;
    }

    // Get all PlayerData
    public Collection<PlayerData> getAllPlayerData() {
        return Collections.unmodifiableCollection(this.playerData.values());
    }

    /**
     * Save all the PlayerData inside data.yml
     *
     * @param playerData the Data being saved
     */
    private void savePlayerData(PlayerData playerData) {
        ConfigurationSection dataSection = this.getPlayerDataSection(playerData.getPlayer());

        // Set Values
        dataSection.set("enabled", playerData.isEnabled());
        dataSection.set("particle", playerData.getParticle().name());
        dataSection.set("item", playerData.getItem().getType().name());
        dataSection.set("block", playerData.getBlock().getMaterial().name());
        dataSection.set("color", playerData.getTrailColor().name());

        // Save values
        this.saveData();
    }

    // Get the Player from data.yml
    private ConfigurationSection getPlayerDataSection(Player player) {
        // Get Player UUID
        String key = player.getUniqueId().toString();

        // If Player is saved, get Data
        if (this.dataConfig.isConfigurationSection(key))
            return this.dataConfig.getConfigurationSection(key);

        // If player does not exist, create it
        return this.dataConfig.createSection(key);
    }

    public void updatePlayerData(Player player, boolean enabled) {
        // Define PlayerData
        PlayerData playerData = this.getPlayerData(player, true);

        // Set Trails as enabled
        playerData.setEnabled(enabled);

        // Set enabled to true
        this.getPlayerDataSection(player).set("enabled", enabled);

        // Save Data
        this.saveData();
    }

    public void updatePlayerData(Player player, Particle particle) {
        // Define PlayerData
        PlayerData playerData = this.getPlayerData(player, true);

        // Set the particle
        playerData.setParticle(particle);

        // Set particle in configuration section
        this.getPlayerDataSection(player).set("particle", particle.name());

        // Save Data
        this.saveData();
    }

    public void updatePlayerData(Player player, ItemStack item) {
        // Define PlayerData
        PlayerData playerData = this.getPlayerData(player, true);

        // Set the ItemStack
        playerData.setItem(item);

        // Set the ItemStack inside configuration section
        this.getPlayerDataSection(player).set("item", item.getType().name());

        // Save Data
        this.saveData();
    }

    public void updatePlayerData(Player player, BlockData block) {
        // Define PlayerData
        PlayerData playerData = this.getPlayerData(player, true);

        // Set the block
        playerData.setBlock(block);

        // Set the block in the configuration section
        this.getPlayerDataSection(player).set("block", block.getMaterial().name());

        // Save Data
        this.saveData();
    }

    public void updatePlayerData(Player player, TrailColor color) {
        // Define PlayerData
        PlayerData playerData = this.getPlayerData(player, true);

        // Set TrailColor
        playerData.setTrailColor(color);

        // Set TrailColor in the configuration section
        this.getPlayerDataSection(player).set("color", color.name());

        // Save Data
        this.saveData();
    }

    private void saveData() {
        try {
            this.dataConfig.save(this.getDataFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File getDataFile() {
        return new File(this.plugin.getDataFolder(), DATA_CONFIG);
    }

}
