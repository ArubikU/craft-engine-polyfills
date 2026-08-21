package dev.arubik.craftengine.script;

import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers the two runtime-configuration features: building a machine's IO on the fly, and pinning
 * menu slots so their contents cannot be moved.
 *
 * <p>These exercise the underlying model rather than the script wrappers, because the wrappers
 * need a live world; the wrappers are thin delegations over exactly these calls.
 */
class IoAndLayoutScriptTest {

    // ------------------------------------------------------------------ IO

    @Test
    @DisplayName("every IO type can be granted per face")
    void everyTypeCanBeConfigured() {
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        for (IOConfiguration.IOType t : IOConfiguration.IOType.values()) {
            cfg.addInput(t, Direction.UP);
            cfg.addOutput(t, Direction.DOWN);
        }
        for (IOConfiguration.IOType t : IOConfiguration.IOType.values()) {
            assertTrue(cfg.acceptsInput(t, Direction.UP), t + " input");
            assertTrue(cfg.providesOutput(t, Direction.DOWN), t + " output");
            assertFalse(cfg.acceptsInput(t, Direction.NORTH), t + " must not leak to other faces");
        }
    }

    @Test
    @DisplayName("input and output are independent per face")
    void inputAndOutputAreIndependent() {
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        IOConfiguration.IOType gas = IOConfiguration.IOType.GAS;

        cfg.addInput(gas, Direction.UP);
        assertTrue(cfg.acceptsInput(gas, Direction.UP));
        assertFalse(cfg.providesOutput(gas, Direction.UP),
                "granting input must not also open the face for output");

        cfg.addOutput(gas, Direction.UP);
        assertTrue(cfg.providesOutput(gas, Direction.UP));

        cfg.removeInput(gas, Direction.UP);
        assertFalse(cfg.acceptsInput(gas, Direction.UP));
        assertTrue(cfg.providesOutput(gas, Direction.UP), "removing one side keeps the other");
    }

    @Test
    @DisplayName("a face can be reconfigured repeatedly without residue")
    void facesCanBeRewritten() {
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        IOConfiguration.IOType item = IOConfiguration.IOType.ITEM;

        // What a pipe face cycling through input -> output -> disabled does.
        cfg.removeInput(item, Direction.NORTH).removeOutput(item, Direction.NORTH);
        cfg.addInput(item, Direction.NORTH);
        assertTrue(cfg.acceptsInput(item, Direction.NORTH));

        cfg.removeInput(item, Direction.NORTH).removeOutput(item, Direction.NORTH);
        cfg.addOutput(item, Direction.NORTH);
        assertFalse(cfg.acceptsInput(item, Direction.NORTH), "the old input must be gone");
        assertTrue(cfg.providesOutput(item, Direction.NORTH));

        cfg.removeInput(item, Direction.NORTH).removeOutput(item, Direction.NORTH);
        assertFalse(cfg.acceptsInput(item, Direction.NORTH));
        assertFalse(cfg.providesOutput(item, Direction.NORTH), "disabled means neither");
    }

