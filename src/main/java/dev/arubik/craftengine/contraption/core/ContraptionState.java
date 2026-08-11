package dev.arubik.craftengine.contraption.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.contraption.ContraptionPushSettings;
import dev.arubik.craftengine.contraption.MovementBehavior;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;

/**
 * The real owner of one contraption's data (CONTRAPTIONS.md §1 "Entity/data separation").
 * {@link render.ContraptionDisplaySwarm}/hitbox swarms and the master clock all read from
 * here; {@link ContraptionManager} is the UUID-keyed registry that holds these. The
 * captured structure's blocks/BE-data live in {@link #level} ({@link ContraptionLevel}),
 * NOT here — this class owns kinematics (position/yaw/stall/behaviors) and keeps the
 * level's live transform in sync every time {@link #setPosition}/{@link #setYawRadians} is
 * called, so {@code ContraptionLevel}'s real-world-reference methods always translate
 * against the contraption's current position.
 *
 * <p>{@code level} is null-tolerant: production code (capture) always provides a real
 * {@link ContraptionLevel}, but a real one can't be constructed without a live Bukkit
 * server (it's a genuine {@code net.minecraft.server.level.ServerLevel} subclass — see that
 * class's constructor). Pure unit tests that only care about kinematics/stall-gate math or
 * the manager registry pass {@code null} here to stay testable without a live server.
 */
public final class ContraptionState {

    private final UUID id;

    /**
     * Which NMS dimension this contraption is anchored in. {@code ContraptionEngine.tickAll}
     * re-resolves the ServerLevel every tick to find the {@link ContraptionLevel}'s
     * projection target. Historically {@code final} (a contraption never left its birth world), but
     * roadmap item #1 (cross-world teleport / minecart portal crossing, see
     * {@code .migration/ROADMAP-world-boundary.md} §1) needs it reassignable so a live contraption can
     * be re-anchored into a different world. Only ever changed by {@link #setAnchor} on the server main
     * thread; once mutable, the engine follows automatically (it reads this field fresh each tick).
     * Unchanged for any contraption that never teleports.
     *
     * <p>Migration note (2026-08-10): changed from Bukkit UUID to NMS ResourceKey to keep ContraptionType
     * API purely NMS-based (no Bukkit dependencies). Conversion: Bukkit World → ResourceKey via
     * {@code ((CraftWorld) bukkitWorld).getHandle().dimension()}, ResourceKey → ServerLevel via
     * {@code server.getLevel(resourceKey)}.
     */
    private ResourceKey<Level> worldId;

    private final ContraptionLevel level;

    /** Bearing's continuous world-space position (Phase 2: stationary, so this never changes). */
    private double x, y, z;

    private double yawRadians;

    /** Anchor offset for entity-anchored types (ghast, minecart). Vec3 from anchor to origin. */
    private net.minecraft.world.phys.Vec3 anchorOffset = net.minecraft.world.phys.Vec3.ZERO;

    /** Yaw offset for entity-anchored types. Contraption yaw minus anchor yaw at capture time. */
    private java.util.OptionalDouble yawOffset = java.util.OptionalDouble.empty();

    /**
     * PITCH rotation (radians) about a HORIZONTAL axis through the bearing pivot (roadmap item #9 phase 5 —
     * PhysContraption TIPPING). The vertical twin of {@link #yawRadians}: where yaw spins the structure about
     * the vertical Y axis, pitch TIPS it about a horizontal axis (see {@code ContraptionMath#rotatePitch} for
     * the exact axis/sign, and {@code ContraptionMath#renderPosition}'s {@code yaw ∘ pitch} composition). A
     * body that is never tipped keeps this at exactly {@code 0}, so every pitch-0 fast path across the
     * transform/render/collision pipeline is taken and the contraption behaves byte-for-byte as it did before
     * pitch existed (bearings, minecart, and any driven contraption never set it). Kept in sync onto the hidden
     * {@link #level} exactly like {@link #yawRadians} — see {@link #setPitchRadians}.
     */
    private double pitchRadians;

    /**
     * ROLL rotation (radians) about a HORIZONTAL axis (Z) through the bearing pivot (roadmap item #9 phase 6 —
     * the horizontal twin of {@link #pitchRadians}). Where pitch tips the body about X (a lean along Z), roll
     * tips it about Z (a lean along X); with BOTH the body can lean in any horizontal direction toward its heavy
     * side (see {@code ContraptionMath#rotateRoll} for the exact axis/sign and {@code renderPosition}'s
     * {@code yaw ∘ pitch ∘ roll} composition). A body that never rolls keeps this at exactly {@code 0}, so every
     * roll-0 fast path across the transform/render/collision pipeline is taken and the contraption behaves
     * byte-for-byte as it did before roll existed (bearings, minecart, and any driven contraption never set it).
     * Kept in sync onto the hidden {@link #level} exactly like {@link #pitchRadians} — see {@link #setRollRadians}.
     */
    private double rollRadians;

