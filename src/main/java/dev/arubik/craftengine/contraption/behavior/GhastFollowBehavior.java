package dev.arubik.craftengine.contraption.behavior;

import java.lang.reflect.Method;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.bearing.GhastHarness;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.phys.Vec3;

/**
 * GHAST bearing kinematics (2026-07-16 goal — "el contraption heredara el movimiento, caracteristicas,
 * scala y demas del happy ghast"): the entity-anchored twin of {@link MinecartFollowBehavior}, reading a
 * REAL vanilla {@code HappyGhast}'s position/yaw/scale every tick and feeding the contraption the DELTA
 * since last tick. The ghast's own unmodified flight AI/rider control is what actually drives movement —
 * no custom {@code EntityType}, no reimplemented flight math.
 *
 * <p><b>Delta, not absolute snap.</b> Like {@link MinecartFollowBehavior} (read its javadoc — this class
 * exists because of the bug documented there), this behavior only RECORDS the ghast's pose each tick and
 * returns the real per-tick delta from {@link #velocityThisTick()}, letting
 * {@code ContraptionEngine#stepKinematics} apply the position itself. That is what populates
 * {@code ContraptionState#lastDelta}, which is the ONLY thing {@code ContraptionHitboxSwarm}/
 * {@code PlayerCarry} read to carry a player standing on the structure. Writing the position directly and
 * returning {@link Vec3#ZERO} would send stepKinematics down its "no movement" branch, which zeroes
 * lastDelta unconditionally — silently stranding every rider on a flying ghast. Yaw is likewise advanced
 * INSIDE {@link #tick} via a continuous {@code += delta} so {@code lastYawDelta} is derived the same way.
 *
 * <p><b>No perpendicular-axis mirror correction.</b> {@link MinecartFollowBehavior} carries a large
 * correction for vanilla's {@code OldMinecartBehavior} yaw quirk (a rail cart's {@code getYRot()} is a
 * flip-clamped travel direction, not a continuous heading). A ghast has no such quirk: it is an ordinary
 * mob whose {@code yRot} IS its continuous facing. Deliberately not copied over — but note that the quirk
 * is the ONLY thing that correction is entitled to explain, and its perpendicular-axis sign flip looks a
 * lot like it is also absorbing the conversion sign error {@link GhastHarness#contraptionYaw} documents.
 * Not investigated here; the minecart is out of scope and its behaviour is reported as correct.
 *
 * <p><b>The degrees-to-radians conversion is NOT negated</b>, unlike the one this class copied from the
 * minecart path — see {@link GhastHarness#contraptionYaw}, which derives the winding of both angle
 * conventions from vanilla's own look-vector math. The old negation inverted every turn ("al girar a la
 * izquierda giraba a la derecha el modelo"); it was invisible while the ghast held still because
 * {@link #yawOffset} silently absorbs any constant, and only a SIGN error survives that.
 *
 * <p><b>Yaw is tracked ABSOLUTELY, not integrated ("siempre debe seguir la vision del ghast").</b> The
 * contraption's target yaw is the ghast's live heading plus {@link #yawOffset}, the constant recorded when
 * the anchor was acquired — so whatever turns the ghast (a rider steering, vanilla's own look/body-rotation
 * control, its wander goal) the structure tracks it, because nothing but the ghast's absolute heading is
 * consulted. This deliberately mirrors {@link MinecartFollowBehavior#correctedYaw()}: both feed an absolute
 * TARGET into {@link #driveYawTowards}'s per-tick cap. Accumulating the ghast's per-tick yaw DELTAS instead
 * (what this class used to do) silently loses every radian the cap clips off — the target is rebuilt from
 * the next delta, never from the truth — so one sharp turn desynced the structure from the ghast's facing
 * PERMANENTLY. With an absolute target the cap can only ever lag, and only for as long as it takes to
 * catch up.
 *
 * <p><b>The offset is GIVEN, not inferred</b> (2026-07-16 — "a veces pasa que el contraption de un happy
 * ghast pierde su norte y al re abrir el sv se acomoda al norte del happy ghast"). {@link #yawOffset} is
 * constructed from persistence, exactly like {@link #anchorOffset} beside it, because it is exactly as
 * un-derivable from geometry — see {@link GhastHarness#captureYawOffset} for why measuring it against a
 * later heading is not an approximation but a redefinition, and {@code GhastContraptionType}'s
 * {@code ANCHOR_YAW} for where it is stored on both persistence paths (the ghast's PDC and the packed
 * harness item). The lazy first-tick measurement this class used to do survives ONLY for an assembly made
 * before that key existed, whose PDC/harness genuinely carries no offset and for which no correct value can
 * be recovered; it reproduces that assembly's pre-existing behaviour rather than inventing a new pose for
 * it.
 *
 * <p><b>Pivot: the ghast's centre.</b> The bearing origin is recomputed every tick by
 * {@link GhastHarness#bearingOrigin} rather than being the ghast's position plus a constant, which is what
 * moves the effective rotation pivot onto the ghast's middle without touching the render pipeline — see
 * that method for the derivation and for why {@link #anchorOffset}'s meaning (and every already-packed
 * harness) survives it unchanged. The pivot used to be the origin CELL, an arbitrary corner of whatever the
 * player glued, so the whole build swung around one of its own corners as the ghast turned.
 *
 * <p><b>Idle stillness is vanilla's, not ours.</b> See {@link #enforceStillness} — a ghast with people
 * standing on it is meant to hold position, and vanilla already implements that; we only widen the trigger
 * to the contraption's deck.
 *
 * <p><b>Scale inheritance.</b> {@code HappyGhast#sanitizeScale} clamps to {@code MAX_SCALE = 1.0F} and
 * {@code getAgeScale()} returns {@code BABY_SCALE = 0.2375F} for a baby / {@code 1.0F} for an adult, so
 * {@code LivingEntity#getScale()} (verified {@code public final float getScale()} against the mapped jar)
 * is the one authoritative number. It is re-read every tick rather than once at assembly because it is
 * genuinely mutable at runtime: the {@code scale} attribute can be changed, and a baby ghast growing up
 * fires {@code ageBoundaryReached} and jumps 0.2375 -> 1.0. Pushed through
 * {@link ContraptionEntity#setScale} (not {@code ContraptionState#setScale}) so the render-transform cache
 * is invalidated and the resize is actually resent; only on an actual change, since that call forces a full
 * re-render of every cell.
 *
 * <p><b>Anchor offset.</b> {@link #anchorOffset} is the vector from the ghast to the contraption's origin
 * cell recorded at capture time — see {@code GhastContraptionType}'s "Why the anchor offset is
 * per-contraption". It is no longer merely ADDED to the ghast's position: it is one input to
 * {@link GhastHarness#bearingOrigin}, which folds it into a bearing origin that orbits the ghast's centre
 * as the yaw changes. It therefore no longer cancels in the {@code now - last} deltas (a rotating anchor
 * offset is exactly how a turning ghast sweeps its structure around), so the sampled origin — not the raw
 * ghast position — is what {@link #lastOrigin} diffs. The first tick still emits the full absolute
 * correction (same technique and reason as {@link MinecartFollowBehavior}'s own first-tick branch: it makes
 * the fresh-assemble and the chunk-load rehydrate paths converge on the identical anchor regardless of what
 * the state was constructed with).
 *
 * <p><b>Dead-anchor safety.</b> Identical dwell-counter design to {@link MinecartFollowBehavior}: since
 * {@code Bukkit.getEntity(uuid)} returns {@code null} for both a DEAD ghast and one merely in an unloaded
 * chunk, only an anchor that stays unresolvable past {@link #ANCHOR_LOST_GRACE_TICKS} — having once been
 * acquired — latches {@link #wantsDisassembleInPlace()}, which {@code ContraptionEngine.tickAll} turns into
 * a safe disassemble-in-place rather than leaking a permanently stalled, anchorless contraption. A ghast
 * killed while nobody was near (so no {@code EntityDeathEvent} handler ran to completion) is caught here.
 */
