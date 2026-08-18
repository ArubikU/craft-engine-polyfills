/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.EquipmentSlot
 */
package dev.arubik.craftengine.contraption.event;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

public class ContraptionInteractEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ContraptionEntity entity;
    private final BlockPos localPos;
    private final Direction face;
    private final EquipmentSlot hand;
    private final boolean rightClick;
    private final ContraptionElement element;
    private boolean cancelled;

    public ContraptionInteractEvent(Player player, ContraptionEntity entity, BlockPos localPos, Direction face, EquipmentSlot hand, boolean rightClick, ContraptionElement element) {
        this.player = player;
        this.entity = entity;
        this.localPos = localPos;
        this.face = face;
        this.hand = hand;
        this.rightClick = rightClick;
        this.element = element;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ContraptionEntity getEntity() {
        return this.entity;
    }

    public ContraptionState getState() {
        return this.entity.state();
    }

    public BlockPos getLocalPos() {
        return this.localPos;
    }

    public Direction getFace() {
        return this.face;
    }

    public EquipmentSlot getHand() {
        return this.hand;
    }

    public boolean isRightClick() {
        return this.rightClick;
    }

    public boolean isLeftClick() {
        return !this.rightClick;
    }

    public ContraptionElement getElement() {
        return this.element;
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

