package dev.arubik.craftengine.fluid.graph;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.fluid.FluidCarrierImpl;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * Builds a {@link FluidGraph} from the world by BFS over connected fluid-carrier blocks
 * (Phase 1 of the hydraulic rewrite — read-only, does NOT change flow).
 *
 * <p>Phase 1 emits ONE node per fluid block and ONE edge per connected adjacent pair. Edge contraction
 * of 2-connection pass-through pipes (the create-pipes-n-physics optimization) is deferred to Phase 1b;
 * a per-block graph is already correct input for the solver.</p>
 */
public final class FluidGraphBuilder {

    /** Default (max) conductance (mB/tick per block of head diff). Refined per pipe tier in Phase 3. */
    private static final double DEFAULT_CONDUCTANCE = 1000.0;
    /** Minimum conductance for a tank outlet that is only just submerged (slowest trickle). */
    private static final double MIN_CONDUCTANCE = 150.0;
    /** Submergence depth (blocks above the connected member) at which the outlet reaches full speed. */
    private static final double SUBMERGENCE_RAMP = 1.0;
    private static final int MAX_BLOCKS = 4096; // safety cap on a single network scan

    private FluidGraphBuilder() {
    }

    /** Build the connected network containing {@code start}, or an empty graph if it isn't a carrier. */
    public static FluidGraph build(Level level, BlockPos start) {
        FluidGraph graph = new FluidGraph();
        if (level == null || start == null || !isCarrier(level, start))
            return graph;

        Set<Long> visited = new HashSet<>();
        Set<Long> edgeKeys = new HashSet<>(); // dedup edges after multiblock-tank group collapse
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        visited.add(start.asLong());

        while (!queue.isEmpty() && visited.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            // A fluid_block_tank group collapses to ONE node (its controller), so a pipe on any member
            // accesses the whole tank and the engine never N×-counts the shared fluid.
            int aIdx = graph.addNode(makeNode(level, canonical(level, pos)));

            boolean dbgPump = dev.arubik.craftengine.fluid.graph.FluidEngine.DEBUG
                    && pumpAt(level, pos) != null;
            for (Direction dir : Direction.values()) {
                BlockPos np = pos.relative(dir);
                if (dbgPump) {
                    boolean carr = isCarrier(level, np);
                    System.out.println("[PumpConnect] pump=" + pos.toShortString() + " dir=" + dir + " np="
                            + np.toShortString() + " isCarrier=" + carr + " connected="
                            + (carr && connected(level, pos, np, dir)) + " outFace="
                            + pumpAt(level, pos).graphOutFace(level));
                }
                if (!isCarrier(level, np) || !connected(level, pos, np, dir))
                    continue;
                // Two block-tank cells NEVER exchange fluid directly: same group is one collapsed node
                // (internal), and DIFFERENT adjacent groups are SEPARATE tanks (they share only through
                // pipes). Without this, touching tank groups equalized + clamped to the smaller cap and lost
                // fluid. Still BFS into np so the whole network is discovered.
                boolean bothTanks = behaviorAt(level, pos,
                        dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class) != null
                        && behaviorAt(level, np,
                                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class) != null;
                int bIdx = graph.addNode(makeNode(level, canonical(level, np)));
                if (!bothTanks
                        && aIdx < bIdx && edgeKeys.add(((long) aIdx << 32) | (bIdx & 0xffffffffL))) {
                    int valve = valveCheck(level, pos, np);
                    double conductance = DEFAULT_CONDUCTANCE;
                    // Tank ↔ non-tank edge: the connection only flows OUT while the group's fluid surface is
                    // ABOVE the connected member, and the flow SPEED ramps with how deep that member is
                    // submerged (just-covered = min, ≥1 block deep = max). Above the surface => block OUT
                    // (one-way IN only) so a drain stops exactly at the connection height (no siphon below it).
                    int tankSide = loneTankSide(level, pos, np); // 1 = pos is the tank, 2 = np is the tank, 0 = none
                    if (tankSide != 0 && valve != -2) {
                        BlockPos member = tankSide == 1 ? pos : np;
                        BlockPos neighbor = tankSide == 1 ? np : pos;
                        double sub = submergence(level, member, neighbor); // blocks of fluid above the OUTLET face
                        if (sub <= 1e-3) {
                            int gate = tankSide == 1 ? -1 : +1; // block OUT of the tank; allow only fill IN
                            valve = (valve == 0) ? gate : (valve == gate ? valve : -2);
                        } else {
                            double t = Math.min(1.0, sub / SUBMERGENCE_RAMP);
                            conductance = MIN_CONDUCTANCE + (DEFAULT_CONDUCTANCE - MIN_CONDUCTANCE) * t;
                        }
                    }
                    if (valve != -2) {
                        int crestY = Math.max(pos.getY(), np.getY());
                        double emf = pumpEmf(level, pos, np);
                        graph.addEdge(new FluidEdge(aIdx, bIdx, conductance, crestY, emf, valve));
                    }
                }
                if (visited.add(np.asLong()))
                    queue.add(np.immutable());
            }
        }
        return graph;
    }

