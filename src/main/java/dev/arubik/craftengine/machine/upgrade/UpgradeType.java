package dev.arubik.craftengine.machine.upgrade;

/**
 * The kinds of effect a machine upgrade module can have.
 *
 * <p>Each type defines how a single unit of magnitude folds into the aggregate
 * {@link UpgradeModifiers}. The aggregation is intentionally pure (no Bukkit /
 * NMS types) so it can be unit-tested on the JVM.</p>
 */
public enum UpgradeType {
    /** Speeds up processing. Each unit of magnitude adds to the speed multiplier (1.0 = base). */
    SPEED,
    /** Reduces fuel/energy consumption. Each unit of magnitude reduces the fuel multiplier toward {@link #MIN_FUEL}. */
    EFFICIENCY,
    /** Grants a chance for bonus output. Each unit of magnitude adds to the yield-bonus expected value. */
    YIELD;

    /** Fuel multiplier never drops below this (a machine always burns at least 10% fuel). */
    public static final double MIN_FUEL = 0.10d;
    /** Speed multiplier is capped to avoid runaway processing. */
    public static final double MAX_SPEED = 16.0d;
}
