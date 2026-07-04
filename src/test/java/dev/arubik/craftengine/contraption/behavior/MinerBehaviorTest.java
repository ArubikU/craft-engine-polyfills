package dev.arubik.craftengine.contraption.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.core.BlockPos;

/**
 * {@link MinerBehavior}'s world-touching path needs a real {@code ServerLevel} (not
 * constructible in a pure-JVM unit test — that's what {@code ContraptionAccessor}'s live
 * server testing is for). This only covers the one branch that's safe to unit test: no
 * world loaded yet (level == null), matching what {@code ContraptionEngine} passes under a
 * pure-JVM test or before a contraption's world has loaded.
 */
class MinerBehaviorTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void noLevelLoaded_neverStallsAndNeverThrows() {
        MinerBehavior miner = new MinerBehavior(new BlockPos(1, 0, 0), 1.0);
        miner.setInputRpm(10.0f);

        // MinerBehavior.tick returns immediately when level == null, never touching ctx.state() —
        // so state itself doesn't matter for this test (no ContraptionState/ContraptionLevel needed).
        miner.tick(new MovementContext(null, null));

        assertFalse(miner.isStalled());
        assertEquals(0.0, miner.accumulatedDamage());
        assertTrue(miner.virtualInventory().isEmpty());
    }

    @Test
    void zeroInputRpm_isInertNotStalled() {
        // Documented design choice (see MinerBehavior's javadoc): no power delivered means
        // inert, not stalled — distinct from the permanent bedrock stall.
        MinerBehavior miner = new MinerBehavior(new BlockPos(1, 0, 0), 1.0);
        assertEquals(0.0f, miner.getInputRpm());

        miner.tick(new MovementContext(null, null));
        assertFalse(miner.isStalled());
    }
}
