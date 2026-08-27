package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A single vanilla registry's id list — see RegistryType (bound as {@code Registry.items}/{@code
 * .blocks}/{@code .biomes}/etc). Instance object is just a {@link Ref} holding a lazy supplier
 * for the underlying NMS {@link Registry}: a hardcoded one (items, blocks, ...) is available the
 * moment the JVM boots, but a DATA-DRIVEN one (biomes, enchantments, damage types, ...) only
 * exists once the server has loaded its datapacks — resolving it eagerly at script-bootstrap time
 * (before any world is even loaded) would just NPE, so every lookup re-resolves through the
 * supplier instead of caching a `Registry` reference directly.
 *
 * <p>Deliberately read-only and identity-only (ids, not full property adapters) — the addon
 * doesn't need to expose (say) a biome's temperature/precipitation, only "does this id exist" and
 * "what ids exist" for the common case of validating a config-authored id or picking one at
 * random. A position-dependent lookup (e.g. the biome AT some coordinate) belongs on the thing
 * that already carries that position — see {@code Block.biome} on BlockType — not here.
 */
public final class RegistryCollectionType {

    public record Ref(Supplier<Registry<?>> supplier) {
        Registry<?> get() { return supplier.get(); }
    }

    private RegistryCollectionType() {}

    public static void register() {
        PolyTypeRegistry.define("RegistryCollection")
            .property("size", obj -> {
                try { return ScriptValue.of(ref(obj).get().keySet().size()); }
                catch (Throwable ignored) { return ScriptValue.of(0); }
            })
            // all() -> Array<Str> — every id currently registered.
            .methodTyped0("all", TypeCodecs.RAW,
                (Ref r) -> {
                    try {
                        List<ScriptValue> out = new ArrayList<>();
                        for (Identifier id : r.get().keySet()) out.add(ScriptValue.of(id.toString()));
                        return new ScriptValue.Array(out);
                    } catch (Throwable ignored) { return new ScriptValue.Array(List.of()); }
                })
            // exists(id) -> bool
            .methodTyped1("exists", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Ref r, String id) -> {
                    try { return r.get().containsKey(Identifier.parse(id)); }
                    catch (Throwable ignored) { return false; }
                })
            // random() -> Str — a uniformly random id from this registry, NULL if it's empty or
            // not yet resolvable (e.g. a datapack registry queried before the server has loaded).
            // Return codec is RAW — dynamic NULL-or-string return, matching the original behavior.
            .methodTyped0("random", TypeCodecs.RAW,
                (Ref r) -> {
                    try {
                        List<Identifier> ids = List.copyOf(r.get().keySet());
                        if (ids.isEmpty()) return ScriptValue.NULL;
                        return ScriptValue.of(ids.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(ids.size())).toString());
                    } catch (Throwable ignored) { return ScriptValue.NULL; }
                });
    }

    public static ScriptValue wrap(Supplier<Registry<?>> supplier) {
        return ScriptValue.ofObj("RegistryCollection", new Ref(supplier));
    }

    private static Ref ref(Object obj) { return (Ref) obj; }
}
