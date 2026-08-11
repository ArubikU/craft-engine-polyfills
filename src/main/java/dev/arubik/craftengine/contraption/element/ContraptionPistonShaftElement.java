package dev.arubik.craftengine.contraption.element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Quaternionf;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;

public final class ContraptionPistonShaftElement implements ContraptionElement {

    private static final Quaternionf IDENTITY = new Quaternionf();
    private static final Map<String, Object> ITEM_CACHE = new ConcurrentHashMap<>();

    private final List<Segment> segments = new ArrayList<>();

    private Vec3 facing;
    private double extended;
    private String headItemId;
    private String pipeItemId;

    public void updateShaft(Vec3 facing, double extended, String headItemId, String pipeItemId) {
        this.facing = facing;
        this.extended = extended;
        this.headItemId = headItemId;
        this.pipeItemId = pipeItemId;
    }

    @Override
    public Key type() { return ElementTypes.PISTON_SHAFT; }

    @Override
    public Vec3 localOffset() { return Vec3.ZERO; }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int[] entityIds() {
        int[] ids = new int[segments.size()];
        for (int i = 0; i < segments.size(); i++) ids[i] = segments.get(i).entityId;
        return ids;
    }

    @Override
    public List<net.minecraft.world.phys.AABB> interactionBounds() { return List.of(); }

    @Override
    public void tick(RenderContext ctx) {}

    @Override
    public void render(RenderContext ctx) {
        List<Player> viewers = ctx.viewers();
        Vec3 bearingWorldPos = ctx.bearing();
        boolean moved = ctx.moved();
        int used = 0;
        if (facing != null && extended > 1.0e-6) {
            Object pipeNms = resolveNmsItem(pipeItemId);
            Object headNms = resolveNmsItem(headItemId);
            int pipeCount = (int) Math.floor(extended - 1.0e-6);
            if (pipeNms != null) {
                for (int i = 1; i <= pipeCount; i++) {
                    Vec3 pos = new Vec3(
                            bearingWorldPos.x + facing.x * i + 0.5,
                            bearingWorldPos.y + facing.y * i + 0.5,
                            bearingWorldPos.z + facing.z * i + 0.5);
                    segment(used++).render(viewers, pipeNms, IDENTITY, pos.x, pos.y, pos.z, moved);
                }
            }
            if (headNms != null) {
                Vec3 pos = new Vec3(
                        bearingWorldPos.x + facing.x * extended + 0.5,
                        bearingWorldPos.y + facing.y * extended + 0.5,
                        bearingWorldPos.z + facing.z * extended + 0.5);
                segment(used++).render(viewers, headNms, headRotationFor(facing), pos.x, pos.y, pos.z, moved);
            }
        }
        for (int i = used; i < segments.size(); i++) {
            segments.get(i).despawnAll(viewers);
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Segment s : segments) s.despawnAll(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {}

    private Segment segment(int index) {
        while (segments.size() <= index) segments.add(new Segment());
        return segments.get(index);
    }

    public static String shaftItemFor(Vec3 facing) {
        if (Math.abs(facing.y) > 0.5) return "cml:iron_pipe_nnnncc";
        if (Math.abs(facing.x) > 0.5) return "cml:iron_pipe_ncncnn";
        return "cml:iron_pipe_cncnnn";
    }

    public static String headItem() {
        return "cml:piston_head";
    }

    public static Quaternionf headRotationFor(Vec3 facing) {
        float pitch, yaw;
        if (facing.y > 0.5)       { pitch = -90f; yaw =   0f; }
        else if (facing.y < -0.5) { pitch =  90f; yaw =   0f; }
        else if (facing.x > 0.5)  { pitch =   0f; yaw =  90f; }
        else if (facing.x < -0.5) { pitch =   0f; yaw = 270f; }
        else if (facing.z > 0.5)  { pitch =   0f; yaw = 180f; }
        else                       { pitch =   0f; yaw =   0f; }
        return new Quaternionf().rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
    }

    private static Object resolveNmsItem(String itemId) {
        if (itemId == null || itemId.isBlank()) return null;
        return ITEM_CACHE.computeIfAbsent(itemId, id -> {
            try {
                var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(Key.of(id));
                if (def == null) return null;
                org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                return bukkit == null ? null : CraftItemStack.asNMSCopy(bukkit);
            } catch (Throwable ignored) {
                return null;
            }
        });
    }

    private static final class Segment {
        final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        private Object lastNmsItem;
        private Quaternionf lastRotation = IDENTITY;

        Segment() {
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        private List<Object> metadata(Object nmsItem, Quaternionf rotation) {
            List<Object> values = new ArrayList<>();
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nmsItem, values);
            DisplayData.LeftRotation.addEntityData(rotation, values);
            DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            return values;
        }

        void render(List<Player> viewers, Object nmsItem, Quaternionf rotation,
                    double x, double y, double z, boolean moved) {
            boolean metaChanged = nmsItem != lastNmsItem || !rotation.equals(lastRotation, 1e-4f);
            lastNmsItem = nmsItem;
            lastRotation = rotation;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) {
                    Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                            entityId, uuid, x, y, z, 0f, 0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0);
                    Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(nmsItem, rotation));
                    p.sendPackets(List.of(add, data), false);
                } else {
                    if (moved) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                                entityId, x, y, z, 0f, 0f, false), false);
                    }
                    if (metaChanged) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(
                                entityId, metadata(nmsItem, rotation)), false);
                    }
                }
            }
            shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) p.sendPacket(despawnPacket, false);
            shownTo.clear();
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            java.lang.reflect.Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
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
