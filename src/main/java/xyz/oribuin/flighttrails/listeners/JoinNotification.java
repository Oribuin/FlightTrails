package xyz.oribuin.flighttrails.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.persist.Chat;

import java.util.ArrayList;
import java.util.List;

public class JoinNotification implements Listener {

    FlightTrails plugin;

    public JoinNotification(FlightTrails plugin) {
        this.plugin = plugin;
    }

    List<String> authorUUID = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        authorUUID.add("9f7cf461-903f-4ef8-8a88-bdbc49806364");
        authorUUID.add("f37c623a-66ee-4439-b924-0c778c3cf143");
        authorUUID.add("9a41a912-7b79-4850-8cf5-4d35f1786463");
        authorUUID.add("4f5dfff9-2f7f-4d61-8f29-5e8d8ebd364a");

        if (authorUUID.contains(event.getPlayer().getUniqueId().toString())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getPlayer().sendMessage(Chat.cl("&aThis server is running &f" + plugin.getDescription().getName() + "&a in version " + plugin.getDescription().getVersion())), 10 * 20);
        }
    }
}
