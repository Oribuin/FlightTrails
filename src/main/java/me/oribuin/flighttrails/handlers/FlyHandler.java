package me.oribuin.flighttrails.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlyHandler {
    private final Set<UUID> toggleFly;

    public FlyHandler() {
        // Create the hashmap
        this.toggleFly = new HashSet<>();
    }

    public void trailToggle(UUID uuid) {
        // If the user has it enabled, remove them
        if (this.trailIsToggled(uuid)) toggleFly.remove(uuid);
        // else, enable it
        else toggleFly.add(uuid);
    }

    // Check if toggleFly contains the user
    public boolean trailIsToggled(UUID uuid) {
        return toggleFly.contains(uuid);
    }
}