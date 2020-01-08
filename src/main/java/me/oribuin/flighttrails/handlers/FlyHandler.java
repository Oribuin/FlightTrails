package me.oribuin.flighttrails.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlyHandler {
    private final Set<UUID> toggleFly;

    public FlyHandler() {
        this.toggleFly = new HashSet<>();
    }

    public void trailToggle(UUID uuid) {
        if (this.trailIsToggled(uuid)) toggleFly.remove(uuid);
        else toggleFly.add(uuid);
    }

    public boolean trailIsToggled(UUID uuid) {
        return toggleFly.contains(uuid);
    }
}