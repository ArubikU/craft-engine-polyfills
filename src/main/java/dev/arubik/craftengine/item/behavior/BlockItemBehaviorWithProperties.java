package dev.arubik.craftengine.item.behavior;

import java.nio.file.Path;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import net.momirealms.craftengine.bukkit.item.behavior.BlockItemBehavior;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.plugin.config.KnownResourceException;
import net.momirealms.craftengine.core.util.Key;

public class BlockItemBehaviorWithProperties extends BlockItemBehavior {
   public static class Factory implements ItemBehaviorFactory<ItemBehavior> {
      public Factory() {
      }

      @Override
      public ItemBehavior create(Pack pack, Path path, Key key, ConfigSection arguments) {
         Object id = arguments.get("block");
         if (id == null) {
            throw new KnownResourceException("warning.config.item.behavior.block.missing_block", "block");
         } else {
            return new BlockItemBehaviorWithProperties(Key.of(id.toString()));
         }
      }

   }

   public static final Factory FACTORY = new Factory();

   public BlockItemBehaviorWithProperties(Key blockId) {
      super(blockId);
   }

   @Override
   protected boolean checkStatePlacement() {
      return true;
   }

   @Override
   protected boolean placeBlock(Location location, ImmutableBlockState blockState, List<BlockState> revertStates) {
      return super.placeBlock(location, blockState, revertStates);
   }
}
