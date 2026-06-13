package dev.arubik.craftengine.machine.upgrade;

import java.util.Map;

import net.momirealms.craftengine.core.util.Key;

/**
 * The aggregate effect of all upgrades installed in a machine.
 *
 * <p>Pure value object computed from item counts and a {@link UpgradeRegistry};
 * carries no Bukkit/NMS state so the aggregation is unit-testable. A machine
 * applies these to its processing loop:</p>
 * <ul>
 *   <li>{@link #speedMultiplier} &mdash; progress advances this many times faster
 *       (use {@link #stepFor(int)} to turn a base per-tick step into an upgraded one).</li>
 *   <li>{@link #fuelMultiplier} &mdash; fraction of base fuel consumed (≤ 1).</li>
 *   <li>{@link #yieldBonus} &mdash; expected number of extra outputs per craft (≥ 0).</li>
 * </ul>
 */
public final class UpgradeModifiers {

    public static final UpgradeModifiers NONE = new UpgradeModifiers(1.0d, 1.0d, 0.0d);

    private final double speedMultiplier;
    private final double fuelMultiplier;
    private final double yieldBonus;

    public UpgradeModifiers(double speedMultiplier, double fuelMultiplier, double yieldBonus) {
        this.speedMultiplier = speedMultiplier;
        this.fuelMultiplier = fuelMultiplier;
        this.yieldBonus = yieldBonus;
    }

    public double speedMultiplier() { return speedMultiplier; }
    public double fuelMultiplier() { return fuelMultiplier; }
    public double yieldBonus() { return yieldBonus; }

    /**
     * Aggregates upgrades from a map of installed item ids to their counts.
     *
     * <p>Unknown ids are ignored. SPEED contributions sum onto a base of 1.0 and
     * are capped at {@link UpgradeType#MAX_SPEED}. EFFICIENCY contributions subtract
     * from 1.0, floored at {@link UpgradeType#MIN_FUEL}. YIELD contributions sum.</p>
     */
    public static UpgradeModifiers compute(Map<Key, Integer> itemCounts, UpgradeRegistry registry) {
        double speed = 1.0d;
        double fuel = 1.0d;
        double yield = 0.0d;
        if (itemCounts != null && registry != null) {
            for (Map.Entry<Key, Integer> e : itemCounts.entrySet()) {
                MachineUpgrade up = registry.get(e.getKey()).orElse(null);
                if (up == null) continue;
                int count = up.effectiveCount(e.getValue() == null ? 0 : e.getValue());
                if (count <= 0) continue;
                double total = up.perItem() * count;
                switch (up.type()) {
                    case SPEED -> speed += total;
                    case EFFICIENCY -> fuel -= total;
                    case YIELD -> yield += total;
                }
            }
        }
        speed = Math.min(Math.max(speed, 1.0d), UpgradeType.MAX_SPEED);
        fuel = Math.min(Math.max(fuel, UpgradeType.MIN_FUEL), 1.0d);
        yield = Math.max(yield, 0.0d);
        return new UpgradeModifiers(speed, fuel, yield);
    }

    /**
     * Turns a base per-tick progress step into the upgraded step, rounding so that
     * a 2x speed advances 2 progress/tick. Always at least 1 while running.
     */
    public int stepFor(int baseStep) {
        int step = (int) Math.round(baseStep * speedMultiplier);
        return Math.max(step, baseStep);
    }

    /** Splits {@link #yieldBonus} into a guaranteed extra count plus a fractional chance [0,1). */
    public int guaranteedBonus() { return (int) Math.floor(yieldBonus); }
    public double bonusChance() { return yieldBonus - Math.floor(yieldBonus); }

    @Override
    public String toString() {
        return "UpgradeModifiers[speed=" + speedMultiplier + ", fuel=" + fuelMultiplier + ", yield=" + yieldBonus + "]";
    }
}
