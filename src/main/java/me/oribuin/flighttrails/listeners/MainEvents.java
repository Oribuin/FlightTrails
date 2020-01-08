package me.oribuin.flighttrails.listeners;

import me.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import static me.oribuin.flighttrails.menus.ColorSelector.dustOptionsMap;

import java.util.UUID;

public class MainEvents implements Listener {
    public FlyHandler flyHandler;

    public MainEvents(FlyHandler flyHandler) {
        this.flyHandler = flyHandler;
    }

    @EventHandler
    public void onStartFlying(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getPlayer().getLocation();

        if (flyHandler.trailIsToggled(player.getUniqueId()) &&  player.hasPermission("flytrails.fly")) {
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
