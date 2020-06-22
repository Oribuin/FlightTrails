package xyz.oribuin.flighttrails.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.data.PlayerData;

public class PlaceholderExp extends PlaceholderExpansion {

    private final FlightTrails plugin;

    public PlaceholderExp(FlightTrails plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null)
            return null;

        PlayerData playerData = this.plugin.getDataManager().getPlayerData(player, false);
        if (playerData == null)
            return null;


        switch (placeholder) {
            case "particle":
                return playerData.getParticle().name().toLowerCase();
            case "block":
                return playerData.getBlock().getMaterial().name().toLowerCase();
            case "item":
                return playerData.getItem().getType().name().toLowerCase();
            case "enabled":
                return String.valueOf(playerData.isEnabled());
            case "color":
                return playerData.getTrailColor().name().toLowerCase();
        }

        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return FlightTrails.getInstance().getDescription().getName().toLowerCase();
    }

    @Override
    public String getAuthor() {
        return FlightTrails.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return FlightTrails.getInstance().getDescription().getVersion();
    }
}

