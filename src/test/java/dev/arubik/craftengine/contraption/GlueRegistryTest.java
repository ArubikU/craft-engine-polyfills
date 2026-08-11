package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** Per-world glue graph registry behaviour for {@link GlueRegistry}. */
class GlueRegistryTest {

    private static final ResourceKey<Level> TEST_WORLD_1 =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test1"));
    private static final ResourceKey<Level> TEST_WORLD_2 =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test2"));

    @AfterEach
    void cleanup() {
        GlueRegistry.clear(TEST_WORLD_1);
        GlueRegistry.clear(TEST_WORLD_2);
    }

    private static BlockPos p(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Test
    void unglued_blockIsItsOwnSingletonStructure() {
        assertEquals(Set.of(p(5, 5, 5)), GlueRegistry.structureAt(TEST_WORLD_1, p(5, 5, 5)));
    }

    @Test
    void gluedBlocks_shareTheStructure() {
        ResourceKey<Level> world = TEST_WORLD_1;
        BlockPos a = p(0, 0, 0), b = p(1, 0, 0), c = p(2, 0, 0);
        GlueRegistry.graphFor(world).glue(a, b);
        GlueRegistry.graphFor(world).glue(b, c);

        assertEquals(Set.of(a, b, c), GlueRegistry.structureAt(world, a));
        assertEquals(Set.of(a, b, c), GlueRegistry.structureAt(world, c));
    }

    @Test
    void differentWorlds_haveIndependentGraphs() {
        ResourceKey<Level> w1 = TEST_WORLD_1;
        ResourceKey<Level> w2 = TEST_WORLD_2;
        GlueRegistry.graphFor(w1).glue(p(0, 0, 0), p(1, 0, 0));

        assertEquals(Set.of(p(0, 0, 0), p(1, 0, 0)), GlueRegistry.structureAt(w1, p(0, 0, 0)));
        assertEquals(Set.of(p(0, 0, 0)), GlueRegistry.structureAt(w2, p(0, 0, 0)));
    }

    @Test
    void graphFor_returnsSameInstanceForSameWorld() {
        ResourceKey<Level> world = TEST_WORLD_1;
        assertSame(GlueRegistry.graphFor(world), GlueRegistry.graphFor(world));
    }

    @Test
    void clear_removesTheGraphEntirely() {
        ResourceKey<Level> world = TEST_WORLD_1;
        GlueRegistry.graphFor(world).glue(p(0, 0, 0), p(1, 0, 0));
        GlueRegistry.clear(world);
        assertEquals(Set.of(p(0, 0, 0)), GlueRegistry.structureAt(world, p(0, 0, 0)));
    }
}
