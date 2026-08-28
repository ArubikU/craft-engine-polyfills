package dev.arubik.craftengine.debug;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A fake player that the server actually knows about: it holds chunk tickets, ticks, and is a real
 * VIEWER, so display entities are sent to it.
 *
 * <h2>Why this and not FakePlayerUtil</h2>
 * {@code FakePlayerUtil} builds a {@code ServerPlayer} but says what it is — "NOT registered with
 * the server — create, use, discard". Such a player holds no ticket and is nobody's viewer, so it
 * loads nothing and ticks nothing. Forced chunks fix the loading half, but not the viewer half:
 * a renderer's per-player work only runs for someone the server is sending packets to. This is the
 * half that could not be faked any other way.
 *
 * <h2>What makes it real</h2>
 * {@link net.minecraft.server.players.PlayerList#placeNewPlayer} is what turns a ServerPlayer into
 * a participant, and it needs a {@link Connection}. The connection here is backed by an
 * {@link EmbeddedChannel}: a real netty channel that accepts writes and discards them, so every
 * packet the server sends this player is built and thrown away. That is the point — building them
 * is the per-viewer work being measured.
 *
 * <p>The channel's outbound queue is drained on a schedule; without that it grows for as long as
 * the player exists, which on a display-entity-heavy machine is fast. That detail is why this class
 * exists rather than being four lines inline.
 *
 * <p>This IS version-coupled, unavoidably — placeNewPlayer's signature is NMS. It fails loudly with
 * the reason rather than half-registering, and {@link #removeAll} exists because leaving one behind
 * would leave a player on the list that no human can log out.
 */
public final class FakePlayer {

    private FakePlayer() {}

    private static final Map<String, ServerPlayer> ACTIVE = new LinkedHashMap<>();
    private static final Map<String, EmbeddedChannel> CHANNELS = new LinkedHashMap<>();

    /** Spawns and registers a fake player. Returns null on success, or why it failed. */
    public static synchronized String spawn(MinecraftServer server, ServerLevel level, String name,
                                            double x, double y, double z) {
        if (ACTIVE.containsKey(name)) return "a fake player named '" + name + "' is already online";
        try {
            // A stable UUID per name, so respawning one does not accumulate distinct profiles in
            // the world's player data.
            UUID uuid = UUID.nameUUIDFromBytes(("CepFake:" + name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            GameProfile profile = new GameProfile(uuid, name);

            ServerPlayer player = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
            player.setPos(x, y, z);

            EmbeddedChannel channel = new EmbeddedChannel();
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            connection.channel = channel;
            connection.address = channel.remoteAddress();

            server.getPlayerList().placeNewPlayer(connection, player,
                    CommonListenerCookie.createInitial(profile, false));

            ACTIVE.put(name, player);
            CHANNELS.put(name, channel);
            return null;
        } catch (Throwable t) {
            return t.getClass().getSimpleName() + ": " + t.getMessage();
        }
    }

    /**
     * Drops everything the server has written to these players. Every packet was still BUILT, which
     * is the work being exercised; keeping the bytes would only grow the heap.
     */
    public static synchronized void drainOutbound() {
        for (EmbeddedChannel ch : CHANNELS.values()) {
            try {
                while (ch.readOutbound() != null) { /* discard */ }
            } catch (Throwable ignored) { }
        }
    }

    /** Disconnects every fake player. Returns how many. */
    public static synchronized int removeAll(MinecraftServer server) {
        int removed = 0;
        for (ServerPlayer p : new LinkedHashMap<>(ACTIVE).values()) {
            try {
                p.connection.disconnect(net.minecraft.network.chat.Component.literal("cep debug"));
                removed++;
            } catch (Throwable ignored) { }
        }
        ACTIVE.clear();
        CHANNELS.clear();
        return removed;
    }

    public static synchronized int count() { return ACTIVE.size(); }

    public static synchronized java.util.Set<String> names() {
        return new java.util.LinkedHashSet<>(ACTIVE.keySet());
    }
}
