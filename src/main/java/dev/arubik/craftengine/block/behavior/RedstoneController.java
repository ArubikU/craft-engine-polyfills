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
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption.Flags;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.IntegerProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.world.Vec3d;
import net.momirealms.craftengine.core.world.WorldPosition;

public class RedstoneController extends DiodeBlockBehavior {

    public static Factory FACTORY = new Factory();

    public static class Factory implements BlockBehaviorFactory {
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
            @SuppressWarnings("unchecked")
            Property<Integer> mode = (Property<Integer>) block.getProperty("mode");
            return new RedstoneController(block, delay, powered, facing, mode);
        }
    }

    public final IntegerProperty MODE;

    public RedstoneController(CustomBlock arg0, int delay, Property<Boolean> powered,
            Property<HorizontalDirection> facing, Property<Integer> mode) {
        super(arg0, delay, powered, facing);
        this.MODE = (IntegerProperty) mode;
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        BlockState state = (BlockState) args[0];

        this.updateNeighborsInFront(level, pos, state);
        try {
            super.onPlace(thisBlock, args, superMethod);
        } catch (Exception e) {
        }
    }

    private void notifyFrontNeighbors(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        level.updateNeighborsAt(pos.relative(Utils.fromDirection(customState.get(FACING))), state.getBlock());
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
                        FastNMS.INSTANCE.method$Level$destroyBlock(level, blockPos, true);
                    }

                });
            }

        }
        BlockState state = (BlockState) args[0];
        ServerLevel level = (ServerLevel) args[1];
        BlockPos pos = (BlockPos) args[2];
        updatePower(level, pos, state);

    }

    private void updatePower(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        boolean currentlyPowered = customState.get(POWERED);
        boolean shouldBePowered = computePower(level, pos, state);
        if (currentlyPowered != shouldBePowered) {
            customState = customState.with(POWERED, shouldBePowered);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, customState.customBlockState().literalObject(),
                    Flags.UPDATE_CLIENTS);
            notifyFrontNeighbors(level, pos, state);
        }
    }

    private boolean computePower(Level level, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        Direction backFace = Utils.fromDirection(customState.get(FACING));
        Direction rightFace = backFace.getCounterClockWise();
        Direction leftFace = backFace.getClockWise();
        boolean backPower = getPower(level, pos.relative(backFace), backFace);
        boolean leftPower = getPower(level, pos.relative(leftFace), leftFace);
        boolean rightPower = getPower(level, pos.relative(rightFace), rightFace);
        switch (customState.get(MODE)) {
            case 0:
                return leftPower;
            case 1:
                return backPower;
            case 2:
                return rightPower;
        }
        return false;
    }

    private boolean getPower(Level level, BlockPos pos, Direction from) {
        return (level.getSignal(pos, from) > 0 || level.getDirectSignal(pos, from) > 0);
    }

    protected int getOutputSignal(BlockGetter world, BlockPos pos, BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElseThrow();
        return customState.get(POWERED) ? 15 : 0;
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (!state.canSurvive((LevelReader) level, pos)) {
            FastNMS.INSTANCE.method$Level$destroyBlock(level, pos, true);
            for (Direction dir : Direction.values())
                level.updateNeighborsAt(pos.relative(dir), (Block) state.getBlock());
            return;
        }
        if (!level.isClientSide())
            updatePower(level, pos, state);

    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
            InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElseThrow();
        int currentMode = customState.get(MODE);
        int nextMode = (currentMode + 1) % 3;
        ImmutableBlockState newState = customState.with(MODE, nextMode);
        FastNMS.INSTANCE.method$LevelWriter$setBlock(level, blockPos, newState.customBlockState().literalObject(),
                Flags.UPDATE_CLIENTS);
        return InteractionResult.SUCCESS;
    }
}