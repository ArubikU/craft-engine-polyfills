package net.eucloid.bitwise.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.ticks.TickPriority;

public class RedstoneOperatorBlock extends DiodeBlock {
  public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 2);
  
  public static final EnumProperty<Direction> FACING = DiodeBlock.FACING;
  
  public static final BooleanProperty INVERT = BooleanProperty.create("invert");
  
  public static final MapCodec<RedstoneOperatorBlock> CODEC = simpleCodec(RedstoneOperatorBlock::new);
  
  public RedstoneOperatorBlock(BlockBehaviour.Properties props) {
    super(props);
    registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition
        .any())
        .setValue((Property)MODE, Integer.valueOf(0)))
        .setValue((Property)FACING, (Comparable)Direction.NORTH))
        .setValue((Property)POWERED, Boolean.valueOf(false)))
        .setValue((Property)INVERT, Boolean.valueOf(false)));
  }
  
  protected MapCodec<? extends DiodeBlock> codec() {
    return (MapCodec)CODEC;
  }
  
  protected int getDelay(BlockState state) {
    return 1;
  }
  
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(new Property[] { (Property)MODE, (Property)POWERED, (Property)FACING, (Property)INVERT });
  }
  
  public boolean isSignalSource(BlockState state) {
    return true;
  }
  
  public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, level, pos, oldState, isMoving);
    scheduleUpdate(level, pos, state);
  }
  
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
    if (!state.canSurvive((LevelReader)level, pos)) {
      BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
      dropResources(state, (LevelAccessor)level, pos, blockEntity);
      level.removeBlock(pos, false);
      for (Direction dir : Direction.values())
        level.updateNeighborsAt(pos.relative(dir), (Block)this); 
      return;
    } 
    scheduleUpdate(level, pos, state);
  }
  
  private void scheduleUpdate(Level level, BlockPos pos, BlockState state) {
    level.scheduleTick(pos, (Block)this, getDelay(state), TickPriority.HIGH);
  }
  
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (isLocked((LevelReader)level, pos, state))
      return; 
    boolean currentlyPowered = ((Boolean)state.getValue((Property)POWERED)).booleanValue();
    boolean shouldBePowered = computePower((Level)level, pos, state);
    if (currentlyPowered != shouldBePowered) {
      level.setBlock(pos, (BlockState)state.setValue((Property)POWERED, Boolean.valueOf(shouldBePowered)), 2);
      notifyFrontNeighbors((Level)level, pos, state);
    } 
  }
  
  private void notifyFrontNeighbors(Level level, BlockPos pos, BlockState state) {
    level.updateNeighborsAt(pos.relative((Direction)state.getValue((Property)FACING)), (Block)this);
  }
  
  private boolean computePower(Level level, BlockPos pos, BlockState state) {
    Direction facing = (Direction)state.getValue((Property)FACING);
    Direction leftDir = facing.getCounterClockWise();
    Direction rightDir = facing.getClockWise();
    Direction backDir = facing.getOpposite();
    boolean rightPower = getPower(level, pos.relative(leftDir), leftDir);
    boolean leftPower = getPower(level, pos.relative(rightDir), rightDir);
    boolean backPower = getPower(level, pos.relative(facing), facing);
    boolean currentInvert = ((Boolean)state.getValue((Property)INVERT)).booleanValue();
    if (currentInvert != backPower)
      level.setBlock(pos, (BlockState)state.setValue((Property)INVERT, Boolean.valueOf(backPower)), 2); 
    switch (((Integer)state.getValue((Property)MODE)).intValue()) {
      case 0:
        if (leftPower && rightPower);
      case 1:
        if (leftPower || rightPower);
      case 2:
      
      default:
        break;
    } 
    boolean result = false;
    return ((Boolean)state.getValue((Property)INVERT)).booleanValue() ? (!result) : result;
  }
  
  private boolean getPower(Level level, BlockPos pos, Direction from) {
    return (level.getSignal(pos, from) > 0 || level.getDirectSignal(pos, from) > 0);
  }
  
  protected int getOutputSignal(BlockGetter world, BlockPos pos, BlockState state) {
    return ((Boolean)state.getValue((Property)POWERED)).booleanValue() ? 15 : 0;
  }
}
