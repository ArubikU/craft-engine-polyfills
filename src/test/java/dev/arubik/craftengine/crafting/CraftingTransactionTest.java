package dev.arubik.craftengine.crafting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.core.util.Key;

/**
 * Pure unit tests for the take / shift-click count math in
 * {@link CraftingTransaction}. No Bukkit on the classpath.
 */
class CraftingTransactionTest {

    @Test
    void maxCraftsBoundedByInputs() {
        // 3 inputs available (min 4), single output 1/craft, ample room.
        int n = CraftingTransaction.maxCrafts(new int[] { 8, 4, 16 }, new int[] { 1 }, new int[] { 9999 });
        assertEquals(4, n); // limited by the input with only 4
    }

    @Test
    void maxCraftsBoundedByOutputRoom() {
        // Inputs allow 64 crafts; output is 4/craft; room for only 10 of that output.
        int n = CraftingTransaction.maxCrafts(new int[] { 64 }, new int[] { 4 }, new int[] { 10 });
        assertEquals(2, n); // 2*4=8 fits, 3*4=12 would overflow 10 -> floor
    }

    @Test
    void maxCraftsBoundedByTightestOfMultipleOutputs() {
        // Two outputs: primary 1/craft (room 100), secondary 2/craft (room 5).
        int n = CraftingTransaction.maxCrafts(new int[] { 50 }, new int[] { 1, 2 }, new int[] { 100, 5 });
        assertEquals(2, n); // secondary caps it: 2*2=4<=5, 3*2=6>5
    }

    @Test
    void zeroWhenAnyInputEmpty() {
        int n = CraftingTransaction.maxCrafts(new int[] { 5, 0, 3 }, new int[] { 1 }, new int[] { 100 });
        assertEquals(0, n);
    }

    @Test
    void zeroWhenNoRoom() {
        int n = CraftingTransaction.maxCrafts(new int[] { 5 }, new int[] { 1 }, new int[] { 0 });
        assertEquals(0, n);
    }

    @Test
    void zeroWhenNoInputs() {
        int n = CraftingTransaction.maxCrafts(new int[] {}, new int[] { 1 }, new int[] { 100 });
        assertEquals(0, n);
    }

    @Test
    void outputProducingNothingImposesNoConstraint() {
        int n = CraftingTransaction.maxCrafts(new int[] { 7 }, new int[] { 0 }, new int[] { 0 });
        assertEquals(7, n); // per==0 output skipped; bounded only by inputs
    }

    @Test
    void cellOverloadMapsCounts() {
        // outputs 1x primary + 3x secondary; inputs allow 10; room: 100 / 6.
        List<CraftCell> outs = List.of(
                CraftCell.of(Key.of("t", "a"), 1),
                CraftCell.of(Key.of("t", "b"), 3));
        int n = CraftingTransaction.maxCrafts(new int[] { 10 }, outs, new int[] { 100, 6 });
        assertEquals(2, n); // secondary: 2*3=6<=6, 3*3=9>6
    }

    @Test
    void normalTakeNeverOverflows() {
        // Simulate the base's normal-take bound: min(1, maxCrafts).
        int n = Math.min(1, CraftingTransaction.maxCrafts(new int[] { 1 }, new int[] { 4 }, new int[] { 2 }));
        assertEquals(0, n); // can't even fit one craft's output -> no craft
        int ok = Math.min(1, CraftingTransaction.maxCrafts(new int[] { 1 }, new int[] { 4 }, new int[] { 8 }));
        assertEquals(1, ok);
    }
}
