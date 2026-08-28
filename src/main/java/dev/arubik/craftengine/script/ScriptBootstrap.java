package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.primitive.*;
import dev.arubik.craftengine.script.types.world.*;
import dev.arubik.craftengine.script.types.world.ContraptionManagerType;
import dev.arubik.craftengine.script.types.entity.*;
import dev.arubik.craftengine.script.types.machine.*;
import dev.arubik.craftengine.script.types.resource.*;
import dev.arubik.craftengine.script.types.chainery.*;
import dev.arubik.craftengine.script.types.util.*;
import dev.arubik.craftengine.script.types.event.EventType;
import dev.arubik.craftengine.script.types.plugins.PluginsType;

/**
 * Initializes the PolyFill scripting system. Call once at plugin enable.
 * Registers all built-in types in correct dependency order (parents before children).
 */
public final class ScriptBootstrap {

    private static boolean initialized = false;

    private ScriptBootstrap() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        PolyTypeRegistry.clear();

        // ---- Event hierarchy (base "Event" first, subtypes extend it) -----------
        EventType.register();

        // ---- Primitive / value types (no parent) --------------------------------
        VectorType.register();
        ItemType.register();
        MapType.register();
        NbtDataType.register();

        // ---- World / block types ------------------------------------------------
        BlockType.register();
        BlockMetadataType.register();  // Block.get_metadata + its sign/container/skull/banner subtypes
        BlockStateType.register();
        WorldType.register();
        LocationType.register();
        dev.arubik.craftengine.script.types.util.ContainerType.register();   // base for ContraptionContainer, etc.
        ContraptionType.register();   // also registers ContraptionWorld (extends World)
        ContraptionManagerType.register();
        dev.arubik.craftengine.script.types.world.RegistryType.register();       // Registry.recipes.* — vanilla recipe registry access
        dev.arubik.craftengine.script.types.machine.RecipeRegistryType.register();
        dev.arubik.craftengine.script.types.machine.RecipeCollectionType.register();
        GlueType.register();       // Glue: read the super-glue graph

        // ---- Entity hierarchy (parents before children) ------------------------
        EntityType.register();
        PlayerType.register();   // extends Entity

        // ---- Machine / structure types -----------------------------------------
        MachineType.register();
        dev.arubik.craftengine.script.types.machine.renderer.MegRendererType.register();          // Machine.get_renderer(id) -> ModelEngine handle
        dev.arubik.craftengine.script.types.machine.renderer.BetterModelRendererType.register();  // Machine.get_renderer(id) -> BetterModel handle
        RedstoneType.register();   // Machine.redstone port object
        IoType.register();         // Machine.io port map
        LayoutType.register();     // Machine.layout menu control
        MultiBlockType.register();
        BeltType.register();
        dev.arubik.craftengine.script.types.machine.BeltItemType.register();
        PipeType.register();
        WorkbenchType.register();
        NetworkType.register();
        BarsType.register();
        RecipeType.register();
        dev.arubik.craftengine.script.types.machine.RecipeOutputsType.register();
        AnimationType.register();
        ServerType.register();     // Server.*_flag — global, server-wide key/value store
        dev.arubik.craftengine.script.types.cmd.CmdType.register(); // Cmd.arg(...) — /cmds invocation context
        // BukkitEvent (generic /events bridge) is registered above as part of EventType.register().
        dev.arubik.craftengine.script.types.event.EventManagerType.register(); // EventManager.register(...) — one-shot dynamic event subscriptions
        dev.arubik.craftengine.script.types.util.TaskManagerType.register();   // TaskManager.schedule/repeat(...) — deferred script execution
        dev.arubik.craftengine.script.types.util.TaskType.register();         // Task.get(...)/Task.id — data bound into a TaskManager-fired script
        dev.arubik.craftengine.script.types.menu.MenuClickType.register();    // MenuClick — bound in a script-menu's click handler
        dev.arubik.craftengine.script.types.menu.MenuType.register();         // Menu.create(...)/MenuBuilder — standalone one-shot GUI menus
        ChainType.register();          // Chain — a single placed chainery span
        ChainManagerType.register();   // ChainManager — create/break/query chains directly

