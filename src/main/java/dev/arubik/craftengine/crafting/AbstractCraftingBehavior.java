package dev.arubik.craftengine.crafting;

import org.bukkit.entity.Player;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Reusable base for a crafting-UI block. Right-clicking opens the
 * {@link AbstractCraftingMenu} produced by {@link #createMenu(Player)}; the menu
 * owns all the recipe/UI plumbing. Implements {@link EntityBlock} with a no-op
 * controller because the crafting grid is a transient per-open session held in
 * the open inventory (vanilla-style) and nothing is persisted to the block.
 *
 * <p>To make a custom crafting block, extend this and implement
 * {@link #createMenu}, returning your own {@link AbstractCraftingMenu} subclass.
 */
public abstract class AbstractCraftingBehavior extends BukkitBlockBehavior implements EntityBlock {

    protected AbstractCraftingBehavior(BlockDefinition block) {
        super(block);
    }

    /** Build a fresh menu for this open. Called once per right-click. */
    protected abstract AbstractCraftingMenu createMenu(Player player);

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
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

    // --- EntityBlock contract (no-op controller; grid is transient) ---

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public final int controllerId() {
        return controllerId;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new TransientController(blockEntity);
    }

    /** Trivial controller; crafting blocks store no per-block crafting state. */
    public static final class TransientController extends BlockEntityController {
        public TransientController(BlockEntity blockEntity) {
            super(blockEntity);
        }
    }
}
