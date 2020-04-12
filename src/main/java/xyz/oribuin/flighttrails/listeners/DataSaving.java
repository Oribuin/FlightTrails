package xyz.oribuin.flighttrails.listeners;

import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.FlightTrails;

import java.io.File;
import java.io.IOException;

public class DataSaving implements Listener {

    private FlightTrails plugin = FlightTrails.getInstance();
    private final File file = new File(plugin.getDataFolder(), "data.yml");
    private final FileConfiguration data = getDataConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            if (!file.exists()) {
                file.createNewFile();
                plugin.getServer().getConsoleSender().sendMessage("[FlightTrails] Created Data File");
            }

            checkData(player);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkData(Player player) throws IOException {
        FileConfiguration config = plugin.getConfig();

        if (data.get(player.getUniqueId() + ".enabled") == null)
            data.set(player.getUniqueId() + ".enabled", config.getBoolean("particle-settings.default.enabled"));

        if (data.get(player.getUniqueId()  + ".particle") == null)
            data.set(player.getUniqueId() + ".particle", config.getString("particle-settings.default.particle"));

        if (data.get(player.getUniqueId() + ".item") == null)
            data.set(player.getUniqueId() + ".item", config.getString("particle-settings.default.item"));

        if (data.get(player.getUniqueId() + ".block") == null)
            data.set(player.getUniqueId() + ".block", config.getString("particle-settings.default.block"));

        if (data.get(player.getUniqueId() + ".color") == null)
            data.set(player.getUniqueId() + ".color", config.getColor("particle-settings.default.color"));

        data.save(file);
    }

    private FileConfiguration getDataConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }
}