        // TypedKey bridge built-ins ("item", "vector", "compound") — after Item/Vector/Map are
        // registered above, since these codecs read/write those PolyType instances.
        TypedKeyBridge.registerBuiltinCustomTypes();
        TypedKeyManagerType.register();    // TypedKey.define(...) — script-defined custom types
        dev.arubik.craftengine.script.types.util.DialogManagerType.register();    // Dialog.base(...) — Paper Dialog API port
        dev.arubik.craftengine.script.types.util.VirtualUIWidgetContextTypes.register(); // VUIWidget + VUIButton/VUIText/VUIScrollbar/... — must run before VirtualUIManagerType below evaluates any dynamic field
        dev.arubik.craftengine.script.types.util.VirtualUIManagerType.register(); // VirtualUI.screen(...) —  camera-locked cursor UI port; extends Player, must run after PlayerType above
        UuidType.register();               // Uuid.random()/to_bytes/most_bits/... — compact-storage helpers
        SQLDriverType.register();          // SQL.query/execute/get_typed/set_typed(...) — SQLDriver front
        RedisDriverType.register();        // Redis.get/set/get_typed/set_typed(...) — RedisDriver front
        PluginsType.register();            // Plugins.vault/luckperms/worldguard/worldedit/dynmap/bluemap/mythicmobs/placeholderapi

        // ---- Inventory / resource types -----------------------------------------
        InventoryType.register();
        FluidTanksType.register(); // registers both FluidTanks and GasTanks
        UpgradesType.register();

        // ---- Data component types (all Minecraft item components as PolyTypes) ---
        DataComponentTypes.register();

