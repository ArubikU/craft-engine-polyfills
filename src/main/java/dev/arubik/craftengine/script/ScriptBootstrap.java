package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.primitive.*;
import dev.arubik.craftengine.script.types.world.*;
import dev.arubik.craftengine.script.types.world.ContraptionManagerType;
import dev.arubik.craftengine.script.types.entity.*;
import dev.arubik.craftengine.script.types.machine.*;
import dev.arubik.craftengine.script.types.resource.*;

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
