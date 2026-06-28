package dev.arubik.craftengine.fluid.graph;

import java.util.ArrayList;
import java.util.List;

import dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.BranchSpec;
import dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.NodeSpec;
import dev.arubik.craftengine.fluid.graph.FluidNetworkSolver.Result;

/**
 * 50+ self-contained tests for the hydraulic {@link FluidNetworkSolver} (the single transport core for
 * fluids AND gases). Pure (no world), invoked via {@code /cep fluid test}. Covers conservation,
 * gravity equalization, check valves, pump emf, loops/oscillation, gas (no-gravity), capacity bounds,
 * convergence and real machine/storage/pipe topologies.
 */
public final class FluidSolverTests {

    public static final class Out {
        public int passed;
        public int failed;
        public final List<String> failures = new ArrayList<>();

        void check(String name, boolean ok) {
            if (ok)
                passed++;
            else {
                failed++;
                failures.add(name);
            }
        }
    }

    private FluidSolverTests() {
    }

    private static final double EPS = 1e-6;

    private static NodeSpec tank(double cap, double head) {
        return new NodeSpec(cap, head);
    }

    private static BranchSpec pipe(int a, int b) {
        return new BranchSpec(a, b, 100, 0, 0);
    }

    private static BranchSpec pipeG(int a, int b, double g) {
        return new BranchSpec(a, b, g, 0, 0);
    }

    private static BranchSpec valve(int a, int b, int sign) {
        return new BranchSpec(a, b, 100, 0, sign);
    }

    private static BranchSpec pump(int a, int b, double emf) {
        return new BranchSpec(a, b, 100, emf, 0);
    }

    private static boolean conserved(Result r) {
        double s = 0;
        for (double v : r.netInflow)
            s += v;
        return Math.abs(s) < 1e-4;
    }

    /** Iterate the solver, feeding heads back each step (head = fill on a unit-capacity model). */
    private static double[] converge(NodeSpec[] init, BranchSpec[] br, int steps) {
        double[] heads = new double[init.length];
        for (int i = 0; i < init.length; i++)
            heads[i] = init[i].head;
        for (int s = 0; s < steps; s++) {
            NodeSpec[] ns = new NodeSpec[init.length];
            for (int i = 0; i < init.length; i++)
                ns[i] = new NodeSpec(init[i].capacitance, heads[i]);
            Result r = FluidNetworkSolver.solve(ns, br, 1.0);
            heads = r.heads;
        }
        return heads;
    }

    /** Simulate the engine: solve + CONSERVATIVE scaled apply with capacities, return final amounts. */
    private static int[] simulate(double[] y, long[] cap, int[] amt0, int[][] edges, double[] emf, int steps) {
        int n = y.length;
        int[] amt = amt0.clone();
        for (int step = 0; step < steps; step++) {
            NodeSpec[] ns = new NodeSpec[n];
            for (int i = 0; i < n; i++)
                ns[i] = new NodeSpec(cap[i], y[i] + amt[i] / (double) cap[i]);
            BranchSpec[] br = new BranchSpec[edges.length];
            for (int e = 0; e < edges.length; e++)
                br[e] = new BranchSpec(edges[e][0], edges[e][1], 100, emf == null ? 0 : emf[e], 0);
            Result r = FluidNetworkSolver.solve(ns, br, 1.0);
            double s = 1.0;
            for (int i = 0; i < n; i++) {
                double d = r.netInflow[i];
                if (d > 0) {
                    double room = cap[i] - amt[i];
                    if (d > room && d > 1e-9)
                        s = Math.min(s, room / d);
                } else if (d < 0) {
                    if (-d > amt[i] && -d > 1e-9)
                        s = Math.min(s, amt[i] / -d);
                }
            }
            s = Math.max(0, s);
            for (int i = 0; i < n; i++) {
                amt[i] += (int) Math.round(r.netInflow[i] * s);
                amt[i] = Math.max(0, Math.min((int) cap[i], amt[i]));
            }
        }
        return amt;
    }

    private static int sum(int[] a) {
        int s = 0;
        for (int v : a)
            s += v;
        return s;
    }

