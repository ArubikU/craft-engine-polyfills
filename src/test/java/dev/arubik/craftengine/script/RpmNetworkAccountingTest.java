package dev.arubik.craftengine.script;

import dev.arubik.craftengine.rotation.KineticMember;
import dev.arubik.craftengine.rotation.RpmNetwork;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The kinetic network keeps its capacity/stress totals incrementally, because
 * {@code report_su} runs for every generator and consumer on every action tick — re-summing all
 * members there cost O(N) per reporter, i.e. O(N&sup2;) per tick for the network, which is what
 * made large builds lag.
 *
 * <p>These tests pin both halves of that: the running totals must stay equal to an exact re-sum,
 * and a settled network must cost nothing.
 */
class RpmNetworkAccountingTest {

    /** Minimal member: records overstress notifications so we can count them. */
    static final class Member implements KineticMember {
        long netId;
        float rpm;
        int overstressNotifications;
        Boolean lastOverstress;

        @Override public long rpmNetworkId() { return netId; }
        @Override public void setRpmNetworkId(long id) { netId = id; }
        @Override public void joinNetwork(long id) { netId = id; }
        @Override public void leaveNetwork() { netId = 0L; }
        @Override public void reportSuToNetwork(float su) {
            RpmNetwork n = RpmNetwork.get(netId);
            if (n != null) n.updateMemberSu(this, su);
        }
        @Override public void onNetworkOverstressChanged(boolean over) {
            overstressNotifications++;
            lastOverstress = over;
        }
        @Override public float getRpm() { return rpm; }
    }

    private final List<Member> members = new ArrayList<>();

    @AfterEach
    void tearDown() {
        RpmNetwork.clear();
        members.clear();
    }

    private Member join(RpmNetwork net, float su) {
        Member m = new Member();
        m.joinNetwork(net.id());
        net.addMember(m, su);
        members.add(m);
        return m;
    }

    /** Runs the once-per-tick drain that pushes settled overstress changes to members. */
    private static void tick() {
        RpmNetwork.flushAll();
    }

    /** What the totals should be, computed the slow exact way. */
    private void assertTotalsExact(RpmNetwork net, String where) {
        float cap = net.totalCapacity();
        float stress = net.totalStress();
        net.recalculate();   // exact rebuild
        assertEquals(net.totalCapacity(), cap, 1e-3, "capacity drifted " + where);
        assertEquals(net.totalStress(), stress, 1e-3, "stress drifted " + where);
    }

    @Test
    @DisplayName("a generator's capacity is counted, a consumer's stress is counted")
    void basicAccounting() {
        RpmNetwork net = RpmNetwork.create();
        join(net, -64f);   // generator
        join(net, 8f);     // consumer
        assertEquals(64f, net.totalCapacity(), 1e-6);
        assertEquals(8f, net.totalStress(), 1e-6);
        assertFalse(net.isOverStressed());
    }

