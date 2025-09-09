package dev.arubik.craftengine.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.event.player.PlayerPickBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.function.Predicate;

final class BlockDataListener implements Listener {

    private final Plugin plugin;
    private final Predicate<Block> customDataPredicate;

    public BlockDataListener(Plugin plugin) {
        this.plugin = plugin;
        this.customDataPredicate = block -> CustomBlockData.hasCustomBlockData(block, plugin);
    }

    private CustomBlockData getCbd(BlockEvent event) {
        return getCbd(event.getBlock());
    }

    private CustomBlockData getCbd(Block block) {
        return CustomBlockData.from(block);
    }

    private void callAndRemove(BlockEvent blockEvent) {
        callAndRemove(blockEvent.getBlock(), blockEvent);
    }

    private boolean callEvent(Block block, Event bukkitEvent) {
        if(!CustomBlockData.hasCustomBlockData(block, plugin) || CustomBlockData.isProtected(block, plugin)) {
            return false;
        }

        //CustomBlockDataRemoveEvent cbdEvent = new CustomBlockDataRemoveEvent(plugin, block, bukkitEvent);
        //Bukkit.getPluginManager().callEvent(cbdEvent);

        //return !cbdEvent.isCancelled();
        return true;
    }

    private void callAndRemoveBlockStateList(List<BlockState> blockStates, Event bukkitEvent) {
        blockStates.stream()
                .map(BlockState::getBlock)
                .filter(customDataPredicate)
                .forEach(block -> callAndRemove(block,bukkitEvent));
    }

    private void callAndRemoveBlockList(List<Block> blocks, Event bukkitEvent) {
        blocks.stream()
                .filter(customDataPredicate)
                .forEach(block -> callAndRemove(block,bukkitEvent));
    }

    private void callAndRemove(Block block, Event bukkitEvent) {
        if (callEvent(block, bukkitEvent)) {
            Optional<BlockContainer> container =  BlockContainer.get(block);

            if(container.isPresent()){
                container.get().destroy(true);
            }else{
                getCbd(block).clear();
            }
        }
    }
    private void callAndDestroy(Block block, Event bukkitEvent) {
        if (callEvent(block, bukkitEvent)) {
            Optional<BlockContainer> container =  BlockContainer.get(block);
            if(container.isPresent()){
                container.get().destroy(false);
            }else{
                getCbd(block).clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        callAndRemove(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if(!CustomBlockData.isDirty(event.getBlock())) {
            callAndRemove(event);
        }
        PersistentDataContainer item = event.getItemInHand().getItemMeta().getPersistentDataContainer();
        if(TypedKey.has(TypedKeys.PERSISTENT_DATA, item)) {
            PersistentDataContainer fromItem = TypedKey.get(TypedKeys.PERSISTENT_DATA, item);
            if(fromItem != null) {
                getCbd(event.getBlock()).loadFrom(fromItem);
            }
        }
    }

    @EventHandler
    public void onPlayerPickBlock(PlayerPickBlockEvent event){
        Block block = event.getBlock();
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        if(CustomBlockData.hasCustomBlockData(block, plugin)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                CustomBlockData cbd = CustomBlockData.from(block);
                if(cbd.isEmpty()) return;
                ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getTargetSlot());
                
                itemStack.editMeta(meta -> {
                TypedKey.set(TypedKeys.PERSISTENT_DATA, cbd.getPdc(), meta.getPersistentDataContainer());
                    List<Component> lore = meta.lore();
                    if(lore == null) lore = new ArrayList<>();
                    lore.add(0,MiniMessage.miniMessage().deserialize("<italic><dark_purple>(+nbt)"));
                    meta.lore(lore);
                });

                event.getPlayer().getInventory().setItem(event.getTargetSlot(), itemStack);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntity(EntityChangeBlockEvent event) {
        if(event.getTo() != event.getBlock().getType()) {
            callAndRemove(event.getBlock(), event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        callAndRemoveBlockList(event.blockList(),event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        callAndRemoveBlockList(event.blockList(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        callAndRemove(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPiston(BlockPistonExtendEvent event) {
        onPiston(event.getBlocks(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPiston(BlockPistonRetractEvent event) {
        onPiston(event.getBlocks(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFade(BlockFadeEvent event) {
        if(event.getBlock().getType() == Material.FIRE) return;
        if(event.getNewState().getType() != event.getBlock().getType()) {
            callAndRemove(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStructure(StructureGrowEvent event) {
        callAndRemoveBlockStateList(event.getBlocks(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFertilize(BlockFertilizeEvent event) {
        callAndRemoveBlockStateList(event.getBlocks(), event);
    }

    private void onPiston(List<Block> blocks, BlockPistonEvent bukkitEvent) {
        Map<Block, CustomBlockData> map = new LinkedHashMap<>();
        BlockFace direction = bukkitEvent.getDirection();
        blocks.stream().filter(customDataPredicate).forEach(block -> {
            CustomBlockData cbd = CustomBlockData.from(block);
            if(cbd.isEmpty() || cbd.isProtected()) return;
            PistonMoveReaction reaction = block.getPistonMoveReaction();
            BlockContainer.get(block).ifPresent(container -> {
                container.closeViewers();
            });
            if(reaction == PistonMoveReaction.BREAK) {
                callAndRemove(block, bukkitEvent);
                return;
            }
            Block destinationBlock = block.getRelative(direction);
            //CustomBlockDataMoveEvent moveEvent = new CustomBlockDataMoveEvent(plugin, block, destinationBlock, bukkitEvent);
            //Bukkit.getPluginManager().callEvent(moveEvent);
            //if (moveEvent.isCancelled()) return;
            map.put(destinationBlock, cbd);
        });
        Utils.reverse(map).forEach((block, cbd) -> {
            cbd.copyTo(block, plugin);
            callAndDestroy(cbd.getBlock(), bukkitEvent);
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        CustomBlockData.clearChunk(event.getChunk().getChunkKey());
    }

    private static final class Utils {

        private static <K, V> Map<K, V> reverse(Map<K, V> map) {
            LinkedHashMap<K, V> reversed = new LinkedHashMap<>();
            List<K> keys = new ArrayList<>(map.keySet());
            Collections.reverse(keys);
            keys.forEach((key) -> reversed.put(key, map.get(key)));
            return reversed;
        }

    }

}