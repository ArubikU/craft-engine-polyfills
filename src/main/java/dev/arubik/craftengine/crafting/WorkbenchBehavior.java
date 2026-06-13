package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;

import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * Immersive-Engineering-style "engineer's workbench" block: right-clicking opens
 * a {@link WorkbenchMenu} (3x2 inputs + 2 outputs + a tool slot), backed by the
 * {@link StationRecipeRegistry}. All matching/UI/take logic is inherited from
 * {@link AbstractCraftingBehavior} / {@link AbstractCraftingMenu}.
 *
 * <p>Registration: expose {@link #FACTORY} under {@link #FACTORY_KEY}; the main
 * thread wires it in {@code block/BlockBehaviors.java}, exactly like
 * {@link CraftingTableBehavior}. Sample recipes self-register via
 * {@link WorkbenchSamples} (triggered from this class's static initializer so a
 * placed workbench always has its recipes).
 */
public class WorkbenchBehavior extends AbstractCraftingBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:workbench");

    public static final Factory FACTORY = new Factory();

    static {
        // Mirror UpgradeableFurnace/CraftingSamples self-registration: ensure the
        // station recipes exist as soon as the behavior class is touched.
        WorkbenchSamples.registerDefaults();
    }

    private final String title;
    private final StationRecipeRegistry registry;

    public WorkbenchBehavior(BlockDefinition block, String title, StationRecipeRegistry registry) {
        super(block);
        this.title = title != null ? title : "Engineer's Workbench";
        this.registry = registry != null ? registry : StationRecipeRegistry.global();
    }

    @Override
    protected AbstractCraftingMenu createMenu(Player player) {
        return new WorkbenchMenu(title, registry);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String title = (String) arguments.getOrDefault("title", "Engineer's Workbench");
            return new WorkbenchBehavior(block, title, StationRecipeRegistry.global());
        }
    }
}
