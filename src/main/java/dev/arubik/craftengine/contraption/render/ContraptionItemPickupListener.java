/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityPickupItemEvent
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.render.ContraptionItemPickupSwarm;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class ContraptionItemPickupListener
implements Listener {
    @EventHandler(ignoreCancelled=true)
    public void onPickup(EntityPickupItemEvent event) {
        Item bukkitItem = event.getItem();
        UUID mirrorUuid = bukkitItem.getUniqueId();
        if (!ContraptionItemPickupSwarm.isMirror(mirrorUuid)) {
            return;
        }
        UUID sourceUuid = ContraptionItemPickupSwarm.sourceOf(mirrorUuid);
        if (sourceUuid == null) {
            return;
        }
        ContraptionLevel level = ContraptionItemPickupSwarm.levelOf(sourceUuid);
        ContraptionItemPickupSwarm.forgetSource(sourceUuid);
        if (level == null) {
            return;
        }
        for (Entity entity : level.getAllEntities()) {
            ItemEntity internal;
            if (!(entity instanceof ItemEntity) || !(internal = (ItemEntity)entity).getUUID().equals(sourceUuid)) continue;
            internal.discard();
            break;
        }
    }
}

