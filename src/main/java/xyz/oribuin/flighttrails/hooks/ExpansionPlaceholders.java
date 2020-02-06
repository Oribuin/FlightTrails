package xyz.oribuin.flighttrails.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Color;
import xyz.oribuin.flighttrails.FlightTrails;
import xyz.oribuin.flighttrails.cmds.CmdSetColor;
import xyz.oribuin.flighttrails.handlers.FlyHandler;
import org.bukkit.entity.Player;
import xyz.oribuin.flighttrails.persist.Data;

public class ExpansionPlaceholders extends PlaceholderExpansion {

    private FlightTrails flightTrails;
    private FlyHandler flyHandler;

    public ExpansionPlaceholders(FlightTrails flightTrails, FlyHandler flyHandler) {
        this.flightTrails = flightTrails;
        this.flyHandler = flyHandler;
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

        if (placeholder.equalsIgnoreCase("color")) {
            if (Data.dustOptionsMap.get(player.getUniqueId()) == null) {
                return "None";
            } else {
                return "" + Color.fromRGB(Data.dustOptionsMap.get(player.getUniqueId()).getColor().asRGB());
            }
        }

        if (placeholder.equalsIgnoreCase("red")) {
            if (Data.dustOptionsMap.get(player.getUniqueId()) == null) {
                return "None";
            } else {
                return "" + Color.fromRGB(Data.dustOptionsMap.get(player.getUniqueId()).getColor().asRGB()).getRed();
            }
        }

        if (placeholder.equalsIgnoreCase("green")) {
            if (Data.dustOptionsMap.get(player.getUniqueId()) == null) {
                return "None";
            } else {
                return "" + Color.fromRGB(Data.dustOptionsMap.get(player.getUniqueId()).getColor().asRGB()).getGreen();
            }
        }



        if (placeholder.equalsIgnoreCase("blue")) {
            if (Data.dustOptionsMap.get(player.getUniqueId()) == null) {
                return "None";
            } else {
                return "" + Color.fromRGB(Data.dustOptionsMap.get(player.getUniqueId()).getColor().asRGB()).getBlue();
            }
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