public final class GhastFollowBehavior implements MovementBehavior {

    /**
     * See {@link MinecartFollowBehavior#MAX_YAW_STEP_RADIANS}'s reasoning, applied to a ghast: a mob's
     * yaw is driven by its look/body-rotation control and can jump in visibly chunky steps (a ridden
     * ghast snaps toward wherever the rider whips the camera). Snapping the whole contraption's yaw to
     * match instantly would read as a jarring rotation pop; capping how far this behavior advances the
     * contraption's yaw per tick keeps it a smooth continuous turn.
     */
    private static final double MAX_YAW_STEP_RADIANS = Math.toRadians(9.0);

    /** See {@link MinecartFollowBehavior#ANCHOR_LOST_GRACE_TICKS} — same 5s-at-20-TPS dwell, same rationale. */
    private static final int ANCHOR_LOST_GRACE_TICKS = 100;

    /** Ignore scale jitter below this; a real change (baby -> adult, or an attribute edit) is far larger. */
    private static final double SCALE_EPSILON = 1.0e-4;

    private final UUID entityId;
    /** Ghast position -> contraption origin cell, at capture. See the class javadoc's "Anchor offset". */
    private final Vec3 anchorOffset;
    private boolean missing;
    private int missingTicks;
    private boolean wantsDisassembleInPlace;
    private boolean hasLastSample;
    /**
     * The bearing origin this behavior derived last tick — the basis {@link #velocityThisTick} diffs
     * against. This tracks the ORIGIN rather than the ghast's raw position because the origin now moves
     * under rotation alone (a turning ghast sweeps its structure around its centre with the ghast itself
     * standing still), so a ghast-position diff would report no translation for a motion that genuinely
     * displaces every cell — and {@code lastDelta}, hence every standing rider, would be left behind.
     */
    private Vec3 lastOrigin = Vec3.ZERO;
    /**
     * {@code contraptionYaw - ghastYaw}, measured when the anchor was acquired and PERSISTED thereafter:
     * the constant that turns the ghast's absolute heading into the contraption's absolute target yaw. See
     * the class javadoc's "Yaw is tracked ABSOLUTELY" and "The offset is GIVEN, not inferred".
     */
    private double yawOffset;
    /** Whether {@link #yawOffset} is known yet — false only on the legacy path, see the class javadoc. */
    private boolean hasYawOffset;
    /** Hash of the cell SET observed last tick — see {@link #checkFracture}. */
    private int lastShapeHash;

