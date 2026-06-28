package dev.arubik.craftengine.fluid.graph;

import dev.arubik.craftengine.fluid.FluidCarrierImpl;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.util.CustomBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Phase 3 of the rewrite: take a {@link FluidGraph}, run ONE {@link FluidNetworkSolver} step against the
 * real per-node stores, and write the resulting volume change back. This is the bridge between the solver
 * and the world.
 *
 * <p>Currently driven manually ({@code /cep fluid step}) so it can be validated against the still-live
 * old transport before the always-on per-tick engine + dirty-tracking replace the per-block push/pull.</p>
 *
 * <p>v1 scope: a network carries a single fluid type (the first non-empty node's type). PIPE/TANK nodes
 * are mutated (their store is the shared {@link FluidKeys#FLUID}); PUMP/HANDLER nodes act as fixed-head
 * boundaries (read, not written) since their fluid lives in machine-specific tanks.</p>
 */
public final class FluidEngine {

    private FluidEngine() {
    }

    // ---------------- always-on driver (Phase 3-rest) ----------------

    /** When true, {@link #tickAll} steps every registered network each driver tick. Default off so the
     * solver coexists with the live transport until validated, then flipped on for the cutover. */
    public static volatile boolean ENABLED = true;

    private static final java.util.Set<Long> SEEDS = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /** Register a position whose network the engine should drive (any block in it). */
    public static void registerSeed(BlockPos pos) {
        if (pos != null)
            SEEDS.add(pos.asLong());
    }

    public static int seedCount() {
        return SEEDS.size();
    }

    public static volatile boolean DEBUG = true;
    private static int dbgTick = 0;

    /** Step every distinct registered network once (dedups blocks shared across seeds). */
    public static void tickAll(Level level) {
        if (!ENABLED || SEEDS.isEmpty() || level == null)
            return;
        java.util.Set<Long> handled = new java.util.HashSet<>();
        int nets = 0, moved = 0, biggest = 0;
        for (long key : SEEDS) {
            if (handled.contains(key))
                continue;
            BlockPos seed = BlockPos.of(key);
            FluidGraph g;
            try {
                g = FluidGraphBuilder.build(level, seed);
            } catch (Throwable t) {
                continue;
            }
            if (g.isEmpty()) {
                handled.add(key);
                continue;
            }
            for (FluidNode n : g.nodes)
                handled.add(n.pos.asLong());
            try {
                int m = step(level, g);
                nets++;
                moved += m;
                biggest = Math.max(biggest, g.nodes.size());
            } catch (Throwable ignored) {
            }
        }
        if (DEBUG && (++dbgTick % 20 == 0) && nets > 0)
            System.out.println("[FluidEngine] nets=" + nets + " biggest=" + biggest + " moved=" + moved
                    + " seeds=" + SEEDS.size());
    }

    /** Build specs from the graph + live stores, solve one step, apply ΔV. Returns total mB moved. */
    public static int step(Level level, FluidGraph graph) {
        if (level == null || graph == null || graph.isEmpty())
            return 0;
        int n = graph.nodes.size();

        // Each node's REAL carrier (its valid store — tank PDC, machine fluid tank, etc).
        dev.arubik.craftengine.fluid.behavior.FluidCarrier[] carriers =
                new dev.arubik.craftengine.fluid.behavior.FluidCarrier[n];
        for (int i = 0; i < n; i++)
            carriers[i] = dev.arubik.craftengine.fluid.FluidTransferHelper
                    .getCarrier(level, graph.nodes.get(i).pos).orElse(null);

        // Network fluid type = first non-empty node. Empty network -> nothing to do.
        FluidType netType = FluidType.EMPTY;
        for (int i = 0; i < n; i++) {
            if (carriers[i] == null)
                continue;
            FluidStack s = carriers[i].getStored(level, graph.nodes.get(i).pos);
            if (s != null && !s.isEmpty()) {
                netType = s.getType();
                break;
            }
        }
        if (netType == FluidType.EMPTY)
            return 0;

        FluidNetworkSolver.NodeSpec[] nodes = new FluidNetworkSolver.NodeSpec[n];
        for (int i = 0; i < n; i++) {
            FluidNode fn = graph.nodes.get(i);
            nodes[i] = new FluidNetworkSolver.NodeSpec(Math.max(1.0, fn.capacityMb), fn.head);
        }
        FluidNetworkSolver.BranchSpec[] branches = new FluidNetworkSolver.BranchSpec[graph.edges.size()];
        for (int e = 0; e < branches.length; e++) {
            FluidEdge fe = graph.edges.get(e);
            branches[e] = new FluidNetworkSolver.BranchSpec(fe.a, fe.b, fe.conductance, fe.emf, fe.allowedSign);
        }

        FluidNetworkSolver.Result r = FluidNetworkSolver.solve(nodes, branches, 1.0);

        // Read current amounts.
        int[] old = new int[n];
        int[] pressure = new int[n];
        for (int i = 0; i < n; i++) {
            FluidStack cur = carriers[i] != null ? carriers[i].getStored(level, graph.nodes.get(i).pos) : null;
            old[i] = (cur == null || cur.isEmpty()) ? 0 : cur.getAmount();
            pressure[i] = (cur == null || cur.isEmpty()) ? 0 : cur.getPressure();
        }
        // CONSERVATIVE apply: scale ALL flows by the single factor that keeps every node within [0,cap].
        // The solver seeks head equilibrium (gravity) that may want a node past its capacity; clamping each
        // node independently would create/destroy mass (1 bucket -> tank 5000 bug). Scaling preserves Σ=0.
        double s = 1.0;
        for (int i = 0; i < n; i++) {
            double d = r.netInflow[i];
            if (d > 0) {
                double room = graph.nodes.get(i).capacityMb - old[i];
                if (d > room && d > 1e-9)
                    s = Math.min(s, room / d);
            } else if (d < 0) {
                double avail = old[i];
                if (-d > avail && -d > 1e-9)
                    s = Math.min(s, avail / -d);
            }
        }
        s = Math.max(0.0, s);

        int moved = 0;
        for (int i = 0; i < n; i++) {
            dev.arubik.craftengine.fluid.behavior.FluidCarrier c = carriers[i];
            if (c == null)
                continue;
            int delta = (int) Math.round(r.netInflow[i] * s);
            if (delta == 0)
                continue;
            FluidNode fn = graph.nodes.get(i);
            int newAmt = Math.max(0, Math.min((int) fn.capacityMb, old[i] + delta));
            moved += Math.abs(newAmt - old[i]);
            c.setStoredRaw(level, fn.pos,
                    newAmt <= 0 ? FluidStack.EMPTY : new FluidStack(netType, newAmt, pressure[i]));
            c.onStoreChanged(level, fn.pos); // refresh model (tank fluidtype/level)
        }
        return moved / 2; // each mB shows up as -delta at the source and +delta at the sink
    }
}
