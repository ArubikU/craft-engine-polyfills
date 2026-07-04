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
        return entity;
    }

    public static ContraptionEntity get(UUID id) {
        return ACTIVE.get(id);
    }

    public static ContraptionEntity remove(UUID id) {
        return ACTIVE.remove(id);
    }

    public static java.util.Collection<ContraptionEntity> all() {
        return ACTIVE.values();
    }

    public static int count() {
        return ACTIVE.size();
    }
}
