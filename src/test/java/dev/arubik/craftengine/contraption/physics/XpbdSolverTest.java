package dev.arubik.craftengine.contraption.physics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.AABB;

/**
 * Proves the property the previous engine could not express: <b>an unbalanced body tips, and where
 * its mass sits decides whether and which way it does.</b>
 *
 * <p>These are not tests of a tipping feature — there is no tipping code to test, and searching the
 * solver for "tip" or "tilt" finds nothing. Tipping is emergent: {@code XpbdSolver} applies each
 * contact impulse at its own lever arm, so a body whose center of mass hangs past its supports
 * receives support impulses that no longer balance. That is why the assertions compare the two ends'
 * heights rather than reading some tilt variable.
 *
 * <p>Every case below uses the SAME geometry and the SAME supports. Only the end block's mass
 * changes, which is exactly the question "does iron weigh more, and does it matter?"
 */
class XpbdSolverTest {

    /** One block-sized lump of mass at a local position — the unit these bodies are assembled from. */
    private record Cell(double x, double y, double z, double mass) {
    }

    /** Mass of the end block in the loaded case — an iron block against three of wool. */
    private static final double IRON = 30.0;

    /** A four-cell beam laid along X, cell centres at x = 0.5 … 3.5, spanning local box [0,4]×[0,1]×[0,1]. */
    private static PhysBody beam(double endMass) {
        List<Cell> cells = List.of(
                new Cell(0.5, 0.5, 0.5, 1.0),
                new Cell(1.5, 0.5, 0.5, 1.0),
                new Cell(2.5, 0.5, 0.5, 1.0),
                new Cell(3.5, 0.5, 0.5, endMass));

        double mass = 0.0;
        Vector3d com = new Vector3d();
        for (Cell c : cells) {
            mass += c.mass();
            com.add(c.x() * c.mass(), c.y() * c.mass(), c.z() * c.mass());
        }
        com.div(mass);

        // The same accumulation MassModel performs: each cell's own unit-cube tensor (m/6, isotropic)
        // plus a parallel-axis transfer m·(|d|²·δ − d⊗d) to the body's center of mass.
        double ixx = 0, iyy = 0, izz = 0, ixy = 0, ixz = 0, iyz = 0;
        for (Cell c : cells) {
            double dx = c.x() - com.x, dy = c.y() - com.y, dz = c.z() - com.z;
            double self = c.mass() / 6.0;
            ixx += self + c.mass() * (dy * dy + dz * dz);
            iyy += self + c.mass() * (dx * dx + dz * dz);
            izz += self + c.mass() * (dx * dx + dy * dy);
            ixy -= c.mass() * dx * dy;
            ixz -= c.mass() * dx * dz;
            iyz -= c.mass() * dy * dz;
        }
        Matrix3d inertia = new Matrix3d(ixx, ixy, ixz, ixy, iyy, iyz, ixz, iyz, izz);

        PhysBody body = new PhysBody();
        body.shape = CollisionShape.ofBoxes(List.of(new AABB(0, 0, 0, 4, 1, 1)), com);
        body.body.setMassProperties(1.0 / mass, inertia.invert(new Matrix3d()));
        // The local frame coincides with world at identity orientation, so the COM's world position is
        // simply its local position — which lands the beam's underside exactly on y = 0.
        body.body.position.set(com);
        return body;
    }

    private static Vector3d comOf(double endMass) {
        double mass = 3.0 + endMass;
        return new Vector3d((0.5 + 1.5 + 2.5 + 3.5 * endMass) / mass, 0.5, 0.5);
    }

    /** World Y of the beam's underside at the given local X — how each end's height is read back. */
    private static double endHeight(PhysBody body, double localX, Vector3d com) {
        Vector3d local = new Vector3d(localX, 0.0, 0.5).sub(com);
        return body.body.toWorld(local, new Vector3d()).y;
    }

    /**
     * A pillar under the beam's MIDDLE, spanning x ∈ [1,3). The beam overhangs it at both ends, so
     * whether it tips is decided purely by which side of the pillar the center of mass falls on — a
     * see-saw. A ledge under one end instead would let the beam slide off and tumble, which measures
     * free fall rather than tipping.
     */
    private static WorldBlockCache pillar() {
        return WorldBlockCache.solidRegion(1, -1, 0, 2, -1, 1);
    }

