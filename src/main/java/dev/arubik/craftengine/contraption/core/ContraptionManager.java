/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package dev.arubik.craftengine.contraption.core;

import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.event.ContraptionSpawnEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public final class ContraptionManager {
    private static final Map<UUID, ContraptionEntity> ACTIVE = new HashMap<UUID, ContraptionEntity>();

    private ContraptionManager() {
    }

    public static ContraptionEntity register(ContraptionEntity entity) {
        ACTIVE.put(entity.state().id(), entity);
        ContraptionWorlds.index(entity);
        ContraptionManager.fireSpawn(entity);
        return entity;
    }

    private static void fireSpawn(ContraptionEntity entity) {
        try {
            Bukkit.getPluginManager().callEvent((Event)new ContraptionSpawnEvent(entity));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static ContraptionEntity get(UUID id) {
        return ACTIVE.get(id);
    }

    public static ContraptionEntity remove(UUID id) {
        ContraptionEntity removed = ACTIVE.remove(id);
        if (removed != null) {
            ContraptionWorlds.unindex(removed);
        }
        return removed;
    }

    public static Collection<ContraptionEntity> all() {
        return ACTIVE.values();
    }

    public static int count() {
        return ACTIVE.size();
    }
}

