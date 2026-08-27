package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.types.primitive.MapType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * BlockState type for scripts. Wraps both NMS BlockState and CE ImmutableBlockState.
 * CE properties take priority over vanilla. Exposed as "BlockState" in scripts.
 *
 * Usage in on_property_change script:
 *   prevState.get("activated")   → "false"
 *   newState.get("activated")    → "true"
 *   newState.id                  → "polyfills:crusher"
 *   newState.properties          → Map of all properties
 */
public final class BlockStateType {

    public record BlockStateRef(BlockState nms, ImmutableBlockState ce) {}

    private BlockStateType() {}

    public static void register() {
        PolyTypeRegistry.define("BlockState")
            // id — CE id if available, else vanilla namespaced key
            .property("id", obj -> {
                BlockStateRef r = ref(obj);
                if (r.ce() != null) {
                    try { return ScriptValue.of(((BlockDefinition) r.ce().owner().value()).id().toString()); }
                    catch (Throwable ignored) {}
                }
                return ScriptValue.of(BuiltInRegistries.BLOCK.getKey(r.nms().getBlock()).toString());
            })
            .property("is_air", obj -> ScriptValue.of(ref(obj).nms().isAir()))
            // properties — Map of all property name→value (CE overrides vanilla)
            .property("properties", obj -> {
                BlockStateRef r = ref(obj);
                LinkedHashMap<String, ScriptValue> map = new LinkedHashMap<>();
                for (var prop : r.nms().getProperties()) {
                    map.put(prop.getName(), ScriptValue.of(r.nms().getValue(prop).toString()));
                }
                // CE priority: re-read each vanilla prop through BlockType.readProperty (handles CE override)
                for (var prop : r.nms().getProperties()) {
                    String ceVal = BlockType.readProperty(r.nms(), prop.getName());
                    if (ceVal != null) map.put(prop.getName(), ScriptValue.of(ceVal));
                }
                return MapType.wrap(map);
            })
            // property_names — Array of all vanilla property names (CE adds same names, so no extras needed)
            .property("property_names", obj -> {
                BlockStateRef r = ref(obj);
                List<ScriptValue> names = new ArrayList<>();
                for (var prop : r.nms().getProperties()) names.add(ScriptValue.of(prop.getName()));
                return new ScriptValue.Array(names);
            })
            // get(name) — get single property value (CE priority)
            // ScriptValue.of(String) already maps null -> NULL, so encoding a null String here
            // reproduces the original val != null ? ScriptValue.of(val) : ScriptValue.NULL exactly.
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.STRING, null,
                (BlockStateRef r, String name) -> BlockType.readProperty(r.nms(), name))
            // has(name) — check property exists
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (BlockStateRef r, String name) -> BlockType.hasProperty(r.nms(), name))
            // equals(other) — compare two BlockState wrappers. Argument stays RAW (TypeCodecs.RAW
            // is an identity passthrough) since the "other" value's real type is only known after
            // an instanceof check inside the body, exactly like the original untyped handler.
            .methodTyped1("equals", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (BlockStateRef r, ScriptValue other) -> {
                    if (!(other instanceof ScriptValue.Obj o) || !(o.instance() instanceof BlockStateRef r2)) return false;
                    return r.nms().equals(r2.nms());
                });
    }

    /** Wrap a NMS BlockState — resolves CE ImmutableBlockState automatically. */
    public static ScriptValue wrap(BlockState nms) {
        if (nms == null) return ScriptValue.NULL;
        ImmutableBlockState ce = null;
        try { ce = BlockStateUtils.getOptionalCustomBlockState(nms).orElse(null); } catch (Throwable ignored) {}
        return ScriptValue.ofObj("BlockState", new BlockStateRef(nms, ce));
    }

    private static BlockStateRef ref(Object obj) { return (BlockStateRef) obj; }
}
