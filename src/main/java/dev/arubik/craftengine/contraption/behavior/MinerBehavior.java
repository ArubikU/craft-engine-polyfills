package dev.arubik.craftengine.contraption.behavior;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.contraption.ContraptionAccessor;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.rotation.RpmConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * MVP stalling drill (CONTRAPTIONS.md §5 Phase 5 "Miners specifically"): accumulates
 * mining damage each tick from a LIVE RPM input (see {@link RpmConsumer}, driven either by
 * the manual test command, {@code MinerBlockBehavior}'s own real-world pull-scan, or a
 * captured {@code RotationalBearingBehavior} in the same structure — see that class's
 * javadoc for the in-structure propagation mechanism) times a fixed {@code gearRatio}, vs.
 * the real world block's vanilla hardness at a fixed bearing-relative offset. Stalls the
 * whole contraption's geographic movement while cutting, breaks the real block via
 * {@link ContraptionAccessor} once damage exceeds hardness (collecting drops into this
 * behavior's own virtual inventory instead of spawning dropped-item entities), then
 * un-stalls.
 *
 * <p><b>RPM is no longer fixed at construction</b> (this used to take a constant {@code rpm}
 * that never updated) — it now implements {@link RpmConsumer} and reads
 * {@link #getInputRpm()} fresh every tick, multiplied by {@link #gearRatio}. As of Task 4
 * (CONTRAPTIONS.md 2026-07-01 session), local input ({@link #getInputRpm()}, set via
 * {@link #setInputRpm}) is preferred over {@code ctx.state().globalRpm()} (the bearing's
 * pulled-in real-motor rpm, fed to every captured block) whenever local input is &gt; 0 — see
 * {@link #tick} for the exact combine. Effective rpm
 * &le; 0 (no power feeding it) is treated as INERT, not stalled: {@link #isStalled()}
 * returns {@code false} so a powerless miner never permanently deadlocks a LINEAR-bearing
 * contraption's movement (there is no in-structure power source for a LINEAR bearing — pure
 * translation produces no RPM — so a miner riding one is inert-by-design until something
 * external feeds it via {@link #setInputRpm}; this was a deliberate choice over "fall back to
 * a small default rpm", documented in CONTRAPTIONS.md). This is distinct from the
 * already-existing "hit bedrock" permanent stall (hardness &lt; 0), which is intentionally
 * unrecoverable.
 *
 * <p><b>Deferred in this pass</b>: block-crack-stage overlay packets (the
 * {@code ClientboundBlockDestructionPacket} shim from Phase-0 spike #4 is ready for this,
 * but {@link MovementBehavior#tick} only has a {@link ServerLevel}, not a viewer list —
 * wiring that needs either extending {@link MovementContext} with viewers or a separate
 * render-side hook, deliberately left for a follow-up so this pass stays correct and
 * testable rather than half-wiring visuals).
 */
public final class MinerBehavior implements MovementBehavior, RpmConsumer {

    private final BlockPos targetOffset;
    /** Multiplier applied to whatever rpm is delivered via {@link #setInputRpm} (config field on {@code MinerBlockBehavior}). */
    private final double gearRatio;
    /** Effective rpm must reach this before the miner will cut at all (Task 3, config field on {@code MinerBlockBehavior}). */
    private final double minRpm;
    /** Stress units this miner demands from the contraption's finite SU budget while operating (Task 4/5 follow-up). */
    private final double suCost;
    private final List<ItemStack> virtualInventory = new ArrayList<>();

    private float inputRpm;
    private double accumulatedDamage;
    private boolean stalled;

    public MinerBehavior(BlockPos targetOffset, double gearRatio, double minRpm, double suCost) {
        this.targetOffset = targetOffset;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
    }

    /** Convenience for callers that don't care about the minRpm/suCost gate (tests, tools) — minRpm 0, suCost 0. */
    public MinerBehavior(BlockPos targetOffset, double gearRatio) {
        this(targetOffset, gearRatio, 0, 0);
    }

    public List<ItemStack> virtualInventory() {
        return virtualInventory;
    }

    /** Damage accumulated on the CURRENT target block (0 right after a break, or if there's nothing to mine). */
    public double accumulatedDamage() {
        return accumulatedDamage;
    }

    @Override
    public void tick(MovementContext ctx) {
        ServerLevel level = ctx.level();
        if (level == null) {
            stalled = false;
            return;
        }

        BlockPos bearing = ContraptionMath.gridSnap(new Vec3(ctx.state().x(), ctx.state().y(), ctx.state().z()));
        BlockPos worldPos = ContraptionMath.toWorld(targetOffset, bearing);

        if (ContraptionAccessor.isAir(level, worldPos)) {
            stalled = false;
            accumulatedDamage = 0;
            return;
        }

        float hardness = ContraptionAccessor.hardnessAt(level, worldPos);
        if (hardness < 0) { // unbreakable (e.g. bedrock) — permanently stalled, never breaks through
            stalled = true;
            return;
        }

        // Task 4 (CONTRAPTIONS.md 2026-07-01 session): local-over-global priority — prefer this
        // miner's OWN locally-fed rpm (set via RpmConsumer#setInputRpm, e.g. a real motor
        // captured directly adjacent to it inside the same ContraptionLevel) over the bearing's
        // globally-fed rpm (ctx.state().globalRpm()), and only fall back to the global feed when
        // nothing local is driving it.
        float rpmToUse = inputRpm > 0 ? inputRpm : ctx.state().globalRpm();
        double effectiveRpm = rpmToUse * gearRatio;
        // Task 3 (CONTRAPTIONS.md 2026-07-01 session): config-driven minRpm gate — mirrors
        // CrusherBlockEntity#canProcess's "inputRpm >= effMinRpm(recipe)" check. Below the
        // threshold is treated the same as zero power: inert, not stalled.
        if (effectiveRpm <= 0 || effectiveRpm < minRpm) {
            // No (or insufficient) power delivered — inert, not stalled (see class javadoc for
            // why this is NOT the same as the bedrock case above: a powerless/underpowered
            // drill shouldn't permanently deadlock a contraption's movement).
            stalled = false;
            return;
        }

        // Task 4/5 follow-up (this session, per the user's explicit "finite SU budget"
        // clarification): this miner's SU demand is added to the contraption's per-tick
        // total, read back by whichever bearing behavior ticks after it in the SAME pass —
        // see ContraptionState#addSuDemand's javadoc for the full finite-resource design.
        ctx.state().addSuDemand((float) suCost);

        stalled = true;
        accumulatedDamage += MiningMath.damagePerTick(effectiveRpm, hardness);

        if (MiningMath.isBroken(accumulatedDamage, hardness)) {
            // Public API veto (ContraptionBlockBreakEvent) — fired here rather than threaded into
            // ContraptionAccessor#breakBlockAndCollect (which stays a pure world primitive with no
            // event/entity coupling). Cancelling skips the break but still resets damage/stall below,
            // so the block survives and the miner re-accumulates from scratch next cycle instead of
            // re-firing the event every tick.
            if (!fireBlockBreakCancelled(ctx, level, worldPos)) {
                ContraptionAccessor.breakBlockAndCollect(level, worldPos, virtualInventory);
            }
            accumulatedDamage = 0;
            stalled = false;
        }
    }

    /**
     * Fires {@link dev.arubik.craftengine.contraption.event.ContraptionBlockBreakEvent} and returns
     * whether it was cancelled. Fail-open (returns {@code false} = "break it") if the owning facade
     * can't be resolved or no live server is present — e.g. a pure-JVM unit test where
     * {@code Bukkit.getPluginManager()} throws — so miner behavior is byte-identical to before this
     * event existed whenever nobody is listening. Same shape as {@code ContraptionAssembler}'s own
     * fire helpers.
     */
    private static boolean fireBlockBreakCancelled(MovementContext ctx, ServerLevel level, BlockPos worldPos) {
        try {
            ContraptionEntity entity =
                    ContraptionManager.get(ctx.state().id());
            if (entity == null) {
                return false;
            }
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(worldPos);
            dev.arubik.craftengine.contraption.event.ContraptionBlockBreakEvent event =
                    new dev.arubik.craftengine.contraption.event.ContraptionBlockBreakEvent(
                            entity, level.getWorld(), worldPos, state);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public boolean isStalled() {
        return stalled;
    }

    // ---------------- RpmConsumer ----------------

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }
}
