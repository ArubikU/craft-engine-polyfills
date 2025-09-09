package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import dev.arubik.craftengine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption.Flags;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.sound.SoundData;
import net.momirealms.craftengine.core.sound.SoundData.SoundValue;
import net.momirealms.craftengine.core.world.Vec3d;

public class ButtonBlockBehavior extends BukkitBlockBehavior {
    
    public static class Factory implements BlockBehaviorFactory {
        @SuppressWarnings("unchecked")
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            int ticksToStayPressed = (int) arguments.getOrDefault("ticks_to_stay_pressed", 20);
            SoundData soundOn = null;
            SoundData soundOff = null;
            Map<String, Object> sounds = (Map<String, Object>) arguments.get("sounds");
            if (sounds != null) {
                soundOn = (SoundData) Optional.ofNullable(sounds.get("on")).map((obj) -> {
                    return SoundData.create(obj, SoundValue.FIXED_1, SoundValue.ranged(0.9F, 1.0F));
                }).orElse(null);
                soundOff = (SoundData) Optional.ofNullable(sounds.get("off")).map((obj) -> {
                    return SoundData.create(obj, SoundValue.FIXED_1, SoundValue.ranged(0.9F, 1.0F));
                }).orElse(null);
            }
            Property<Boolean> powered = (Property<Boolean>) block.getProperty("powered");
            return new ButtonBlockBehavior(block, ticksToStayPressed, soundOn, soundOff, powered);
        }
    }

    protected final CustomBlock customBlock;
    protected final int ticksToStayPressed;
    protected final SoundData soundOn;
    protected final SoundData soundOff;
    protected final BooleanProperty POWERED;

    public ButtonBlockBehavior(CustomBlock block, int ticksToStayPressed, SoundData soundOn, SoundData soundOff, Property<Boolean> powered) {
        super(block);
        this.customBlock = block;
        this.ticksToStayPressed = ticksToStayPressed;
        this.soundOn = soundOn;
        this.soundOff = soundOff;
        this.POWERED = (BooleanProperty) powered;
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
       BlockState state2 =(BlockState) state.customBlockState().literalObject();
       Level level = (Level) context.getLevel().serverWorld();
       BlockPos blockPos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
       BukkitServerPlayer player = (BukkitServerPlayer) context.getPlayer();
       Player mcPlayer = (Player) player.serverPlayer();
       InteractionHand hand = context.getHand().equals(net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
       BlockHitResult hitResult = Utils.toBlockHitResult(context.getHitResult().getLocation(), context.getHitResult().getDirection(), context.getClickedPos());
       return this.use(state2, level, blockPos, mcPlayer, hand, hitResult);
    }
    
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        if (customState == null || customState.owner().value() != this.customBlock) {
            return InteractionResult.PASS;
        }

        if (customState.get(POWERED)) {
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            press(customState, level, blockPos, player);
        }
        
        return InteractionResult.SUCCESS;
    }

    public void press(ImmutableBlockState state, Level level, BlockPos pos, Player player) {
        ImmutableBlockState newState = state.with(POWERED, true);
        FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(), Flags.UPDATE_CLIENTS);
        
        updateNeighbours(newState, level, pos);
        BlockState nmsstate = (BlockState) newState.customBlockState().literalObject();
        level.scheduleTick(pos, nmsstate.getBlock(), this.ticksToStayPressed);
        playSound(player, level, pos, true);
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        BlockState blockState = (BlockState) args[0];
        ServerLevel level = (ServerLevel) args[1];
        BlockPos pos = (BlockPos) args[2];

        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        if (customState != null && customState.get(POWERED)) {
            ImmutableBlockState newState = customState.with(POWERED, false);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(level, pos, newState.customBlockState().literalObject(), Flags.UPDATE_CLIENTS);
            updateNeighbours(newState, level, pos);
            playSound(null, level, pos, false);
        }
    }

    protected void playSound(Player player, Level level, BlockPos pos, boolean isPress) {
        SoundData soundData = isPress ? this.soundOn : this.soundOff;
        if (soundData != null) {
            (new BukkitWorld(level.getWorld())).playBlockSound(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5), soundData);
        }
    }

    private void updateNeighbours(ImmutableBlockState state, Level level, BlockPos pos) {
        BlockState nmsstate = (BlockState) state.customBlockState().literalObject();
        level.updateNeighborsAt(pos, nmsstate.getBlock());
    }

    @Override
    public int getSignal(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState blockState = (BlockState) args[0];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        if (customState != null && customState.owner().value() == this.customBlock) {
            return customState.get(POWERED) ? 15 : 0;
        }
        return 0;
    }

    @Override
    public int getDirectSignal(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState blockState = (BlockState) args[0];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        if (customState != null && customState.owner().value() == this.customBlock && customState.get(POWERED)) {
            return 0; 
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        BlockState blockState = (BlockState) args[0];
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).orElse(null);
        return customState != null && customState.owner().value() == this.customBlock;
    }

}
