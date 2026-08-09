package dev.arubik.craftengine.pipe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PipeTypeTest {

    @Test
    void copperPipeConnectsToFluidBlockTank() {
        assertTrue(PipeType.COPPER.connectsTo().contains("cml:fluid_block_tank"));
    }
}