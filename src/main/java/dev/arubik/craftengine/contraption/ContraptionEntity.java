package dev.arubik.craftengine.contraption;

import java.util.List;

import dev.arubik.craftengine.contraption.render.ContraptionBlockEntityElementMirror;
import dev.arubik.craftengine.contraption.render.ContraptionDisplaySwarm;
import dev.arubik.craftengine.contraption.render.ContraptionEntityMirrorSwarm;
import dev.arubik.craftengine.contraption.render.ContraptionFurnitureSwarm;
import dev.arubik.craftengine.contraption.render.ContraptionHitboxSwarm;
import dev.arubik.craftengine.contraption.render.ContraptionItemPickupSwarm;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * Thin facade over a {@link ContraptionState} (CONTRAPTIONS.md §1 "Entity/data
 * separation") — owns the render swarm(s), looks everything else up from the state. Kept
 * separate from {@link ContraptionState} so the "brain" (block map, NBT, network graphs)
 * can outlive a facade despawn/respawn across a chunk unload/reload.
 */
public final class ContraptionEntity {

    private final ContraptionState state;
    private final ContraptionDisplaySwarm displaySwarm = new ContraptionDisplaySwarm();
    private final ContraptionHitboxSwarm hitboxSwarm = new ContraptionHitboxSwarm();
    private final ContraptionEntityMirrorSwarm entityMirrorSwarm = new ContraptionEntityMirrorSwarm();
    private final ContraptionItemPickupSwarm itemPickupSwarm = new ContraptionItemPickupSwarm();
    private final ContraptionBlockEntityElementMirror elementMirror = new ContraptionBlockEntityElementMirror();
    private final ContraptionFurnitureSwarm furnitureSwarm = new ContraptionFurnitureSwarm();
    /** 6-directional extending-piston shaft (pipe + head) render — only active for a piston bearing. */
    private final dev.arubik.craftengine.contraption.render.ContraptionPistonShaftSwarm shaftSwarm =
            new dev.arubik.craftengine.contraption.render.ContraptionPistonShaftSwarm();

    // Packet-volume optimization (2026-07-01 session — "optimizemos el envio de packets... para
    // solo actualizar lo necesario"): a stalled/idle contraption's bearing transform doesn't
    // change tick-to-tick, so re-sending every cell's ClientboundEntityPositionSyncPacket to
    // every viewer every single tick regardless is pure waste — track the last rendered
    // transform and only ask a swarm to resend position packets when it actually changed.
    // Metadata/blockstate-dirty resends (furnace lit toggling, etc.) are unaffected — those are
    // already tracked completely independently per-cell (see ContraptionDisplaySwarm.Cell).
    private double lastRenderX = Double.NaN, lastRenderY, lastRenderZ, lastRenderYaw;

    public ContraptionEntity(ContraptionState state) {
        this.state = state;
        rebuildSwarm(List.of());
    }

    /**
     * Force an immediate display-metadata resend for a captured cell, bypassing the normal
     * passive per-tick {@code BlockState} re-read/compare — see {@code ContraptionDisplaySwarm
     * #markDirty}'s own javadoc. Used by {@code ContraptionInteractionListener} right after a
     * successful interaction it dispatched into a captured block (e.g. a bucket fill), so the
     * visual updates the same tick regardless of any blockstate-identity quirk.
     */
    public void markDisplayDirty(net.minecraft.core.BlockPos local) {
        displaySwarm.markDirty(local);
    }

    public ContraptionState state() {
        return state;
    }

    /** Exposed for {@code ContraptionSeatListener} to reach the swarm's sittable seat slots. */
    public ContraptionFurnitureSwarm furnitureSwarm() {
        return furnitureSwarm;
    }

    /**
     * Call after the underlying {@link dev.arubik.craftengine.contraption.level.ContraptionLevel}
     * changes shape (blocks added/removed) — called once from the constructor (with an empty
     * viewer list — nothing to despawn-to yet) AND every tick from {@code ContraptionEngine.tickAll}
     * (see that class), so a piston/etc. moving a block live inside the level is picked up
     * automatically rather than only at initial capture time. {@code viewers} is needed so a cell
     * that genuinely stops existing (e.g. a piston head retracting) can be despawned for whoever
     * currently sees it — every swarm's {@code rebuild} otherwise REUSES existing entities as-is
     * (never despawns/recreates just because this ran again — see each swarm's own javadoc).
     */
    public void rebuildSwarm(List<Player> viewers) {
        furnitureSwarm.rebuild(state.furniture()); // doesn't depend on `level` — rebuild even under the null-tolerant test path below
        if (state.level() == null) {
            return; // null-tolerant for pure kinematics/registry unit tests — see ContraptionState's javadoc
        }
        // Re-sync the level's own tracked-cell set against its LIVE block contents FIRST — see
        // ContraptionLevel#refreshLocalPositions javadoc — so the three swarm rebuilds below
        // (which all key off level.localPositions()) actually see any block that moved in/out of
        // a tracked cell since the last rebuild (e.g. a piston push), not just what was captured.
        state.level().refreshLocalPositions();
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        displaySwarm.rebuild(state.level(), viewers);
        hitboxSwarm.rebuild(state.level(), viewers, bearing);
        elementMirror.rebuild(state.level(), viewers);
    }

    /**
     * Render every owned swarm for the given viewers at the state's current transform, with no
     * real-world light reference (test/null-tolerant path — see {@link #render(List, net.minecraft.server.level.ServerLevel)}).
     * {@link ContraptionDisplaySwarm} falls back to full-bright when {@code realLevel} is null.
     */
    public void render(List<Player> viewers) {
        render(viewers, null);
    }

    /**
     * Render every owned swarm for the given viewers at the state's current transform.
     * {@code realLevel} is the contraption's REAL {@code ServerLevel} (the actual world it's
     * currently in, resolved once per tick by {@code ContraptionEngine.tickAll}) — passed through
     * so {@link ContraptionDisplaySwarm} can read the real block/sky light at the bearing's
     * current real-world position instead of the flat full-bright override it used to hardcode
     * (2026-07-02 — "la luz del ambiente no esta afectando el contraption... el contraption
     * brilla" reported testing at night). Null is tolerated (falls back to full-bright) for the
     * pure-kinematics/registry unit test path that never has a live Bukkit world.
     */
    public void render(List<Player> viewers, net.minecraft.server.level.ServerLevel realLevel) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        boolean moved = Double.isNaN(lastRenderX) || bearing.x != lastRenderX || bearing.y != lastRenderY
                || bearing.z != lastRenderZ || yaw != lastRenderYaw;
        lastRenderX = bearing.x;
        lastRenderY = bearing.y;
        lastRenderZ = bearing.z;
        lastRenderYaw = yaw;

        displaySwarm.render(viewers, bearing, yaw, state.level(), moved, realLevel);
        hitboxSwarm.render(viewers, bearing, yaw, moved);
        // Furniture-owned real entities (meta ItemDisplay + hitbox colliders) are excluded here —
        // see ContraptionEntityMirrorSwarm's own javadoc, "Furniture-owned real entities excluded"
        // — they're already mirrored by furnitureSwarm.render below.
        entityMirrorSwarm.render(viewers, state.level(), state.furniture());
        itemPickupSwarm.tick(state.level());
        elementMirror.render(viewers, state.level(), realLevel);
        furnitureSwarm.render(viewers, bearing, yaw, realLevel, state.level());
        renderPistonShaft(viewers, realLevel, moved);
    }

    /**
     * Renders the extending-piston shaft (pipe + head) for a piston bearing, anchored at the FIXED
     * real bearing position (the body never moves) along the piston's facing at its current
     * extension. No-op for non-piston contraptions or when the shaft block ids aren't configured
     * (graceful — see {@code ContraptionPistonShaftSwarm}).
     */
    private void renderPistonShaft(List<Player> viewers, net.minecraft.server.level.ServerLevel realLevel, boolean moved) {
        dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior piston = null;
        for (MovementBehavior b : state.behaviors()) {
            if (b instanceof dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior p) {
                piston = p;
                break;
            }
        }
        if (piston == null || realLevel == null) {
            shaftSwarm.despawnAll(viewers);
            return;
        }
        double extended = piston.extendedBlocks();
        if (extended <= 1.0e-6) {
            shaftSwarm.despawnAll(viewers);
            return;
        }
        Vec3 facing = piston.direction();
        net.minecraft.core.BlockPos bearingPos = state.originBearingBlockPos();
        // Resolved directly to CraftEngine ITEM ids (2026-07-03 fix — "los entity renderer no se
        // estan renderizando en tu shaft ... solo renderizabas bloques solidos y no los que usan
        // entity"): the shaft/head appearances are entity-renderer-bound over a null block model,
        // which only becomes visible via a real ITEM_DISPLAY entity (see
        // ContraptionPistonShaftSwarm's class javadoc for the full root-cause) — no blockstate
        // roundtrip needed at all here, the item ids are already a static 1:1 mapping from facing.
        String headItemId = dev.arubik.craftengine.contraption.render.ContraptionPistonShaftSwarm.headItem();
        String shaftItemId = dev.arubik.craftengine.contraption.render.ContraptionPistonShaftSwarm.shaftItemFor(facing);
        Vec3 bearingCorner = new Vec3(bearingPos.getX(), bearingPos.getY(), bearingPos.getZ());
        shaftSwarm.render(viewers, bearingCorner, facing, extended, headItemId, shaftItemId, moved);
    }

    /**
     * Carry whichever of {@code candidates} are standing on the hitbox footprint (Phase 4).
     *
     * <p><b>Seated riders excluded (2026-07-02 session, live-test fix — "el sit ya funciona a
     * medias me tepea a donde deberia sentarme pero al microsegundo me baja y puedo
     * moverme").</b> {@code ContraptionHitboxSwarm#isStandingOnFootprint}'s own Y-window is
     * deliberately generous (-0.3 to +0.9 blocks above a slot's top face — see that method's
     * javadoc) so it tolerates normal walking/stepping; a player SEATED in a furniture seat
     * placed directly on/above the contraption's footprint sits well inside that same window,
     * so before this fix {@link #carryRiders} kept independently re-classifying an already-
     * seated rider as a STANDING rider too, every tick, on top of {@link #carrySeatedRiders}'s
     * own handling of the exact same player. Two competing systems then fought over the same
     * tick: this method's {@code hitboxSwarm.carryRiders} applied its own axis-aligned
     * footprint-delta {@code PlayerCarry.carry} call (a velocity/knockback-style packet, per
     * {@code PlayerCarry}'s own class javadoc), then {@link #carrySeatedRiders} applied ITS OWN
     * seat-delta {@code PlayerCarry.carry} call plus a hard corrective teleport — the standing
     * path's velocity packet, sent moments earlier in the same tick, briefly asserted a
     * different (footprint-derived, not seat-derived) velocity as the client's new authoritative
     * motion before the seat teleport corrected position back, which is exactly what a
     * "teleports me correctly, then a microsecond later drops/releases me" symptom looks like
     * client-side — a real but momentary velocity assertion the seat lock has to fight/override
     * every single tick, rather than the player simply never having a competing signal sent to
     * them at all. Fixed by never handing an already-seated player to the standing-rider carry
     * candidate list in the first place — {@link #carrySeatedRiders} is already the sole,
     * correct owner of a seated player's position/velocity every tick (see its own javadoc,
     * "Position-lock, not velocity-nudge").
     */
    public void carryRiders(List<ServerPlayer> candidates) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        java.util.Map<java.util.UUID, Vec3> seated = state.seatedRiders();
        List<ServerPlayer> standingOnly = seated.isEmpty() ? candidates
                : candidates.stream().filter(sp -> !seated.containsKey(sp.getUUID())).toList();
        hitboxSwarm.carryRiders(standingOnly, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(),
                state.level(), state.yawRadians(), state.lastYawDelta());
    }

    /**
     * Non-player counterpart to {@link #carryRiders} (2026-07-02 session — "otras entidades que
     * no sean jugador no se mantienen sobre la contraption y la atraviesan": mobs, dropped items,
     * boats, etc. standing on top of a moving contraption used to just get left behind/fall
     * through instead of riding along). Gathers real, nearby non-player entities itself (scoped to
     * a small AABB around the contraption's current real-world footprint — see
     * {@code ContraptionHitboxSwarm#carryNearbyEntities} javadoc), excluding real players (handled
     * separately by {@link #carryRiders}, which needs {@code PlayerCarry}'s client-prediction-aware
     * nudging instead of a raw teleport) and {@code ContraptionItemPickupSwarm}'s own real-world
     * mirror {@code ItemEntity}s (already independently position-synced every tick by that swarm).
     */
    public void carryEntities(net.minecraft.world.level.Level realLevel) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        hitboxSwarm.carryNearbyEntities(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians());
    }

    /**
     * Bystander wall-pushback (2026-07-02 session — walking INTO an approaching contraption wall
     * still clips through it): see {@code ContraptionHitboxSwarm#pushBackNearbyBystanders}'s own
     * javadoc for the full root-cause/design writeup. Separate from {@link #carryRiders} — that
     * method only validates players already standing on top; this one catches anyone merely in
     * the structure's path.
     */
    public void pushBackBystanders(net.minecraft.world.level.Level realLevel) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        hitboxSwarm.pushBackNearbyBystanders(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians());
    }

    /**
     * Solid collision for NON-player entities (2026-07-03 — "entidades que no son players ... las
     * atraviesan"): mobs/animals/items run server-side physics against the empty real world (the
     * contraption's blocks live in a hidden level, its colliders are packet-only client-side for
     * players), so nothing stops them phasing through the structure. This pushes them out
     * server-authoritatively — see {@code ContraptionHitboxSwarm#pushBackNearbyEntities}.
     */
    public void pushBackEntities(net.minecraft.world.level.Level realLevel) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        hitboxSwarm.pushBackNearbyEntities(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians());
    }

    /**
     * Task 1 (CONTRAPTIONS.md 2026-07-01 session — furniture seats): carries every real player
     * recorded in {@code state.seatedRiders()} at their FIXED bearing-relative local offset,
     * fully yaw-rotation-aware (unlike {@link #carryRiders}'s axis-aligned footprint math) — see
     * {@code ContraptionFurnitureCapture#captureNear}'s javadoc for how a rider gets registered.
     *
     * <p><b>Real vehicle-mounting, not position-lock (2026-07-02 session, seat-only rework — user
     * correction: "es para el sistema de seat de los furniture que estan dentro de el
     * contraption").</b> The earlier version of this method hard-teleported the PLAYER directly,
     * every tick, plus zeroed their vertical velocity and called {@code PlayerCarry.carry} for its
     * anti-fly-kick side effect. That's replaced entirely: each seated rider is now a genuine
     * vanilla passenger of a real, invisible {@code ArmorStand} mount entity (see
     * {@link ContraptionSeatMount}) spawned when they sat down. This method's ONLY remaining job
     * is to reposition that MOUNT entity to the seat's current real-world position/yaw every tick
     * — vanilla's own passenger-follows-vehicle mechanics (a real entity's {@code positionRider}
     * call, run unconditionally every tick for every vehicle with passengers) does the rest,
     * moving the player automatically with zero risk of drift/desync and no competing signal for
     * {@link #carryRiders}'s standing-rider velocity nudges to fight (a mounted passenger is
     * excluded from that candidate list by construction — see {@link #carryRiders}'s own javadoc).
     * No more double-carry conflicts, no more velocity-packet fighting, no more possible desync
     * between "where we teleported the player" and "where the client thinks they are": the mount
     * entity IS the seat's real position, and the player rides it exactly the way a boat/horse
     * passenger does.
     */
    public void carrySeatedRiders() {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        for (java.util.Map.Entry<java.util.UUID, Vec3> e : state.seatedRiders().entrySet()) {
            java.util.UUID id = e.getKey();
            java.util.UUID mountId = state.seatedRiderMount(id);
            org.bukkit.entity.Entity mount = ContraptionSeatMount.resolve(mountId);
            if (mount == null) {
                continue; // mount despawned/world unloaded — stays registered, nothing to reposition this tick
            }
            Vec3 target = dev.arubik.craftengine.contraption.ContraptionMath.renderPosition(e.getValue(), bearing, yaw);
            float seatYaw = 0f;
            for (ContraptionFurnitureSwarm.SeatSlot slot : furnitureSwarm.seatSlots()) {
                if (id.equals(slot.occupant())) {
                    seatYaw = slot.currentYawDegrees(yaw);
                    break;
                }
            }
            ContraptionSeatMount.reposition(mount, target, seatYaw);
            rotateSeatedRiderView(id, seatYaw);
        }
        // Drop stale per-rider yaw bookkeeping for anyone no longer seated.
        seatFacing.keySet().retainAll(state.seatedRiders().keySet());
        seatRiderAppliedYaw.keySet().retainAll(state.seatedRiders().keySet());
    }

    /** Per-seated-rider tracking for the "rotate the player's own view with the contraption" behavior. */
    private final java.util.Map<java.util.UUID, Float> seatFacing = new java.util.HashMap<>();
    private final java.util.Map<java.util.UUID, Float> seatRiderAppliedYaw = new java.util.HashMap<>();

    /**
     * Rotates a seated rider's camera by however much the SEAT's facing changed this tick — but
     * only while the rider isn't actively turning their own view (2026-07-03 — "si el jugador no
     * esta moviendo su yaw ... lo rote por el"). {@code seatYaw} is the seat's current world facing
     * (degrees); the per-tick change in it is exactly the contraption's rotation the rider should
     * be carried around by. If the rider's real yaw drifted from the value we last left them at,
     * they're looking around themselves — skip the nudge that tick (don't fight their input) and
     * just re-baseline. See {@link ContraptionSeatMount#rotateRiderView} for the passenger-safe
     * relative-teleport used to apply it.
     */
    private void rotateSeatedRiderView(java.util.UUID id, float seatYaw) {
        Float prevSeat = seatFacing.put(id, seatYaw);
        if (prevSeat == null) {
            return; // first tick seated — establish baseline, nothing to rotate yet
        }
        float seatDelta = org.bukkit.Location.normalizeYaw(seatYaw - prevSeat);
        if (Math.abs(seatDelta) < 1.0e-3f) {
            return; // contraption didn't rotate this tick
        }
        org.bukkit.entity.Player bp = org.bukkit.Bukkit.getPlayer(id);
        if (bp == null) {
            return;
        }
        float curYaw = bp.getLocation().getYaw();
        Float applied = seatRiderAppliedYaw.get(id);
        boolean turningSelf = applied != null
                && Math.abs(org.bukkit.Location.normalizeYaw(curYaw - applied)) > 0.75f;
        if (turningSelf) {
            seatRiderAppliedYaw.put(id, curYaw); // rider is looking around — don't force, just re-baseline
            return;
        }
        ContraptionSeatMount.rotateRiderView(bp, seatDelta);
        seatRiderAppliedYaw.put(id, org.bukkit.Location.normalizeYaw(curYaw + seatDelta));
    }

    /**
     * Full teardown despawn — despawns EVERY swarm including the hitbox/shulker collider swarm.
     * Kept for callers that don't care about the fall-through-the-floor ordering bug (e.g. a
     * contraption vanishing with no real-block restore following it at all). {@link
     * dev.arubik.craftengine.contraption.ContraptionAssembler#disassemble} and {@link
     * dev.arubik.craftengine.contraption.HologramTest#stop} do NOT use this directly anymore —
     * see {@link #despawnRest} + {@link #despawnHitboxesOnly}'s javadocs for why the hitbox
     * despawn needs to happen strictly AFTER the real blocks are restored into the world.
     */
    public void despawn(List<Player> viewers) {
        despawnRest(viewers);
        despawnHitboxesOnly(viewers);
    }

    /**
     * Everything {@link #despawn} does EXCEPT the hitbox/shulker-collider swarm (see
     * {@link #despawnHitboxesOnly}) — split out (CONTRAPTIONS.md — "no despawnees los shulker
     * hitbox hasta que los bloques esten 100% puestos") so a real-block-restoring teardown
     * (disassemble/HologramTest#stop) can run this part FIRST (as before), then actually write
     * the blocks back into the world, and only THEN despawn the hitboxes — otherwise there's a
     * window where the packet-spawned INTERACTION/SHULKER hitbox entities a standing player's
     * OWN CLIENT was locally colliding against are gone, but the real blocks aren't in the world
     * client-side yet either, so the player falls/clips through with nothing solid under them.
     */
    public void despawnRest(List<Player> viewers) {
        displaySwarm.despawnAll(viewers);
        entityMirrorSwarm.despawnAll(viewers);
        itemPickupSwarm.despawnAll();
        elementMirror.despawnAll(viewers);
        furnitureSwarm.despawnAll(viewers);
        shaftSwarm.despawnAll(viewers);
        // Seated riders (real vehicle-mounting, 2026-07-02 rework — see ContraptionSeatMount):
        // a contraption vanishing out from under a seated rider must release the real passenger
        // relationship and remove the mount entity same as an explicit sneak-dismount, otherwise
        // the player is left mounted on an entity about to be orphaned/removed with no landing
        // position ever computed. ContraptionSeatListener.dismount does the actual
        // unmount+teleport (shared logic) — this just drives it for every still-seated rider.
        for (java.util.UUID id : state.seatedRiders().keySet()) {
            ContraptionSeatListener.dismount(state, id);
        }
    }

    /**
     * Just the hitbox/shulker-collider swarm despawn — see {@link #despawnRest}'s javadoc for
     * why a real-block-restoring teardown must call this SEPARATELY, and strictly AFTER the
     * real blocks are already back in the world.
     */
    public void despawnHitboxesOnly(List<Player> viewers) {
        hitboxSwarm.despawnAll(viewers);
    }

    /**
     * Snapshot of real players the hitbox swarm currently considers "standing on this
     * contraption's footprint" (CONTRAPTIONS.md — the despawn-ordering fall-through fix): used
     * right after real blocks are restored back into the world to nudge each one's Y up by a
     * tiny safety epsilon before/while the (now-delayed) hitbox despawn runs, so they don't end
     * up embedded exactly at the old hitbox-top / new block-top boundary. Delegates to {@link
     * ContraptionHitboxSwarm#currentRiderIds()} rather than duplicating its footprint math here.
     */
    public java.util.Set<java.util.UUID> currentRiderIds() {
        return hitboxSwarm.currentRiderIds();
    }

    public int cellCount() {
        return displaySwarm.cellCount();
    }

    public int hitboxCellCount() {
        return hitboxSwarm.cellCount();
    }

    public int mirroredEntityCount() {
        return entityMirrorSwarm.cellCount();
    }

    public int pickupMirrorCount() {
        return itemPickupSwarm.mirrorCount();
    }
}
