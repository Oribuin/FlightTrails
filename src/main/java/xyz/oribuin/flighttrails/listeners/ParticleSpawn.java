package xyz.oribuin.flighttrails.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.flighttrails.FlightTrails;

import java.io.File;
import java.io.IOException;

public class ParticleSpawn implements Listener {

    private FlightTrails plugin = FlightTrails.getInstance();

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isHidden(player))
            return;

        if (!player.hasPermission("flighttrails.use"))
            return;

        if (getDataConfig().getBoolean(player.getUniqueId() + ".enabled", true)) {
            if (!plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
                spawnParticles(player);
            }
        }
    }

    private FileConfiguration getDataConfig() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
    }

    private boolean isHidden(Player player) {
        return player.hasMetadata("vanished");
    }

    private void spawnParticles(Player player) {
        if (!getDataConfig().getBoolean(player.getUniqueId() + ".enabled", true))
            return;

        if (isHidden(player))
            return;

        if (!player.isFlying())
            return;
        // Particle Type
        Particle particle = Particle.valueOf(plugin.getConfig().getString("particle-settings.particle"));
        // Particle Settings
        int particleCount = plugin.getConfig().getInt("particle-settings.count");
        int particleSize = plugin.getConfig().getInt("particle-settings.size");

        try {
            new DataSaving().checkData(player);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, itemStack);
                break;
            default:
                player.getWorld().spawnParticle(particle, player.getLocation(), particleCount, 0, 0, 0, 0);
        }
    }
}
