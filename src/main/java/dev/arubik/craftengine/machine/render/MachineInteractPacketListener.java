package dev.arubik.craftengine.machine.render;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Packet-level click detection for {@link MachineInteraction} markers — same technique as
 * {@code dev.arubik.craftengine.chainery.ChainInteractPacketListener}: a packet-only INTERACTION
 * entity never produces a Bukkit {@code PlayerInteractEntityEvent}, so this intercepts the raw
 * {@code INTERACT_ENTITY} packet, resolves the clicked id via {@link MachineInteraction#refOf(int)},
 * and dispatches to {@code on_renderer_interact} via the owning {@link RendererManager}'s
 * {@link RendererManager.InteractScriptRunner} on the main thread.
 */
public final class MachineInteractPacketListener implements PacketListener {

    /** Renderer-interaction hook name — distinct from the whole-block {@code on_right_click}/
     *  {@code on_left_click} hooks {@code DataMachineBlockEntity} already fires. */
    private static final String HOOK_NAME = "on_renderer_interact";

    /** Per-player last right-click nanotime — dedupes the INTERACT + INTERACT_AT pair a single
     *  right-click sends. Separate from {@code ChainInteractPacketListener}'s own debounce map. */
    private static final java.util.Map<java.util.UUID, Long> LAST_RIGHT = new java.util.concurrent.ConcurrentHashMap<>();

    public static void register() {
        PacketEvents.getAPI().getEventManager()
                .registerListener(new MachineInteractPacketListener(), PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        try {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            MachineInteraction.Ref ref = MachineInteraction.refOf(wrapper.getEntityId());
            if (ref == null) {
                return; // not one of our renderer markers — leave the packet alone
            }
            Player player = Bukkit.getPlayer(event.getUser().getUUID());
            if (player == null) {
                return;
            }
            WrapperPlayClientInteractEntity.InteractAction raw = wrapper.getAction();
            boolean attack = raw == WrapperPlayClientInteractEntity.InteractAction.ATTACK;
            // Right-click on an INTERACTION entity arrives as INTERACT_AT and/or INTERACT — accept
            // both and dedupe so the pair fires once (see ChainInteractPacketListener for why).
            boolean rightClick = raw == WrapperPlayClientInteractEntity.InteractAction.INTERACT
                    || raw == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT;
            event.setCancelled(true); // consume the click
            if (!attack && !rightClick) {
                return;
            }
            if (rightClick) {
                long now = System.nanoTime();
                Long last = LAST_RIGHT.put(player.getUniqueId(), now);
                if (last != null && now - last < 150_000_000L) {
                    return; // duplicate of the same physical right-click
                }
            }
            Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(), () -> {
                RendererManager.InteractScriptRunner runner = ref.owner() != null ? ref.owner().interactRunner() : null;
                if (runner == null || ref.onInteractRef() == null) {
                    return; // no InteractScriptRunner wired on this machine-entity class — no-op silently
                }
                try {
                    net.minecraft.server.level.ServerPlayer sp = ((CraftPlayer) player).getHandle();
                    double[] off = ref.marker() != null ? ref.marker().lastOffset() : new double[]{0, 0, 0};
                    runner.run(ref.onInteractRef(), sp, HOOK_NAME, off[0], off[1], off[2]);
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable ignored) {
            // never let a click crash the netty decode path
        }
    }
}