        // ---- Images singleton (for Images.from('id') in title expressions) --------
        PolyTypeRegistry.define("Images")
            .method("from", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String id = args.get(0).asStr();
                int shift = args.size() >= 2 ? (int) args.get(1).asNum() : -8;
                return ScriptValue.ofObj("_TitleImage", new String[]{id, String.valueOf(shift)});
            })
            // Images.shift(n) — a bare cursor shift with NO image glyph, for titles that need to
            // nudge plain text mid-string the way the ported PlayerWarps pack's own Nexo config
            // used bare %nexo_shift_n% placeholders between decorative text segments (not just
            // right before a background image). See MenuText#shiftOnly — chains this project's
            // offset_chars.yml glyphs to hit any arbitrary magnitude, not capped to one character.
            .method("shift", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                int shift = (int) args.get(0).asNum();
                return ScriptValue.ofObj("_TitleShift", shift);
            });

        // ---- Global builtins ----------------------------------------------------
        ScriptBuiltins.init();

        // Every type is registered by now, so scan the whole registry ONCE and emit exactly one
        // generated PolyClass per PolyType (see PolyClassGenerator). Doing it here — rather than
        // lazily, mid-compile — means each wrapper covers its type's FULL member set, so every
        // member gets a real generated Java method instead of only those registered before the
        // first script happened to compile against that type.
        int polyClasses = PolyClassGenerator.buildAll();
        // Reported because "did the PolyClass engine actually run?" was, until now, unanswerable
        // from a production log: neither this nor the whole-file class JIT said anything at all,
        // while the per-formula JIT logged two thousand lines. Absence of evidence read exactly
        // like evidence of absence.
        java.util.logging.Logger.getLogger("CraftEnginePolyfills").info(
                "[CEPolyfills] [PolyClass] generated " + polyClasses + " wrapper class(es) for "
                        + PolyTypeRegistry.typeNames().size() + " registered PolyType(s)");
    }

    public static void reload() {
        initialized = false;
        init();
    }

    /** The baseline namespace set every major script-firing entry point in this codebase binds by
     *  hand (Cmd execution, the generic events bridge, TaskManager, menu clicks, machine ticks,
     *  ...). Every one of these instances is a true singleton — same object on every call — so this
     *  map is built exactly ONCE at class-load instead of each of those ~15 call sites (and, worse,
     *  machine tick/action-script execution specifically — a genuine per-tick-per-machine hot path)
     *  re-allocating its own fresh {@code ScriptValue.ofObj} wrapper for every one of these on every
     *  single call. Callers merge it in via {@link ScriptContext.Builder#typedAll}. */
    private static final java.util.Map<String, ScriptValue> GLOBAL_SINGLETONS = buildGlobalSingletons();

    private static java.util.Map<String, ScriptValue> buildGlobalSingletons() {
        java.util.LinkedHashMap<String, ScriptValue> m = new java.util.LinkedHashMap<>();
        m.put("Server", ScriptValue.ofObj("Server", dev.arubik.craftengine.script.types.world.ServerType.INSTANCE));
        m.put("Item", ScriptValue.ofObj("Item", dev.arubik.craftengine.script.types.primitive.ItemType.NAMESPACE));
        m.put("Menu", ScriptValue.ofObj("Menu", dev.arubik.craftengine.script.types.menu.MenuType.INSTANCE));
        m.put("EventManager", ScriptValue.ofObj("EventManager", dev.arubik.craftengine.script.types.event.EventManagerType.INSTANCE));
        m.put("TaskManager", ScriptValue.ofObj("TaskManager", dev.arubik.craftengine.script.types.util.TaskManagerType.INSTANCE));
        m.put("Dialog", ScriptValue.ofObj("Dialog", dev.arubik.craftengine.script.types.util.DialogManagerType.INSTANCE));
        m.put("VirtualUI", ScriptValue.ofObj("VirtualUI", dev.arubik.craftengine.script.types.util.VirtualUIManagerType.INSTANCE));
        m.put("SQL", ScriptValue.ofObj("SQL", dev.arubik.craftengine.script.types.util.SQLDriverType.INSTANCE));
        m.put("Redis", ScriptValue.ofObj("Redis", dev.arubik.craftengine.script.types.util.RedisDriverType.INSTANCE));
        m.put("Uuid", ScriptValue.ofObj("Uuid", dev.arubik.craftengine.script.types.primitive.UuidType.NAMESPACE));
        m.put("Plugins", ScriptValue.ofObj("Plugins", PluginsType.INSTANCE));
        m.put("Images", ScriptValue.ofObj("Images", "images_singleton"));
        // Extra globals machine scripts specifically also bind (see DataMachineBlockEntity /
        // DataMultiBlockMachineBlockEntity) — folded into the same shared map since these are
        // equally real singletons, so every caller gets the full set for free.
        m.put("ContraptionManager", ScriptValue.ofObj("ContraptionManager", dev.arubik.craftengine.script.types.world.ContraptionManagerType.INSTANCE));
        m.put("Registry", ScriptValue.ofObj("Registry", dev.arubik.craftengine.script.types.world.RegistryType.INSTANCE));
        m.put("ChainManager", ScriptValue.ofObj("ChainManager", dev.arubik.craftengine.script.types.chainery.ChainManagerType.INSTANCE));
        m.put("TypedKey", ScriptValue.ofObj("TypedKey", dev.arubik.craftengine.script.types.util.TypedKeyManagerType.INSTANCE));
        m.put("Glue", ScriptValue.ofObj("Glue", dev.arubik.craftengine.script.types.world.GlueType.INSTANCE));
        return java.util.Collections.unmodifiableMap(m);
    }

    /** Shared, precomputed-once global singleton bindings (Server, Item, Menu, EventManager,
     *  TaskManager, Dialog, VirtualUI, SQL, Redis, Uuid, Plugins, Images, ContraptionManager,
     *  Registry, ChainManager, TypedKey, Glue) — merge into any {@link ScriptContext.Builder} via
     *  {@code .typedAll(ScriptBootstrap.globalSingletons())} instead of hand-rolling the same
     *  {@code .typed(...)} chain. */
    public static java.util.Map<String, ScriptValue> globalSingletons() {
        return GLOBAL_SINGLETONS;
    }

    /** The same singletons, pre-built as a context so a caller can LAYER them under its own
     *  bindings instead of copying eighteen entries in per machine per tick. Built once; the map is
     *  a constant and a ScriptContext is immutable. */
    private static volatile ScriptContext GLOBAL_SINGLETONS_CTX;

    public static ScriptContext globalSingletonsContext() {
        ScriptContext c = GLOBAL_SINGLETONS_CTX;
        if (c == null) {
            c = ScriptContext.builder().typedAll(GLOBAL_SINGLETONS).build();
            GLOBAL_SINGLETONS_CTX = c;
        }
        return c;
    }

    /** The baseline namespace set every major script-firing entry point in this codebase already
     *  binds — see {@link #globalSingletons()}. Used by {@link ScriptRegistry}'s {@code __init__}/
     *  {@code __unload__} lifecycle hooks, which fire with no other natural "caller context" (no
     *  player, no menu, no command) to inherit bindings from. */
    public static ScriptContext commonContext() {
        return ScriptContext.builder().typedAll(GLOBAL_SINGLETONS).build();
    }
}
