package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.util.ServerFlags;

/**
 * "Server" singleton — the script-facing surface over {@link ServerFlags}: plain global
 * key/value state, scoped to neither a block ({@code Machine.*_flag}), an entity ({@code
 * Entity.*_flag}/{@code Player.*_flag}), nor a single world ({@code World.*_flag}).
 *
 * <p>Same absent-default convention as every other {@code *_flag} family (0 / ""). A cross-block
 * feature that needs to track "every X currently registered under a name" — a frequency, a team,
 * a global toggle — stores it here as one string per name and encodes/decodes that string itself
 * with {@code split}/{@code join} (see {@code ScriptFormula}); this stays a flat store on purpose
 * so it keeps being reusable instead of growing a bespoke shape per feature.
 *
 * <pre>
 *   Server.set_flag("boss_defeated", 1)
 *   Server.set_str_flag("teleporter_freq_home", "minecraft:overworld,120,64,-30;minecraft:the_nether,15,80,4")
 *   for entry in split(Server.get_str_flag("teleporter_freq_home"), ";") { ... }
 * </pre>
 */
public final class ServerType {

    public static final Object INSTANCE = new Object();

    private ServerType() {
    }

    public static void register() {
        PolyTypeRegistry.define("Server")
            .method("get_flag", (obj, args) ->
                ScriptValue.of(args.isEmpty() ? 0 : ServerFlags.getInt(args.get(0).asStr())))
            .method("set_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                ServerFlags.setInt(args.get(0).asStr(), (int) args.get(1).asNum());
                return ScriptValue.of(true);
            })
            .method("get_str_flag", (obj, args) ->
                ScriptValue.of(args.isEmpty() ? "" : ServerFlags.getStr(args.get(0).asStr())))
            .method("set_str_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                ServerFlags.setStr(args.get(0).asStr(), args.get(1).asStr());
                return ScriptValue.of(true);
            });
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("Server", INSTANCE);
    }
}
