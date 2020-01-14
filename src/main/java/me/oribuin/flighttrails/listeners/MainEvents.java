package me.oribuin.flighttrails.listeners;

import me.oribuin.flighttrails.FlightTrails;
import me.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.metadata.MetadataValue;

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

        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {
            /*
            if (plugin.getConfig().getBoolean("vanish-hook", true)) {
                if (!player.canSee(player))
                    return;
            }
             */

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
        Location loc = player.getLocation();
        if (flyHandler.trailIsToggled(player.getUniqueId()) && player.hasPermission("flytrails.fly")) {

            /*
            if (plugin.getConfig().getBoolean("vanish-hook", true)) {
                if (!player.canSee(player))
                    return;
            }
             */

            if (player.isFlying()) {
                Particle.DustOptions color = dustOptionsMap.get(player.getUniqueId());
                if (color != null) {
                    player.getLocation().getWorld().spawnParticle(Particle.REDSTONE, loc, 1, color);
                }
            }
        }
    }

    private boolean canSee(Player player, Player target) {
        if (player == null || target == null)
            return true;

        for (MetadataValue meta : player.getMetadata("vanished"))
            if (meta.asBoolean())
                return false;

        return player.canSee(target);
    }
}