    /**
     * Uniform SCALE factor of the whole contraption (roadmap item #9 — per-contraption {@code scale}
     * transform for a "creative phys wand" and scale-aware rendering/collision). {@code 1.0} means "no
     * scaling, exactly as before this field existed"; values in {@code [0.1, 10.0]} (the {@link #MIN_SCALE}/
     * {@link #MAX_SCALE} clamp enforced by {@link #setScale}) render/collide/persist the structure that many
     * times bigger or smaller ABOUT its bearing pivot (see {@code ContraptionMath#renderPosition}'s
     * scale-about-pivot math). Kept in sync onto the hidden {@link #level} exactly like {@link #yawRadians}/
     * {@link #pitchRadians} — see {@link #setScale}. A contraption that is never scaled keeps this at exactly
     * {@code 1.0}, in which case every scale-1 fast path across the transform/render/collision/persistence
     * pipeline is taken and the contraption behaves byte-for-byte as it did before scaling existed.
     */
    private double scale = 1.0;

    /** Minimum permitted {@link #scale}. */
    public static final double MIN_SCALE = 0.1;

    /** Maximum permitted {@link #scale}. */
    public static final double MAX_SCALE = 10.0;

    private boolean stalled;

    private final List<MovementBehavior> behaviors = new ArrayList<>();

    private List<ContraptionFurniture> furniture = List.of();

    /**
     * Per-contraption entity-collision push/carry tuning (see {@link ContraptionPushSettings}),
     * read by the hitbox swarm's pushback resolution. Defaults to
     * {@link ContraptionPushSettings#DEFAULT} (behavior identical to before this setting existed);
     * never {@code null} (see {@link #setPushSettings}).
     */
    private ContraptionPushSettings pushSettings = ContraptionPushSettings.DEFAULT;

    /** World-space delta actually applied last tick (zero while stalled), fed to {@code PlayerCarry} by the hitbox swarm. */
    private double lastDeltaX, lastDeltaY, lastDeltaZ;

    /**
     * Yaw ROTATION (radians) actually applied last tick (2026-07-03 session — "rotation carry ...
     * falla en el rotation bearing"). A pure ROTATIONAL bearing spins in place, so
     * {@link #lastDeltaX}/Y/Z stay zero even though every off-axis cell (and any rider standing on
     * one) sweeps a real arc — the standing-carry needs this rotational component too, not just the
     * translational delta. Also drives the seat's "rotate the player's own view with the
     * contraption" behavior in {@code ContraptionEntity#carrySeatedRiders}.
     */
    private double lastYawDelta;

    /**
     * PITCH rotation (radians) actually applied last tick — the vertical twin of {@link #lastYawDelta}
     * (roadmap item #9 phase 5 — TIPPING). A body that TIPS (pitches about a horizontal axis) sweeps every
     * off-axis cell through a real arc even when {@link #lastDeltaX}/Y/Z stay zero, exactly as a pure yaw spin
     * does — so the standing/seat carry needs this rotational component too. Recorded by
     * {@code ContraptionEngine#stepKinematics} from the pitch change a behavior made this tick (mirroring the
     * {@link #lastYawDelta} handling), and left at exactly {@code 0} for any contraption that never tips.
     */
    private double lastPitchDelta;

    /**
     * ROLL rotation (radians) actually applied last tick — the horizontal twin of {@link #lastPitchDelta}
     * (roadmap item #9 phase 6 — ROLL). A body that LEANS via roll (tips about Z) sweeps every off-axis cell
     * through a real arc even when {@link #lastDeltaX}/Y/Z stay zero, exactly as a pitch tip or yaw spin does —
     * so the standing/seat carry needs this rotational component too. Recorded by
     * {@code ContraptionEngine#stepKinematics} from the roll change a behavior made this tick (mirroring the
     * {@link #lastPitchDelta} handling), and left at exactly {@code 0} for any contraption that never rolls.
     */
    private double lastRollDelta;

    /**
     * The RPM the assembled bearing is currently pulling from a REAL adjacent motor and
     * feeding "globally" to every captured block in this contraption (Task 4, CONTRAPTIONS.md
     * 2026-07-01 session) — see {@code behavior.RotationalBearingBehavior}/
     * {@code behavior.LinearActuatorBehavior}, which set this every tick via
     * {@code behavior.RealMotorLink}. Zero when no real motor is adjacent (or the bearing type
     * doesn't produce rotation at all, e.g. MINECART).
     *
     * <p><b>Local-over-global priority</b>: individual captured {@code RpmConsumer}s (e.g.
     * {@code MinerBehavior}) should prefer their OWN locally-fed rpm (via
     * {@code RpmConsumer#setInputRpm}, e.g. from a real motor captured adjacent to them inside
     * the same {@code ContraptionLevel}) over this global feed, and only fall back to
     * {@link #globalRpm()} when their local input is 0 — see each consumer's own
     * {@code tick()} for exactly how it's combined.
     */
    private float globalRpm;

