package dev.arubik.craftengine.contraption.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Bukkit-player-list -> CraftEngine-player-list lookup, shared by the contraption render drivers.
 *
 * <p>Public because teardown paths outside this package need it too: despawning a contraption to an
 * empty viewer list sends no despawn packets while still clearing the swarm's records, which strands
 * the fake entities on every client that could see them.
 *
 * <p><b>Cost.</b> CraftEngine exposes its online users only as an unindexed {@code Iterable}, so the
 * only way to map a Bukkit player to its CraftEngine counterpart is to scan that iterable and compare
 * platform-player UUIDs. {@link #resolve} therefore builds the UUID index ONCE per call and looks each
 * Bukkit player up in it — O(users + players) — rather than re-scanning the whole user list per player
 * (O(users x players)). Callers on the per-tick path must additionally hoist the call itself: see
 * {@code ContraptionEngine.tickAll}, which resolves one list per WORLD per tick and shares it across
 * every contraption in that world.
 */
public final class CePlayers {

    private CePlayers() {
    }

    public static List<net.momirealms.craftengine.core.entity.player.Player> resolve(Iterable<? extends Player> bukkitPlayers) {
        Map<UUID, net.momirealms.craftengine.core.entity.player.Player> byId = onlineByUuid();
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = new ArrayList<>();
        for (Player p : bukkitPlayers) {
            net.momirealms.craftengine.core.entity.player.Player ce = byId.get(p.getUniqueId());
            if (ce != null) {
                viewers.add(ce);
            }
        }
        return viewers;
    }

    /**
     * Every online CraftEngine player keyed by their platform (Bukkit) UUID. Fail-open: returns an
     * empty map under the pure-JVM unit-test path where {@code CraftEngine.instance()} throws (no live
     * server), which degrades every caller to "no viewers" exactly as the old per-player scan did.
     */
    private static Map<UUID, net.momirealms.craftengine.core.entity.player.Player> onlineByUuid() {
        Map<UUID, net.momirealms.craftengine.core.entity.player.Player> byId = new HashMap<>();
        try {
            for (net.momirealms.craftengine.core.entity.player.Player p : net.momirealms.craftengine.core.plugin.CraftEngine
                    .instance().networkManager().onlineUsers()) {
                Object pp = p.platformPlayer();
                if (pp instanceof Player b) {
                    byId.put(b.getUniqueId(), p);
                }
            }
        } catch (Throwable ignored) {
        }
        return byId;
    }

    public static net.momirealms.craftengine.core.entity.player.Player resolveOne(Player player) {
        return onlineByUuid().get(player.getUniqueId());
    }
}
