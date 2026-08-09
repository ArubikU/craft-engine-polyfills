package dev.arubik.craftengine.machine.menu.layout;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MachineLayoutTest {

    @Test
    void legacyAddSlotUsesTheSameVisualAndMachineSlot() {
        MachineLayout layout = new MachineLayout(null, 9, "Legacy");

        layout.addSlot(2, MenuSlotType.INPUT);

        assertEquals(MenuSlotType.INPUT, layout.getSlotType(2));
        assertEquals(2, layout.getMachineSlot(2));
    }

    @Test
    void explicitMachineSlotMappingIsPreserved() {
        MachineLayout layout = new MachineLayout(null, 9, "Mapped");

        layout.addSlot(1, MenuSlotType.INPUT, 5);
        layout.addSlot(4, MenuSlotType.FUEL, 8);

        assertEquals(5, layout.getMachineSlot(1));
        assertEquals(8, layout.getMachineSlot(4));
        assertArrayEquals(new int[] { 1 }, layout.getSlotsOfType(MenuSlotType.INPUT));
        assertArrayEquals(new int[] { 4 }, layout.getSlotsOfType(MenuSlotType.FUEL));
    }

    @Test
    void slotQueriesAreSortedAndOnlyReturnConfiguredTypes() {
        MachineLayout layout = new MachineLayout(null, 6, "Background");

        layout.addSlot(5, MenuSlotType.INPUT, 9);
        layout.addSlot(1, MenuSlotType.INPUT, 7);
        layout.setDynamicProvider(3, (machine, tick) -> null);

        assertEquals(MenuSlotType.INPUT, layout.getSlotType(1));
        assertEquals(MenuSlotType.INPUT, layout.getSlotType(5));
        assertEquals(MenuSlotType.DYNAMIC, layout.getSlotType(3));
        assertArrayEquals(new int[] { 1, 5 }, layout.getSlotsOfType(MenuSlotType.INPUT));
        assertArrayEquals(new int[] { 3 }, layout.getSlotsOfType(MenuSlotType.DYNAMIC));
        assertArrayEquals(new int[0], layout.getSlotsOfType(MenuSlotType.BACKGROUND));
    }
}