    // ---------------- helpers ----------------

    private static boolean isCarrier(Level level, BlockPos pos) {
        return FluidTransferHelper.getCarrier(level, pos).isPresent();
    }

    private static dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity pumpAt(Level level,
            BlockPos pos) {
        net.momirealms.craftengine.core.block.entity.BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes
                .getIfLoaded(level, pos);
        return be != null
                && be.controller instanceof dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity m ? m
                        : null;
    }

    /**
     * Valve constraint on the a→b edge: returns -2 if a closed valve is on either end (no flow), else the
     * one-way sign — a valve passes fluid only DOWNWARD (gravity), so the higher end may push to the lower.
     * 0 = no valve / horizontal valve (bidirectional).
     */
    private static int valveCheck(Level level, BlockPos a, BlockPos b) {
        dev.arubik.craftengine.fluid.behavior.ValveBehavior va = behaviorAt(level, a,
                dev.arubik.craftengine.fluid.behavior.ValveBehavior.class);
        dev.arubik.craftengine.fluid.behavior.ValveBehavior vb = behaviorAt(level, b,
                dev.arubik.craftengine.fluid.behavior.ValveBehavior.class);
        if (va == null && vb == null)
            return 0;
        if ((va != null && !va.isOpen(level, a)) || (vb != null && !vb.isOpen(level, b)))
            return -2; // closed -> no flow
        if (a.getY() > b.getY())
            return +1; // a higher -> a→b (downward) only
        if (b.getY() > a.getY())
            return -1; // b higher -> b→a (downward) only
        return 0; // same Y -> bidirectional
    }

    /** Returns 1 if exactly {@code a} is a tank (and b isn't), 2 if exactly {@code b} is, else 0. */
    private static int loneTankSide(Level level, BlockPos a, BlockPos b) {
        boolean ta = behaviorAt(level, a,
                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class) != null;
        boolean tb = behaviorAt(level, b,
                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class) != null;
        if (ta && !tb)
            return 1;
        if (tb && !ta)
            return 2;
        return 0;
    }

    /**
     * Blocks of fluid above the OUTLET face of a tank member toward {@code neighbor}: >0 submerged, ≤0 dry.
     * The outlet sits on the floor of the block for a DOWN or side connection (drains from the bottom), and
     * on the ceiling (memberY+1) for an UP connection — so a hole on the top face only escapes once the
     * column is full, while a hole on the bottom face drains everything.
     */
    private static double submergence(Level level, BlockPos member, BlockPos neighbor) {
        dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior tank = behaviorAt(level, member,
                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class);
        if (tank == null)
            return 0.0;
        BlockPos controller = tank.controllerOf(level, member);
        dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.Group g = tank.scanGroup(level, controller);
        FluidStack stored = tank.getStored(level, controller);
        long cap = Math.max(1L, tank.getCapacity(level, controller));
        double fill = (stored == null || stored.isEmpty()) ? 0.0
                : Math.min(1.0, stored.getAmount() / (double) cap);
        double surfaceY = g.minY + fill * g.height;
        double outletY = member.getY() + (neighbor.getY() > member.getY() ? 1.0 : 0.0); // UP face = ceiling
        return surfaceY - outletY;
    }

    /** emf (blocks of lift) on the edge a→b: a pump drives its OUT face. Positive = a→b. */
    private static double pumpEmf(Level level, BlockPos a, BlockPos b) {
        dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity pa = pumpAt(level, a);
        if (pa != null && b.equals(a.relative(pa.graphOutFace(level))))
            return pa.graphPressure(); // a's OUT points at b -> a drives a→b
        dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity pb = pumpAt(level, b);
        if (pb != null && a.equals(b.relative(pb.graphOutFace(level))))
            return -pb.graphPressure(); // b's OUT points at a -> b drives b→a = -(a→b)
        return 0.0;
    }

