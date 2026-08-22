package dev.arubik.craftengine.multiblock;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Immersive-Engineering-style multiblock assembly: right-click ANY block of a structure with an
 * engineer's hammer to form it. Gated cheaply on "is the held item a hammer?" so it adds no cost to
 * ordinary clicks — only when a hammer is in hand does it scan the registered multiblock schemas.
 */
public class HammerAssembleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND)
            return;
        ItemStack hand = e.getItem();
        if (hand == null || hand.getType().isAir())
            return;
        Key hammer = CraftEngineItems.getCustomItemId(hand);
        if (!HammerItems.isHammer(hammer))
            return; // cheap early-out: not a hammer -> do nothing

        Block clicked = e.getClickedBlock();
        if (clicked == null)
            return;

        Level level = ((org.bukkit.craftbukkit.CraftWorld) clicked.getWorld()).getHandle();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());

        // Fluid block tank: a hammer right-click toggles the group's window (cost = group block count).
        net.momirealms.craftengine.core.block.ImmutableBlockState ce = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (ce != null && !ce.isEmpty()) {
            dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior tank = ce.behavior()
                    .getFirst(dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.class);
            if (tank != null) {
                try {
                    int cost = tank.toggleWindowed(level, pos);
                    e.setCancelled(true);
                    clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_COPPER_HIT, 0.7f, 1.2f);
                    damageHammer(e.getPlayer(), cost);
                } catch (Throwable ignored) {
                }
                return;
            }
        }

        // Only report a failure for the multiblock the clicked block actually belongs to —
        // every other schema "failing" here is just noise.
        MultiBlockBehavior owner = null;
        for (MultiBlockBehavior beh : MultiBlockBehavior.registry()) {
            if (!beh.acceptsHammer(hammer))
                continue;
            if (beh.ownsBlockAt(level, pos))
                owner = beh;
            try {
                if (beh.tryAssemble(level, pos)) {
                    e.setCancelled(true);
                    clicked.getWorld().playSound(clicked.getLocation(),
                            org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
                    damageHammer(e.getPlayer(), beh.structureBlockCount());
                    return;
                }
            } catch (Throwable t) {
                // This used to be swallowed outright, so a bug in the matcher looked exactly like
                // a badly built structure: nothing happened and nothing was logged.
                dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                        .warning("[MultiBlock] assembly threw for " + beh.block().id() + ": " + t);
                e.getPlayer().sendMessage("\u00a7cAssembly failed: \u00a77" + t);
                e.setCancelled(true);
                return;
            }
        }

        if (owner != null) {
            String why = owner.describeFormFailure(level, pos);
            if (why != null) {
                e.getPlayer().sendMessage("\u00a7e" + owner.block().id() + " \u00a77did not assemble: \u00a7f" + why);
                e.setCancelled(true);
            }
        }
    }

    /** Spend hammer durability equal to the number of blocks assembled; break it if it runs out. */
    private static void damageHammer(org.bukkit.entity.Player player, int cost) {
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE || cost <= 0)
            return;
        ItemStack stack = player.getInventory().getItemInMainHand();
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof org.bukkit.inventory.meta.Damageable dm) || !dm.hasMaxDamage())
            return;
        int max = dm.getMaxDamage();
        int dmg = dm.getDamage() + cost;
        if (dmg >= max) {
            stack.setAmount(stack.getAmount() - 1);
            player.getInventory().setItemInMainHand(stack.getAmount() <= 0 ? null : stack);
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        } else {
            dm.setDamage(dmg);
            stack.setItemMeta(dm);
            player.getInventory().setItemInMainHand(stack);
        }
    }
}

