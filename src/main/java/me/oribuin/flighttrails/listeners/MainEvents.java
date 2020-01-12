package me.oribuin.flighttrails.listeners;

//import de.myzelyam.api.vanish.VanishAPI;
import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static me.oribuin.flighttrails.menus.ColorSelector.dustOptionsMap;

public class MainEvents implements Listener {
    public FlightTrails plugin;
    public FlyHandler flyHandler;

    public MainEvents(FlightTrails instance, FlyHandler flyHandler) {
        this.plugin = instance;
        this.flyHandler = flyHandler;
    }

    @EventHandler
    public void onStartFlying(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getPlayer().getLocation();

        /* SuperVanish Hook */
        if (plugin.getConfig().getBoolean("supervanish-hook", true)) {
            if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
                /*
                boolean isVanished = VanishAPI.isInvisible(player);
                if (isVanished) {
                    return;
                }

                 */
            }
        }

        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {
            if (!player.isFlying()) {
                Particle.DustOptions color = dustOptionsMap.get(player.getUniqueId());
                if (color != null) {
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, 1, color);
                }
            }
        }
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getPlayer().getLocation();

        /* SuperVanish Hook */
        if (plugin.getConfig().getBoolean("supervanish-hook", true)) {
            if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
                /*
                boolean isVanished = VanishAPI.isInvisible(player);
                if (isVanished) {
                    return;
                }
                */
            }
        }
        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {
            if (player.isFlying()) {
                Particle.DustOptions color = dustOptionsMap.get(player.getUniqueId());
                if (color != null) {
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, 1, color);
                }
            }
        }
    }
}
