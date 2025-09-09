package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public abstract class WireBlockBehavior extends BukkitBlockBehavior {

    public final EnumProperty<RedstoneSide> NORTH;
    public final EnumProperty<RedstoneSide> EAST;
    public final EnumProperty<RedstoneSide> SOUTH;
    public final EnumProperty<RedstoneSide> WEST;
    public final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;

    @SuppressWarnings("unchecked")
    public WireBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
        this.NORTH = (EnumProperty<RedstoneSide>) customBlock.getProperty("north");
        this.EAST = (EnumProperty<RedstoneSide>) customBlock.getProperty("east");
        this.SOUTH = (EnumProperty<RedstoneSide>) customBlock.getProperty("south");
        this.WEST = (EnumProperty<RedstoneSide>) customBlock.getProperty("west");

        this.PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
                Direction.NORTH, NORTH,
                Direction.EAST, EAST,
                Direction.SOUTH, SOUTH,
                Direction.WEST, WEST));
    }

    protected abstract boolean canConnectTo(BlockState state, BlockGetter world, BlockPos pos, Direction dir);

    protected abstract boolean canClimbTo(BlockState state, BlockGetter world, BlockPos pos);

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

            ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).get();
            ImmutableBlockState newState = getConnectionState(level, customState, pos);
            if (!newState.equals(customState)) {
                FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(), 3);
            }
        
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (!level.isClientSide) {
            ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).get();
            if (customState != null) {
                ImmutableBlockState newState = updateShape(customState, Direction.DOWN, level.getBlockState(pos.below()), level, pos, pos.below());
                if (newState == null) {
                    FastNMS.INSTANCE.method$Level$destroyBlock(level, pos, true);
                    return;
                }
                if (!newState.equals(customState)) {
                    FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(), 3);
                }
            }
        }
    }

    public ImmutableBlockState updateShape(ImmutableBlockState state, Direction direction, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        if (direction == Direction.DOWN) {
            return this.canSurvive((BlockState) state.customBlockState().literalObject(), world, pos) ? state : null;
        } else if (direction == Direction.UP) {
            return this.getConnectionState(world, state, pos);
        } else {
            RedstoneSide redstoneside = this.getConnectingSide(world, pos, direction);
            if (redstoneside.isConnected() == state.get(PROPERTY_BY_DIRECTION.get(direction)).isConnected() && !isCross(state)) {
                return state.with(PROPERTY_BY_DIRECTION.get(direction), redstoneside);
            } else {
                return this.getConnectionState(world, getCrossState(state).with(PROPERTY_BY_DIRECTION.get(direction), redstoneside), pos);
            }
        }
    }

    protected ImmutableBlockState getCrossState(ImmutableBlockState currentState) {
        ImmutableBlockState builder = currentState;
        return builder
                .with(NORTH, RedstoneSide.SIDE)
                .with(EAST, RedstoneSide.SIDE)
                .with(SOUTH, RedstoneSide.SIDE)
                .with(WEST, RedstoneSide.SIDE);
    }

    private ImmutableBlockState getConnectionState(BlockGetter world, ImmutableBlockState state, BlockPos pos) {
        boolean wasDot = isDot(state);
        state = this.getMissingConnections(world, state, pos);
        if (wasDot && isDot(state)) {
            return state;
        }
        boolean north = state.get(NORTH).isConnected();
        boolean south = state.get(SOUTH).isConnected();
        boolean east = state.get(EAST).isConnected();
        boolean west = state.get(WEST).isConnected();
        boolean noX = !north && !south;
        boolean noZ = !east && !west;

        if (!north && noZ) state = state.with(NORTH, RedstoneSide.SIDE);
        if (!south && noZ) state = state.with(SOUTH, RedstoneSide.SIDE);
        if (!east && noX) state = state.with(EAST, RedstoneSide.SIDE);
        if (!west && noX) state = state.with(WEST, RedstoneSide.SIDE);

        return state;
    }

    private ImmutableBlockState getMissingConnections(BlockGetter world, ImmutableBlockState state, BlockPos pos) {
        boolean canClimbUp = !world.getBlockState(pos.above()).isRedstoneConductor(world, pos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            //if (!state.get(PROPERTY_BY_DIRECTION.get(direction)).isConnected()) {
                RedstoneSide side = this.getConnectingSide(world, pos, direction, canClimbUp);
                state = state.with(PROPERTY_BY_DIRECTION.get(direction), side);
            //}
        }
        return state;
    }

    private RedstoneSide getConnectingSide(BlockGetter world, BlockPos pos, Direction dir) {
        return this.getConnectingSide(world, pos, dir, !world.getBlockState(pos.above()).isRedstoneConductor(world, pos));
    }

    private RedstoneSide getConnectingSide(BlockGetter world, BlockPos pos, Direction dir, boolean canClimbUp) {
        BlockPos facingPos = pos.relative(dir);
        BlockState facingState = world.getBlockState(facingPos);
        if (canClimbUp) {
            if (this.canSurviveOn(world, facingPos, facingState) && canClimbTo(world.getBlockState(facingPos.above()), world, facingPos.above())) {
                if (facingState.isFaceSturdy(world, facingPos, dir.getOpposite())) {
                    Bukkit.getConsoleSender().sendMessage("WireBlockBehavior: getConnectingSide at " + pos + " towards " + dir + " = UP");
                    return RedstoneSide.UP;
                }
            }
        }
        RedstoneSide side = (!canConnectTo(facingState, world, facingPos, dir) && (facingState.isRedstoneConductor(world, facingPos) || !canConnectTo(world.getBlockState(facingPos.below()), world, facingPos.below(), null))
                ? RedstoneSide.NONE : RedstoneSide.SIDE);
        Bukkit.getConsoleSender().sendMessage("WireBlockBehavior: getConnectingSide at " + pos + " towards " + dir + " = " + side);
        return side;
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = world.getBlockState(blockpos);
        return this.canSurviveOn(world, blockpos, blockstate);
    }

    private boolean canSurviveOn(BlockGetter world, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(world, pos, Direction.UP) || state.is(Blocks.HOPPER);
    }

    protected boolean isDot(ImmutableBlockState state) {
        return !state.get(NORTH).isConnected() && !state.get(SOUTH).isConnected() &&
                !state.get(EAST).isConnected() && !state.get(WEST).isConnected();
    }

    protected boolean isCross(ImmutableBlockState state) {
        return state.get(NORTH).isConnected() && state.get(SOUTH).isConnected() &&
                state.get(EAST).isConnected() && state.get(WEST).isConnected();
    }
}
