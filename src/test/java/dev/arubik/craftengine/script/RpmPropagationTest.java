package dev.arubik.craftengine.script;

import dev.arubik.craftengine.rotation.RpmPropagation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the kinetic propagation rules, and simulates whole networks tick by tick to reproduce the
 * three reported failures: the gearbox destroying itself, kinetic models flickering between
 * activated and not, and chains of stacked cogwheels never connecting.
 */
class RpmPropagationTest {

    private static final int MAX = Integer.MAX_VALUE;

    // ---------------------------------------------------------------- rules

    @Nested
    @DisplayName("provider distance")
    class ProviderDistance {

        @Test
        @DisplayName("a real source is distance 0 even before its script has stamped it")
        void genuineSourceIsZero() {
            // The motor declares no rpm input, so it originates. Its action script only records
            // distance 0 at the END of its tick, and block-entity order is arbitrary, so the
            // stored value is still MAX when a neighbour looks.
            assertEquals(0, RpmPropagation.providerDistance(true, false, MAX));
        }

        @Test
        @DisplayName("an emitting RELAY keeps its real distance and never claims to be the origin")
        void relayKeepsItsDistance() {
            // The regression that destroyed gearboxes: a cog/shaft re-emitting via set_rpm_output
            // is also rpmSourceActive, and calling that "distance 0" made every downstream block
            // look like an origin to its own upstream.
            assertEquals(3, RpmPropagation.providerDistance(true, true, 3));
        }

        @Test
        @DisplayName("an idle provider reports whatever it stored")
        void idleProviderUsesStoredDistance() {
            assertEquals(7, RpmPropagation.providerDistance(false, true, 7));
            assertEquals(MAX, RpmPropagation.providerDistance(false, true, MAX));
        }
    }

    @Nested
    @DisplayName("pull eligibility")
    class PullEligibility {

        @Test
        @DisplayName("power only flows from strictly closer to the source")
        void onlyFromCloser() {
            assertTrue(RpmPropagation.canPullFrom(0, 1));
            assertTrue(RpmPropagation.canPullFrom(2, MAX));
            assertFalse(RpmPropagation.canPullFrom(2, 2), "equal distance would let two blocks feed each other");
            assertFalse(RpmPropagation.canPullFrom(3, 2), "must never pull from downstream");
        }

        @Test
        @DisplayName("a gearbox does not pull back from the block it just drove")
        void gearboxDoesNotPullFromItsOwnOutput() {
            int gearbox = 2;
            // The shaft the gearbox drives sits one hop farther and is itself emitting.
            int downstream = RpmPropagation.providerDistance(true, true, gearbox + 1);
            assertFalse(RpmPropagation.canPullFrom(downstream, gearbox),
                    "pulling from its own output is what fed the gearbox an inverted value");
        }
    }

    @Nested
    @DisplayName("conflict detection")
    class Conflicts {

        @Test
        @DisplayName("two different live speeds conflict")
        void differentSpeedsConflict() {
            assertTrue(RpmPropagation.isConflict(32f, -32f));
            assertTrue(RpmPropagation.isConflict(32f, 64f));
        }

        @Test
        @DisplayName("a zero feed is 'nothing here', never a conflict")
        void zeroIsNotAConflict() {
            // A build must never be destroyed just because one side has no power.
            assertFalse(RpmPropagation.isConflict(0f, 32f));
            assertFalse(RpmPropagation.isConflict(32f, 0f));
            assertFalse(RpmPropagation.isConflict(0f, 0f));
        }

        @Test
        @DisplayName("two feeds from the SAME source never conflict, however different")
        void sameSourceIsNeverAConflict() {
            // Reported: adjusting a motor's RPM destroyed two cogwheels driven by that same motor.
            // A speed change reaches the build over several ticks, so mid-change one route still
            // carries the old value — same source, so not a conflict at all.
            long stamp = 1234L;
            assertFalse(RpmPropagation.isConflict(32f, 64f, stamp, stamp),
                    "one motor cannot fight itself");
            assertFalse(RpmPropagation.isConflict(32f, -32f, stamp, stamp));
        }

