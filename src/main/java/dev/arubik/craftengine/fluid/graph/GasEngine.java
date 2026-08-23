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

    public static volatile boolean ENABLED = true;
    private static final double DEFAULT_CONDUCTANCE = 1000.0;
    private static final int MAX_BLOCKS = 4096;
    private static final java.util.Set<Long> SEEDS = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private GasEngine() {
    }

    public static void registerSeed(BlockPos pos) {
        if (pos != null)
            SEEDS.add(pos.asLong());
    }

    /** Step every distinct registered gas network once (mirror of FluidEngine.tickAll). */
    public static void tickAll(Level level) {
        if (!ENABLED || SEEDS.isEmpty() || level == null)
            return;
        Set<Long> handled = new HashSet<>();
        for (long key : SEEDS) {
            if (handled.contains(key))
                continue;
            BlockPos seed = BlockPos.of(key);
            if (!isCarrier(level, seed)) {
                handled.add(key);
                continue;
            }
            try {
                // mark all positions in this network handled by stepping from the seed
                handled.add(key);
                step(level, seed);
            } catch (Throwable ignored) {
            }
        }
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

        // Per-position lookup caches, scoped to this one BFS — see EnergyEngine.step's identical
        // comment: without these, a single node's block-state lookup + behavior-list search (via
        // carrier/connectable/closedValve) ran once per incident direction instead of once total.
        Map<Long, java.util.Optional<GasCarrier>> carrierCache = new HashMap<>();
        Map<Long, ConnectableBlockBehavior> connCache = new HashMap<>();
        Map<Long, Boolean> valveCache = new HashMap<>();

        while (!queue.isEmpty() && positions.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            int ai = idx(index, positions, pos);
            GasCarrier selfCarrier = carrier(level, pos, carrierCache);
            ConnectableBlockBehavior selfConn = connectable(level, pos, connCache);
            boolean selfClosedValve = closedValve(level, pos, valveCache);
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                GasCarrier otherCarrier = carrier(level, np, carrierCache);
                if (DEBUG && start.equals(pos))
                    System.out.println("[GasConnect] node=" + pos.toShortString() + " dir=" + dir + " np="
                            + np.toShortString() + " isCarrier=" + (otherCarrier != null) + " connected="
                            + (otherCarrier != null && connected(selfConn, connectable(level, np, connCache), level, pos, np, dir)));
                if (otherCarrier == null || !connected(selfConn, connectable(level, np, connCache), level, pos, np, dir))
                    continue;
                int bi = idx(index, positions, np);
                if (ai < bi && !selfClosedValve && !closedValve(level, np, valveCache)) {
                    // Connectivity is not permission. Each carrier's declared gas IO decides which
                    // way (if either) gas may cross this face; carriers that are plain conduits
                    // default to open, so pipes/pumps/tanks behave exactly as before.
                    boolean aToB = selfCarrier != null && selfCarrier.canGasOutput(level, pos, dir)
                            && otherCarrier.canGasInput(level, np, dir.getOpposite());
                    boolean bToA = otherCarrier.canGasOutput(level, np, dir.getOpposite())
                            && selfCarrier != null && selfCarrier.canGasInput(level, pos, dir);
                    if (aToB || bToA)
                        edgePairs.add(new int[] { ai, bi, (aToB && bToA) ? 0 : (aToB ? 1 : -1) });
                }
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
            // p[2] is the allowed flow direction: 0 both ways, +1 a->b only, -1 b->a only.
            branches[e] = new FluidNetworkSolver.BranchSpec(p[0], p[1], DEFAULT_CONDUCTANCE, 0.0, p[2]);
        }

        FluidNetworkSolver.Result r = FluidNetworkSolver.solve(nodes, branches, 1.0);

        int[] old = new int[n];
        for (int i = 0; i < n; i++) {
            GasStack cur = carriers[i] != null ? carriers[i].getStoredGas(level, positions.get(i)) : null;
            old[i] = (cur == null || cur.isEmpty()) ? 0 : cur.getAmount();
        }
        if (DEBUG && n <= 6) {
            StringBuilder sb = new StringBuilder("[GasStepEntry] n=" + n + " netType=" + netType + " edges="
                    + branches.length);
            for (int i = 0; i < n; i++)
                sb.append(" #").append(i).append("(").append(carriers[i] == null ? "NULL"
                        : carriers[i].getClass().getSimpleName()).append(" ").append(old[i]).append("/")
                        .append(caps[i]).append(")");
            System.out.println(sb);
        }
        // EDGE-BASED conservative apply (mirror FluidEngine): move each edge's solved flow capped by the
        // source's amount AND the destination's room. Mass-conserving; no global-scale deadlock. Gas has no
        // gravity (head = fill only), so this equalizes fill across the network = gas-pressure equalization.
        int[] amt = old.clone();
        int moved = 0;
        for (int e = 0; e < branches.length; e++) {
            double q = r.flows[e];
            int from, to;
            if (q > 1e-9) {
                from = branches[e].a;
                to = branches[e].b;
            } else if (q < -1e-9) {
                from = branches[e].b;
                to = branches[e].a;
            } else {
                continue;
            }
            int want = (int) Math.round(Math.abs(q));
            int room = (int) caps[to] - amt[to];
            int m = Math.min(want, Math.min(amt[from], room));
            if (m > 0) {
                amt[from] -= m;
                amt[to] += m;
                moved += m;
            }
        }
        for (int i = 0; i < n; i++) {
            GasCarrier c = carriers[i];
            if (c == null || amt[i] == old[i])
                continue;
            c.setStoredGasRaw(level, positions.get(i),
                    amt[i] <= 0 ? GasStack.EMPTY : new GasStack(netType, amt[i], 0));
        }
        return moved;
    }

    public static volatile boolean DEBUG = false;

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

    /** BFS-scoped cached carrier lookup — see EnergyEngine's identical helper. */
    private static GasCarrier carrier(Level level, BlockPos pos, Map<Long, java.util.Optional<GasCarrier>> cache) {
        return cache.computeIfAbsent(pos.asLong(), k -> GasTransferHelper.getCarrier(level, pos)).orElse(null);
    }

    /** A CLOSED gas valve blocks flow on every incident edge (gas has no gravity, so open = bidirectional). */
    private static boolean closedValve(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos))
            return false; // see GasTransferHelper#getCarrier: an unguarded read here force-loads the chunk
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);
        if (state == null || state.isEmpty())
            return false;
        dev.arubik.craftengine.gas.behavior.GasValveBehavior v = state.behavior()
                .getFirst(dev.arubik.craftengine.gas.behavior.GasValveBehavior.class);
        return v != null && !v.isOpen(level, pos);
    }

    /** BFS-scoped cached valve-state lookup — see EnergyEngine's identical helper pattern. */
    private static boolean closedValve(Level level, BlockPos pos, Map<Long, Boolean> cache) {
        return cache.computeIfAbsent(pos.asLong(), k -> closedValve(level, pos));
    }

    private static ConnectableBlockBehavior connectable(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos))
            return null; // see GasTransferHelper#getCarrier: an unguarded read here force-loads the chunk
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state == null || state.isEmpty())
            return null;
        return state.behavior().getFirst(ConnectableBlockBehavior.class);
    }

    /** BFS-scoped cached connectable lookup — see EnergyEngine's identical helper. */
    private static ConnectableBlockBehavior connectable(Level level, BlockPos pos, Map<Long, ConnectableBlockBehavior> cache) {
        ConnectableBlockBehavior cached = cache.get(pos.asLong());
        if (cached != null) return cached;
        ConnectableBlockBehavior computed = connectable(level, pos);
        if (computed != null) cache.put(pos.asLong(), computed);
        return computed;
    }

    private static boolean connected(ConnectableBlockBehavior ca, ConnectableBlockBehavior cb, Level level, BlockPos a, BlockPos b, Direction aToB) {
        if (ca == null || cb == null) {
            if (DEBUG)
                System.out.println("[GasConn2] a=" + a.toShortString() + " b=" + b.toShortString() + " caNull="
                        + (ca == null) + " cbNull=" + (cb == null));
            return false;
        }
        boolean aOk = ca.canConnectTo(level, a, aToB);
        boolean bOk = cb.canConnectTo(level, b, aToB.getOpposite());
        if (DEBUG && !(aOk && bOk))
            System.out.println("[GasConn2] a=" + a.toShortString() + "(" + ca.getClass().getSimpleName() + " "
                    + ca.getConnectableFaces() + ") dir=" + aToB + " aOk=" + aOk + " | b=" + b.toShortString() + "("
                    + cb.getClass().getSimpleName() + " " + cb.getConnectableFaces() + ") bOk=" + bOk);
        return aOk && bOk;
    }
}