    public static Out run() {
        Out o = new Out();

        // ---- engine conservation under capacity + gravity (the "1 bucket -> tank 5000" bug) ----
        {
            // top tank(Y=2,1000mB) - pipe(Y=1,cap1000,0) - bottom tank(Y=0,cap5000,0)
            double[] y = { 2, 1, 0 };
            long[] cap = { 5000, 1000, 5000 };
            int[] a0 = { 1000, 0, 0 };
            int[][] e = { { 0, 1 }, { 1, 2 } };
            int[] fin = simulate(y, cap, a0, e, null, 600);
            o.check("engine_conserve_total", sum(fin) == 1000);
            o.check("engine_no_overflow", fin[0] <= 5000 && fin[1] <= 1000 && fin[2] <= 5000);
            o.check("engine_pools_bottom", fin[2] >= fin[0]); // gravity: bottom holds >= top
        }
        {
            // two equal-Y tanks, one full one empty -> equalize, conserved
            double[] y = { 0, 0 };
            long[] cap = { 5000, 5000 };
            int[] a0 = { 4000, 0 };
            int[] fin = simulate(y, cap, a0, new int[][] { { 0, 1 } }, null, 400);
            o.check("engine_eq_conserve", sum(fin) == 4000);
            o.check("engine_eq_split", Math.abs(fin[0] - fin[1]) < 50);
        }
        {
            // pump lifts from low tank to high tank, conserved, no overflow
            double[] y = { 0, 3 };
            long[] cap = { 5000, 5000 };
            int[] a0 = { 3000, 0 };
            int[] fin = simulate(y, cap, a0, new int[][] { { 0, 1 } }, new double[] { 20 }, 600);
            o.check("engine_pump_conserve", sum(fin) == 3000);
            o.check("engine_pump_raised", fin[1] > 0);
        }
        {
            // tiny amount (1 mB) never duplicates
            double[] y = { 1, 0 };
            long[] cap = { 5000, 5000 };
            int[] fin = simulate(y, cap, new int[] { 1, 0 }, new int[][] { { 0, 1 } }, null, 50);
            o.check("engine_tiny_conserve", sum(fin) == 1);
        }

        // ---- 1-10: two-tank equalization at various heads + conservation ----
        for (int k = 0; k < 6; k++) {
            double h0 = 2 + 2 * k, h1 = 0;
            NodeSpec[] n = { tank(1000, h0), tank(1000, h1) };
            BranchSpec[] b = { pipe(0, 1) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("eq2_toward_" + k, r.heads[0] < h0 - EPS && r.heads[1] > h1 + EPS && r.flows[0] > 0);
            o.check("eq2_conserved_" + k, conserved(r));
        }
        // equal heads -> no flow
        {
            NodeSpec[] n = { tank(1000, 5), tank(1000, 5) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pipe(0, 1) }, 1.0);
            o.check("eq2_equal_noflow", Math.abs(r.flows[0]) < 1e-3);
        }
        // both empty -> no flow
        {
            NodeSpec[] n = { tank(1000, 0), tank(1000, 0) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pipe(0, 1) }, 1.0);
            o.check("eq2_empty_noflow", Math.abs(r.flows[0]) < 1e-3);
        }
        // single node -> nothing
        {
            Result r = FluidNetworkSolver.solve(new NodeSpec[] { tank(1000, 7) }, new BranchSpec[] {}, 1.0);
            o.check("single_node", r.heads[0] > 0 && r.flows.length == 0);
        }
        // no branches, two nodes -> independent, no change
        {
            NodeSpec[] n = { tank(1000, 9), tank(1000, 1) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] {}, 1.0);
            o.check("no_branch_independent", Math.abs(r.heads[0] - 9) < 1e-3 && Math.abs(r.heads[1] - 1) < 1e-3);
        }

        // ---- 11-20: gravity direction (higher Y drains down), 3-tank line, conservation ----
        for (int k = 0; k < 4; k++) {
            // node0 high, node1 low: flow must go 0->1
            NodeSpec[] n = { tank(1000, 10 + k), tank(1000, 0) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pipe(0, 1) }, 1.0);
            o.check("gravity_down_" + k, r.flows[0] > 0);
        }
        {
            NodeSpec[] n = { tank(1000, 12), tank(1000, 6), tank(1000, 0) };
            BranchSpec[] b = { pipe(0, 1), pipe(1, 2) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("line3_conserved", conserved(r));
            o.check("line3_flow_down", r.flows[0] > 0 && r.flows[1] > 0);
            double[] eq = converge(n, b, 400);
            o.check("line3_converges", Math.abs(eq[0] - eq[2]) < 0.2 && Math.abs(eq[0] - 6) < 0.5);
        }
        // 5-tank line converges to common head (mass-weighted mean = 5)
        {
            NodeSpec[] n = new NodeSpec[5];
            for (int i = 0; i < 5; i++)
                n[i] = tank(1000, i * 2.5); // 0,2.5,5,7.5,10 -> mean 5
            BranchSpec[] b = { pipe(0, 1), pipe(1, 2), pipe(2, 3), pipe(3, 4) };
            double[] eq = converge(n, b, 800);
            boolean flat = true;
            for (int i = 0; i < 5; i++)
                flat &= Math.abs(eq[i] - 5) < 0.3;
            o.check("line5_flat5", flat);
        }

        // ---- 21-30: check valves (one-way) ----
        for (int k = 0; k < 3; k++) {
            // a low, b high, valve a->b only: backflow blocked
            NodeSpec[] n = { tank(1000, 0), tank(1000, 10) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { valve(0, 1, +1) }, 1.0);
            o.check("valve_block_back_" + k, !r.active[0] && Math.abs(r.flows[0]) < 1e-3);
        }
        for (int k = 0; k < 3; k++) {
            // a high, b low, valve a->b only: forward allowed
            NodeSpec[] n = { tank(1000, 10), tank(1000, 0) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { valve(0, 1, +1) }, 1.0);
            o.check("valve_allow_fwd_" + k, r.active[0] && r.flows[0] > 0);
        }
        {
            // reverse-sign valve: b->a only, a high -> blocked
            NodeSpec[] n = { tank(1000, 10), tank(1000, 0) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { valve(0, 1, -1) }, 1.0);
            o.check("valve_rev_block", !r.active[0]);
            NodeSpec[] n2 = { tank(1000, 0), tank(1000, 10) };
            Result r2 = FluidNetworkSolver.solve(n2, new BranchSpec[] { valve(0, 1, -1) }, 1.0);
            o.check("valve_rev_allow", r2.active[0] && r2.flows[0] < 0);
        }
        // valve in a 3-node so one path blocked, other flows
        {
            NodeSpec[] n = { tank(1000, 0), tank(1000, 10), tank(1000, 5) };
            BranchSpec[] b = { valve(0, 1, +1), pipe(1, 2) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("valve_mixed_conserved", conserved(r));
            o.check("valve_mixed_blocked", !r.active[0]);
        }

        // ---- 31-38: pump emf lift ----
        for (int k = 1; k <= 4; k++) {
            NodeSpec[] n = { tank(1000, 0), tank(1000, 0) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pump(0, 1, k) }, 1.0);
            o.check("pump_lift_" + k, r.flows[0] > 0 && r.heads[1] > r.heads[0]);
            o.check("pump_conserved_" + k, conserved(r));
        }
        // pump overcomes adverse head when emf big enough
        {
            NodeSpec[] n = { tank(1000, 0), tank(1000, 3) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pump(0, 1, 10) }, 1.0);
            o.check("pump_over_head", r.flows[0] > 0);
        }
        // pump too weak vs adverse head -> little/negative net into b
        {
            NodeSpec[] n = { tank(1000, 0), tank(1000, 20) };
            Result r = FluidNetworkSolver.solve(n, new BranchSpec[] { pump(0, 1, 1) }, 1.0);
            o.check("pump_weak", r.flows[0] < 0); // head difference dominates
        }

        // ---- 39-44: loops / no oscillation ----
        {
            // triangle of equal tanks: no flow, no NaN
            NodeSpec[] n = { tank(1000, 5), tank(1000, 5), tank(1000, 5) };
            BranchSpec[] b = { pipe(0, 1), pipe(1, 2), pipe(2, 0) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            boolean quiet = true;
            for (double q : r.flows)
                quiet &= Math.abs(q) < 1e-2 && !Double.isNaN(q);
            o.check("triangle_quiet", quiet);
        }
        {
            // triangle with one high node: converges flat, no oscillation across steps
            NodeSpec[] n = { tank(1000, 9), tank(1000, 0), tank(1000, 0) };
            BranchSpec[] b = { pipe(0, 1), pipe(1, 2), pipe(2, 0) };
            double[] eq = converge(n, b, 500);
            o.check("triangle_converges", Math.abs(eq[0] - eq[1]) < 0.3 && Math.abs(eq[1] - eq[2]) < 0.3);
            // monotonic: high node never rises above start
            double[] h = { 9, 0, 0 };
            boolean mono = true;
            for (int s = 0; s < 50; s++) {
                NodeSpec[] ns = { tank(1000, h[0]), tank(1000, h[1]), tank(1000, h[2]) };
                Result r = FluidNetworkSolver.solve(ns, b, 1.0);
                if (r.heads[0] > h[0] + 1e-6)
                    mono = false;
                h = r.heads;
            }
            o.check("triangle_monotonic", mono);
        }
        {
            // parallel branches between two nodes (multigraph) — stable
            NodeSpec[] n = { tank(1000, 8), tank(1000, 0) };
            BranchSpec[] b = { pipe(0, 1), pipe(0, 1) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("parallel_conserved", conserved(r));
            o.check("parallel_flow", r.flows[0] > 0 && r.flows[1] > 0);
        }

        // ---- 45-50: conductance, capacitance, dt, isolated components ----
        {
            // higher conductance -> more flow in one step
            NodeSpec[] n = { tank(1000, 10), tank(1000, 0) };
            Result lo = FluidNetworkSolver.solve(n, new BranchSpec[] { pipeG(0, 1, 50) }, 1.0);
            Result hi = FluidNetworkSolver.solve(n, new BranchSpec[] { pipeG(0, 1, 500) }, 1.0);
            o.check("conductance_scales", hi.flows[0] > lo.flows[0]);
        }
        {
            // bigger tank moves head less for same inflow (capacitance)
            NodeSpec[] small = { tank(100, 10), tank(100, 0) };
            NodeSpec[] big = { tank(100000, 10), tank(100000, 0) };
            Result rs = FluidNetworkSolver.solve(small, new BranchSpec[] { pipe(0, 1) }, 1.0);
            Result rb = FluidNetworkSolver.solve(big, new BranchSpec[] { pipe(0, 1) }, 1.0);
            o.check("capacitance_damps", (10 - rs.heads[0]) > (10 - rb.heads[0]));
        }
        {
            // two isolated pairs solve independently
            NodeSpec[] n = { tank(1000, 10), tank(1000, 0), tank(1000, 6), tank(1000, 2) };
            BranchSpec[] b = { pipe(0, 1), pipe(2, 3) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("isolated_pairs", r.flows[0] > 0 && r.flows[1] > 0 && conserved(r));
        }
        {
            // smaller dt -> stiffer implicit step -> LESS head movement toward equilibrium per call.
            NodeSpec[] n = { tank(1000, 10), tank(1000, 0) };
            Result big = FluidNetworkSolver.solve(n, new BranchSpec[] { pipe(0, 1) }, 1.0);
            Result tiny = FluidNetworkSolver.solve(n, new BranchSpec[] { pipe(0, 1) }, 0.1);
            o.check("dt_scales", (10 - tiny.heads[0]) < (10 - big.heads[0]));
        }
        {
            // 10-node chain converges flat (real long pipe run)
            NodeSpec[] n = new NodeSpec[10];
            for (int i = 0; i < 10; i++)
                n[i] = tank(1000, i == 0 ? 20 : 0);
            BranchSpec[] b = new BranchSpec[9];
            for (int i = 0; i < 9; i++)
                b[i] = pipe(i, i + 1);
            double[] eq = converge(n, b, 4000);
            boolean flat = true;
            for (int i = 0; i < 10; i++)
                flat &= Math.abs(eq[i] - 2.0) < 0.4; // 20 spread over 10 -> 2
            o.check("chain10_flat", flat);
        }
        {
            // gas analog: no gravity, fill-only head, two tanks equalize fill
            NodeSpec[] n = { tank(1000, 0.8), tank(1000, 0.0) }; // head = fill fraction
            BranchSpec[] b = { pipe(0, 1) };
            double[] eq = converge(n, b, 300);
            o.check("gas_equalize_fill", Math.abs(eq[0] - eq[1]) < 0.02 && Math.abs(eq[0] - 0.4) < 0.05);
        }
        {
            // pump + check valve in series (real pump->valve->tank): forward flows, no backflow
            NodeSpec[] n = { tank(1000, 0), tank(1000, 0), tank(1000, 0) };
            BranchSpec[] b = { pump(0, 1, 6), valve(1, 2, +1) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            o.check("pump_valve_series", r.flows[0] > 0 && r.active[1] && conserved(r));
        }
        {
            // star: one source tank feeds 4 sinks (manifold) — conserved, all sinks gain
            NodeSpec[] n = { tank(1000, 12), tank(1000, 0), tank(1000, 0), tank(1000, 0), tank(1000, 0) };
            BranchSpec[] b = { pipe(0, 1), pipe(0, 2), pipe(0, 3), pipe(0, 4) };
            Result r = FluidNetworkSolver.solve(n, b, 1.0);
            boolean allGain = r.netInflow[1] > 0 && r.netInflow[2] > 0 && r.netInflow[3] > 0 && r.netInflow[4] > 0;
            o.check("manifold_feeds_all", allGain && conserved(r));
        }
        return o;
    }
}
