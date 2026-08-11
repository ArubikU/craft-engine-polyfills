package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.behavior.LinearActuatorBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEngine;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;

/** Master-clock stall-gate + kinematics behaviour for {@link ContraptionEngine}. */
class ContraptionEngineTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static final ResourceKey<Level> TEST_WORLD =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test"));

    @AfterEach
    void clearRegistry() {
        for (ContraptionEntity e : java.util.List.copyOf(ContraptionManager.all())) {
            ContraptionManager.remove(e.state().id());
        }
    }

    /** Pure kinematics test — no ContraptionLevel needed (see ContraptionState's javadoc on null-tolerance). */
    private static ContraptionEntity register() {
        ContraptionState state = new ContraptionState(UUID.randomUUID(), TEST_WORLD, null, 0, 64, 0);
        return ContraptionManager.register(new ContraptionEntity(state));
    }

    /** A behavior that never moves anything but can be told to stall on demand — proves the vote gates ALL movement. */
    private static final class StallableBehavior implements MovementBehavior {
        boolean stalled = false;
        int tickCount = 0;

        @Override
        public void tick(MovementContext ctx) {
            tickCount++;
        }

        @Override
        public boolean isStalled() {
            return stalled;
        }
    }

    @Test
    void linearActuator_movesTheBearingEachTick() {
        ContraptionEntity entity = register();
        // 20 blocks/sec on X -> exactly 1 block/tick.
        entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(20, 0, 0));

        ContraptionEngine.tickAll();

        assertEquals(1.0, entity.state().x(), 1e-9);
        assertEquals(64.0, entity.state().y(), 1e-9);
        assertEquals(0.0, entity.state().z(), 1e-9);
        assertFalse(entity.state().isStalled());
    }

    @Test
    void stalledBehavior_freezesMovementButStillTicks() {
        ContraptionEntity entity = register();
        entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(20, 0, 0));
        StallableBehavior stall = new StallableBehavior();
        stall.stalled = true;
        entity.state().addBehavior(stall);

        ContraptionEngine.tickAll();

        assertEquals(0.0, entity.state().x(), 1e-9, "movement must be zeroed while stalled");
        assertTrue(entity.state().isStalled());
        assertEquals(1, stall.tickCount, "internal state still advances while stalled");
    }

    @Test
    void unstalling_resumesMovementNextTick() {
        ContraptionEntity entity = register();
        entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(20, 0, 0));
        StallableBehavior stall = new StallableBehavior();
        stall.stalled = true;
        entity.state().addBehavior(stall);

        ContraptionEngine.tickAll(); // stalled, no movement
        stall.stalled = false;
        ContraptionEngine.tickAll(); // now free

        assertEquals(1.0, entity.state().x(), 1e-9);
        assertFalse(entity.state().isStalled());
    }

    @Test
    void multipleBehaviors_velocitiesSum() {
        ContraptionEntity entity = register();
        entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(20, 0, 0));
        entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(0, 0, 10));

        ContraptionEngine.tickAll();

        assertEquals(1.0, entity.state().x(), 1e-9);
        assertEquals(0.5, entity.state().z(), 1e-9);
    }

    @Test
    void noBehaviors_positionNeverChanges() {
        ContraptionEntity entity = register();
        double x0 = entity.state().x();
        ContraptionEngine.tickAll();
        ContraptionEngine.tickAll();
        assertEquals(x0, entity.state().x(), 1e-9);
    }

    @Test
    void linearActuatorBehavior_velocityIsConstantEveryTick() {
        LinearActuatorBehavior actuator = LinearActuatorBehavior.blocksPerSecond(20, -10, 5);
        Vec3 v1 = actuator.velocityThisTick();
        actuator.tick(null);
        Vec3 v2 = actuator.velocityThisTick();
        assertEquals(v1, v2);
        assertEquals(1.0, v1.x, 1e-9);
        assertEquals(-0.5, v1.y, 1e-9);
        assertEquals(0.25, v1.z, 1e-9);
        assertFalse(actuator.isStalled());
    }
}
