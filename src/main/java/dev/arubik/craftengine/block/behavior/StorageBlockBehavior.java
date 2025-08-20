package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.util.BlockEntityBehaviorController;
import dev.arubik.craftengine.util.SyncedGuiHolder;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.item.context.UseOnContext;

import java.util.Map;

import org.bukkit.entity.Player;

/**
 * Simple storage block behavior backed by SyncedGuiHolder. Size and title are configurable.
 */
public class StorageBlockBehavior extends NmsBlockBehavior {
    public static final Factory FACTORY = new Factory();

    private final int size;
    private final String title;

    public StorageBlockBehavior(CustomBlock customBlock, int size, String title) {
        super(customBlock);
        this.size = Math.max(9, Math.min(size, 54));
        this.title = title != null ? title : "Storage";
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            int size = 27;
            String title = "Storage";
            Object s = arguments.get("size");
            if (s instanceof Number n) size = n.intValue();
            Object t = arguments.get("title");
            if (t != null) title = String.valueOf(t);
            return new StorageBlockBehavior(block, size, title);
        }
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            
            Object levelHandle =((BukkitWorld) context.getLevel()).serverWorld();
            Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());

            if (levelHandle instanceof Level level && posHandle instanceof BlockPos pos) {
                SyncedGuiHolder holder = SyncedGuiHolder.getOrCreate(level, pos, size, title);
                BukkitServerPlayer player = (BukkitServerPlayer) context.getPlayer();
                Player bukkit = player.platformPlayer();
                if (bukkit instanceof org.bukkit.entity.Player p) {
                    holder.open(p);
                }
                if (level instanceof ServerLevel server) {
                    BlockEntityBehaviorController.register(server, pos);
                }
                return InteractionResult.SUCCESS;
            }
        } catch (Throwable ignored) {}
        return InteractionResult.PASS;
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos pos, BlockState state, java.util.concurrent.Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        SyncedGuiHolder.closeAll(level, pos);
        SyncedGuiHolder.get(level, pos).ifPresent(holder -> {
            holder.closeViewers();
            holder.dropInventoryContents(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        });
        BlockEntityBehaviorController.unregister(level, pos);
    }

    @Override
    public void onPlace(Object thisBlock, Level level, BlockPos pos, BlockState state, java.util.concurrent.Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        BlockEntityBehaviorController.register(level, pos);
    }

    @Override
    public void tick(Object thisBlock, Level level, BlockPos pos, BlockState state, java.util.concurrent.Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        // Process queued moves and keep state alive
        BlockEntityBehaviorController.tick(level, pos);
    }

    // 1.20.2+ LevelAccessor level, BlockPos pos, BlockState state
    @Override
    public Object pickupBlock(Object thisObj, LevelAccessor level, BlockPos pos, BlockState state, java.util.concurrent.Callable<Object> superMethod) {
        try {
            return superMethod.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock
    @Override
    public void onLand(Object thisBlock, Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        // no-op for storage block
    }

}
