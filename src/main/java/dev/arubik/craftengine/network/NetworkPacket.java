package dev.arubik.craftengine.network;

import dev.arubik.craftengine.script.ScriptValue;
import java.util.UUID;

/**
 * A packet sent across a network channel.
 * Carries a typed ScriptValue payload from a sender UUID.
 */
public record NetworkPacket(
    String type,        // PacketType id: "signal", "redstone", "item", "fluid", "custom", etc.
    int channel,
    UUID sender,
    ScriptValue payload
) {
    public static NetworkPacket of(String type, int channel, UUID sender, ScriptValue payload) {
        return new NetworkPacket(type, channel, sender, payload);
    }

    public static NetworkPacket signal(int channel, UUID sender, ScriptValue payload) {
        return new NetworkPacket("signal", channel, sender, payload);
    }

    public static NetworkPacket signal(int channel, UUID sender, double value) {
        return new NetworkPacket("signal", channel, sender, ScriptValue.of(value));
    }
}
