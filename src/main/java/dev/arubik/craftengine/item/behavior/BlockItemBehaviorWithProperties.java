package dev.arubik.craftengine.item.behavior;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockAttemptPlaceEvent;
import net.momirealms.craftengine.bukkit.api.event.CustomBlockPlaceEvent;
import net.momirealms.craftengine.bukkit.block.BukkitBlockManager;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.reflection.minecraft.CoreReflections;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.AdventureModeUtils;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.DirectionUtils;
import net.momirealms.craftengine.bukkit.util.EventUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateOption;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.item.behavior.BlockBoundItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.item.context.BlockPlaceContext;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.plugin.config.Config;
import net.momirealms.craftengine.core.plugin.context.ContextHolder;
import net.momirealms.craftengine.core.plugin.context.PlayerOptionalContext;
import net.momirealms.craftengine.core.plugin.context.event.EventTrigger;
import net.momirealms.craftengine.core.plugin.context.parameter.DirectContextParameters;
import net.momirealms.craftengine.core.plugin.locale.LocalizedResourceConfigException;
import net.momirealms.craftengine.core.util.Cancellable;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.MiscUtils;
import net.momirealms.craftengine.core.util.VersionHelper;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.WorldPosition;

public class BlockItemBehaviorWithProperties extends BlockBoundItemBehavior {
    public static class Factory implements ItemBehaviorFactory {
   public Factory() {
   }

   public ItemBehavior create(Pack pack, Path path, Key key, Map<String, Object> arguments) {
      Object id = arguments.get("block");
      if (id == null) {
         throw new LocalizedResourceConfigException("warning.config.item.behavior.block.missing_block", new IllegalArgumentException("Missing required parameter 'block' for block_item behavior"), new String[0]);
      } else if (id instanceof Map) {
         Map<?, ?> map = (Map)id;
         if (map.containsKey(key.toString())) {
            BukkitBlockManager.instance().parser().parseSection(pack, path, key, MiscUtils.castToMap(map.get(key.toString()), false));
         } else {
            BukkitBlockManager.instance().parser().parseSection(pack, path, key, MiscUtils.castToMap(map, false));
         }

         return new BlockItemBehaviorWithProperties(key);
      } else {
         return new BlockItemBehaviorWithProperties(Key.of(id.toString()));
      }
   }
}

   public static final Factory FACTORY = new Factory();
   private final Key blockId;

   public BlockItemBehaviorWithProperties(Key blockId) {
      this.blockId = blockId;
   }

   public InteractionResult useOnBlock(UseOnContext context) {
      return this.place(new BlockPlaceContext(context));
   }