    @Test
    @DisplayName("types do not bleed into each other")
    void typesAreIsolated() {
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOConfiguration.IOType.GAS, Direction.UP);
        assertFalse(cfg.acceptsInput(IOConfiguration.IOType.FLUID, Direction.UP),
                "opening gas must not open fluid on the same face");
        assertFalse(cfg.acceptsInput(IOConfiguration.IOType.ITEM, Direction.UP));
    }

    @Test
    @DisplayName("probing every pair reproduces a config exactly — the copy mutableIO makes")
    void probeBasedCopyIsFaithful() {
        // mutableIO rebuilds a private copy by probing the public interface rather than reaching
        // into Simple's fields, so it works for any implementation. This is that round trip.
        IOConfiguration.Simple original = new IOConfiguration.Simple();
        original.addInput(IOConfiguration.IOType.GAS, Direction.UP);
        original.addOutput(IOConfiguration.IOType.FLUID, Direction.DOWN);
        original.addInput(IOConfiguration.IOType.ITEM, Direction.NORTH);
        original.addOutput(IOConfiguration.IOType.ITEM, Direction.SOUTH);

        IOConfiguration.Simple copy = new IOConfiguration.Simple();
        for (IOConfiguration.IOType t : IOConfiguration.IOType.values()) {
            for (Direction d : Direction.values()) {
                if (original.acceptsInput(t, d)) copy.addInput(t, d);
                if (original.providesOutput(t, d)) copy.addOutput(t, d);
            }
        }

        for (IOConfiguration.IOType t : IOConfiguration.IOType.values()) {
            for (Direction d : Direction.values()) {
                assertEquals(original.acceptsInput(t, d), copy.acceptsInput(t, d), t + " in " + d);
                assertEquals(original.providesOutput(t, d), copy.providesOutput(t, d), t + " out " + d);
            }
        }
    }

    @Test
    @DisplayName("editing the copy leaves the original alone")
    void copyIsIndependent() {
        // The reason mutableIO copies at all: a machine points at its shared MachineDefinition's
        // config, and editing that in place would reconfigure every machine of the same type.
        IOConfiguration.Simple shared = new IOConfiguration.Simple();
        shared.addInput(IOConfiguration.IOType.GAS, Direction.UP);

        IOConfiguration.Simple mine = new IOConfiguration.Simple();
        for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
            for (Direction d : Direction.values()) {
                if (shared.acceptsInput(t, d)) mine.addInput(t, d);
                if (shared.providesOutput(t, d)) mine.addOutput(t, d);
            }

        mine.addOutput(IOConfiguration.IOType.GAS, Direction.DOWN);
        assertTrue(mine.providesOutput(IOConfiguration.IOType.GAS, Direction.DOWN));
        assertFalse(shared.providesOutput(IOConfiguration.IOType.GAS, Direction.DOWN),
                "the shared definition must be untouched");
    }

    @Test
    @DisplayName("clearing closes every face of every type")
    void clearClosesEverything() {
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
            for (Direction d : Direction.values()) cfg.addInput(t, d).addOutput(t, d);

        for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
            for (Direction d : Direction.values()) cfg.removeInput(t, d).removeOutput(t, d);

        for (IOConfiguration.IOType t : IOConfiguration.IOType.values())
            for (Direction d : Direction.values()) {
                assertFalse(cfg.acceptsInput(t, d), t + " " + d);
                assertFalse(cfg.providesOutput(t, d), t + " " + d);
            }
    }

    // -------------------------------------------------------------- layout

    private MachineLayout layout() {
        // null inventory type on purpose: Bukkit's InventoryType is registry-backed and cannot
        // initialize without a running server. The constructor only stores it, and none of the
        // slot-locking logic reads it.
        return new MachineLayout(null, 54, "test");
    }

    @Test
    @DisplayName("a slot starts unlocked and can be pinned and released")
    void lockRoundTrip() {
        MachineLayout l = layout();
        assertFalse(l.isLocked(13));
        l.setLocked(13, true);
        assertTrue(l.isLocked(13));
        l.setLocked(13, false);
        assertFalse(l.isLocked(13));
    }

    @Test
    @DisplayName("locking is independent of the slot's type")
    void lockIsIndependentOfSlotType() {
        // The point of the feature: pin a placeholder into a REAL input/output so the machine
        // still tracks the slot while the player cannot touch it.
        MachineLayout l = layout();
        l.addSlot(20, MenuSlotType.INPUT);
        l.addSlot(24, MenuSlotType.OUTPUT);
        l.setLocked(20, true);
        l.setLocked(24, true);

        assertEquals(MenuSlotType.INPUT, l.getSlotType(20), "the slot keeps its kind");
        assertEquals(MenuSlotType.OUTPUT, l.getSlotType(24));
        assertTrue(l.isLocked(20));
        assertTrue(l.isLocked(24));
    }

    @Test
    @DisplayName("locked slots are reported in order")
    void lockedSlotsAreListed() {
        MachineLayout l = layout();
        l.setLocked(30, true);
        l.setLocked(4, true);
        l.setLocked(17, true);
        assertArrayEquals(new int[]{4, 17, 30}, l.getLockedSlots());
    }

    @Test
    @DisplayName("locking every slot of a kind pins exactly those")
    void lockByType() {
        MachineLayout l = layout();
        l.addSlot(10, MenuSlotType.UPGRADE);
        l.addSlot(11, MenuSlotType.UPGRADE);
        l.addSlot(12, MenuSlotType.INPUT);

        for (int s : l.getSlotsOfType(MenuSlotType.UPGRADE)) l.setLocked(s, true);

        assertTrue(l.isLocked(10));
        assertTrue(l.isLocked(11));
        assertFalse(l.isLocked(12), "a slot of another kind must be left alone");
    }

    @Test
    @DisplayName("unlocking everything clears the whole set")
    void unlockAll() {
        MachineLayout l = layout();
        l.setLocked(1, true);
        l.setLocked(2, true);
        for (int s : l.getLockedSlots()) l.setLocked(s, false);
        assertEquals(0, l.getLockedSlots().length);
    }

    @Test
    @DisplayName("locking is idempotent")
    void lockingTwiceIsHarmless() {
        MachineLayout l = layout();
        l.setLocked(7, true);
        l.setLocked(7, true);
        assertArrayEquals(new int[]{7}, l.getLockedSlots(), "no duplicate entries");
        l.setLocked(7, false);
        l.setLocked(7, false);
        assertEquals(0, l.getLockedSlots().length);
    }
}
