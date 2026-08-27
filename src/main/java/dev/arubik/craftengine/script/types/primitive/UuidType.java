package dev.arubik.craftengine.script.types.primitive;

import java.util.UUID;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .method("random", (obj, args) -> ScriptValue.of(UUID.randomUUID().toString()))
            .method("is_valid", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try { UUID.fromString(args.get(0).asStr()); return ScriptValue.of(true); }
                catch (IllegalArgumentException e) { return ScriptValue.of(false); }
            })
            // Uuid.to_bytes(uuid) -> base64 string of the 16 raw bytes (same encoding TypedKeyBridge
            // uses for "byte_array" everywhere, so this composes with get_typed/set_typed for free).
            .method("to_bytes", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                try {
                    UUID u = UUID.fromString(args.get(0).asStr());
                    java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(16);
                    buf.putLong(u.getMostSignificantBits());
                    buf.putLong(u.getLeastSignificantBits());
                    return ScriptValue.of(java.util.Base64.getEncoder().encodeToString(buf.array()));
                } catch (Throwable t) { return ScriptValue.of(""); }
            })
            .method("from_bytes", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                try {
                    byte[] bytes = java.util.Base64.getDecoder().decode(args.get(0).asStr());
                    if (bytes.length != 16) return ScriptValue.of("");
                    java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(bytes);
                    return ScriptValue.of(new UUID(buf.getLong(), buf.getLong()).toString());
                } catch (Throwable t) { return ScriptValue.of(""); }
            })
            // Uuid.most_bits(uuid)/least_bits(uuid) -> the two signed 64-bit halves, each exact as a
            // Num for any value that matters in practice (a script comparing/storing these, not
            // doing further bitwise math on them) — use both together with from_bits for a lossless
            // "two BIGINT columns" storage shape instead of a 36-char string column.
            .method("most_bits", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                try { return ScriptValue.of((double) UUID.fromString(args.get(0).asStr()).getMostSignificantBits()); }
                catch (Throwable t) { return ScriptValue.of(0); }
            })
            .method("least_bits", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                try { return ScriptValue.of((double) UUID.fromString(args.get(0).asStr()).getLeastSignificantBits()); }
                catch (Throwable t) { return ScriptValue.of(0); }
            })
            .method("from_bits", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of("");
                long most = (long) args.get(0).asNum();
                long least = (long) args.get(1).asNum();
                return ScriptValue.of(new UUID(most, least).toString());
            });
    }
}
