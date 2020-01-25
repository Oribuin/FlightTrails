package xyz.oribuin.flighttrails.listeners;

import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import xyz.oribuin.flighttrails.persist.Data;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.MetadataValue;


public class MainEvents implements Listener {
    FlightTrails plugin;
    FlyHandler flyHandler;

    public MainEvents(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    public Particle.DustOptions particleColor(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), plugin.getConfig().getInt("particle-size"));
    }


    @EventHandler
    public void onStartFlying(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getPlayer().getLocation();

        Particle.DustOptions color = Data.dustOptionsMap.get(player.getUniqueId());

        if (flyHandler.trailIsToggled(player.getUniqueId()) && !player.hasPermission("flytrails.fly")) {
            flyHandler.trailToggle(player.getUniqueId());
            return;
        }

        // If the user has flight trails enabled and they have the flight trails permission.
        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {

            // If vanish-hook is enabled, Get all online players.
            if (plugin.getConfig().getBoolean("vanish-hook", true)) {
                for (Player pl : player.getServer().getOnlinePlayers()) {
                    // If the user is in spectator mode, don't display particles.
                    if (player.getGameMode() == GameMode.SPECTATOR)
                        return;

                    // If the user has the metadata "Vanished" Don't display particles.
                    for (MetadataValue meta : player.getMetadata("vanished")) {
                        if (meta.asBoolean())
                            return;
                    }

                    // If  the user can't see the user, disable particles.
                    if (!pl.canSee(player)) return;
                }
            }

            // If the user is flying, spawn the particle.
            if (plugin.getConfig().getBoolean("flight-trail") && !player.isFlying()) {
                if (color != null) {
                    color = particleColor(Data.dustOptionsMap.get(player.getUniqueId()).getColor().getRed(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getGreen(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getBlue());
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, plugin.getConfig().getInt("particle-count"), color);
                }
            }
        }
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        Particle.DustOptions color = Data.dustOptionsMap.get(player.getUniqueId());

        if (flyHandler.trailIsToggled(player.getUniqueId()) && !player.hasPermission("flytrails.fly")) {
            flyHandler.trailToggle(player.getUniqueId());
            return;
        }

        // If the user has flight trails enabled and has permission.
        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {

            // If vanish-hook is enabled, Get all online players.
            if (plugin.getConfig().getBoolean("vanish-hook", true)) {
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

            // If the user is flying, display the particles.
            if (plugin.getConfig().getBoolean("flight-trail") && player.isFlying()) {
                if (color != null) {
                    color = particleColor(Data.dustOptionsMap.get(player.getUniqueId()).getColor().getRed(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getGreen(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getBlue());
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, plugin.getConfig().getInt("particle-count"), color);
                }

                // If the elytra trails are enabled, spawn particles
            } else if (plugin.getConfig().getBoolean("elytra-trail", true) && player.isGliding()) {
                if (color != null) {
                    color = particleColor(Data.dustOptionsMap.get(player.getUniqueId()).getColor().getRed(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getGreen(), Data.dustOptionsMap.get(player.getUniqueId()).getColor().getBlue());
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, plugin.getConfig().getInt("particle-count"), color);
                }
            }
        }
    }
}