package dev.arubik.craftengine.contraption.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;

/**
 * Fired on the main thread just BEFORE a real player's held item is placed as a block INTO a
 * contraption — see {@code ContraptionInteractionListener#tryPlace}, which reaches this point when
 * a right-click wasn't consumed as an "interact with the existing captured block" and falls through
 * to the held item's real placement (vanilla {@code BlockItem#place} or CraftEngine's
 * {@code BlockItemBehavior#useOnBlock}, both dispatched against the hidden {@code ContraptionLevel}).
 * The {@link #getTargetLocalPos() target position} is the LOCAL-space cell {@code BlockPlaceContext}
 * resolved as the placement target (the clicked cell if replaceable, else its face-relative
 * neighbor), in the contraption's own frame — NOT a real-world block position.
 *
 * <p>{@linkplain Cancellable Cancelling} this event aborts the placement: {@code tryPlace} returns a
 * NON-consuming outcome, so vanilla does not consume the held item and nothing is written into the
 * contraption. When no listener cancels, behavior is byte-identical to before this event existed.
 *
 * <p>Fired once per hand attempted (main hand first, then off hand only if the main-hand placement
 * didn't consume), mirroring the real vanilla two-hand placement fallback.
 */
public class ContraptionBlockPlaceEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ContraptionEntity entity;
    private final BlockPos targetLocalPos;
    private final ItemStack item;
    private final EquipmentSlot hand;
    private boolean cancelled;

    /**
     * @param player         the real-world player placing the block
     * @param entity         the contraption the block is being placed into
     * @param targetLocalPos the LOCAL-space target cell {@code BlockPlaceContext} resolved (contraption frame)
     * @param item           a Bukkit snapshot of the held item about to be placed (never mutated by the place)
     * @param hand           the hand holding the placing item ({@link EquipmentSlot#HAND} or {@link EquipmentSlot#OFF_HAND})
     */
    public ContraptionBlockPlaceEvent(Player player, ContraptionEntity entity, BlockPos targetLocalPos,
            ItemStack item, EquipmentSlot hand) {
        this.player = player;
        this.entity = entity;
        this.targetLocalPos = targetLocalPos;
        this.item = item;
        this.hand = hand;
    }

    /** The real-world player placing the block. */
    public Player getPlayer() {
        return player;
    }

    /** The contraption the block is being placed into. */
    public ContraptionEntity getEntity() {
        return entity;
    }

    /** Kinematic/transform state of the target contraption — convenience for {@code getEntity().state()}. */
    public ContraptionState getState() {
        return entity.state();
    }

    /** The LOCAL-space target cell the placement resolved (contraption frame — NOT a real-world block). */
    public BlockPos getTargetLocalPos() {
        return targetLocalPos;
    }

    /** A Bukkit snapshot of the held item about to be placed (the block/item being placed). */
    public ItemStack getItem() {
        return item;
    }

    /** The hand holding the placing item. */
    public EquipmentSlot getHand() {
        return hand;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
