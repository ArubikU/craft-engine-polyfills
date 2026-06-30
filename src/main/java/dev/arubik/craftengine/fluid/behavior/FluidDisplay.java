package dev.arubik.craftengine.fluid.behavior;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector3f;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * A server-side fake {@code minecraft:item_display} entity for the multiblock tank's fluid, sent to players
 * by PACKET (no real Bukkit entity). One per footprint cell per filled layer; the controller broadcasts to
 * the players tracking its chunk every tick, so anyone who loads the chunk (or joins, or widens view distance)
 * gets it on the next tick and players who leave the chunk drop it. Mirrors {@code ConveyorItemDisplay} but
 * static (no rotation) and carries its own target position + scale.
 */
public final class FluidDisplay {

    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;

    /** Players already sent the spawn (add) packet. */
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

    /** target absolute world position (model centre) + scale. */
    double tx, ty, tz;
    private Vector3f scale = new Vector3f(1f, 1f, 1f);
    private Object nmsItemStack;
    private boolean metaDirty = true;

    public FluidDisplay() {
        this.entityId = nextEntityId();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    /** Set the NMS item stack (net.minecraft.world.item.ItemStack); marks metadata dirty when it changes. */
    public void setNmsItem(Object nms) {
        if (nms != this.nmsItemStack) {
            this.nmsItemStack = nms;
            this.metaDirty = true;
        }
    }

    public void setScale(float x, float y, float z) {
        Vector3f next = new Vector3f(x, y, z);
        if (!next.equals(this.scale, 1e-4f)) {
            this.scale = next;
            this.metaDirty = true;
        }
    }

    public void setTarget(double x, double y, double z) {
        this.tx = x;
        this.ty = y;
        this.tz = z;
    }

    public boolean consumeMetaDirty() {
        boolean d = metaDirty;
        metaDirty = false;
        return d;
    }

    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        if (nmsItemStack != null)
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItemStack, values);
        DisplayData.Scale.addEntityData(scale, values);
        // Full block+sky light so the fluid never renders pitch-black inside the tank.
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        return values;
    }

    private void spawn(Player player) {
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, tx, ty, tz, 0f, 0f,
                net.minecraft.world.entity.EntityType.ITEM_DISPLAY, 0,
                net.minecraft.world.phys.Vec3.ZERO, 0);
        Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
        player.sendPackets(List.of(add, data), false);
    }

    private void updateMetadata(Player player) {
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
    }

    private void updatePosition(Player player) {
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                entityId, tx, ty, tz, 0f, 0f, false), false);
    }

    public void despawn(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    /**
     * Per-viewer render: spawns for any tracked player who hasn't seen it, re-pushes metadata when
     * {@code forceMeta} (item/scale changed), and moves it for the rest. Players who left the chunk are
     * dropped so they re-spawn on return.
     */
    public void render(List<Player> viewers, boolean forceMeta) {
        Set<UUID> current = new HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            if (id == null)
                continue;
            current.add(id);
            if (shownTo.add(id)) {
                spawn(p);
            } else {
                updatePosition(p);
                if (forceMeta)
                    updateMetadata(p);
            }
        }
        shownTo.retainAll(current);
    }

    /** Despawn for everyone currently tracking + forget them. */
    public void despawnAll(List<Player> viewers) {
        for (Player p : viewers)
            despawn(p);
        shownTo.clear();
    }

    private static UUID uuidOf(Player player) {
        Object pp = player.platformPlayer();
        return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            ENTITY_COUNTER = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