        @Test
        @DisplayName("independent sources still conflict")
        void differentSourcesStillConflict() {
            assertTrue(RpmPropagation.isConflict(32f, 64f, 100L, 200L));
        }

        @Test
        @DisplayName("an unstamped feed is not silently exempted")
        void unstampedFeedsStillConflict() {
            assertTrue(RpmPropagation.isConflict(32f, 64f, -1L, -1L),
                    "two 'no source' feeds must not count as the same source");
        }

        @Test
        @DisplayName("a conflict must persist before it costs the player a block")
        void conflictMustPersist() {
            assertFalse(RpmPropagation.conflictShouldBreak(1), "one tick is a transition");
            assertFalse(RpmPropagation.conflictShouldBreak(
                    RpmPropagation.CONFLICT_TICKS_BEFORE_BREAK - 1));
            assertTrue(RpmPropagation.conflictShouldBreak(
                    RpmPropagation.CONFLICT_TICKS_BEFORE_BREAK));
        }

        @Test
        @DisplayName("the grace outlasts a speed change spreading through a build")
        void graceOutlastsASpeedChange() {
            // A change has to cross the chain; the break threshold must be longer than that.
            assertTrue(RpmPropagation.CONFLICT_TICKS_BEFORE_BREAK > RpmPropagation.MAX_PULL_BACKOFF,
                    "breaking sooner than a scan interval would destroy blocks mid-adjustment");
        }

        @Test
        @DisplayName("identical speeds on one shaft are fine")
        void sameSpeedIsFine() {
            assertFalse(RpmPropagation.isConflict(32f, 32f));
            assertFalse(RpmPropagation.isConflict(32f, 32.001f), "within epsilon");
        }
    }

    @Nested
    @DisplayName("inversion")
    class Inversion {
        @Test
        @DisplayName("an inverted output face reverses the value, a same face does not")
        void inversion() {
            assertEquals(-32f, RpmPropagation.applyInversion(32f, true));
            assertEquals(32f, RpmPropagation.applyInversion(32f, false));
        }
    }

    @Nested
    @DisplayName("pull scheduling")
    class PullScheduling {

        @Test
        @DisplayName("a pure source never pulls")
        void sourceNeverPulls() {
            assertFalse(RpmPropagation.shouldPull(true, false, true));
        }

        @Test
        @DisplayName("a relay keeps pulling while emitting, so it notices its source stopping")
        void relayKeepsPulling() {
            assertTrue(RpmPropagation.shouldPull(true, true, true));
        }

        @Test
        @DisplayName("a non-kinetic machine never pulls")
        void nonKineticNeverPulls() {
            assertFalse(RpmPropagation.shouldPull(false, true, false));
        }

        @Test
        @DisplayName("a value outlives the slowest relaying block's action interval")
        void freshnessCoversCogwheelInterval() {
            // cogwheel_small / cogwheel_large tick their script every 2 ticks; if a value expired
            // between two refreshes the block would blink.
            assertTrue(RpmPropagation.SOURCE_MAX_AGE > 2,
                    "freshness must outlast the cogwheel action_interval of 2");
        }

        @Test
        @DisplayName("freshness is measured against the source, not per hop")
        void freshnessIsAbsolute() {
            assertTrue(RpmPropagation.isFresh(100L, 100L), "same tick");
            assertTrue(RpmPropagation.isFresh(100L, 100L + RpmPropagation.SOURCE_MAX_AGE));
            assertFalse(RpmPropagation.isFresh(100L, 100L + RpmPropagation.SOURCE_MAX_AGE + 1));
            assertFalse(RpmPropagation.isFresh(-1L, 100L), "never stamped is never fresh");
        }
    }

    // ------------------------------------------------------- network simulation

    /** A kinetic block, reduced to the state the propagation rules actually read. */
    static final class Node {
        final String name;
        final boolean relay;          // declares an rpm input face
        final List<Node> pullFrom = new ArrayList<>();   // neighbours reachable by our own pull
        final List<Node> relayTo = new ArrayList<>();    // neighbours we drive via relay_to
        float rpm = 0;
        boolean emitting = false;
        int distance = MAX;
        /** Tick the SOURCE behind this value produced it; -1 when there is none. */
        long stamp = -1L;
        /** activated state as `status` would report it, sampled once per tick. */
        final List<Boolean> activationHistory = new ArrayList<>();

