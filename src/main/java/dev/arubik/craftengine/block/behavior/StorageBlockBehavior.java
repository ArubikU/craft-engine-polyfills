package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.StorageBlockEntity;
import dev.arubik.craftengine.util.SoundMap;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class StorageBlockBehavior extends BukkitBlockBehavior
    implements net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder, EntityBlock {
  public static final Factory FACTORY = new Factory();

  private final int size;

  private final String title;

  private final dev.arubik.craftengine.util.SoundMap soundMap;

  public StorageBlockBehavior(BlockDefinition customBlock, int size, String title,
      dev.arubik.craftengine.util.SoundMap soundMap) {
    super(customBlock);
    this.size = Math.max(9, Math.min(size, 54));
    this.title = (title != null) ? title : "Storage";
    this.soundMap = soundMap;
  }

  private int controllerId;

  @Override
  public void initControllerId(int id) {
    this.controllerId = id;
  }

  @Override
  public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
    return new StorageBlockEntity(blockEntity, size, title, soundMap);
  }

  /** Looks up the loaded {@link StorageBlockEntity} at {@code pos}, or null. */
  public static StorageBlockEntity getStorage(Level level, BlockPos pos) {
    BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
    return (be != null && be.controller instanceof StorageBlockEntity s) ? s : null;
  }

  public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
    public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
      int size = (int) arguments.getOrDefault("size", 27);
      String title = (String) arguments.getOrDefault("title", "Storage");
      Object soundMapObj = arguments.get("sound-map");
      SoundMap soundMap = null;
      if (soundMapObj instanceof Map) {
        soundMap = SoundMap.fromMap((Map<String, Object>) soundMapObj);
      }
      return (BlockBehavior) new StorageBlockBehavior(block, size, title, soundMap);
    }
  }

  public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
    ServerLevel serverLevel = ((CraftWorld) ((BukkitWorld) context.getLevel()).platformWorld()).getHandle();
    try {
      Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());
      if (posHandle instanceof BlockPos) {
        BlockPos pos = (BlockPos) posHandle;
        StorageBlockEntity holder = getStorage(serverLevel, pos);
        if (holder == null)
          return InteractionResult.PASS;
        BukkitServerPlayer player = (BukkitServerPlayer) context.getPlayer();
        Player bukkit = player.platformPlayer();
        if (bukkit instanceof Player) {
          Player p = bukkit;
          holder.open(p);
        }
        return InteractionResult.SUCCESS_AND_CANCEL;
      }
    } catch (Throwable throwable) {
    }
    return InteractionResult.PASS;
  }

  public Object getContainer(Object thisBlock, Object[] args) {
    Level level = (Level) args[1];
    BlockPos pos = (BlockPos) args[2];
    return getStorage(level, pos);
  }

  @Override
  public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
    Level level = (Level) args[1];
    BlockPos pos = (BlockPos) args[2];
    StorageBlockEntity storage = getStorage(level, pos);
    if (storage == null)
      return 0;
    return storage.getAnalogOutput();
  }

  @Override
  public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
    return true;
  }
}
