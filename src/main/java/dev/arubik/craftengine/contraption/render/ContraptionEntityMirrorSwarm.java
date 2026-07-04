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
            UUID id = entity.getUUID();
            present.add(id);
            Cell cell = cells.computeIfAbsent(id, k -> new Cell(entityTypeFor(entity)));
            cell.updateAppearance(entity);
            Vec3 real = level.realWorldPositionOf(entity.position());
            cell.render(viewers, real.x, real.y, real.z, yawDegrees);
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
        return entity instanceof ItemEntity ? EntityType.ITEM : EntityType.ARMOR_STAND;
    }

    /** One fake mirror entity for a single real entity living inside the ContraptionLevel. */
    private static final class Cell {
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final EntityType<?> type;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        private Object nmsItemStack; // ItemEntity mirrors only
        private boolean metaDirty = true;

        Cell(EntityType<?> type) {
            this.type = type;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        /** Refresh whatever appearance state we mirror from the source entity (currently: carried item stack). */
        void updateAppearance(Entity source) {
            if (source instanceof ItemEntity itemEntity) {
                Object nms = itemEntity.getItem().copy();
                if (!itemsEqual(nms, nmsItemStack)) {
                    nmsItemStack = nms;
                    metaDirty = true;
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
