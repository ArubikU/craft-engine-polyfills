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
 * @param machines  machine ids this upgrade applies to; EMPTY means every machine.
 *                  Scoping exists because the registry backing these is global — an
 *                  unscoped entry would speed up every machine that reads it, which
 *                  is rarely what a pack wants once there is more than one machine
 */
public record MachineUpgrade(Key itemId, UpgradeType type, double perItem, int maxCount,
        java.util.Set<String> machines) {

    public MachineUpgrade(Key itemId, UpgradeType type, double perItem, int maxCount) {
        this(itemId, type, perItem, maxCount, java.util.Set.of());
    }

    /** Whether this upgrade applies to the given machine id. */
    public boolean appliesTo(String machineId) {
        return machines.isEmpty() || (machineId != null && machines.contains(machineId));
    }

    public MachineUpgrade {
        if (itemId == null) throw new IllegalArgumentException("itemId must not be null");
        if (type == null) throw new IllegalArgumentException("type must not be null");
        if (maxCount < 0) throw new IllegalArgumentException("maxCount must be >= 0");
        machines = machines == null ? java.util.Set.of() : java.util.Set.copyOf(machines);
    }

    /** Effective contributing count given a raw count, honoring {@link #maxCount}. */
    public int effectiveCount(int rawCount) {
        if (rawCount <= 0) return 0;
        return maxCount == 0 ? rawCount : Math.min(rawCount, maxCount);
    }
}
