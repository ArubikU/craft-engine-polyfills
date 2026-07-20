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

    /** Per-player last right-click nanotime — dedupes the INTERACT + INTERACT_AT pair a single right-click sends. */
    private static final java.util.Map<java.util.UUID, Long> LAST_RIGHT = new java.util.concurrent.ConcurrentHashMap<>();

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
            WrapperPlayClientInteractEntity.InteractAction raw = wrapper.getAction();
            boolean attack = raw == WrapperPlayClientInteractEntity.InteractAction.ATTACK;
            // Right-click on an INTERACTION entity arrives as INTERACT_AT and/or INTERACT — accept BOTH (that's
            // why right-click "no servía": earlier code only took INTERACT and the client was sending INTERACT_AT)
            // and dedupe so the pair fires once.
            boolean rightClick = raw == WrapperPlayClientInteractEntity.InteractAction.INTERACT
                    || raw == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT;
            event.setCancelled(true); // consume the click; handlers decide what it means
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
            boolean sneak = player.isSneaking();
            ChainInteractEvent.Action action = attack ? ChainInteractEvent.Action.LEFT_CLICK
                    : ChainInteractEvent.Action.RIGHT_CLICK;
            Vector3d point = pointOf(chain, ref.segment());
            org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                    () -> {
                        if (attack) {
                            playBreakSound(player.getWorld(), chain.material.linkItem(), point);
                        } else {
                            handleRightClick(player, chain, sneak);
                        }
                        org.bukkit.Bukkit.getPluginManager()
                                .callEvent(new ChainInteractEvent(player, chain, action, ref.segment(), point));
                    });
        } catch (Throwable ignored) {
            // never let a chain click crash the netty decode path
        }
    }

    /**
     * The add/remove-links system, driven by clicking the CHAIN itself (user: "la idea es que funcione el sistema
     * de agregar y quitar eslabones"): sneak-right removes a link (refunds it, refuses if it'd over-tension),
     * right-click with chain items adds a link, otherwise just shows the current length.
     */
    private static void handleRightClick(org.bukkit.entity.Player player, Chain chain, boolean sneak) {
        if (sneak) {
            switch (ChainEngine.tryRemoveLink(chain)) {
                case REMOVED -> {
                    giveBack(player, chain.material.linkItem());
                    player.sendActionBar(net.kyori.adventure.text.Component.text(
                            "§aCadena acortada — " + chain.blocks + " eslabones"));
                }
                case WOULD_BREAK -> player.sendActionBar(net.kyori.adventure.text.Component.text(
                        "§cQuitar otra cadena la reventaría — tensión demasiado alta"));
                case AT_MIN -> player.sendActionBar(net.kyori.adventure.text.Component.text(
                        "§eLa cadena ya está al mínimo"));
            }
            return;
        }
        org.bukkit.inventory.ItemStack hand = player.getInventory().getItemInMainHand();
        if (ChaineryItemBehavior.isLink(hand, chain.material.linkItem())) {
            if (chain.blocks >= chain.material.maxBlocks()) {
                player.sendActionBar(net.kyori.adventure.text.Component.text(
                        "§eLa cadena ya está al máximo (" + chain.material.maxBlocks() + ")"));
                return;
            }
            chain.blocks += 1;
            hand.setAmount(hand.getAmount() - 1);
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§aCadena extendida — " + chain.blocks + "/" + chain.material.maxBlocks() + " eslabones"));
        } else {
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§7Cadena — " + chain.blocks + " eslabones (click con cadenas para alargar, sneak para acortar)"));
        }
    }

    /** Refunds one chain link item to the player (drops it if the inventory is full). */
    private static void giveBack(org.bukkit.entity.Player player, String linkId) {
        org.bukkit.inventory.ItemStack item = ChainEngine.linkItemStack(linkId);
        if (item == null) {
            return;
        }
        for (org.bukkit.inventory.ItemStack overflow : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
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
