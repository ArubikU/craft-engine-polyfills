package dev.arubik.craftengine.script.types.chainery;

import dev.arubik.craftengine.chainery.Chain;
import dev.arubik.craftengine.chainery.ChainEngine;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import org.bukkit.Bukkit;

/**
 * "Chain" type for the PolyFill script system — a single placed CHAINERY span (see {@link Chain}).
 * Instance object: the {@link Chain} itself, the same one held by {@code ChainRegistry}.
 */
public final class ChainType {

    private ChainType() {}

    public static void register() {
        PolyTypeRegistry.define("Chain")
            .property("id", obj -> ScriptValue.of(chain(obj).id.toString()))
            .property("blocks", obj -> ScriptValue.of(chain(obj).blocks))
            .property("rest_length", obj -> ScriptValue.of(chain(obj).restLength()))
            .property("world", obj -> {
                org.bukkit.World w = Bukkit.getWorld(chain(obj).worldId);
                return w != null ? ScriptValue.of(w.getName()) : ScriptValue.NULL;
            })
            .property("a", obj -> {
                net.minecraft.core.BlockPos p = chain(obj).a;
                return VectorType.wrap(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            })
            .property("b", obj -> {
                net.minecraft.core.BlockPos p = chain(obj).b;
                return VectorType.wrap(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            })
            // Persistent per-chain data (int/string) — the same "0"/"" absent-default convention as
            // Machine/Server/World's *_flag family, backed directly by Chain's own typed NBT
            // convenience getters/setters (see Chain#getInt/getString) rather than a second store.
            // break(drop_items?) — severs this chain. drop_items defaults to false: a script-triggered
            // break didn't necessarily come with items consumed on creation, so refunding is opt-in.
            // Not migrated to methodTyped1: drop_items has a default-if-missing shape
            // (!args.isEmpty() && args.get(0).asBool()) — the default applies to the ARGUMENT when
            // absent, not to the return value, which onMissingArgs can't express. Left untyped.
            .method("break", (obj, args) -> {
                boolean drop = !args.isEmpty() && args.get(0).asBool();
                ChainEngine.breakChain(chain(obj), drop);
                return ScriptValue.of(true);
            });
    }

    public static ScriptValue wrap(Chain chain) {
        if (chain == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Chain", chain);
    }

    private static Chain chain(Object obj) { return (Chain) obj; }
}
