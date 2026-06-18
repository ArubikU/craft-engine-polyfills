package dev.arubik.craftengine.conveyor;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Key;

/**
 * Single-item, context-aware placement for the funnel family.
 *
 * <p>One held item ({@code cml:funnel}) places the right block by the face you click:</p>
 * <ul>
 *   <li>click a block's TOP face &rarr; {@code cml:floor_funnel} on top of it (feeds DOWN);</li>
 *   <li>click a block's BOTTOM face &rarr; {@code cml:ceiling_funnel} under it (extracts from above);</li>
 *   <li>click a side face &rarr; the directional {@code cml:funnel} facing the player.</li>
 * </ul>
 *
 * <p>The {@code cml:funnel} item carries NO {@code block_item} behavior, so this listener is the
 * sole placement path (no double placement). All three blocks drop {@code cml:funnel} on break
 * (loot config), so the player never juggles three different items.</p>
 */
public class FunnelPlaceListener implements Listener {

    private static final Key FUNNEL_ITEM = Key.of("cml", "funnel");
    private static final Key FLOOR_BLOCK = Key.of("cml", "floor_funnel");
    private static final Key CEILING_BLOCK = Key.of("cml", "ceiling_funnel");
    private static final Key FUNNEL_BLOCK = Key.of("cml", "funnel");

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND)
            return;
        org.bukkit.entity.Player player = e.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Key id = CraftEngineItems.getCustomItemId(hand);
        if (id == null || !FUNNEL_ITEM.equals(id))
            return;

        Block clicked = e.getClickedBlock();
        BlockFace face = e.getBlockFace();
        if (clicked == null || face == null)
            return;

        // Let players OPEN a container they click (unless sneaking) instead of placing on it.
        if (!player.isSneaking() && clicked.getState() instanceof org.bukkit.inventory.InventoryHolder)
            return;

        Block target = clicked.getRelative(face);
        if (!ConveyorBlockEntity.isReplaceable(target))
            return;

        Key blockId;
        BlockFace funnelFacing = null;
        if (face == BlockFace.UP) {
            blockId = FLOOR_BLOCK;
        } else if (face == BlockFace.DOWN) {
            blockId = CEILING_BLOCK;
        } else {
            blockId = FUNNEL_BLOCK;
            funnelFacing = player.getFacing(); // directional: face where the player looks
        }

        BlockDefinition def = CraftEngineBlocks.byId(blockId);
        if (def == null)
            return;
        ImmutableBlockState state = def.defaultState();
        if (funnelFacing != null)
            state = withFacing(state, funnelFacing);

        Location loc = target.getLocation();
        boolean placed = CraftEngineBlocks.place(loc, state, UpdateFlags.UPDATE_ALL, false);
        e.setCancelled(true);
        if (!placed)
            return;
        // Fresh block: clear any stale persisted item from a previous occupant.
        try {
            dev.arubik.craftengine.util.CustomBlockData.from(target).clear();
        } catch (Throwable ignored) {
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
        try {
            loc.getWorld().playSound(loc, org.bukkit.Sound.BLOCK_COPPER_PLACE, 1f, 1f);
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withFacing(ImmutableBlockState state, BlockFace face) {
        Property p = state.getProperty("facing");
        if (p == null)
            return state;
        try {
            Object value = p.valueByName(face.name().toLowerCase());
            if (value == null)
                return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }
}