    /**
     * Whether {@link #lastShapeHash} has been primed. A flag rather than a sentinel value, because no int
     * is available to spare: an empty set hashes to 0 and any set could hash to 0 by coincidence, so a
     * magic number here would silently mean "never fracture this contraption".
     */
    private boolean shapeObserved;
    private boolean wantsFractureCheck;
    private Vec3 pendingVelocity = Vec3.ZERO;

    /**
     * @param yawOffset the persisted {@link GhastHarness#captureYawOffset} for this assembly; empty ONLY
     *        for one assembled before that value was persisted, which falls back to measuring it on the
     *        first tick (see the class javadoc's "The offset is GIVEN, not inferred")
     */
    public GhastFollowBehavior(UUID entityId, Vec3 anchorOffset, OptionalDouble yawOffset) {
        this.entityId = entityId;
        this.anchorOffset = anchorOffset;
        this.hasYawOffset = yawOffset.isPresent();
        this.yawOffset = yawOffset.orElse(0.0);
    }

    /** The real anchor ghast's UUID — so a teardown that removes the whole contraption can also reach the anchor. */
    public UUID entityId() {
        return entityId;
    }

    /** This contraption's ghast-to-origin vector, so a save-to-item can persist it. See the class javadoc. */
    public Vec3 anchorOffset() {
        return anchorOffset;
    }

