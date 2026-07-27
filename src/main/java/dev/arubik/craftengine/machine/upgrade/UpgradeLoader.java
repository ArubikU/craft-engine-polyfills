package dev.arubik.craftengine.machine.upgrade;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code upgrades/*.json} into {@link UpgradeRegistry#global()}.
 *
 * <p>
 * {@link UpgradeRegistry} was already an open runtime map — it just had nothing
 * that filled it from data. The only entries came from three hardcoded
 * {@code register(...)} calls in {@code UpgradeableFurnaceBlockEntity}, fired
 * lazily from a block-entity constructor behind a {@code static boolean} guard,
 * which meant the upgrade set depended on whether anyone had placed a furnace
 * yet and never reset on reload. Now it is filled on every load pass like
 * everything else.
 *
 * <pre>{@code
 * {
 *   "item": "minecraft:sugar",   // id of the item placed in an upgrade slot
 *   "type": "speed",             // speed | efficiency | yield
 *   "per_item": 1.0,             // magnitude one item contributes
 *   "max_count": 3               // 0 = unlimited
 * }
 * }</pre>
 */
public final class UpgradeLoader {

    private UpgradeLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("upgrades", Registries.PHASE_DEFINITIONS, UpgradeLoader::load);
    }

    public static void load() {
        // Not a data.Registry, so the central reload does not clear it for us.
        UpgradeRegistry.global().clear();
        int count = DataFiles.loadDirectory("upgrades", UpgradeLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger().info("Loaded " + count + " machine upgrades.");
    }

    private static void apply(JsonView view, String fileName) {
        Key item = view.key("item", "minecraft");
        UpgradeType type = view.enumValue("type", UpgradeType.class);
        double perItem = view.rangedDouble("per_item", 1.0, -1000.0, 1000.0);
        int maxCount = view.rangedInt("max_count", 0, 0, 4096);
        UpgradeRegistry.global().register(new MachineUpgrade(item, type, perItem, maxCount));
    }
}
