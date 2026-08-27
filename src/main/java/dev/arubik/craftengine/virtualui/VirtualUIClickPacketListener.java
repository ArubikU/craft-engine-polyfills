package dev.arubik.craftengine.virtualui;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Click detection for VirtualUI — needed because real {@code GameMode.SPECTATOR} (see
 * {@code VirtualUICameraSystem#show}) simply never fires Bukkit's {@code PlayerInteractEvent} for
 * an empty-air click: spectators have no collision, so there's never a "target" for Bukkit's own
 * interact processing to key off of.
 *
 * <p>PRIMARY detection is now {@code INTERACT_ENTITY} targeting each session's own packet-only
 * {@code EntityType.INTERACTION} click marker (see {@link CameraSession#clickMarkerEntityId()}'s
 * doc) — kept permanently centered a short distance in front of the live camera anchor, so it's
 * ALWAYS the nearest thing along the look ray and every click's raycast is guaranteed to land on
 * it. Same technique this codebase already uses for chain-link/contraption secondary hitboxes (see
 * {@code ChainInteractPacketListener}/{@code ContraptionSecondaryHitbox}) — and the same lesson
 * applies here: a right-click on an {@code INTERACTION} entity can arrive as {@code INTERACT}
 * and/or {@code INTERACT_AT}, so BOTH must be accepted, not just one.
 *
 * <p>{@code USE_ITEM}/{@code PLAYER_BLOCK_PLACEMENT}/{@code ANIMATION} are kept as secondary,
 * redundant signals (belt-and-suspenders) — harmless duplicates collapse via the shared debounce.
 * Any OTHER {@code INTERACT_ENTITY} (one that doesn't target our own marker — a real nearby entity,
 * or one of the widget display entities themselves) is still cancelled outright without triggering
 * a click: that's what let vanilla's own "start spectating this entity" feature fire before the
 * marker existed, and the marker being the closest thing along the ray should make this branch
 * unreachable in practice, but it stays as a safety net.
 */
public final class VirtualUIClickPacketListener implements PacketListener {

    /** Dedupes the handful of packets one physical click can produce — shared with
     *  {@link VirtualUIListener#onSwing} so every path debounces against every other. */
    private static final Map<UUID, Long> LAST_CLICK = new ConcurrentHashMap<>();
    private static final long DEBOUNCE_NANOS = 150_000_000L;

    public static void register() {
        PacketEvents.getAPI().getEventManager()
                .registerListener(new VirtualUIClickPacketListener(), PacketListenerPriority.LOW);
    }

    /** Fires {@code session}'s click handling for {@code player} unless a debounced duplicate of
     *  the same physical click already fired within {@link #DEBOUNCE_NANOS}. Runs on the main
     *  thread regardless of caller — safe to call directly from a packet-receive callback. */
    static void trigger(Player player, CameraSession session) {
        long now = System.nanoTime();
        Long last = LAST_CLICK.put(player.getUniqueId(), now);
        if (last != null && now - last < DEBOUNCE_NANOS) return; // duplicate of the same physical click

        org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                () -> VirtualUIClickSystem.handleClick(session, player));
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        var type = event.getPacketType();
        boolean isRedundantClickSignal = type.equals(PacketType.Play.Client.USE_ITEM)
                || type.equals(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT)
                || type.equals(PacketType.Play.Client.ANIMATION);
        boolean isEntityInteract = type.equals(PacketType.Play.Client.INTERACT_ENTITY);
        if (!isRedundantClickSignal && !isEntityInteract) return;

        Player player = org.bukkit.Bukkit.getPlayer(event.getUser().getUUID());
        if (player == null) return;
        CameraSession session = VirtualUICameraSystem.session(player);
        if (session == null) return;

        // The player is spectating a fixed anchor while a VirtualUI is open — every click is
        // cursor input, never a real world interaction.
        event.setCancelled(true);

        if (isRedundantClickSignal) {
            trigger(player, session);
            return;
        }

        // isEntityInteract: only a click that actually targets OUR OWN marker counts — everything
        // else (a real entity, or one of the widget displays) is cancelled above and dropped here.
        try {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getEntityId() != session.clickMarkerEntityId()) return;
            var action = wrapper.getAction();
            boolean isClick = action == WrapperPlayClientInteractEntity.InteractAction.ATTACK
                    || action == WrapperPlayClientInteractEntity.InteractAction.INTERACT
                    || action == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT;
            if (isClick) trigger(player, session);
        } catch (Throwable ignored) {
            // never let click detection crash the netty decode path
        }
    }
}