    /**
     * This contraption's yaw offset, so a save-to-item can persist it — the yaw twin of
     * {@link #anchorOffset()}, and the reason a packed harness comes back in the pose it was sheared in
     * rather than facing north. Empty only on the legacy path, before the first tick has measured one.
     */
    public OptionalDouble yawOffset() {
        return hasYawOffset ? OptionalDouble.of(yawOffset) : OptionalDouble.empty();
    }

    @Override
    public void tick(MovementContext ctx) {
        checkFracture(ctx);
        Entity entity = Bukkit.getEntity(entityId);
        if (entity == null || !entity.isValid()) {
            missing = true;
            pendingVelocity = Vec3.ZERO;
            if (hasLastSample && missingTicks < ANCHOR_LOST_GRACE_TICKS
                    && ++missingTicks >= ANCHOR_LOST_GRACE_TICKS) {
                wantsDisassembleInPlace = true;
            }
            return;
        }
        missing = false;
        missingTicks = 0;

        // A ghast keeps its UUID across a vanilla portal crossing exactly as a minecart does, so the same
        // re-anchor path applies — see MinecartFollowBehavior's "Portal crossing" javadoc for the full
        // design (owning facade recovered via ContraptionWorlds' reverse index; no ContraptionEntity
        // reference needs threading through MovementContext).
        if (!((org.bukkit.craftbukkit.CraftWorld) entity.getWorld()).getHandle().dimension().equals(ctx.state().worldId())) {
            ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
            if (facade != null) {
                Vec3 origin = originOf(entity, ctx.state().yawRadians());
                facade.teleport(entity.getWorld(), origin.x, origin.y, origin.z, ctx.state().yawRadians());
            }
            hasLastSample = false;
            pendingVelocity = Vec3.ZERO;
            return;
        }

        syncScale(ctx, entity);
        enforceStillness(ctx, entity);

        float ghastYawDegrees = entity.getLocation().getYaw();

        if (!hasLastSample) {
            hasLastSample = true;
            if (!hasYawOffset) {
                // Legacy assembly only — see the class javadoc's "The offset is GIVEN, not inferred".
                yawOffset = GhastHarness.captureYawOffset(ctx.state().yawRadians(), ghastYawDegrees);
                hasYawOffset = true;
            }
            // SNAP the yaw rather than driving it. MAX_YAW_STEP_RADIANS exists to smooth a live turn, and
            // this is not one: the state was just CONSTRUCTED (a rehydrate and a restore-from-item both
            // build it at yaw 0, since a capture stores yaw-0 local cells), so the whole offset from the
            // pose it is supposed to already be in shows up here at once. Capping it would spin the
            // structure into place over several seconds instead of it simply being where it was. This is
            // the yaw twin of the full absolute position correction below, and for the same reason: it
            // makes the fresh-assemble and the rehydrate/restore paths converge on the identical pose
            // regardless of what the state was constructed with. At a fresh assemble the offset was
            // measured against this same state's yaw, so this is exactly a no-op.
            ctx.state().setYawRadians(GhastHarness.followTargetYaw(yawOffset, ghastYawDegrees));
            lastOrigin = originOf(entity, ctx.state().yawRadians());
            // Full absolute correction as this tick's delta — see class javadoc's "Anchor offset".
            pendingVelocity = lastOrigin.subtract(new Vec3(ctx.state().x(), ctx.state().y(), ctx.state().z()));
            return;
        }

        // Yaw FIRST, then sample the origin at that new yaw: the origin orbits the ghast's centre as a
        // function of yaw, so reading it before the turn would report a translation for last tick's pose.
        // stepKinematics derives lastYawDelta from the yaw written here and lastDelta from the velocity
        // returned below, and the carry math reconstructs the previous tick's transform as
        // (bearing - lastDelta, yaw - lastYawDelta) — which is only the true previous transform if both
        // come from the same pose, i.e. this order.
        driveYawTowards(ctx, GhastHarness.followTargetYaw(yawOffset, ghastYawDegrees));
        Vec3 origin = originOf(entity, ctx.state().yawRadians());
        pendingVelocity = origin.subtract(lastOrigin);
        lastOrigin = origin;
    }

