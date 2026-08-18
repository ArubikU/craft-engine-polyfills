/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer
 *  net.momirealms.craftengine.bukkit.util.LocationUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.block.behavior.EntityBlock
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.entity.player.InteractionResult
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.context.UseOnContext
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.conveyor.FloorFunnelBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FloorFunnelBehavior
extends NmsBlockBehavior
implements EntityBlock {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:floor_funnel");
    public static final Factory FACTORY = new Factory();
    private int controllerId;

    public FloorFunnelBehavior(BlockDefinition block) {
        super(block);
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new FloorFunnelBlockEntity(blockEntity);
    }

    private static FloorFunnelBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            BlockEntityController blockEntityController;
            CEWorld world = CeWorlds.of((World)((ServerLevel)levelObj).getWorld()).storageWorld();
            if (world == null) {
                return null;
            }
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && (blockEntityController = be.controller) instanceof FloorFunnelBlockEntity) {
                FloorFunnelBlockEntity f = (FloorFunnelBlockEntity)blockEntityController;
                return f;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            boolean handEmpty;
            BukkitServerPlayer p;
            ServerLevel level = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
            Object posHandle = LocationUtils.toBlockPos((net.momirealms.craftengine.core.world.BlockPos)context.getClickedPos());
            if (!(posHandle instanceof BlockPos)) {
                return InteractionResult.PASS;
            }
            FloorFunnelBlockEntity f = FloorFunnelBehavior.controllerAt(level, posHandle);
            if (f == null) {
                return InteractionResult.PASS;
            }
            net.momirealms.craftengine.core.entity.player.Player player = context.getPlayer();
            if (!(player instanceof BukkitServerPlayer) || !((player = (p = (BukkitServerPlayer)player).platformPlayer()) instanceof Player)) {
                return InteractionResult.PASS;
            }
            net.momirealms.craftengine.core.entity.player.Player player2 = player;
            PlayerInventory inv = player2.getInventory();
            ItemStack hand = inv.getItemInMainHand();
            boolean bl = handEmpty = hand == null || hand.getType().isAir();
            if (!handEmpty) {
                return InteractionResult.PASS;
            }
            ItemStack out = f.takeHeld();
            if (out == null) {
                return InteractionResult.PASS;
            }
            for (ItemStack left : inv.addItem(new ItemStack[]{out}).values()) {
                player2.getWorld().dropItem(player2.getLocation(), left);
            }
            return InteractionResult.SUCCESS_AND_CANCEL;
        }
        catch (Throwable throwable) {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos, BlockState oldState, Boolean movedByPiston) {
        FloorFunnelBlockEntity f = FloorFunnelBehavior.controllerAt(level, nmsPos);
        if (f != null) {
            f.dropHeld();
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new FloorFunnelBehavior(block);
        }
    }
}

