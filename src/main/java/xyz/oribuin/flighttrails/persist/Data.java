package xyz.oribuin.flighttrails.persist;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Data {

    public static Map<UUID, Particle.DustOptions> dustOptionsMap = new HashMap<>();

    /*
    public static void addDataUser(FlightTrails plugin, Player player) {
        //FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));

        //if (dataConfig.getConfigurationSection("users") == null) return;
        //dataConfig.createSection("users", dustOptionsMap);
    }

    public static void removeUserData(FlightTrails plugin, Player player) {
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));

        if (dataConfig.getConfigurationSection("users") == null) return;
        if (dataConfig.getConfigurationSection("users." + dustOptionsMap.get(player.getUniqueId())) == null) return;

        if (dataConfig.getConfigurationSection("users." + dustOptionsMap.get(player.getUniqueId())) != null)
            dataConfig.getConfigurationSection("users." + dustOptionsMap.get(player.getUniqueId())).getKeys(false).remove(player.getUniqueId());
    }

    public static void saveData(FlightTrails plugin, Player player) {
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));

        if (dataConfig.getConfigurationSection("users") == null) return;

        dustOptionsMap.keySet().forEach(uuid -> dataConfig.set("users." + player.getUniqueId() + dustOptionsMap.get(player.getUniqueId()).getColor(), dustOptionsMap.get(player.getUniqueId())));
    }
     */
}
