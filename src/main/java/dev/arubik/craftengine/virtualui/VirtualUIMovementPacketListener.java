package dev.arubik.craftengine.virtualui;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import dev.arubik.craftengine.virtualui.model.CameraSession;
import org.bukkit.entity.Player;

/**
 * Raw-packet cursor tracking, instant position freeze, AND a rotation-cone clamp — intercepts every
 * incoming {@code PLAYER_POSITION}/{@code PLAYER_ROTATION}/{@code PLAYER_POSITION_AND_ROTATION}/
 * {@code PLAYER_FLYING} packet.
 *
 * <p>Rotation is read straight off the wire, then clamped per-axis (if {@code camera.max_yaw_degrees}/
 * {@code camera.max_pitch_degrees} > 0) to a cone centered on {@link CameraSession#openYaw()}/
 * {@link CameraSession#openPitch()} —
 * wherever the player was actually looking the instant the screen opened — before being fed to
 * {@link VirtualUICameraSystem#accumulateCursorFromRawRotation}. Unlike 's own reference
 * (which leaves rotation completely unrestricted), this keeps a player from spinning all the way
 * around while a screen is open; position, if the packet carries one, is separately REWRITTEN in
 * place back to {@code session.originalLocation()}'s X/Y/Z. Either correction (position freeze,
 * rotation clamp, or both) is applied via ONE {@link WrapperPlayClientPlayerFlying#write()} before
 * the packet is ever let through to vanilla's own movement handling.
 *
 * <p>Rewriting beats cancelling here: a cancelled {@code PlayerMoveEvent} still lets the client's
 * local prediction move for that one packet before Bukkit's own "teleport back to `from`"
 * correction arrives on a LATER tick — a visible round-trip of slip/rubber-banding every time the
 * player tries to walk (or, for rotation, a snap-back once the correction lands). Overwriting the
 * packet's own fields before vanilla ever sees them means the server never even perceives the
 * disallowed move happened, so there is nothing to correct after the fact — zero-tick, zero-slip.
 * {@code VirtualUIListener#onMove} is kept purely as a defensive backup for position (should be a
 * no-op in practice, since the position component never actually changes anymore by the time it
 * fires) — rotation has no such backstop since Bukkit exposes no analogous look-change event.
 *
 * <p>All four packet types share this one wire shape in PacketEvents' model
 * ({@link WrapperPlayClientPlayerFlying} is their common base, exposing a unified
 * {@code getLocation()} plus {@code hasRotationChanged()}/{@code hasPositionChanged()} flags), so
 * one wrapper handles every variant without needing to special-case which specific packet arrived.
 */
public final class VirtualUIMovementPacketListener implements PacketListener {

    public static void register() {
        PacketEvents.getAPI().getEventManager()
                .registerListener(new VirtualUIMovementPacketListener(), PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        var type = event.getPacketType();
        if (!(type.equals(PacketType.Play.Client.PLAYER_POSITION)
                || type.equals(PacketType.Play.Client.PLAYER_ROTATION)
                || type.equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION)
                || type.equals(PacketType.Play.Client.PLAYER_FLYING))) {
            return;
        }
        Player player = org.bukkit.Bukkit.getPlayer(event.getUser().getUUID());
        if (player == null) return;
        CameraSession session = VirtualUICameraSystem.session(player);
        if (session == null) return;

        try {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
            Location loc = wrapper.getLocation();
            float yaw = loc.getYaw(), pitch = loc.getPitch();
            boolean rewrite = false;
            boolean rotationClamped = false;

            if (wrapper.hasRotationChanged()) {
                double maxYaw = VirtualUIConfig.get().maxYawDegrees();
                double maxPitch = VirtualUIConfig.get().maxPitchDegrees();
                if (maxYaw > 0) {
                    double halfYaw = maxYaw / 2.0;
                    double devYaw = normalizeDegrees(yaw - session.openYaw());
                    if (Math.abs(devYaw) > halfYaw) {
                        yaw = (float) normalizeDegrees(session.openYaw() + Math.copySign(halfYaw, devYaw));
                        rewrite = true;
                        rotationClamped = true;
                    }
                }
                if (maxPitch > 0) {
                    double halfPitch = maxPitch / 2.0;
                    double devPitch = pitch - session.openPitch();
                    if (Math.abs(devPitch) > halfPitch) {
                        pitch = (float) (session.openPitch() + Math.copySign(halfPitch, devPitch));
                        rewrite = true;
                        rotationClamped = true;
                    }
                }
                VirtualUICameraSystem.accumulateCursorFromRawRotation(player, session, yaw, pitch);
            }

            // The rewrite above only changes what the SERVER thinks the rotation is — it never
            // tells the CLIENT its own camera got clamped, so past the cone the client keeps
            // rendering (and raycasting/clicking) from its own true, unclamped view while every
            // widget/the click marker stays pinned to the server's clamped copy: a growing
            // disconnect between "what you're looking at" and "what's actually clickable" the
            // further past the limit you push. A corrective teleport (position unchanged, rotation
            // snapped to the clamped value) forces the client's OWN view back in sync — the same
            // mechanism vanilla anti-cheat corrections use.
            //
            // shouldSendRotationCorrection is a hard debounce, not optional: a player holding their
            // look input against the cone sends a steady stream of packets that ALL exceed it, and
            // without this every one of them used to schedule its own player.teleport() — dozens per
            // second, each round-tripping a teleport+ack — which is exactly what got a real player
            // kicked with "exceeding packet rate limit" mid-session. This skips a correction whose
            // target hasn't actually changed since the last one sent (plus a flat 100ms floor).
            if (rotationClamped && session.shouldSendRotationCorrection(yaw, pitch)) {
                float finalYaw = yaw, finalPitch = pitch;
                org.bukkit.Location frozen = session.originalLocation();
                org.bukkit.Location correction = new org.bukkit.Location(
                        frozen.getWorld(), frozen.getX(), frozen.getY(), frozen.getZ(), finalYaw, finalPitch);
                org.bukkit.Bukkit.getScheduler().runTask(dev.arubik.craftengine.CraftEnginePolyfills.instance(),
                        () -> player.teleport(correction));
            }

            double x = loc.getX(), y = loc.getY(), z = loc.getZ();
            if (wrapper.hasPositionChanged()) {
                org.bukkit.Location frozen = session.originalLocation();
                x = frozen.getX(); y = frozen.getY(); z = frozen.getZ();
                rewrite = true;
            }

            if (rewrite) {
                wrapper.setLocation(new Location(x, y, z, yaw, pitch));
                wrapper.write();
            }
        } catch (Throwable ignored) {
            // never let cursor tracking/freeze/clamp crash the netty decode path
        }
    }

    private static double normalizeDegrees(double d) {
        d %= 360.0;
        if (d >= 180.0) d -= 360.0;
        if (d < -180.0) d += 360.0;
        return d;
    }
}
