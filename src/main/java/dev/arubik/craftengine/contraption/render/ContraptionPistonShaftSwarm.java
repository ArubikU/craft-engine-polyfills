package dev.arubik.craftengine.contraption.render;

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

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Packet-only render of an extending piston bearing's SHAFT — the middle "pipe" segments plus
 * the leading "head", drawn along the bearing's facing at its current extension. The bearing
 * BODY is the real block at the anchor (never captured, stays in the world), and the pushed
 * structure renders via {@code ContraptionDisplaySwarm} at its extended position — this swarm
 * fills the visible gap between them with the pipe run and caps it with the head, exactly like
 * Create's mechanical-piston pole.
 *
 * <p><b>2026-07-03 rewrite — "veo que los entity renderer no se estan renderizando en tu shaft.
 * creo que solo renderizabas bloques solidos y no los que usan entity."</b> The bearing's pipe/
 * head appearances are configured with {@code entity-renderer: { item: ... }} over a null block
 * model (same pattern {@code cml:copper_tank}/{@code cml:iron_pipe} use) — CraftEngine renders
 * THAT visual via a real {@code ITEM_DISPLAY} entity spawned per-position by its own world-storage
 * layer (confirmed by reading {@code ContraptionBlockEntityElementMirror}'s javadoc: the
 * "entity-renderer" YAML path becomes an {@code ItemDisplayBlockEntityElement}, mirrored there as
 * a real {@code EntityType.ITEM_DISPLAY}), NOT a raw {@code BlockState} on a {@code BLOCK_DISPLAY}.
 * The previous version of this class sent {@code DisplayData.BlockDisplayData.BlockState} for a
 * state whose bound model is deliberately NULL (invisible) — it rendered nothing at all, which is
 * exactly the reported bug. Fixed by copying the same technique every other item-visual swarm in
 * this codebase already uses ({@code ConveyorItemDisplay}, {@code FluidDisplay}): resolve the
 * bearing's own (facing, part) combo directly to a CraftEngine ITEM id in plain Java (no
 * blockstate roundtrip needed at all for this packet-only visual — the item ids are already
 * hardcoded 1:1 with facing/axis in {@code bearing.yml}), then spawn a real {@code ITEM_DISPLAY}
 * per segment showing that item, same as {@code FluidDisplay}/{@code ConveyorItemDisplay} do.
 */
public final class ContraptionPistonShaftSwarm {

    private static final Quaternionf IDENTITY = new Quaternionf();

    /** One packet-only item_display segment, reused across ticks by its index key. */
    private final List<Segment> segments = new ArrayList<>();

    /** {@code net.minecraft.world.item.ItemStack} cache keyed by CraftEngine item id — resolved once. */
    private static final Map<String, Object> ITEM_CACHE = new ConcurrentHashMap<>();

