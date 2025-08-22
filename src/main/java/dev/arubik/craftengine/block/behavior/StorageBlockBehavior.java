package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.util.BlockEntityBehaviorController;
import dev.arubik.craftengine.util.SyncedGuiHolder;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.plugin.CraftEngine;

import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.entity.Player;

/**
 * Simple storage block behavior backed by SyncedGuiHolder. Size and title are configurable.
 */
public class StorageBlockBehavior extends BukkitBlockBehavior {
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
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {}
        return InteractionResult.PASS;
    }

    public WorldlyContainer getContainer(Level level, BlockPos pos) {
        return SyncedGuiHolder.getOrCreate(level, pos, size, title).getContainer();
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        boolean movedByPiston = (Boolean) args[3];

        CraftEngine.instance().logger().info("Removing storage block at " + pos + " in " + level.dimension().location());
        if(!movedByPiston){
        SyncedGuiHolder.get(level, pos).ifPresent(holder -> {
            holder.closeViewers();
            holder.dropInventoryContents(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            CraftEngine.instance().logger().info("Dropped inventory contents for storage block at " + pos + " in " + level.dimension().location());
        });
        BlockEntityBehaviorController.unregister(level, pos);
        CraftEngine.instance().logger().info("Unregistered storage block at " + pos + " in " + level.dimension().location());
        }superMethod.call();
    }

    @Override
    public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockState newState = (BlockState) args[3];
        CraftEngine.instance().logger().info("Removing storage block at " + pos + " in " + level.dimension().location());
        
        SyncedGuiHolder.get(level, pos).ifPresent(holder -> {
            holder.closeViewers();
            holder.dropInventoryContents(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            CraftEngine.instance().logger().info("Dropped inventory contents for storage block at " + pos + " in " + level.dimension().location());
        });
        BlockEntityBehaviorController.unregister(level, pos);
        CraftEngine.instance().logger().info("Unregistered storage block at " + pos + " in " + level.dimension().location());
        superMethod.call();
    }


    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        try { superMethod.call(); } catch (Exception ignored) {}
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockEntityBehaviorController.register(level, pos);
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        try { superMethod.call(); } catch (Exception ignored) {}
        // Process queued moves and keep state alive
        BlockEntityBehaviorController.tick(level, pos);
    }

}
