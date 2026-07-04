package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.BaseEntityData;
import net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData;
import net.momirealms.craftengine.core.entity.player.Player;

import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Packet-only {@code SHULKER}-based hitbox layer, ADDITIONAL to (not a replacement for)
 * {@link ContraptionHitboxSwarm}'s {@code INTERACTION} slots — added per the 2026-07-02 session
 * request to mirror how real CraftEngine furniture positions its own shulker collider at an
 * arbitrary FRACTIONAL local offset (not grid-snapped to a block's 0,0,0 corner), which reads
 * better for a contraption cell that's mid-move/mid-rotation (see class javadoc motivation:
 * "util cuando un coso esta que se mueve").
 *
 * <p><b>What decompiling CraftEngine's own furniture collider revealed</b> (see
 * {@code net.momirealms.craftengine.bukkit.entity.furniture.hitbox.ShulkerFurnitureHitbox} /
 * {@code ShulkerFurnitureHitboxConfig} in {@code testserver/plugins/craft-engine.jar}): the
 * shulker CraftEngine spawns for a furniture piece is <b>packet-only</b> — its {@code show}/
 * {@code hide} methods only ever call {@code Player#sendPacket} with a bundled
 * {@code ClientboundAddEntityPacket} (one {@code ITEM_DISPLAY} anchor + one {@code SHULKER})
 * plus a {@code ClientboundSetEntityDataPacket}; it is never added to the real server
 * {@code Level}. It has NO real server-side collision physics of its own — the shulker you see
 * is purely cosmetic/visual, exactly like every other swarm in this package.
 *
 * <p>The REAL pushback/standing-collision physics real CraftEngine furniture gets comes from a
 * completely separate object: {@code AbstractFurnitureHitBox#createCollider} builds a
 * {@code BukkitCollider}, which (per {@code ColliderType}) spawns a genuine NMS
 * {@code Interaction} (or {@code Boat}) entity via {@code FastNMS.createCollisionInteraction}/
 * {@code createCollisionBoat} — a REAL entity actually added to the level, separate from the
 * visual shulker packets. Mirroring that exactly (a second, real, server-spawned collision
 * entity) is a materially bigger change than this task's ask; consistent with this project's
 * established packet-only convention (see {@link ContraptionHitboxSwarm}'s own javadoc, which
 * made the identical call for its INTERACTION slots), this class stays packet-only too — the
 * shulker here is the same kind of real-BLOCK-SHAPED visual box CraftEngine shows, and
 * "collision" is still this class's own manual standing-check math (see
 * {@link #isStandingOnFootprint}), just against a shulker-shaped 1×1×1 box (scaled via the
 * {@code Scale} attribute, matching {@code ShulkerFurnitureHitboxConfig}'s own scale handling)
 * instead of Interaction's continuous width/height metadata.
 *
 * <p>Positioning: like {@code ShulkerFurnitureHitboxConfig}, a {@link Slot} stores a continuous
 * bearing-local offset ({@code lx,ly,lz}, NOT snapped to a block's integer corner) plus its own
 * {@code scale} — callers push arbitrary fractional offsets via {@link #addSlot}, there is no
 * auto-derivation from the captured level here (unlike {@code ContraptionHitboxSwarm}'s
 * grid-based {@code autoSlots}) since this class exists specifically for the
 * non-grid-snapped case.
 */
public final class ContraptionShulkerColliderSwarm {

    private final List<Slot> slots = new ArrayList<>();
    private final Set<UUID> currentRiders = new HashSet<>();

    /**
     * Registers a fractional bearing-local offset slot. {@code scale} follows vanilla's own
     * shulker {@code Scale} attribute convention (1.0 = a normal full-block 1×1×1 box);
     * {@code key} identifies this slot across rebuilds so an unchanged slot REUSES its existing
     * entity id / {@code shownTo} tracking instead of despawning+respawning (same diff-based
     * reuse pattern {@code ContraptionHitboxSwarm#rebuild}'s javadoc documents) — pass a stable
     * caller-owned key (e.g. the furniture cell's own id, or a fixed slot index).
     *
     * <p>Equivalent to {@link #addSlot(Object, double, double, double, float, net.minecraft.core.Direction, int)}
     * with {@code attachFace=DOWN, peek=0} (the previous always-full-cube behavior) — kept for
     * callers that don't need shape-aware sizing.
     */
    public void addSlot(Object key, double lx, double ly, double lz, float scale) {
        addSlot(key, lx, ly, lz, scale, net.minecraft.core.Direction.DOWN, 0);
    }

    /**
     * Full-parameter slot registration (2026-07-02 session — "genera shulker hitbox dependiendo
     * del bounding box ... puedes usar peek para ahorrar shulker box's"). {@code attachFace} is
     * vanilla's {@code ShulkerData.AttachFace} — the box grows AWAY from this face (see this
     * class's own decompile writeup, below, for the exact math); {@code peek} is the raw
     * {@code ShulkerData.RawPeekAmount} byte, 0-100.
     *
     * <p><b>Decompiled peek-to-collision relationship</b> (verified via {@code javap} against
     * this project's mapped {@code net.minecraft.world.entity.monster.Shulker} — NOT guessed):
     * {@code Shulker#makeBoundingBox} computes {@code physicalPeek = getPhysicalPeek(currentPeekAmount)}
     * where {@code currentPeekAmount} tracks {@code getRawPeekAmount()/100f} once its
     * open/close animation settles, then calls
     * {@code getProgressAabb(scale, attachFace.getOpposite(), physicalPeek, entityPos)} ->
     * {@code getProgressDeltaAabb(scale, direction, -1.0f, physicalPeek, pos)}. That method
     * builds the base box {@code [-scale/2, 0, -scale/2]}-{@code [scale/2, scale, scale/2]}
     * (a bottom-anchored {@code scale}×{@code scale}×{@code scale} cube — "bottom" meaning the
     * ATTACH face, since {@code direction = attachFace.getOpposite()}), then
     * {@code expandTowards(direction * max(-1,physicalPeek) * scale)} and
     * {@code contract(direction * -(1+min(-1,physicalPeek)) * scale)}. Since
     * {@code physicalPeek ∈ [0,1]} (never negative once settled), {@code max(-1,physicalPeek)=physicalPeek}
     * and {@code min(-1,physicalPeek)=-1} so the contract term is always {@code 1+(-1)=0} (no-op).
     * Net effect: peek EXPANDS the box outward along {@code attachFace.getOpposite()} (i.e. AWAY
     * from the attach face) by {@code physicalPeek*scale}, where {@code physicalPeek=
     * 0.5 - sin((0.5+peek/100)*PI)*0.5} ({@code getPhysicalPeek}, decompiled) — at
     * {@code peek=0}, {@code physicalPeek=0} (no growth, exactly the classic 1×1×1 box); at
     * {@code peek=100} ({@code peek/100=1.0}), {@code physicalPeek=0.5-sin(1.5*PI)*0.5=0.5-(-0.5)=1.0},
     * i.e. a FULL extra {@code scale} of length added on top of the base cube — a
     * {@code peek=100} shulker attached DOWN (growing UP, opposite of DOWN) with {@code scale=1}
     * spans a full 2 blocks of height, confirming peek can cover an extra whole block per 100
     * units with NO second entity. The near (attach) face never contracts, so the box's base
     * stays anchored exactly at the attach point regardless of peek — this is what lets
     * {@link ContraptionHitboxSwarm#rebuild} stack a 2-tall column under ONE shulker (attachFace
     * DOWN at the bottom cell's local offset, peek=100) instead of two separate scale-1 shulkers.
     */
    public void addSlot(Object key, double lx, double ly, double lz, float scale,
            net.minecraft.core.Direction attachFace, int peek) {
        for (Slot existing : slots) {
            if (existing.key.equals(key)) {
                existing.updateOffset(lx, ly, lz, scale, attachFace, peek);
                return;
            }
        }
        slots.add(new Slot(key, lx, ly, lz, scale, attachFace, peek));
    }

    /** Drops any slot whose key is not in {@code liveKeys}, despawning it for every viewer first. */
    public void prune(Set<Object> liveKeys, List<Player> viewers) {
        java.util.Iterator<Slot> it = slots.iterator();
        while (it.hasNext()) {
            Slot s = it.next();
            if (!liveKeys.contains(s.key)) {
                for (Player p : viewers) {
                    s.despawn(p);
                }
                it.remove();
            }
        }
    }

    public void clear(List<Player> viewers) {
        for (Slot s : slots) {
            for (Player p : viewers) {
                s.despawn(p);
            }
        }
        slots.clear();
        currentRiders.clear();
    }

    /**
     * Render every slot at the bearing's current world-space position, rotated around the
     * bearing by {@code yawRadians} (2026-07-02 session — see {@code ContraptionHitboxSwarm
     * #render}'s javadoc for the full "rotate doesn't affect hitboxes" bug this fixes). {@code
     * moved} — see {@code ContraptionEntity#render} — skips resending position-sync packets when
     * the contraption's transform hasn't changed since the last call.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        for (Slot slot : slots) {
            Vec3 pos = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(
                    new Vec3(slot.lx, slot.ly, slot.lz), bearingWorldPos, yawRadians);
            slot.render(viewers, pos.x, pos.y, pos.z, moved);
        }
    }

    public int slotCount() {
        return slots.size();
    }

    /**
     * Manual standing-detection against each slot's shulker-shaped box (scaled 1×1×1), the same
     * packet-only "collision" approach {@link ContraptionHitboxSwarm#carryRiders} uses — real
     * per-tick carrying is left to the caller (this class only reports who's standing where; no
     * caller wires it into {@code PlayerCarry} yet — this layer is additive/opt-in per
     * {@link #addSlot}, unlike {@code ContraptionHitboxSwarm}'s always-on auto top-cell slots).
     */
    public boolean isStandingOnFootprint(Vec3 playerPos, Vec3 bearingWorldPos) {
        for (Slot slot : slots) {
            double half = slot.scale / 2.0;
            double minX = bearingWorldPos.x + slot.lx - half;
            double maxX = bearingWorldPos.x + slot.lx + half;
            double minZ = bearingWorldPos.z + slot.lz - half;
            double maxZ = bearingWorldPos.z + slot.lz + half;
            double topY = bearingWorldPos.y + slot.ly + slot.scale + slot.peekGrowth();
            if (playerPos.x >= minX - 0.3 && playerPos.x <= maxX + 0.3
                    && playerPos.z >= minZ - 0.3 && playerPos.z <= maxZ + 0.3
                    && playerPos.y >= topY - 0.3 && playerPos.y <= topY + 0.9) {
                return true;
            }
        }
        return false;
    }

    /**
     * One packet-only fake shulker collider: an {@code ITEM_DISPLAY} anchor (unused visually
     * here — vanilla shulker itself has no separate anchor requirement the way CraftEngine's
     * variant-swap rendering does, so this is a single {@code SHULKER} entity, simpler than
     * {@code ShulkerFurnitureHitbox}'s 2-entity spawn) at a continuous bearing-local offset,
     * scaled via the {@code Scale} attribute (vanilla 1.20.5+; ignored pre-1.20.5, matching
     * {@code ShulkerFurnitureHitboxConfig}'s own version-gated scale handling).
     */
    private static final class Slot {
        final Object key;
        double lx, ly, lz;
        float scale;
        net.minecraft.core.Direction attachFace;
        int peek;
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        /**
         * Set by {@link #updateOffset} when {@code scale} changes since the last spawn/resend;
         * cleared once {@link #render} has pushed the updated {@code Attributes.SCALE} packet to
         * every current viewer. See {@link #render}'s javadoc for why the Scale attribute needs
         * its own explicit resend path, unlike entity-data/position which already go out every
         * tick regardless.
         */
        private volatile boolean scaleDirty = false;

        Slot(Object key, double lx, double ly, double lz, float scale,
                net.minecraft.core.Direction attachFace, int peek) {
            this.key = key;
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        void updateOffset(double lx, double ly, double lz, float scale,
                net.minecraft.core.Direction attachFace, int peek) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            if (this.scale != scale) {
                // A cell whose captured block CHANGES shape across a rebuild (e.g. a stair swapped
                // for a slab) needs the Scale attribute RESENT to everyone already tracking this
                // slot — see #render's javadoc for why this can't just happen unconditionally every
                // tick (the attributes packet is comparatively heavy/rare, unlike the entity-data/
                // position-sync traffic that already goes out every tick regardless).
                scaleDirty = true;
            }
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
        }

        /**
         * The real-world-scale extent this slot's box grows by along {@link #attachFace}'s
         * opposite direction, from peek alone (see this class's {@code addSlot} javadoc for the
         * decompiled derivation) — {@code physicalPeek * scale}, where {@code physicalPeek =
         * 0.5 - sin((0.5 + peek/100) * PI) * 0.5}. Used by
         * {@link ContraptionHitboxSwarm}/{@link #isStandingOnFootprint} callers that need this
         * slot's REAL grown extent, not just its base {@code scale}.
         */
        double peekGrowth() {
            float peekFrac = peek / 100f;
            double physicalPeek = 0.5 - Math.sin((0.5 + peekFrac) * Math.PI) * 0.5;
            return physicalPeek * scale;
        }

        private List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            // Invisible shared-flag bit — a real vanilla shulker's block-shaped collision SHAPE
            // is a purely client-side rendering fact of the entity type; hiding the model still
            // leaves the manual standing-check math (this class's own) as the actual mechanism,
            // same convention as ContraptionHitboxSwarm's invisible INTERACTION cells. Visible
            // here would work too (useful for debugging) — kept invisible to match production
            // use, matching CraftEngine's own peek/attach-face-driven shulker which IS visible
            // by design (it's the furniture's literal model) but ours is a bare collider, not a
            // rendered furniture piece, so invisible is the right default.
            BaseEntityData.SharedFlags.addEntityData((byte) 0x20, values);
            ShulkerData.AttachFace.addEntityData(attachFace, values);
            ShulkerData.RawPeekAmount.addEntityData((byte) peek, values);
            ShulkerData.Color.addEntityData((byte) 16, values); // 16 = no color / default
            return values;
        }

        void spawn(Player player, double x, double y, double z) {
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, 0f, EntityType.SHULKER, 0, Vec3.ZERO, 0);
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
            // Scale attribute (2026-07-02 session — see field-level javadoc on #scaleDirty / MNms
            // #constructor$ClientboundScaleAttributePacket): without this the client-visible/
            // -collidable shulker box is ALWAYS a full 1x1x1 regardless of `scale`, since
            // Attributes.SCALE is synced via its own dedicated attributes packet, not via the
            // entity-data packet above (which only carries AttachFace/RawPeekAmount/Color, none of
            // which affect the box's actual size) — this was the root cause of "slabs/stairs render
            // a full shulker instead of their real bounding box."
            Object scalePacket = MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(entityId, scale);
            player.sendPackets(List.of(addPacket, dataPacket, scalePacket), false);
        }

        void updatePosition(Player player, double x, double y, double z) {
            player.sendPacket(MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
        }

        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }

        void render(List<Player> viewers, double x, double y, double z, boolean moved) {
            Set<UUID> current = new HashSet<>();
            // Scale-change resend (see #scaleDirty's javadoc / #updateOffset): captured once per
            // render call so every viewer already tracking this slot gets the fresh attribute, not
            // just newly-spawned-this-call ones — cleared at the end of the loop, not per-viewer,
            // so a viewer added THIS SAME call (spawn() below, which already sends the current
            // scale) doesn't ALSO get a redundant second attributes packet.
            boolean resendScale = scaleDirty;
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                if (shownTo.add(id)) {
                    spawn(p, x, y, z);
                } else {
                    if (moved) {
                        updatePosition(p, x, y, z);
                    }
                    if (resendScale) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(entityId, scale), false);
                    }
                }
            }
            if (resendScale) {
                scaleDirty = false;
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