    private static Object resolveNmsItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        return ITEM_CACHE.computeIfAbsent(itemId, id -> {
            try {
                var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(Key.of(id));
                if (def == null) {
                    return null;
                }
                org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                return bukkit == null ? null : CraftItemStack.asNMSCopy(bukkit);
            } catch (Throwable ignored) {
                return null;
            }
        });
    }

    /** Shaft (part=3) item id for the facing's axis — matches bearing.yml's s_vert/s_ns/s_ew entity-renderer items. */
    public static String shaftItemFor(Vec3 facing) {
        if (Math.abs(facing.y) > 0.5) {
            return "cml:iron_pipe_nnnncc";
        }
        if (Math.abs(facing.x) > 0.5) {
            return "cml:iron_pipe_ncncnn";
        }
        return "cml:iron_pipe_cncnnn";
    }

    /** Head (part=2) item id — ONE plain unrotated item (2026-07-04 — "las orientaciones no cargan":
     *  an item's own model x/y rotation isn't respected here; orientation is applied at render time
     *  via {@link #headRotationFor}, matching bearing.yml's entity-renderer `rotation:` degrees). */
    public static String headItem() {
        return "cml:piston_head";
    }

    /** Rotation (pitch, yaw in degrees) for the head item at this facing — same angles bearing.yml's
     *  h_up/h_down/... entity-renderer `rotation:` strings use, applied here via LeftRotation since
     *  this packet-only ITEM_DISPLAY doesn't go through CraftEngine's own entity-renderer config. */
    public static Quaternionf headRotationFor(Vec3 facing) {
        float pitch, yaw;
        if (facing.y > 0.5) {
            pitch = -90f; yaw = 0f;
        } else if (facing.y < -0.5) {
            pitch = 90f; yaw = 0f;
        } else if (facing.x > 0.5) {
            pitch = 0f; yaw = 90f;
        } else if (facing.x < -0.5) {
            pitch = 0f; yaw = 270f;
        } else if (facing.z > 0.5) {
            pitch = 0f; yaw = 180f;
        } else {
            pitch = 0f; yaw = 0f;
        }
        return new Quaternionf().rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
    }

    /**
     * Render the shaft for this tick. {@code bearingWorldPos} is the bearing block's own corner
     * (integer origin, same convention the other swarms use); {@code facing} is the unit push
     * direction; {@code extended} is the current continuous extension in blocks; {@code headItemId}/
     * {@code pipeItemId} are the CraftEngine item ids to display (either may be null → that piece
     * is skipped). Segments no longer needed this tick are despawned.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, Vec3 facing, double extended,
            String headItemId, String pipeItemId, boolean moved) {
        int used = 0;
        Object pipeNms = resolveNmsItem(pipeItemId);
        Object headNms = resolveNmsItem(headItemId);
        // Pipe run: one segment per whole block between the body and the head — no rotation needed,
        // the pipe items are already per-axis oriented assets (iron_pipe_*).
        int pipeCount = (int) Math.floor(extended - 1.0e-6);
        if (pipeNms != null) {
            for (int i = 1; i <= pipeCount; i++) {
                Vec3 pos = new Vec3(bearingWorldPos.x + facing.x * i + 0.5, bearingWorldPos.y + facing.y * i + 0.5,
                        bearingWorldPos.z + facing.z * i + 0.5);
                segment(used++).render(viewers, pipeNms, IDENTITY, pos.x, pos.y, pos.z, moved);
            }
        }
        // Head: at the continuous leading edge — ONE plain item, rotated to match facing.
        if (headNms != null && extended > 1.0e-6) {
            Vec3 pos = new Vec3(bearingWorldPos.x + facing.x * extended + 0.5, bearingWorldPos.y + facing.y * extended + 0.5,
                    bearingWorldPos.z + facing.z * extended + 0.5);
            segment(used++).render(viewers, headNms, headRotationFor(facing), pos.x, pos.y, pos.z, moved);
        }
        // Despawn any segments beyond what we used this tick (extension shrank / fully retracted).
        for (int i = used; i < segments.size(); i++) {
            segments.get(i).despawnAll(viewers);
        }
    }

    public void despawnAll(List<Player> viewers) {
        for (Segment s : segments) {
            s.despawnAll(viewers);
        }
    }

    private Segment segment(int index) {
        while (segments.size() <= index) {
            segments.add(new Segment());
        }
        return segments.get(index);
    }

    /** One reusable packet-only item_display, centered on the segment's block cell. */
    private static final class Segment {
        private final int entityId = nextEntityId();
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

        void render(List<Player> viewers, Object nmsItem, Quaternionf rotation, double x, double y, double z, boolean moved) {
            boolean metaChanged = nmsItem != lastNmsItem || !rotation.equals(lastRotation, 1e-4f);
            lastNmsItem = nmsItem;
            lastRotation = rotation;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
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
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(nmsItem, rotation)), false);
                    }
                }
            }
            shownTo.retainAll(current);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                p.sendPacket(despawnPacket, false);
            }
            shownTo.clear();
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    // ---- fresh server-unique fake entity id ----
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
