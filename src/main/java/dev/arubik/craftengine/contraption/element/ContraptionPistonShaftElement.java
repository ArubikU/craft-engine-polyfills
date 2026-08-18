/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class ContraptionPistonShaftElement
implements ContraptionElement {
    private static final Quaternionf IDENTITY;
    private static final Map<String, Object> ITEM_CACHE;
    private final List<Segment> segments = new ArrayList<Segment>();
    private Vec3 facing;
    private double extended;
    private String headItemId;
    private String pipeItemId;
    private static final AtomicInteger ENTITY_COUNTER;

    public void updateShaft(Vec3 facing, double extended, String headItemId, String pipeItemId) {
        this.facing = facing;
        this.extended = extended;
        this.headItemId = headItemId;
        this.pipeItemId = pipeItemId;
    }

    @Override
    public Key type() {
        return ElementTypes.PISTON_SHAFT;
    }

    @Override
    public Vec3 localOffset() {
        return Vec3.ZERO;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        int[] ids = new int[this.segments.size()];
        for (int i = 0; i < this.segments.size(); ++i) {
            ids[i] = this.segments.get((int)i).entityId;
        }
        return ids;
    }

    @Override
    public List<AABB> interactionBounds() {
        return List.of();
    }

    @Override
    public void tick(RenderContext ctx) {
    }

    @Override
    public void render(RenderContext ctx) {
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = ctx.viewers();
        Vec3 bearingWorldPos = ctx.bearing();
        boolean moved = ctx.moved();
        int used = 0;
        if (this.facing != null && this.extended > 1.0E-6) {
            Object pipeNms = ContraptionPistonShaftElement.resolveNmsItem(this.pipeItemId);
            Object headNms = ContraptionPistonShaftElement.resolveNmsItem(this.headItemId);
            int pipeCount = (int)Math.floor(this.extended - 1.0E-6);
            if (pipeNms != null) {
                for (int i = 1; i <= pipeCount; ++i) {
                    Vec3 pos = new Vec3(bearingWorldPos.x + this.facing.x * (double)i + 0.5, bearingWorldPos.y + this.facing.y * (double)i + 0.5, bearingWorldPos.z + this.facing.z * (double)i + 0.5);
                    this.segment(used++).render(viewers, pipeNms, IDENTITY, pos.x, pos.y, pos.z, moved);
                }
            }
            if (headNms != null) {
                Vec3 pos = new Vec3(bearingWorldPos.x + this.facing.x * this.extended + 0.5, bearingWorldPos.y + this.facing.y * this.extended + 0.5, bearingWorldPos.z + this.facing.z * this.extended + 0.5);
                this.segment(used++).render(viewers, headNms, ContraptionPistonShaftElement.headRotationFor(this.facing), pos.x, pos.y, pos.z, moved);
            }
        }
        for (int i = used; i < this.segments.size(); ++i) {
            this.segments.get(i).despawnAll(viewers);
        }
    }

    @Override
    public void despawn(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        for (Segment s : this.segments) {
            s.despawnAll(viewers);
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private Segment segment(int index) {
        while (this.segments.size() <= index) {
            this.segments.add(new Segment());
        }
        return this.segments.get(index);
    }

    public static String shaftItemFor(Vec3 facing) {
        if (Math.abs(facing.y) > 0.5) {
            return "cml:iron_pipe_nnnncc";
        }
        if (Math.abs(facing.x) > 0.5) {
            return "cml:iron_pipe_ncncnn";
        }
        return "cml:iron_pipe_cncnnn";
    }

    public static String headItem() {
        return "cml:piston_head";
    }

    public static Quaternionf headRotationFor(Vec3 facing) {
        float yaw;
        float pitch;
        if (facing.y > 0.5) {
            pitch = -90.0f;
            yaw = 0.0f;
        } else if (facing.y < -0.5) {
            pitch = 90.0f;
            yaw = 0.0f;
        } else if (facing.x > 0.5) {
            pitch = 0.0f;
            yaw = 90.0f;
        } else if (facing.x < -0.5) {
            pitch = 0.0f;
            yaw = 270.0f;
        } else if (facing.z > 0.5) {
            pitch = 0.0f;
            yaw = 180.0f;
        } else {
            pitch = 0.0f;
            yaw = 0.0f;
        }
        return new Quaternionf().rotateY((float)Math.toRadians(yaw)).rotateX((float)Math.toRadians(pitch));
    }

    private static Object resolveNmsItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        return ITEM_CACHE.computeIfAbsent(itemId, id -> {
            try {
                BukkitItemDefinition def = CraftEngineItems.byId((Key)Key.of((String)id));
                if (def == null) {
                    return null;
                }
                ItemStack bukkit = def.buildBukkitItem();
                return bukkit == null ? null : CraftItemStack.asNMSCopy((ItemStack)bukkit);
            }
            catch (Throwable ignored) {
                return null;
            }
        });
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger counter;
        IDENTITY = new Quaternionf();
        ITEM_CACHE = new ConcurrentHashMap<String, Object>();
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

    private static final class Segment {
        final int entityId = ContraptionPistonShaftElement.nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        private Object lastNmsItem;
        private Quaternionf lastRotation = IDENTITY;

        Segment() {
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        private List<Object> metadata(Object nmsItem, Quaternionf rotation) {
            ArrayList<Object> values = new ArrayList<Object>();
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
            DisplayData.LeftRotation.addEntityData(rotation, values);
            DisplayData.BrightnessOverride.addEntityData(0xF000F0, values);
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            return values;
        }

        void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Object nmsItem, Quaternionf rotation, double x, double y, double z, boolean moved) {
            boolean metaChanged = nmsItem != this.lastNmsItem || !rotation.equals((Quaternionfc)this.lastRotation, 1.0E-4f);
            this.lastNmsItem = nmsItem;
            this.lastRotation = rotation;
            HashSet<UUID> current = new HashSet<UUID>();
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                UUID id = Segment.uuidOf(p);
                if (id == null) continue;
                current.add(id);
                if (this.shownTo.add(id)) {
                    Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
                    Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata(nmsItem, rotation));
                    p.sendPackets(List.of(add, data), false);
                    continue;
                }
                if (moved) {
                    p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, x, y, z, 0.0f, 0.0f, false), false);
                }
                if (!metaChanged) continue;
                p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata(nmsItem, rotation)), false);
            }
            this.shownTo.retainAll(current);
        }

        void despawnAll(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                p.sendPacket(this.despawnPacket, false);
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
    }
}

