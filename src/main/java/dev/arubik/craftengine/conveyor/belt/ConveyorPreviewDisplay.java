package dev.arubik.craftengine.conveyor.belt;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ConveyorPreviewDisplay {
    private static final byte FLAG_GLOWING = 64;
    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;
    private Object nmsItemStack;
    private Quaternionf rotation = new Quaternionf();
    private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger ENTITY_COUNTER;

    public ConveyorPreviewDisplay() {
        this.entityId = ConveyorPreviewDisplay.nextEntityId();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
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
        ArrayList<Object> values = new ArrayList<Object>();
        // BYTE-serialized accessor — a bare int literal autoboxes to Integer, which
        // SynchedEntityData's write path casts straight to Byte with no coercion, crashing the
        // client's packet decoder the moment this preview entity's metadata is sent (this is the
        // "left click with cml:conveyor item" disconnect: entity flags byte 0x40 = glowing).
        values.add(SynchedEntityData.DataValue.create((EntityDataAccessor)new EntityDataAccessor(0, EntityDataSerializers.BYTE), (byte)64));
        if (this.nmsItemStack != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(this.nmsItemStack, values);
        }
        DisplayData.Scale.addEntityData(this.scale, values);
        DisplayData.BrightnessOverride.addEntityData(0xF000F0, values);
        DisplayData.LeftRotation.addEntityData(this.rotation, values);
        return values;
    }

    private void spawn(net.momirealms.craftengine.core.entity.player.Player player, double x, double y, double z) {
        Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
        Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata());
        player.sendPackets(List.of(addPacket, dataPacket), false);
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, double x, double y, double z) {
        HashSet<UUID> current = new HashSet<UUID>();
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            UUID id = ConveyorPreviewDisplay.uuidOf(p);
            if (id == null) continue;
            current.add(id);
            if (!this.shownTo.add(id)) continue;
            this.spawn(p, x, y, z);
        }
        this.shownTo.retainAll(current);
    }

    public void despawn(net.momirealms.craftengine.core.entity.player.Player player) {
        player.sendPacket(this.despawnPacket, false);
    }

    public void despawnAll(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            this.despawn(p);
        }
        this.shownTo.clear();
    }

    private static UUID uuidOf(net.momirealms.craftengine.core.entity.player.Player player) {
        UUID uUID;
        Object pp = player.platformPlayer();
        if (pp instanceof Player) {
            Player b = (Player)pp;
            uUID = b.getUniqueId();
        } else {
            uUID = null;
        }
        return uUID;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger counter;
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger)f.get(null);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }
}

