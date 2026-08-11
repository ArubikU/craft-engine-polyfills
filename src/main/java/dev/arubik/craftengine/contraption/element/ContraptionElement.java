package dev.arubik.craftengine.contraption.element;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;

public interface ContraptionElement {

    Key type();
    Vec3 localOffset();
    boolean isValid();

    /** All packet entity IDs owned by this element — BLOCK_DISPLAY, ITEM, etc. */
    int[] entityIds();

    /**
     * Local-space AABBs used to create INTERACTION click-detection entities.
     * The overlay element reads these, manages entity lifecycle and LOD, and dispatches onInteract.
     * Return empty list for non-interactable elements (hitbox, entity mirror, etc.).
     */
    default List<AABB> interactionBounds() { return List.of(); }

    /** Called by the interaction overlay when a player clicks one of this element's interaction entities. */
    default boolean onInteract(Player player, int entityId, Vec3 hitPos, InteractionHand hand) { return false; }

    // ---- lifecycle ----

    default void tick(RenderContext ctx) {}
    void render(RenderContext ctx);
    void despawn(List<Player> viewers);
    void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns);

    // ---- persistence (opt-in) ----

    default boolean isPersistent() { return false; }
    default CompoundTag toNbt() { return null; }
}
