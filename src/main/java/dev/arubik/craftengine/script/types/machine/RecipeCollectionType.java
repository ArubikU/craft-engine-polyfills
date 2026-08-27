package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.ArrayList;
import java.util.List;

/**
 * A single vanilla recipe TYPE's collection (stonecutting, smelting, blasting, ...) — see
 * RecipeRegistryType (bound as {@code Registry.recipes.stonecutter}/{@code .furnace}/etc).
 * Instance object is just a {@link Ref} wrapping which NMS {@code RecipeType} this collection
 * queries; {@code for_input(item_id)} does the actual lookup.
 */
public final class RecipeCollectionType {

    /** Which vanilla recipe type this collection represents. */
    public record Ref(net.minecraft.world.item.crafting.RecipeType<?> nmsType) {}

    private RecipeCollectionType() {}

    public static void register() {
        PolyTypeRegistry.define("RecipeCollection")
            // for_input(item_id) -> Array<Recipe> — every recipe of this collection's type whose
            // single ingredient slot accepts `item_id`, each wrapped the SAME "Recipe" script type
            // AbstractProcessingRecipe uses (see RecipeType.VanillaRecipeRef) so a caller (the saw's
            // own recipe-filter logic) can read `.inputs`/`.outputs` uniformly either way.
            //
            // RecipeManager in this MC version only exposes getRecipeFor (first match) and the
            // full unfiltered getRecipes() — no getRecipesFor(type, input, level) plural lookup —
            // so multiple candidates (e.g. stone -> several stonecutting outputs) means filtering
            // getRecipes() by type + a manual matches() check ourselves.
            // obj cast: original used `obj instanceof Ref ref` — treated the same as the plain-cast
            // helpers other migrated types use (e.g. ContraptionType#cl), since every instance of
            // this PolyType is always created via RecipeCollectionType#wrap. The return codec
            // DECLARES the element type — every element is a "Recipe"-wrapped VanillaRecipeRef
            // (RecipeType#wrapVanilla), so the handler returns a real List and the codec does the
            // ScriptValue.Array wrapping instead of this body building it by hand.
            .methodTyped1("for_input", TypeCodecs.STRING,
                TypeCodecs.listOf("Recipe", RecipeType.VanillaRecipeRef.class),
                List.<RecipeType.VanillaRecipeRef>of(),
                (Ref ref, String itemId) -> {
                    try {
                        MinecraftServer server = MinecraftServer.getServer();
                        if (server == null) return List.<RecipeType.VanillaRecipeRef>of();
                        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
                        if (item == null) return List.<RecipeType.VanillaRecipeRef>of();
                        ItemStack single = new ItemStack(item, 1);
                        SingleRecipeInput input = new SingleRecipeInput(single);
                        ServerLevel level = server.overworld();
                        if (level == null) return List.<RecipeType.VanillaRecipeRef>of();
                        RecipeManager rm = server.getRecipeManager();

                        List<RecipeType.VanillaRecipeRef> out = new ArrayList<>();
                        for (RecipeHolder<?> holder : rm.getRecipes()) {
                            Recipe<?> recipe = holder.value();
                            if (recipe.getType() != ref.nmsType()) continue;
                            boolean matches;
                            ItemStack result;
                            try {
                                @SuppressWarnings("unchecked")
                                Recipe<net.minecraft.world.item.crafting.RecipeInput> raw =
                                        (Recipe<net.minecraft.world.item.crafting.RecipeInput>) recipe;
                                matches = raw.matches(input, level);
                                if (!matches) continue;
                                result = raw.assemble(input, level.registryAccess());
                            } catch (Throwable ignored) { continue; }
                            if (result == null || result.isEmpty()) continue;
                            int time = recipe instanceof AbstractCookingRecipe cooking ? cooking.cookingTime() : 0;
                            RecipeType.VanillaRecipeRef vref = new RecipeType.VanillaRecipeRef(
                                    holder.id().identifier().toString(), single.copy(), result, time);
                            out.add(vref);
                        }
                        return out;
                    } catch (Throwable ignored) {
                        return List.<RecipeType.VanillaRecipeRef>of();
                    }
                });
    }

    public static ScriptValue wrap(net.minecraft.world.item.crafting.RecipeType<?> nmsType) {
        return ScriptValue.ofObj("RecipeCollection", new Ref(nmsType));
    }
}
