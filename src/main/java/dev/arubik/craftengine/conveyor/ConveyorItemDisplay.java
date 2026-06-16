package dev.arubik.craftengine.conveyor;

import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.world.BlockPos;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A server-side fake {@code minecraft:item_display} entity used to visualise the
 * single in-transit item riding a conveyor belt. Driven imperatively by
 * {@link ConveyorBlockEntity}: the controller decides which players track the
 * belt's chunk and calls {@link #spawn}, {@link #updatePosition} and
 * {@link #despawn} each tick.
 *
 * <p>Modelled on {@code machine/render/element/ShulkerBoxHitboxElement} but it
 * is NOT a {@code ConstantBlockEntityElement}: the position changes every tick,
 * which the constant-element pipeline does not support, so we broadcast packets
 * directly to tracked players.</p>
 */
public final class ConveyorItemDisplay {

    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;

    /** absolute world position currently displayed */
    private double curX, curY, curZ;
    private Object nmsItemStack;

    /** Players who have already been sent the spawn (add) packet for this entity. */
    private final java.util.Set<UUID> shownTo = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public ConveyorItemDisplay() {
        this.entityId = nextEntityId();
        this.despawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    public int entityId() {
        return entityId;
    }

    /** Orientation of the carried item: yaw from belt facing + pitch from slope. */
    private org.joml.Quaternionf rotation = new org.joml.Quaternionf();

    /** Set the NMS item stack to render (net.minecraft.world.item.ItemStack). */
    public void setNmsItem(Object nmsItemStack) {
        this.nmsItemStack = nmsItemStack;
    }

    /** True when the rotation changed since the last metadata push (needs re-send). */
    private boolean rotationDirty = false;

    /** Set the display rotation (oriented along the belt + tilted on ramps). */
    public void setRotation(org.joml.Quaternionf rotation) {
        org.joml.Quaternionf next = rotation != null ? rotation : new org.joml.Quaternionf();
        if (!next.equals(this.rotation, 1e-4f))
            this.rotationDirty = true;
        this.rotation = next;
    }

    /** Display scale (block units). Default 0.5 = half a block (belt item). */
    private org.joml.Vector3f scale = new org.joml.Vector3f(0.5f, 0.5f, 0.5f);

    public void setScale(float s) {
        setScale(s, s, s);
    }

    public void setScale(float x, float y, float z) {
        org.joml.Vector3f next = new org.joml.Vector3f(x, y, z);
        if (!next.equals(this.scale, 1e-4f))
            this.rotationDirty = true; // metadata needs a re-push
        this.scale = next;
    }

    /** Consume the rotation-changed flag (so callers re-send metadata only when needed). */
    public boolean consumeRotationDirty() {
        boolean d = rotationDirty;
        rotationDirty = false;
        return d;
    }

    /** Build the entity metadata list (item + scale + orientation + interpolation). */
    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        if (nmsItemStack != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItemStack, values);
        }
        // Render the item at the configured scale (default half a block, for the belt).
        DisplayData.Scale.addEntityData(scale, values);
        // Force full block+sky light so the item never renders pitch-black in shade.
        // Brightness override packs (blockLight << 4) | (skyLight << 20); 15/15 = full.
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        // Orient along the belt (yaw) + tilt on ramps (pitch).
        DisplayData.LeftRotation.addEntityData(rotation, values);
        // Smoothly interpolate the position + rotation we issue each tick.
        DisplayData.PosRotInterpolationDuration.addEntityData(1, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    /**
     * Spawn the display for one player at the given absolute world position.
     */
    public void spawn(Player player, double x, double y, double z) {
        this.curX = x;
        this.curY = y;
        this.curZ = z;
        Object addPacket = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, x, y, z, 0f, 0f,
                net.minecraft.world.entity.EntityType.ITEM_DISPLAY, 0,
                net.minecraft.world.phys.Vec3.ZERO, 0);
        Object dataPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata());
        player.sendPackets(List.of(addPacket, dataPacket), false);
    }

    /** Push the latest item metadata to a player (call when the carried item changes). */
    public void updateMetadata(Player player) {
        player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
    }

    /** Move the display to an absolute world position for one player. */
    public void updatePosition(Player player, double x, double y, double z) {
        this.curX = x;
        this.curY = y;
        this.curZ = z;
        player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
    }

    /** Remove the display for one player. */
    public void despawn(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    /**
     * Per-viewer render: spawns the entity for any tracked player who hasn't seen it
     * yet (so EVERY player — not just whoever was online at first spawn — sees the
     * item), moves it for those who already have, and re-pushes metadata when
     * {@code forceMeta} (item/rotation changed). Players who left the chunk are dropped
     * from the seen-set so they re-spawn on return.
     */
    public void render(List<Player> viewers, double x, double y, double z, boolean forceMeta) {
        java.util.Set<UUID> current = new java.util.HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            if (id == null)
                continue;
            current.add(id);
            if (shownTo.add(id)) {
                spawn(p, x, y, z); // sends add + data (current item + rotation)
            } else {
                updatePosition(p, x, y, z);
                if (forceMeta)
                    updateMetadata(p);
            }
        }
        shownTo.retainAll(current);
        this.curX = x;
        this.curY = y;
        this.curZ = z;
    }

    /** Forget who has seen this entity (call after despawning for everyone). */
    public void clearShown() {
        shownTo.clear();
    }

    private static UUID uuidOf(Player player) {
        Object pp = player.platformPlayer();
        return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
