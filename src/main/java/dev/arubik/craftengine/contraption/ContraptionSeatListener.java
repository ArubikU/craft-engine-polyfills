package dev.arubik.craftengine.contraption;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

import dev.arubik.craftengine.contraption.render.ContraptionFurnitureSwarm;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Completes the furniture-seat gap {@code ContraptionFurnitureCapture#captureNear}'s javadoc
 * documents as a TODO: a captured seat has no real vehicle entity left to mount (see that
 * class's design notes for why), so this listener is the ONLY way to sit down after assembly,
 * and the only way to explicitly stand back up mid-flight — {@link ContraptionState#addSeatedRider}
 * was previously populated ONCE at capture time and never grew/shrank afterward.
 *
 * <p><b>Sit (right-click a free seat).</b> Same manual-raycast-in-real-space technique
 * {@link ContraptionInteractionListener} already uses for block cells, run independently against
 * every {@link ContraptionFurnitureSwarm.SeatSlot} instead of block cells. Registered at
 * {@link EventPriority#NORMAL} (runs BEFORE {@link ContraptionInteractionListener}'s
 * {@code HIGH}) so a successful sit cancels the event first; that listener already declares
 * {@code ignoreCancelled = true} and so never double-fires against the same click.
 *
 * <p><b>Two event types, not one (2026-07-02 session — "sigo sin poder sentarme").</b> A
 * furniture seat's clickable surface is {@code render.ContraptionFurnitureSwarm}'s own real,
 * packet-only {@code INTERACTION} hitbox mirror (see that class's "Hitbox/collider" javadoc) —
 * the CLIENT genuinely sees a real, collidable entity there, so right-clicking it is reported to
 * the server as a {@link PlayerInteractEntityEvent} (an ENTITY interaction), never a
 * {@link PlayerInteractEvent} (a block/air interaction) — exactly the same "entity, not block"
 * distinction {@link ContraptionInteractionListener}'s own {@code onInteractEntity} javadoc
 * already documents for captured blocks sitting under a hitbox mirror. This listener previously
 * only ever registered {@link #onInteract} against {@link PlayerInteractEvent}, so a click that
 * lands on the seat's own hitbox mirror (the overwhelmingly common case: the seat's
 * {@code type: interaction} hitbox in the furniture's config, e.g. {@code cml:street_chair}'s
 * {@code position: 0,0,0} hitbox with its {@code seats:} entry, is a small box roughly where a
 * player naturally aims) never reached this listener's sit logic AT ALL — the click was instead
 * only ever seen by {@link ContraptionInteractionListener#onInteractEntity}, which raycasts
 * against captured BLOCKS only and does nothing for a miss, silently swallowing every attempt.
 * {@link #onInteractEntity} below closes that gap: identical sit logic, registered against
 * {@link PlayerInteractEntityEvent} at the same {@code NORMAL} priority (still strictly before
 * {@link ContraptionInteractionListener#onInteractEntity}'s {@code HIGH}), so a successful sit
 * cancels the event before that listener's block-only raycast ever runs and misses.
 *
 * <p><b>REVISED, round 2 (2026-07-02, same day) — {@code PlayerInteractEntityEvent} doesn't fire
 * at all for this click.</b> Live-tested and disproven: {@code ContraptionInteractPacketDebug}'s
 * own javadoc already documents (from an earlier debugging session, for the analogous block-cell
 * case) that our packet-only fake entities have no real server-side Bukkit entity backing them —
 * the client sends a real {@code ServerboundInteractPacket} referencing the fake id, but vanilla's
 * packet handler can't resolve a target for an id the server never actually spawned, so it drops
 * the packet BEFORE Bukkit's event system ever constructs a {@code PlayerInteractEntityEvent}.
 * {@link #onInteractEntity} below was therefore dead code the moment it was written — correctly
 * implemented, listening for an event that is simply never delivered for this click. The REAL fix
 * mirrors what {@code ContraptionInteractPacketDebug} already does for blocks: intercept the raw
 * {@code INTERACT_ENTITY} packet below Bukkit's event layer and drive the seat raycast
 * ({@link #tryHandleSit}) directly from there, before falling back to
 * {@link ContraptionInteractionListener}'s block raycast — see that class's javadoc for the
 * updated dispatch order and the thread-hop rationale. {@link #onInteractEntity} is kept
 * registered as a harmless no-op fallback only.
 *
 * <p><b>Real vehicle-mounting (2026-07-02 session, seat-only rework — explicit user correction:
 * "es para el sistema de seat de los furniture que estan dentro de el contraption").</b> Sitting
 * down now spawns a real, invisible {@code ArmorStand} mount entity in the real world at the
 * seat's current position (see {@link ContraptionSeatMount}) and makes the player a genuine
 * vanilla passenger of it via {@code Entity#addPassenger} — exactly the pattern CraftEngine's own
 * furniture seats already use ({@code BukkitSeat}/{@code BukkitSeatManager} in CraftEngine's real
 * source). {@code ContraptionEntity#carrySeatedRiders} repositions that mount entity every tick to
 * track the contraption's current transform; vanilla's own passenger-follows-vehicle mechanics
 * move the player automatically — no per-tick player teleport or velocity packet needed at all.
 *
 * <p><b>Stand (sneak).</b> Vanilla's own vehicle-dismount-on-sneak (a real client-side gesture
 * tied to actually being a passenger) is NOT relied on here — {@link PlayerToggleSneakEvent} is
 * listened for directly instead, same as before this rework, since it's simple and already proven
 * to work independent of whatever the passenger relationship is doing. Dismounting removes the
 * passenger relationship and the mount entity (see {@link #dismount}), then teleports the player
 * to a small yaw-aware offset from the seat's last real position — reusing this session's
 * already-fixed dismount-position logic (computed from the SEAT, not from the player's own
 * possibly-stale location).
 */
public final class ContraptionSeatListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard — same convention as every other wand/click listener
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        if (tryHandleSit(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * See class javadoc, "Two event types, not one". DEAD CODE as of the 2026-07-02 live test
     * ("el furniture ya no tiene el armor stand pero el interact aun no sirve") — {@code
     * PlayerInteractEntityEvent} genuinely never fires for a click against one of our packet-only
     * {@code INTERACTION} hitbox mirrors at all, proven the same way {@code
     * ContraptionInteractPacketDebug}'s own javadoc documents for {@code
     * ContraptionInteractionListener}'s block raycast: the server never spawned a real Bukkit
     * entity under that fake id, so vanilla's packet handler can't resolve a target and drops the
     * {@code ServerboundInteractPacket} before Bukkit's event system ever constructs this event —
     * confirmed by that same live packet-level log line ("INTERACT_ENTITY packets arrived every
     * click, but zero raw-Bukkit-event... logs ever followed") applying equally to a seat's own
     * hitbox mirror, which is spawned by this exact same packet-only technique. This handler is
     * kept registered as a harmless no-op fallback (in case some future click path DOES route
     * through real Bukkit entity interaction, e.g. a real vehicle) but the ACTUAL fix is
     * {@link ContraptionInteractPacketDebug}, which now also tries {@link #tryHandleSit} — see
     * that class's javadoc.
     *
     * <p><b>Real-entity bypass (2026-07-02 live-test fix — "sigo sin poder desarmar el
     * contraption", minecart bearing specifically).</b> Exactly the same landmine
     * {@link ContraptionInteractPacketDebug}'s own "Real-entity bypass" javadoc documents and
     * fixes at the packet layer — this handler never got the equivalent fix at the Bukkit-event
     * layer, and unlike the packet interceptor it is NOT dead code: it fires normally for a click
     * against a REAL entity (a genuine {@code PlayerInteractEntityEvent} is constructed for those),
     * it's only fake packet-only hitbox mirrors that never reach it. {@link #tryHandleSit}/
     * {@link #hasSeatUnderAim} are both keyed purely off the PLAYER'S AIM — they never look at
     * {@code event.getRightClicked()} at all — so right-clicking a REAL entity such as a
     * {@link MinecartBearing} minecart (whose captured structure sits directly around/above it,
     * making a seat-slot aim-hit very plausible) could find a nearby free seat and cancel the event
     * right here at {@code NORMAL}, BEFORE {@link BearingHammerListener#onInteractEntity}
     * (registered at {@code HIGH}, {@code ignoreCancelled = true}) ever got a chance to see the
     * still-uncancelled event and disassemble the bearing — silently swallowing the hammer click.
     * Fix: skip this listener entirely for a click on any real, already-existing entity that has
     * its own dedicated handler (currently just {@link MinecartBearing#isBearing}) — those get real
     * events their own listeners already handle correctly, so this aim-based seat fallback has no
     * business intercepting them first.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard — same convention as every other wand/click listener
        }
        if (MinecartBearing.isBearing(event.getRightClicked())) {
            return; // real entity with its own dedicated handler — see javadoc "Real-entity bypass"
        }
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        if (tryHandleSit(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Shared guard + sit attempt used by both real Bukkit-event entry points above AND
     * {@link ContraptionInteractPacketDebug}'s packet-level interceptor (the path that actually
     * fires for a click on a seat's packet-only hitbox mirror — see that class's javadoc). Package-
     * private so the packet interceptor can call it directly without going through an event that
     * will never be delivered for this case.
     */
    static boolean tryHandleSit(ServerPlayer player) {
        if (player.isPassenger() || isAlreadySeated(player.getUUID())) {
            return false; // already riding something (real vehicle, or an already-seated contraption rider)
        }
        return trySit(player);
    }

    /**
     * See class javadoc "Stand (sneak)". Thin trigger — the actual unmount+teleport is shared
     * with {@code ContraptionEntity#despawnRest} (a contraption vanishing out from under a seated
     * rider needs the exact same release) via {@link #dismount}.
     */
    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return; // only the sneak-DOWN edge dismounts, matching vanilla vehicle dismount
        }
        UUID id = event.getPlayer().getUniqueId();
        for (ContraptionEntity entity : ContraptionManager.all()) {
            if (!entity.state().seatedRiders().containsKey(id)) {
                continue;
            }
            dismount(entity.state(), id);
            return; // a player can only be seated in one contraption at a time
        }
    }

    /**
     * Mirrors CraftEngine's own {@code BukkitSeatManager}, which hooks both of these events to
     * release a seated rider's vehicle relationship — a player who disconnects or dies mid-ride
     * otherwise leaves {@code state.seatedRiders()}/{@code seatedRiderMount} bookkeeping and the
     * real {@code ArmorStand} mount entity ({@link ContraptionSeatMount#mount}) orphaned forever,
     * since {@link #onToggleSneak} never fires for either case.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dismountIfSeated(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        dismountIfSeated(event.getEntity().getUniqueId(), false);
    }

    private static void dismountIfSeated(UUID id, boolean skipTeleport) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            if (!entity.state().seatedRiders().containsKey(id)) {
                continue;
            }
            dismount(entity.state(), id, skipTeleport);
            return; // a player can only be seated in one contraption at a time
        }
    }

    /**
     * Releases a seated rider: removes the real passenger relationship + mount entity (see
     * {@link ContraptionSeatMount}), vacates the seat slot, clears the rider from {@code state},
     * and teleports the player to a sensible nearby landing position.
     *
     * <p><b>Dismount position, computed from the SEAT/mount, not from {@code
     * Player#getLocation()}</b> (2026-07-02 session — preserved across the real-vehicle-mounting
     * rework from an earlier live-test fix in this same session): a small forward-and-up offset
     * in the seat's current FACING direction (yaw-rotation-aware, so it rotates correctly with
     * the contraption), landing the player standing just in front of/next to the seat instead of
     * embedded in its model. Prefers the occupied {@code SeatSlot}'s own freshly-computed real
     * position/yaw; if the slot can't be found by occupant id anymore (furniture swarm rebuilt
     * mid-ride — should-never-happen but cheap to guard), falls back to the mount entity's OWN
     * last real location/yaw (read BEFORE it's removed below — the freshest available real-world
     * position, since {@code ContraptionEntity#carrySeatedRiders} repositions it every tick); if
     * even that's unavailable, falls back once more to the player's own current location so this
     * path can never NPE (closing the same "fallback read after the value was already cleared"
     * bug class an earlier version of this method hit for the previous, now-removed
     * {@code seatedRiderLastReal} map).
     */
    static void dismount(ContraptionState state, UUID id) {
        dismount(state, id, false);
    }

    /**
     * {@code skipTeleport} is set by {@link #onQuit} — {@code Bukkit.getPlayer(id)} can still
     * return the quitting player at this point in the event lifecycle (they're not fully removed
     * yet), but {@code Player#teleport} on an already-disconnecting player is pointless at best
     * and risks throwing/misbehaving depending on how far along the disconnect is, so the quit
     * path only unmounts + clears bookkeeping and never attempts the teleport.
     */
    static void dismount(ContraptionState state, UUID id, boolean skipTeleport) {
        // Standing up releases the helm if this rider was driving (steer-vehicle).
        VehicleDriverRegistry.clearDriver(id);
        org.bukkit.entity.Player bukkit = skipTeleport ? null : org.bukkit.Bukkit.getPlayer(id);

        ContraptionFurnitureSwarm.SeatSlot occupiedSlot = null;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            if (entity.state() != state) {
                continue;
            }
            for (ContraptionFurnitureSwarm.SeatSlot slot : entity.furnitureSwarm().seatSlots()) {
                if (id.equals(slot.occupant())) {
                    occupiedSlot = slot;
                    slot.vacate();
                }
            }
            break;
        }

        // Read the mount's own current real location BEFORE unmounting/removing it below — the
        // freshest available fallback position (see method javadoc).
        UUID mountId = state.seatedRiderMount(id);
        org.bukkit.entity.Entity mount = ContraptionSeatMount.resolve(mountId);
        Vec3 fallbackMountPos = mount != null
                ? new Vec3(mount.getLocation().getX(), mount.getLocation().getY(), mount.getLocation().getZ())
                : null;
        float fallbackMountYaw = mount != null ? mount.getLocation().getYaw() : 0f;

        state.removeSeatedRider(id);
        ContraptionSeatMount.unmount(mountId);

        if (bukkit == null) {
            return; // offline (e.g. quit mid-dismount) — nothing left to teleport
        }

        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        double yaw = state.yawRadians();
        Vec3 seatPos = occupiedSlot != null
                ? occupiedSlot.currentRealPosition(bearing, yaw, state.pitchRadians(), state.rollRadians(),
                        state.scale())
                : fallbackMountPos != null ? fallbackMountPos
                : new Vec3(bukkit.getLocation().getX(), bukkit.getLocation().getY(), bukkit.getLocation().getZ());
        float seatYaw = occupiedSlot != null ? occupiedSlot.currentYawDegrees(yaw)
                : fallbackMountPos != null ? fallbackMountYaw
                : (float) Math.toDegrees(yaw);
        double yawRad = Math.toRadians(seatYaw);
        double forwardX = -Math.sin(yawRad) * 0.7;
        double forwardZ = Math.cos(yawRad) * 0.7;

        org.bukkit.Location dismountLoc = new org.bukkit.Location(bukkit.getWorld(),
                seatPos.x + forwardX, seatPos.y + 0.1, seatPos.z + forwardZ,
                bukkit.getLocation().getYaw(), bukkit.getLocation().getPitch());
        bukkit.teleport(dismountLoc);
    }

    private static boolean isAlreadySeated(UUID id) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            if (entity.state().seatedRiders().containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read-only peek used by {@link ContraptionInteractPacketDebug} to decide packet cancellation
     * SYNCHRONOUSLY (must happen inline, before the packet continues down the pipeline — see that
     * class's thread-hop javadoc) without performing the actual seat/teleport mutation, which is
     * not safe to run off the main thread. Deliberately duplicates {@link #trySit}'s raycast-only
     * half rather than sharing code with it, so the mutating half of {@code trySit} stays a single
     * self-contained unit that's always run atomically on the main thread via
     * {@link #tryHandleSit}.
     */
    static boolean hasSeatUnderAim(ServerPlayer player) {
        double maxDistance = player.blockInteractionRange();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));

        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
            double yaw = state.yawRadians();
            for (ContraptionFurnitureSwarm.SeatSlot slot : entity.furnitureSwarm().seatSlots()) {
                if (!slot.isFree()) {
                    continue;
                }
                Vec3 real = slot.currentRealPosition(bearing, yaw, state.pitchRadians(), state.rollRadians(),
                        state.scale());
                AABB box = new AABB(real.x - 0.5, real.y - 0.5, real.z - 0.5, real.x + 0.5, real.y + 0.5, real.z + 0.5);
                if (box.clip(eye, end).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * See class javadoc "Sit". Returns true (and seats the player) iff a free seat was hit.
     *
     * <p><b>One rider per seat, verified (2026-07-02 session — "cada seat solo debe sostener 1
     * jugador").</b> Already enforced correctly, no change needed: the candidate scan below
     * skips every {@link ContraptionFurnitureSwarm.SeatSlot} where {@code !slot.isFree()} (i.e.
     * already has a non-null {@code occupant}), so an occupied seat is never selected as
     * {@code bestSlot} for a second player. This method — like every entry point that reaches
     * it ({@link #tryHandleSit}, itself only ever invoked on the main thread via {@code
     * ContraptionInteractPacketDebug}'s {@code Bukkit.getScheduler().runTask} hop) — only ever
     * runs on the single-threaded main server thread, so two players racing to click the same
     * seat can never both observe {@code isFree() == true} concurrently: whichever click's
     * scheduled task runs first calls {@link ContraptionFurnitureSwarm.SeatSlot#occupy}
     * synchronously before the second task ever gets a turn, and the second task's own fresh
     * scan of {@code seatSlots()} then correctly sees the slot as taken and fails to select it
     * (falls through to {@code bestSlot == null} -&gt; {@code false}), never overwriting/kicking
     * the first occupant.
     */
    private static boolean trySit(ServerPlayer player) {
        double maxDistance = player.blockInteractionRange();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));

        ContraptionState bestState = null;
        ContraptionEntity bestEntity = null;
        ContraptionFurnitureSwarm.SeatSlot bestSlot = null;
        double bestDistSq = Double.MAX_VALUE;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
            double yaw = state.yawRadians();
            for (ContraptionFurnitureSwarm.SeatSlot slot : entity.furnitureSwarm().seatSlots()) {
                if (!slot.isFree()) {
                    continue;
                }
                Vec3 real = slot.currentRealPosition(bearing, yaw, state.pitchRadians(), state.rollRadians(),
                        state.scale());
                // Best-effort click target box (same scoped shortcut ContraptionFurnitureSwarm's
                // own HitboxCell already documents taking — not a faithful reproduction of the
                // furniture's real configured collider shape).
                AABB box = new AABB(real.x - 0.5, real.y - 0.5, real.z - 0.5, real.x + 0.5, real.y + 0.5, real.z + 0.5);
                Optional<Vec3> clip = box.clip(eye, end);
                if (clip.isEmpty()) {
                    continue;
                }
                double distSq = eye.distanceToSqr(clip.get());
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    bestState = state;
                    bestEntity = entity;
                    bestSlot = slot;
                }
            }
        }
        if (bestSlot == null) {
            return false;
        }

        Vec3 bearing = new Vec3(bestState.x(), bestState.y(), bestState.z());
        double yaw = bestState.yawRadians();
        Vec3 seatPos = bestSlot.currentRealPosition(bearing, yaw, bestState.pitchRadians(), bestState.rollRadians(),
                bestState.scale());
        float seatYaw = bestSlot.currentYawDegrees(yaw);

        // Real vehicle-mounting (2026-07-02 session, seat-only rework) — spawn a real, invisible
        // ArmorStand mount at the seat's current real position and make the player a genuine
        // vanilla passenger of it (see ContraptionSeatMount's javadoc for the exact CraftEngine-
        // matching setup). If addPassenger somehow fails (mount() returns null — mirrors
        // BukkitSeat's own defensive check), bail out WITHOUT occupying the slot/registering the
        // rider, so a failed mount can't leave a seat permanently stuck "occupied" with nobody
        // actually seated.
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getBukkitEntity();
        org.bukkit.entity.ArmorStand mount = ContraptionSeatMount.mount(bukkitPlayer, bukkitPlayer.getWorld(), seatPos, seatYaw);
        if (mount == null) {
            return false;
        }

        bestSlot.occupy(player.getUUID());
        bestState.addSeatedRider(player.getUUID(), bestSlot.bearingLocalOffset());
        bestState.setSeatedRiderMount(player.getUUID(), mount.getUniqueId());
        // Roadmap item #9: sitting in a scaled contraption resizes the rider to match it, so they actually
        // fill the giant sofa instead of perching on it. Applied here (not left to the next tick's
        // carrySeatedRiders) so the resize lands on the same tick as the sit, with no visible pop.
        // The scale is INTENTIONALLY never restored on dismount — see
        // ContraptionSeatMount#applyContraptionScale's javadoc before "repairing" that into a restore.
        ContraptionSeatMount.applyContraptionScale(bukkitPlayer, bestState.scale());
        // Steer-vehicle: the first player to take a DRIVER seat on a VEHICLE contraption becomes its pilot
        // (user: "conduce el primero en sentarse"). A seat is a driver seat if its furniture id is registered
        // as a vehicle_seat; if the vehicle has NO marked seat at all, ANY seat drives (so it works out of the
        // box with any furniture). Passengers just ride.
        if (bestState.bearingType() == BearingType.VEHICLE
                && VehicleDriverRegistry.driverOf(bestState.id()) == null
                && isDriverSeat(bestEntity, bestSlot)) {
            VehicleDriverRegistry.setDriver(bestState.id(), player.getUUID());
            org.bukkit.entity.Player bp = org.bukkit.Bukkit.getPlayer(player.getUUID());
            if (bp != null) {
                bp.sendActionBar(net.kyori.adventure.text.Component.text(
                        "§bAl timón — WASD mover, A/D girar, salto subir, sprint bajar, agáchate para bajar."));
            }
        }
        return true;
    }

    /** Whether {@code slot} confers driving: it's a marked vehicle_seat, or the vehicle has no marked seat at
     *  all (fallback so any seat drives). Block seats ({@code furniture == null}) count only under the fallback. */
    private static boolean isDriverSeat(ContraptionEntity entity, ContraptionFurnitureSwarm.SeatSlot slot) {
        boolean anyMarked = false;
        for (ContraptionFurnitureSwarm.SeatSlot s : entity.furnitureSwarm().seatSlots()) {
            if (s.furniture != null && VehicleDriverRegistry.isVehicleSeat(s.furniture.definitionId().toString())) {
                anyMarked = true;
                break;
            }
        }
        if (!anyMarked) {
            return true; // no dedicated helm on this vehicle — any seat drives
        }
        return slot.furniture != null
                && VehicleDriverRegistry.isVehicleSeat(slot.furniture.definitionId().toString());
    }
}
