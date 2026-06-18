package dev.arubik.craftengine.conveyor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * A server-side fake {@code minecraft:item_display} entity used to render a single
 * translucent, GLOWING preview belt while the conveyor wand is laying out a route.
 *
 * <p>Modelled on {@link ConveyorItemDisplay} (direct clientbound packets to tracked
 * players) but tuned for a full-block-scale preview: it shows the render-only
 * conveyor preview item-model (blue 50% alpha textures), forces full brightness and
 * sets the shared {@code glowing} entity flag so the belt outline pops. The wand
 * spawns one per planned cell and despawns them when the selection changes.</p>
 */
public final class ConveyorPreviewDisplay {

    /** Shared-entity-flags bit for "glowing" (Entity flag index 6). */
    private static final byte FLAG_GLOWING = 0x40;

    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;

    private Object nmsItemStack;
    private Quaternionf rotation = new Quaternionf();
    private Vector3f scale = new Vector3f(1f, 1f, 1f);
    private final java.util.Set<UUID> shownTo = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public ConveyorPreviewDisplay() {
        this.entityId = nextEntityId();
        this.despawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    public void setNmsItem(Object nmsItemStack) {
        this.nmsItemStack = nmsItemStack;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation != null ? rotation : new Quaternionf();
    }

    public void setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
    }

    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        // Shared flags: set the glowing bit so the preview gets a coloured outline.
        values.add(SynchedEntityData.DataValue.create(
                new net.minecraft.network.syncher.EntityDataAccessor<>(0, EntityDataSerializers.BYTE),
                FLAG_GLOWING));
        if (nmsItemStack != null)
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItemStack, values);
        DisplayData.Scale.addEntityData(scale, values);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        DisplayData.LeftRotation.addEntityData(rotation, values);
        return values;
    }

    private void spawn(Player player, double x, double y, double z) {
        Object addPacket = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, x, y, z, 0f, 0f,
                EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
        Object dataPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata());
        player.sendPackets(List.of(addPacket, dataPacket), false);
    }

    /** Spawn for any tracked viewer who hasn't seen it; drop viewers who left. */
    public void render(List<Player> viewers, double x, double y, double z) {
        java.util.Set<UUID> current = new java.util.HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            if (id == null)
                continue;
            current.add(id);
            if (shownTo.add(id))
                spawn(p, x, y, z);
        }
        shownTo.retainAll(current);
    }

    public void despawn(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    public void despawnAll(List<Player> viewers) {
        for (Player p : viewers)
            despawn(p);
        shownTo.clear();
    }

    private static UUID uuidOf(Player player) {
        Object pp = player.platformPlayer();
        return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
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

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