        Node(String name, boolean relay) { this.name = name; this.relay = relay; }
    }

    /** A source: emits a fixed rpm, declares no input. */
    private static Node source(String name, float rpm) {
        Node n = new Node(name, false);
        n.rpm = rpm;
        n.emitting = true;
        n.distance = 0;
        n.stamp = 0L;
        return n;
    }

    private static Node relay(String name) { return new Node(name, true); }

    /** Links a -> b as a pull edge (b can pull from a) — how shafts and gearboxes connect. */
    private static void pullEdge(Node from, Node to) { to.pullFrom.add(from); }

    /** Links a -> b as a relay edge — how cogwheels mesh, across the axis their pull cannot see. */
    private static void relayEdge(Node from, Node to) { from.relayTo.add(to); }

    /**
     * One game tick over every node, in the given order. Order is a parameter because
     * block-entity iteration order is arbitrary in game and several bugs only showed up in one
     * of the two orders.
     */
    private static long now = 0L;

    private static void tick(List<Node> order) {
        now++;
        for (Node n : order) {
            // A genuine source mints a fresh stamp every tick it produces power.
            if (!n.relay && n.emitting) n.stamp = now;

            boolean powered = n.emitting || n.distance < MAX;
            if (RpmPropagation.shouldPull(true, n.relay, n.emitting)
                    && RpmPropagation.pullDueThisTick(0, now, powered)) {
                float delivered = 0;
                int best = MAX;
                long bestStamp = -1L;
                for (Node p : n.pullFrom) {
                    int pd = RpmPropagation.providerDistance(p.emitting, p.relay, p.distance);
                    if (!RpmPropagation.canPullFrom(pd, n.distance)) continue;
                    if (pd < best) { best = pd; delivered = p.rpm; bestStamp = p.stamp; }
                }
                if (best < MAX) {
                    n.rpm = delivered;
                    n.distance = RpmPropagation.distanceAfterPull(best);
                    n.stamp = bestStamp;              // carry the source's stamp, do not mint one
                    n.emitting = delivered != 0;
                } else if (n.relay && RpmPropagation.isFresh(n.stamp, now)) {
                    // Found nothing, but the value we were handed has not expired. Keep the whole
                    // reading including the DISTANCE — dropping it to MAX would make the next hop
                    // see us as infinitely far away and the chain would never form.
                } else {
                    n.distance = MAX;
                    n.emitting = false;
                    n.rpm = 0;
                    n.stamp = -1L;
                }
            }

            // The action script re-emits, then drives meshed neighbours.
            if (!n.emitting) continue;
            if (!RpmPropagation.isFresh(n.stamp, now)) continue;   // we are stale ourselves
            for (Node t : n.relayTo) {
                int myDist = RpmPropagation.providerDistance(n.emitting, n.relay, n.distance);
                int theirDist = RpmPropagation.providerDistance(t.emitting, t.relay, t.distance);
                if (!RpmPropagation.canPullFrom(myDist, theirDist)) continue;   // anti-loop
                t.rpm = -n.rpm;                       // meshed gears reverse
                t.emitting = t.rpm != 0;
                t.distance = RpmPropagation.distanceAfterPull(myDist);
                t.stamp = n.stamp;                    // the SOURCE's stamp, passed on unchanged
            }
        }
        for (Node n : order) n.activationHistory.add(n.rpm != 0);
    }

    /** Runs until every relay has stopped, returning how many ticks that took. */
    private static int ticksUntilStopped(List<Node> all, List<Node> relays, int limit) {
        for (int i = 1; i <= limit; i++) {
            tick(all);
            boolean allStopped = true;
            for (Node r : relays) if (r.rpm != 0) { allStopped = false; break; }
            if (allStopped) return i;
        }
        return -1;
    }

