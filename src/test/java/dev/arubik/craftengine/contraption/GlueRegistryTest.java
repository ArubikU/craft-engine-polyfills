package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;

/** Per-world glue graph registry behaviour for {@link GlueRegistry}. */
class GlueRegistryTest {

    private static BlockPos p(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Test
    void unglued_blockIsItsOwnSingletonStructure() {
        UUID world = UUID.randomUUID();
        assertEquals(Set.of(p(5, 5, 5)), GlueRegistry.structureAt(world, p(5, 5, 5)));
    }

    @Test
    void gluedBlocks_shareTheStructure() {
        UUID world = UUID.randomUUID();
        BlockPos a = p(0, 0, 0), b = p(1, 0, 0), c = p(2, 0, 0);
        GlueRegistry.graphFor(world).glue(a, b);
        GlueRegistry.graphFor(world).glue(b, c);

        assertEquals(Set.of(a, b, c), GlueRegistry.structureAt(world, a));
        assertEquals(Set.of(a, b, c), GlueRegistry.structureAt(world, c));
    }

    @Test
    void differentWorlds_haveIndependentGraphs() {
        UUID w1 = UUID.randomUUID();
        UUID w2 = UUID.randomUUID();
        GlueRegistry.graphFor(w1).glue(p(0, 0, 0), p(1, 0, 0));

        assertEquals(Set.of(p(0, 0, 0), p(1, 0, 0)), GlueRegistry.structureAt(w1, p(0, 0, 0)));
        assertEquals(Set.of(p(0, 0, 0)), GlueRegistry.structureAt(w2, p(0, 0, 0)));
    }

    @Test
    void graphFor_returnsSameInstanceForSameWorld() {
        UUID world = UUID.randomUUID();
        assertSame(GlueRegistry.graphFor(world), GlueRegistry.graphFor(world));
    }

    @Test
    void clear_removesTheGraphEntirely() {
        UUID world = UUID.randomUUID();
        GlueRegistry.graphFor(world).glue(p(0, 0, 0), p(1, 0, 0));
        GlueRegistry.clear(world);
        assertEquals(Set.of(p(0, 0, 0)), GlueRegistry.structureAt(world, p(0, 0, 0)));
    }
}
