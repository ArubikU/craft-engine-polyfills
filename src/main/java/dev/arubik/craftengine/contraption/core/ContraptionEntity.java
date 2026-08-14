package dev.arubik.craftengine.contraption.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.craftbukkit.CraftServer;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.furniture.ContraptionSeatMount;
import dev.arubik.craftengine.contraption.listener.ContraptionProjectileCollision;
import dev.arubik.craftengine.contraption.listener.ContraptionVoidDrop;
import dev.arubik.craftengine.contraption.player.CePlayers;
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
    private final ContraptionItemPickupSwarm itemPickupSwarm = new ContraptionItemPickupSwarm();
    /** 6-directional extending-piston shaft (pipe + head) render — only active for a piston bearing. */
    // shaftSwarm removed — piston shaft handled by ContraptionPistonShaftElement

    // Packet-volume optimization (2026-07-01 session — "optimizemos el envio de packets... para
    // solo actualizar lo necesario"): a stalled/idle contraption's bearing transform doesn't
    // change tick-to-tick, so re-sending every cell's ClientboundEntityPositionSyncPacket to
    // every viewer every single tick regardless is pure waste — track the last rendered
    // transform and only ask a swarm to resend position packets when it actually changed.
    // Metadata/blockstate-dirty resends (furnace lit toggling, etc.) are unaffected — those are
    // already tracked completely independently per-cell (see ContraptionDisplaySwarm.Cell).
    private double lastRenderX = Double.NaN, lastRenderY, lastRenderZ, lastRenderYaw, lastRenderPitch, lastRenderRoll;
    /** Last uniform SCALE rendered (roadmap item #9 — per-contraption {@code scale}); a change marks the swarm "moved" so every cell's position/size packet is resent. Starts at {@code 1.0} (un-scaled). */
    private double lastRenderScale = 1.0;
    /** Players currently receiving element (visual display) packets — used to despawn when culling removes them. */
    private final Set<UUID> elementViewerIds = ConcurrentHashMap.newKeySet();

    public ContraptionEntity(ContraptionState state) {
        this.state = state;
        rebuildSwarm(List.of());
    }

    /**
     * <b>Cross-world teleport (roadmap item #1 — see {@code .migration/ROADMAP-world-boundary.md}
     * §1).</b> Atomically moves this whole contraption — hidden mini-dimension and all — to project
     * into a DIFFERENT real world at {@code (x, y, z, yawRadians)}, without recreating the hidden
     * {@link dev.arubik.craftengine.contraption.level.ContraptionLevel} (only its projection pointer
     * and the state's anchor move). The primary caller is
     * {@link dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior} when its real anchor
     * minecart crosses a nether/end portal and vanilla relocates the cart into a new world; it is also
     * a clean public entry point for admin/scripted moves.
     *
     * <p><b>Ordering (all synchronous, main-thread — the {@code ContraptionAccessor} discipline).</b>
     * <ol>
     *   <li><b>Despawn the OLD world's real-world satellites.</b> Resolved against the world
     *       {@code state.worldId()} STILL points at (captured before the re-anchor). {@link #despawn}
     *       sends destroy packets for the render/element/furniture/piston-shaft/item-pickup mirror
     *       swarms ({@link #despawnRest}) AND the hitbox/shulker-collider swarm
     *       ({@link #despawnHitboxesOnly}) to those viewers, so nothing lingers for players left behind
     *       in the old world. {@code despawnRest} additionally dismounts every seated rider (releasing
     *       their real {@link ContraptionSeatMount} ArmorStand) — a seated rider is therefore DROPPED
     *       by a teleport rather than carried across the boundary (documented limitation: a real
     *       vanilla passenger cannot follow the mini-dimension through a portal; re-seating post-cross
     *       is deferred).</li>
     *   <li><b>Re-anchor level + state</b> to the destination world/transform via
     *       {@link dev.arubik.craftengine.contraption.level.ContraptionLevel#reanchor} +
     *       {@link ContraptionState#setAnchor} (both with the SAME transform).</li>
     *   <li><b>Force a full re-render next tick</b> via {@link #markMoved} (nulls the render-transform
     *       cache), so {@code ContraptionEngine.tickAll} — which already re-resolves the world from
     *       {@code state.worldId()} each tick and calls {@link #rebuildSwarm}/{@link #render} — respawns
     *       every swarm for the DESTINATION world's viewers with no further work here.</li>
     * </ol>
     *
     * <p><b>Back-index untouched.</b> {@link dev.arubik.craftengine.contraption.ContraptionWorlds}'s
     * reverse map is keyed by the {@link dev.arubik.craftengine.contraption.level.ContraptionLevel}
     * instance (its {@code ContraptionBoundary} identity), which does NOT change on a re-anchor — only
     * the world it projects into does — so no index maintenance is needed here.
     *
     * <p><b>Not touched:</b> {@code world} must be a real {@code CraftWorld}-backed
     * {@link net.minecraft.server.level.ServerLevel}; no-op on the null-tolerant unit-test path where
     * {@code state.level()} is {@code null}.
     */
    public void teleport(org.bukkit.World world, double x, double y, double z, double yawRadians) {
        if (world == null) {
            return;
        }
        // 1) Despawn the OLD world's satellites for whoever currently sees them (state.worldId() still
        //    points at the old world at this point — capture its viewers BEFORE the re-anchor below).
        org.bukkit.World oldWorld = null;
        try {
            MinecraftServer server = ((CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(state.worldId());
            oldWorld = level != null ? level.getWorld() : null;
        } catch (Throwable ignored) {
            // pure-JVM unit-test path (no live server) — nothing to despawn
        }
        if (oldWorld != null) {
            despawn(CePlayers.resolve(oldWorld.getPlayers()));
        }
        // 2) Re-anchor the hidden level's projection pointer + the state's world/transform (same pose).
        if (state.level() != null) {
            net.minecraft.server.level.ServerLevel newHandle =
                    ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
            state.level().reanchor(newHandle, x, y, z, yawRadians);
        }
        state.setAnchor(((org.bukkit.craftbukkit.CraftWorld) world).getHandle().dimension(), x, y, z, yawRadians);
        // 3) Force a full re-render into the destination world on the next tick.
        markMoved();
    }

    /**
     * Invalidates the cached "last rendered transform" (see {@link #lastRenderX}) so the very next
     * {@link #render} treats the contraption as moved and re-sends every cell's position packet to all
     * current viewers — used after a {@link #teleport} re-anchor so the destination world's viewers get
     * a full resend even if the numeric transform happens to match the last one rendered in the old
     * world. Cheap and idempotent.
     */
    public void markMoved() {
        lastRenderX = Double.NaN;
    }

    /**
     * Whether this contraption's swarms are currently suspended — see {@link #suspendRender}.
     * Owned by {@code ContraptionEngine.tickAll}, which is the only thing allowed to flip it.
     */
    private boolean renderSuspended;

    public boolean renderSuspended() {
        return renderSuspended;
    }

    /**
     * <b>Stops rendering this contraption entirely, giving every client back its entities first.</b>
     * Used by {@code ContraptionEngine.tickAll} to drop a contraption nobody can currently see (its real
     * position's chunk is unloaded, or its world has no players) out of the per-tick render cost.
     *
     * <p><b>Why a despawn is mandatory, not merely tidy.</b> Every swarm tracks who it has spawned for in
     * a per-cell {@code shownTo} set and ends its render with {@code shownTo.retainAll(currentViewers)} —
     * which FORGETS a viewer without sending them a despawn packet. So merely skipping {@code render} and
     * resuming later is not symmetric: the swarm still believes each client has the entities, and a client
     * that dropped them in the meantime (relog, world change, an unload that discarded them) would never be
     * sent the spawn packet again on resume, leaving the contraption permanently invisible-but-solid for
     * them. Despawning here clears every {@code shownTo}, so {@link #render} rebuilds the client state from
     * scratch when the contraption resumes — correct for whoever is actually watching by then, not for
     * whoever was watching when it went quiet.
     *
     * <p><b>Deliberately not {@link #despawnRest}</b>, despite covering the same swarms: that method also
     * dismounts every seated rider (correct when a contraption is being torn down for good, wrong here — a
     * suspend is temporary and must not eject a passenger just because their chunk briefly unloaded). The
     * mini-dimension, the state, the behaviors and the seat registrations all survive untouched; only the
     * client-side projection goes.
     *
     * <p><b>{@link ContraptionItemPickupSwarm} is deliberately left running/untouched.</b> Its
     * {@code despawnAll} is not a despawn at all — it RELEASES each mirror into the world as a permanent,
     * gravity-driven real item, which is right when the contraption is being destroyed (the internal source
     * item dies with the level, so the mirror is the only surviving copy) and wrong here: the source item
     * survives a suspend, so releasing the mirror would drop a real duplicate into the world and then mint a
     * second mirror for the same item on resume. Its mirrors are real entities the server already tracks
     * (nothing to strand on a client) and its {@code tick} re-syncs them by UUID, so simply not ticking it
     * while suspended is both free and correct.
     *
     * <p>{@code viewers} must be the world's REAL players, for the same reason every other despawn path
     * needs them (see {@link dev.arubik.craftengine.contraption.player.CePlayers}) — an empty list clears the
     * records while sending nothing. Passing an empty list is only correct when the world genuinely has no
     * players, in which case there is nobody to strand.
     */
    public void suspendRender(List<Player> viewers) {
        hitboxElement().despawnAll(viewers);
        // seatElement seats migrated to ContraptionSeatElement — despawn handled by element loop
        // piston shaft despawn handled by element loop
        for (dev.arubik.craftengine.contraption.element.ContraptionElement e : state.elements()) {
            e.despawn(viewers);
        }
        elementViewerIds.clear();
        renderSuspended = true;
        markMoved();
    }

    /**
     * Clears the {@link #suspendRender} flag. The swarms need no priming: their {@code rebuild} repopulates
     * the cell set from the level and {@code render} re-spawns for every current viewer, because the
     * suspend already emptied each {@code shownTo}.
     */
    public void resumeRender() {
        renderSuspended = false;
        markMoved();
    }

    /**
     * Force an immediate display-metadata resend for a captured cell, bypassing the normal
     * passive per-tick {@code BlockState} re-read/compare — see {@code ContraptionDisplaySwarm
     * #markDirty}'s own javadoc. Used by {@code ContraptionInteractionListener} right after a
     * successful interaction it dispatched into a captured block (e.g. a bucket fill), so the
     * visual updates the same tick regardless of any blockstate-identity quirk.
     */
    public void markDisplayDirty(net.minecraft.core.BlockPos local) {
        dev.arubik.craftengine.contraption.element.ContraptionElement el = state.elementByLocalPos(local);
        if (el instanceof dev.arubik.craftengine.contraption.element.ContraptionBlockElement blockEl) {
            blockEl.markDirty();
        }
    }

    public ContraptionState state() {
        return state;
    }

    /**
     * <b>Public setter path for a live contraption's uniform SCALE</b> (roadmap item #9 — the "creative
     * phys wand" entry point). Clamps + applies via {@link ContraptionState#setScale} (which pushes the full
     * transform onto the hidden level so the real-world projection resizes in lock-step), then invalidates
     * the render-transform cache via {@link #markMoved} so the very next {@link #render} treats the
     * contraption as moved and resends every cell's scaled position/size packet to all current viewers. Safe
     * to call every tick (idempotent when the value is unchanged — {@code render}'s own {@code moved}
     * gate/each swarm's per-cell dirty tracking suppress redundant packets). Main-thread only.
     */
    public void setScale(double scale) {
        state.setScale(scale);
        markMoved();
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
        // Block seats migrated to ContraptionSeatElement — rebuild handled by element system
        if (state.level() == null) {
            return; // null-tolerant for pure kinematics/registry unit tests — see ContraptionState's javadoc
        }
        state.level().refreshLocalPositions();
        // Bake internal emitter light map (O(emitters×cells) once, not per-element per-tick).
        state.lightMap().bake(state.level());
        // Build element list FIRST so hitboxElement() can be found below.
        dev.arubik.craftengine.contraption.element.ElementBuilder.rebuild(state, viewers);
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        var hb = hitboxElement();
        if (hb != null) hb.rebuild(state.level(), viewers, bearing);
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
        double pitch = state.pitchRadians();
        double roll = state.rollRadians();
        double scale = state.scale();
        boolean moved = Double.isNaN(lastRenderX) || bearing.x != lastRenderX || bearing.y != lastRenderY
                || bearing.z != lastRenderZ || yaw != lastRenderYaw || pitch != lastRenderPitch
                || roll != lastRenderRoll || scale != lastRenderScale;
        lastRenderX = bearing.x;
        lastRenderY = bearing.y;
        lastRenderZ = bearing.z;
        lastRenderYaw = yaw;
        lastRenderPitch = pitch;
        lastRenderRoll = roll;
        lastRenderScale = scale;

        // displaySwarm removed — block rendering now handled by ContraptionBlockElement via renderElements()
        // Pitch+roll+scale threaded so the interaction/shulker colliders orbit to the SAME cell positions the
        // block_display renders (canonical renderPosition mapping) — previously the hitbox swarm ignored
        // pitch/roll, so a tipping/leaning contraption's colliders stayed flat while the visual tilted.
        // A ridden anchor is driven by its RIDER'S client, which collides it against these solid
        // packet colliders — so the rider must not receive them or the anchor grinds against its own
        // structure. See ContraptionShulkerColliderSwarm#setExcludedViewer.
        hitboxElement().setColliderExcludedViewer(anchorRiderId());
        hitboxElement().render(viewers, bearing, yaw, pitch, roll, scale, moved);
        // Furniture-owned real entities (meta ItemDisplay + hitbox colliders) are excluded here —
        // Live entity mirroring (items, falling blocks) now handled by ContraptionLiveEntityMirrorElement
        // in the element render loop above.
        // Eject anything that has fallen into the contraption's void back into the real world (drops its items
        // at its last visible position) — see ContraptionVoidDrop. Main thread here, with the real level.
        if (realLevel != null) {
            ContraptionVoidDrop.handle(state.level(), realLevel);
            // Stick real-world projectiles onto this contraption on impact (they stay real-world entities; we only
            // compute the hit and pin them to the moving hull) — see ContraptionProjectileCollision.
            ContraptionProjectileCollision.tick(state, state.level(), realLevel);
        }
        itemPickupSwarm.tick(state.level());
        // Scale threaded into BOTH mirrors (2026-07-16 fix, roadmap item #9): a scaled contraption's
        // CraftEngine entity-renderer block visuals and its captured furniture used to stay at size 1
        // (and, for furniture, at unscaled POSITIONS too — it projects via ContraptionMath directly,
        // whereas the element mirror goes through ContraptionLevel#realWorldPositionOf, which already
        // carried the level's scale). See each swarm's own "Uniform scale" render javadoc.
        // Pitch+roll threaded (2026-07-16 follow-up to the same-day scale fix): captured furniture used to
        // project through a yaw+scale-only transform, so a TIPPING/LEANING contraption tilted its blocks
        // while its sofas/lamps stayed level inside the rolled hull — and its furniture colliders with them.
        // See ContraptionFurnitureSwarm#render's "Pitch/roll" javadoc.
        // seatElement().render removed — furniture rendering now handled by ContraptionFurnitureElement via renderElements()
        renderPistonShaft(viewers, realLevel, moved);
        renderElements(viewers, bearing, yaw, pitch, roll, scale, moved, realLevel);
    }

    private void renderElements(List<Player> viewers, Vec3 bearing, double yaw, double pitch,
                                double roll, double scale, boolean moved, ServerLevel realLevel) {
        List<dev.arubik.craftengine.contraption.element.ContraptionElement> elements = state.elements();
        if (elements.isEmpty()) return;

        // --- Entity Culling ---
        dev.arubik.craftengine.contraption.config.ContraptionConfig cfg =
                dev.arubik.craftengine.contraption.config.ContraptionConfig.get();
        List<Player> culledViewers;
        if (cfg.entityCullingEnabled()) {
            double maxDistSq = cfg.entityCullingDistance() * cfg.entityCullingDistance();
            culledViewers = new ArrayList<>(viewers.size());
            for (Player p : viewers) {
                try {
                    Object pp = p.platformPlayer();
                    if (pp instanceof org.bukkit.entity.Player bp) {
                        org.bukkit.Location loc = bp.getLocation();
                        double dx = loc.getX() - bearing.x;
                        double dy = loc.getY() - bearing.y;
                        double dz = loc.getZ() - bearing.z;
                        if (dx * dx + dy * dy + dz * dz <= maxDistSq) {
                            culledViewers.add(p);
                        }
                    }
                } catch (Throwable ignored) {
                    culledViewers.add(p);
                }
            }
            // Despawn elements for viewers who moved out of culling range
            if (!elementViewerIds.isEmpty()) {
                Set<UUID> newIds = new java.util.HashSet<>(culledViewers.size());
                for (Player p : culledViewers) newIds.add(p.uuid());
                List<Player> departed = new ArrayList<>();
                for (Player p : viewers) {
                    if (elementViewerIds.contains(p.uuid()) && !newIds.contains(p.uuid())) {
                        departed.add(p);
                    }
                }
                if (!departed.isEmpty()) {
                    for (dev.arubik.craftengine.contraption.element.ContraptionElement el : elements) {
                        el.despawn(departed);
                    }
                }
            }
            // --- Frustum culling (applied after distance filter) ---
            if (cfg.entityCullingFrustumEnabled() && !culledViewers.isEmpty()) {
                double halfFovCos = Math.cos(Math.toRadians(cfg.entityCullingFovDegrees() / 2.0));
                double nearBypassSq = cfg.entityCullingNearBypass() * cfg.entityCullingNearBypass();
                double expansion = cfg.entityCullingExpansion();
                List<Player> frustumPassed = new ArrayList<>(culledViewers.size());
                for (Player p : culledViewers) {
                    try {
                        Object pp = p.platformPlayer();
                        if (!(pp instanceof org.bukkit.entity.Player bp)) { frustumPassed.add(p); continue; }
                        org.bukkit.Location eye = bp.getEyeLocation();
                        double dx = bearing.x - eye.getX();
                        double dy = bearing.y - eye.getY();
                        double dz = bearing.z - eye.getZ();
                        double distSq = dx * dx + dy * dy + dz * dz;
                        // Always show if within near_bypass distance
                        if (distSq <= nearBypassSq) { frustumPassed.add(p); continue; }
                        double dist = Math.sqrt(distSq);
                        // Player look direction (Bukkit yaw convention: 0=south, 90=west, 180=north, 270=east)
                        float eyeYaw = eye.getYaw();
                        float eyePitch = eye.getPitch();
                        double cosP = Math.cos(Math.toRadians(eyePitch));
                        double lookX = -Math.sin(Math.toRadians(eyeYaw)) * cosP;
                        double lookY = -Math.sin(Math.toRadians(eyePitch));
                        double lookZ =  Math.cos(Math.toRadians(eyeYaw)) * cosP;
                        // Dot product of look direction and direction to bearing
                        double dot = (dx * lookX + dy * lookY + dz * lookZ) / dist;
                        // Account for contraption bounding radius: expand FOV cone by atan(expansion/dist)
                        double expandedHalfFovCos = halfFovCos;
                        if (expansion > 0) {
                            double expandAngle = Math.atan(expansion / dist);
                            expandedHalfFovCos = Math.cos(Math.max(0, Math.toRadians(cfg.entityCullingFovDegrees() / 2.0) - expandAngle));
                        }
                        if (dot >= expandedHalfFovCos) frustumPassed.add(p);
                    } catch (Throwable ignored) { frustumPassed.add(p); }
                }
                // Despawn for players who passed distance but failed frustum
                if (frustumPassed.size() < culledViewers.size() && !elementViewerIds.isEmpty()) {
                    List<Player> frustumDeparted = new ArrayList<>();
                    Set<UUID> frustumPassedIds = new java.util.HashSet<>();
                    for (Player p : frustumPassed) frustumPassedIds.add(p.uuid());
                    for (Player p : culledViewers) {
                        if (elementViewerIds.contains(p.uuid()) && !frustumPassedIds.contains(p.uuid())) {
                            frustumDeparted.add(p);
                        }
                    }
                    if (!frustumDeparted.isEmpty()) {
                        for (dev.arubik.craftengine.contraption.element.ContraptionElement el : elements) {
                            el.despawn(frustumDeparted);
                        }
                    }
                }
                culledViewers = frustumPassed;
            }
            elementViewerIds.clear();
            for (Player p : culledViewers) elementViewerIds.add(p.uuid());
        } else {
            culledViewers = viewers;
        }

        dev.arubik.craftengine.contraption.element.RenderContext ctx =
                new dev.arubik.craftengine.contraption.element.RenderContext(
                        culledViewers, bearing, yaw, pitch, roll, scale, moved, state.level(), realLevel, state.lightMap(), elements);
        for (dev.arubik.craftengine.contraption.element.ContraptionElement element : elements) {
            if (!element.isValid()) continue;
            element.tick(ctx);
            element.render(ctx);
        }
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
            // piston shaft despawn handled by element loop
            return;
        }
        double extended = piston.extendedBlocks();
        Vec3 facing = piston.direction();
        String headItemId = dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement.headItem();
        String shaftItemId = dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement.shaftItemFor(facing);
        var shaftElem = pistonShaftElement();
        if (shaftElem != null) shaftElem.updateShaft(facing, extended, headItemId, shaftItemId);
    }

    private dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement pistonShaftElement() {
        for (dev.arubik.craftengine.contraption.element.ContraptionElement e : state.elements()) {
            if (e instanceof dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement s) return s;
        }
        return null;
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
    /**
     * The player currently riding this contraption's anchor entity, or {@code null}.
     *
     * <p>Only an entity-anchored contraption has one; a block bearing cannot be ridden, so this is
     * {@code null} for every other kind and they are unaffected.
     */
    private dev.arubik.craftengine.contraption.element.ContraptionHitboxElement hitboxElement() {
        for (dev.arubik.craftengine.contraption.element.ContraptionElement e : state.elements()) {
            if (e instanceof dev.arubik.craftengine.contraption.element.ContraptionHitboxElement h) {
                return h;
            }
        }
        return null;
    }


    private java.util.UUID anchorRiderId() {
        java.util.UUID anchorId = state.anchorEntityId();
        if (anchorId == null) {
            return null;
        }
        org.bukkit.entity.Entity anchor = org.bukkit.Bukkit.getEntity(anchorId);
        if (anchor == null) {
            return null;
        }
        for (org.bukkit.entity.Entity passenger : anchor.getPassengers()) {
            if (passenger instanceof org.bukkit.entity.Player player) {
                return player.getUniqueId();
            }
        }
        return null;
    }

    public void carryRiders(List<ServerPlayer> candidates) {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        java.util.Map<java.util.UUID, Vec3> seated = state.seatedRiders();
        List<ServerPlayer> standingOnly = seated.isEmpty() ? candidates
                : candidates.stream().filter(sp -> !seated.containsKey(sp.getUUID())).toList();
        hitboxElement().carryRiders(standingOnly, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(),
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
        hitboxElement().carryNearbyEntities(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians(), state.anchorEntityId());
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
        hitboxElement().pushBackNearbyBystanders(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale(), state.pushSettings());
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
        hitboxElement().pushBackNearbyEntities(realLevel, bearing, state.lastDeltaX(), state.lastDeltaY(), state.lastDeltaZ(), state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale(), state.pushSettings(), state.anchorEntityId());
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
     *
     * <p><b>Full pose, not yaw-only (2026-07-16 fix — the reported seat drift).</b> The mount's target used
     * to come from the yaw-only {@code renderPosition(local, bearing, yaw)} overload, which pins
     * {@code pitch = roll = 0} and {@code scale = 1} — while the furniture the seat belongs to, and every
     * captured block around it, projected through the contraption's FULL live pose. On any scaled or tilted
     * contraption the seat was therefore computed against a different transform than its own sofa, so the
     * mount (and the rider on it) sat visibly away from the seat — further off the further the seat was from
     * the bearing pivot, and dragged along every tick since this method re-asserts it. The full pose is now
     * threaded, here and through {@code SeatSlot#currentRealPosition}, so a seat rides exactly the
     * {@code renderPosition} mapping its furniture does. At {@code scale == 1 && pitch == 0 && roll == 0}
     * this is byte-for-byte the old call (see {@code ContraptionMath}'s overload chain).
     *
     * <p><b>Rider scale (roadmap item #9).</b> A seated rider is resized to the contraption's own uniform
     * {@code scale} every tick, guarded by a base-value compare inside
     * {@link ContraptionSeatMount#applyContraptionScale}, so rescaling a contraption with someone already
     * sitting in it (the creative phys wand) resizes them live. <b>The scale is deliberately never restored
     * on dismount — that is an intentional gag, not a missing teardown; see that method's javadoc before
     * "fixing" it.</b>
     */
    public void carrySeatedRiders() {
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        double pitch = state.pitchRadians();
        double roll = state.rollRadians();
        double scale = state.scale();
        for (java.util.Map.Entry<java.util.UUID, Vec3> e : state.seatedRiders().entrySet()) {
            java.util.UUID id = e.getKey();
            java.util.UUID mountId = state.seatedRiderMount(id);
            org.bukkit.entity.Entity mount = ContraptionSeatMount.resolve(mountId);
            if (mount == null) {
                continue; // mount despawned/world unloaded — stays registered, nothing to reposition this tick
            }
            Vec3 target = ContraptionMath.renderPosition(e.getValue(), bearing,
                    yaw, pitch, roll, scale);
            float seatYaw = 0f;
            // Block seat lookup migrated to ContraptionSeatElement
            ContraptionSeatMount.reposition(mount, target, seatYaw);
            rotateSeatedRiderView(id, seatYaw);
            org.bukkit.entity.Player seated = org.bukkit.Bukkit.getPlayer(id);
            if (seated != null) {
                ContraptionSeatMount.applyContraptionScale(seated, scale);
            }
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
        itemPickupSwarm.despawnAll();
        // Block seats despawned via element loop below
        // piston shaft despawn handled by element loop
        for (dev.arubik.craftengine.contraption.element.ContraptionElement e : state.elements()) {
            e.despawn(viewers);
        }
        // Seated riders (real vehicle-mounting, 2026-07-02 rework — see ContraptionSeatMount):
        // a contraption vanishing out from under a seated rider must release the real passenger
        // relationship and remove the mount entity same as an explicit sneak-dismount, otherwise
        // the player is left mounted on an entity about to be orphaned/removed with no landing
        // position ever computed. ContraptionSeatListener.dismount does the actual
        // unmount+teleport (shared logic) — this just drives it for every still-seated rider.
        // Seat dismount — ContraptionSeatListener removed, handled by interaction system
        for (java.util.UUID id : state.seatedRiders().keySet()) {
            state.removeSeatedRider(id);
        }
    }

    /**
     * Just the hitbox/shulker-collider swarm despawn — see {@link #despawnRest}'s javadoc for
     * why a real-block-restoring teardown must call this SEPARATELY, and strictly AFTER the
     * real blocks are already back in the world.
     */
    public void despawnHitboxesOnly(List<Player> viewers) {
        var h = hitboxElement();
        if (h != null) h.despawnAll(viewers);
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
        var h = hitboxElement();
        return h != null ? h.currentRiderIds() : java.util.Set.of();
    }

    public int cellCount() {
        return (int) state.elements().stream()
                .filter(e -> e.type().equals(dev.arubik.craftengine.contraption.element.ElementTypes.BLOCK))
                .count();
    }

    public int hitboxCellCount() {
        var h = hitboxElement();
        return h != null ? h.cellCount() : 0;
    }

    public int mirroredEntityCount() {
        for (dev.arubik.craftengine.contraption.element.ContraptionElement e : state.elements()) {
            if (e instanceof dev.arubik.craftengine.contraption.element.ContraptionLiveEntityMirrorElement m) {
                return m.entityIds().length;
            }
        }
        return 0;
    }

    public int pickupMirrorCount() {
        return itemPickupSwarm.mirrorCount();
    }
}
