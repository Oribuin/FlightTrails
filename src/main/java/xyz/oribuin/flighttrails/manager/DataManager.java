package xyz.oribuin.flighttrails.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

public class DataManager extends Manager implements Listener {

    private final static String DATA_CONFIG = "data.yml";

    private FileConfiguration dataConfig;
    private Map<UUID, PlayerData> playerData;

    public DataManager(FlightTrails plugin) {
        super(plugin);
        this.playerData = new HashMap<>();
    }

    @Override
    public void reload() {
        FileUtils.createFile(this.plugin, DATA_CONFIG);
        this.dataConfig = YamlConfiguration.loadConfiguration(this.getDataFile());

        this.playerData.clear();
        for (Player player : Bukkit.getOnlinePlayers())
            this.getPlayerData(player, false);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getPlayerData(event.getPlayer(), false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerData.remove(event.getPlayer().getUniqueId());
    }

    public PlayerData getPlayerData(Player player, boolean create) {
        PlayerData playerData = this.playerData.get(player.getUniqueId());
        if (playerData == null) {
            String key = player.getUniqueId().toString();
            ConfigurationSection playerDataSection = this.dataConfig.getConfigurationSection(key);
            if (playerDataSection == null) {
                if (!create)
                    return null;

                playerData = PlayerData.getDefault(player);
                this.savePlayerData(playerData);
            } else {
                playerData = new PlayerData(player, playerDataSection);
            }

            this.playerData.put(player.getUniqueId(), playerData);
        }

        return playerData;
    }

    public Collection<PlayerData> getAllPlayerData() {
        return Collections.unmodifiableCollection(this.playerData.values());
    }

    private void savePlayerData(PlayerData playerData) {
        ConfigurationSection dataSection = this.getPlayerDataSection(playerData.getPlayer());
        dataSection.set("enabled", playerData.isEnabled());
        dataSection.set("particle", playerData.getParticle().name());
        dataSection.set("item", playerData.getItem().getType().name());
        dataSection.set("block", playerData.getBlock().getMaterial().name());
        dataSection.set("color", playerData.getTrailColor().name());
        this.saveData();
    }

    private ConfigurationSection getPlayerDataSection(Player player) {
        String key = player.getUniqueId().toString();
        if (this.dataConfig.isConfigurationSection(key))
            return this.dataConfig.getConfigurationSection(key);
        return this.dataConfig.createSection(key);
    }

    public void updatePlayerData(Player player, boolean enabled) {
        PlayerData playerData = this.getPlayerData(player, true);
        playerData.setEnabled(enabled);
        this.getPlayerDataSection(player).set("enabled", enabled);
        this.saveData();
    }

    public void updatePlayerData(Player player, Particle particle) {
        PlayerData playerData = this.getPlayerData(player, true);
        playerData.setParticle(particle);
        this.getPlayerDataSection(player).set("particle", particle.name());
        this.saveData();
    }

    public void updatePlayerData(Player player, ItemStack item) {
        PlayerData playerData = this.getPlayerData(player, true);
        playerData.setItem(item);
        this.getPlayerDataSection(player).set("item", item.getType().name());
        this.saveData();
    }

    public void updatePlayerData(Player player, BlockData block) {
        PlayerData playerData = this.getPlayerData(player, true);
        playerData.setBlock(block);
        this.getPlayerDataSection(player).set("block", block.getMaterial().name());
        this.saveData();
    }

    public void updatePlayerData(Player player, TrailColor color) {
        PlayerData playerData = this.getPlayerData(player, true);
        playerData.setTrailColor(color);
        this.getPlayerDataSection(player).set("color", color.name());
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