    private static boolean stableOn(Node n, int lastNTicks) {
        List<Boolean> h = n.activationHistory;
        if (h.size() < lastNTicks) return false;
        for (int i = h.size() - lastNTicks; i < h.size(); i++) if (!h.get(i)) return false;
        return true;
    }

    @org.junit.jupiter.api.BeforeEach
    void resetClock() { now = 0L; }

    @Test
    @DisplayName("motor -> shaft: connects and stays steadily activated")
    void motorDrivesShaft() {
        Node motor = source("motor", 32);
        Node shaft = relay("shaft");
        pullEdge(motor, shaft);
        List<Node> all = List.of(motor, shaft);

        for (int i = 0; i < 20; i++) tick(all);

        assertEquals(32f, shaft.rpm, 1e-6, "shaft takes the motor's speed");
        assertEquals(1, shaft.distance, "shaft is one hop from the motor");
        assertTrue(stableOn(shaft, 10), "shaft must not flicker: " + shaft.activationHistory);
    }

    @Test
    @DisplayName("motor -> shaft works regardless of block-entity tick order")
    void motorDrivesShaftInEitherOrder() {
        for (boolean shaftFirst : new boolean[]{false, true}) {
            Node motor = source("motor", 32);
            Node shaft = relay("shaft");
            pullEdge(motor, shaft);
            List<Node> order = shaftFirst ? List.of(shaft, motor) : List.of(motor, shaft);

            for (int i = 0; i < 20; i++) tick(order);

            assertEquals(32f, shaft.rpm, 1e-6,
                    "tick order must not decide whether the shaft connects (shaftFirst=" + shaftFirst + ")");
        }
    }

