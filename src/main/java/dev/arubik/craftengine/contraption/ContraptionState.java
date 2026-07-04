package dev.arubik.craftengine.contraption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import net.minecraft.world.phys.Vec3;

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
    private final UUID worldId;
    private final ContraptionLevel level;

    /** Bearing's continuous world-space position (Phase 2: stationary, so this never changes). */
    private double x, y, z;
    private double yawRadians;
    private boolean stalled;
    private final List<MovementBehavior> behaviors = new ArrayList<>();
    private List<ContraptionFurniture> furniture = List.of();

    /** World-space delta ACTUALLY applied last tick (zero while stalled) — fed to {@code PlayerCarry} by the hitbox swarm. */
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

    public ContraptionState(UUID id, UUID worldId, ContraptionLevel level, double x, double y, double z) {
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
            level.setTransform(x, y, z, 0);
        }
    }

    public UUID id() {
        return id;
    }

    public UUID worldId() {
        return worldId;
    }

    public ContraptionLevel level() {
        return level;
    }

    /** See {@link #originX} javadoc. */
    public net.minecraft.core.BlockPos originBearingBlockPos() {
        return net.minecraft.core.BlockPos.containing(originX, originY, originZ);
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians);
        }
    }

    public double yawRadians() {
        return yawRadians;
    }

    public void setYawRadians(double yawRadians) {
        this.yawRadians = yawRadians;
        if (level != null) {
            level.setTransform(x, y, z, yawRadians);
        }
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public List<MovementBehavior> behaviors() {
        return behaviors;
    }

    public void addBehavior(MovementBehavior behavior) {
        behaviors.add(behavior);
    }

    public void setLastDelta(double dx, double dy, double dz) {
        this.lastDeltaX = dx;
        this.lastDeltaY = dy;
        this.lastDeltaZ = dz;
    }

    public double lastDeltaX() {
        return lastDeltaX;
    }

    public double lastDeltaY() {
        return lastDeltaY;
    }

    public double lastDeltaZ() {
        return lastDeltaZ;
    }

    /** Yaw rotation (radians) applied last tick — see {@link #lastYawDelta}. */
    public void setLastYawDelta(double dyaw) {
        this.lastYawDelta = dyaw;
    }

    public double lastYawDelta() {
        return lastYawDelta;
    }

    /** CraftEngine furniture captured at assembly time — see {@link ContraptionFurnitureCapture}'s javadoc. */
    public List<ContraptionFurniture> furniture() {
        return furniture;
    }

    public void setFurniture(List<ContraptionFurniture> furniture) {
        this.furniture = furniture != null ? furniture : List.of();
    }

    /** See {@link #globalRpm} field javadoc. */
    public float globalRpm() {
        return globalRpm;
    }

    public void setGlobalRpm(float globalRpm) {
        this.globalRpm = globalRpm;
    }

    /** Zeroes the per-tick SU demand accumulator — called once per engine tick, before any behavior ticks. See {@link #suDemand} field javadoc. */
    public void resetSuDemand() {
        this.suDemand = 0f;
    }

    /** Adds {@code su} to this tick's accumulated demand — called by an operating consumer (e.g. {@code MinerBehavior}). */
    public void addSuDemand(float su) {
        this.suDemand += su;
    }

    /** This tick's accumulated SU demand so far (grows as each consumer's tick() runs; read whole by the bearing, which always ticks last). */
    public float suDemand() {
        return suDemand;
    }

    /** Registers (or overwrites) a player as carried at a fixed bearing-relative local offset — see {@link #seatedRiders} field javadoc. */
    public void addSeatedRider(UUID playerId, Vec3 localOffset) {
        seatedRiders.put(playerId, localOffset);
    }

    /** Records which real mount entity (see {@link #seatedRiderMount}) a seated rider is a passenger of. */
    public void setSeatedRiderMount(UUID playerId, UUID mountEntityId) {
        seatedRiderMount.put(playerId, mountEntityId);
    }

    /** The real mount entity UUID a seated rider is currently a passenger of, or null if not tracked. */
    public UUID seatedRiderMount(UUID playerId) {
        return seatedRiderMount.get(playerId);
    }

    public void removeSeatedRider(UUID playerId) {
        seatedRiders.remove(playerId);
        seatedRiderMount.remove(playerId);
    }

    public Map<UUID, Vec3> seatedRiders() {
        return Map.copyOf(seatedRiders);
    }
}
