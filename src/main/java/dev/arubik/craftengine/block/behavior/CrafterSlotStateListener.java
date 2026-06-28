package dev.arubik.craftengine.block.behavior;

import java.util.UUID;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSlotStateChange;

import dev.arubik.craftengine.block.entity.CustomCrafterBlockEntity;

/**
 * Applies crafter slot enable/disable toggles to our custom crafter block entity.
 *
 * <p>Vanilla handles this in {@code handleContainerSlotStateChanged}, but it is hard-gated on
 * {@code crafterMenu.getContainer() instanceof CrafterBlockEntity}. Our container is a
 * {@link CustomCrafterBlockEntity}, so that branch never runs. We intercept the client
 * {@code SLOT_STATE_CHANGE} packet and apply it to the BE the player has open
 * ({@link CustomCrafterBehavior#OPEN}).</p>
 */
public final class CrafterSlotStateListener implements PacketListener {

    public static void register() {
        PacketEvents.getAPI().getEventManager().registerListener(new CrafterSlotStateListener(),
                PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.SLOT_STATE_CHANGE))
            return;
        UUID uuid = event.getUser().getUUID();
        if (uuid == null)
            return;
        CustomCrafterBlockEntity be = CustomCrafterBehavior.OPEN.get(uuid);
        if (be == null)
            return;
        WrapperPlayClientSlotStateChange wrapper = new WrapperPlayClientSlotStateChange(event);
        int slot = wrapper.getSlot();
        boolean enabled = wrapper.isState();
        // Run on the main thread — BE state mutation must not happen off-tick.
        org.bukkit.Bukkit.getScheduler().runTask(
                org.bukkit.Bukkit.getPluginManager().getPlugin("CraftEnginePolyfill"),
                () -> be.setSlotState(slot, enabled));
    }
}
