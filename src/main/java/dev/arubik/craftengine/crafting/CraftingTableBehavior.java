package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;

import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * The default JIT crafting-table block, now sitting on top of
 * {@link AbstractCraftingBehavior}: right-clicking opens a
 * {@link CraftingTableMenu} sized to a configurable W x H grid, backed by a
 * {@link CraftingRecipeRegistry}. The matched output updates live and taking it
 * consumes one of every input (vanilla-style, instant output).
 *
 * <p>Register the factory under {@link #FACTORY_KEY}; recipes are defined
 * against {@link CraftingRecipeRegistry} (see {@link CraftingSamples}).
 */
public class CraftingTableBehavior extends AbstractCraftingBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:crafting_table");

    public static final Factory FACTORY = new Factory();

    private final int gridWidth;
    private final int gridHeight;
    private final String title;
    private final CraftingRecipeRegistry registry;

    public CraftingTableBehavior(BlockDefinition block, int gridWidth, int gridHeight, String title,
            CraftingRecipeRegistry registry) {
        super(block);
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.title = title != null ? title : "Crafting";
        this.registry = registry != null ? registry : CraftingRecipeRegistry.global();
    }

    @Override
    protected AbstractCraftingMenu createMenu(Player player) {
        return new CraftingTableMenu(gridWidth, gridHeight, title, registry);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            int width = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("grid-width", 3), "grid-width");
            int height = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("grid-height", 3), "grid-height");
            String title = (String) arguments.getOrDefault("title", "Crafting");
            return new CraftingTableBehavior(block, width, height, title, CraftingRecipeRegistry.global());
        }
    }
}
