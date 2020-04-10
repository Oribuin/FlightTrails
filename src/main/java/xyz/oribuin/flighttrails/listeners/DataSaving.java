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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        File file = new File(plugin.getDataFolder(), "data.yml");
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(file);

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
        File file = new File(plugin.getDataFolder(), "data.yml");
        FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(file);

        if (dataConfig.get(player.getUniqueId() + ".enabled") == null)
            dataConfig.set(player.getUniqueId() + ".enabled", false);

        if (dataConfig.get(player.getUniqueId()  + ".particle") == null)
            dataConfig.set(player.getUniqueId() + ".particle", "FALLING_DUST");

        if (dataConfig.get(player.getUniqueId() + ".item") == null)
            dataConfig.set(player.getUniqueId() + ".item", "BLUE_CONCRETE");

        if (dataConfig.get(player.getUniqueId() + ".block") == null)
            dataConfig.set(player.getUniqueId() + ".block", "BLUE_CONCRETE");

        if (dataConfig.get(player.getUniqueId() + ".color") == null)
            dataConfig.set(player.getUniqueId() + ".color", Color.fromRGB(0, 0, 0));

        dataConfig.save(file);
    }
}
