package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;

import dev.arubik.craftengine.multiblock.HorizontalDoubleBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Immersive-Engineering-style "engineer's workbench" block. Now a REAL 2-wide
 * double block: it extends {@link HorizontalDoubleBlockBehavior}, so the placed
 * block is the LEFT (master) half and a second RIGHT half is auto-placed beside
 * it (one cell toward {@code facing.clockWise()}). Breaking either half removes
 * both. Right-clicking EITHER half forwards to the master, which opens the
 * {@link WorkbenchMenu} (3x2 inputs + 2 outputs + a tool slot), backed by the
 * {@link StationRecipeRegistry}.
 *
 * <p>Registration: expose {@link #FACTORY} under {@link #FACTORY_KEY} (unchanged
 * key {@code polyfills:workbench}); the main thread wires it in
 * {@code block/BlockBehaviors.java}. Sample recipes self-register via
 * {@link WorkbenchSamples} (triggered from this class's static initializer so a
 * placed workbench always has its recipes).
 *
 * <p>Required block-config properties: a {@code 4-direction} {@code facing} and a
 * string {@code half} with values {@code left}/{@code right}. If absent, the
 * behavior degrades to a single-block crafting station (still opens its menu).
 */
public class WorkbenchBehavior extends HorizontalDoubleBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:workbench");

    public static final Factory FACTORY = new Factory();

    static {
        // Mirror UpgradeableFurnace/CraftingSamples self-registration: ensure the
        // station recipes exist as soon as the behavior class is touched.
        WorkbenchSamples.registerDefaults();
    }

    private final String title;
    private final StationRecipeRegistry registry;

    public WorkbenchBehavior(BlockDefinition block, String title, StationRecipeRegistry registry,
            String facingProperty, String halfProperty) {
        super(block, facingProperty, halfProperty);
        this.title = title != null ? title : "Engineer's Workbench";
        this.registry = registry != null ? registry : StationRecipeRegistry.global();
    }

    protected AbstractCraftingMenu createMenu(Player player) {
        return new WorkbenchMenu(title, registry);
    }

    /**
     * Right-click on either half lands here (resolved to the master). Open the
     * workbench menu for the clicking player. The RIGHT half never opens its own
     * menu; it always forwards through {@link HorizontalDoubleBlockBehavior}.
     */
    @Override
    protected InteractionResult onMasterUse(UseOnContext context, ImmutableBlockState masterState, BlockPos masterPos) {
        try {
            if (context.getPlayer() instanceof BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof Player bukkit) {
                AbstractCraftingMenu menu = createMenu(bukkit);
                if (menu != null) {
                    menu.open(bukkit);
                    return InteractionResult.SUCCESS_AND_CANCEL;
                }
            }
        } catch (Throwable ignored) {
            // fall through to PASS
        }
        return InteractionResult.PASS;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String title = (String) arguments.getOrDefault("title", "Engineer's Workbench");
            String facingProp = (String) arguments.getOrDefault("facing_property", DEFAULT_FACING_PROPERTY);
            String halfProp = (String) arguments.getOrDefault("half_property", DEFAULT_HALF_PROPERTY);
            return new WorkbenchBehavior(block, title, StationRecipeRegistry.global(), facingProp, halfProp);
        }
    }
}
