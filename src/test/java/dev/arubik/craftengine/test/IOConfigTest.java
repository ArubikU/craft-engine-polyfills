package dev.arubik.craftengine.test;

import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.util.TransferAccessMode;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IOConfigTest {

    @Test
    public void testOpenConfiguration() {
        IOConfiguration config = new IOConfiguration.Open();
        assertTrue(config.acceptsInput(IOType.FLUID, Direction.NORTH));
        assertTrue(config.providesOutput(IOType.GAS, Direction.UP));
        assertTrue(config.canConnect(Direction.WEST));
    }

    @Test
    public void testClosedConfiguration() {
        IOConfiguration config = new IOConfiguration.Closed();
        assertFalse(config.acceptsInput(IOType.FLUID, Direction.NORTH));
        assertFalse(config.providesOutput(IOType.GAS, Direction.UP));
        assertFalse(config.canConnect(Direction.WEST));
    }

    @Test
    public void testSimpleConfiguration() {
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        config.addInput(IOType.FLUID, Direction.UP);
        config.addOutput(IOType.GAS, Direction.DOWN);

        assertTrue(config.acceptsInput(IOType.FLUID, Direction.UP));
        assertFalse(config.acceptsInput(IOType.FLUID, Direction.DOWN));

        assertTrue(config.providesOutput(IOType.GAS, Direction.DOWN));
        assertFalse(config.providesOutput(IOType.GAS, Direction.UP));
    }

    @Test
    public void testAccessModeEnum() {
        // Verify existence of NONE
        assertNotNull(TransferAccessMode.NONE);
        assertEquals(3, TransferAccessMode.values().length);
    }
}
