package dev.arubik.craftengine.machine.upgrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.core.util.Key;

class UpgradeModifiersTest {

    private static final Key SPEED = Key.of("test", "speed");
    private static final Key EFF = Key.of("test", "efficiency");
    private static final Key YIELD = Key.of("test", "yield");
    private static final Key UNKNOWN = Key.of("test", "unknown");

    private UpgradeRegistry registry() {
        return new UpgradeRegistry()
                .register(SPEED, UpgradeType.SPEED, 1.0, 4)        // +1 speed each, cap 4 items
                .register(EFF, UpgradeType.EFFICIENCY, 0.25, 3)    // -25% fuel each, cap 3
                .register(YIELD, UpgradeType.YIELD, 0.5, 0);       // +0.5 expected output each, unlimited
    }

    private static Map<Key, Integer> counts(Object... kv) {
        Map<Key, Integer> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((Key) kv[i], (Integer) kv[i + 1]);
        return m;
    }

    @Test
    void noneIsIdentity() {
        assertEquals(1.0, UpgradeModifiers.NONE.speedMultiplier());
        assertEquals(1.0, UpgradeModifiers.NONE.fuelMultiplier());
        assertEquals(0.0, UpgradeModifiers.NONE.yieldBonus());
    }

    @Test
    void emptyOrNullYieldsNone() {
        UpgradeModifiers m = UpgradeModifiers.compute(counts(), registry());
        assertEquals(1.0, m.speedMultiplier());
        assertEquals(1.0, m.fuelMultiplier());
        assertEquals(0.0, m.yieldBonus());
        UpgradeModifiers n = UpgradeModifiers.compute(null, registry());
        assertEquals(1.0, n.speedMultiplier());
    }

    @Test
    void speedSumsThenCapsAtCountAndMax() {
        // 3 speed items -> +3 -> 4x
        assertEquals(4.0, UpgradeModifiers.compute(counts(SPEED, 3), registry()).speedMultiplier());
        // 10 speed items but maxCount=4 -> +4 -> 5x (not 11x)
        assertEquals(5.0, UpgradeModifiers.compute(counts(SPEED, 10), registry()).speedMultiplier());
    }

    @Test
    void efficiencyFloorsAtMinFuel() {
        // 1 eff -> 0.75 fuel
        assertEquals(0.75, UpgradeModifiers.compute(counts(EFF, 1), registry()).fuelMultiplier(), 1e-9);
        // 3 eff -> 1 - 0.75 = 0.25
        assertEquals(0.25, UpgradeModifiers.compute(counts(EFF, 3), registry()).fuelMultiplier(), 1e-9);
        // maxCount=3 so 100 eff still 0.25 (above MIN_FUEL 0.10), never below floor
        double fuel = UpgradeModifiers.compute(counts(EFF, 100), registry()).fuelMultiplier();
        assertTrue(fuel >= UpgradeType.MIN_FUEL);
        assertEquals(0.25, fuel, 1e-9);
    }

    @Test
    void yieldSplitsIntoGuaranteedAndChance() {
        UpgradeModifiers m = UpgradeModifiers.compute(counts(YIELD, 3), registry()); // 1.5 expected
        assertEquals(1.5, m.yieldBonus(), 1e-9);
        assertEquals(1, m.guaranteedBonus());
        assertEquals(0.5, m.bonusChance(), 1e-9);
    }

    @Test
    void unknownItemsIgnoredAndTypesCombine() {
        UpgradeModifiers m = UpgradeModifiers.compute(counts(SPEED, 2, EFF, 2, YIELD, 1, UNKNOWN, 99), registry());
        assertEquals(3.0, m.speedMultiplier(), 1e-9);   // +2
        assertEquals(0.5, m.fuelMultiplier(), 1e-9);    // -0.5
        assertEquals(0.5, m.yieldBonus(), 1e-9);        // +0.5
    }

    @Test
    void stepForScalesWithSpeed() {
        UpgradeModifiers x2 = new UpgradeModifiers(2.0, 1.0, 0.0);
        assertEquals(2, x2.stepFor(1));
        assertEquals(1, UpgradeModifiers.NONE.stepFor(1));
    }

    @Test
    void effectiveCountHonorsMax() {
        MachineUpgrade up = new MachineUpgrade(SPEED, UpgradeType.SPEED, 1.0, 4);
        assertEquals(4, up.effectiveCount(10));
        assertEquals(2, up.effectiveCount(2));
        assertEquals(0, up.effectiveCount(0));
        assertFalse(new UpgradeRegistry().isUpgrade(UNKNOWN));
    }
}
