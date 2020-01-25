package xyz.oribuin.flighttrails.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.cmds.CmdSetColor;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.entity.Player;

public class TrailsPlaceholderExpansion extends PlaceholderExpansion {

    private FlightTrails flightTrails;
    private FlyHandler flyHandler;
    private CmdSetColor cmdSetColor;

    public TrailsPlaceholderExpansion(FlightTrails flightTrails, FlyHandler flyHandler, CmdSetColor cmdSetColor) {
        this.flightTrails = flightTrails;
        this.flyHandler = flyHandler;
        this.cmdSetColor = cmdSetColor;
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null)
            return null;


        switch (placeholder) {
            case "toggled":
                return "" + flyHandler.trailIsToggled(player.getUniqueId());
            case "author":
                return "Oribuin";
        }

        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "FlightTrails".toLowerCase();
    }

    @Override
    public String getAuthor() {
        return flightTrails.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return flightTrails.getDescription().getVersion();
    }
}
