package dev.arubik.craftengine.contraption;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.world.phys.Vec3;

/**
 * Real vehicle-mounting for contraption furniture seats (2026-07-02 session — explicit user
 * correction: the earlier hand-rolled teleport/velocity-lock seat mechanism was scoped wrong;
 * a seat is a "sit down and lock" interaction, exactly what CraftEngine's OWN furniture seats
 * already implement via a genuine vanilla passenger relationship — see
 * {@code net.momirealms.craftengine.bukkit.entity.seat.BukkitSeat}/{@code BukkitSeatManager} in
 * CraftEngine's real source (read directly, not decompiled). This class reproduces that exact
 * pattern for a seat living on a moving contraption: spawn a small/invisible/no-AI/no-gravity
 * {@code ArmorStand} in the REAL world at the seat's current real-world position, and call
 * {@code Entity#addPassenger} to make the player a genuine vanilla passenger — moving the mount
 * every tick (see {@link #reposition}) moves the rider automatically via vanilla's own
 * passenger-follows-vehicle mechanics, no velocity packets or per-tick player teleports needed.
 *
 * <p>Deliberately NOT used by {@code PlayerCarry} (the general standing/walking carry case) —
 * that class's own javadoc documents in detail why real mounting is the WRONG tool there
 * (vanilla forcibly locks a real passenger's position every tick with no way to also let them
 * walk freely). A seat's rider isn't meant to walk around at all, which is exactly the
 * passenger-lock behavior this class leans on.
 */
final class ContraptionSeatMount {

    private ContraptionSeatMount() {
    }

    /**
     * Vertical correction applied between the seat's own intended sit position and where the
     * ArmorStand mount is actually spawned — copied from real CraftEngine's
     * {@code BukkitSeat#calculateSeatLocation}/{@code spawnSeatEntityForPlayer} (1.20.2+ branch;
     * this project targets 1.21.11, well above that cutoff, so the older {@code 0.990625}
     * constant CraftEngine only uses below 1.20.2 is irrelevant here). CraftEngine computes the
     * PLAYER's intended seat position as {@code sourceLocation + offset + (0, 0.6, 0)}, then
     * spawns the actual ArmorStand {@code 0.9875} BELOW that (i.e. at
     * {@code seatPosition - (0, 0.9875, 0)}) — compensating for a real, non-marker, small
     * ArmorStand's own non-zero vanilla passenger-mount height, so the rider's real seated eye/
     * body height ends up exactly at the intended seat position despite the stand's feet being
     * spawned lower. {@code SEAT_HEIGHT} and {@code ARMOR_STAND_SPAWN_CORRECTION} below are that
     * same pair of constants, applied the same way, so a contraption's seat lands a rider at the
     * IDENTICAL relative height CraftEngine's own real (non-contraption) furniture seat would.
     */
    private static final double SEAT_HEIGHT = 0.6;
    private static final double ARMOR_STAND_SPAWN_CORRECTION = 0.9875;

