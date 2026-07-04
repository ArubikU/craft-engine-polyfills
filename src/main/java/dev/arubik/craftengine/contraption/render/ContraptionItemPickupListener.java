package dev.arubik.craftengine.contraption.render;

import java.util.UUID;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Reverse leg of {@link ContraptionItemPickupSwarm}: a real player (or any vanilla
 * item-pickup-capable entity — a real hopper minecart, etc.) picking up one of that swarm's
 * real-world pickup mirrors via completely normal vanilla mechanics fires this event; the
 * corresponding INTERNAL {@link ItemEntity} still sitting inside the owning
 * {@link ContraptionLevel} is then discarded so it doesn't keep existing — otherwise the
 * item would visually/functionally still be there for the contraption's own internal systems
 * (a captured hopper) even though a real player already carried it away.
 */
public final class ContraptionItemPickupListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        Item bukkitItem = event.getItem();
        UUID mirrorUuid = bukkitItem.getUniqueId();
        if (!ContraptionItemPickupSwarm.isMirror(mirrorUuid)) {
            return; // not one of ours — ordinary item, nothing to bridge
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
            if (entity instanceof ItemEntity internal && internal.getUUID().equals(sourceUuid)) {
                internal.discard();
                break;
            }
        }
    }
}