    /**
     * This tick's accumulated stress-unit (SU) demand from every captured consumer that
     * actually operated this tick (Task 4/5 follow-up, CONTRAPTIONS.md 2026-07-01 session —
     * "rpm and su are finite resources... the contraption should stop or reduce speed until
     * it can work"). Reset once per engine tick by {@code ContraptionEngine.stepKinematics}
     * BEFORE any behavior ticks ({@link #resetSuDemand()}); every {@code RpmConsumer} behavior
     * (e.g. {@code MinerBehavior}) that spends power this tick calls {@link #addSuDemand} with
     * its own SU cost. Because a bearing behavior is always appended to
     * {@code behaviors()} AFTER every auto-captured consumer (see
     * {@code ContraptionAssembler#assemble}), the bearing (whichever of
     * {@code RotationalBearingBehavior}/{@code LinearActuatorBehavior} is attached) always
     * ticks LAST within the same pass and therefore sees the FULL total demand for this tick
     * before it reports load to the real external motor via {@code RpmProvider#reportStressLoad}
     * — that's how "reduce speed until it can work" is actually enforced: the real motor's own
     * overstress logic (not this class's) decides to reduce/zero {@code RpmProvider#getRpm()}
     * once the reported load exceeds {@code stressCapacity()}, and the bearing reads back
     * whatever rpm the motor actually delivers afterward (see that class's own {@code tick()}).
     *
     * <p><b>Internal-motor case, honestly scoped as incomplete</b>: this only closes the loop
     * for the EXTERNAL "bearing pulls from a real adjacent motor" path. An INTERNAL rpm/su net
     * (a real motor CAPTURED inside the same {@code ContraptionLevel}, feeding local consumers
     * directly via their own adjacency scan) is not implemented — see
     * {@code RotationalBearingBehavior}'s "documented simplification" javadoc for why a real
     * in-{@code ContraptionLevel} adjacency network was already out of scope before this
     * session; the finite-budget enforcement above only applies to the one real network path
     * (bearing &lt;-&gt; external motor) that actually exists.
     */
    private float suDemand;

    /**
     * Task 1 (CONTRAPTIONS.md 2026-07-01 session — furniture seats): real players carried at a
     * FIXED bearing-relative local offset (yaw-0 basis) — see
     * {@code ContraptionFurnitureCapture#captureNear}'s javadoc for how these get populated (a
     * real seat a player was sitting in at assembly time) and {@code ContraptionEntity#carrySeatedRiders}
     * for the per-tick reposition of each rider's real mount entity (see {@link #seatedRiderMount}).
     *
     * <p><b>Real vehicle-mounting (2026-07-02 session, seat-only rework — "es para el sistema de
     * seat de los furniture").</b> Unlike the general standing/walking carry case ({@code
     * PlayerCarry}, explicitly velocity-based and out of scope here), a seat is a genuine
     * "sit down and lock" interaction — exactly what CraftEngine's own furniture seats already do
     * via a real vanilla passenger relationship (see {@code BukkitSeat}/{@code BukkitSeatManager}
     * in CraftEngine's own source). This offset map is now paired with {@link #seatedRiderMount},
     * which tracks the REAL invisible {@code ArmorStand} entity each rider is a genuine vanilla
     * passenger of — repositioning that entity every tick (not the player) is what actually moves
     * the rider, via vanilla's own passenger-follows-vehicle mechanics.
     */
    private final Map<UUID, Vec3> seatedRiders = new HashMap<>();
    /**
     * Real mount entity UUID per seated rider (2026-07-02 session, real vehicle-mounting rework)
     * — a small invisible/no-AI/no-gravity {@code ArmorStand} spawned in the REAL world at the
     * seat's current real-world position, whose sole job is to carry the player as a genuine
     * vanilla passenger (see {@link #seatedRiders}' javadoc). {@code ContraptionEntity
     * #carrySeatedRiders} repositions this entity every tick to track the contraption's current
     * transform; moving it moves the mounted player automatically. Removed (and the passenger
     * relationship released) on dismount/despawn.
     */
    private final Map<UUID, UUID> seatedRiderMount = new HashMap<>();

    /**
     * The bearing's real-world position AT CAPTURE TIME, fixed forever — {@link #x}/{@link #y}/
     * {@link #z} are the LIVE (moving) position, mutated every tick by {@link #setPosition}.
     * Needed to translate {@link GlueGraph}/{@link GlueRegistry} entries (keyed by ORIGINAL
     * absolute {@link net.minecraft.core.BlockPos}) to wherever the contraption actually ends
     * up when disassembled — see {@code ContraptionAssembler#disassemble}'s use of this.
     */
    private final double originX, originY, originZ;

