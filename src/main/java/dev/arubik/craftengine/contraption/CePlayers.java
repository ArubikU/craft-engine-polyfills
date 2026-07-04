package dev.arubik.craftengine.contraption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

/** Bukkit-player-list -> CraftEngine-player-list lookup, shared by the contraption render drivers. */
final class CePlayers {

    private CePlayers() {
    }

    static List<net.momirealms.craftengine.core.entity.player.Player> resolve(Iterable<? extends Player> bukkitPlayers) {
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = new ArrayList<>();
        for (Player p : bukkitPlayers) {
            net.momirealms.craftengine.core.entity.player.Player ce = resolveOne(p);
            if (ce != null) {
                viewers.add(ce);
            }
        }
        return viewers;
    }

    static net.momirealms.craftengine.core.entity.player.Player resolveOne(Player player) {
        try {
            UUID want = player.getUniqueId();
            for (net.momirealms.craftengine.core.entity.player.Player p : net.momirealms.craftengine.core.plugin.CraftEngine
                    .instance().networkManager().onlineUsers()) {
                Object pp = p.platformPlayer();
                if (pp instanceof Player b && b.getUniqueId().equals(want)) {
                    return p;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