    @Test
    @DisplayName("cog meshed onto a cog connects and neither flickers")
    void cogDrivesCog() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);      // cogA sits on the motor's axis
        relayEdge(cogA, cogB);      // cogB meshes across the axis — pull cannot see it
        List<Node> all = List.of(motor, cogA, cogB);

        for (int i = 0; i < 30; i++) tick(all);

        assertEquals(-32f, cogB.rpm, 1e-6, "meshed cog turns the other way at the same speed");
        assertTrue(stableOn(cogB, 15),
                "the relayed cog must stay activated, not blink: " + cogB.activationHistory);
    }

    @Test
    @DisplayName("a three-cog chain fully connects (the stacked-cog case that never linked)")
    void cogChainConnects() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        Node cogC = relay("cogC");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        relayEdge(cogB, cogC);
        List<Node> all = List.of(motor, cogA, cogB, cogC);

        for (int i = 0; i < 40; i++) tick(all);

        assertEquals(1, cogA.distance);
        assertEquals(2, cogB.distance, "each mesh is one hop farther from the source");
        assertEquals(3, cogC.distance, "the third cog never linked before: its driver kept resetting to MAX");
        assertEquals(32f, cogA.rpm, 1e-6);
        assertEquals(-32f, cogB.rpm, 1e-6);
        assertEquals(32f, cogC.rpm, 1e-6, "two reversals cancel");
        assertTrue(stableOn(cogC, 15), "end of chain must be steady: " + cogC.activationHistory);
    }

    @Test
    @DisplayName("a relayed cog never reports a conflict against its own driver")
    void relayedCogDoesNotConflictWithDriver() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        pullEdge(cogB, cogA);   // cogA can also *see* cogB as a neighbour

        List<Node> all = List.of(motor, cogA, cogB);
        for (int i = 0; i < 20; i++) tick(all);

        // cogB is downstream (distance 2) so cogA must refuse to pull from it; if it did, it would
        // read -32 against its own +32 and the conflict rule would destroy a block.
        int cogBAsProvider = RpmPropagation.providerDistance(cogB.emitting, cogB.relay, cogB.distance);
        assertFalse(RpmPropagation.canPullFrom(cogBAsProvider, cogA.distance),
                "upstream must never pull from its own downstream");
    }

    @Test
    @DisplayName("the whole chain stops within the grace window when the source dies")
    void chainStopsWhenSourceStops() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        List<Node> all = List.of(motor, cogA, cogB);

        for (int i = 0; i < 20; i++) tick(all);
        assertEquals(-32f, cogB.rpm, 1e-6, "running before the motor stops");

        // Motor runs out of gas.
        motor.rpm = 0;
        motor.emitting = false;
        motor.distance = MAX;
        for (int i = 0; i < RpmPropagation.SOURCE_MAX_AGE + 10; i++) tick(all);

        assertEquals(0f, cogA.rpm, 1e-6, "the relay must notice its source stopping");
        assertEquals(0f, cogB.rpm, 1e-6, "and the whole chain must wind down, not latch on forever");
    }

    // ------------------------------------------------------------- gearbox

    /**
     * The six block faces as (axis, axisDirection sign), matching NMS Direction.
     * Ported 1:1 from Create's RotationPropagator.getAxisModifier so the expectations below are
     * the mod's real behaviour, not a guess.
     */
    private enum Face {
        DOWN(1, -1), UP(1, +1), NORTH(2, -1), SOUTH(2, +1), WEST(0, -1), EAST(0, +1);
        final int axis, sign;
        Face(int axis, int sign) { this.axis = axis; this.sign = sign; }
        Face opposite() {
            return switch (this) {
                case DOWN -> UP; case UP -> DOWN;
                case NORTH -> SOUTH; case SOUTH -> NORTH;
                case WEST -> EAST; case EAST -> WEST;
            };
        }
    }
    // ------------------------------------------------- orphaned kinetic networks

    @Test
    @DisplayName("two cogs that mesh BOTH ways do not sustain each other")
    void mutuallyMeshedCogsDoNotSelfFeed() {
        // The reported build: cog next to cog, each able to drive the other.
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        relayEdge(cogB, cogA);          // the back-edge that made the pair a closed loop
        List<Node> all = List.of(motor, cogA, cogB);

        for (int i = 0; i < 20; i++) tick(all);
        assertTrue(cogA.rpm != 0 && cogB.rpm != 0, "both turning while driven");

        cogA.pullFrom.clear();
        for (int i = 0; i < RpmPropagation.SOURCE_MAX_AGE + 20; i++) tick(all);

        assertEquals(0f, cogA.rpm, 1e-6, "the loop must not sustain itself");
        assertEquals(0f, cogB.rpm, 1e-6);
    }

    @Test
    @DisplayName("a long chain unwinds completely, not just at its head")
    void longChainUnwindsEntirely() {
        Node motor = source("motor", 32);
        Node a = relay("a"), b = relay("b"), c = relay("c"), d = relay("d");
        pullEdge(motor, a);
        relayEdge(a, b);
        relayEdge(b, c);
        relayEdge(c, d);
        List<Node> all = List.of(motor, a, b, c, d);

        for (int i = 0; i < 40; i++) tick(all);
        assertTrue(d.rpm != 0, "the whole chain is driven first");

        a.pullFrom.clear();
        for (int i = 0; i < (RpmPropagation.SOURCE_MAX_AGE + 2) * 5; i++) tick(all);

        for (Node n : List.of(a, b, c, d)) {
            assertEquals(0f, n.rpm, 1e-6, n.name + " must wind down");
            assertEquals(MAX, n.distance, n.name + " must forget its distance");
        }
    }

    @Test
    @DisplayName("a stopped motor stops the chain just like a removed one")
    void stoppedMotorAlsoStopsTheChain() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        List<Node> all = List.of(motor, cogA, cogB);

        for (int i = 0; i < 20; i++) tick(all);
        motor.rpm = 0;
        motor.emitting = false;
        motor.distance = MAX;
        for (int i = 0; i < RpmPropagation.SOURCE_MAX_AGE + 10; i++) tick(all);

        assertEquals(0f, cogA.rpm, 1e-6);
        assertEquals(0f, cogB.rpm, 1e-6);
    }

    @Test
    @DisplayName("the chain restarts when a motor comes back")
    void chainRestartsWhenSourceReturns() {
        Node motor = source("motor", 32);
        Node cogA = relay("cogA");
        Node cogB = relay("cogB");
        pullEdge(motor, cogA);
        relayEdge(cogA, cogB);
        List<Node> all = List.of(motor, cogA, cogB);

        for (int i = 0; i < 20; i++) tick(all);
        motor.rpm = 0; motor.emitting = false; motor.distance = MAX;
        for (int i = 0; i < RpmPropagation.SOURCE_MAX_AGE + 10; i++) tick(all);
        assertEquals(0f, cogB.rpm, 1e-6, "stopped first");

        // Nothing may latch: putting the motor back must bring the whole chain up again.
        motor.rpm = 32; motor.emitting = true; motor.distance = 0;
        for (int i = 0; i < 20; i++) tick(all);
        assertEquals(32f, cogA.rpm, 1e-6);
        assertEquals(-32f, cogB.rpm, 1e-6);
    }

    // ------------------------------------------------------- neighbour-scan backoff

    @Nested
    @DisplayName("pull backoff")
    class PullBackoff {

        @Test
        @DisplayName("a block that just changed scans every tick")
        void unstableScansEveryTick() {
            assertEquals(1, RpmPropagation.pullInterval(0));
            assertEquals(1, RpmPropagation.pullInterval(7));
            for (long t = 0; t < 10; t++) {
                assertTrue(RpmPropagation.pullDueThisTick(0, t, false), "tick " + t);
            }
        }

        @Test
        @DisplayName("a POWERED block never backs off, however settled it looks")
        void poweredNeverBacksOff() {
            // Backing off a running chain broke cogwheels outright: a hop only copies its
            // source's stamp when it scans, so the stamp aged a whole interval per hop and long
            // chains expired before they could establish.
            for (long t = 0; t < 40; t++) {
                assertTrue(RpmPropagation.pullDueThisTick(100000, t, true),
                        "a powered block must re-scan every tick, missed at " + t);
            }
        }

        @Test
        @DisplayName("the freshness window outlasts the worst backoff plus a slow action interval")
        void freshnessOutlastsBackoff() {
            // The regression in one assertion: SOURCE_MAX_AGE was equal to MAX_PULL_BACKOFF, so a
            // backed-off block let its stamp expire before it looked again.
            int slowestActionInterval = 2;   // cogwheel_small / cogwheel_large
            assertTrue(RpmPropagation.SOURCE_MAX_AGE
                            > RpmPropagation.MAX_PULL_BACKOFF + slowestActionInterval,
                    "SOURCE_MAX_AGE=" + RpmPropagation.SOURCE_MAX_AGE
                            + " must exceed backoff " + RpmPropagation.MAX_PULL_BACKOFF
                            + " + action interval " + slowestActionInterval);
        }

        @Test
        @DisplayName("a settled block backs off, but never past the cap")
        void settledBacksOff() {
            assertEquals(2, RpmPropagation.pullInterval(8));
            assertEquals(2, RpmPropagation.pullInterval(23));
            assertEquals(RpmPropagation.MAX_PULL_BACKOFF, RpmPropagation.pullInterval(24));
            assertEquals(RpmPropagation.MAX_PULL_BACKOFF, RpmPropagation.pullInterval(100000));
        }

        @Test
        @DisplayName("the cap bounds how late a speed change can be noticed")
        void backoffStaysImperceptible() {
            // A speed change is the one thing that does not fire a neighbour update, so the cap
            // is also the worst-case delay before the chain sees it.
            assertTrue(RpmPropagation.MAX_PULL_BACKOFF <= 4,
                    "more than 4 ticks would start to be visible in play");
        }

        @Test
        @DisplayName("an idle block still scans regularly, just less often")
        void settledStillScans() {
            int scans = 0;
            for (long t = 0; t < 100; t++) {
                if (RpmPropagation.pullDueThisTick(1000, t, false)) scans++;
            }
            assertEquals(100 / RpmPropagation.MAX_PULL_BACKOFF, scans,
                    "a settled block must keep checking, just at the backed-off interval");
            assertTrue(scans > 0, "backoff must never mean 'never scan again'");
        }

        @Test
        @DisplayName("backing off cuts most of the scanning on a running build")
        void backoffSavesMostScans() {
            int eager = 0, settled = 0;
            for (long t = 0; t < 1000; t++) {
                if (RpmPropagation.pullDueThisTick(0, t, false)) eager++;
                if (RpmPropagation.pullDueThisTick(1000, t, false)) settled++;
            }
            assertEquals(1000, eager);
            assertTrue(settled <= eager / 4,
                    "an idle block should do at most a quarter of the scans, got " + settled);
        }
    }

    // ------------------------------------------- reported builds: how fast they stop

    /** The maximum any chain may take to wind down, whatever its length. */
    private static int windDownBudget() {
        return RpmPropagation.SOURCE_MAX_AGE + 3;
    }

    @Test
    @DisplayName("four meshed cogs stop promptly, not after two seconds")
    void fourCogsStopPromptly() {
        // Reported: four small cogwheels took roughly 40 ticks (~2s) to stop. That was a per-hop
        // countdown — each cog only died after its OWN budget expired, so the delay stacked with
        // chain length. With one shared source stamp they all expire together.
        Node motor = source("motor", 32);
        Node shaft = relay("shaft");
        Node c1 = relay("c1"), c2 = relay("c2"), c3 = relay("c3"), c4 = relay("c4");
        pullEdge(motor, shaft);
        pullEdge(shaft, c1);
        relayEdge(c1, c2);
        relayEdge(c2, c3);
        relayEdge(c3, c4);
        List<Node> all = List.of(motor, shaft, c1, c2, c3, c4);
        List<Node> relays = List.of(shaft, c1, c2, c3, c4);

        for (int i = 0; i < 30; i++) tick(all);
        assertTrue(c4.rpm != 0, "the whole train runs first");

        shaft.pullFrom.clear();   // break the shaft off the motor
        int took = ticksUntilStopped(all, relays, 200);

        assertTrue(took > 0, "the train must stop at all");
        assertTrue(took <= windDownBudget(),
                "should stop within " + windDownBudget() + " ticks, took " + took);
    }

    @Test
    @DisplayName("wind-down time does not grow with chain length")
    void windDownIsIndependentOfLength() {
        int shortChain = windDown(2);
        int longChain = windDown(12);
        assertTrue(longChain <= windDownBudget(),
                "a 12-hop chain must stop just as fast, took " + longChain);
        assertTrue(Math.abs(longChain - shortChain) <= 1,
                "length must not change the delay: 2 hops=" + shortChain + " 12 hops=" + longChain);
    }

    /** Builds motor -> shaft -> N meshed cogs, cuts the motor, returns ticks until stopped. */
    private int windDown(int cogs) {
        now = 0L;
        Node motor = source("motor", 32);
        Node shaft = relay("shaft");
        pullEdge(motor, shaft);
        List<Node> all = new ArrayList<>(List.of(motor, shaft));
        List<Node> relays = new ArrayList<>(List.of(shaft));
        Node prev = shaft;
        for (int i = 0; i < cogs; i++) {
            Node c = relay("c" + i);
            if (i == 0) pullEdge(shaft, c); else relayEdge(prev, c);
            all.add(c);
            relays.add(c);
            prev = c;
        }
        for (int i = 0; i < 60; i++) tick(all);
        assertTrue(prev.rpm != 0, "chain of " + cogs + " must run first");
        shaft.pullFrom.clear();
        int took = ticksUntilStopped(all, relays, 400);
        assertTrue(took > 0, "chain of " + cogs + " never stopped");
        return took;
    }

    @Test
    @DisplayName("the reported mixed build stops: shaft, cogs, a large cog and its own shaft")
    void mixedBuildStops() {
        // Reported as never dying: a shaft into two cogs, one feeding a large cog diagonally,
        // that large cog driving its own shaft into another small cog, repeated about four times.
        // Any hop that PULLS used to hand out a full fresh budget, which reset the decay and kept
        // the whole thing alive forever.
        Node motor = source("motor", 32);
        Node mainShaft = relay("mainShaft");
        pullEdge(motor, mainShaft);

        List<Node> all = new ArrayList<>(List.of(motor, mainShaft));
        List<Node> relays = new ArrayList<>(List.of(mainShaft));
        Node feed = mainShaft;

        for (int stage = 0; stage < 4; stage++) {
            Node smallA = relay("smallA" + stage);
            Node smallB = relay("smallB" + stage);
            Node large = relay("large" + stage);
            Node stageShaft = relay("shaft" + stage);

            pullEdge(feed, smallA);        // small cog on the shaft's axis
            relayEdge(smallA, smallB);     // cog meshing cog
            relayEdge(smallB, large);      // large cog driven diagonally
            pullEdge(large, stageShaft);   // the large cog's own shaft — the pull hop that broke it

            all.addAll(List.of(smallA, smallB, large, stageShaft));
            relays.addAll(List.of(smallA, smallB, large, stageShaft));
            feed = stageShaft;
        }

        for (int i = 0; i < 120; i++) tick(all);
        assertTrue(feed.rpm != 0, "the far end of the build must run first");

        mainShaft.pullFrom.clear();   // break the motor's shaft
        int took = ticksUntilStopped(all, relays, 400);

        assertTrue(took > 0, "the build must not stay alive forever");
        assertTrue(took <= windDownBudget(),
                "should stop within " + windDownBudget() + " ticks, took " + took);
        for (Node n : relays) {
            assertEquals(0f, n.rpm, 1e-6, n.name + " must be stopped");
            assertFalse(n.emitting, n.name + " must not still claim to emit");
        }
    }

    @Test
    @DisplayName("a pull hop cannot mint fresh life for the chain behind it")
    void pullHopDoesNotRefreshTheChain() {
        // The precise defect: a shaft is alive only because it pulled from a coasting cog, yet it
        // had a spent countdown and so handed out a FULL budget, resetting the decay every time.
        Node motor = source("motor", 32);
        Node cog = relay("cog");
        Node shaft = relay("shaft");
        pullEdge(motor, cog);
        pullEdge(cog, shaft);
        List<Node> all = List.of(motor, cog, shaft);

        for (int i = 0; i < 20; i++) tick(all);
        long stampWhileRunning = shaft.stamp;
        assertEquals(motor.stamp, stampWhileRunning,
                "a relay must carry the SOURCE's stamp, never mint its own");

        cog.pullFrom.clear();
        int took = ticksUntilStopped(all, List.of(cog, shaft), 200);
        assertTrue(took > 0 && took <= windDownBudget(), "took " + took);
    }

    @Test
    @DisplayName("a running chain never flickers while its source keeps refreshing")
    void runningChainDoesNotFlicker() {
        Node motor = source("motor", 32);
        Node shaft = relay("shaft");
        Node c1 = relay("c1"), c2 = relay("c2"), c3 = relay("c3");
        pullEdge(motor, shaft);
        pullEdge(shaft, c1);
        relayEdge(c1, c2);
        relayEdge(c2, c3);
        List<Node> all = List.of(motor, shaft, c1, c2, c3);

        for (int i = 0; i < 60; i++) tick(all);
        for (Node n : List.of(shaft, c1, c2, c3)) {
            assertTrue(stableOn(n, 30), n.name + " flickered: " + n.activationHistory);
        }
    }

    @Test
    @DisplayName("the whole build comes back when the motor is reconnected")
    void mixedBuildRestarts() {
        Node motor = source("motor", 32);
        Node shaft = relay("shaft");
        Node c1 = relay("c1"), c2 = relay("c2");
        pullEdge(motor, shaft);
        pullEdge(shaft, c1);
        relayEdge(c1, c2);
        List<Node> all = List.of(motor, shaft, c1, c2);

        for (int i = 0; i < 30; i++) tick(all);
        List<Node> broken = new ArrayList<>(shaft.pullFrom);
        shaft.pullFrom.clear();
        ticksUntilStopped(all, List.of(shaft, c1, c2), 200);
        assertEquals(0f, c2.rpm, 1e-6, "stopped first");

        shaft.pullFrom.addAll(broken);
        for (int i = 0; i < 30; i++) tick(all);
        assertEquals(32f, c1.rpm, 1e-6, "nothing may latch off permanently");
        assertEquals(-32f, c2.rpm, 1e-6);
    }
}
