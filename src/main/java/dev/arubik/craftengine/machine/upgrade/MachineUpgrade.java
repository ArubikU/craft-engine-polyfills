package dev.arubik.craftengine.machine.upgrade;

import net.momirealms.craftengine.core.util.Key;

/**
 * A registered upgrade module: an item id that, when placed in a machine's
 * upgrade slot, applies an effect.
 *
 * @param itemId    the craft-engine item id (or vanilla id) that represents this upgrade
 * @param type      what the upgrade does
 * @param perItem   the magnitude contributed by a single item
 * @param maxCount  the maximum number of this item that contributes (0 = unlimited);
 *                  counts beyond this are ignored, preventing degenerate stacking
 */
public record MachineUpgrade(Key itemId, UpgradeType type, double perItem, int maxCount) {

    public MachineUpgrade {
        if (itemId == null) throw new IllegalArgumentException("itemId must not be null");
        if (type == null) throw new IllegalArgumentException("type must not be null");
        if (maxCount < 0) throw new IllegalArgumentException("maxCount must be >= 0");
    }

    /** Effective contributing count given a raw count, honoring {@link #maxCount}. */
    public int effectiveCount(int rawCount) {
        if (rawCount <= 0) return 0;
        return maxCount == 0 ? rawCount : Math.min(rawCount, maxCount);
    }
}
