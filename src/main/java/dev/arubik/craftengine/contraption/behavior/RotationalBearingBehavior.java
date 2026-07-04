package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Continuous rotation around the bearing (CONTRAPTIONS.md §7 originally deferred "past MVP",
 * shipped in Phase 6): increments the contraption's yaw every tick. Reuses the existing
 * rotate-around-bearing math already used for rendering ({@code ContraptionMath#renderPosition},
 * driven off {@code ContraptionState#yawRadians}) rather than adding a second rotation code
 * path — this behavior's whole job is mutating that one value. Never stalls on its own, same
 * stall-gating contract as {@code LinearActuatorBehavior}/{@code MinerBehavior}.
 *
 * <p><b>Task 4 (CONTRAPTIONS.md 2026-07-01 session) — the bearing is no longer an RPM
 * SOURCE, it's an RPM RELAY.</b> Previously this class took a fixed constructor {@code rpm}
 * (from {@code BearingBlockBehavior}'s {@code rpm:} config field, or the
 * {@code ContraptionAssembler.DEFAULT_ROTATIONAL_RPM} fallback) and spun at that constant
 * forever. Per the user's explicit clarification, a bearing should instead pull its spin rate
 * from a REAL adjacent block's {@link RpmProvider} (e.g. a motor placed next to the bearing
 * block before assembly) — {@link #motorPos} is that neighbor's real-world {@link BlockPos}
 * (found once via {@link RealMotorLink#findAdjacentMotor} at assembly time, in
 * {@code ContraptionAssembler.attachDefaultBehavior}) and is re-resolved fresh EVERY tick via
 * {@link RealMotorLink#currentRpm} — {@code ctx.level()} inside {@link #tick} is the REAL
 * world's {@code ServerLevel} (see {@code ContraptionEngine.stepKinematics}, which resolves it
 * from {@code ContraptionState#worldId}), not the {@code ContraptionLevel} mini-dimension, so
 * this reads the motor's LIVE state even though the bearing's own block/BlockEntity no longer
 * exists post-capture. {@code fallbackRpm} (the old config/default value) is used only when
 * {@link #motorPos} is {@code null} (no motor was adjacent at assembly time) — this keeps a
 * bearing usable stand-alone (tests, or a build that intentionally wants a constant spin) while
 * making "feed off a real motor" the primary, documented path.
 *
 * <p><b>Global feed, local-over-global priority.</b> Every tick this behavior pushes its
 * current rpm onto {@code ContraptionState#setGlobalRpm} — available to every captured block in
 * the SAME contraption via {@code ContraptionState#globalRpm()}. It ALSO still pushes directly
 * to every {@link RpmConsumer} {@link MovementBehavior} captured in the same structure (the
 * pre-existing "captured together stands in for adjacency-connected" simplification, unchanged
 * from before this session — see the git history of this class for that write-up) — but each
 * consumer decides for itself whether to actually use that pushed value or prefer a more local
 * source; see e.g. {@code MinerBehavor#tick}'s explicit "local-over-global" combine logic. This
 * class does not decide priority — it only ever offers rpm, on both the direct-push and the
 * state-global channel.
 *
 * <p><b>SU demand scales with structure size.</b> Every tick this also reports
 * {@code suPerBlock * ctx.state().level().blockCount()} stress-units of load to the real motor
 * via {@link RealMotorLink#reportStressLoad} — a bigger captured structure demands more SU from
 * whatever motor is driving it, same "load reporting" contract {@code CrusherBlockEntity}
 * already uses per-recipe (see {@link RpmProvider#reportStressLoad}'s own javadoc). If the
 * motor decides that overstresses it, it's free to reduce/zero {@link RpmProvider#getRpm()} on
 * its own — this class doesn't implement overstress logic itself, it only reports the demand
 * and reads back whatever the motor actually delivers.
 */
public final class RotationalBearingBehavior implements MovementBehavior, RpmProvider {

    /** Real-world neighbor position of the motor this bearing pulls from, or {@code null} if none was found at assembly. */
    private final BlockPos motorPos;
    /** Used only when {@link #motorPos} is {@code null} — see class javadoc. */
    private final double fallbackRpm;
    /** SU demanded from the real motor per captured block — see {@code BearingBlockBehavior#suPerBlock()}. */
    private final double suPerBlock;

    private double currentRpm;

    public RotationalBearingBehavior(BlockPos motorPos, double fallbackRpm, double suPerBlock) {
        this.motorPos = motorPos;
        this.fallbackRpm = fallbackRpm;
        this.suPerBlock = suPerBlock;
    }

    /** Convenience for callers with no real motor to link (tests, tools) — always uses {@code fallbackRpm}, no SU reporting. */
    public RotationalBearingBehavior(double fallbackRpm) {
        this(null, fallbackRpm, 0);
    }

    @Override
    public void tick(MovementContext ctx) {
        if (motorPos != null) {
            int blockCount = ctx.state().level() != null ? ctx.state().level().blockCount() : 1;
            // Task 4/5 follow-up: base per-block overhead PLUS the actual per-tick demand every
            // operating consumer (e.g. a MinerBehavior that's actually cutting) reported this
            // same pass — see ContraptionState#suDemand's javadoc for why this bearing always
            // ticks last and therefore sees the full total.
            float totalSu = (float) (suPerBlock * blockCount) + ctx.state().suDemand();
            RealMotorLink.reportStressLoad(ctx.level(), motorPos, totalSu);
            currentRpm = RealMotorLink.currentRpm(ctx.level(), motorPos);
        } else {
            currentRpm = fallbackRpm;
        }

        double radiansPerTick = currentRpm * 2.0 * Math.PI / 60.0 / 20.0; // 20 ticks/sec
        ctx.state().setYawRadians(ctx.state().yawRadians() + radiansPerTick);

        float rpmNow = getRpm();
        ctx.state().setGlobalRpm(rpmNow); // Task 4: feed globally to the whole contraption

        // Pre-existing simplified in-structure direct push (documented above) — kept for
        // callers/tests that read a consumer's own getInputRpm() directly.
        for (MovementBehavior other : ctx.state().behaviors()) {
            if (other != this && other instanceof RpmConsumer consumer) {
                consumer.setInputRpm(rpmNow);
            }
        }
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    @Override
    public Vec3 velocityThisTick() {
        return Vec3.ZERO; // rotation only — no translation
    }

    // ---------------- RpmProvider ----------------

    /** Never stalls (see {@link #isStalled}), so this always reports the last-resolved rpm. */
    @Override
    public float getRpm() {
        return (float) currentRpm;
    }
}
