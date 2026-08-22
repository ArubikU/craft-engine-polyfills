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
        BlockStateType.register();
        WorldType.register();
        LocationType.register();
        ContraptionType.register();   // also registers ContraptionWorld (extends World)
        ContraptionManagerType.register();
        GlueType.register();       // Glue: read the super-glue graph

        // ---- Entity hierarchy (parents before children) ------------------------
        EntityType.register();
        PlayerType.register();   // extends Entity

        // ---- Machine / structure types -----------------------------------------
        MachineType.register();
        RedstoneType.register();   // Machine.redstone port object
        IoType.register();         // Machine.io port map
        LayoutType.register();     // Machine.layout menu control
        MultiBlockType.register();
        BeltType.register();
        PipeType.register();
        WorkbenchType.register();
        NetworkType.register();
        BarsType.register();
        RecipeType.register();
        AnimationType.register();
        ServerType.register();     // Server.*_flag — global, server-wide key/value store
        ChainType.register();          // Chain — a single placed chainery span
        ChainManagerType.register();   // ChainManager — create/break/query chains directly

        // TypedKey bridge built-ins ("item", "vector", "compound") — after Item/Vector/Map are
        // registered above, since these codecs read/write those PolyType instances.
        TypedKeyBridge.registerBuiltinCustomTypes();
        TypedKeyManagerType.register();    // TypedKey.define(...) — script-defined custom types

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
            });

        // ---- Global builtins ----------------------------------------------------
        ScriptBuiltins.init();
    }

    public static void reload() {
        initialized = false;
        init();
    }
}
