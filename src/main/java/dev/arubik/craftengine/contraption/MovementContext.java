package dev.arubik.craftengine.contraption;

import net.minecraft.server.level.ServerLevel;

/**
 * Per-tick context passed to every {@link MovementBehavior#tick}. {@code level} is null
 * when the contraption's world isn't currently loaded (e.g. under a pure-JVM unit test, or
 * a genuinely unloaded Bukkit world) — behaviors that touch the real world (e.g.
 * {@code MinerBehavior} via {@link ContraptionAccessor}) must no-op when it's null.
 */
public record MovementContext(ContraptionState state, ServerLevel level) {
}
