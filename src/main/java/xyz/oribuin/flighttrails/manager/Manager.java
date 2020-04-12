package xyz.oribuin.flighttrails.manager;

import xyz.oribuin.flighttrails.FlightTrails;

public abstract class Manager {

    protected final FlightTrails plugin;

    public Manager(FlightTrails plugin) {
        this.plugin = plugin;
    }

    public abstract void reload();

}
