package dev.arubik.craftengine.contraption.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Fired on the main thread just BEFORE a real player's click is dispatched into the captured
 * block it visually landed on inside a contraption — see
 * {@code ContraptionInteractionListener#forward} (right-click) and {@code #forwardAttack}
 * (left-click), which route a raycast-resolved click through to the block living in the hidden
 * {@code ContraptionLevel}. The {@link #getLocalPos() local position} and {@link #getFace() face}
 * are the contraption's own LOCAL-space cell/face the raycast resolved (yaw/pitch/scale correct),
 * NOT a real-world block position.
 *
 * <p>{@linkplain Cancellable Cancelling} this event skips the dispatch entirely: the click does
 * nothing (no {@code useItemOn}/{@code useWithoutItem}/{@code attack} runs, no placement fallback
 * for a right-click), exactly as if the raycast had missed. When no listener cancels, behavior is
 * byte-identical to before this event existed.
 *
 * <p>{@link #isRightClick()} distinguishes the two dispatch paths: a right-click ({@code forward})
 * may fall through to a block PLACEMENT — which fires its own {@link ContraptionBlockPlaceEvent}
 * afterward — whereas a left-click ({@code forwardAttack}) only ever runs the non-destructive
 * {@code attack} hook (captured blocks can't be mined directly).
 */
public class ContraptionInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ContraptionEntity entity;
    private final BlockPos localPos;
    private final Direction face;
    private final EquipmentSlot hand;
    private final boolean rightClick;
    private final ContraptionElement element;
    private boolean cancelled;

    /**
     * @param player     the real-world player who clicked
     * @param entity     the contraption whose captured cell was clicked
     * @param localPos   the LOCAL-space cell position the raycast resolved (contraption frame, not world)
     * @param face       the LOCAL-space face of that cell the ray hit
     * @param hand       the hand the interaction was attributed to (always {@link EquipmentSlot#HAND} —
     *                   the listener handles only the main hand to dodge Bukkit's main/off double-fire)
     * @param rightClick {@code true} for a right-click (interact/placement dispatch), {@code false} for
     *                   a left-click (non-destructive {@code attack} dispatch)
     * @param element    the resolved ContraptionElement at the hit position, or null if none matched
     */
    public ContraptionInteractEvent(Player player, ContraptionEntity entity, BlockPos localPos,
            Direction face, EquipmentSlot hand, boolean rightClick, ContraptionElement element) {
        this.player = player;
        this.entity = entity;
        this.localPos = localPos;
        this.face = face;
        this.hand = hand;
        this.rightClick = rightClick;
        this.element = element;
    }

    /** The real-world player who clicked. */
    public Player getPlayer() {
        return player;
    }

    /** The contraption whose captured cell was clicked. */
    public ContraptionEntity getEntity() {
        return entity;
    }

    /** Kinematic/transform state of the clicked contraption — convenience for {@code getEntity().state()}. */
    public ContraptionState getState() {
        return entity.state();
    }

    /** The LOCAL-space cell position the raycast resolved (contraption frame — NOT a real-world block). */
    public BlockPos getLocalPos() {
        return localPos;
    }

    /** The LOCAL-space face of the clicked cell the ray hit. */
    public Direction getFace() {
        return face;
    }

    /** The hand the interaction was attributed to (always {@link EquipmentSlot#HAND}). */
    public EquipmentSlot getHand() {
        return hand;
    }

    /** {@code true} for a right-click (interact/placement), {@code false} for a left-click (attack). */
    public boolean isRightClick() {
        return rightClick;
    }

    /** {@code true} for a left-click (attack), {@code false} for a right-click. Inverse of {@link #isRightClick()}. */
    public boolean isLeftClick() {
        return !rightClick;
    }

    /** The resolved ContraptionElement at the hit position, or {@code null} if no element matched. */
    public ContraptionElement getElement() {
        return element;
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
