package dev.arubik.craftengine.loot;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Makes the "scrapped" machine upgrade ({@code cml:upgrade_scrapped}, identical to
 * {@code cml:upgrade_copper}) a LOOT-ONLY item: it has no crafting recipe, and is instead injected
 * into a handful of vanilla dungeon/structure chest loot tables with a low chance.
 *
 * <p>Implemented via Bukkit's {@link org.bukkit.event.world.LootGenerateEvent}, which fires whenever
 * a vanilla loot table is rolled (chests, fishing, etc.). We only add to the curated chest tables in
 * {@link #TARGET_TABLES}, so the drop feels like a rare salvage find without flooding loot.</p>
 */
public final class UpgradeLootListener implements Listener {

    /** Modest ~8% roll, one upgrade item, on these structure chest tables (loot-only item). */
    private static final double CHANCE = 0.08;

    private static final LootTables[] TARGET_TABLES = {
            LootTables.SIMPLE_DUNGEON,
            LootTables.ABANDONED_MINESHAFT,
            LootTables.STRONGHOLD_CORRIDOR,
            LootTables.STRONGHOLD_CROSSING,
            LootTables.BASTION_TREASURE,
            LootTables.NETHER_BRIDGE,
    };

    @EventHandler(ignoreCancelled = true)
    public void onLootGenerate(org.bukkit.event.world.LootGenerateEvent event) {
        if (event.getLootTable() == null)
            return;
        NamespacedKey rolled = event.getLootTable().getKey();
        boolean match = false;
        for (LootTables t : TARGET_TABLES) {
            if (t.getKey().equals(rolled)) {
                match = true;
                break;
            }
        }
        if (!match)
            return;
        if (ThreadLocalRandom.current().nextDouble() >= CHANCE)
            return;

        ItemStack upgrade = buildUpgrade();
        if (upgrade != null)
            event.getLoot().add(upgrade);
    }

    private static ItemStack buildUpgrade() {
        try {
            var def = CraftEngineItems.byId(Key.of("cml", "upgrade_scrapped"));
            if (def == null)
                return null;
            ItemStack stack = def.buildBukkitItem();
            if (stack != null)
                stack.setAmount(1);
            return stack;
        } catch (Throwable t) {
            return null;
        }
    }
}
