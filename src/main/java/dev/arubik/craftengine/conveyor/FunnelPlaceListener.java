/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class FunnelPlaceListener
implements Listener {
    private static final Key FUNNEL_ITEM = Key.of((String)"cml", (String)"funnel");
    private static final Key FLOOR_BLOCK = Key.of((String)"cml", (String)"floor_funnel");
    private static final Key CEILING_BLOCK = Key.of((String)"cml", (String)"ceiling_funnel");
    private static final Key FUNNEL_BLOCK = Key.of((String)"cml", (String)"funnel");

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlace(PlayerInteractEvent e) {
        Key blockId;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Key id = CraftEngineItems.getCustomItemId((ItemStack)hand);
        if (id == null || !FUNNEL_ITEM.equals(id)) {
            return;
        }
        Block clicked = e.getClickedBlock();
        BlockFace face = e.getBlockFace();
        if (clicked == null || face == null) {
            return;
        }
        if (!player.isSneaking() && clicked.getState() instanceof InventoryHolder) {
            return;
        }
        Block target = clicked.getRelative(face);
        if (!ConveyorBlockEntity.isReplaceable(target)) {
            return;
        }
        BlockFace funnelFacing = null;
        if (face == BlockFace.UP) {
            blockId = FLOOR_BLOCK;
        } else if (face == BlockFace.DOWN) {
            blockId = CEILING_BLOCK;
        } else {
            blockId = FUNNEL_BLOCK;
            funnelFacing = face;
        }
        BlockDefinition def = CraftEngineBlocks.byId((Key)blockId);
        if (def == null) {
            return;
        }
        ImmutableBlockState state = def.defaultState();
        if (funnelFacing != null) {
            state = FunnelPlaceListener.withFacing(state, funnelFacing);
        }
        Location loc = target.getLocation();
        boolean placed = CraftEngineBlocks.place((Location)loc, (ImmutableBlockState)state, (int)3, (boolean)false);
        e.setCancelled(true);
        if (!placed) {
            return;
        }
        try {
            ServerLevel level = ((CraftWorld)target.getWorld()).getHandle();
            BlockPos nmsPos = new BlockPos(target.getX(), target.getY(), target.getZ());
            PersistentBlockEntity.executeAt((Level)level, nmsPos, PersistentBlockEntity::clear);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
        try {
            loc.getWorld().playSound(loc, Sound.BLOCK_COPPER_PLACE, 1.0f, 1.0f);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static ImmutableBlockState withFacing(ImmutableBlockState state, BlockFace face) {
        Property p = state.getProperty("facing");
        if (p == null) {
            return state;
        }
        try {
            Comparable value = p.valueByName(face.name().toLowerCase());
            if (value == null) {
                return state;
            }
            return ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, value);
        }
        catch (Throwable t) {
            return state;
        }
    }
}

