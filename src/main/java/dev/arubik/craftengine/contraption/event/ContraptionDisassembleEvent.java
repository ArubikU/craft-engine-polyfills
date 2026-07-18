package dev.arubik.craftengine.contraption.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.arubik.craftengine.contraption.ContraptionEntity;

/**
 * Fired on the main thread just BEFORE a contraption disassembles — before any teardown runs
 * (no swarm despawn, no block restore, no mini-dimension dispose has happened yet); see
 * {@code ContraptionAssembler#disassemble} and {@code MinecartBearing#disassemble}.
 *
 * <p>{@linkplain Cancellable Cancelling} this event aborts the disassembly: the contraption
 * stays live and assembled exactly as it was. When no listener cancels, behavior is identical
 * to before this event existed. The counterpart {@link ContraptionDisassembledEvent} fires
 * afterward, only when the disassembly actually goes through.
 */
public class ContraptionDisassembleEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ContraptionEntity entity;
    private boolean cancelled;

    public ContraptionDisassembleEvent(ContraptionEntity entity) {
        this.entity = entity;
    }

    /** The contraption about to be disassembled (still fully live/registered when this fires). */
    public ContraptionEntity getEntity() {
        return entity;
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
