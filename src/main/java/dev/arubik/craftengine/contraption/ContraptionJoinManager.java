package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Debug-only bookkeeping for {@code /cepolyfill contraption join}/{@code leave} (see
 * {@code CepCommand}): remembers each player's real-world location from just before they
 * were teleported INTO a {@link dev.arubik.craftengine.contraption.level.ContraptionLevel},
 * so {@code leave} can put them back. A simple {@code Map<UUID, Location>} is sufficient —
 * this is a debug noclip tool, not a player-facing feature, so there's no attempt to survive
 * a server restart or handle a player who never calls {@code leave} (their entry just sits
 * here harmlessly until they do, or forever if they don't — matches the scope of every other
 * throwaway debug harness in this package, e.g. {@link HologramTest}'s own {@code ACTIVE} map).
 */
public final class ContraptionJoinManager {

    private ContraptionJoinManager() {
    }

    private static final Map<UUID, Location> PRE_JOIN_LOCATION = new HashMap<>();

    /** Records {@code player}'s current (real-world) location so {@link #leave} can restore it later. */
    public static void recordPreJoinLocation(Player player) {
        PRE_JOIN_LOCATION.put(player.getUniqueId(), player.getLocation().clone());
    }

    /** Whether {@code player} currently has a stored pre-join location (i.e. is "inside" via {@code join}). */
    public static boolean hasPreJoinLocation(Player player) {
        return PRE_JOIN_LOCATION.containsKey(player.getUniqueId());
    }

    /**
     * Removes and returns {@code player}'s stored pre-join location, or {@code null} if they
     * never {@code join}ed (or already {@code leave}d) — callers must handle {@code null}
     * gracefully rather than assume {@code join} was always called first.
     */
    public static Location consumePreJoinLocation(Player player) {
        return PRE_JOIN_LOCATION.remove(player.getUniqueId());
    }
}
