package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;

import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * EXTENSIBILITY SAMPLE block: opens a {@link ForgeMenu} (2x2 grid + fuel slot).
 * Demonstrates that a custom crafting block needs only a tiny behavior plus a
 * menu subclass; all matching/UI/take logic is inherited from
 * {@link AbstractCraftingBehavior} / {@link AbstractCraftingMenu}.
 *
 * <p>If registered, expose the factory under {@link #FACTORY_KEY} the same way
 * {@link CraftingTableBehavior} is wired.
 */
public class ForgeBehavior extends AbstractCraftingBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:forge");

    public static final Factory FACTORY = new Factory();

    private final String title;
    private final CraftingRecipeRegistry registry;

    public ForgeBehavior(BlockDefinition block, String title, CraftingRecipeRegistry registry) {
        super(block);
        this.title = title != null ? title : "Forge";
        this.registry = registry != null ? registry : CraftingRecipeRegistry.global();
    }

    @Override
    protected AbstractCraftingMenu createMenu(Player player) {
        return new ForgeMenu(title, registry);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String title = (String) arguments.getOrDefault("title", "Forge");
            return new ForgeBehavior(block, title, CraftingRecipeRegistry.global());
        }
    }
}
