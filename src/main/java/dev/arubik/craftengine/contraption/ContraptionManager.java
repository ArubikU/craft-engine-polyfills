package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UUID -> {@link ContraptionEntity} registry (CONTRAPTIONS.md §3 package layout). Chunk
 * load/unload persistence hooks are deferred to Phase 6 (manifest-backed re-instantiation)
 * — for now this is purely an in-memory session registry, which is all Phases 2-5 need.
 */
public final class ContraptionManager {

    private ContraptionManager() {
    }

    private static final Map<UUID, ContraptionEntity> ACTIVE = new HashMap<>();

    public static ContraptionEntity register(ContraptionEntity entity) {
        ACTIVE.put(entity.state().id(), entity);
        // Keep the ContraptionLevel -> ContraptionEntity reverse index (roadmap item #5) in lockstep
        // with this forward UUID map — populated here, cleared in remove(...) below — so the two can
        // never drift. No-op for a level-less (unit-test) facade. See ContraptionWorlds.
        ContraptionWorlds.index(entity);
        // Universal "a contraption now exists" hook (public API) — fires for EVERY registration path
        // (fresh assemble, chunk-reload rehydrate, place-from-item respawn, cross-world re-anchor),
        // since this is the single choke point they all pass through. Non-cancellable: the facade is
        // already live by now. See ContraptionSpawnEvent for how it differs from the assemble-only
        // ContraptionAssembledEvent.
        fireSpawn(entity);
        return entity;
    }

    /**
     * Fires {@link dev.arubik.craftengine.contraption.event.ContraptionSpawnEvent}. Fail-open
     * (swallows everything) so this is a pure no-op when no live server is present — e.g. a
     * pure-JVM unit test where {@code Bukkit.getPluginManager()} throws — leaving registration
     * behavior byte-identical to before events existed whenever nobody is listening. Same shape as
     * {@code ContraptionAssembler}'s own fire helpers.
     */
    private static void fireSpawn(ContraptionEntity entity) {
        try {
            org.bukkit.Bukkit.getPluginManager()
                    .callEvent(new dev.arubik.craftengine.contraption.event.ContraptionSpawnEvent(entity));
        } catch (Throwable ignored) {
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

    public static java.util.Collection<ContraptionEntity> all() {
        return ACTIVE.values();
    }

    public static int count() {
        return ACTIVE.size();
    }
}
