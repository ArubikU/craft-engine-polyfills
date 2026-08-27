package dev.arubik.craftengine.virtualui;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import org.bukkit.entity.Player;

/**
 * Mouse-wheel widget input — port of Create's {@code ScrollInput}/{@code SelectionScrollInput}
 * gesture: hovering a {@link dev.arubik.craftengine.virtualui.model.Widget.ScrollbarWidget} or
 * {@link dev.arubik.craftengine.virtualui.model.Widget.SelectWidget} and scrolling the wheel nudges
 * its value/advances its option, no click required. Vanilla reports "scroll the hotbar" as a
 * {@code HELD_ITEM_CHANGE} packet carrying only the new absolute slot (0-8, wrapping); this derives
 * a direction from the delta against {@link CameraSession#lastHeldSlot()} and feeds
 * {@link VirtualUIClickSystem#nudgeHoveredWidget}, then CANCELS the packet outright so a
 * VirtualUI session never actually changes the player's real held slot (their hotbar hand is
 * frozen too, matching every other real-world-side-effect this system already suppresses while
 * open).
 */
public final class VirtualUIScrollPacketListener implements PacketListener {

    public static void register() {
        PacketEvents.getAPI().getEventManager()
                .registerListener(new VirtualUIScrollPacketListener(), PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.HELD_ITEM_CHANGE)) return;

        Player player = org.bukkit.Bukkit.getPlayer(event.getUser().getUUID());
        if (player == null) return;
        CameraSession session = VirtualUICameraSystem.session(player);
        if (session == null) return;

        event.setCancelled(true);

        try {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);
            int newSlot = wrapper.getSlot();
            int lastSlot = session.lastHeldSlot();
            session.setLastHeldSlot(newSlot);
            if (lastSlot < 0) return; // first packet this session — no direction to derive yet

            // Slots wrap 0-8; a single wheel notch is always a ±1 step (mod 9) — treat the SHORTER
            // wrap direction as the real one (e.g. 8 -> 0 is "+1", not "-8").
            int delta = newSlot - lastSlot;
            if (delta > 4) delta -= 9;
            if (delta < -4) delta += 9;
            if (delta == 0) return;
            int direction = delta > 0 ? 1 : -1;

            org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                    () -> VirtualUIClickSystem.nudgeHoveredWidget(session, player, direction));
        } catch (Throwable ignored) {
            // never let scroll tracking crash the netty decode path
        }
    }
}