    /**
     * The player who assembled/owns this contraption, or {@code null} for one with no owning
     * player (a redstone/euler-driven piston, or anything assembled without a triggering click)
     * — roadmap item #7 "Claims / protection system" Phase C1 (ownership plumbing,
     * {@code .migration/ROADMAP-claims-and-phys.md} §1). The design's core gap was that "the
     * current code has no concept of an owning player past the assembly click": movement and
     * mining happen in {@code ContraptionEngine.tickAll} with no player in scope, so the
     * protection checks had nobody to attribute an autonomous action to. This field closes that
     * gap — it is set at every assemble site that has a triggering player (see
     * {@code BearingHammerListener} / {@code ContraptionAssembler#assemble} /
     * {@code MinecartBearing#assemble}) and read by {@code protection.ContraptionMoveGuard}
     * (per-tick move gate), {@code behavior.MinerBehavior} (drill-into-claim gate), and
     * {@code protection.ContraptionOwnership} (anti-theft interact/disassemble).
     *
     * <p><b>Phase-1 persistence limitation (documented, intentional).</b> {@code owner} is
     * IN-MEMORY ONLY for this phase — it is NOT written to {@code MinecartBearing}'s entity PDC
     * nor to {@code BlockAnchoredContraptionStore}'s on-disk manifest, so a contraption that
     * survives a chunk unload / server restart rehydrates with {@code owner == null} and reverts
     * to unowned (region/theft checks fall back to allow, exactly as today). Persisting the owner
     * across restart is a Phase-C1 follow-up (it needs new fields threaded through both stores'
     * NBT schemas); kept out of this phase to stay strictly additive and behavior-preserving.
     */
    private UUID owner;

    /**
     * Boundary-crossing throttle cache for {@code protection.ContraptionMoveGuard}. The packed
     * ({@link net.minecraft.core.BlockPos#asLong}) snapped bearing cell of the last region check,
     * paired with {@link #lastRegionAllowed}. The move gate re-queries the protection chain only
     * when this changes (a large contraption drifting within one block reuses the cached verdict
     * instead of hammering a claim plugin every tick). {@code Long.MIN_VALUE} = "never checked".
     */
    private long lastRegionCheckKey = Long.MIN_VALUE;

    /** The cached move-gate verdict for {@link #lastRegionCheckKey}. Defaults to allowed (no check yet). */
    private boolean lastRegionAllowed = true;

    /**
     * The kind of contraption this was assembled as — its own identity, independent of the
     * world.
     *
     * <p>Both persistence paths ({@code CraftEnginePolyfills}'s shutdown save and
     * {@code ContraptionChunkLifecycleListener}'s unload save) used to re-derive this by reading the
     * anchor block back out of the real world via {@code BearingBlockBehavior#typeAt}, defaulting to
     * rotational when that found nothing. That is sound for LINEAR/ROTATIONAL,
     * whose bearing block stays put in the world and pins the structure to it.
     *
     * <p>It is wrong for PHYS, and silently so: a phys contraption has no pinning
     * bearing, its anchor block is captured INTO the structure, and the body then FALLS AWAY from the
     * anchor coordinates. By save time the real world at the anchor position is ordinary air, so the
     * lookup returns null and every phys contraption was persisted as ROTATIONAL — coming back after a
     * restart as a spinning bearing instead of a falling body.
     *
     * <p>Storing the type here removes the dependency on a block that is not required to still be
     * there. {@code null} only for a state built by a path that predates this field (or a pure-JVM
     * test), in which case callers fall back to the old block lookup.
     */
    private Key bearingType;

    /**
     * The real entity this contraption is anchored to and driven by (2026-07-16 — "self block of this
     * contraption should not affect the same ghast"), or {@code null} for a block-anchored one.
     *
     * <p>A harnessed ghast flies INSIDE the structure it carries, which makes it the one entity the
     * structure must never collide with: it is not a bystander in the contraption's path, it IS the
     * contraption's path. Left un-excluded, {@code ContraptionHitboxSwarm}'s solid pushback would eject
     * it out of its own walls every tick and the carry would then re-apply the structure's delta on top
     * of the ghast's own movement, doubling it. Recorded here rather than derived from the anchoring
     * {@link MovementBehavior} so {@link ContraptionEntity}'s collision passes can read it without
     * knowing which bearing type built them.
     */
    private UUID anchorEntityId;

    /**
     * Constructs a new contraption state at the given position.
     *
     * @param id unique contraption identifier
     * @param worldId NMS dimension key this contraption is anchored in
     * @param level the captured structure's blocks/BE-data, or null for pure-kinematics tests
     * @param x bearing world X
     * @param y bearing world Y
     * @param z bearing world Z
     */
    public ContraptionState(UUID id, ResourceKey<Level> worldId, ContraptionLevel level, double x, double y, double z) {
        this.id = id;
        this.worldId = worldId;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        if (level != null) {
            // Birth pose — pushed through the CANONICAL full-pose overload (see setPosition's javadoc for
            // why every transform push in this class must carry all seven components). A fresh state is
            // flat and un-scaled, so this is arithmetically the old 5-arg call plus an explicit
            // roll=0/scale=1 affirmation.
            level.setTransform(x, y, z, 0.0, 0.0, 0.0, this.scale);
        }
    }

    /**
     * Returns the contraption type this was assembled as, or {@code null} if never recorded.
     *
     * @return contraption type Key or null
     */
    public Key bearingType() {
        return bearingType;
    }

