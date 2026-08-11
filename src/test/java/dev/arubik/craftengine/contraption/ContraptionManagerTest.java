package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;

/**
 * Registry + facade behaviour for {@link ContraptionManager}/{@link ContraptionEntity} that
 * doesn't need a real {@link dev.arubik.craftengine.contraption.level.ContraptionLevel}
 * (that's a genuine {@code net.minecraft.world.level.Level} subclass — constructing one
 * needs a live Bukkit server, see {@link ContraptionState}'s javadoc). Swarm-cell-count
 * behavior against real captured blocks is verified live instead (via
 * {@code /cep contraption spawn-holo}), same as {@code ContraptionCapture} itself.
 */
class ContraptionManagerTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static final ResourceKey<Level> TEST_WORLD =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test"));

    private static ContraptionEntity entityWithNullLevel() {
        ContraptionState state = new ContraptionState(UUID.randomUUID(), TEST_WORLD, null, 0, 64, 0);
        return new ContraptionEntity(state);
    }

    @Test
    void registerMakesEntityRetrievableById() {
        ContraptionEntity entity = entityWithNullLevel();
        ContraptionManager.register(entity);
        assertSame(entity, ContraptionManager.get(entity.state().id()));
        ContraptionManager.remove(entity.state().id());
    }

    @Test
    void removeDropsItFromTheRegistry() {
        ContraptionEntity entity = entityWithNullLevel();
        ContraptionManager.register(entity);
        ContraptionManager.remove(entity.state().id());
        assertNull(ContraptionManager.get(entity.state().id()));
    }

    @Test
    void unknownIdReturnsNull() {
        assertNull(ContraptionManager.get(UUID.randomUUID()));
    }

    @Test
    void nullLevel_rebuildSwarmIsANoOpNotACrash() {
        ContraptionEntity entity = entityWithNullLevel();
        entity.rebuildSwarm(java.util.List.of());
        assertEquals(0, entity.cellCount());
    }

    @Test
    void despawnClearsRenderedCells() {
        ContraptionEntity entity = entityWithNullLevel();
        entity.despawn(List.of());
        assertEquals(0, entity.cellCount());
    }

    @Test
    void stateTransformDefaultsToZeroYaw() {
        ContraptionEntity entity = entityWithNullLevel();
        assertEquals(0.0, entity.state().yawRadians());
        assertTrue(!entity.state().isStalled());
    }
}
