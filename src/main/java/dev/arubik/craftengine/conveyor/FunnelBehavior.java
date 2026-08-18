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
 *  net.momirealms.craftengine.core.block.property.Property
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
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.conveyor.FunnelBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import dev.arubik.craftengine.util.MNms;
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
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class FunnelBehavior
extends NmsBlockBehavior
implements EntityBlock {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:funnel");
    public static final Factory FACTORY = new Factory();
    private int controllerId;

    public FunnelBehavior(BlockDefinition block) {
        super(block);
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new FunnelBlockEntity(blockEntity);
    }

    private static FunnelBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            BlockEntityController blockEntityController;
            CEWorld world = CeWorlds.of((World)((ServerLevel)levelObj).getWorld()).storageWorld();
            if (world == null) {
                return null;
            }
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && (blockEntityController = be.controller) instanceof FunnelBlockEntity) {
                FunnelBlockEntity f = (FunnelBlockEntity)blockEntityController;
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
            BukkitServerPlayer p;
            net.momirealms.craftengine.core.entity.player.Player player;
            ServerLevel level = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
            Object posHandle = LocationUtils.toBlockPos((net.momirealms.craftengine.core.world.BlockPos)context.getClickedPos());
            if (!(posHandle instanceof BlockPos)) {
                return InteractionResult.PASS;
            }
            BlockPos bp = (BlockPos)posHandle;
            String cur = FunnelBehavior.enumName(state, "mode");
            String next = "in".equalsIgnoreCase(cur) ? "out" : "in";
            ImmutableBlockState ns = FunnelBehavior.withEnum(state, "mode", next);
            if (ns != null && ns != state) {
                Object nms = ns.customBlockState().minecraftState();
                MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            }
            if ((player = context.getPlayer()) instanceof BukkitServerPlayer) {
                p = (BukkitServerPlayer) player;
                Player bukkitPlayer = (Player) p.platformPlayer();
                if (bukkitPlayer != null) {
                    bukkitPlayer.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize("\u00a7eFunnel mode: \u00a7f" + next.toUpperCase()));
                }
            }
            return InteractionResult.SUCCESS_AND_CANCEL;
        }
        catch (Throwable throwable) {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos, BlockState oldState, Boolean movedByPiston) {
        FunnelBlockEntity f = FunnelBehavior.controllerAt(level, nmsPos);
        if (f != null) {
            f.dropTransit();
        }
    }

    private static String enumName(ImmutableBlockState state, String name) {
        Property p = state.getProperty(name);
        if (p == null) {
            return null;
        }
        Comparable v = state.get(p);
        if (v == null) {
            return null;
        }
        try {
            return Property.formatValue((Property)p, (Comparable)v);
        }
        catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null) {
            return state;
        }
        try {
            Comparable value = p.valueByName(valueName.toLowerCase());
            if (value == null) {
                return state;
            }
            return ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, value);
        }
        catch (Throwable t) {
            return state;
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new FunnelBehavior(block);
        }
    }
}

