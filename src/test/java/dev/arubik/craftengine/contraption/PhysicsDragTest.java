package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;

/**
 * Pins the chain that carries a player standing on a PHYS contraption.
 *
 * <h2>What was broken, and why a test exists at all</h2>
 * Riders are not moved by reading a contraption's position. They are moved by
 * {@code ContraptionState#lastDeltaX/Y/Z} and {@code lastYawDelta}, which
 * {@code ContraptionEngine#stepKinematics} derives — the translation from the summed
 * {@link MovementBehavior#velocityThisTick()}, and the rotation from comparing the state's angles
 * against a snapshot taken BEFORE the behaviors ticked. Every carry and push path
 * ({@code carryRiders}, {@code carryNearbyEntities}, {@code pushBackNearby*}) reads only those
 * deltas.
 *
 * <p>The physics rewrite bypassed that contract: the solver wrote the transform straight onto the
 * state and {@link PhysicsBehavior} reported {@code Vec3.ZERO}. Both deltas were therefore zero on
 * every tick, and a phys contraption — however fast it fell or spun — reported itself motionless,
 * so a player standing on one was silently left behind in mid-air.
 *
 * <p>Nothing about that failure is visible from the contraption's own movement, which looked
 * perfect. It is only visible in the deltas, which is exactly what these tests assert.
 */
class PhysicsDragTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @AfterEach
    void clearRegistry() {
        for (ContraptionEntity e : java.util.List.copyOf(ContraptionManager.all())) {
            ContraptionManager.remove(e.state().id());
        }
    }

    /** Pure kinematics — no ContraptionLevel needed (see ContraptionState's null-tolerance javadoc). */
    private static ContraptionState state() {
        return new ContraptionState(UUID.randomUUID(), UUID.randomUUID(), null, 0, 64, 0);
    }

    @Test
    @DisplayName("the solved translation is REPORTED, so stepKinematics can derive lastDelta from it")
    void reportsSolvedTranslationAsVelocity() {
        PhysicsBehavior behavior = new PhysicsBehavior();
        behavior.setPendingMotion(new Vec3(0.25, -0.4, 0.125), 0.0, 0.0, 0.0);

        Vec3 reported = behavior.velocityThisTick();

        assertEquals(0.25, reported.x, 1.0E-9);
        assertEquals(-0.4, reported.y, 1.0E-9);
        assertEquals(0.125, reported.z, 1.0E-9);
        // The regression itself: a zero here is what stranded riders, and it looked like nothing was
        // wrong because the contraption still moved (the solver had already placed it).
        assertNotEquals(Vec3.ZERO, reported);
    }

    @Test
    @DisplayName("rotation is applied INSIDE tick(), which is the only point stepKinematics can see it")
    void appliesRotationDuringTickSoAngularDeltasAreDerived() {
        ContraptionState state = state();
        PhysicsBehavior behavior = new PhysicsBehavior();
        state.addBehavior(behavior);

        // What stepKinematics does: snapshot the angles, tick the behaviors, then difference them.
        double yawBefore = state.yawRadians();
        double pitchBefore = state.pitchRadians();
        double rollBefore = state.rollRadians();

        behavior.setPendingMotion(Vec3.ZERO, 0.30, 0.10, 0.05);
        behavior.tick(new MovementContext(state, null));

        assertEquals(0.30, state.yawRadians() - yawBefore, 1.0E-9);
        assertEquals(0.10, state.pitchRadians() - pitchBefore, 1.0E-9);
        assertEquals(0.05, state.rollRadians() - rollBefore, 1.0E-9);
    }

    @Test
    @DisplayName("a body that has not been solved reports no motion, so it cannot drift")
    void reportsNothingWithoutASolve() {
        PhysicsBehavior behavior = new PhysicsBehavior();

        assertEquals(Vec3.ZERO, behavior.velocityThisTick());

        // A solve landed, then the world unloaded and PhysicsWorld skipped the body. Retaining the
        // old delta would re-apply it every tick forever and walk the contraption away at its
        // last-known speed with no solver left to correct it.
        behavior.setPendingMotion(new Vec3(0.5, 0.0, 0.0), 0.0, 0.0, 0.0);
        behavior.clearPendingMotion();

        assertEquals(Vec3.ZERO, behavior.velocityThisTick());
    }

    /**
     * Stands in for {@link PhysicsBehavior} against the ENGINE half of the chain.
     *
     * <p>The real behavior cannot be driven through {@code ContraptionEngine#tickAll} here:
     * {@code PhysicsWorld#stepAll} runs first, finds no Bukkit world (there is no server under a unit
     * test), correctly treats the contraption as unloaded, and clears its pending motion. So this
     * asserts the engine contract — "a behavior that reports a velocity and rotates during tick() gets
     * both deltas derived" — while the tests above assert that {@link PhysicsBehavior} does exactly
     * those two things. Together that is the whole path.
     */
    private static final class ReportingBehavior implements MovementBehavior {
        private final Vec3 delta;
        private final double yaw;

        ReportingBehavior(Vec3 delta, double yaw) {
            this.delta = delta;
            this.yaw = yaw;
        }

        @Override
        public void tick(MovementContext ctx) {
            ctx.state().setYawRadians(ctx.state().yawRadians() + yaw);
        }

        @Override
        public boolean isStalled() {
            return false;
        }

        @Override
        public Vec3 velocityThisTick() {
            return delta;
        }
    }

    @Test
    @DisplayName("the engine turns a reported velocity + tick-time rotation into the deltas riders are carried by")
    void reportedMotionBecomesTheRiderCarryDeltas() {
        ContraptionState state = state();
        state.addBehavior(new ReportingBehavior(new Vec3(0.0, -0.32, 0.0), 0.25));
        ContraptionManager.register(new ContraptionEntity(state));

        double startY = state.y();
        ContraptionEngine.tickAll();

        // The contraption moved...
        assertEquals(startY - 0.32, state.y(), 1.0E-9);
        // ...and, critically, SAID SO. These are the exact values carryRiders/carryNearbyEntities
        // read; a zero in either is a rider left floating where the contraption used to be.
        assertEquals(-0.32, state.lastDeltaY(), 1.0E-9);
        assertEquals(0.25, state.lastYawDelta(), 1.0E-9);
        assertTrue(Math.abs(state.lastDeltaY()) > 0.0, "a falling contraption must report a nonzero delta");
    }
}
