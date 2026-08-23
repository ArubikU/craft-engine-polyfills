/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.CraftEngine
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import org.bukkit.entity.Player;

public final class CePlayers {
    private CePlayers() {
    }

    // onlineByUuid() walks EVERY online user to build a fresh map — fine for resolve(), which
    // already needs to touch every entry once, but resolveOne() only needs a single O(1) lookup
    // and used to pay the same full-server-wide rebuild for it. At 200 players, this was called
    // every 2 ticks per active glue/conveyor/pipe-wand user (SlimeIndicator#render/despawn), so
    // rebuilding an O(players) map just to look up one entry was real, avoidable waste. Cache the
    // built map for the current tick only — cheap to keep correct (a player joining/leaving mid
    // tick is not observable to any single caller anyway) and self-invalidates every tick without
    // needing a join/quit listener.
    private static Map<UUID, net.momirealms.craftengine.core.entity.player.Player> tickCache = Map.of();
    private static int tickCacheTick = Integer.MIN_VALUE;

    public static List<net.momirealms.craftengine.core.entity.player.Player> resolve(Iterable<? extends Player> bukkitPlayers) {
        Map<UUID, net.momirealms.craftengine.core.entity.player.Player> byId = CePlayers.onlineByUuid();
        ArrayList<net.momirealms.craftengine.core.entity.player.Player> viewers = new ArrayList<net.momirealms.craftengine.core.entity.player.Player>();
        for (Player player : bukkitPlayers) {
            net.momirealms.craftengine.core.entity.player.Player ce = byId.get(player.getUniqueId());
            if (ce == null) continue;
            viewers.add(ce);
        }
        return viewers;
    }

    private static Map<UUID, net.momirealms.craftengine.core.entity.player.Player> onlineByUuid() {
        HashMap<UUID, net.momirealms.craftengine.core.entity.player.Player> byId = new HashMap<UUID, net.momirealms.craftengine.core.entity.player.Player>();
        try {
            for (net.momirealms.craftengine.core.entity.player.Player p : CraftEngine.instance().networkManager().onlineUsers()) {
                Object pp = p.platformPlayer();
                if (!(pp instanceof Player)) continue;
                Player b = (Player)pp;
                byId.put(b.getUniqueId(), p);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return byId;
    }

    public static net.momirealms.craftengine.core.entity.player.Player resolveOne(Player player) {
        int now = org.bukkit.Bukkit.getCurrentTick();
        Map<UUID, net.momirealms.craftengine.core.entity.player.Player> cache = tickCache;
        if (tickCacheTick != now) {
            cache = CePlayers.onlineByUuid();
            tickCache = cache;
            tickCacheTick = now;
        }
        return cache.get(player.getUniqueId());
    }
}

