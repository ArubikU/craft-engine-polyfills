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

    /** Set the display rotation (oriented along the belt + tilted on ramps). */
    public void setRotation(org.joml.Quaternionf rotation) {
        this.rotation = rotation != null ? rotation : new org.joml.Quaternionf();
    }

    /** Build the entity metadata list (item + scale + orientation + interpolation). */
    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        if (nmsItemStack != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItemStack, values);
        }
        // Render the item at roughly half scale so it sits on the belt.
        DisplayData.Scale.addEntityData(new Vector3f(0.5f, 0.5f, 0.5f), values);
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
