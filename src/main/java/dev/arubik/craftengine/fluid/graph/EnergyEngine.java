package dev.arubik.craftengine.fluid.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.energy.EnergyCarrier;
import dev.arubik.craftengine.energy.EnergyTransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * CraftEnergy network: solved with the same {@link FluidNetworkSolver} as {@link GasEngine}, with
 * NO gravity term — a node's "head" is just its fill fraction (0..1), so the solver equalizes
 * charge level across the network exactly like gas equalizes pressure. This is the FE-alike cable
 * network: producers/consumers gate flow through {@link EnergyCarrier#canEnergyOutput}/
 * {@link EnergyCarrier#canEnergyInput}, cables are plain open conduits.
 *
 * <p>Self-contained (BFS + specs + apply inline) and ticked from the same scheduler slot as
 * {@link FluidEngine}/{@link GasEngine} — see {@code CraftEnginePolyfills.onEnable}.
 */
public final class EnergyEngine {

    public static volatile boolean ENABLED = true;
    private static final double DEFAULT_CONDUCTANCE = 1000.0;
    private static final int MAX_BLOCKS = 4096;
    private static final Set<Long> SEEDS = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private EnergyEngine() {
    }

    public static void registerSeed(BlockPos pos) {
        if (pos != null)
            SEEDS.add(pos.asLong());
    }

    /** Step every distinct registered energy network once (mirror of GasEngine.tickAll). */
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
                handled.add(key);
                step(level, seed);
            } catch (Throwable ignored) {
            }
        }
    }

    /** Build the energy network containing {@code start}, solve one step, apply the flow. Returns units moved. */
    public static int step(Level level, BlockPos start) {
        if (level == null || start == null || !isCarrier(level, start))
            return 0;

        List<BlockPos> positions = new ArrayList<>();
        Map<Long, Integer> index = new HashMap<>();
        List<int[]> edgePairs = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        visited.add(start.asLong());

        // Per-position lookup caches, scoped to this one BFS: every position is examined as a
        // neighbor from up to 6 directions (once per incident edge) and, when dequeued, its OWN
        // carrier/connectable are re-derived once per outgoing direction too — without this, a
        // single node's block-state lookup + behavior-list search ran up to ~12x per BFS visit
        // instead of once, purely from re-deriving the same (level, pos) answer repeatedly.
        Map<Long, java.util.Optional<EnergyCarrier>> carrierCache = new HashMap<>();
        Map<Long, ConnectableBlockBehavior> connCache = new HashMap<>();

        while (!queue.isEmpty() && positions.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            int ai = idx(index, positions, pos);
            EnergyCarrier selfCarrier = carrier(level, pos, carrierCache);
            ConnectableBlockBehavior selfConn = connectable(level, pos, connCache);
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                EnergyCarrier otherCarrier = carrier(level, np, carrierCache);
                if (otherCarrier == null || !connected(selfConn, connectable(level, np, connCache), level, pos, np, dir))
                    continue;
                int bi = idx(index, positions, np);
                if (ai < bi) {
                    boolean aToB = selfCarrier != null && selfCarrier.canEnergyOutput(level, pos, dir)
                            && otherCarrier.canEnergyInput(level, np, dir.getOpposite());
                    boolean bToA = otherCarrier.canEnergyOutput(level, np, dir.getOpposite())
                            && selfCarrier != null && selfCarrier.canEnergyInput(level, pos, dir);
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

        EnergyCarrier[] carriers = new EnergyCarrier[n];
        for (int i = 0; i < n; i++)
            carriers[i] = EnergyTransferHelper.getCarrier(level, positions.get(i)).orElse(null);

        FluidNetworkSolver.NodeSpec[] nodes = new FluidNetworkSolver.NodeSpec[n];
        long[] caps = new long[n];
        for (int i = 0; i < n; i++) {
            long cap = carriers[i] != null ? Math.max(1L, carriers[i].getEnergyCapacity(level, positions.get(i)))
                    : 1L;
            caps[i] = cap;
            int amt = carriers[i] != null ? carriers[i].getStoredEnergy(level, positions.get(i)) : 0;
            double head = amt / (double) cap; // no gravity term — energy has no gravity/head
            nodes[i] = new FluidNetworkSolver.NodeSpec(cap, head);
        }
        FluidNetworkSolver.BranchSpec[] branches = new FluidNetworkSolver.BranchSpec[edgePairs.size()];
        for (int e = 0; e < branches.length; e++) {
            int[] p = edgePairs.get(e);
            branches[e] = new FluidNetworkSolver.BranchSpec(p[0], p[1], DEFAULT_CONDUCTANCE, 0.0, p[2]);
        }

        FluidNetworkSolver.Result r = FluidNetworkSolver.solve(nodes, branches, 1.0);

        int[] old = new int[n];
        for (int i = 0; i < n; i++)
            old[i] = carriers[i] != null ? carriers[i].getStoredEnergy(level, positions.get(i)) : 0;

        // Edge-based conservative apply (mirror GasEngine/FluidEngine): move each edge's solved flow
        // capped by the source's amount AND the destination's room — mass-conserving, no deadlock.
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
            EnergyCarrier c = carriers[i];
            long key = positions.get(i).asLong();
            // Net change at THIS node from this one step — an ephemeral (non-persisted) reading so
            // a player can right-click a cell/cable and see "how much is actually flowing through
            // the network right now", not just the buffer's static total. Recorded even for a node
            // whose amount didn't change (delta 0 IS the answer while nothing's moving).
            LAST_DELTA.put(key, amt[i] - old[i]);
            if (c == null || amt[i] == old[i])
                continue;
            c.setStoredEnergyRaw(level, positions.get(i), Math.max(0, amt[i]));
        }
        return moved;
    }

    /** Per-node net change (mB-equivalent CE units) from that node's most recent {@link #step}
     * — how much a player watching a cell/cable would see it gain (+) or lose (-) THIS network
     * tick. 0 if the node hasn't been part of a step yet, or genuinely wasn't moving anything. */
    public static int lastDelta(BlockPos pos) {
        if (pos == null) return 0;
        Integer d = LAST_DELTA.get(pos.asLong());
        return d != null ? d : 0;
    }

    private static final Map<Long, Integer> LAST_DELTA = new java.util.concurrent.ConcurrentHashMap<>();

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
        return EnergyTransferHelper.getCarrier(level, pos).isPresent();
    }

    /** BFS-scoped cached carrier lookup — see the cache field comment at the call site. */
    private static EnergyCarrier carrier(Level level, BlockPos pos, Map<Long, java.util.Optional<EnergyCarrier>> cache) {
        return cache.computeIfAbsent(pos.asLong(), k -> EnergyTransferHelper.getCarrier(level, pos)).orElse(null);
    }

    private static ConnectableBlockBehavior connectable(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos))
            return null;
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos))
                .orElse(null);
        if (state == null || state.isEmpty())
            return null;
        return state.behavior().getFirst(ConnectableBlockBehavior.class);
    }

    /** BFS-scoped cached connectable lookup — a sentinel isn't needed since a real ConnectableBlockBehavior
     *  is never null once present; computeIfAbsent correctly re-tries a null result on repeat lookups, which
     *  is fine here since a true null (not a carrier / no chunk) is cheap to re-derive and rare in practice. */
    private static ConnectableBlockBehavior connectable(Level level, BlockPos pos, Map<Long, ConnectableBlockBehavior> cache) {
        ConnectableBlockBehavior cached = cache.get(pos.asLong());
        if (cached != null) return cached;
        ConnectableBlockBehavior computed = connectable(level, pos);
        if (computed != null) cache.put(pos.asLong(), computed);
        return computed;
    }

    private static boolean connected(ConnectableBlockBehavior ca, ConnectableBlockBehavior cb, Level level, BlockPos a, BlockPos b, Direction aToB) {
        if (ca == null || cb == null)
            return false;
        return ca.canConnectTo(level, a, aToB) && cb.canConnectTo(level, b, aToB.getOpposite());
    }
}
