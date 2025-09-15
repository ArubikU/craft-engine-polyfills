package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.util.BlockContainer;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class StorageBlockBehavior extends BukkitBlockBehavior {
  public static final Factory FACTORY = new Factory();
  
  private final int size;
  
  private final String title;
  
  public StorageBlockBehavior(CustomBlock customBlock, int size, String title) {
    super(customBlock);
    this.size = Math.max(9, Math.min(size, 54));
    this.title = (title != null) ? title : "Storage";
  }
  
  public static class Factory implements BlockBehaviorFactory {
    public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
      int size = 27;
      String title = "Storage";
      Object s = arguments.get("size");
      if (s instanceof Number) {
        Number n = (Number)s;
        size = n.intValue();
      } 
      Object t = arguments.get("title");
      if (t != null)
        title = String.valueOf(t); 
      return (BlockBehavior)new StorageBlockBehavior(block, size, title);
    }
  }
  
  public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
    ServerLevel serverLevel = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
    try {
      Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());
      if (posHandle instanceof BlockPos) {
        BlockPos pos = (BlockPos)posHandle;
        BlockContainer holder = BlockContainer.getOrCreate((Level)serverLevel, pos, this.size, this.title);
        BukkitServerPlayer player = (BukkitServerPlayer)context.getPlayer();
        Player bukkit = player.platformPlayer();
        if (bukkit instanceof Player) {
          Player p = bukkit;
          holder.open(p);
        } 
        return InteractionResult.SUCCESS_AND_CANCEL;
      } 
    } catch (Throwable throwable) {}
    return InteractionResult.PASS;
  }
  
  public Object getContainer(Object thisBlock, Object[] args) {
    Level level = (Level)args[1];
    BlockPos pos = (BlockPos)args[2];
    return BlockContainer.getOrCreate(level, pos, this.size, this.title);
  }
  
  @Override
  public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
    Optional<BlockContainer> container = BlockContainer.get((Level)args[1], (BlockPos)args[2]);
    if (container.isEmpty())
      return 0; 
    return ((BlockContainer)container.get()).getAnalogOutput();
  }
  
  @Override
  public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
    return true;
  }
}
