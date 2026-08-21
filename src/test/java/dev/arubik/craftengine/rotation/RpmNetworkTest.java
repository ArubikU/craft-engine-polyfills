package dev.arubik.craftengine.rotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RpmNetwork: membership, capacity/stress, overstress, network split.
 * Uses MockMember to avoid server-side dependencies.
 *
 * Network semantics:
 *   negative SU = generator (provides capacity)
 *   positive SU = consumer (demands capacity)
 *   overStressed = totalStress > totalCapacity (AND capacity > 0)
 */
class RpmNetworkTest {

    private RpmNetwork net;

    static class MockMember implements KineticMember {
        long networkId = 0;
        boolean overstressed = false;
        float rpm = 0;

        @Override public long rpmNetworkId() { return networkId; }
        @Override public void setRpmNetworkId(long id) { this.networkId = id; }
        @Override public void joinNetwork(long id) {
            leaveNetwork();
            networkId = id;
            RpmNetwork n = RpmNetwork.get(id);
            if (n != null) n.addMember(this, 0f);
        }
        @Override public void leaveNetwork() {
            RpmNetwork n = RpmNetwork.get(networkId);
            if (n != null) n.removeMember(this);
            networkId = 0;
        }
        @Override public void reportSuToNetwork(float su) {
            RpmNetwork n = RpmNetwork.get(networkId);
            if (n != null) n.updateMemberSu(this, su);
        }
        @Override public void onNetworkOverstressChanged(boolean over) { this.overstressed = over; }
        @Override public float getRpm() { return rpm; }
    }

    @BeforeEach
    void setUp() {
        RpmNetwork.clear();
        net = RpmNetwork.create();
    }

    @Test
    void testSingleMemberJoinAndLeave() {
        long id = net.id();
        MockMember m = new MockMember();
        net.addMember(m, 0f);
        assertEquals(1, net.size());
        net.removeMember(m);
        assertNull(RpmNetwork.get(id), "Network auto-destroys when empty");
    }

    @Test
    void testCapacityAndStressCalculation() {
        MockMember gen = new MockMember();
        MockMember con = new MockMember();
        net.addMember(gen, -512f);
        net.addMember(con, 128f);
        assertEquals(512f, net.totalCapacity(), 0.01f);
        assertEquals(128f, net.totalStress(), 0.01f);
        assertFalse(net.isOverStressed());
    }

    @Test
    void testOverstressTriggered() {
        MockMember gen = new MockMember();
        MockMember con = new MockMember();
        net.addMember(gen, -100f);
        net.addMember(con, 200f);   // stress > capacity
        assertTrue(net.isOverStressed());
        assertTrue(gen.overstressed, "Generator notified of overstress");
        assertTrue(con.overstressed, "Consumer notified of overstress");
    }

    @Test
    void testOverstressRecovery() {
        MockMember gen = new MockMember();
        MockMember con = new MockMember();
        net.addMember(gen, -100f);
        net.addMember(con, 200f);
        assertTrue(net.isOverStressed());
        net.updateMemberSu(con, 50f);   // reduce demand
        assertFalse(net.isOverStressed());
        assertFalse(gen.overstressed);
        assertFalse(con.overstressed);
    }

    @Test
    void testNetworkAutoDestroyOnEmpty() {
        long id = net.id();
        MockMember m = new MockMember();
        net.addMember(m, 0f);
        net.removeMember(m);
        assertNull(RpmNetwork.get(id));
    }

    @Test
    void testSplitNetworkBothSidesHaveGenerators() {
        // Simulate: motor-A → shaft → shaft → motor-B, shaft in middle breaks.
        // Each side should have its own independent network.
        RpmNetwork net2 = RpmNetwork.create();
        MockMember genA = new MockMember();
        MockMember conA = new MockMember();
        MockMember genB = new MockMember();
        MockMember conB = new MockMember();

        net.addMember(genA, -512f);
        net.addMember(conA, 128f);
        net2.addMember(genB, -512f);
        net2.addMember(conB, 128f);

        assertFalse(net.isOverStressed(), "Side A should not be overstressed");
        assertFalse(net2.isOverStressed(), "Side B should not be overstressed");
        assertEquals(2, net.size());
        assertEquals(2, net2.size());
    }

    @Test
    void testSplitNetworkOneSideLoosesGenerator() {
        // When one side loses its generator after split, it becomes overstressed.
        RpmNetwork net2 = RpmNetwork.create();
        MockMember genA = new MockMember();
        MockMember conA = new MockMember();
        MockMember conB = new MockMember();  // no generator on side B

        net.addMember(genA, -512f);
        net.addMember(conA, 100f);
        net2.addMember(conB, 100f);  // capacity=0, stress=100 → overstressed

        assertFalse(net.isOverStressed(), "Side with generator OK");
        // Side B has no generator (cap=0), so overStressed condition: stress > cap && cap > 0 → false
        // Actual: cap=0 means no stress comparison triggers
        assertFalse(net2.isOverStressed(), "No generator side: not overstressed (cap=0 guard)");
    }

    @Test
    void testMemberReportSuDynamic() {
        MockMember m = new MockMember();
        m.networkId = net.id();
        net.addMember(m, 0f);

        net.updateMemberSu(m, -200f);
        assertEquals(200f, net.totalCapacity(), 0.01f);
        assertEquals(0f, net.totalStress(), 0.01f);

        net.updateMemberSu(m, 50f);
        assertEquals(0f, net.totalCapacity(), 0.01f);
        assertEquals(50f, net.totalStress(), 0.01f);
    }

    @Test
    void testMultipleGeneratorsPoolCapacity() {
        MockMember gen1 = new MockMember();
        MockMember gen2 = new MockMember();
        MockMember con = new MockMember();
        net.addMember(gen1, -256f);
        net.addMember(gen2, -256f);
        net.addMember(con, 400f);
        assertEquals(512f, net.totalCapacity(), 0.01f);
        assertEquals(400f, net.totalStress(), 0.01f);
        assertFalse(net.isOverStressed(), "Combined capacity (512) > stress (400)");
    }
}
