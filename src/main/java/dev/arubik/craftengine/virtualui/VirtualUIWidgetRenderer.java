package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.util.MNms;
import dev.arubik.craftengine.virtualui.render.WidgetVisual;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * One packet-only display entity for a single widget PART, for a single viewer session — the
 * generic spawn/move/despawn machinery shared by EVERY widget kind, modelled on
 * {@code ChainBlockDisplay}/{@code ConveyorItemDisplay}. Deliberately knows nothing about widget
 * kinds: what to actually render each tick comes entirely from whatever {@link WidgetVisual} is
 * handed to {@link #render}, so adding a new widget kind (or a new part on an existing one — see
 * {@code dev.arubik.craftengine.virtualui.render.PositionedVisual}) never touches this class
 * (open/closed — see {@code WidgetVisualRegistry}).
 *
 * <p>The display entity TYPE is learned from the first {@link WidgetVisual} it's ever given, not
 * passed in up front — {@link WidgetVisual#entityType()} is documented to stay stable for one
 * renderer's lifetime, so this only needs to ask once.
 *
 * <p>Single-viewer (unlike {@code ChainBlockDisplay}'s {@code Set<UUID> shownTo>} for many
 * viewers) because a VirtualUI session is inherently one player looking at their own locked
 * camera — no other player ever sees these entities.
 */
public final class VirtualUIWidgetRenderer {

    private final int entityId = nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;

    private EntityType<?> entityType;
    private WidgetVisual visual;
    private boolean spawned = false;
    private double lastX, lastY, lastZ;

    public VirtualUIWidgetRenderer() {
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    /** Renders this tick: spawns on first call (learning its entity type from {@code visual}),
     *  otherwise repositions and — only when {@code visual} actually changed (per
     *  {@link WidgetVisual#sameAs}) — resends metadata. */
    public void render(Player player, WidgetVisual visual, double x, double y, double z) {
        if (entityType == null) entityType = visual.entityType();
        boolean visualDirty = this.visual == null || !this.visual.sameAs(visual);
        this.visual = visual;
        if (!spawned) {
            spawn(player, x, y, z);
            spawned = true;
        } else {
            boolean moved = Math.abs(x - lastX) > 1.0e-4 || Math.abs(y - lastY) > 1.0e-4 || Math.abs(z - lastZ) > 1.0e-4;
            if (moved) updatePosition(player, x, y, z);
            if (visualDirty) updateMetadata(player);
        }
        lastX = x; lastY = y; lastZ = z;
    }

    private void spawn(Player player, double x, double y, double z) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, x, y, z, 0f, 0f, entityType, 0, Vec3.ZERO, 0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadataOrEmpty());
        player.sendPackets(List.of(add, data), false);
    }

    private void updatePosition(Player player, double x, double y, double z) {
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                entityId, x, y, z, 0f, 0f, false), false);
    }

    private void updateMetadata(Player player) {
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadataOrEmpty()), false);
    }

    private List<Object> metadataOrEmpty() {
        return visual != null ? visual.buildMetadata() : List.of();
    }

    public void despawn(Player player) {
        if (!spawned) return;
        player.sendPacket(despawnPacket, false);
        spawned = false;
    }

    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() { return ENTITY_COUNTER.incrementAndGet(); }
}
