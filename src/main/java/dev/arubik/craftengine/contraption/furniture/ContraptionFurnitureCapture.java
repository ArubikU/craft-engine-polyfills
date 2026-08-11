package dev.arubik.craftengine.contraption.furniture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBox;
import net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitboxPart;
import net.momirealms.craftengine.core.world.WorldPosition;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;

/**
 * Detects real CraftEngine furniture sitting within a glued footprint at assembly time and
 * captures it as a real contraption CELL — a genuinely LIVE {@code BukkitFurniture} re-placed
 * inside the contraption's own hidden {@link ContraptionLevel}, the same "the real object lives
 * in a stable local address space, only a packet-only mirror gets repositioned in the real
 * world" treatment blocks already get (see {@link ContraptionLevel}'s own javadoc, "Real vs.
 * fake positions"; see {@link ContraptionFurniture}'s javadoc for the full architecture writeup
 * and why the two EARLIER approaches — "reposition the real furniture every tick via
 * {@code Furniture#moveTo}" and "destroy it, rebuild purely from captured config" — were both
 * abandoned).
 */
public final class ContraptionFurnitureCapture {

    private ContraptionFurnitureCapture() {
    }

    /**
     * Task 1 (CONTRAPTIONS.md 2026-07-01 session — furniture seats): {@code seatedRiders} maps
     * a real player's UUID to the bearing-relative local offset (yaw-0 basis, same convention
     * as {@link ContraptionFurniture#localOffset()}) of the seat they were sitting in AT
     * CAPTURE TIME. See {@link #captureNear}'s javadoc for the full design writeup.
     *
     * <p>{@code seatedRiderMounts} (2026-07-02 session, real vehicle-mounting rework) pairs each
     * such rider with the REAL mount entity's UUID this capture spawned to keep them a genuine
     * vanilla passenger across the hand-off from CraftEngine's own real seat entity (destroyed
     * during capture) to this contraption's own tracking — see {@link #captureNear}'s "Seats"
     * javadoc.
     */
    public record Result(List<ContraptionFurniture> furniture, Map<UUID, Vec3> seatedRiders,
            Map<UUID, UUID> seatedRiderMounts) {
    }

