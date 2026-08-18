/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package dev.arubik.craftengine.contraption.event;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ContraptionDisassembleEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ContraptionEntity entity;
    private boolean cancelled;

    public ContraptionDisassembleEvent(ContraptionEntity entity) {
        this.entity = entity;
    }

    public ContraptionEntity getEntity() {
        return this.entity;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

