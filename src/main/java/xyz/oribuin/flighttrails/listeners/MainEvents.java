package xyz.oribuin.flighttrails.listeners;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.cmds.CmdSetColor;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.MetadataValue;
import xyz.oribuin.flighttrails.persist.Data;

import java.util.ArrayList;
import java.util.List;


public class MainEvents implements Listener {
    FlightTrails plugin;
    FlyHandler flyHandler;

    public MainEvents(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        Particle.DustOptions color = Data.dustOptionsMap.get(player.getUniqueId());


        int r = config.getInt("default-color.red");
        int g = config.getInt("default-color.green");
        int b = config.getInt("default-color.blue");

        if (flyHandler.trailIsToggled(player.getUniqueId()) && !player.hasPermission("flytrails.fly")) {
            flyHandler.trailToggle(player.getUniqueId());
            return;
        }

        // If the user has flight trails enabled and has permission.
        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {

            // If vanish-hook is enabled, Get all online players.
            if (config.getBoolean("vanish-hook", true)) {
                for (Player pl : player.getServer().getOnlinePlayers()) {

                    // If the user is in spectator mode, don't display particles.
                    if (player.getGameMode() == GameMode.SPECTATOR)
                        return;

                    // If the user has the meta data "vanished" Don't display particles.
                    for (MetadataValue meta : player.getMetadata("vanished")) {
                        if (meta.asBoolean())
                            return;
                    }
                    // If  the user can't see the user, disable particles.
                    if (!pl.canSee(player)) return;
                }
            }

            if (config.getBoolean("default-color.enabled", true)) {
                if (Data.dustOptionsMap.get(player.getUniqueId()) == null) {
                    Data.dustOptionsMap.put(player.getUniqueId(), new Particle.DustOptions(Color.fromRGB(r, g, b), config.getInt("particle-size")));
                    color = particleColor(player);
                }
            }

            // If the user is flying, display the particles.
            if (config.getBoolean("flight-trail", true) && player.isFlying()) {

                if (color != null) {
                    color = particleColor(player);
                    spawnParticles(player, color);
                }

                // If the elytra trails are enabled, spawn particles
            } else if (config.getBoolean("elytra-trail", true) && player.isGliding()) {
                if (color != null) {
                    color = particleColor(player);
                    spawnParticles(player, color);
                }
            }
        }
    }

    private void spawnParticles(Player player, Particle.DustOptions color) {
        FileConfiguration config = plugin.getConfig();
        if (player.getLocation().getWorld() == null) return;

        if (config.getStringList("disabled-worlds").contains(player.getLocation().getWorld().getName()))
            return;

        if (config.getString("particle-effect") == null || config.getString("particle-effect").toUpperCase().equals("REDSTONE")) {
            player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), config.getInt("particle-count"), 0, 0, 0, color);
        } else {
            player.getLocation().getWorld().spawnParticle(Particle.valueOf(config.getString("particle-effect").toUpperCase()), player.getLocation(), config.getInt("particle-count"), 0, 0, 0, 0);
        }

    }

    private Particle.DustOptions particleColor(Player player) {
        return new Particle.DustOptions(Color.fromRGB(Data.dustOptionsMap.get(player.getUniqueId()).getColor().getRed(),
                Data.dustOptionsMap.get(player.getUniqueId()).getColor().getGreen(),
                Data.dustOptionsMap.get(player.getUniqueId()).getColor().getBlue()),
                plugin.getConfig().getInt("particle-size"));
    }

}