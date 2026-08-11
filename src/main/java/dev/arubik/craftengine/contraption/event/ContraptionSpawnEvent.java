package dev.arubik.craftengine.contraption.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;

/**
 * Fired on the main thread the instant a {@link ContraptionEntity} is registered with
 * {@code ContraptionManager#register} — the single choke point EVERY live contraption passes
 * through, whatever brought it into existence. This is the universal "a contraption now exists
 * in the world" hook: it fires for a fresh player/redstone assemble, for a chunk-reload
 * rehydrate of a persisted contraption, for a place-from-item respawn (the chest-minecart
 * pickup/place flow), and for anything else future code routes through {@code register}
 * (e.g. a cross-world teleport that re-registers the facade in the destination world).
 *
 * <p><b>How this differs from {@link ContraptionAssembledEvent}.</b> {@code ContraptionAssembledEvent}
 * fires ONLY when a brand-new contraption is freshly assembled out of real world blocks (a player
 * hammer-strike or a redstone-driven piston/bearing) — it never fires for a rehydrate or a
 * place-from-item, because those paths don't run the capture/assemble step at all. This event fires
 * for ALL of those, including the assemble case (where BOTH fire, spawn slightly after assembled,
 * since {@code register} runs during assembly). Listen here when you want to observe every
 * contraption that becomes live regardless of origin (bookkeeping, indexing, visual attachment);
 * listen to {@code ContraptionAssembledEvent} when you specifically want only genuine fresh
 * assemblies.
 *
 * <p>Not cancellable — the contraption already exists and is registered by the time this fires.
 * The pre-assembly {@link ContraptionAssembleEvent} remains the veto point for a fresh assemble;
 * there is intentionally no veto for a rehydrate/respawn (refusing to register a persisted
 * contraption would silently lose it).
 */
public class ContraptionSpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ContraptionEntity entity;

    public ContraptionSpawnEvent(ContraptionEntity entity) {
        this.entity = entity;
    }

    /** The freshly-registered, now-live contraption (regardless of what caused the registration). */
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
