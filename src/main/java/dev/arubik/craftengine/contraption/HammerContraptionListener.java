package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.event.ContraptionInteractEvent;
import dev.arubik.craftengine.multiblock.HammerItems;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Handles hammer interaction with contraptions via ContraptionInteractEvent.
 *
 * <p>
 * Replaces entity-event-based hammer logic (BearingHammerListener entity handlers)
 * with contraption-raycast-based logic. Hammer + sneak on contraption = pack to item.
 *
 * <h2>Why ContraptionInteractEvent</h2>
 * ContraptionInteractionListener already raycasts player aim into contraption local space,
 * fires ContraptionInteractEvent before dispatch. Handles both entity-anchored (minecart/ghast)
 * and block-anchored contraptions uniformly via raycast, not entity detection.
 *
 * <h2>Trigger</h2>
 * Right-click contraption cell while:
 * - Holding hammer (HammerItems.isHammer)
 * - Sneaking
 * - Contraption type supports packing (canPackToItem)
 *
 * <h2>Action</h2>
 * Calls ContraptionType.toItem(), gives player packed item.
 */
public class HammerContraptionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHammerContraption(ContraptionInteractEvent event) {
        if (!event.isRightClick()) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        // Check hammer
        org.bukkit.inventory.ItemStack bukkitHand = player.getInventory().getItemInMainHand();
        Key hammer = CraftEngineItems.getCustomItemId(bukkitHand);
        if (!HammerItems.isHammer(hammer)) return;

        ContraptionEntity entity = event.getEntity();
        Key bearingType = entity.state().bearingType();
        if (bearingType == null) return;

        dev.arubik.craftengine.contraption.api.ContraptionType type = ContraptionTypeRegistry.get(bearingType);
        if (type == null || !type.canPackToItem()) return;

        event.setCancelled(true);

        // Pack to NMS item
        net.minecraft.world.level.Level level = ((org.bukkit.craftbukkit.CraftWorld)
            player.getWorld()).getHandle();

        ItemStack nmsItem = type.toItem(entity, level);
        if (nmsItem == null) {
            player.sendMessage("§cFailed to pack contraption.");
            return;
        }

        // Convert NMS → Bukkit and give to player
        org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
        java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> overflow =
            player.getInventory().addItem(bukkitItem);
        for (org.bukkit.inventory.ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.0f);
        player.sendMessage("§bContraption packed to item.");
    }
}
