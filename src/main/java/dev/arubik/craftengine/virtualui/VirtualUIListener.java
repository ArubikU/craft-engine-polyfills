package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.virtualui.model.CameraSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Wires the remaining real Bukkit input an open VirtualUI session needs: disconnecting
 * force-closes any open session so widget displays are never left orphaned, sneak is the universal
 * close gesture, {@link #onMove} is a defensive backstop for the position freeze, and
 * {@link #onSwing} is a backstop for left-click detection (see its own doc — the primary path is
 * now packet-level). Neither click direction is really "handled here" anymore in the primary
 * sense — {@link VirtualUIClickPacketListener} intercepts the raw packets for both directly,
 * since Bukkit has no reliable higher-level event for either in Spectator.
 */
public final class VirtualUIListener implements Listener {

    /** Position freeze backstop — the REAL freeze happens earlier and faster, in
     *  {@code VirtualUIMovementPacketListener}: it rewrites a move packet's own position fields
     *  back to {@code session.originalLocation()} before the packet ever reaches vanilla's movement
     *  handling, so by the time THIS event fires the position component should already be a no-op
     *  in practice. Kept only in case some position-changing path doesn't route through that
     *  packet listener (a plugin-triggered velocity/knockback, for instance). */
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!VirtualUICameraSystem.isOpen(event.getPlayer())) return;
        // Cancelling a PlayerMoveEvent reverts the WHOLE move — position AND yaw/pitch — back to
        // getFrom(). Rotation is intentionally never frozen (see VirtualUIMovementPacketListener),
        // so a blanket cancel here snapped the player's visual look direction back to wherever it
        // was on the last position-carrying packet every time they only turned their head. Only
        // cancel when position itself actually drifted from the frozen anchor (a knockback/velocity
        // path that didn't route through the packet-level freeze) — a pure look-around must go
        // through untouched.
        var from = event.getFrom();
        var to = event.getTo();
        if (to == null) return;
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setTo(new org.bukkit.Location(to.getWorld(), from.getX(), from.getY(), from.getZ(),
                    to.getYaw(), to.getPitch()));
        }
    }

    /** Left-click / cursor click, secondary path — the PRIMARY detection is now the raw
     *  {@code ANIMATION} packet in {@code VirtualUIClickPacketListener} (which also cancels that
     *  packet, so this Bukkit-level event won't actually fire while that cancellation is active;
     *  kept only as a backstop in case the packet type ever goes unrecognized on some server/
     *  PacketEvents version combo).  treats every swing as one unified click with no
     *  left/right distinction at all — same as {@code VirtualUIClickSystem.handleClick} here. */
    @EventHandler(ignoreCancelled = true)
    public void onSwing(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;
        Player player = event.getPlayer();
        CameraSession session = VirtualUICameraSystem.session(player);
        if (session == null) return;
        VirtualUIClickPacketListener.trigger(player, session);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (VirtualUICameraSystem.isOpen(player)) {
            // Sneak is the universal "close" gesture, matching most hologram-menu plugins' UX and
            // giving a VirtualUI without an explicit close button a guaranteed way out.
            VirtualUICameraSystem.hide(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (VirtualUICameraSystem.isOpen(event.getPlayer())) {
            VirtualUICameraSystem.hide(event.getPlayer());
        }
    }
}
