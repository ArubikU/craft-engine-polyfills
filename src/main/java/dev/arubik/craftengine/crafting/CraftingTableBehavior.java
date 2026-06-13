package dev.arubik.craftengine.crafting;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * A reusable JIT crafting-table block: right-clicking opens a
 * {@link CraftingTableMenu} sized to a configurable W x H grid, backed by a
 * {@link CraftingRecipeRegistry}. The matched output updates live and taking it
 * consumes one of every input (vanilla-style, instant output).
 *
 * <p>Implements {@link EntityBlock} per the framework contract; the controller
 * is a no-op marker since the crafting grid is a transient per-open session
 * (held in the open inventory) and nothing is persisted to the block.
 *
 * <p>Register the factory under {@link #FACTORY_KEY} and expose this behavior on
 * a custom block in config. Recipes are defined against
 * {@link CraftingRecipeRegistry} (see {@link CraftingSamples}).
 */
public class CraftingTableBehavior extends BukkitBlockBehavior implements EntityBlock {

    public static final Key FACTORY_KEY = Key.of("polyfills:crafting_table");

    public static final Factory FACTORY = new Factory();

    private final int gridWidth;
    private final int gridHeight;
    private final String title;
    private final CraftingRecipeRegistry registry;

    private int controllerId;

    public CraftingTableBehavior(BlockDefinition block, int gridWidth, int gridHeight, String title,
            CraftingRecipeRegistry registry) {
        super(block);
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.title = title != null ? title : "Crafting";
        this.registry = registry != null ? registry : CraftingRecipeRegistry.global();
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            if (context.getPlayer() instanceof BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof Player bukkit) {
                CraftingTableMenu menu = new CraftingTableMenu(gridWidth, gridHeight, title, registry);
                menu.open(bukkit);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
            // fall through to PASS
        }
        return InteractionResult.PASS;
    }

    // --- EntityBlock contract (no-op controller; grid is transient) ---

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new CraftingTableController(blockEntity);
    }

    /** Trivial controller; this block stores no per-block crafting state. */
    public static final class CraftingTableController extends BlockEntityController {
        public CraftingTableController(BlockEntity blockEntity) {
            super(blockEntity);
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            int width = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("grid-width", 3), "grid-width");
            int height = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("grid-height", 3), "grid-height");
            String title = (String) arguments.getOrDefault("title", "Crafting");
            // All recipes live in the global registry by default; the dimensions
            // bucket keeps lookup scoped to this table's size.
            return new CraftingTableBehavior(block, width, height, title, CraftingRecipeRegistry.global());
        }
    }
}
