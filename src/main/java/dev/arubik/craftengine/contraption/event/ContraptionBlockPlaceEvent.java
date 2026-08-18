/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.contraption.event;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ContraptionBlockPlaceEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ContraptionEntity entity;
    private final BlockPos targetLocalPos;
    private final ItemStack item;
    private final EquipmentSlot hand;
    private boolean cancelled;

    public ContraptionBlockPlaceEvent(Player player, ContraptionEntity entity, BlockPos targetLocalPos, ItemStack item, EquipmentSlot hand) {
        this.player = player;
        this.entity = entity;
        this.targetLocalPos = targetLocalPos;
        this.item = item;
        this.hand = hand;
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

    public BlockPos getTargetLocalPos() {
        return this.targetLocalPos;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public EquipmentSlot getHand() {
        return this.hand;
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

