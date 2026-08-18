/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Sound
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.event.ContraptionInteractEvent;
import dev.arubik.craftengine.multiblock.HammerItems;
import java.util.HashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HammerContraptionListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onHammerContraption(ContraptionInteractEvent event) {
        if (!event.isRightClick()) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        ItemStack bukkitHand = player.getInventory().getItemInMainHand();
        Key hammer = CraftEngineItems.getCustomItemId((ItemStack)bukkitHand);
        if (!HammerItems.isHammer(hammer)) {
            return;
        }
        ContraptionEntity entity = event.getEntity();
        Key bearingType = entity.state().bearingType();
        if (bearingType == null) {
            return;
        }
        ContraptionType type = ContraptionTypeRegistry.get(bearingType);
        if (type == null || !type.canPackToItem()) {
            return;
        }
        event.setCancelled(true);
        ServerLevel level = ((CraftWorld)player.getWorld()).getHandle();
        net.minecraft.world.item.ItemStack nmsItem = type.toItem(entity, (Level)level);
        if (nmsItem == null) {
            player.sendMessage("\u00a7cFailed to pack contraption.");
            return;
        }
        ItemStack bukkitItem = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nmsItem);
        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(new ItemStack[]{bukkitItem});
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.0f);
        player.sendMessage("\u00a7bContraption packed to item.");
    }
}

