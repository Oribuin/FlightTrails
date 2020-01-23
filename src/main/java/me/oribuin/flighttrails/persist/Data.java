package me.oribuin.flighttrails.persist;

import org.bukkit.Particle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Data {
    public static Map<UUID, Particle.DustOptions> dustOptionsMap = new HashMap<>();

    //this.dustOptionsMap.forEach(((uuid, dustOptions) -> data.createSection(cmdSetColor.dustOptionsMap.get(pplayer.getUniqueId()).toString())));
}
