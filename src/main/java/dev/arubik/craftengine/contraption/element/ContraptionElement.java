package dev.arubik.craftengine.contraption.element;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;

/**
 * Self-contained contraption element — the atomic unit of a contraption's captured structure.
 *
 * <p>Each element owns its data, renders itself via packet entities, handles its lifecycle,
 * and exposes its packet entity IDs so interaction events can resolve back to the element.
 * Pure NMS — no Bukkit dependency.
 *
 * <p>Most elements are ephemeral (derived from ContraptionLevel on spawn, never serialized).
 * A persistent element overrides {@link #isPersistent()} and {@link #toNbt()} to survive
 * across serialization cycles independently of the level's own block/furniture data.
 */
public interface ContraptionElement {

    Key type();

    Vec3 localOffset();

    boolean isValid();

    /**
     * Packet entity IDs owned by this element. Used for interaction dispatch:
     * when a player clicks a packet entity, the owning element is resolved via these IDs.
     */
    int[] entityIds();

    // ---- lifecycle ----

    default void tick(RenderContext ctx) {}

    void render(RenderContext ctx);

    void despawn(List<Player> viewers);

    void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns);

    // ---- persistence (opt-in) ----

    default boolean isPersistent() { return false; }

    default CompoundTag toNbt() { return null; }
}
