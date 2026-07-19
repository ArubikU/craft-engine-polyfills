package dev.arubik.craftengine.chainery;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Extends a placed chain by right-clicking either of its endpoint blocks with chain items in hand (CHAINERY —
 * "permite extender la longitud de la chain dándole click derecho con los items de chain"). Each item adds
 * one link of SLACK: the rope's natural length grows past the endpoint gap, so it sags and behaves as a
 * hanging physical chain instead of a taut tether — up to the material's max. Consumes one item per link and
 * cancels the block-item placement that would otherwise fire.
 */
public class ChaineryInteractListener implements Listener {

    /**
     * Severs any chain whose endpoint sits at a just-broken block — via the REGISTRY endpoint index (which
     * persists in chains.dat), not the block entity, so a mine/explosion after a server restart still breaks
     * the chain even if the anchor block-entity's id didn't survive the reload ("al reiniciar el sv ... si
     * rompo un anchor la chain no se rompe"). Covers player mining and both explosion kinds.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreak(org.bukkit.event.block.BlockBreakEvent event) {
        severAt(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        for (Block b : event.blockList()) {
            severAt(b);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        for (Block b : event.blockList()) {
            severAt(b);
        }
    }

    private static void severAt(Block b) {
        try {
            Chain chain = ChainRegistry.at(b.getWorld().getUID(), new BlockPos(b.getX(), b.getY(), b.getZ()));
            if (chain != null) {
                ChainEngine.breakChain(chain, true);
            }
        } catch (Throwable ignored) {
            // never let a chain-sever failure abort the block break
        }
    }

    // LOWEST + not-ignoring-cancelled: run BEFORE CraftEngine's block-item placement so cancelling here stops
    // it placing a new chain block instead of extending (the earlier HIGH handler ran too late — "sigue sin
    // aumentar").
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block clicked = event.getClickedBlock();
        ItemStack hand = event.getItem();
        if (clicked == null || hand == null || hand.getAmount() <= 0) {
            return;
        }
        Level level = ((CraftWorld) clicked.getWorld()).getHandle();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        ChainBlockEntity be = ChaineryBlockBehavior.getAt(level, pos);
        if (be == null) {
            return;
        }
        UUID chainId = be.getChainId();
        Chain chain = chainId == null ? null : ChainRegistry.get(chainId);
        if (chain == null) {
            return;
        }
        // Only chain items of this chain's own material extend it (CE or vanilla — see isLink).
        if (!ChaineryItemBehavior.isLink(hand, chain.material.linkItem())) {
            return;
        }

        // It's an extend gesture — never let the block-item place a block from it.
        event.setCancelled(true);
        event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);

        if (chain.blocks >= chain.material.maxBlocks()) {
            event.getPlayer().sendActionBar(net.kyori.adventure.text.Component.text(
                    "§eLa cadena ya está al máximo (" + chain.material.maxBlocks() + ")"));
            return;
        }
        chain.blocks += 1;
        hand.setAmount(hand.getAmount() - 1);
        event.getPlayer().sendActionBar(net.kyori.adventure.text.Component.text(
                "§aCadena extendida — " + chain.blocks + "/" + chain.material.maxBlocks() + " eslabones (más holgura)"));
    }
}
