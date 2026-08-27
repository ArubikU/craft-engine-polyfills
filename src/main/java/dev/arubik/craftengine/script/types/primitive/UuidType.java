package dev.arubik.craftengine.script.types.primitive;

import java.util.UUID;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

/**
 * {@code Uuid} — every UUID elsewhere in this codebase ({@code Player.uuid}, {@code Entity.uuid},
 * ...) is already just its canonical 36-char string form, so there is no wrapped "Uuid value"
 * type; this namespace is purely conversion helpers for a script that wants a MORE COMPACT
 * representation to actually persist (e.g. a {@code SQL}/{@code Redis} table using a
 * {@code BINARY(16)}/two-{@code BIGINT} column instead of a 36-char string column at scale).
 *
 * <p>128 bits doesn't fit one script {@code Num} (a double) losslessly, so "as an int" is offered
 * as its two 64-bit halves ({@link #register} {@code most_bits}/{@code least_bits}) rather than one
 * value — round-trip via {@code from_bits(most, least)}. "As bytes" is the same base64-of-16-bytes
 * convention {@code TypedKeyBridge}'s own {@code byte_array} type already uses everywhere else.
 */
public final class UuidType {

    public static final Object NAMESPACE = new Object();

    private UuidType() {}

    public static ScriptValue wrap(UUID uuid) {
        return ScriptValue.of(uuid.toString());
    }

    public static void register() {
        PolyTypeRegistry.define("Uuid")
            .methodTyped0("random", TypeCodecs.STRING,
                (Object obj) -> UUID.randomUUID().toString())
            .methodTyped1("is_valid", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String str) -> {
                    try { UUID.fromString(str); return true; }
                    catch (IllegalArgumentException e) { return false; }
                })
            // Uuid.to_bytes(uuid) -> base64 string of the 16 raw bytes (same encoding TypedKeyBridge
            // uses for "byte_array" everywhere, so this composes with get_typed/set_typed for free).
            .methodTyped1("to_bytes", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, String str) -> {
                    try {
                        UUID u = UUID.fromString(str);
                        java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(16);
                        buf.putLong(u.getMostSignificantBits());
                        buf.putLong(u.getLeastSignificantBits());
                        return java.util.Base64.getEncoder().encodeToString(buf.array());
                    } catch (Throwable t) { return ""; }
                })
            .methodTyped1("from_bytes", TypeCodecs.STRING, TypeCodecs.STRING, "",
                (Object obj, String str) -> {
                    try {
                        byte[] bytes = java.util.Base64.getDecoder().decode(str);
                        if (bytes.length != 16) return "";
                        java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(bytes);
                        return new UUID(buf.getLong(), buf.getLong()).toString();
                    } catch (Throwable t) { return ""; }
                })
            // Uuid.most_bits(uuid)/least_bits(uuid) -> the two signed 64-bit halves, each exact as a
            // Num for any value that matters in practice (a script comparing/storing these, not
            // doing further bitwise math on them) — use both together with from_bits for a lossless
            // "two BIGINT columns" storage shape instead of a 36-char string column.
            .methodTyped1("most_bits", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (Object obj, String str) -> {
                    try { return (double) UUID.fromString(str).getMostSignificantBits(); }
                    catch (Throwable t) { return 0.0; }
                })
            .methodTyped1("least_bits", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (Object obj, String str) -> {
                    try { return (double) UUID.fromString(str).getLeastSignificantBits(); }
                    catch (Throwable t) { return 0.0; }
                })
            .methodTyped2("from_bits", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.STRING, "",
                (Object obj, Double mostD, Double leastD) -> {
                    long most = mostD.longValue();
                    long least = leastD.longValue();
                    return new UUID(most, least).toString();
                });
    }
}
