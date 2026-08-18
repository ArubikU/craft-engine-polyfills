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
        return CePlayers.onlineByUuid().get(player.getUniqueId());
    }
}

