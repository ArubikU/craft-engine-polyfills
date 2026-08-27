package dev.arubik.craftengine.script.types.chainery;

import dev.arubik.craftengine.chainery.Chain;
import dev.arubik.craftengine.chainery.ChainEngine;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import org.bukkit.Bukkit;

/**
 * "Chain" type for the PolyFill script system — a single placed CHAINERY span (see {@link Chain}).
 * Instance object: the {@link Chain} itself, the same one held by {@code ChainRegistry}.
 */
public final class ChainType {

    private ChainType() {}

    /** Return codec for the {@code a}/{@code b} endpoint properties — same fixed "Vector" name
     *  {@code VectorType.wrap} boxes under. */
    private static final dev.arubik.craftengine.script.PolyType.TypeCodec<org.joml.Vector3d> VECTOR_CODEC =
            TypeCodecs.polyType("Vector", org.joml.Vector3d.class);

    public static void register() {
        PolyTypeRegistry.define("Chain")
            .propertyTyped("id", TypeCodecs.STRING, (Chain c) -> c.id.toString())
            // blocks is an int; ScriptValue.of(int) already widened to of(double), so DOUBLE here is
            // the identical ScriptValue.Num.
            .propertyTyped("blocks", TypeCodecs.DOUBLE, (Chain c) -> (double) c.blocks)
            .propertyTyped("rest_length", TypeCodecs.DOUBLE, (Chain c) -> c.restLength())
            // `world` is the world's NAME (a string), not a World object; STRING encodes a Java null
            // back to ScriptValue.NULL, reproducing the old unknown-world branch exactly.
            .propertyTyped("world", TypeCodecs.STRING, (Chain c) -> {
                org.bukkit.World w = Bukkit.getWorld(c.worldId);
                return w != null ? w.getName() : null;
            })
            // VectorType.wrap(x,y,z) is ofObj("Vector", new Vector3d(x,y,z)) — one FIXED PolyType
            // name, so VECTOR_CODEC reproduces it exactly.
            .propertyTyped("a", VECTOR_CODEC, (Chain c) -> {
                net.minecraft.core.BlockPos p = c.a;
                return new org.joml.Vector3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            })
            .propertyTyped("b", VECTOR_CODEC, (Chain c) -> {
                net.minecraft.core.BlockPos p = c.b;
                return new org.joml.Vector3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            })
            // Persistent per-chain data (int/string) — the same "0"/"" absent-default convention as
            // Machine/Server/World's *_flag family, backed directly by Chain's own typed NBT
            // convenience getters/setters (see Chain#getInt/getString) rather than a second store.
            // break(drop_items?) — severs this chain. drop_items defaults to false: a script-triggered
            // break didn't necessarily come with items consumed on creation, so refunding is opt-in.
            // drop_items has a default-if-missing shape (!args.isEmpty() && args.get(0).asBool()):
            // the default applies to the ARGUMENT when absent while the body still runs, which is
            // methodTypedOpt1, not onMissingArgs.
            .methodTypedOpt1("break", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (Chain chain, Boolean drop) -> {
                    ChainEngine.breakChain(chain, drop);
                    return true;
                });
    }

    public static ScriptValue wrap(Chain chain) {
        if (chain == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Chain", chain);
    }

}
