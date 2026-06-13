package dev.arubik.craftengine.machine.upgrade;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.momirealms.craftengine.core.util.Key;

/**
 * Registry mapping item ids to {@link MachineUpgrade} definitions.
 *
 * <p>A machine resolves the items in its upgrade slots against a registry to
 * obtain {@link UpgradeModifiers}. A process-wide {@link #global()} instance is
 * provided for convenience, but machines may use a private registry to expose a
 * curated upgrade set.</p>
 */
public final class UpgradeRegistry {

    private static final UpgradeRegistry GLOBAL = new UpgradeRegistry();

    private final Map<Key, MachineUpgrade> upgrades = new ConcurrentHashMap<>();

    public static UpgradeRegistry global() {
        return GLOBAL;
    }

    /** Registers (or replaces) the upgrade for its item id. Returns {@code this} for chaining. */
    public UpgradeRegistry register(MachineUpgrade upgrade) {
        upgrades.put(upgrade.itemId(), upgrade);
        return this;
    }

    /** Convenience overload building the {@link MachineUpgrade} in place. */
    public UpgradeRegistry register(Key itemId, UpgradeType type, double perItem, int maxCount) {
        return register(new MachineUpgrade(itemId, type, perItem, maxCount));
    }

    public Optional<MachineUpgrade> get(Key itemId) {
        return Optional.ofNullable(upgrades.get(itemId));
    }

    public boolean isUpgrade(Key itemId) {
        return itemId != null && upgrades.containsKey(itemId);
    }

    public void clear() {
        upgrades.clear();
    }
}
