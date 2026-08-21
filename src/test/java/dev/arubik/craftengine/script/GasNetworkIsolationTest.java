package dev.arubik.craftengine.script;

import dev.arubik.craftengine.fluid.graph.FluidNetworkSolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reproduces the "gas motor next to another gas motor donates 50% of its gas" bug at the level of
 * the network solver, and pins the fix.
 *
 * <p>GasEngine models each carrier as a node whose head is its fill fraction and each connection as
 * a branch; {@link FluidNetworkSolver} then drives connected nodes to equal head. Two machines with
 * the same capacity therefore converge on an exact 50/50 split. The fix does not change the solver —
 * it stops GasEngine from creating the branch at all when neither machine's declared {@code io}
 * permits gas to cross that face, and uses {@code allowedSign} for the asymmetric pipe→machine case.
 */
class GasNetworkIsolationTest {

    private static final double CAP = 10_000;
    private static final double CONDUCTANCE = 1000.0;
    private static final double DT = 1.0;

    private static FluidNetworkSolver.NodeSpec node(double amount) {
        return new FluidNetworkSolver.NodeSpec((long) CAP, amount / CAP);
    }

    /** Runs {@code ticks} solver steps, returning the per-node amounts in mB. */
    private static double[] simulate(double[] startAmounts, FluidNetworkSolver.BranchSpec[] branches, int ticks) {
        double[] amt = startAmounts.clone();
        for (int t = 0; t < ticks; t++) {
            FluidNetworkSolver.NodeSpec[] nodes = new FluidNetworkSolver.NodeSpec[amt.length];
            for (int i = 0; i < amt.length; i++) nodes[i] = node(amt[i]);
            FluidNetworkSolver.Result r = FluidNetworkSolver.solve(nodes, branches, DT);
            for (int i = 0; i < amt.length; i++) {
                amt[i] = Math.max(0, Math.min(CAP, amt[i] + r.netInflow[i]));
            }
        }
        return amt;
    }

    @Test
    @DisplayName("BUG: a bidirectional branch equalizes two equal-capacity tanks to 50/50")
    void bidirectionalBranchEqualizesToHalf() {
        // This is what GasEngine used to build for motor<->motor: allowedSign 0, no IO check.
        FluidNetworkSolver.BranchSpec[] branches = {
                new FluidNetworkSolver.BranchSpec(0, 1, CONDUCTANCE, 0.0, 0)
        };
        double[] end = simulate(new double[]{8000, 0}, branches, 200);

        assertEquals(4000, end[0], 50, "full motor should have been drained toward half");
        assertEquals(4000, end[1], 50, "empty neighbour should have been filled to half");
        assertEquals(8000, end[0] + end[1], 1, "gas is conserved, just redistributed");
    }

    @Test
    @DisplayName("FIX: with no permitted edge the two motors never exchange gas")
    void noEdgeMeansNoTransfer() {
        // gas_motor_mk1 declares io.input only (faces: up) and NO gas output, so neither
        // aToB nor bToA holds and GasEngine now emits no branch at all for that face.
        FluidNetworkSolver.BranchSpec[] branches = new FluidNetworkSolver.BranchSpec[0];
        double[] end = simulate(new double[]{8000, 0}, branches, 200);

        assertEquals(8000, end[0], 1e-6, "motor keeps all of its gas");
        assertEquals(0, end[1], 1e-6, "neighbour receives nothing");
    }

    @Test
    @DisplayName("FIX: a pipe still feeds a machine through a one-way branch")
    void oneWayBranchStillFeedsTheMachine() {
        // pipe (node 0, open conduit) -> motor (node 1, accepts gas on its input face).
        // aToB permitted, bToA not: allowedSign = +1.
        FluidNetworkSolver.BranchSpec[] branches = {
                new FluidNetworkSolver.BranchSpec(0, 1, CONDUCTANCE, 0.0, 1)
        };
        double[] end = simulate(new double[]{8000, 0}, branches, 200);

        assertTrue(end[1] > 100, "motor must still be fillable from a pipe, got " + end[1]);
        assertTrue(end[0] < 8000, "pipe must have given gas away, got " + end[0]);
    }

    @Test
    @DisplayName("FIX: a one-way branch never flows backwards out of the machine")
    void oneWayBranchDoesNotDrainTheMachine() {
        // Same +1 branch, but now the MACHINE is the full one. Gas must not flow b->a.
        FluidNetworkSolver.BranchSpec[] branches = {
                new FluidNetworkSolver.BranchSpec(0, 1, CONDUCTANCE, 0.0, 1)
        };
        double[] end = simulate(new double[]{0, 8000}, branches, 200);

        assertEquals(8000, end[1], 1e-6, "machine must not back-feed the pipe");
        assertEquals(0, end[0], 1e-6, "pipe must stay empty");
    }
}
