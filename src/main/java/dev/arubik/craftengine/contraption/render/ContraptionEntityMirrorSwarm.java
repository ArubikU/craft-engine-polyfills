package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import dev.arubik.craftengine.contraption.ContraptionFurniture;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.item.ItemEntityData;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * Packet-only mirror for the REAL entities (items, mobs, etc.) that end up physically living
 * inside a {@link ContraptionLevel} once it's captured — those entities keep ticking/moving
 * completely normally (real AI, real physics, real item merging), but the level itself hosts
 * zero real players, so nothing a real client would ever render them directly. This swarm is
 * the entity-side counterpart to {@link ContraptionDisplaySwarm} (captured blocks) and
 * {@link ContraptionHitboxSwarm} (rider collision): every tick it walks the level's real
 * entities and keeps one packet-only fake display per entity, positioned at
 * {@link ContraptionLevel#realWorldPositionOf(Vec3)} with the bearing's current yaw, sent to
 * whichever real viewers the caller resolved (same {@code viewers} list
 * {@link dev.arubik.craftengine.contraption.ContraptionEntity#render} already passes to the
 * other two swarms — see that class for why an all-world-players list, not chunk-tracking, is
 * this codebase's chosen viewer source for contraption rendering).
 *
 * <p>Best-effort fidelity, matching CONTRAPTIONS.md's stated scope: {@link ItemEntity}s render
 * as a real {@code minecraft:item} entity carrying the actual stack (so dropped items inside a
 * moving contraption look correct); every other entity type renders as a small invisible-ish
 * generic marker (an {@code ARMOR_STAND}) — full per-mob model fidelity is explicitly out of
 * scope here, same call already made for {@link ContraptionHitboxSwarm}'s INTERACTION hitboxes.
 *
 * <p><b>Furniture-owned real entities excluded (2026-07-02 session — "el furniture sigue
 * mostrando el armor stand").</b> Every {@link ContraptionFurniture} cell has a genuinely LIVE
 * {@code BukkitFurniture} placed inside this same {@link ContraptionLevel} (see that class's own
 * javadoc) — a real meta {@code ItemDisplay} entity PLUS one real {@code INTERACTION}/
 * {@code SHULKER} collider entity per configured hitbox part (confirmed by decompiling
 * CraftEngine's {@code AbstractFurnitureHitBox#createCollider}/{@code BukkitCollider} this
 * session). {@code level.getAllEntities()} returns ALL of those too, and since none of them is an
 * {@link ItemEntity} they used to fall through {@link #entityTypeFor} into the generic
 * {@code ARMOR_STAND} fallback branch — i.e. this swarm was mirroring the furniture's own real
 * meta entity/colliders a SECOND time, redundantly, ON TOP of {@code
 * render.ContraptionFurnitureSwarm}'s own purpose-built per-element/per-hitbox mirrors. That
 * redundant, wrongly-typed extra mirror is exactly the reported "ghost armor stand floating on
 * the furniture" — this swarm was only ever meant to catch genuinely INCIDENTAL real entities
 * that happen to be inside the captured footprint (mobs, boats, stray players), the same way
 * {@code ContraptionEntity#carryEntities}'s own javadoc already excludes {@code
 * ContraptionItemPickupSwarm}'s own mirror {@code ItemEntity}s from double-handling — furniture's
 * own backing entities need the identical treatment. {@link #furnitureOwnedEntityIds} is rebuilt
 * from {@code furniture}'s live {@code BukkitFurniture#entityId()}/{@code
 * interactableEntityIds()}/{@code colliderEntityIds()} every {@link #render} call (cheap: small
 * per-piece int arrays, no allocation-heavy world scan) and used purely to skip those ids below —
 * every other real entity inside the level (an actual stray mob, etc.) is still mirrored exactly
 * as before.
 */
public final class ContraptionEntityMirrorSwarm {

    private final Map<UUID, Cell> cells = new HashMap<>();

    /** Render every real entity currently inside {@code level} for {@code viewers}; despawns mirrors whose source entity is gone. */
    public void render(List<Player> viewers, ContraptionLevel level) {
        render(viewers, level, java.util.List.of());
    }

    /**
     * See class javadoc, "Furniture-owned real entities excluded" — {@code furniture} is the
     * SAME {@code state.furniture()} list {@code render.ContraptionFurnitureSwarm} already reads,
     * passed here purely to build the skip-set below.
     */
    public void render(List<Player> viewers, ContraptionLevel level, List<ContraptionFurniture> furniture) {
        render(viewers, level, furniture, 0.0, 0.0, 1.0);
    }

    /**
     * Full render with the body's tilt/scale so a mirrored FALLING BLOCK tumbles WITH the contraption (user:
     * "para el falling block usa un render custom para que soporte bien el yaw y pitch"): a falling block is
     * mirrored as a BLOCK_DISPLAY (not a flat FALLING_BLOCK entity) whose yaw rides the entity body and whose
     * pitch/roll ride the LeftRotation — the exact same orientation lever {@link ContraptionDisplaySwarm}'s cells
     * use — so it stays glued to the contraption's pose instead of standing bolt-upright.
     */
    public void render(List<Player> viewers, ContraptionLevel level, List<ContraptionFurniture> furniture,
            double pitchRadians, double rollRadians, double scale) {
        if (level == null) {
            return;
        }
        Set<Integer> furnitureOwnedEntityIds = collectFurnitureOwnedEntityIds(furniture);
        Set<UUID> present = new HashSet<>();
        double yawRadians = level.realYawRadians();
        float yawDegrees = (float) Math.toDegrees(yawRadians);
        for (Entity entity : level.getAllEntities()) {
            if (entity == null || entity.isRemoved()) {
                continue;
            }
            if (furnitureOwnedEntityIds.contains(entity.getId())) {
                continue; // already mirrored by ContraptionFurnitureSwarm — see class javadoc
            }
            // Only entities we can render faithfully at the contraption's arbitrary tilt stay inside it. Everything
            // else (a minecart on a captured rail, a mob that wandered in, a projectile) is EJECTED to the real
            // world (user: "las que no como minecarts o demás salgan al mundo real"). Projectiles then live in the
            // real world and ContraptionProjectileCollision sticks them onto a contraption on impact.
            if (!canMirror(entity)) {
                eject(entity, level);
                continue;
            }
            UUID id = entity.getUUID();
            present.add(id);
            Cell cell = cells.computeIfAbsent(id, k -> new Cell(entityTypeFor(entity)));
            cell.updateAppearance(entity);
            if (cell.type == EntityType.BLOCK_DISPLAY) {
                // Centre the block model on the falling entity (its position is the feet), tilt/scale it with the
                // body, and transform through the contraption pose. The +0.5 is in LOCAL space so it rotates too.
                cell.setOrientation((float) pitchRadians, (float) rollRadians, (float) scale);
                Vec3 real = level.realWorldPositionOf(entity.position().add(0.0, 0.5 * scale, 0.0));
                cell.render(viewers, real.x, real.y, real.z, yawDegrees);
            } else {
                Vec3 real = level.realWorldPositionOf(entity.position());
                cell.render(viewers, real.x, real.y, real.z, yawDegrees);
            }
        }
        cells.entrySet().removeIf(e -> {
            if (present.contains(e.getKey())) {
                return false;
            }
            e.getValue().despawnAll(viewers);
            return true;
        });
    }

    /** See class javadoc, "Furniture-owned real entities excluded". Fails open per-piece — one malformed live furniture shouldn't break the exclusion for every other one. */
    private static Set<Integer> collectFurnitureOwnedEntityIds(List<ContraptionFurniture> furniture) {
        if (furniture == null || furniture.isEmpty()) {
            return Set.of();
        }
        Set<Integer> ids = new HashSet<>();
        for (ContraptionFurniture cf : furniture) {
            if (!cf.hasLiveFurniture()) {
                continue;
            }
            try {
                BukkitFurniture live = cf.liveFurniture();
                ids.add(live.entityId());
                int[] interactable = live.interactableEntityIds();
                if (interactable != null) {
                    for (int i : interactable) {
                        ids.add(i);
                    }
                }
                int[] colliders = live.colliderEntityIds();
                if (colliders != null) {
                    for (int i : colliders) {
                        ids.add(i);
                    }
                }
            } catch (Throwable ignored) {
                // Fail-open — see method javadoc.
            }
        }
        return ids;
    }

    /** Despawn every mirror for one viewer set (e.g. contraption torn down). */
    public void despawnAll(List<Player> viewers) {
        for (Cell cell : cells.values()) {
            cell.despawnAll(viewers);
        }
        cells.clear();
    }

    public int cellCount() {
        return cells.size();
    }

    private static EntityType<?> entityTypeFor(Entity entity) {
        if (entity instanceof net.minecraft.world.entity.item.FallingBlockEntity) {
            return EntityType.BLOCK_DISPLAY; // custom render — orient with the body (see #render)
        }
        return EntityType.ITEM; // ItemEntity — a camera-facing billboard, orientation-agnostic
    }

    /**
     * Whether an entity inside the contraption can be rendered faithfully at the body's arbitrary tilt, so it
     * stays and is mirrored. True only for:
     * <ul>
     *   <li>{@link ItemEntity} / {@link net.minecraft.world.entity.ExperienceOrb} — camera-facing billboards,
     *       orientation-agnostic, so a tipped ship doesn't misrender them;</li>
     *   <li>{@link net.minecraft.world.entity.item.FallingBlockEntity} — mirrored as a BLOCK_DISPLAY that we tilt
     *       with the body (see {@link #render});</li>
     *   <li>{@link net.minecraft.world.entity.Display} entities — a real transformation matrix (full yaw+pitch+roll).</li>
     * </ul>
     * Everything else (living mobs render upright, minecarts/boats fixed, projectiles aligned to their own flight)
     * has no faithful tilted render, so it is ejected to the real world instead.
     */
    private static boolean canMirror(Entity entity) {
        return entity instanceof ItemEntity
                || entity instanceof net.minecraft.world.entity.item.FallingBlockEntity;
    }

    /** Moves an un-mirror-able entity out of the hidden contraption level into the REAL world at its live pose. */
    private static void eject(Entity entity, ContraptionLevel level) {
        try {
            if (!(level.realLevel() instanceof net.minecraft.server.level.ServerLevel realLevel)) {
                return;
            }
            Vec3 real = level.realWorldPositionOf(entity.position());
            org.bukkit.World world = realLevel.getWorld();
            org.bukkit.entity.Entity bukkit = entity.getBukkitEntity();
            org.bukkit.Location dest = new org.bukkit.Location(world, real.x, real.y, real.z,
                    bukkit.getLocation().getYaw(), bukkit.getLocation().getPitch());
            bukkit.teleport(dest);
        } catch (Throwable ignored) {
            // a teleport that can't complete this tick (chunk not ready, etc.) — retried next tick, still no mirror
        }
    }

    /** One fake mirror entity for a single real entity living inside the ContraptionLevel. */
    private static final class Cell {
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final EntityType<?> type;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        private Object nmsItemStack; // ItemEntity mirrors only
        private net.minecraft.world.level.block.state.BlockState blockState; // BLOCK_DISPLAY (falling block) mirrors only
        private float pitch, roll, scaleF = 1f; // BLOCK_DISPLAY orientation, refreshed per tick
        private boolean metaDirty = true;

        Cell(EntityType<?> type) {
            this.type = type;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        /** Sets this tick's tilt/scale for a BLOCK_DISPLAY (falling-block) mirror; dirties metadata if it changed. */
        void setOrientation(float pitch, float roll, float scale) {
            if (pitch != this.pitch || roll != this.roll || scale != this.scaleF) {
                this.pitch = pitch;
                this.roll = roll;
                this.scaleF = scale;
                this.metaDirty = true;
            }
        }

        /** Refresh whatever appearance state we mirror from the source entity (carried item stack / falling block state). */
        void updateAppearance(Entity source) {
            if (source instanceof ItemEntity itemEntity) {
                Object nms = itemEntity.getItem().copy();
                if (!itemsEqual(nms, nmsItemStack)) {
                    nmsItemStack = nms;
                    metaDirty = true;
                }
            } else if (source instanceof net.minecraft.world.entity.item.FallingBlockEntity falling) {
                net.minecraft.world.level.block.state.BlockState bs = falling.getBlockState();
                if (!bs.equals(this.blockState)) {
                    this.blockState = bs;
                    this.metaDirty = true;
                }
            }
        }

        private static boolean itemsEqual(Object a, Object b) {
            return a == b || (a != null && a.equals(b));
        }

        private List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            if (type == EntityType.ITEM && nmsItemStack != null) {
                // A real EntityType.ITEM spawn packet gets a vanilla ItemEntity client-side —
                // its SynchedEntityData only has base Entity's 8 slots + ItemEntity's own single
                // DATA_ITEM accessor (id 8). DisplayData.ItemDisplayData.ItemStack (previously
                // used here) is keyed for a completely different entity type (ITEM_DISPLAY,
                // which walks DisplayData's ~15-field chain) and lands around id 22 in
                // CraftEngine's ClassTreeIdRegistry — writing a DataValue at that id against a
                // 9-slot ItemEntity throws ArrayIndexOutOfBoundsException client-side (crashes
                // the client on any dropped item picked up by this mirror, e.g. redstone dust
                // dropped inside a captured contraption level).
                ItemEntityData.Item.addEntityData(nmsItemStack, values);
            } else if (type == EntityType.BLOCK_DISPLAY && blockState != null) {
                // Falling block rendered as an oriented block_display — SAME lever ContraptionDisplaySwarm uses:
                // yaw rides the entity BODY (in the spawn/position packet's yRot), pitch/roll ride the transform
                // LeftRotation = rotateX(pitch)·rotateZ(roll), paired with a centre-recentring Translation, and
                // uniform Scale. So the falling anvil tumbles glued to the contraption's pose.
                net.momirealms.craftengine.bukkit.entity.data.DisplayData.BlockDisplayData.BlockState
                        .addEntityData(blockState, values);
                float s = scaleF;
                if (pitch == 0f && roll == 0f) {
                    net.momirealms.craftengine.bukkit.entity.data.DisplayData.Translation.addEntityData(
                            new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
                    if (s != 1f) {
                        net.momirealms.craftengine.bukkit.entity.data.DisplayData.Scale.addEntityData(
                                new org.joml.Vector3f(s, s, s), values);
                    }
                } else {
                    org.joml.Quaternionf q = new org.joml.Quaternionf().rotateX(pitch).rotateZ(roll);
                    org.joml.Vector3f t = q.transform(new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
                    net.momirealms.craftengine.bukkit.entity.data.DisplayData.Translation.addEntityData(t, values);
                    net.momirealms.craftengine.bukkit.entity.data.DisplayData.LeftRotation.addEntityData(q, values);
                    net.momirealms.craftengine.bukkit.entity.data.DisplayData.Scale.addEntityData(
                            new org.joml.Vector3f(s, s, s), values);
                }
                net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride.addEntityData(
                        (15 << 4) | (15 << 20), values);
            }
            return values;
        }

        void spawn(Player player, double x, double y, double z, float yawDegrees) {
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, yawDegrees, type, 0, Vec3.ZERO, 0);
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
            player.sendPackets(List.of(addPacket, dataPacket), false);
        }

        void updatePosition(Player player, double x, double y, double z, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, yawDegrees, 0f, false), false);
        }

        void updateMetadata(Player player) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
        }

        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                despawn(p);
            }
            shownTo.clear();
        }

        void render(List<Player> viewers, double x, double y, double z, float yawDegrees) {
            boolean forceMeta = metaDirty;
            metaDirty = false;
            Set<UUID> current = new HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, x, y, z, yawDegrees);
                } else {
                    updatePosition(p, x, y, z, yawDegrees);
                    if (forceMeta) {
                        updateMetadata(p);
                    }
                }
            }
            shownTo.retainAll(current);
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
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
