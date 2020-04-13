package xyz.oribuin.flighttrails.util;

import org.bukkit.Color;

public enum TrailColor {

    RED(Color.RED),
    ORANGE(Color.ORANGE),
    YELLOW(Color.YELLOW),
    GOLD(Color.OLIVE),
    GREEN(Color.LIME),
    DARK_GREEN(Color.GREEN),
    BLUE(Color.BLUE),
    AQUA(Color.AQUA),
    DARK_AQUA(Color.TEAL),
    DARK_BLUE(Color.NAVY),
    PURPLE(Color.PURPLE),
    PINK(Color.FUCHSIA),
    WHITE(Color.WHITE),
    BLACK(Color.BLACK),
    GRAY(Color.GRAY),
    LIGHT_GRAY(Color.SILVER),
    BROWN(Color.MAROON);

    private final Color color;

    TrailColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

}
