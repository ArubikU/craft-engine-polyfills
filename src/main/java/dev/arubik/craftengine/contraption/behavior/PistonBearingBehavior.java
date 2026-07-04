package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * 6-directional extending-piston bearing (2026-07-03 goal — rework of the old one-way
 * {@link LinearActuatorBehavior}). Unlike the old actuator (which translated the whole contraption
 * indefinitely along a hardcoded +X), this drives a Create-style mechanical-piston cycle: the
 * captured structure is pushed OUT along the bearing's own {@code facing} up to a configurable
 * {@code maxDistance}, held/handled at the end per {@link Mode}, and retracted back — all while the
 * bearing BODY block stays fixed at its real-world anchor ({@link #bodyPos}) so it can keep reading
 * a real adjacent rpm/su provider AND external-world redstone even though the structure itself lives
 * in the hidden contraption dimension.
 *
 * <p><b>Redstone return</b> (user: "al recibir 1 tick de redstone sin importar lo que sea
 * volvera"): any rising redstone edge at the body's real position forces a retract, from any phase.
 *
 * <p><b>rpm/su scaling</b> (user: "por cada bloque y velocidad debe variar el rpm y su"): SU demand
 * is {@code suPerBlock * capturedBlocks * distanceMultiplier + this-tick consumer demand}, where the
 * distance multiplier adds +10% per extra block of configured travel beyond the base 1 (user: "de
 * base es 1 bloque de empuje. por cada bloque extra consume +10% mas su"). Speed comes from the real
 * adjacent motor ({@code rpm/60} blocks/sec, same convention as the old actuator) and falls back to
 * {@link #baseSpeedBlocksPerSec} (default 1 block/s) when no motor is present.
 *
 * <p><b>Modes</b> — {@link Mode#LINEAR} and {@link Mode#ROUND_ROBIN} are fully driven here. The two
 * "euler" modes (become REAL blocks at the extended end, re-assemble on redstone) need to trigger a
 * whole-contraption disassemble, which a {@link MovementBehavior} can't do by itself — this class
 * exposes {@link #wantsDisassembleAtEnd()} so the owning system (engine/manager) performs it; until
 * that integration lands they behave like {@link Mode#LINEAR} (hold at end). See the TODOs.
 */
public final class PistonBearingBehavior implements MovementBehavior {

    /** How the piston behaves once fully extended. */
    public enum Mode {
        /** Extend to the end and HOLD; a redstone pulse retracts it (and another re-extends). */
        /** Redstone PULSE drives one move at a time: pulse extends (→ solid), pulse retracts (→ solid). */
        LINEAR,
        /** Redstone ON = auto-cycle (extend → dwell → retract → dwell → …); OFF = stop at the current solid end. */
        ROUND_ROBIN;

        public static Mode fromString(String s) {
            if (s == null) {
                return LINEAR;
            }
            switch (s.toLowerCase(java.util.Locale.ROOT)) {
                // The old EULER/ROBIN_EULER (become-real-blocks) behaviour is now the STANDARD for
                // BOTH modes (2026-07-03 — "que ya no exista la version euler, sera el estandar; al
                // extender o contraer se vuelven bloques solidos") — kept here as aliases.
                case "round_robin":
                case "roundrobin":
                case "robin_euler":
                case "robineuler":
                    return ROUND_ROBIN;
                default:
                    return LINEAR;
            }
        }
    }

    private enum Phase {
        EXTENDING, AT_END, RETRACTING, RETRACTED
    }

    /** Unit direction the structure is pushed along (the bearing block's facing). */
    private final Vec3 dir;
    /** Real-world anchor of the bearing BODY — used for motor lookup AND redstone reads; never captured. */
    private final BlockPos bodyPos;
    /** Adjacent real motor found at assembly, re-resolved every tick (may be null → fallback speed). */
    private final BlockPos motorPos;
    // Config values — NON-final so the right-click menu can live-update a running bearing
    // (2026-07-03 — "cambiar el modo mientras esta encendido"). The block entity pushes new values
    // via the setters below when the player edits them.
    /** Max travel in blocks (default 1). */
    private int maxDistance;
    /** Fallback traction speed (blocks/sec) when no motor is present — default 1. */
    private double baseSpeedBlocksPerSec;
    /** Base SU per captured block (before the distance multiplier). */
    private final double suPerBlock;
    private Mode mode;
    /** For ROUND_ROBIN / ROBIN_EULER: ticks to wait at the extended end before retracting. */
    private long roundRobinDelayTicks;

    private Phase phase = Phase.EXTENDING;
    /** Blocks extended so far (0 .. maxDistance). */
    private double extended = 0.0;
    private boolean started = false;
    private boolean reachedEndThisCycle = false;
    private Vec3 velocityPerTick = Vec3.ZERO;

    public PistonBearingBehavior(Vec3 directionUnit, BlockPos bodyPos, BlockPos motorPos, int maxDistance,
            double baseSpeedBlocksPerSec, double suPerBlock, Mode mode, long roundRobinDelayTicks) {
        this.dir = directionUnit.normalize();
        this.bodyPos = bodyPos == null ? null : bodyPos.immutable();
        this.motorPos = motorPos == null ? null : motorPos.immutable();
        this.maxDistance = Math.max(1, maxDistance);
        this.baseSpeedBlocksPerSec = baseSpeedBlocksPerSec;
        this.suPerBlock = suPerBlock;
        this.mode = mode == null ? Mode.LINEAR : mode;
        this.roundRobinDelayTicks = Math.max(0, roundRobinDelayTicks);
    }

    /**
     * A piston that starts already FULLY EXTENDED and immediately RETRACTS — used by the euler
     * re-assemble (2026-07-03): after a EULER/ROBIN_EULER piston dropped its load as real blocks at
     * the extended end, a redstone pulse (or the robin_euler timer) re-captures those blocks and
     * pulls them back home. Its captured structure's local offsets already encode the extended
     * position, so starting at {@code extended = maxDistance} in {@code RETRACTING} makes it travel
     * back to the body over the normal retract path.
     */
    public static PistonBearingBehavior startRetracting(Vec3 directionUnit, BlockPos bodyPos, BlockPos motorPos,
            int maxDistance, double baseSpeedBlocksPerSec, double suPerBlock, Mode mode, long roundRobinDelayTicks) {
        PistonBearingBehavior b = new PistonBearingBehavior(directionUnit, bodyPos, motorPos, maxDistance,
                baseSpeedBlocksPerSec, suPerBlock, mode, roundRobinDelayTicks);
        b.phase = Phase.RETRACTING;
        b.extended = b.maxDistance;
        b.reachedEndThisCycle = false;
        return b;
    }

    /** SU multiplier from configured travel: +10% per extra block beyond the base 1. */
    private double distanceSuMultiplier() {
        return 1.0 + 0.10 * Math.max(0, maxDistance - 1);
    }

    @Override
    public void tick(MovementContext ctx) {
        double perTick = resolvePerTickSpeed(ctx);
        if (!started) {
            started = true; // one start sound (extend or contract) on the first tick of this move
            playSound(ctx, phase == Phase.RETRACTING ? net.minecraft.sounds.SoundEvents.PISTON_CONTRACT
                    : net.minecraft.sounds.SoundEvents.PISTON_EXTEND, 0.85f);
        }
        // The piston is now a ONE-WAY mover (2026-07-03 unify — "al extender o contraer se vuelven
        // bloques solidos"): it travels to its end and then HOLDS. The block entity / engine turns it
        // into REAL blocks at rest (AT_END or RETRACTED), and re-captures it for the next move.
        switch (phase) {
            case EXTENDING -> {
                double step = Math.min(perTick, maxDistance - extended);
                extended += step;
                velocityPerTick = dir.scale(step);
                if (extended >= maxDistance - 1.0e-6) {
                    extended = maxDistance;
                    phase = Phase.AT_END;
                    reachedEndThisCycle = true;
                    playSound(ctx, net.minecraft.sounds.SoundEvents.PISTON_EXTEND, 0.6f); // thunk
                }
            }
            case RETRACTING -> {
                double step = Math.min(perTick, extended);
                extended -= step;
                velocityPerTick = dir.scale(-step);
                if (extended <= 1.0e-6) {
                    extended = 0.0;
                    phase = Phase.RETRACTED;
                    playSound(ctx, net.minecraft.sounds.SoundEvents.PISTON_CONTRACT, 0.6f); // thunk
                }
            }
            case AT_END, RETRACTED -> velocityPerTick = Vec3.ZERO; // hold; solidified by the owner
        }
    }

    /** Plays a block sound at the body's real position (piston extend/contract on phase transitions). */
    private void playSound(MovementContext ctx, net.minecraft.sounds.SoundEvent sound, float pitch) {
        if (bodyPos == null || ctx.level() == null) {
            return;
        }
        try {
            ctx.level().playSeededSound(null, bodyPos.getX() + 0.5, bodyPos.getY() + 0.5, bodyPos.getZ() + 0.5,
                    net.minecraft.core.Holder.direct(sound), net.minecraft.sounds.SoundSource.BLOCKS, 0.5f, pitch, 0L);
        } catch (Throwable ignored) {
        }
    }

    private double resolvePerTickSpeed(MovementContext ctx) {
        double speedBlocksPerSec;
        float rpmNow;
        if (motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            float totalSu = (float) (suPerBlock * blockCount * distanceSuMultiplier()) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad(ctx.level(), motorPos, totalSu);
            rpmNow = RealMotorLink.currentRpm(ctx.level(), motorPos);
            speedBlocksPerSec = rpmNow / 60.0;
        } else {
            rpmNow = 0f;
            speedBlocksPerSec = baseSpeedBlocksPerSec;
        }
        // Retransmit the consumed rpm to the captured structure's own consumers (2026-07-03 — "el
        // bearing consume pero tambien retrasmite el rpm y su restante a los bloques internos, para
        // que funcionen aunque no tengan motores propios"). Same feed the ROTATIONAL bearing does:
        // a global rpm every captured block can read, PLUS a direct push to every in-structure
        // RpmConsumer behavior (e.g. a captured miner), so they run off the bearing's motor even
        // with no motor of their own. SU is already shared through ctx.state().suDemand() (consumers
        // add their demand; this bearing reports the grand total to the real motor above).
        ctx.state().setGlobalRpm(rpmNow);
        for (MovementBehavior other : ctx.state().behaviors()) {
            if (other != this && other instanceof dev.arubik.craftengine.rotation.RpmConsumer consumer) {
                consumer.setInputRpm(rpmNow);
            }
        }
        return Math.max(0.0, speedBlocksPerSec) / 20.0;
    }

    /**
     * True once the piston has fully EXTENDED — the owner then turns its load into REAL blocks at the
     * extended position (the standard "become solid at rest" for BOTH modes now, 2026-07-03 unify).
     * See {@code ContraptionEngine.tickAll} / {@code EulerExtendedRegistry}.
     */
    public boolean wantsDisassembleAtEnd() {
        return phase == Phase.AT_END && reachedEndThisCycle;
    }

    /** Blocks currently extended (0 .. {@link #maxDistance()}) — drives the 3-piece shaft render. */
    public double extendedBlocks() {
        return extended;
    }

    /** Unit push direction (the bearing's facing) — drives the shaft render. */
    public Vec3 direction() {
        return dir;
    }

    /** Configured max travel in blocks. */
    public int maxDistance() {
        return maxDistance;
    }

    public double baseSpeedBlocksPerSec() {
        return baseSpeedBlocksPerSec;
    }

    public double suPerBlock() {
        return suPerBlock;
    }

    public Mode mode() {
        return mode;
    }

    public long roundRobinDelayTicks() {
        return roundRobinDelayTicks;
    }

    // --- live config setters (menu edits on a running bearing) ---
    public void setMode(Mode m) {
        this.mode = m == null ? Mode.LINEAR : m;
    }

    public void setMaxDistance(int d) {
        this.maxDistance = Math.max(1, d);
    }

    public void setBaseSpeedBlocksPerSec(double s) {
        this.baseSpeedBlocksPerSec = s;
    }

    public void setRoundRobinDelayTicks(long t) {
        this.roundRobinDelayTicks = Math.max(0, t);
    }

    /** True once fully retracted — the block entity then turns the load into real blocks at home. */
    public boolean isFullyRetracted() {
        return phase == Phase.RETRACTED;
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return velocityPerTick;
    }
}