    private static void settle(PhysBody body, int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            XpbdSolver.step(List.of(body), 1.0);
        }
    }

    @Test
    @DisplayName("balanced on a pillar, a uniform beam stays level")
    void balancedBeamStaysLevel() {
        PhysBody body = beam(1.0);
        Vector3d com = comOf(1.0); // x = 2.0 — dead centre of the pillar
        body.world = pillar();

        settle(body, 40);

        double tilt = endHeight(body, 0.0, com) - endHeight(body, 4.0, com);
        assertTrue(Math.abs(tilt) < 0.15,
                "a balanced beam should stay level, but the ends differ by " + tilt);
    }

    @Test
    @DisplayName("the same beam with an iron block on one end tips that end down")
    void ironLoadedEndTipsDown() {
        PhysBody body = beam(IRON);
        Vector3d com = comOf(IRON); // x = 3.32 — past the pillar's far edge at x = 3
        body.world = pillar();

        settle(body, 12);

        double ironEnd = endHeight(body, 4.0, com);
        double woolEnd = endHeight(body, 0.0, com);
        assertTrue(ironEnd < woolEnd - 0.1,
                "the iron end should have tipped below the wool end, but ironEnd=" + ironEnd
                        + " woolEnd=" + woolEnd);
    }

    @Test
    @DisplayName("mass distribution alone decides it: identical geometry, opposite outcomes")
    void massDistributionDecidesTheTilt() {
        PhysBody uniform = beam(1.0);
        uniform.world = pillar();
        settle(uniform, 12);
        double uniformTilt = endHeight(uniform, 0.0, comOf(1.0)) - endHeight(uniform, 4.0, comOf(1.0));

        PhysBody loaded = beam(IRON);
        loaded.world = pillar();
        settle(loaded, 12);
        double loadedTilt = endHeight(loaded, 0.0, comOf(IRON)) - endHeight(loaded, 4.0, comOf(IRON));

        assertTrue(loadedTilt > uniformTilt + 0.1,
                "the iron-laden beam must tip harder than the uniform one; loadedTilt=" + loadedTilt
                        + " uniformTilt=" + uniformTilt);
    }

    /** Ground under the whole beam, top face at y = 0. */
    private static WorldBlockCache floor() {
        return WorldBlockCache.solidRegion(-1, -1, -1, 5, -1, 2);
    }

    /**
     * The impact record is what {@code ContraptionImpactDetonator} thresholds on, so the number it reads
     * has to be the speed the body was ACTUALLY travelling at — not the remnant left after the position
     * solve has already stopped it, which is a fraction of it and shrinks as the hit gets harder.
     */
    @Test
    @DisplayName("a dropped body records the speed it was really falling at, not the post-solve remnant")
    void droppedBodyRecordsItsApproachSpeed() {
        PhysBody body = beam(1.0);
        body.world = floor();
        body.body.position.y += 8.0;

        double fastestObserved = 0.0;
        double impact = 0.0;
        for (int tick = 0; tick < 80 && impact == 0.0; tick++) {
            fastestObserved = Math.max(fastestObserved, -body.body.linearVelocity.y);
            XpbdSolver.step(List.of(body), 1.0);
            impact = body.maxImpactSpeed();
        }

        assertTrue(impact > 0.6, "an 8-block drop should land at roughly free-fall speed, but recorded " + impact);
        // fastestObserved is read at the START of the tick the beam lands on, so gravity makes the true
        // impact marginally faster still. Anything materially BELOW it would mean the recorded number had
        // been attenuated by the solve — the exact failure this record is positioned to avoid.
        assertTrue(impact >= fastestObserved,
                "the recorded impact should be at least the last observed fall speed, but impact=" + impact
                        + " fell at " + fastestObserved);
    }

    @Test
    @DisplayName("a body sitting on the ground records no impact — contact is not collision")
    void restingBodyRecordsNoImpact() {
        PhysBody body = beam(1.0);
        body.world = floor();

        settle(body, 60);
        XpbdSolver.step(List.of(body), 1.0);

        assertTrue(body.maxImpactSpeed() < 0.05,
                "a resting body is in permanent contact but is not crashing; recorded "
                        + body.maxImpactSpeed());
    }

    @Test
    @DisplayName("the impact record is per-tick, not cumulative — it does not remember an old crash")
    void impactRecordResetsEachTick() {
        PhysBody body = beam(1.0);
        body.world = floor();
        body.body.position.y += 8.0;

        settle(body, 80);

        assertTrue(body.maxImpactSpeed() < 0.05,
                "long after landing, the body should report no impact, but reports " + body.maxImpactSpeed());
    }

    @Test
    @DisplayName("a fully supported body settles flush and sheds its velocity — it does not sink or bounce")
    void fullySupportedBodyRests() {
        PhysBody body = beam(1.0);
        body.world = WorldBlockCache.solidRegion(-1, -1, -1, 5, -1, 2);
        double startY = body.body.position.y;

        settle(body, 120);

        assertTrue(Math.abs(body.body.position.y - startY) < 0.05,
                "a supported body must rest flush, not sink or bounce; y=" + body.body.position.y
                        + " started at " + startY);
        assertTrue(body.body.linearVelocity.length() < 0.05,
                "a settled body should have shed its velocity, but |v|=" + body.body.linearVelocity.length());
    }

    @Test
    @DisplayName("per-block friction: a low-friction body slides farther than a high-friction one")
    void frictionControlsSliding() {
        // Rotation is disabled (zero inverse inertia) so this isolates SLIDING — a free cube would tumble
        // under high friction and confound the distance. Friction's job is the tangential brake, which is
        // exactly what a non-rotating slide measures.
        PhysBody ice = slider();
        ice.body.setFriction(0.05);
        PhysBody honey = slider();
        honey.body.setFriction(1.5);

        for (int t = 0; t < 40; t++) {
            XpbdSolver.step(List.of(ice), 1.0);
            XpbdSolver.step(List.of(honey), 1.0);
        }

        assertTrue(ice.body.position.x > honey.body.position.x + 0.5,
                "ice (0.05) must slide farther than honey (1.5); ice.x=" + ice.body.position.x
                        + " honey.x=" + honey.body.position.x);
    }

    /** A non-rotating unit cube resting on solid ground, shoved along +X at 1 block/tick. */
    private static PhysBody slider() {
        PhysBody body = cube(1.0);
        body.body.setMassProperties(1.0, new Matrix3d().zero()); // zero inverse inertia — no tumbling
        body.world = WorldBlockCache.solidRegion(-5, -1, -5, 60, -1, 5);
        body.body.linearVelocity.set(1.0, 0.0, 0.0);
        return body;
    }

    /** A single-cell body of the given mass, local box [0,1]^3, its COM at the cell centre. */
    private static PhysBody cube(double mass) {
        org.joml.Vector3d com = new org.joml.Vector3d(0.5, 0.5, 0.5);
        PhysBody body = new PhysBody();
        body.shape = CollisionShape.ofBoxes(List.of(new AABB(0, 0, 0, 1, 1, 1)), com);
        double self = mass / 6.0;
        Matrix3d inertia = new Matrix3d(self, 0, 0, 0, self, 0, 0, 0, self);
        body.body.setMassProperties(1.0 / mass, inertia.invert(new Matrix3d()));
        body.body.position.set(com);
        return body;
    }


    @Test
    @DisplayName("a floating body settles at the waterline instead of bobbing forever")
    void buoyantBodySettlesRatherThanOscillating() {
        PhysBody wood = cube(6.0);
        wood.floatability = 0.0; // buoyancy is floatability's call now, not the mass's — see FloatabilityModel
        wood.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, false);

        for (int tick = 0; tick < 400; tick++) {
            XpbdSolver.step(List.of(wood), 1.0);
        }

        // Buoyancy without drag is a spring: it would still be oscillating here. Drag is what takes
        // the energy out, and this asserts the energy actually left.
        assertTrue(Math.abs(wood.body.linearVelocity.y) < 0.02,
                "a settled floating body should have stopped moving, but v.y=" + wood.body.linearVelocity.y);
        // ...and it FLOATED: started at y=0.5, 7.5 blocks under, and rose to the waterline. A fully
        // buoyant unit cube settles half submerged, which puts its centre exactly at the surface (8.0).
        // This used to assert it "holds its depth" near y=0.5, which encoded the very bug it was meant to
        // guard: a lift that merely cancels gravity is NEUTRAL buoyancy, and neutral buoyancy has no
        // restoring force, so wool sank until fully under and hung there. See XpbdSolver#BUOYANCY_GAIN.
        assertTrue(Math.abs(wood.body.position.y - 8.0) < 0.5,
                "a fully buoyant body should settle at the waterline (8.0), but y=" + wood.body.position.y);
    }


    @Test
    @DisplayName("a river carries a floating body downstream; still water does not")
    void currentPushesFloatingBodies() {
        net.minecraft.world.phys.Vec3 downstream =
                new net.minecraft.world.phys.Vec3(WorldBlockCache.FLOW_SPEED, 0, 0);

        PhysBody drifting = cube(6.0);
        drifting.world = WorldBlockCache.fluidRegion(-40, -8, -4, 40, 8, 4, 8.0, false, downstream);
        PhysBody moored = cube(6.0);
        moored.world = WorldBlockCache.fluidRegion(-40, -8, -4, 40, 8, 4, 8.0, false,
                net.minecraft.world.phys.Vec3.ZERO);

        double startX = drifting.body.position.x;
        for (int tick = 0; tick < 80; tick++) {
            XpbdSolver.step(List.of(drifting), 1.0);
            XpbdSolver.step(List.of(moored), 1.0);
        }

        assertTrue(drifting.body.position.x > startX + 1.0,
                "a body in a current must be carried downstream; x moved to " + drifting.body.position.x);
        // The control: identical body, identical water, only the flow differs. Still water must not
        // drift, which is what proves the motion comes from the current and not from a leak in the
        // buoyancy or drag terms.
        assertTrue(Math.abs(moored.body.position.x - startX) < 0.25,
                "a body in still water must stay put; x=" + moored.body.position.x);
    }

    @Test
    @DisplayName("the current carries a body TOWARD the flow speed and no further — drag is its own speed limit")
    void driftConvergesOnTheFlowSpeed() {
        net.minecraft.world.phys.Vec3 downstream =
                new net.minecraft.world.phys.Vec3(WorldBlockCache.FLOW_SPEED, 0, 0);
        PhysBody raft = cube(6.0);
        raft.world = WorldBlockCache.fluidRegion(-400, -8, -4, 400, 8, 4, 8.0, false, downstream);

        for (int tick = 0; tick < 200; tick++) {
            XpbdSolver.step(List.of(raft), 1.0);
        }

        // Drag pulls the body toward the fluid's velocity, so the flow speed IS the terminal speed —
        // there is no separate clamp keeping it there. Overshooting would mean the current is pumping
        // energy in rather than exchanging it.
        assertTrue(raft.body.linearVelocity.x > 0.0,
                "the raft should be moving downstream, but v.x=" + raft.body.linearVelocity.x);
        assertTrue(raft.body.linearVelocity.x <= WorldBlockCache.FLOW_SPEED + 0.02,
                "the raft must not outrun the current that carries it; v.x=" + raft.body.linearVelocity.x
                        + " flow=" + WorldBlockCache.FLOW_SPEED);
    }

    @Test
    @DisplayName("two bodies solved together collide instead of passing through each other")
    void bodiesCollideWithEachOther() {
        // Ground under both, one cube resting on it and a second dropped directly onto the first.
        PhysBody lower = cube(10.0);
        lower.world = WorldBlockCache.solidRegion(-2, -1, -2, 2, -1, 2);
        PhysBody upper = cube(10.0);
        upper.world = WorldBlockCache.solidRegion(-2, -1, -2, 2, -1, 2);
        upper.body.position.set(0.5, 3.0, 0.5);

        // Solved in ONE call, which is what contraption-vs-contraption requires: separate calls would
        // have each body push off a stale copy of the other. PhysicsWorld only ever splits bodies
        // across threads when they CANNOT reach each other, so a pair like this always stays together.
        for (int tick = 0; tick < 120; tick++) {
            XpbdSolver.step(List.of(lower, upper), 1.0);
        }

        // The upper cube must come to rest ON the lower one, not inside it and not through it.
        double gap = upper.body.position.y - lower.body.position.y;
        assertTrue(gap > 0.85,
                "the upper body must sit on top of the lower one, not sink into it; gap=" + gap);
        assertTrue(upper.body.position.y > 1.0,
                "the upper body must not fall through the lower one; y=" + upper.body.position.y);
    }

    @Test
    @DisplayName("the broadphase sees bodies that are still apart but will meet this tick")
    void broadphaseCoversATickOfClosingMotion() {
        PhysBody a = cube(10.0);
        PhysBody b = cube(10.0);
        // Two blocks of clear air between them: not touching, and not close to touching.
        a.body.position.set(0.5, 0.5, 0.5);
        b.body.position.set(3.5, 0.5, 0.5);

        // Stationary, they are correctly judged as unable to meet.
        assertFalse(ContactGenerator.broadphaseOverlap(a.body, a.shape, b.body, b.shape),
                "two parked bodies two blocks apart must not be considered a possible contact");

        // Closing fast, they WILL meet before this test is asked again. PhysicsWorld partitions islands
        // with this exact call, once per tick, and may solve separate islands on separate threads — so a
        // false here is two contraptions that never see each other and pass straight through.
        a.body.linearVelocity.set(0.8, 0, 0);
        b.body.linearVelocity.set(-0.8, 0, 0);
        assertTrue(ContactGenerator.broadphaseOverlap(a.body, a.shape, b.body, b.shape),
                "bodies closing at 1.6 blocks/tick across a 2-block gap must be solved together");
    }

    @Test
    @DisplayName("two bodies driven into each other do not end up inside one another")
    void fastBodiesDoNotInterpenetrate() {
        PhysBody left = cube(10.0);
        PhysBody right = cube(10.0);
        left.body.position.set(0.5, 0.5, 0.5);
        right.body.position.set(4.5, 0.5, 0.5);
        left.body.linearVelocity.set(0.6, 0, 0);
        right.body.linearVelocity.set(-0.6, 0, 0);

        for (int tick = 0; tick < 40; tick++) {
            XpbdSolver.step(List.of(left, right), 1.0);
        }

        // Each cube is 1 wide, so their centres can never legitimately be closer than ~1 apart. Ending
        // up nearer than that means one is inside the other; ending up with left PAST right means they
        // tunnelled clean through.
        double gap = right.body.position.x - left.body.position.x;
        assertTrue(gap > 0.85, "the cubes must not be inside each other; centre gap=" + gap);
    }

    @Test
    @DisplayName("floatability decides flotation, and mass does not")
    void floatabilityDecidesFlotation() {
        // Deliberately the SAME mass for all three. Under the old model mass alone decided this, so a
        // heavy block could never be made to float; that is exactly the coupling floatability removes.
        PhysBody floats = cube(30.0);
        floats.floatability = 0.0;
        floats.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, false);
        PhysBody sinks = cube(30.0);
        sinks.floatability = 1.0;
        sinks.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, false);
        PhysBody rises = cube(30.0);
        rises.floatability = -1.0;
        rises.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, false);

        double start = floats.body.position.y;
        for (int tick = 0; tick < 60; tick++) {
            XpbdSolver.step(List.of(floats), 1.0);
            XpbdSolver.step(List.of(sinks), 1.0);
            XpbdSolver.step(List.of(rises), 1.0);
        }

        assertTrue(sinks.body.position.y < start - 0.5,
                "floatability 1 keeps full gravity in water — it must sink; y=" + sinks.body.position.y);
        assertTrue(rises.body.position.y > start + 0.5,
                "floatability -1 must rise; y=" + rises.body.position.y);
        // 0 is fully buoyant, so it must RISE toward the surface — not hold its depth. Holding depth is
        // what a lift of exactly 1x the body's weight buys, and that is neutral buoyancy rather than
        // flotation; see XpbdSolver#BUOYANCY_GAIN.
        assertTrue(floats.body.position.y > start + 0.5,
                "floatability 0 must float up, not hover; y=" + floats.body.position.y);
    }

    @Test
    @DisplayName("floatability does not touch gravity in air — a floaty block still falls")
    void floatabilityIsFluidOnly() {
        PhysBody wood = cube(6.0);
        wood.floatability = 0.0; // floats in water...
        wood.world = WorldBlockCache.EMPTY; // ...but there is no water here

        double start = wood.body.position.y;
        for (int tick = 0; tick < 40; tick++) {
            XpbdSolver.step(List.of(wood), 1.0);
        }

        assertTrue(wood.body.position.y < start - 1.0,
                "a floatability-0 block is not weightless — in AIR it must fall; y=" + wood.body.position.y);
    }

    @Test
    @DisplayName("stone sinks in water but rides on lava — one expression, no special case")
    void lavaFloatsWhatWaterDoesNot() {
        PhysBody inWater = cube(15.0);
        inWater.floatability = 1.0; // stone
        inWater.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, false);
        PhysBody inLava = cube(15.0);
        inLava.floatability = 1.0;
        inLava.world = WorldBlockCache.fluidRegion(-2, -8, -2, 2, 8, 2, 8.0, true);

        double start = inWater.body.position.y;
        for (int tick = 0; tick < 60; tick++) {
            XpbdSolver.step(List.of(inWater), 1.0);
            XpbdSolver.step(List.of(inLava), 1.0);
        }

        // water: (1 - 1) = 0 lift -> sinks.  lava: (2 - 1) = 1x lift -> holds.
        assertTrue(inWater.body.position.y < start - 0.5, "stone must sink in water; y=" + inWater.body.position.y);
        assertTrue(inLava.body.position.y > inWater.body.position.y,
                "the SAME stone must ride higher on lava; lava=" + inLava.body.position.y
                        + " water=" + inWater.body.position.y);
    }
}
