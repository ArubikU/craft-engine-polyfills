/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ContraptionJoinManager {
    private static final Map<UUID, Location> PRE_JOIN_LOCATION = new HashMap<UUID, Location>();

    private ContraptionJoinManager() {
    }

    public static void recordPreJoinLocation(Player player) {
        PRE_JOIN_LOCATION.put(player.getUniqueId(), player.getLocation().clone());
    }

    public static boolean hasPreJoinLocation(Player player) {
        return PRE_JOIN_LOCATION.containsKey(player.getUniqueId());
    }

    public static Location consumePreJoinLocation(Player player) {
        return PRE_JOIN_LOCATION.remove(player.getUniqueId());
    }
}

