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
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Behavior backing a {@link FunnelBlockEntity} ({@code polyfills:funnel}). Right-click
 * toggles the {@code mode} property (in ↔ out). Breaking drops any in-transit item.
 */
public class FunnelBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior implements EntityBlock {

    public static final Key FACTORY_KEY = Key.of("polyfills:funnel");
    public static final Factory FACTORY = new Factory();

    private int controllerId;

    public FunnelBehavior(BlockDefinition block) {
        super(block);
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new FunnelBlockEntity(blockEntity);
    }

    private static FunnelBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            CEWorld world = new BukkitWorld(((ServerLevel) levelObj).getWorld()).storageWorld();
            if (world == null)
                return null;
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && be.controller instanceof FunnelBlockEntity f)
                return f;
        } catch (Throwable ignored) {
        }
        return null;
    }

    // Right-click: toggle in/out mode (in place; keeps the block entity).
    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = ((CraftWorld) ((BukkitWorld) context.getLevel()).platformWorld()).getHandle();
            Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());
            if (!(posHandle instanceof BlockPos bp))
                return InteractionResult.PASS;
            String cur = enumName(state, FunnelBlockEntity.PROP_MODE);
            String next = "in".equalsIgnoreCase(cur) ? "out" : "in";
            ImmutableBlockState ns = withEnum(state, FunnelBlockEntity.PROP_MODE, next);
            if (ns != null && ns != state) {
                Object nms = ns.customBlockState().minecraftState();
                dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, bp, nms, 2);
            }
            if (context.getPlayer() instanceof BukkitServerPlayer p
                    && p.platformPlayer() instanceof Player player) {
                player.sendMessage("§eFunnel mode: §f" + next.toUpperCase());
            }
            return InteractionResult.SUCCESS_AND_CANCEL;
        } catch (Throwable ignored) {
        }
        return InteractionResult.PASS;
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos,
            net.minecraft.world.level.block.state.BlockState oldState, Boolean movedByPiston) {
        FunnelBlockEntity f = controllerAt(level, nmsPos);
        if (f != null)
            f.dropTransit();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String enumName(ImmutableBlockState state, String name) {
        Property p = state.getProperty(name);
        if (p == null)
            return null;
        Object v = state.get(p);
        if (v == null)
            return null;
        try {
            return Property.formatValue(p, (Comparable<?>) v);
        } catch (Throwable t) {
            return String.valueOf(v);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null)
            return state;
        try {
            Object value = p.valueByName(valueName.toLowerCase());
            if (value == null)
                return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new FunnelBehavior(block);
        }
    }
}