    /** Collapse a multiblock-tank member to its group controller; everything else is its own node. */
    private static BlockPos canonical(Level level, BlockPos pos) {
        dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior tank = behaviorAt(level, pos,
                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class);
        return tank != null ? tank.controllerOf(level, pos) : pos;
    }

    private static <T> T behaviorAt(Level level, BlockPos pos, Class<T> type) {
        ImmutableBlockState state = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (state == null || state.isEmpty())
            return null;
        return state.behavior().getFirst(type);
    }

    /** Two adjacent carriers are connected only if BOTH expose a connectable face toward each other. */
    private static boolean connected(Level level, BlockPos a, BlockPos b, Direction aToB) {
        ConnectableBlockBehavior ca = behaviorAt(level, a, ConnectableBlockBehavior.class);
        ConnectableBlockBehavior cb = behaviorAt(level, b, ConnectableBlockBehavior.class);
        if (ca == null || cb == null)
            return false;
        if (!ca.canConnectTo(level, a, aToB) || !cb.canConnectTo(level, b, aToB.getOpposite()))
            return false;
        // I/O-aware: each side must allow fluid in OR out on that local face. Pipes/tanks use Open IO
        // (always true); machines restrict to their configured fluid I/O faces.
        return ioAllowsFluid(level, a, ca, aToB) && ioAllowsFluid(level, b, cb, aToB.getOpposite());
    }

    private static boolean ioAllowsFluid(Level level, BlockPos pos, ConnectableBlockBehavior beh, Direction worldDir) {
        try {
            dev.arubik.craftengine.multiblock.IOConfiguration io = beh.getIOConfiguration(level, pos);
            net.minecraft.world.level.block.state.BlockState st = level.getBlockState(pos);
            net.minecraft.core.Direction local = beh.toLocalDirection(worldDir, st);
            return io.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, local)
                    || io.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, local);
        } catch (Throwable t) {
            return true; // be permissive on error (don't drop valid pipe/tank connections)
        }
    }

    private static FluidNode makeNode(Level level, BlockPos pos) {
        FluidNode.Kind kind = classify(level, pos);
        dev.arubik.craftengine.fluid.behavior.FluidCarrier carrier = FluidTransferHelper.getCarrier(level, pos)
                .orElse(null);
        long capacity = carrier != null ? Math.max(1L, carrier.getCapacity(level, pos)) : 1L;
        FluidStack stored = carrier != null ? carrier.getStored(level, pos) : FluidStack.EMPTY;
        double fill = (stored == null || stored.isEmpty())
                ? 0.0
                : Math.min(1.0, stored.getAmount() / (double) capacity);
        // Hydraulic head = the fluid SURFACE world-Y. For a multiblock tank the node sits at the controller
        // (bottom min-corner) but its column is `height` tall, so the surface is controllerY + fill*height —
        // NOT controllerY + fill. Using the bottom Y made tall tanks under-report their head and only push out
        // through connections at/below the bottom (a mid-height pipe never received). Plain 1-tall carriers
        // keep Y + fill. This makes height gating emergent: a pipe above the surface simply has higher head.
        dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior tank = behaviorAt(level, pos,
                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class);
        int columnH = 1;
        if (tank != null) {
            try {
                columnH = Math.max(1, tank.scanGroup(level, pos).height);
            } catch (Throwable ignored) {
            }
        }
        double head = pos.getY() + fill * columnH; // surface world-Y (gravity/pressure baseline)
        return new FluidNode(pos.immutable(), kind, capacity, head);
    }

    private static FluidNode.Kind classify(Level level, BlockPos pos) {
        if (behaviorAt(level, pos, dev.arubik.craftengine.fluid.behavior.MachinePumpBehavior.class) != null
                || behaviorAt(level, pos, dev.arubik.craftengine.fluid.behavior.PumpBehavior.class) != null)
            return FluidNode.Kind.PUMP;
        if (behaviorAt(level, pos, dev.arubik.craftengine.fluid.behavior.TankBlockBehavior.class) != null)
            return FluidNode.Kind.TANK;
        if (behaviorAt(level, pos, dev.arubik.craftengine.fluid.behavior.PipeBehavior.class) != null)
            return FluidNode.Kind.PIPE;
        return FluidNode.Kind.HANDLER;
    }

}
