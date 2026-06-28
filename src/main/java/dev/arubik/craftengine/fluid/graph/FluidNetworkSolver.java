package dev.arubik.craftengine.fluid.graph;

/**
 * Steady-step hydraulic network solver (Phase 2 of the rewrite — see {@code TOADD/fluid_rewrite_roadmap.md}),
 * ported from create-pipes-n-physics {@code engine.solve.NetworkSolver}.
 *
 * <p>Models the pipe network as an electrical-circuit analogue and advances it one tick with an
 * <b>implicit-Euler</b> step on heads:</p>
 *
 * <pre>(C/dt + L) h' = (C/dt) h + pump terms</pre>
 *
 * where {@code C} = node capacitance (mB per block of head), {@code L} = weighted graph Laplacian over
 * the <i>active</i> branches (conductance), and the pump terms inject each branch's {@code emf} (lift in
 * blocks). Implicit Euler ⇒ monotonic convergence, no oscillation — so tanks equalize smoothly and
 * loops cannot ping-pong (the whole reason for the rewrite).
 *
 * <p>An <b>active-set loop</b> enforces one-way constraints ({@code allowedSign}, i.e. check valves /
 * pump direction): solve → compute flows → deactivate any branch whose flow violates its sign → re-solve,
 * terminating in ≤ |branches| rounds.</p>
 *
 * <p>Pure numeric (arrays in, arrays out) — no world dependency, unit-testable. The dense Gaussian path
 * handles the small networks players build; a CG fallback for very large networks is a future addition
 * (see {@code DIRECT_SOLVE_LIMIT}). Crest/siphon gating is a documented follow-up.</p>
 */
public final class FluidNetworkSolver {

    public static final double FLOW_TOLERANCE = 1.0e-7; // mB/tick below which a flow is "zero"
    public static final int DIRECT_SOLVE_LIMIT = 128; // dense Gaussian up to here
    private static final double CAP_REGULARIZATION = 1.0e-9; // keeps capacitance-free components non-singular

    /** A node: capacitance (mB/block of head) and current head (blocks). */
    public static final class NodeSpec {
        public final double capacitance;
        public final double head;

        public NodeSpec(double capacitance, double head) {
            this.capacitance = capacitance;
            this.head = head;
        }
    }

    /** A branch a→b with conductance, pump emf (blocks driving a→b), and a one-way sign constraint. */
    public static final class BranchSpec {
        public final int a;
        public final int b;
        public final double conductance;
        public final double emf;
        public final int allowedSign; // +1 = a→b only, -1 = b→a only, 0 = bidirectional

        public BranchSpec(int a, int b, double conductance, double emf, int allowedSign) {
            this.a = a;
            this.b = b;
            this.conductance = conductance;
            this.emf = emf;
            this.allowedSign = allowedSign;
        }
    }

    public static final class Result {
        public final double[] heads; // end-of-tick head per node
        public final double[] flows; // mB/tick per branch (+ = a→b)
        public final double[] netInflow; // ΔV per node this tick (mB)
        public final boolean[] active; // branch still carrying flow (one-way not violated)

        Result(double[] heads, double[] flows, double[] netInflow, boolean[] active) {
            this.heads = heads;
            this.flows = flows;
            this.netInflow = netInflow;
            this.active = active;
        }
    }

    private FluidNetworkSolver() {
    }

    /** Advance the network one step of length {@code dt} (ticks). */
    public static Result solve(NodeSpec[] nodes, BranchSpec[] branches, double dt) {
        int n = nodes.length;
        int m = branches.length;
        boolean[] active = new boolean[m];
        java.util.Arrays.fill(active, true);

        double[] hPrime = new double[n];
        double[] flows = new double[m];

        // Active-set loop: at most m rounds (each round disables ≥1 branch or converges).
        for (int round = 0; round <= m; round++) {
            hPrime = solveHeads(nodes, branches, active, dt);
            boolean changed = false;
            for (int e = 0; e < m; e++) {
                if (!active[e])
                    continue;
                BranchSpec br = branches[e];
                double q = br.conductance * (hPrime[br.a] - hPrime[br.b] + br.emf);
                flows[e] = q;
                if (br.allowedSign > 0 && q < -FLOW_TOLERANCE) {
                    active[e] = false;
                    changed = true;
                } else if (br.allowedSign < 0 && q > FLOW_TOLERANCE) {
                    active[e] = false;
                    changed = true;
                }
            }
            if (!changed)
                break;
        }

        // Recompute final flows from the converged heads (inactive branches carry 0).
        double[] netInflow = new double[n];
        for (int e = 0; e < m; e++) {
            BranchSpec br = branches[e];
            double q = active[e] ? br.conductance * (hPrime[br.a] - hPrime[br.b] + br.emf) : 0.0;
            flows[e] = q;
            netInflow[br.a] -= q; // a loses q
            netInflow[br.b] += q; // b gains q
        }
        return new Result(hPrime, flows, netInflow, active);
    }

    /** Assemble (C/dt + L) h' = (C/dt) h + pump terms over the ACTIVE branches and solve for h'. */
    private static double[] solveHeads(NodeSpec[] nodes, BranchSpec[] branches, boolean[] active, double dt) {
        int n = nodes.length;
        double[][] A = new double[n][n];
        double[] rhs = new double[n];

        for (int i = 0; i < n; i++) {
            double cdt = nodes[i].capacitance / dt;
            A[i][i] = cdt + CAP_REGULARIZATION;
            rhs[i] = cdt * nodes[i].head + CAP_REGULARIZATION * nodes[i].head;
        }
        for (int e = 0; e < branches.length; e++) {
            if (!active[e])
                continue;
            BranchSpec br = branches[e];
            double g = br.conductance;
            A[br.a][br.a] += g;
            A[br.b][br.b] += g;
            A[br.a][br.b] -= g;
            A[br.b][br.a] -= g;
            // emf drives a→b: q = g(h_a - h_b + emf) ⇒ rhs[a] -= g·emf, rhs[b] += g·emf
            rhs[br.a] -= g * br.emf;
            rhs[br.b] += g * br.emf;
        }
        return gaussianSolve(A, rhs);
    }

    /** Dense Gaussian elimination with partial pivoting. n ≤ {@link #DIRECT_SOLVE_LIMIT} in practice. */
    private static double[] gaussianSolve(double[][] A, double[] b) {
        int n = b.length;
        for (int col = 0; col < n; col++) {
            int piv = col;
            double best = Math.abs(A[col][col]);
            for (int r = col + 1; r < n; r++) {
                double v = Math.abs(A[r][col]);
                if (v > best) {
                    best = v;
                    piv = r;
                }
            }
            if (piv != col) {
                double[] tmp = A[piv];
                A[piv] = A[col];
                A[col] = tmp;
                double tb = b[piv];
                b[piv] = b[col];
                b[col] = tb;
            }
            double diag = A[col][col];
            if (Math.abs(diag) < 1e-12)
                continue; // singular row — leave as-is (regularization should prevent this)
            for (int r = col + 1; r < n; r++) {
                double f = A[r][col] / diag;
                if (f == 0.0)
                    continue;
                for (int c = col; c < n; c++)
                    A[r][c] -= f * A[col][c];
                b[r] -= f * b[col];
            }
        }
        double[] x = new double[n];
        for (int row = n - 1; row >= 0; row--) {
            double sum = b[row];
            for (int c = row + 1; c < n; c++)
                sum -= A[row][c] * x[c];
            double diag = A[row][row];
            x[row] = Math.abs(diag) < 1e-12 ? 0.0 : sum / diag;
        }
        return x;
    }
}
