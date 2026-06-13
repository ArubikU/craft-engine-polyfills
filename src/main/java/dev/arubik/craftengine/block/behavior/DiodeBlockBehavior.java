package dev.arubik.craftengine.block.behavior;

import net.momirealms.craftengine.core.block.UpdateFlags;
import java.util.Optional;

import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.momirealms.craftengine.bukkit.block.behavior.AbstractCanSurviveBlockBehavior;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.world.context.BlockPlaceContext;
import net.momirealms.craftengine.core.world.context.UseOnContext;

public class DiodeBlockBehavior extends AbstractCanSurviveBlockBehavior {
    protected final Property<Boolean> POWERED;
    protected final Property<net.momirealms.craftengine.core.util.Direction> FACING;

    public DiodeBlockBehavior(BlockDefinition arg0, int delay, Property<Boolean> powered,
            Property<net.momirealms.craftengine.core.util.Direction> facing) {
        super(arg0, delay);
        this.POWERED = powered;
        this.FACING = facing;
    }

    protected int getDelay(BlockState state) {
        return 1;
    }

    protected boolean sideInputDiodesOnly() {
        return false;
    }

    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isDiode(BlockState state) {
        ImmutableBlockState optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        if (optionalCustomState.behavior() == null)
            return false;
        if (optionalCustomState.behavior() instanceof DiodeBlockBehavior)
            return true;
        if (optionalCustomState.behavior() instanceof CompositeBlockBehavior composite) {
            return composite.getFirst(DiodeBlockBehavior.class) != null;
        }
        return false;
    }

