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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ConveyorItemDisplay {
    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private int packedLight = 0xF000F0;
    private final Object despawnPacket;
    private double curX;
    private double curY;
    private double curZ;
    private Object nmsItemStack;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private Quaternionf rotation = new Quaternionf();
    private boolean rotationDirty = false;
    private Vector3f scale = new Vector3f(0.5f, 0.5f, 0.5f);
    private static final AtomicInteger ENTITY_COUNTER;

    public ConveyorItemDisplay() {
        this.entityId = ConveyorItemDisplay.nextEntityId();
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
    }

    public int entityId() {
        return this.entityId;
    }

    public void setNmsItem(Object nmsItemStack) {
        this.nmsItemStack = nmsItemStack;
    }

    public void setRotation(Quaternionf rotation) {
        Quaternionf next;
        Quaternionf quaternionf = next = rotation != null ? rotation : new Quaternionf();
        if (!next.equals((Quaternionfc)this.rotation, 1.0E-4f)) {
            this.rotationDirty = true;
        }
        this.rotation = next;
    }

    public void setScale(float s) {
        this.setScale(s, s, s);
    }

    public void setScale(float x, float y, float z) {
        Vector3f next = new Vector3f(x, y, z);
        if (!next.equals((Vector3fc)this.scale, 1.0E-4f)) {
            this.rotationDirty = true;
        }
        this.scale = next;
    }

    public boolean consumeRotationDirty() {
        boolean d = this.rotationDirty;
        this.rotationDirty = false;
        return d;
    }

    private List<Object> metadata() {
        ArrayList<Object> values = new ArrayList<Object>();
        if (this.nmsItemStack != null) {
            DisplayData.ItemDisplayData.ItemStack.addEntityData(this.nmsItemStack, values);
        }
        DisplayData.Scale.addEntityData(this.scale, values);
        DisplayData.BrightnessOverride.addEntityData(this.packedLight, values);
        DisplayData.LeftRotation.addEntityData(this.rotation, values);
        // Widened from 1/2 to match ROTATION_PACKET_INTERVAL (DataMachineBlockEntity) — metadata
        // packets for a continuously-spinning display are now throttled to every few ticks instead
        // of every tick, so the client needs a matching interpolation window to smooth across the
        // gap instead of holding still then snapping.
        DisplayData.PosRotInterpolationDuration.addEntityData(4, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(4, values);
        return values;
    }

    public void spawn(net.momirealms.craftengine.core.entity.player.Player player, double x, double y, double z) {
        this.curX = x;
        this.curY = y;
        this.curZ = z;
        Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
        Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata());
        player.sendPackets(List.of(addPacket, dataPacket), false);
    }

    public void updateMetadata(net.momirealms.craftengine.core.entity.player.Player player) {
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata()), false);
    }

    public boolean setLightFromLevel(ServerLevel level, double wx, double wy, double wz) {
        int packed;
        BlockPos pos = BlockPos.containing((double)wx, (double)wy, (double)wz);
        int bl = level.getBrightness(LightLayer.BLOCK, pos);
        int sl = level.getBrightness(LightLayer.SKY, pos);
        if (bl == 0 && sl == 0) {
            int sumBl = 0;
            int sumSl = 0;
            for (Direction dir : Direction.values()) {
                BlockPos n = pos.relative(dir);
                sumBl += level.getBrightness(LightLayer.BLOCK, n);
                sumSl += level.getBrightness(LightLayer.SKY, n);
            }
            bl = sumBl / 6;
            sl = sumSl / 6;
        }
        if ((packed = bl << 4 | sl << 20) == this.packedLight) {
            return false;
        }
        this.packedLight = packed;
        return true;
    }

    public void updatePosition(net.momirealms.craftengine.core.entity.player.Player player, double x, double y, double z) {
        this.curX = x;
        this.curY = y;
        this.curZ = z;
        player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, x, y, z, 0.0f, 0.0f, false), false);
    }

    public void despawn(net.momirealms.craftengine.core.entity.player.Player player) {
        player.sendPacket(this.despawnPacket, false);
    }

    public void despawnAll(ServerLevel level) {
        if (this.shownTo.isEmpty()) {
            return;
        }
        try {
            for (UUID id : this.shownTo) {
                ServerPlayer sp = level.getServer().getPlayerList().getPlayer(id);
                if (sp == null) continue;
                sp.connection.send((Packet)this.despawnPacket);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.shownTo.clear();
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, double x, double y, double z, boolean forceMeta) {
        HashSet<UUID> current = new HashSet<UUID>();
        // Most of these displays sit at a fixed offset from their block and never actually move —
        // only their rotation/scale/item animate. Sending a position-sync packet every tick anyway
        // was pure waste; only send one when the anchor position genuinely changed.
        boolean moved = Math.abs(x - this.curX) > 1.0E-4 || Math.abs(y - this.curY) > 1.0E-4 || Math.abs(z - this.curZ) > 1.0E-4;
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            UUID id = ConveyorItemDisplay.uuidOf(p);
            if (id == null) continue;
            current.add(id);
            if (this.shownTo.add(id)) {
                this.spawn(p, x, y, z);
                continue;
            }
            if (moved) this.updatePosition(p, x, y, z);
            if (!forceMeta) continue;
            this.updateMetadata(p);
        }
        this.shownTo.retainAll(current);
        this.curX = x;
        this.curY = y;
        this.curZ = z;
    }

    public void clearShown() {
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

