package dev.arubik.craftengine.contraption.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.arubik.craftengine.contraption.ContraptionEntity;

/**
 * Fired on the main thread just AFTER a contraption has successfully assembled — the real
 * blocks have been captured/removed, the {@link ContraptionEntity} has been created and
 * registered with {@code ContraptionManager}, and its default movement behavior is attached
 * (see {@code ContraptionAssembler#assemble}/{@code #assemblePiston} and
 * {@code MinecartBearing#assemble}).
 *
 * <p>Not cancellable — the contraption already exists by the time this fires. Pair with the
 * pre-assembly {@link ContraptionAssembleEvent} if you need to veto.
 */
public class ContraptionAssembledEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ContraptionEntity entity;

    public ContraptionAssembledEvent(ContraptionEntity entity) {
        this.entity = entity;
    }

    /** The freshly-assembled, already-registered contraption. */
    public ContraptionEntity getEntity() {
        return entity;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