    /**
     * Scans real entities within (and one block around) {@code worldPositions}' bounding box
     * for CraftEngine furniture, and for each one: places a genuinely LIVE {@code BukkitFurniture}
     * of the same definition/variant/facing inside {@code fakeLevel} (the contraption's own
     * hidden {@code ContraptionLevel}) at the local position corresponding to
     * {@code bearingWorldPos}-relative offset, THEN destroys the original real-world furniture
     * (no drops/sound — it's being absorbed into the contraption, not broken). Called once, at
     * assembly time, before the bearing has ever moved (yaw 0) — see {@link ContraptionFurniture}'s
     * javadoc for why that means no inverse-rotation is needed here.
     *
     * <p><b>Seats (Task 1, this session).</b> A CraftEngine furniture seat is a real, separate
     * marker entity a real player vehicle-mounts (real {@code Entity#getPassengers()}
     * relationship) — detected via {@code CraftEngineFurniture.isSeat(Entity)}/
     * {@code getLoadedFurnitureBySeat(Entity)} (the same public API class already used above
     * for the furniture itself). Design choice, see this class's OWN javadoc above for why the
     * live furniture object is destroyed rather than followed: the identical reasoning applies
     * to a live seat entity — continuously repositioning/rotating a real vehicle entity every
     * tick to track a spinning bearing would hit the same "not built for continuous relocation"
     * problem {@code Furniture#moveTo} already has, so a captured seat is NOT kept as a real
     * followed entity either.
     *
     * <p>Instead, this reuses {@link dev.arubik.craftengine.contraption.player.PlayerCarry} — the
     * SAME mechanism already proven for players standing on a moving contraption's hitbox
     * footprint (Phase 4) — for whichever real player is ACTUALLY sitting at capture time: the
     * seat entity's real-world position becomes a fixed bearing-relative local offset (recorded
     * in the returned {@link Result#seatedRiders()}), and every subsequent tick
     * {@code ContraptionEntity#carrySeatedRiders} feeds that player the delta between last
     * tick's and this tick's {@code ContraptionMath#renderPosition} of that fixed offset — full
     * yaw-rotation-aware, unlike the axis-aligned hitbox footprint (Phase 4 explicitly left yaw
     * out of THAT math; a seat's fixed local offset needs it, since a chair 3 blocks off-axis
     * from a spinning bearing sweeps a real arc). The real seat entity itself is destroyed along
     * with the furniture (added to the same destroy pass) — this naturally dismounts the real
     * passenger relationship; {@code PlayerCarry} takes over that same tick.
     *
     * <p><b>Post-assembly sit/stand — RESOLVED</b> by {@code ContraptionSeatListener}:
     * {@link Result#seatedRiders()} is still only populated ONCE here, at capture time (a rider
     * caught mid-sit at assembly), but {@code ContraptionSeatListener} now grows/shrinks
     * {@code ContraptionState#seatedRiders()} afterward too — right-click a free
     * {@code render.ContraptionFurnitureSwarm.SeatSlot} to sit down post-assembly, sneak to
     * explicitly stand back up. See that listener's javadoc for the full design (same
     * bearing-relative fixed-offset carrying mechanism, no real vehicle mount, consistent with
     * this class's own reasoning above for why a live seat entity is never kept/followed).
     */
    public static Result captureNear(Level level, Set<BlockPos> worldPositions, BlockPos bearingWorldPos,
            ContraptionLevel fakeLevel) {
        List<ContraptionFurniture> out = new ArrayList<>();
        Map<UUID, Vec3> seatedRiders = new HashMap<>();
        Map<UUID, UUID> seatedRiderMounts = new HashMap<>();
        if (worldPositions.isEmpty()) {
            return new Result(out, seatedRiders, seatedRiderMounts);
        }
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : worldPositions) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        // Inflated by 2 (not 1): a furniture's meta/anchor entity position (what's checked below)
        // can sit a block or more away from the actual cell a player perceives it as "on"/"inside"
        // — e.g. a multi-hitbox furniture piece whose CONFIGURED hitbox offset extends past its
        // own anchor tile, or a wall/ceiling-anchored piece whose anchor is offset toward the
        // attaching face. 1 block of padding was tight enough to miss those; 2 keeps the false-
        // positive risk low (still just the structure's own footprint plus a small margin) while
        // comfortably covering the "furniture visually on top of/inside a glued block's cell but
        // its anchor entity isn't exactly centered in that cell" case this method exists to catch.
        AABB bounds = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1).inflate(2.0);
        List<Entity> nearby;
        try {
            nearby = level.getEntities((Entity) null, bounds, e -> true);
        } catch (Throwable t) {
            org.bukkit.Bukkit.getLogger().warning(
                    "[Contraption] furniture capture: getEntities(bounds=" + bounds + ") threw: " + t);
            return new Result(out, seatedRiders, seatedRiderMounts);
        }
        List<org.bukkit.entity.Entity> toDestroy = new ArrayList<>();
        List<org.bukkit.entity.Entity> seatsToRemove = new ArrayList<>();
        List<Object> capturedFurnitureObjects = new ArrayList<>(); // parallel to `out` — for seat->furniture matching below
        for (Entity nmsEntity : nearby) {
            org.bukkit.entity.Entity bukkit = nmsEntity.getBukkitEntity();
            if (bukkit == null) {
                continue;
            }
            // isFurniture(bukkit) is only true for the META entity (the real ItemDisplay CraftEngine
            // tags with FURNITURE_KEY — confirmed against CraftEngine's own BukkitFurnitureManager:
            // colliders/seats never carry that tag). getEntities() also hands us those OTHER real
            // entities a furniture piece owns (its INTERACTION collider(s), seat marker(s)) — those
            // correctly fail isFurniture() and fall through here without being mistaken for the
            // furniture itself.
            if (!CraftEngineFurniture.isFurniture(bukkit)) {
                continue;
            }
            BukkitFurniture furniture = CraftEngineFurniture.getLoadedFurnitureByMetaEntity(bukkit);
            if (furniture == null) {
                // Tagged as furniture but not (yet) registered in CraftEngine's own live map — a
                // real desync (e.g. this meta entity's id was never (re)registered after a chunk
                // reload) that silently dropped this piece from every previous capture with zero
                // trace. Log it instead of swallowing it so a repro is actually diagnosable.
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: entity id="
                        + bukkit.getEntityId() + " at " + bukkit.getLocation()
                        + " is tagged as CraftEngine furniture but getLoadedFurnitureByMetaEntity returned null"
                        + " — not captured into the contraption.");
                continue;
            }
            try {
                if (!belongsToStructure(furniture, worldPositions)) {
                    // Real geometry check (hitbox-part world AABBs vs the structure's actual
                    // block cells) says this piece's footprint doesn't actually touch any glued
                    // block — it just happened to fall inside the generous discovery AABB above
                    // (e.g. furniture on a neighboring, non-glued block). Leave it in the world.
                    continue;
                }
                WorldPosition pos = furniture.position();
                Vec3 localOffset = new Vec3(pos.x() - bearingWorldPos.getX(),
                        pos.y() - bearingWorldPos.getY(), pos.z() - bearingWorldPos.getZ());
                String variantName = furniture.getCurrentVariant().name();
                net.momirealms.craftengine.core.util.Key definitionId = furniture.config().id();

                // Place a genuinely LIVE furniture instance inside the hidden ContraptionLevel at
                // this same local offset (local offsets map 1:1 onto that level's own block
                // positions — see ContraptionLevel's own javadoc, "Real vs. fake positions") —
                // see ContraptionFurniture's javadoc for the full architecture writeup on why this
                // replaces the earlier "destroy it, rebuild from static config" approach.
                BukkitFurniture liveFurniture = placeInFakeLevel(fakeLevel, definitionId, variantName, localOffset, pos.yRot());

                out.add(new ContraptionFurniture(definitionId, variantName, localOffset, pos.yRot(), liveFurniture));
                // Persist metadata onto the level too (2026-07-03 — furniture-survives-restart): the
                // live object above lives in the never-saved ContraptionLevel; this record is what
                // rides along in the structure NBT so rehydrate can re-place it.
                if (fakeLevel != null) {
                    fakeLevel.addFurnitureRecord(new ContraptionLevel.FurnitureRecord(
                            definitionId.toString(), variantName, localOffset.x, localOffset.y, localOffset.z, pos.yRot()));
                }
                capturedFurnitureObjects.add(furniture);
                toDestroy.add(bukkit);
            } catch (Throwable t) {
                // Malformed/edge-case furniture: skip it rather than aborting the whole capture,
                // but log WHY — this used to fail completely silently, making "furniture doesn't
                // glue to the contraption" unreportable/undiagnosable in the field.
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: entity id="
                        + bukkit.getEntityId() + " at " + bukkit.getLocation()
                        + " matched CraftEngine furniture but capture threw: " + t);
            }
        }

        // Task 1 (this session) — seats: a second pass over the SAME `nearby` scan for real
        // seat marker entities (CraftEngineFurniture.isSeat/getLoadedFurnitureBySeat — same
        // public API class used above). Only seats belonging to a furniture piece we actually
        // captured above are relevant; a seat entity's own position becomes a fixed
        // bearing-relative local offset, and any real player currently riding it is recorded
        // into `seatedRiders`. See this method's own javadoc for the full design writeup.
        for (Entity nmsEntity : nearby) {
            org.bukkit.entity.Entity bukkit = nmsEntity.getBukkitEntity();
            if (bukkit == null) {
                continue;
            }
            try {
                if (!CraftEngineFurniture.isSeat(bukkit)) {
                    continue;
                }
                Object owningFurniture = CraftEngineFurniture.getLoadedFurnitureBySeat(bukkit);
                if (owningFurniture == null || !capturedFurnitureObjects.contains(owningFurniture)) {
                    continue; // seat belongs to furniture outside this glued structure — leave it alone
                }
                org.bukkit.Location seatLoc = bukkit.getLocation();
                Vec3 seatLocalOffset = new Vec3(seatLoc.getX() - bearingWorldPos.getX(),
                        seatLoc.getY() - bearingWorldPos.getY(), seatLoc.getZ() - bearingWorldPos.getZ());
                // Real vehicle-mounting (2026-07-02 session, seat-only rework): CraftEngine's own
                // real seat entity (the one `bukkit` refers to here) is about to be destroyed
                // below along with the furniture — spawn OUR OWN mount entity at the exact same
                // real position/yaw first and hand the player off onto it, so they stay a genuine
                // vanilla passenger throughout the capture instead of being silently dismounted
                // (see ContraptionSeatMount's javadoc for the exact setup this mirrors).
                Vec3 seatRealPos = new Vec3(seatLoc.getX(), seatLoc.getY(), seatLoc.getZ());
                for (org.bukkit.entity.Entity passenger : bukkit.getPassengers()) {
                    if (passenger instanceof org.bukkit.entity.Player player) {
                        org.bukkit.entity.ArmorStand mount = ContraptionSeatMount.mount(
                                player, bukkit.getWorld(), seatRealPos, seatLoc.getYaw());
                        if (mount != null) {
                            seatedRiders.put(player.getUniqueId(), seatLocalOffset);
                            seatedRiderMounts.put(player.getUniqueId(), mount.getUniqueId());
                        }
                    }
                }
                seatsToRemove.add(bukkit); // the REAL-WORLD seat marker entity — a fresh one is owned by the live furniture placed inside the fake ContraptionLevel instead
            } catch (Throwable ignored) {
                // One malformed seat shouldn't abort the whole capture — same fail-open shape
                // as the furniture loop above.
            }
        }

        // Destroy AFTER both scan loops (not inside them) so removing one furniture/seat entity
        // never perturbs the `nearby` list we're still iterating. Seats are plain Bukkit
        // entities (not furniture themselves) — removed directly rather than through
        // CraftEngineFurniture.remove, which is furniture-specific.
        //
        // Per-entity try/catch (bug fix, this session — "el mueble original nunca se
        // despawnea/elimina cuando se captura"): every OTHER operation in this class already
        // fails open per-entity so one bad piece can't take out the whole capture; this loop
        // used to be the one exception — a single CraftEngineFurniture.remove throwing (e.g. a
        // misbehaving custom FurnitureController#preRemove/postRemove hook) aborted the ENTIRE
        // remaining loop, silently leaving every furniture piece destroyed AFTER the one that
        // threw still fully real/visible in the world — even though its config was already
        // committed to `out` and is now ALSO being rendered as a packet-mirror by
        // ContraptionFurnitureSwarm. That's exactly the "original + mirror both visible"
        // symptom: capture (config) and destroy (real-world removal) must both succeed
        // independently per piece, not be allowed to partially fail together.
        for (org.bukkit.entity.Entity bukkit : toDestroy) {
            try {
                boolean removed = CraftEngineFurniture.remove(bukkit, false, false);
                if (!removed) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: entity id="
                            + bukkit.getEntityId() + " at " + bukkit.getLocation()
                            + " was captured but CraftEngineFurniture.remove returned false (not"
                            + " recognised as furniture anymore) — the real entity may still be visible"
                            + " alongside its packet-mirror.");
                }
            } catch (Throwable t) {
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: entity id="
                        + bukkit.getEntityId() + " at " + bukkit.getLocation()
                        + " was captured but CraftEngineFurniture.remove threw — the real entity is"
                        + " still visible alongside its packet-mirror: " + t);
            }
        }
        for (org.bukkit.entity.Entity seat : seatsToRemove) {
            try {
                seat.remove();
            } catch (Throwable ignored) {
            }
        }
        if (!out.isEmpty() || !seatedRiders.isEmpty()) {
            org.bukkit.Bukkit.getLogger().info("[Contraption] furniture capture: captured " + out.size()
                    + " furniture piece(s), " + seatedRiders.size() + " seated rider(s), from "
                    + nearby.size() + " nearby entities scanned.");
        }
        return new Result(out, seatedRiders, seatedRiderMounts);
    }

    /**
     * Re-places live CraftEngine furniture inside a freshly-loaded {@link ContraptionLevel} from
     * the persisted {@link ContraptionLevel.FurnitureRecord}s (2026-07-03 session — "todos los
     * craft engine furnitures se pierden al reiniciar el sv"). Called on rehydrate (e.g.
     * {@code MinecartBearing#rehydrate}) after the blocks + records have been loaded from the
     * structure NBT: for each record it places a genuinely LIVE {@code BukkitFurniture} back inside
     * the hidden level at its captured local offset (exactly like {@link #captureNear} did
     * originally), and returns the {@link ContraptionFurniture} list to hand to
     * {@code ContraptionState#setFurniture} so both the packet mirror
     * ({@code ContraptionFurnitureSwarm}) and disassemble restore work identically to a
     * freshly-assembled contraption. Yaw is captured at yaw-0 basis, so no inverse-rotation is
     * needed (same reasoning as {@link ContraptionFurniture}'s javadoc).
     */
    public static List<ContraptionFurniture> restoreIntoFakeLevel(ContraptionLevel fakeLevel,
            List<ContraptionLevel.FurnitureRecord> records) {
        List<ContraptionFurniture> out = new ArrayList<>();
        if (fakeLevel == null || records == null || records.isEmpty()) {
            return out;
        }
        for (ContraptionLevel.FurnitureRecord r : records) {
            try {
                net.momirealms.craftengine.core.util.Key definitionId = net.momirealms.craftengine.core.util.Key.of(r.definitionId());
                Vec3 localOffset = new Vec3(r.lx(), r.ly(), r.lz());
                BukkitFurniture live = placeInFakeLevel(fakeLevel, definitionId, r.variantName(), localOffset, r.yaw());
                out.add(new ContraptionFurniture(definitionId, r.variantName(), localOffset, r.yaw(), live));
            } catch (Throwable t) {
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture rehydrate: " + r.definitionId()
                        + " variant=" + r.variantName() + " failed to re-place: " + t);
            }
        }
        return out;
    }

    /**
     * Precise real-geometry membership test — replaces "furniture anchor entity happens to be
     * within a fixed-radius guess of the structure's bounding box" with "does this furniture's
     * ACTUAL hitbox footprint (the same real, precise, world-space {@code AABB}s CraftEngine
     * itself builds for collision/interaction — {@code Furniture#hitboxes()} ->
     * {@code FurnitureHitBox#parts()} -> {@code FurnitureHitboxPart#aabb()}, already resolved to
     * absolute world coordinates via {@code Furniture#getRelativePosition} at hitbox-construction
     * time, confirmed by decompiling {@code InteractionFurnitureHitbox}'s constructor) overlap
     * any of the structure's own glued block cells" — exact set/geometry membership, not a
     * hardcoded-inflate distance guess.
     *
     * <p>CraftEngine's public API (searched exhaustively — every class under
     * {@code net.momirealms.craftengine.(bukkit.)?(api|core.entity.furniture)} in the jar via
     * {@code javap}) has NO block-position-keyed furniture lookup (no
     * {@code getFurniture(BlockPos)}/{@code furnitureAt(Location)} — {@code CraftEngineFurniture}
     * only offers entity-id-keyed lookups (`getLoadedFurnitureByMetaEntity/BySeat/ByCollider`)
     * and a raytrace-from-player API, both wrong shapes for "give me the furniture anchored to
     * THIS block"), and no furniture-level "anchor block" accessor distinct from the raw
     * meta-entity {@code position()} (only {@code FurniturePersistentData#anchorType()}, which is
     * just the GROUND/WALL/CEILING attachment MODE enum, not a spatial offset). So candidate
     * discovery still has to be the real-entity AABB scan above (the only way to even find
     * furniture without a block-keyed API) — but THIS method is what actually decides
     * acceptance, using real per-part geometry instead of trusting the discovery scan's radius.
     *
     * <p>Falls back to a bare "is the anchor position's block (or one of its 6 neighbors) in the
     * structure" check if a piece has no hitbox parts at all (e.g. a purely decorative/no-hitbox
     * variant) — still exact block-set membership, never a distance/radius guess.
     */
    private static boolean belongsToStructure(BukkitFurniture furniture, Set<BlockPos> worldPositions) {
        List<FurnitureHitBox> hitboxes = furniture.hitboxes();
        boolean checkedAnyPart = false;
        if (hitboxes != null) {
            for (FurnitureHitBox hitBox : hitboxes) {
                List<FurnitureHitboxPart> parts = hitBox.parts();
                if (parts == null) {
                    continue;
                }
                for (FurnitureHitboxPart part : parts) {
                    checkedAnyPart = true;
                    if (aabbOverlapsAnyBlock(part.aabb(), worldPositions)) {
                        return true;
                    }
                }
            }
        }
        if (checkedAnyPart) {
            // Had real hitbox geometry to check and none of it touched the structure.
            return false;
        }
        // No hitbox parts (no-collision decorative furniture) — fall back to exact block-set
        // membership of the anchor position's own block and its 6 face-neighbors (still a set
        // lookup, not a distance guess; covers wall/ceiling anchors offset toward the block
        // they're actually mounted against).
        WorldPosition pos = furniture.position();
        BlockPos anchorBlock = BlockPos.containing(pos.x(), pos.y(), pos.z());
        if (worldPositions.contains(anchorBlock)) {
            return true;
        }
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (worldPositions.contains(anchorBlock.relative(dir))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places a real, live {@code BukkitFurniture} inside the contraption's hidden
     * {@link ContraptionLevel} at local position {@code localOffset} (rounded to the level's own
     * block-position address space — furniture placement needs a concrete {@code Location}, and
     * local offsets map 1:1 onto that level's block positions per its own javadoc). Ensures the
     * target chunk is force-loaded/ticking first ({@link ContraptionLevel#ensureChunkReady}) —
     * furniture doesn't go through {@link ContraptionLevel#putBlock}, so nothing else would have
     * already done this for a position that has no captured block of its own (e.g. a wall-mounted
     * piece's anchor cell, or a floor piece sitting one Y above its supporting block's cell).
     * Returns {@code null} (logged) if placement fails — capture still proceeds for every OTHER
     * furniture piece; a single bad definition/variant shouldn't abort the whole capture.
     */
    private static BukkitFurniture placeInFakeLevel(ContraptionLevel fakeLevel,
            net.momirealms.craftengine.core.util.Key definitionId, String variantName, Vec3 localOffset, float yaw) {
        if (fakeLevel == null) {
            return null; // null-tolerant test path — see ContraptionState's own javadoc
        }
        try {
            BlockPos local = BlockPos.containing(localOffset.x, localOffset.y, localOffset.z);
            fakeLevel.ensureChunkReady(local);
            org.bukkit.Location loc = new org.bukkit.Location(fakeLevel.getWorld(),
                    localOffset.x, localOffset.y, localOffset.z, yaw, 0f);
            BukkitFurniture placed = CraftEngineFurniture.place(loc, definitionId, variantName, true);
            if (placed == null) {
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: " + definitionId
                        + " variant=" + variantName + " failed to place inside the hidden ContraptionLevel at "
                        + loc + " — this piece will have no live backing instance (mirror falls back to static config).");
            }
            return placed;
        } catch (Throwable t) {
            org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture capture: " + definitionId
                    + " threw while placing inside the hidden ContraptionLevel: " + t);
            return null;
        }
    }

    /**
     * Reverse of {@link #captureNear}: destroys each captured piece's LIVE instance inside the
     * hidden {@link ContraptionLevel} and re-spawns a REAL CraftEngine furniture entity in the
     * real world at its restored position — bug fix (this session — "al disassemble debe
     * despawnearse el mirror animado y volver a spawnear el mueble real normal"). Without this, a
     * captured furniture piece just vanished forever on disassemble: {@code
     * ContraptionEntity#despawnRest} already despawns the packet-mirror ({@code
     * ContraptionFurnitureSwarm#despawnAll}), but nothing ever put a real furniture back, unlike
     * blocks (which {@code ContraptionCapture#restoreRotated} genuinely re-places). The fake-level
     * instance MUST be destroyed here too (not left for {@code ContraptionLevel#dispose} to
     * silently discard) — {@code dispose()} tears down the whole hidden dimension with no vanilla
     * save/state-change event, so a CraftEngine-tracked furniture left alive in it would orphan
     * forever in {@code BukkitFurnitureManager}'s own internal maps (a real resource/registration
     * leak, not just a visual one).
     *
     * <p>Call this AFTER the real blocks have already been restored (same ordering
     * {@code ContraptionAssembler#disassemble}/{@code HologramTest#stop} already use for blocks)
     * so a wall/ceiling/floor-anchored furniture variant has its supporting block back in the
     * world first. {@code snappedBearingWorldPos}/{@code quarterTurns} MUST be the exact same
     * grid-snapped bearing position and 90-degree rotation count already computed for
     * {@code ContraptionCapture#restoreRotated} at this same disassembly — see that method's own
     * javadoc; reusing the identical values keeps the furniture seated cleanly on the SAME
     * block-aligned grid the blocks themselves just landed on, instead of at the raw continuous
     * bearing transform (which can leave it visually embedded between blocks or at a diagonal
     * offset — CONTRAPTIONS.md "para que no se queden entre bloques").
     *
     * <p>Rotation: each cell's captured {@code localOffset} is rotated by {@code quarterTurns}
     * 90-degree steps around the bearing's local origin using the EXACT same integer
     * quarter-turn convention {@code ContraptionCapture#rotateLocal} already applies to block
     * offsets (rotate the local X/Z, keep Y), then added to the already-snapped bearing block
     * position — so the offset itself is snapped to the block grid the same way a block's own
     * local offset is, not left as a raw fractional vector. {@code yawOffsetDegrees} gets
     * {@code quarterTurns * 90} added the same way {@code BlockState#rotate} rotates a block's
     * own facing, so the restored furniture faces the correct cardinal direction.
     *
     * <p>Uses {@code CraftEngineFurniture#place(Location, Key, String, boolean)} — the same
     * public placement API a normal {@code /ce} furniture give-and-place goes through (decompiled
     * this session: {@code BukkitFurnitureManager#place} resolves the definition, builds a fresh
     * {@code FurniturePersistentData}, and spawns a brand-new real meta {@code ItemDisplay} +
     * colliders — a completely independent, correctly-registered live {@code Furniture}, not
     * reusing anything from the destroyed original). The trailing {@code boolean} is CraftEngine's
     * own "force" placement-validity flag (bypasses its own placement-legality checks, e.g. anchor
     * support) — passed {@code true} since the exact spot a furniture piece originally occupied is
     * by definition valid (it was real before capture) and the supporting block was just restored
     * moments before this runs.
     */
    public static void restoreFurniture(org.bukkit.World bukkitWorld, List<ContraptionFurniture> furniture,
            BlockPos snappedBearingWorldPos, int quarterTurns) {
        if (furniture == null || furniture.isEmpty()) {
            return;
        }
        for (ContraptionFurniture cf : furniture) {
            // Destroy the LIVE fake-level instance FIRST — see this method's own javadoc for why
            // this can't just be left for ContraptionLevel#dispose to silently discard (orphans
            // CraftEngine's own tracking maps, a real leak, not just visual).
            if (cf.hasLiveFurniture()) {
                try {
                    CraftEngineFurniture.remove(cf.liveFurniture().getBukkitEntity(), false, false);
                } catch (Throwable t) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture restore: " + cf.definitionId()
                            + " threw while removing its live fake-level instance: " + t);
                }
            }
            try {
                Vec3 local = cf.localOffset();
                Vec3 rotatedLocal = rotateLocalQuarterTurns(local, quarterTurns);
                double worldX = snappedBearingWorldPos.getX() + rotatedLocal.x;
                double worldY = snappedBearingWorldPos.getY() + rotatedLocal.y;
                double worldZ = snappedBearingWorldPos.getZ() + rotatedLocal.z;
                float yaw = cf.yawOffsetDegrees() + quarterTurns * 90f;
                org.bukkit.Location loc = new org.bukkit.Location(bukkitWorld, worldX, worldY, worldZ, yaw, 0f);
                BukkitFurniture placed = CraftEngineFurniture.place(loc, cf.definitionId(), cf.variantName(), true);
                if (placed == null) {
                    org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture restore: "
                            + cf.definitionId() + " variant=" + cf.variantName() + " at " + loc
                            + " failed to place (definition/variant no longer valid?) — this piece"
                            + " is lost on disassemble.");
                }
            } catch (Throwable t) {
                org.bukkit.Bukkit.getLogger().warning("[Contraption] furniture restore: " + cf.definitionId()
                        + " threw during CraftEngineFurniture.place: " + t);
            }
        }
    }

    /**
     * Same integer 90-degree-step rotation {@code ContraptionCapture#rotateLocal} applies to a
     * block's integer {@code BlockPos} offset, but kept in continuous {@code Vec3} space (a
     * furniture's captured local offset is fractional, not grid-snapped) — X/Z rotate, Y is
     * untouched. Kept as its OWN small helper (rather than reusing
     * {@code ContraptionMath.rotateYaw}, which takes a continuous radian angle) so the exact
     * same "rotate 90 degrees clockwise, quarterTurns times" integer-step convention blocks
     * already use for disassembly is mirrored bit-for-bit, not approximated via trig — trig
     * rotation of a non-axis-aligned offset by "quarterTurns * 90 degrees in radians" would drift
     * from the block grid's exact integer rotation due to floating-point error, defeating the
     * whole point of this method (keep furniture snapped to the SAME grid the blocks land on).
     */
    private static Vec3 rotateLocalQuarterTurns(Vec3 local, int quarterTurns) {
        double x = local.x;
        double z = local.z;
        for (int i = 0; i < ((quarterTurns % 4) + 4) % 4; i++) {
            double newX = -z;
            double newZ = x;
            x = newX;
            z = newZ;
        }
        return new Vec3(x, local.y, z);
    }

    /** Does {@code aabb} (CraftEngine's own real-world-space hitbox AABB) overlap any block's unit cube in {@code worldPositions}? */
    private static boolean aabbOverlapsAnyBlock(net.momirealms.craftengine.core.world.collision.AABB aabb,
            Set<BlockPos> worldPositions) {
        if (aabb == null) {
            return false;
        }
        for (BlockPos p : worldPositions) {
            boolean overlapsX = aabb.maxX > p.getX() && aabb.minX < p.getX() + 1;
            boolean overlapsY = aabb.maxY > p.getY() && aabb.minY < p.getY() + 1;
            boolean overlapsZ = aabb.maxZ > p.getZ() && aabb.minZ < p.getZ() + 1;
            if (overlapsX && overlapsY && overlapsZ) {
                return true;
            }
        }
        return false;
    }
}
