package dev.arubik.craftengine.chainery;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

import org.joml.Vector3d;

/**
 * Packet-level detection of clicks on a chain's {@link ChainInteraction} entities (CHAINERY — "la detección de
 * interacción también por packets"). A packet-only INTERACTION entity never produces a Bukkit
 * {@code PlayerInteractEntityEvent} (the server has no real entity under that id — same reason
 * {@code ContraptionInteractPacketDebug} exists), so this intercepts the raw {@code INTERACT_ENTITY} packet,
 * resolves the clicked id back to its chain+segment, and fires {@link ChainInteractEvent} on the main thread.
 */
public final class ChainInteractPacketListener implements PacketListener {

    public static void register() {
        PacketEvents.getAPI().getEventManager()
                .registerListener(new ChainInteractPacketListener(), PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            return;
        }
        try {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            // INTERACT_AT duplicates INTERACT for the same right-click — ignore it to fire exactly once.
            WrapperPlayClientInteractEntity.InteractAction raw = wrapper.getAction();
            if (raw == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                return;
            }
            ChainInteraction.Ref ref = ChainInteraction.refOf(wrapper.getEntityId());
            if (ref == null) {
                return; // not one of our chain links
            }
            Chain chain = ChainRegistry.get(ref.chainId());
            if (chain == null) {
                return;
            }
            org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(event.getUser().getUUID());
            if (player == null) {
                return;
            }
        boolean attack = raw == WrapperPlayClientInteractEntity.InteractAction.ATTACK;
            ChainInteractEvent.Action action = attack ? ChainInteractEvent.Action.LEFT_CLICK
                    : ChainInteractEvent.Action.RIGHT_CLICK;
            // The clicked link's centre = midpoint of its rope segment (best-effort point for the event).
            Vector3d point = pointOf(chain, ref.segment());
            event.setCancelled(true); // consume the click; handlers decide what it means
            org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                    () -> {
                        if (attack) {
                            playBreakSound(player.getWorld(), chain.material.linkItem(), point);
                        }
                        org.bukkit.Bukkit.getPluginManager()
                                .callEvent(new ChainInteractEvent(player, chain, action, ref.segment(), point));
                    });
        } catch (Throwable ignored) {
            // never let a chain click crash the netty decode path
        }
    }

    /** Plays the chain block's own break sound at {@code point} (user: "si las golpeas ... suene el break sound"). */
    private static void playBreakSound(org.bukkit.World world, String linkId, Vector3d point) {
        try {
            net.minecraft.world.level.block.state.BlockState state = ChainRenderer.resolveBaseState(linkId);
            if (state == null) {
                return;
            }
            net.minecraft.world.level.block.SoundType st = state.getSoundType();
            net.minecraft.server.level.ServerLevel nms =
                    ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
            nms.playSound(null, point.x, point.y, point.z, st.getBreakSound(),
                    net.minecraft.sounds.SoundSource.BLOCKS, st.getVolume(), st.getPitch());
        } catch (Throwable ignored) {
        }
    }

    private static Vector3d pointOf(Chain chain, int segment) {
        int n = chain.rope.particleCount();
        if (n < 2) {
            return new Vector3d(chain.a.getX() + 0.5, chain.a.getY() + 0.5, chain.a.getZ() + 0.5);
        }
        int stride = chain.rope.renderStride();
        int i0 = Math.min(segment * stride, n - 1);
        int i1 = Math.min((segment + 1) * stride, n - 1);
        Vector3d p0 = chain.rope.particle(i0);
        Vector3d p1 = chain.rope.particle(i1);
        return new Vector3d((p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0);
    }
}