    /**
     * Records the contraption type. Called at assemble and at rehydrate, where the type is authoritatively known.
     *
     * @param bearingType contraption type Key to record
     */
    public void setBearingType(Key bearingType) {
        this.bearingType = bearingType;
    }

    /**
     * Returns the anchor offset for entity-anchored types (ghast, minecart). Vec3 from anchor to origin.
     *
     * @return anchor offset
     */
    public net.minecraft.world.phys.Vec3 anchorOffset() {
        return anchorOffset;
    }

    /**
     * Returns the yaw offset for entity-anchored types. Contraption yaw minus anchor yaw at capture time.
     *
     * @return yaw offset or empty
     */
    public java.util.OptionalDouble yawOffset() {
        return yawOffset;
    }

    /**
     * Blast immunity 0..1: 0 = cells break normally, 1 = never break (only pushed), between = partial (the carve
     * scales the break threshold by {@code 1 - explosionProof}). A bearing-config float.
     */
    private double explosionProof;

    /**
     * Returns blast immunity 0..1. 0 = breakable, 1 = immune, partial in between. Default 0.
     *
     * @return explosion proof value
     */
    public double explosionProof() {
        return explosionProof;
    }

    /**
     * Sets blast immunity (clamped 0..1). Recorded at assemble (from the bearing's config), restored on rehydrate.
     *
     * @param explosionProof immunity value to set
     */
    public void setExplosionProof(double explosionProof) {
        this.explosionProof = Math.max(0.0, Math.min(1.0, explosionProof));
    }

    /**
     * Returns the anchor entity excluded from this contraption's own collision, or {@code null}.
     *
     * @return anchor entity UUID or null
     */
    public UUID anchorEntityId() {
        return anchorEntityId;
    }

    /**
     * Records the anchor entity. Called at assemble and at rehydrate, where the anchor is known.
     *
     * @param anchorEntityId entity UUID to record
     */
    public void setAnchorEntityId(UUID anchorEntityId) {
        this.anchorEntityId = anchorEntityId;
    }

    /**
     * Returns the unique contraption identifier.
     *
     * @return contraption UUID
     */
    public UUID id() {
        return id;
    }

    /**
     * Returns the NMS dimension key this contraption is anchored in.
     *
     * @return world resource key
     */
    public ResourceKey<Level> worldId() {
        return worldId;
    }

    /**
     * Returns the captured structure's blocks/BE-data, or null for pure-kinematics tests.
     *
     * @return contraption level or null
     */
    public ContraptionLevel level() {
        return level;
    }

    /**
     * Returns the bearing's real-world position at capture time, fixed forever.
     *
     * @return origin block position
     */
    public net.minecraft.core.BlockPos originBearingBlockPos() {
        return net.minecraft.core.BlockPos.containing(originX, originY, originZ);
    }

    /**
     * Returns the bearing's continuous world-space X position.
     *
     * @return world X
     */
    public double x() {
        return x;
    }

    /**
     * Returns the bearing's continuous world-space Y position.
     *
     * @return world Y
     */
    public double y() {
        return y;
    }

    /**
     * Returns the bearing's continuous world-space Z position.
     *
     * @return world Z
     */
    public double z() {
        return z;
    }

