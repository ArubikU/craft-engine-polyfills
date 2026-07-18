package dev.arubik.craftengine.contraption.behavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import dev.arubik.craftengine.contraption.ContraptionAccessor;
import dev.arubik.craftengine.contraption.ContraptionEntity;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * <b>Roadmap item #6 — the captured-hopper ↔ real-world item I/O bridge</b> (design §4 "Block
 * accessor + hopper/inventory I/O bridge", the XL item). A vanilla hopper captured inside a
 * contraption's hidden {@link ContraptionLevel} genuinely ticks there (the chunk is force-loaded to
 * {@code BLOCK_TICKING} — see {@link ContraptionLevel}'s javadoc), but every neighbor cell OUTSIDE
 * the captured footprint is void air, so vanilla's own {@code suckInItems}/{@code ejectItems} —
 * which resolve neighbors against the mini-dimension — find nothing and silently no-op. They never
 * lose an item (vanilla only removes on a successful insert), so the native tick is left entirely
 * alone: it still handles hopper-to-hopper transfers <em>inside</em> the contraption for free. This
 * class adds the missing piece — moving items between the captured hopper and the REAL chest/barrel
 * adjacent to the contraption's current real-world footprint — as an explicit per-tick pass, NOT by
 * overriding {@code getBlockState}/{@code getBlockEntity} (design §4 rejects that: vanilla internals
 * call them pervasively and redirecting out-of-footprint cells would destabilize the whole level).
 *
 * <p><b>Only while docked.</b> A captured hopper's real neighbor cell is only well-defined when the
 * contraption is {@linkplain ContraptionWorlds#isGridAligned grid-aligned} (integer position +
 * cardinal yaw) — mid-motion or rotated, "the cell below the hopper's mouth" is a fractional, rotated
 * point with no single real {@code BlockPos}. So the bridge fires only at genuine integer/cardinal
 * stops, exactly Create's docking rule (design §4 "only-when-aligned"); a constantly-moving
 * contraption I/Os only at rest. When nothing real is adjacent, it does nothing — no gameplay change.
 *
 * <p><b>Vanilla hopper, not the funnel.</b> Of the two "hopper flavors" design §4 lists, this bridges
 * the <em>vanilla</em> {@link HopperBlockEntity}: it is a real ticking {@code Container} with clean,
 * well-known direction semantics (pull from above, push to facing) and a single vanilla choke point
 * ({@link HopperBlockEntity#getContainerAt}) this can replicate against {@code realLevel}, and it is
 * NOT a CraftEngine custom block — so it can't ride the {@link MovementBehaviorRegistry} auto-attach
 * path (that is keyed by CraftEngine block {@link net.momirealms.craftengine.core.util.Key}, which a
 * vanilla block has none of). Hence a dedicated engine pass ({@link #tick}, called once per
 * contraption from {@code ContraptionEngine.tickAll}) rather than a per-block {@link MovementBehavior}.
 * {@code conveyor.FunnelBlockEntity} parity is deferred: its own {@code containerAt} resolves against
 * whatever level it ticks in (the mini-dimension when captured, i.e. void), so it would need this same
 * {@link ContraptionWorlds#realBlockNeighbor} seam re-plumbed through it — out of scope for this MVP.
 *
 * <p><b>Dupe safety.</b> Every real-world touch goes through {@link ContraptionAccessor} — the sole
 * class permitted to act on the real world for a contraption. {@link ContraptionAccessor#transferItem}
 * is a peek-then-move single-item primitive that leaves one container in the same synchronous main-
 * thread step it enters the other, so an item is never duplicated across the two independently-ticking
 * levels nor voided (see that method's javadoc). One item per cooldown per hopper.
 *
 * <p><b>Cooldown.</b> Vanilla hoppers move on an 8-tick transfer cooldown. Since the captured hopper's
 * native suck/eject no-op against void (so its own {@code cooldownTime} never advances from real I/O),
 * this pass owns the throttle: a per-hopper counter, keyed by local {@code BlockPos}, reset to
 * {@link #TRANSFER_COOLDOWN} after a successful move and decremented every tick otherwise — matching
 * vanilla throughput without reaching into the hopper's private cooldown field. State is held in a
 * {@link WeakHashMap} keyed by the {@link ContraptionLevel}, so it is dropped automatically when the
 * contraption (and its level) is disposed — no manual cleanup, no leak.
 */
public final class ContraptionHopperBridge {

    private ContraptionHopperBridge() {
    }

    /** Vanilla hopper transfer cooldown, in ticks — one item moves at most this often per captured hopper. */
    private static final int TRANSFER_COOLDOWN = 8;

    /**
     * Per-contraption, per-hopper cooldown counters (local {@link BlockPos#asLong} &rarr; ticks left).
     * Weak-keyed by {@link ContraptionLevel}: when a contraption is disposed its whole entry is
     * garbage-collected with the level, so this never needs explicit teardown. Only ever touched on the
     * server main thread (the engine tick), so a plain inner {@link HashMap} is safe.
     */
    private static final Map<ContraptionLevel, Map<Long, Integer>> COOLDOWNS = new WeakHashMap<>();

    /**
     * Runs the hopper I/O bridge for one contraption this tick — called once per contraption from
     * {@code ContraptionEngine.tickAll}, right after render. Scans the contraption's captured cells for
     * vanilla hoppers and, for each whose cooldown has elapsed, attempts to move ONE item between the
     * hopper and the real world (see {@link #tryTransfer}). No-op (beyond decrementing cooldowns) unless
     * the contraption is {@linkplain ContraptionWorlds#isGridAligned docked} onto the real grid and has
     * a live real {@link ServerLevel} to project into. Fail-open per hopper: a single misbehaving
     * transfer can never abort the pass or the master tick loop.
     */
    public static void tick(ContraptionEntity entity) {
        if (entity == null) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level == null || !(level.realLevel() instanceof ServerLevel realLevel)) {
            return;
        }
        Map<Long, Integer> cooldowns = COOLDOWNS.computeIfAbsent(level, k -> new HashMap<>());
        boolean docked = ContraptionWorlds.isGridAligned(level);
        for (BlockPos local : level.localPositions()) {
            BlockEntity be;
            try {
                be = level.getBlockEntity(local);
            } catch (Throwable t) {
                continue;
            }
            if (!(be instanceof HopperBlockEntity hopper)) {
                continue;
            }
            long key = local.asLong();
            int cd = cooldowns.getOrDefault(key, 0);
            if (cd > 0) {
                cooldowns.put(key, cd - 1); // cooldown advances even off-grid, so it never over-throttles on re-dock
                continue;
            }
            if (!docked) {
                continue; // off-grid: real neighbor undefined, so leave the ready (cd==0) hopper ready
            }
            boolean moved = false;
            try {
                moved = tryTransfer(level, realLevel, local, hopper);
            } catch (Throwable ignored) {
                // a single hopper's failure must not break the rest of the pass
            }
            if (moved) {
                cooldowns.put(key, TRANSFER_COOLDOWN);
            }
        }
    }

    /**
     * Attempts ONE item move for a single captured hopper, mirroring vanilla's two hopper choke points
     * against the real world. PUSH takes priority (as vanilla ejects before it sucks): move one item
     * from the hopper into the real container in its facing direction; if that moves nothing, PULL one
     * item from the real container directly above into the hopper. Exactly one item moves per call
     * (whichever direction succeeds first), so throughput is one item per {@link #TRANSFER_COOLDOWN}.
     *
     * <p>Direction semantics match vanilla precisely: the hopper pushes out its {@link HopperBlock#FACING}
     * side — the item enters the destination through the face opposite the real push heading — and pulls
     * from the cell above, taking items out through that source's DOWN face. UP/DOWN are yaw-invariant,
     * so "above" needs no rotation; only the horizontal facing is rotated into the real world (handled
     * by {@link ContraptionWorlds#realBlockNeighbor}/{@link ContraptionWorlds#realDirectionOf}).
     */
    private static boolean tryTransfer(ContraptionLevel level, ServerLevel realLevel, BlockPos local,
            HopperBlockEntity hopper) {
        BlockState state = level.getBlockState(local);
        if (!state.hasProperty(HopperBlock.FACING)) {
            return false;
        }
        Direction localFacing = state.getValue(HopperBlock.FACING);

        // PUSH: hopper -> real container in the (rotated) facing direction.
        Optional<BlockPos> outCell = ContraptionWorlds.realBlockNeighbor(level, local, localFacing);
        Optional<Direction> outHeading = ContraptionWorlds.realDirectionOf(level, localFacing);
        if (outCell.isPresent() && outHeading.isPresent()) {
            Container dest = ContraptionAccessor.realContainerAt(realLevel, outCell.get());
            if (dest != null && dest != hopper
                    // hopper is a plain Container (not worldly) — its `from` face is ignored; the item
                    // enters `dest` through the face opposite the real push heading, exactly like vanilla.
                    && ContraptionAccessor.transferItem(hopper, Direction.DOWN, dest,
                            outHeading.get().getOpposite())) {
                return true;
            }
        }

        // PULL: real container directly above -> hopper (local UP == real UP; yaw-invariant).
        Optional<BlockPos> inCell = ContraptionWorlds.realBlockNeighbor(level, local, Direction.UP);
        if (inCell.isPresent()) {
            Container src = ContraptionAccessor.realContainerAt(realLevel, inCell.get());
            if (src != null && src != hopper
                    // item leaves the source through its DOWN face and enters the hopper from above.
                    && ContraptionAccessor.transferItem(src, Direction.DOWN, hopper, Direction.UP)) {
                return true;
            }
        }
        return false;
    }
}
