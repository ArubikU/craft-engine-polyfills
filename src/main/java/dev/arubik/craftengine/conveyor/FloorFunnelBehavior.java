package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
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
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Behavior backing a {@link FloorFunnelBlockEntity} ({@code polyfills:floor_funnel}).
 *
 * <p>An instant, receive-only hopper: it accepts a WHOLE stack at once (from a belt, a
 * dropped item on top, or a hopper/dropper above) and deposits it straight DOWN into a
 * worldly container — semi-instantly (partial fills are kept until the container frees).
 * With no container below, it drops the stack into the world. Right-click with an empty
 * hand extracts the held stack; breaking drops it.</p>
 */
public class FloorFunnelBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior implements EntityBlock {

    public static final Key FACTORY_KEY = Key.of("polyfills:floor_funnel");
    public static final Factory FACTORY = new Factory();

    private int controllerId;

    public FloorFunnelBehavior(BlockDefinition block) {
        super(block);
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new FloorFunnelBlockEntity(blockEntity);
    }

    private static FloorFunnelBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            CEWorld world = new BukkitWorld(((ServerLevel) levelObj).getWorld()).storageWorld();
            if (world == null)
                return null;
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && be.controller instanceof FloorFunnelBlockEntity f)
                return f;
        } catch (Throwable ignored) {
        }
        return null;
    }

    // Right-click with an EMPTY hand: extract the held stack to the player.
    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = ((CraftWorld) ((BukkitWorld) context.getLevel()).platformWorld()).getHandle();
            Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());
            if (!(posHandle instanceof BlockPos))
                return InteractionResult.PASS;
            FloorFunnelBlockEntity f = controllerAt(level, posHandle);
            if (f == null)
                return InteractionResult.PASS;
            if (!(context.getPlayer() instanceof BukkitServerPlayer p)
                    || !(p.platformPlayer() instanceof Player player))
                return InteractionResult.PASS;

            org.bukkit.inventory.PlayerInventory inv = player.getInventory();
            org.bukkit.inventory.ItemStack hand = inv.getItemInMainHand();
            boolean handEmpty = hand == null || hand.getType().isAir();
            if (!handEmpty)
                return InteractionResult.PASS; // only an empty hand extracts

            org.bukkit.inventory.ItemStack out = f.takeHeld();
            if (out == null)
                return InteractionResult.PASS;
            for (org.bukkit.inventory.ItemStack left : inv.addItem(out).values())
                player.getWorld().dropItem(player.getLocation(), left);
            return InteractionResult.SUCCESS_AND_CANCEL;
        } catch (Throwable ignored) {
        }
        return InteractionResult.PASS;
    }

    // Break: drop the held stack.
    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos,
            net.minecraft.world.level.block.state.BlockState oldState, Boolean movedByPiston) {
        FloorFunnelBlockEntity f = controllerAt(level, nmsPos);
        if (f != null)
            f.dropHeld();
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new FloorFunnelBehavior(block);
        }
    }
}