    @Test
    @DisplayName("updating a member moves the totals without re-summing")
    void updateMovesTotals() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);

        load.reportSuToNetwork(32f);
        assertEquals(32f, net.totalStress(), 1e-6);
        assertEquals(64f, net.totalCapacity(), 1e-6);

        gen.reportSuToNetwork(-16f);
        assertEquals(16f, net.totalCapacity(), 1e-6);
        assertTotalsExact(net, "after updates");
    }

    @Test
    @DisplayName("a member flipping from consumer to generator moves between the totals")
    void memberCanChangeSides() {
        RpmNetwork net = RpmNetwork.create();
        Member m = join(net, 20f);
        assertEquals(20f, net.totalStress(), 1e-6);
        assertEquals(0f, net.totalCapacity(), 1e-6);

        m.reportSuToNetwork(-20f);
        assertEquals(0f, net.totalStress(), 1e-6);
        assertEquals(20f, net.totalCapacity(), 1e-6);
        assertTotalsExact(net, "after a side flip");
    }

    @Test
    @DisplayName("removing a member takes its contribution with it")
    void removalUpdatesTotals() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        join(net, 8f);
        assertEquals(64f, net.totalCapacity(), 1e-6);

        net.removeMember(gen);
        assertEquals(0f, net.totalCapacity(), 1e-6, "the generator's capacity must be gone");
        assertEquals(8f, net.totalStress(), 1e-6, "the consumer is untouched");
    }

    @Test
    @DisplayName("re-reporting the SAME value is free — the settled-network fast path")
    void steadyStateIsFree() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);
        int before = gen.overstressNotifications + load.overstressNotifications;

        // What a running build does every single tick.
        for (int i = 0; i < 1000; i++) {
            gen.reportSuToNetwork(-64f);
            load.reportSuToNetwork(8f);
        }

        assertEquals(64f, net.totalCapacity(), 1e-6, "totals must not drift on repeat reports");
        assertEquals(8f, net.totalStress(), 1e-6);
        assertEquals(before, gen.overstressNotifications + load.overstressNotifications,
                "an unchanged network must not notify anyone");
    }

    @Test
    @DisplayName("totals stay exact across many changing updates")
    void noDriftOverManyUpdates() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -100f);
        List<Member> loads = new ArrayList<>();
        for (int i = 0; i < 20; i++) loads.add(join(net, 1f));

        // Values that do not divide evenly, to provoke float drift.
        for (int round = 1; round <= 300; round++) {
            for (int i = 0; i < loads.size(); i++) {
                loads.get(i).reportSuToNetwork((round % 7) * 0.1f + i * 0.3f);
            }
            gen.reportSuToNetwork(-100f - (round % 5) * 0.7f);
        }
        assertTotalsExact(net, "after 300 rounds of updates");
    }

    @Test
    @DisplayName("overstress flips only when it actually changes")
    void overstressNotifiesOnlyOnChange() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member load = join(net, 5f);
        int baseline = gen.overstressNotifications;

        for (int i = 0; i < 50; i++) load.reportSuToNetwork(5f + (i % 2) * 0.001f);
        tick();
        assertEquals(baseline, gen.overstressNotifications, "still comfortably under capacity");

        load.reportSuToNetwork(50f);   // now over
        assertTrue(net.isOverStressed());
        tick();
        assertEquals(baseline + 1, gen.overstressNotifications, "exactly one notification");

        load.reportSuToNetwork(60f);   // still over, no new notification
        tick();
        assertEquals(baseline + 1, gen.overstressNotifications);
    }

    @Test
    @DisplayName("a network with load but no generator counts as stalled")
    void zeroCapacityIsStalled() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);
        assertFalse(net.isOverStressed());

        gen.reportSuToNetwork(0f);   // the motor ran out of fuel
        assertFalse(net.hasCapacity(), "nothing generates any more");
        assertTrue(net.isOverStressed(),
                "load with no capacity must read as stalled, not as healthy");
        tick();
        assertEquals(Boolean.TRUE, load.lastOverstress, "members must be told");
    }

    @Test
    @DisplayName("an idle network with neither capacity nor load is not stalled")
    void emptyNetworkIsNotStalled() {
        RpmNetwork net = RpmNetwork.create();
        join(net, 0f);
        assertFalse(net.isOverStressed(), "nothing to drive and nothing asking: that is fine");
    }

    // ------------------------------------------- Create-style structural tracking

    @Test
    @DisplayName("the sources view answers 'is anything driving this?' without a member scan")
    void sourcesViewTracksGenerators() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        join(net, 8f);
        join(net, 4f);

        assertTrue(net.hasSource());
        assertEquals(1, net.sourceCount(), "only the generator counts as a source");

        gen.reportSuToNetwork(0f);
        assertFalse(net.hasSource(), "a generator that stopped is no longer a source");
        assertEquals(0, net.sourceCount());

        gen.reportSuToNetwork(-64f);
        assertTrue(net.hasSource(), "and it comes back when it starts again");
    }

    @Test
    @DisplayName("a member that flips sides moves in and out of the sources view")
    void sourcesViewFollowsSideFlips() {
        RpmNetwork net = RpmNetwork.create();
        Member m = join(net, 10f);
        assertEquals(0, net.sourceCount());
        m.reportSuToNetwork(-10f);
        assertEquals(1, net.sourceCount());
        m.reportSuToNetwork(10f);
        assertEquals(0, net.sourceCount());
    }

    @Test
    @DisplayName("members that left without saying so are pruned on the next exact rebuild")
    void staleMembersArePruned() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member ghost = join(net, 16f);
        assertEquals(16f, net.totalStress(), 1e-6);

        // Broken or unloaded without leaveNetwork: its id no longer points here.
        ghost.setRpmNetworkId(0L);

        net.recalculate();   // the periodic exact rebuild
        assertEquals(0f, net.totalStress(), 1e-6, "the ghost's stress must be dropped");
        assertEquals(64f, net.totalCapacity(), 1e-6, "the real generator is untouched");
        assertEquals(1, net.size(), "and the ghost is no longer held in memory");
        assertEquals(1, net.sourceCount());
        assertNotNull(gen);
    }

    @Test
    @DisplayName("dissolving twice does no extra work")
    void dissolveIsIdempotent() {
        RpmNetwork net = RpmNetwork.create();
        Member a = join(net, -10f);
        Member b = join(net, 5f);
        net.dissolve();
        int notifications = a.overstressNotifications + b.overstressNotifications;

        net.dissolve();
        net.dissolve();
        assertEquals(notifications, a.overstressNotifications + b.overstressNotifications,
                "a burst of removals must not re-walk the members over and over");
        assertEquals(0, net.size());
        assertFalse(net.hasSource());
    }

    @Test
    @DisplayName("a dissolved network reports no capacity or stress")
    void dissolvedNetworkIsEmpty() {
        RpmNetwork net = RpmNetwork.create();
        join(net, -64f);
        join(net, 8f);
        net.dissolve();
        assertEquals(0f, net.totalCapacity(), 1e-6);
        assertEquals(0f, net.totalStress(), 1e-6);
    }

    // --------------------------------------------- O(1) removal / lazy revalidation

    @Test
    @DisplayName("removing one member does NOT reset everyone else")
    void removalLeavesTheRestAlone() {
        // The lag complaint: breaking a single shaft used to dissolve the network, resetting the
        // networkId of every other member and forcing the whole build to re-form.
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -100f);
        List<Member> loads = new ArrayList<>();
        for (int i = 0; i < 50; i++) loads.add(join(net, 1f));

        net.removeMember(loads.get(10));

        assertEquals(net.id(), gen.netId, "the generator must stay where it was");
        for (int i = 0; i < loads.size(); i++) {
            if (i == 10) continue;
            assertEquals(net.id(), loads.get(i).netId,
                    "member " + i + " must not have been kicked out");
        }
        assertEquals(50, net.size(), "50 members left of the original 51");
        assertEquals(49f, net.totalStress(), 1e-6, "one unit of stress left with it");
    }

    @Test
    @DisplayName("removing many members in a burst stays cheap and exact")
    void burstRemovalIsExact() {
        RpmNetwork net = RpmNetwork.create();
        join(net, -500f);
        List<Member> loads = new ArrayList<>();
        for (int i = 0; i < 200; i++) loads.add(join(net, 2f));
        assertEquals(400f, net.totalStress(), 1e-6);

        for (int i = 0; i < 150; i++) net.removeMember(loads.get(i));

        assertEquals(100f, net.totalStress(), 1e-6, "50 members x 2 SU remain");
        assertEquals(500f, net.totalCapacity(), 1e-6);
        assertTotalsExact(net, "after a burst of 150 removals");
        assertEquals(51, net.size());
    }

    @Test
    @DisplayName("removing the last member retires the network")
    void lastRemovalRetiresTheNetwork() {
        RpmNetwork net = RpmNetwork.create();
        Member only = join(net, -10f);
        long id = net.id();
        net.removeMember(only);
        assertNull(RpmNetwork.get(id), "an empty network must not be left behind");
    }

    @Test
    @DisplayName("losing the generator stalls the remaining consumers")
    void losingTheGeneratorStallsTheRest() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);
        assertFalse(net.isOverStressed());

        net.removeMember(gen);

        assertFalse(net.hasSource(), "nothing generates any more");
        assertTrue(net.isOverStressed(), "load with no capacity reads as stalled");
        tick();
        assertEquals(Boolean.TRUE, load.lastOverstress, "and the consumer is told");
    }

    @Test
    @DisplayName("removing a consumer can un-stall an overstressed network")
    void removingLoadCanRelieveOverstress() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member small = join(net, 4f);
        Member hog = join(net, 40f);
        assertTrue(net.isOverStressed(), "40 + 4 over a capacity of 10");

        tick();   // settle the initial overstress before relieving it
        net.removeMember(hog);

        assertFalse(net.isOverStressed(), "4 fits under 10 again");
        tick();
        assertEquals(Boolean.FALSE, gen.lastOverstress, "members must be told it recovered");
        assertEquals(Boolean.FALSE, small.lastOverstress);
    }

    @Test
    @DisplayName("a member that migrates to another network stops counting in the old one")
    void migratingMemberLeavesTheOldTotals() {
        // What a split looks like: each half follows its own source, which is how the network
        // heals now that removal no longer dissolves everything.
        RpmNetwork a = RpmNetwork.create();
        RpmNetwork b = RpmNetwork.create();
        Member genA = join(a, -50f);
        Member shared = join(a, 12f);
        Member genB = join(b, -50f);
        assertEquals(12f, a.totalStress(), 1e-6);

        // syncNetworkWithSource re-points it at the other source's network.
        a.removeMember(shared);
        shared.joinNetwork(b.id());
        b.addMember(shared, 12f);

        assertEquals(0f, a.totalStress(), 1e-6, "the old network must forget it");
        assertEquals(12f, b.totalStress(), 1e-6, "and the new one must count it");
        assertEquals(1, a.size());
        assertEquals(2, b.size());
        assertNotNull(genA);
        assertNotNull(genB);
    }

    @Test
    @DisplayName("stragglers left behind are dropped by the pruning pass")
    void stragglersArePrunedNotStranded() {
        RpmNetwork net = RpmNetwork.create();
        join(net, -64f);
        Member stray = join(net, 30f);
        assertEquals(30f, net.totalStress(), 1e-6);

        // The block wound down on its own and left, without the network being told.
        stray.leaveNetwork();

        net.recalculate();
        assertEquals(0f, net.totalStress(), 1e-6, "the straggler stops skewing the total");
        assertEquals(1, net.size());
    }

    @Test
    @DisplayName("a network survives losing and regaining its generator")
    void generatorCanComeBack() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);

        net.removeMember(gen);
        assertTrue(net.isOverStressed());
        tick();

        Member gen2 = join(net, -64f);
        assertFalse(net.isOverStressed(), "a new generator must revive the network");
        assertTrue(net.hasSource());
        assertEquals(64f, net.totalCapacity(), 1e-6);
        tick();
        assertEquals(Boolean.FALSE, load.lastOverstress);
        assertNotNull(gen2);
    }

    @Test
    @DisplayName("removal, update and re-add interleaved keep the totals honest")
    void interleavedChurnStaysExact() {
        RpmNetwork net = RpmNetwork.create();
        join(net, -1000f);
        List<Member> loads = new ArrayList<>();
        for (int i = 0; i < 40; i++) loads.add(join(net, 3f));

        for (int round = 0; round < 60; round++) {
            Member m = loads.get(round % loads.size());
            m.reportSuToNetwork(2f + (round % 4));
            if (round % 5 == 0) {
                net.removeMember(m);
                m.joinNetwork(net.id());
                net.addMember(m, 3f);
            }
        }
        assertTotalsExact(net, "after interleaved churn");
        assertEquals(41, net.size(), "nothing lost or duplicated");
    }

    // ------------------------------------------------- batched sync (networkDirty)

    @Test
    @DisplayName("a flag that flaps within one tick never reaches the members")
    void flappingWithinATickCostsNothing() {
        // Create batches network syncs behind a dirty flag instead of walking every member on each
        // change. Several machines reporting in the same tick can push the flag over and back; a
        // network that ends the tick where it started must cost nothing at all.
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member load = join(net, 5f);
        tick();
        int baseline = gen.overstressNotifications;

        for (int i = 0; i < 20; i++) {
            load.reportSuToNetwork(50f);   // over
            load.reportSuToNetwork(5f);    // back under
        }
        tick();

        assertFalse(net.isOverStressed());
        assertEquals(baseline, gen.overstressNotifications,
                "20 round trips in one tick must produce no notifications at all");
    }

    @Test
    @DisplayName("many changes in one tick collapse into a single notification")
    void burstCollapsesIntoOneNotification() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        List<Member> loads = new ArrayList<>();
        for (int i = 0; i < 30; i++) loads.add(join(net, 0f));
        tick();
        int baseline = gen.overstressNotifications;

        // Every consumer switches on at once — the flag crosses only once.
        for (Member m : loads) m.reportSuToNetwork(1f);
        tick();

        assertTrue(net.isOverStressed(), "30 SU over a capacity of 10");
        assertEquals(baseline + 1, gen.overstressNotifications,
                "one walk of the members, not thirty");
    }

    @Test
    @DisplayName("nothing is notified until the tick drain runs")
    void notificationIsDeferredToTheDrain() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member load = join(net, 1f);
        tick();
        int baseline = gen.overstressNotifications;

        load.reportSuToNetwork(100f);
        assertTrue(net.isOverStressed(), "the state itself is up to date immediately");
        assertEquals(baseline, gen.overstressNotifications, "but nobody has been walked yet");

        tick();
        assertEquals(baseline + 1, gen.overstressNotifications, "the drain delivers it");
    }

    @Test
    @DisplayName("draining twice does not notify twice")
    void drainIsIdempotent() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member load = join(net, 1f);
        tick();
        int baseline = gen.overstressNotifications;

        load.reportSuToNetwork(100f);
        tick();
        tick();
        tick();
        assertEquals(baseline + 1, gen.overstressNotifications);
    }

    @Test
    @DisplayName("a quiet network costs nothing to drain")
    void quietNetworkDrainsFree() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -64f);
        Member load = join(net, 8f);
        tick();
        int baseline = gen.overstressNotifications + load.overstressNotifications;

        // A settled build reporting the same values every tick, as it does in game.
        for (int t = 0; t < 200; t++) {
            gen.reportSuToNetwork(-64f);
            load.reportSuToNetwork(8f);
            tick();
        }
        assertEquals(baseline, gen.overstressNotifications + load.overstressNotifications,
                "200 quiet ticks must not walk the members once");
    }

    @Test
    @DisplayName("the state itself is always current, only the notification is batched")
    void stateIsNeverStale() {
        RpmNetwork net = RpmNetwork.create();
        Member gen = join(net, -10f);
        Member load = join(net, 1f);

        load.reportSuToNetwork(100f);
        assertTrue(net.isOverStressed(), "a script reading is_overstressed must see it at once");
        assertEquals(100f, net.totalStress(), 1e-6);

        load.reportSuToNetwork(1f);
        assertFalse(net.isOverStressed());
        assertNotNull(gen);
    }
}
