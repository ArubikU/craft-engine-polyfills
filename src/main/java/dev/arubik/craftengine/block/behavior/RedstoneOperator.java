package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.concurrent.Callable;

import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption.Flags;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.world.Vec3d;
import net.momirealms.craftengine.core.world.WorldPosition;

public class RedstoneOperator extends DiodeBlockBehavior {

    public static Factory FACTORY = new Factory();

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            int delay = 0;
            Object d = arguments.get("delay");
            if (d instanceof Number n)
                delay = n.intValue();
            @SuppressWarnings("unchecked")
            Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
            @SuppressWarnings("unchecked")
            Property<HorizontalDirection> facing = (Property<HorizontalDirection>) block.getProperty("facing");

            Property<?> mode = block.getProperty("mode");
            @SuppressWarnings("unchecked")
            Property<Boolean> invert = (Property<Boolean>) block.getProperty("invert");
            return new RedstoneOperator(block, delay, powered, facing, mode, invert);
        }
    }

    public final Property<?> MODE;
    public final BooleanProperty INVERT;

    public RedstoneOperator(CustomBlock arg0, int delay, Property<Boolean> powered,
            Property<HorizontalDirection> facing, Property<?> mode, Property<Boolean> invert) {
        super(arg0, delay, powered, facing);
        this.MODE = mode;
        this.INVERT = (BooleanProperty) invert;
    }

    private int getMode(ImmutableBlockState state) {
        Object val = state.get(MODE);
        if (val instanceof Integer i)
            return i;
        if (val instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private ImmutableBlockState setMode(ImmutableBlockState state, int mode) {
        if (MODE instanceof IntegerProperty) {
            return state.with((Property<Integer>) MODE, mode);
        } else {
            return state.with((Property<String>) MODE, String.valueOf(mode));
        }
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockState state = (BlockState) args[0];
        try {
            super.onPlace(thisBlock, args, superMethod);
        } catch (Exception e) {
        }
        scheduleUpdate(level, pos, state);
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        if (!state.canSurvive((LevelReader) level, pos)) {

            FastNMS.INSTANCE.method$LevelWriter$destroyBlock(level, pos, true);
            for (Direction dir : Direction.values())
                level.updateNeighborsAt(pos.relative(dir), (Block) state.getBlock());
            return;
        }
        scheduleUpdate(level, pos, state);
    }

    private void scheduleUpdate(Level level, BlockPos pos, BlockState state) {
        level.scheduleTick(pos, (Block) state.getBlock(), getDelay(state), TickPriority.HIGH);
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {

        if (this.delay != 0) {
            Object blockState = args[0];
            Object level = args[1];
            Object blockPos = args[2];
            if (!this.canSurvive(thisBlock, args, () -> {
                return true;
            })) {
                BlockStateUtils.getOptionalCustomBlockState(blockState).ifPresent((customState) -> {
                    if (!customState.isEmpty() && customState.owner().value() == this.customBlock) {
                        BukkitWorld world = new BukkitWorld(FastNMS.INSTANCE.method$Level$getCraftWorld(level));
                        WorldPosition position = new WorldPosition(world,
                                Vec3d.atCenterOf(LocationUtils.fromBlockPos(blockPos)));
                        world.playBlockSound(position, customState.settings().sounds().breakSound());
                        FastNMS.INSTANCE.method$LevelWriter$destroyBlock(level, blockPos, true);
                    }

                });
            }

        }
        BlockState state = (BlockState) args[0];
        ServerLevel level = (ServerLevel) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (isLocked((LevelReader) level, pos, state))
            return;
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        boolean currentlyPowered = customState.get(POWERED);
        boolean shouldBePowered = computePower((Level) level, pos, state);
        if (currentlyPowered != shouldBePowered) {
            customState = customState.with(POWERED, shouldBePowered);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, customState.customBlockState().literalObject(),
                    Flags.UPDATE_CLIENTS);
            notifyFrontNeighbors((Level) level, pos, state);
        }

    }

    private void notifyFrontNeighbors(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        level.updateNeighborsAt(pos.relative(Utils.fromDirection(customState.get(FACING))), state.getBlock());
    }

    private boolean computePower(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        Direction facing = Utils.fromDirection(customState.get(FACING));
        Direction leftDir = facing.getCounterClockWise();
        Direction rightDir = facing.getClockWise();
        boolean rightPower = getPower(level, pos.relative(leftDir), leftDir);
        boolean leftPower = getPower(level, pos.relative(rightDir), rightDir);
        boolean backPower = getPower(level, pos.relative(facing), facing);
        boolean currentInvert = customState.get(INVERT);
        if (currentInvert != backPower) {
            customState = customState.with(INVERT, backPower);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, customState.customBlockState().literalObject(),
                    Flags.UPDATE_CLIENTS);
        }
        // and or and xor
        boolean invert = customState.get(INVERT);
        switch (getMode(customState)) {
            case 0:
                return invert ? !(leftPower && rightPower) : (leftPower && rightPower);
            case 1:
                return invert ? !(leftPower || rightPower) : (leftPower || rightPower);
            case 2:
                return invert ? !(leftPower ^ rightPower) : (leftPower ^ rightPower);
            default:
                return false;
        }
    }

    private boolean getPower(Level level, BlockPos pos, Direction from) {
        return (level.getSignal(pos, from) > 0 || level.getDirectSignal(pos, from) > 0);
    }

    protected int getOutputSignal(BlockGetter world, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        return customState.get(POWERED) ? 15 : 0;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
            InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (FastNMS.INSTANCE.method$LevelReader$isClientSide(level)) {
            return InteractionResult.SUCCESS;
        }
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElseThrow();
        int currentMode = getMode(customState);
        int nextMode = (currentMode + 1) % 3;
        ImmutableBlockState newState = setMode(customState, nextMode);
        FastNMS.INSTANCE.method$LevelWriter$setBlock(level, blockPos, newState.customBlockState().literalObject(),
                Flags.UPDATE_CLIENTS);
        return InteractionResult.SUCCESS;
    }
}