    /**
     * Moves the bearing to a new continuous world position and pushes the <b>COMPLETE</b> current pose
     * (position + yaw + pitch + roll + scale) onto the hidden {@link #level}.
     *
     * <p><b>Root cause this fixes (physics-redesign flaw #10, {@code .migration/ROADMAP-physics-redesign.md}
     * §6.3 — "a real desync bug, independent of everything else").</b> This setter — like
     * {@link #setYawRadians} and {@link #setAnchor} — used to push through the SHORT 5-arg
     * {@code level.setTransform(x, y, z, yaw, pitch)} overload, which writes only four of the level's seven
     * live transform components. Every transform push in this class must therefore go through the CANONICAL
     * 7-arg {@code setTransform(x, y, z, yaw, pitch, roll, scale)} overload and carry the object's CURRENT
     * {@link #rollRadians} and {@link #scale}, so there is exactly one way a pose reaches
     * {@link ContraptionLevel} and no setter can ever publish a partial one.
     *
     * <p>Why partial pushes are a correctness hazard even though the short overloads happen to leave
     * {@code realRoll}/{@code realScale} untouched today: the level's live pose is then assembled from
     * writes made at DIFFERENT times by DIFFERENT setters, i.e. it is a second, implicit source of truth
     * that only agrees with this object as long as every short overload keeps its "preserve what I don't
     * write" contract. That contract is invisible at the call site, unenforced by the type system, and one
     * added field away from silently dropping a DOF — which is exactly what the render/physics desync
     * (render draws the tilt/scale, collision reasons about a body without it) looks like from the outside.
     * Pushing the whole pose every time makes {@link ContraptionState} the single source of truth by
     * construction. This is a no-op for every existing contraption: the values written are the ones the
     * level already held. Main-thread only.
     */
    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, scale);
        }
    }

    /**
     * Returns the yaw rotation (radians).
     *
     * @return yaw in radians
     */
    public double yawRadians() {
        return yawRadians;
    }

    /**
     * Sets the yaw rotation (radians) and pushes the complete current pose (position + yaw + pitch + roll +
     * scale) onto the hidden {@link #level} through the canonical 7-arg overload. See {@link #setPosition}
     * for the full root-cause writeup of why no setter here may push a partial pose (physics-redesign
     * flaw #10). Behaviourally identical for every existing contraption; only the completeness of the push
     * changes.
     *
     * @param yawRadians yaw in radians
     */
    public void setYawRadians(double yawRadians) {
        this.yawRadians = yawRadians;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, scale);
        }
    }

    /**
     * Sets the anchor offset for entity-anchored types.
     *
     * @param anchorOffset Vec3 from anchor to origin
     */
    public void setAnchorOffset(net.minecraft.world.phys.Vec3 anchorOffset) {
        this.anchorOffset = anchorOffset;
    }

    /**
     * Sets the yaw offset for entity-anchored types.
     *
     * @param yawOffset contraption yaw minus anchor yaw at capture time
     */
    public void setYawOffset(java.util.OptionalDouble yawOffset) {
        this.yawOffset = yawOffset;
    }

    /**
     * Returns the pitch rotation (radians) about the horizontal tipping axis. Default 0.
     *
     * @return pitch in radians
     */
    public double pitchRadians() {
        return pitchRadians;
    }

    /**
     * Sets the pitch rotation (radians) and pushes the full transform (including pitch) onto the hidden
     * {@link #level}, exactly like {@link #setYawRadians} does for yaw, so {@code ContraptionLevel}'s
     * real-world-projection methods ({@code realWorldPositionOf}/{@code realOrientationOf}) tip in lock-step.
     * The vertical twin of {@link #setYawRadians} (roadmap item #9 phase 5).
     *
     * @param pitchRadians pitch in radians
     */
    public void setPitchRadians(double pitchRadians) {
        this.pitchRadians = pitchRadians;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, scale);
        }
    }

    /**
     * Returns the roll rotation (radians) about the horizontal Z tilt axis. Default 0.
     *
     * @return roll in radians
     */
    public double rollRadians() {
        return rollRadians;
    }

    /**
     * Sets the roll rotation (radians) and pushes the full transform (position + yaw + pitch + roll + scale)
     * onto the hidden {@link #level} through the canonical roll-aware setTransform, exactly like
     * {@link #setPitchRadians} does for pitch, so {@code ContraptionLevel}'s real-world-projection methods
     * ({@code realWorldPositionOf}/{@code realOrientationOf}) lean in lock-step. The horizontal twin of
     * {@link #setPitchRadians} (roadmap item #9 phase 6).
     *
     * @param rollRadians roll in radians
     */
    public void setRollRadians(double rollRadians) {
        this.rollRadians = rollRadians;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, scale);
        }
    }

    /**
     * Returns the uniform scale factor of the whole contraption. Default {@code 1.0}.
     *
     * @return scale factor
     */
    public double scale() {
        return scale;
    }

    /**
     * Sets the uniform scale factor (roadmap item #9), clamped to {@code [}{@link #MIN_SCALE}{@code ,}
     * {@link #MAX_SCALE}{@code ]}, and pushes the full transform (position + yaw + pitch + scale) onto the
     * hidden {@link #level} exactly like {@link #setYawRadians}/{@link #setPitchRadians} do, so
     * {@code ContraptionLevel}'s real-world-projection methods ({@code realWorldPositionOf}/
     * {@code realOrientationOf}) resize in lock-step and the next render/hitbox pass resends every cell at
     * its scaled position. This is the public setter path a later item/command (the "creative phys wand")
     * calls to resize a live contraption; the caller should then invalidate the render-transform cache (see
     * {@code ContraptionEntity#setScale}/{@code markMoved}) so the change is resent next tick. A {@code NaN}
     * argument is ignored defensively (leaves the current scale untouched). Main-thread only.
     *
     * @param scale scale factor to set
     */
    public void setScale(double scale) {
        if (Double.isNaN(scale)) {
            return;
        }
        this.scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
        if (level != null) {
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, this.scale);
        }
    }

    /**
     * Re-anchors this contraption into a new world at a new transform (roadmap item #1,
     * cross-world teleport / minecart portal crossing). Atomically rewrites {@link #worldId} and
     * the full position/yaw and pushes the transform onto the hidden {@link #level}, so the next
     * engine tick resolves and projects into {@code worldId}'s world at the new pose. The paired
     * {@link ContraptionLevel#reanchor} must be called for the same transform to swap the level's own
     * real-world projection pointer. {@link dev.arubik.craftengine.contraption.ContraptionEntity#teleport}
     * drives both. A {@code null worldId} is ignored (transform-only move, defensive). This does not
     * touch {@link #originX}/Y/Z (the capture-time bearing origin used by disassemble's grid math),
     * which remain the original birth-world anchor. A re-anchored minecart contraption disassembles
     * from its live position via {@link #x}/{@link #y}/{@link #z}, not the origin. Main-thread only.
     *
     * @param worldId NMS dimension key, or null to leave unchanged
     * @param x new world X
     * @param y new world Y
     * @param z new world Z
     * @param yawRadians new yaw in radians
     */
    public void setAnchor(ResourceKey<Level> worldId, double x, double y, double z, double yawRadians) {
        if (worldId != null) {
            this.worldId = worldId;
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.yawRadians = yawRadians;
        if (level != null) {
            // COMPLETE pose push (physics-redesign flaw #10 — see setPosition): a re-anchor rewrites
            // position + yaw, and must carry the body's CURRENT pitch/roll/scale across with it rather than
            // publishing four of seven components. A contraption that teleports mid-tip/mid-lean, or a
            // scaled one, keeps its pose through the crossing.
            level.setTransform(x, y, z, yawRadians, pitchRadians, rollRadians, scale);
        }
    }

    /**
     * Returns whether this contraption is stalled.
     *
     * @return true if stalled
     */
    public boolean isStalled() {
        return stalled;
    }

    /**
     * Sets the stall state.
     *
     * @param stalled stall state
     */
    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    /**
     * Returns the list of movement behaviors.
     *
     * @return behavior list
     */
    public List<MovementBehavior> behaviors() {
        return behaviors;
    }

    /**
     * Adds a movement behavior.
     *
     * @param behavior behavior to add
     */
    public void addBehavior(MovementBehavior behavior) {
        behaviors.add(behavior);
    }

    /**
     * Sets the world-space delta actually applied last tick.
     *
     * @param dx delta X
     * @param dy delta Y
     * @param dz delta Z
     */
    public void setLastDelta(double dx, double dy, double dz) {
        this.lastDeltaX = dx;
        this.lastDeltaY = dy;
        this.lastDeltaZ = dz;
    }

    /**
     * Returns the world-space delta X actually applied last tick.
     *
     * @return last delta X
     */
    public double lastDeltaX() {
        return lastDeltaX;
    }

    /**
     * Returns the world-space delta Y actually applied last tick.
     *
     * @return last delta Y
     */
    public double lastDeltaY() {
        return lastDeltaY;
    }

    /**
     * Returns the world-space delta Z actually applied last tick.
     *
     * @return last delta Z
     */
    public double lastDeltaZ() {
        return lastDeltaZ;
    }

    /**
     * Sets the yaw rotation (radians) applied last tick.
     *
     * @param dyaw yaw delta in radians
     */
    public void setLastYawDelta(double dyaw) {
        this.lastYawDelta = dyaw;
    }

    /**
     * Returns the yaw rotation (radians) applied last tick.
     *
     * @return last yaw delta
     */
    public double lastYawDelta() {
        return lastYawDelta;
    }

    /**
     * Sets the pitch rotation (radians) applied last tick.
     *
     * @param dpitch pitch delta in radians
     */
    public void setLastPitchDelta(double dpitch) {
        this.lastPitchDelta = dpitch;
    }

    /**
     * Returns the pitch rotation (radians) applied last tick.
     *
     * @return last pitch delta
     */
    public double lastPitchDelta() {
        return lastPitchDelta;
    }

    /**
     * Sets the roll rotation (radians) applied last tick.
     *
     * @param droll roll delta in radians
     */
    public void setLastRollDelta(double droll) {
        this.lastRollDelta = droll;
    }

    /**
     * Returns the roll rotation (radians) applied last tick.
     *
     * @return last roll delta
     */
    public double lastRollDelta() {
        return lastRollDelta;
    }

    /**
     * Returns CraftEngine furniture captured at assembly time.
     *
     * @return furniture list
     */
    public List<ContraptionFurniture> furniture() {
        return furniture;
    }

    /**
     * Sets the captured furniture list.
     *
     * @param furniture furniture list, or null for empty
     */
    public void setFurniture(List<ContraptionFurniture> furniture) {
        this.furniture = furniture != null ? furniture : List.of();
    }

    /**
     * Returns the RPM the assembled bearing is currently pulling from a real adjacent motor.
     *
     * @return global RPM
     */
    public float globalRpm() {
        return globalRpm;
    }

    /**
     * Sets the global RPM fed from an external motor.
     *
     * @param globalRpm RPM value
     */
    public void setGlobalRpm(float globalRpm) {
        this.globalRpm = globalRpm;
    }

    /**
     * Zeroes the per-tick SU demand accumulator. Called once per engine tick, before any behavior ticks.
     */
    public void resetSuDemand() {
        this.suDemand = 0f;
    }

    /**
     * Adds {@code su} to this tick's accumulated demand. Called by an operating consumer (e.g. {@code MinerBehavior}).
     *
     * @param su stress units to add
     */
    public void addSuDemand(float su) {
        this.suDemand += su;
    }

    /**
     * Returns this tick's accumulated SU demand so far (grows as each consumer's tick() runs; read whole by the bearing, which always ticks last).
     *
     * @return accumulated stress demand
     */
    public float suDemand() {
        return suDemand;
    }

    /**
     * Registers (or overwrites) a player as carried at a fixed bearing-relative local offset.
     *
     * @param playerId player UUID
     * @param localOffset local offset Vec3
     */
    public void addSeatedRider(UUID playerId, Vec3 localOffset) {
        seatedRiders.put(playerId, localOffset);
    }

    /**
     * Records which real mount entity a seated rider is a passenger of.
     *
     * @param playerId player UUID
     * @param mountEntityId mount entity UUID
     */
    public void setSeatedRiderMount(UUID playerId, UUID mountEntityId) {
        seatedRiderMount.put(playerId, mountEntityId);
    }

    /**
     * Returns the real mount entity UUID a seated rider is currently a passenger of, or null if not tracked.
     *
     * @param playerId player UUID
     * @return mount entity UUID or null
     */
    public UUID seatedRiderMount(UUID playerId) {
        return seatedRiderMount.get(playerId);
    }

    /**
     * Removes a seated rider and their mount tracking.
     *
     * @param playerId player UUID
     */
    public void removeSeatedRider(UUID playerId) {
        seatedRiders.remove(playerId);
        seatedRiderMount.remove(playerId);
    }

    /**
     * Returns an immutable copy of the seated riders map.
     *
     * @return seated riders map
     */
    public Map<UUID, Vec3> seatedRiders() {
        return Map.copyOf(seatedRiders);
    }

    /**
     * Returns the owning player's UUID, or {@code null} if this contraption has no owner.
     *
     * @return owner UUID or null
     */
    public UUID owner() {
        return owner;
    }

    /**
     * Sets (or clears, with {@code null}) the owning player. Main-thread only.
     *
     * @param owner owner UUID or null
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    /**
     * Returns the packed snapped-cell key of the last move-gate region check.
     *
     * @return region check key
     */
    public long lastRegionCheckKey() {
        return lastRegionCheckKey;
    }

    /**
     * Returns the cached move-gate verdict for {@link #lastRegionCheckKey}.
     *
     * @return true if allowed
     */
    public boolean lastRegionAllowed() {
        return lastRegionAllowed;
    }

    /**
     * Records a fresh move-gate region check.
     *
     * @param key snapped-cell key
     * @param allowed verdict
     */
    public void setLastRegionCheck(long key, boolean allowed) {
        this.lastRegionCheckKey = key;
        this.lastRegionAllowed = allowed;
    }

    /**
     * Returns per-contraption entity-collision push/carry tuning. Never {@code null}.
     *
     * @return push settings
     */
    public ContraptionPushSettings pushSettings() {
        return pushSettings;
    }

    /**
     * Sets this contraption's push/carry tuning. A {@code null} argument resets to {@link ContraptionPushSettings#DEFAULT}.
     *
     * @param pushSettings push settings or null for default
     */
    public void setPushSettings(ContraptionPushSettings pushSettings) {
        this.pushSettings = pushSettings != null ? pushSettings : ContraptionPushSettings.DEFAULT;
    }

    // ---- Light map (baked emitter cache for elements) ----

    private final dev.arubik.craftengine.contraption.element.ContraptionLightMap lightMap =
            new dev.arubik.craftengine.contraption.element.ContraptionLightMap();

    public dev.arubik.craftengine.contraption.element.ContraptionLightMap lightMap() {
        return lightMap;
    }

    // ---- Element system (refactored global element registry) ----

    private final List<ContraptionElement> elements = new ArrayList<>();
    private final Int2ObjectOpenHashMap<ContraptionElement> entityIdIndex = new Int2ObjectOpenHashMap<>();

    public List<ContraptionElement> elements() {
        return Collections.unmodifiableList(elements);
    }

    public void addElement(ContraptionElement element) {
        elements.add(element);
        for (int eid : element.entityIds()) {
            entityIdIndex.put(eid, element);
        }
    }

    public void removeElement(ContraptionElement element) {
        elements.remove(element);
        for (int eid : element.entityIds()) {
            entityIdIndex.remove(eid);
        }
    }

    public void setElements(List<ContraptionElement> newElements) {
        elements.clear();
        entityIdIndex.clear();
        if (newElements != null) {
            for (ContraptionElement e : newElements) {
                addElement(e);
            }
        }
    }

    public ContraptionElement elementByEntityId(int entityId) {
        return entityIdIndex.get(entityId);
    }

    public ContraptionElement elementByLocalPos(BlockPos local) {
        for (ContraptionElement e : elements) {
            Vec3 offset = e.localOffset();
            if (BlockPos.containing(offset.x, offset.y, offset.z).equals(local)) {
                return e;
            }
        }
        return null;
    }

    public void rebuildEntityIdIndex() {
        entityIdIndex.clear();
        for (ContraptionElement e : elements) {
            for (int eid : e.entityIds()) {
                entityIdIndex.put(eid, e);
            }
        }
    }
}