   public InteractionResult place(BlockPlaceContext context) {
      Optional<CustomBlock> optionalBlock = BukkitBlockManager.instance().blockById(this.blockId);
      if (optionalBlock.isEmpty()) {
         CraftEngine.instance().logger().warn("Failed to place unknown block " + String.valueOf(this.blockId));
         return InteractionResult.FAIL;
      } else if (!context.canPlace()) {
         return InteractionResult.PASS;
      } else {
         Player player = context.getPlayer();
         CustomBlock block = (CustomBlock)optionalBlock.get();
         BlockPos pos = context.getClickedPos();
         int maxY = context.getLevel().worldHeight().getMaxBuildHeight() - 1;
         if (context.getClickedFace() == Direction.UP && pos.y() >= maxY) {
            if (player != null) {
               BukkitServerPlayer cePlayer = (BukkitServerPlayer)player;
               cePlayer.platformPlayer().sendMessage(net.kyori.adventure.text.Component.translatable("build.tooHigh").args(net.kyori.adventure.text.Component.text(maxY)).color(net.kyori.adventure.text.format.NamedTextColor.RED));
            }

            return InteractionResult.FAIL;
         } else {
            ImmutableBlockState blockStateToPlace = this.getPlacementState(context, block);
            if (blockStateToPlace == null) {
               return InteractionResult.PASS;
            } else {
               BlockPos againstPos = context.getAgainstPos();
               World world = (World)context.getLevel().platformWorld();
               Location placeLocation = new Location(world, (double)pos.x(), (double)pos.y(), (double)pos.z());
               Block bukkitBlock = world.getBlockAt(placeLocation);
               Block againstBlock = world.getBlockAt(againstPos.x(), againstPos.y(), againstPos.z());
               org.bukkit.entity.Player bukkitPlayer = player != null ? (org.bukkit.entity.Player)player.platformPlayer() : null;
               if (player != null) {
                  if (player.isAdventureMode()) {
                     Object againstBlockState = BlockStateUtils.blockDataToBlockState(againstBlock.getBlockData());
                     Optional<ImmutableBlockState> optionalCustomState = BlockStateUtils.getOptionalCustomBlockState(againstBlockState);
                     if (optionalCustomState.isEmpty()) {
                        if (!AdventureModeUtils.canPlace(context.getItem(), context.getLevel(), againstPos, againstBlockState)) {
                           return InteractionResult.FAIL;
                        }
                     } else {
                        ImmutableBlockState customState = (ImmutableBlockState)optionalCustomState.get();
                        if (!AdventureModeUtils.canPlace(context.getItem(), context.getLevel(), againstPos, Config.simplifyAdventurePlaceCheck() ? customState.vanillaBlockState().literalObject() : againstBlockState)) {
                           return InteractionResult.FAIL;
                        }
                     }
                  }

                  CustomBlockAttemptPlaceEvent attemptPlaceEvent = new CustomBlockAttemptPlaceEvent(bukkitPlayer, placeLocation.clone(), blockStateToPlace, DirectionUtils.toBlockFace(context.getClickedFace()), bukkitBlock, context.getHand());
                  if (EventUtils.fireAndCheckCancel(attemptPlaceEvent)) {
                     return InteractionResult.FAIL;
                  }
               }

               BlockState previousState = bukkitBlock.getState();
               List<BlockState> revertStates = new ArrayList(2);
               revertStates.add(previousState);
               this.placeBlock(placeLocation, blockStateToPlace, revertStates);
               if (player != null) {
                  BlockPlaceEvent bukkitPlaceEvent = new BlockPlaceEvent(bukkitBlock, previousState, againstBlock, (ItemStack)context.getItem().getItem(), bukkitPlayer, true, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
                  if (EventUtils.fireAndCheckCancel(bukkitPlaceEvent)) {
                     Iterator var26 = revertStates.iterator();

                     while(var26.hasNext()) {
                        BlockState state = (BlockState)var26.next();
                        state.update(true, false);
                     }

                     return InteractionResult.FAIL;
                  }

                  CustomBlockPlaceEvent customPlaceEvent = new CustomBlockPlaceEvent(bukkitPlayer, placeLocation.clone(), blockStateToPlace, world.getBlockAt(placeLocation), context.getHand());
                  if (EventUtils.fireAndCheckCancel(customPlaceEvent)) {
                     Iterator var27 = revertStates.iterator();

                     while(var27.hasNext()) {
                        BlockState state = (BlockState)var27.next();
                        state.update(true, false);
                     }

                     return InteractionResult.FAIL;
                  }
               }

               WorldPosition position = new WorldPosition(context.getLevel(), (double)pos.x() + 0.5, (double)pos.y() + 0.5, (double)pos.z() + 0.5);
               Cancellable dummy = Cancellable.dummy();
               PlayerOptionalContext functionContext = PlayerOptionalContext.of(player, ContextHolder.builder().withParameter(DirectContextParameters.BLOCK, new BukkitExistingBlock(bukkitBlock)).withParameter(DirectContextParameters.POSITION, position).withParameter(DirectContextParameters.EVENT, dummy).withParameter(DirectContextParameters.HAND, context.getHand()).withParameter(DirectContextParameters.ITEM_IN_HAND, context.getItem()));
               block.execute(functionContext, EventTrigger.PLACE);
               if (dummy.isCancelled()) {
                  return InteractionResult.SUCCESS_AND_CANCEL;
               } else {
                  if (player != null) {
                     if (!player.isCreativeMode()) {
                        Item<?> item = context.getItem();
                        item.count(item.count() - 1);
                     }

                     player.swingHand(context.getHand());
                  }

                  block.setPlacedBy(context, blockStateToPlace);
                  context.getLevel().playBlockSound(position, blockStateToPlace.settings().sounds().placeSound());
                  world.sendGameEvent(bukkitPlayer, GameEvent.BLOCK_PLACE, new Vector(pos.x(), pos.y(), pos.z()));
                  return InteractionResult.SUCCESS;
               }
            }
         }
      }
   }

   protected ImmutableBlockState getPlacementState(BlockPlaceContext context, CustomBlock block) {
      ImmutableBlockState state = block.getStateForPlacement(context);
      return state != null && this.canPlace(context, state) ? state : null;
   }

   protected boolean checkStatePlacement() {
      return true;
   }

   protected boolean canPlace(BlockPlaceContext context, ImmutableBlockState state) {
      try {
         Player cePlayer = context.getPlayer();
         Object player = cePlayer != null ? cePlayer.serverPlayer() : null;
         Object blockState = state.customBlockState().literalObject();
         Object blockPos = LocationUtils.toBlockPos(context.getClickedPos());
         Object voxelShape;
         if (VersionHelper.isOrAbove1_21_6()) {
            voxelShape = CoreReflections.method$CollisionContext$placementContext.invoke((Object)null, player);
         } else if (player != null) {
            voxelShape = CoreReflections.method$CollisionContext$of.invoke((Object)null, player);
         } else {
            voxelShape = CoreReflections.instance$CollisionContext$empty;
         }

         Object world = FastNMS.INSTANCE.field$CraftWorld$ServerLevel((World)context.getLevel().platformWorld());
         boolean defaultReturn = (!this.checkStatePlacement() || FastNMS.INSTANCE.method$BlockStateBase$canSurvive(blockState, world, blockPos)) && (Boolean)CoreReflections.method$ServerLevel$checkEntityCollision.invoke(world, blockState, player, voxelShape, blockPos, true);
         Block block = FastNMS.INSTANCE.method$CraftBlock$at(world, blockPos);
         BlockData blockData = FastNMS.INSTANCE.method$CraftBlockData$fromData(blockState);
         BlockCanBuildEvent canBuildEvent = new BlockCanBuildEvent(block, cePlayer != null ? (org.bukkit.entity.Player)cePlayer.platformPlayer() : null, blockData, defaultReturn, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
         Bukkit.getPluginManager().callEvent(canBuildEvent);
         return canBuildEvent.isBuildable();
      } catch (ReflectiveOperationException var13) {
         CraftEngine.instance().logger().warn("Failed to check canPlace", var13);
         return false;
      }
   }

   protected boolean placeBlock(Location location, ImmutableBlockState blockState, List<BlockState> revertStates) {
      return CraftEngineBlocks.place(location, blockState, UpdateOption.UPDATE_ALL_IMMEDIATE, false);
   }

   public Key block() {
      return this.blockId;
   }
}
