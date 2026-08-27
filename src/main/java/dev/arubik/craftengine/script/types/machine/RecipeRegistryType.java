package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * {@code Registry.recipes} — one property per vanilla recipe kind this addon adapts, each
 * returning a {@link RecipeCollectionType} ({@code Registry.recipes.stonecutter.for_input(id)}).
 * Singleton sentinel, same pattern as ContraptionManager/Registry itself.
 */
public final class RecipeRegistryType {

    public static final Object INSTANCE = new Object();

    private RecipeRegistryType() {}

    public static void register() {
        PolyTypeRegistry.define("RecipeRegistry")
            .property("stonecutter",    obj -> RecipeCollectionType.wrap(RecipeType.STONECUTTING))
            .property("furnace",        obj -> RecipeCollectionType.wrap(RecipeType.SMELTING))
            .property("blast_furnace",  obj -> RecipeCollectionType.wrap(RecipeType.BLASTING))
            .property("smoker",         obj -> RecipeCollectionType.wrap(RecipeType.SMOKING))
            .property("campfire",       obj -> RecipeCollectionType.wrap(RecipeType.CAMPFIRE_COOKING));
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("RecipeRegistry", INSTANCE);
    }
}