    /**
     * Spawns a fresh real mount entity at {@code realPos}/{@code yawDegrees} (the seat's own
     * intended sit position — i.e. {@code SeatConfig#position()}, rotated/translated, with NO
     * vertical correction applied yet) and mounts {@code player} onto it as a genuine vanilla
     * passenger. Mirrors {@code BukkitSeat#spawnSeatEntityForPlayer}'s armor-stand branch exactly
     * (small, invisible, silent, invulnerable, no arms, no AI, no gravity, can't tick, not
     * persistent, minimal health, and critically NOT a marker — see {@link #SEAT_HEIGHT}'s
     * javadoc: a marker ArmorStand's vanilla passenger-mount height collapses to zero, which is
     * NOT what the {@code 0.6}/{@code 0.9875} constants below are calibrated for, so setting
     * marker here would silently reintroduce a large vertical seat-height error) — this project
     * always uses the armor-stand branch (never the item-display fallback CraftEngine uses on
     * older versions), since {@code ArmorStand} is CraftEngine's own primary/simpler pattern and
     * there's no legacy-version constraint here.
     *
     * <p>Returns null (and removes the freshly spawned entity again) if {@code addPassenger}
     * fails — same defensive check {@code BukkitSeat} itself performs — so a caller never treats
     * a mount as "active" when the player never actually became its passenger.
     */
    static ArmorStand mount(Player player, World world, Vec3 realPos, float yawDegrees) {
        double spawnY = realPos.y + SEAT_HEIGHT - ARMOR_STAND_SPAWN_CORRECTION;
        Location loc = new Location(world, realPos.x, spawnY, realPos.z, yawDegrees, 0f);
        ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        org.bukkit.attribute.AttributeInstance health = stand.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(0.01);
        }
        stand.setSmall(true);
        stand.setInvisible(true);
        stand.setSilent(true);
        stand.setInvulnerable(true);
        stand.setArms(false);
        stand.setCanTick(false);
        stand.setAI(false);
        stand.setGravity(false);
        stand.setPersistent(false);
        // Deliberately NOT setMarker(true) — see this method's own javadoc: a marker stand's
        // vanilla passenger-mount height is zero, incompatible with the SEAT_HEIGHT/
        // ARMOR_STAND_SPAWN_CORRECTION math above, which is calibrated for a real (non-marker)
        // small ArmorStand exactly like CraftEngine's own seat entity.
        if (!stand.addPassenger(player)) {
            stand.remove();
            return null;
        }
        return stand;
    }

    /**
     * Repositions an already-mounted seat entity to the given real-world position/yaw — called
     * every tick by {@code ContraptionEntity#carrySeatedRiders} to track the contraption's
     * current transform. Moving this real entity moves its passenger automatically via vanilla's
     * own passenger mechanics (no need to touch the player directly at all).
     *
     * <p>{@code realPos} is the seat's own intended sit position (same convention as
     * {@link #mount}'s {@code realPos} parameter) — the SAME {@link #SEAT_HEIGHT}/
     * {@link #ARMOR_STAND_SPAWN_CORRECTION} vertical correction is applied here every tick, or
     * this repositioning would silently re-desync the mount back to the uncorrected height the
     * very first time the contraption moves after the player sits down.
     */
    static void reposition(Entity mount, Vec3 realPos, float yawDegrees) {
        Location loc = mount.getLocation();
        loc.setX(realPos.x);
        loc.setY(realPos.y + SEAT_HEIGHT - ARMOR_STAND_SPAWN_CORRECTION);
        loc.setZ(realPos.z);
        loc.setYaw(yawDegrees);
        mount.teleport(loc);
    }

    /**
     * Rotates a seated rider's OWN view yaw by {@code deltaYawDegrees}, WITHOUT moving them and
     * without dismounting (2026-07-03 session — "haz que los seat, al cambiar la rotacion del
     * contraption, si el jugador no esta moviendo su yaw, lo rote por el"). A seat rider is a real
     * vanilla passenger, so their camera yaw is normally independent of the mount — the only
     * vanilla-correct way to nudge it is a RELATIVE teleport through the player's own connection
     * (every field relative → position/velocity deltas are zero, only yaw changes by the delta;
     * this is exactly how vanilla applies knock-rotation and keeps all the awaiting-teleport
     * bookkeeping/ack-gating intact, so it never triggers a "moved wrongly" kick). Best-effort:
     * any mapping mismatch is swallowed rather than breaking the per-tick seat loop.
     */
    static void rotateRiderView(Player player, float deltaYawDegrees) {
        if (deltaYawDegrees == 0f) {
            return;
        }
        try {
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            sp.connection.teleport(new net.minecraft.world.entity.PositionMoveRotation(
                    net.minecraft.world.phys.Vec3.ZERO, net.minecraft.world.phys.Vec3.ZERO, deltaYawDegrees, 0f),
                    java.util.EnumSet.allOf(net.minecraft.world.entity.Relative.class));
        } catch (Throwable ignored) {
            // mapping/version mismatch — skip the view nudge rather than break the seat carry loop
        }
    }

    /**
     * Sets a seated rider's OWN entity scale to the contraption's uniform {@code scale} (roadmap item #9),
     * so a player sitting in a {@code scale=3} contraption is themselves three times the size and actually
     * fills the giant sofa they're on instead of perching on it like a doll. Uses vanilla's real
     * {@code minecraft:scale} attribute ({@code Attributes.SCALE}, present since 1.20.5) — verified against
     * this project's own {@code mappedServerJar.jar}: {@code Attributes.SCALE} is a
     * {@code Holder<Attribute>} and {@code LivingEntity#getAttribute(Holder<Attribute>)} is the accessor —
     * reached through NMS rather than Bukkit's {@code Attribute} registry constant purely because the NMS
     * handle is the one this project can verify against a jar it actually has on disk.
     *
     * <p><b>INTENTIONAL, DO NOT "FIX": the scale is NEVER restored on dismount.</b> There is deliberately no
     * counterpart to this method — no restore on stand, on sneak-dismount, on disassemble, on teleport, on
     * death, or on logoff. A player who sits in a scaled contraption KEEPS that size after standing up,
     * permanently, until something else changes it. This is a gag the user asked for explicitly and by
     * name; it is not an oversight, not a leak, and not a missing teardown path. Every seat-release path
     * ({@code ContraptionSeatListener#dismount} and friends) is therefore correct in saying nothing about
     * scale at all. If you are here because this "looks like a bug" — it is the feature.
     *
     * <p>Write-only and idempotent: it is safe to call every tick (and
     * {@code ContraptionEntity#carrySeatedRiders} does, guarded by a base-value compare) so that rescaling a
     * contraption with someone already sitting in it — e.g. via the creative phys wand — resizes them live.
     * Best-effort: a mapping/attribute failure is swallowed rather than breaking the per-tick seat loop, the
     * same convention {@link #rotateRiderView} already uses.
     */
    static void applyContraptionScale(Player player, double scale) {
        try {
            net.minecraft.server.level.ServerPlayer sp =
                    ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle();
            net.minecraft.world.entity.ai.attributes.AttributeInstance inst =
                    sp.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
            if (inst == null || inst.getBaseValue() == scale) {
                return; // unsupported, or already exactly this size — nothing to send
            }
            inst.setBaseValue(scale);
        } catch (Throwable ignored) {
            // mapping/version mismatch — skip the resize rather than break the seat carry loop
        }
    }

    /** Resolves a previously-spawned mount entity by its real Bukkit UUID, or null if it's gone (e.g. world unload). */
    static Entity resolve(UUID mountEntityId) {
        if (mountEntityId == null) {
            return null;
        }
        return org.bukkit.Bukkit.getEntity(mountEntityId);
    }

    /**
     * Removes the passenger relationship and despawns the mount entity — see
     * {@code BukkitSeatManager#tryLeavingSeat}'s equivalent teardown. Safe to call with a null/
     * already-gone entity (no-op).
     */
    static void unmount(UUID mountEntityId) {
        Entity mount = resolve(mountEntityId);
        if (mount == null) {
            return;
        }
        mount.eject();
        mount.remove();
    }
}
