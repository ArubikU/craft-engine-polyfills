package dev.arubik.craftengine.contraption.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;

class ContraptionHitboxSwarmTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static BlockPos p(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Test
    void singleBlock_isItsOwnTopCell() {
        assertEquals(Set.of(p(0, 0, 0)), dev.arubik.craftengine.contraption.element.ContraptionHitboxElement.topCellsOf(Set.of(p(0, 0, 0))));
    }

    @Test
    void twoStackedBlocks_onlyTopOneIsExposed() {
        Set<BlockPos> occupied = Set.of(p(0, 0, 0), p(0, 1, 0));
        assertEquals(Set.of(p(0, 1, 0)), dev.arubik.craftengine.contraption.element.ContraptionHitboxElement.topCellsOf(occupied));
    }

    @Test
    void flatPlatform_everyCellIsExposed() {
        Set<BlockPos> occupied = Set.of(p(0, 0, 0), p(1, 0, 0), p(0, 0, 1));
        assertEquals(occupied, dev.arubik.craftengine.contraption.element.ContraptionHitboxElement.topCellsOf(occupied));
    }

    @Test
    void emptySet_hasNoTopCells() {
        assertEquals(Set.of(), dev.arubik.craftengine.contraption.element.ContraptionHitboxElement.topCellsOf(Set.of()));
    }

    @Test
    void threeStackedBlocks_onlyTheVeryTopIsExposed() {
        Set<BlockPos> occupied = Set.of(p(0, 0, 0), p(0, 1, 0), p(0, 2, 0));
        assertEquals(Set.of(p(0, 2, 0)), dev.arubik.craftengine.contraption.element.ContraptionHitboxElement.topCellsOf(occupied));
    }
}