    /** This contraption's bearing origin for the ghast's live pose at {@code yawRadians} — see {@link GhastHarness#bearingOrigin}. */
    private Vec3 originOf(Entity entity, double yawRadians) {
        Location loc = entity.getLocation();
        return GhastHarness.bearingOrigin(new Vec3(loc.getX(), loc.getY(), loc.getZ()), centreHeightOf(entity),
                anchorOffset, yawRadians);
    }

    /**
     * How far the ghast's centre sits above its position. A {@code HappyGhast} is
     * {@code EntityType.HAPPY_GHAST.sized(4.0F, 4.0F)} with its position at its FEET (verified against the
     * mapped jar), so this is {@code 2.0} for an unscaled adult — but it is read from the LIVE bounding box
     * rather than assumed, because the box tracks {@code getScale()} and a scaled ghast's centre is
     * genuinely somewhere else.
     */
    private static double centreHeightOf(Entity entity) {
        return ((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle().getBoundingBox().getYsize() / 2.0;
    }

    /** Mirrors the ghast's live {@code LivingEntity#getScale()} onto the contraption — see class javadoc's "Scale inheritance". */
    private void syncScale(MovementContext ctx, Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        double ghastScale = ((org.bukkit.craftbukkit.entity.CraftLivingEntity) living).getHandle().getScale();
        // Compare against what setScale would ACTUALLY store, not the raw ghast value. ContraptionState
        // clamps to [MIN_SCALE, MAX_SCALE], so a ghast below MIN_SCALE (reachable via the `scale`
        // attribute — vanilla's own sanitizeScale only caps the TOP at 1.0) would never satisfy an
        // equality check against the stored value: every tick would see a difference, re-push the same
        // clamped number, and force a full re-render of every cell forever. Clamping first makes the
        // comparison a fixed point.
        ghastScale = Math.max(ContraptionState.MIN_SCALE,
                Math.min(ContraptionState.MAX_SCALE, ghastScale));
        if (Math.abs(ghastScale - ctx.state().scale()) < SCALE_EPSILON) {
            return;
        }
        ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
        if (facade != null) {
            facade.setScale(ghastScale); // invalidates the render-transform cache, unlike state().setScale
        } else {
            ctx.state().setScale(ghastScale);
        }
    }

    /**
     * Holds the ghast still while people stand on its CONTRAPTION and nobody is riding it ("si hay usuarios
     * parados en el ghast o el contraption y nadie lo controla, el ghast este debe quedarse quieto como
     * normal").
     *
     * <h2>Why the ghast drifts, and why none of this is our own physics</h2>
     * Vanilla already implements the whole behaviour, and correctly — decompiled from the mapped jar:
     * <ul>
     *   <li>{@code HappyGhast#tick} calls {@code scanPlayerAboveGhast()} and, when it hits,
     *   {@code setServerStillTimeout(10)}.</li>
     *   <li>{@code isOnStillTimeout()} is {@code staysStill() || serverStillTimeout > 0}.</li>
     *   <li>{@code adultGhastSetup} builds {@code new Ghast.GhastMoveControl(this, true, this::isOnStillTimeout)}
     *   (confirmed via the class's BootstrapMethods table), and that control's {@code tick} calls
     *   {@code stopInPlace()} every tick the supplier is true — so the wander goal cannot move it.</li>
     *   <li>{@code getControllingPassenger()} returns null while {@code isOnStillTimeout()}, so a still ghast
     *   is also un-steerable.</li>
     * </ul>
     * The bug is the TRIGGER's reach, not the mechanism. {@code scanPlayerAboveGhast} tests an AABB that is
     * only the ghast's own box inflated by 1 on X/Z and spanning {@code [maxY - 1e-5, maxY + ysize/2]} — the
     * ghast's "hat", nothing more. Standing on a harness contraption means standing on a deck the player
     * glued wherever they liked: below the ghast, beside it, or more than half a ghast-height above it. None
     * of that is in the scan box, so the timeout never fires, {@code RandomFloatAroundGoal} keeps wandering,
     * and the structure drifts with its anchor. (The contraption is NOT the culprit: {@code ContraptionState
     * #anchorEntityId} does exempt the anchor ghast from both {@code ContraptionHitboxSwarm#carryNearbyEntities}
     * and {@code #pushBackNearbyEntities}, so we never push it — verified.)
     *
     * <p>So this only widens the trigger, reusing vanilla's own lever: whichever players the hitbox swarm
     * already tracks as standing on the footprint feed the SAME {@code setServerStillTimeout(10)} the hat-scan
     * feeds. It self-decays — {@code tick} counts it down and re-syncs — so simply not calling this releases
     * the ghast within 10 ticks, and nothing needs cleaning up on teardown.
     *
     * <h2>The two exclusions, both vanilla's</h2>
     * <b>Riders.</b> {@code scanPlayerAboveGhast} skips any player whose {@code getRootVehicle()} is a
     * {@code HappyGhast}, which is what stops the rule from deadlocking: a rider sits inside the scan box, so
     * counting them would make the ghast still, which nulls {@code getControllingPassenger()}, which keeps it
     * still — nobody could ever fly it. The same trap is live here (a rider stands on the deck's cells too),
     * so this checks {@code getFirstPassenger() instanceof Player} — the ONLY non-circular test available,
     * since {@code getControllingPassenger()} is itself gated on {@code isOnStillTimeout()} and would answer
     * "nobody is controlling it" the instant we made it still. That check is also literally the user's "y
     * nadie lo controla": someone aboard means hands on the wheel, and we stay out of the way.
     *
     * <p><b>Spectators.</b> Skipped for the same reason vanilla skips them — a spectator is not standing on
     * anything, they are flying through it.
     */
    private void enforceStillness(MovementContext ctx, Entity entity) {
        if (SET_SERVER_STILL_TIMEOUT == null) {
            return;
        }
        if (!(((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle() instanceof HappyGhast ghast)) {
            return;
        }
        if (ghast.getFirstPassenger() instanceof net.minecraft.world.entity.player.Player) {
            return; // being ridden — see "The two exclusions"
        }
        ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
        if (facade == null || !anyStandingOnDeck(facade.currentRiderIds())) {
            return;
        }
        try {
            SET_SERVER_STILL_TIMEOUT.invoke(ghast, STILL_TIMEOUT_TICKS);
        } catch (ReflectiveOperationException ignored) {
            // best-effort — a ghast that keeps wandering is far better than a broken follow tick
        }
    }

    /**
     * Whether any of {@code riderIds} — the hitbox swarm's own standing-on-the-footprint set, so the
     * footprint math is never duplicated here — is a real, non-spectator player.
     */
    private static boolean anyStandingOnDeck(Set<UUID> riderIds) {
        for (UUID id : riderIds) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(id);
            if (player != null && player.getGameMode() != GameMode.SPECTATOR) {
                return true;
            }
        }
        return false;
    }

    /** Exactly what {@code HappyGhast#tick}'s own hat-scan sets, so a deck-stander and a hat-stander decay identically. */
    private static final int STILL_TIMEOUT_TICKS = 10;

    /**
     * {@code HappyGhast#setServerStillTimeout(int)} — private, and the only entry point that both stores the
     * timeout and runs {@code syncStayStillFlag()} (which is what actually drives {@code staysStill()} onto
     * the client) plus the position resync vanilla pairs with it. There is no Bukkit surface for it:
     * {@code CraftHappyGhast} adds nothing to {@code CraftAnimals} (verified via javap). Left null rather
     * than fatal if a future mapping change loses it — the contraption keeps flying, it just wanders again.
     */
    private static final Method SET_SERVER_STILL_TIMEOUT;

    static {
        Method method = null;
        try {
            method = HappyGhast.class.getDeclaredMethod("setServerStillTimeout", int.class);
            method.setAccessible(true);
        } catch (ReflectiveOperationException | RuntimeException e) {
            CraftEnginePolyfills.instance().getLogger().warning(
                    "[Contraption] HappyGhast#setServerStillTimeout is unavailable; a harnessed ghast will"
                            + " wander while players stand on its contraption: " + e);
        }
        SET_SERVER_STILL_TIMEOUT = method;
    }

    /** Advances {@code ContraptionState}'s yaw toward {@code targetYaw} by at most one capped step, along the shortest arc. */
    private void driveYawTowards(MovementContext ctx, double targetYaw) {
        double need = GhastHarness.shortestAngleDelta(ctx.state().yawRadians(), targetYaw);
        double step = Math.max(-MAX_YAW_STEP_RADIANS, Math.min(MAX_YAW_STEP_RADIANS, need));
        if (step == 0) {
            return;
        }
        ctx.state().setYawRadians(ctx.state().yawRadians() + step);
    }

    /**
     * Latches a fracture check whenever this contraption's cell set changes (2026-07-16 — "haz que si en un
     * contraption del tipo ghast un bloque deja de estar conectado a cualquier otro, le ocurra lo de
     * convertirse en phys"). Only latched here; {@code ContraptionEngine.tickAll} polls it and is the only
     * thing that may act on it, because splitting REGISTERS new contraptions and a behavior cannot mutate
     * {@code ContraptionManager}'s set while the engine is iterating it.
     *
     * <p>A cell-count change is the same trigger {@code PhysicsWorld#stepAll} uses for a phys body, for the
     * same reason: it is cheap, and {@code ContraptionSplitter#splitIfDisconnected} is itself cheap and
     * idempotent for a body that turns out to still be connected.
     *
     * <p><b>The first observation only primes the baseline, and never fractures.</b> That is a deliberate
     * difference from the phys path, whose {@code Entry#lastCellCount} starts at -1 and therefore
     * flood-fills the tick a body is assembled. A ghast contraption must not: glue is not adjacency (see
     * {@code ContraptionSplitter}'s "Why adjacency, and explicitly NOT the glue graph"), so a build the
     * player legitimately glued across a gap would shatter the instant it was assembled onto the ghast.
     * Only a change to a structure this behavior has already seen intact counts as a fracture.
     */
    private void checkFracture(MovementContext ctx) {
        if (ctx.state().level() == null) {
            return;
        }
        // The cell SET's shape, not its size. A piston inside the contraption is the case that matters and
        // the case a count cannot see: pushing a block removes one cell and adds another, so the count is
        // IDENTICAL while the structure has genuinely changed — and that is precisely the move that
        // detaches a block ("yo empuje la tnt con un piston cuando ya estaba armado el contraption"). A
        // count-only check watched the one number a piston is guaranteed not to alter, so a pushed-off
        // block hung in the air forever.
        int shape = ctx.state().level().localPositions().hashCode();
        if (shapeObserved && shape != lastShapeHash) {
            wantsFractureCheck = true;
        }
        lastShapeHash = shape;
        shapeObserved = true;
    }

    /**
     * Whether this contraption's cells changed since the last poll and it may therefore have fractured —
     * see {@link #checkFracture}. Consuming (clears the latch) so one change is only ever acted on once.
     */
    public boolean consumeFractureCheck() {
        boolean want = wantsFractureCheck;
        wantsFractureCheck = false;
        return want;
    }

    @Override
    public boolean isStalled() {
        return missing;
    }

    /** See {@link MinecartFollowBehavior#wantsDisassembleInPlace()} — same latch, same polling by {@code ContraptionEngine.tickAll}. */
    public boolean wantsDisassembleInPlace() {
        return wantsDisassembleInPlace;
    }

    @Override
    public Vec3 velocityThisTick() {
        return pendingVelocity;
    }
}
