package dev.arubik.craftengine.fluid.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * Phase 5: gas network solved with the same {@link FluidNetworkSolver}, but with NO gravity term —
 * gases have no head/lift, so each node's "head" is just its fill fraction (0..1). The solver then
 * equalizes fill across the network (pressure equalization), one-way valves via {@code allowedSign}.
 *
 * <p>Self-contained (BFS + specs + apply inline), gated behind {@link #ENABLED} like {@link FluidEngine}.
 * Uses each block's REAL gas store via {@link GasCarrier} ({@code getStoredGas} / {@code getGasCapacity}
 * / {@code setStoredGasRaw}).</p>
 */
public final class GasEngine {

    public static volatile boolean ENABLED = false;
    private static final double DEFAULT_CONDUCTANCE = 1000.0;
    private static final int MAX_BLOCKS = 4096;

    private GasEngine() {
    }

    /** Build the gas network containing {@code start}, solve one step, apply ΔV. Returns units moved. */
    public static int step(Level level, BlockPos start) {
        if (level == null || start == null || !isCarrier(level, start))
            return 0;

        // BFS the connected gas network.
        List<BlockPos> positions = new ArrayList<>();
        Map<Long, Integer> index = new HashMap<>();
        List<int[]> edgePairs = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        visited.add(start.asLong());

        while (!queue.isEmpty() && positions.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            int ai = idx(index, positions, pos);
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                if (!isCarrier(level, np) || !connected(level, pos, np, dir))
                    continue;
                int bi = idx(index, positions, np);
                if (ai < bi)
                    edgePairs.add(new int[] { ai, bi });
                if (visited.add(np.asLong()))
                    queue.add(np.immutable());
            }
        }

        int n = positions.size();
        if (n == 0)
            return 0;

        GasCarrier[] carriers = new GasCarrier[n];
        for (int i = 0; i < n; i++)
            carriers[i] = GasTransferHelper.getCarrier(level, positions.get(i)).orElse(null);

        GasType netType = null;
        for (int i = 0; i < n; i++) {
            if (carriers[i] == null)
                continue;
            GasStack s = carriers[i].getStoredGas(level, positions.get(i));
            if (s != null && !s.isEmpty()) {
                netType = s.getType();
                break;
            }
        }
        if (netType == null)
            return 0;

        FluidNetworkSolver.NodeSpec[] nodes = new FluidNetworkSolver.NodeSpec[n];
        long[] caps = new long[n];
        for (int i = 0; i < n; i++) {
            long cap = carriers[i] != null ? Math.max(1L, carriers[i].getGasCapacity(level, positions.get(i))) : 1L;
            caps[i] = cap;
            GasStack s = carriers[i] != null ? carriers[i].getStoredGas(level, positions.get(i)) : null;
            int amt = (s == null || s.isEmpty()) ? 0 : s.getAmount();
            double head = amt / (double) cap; // NO Y term: gases have no gravity
            nodes[i] = new FluidNetworkSolver.NodeSpec(cap, head);
        }
        FluidNetworkSolver.BranchSpec[] branches = new FluidNetworkSolver.BranchSpec[edgePairs.size()];
        for (int e = 0; e < branches.length; e++) {
            int[] p = edgePairs.get(e);
            branches[e] = new FluidNetworkSolver.BranchSpec(p[0], p[1], DEFAULT_CONDUCTANCE, 0.0, 0);
        }

        FluidNetworkSolver.Result r = FluidNetworkSolver.solve(nodes, branches, 1.0);

        int moved = 0;
        for (int i = 0; i < n; i++) {
            GasCarrier c = carriers[i];
            if (c == null)
                continue;
            int delta = (int) Math.round(r.netInflow[i]);
            if (delta == 0)
                continue;
            GasStack cur = c.getStoredGas(level, positions.get(i));
            int oldAmt = (cur == null || cur.isEmpty()) ? 0 : cur.getAmount();
            int newAmt = Math.max(0, Math.min((int) caps[i], oldAmt + delta));
            moved += Math.abs(newAmt - oldAmt);
            c.setStoredGasRaw(level, positions.get(i), newAmt <= 0 ? GasStack.EMPTY : new GasStack(netType, newAmt, 0));
        }
        return moved / 2;
    }

    // ---------------- helpers ----------------

    private static int idx(Map<Long, Integer> index, List<BlockPos> positions, BlockPos pos) {
        Integer existing = index.get(pos.asLong());
        if (existing != null)
            return existing;
        int i = positions.size();
        positions.add(pos.immutable());
        index.put(pos.asLong(), i);
        return i;
    }

    private static boolean isCarrier(Level level, BlockPos pos) {
        return GasTransferHelper.getCarrier(level, pos).isPresent();
    }

    private static ConnectableBlockBehavior connectable(Level level, BlockPos pos) {
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state == null || state.isEmpty())
            return null;
        return state.behavior().getFirst(ConnectableBlockBehavior.class);
    }

    private static boolean connected(Level level, BlockPos a, BlockPos b, Direction aToB) {
        ConnectableBlockBehavior ca = connectable(level, a);
        ConnectableBlockBehavior cb = connectable(level, b);
        if (ca == null || cb == null)
            return false;
        return ca.canConnectTo(level, a, aToB) && cb.canConnectTo(level, b, aToB.getOpposite());
    }
}