    @Override
    protected boolean canSurvive(Object thisBlock, Object state, Object world, Object blockPos) {
        try {
            Optional<ImmutableBlockState> optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (optionalCustomState.isEmpty())
                return false;
            int x = dev.arubik.craftengine.util.MNms.INSTANCE.field$Vec3i$x(blockPos);
            int y = dev.arubik.craftengine.util.MNms.INSTANCE.field$Vec3i$y(blockPos) - 1;
            int z = dev.arubik.craftengine.util.MNms.INSTANCE.field$Vec3i$z(blockPos);
            Object belowPos = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$BlockPos(x, y, z);
            Object belowState = dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockGetter$getBlockState(world, belowPos);
            return dev.arubik.craftengine.util.MNms.INSTANCE.method$BlockStateBase$isFaceSturdy(
                    belowState, world, belowPos, Direction.UP,
                    net.minecraft.world.level.block.SupportType.FULL);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void tick(Object thisBlock, Object[] args) {

        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (!this.isLocked(level, pos, state)) {
            ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
            boolean poweredValue = customState.get(POWERED);
            boolean shouldTurnOn = this.shouldTurnOn(level, pos, state);
            if (poweredValue && !shouldTurnOn) {
                // CraftBukkit start
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(level, pos, 15, 0)
                        .getNewCurrent() != 0) {
                    return;
                }
                // CraftBukkit end
                customState = customState.with(POWERED, false);
                dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, pos, customState.customBlockState().minecraftState(),
                        UpdateFlags.UPDATE_CLIENTS);
            } else if (!poweredValue) {
                // CraftBukkit start
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(level, pos, 0, 15)
                        .getNewCurrent() != 15) {
                    return;
                }
                // CraftBukkit end
                customState = customState.with(POWERED, true);
                dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$setBlock(level, pos, customState.customBlockState().minecraftState(),
                        UpdateFlags.UPDATE_CLIENTS);
                if (!shouldTurnOn) {

                    level.scheduleTick(pos, state.getBlock(), this.getDelay(state), TickPriority.VERY_HIGH);
                }
            }
        }
        super.tick(thisBlock, args);
    }

    protected boolean isLocked(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return this.getInputSignal(level, pos, state) > 0;
    }

    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        net.momirealms.craftengine.core.util.Direction direction = customState.get(FACING);
        BlockPos blockPos = pos.relative(Utils.fromDirection(direction));
        int signal = level.getSignal(blockPos, Utils.fromDirection(direction));
        if (signal >= 15) {
            return signal;
        } else {
            BlockState blockState = level.getBlockState(blockPos);
            return Math.max(signal,
                    blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0);
        }
    }

    protected int getAlternateSignal(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        net.momirealms.craftengine.core.util.Direction direction = customState.get(FACING);
        Direction minecraftDirection = Utils.fromDirection(direction);
        Direction clockWise = minecraftDirection.getClockWise();
        Direction counterClockWise = minecraftDirection.getCounterClockWise();
        boolean flag = this.sideInputDiodesOnly();

        // Get alternate signal from sides (for comparators and similar blocks)
        int clockWiseSignal = this.getControlInputSignal(level, pos.relative(clockWise), clockWise, flag);
        int counterClockWiseSignal = this.getControlInputSignal(level, pos.relative(counterClockWise), counterClockWise,
                flag);

        return Math.max(clockWiseSignal, counterClockWiseSignal);
    }

    protected int getControlInputSignal(Level level, BlockPos pos, Direction direction, boolean flag) {
        // For diode blocks, this is similar to getting signal but with specific logic
        // for control inputs
        int signal = level.getSignal(pos, direction);
        if (signal >= 15) {
            return signal;
        } else {
            BlockState blockState = level.getBlockState(pos);
            return Math.max(signal,
                    blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0);
        }
    }

    @Override
    public int getDirectSignal(Object thisBlock, Object[] args) {
        return getSignal(thisBlock, args);
    }

    @Override
    public int getSignal(Object thisBlock, Object[] args) {
        BlockState blockState = (BlockState) args[0];
        BlockGetter blockAccess = (BlockGetter) args[1];
        BlockPos pos = (BlockPos) args[2];
        Direction side = (Direction) args[3];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElseThrow();
        if (!customState.get(POWERED)) {
            return 0;
        } else {
            return Utils.fromDirection(customState.get(FACING)) == side
                    ? this.getOutputSignal(blockAccess, pos, blockState)
                    : 0;
        }
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args) {
        return true;
    }

    public boolean shouldPrioritize(BlockGetter level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        Direction opposite = Utils.fromDirection(customState.get(FACING)).getOpposite();
        BlockState blockState = level.getBlockState(pos.relative(opposite));
        ImmutableBlockState customBlockState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        return isDiode(blockState) && Utils.fromDirection(customBlockState.get(FACING)) != opposite;
    }

    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        Block block = state.getBlock();
        if (!this.isLocked(level, pos, state)) {
            boolean poweredValue = customState.get(POWERED);
            boolean shouldTurnOn = this.shouldTurnOn(level, pos, state);
            if (poweredValue != shouldTurnOn && !level.getBlockTicks().willTickThisTick(pos, block)) {
                TickPriority tickPriority = TickPriority.HIGH;
                if (this.shouldPrioritize(level, pos, state)) {
                    tickPriority = TickPriority.EXTREMELY_HIGH;
                } else if (poweredValue) {
                    tickPriority = TickPriority.VERY_HIGH;
                }

                level.scheduleTick(pos, state.getBlock(), this.getDelay(state), tickPriority);
            }
        }
    }

    @Override
    public ImmutableBlockState updateStateForPlacement(BlockPlaceContext context, ImmutableBlockState state) {
        // Set the facing direction based on the player's facing direction (opposite)
        net.momirealms.craftengine.core.util.Direction blockFacing = context.getHorizontalDirection().opposite();
        return state.with(FACING, blockFacing);
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args) {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockState state = (BlockState) args[0];

        this.updateNeighborsInFront(level, pos, state);
        try {
            super.onPlace(thisBlock, args);
        } catch (Exception e) {
        }
    }

    protected void updateNeighborsInFront(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        net.momirealms.craftengine.core.util.Direction facing = customState.get(FACING);
        Direction direction = Utils.fromDirection(facing);
        BlockPos frontPos = pos.relative(direction.getOpposite());

        // Update neighbors using FastNMS methods
        level.updateNeighborsAt(frontPos, state.getBlock());
        for (Direction neighborDirection : Direction.values()) {
            if (neighborDirection != direction) {
                level.updateNeighborsAt(frontPos.relative(neighborDirection), state.getBlock());
            }
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        // args[0] = BlockState state, args[1] = Level level, args[2] = BlockPos pos,
        // args[3] = movedByPiston (optional Boolean)
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        boolean movedByPiston = args.length > 3 && (args[3] instanceof Boolean b) && b;

        if (!movedByPiston) {
            this.updateNeighborsInFront(level, pos, state);
        }

        super.affectNeighborsAfterRemoval(thisBlock, args);
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (state.canSurvive(level, pos)) {
            this.checkTickOnNeighbor(level, pos, state);
        } else {
            dev.arubik.craftengine.util.MNms.INSTANCE.method$LevelWriter$destroyBlock(level, pos, true);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), state.getBlock());
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        BlockState state2 = (BlockState) state.customBlockState().minecraftState();
        Level level = (Level) context.getLevel().minecraftWorld();
        BlockPos blockPos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
        BukkitServerPlayer player = (BukkitServerPlayer) context.getPlayer();
        Player mcPlayer = (Player) player.serverPlayer();
        InteractionHand hand = context.getHand().equals(
                net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;
        BlockHitResult hitResult = Utils.toBlockHitResult(context.getHitResult().location(),
                context.getHitResult().direction(), context.getClickedPos());
        return this.use(state2, level, blockPos, mcPlayer, hand, hitResult);
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
            InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return InteractionResult.PASS;
    }
}
