/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.advancements.criterion.ItemPredicate
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.fluid;

import com.google.gson.JsonElement;
import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.VanillaData;
import dev.arubik.craftengine.fluid.FluidInteraction;
import dev.arubik.craftengine.fluid.FluidType;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.momirealms.craftengine.core.util.Key;

public final class FluidInteractionLoader {
    private FluidInteractionLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("fluid_interactions", 200, FluidInteractionLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("fluid_interactions", FluidInteractionLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger().info("Loaded " + FluidInteraction.REGISTRY.size() + " fluid interactions.");
        }
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills") : Key.of((String)"polyfills", (String)FluidInteractionLoader.stripExtension(fileName).replace('/', '_'));
        FluidInteraction.Kind kind = view.enumValue("kind", FluidInteraction.Kind.class);
        FluidType fluid = FluidType.byName(view.string("fluid"));
        if (fluid == null) {
            throw view.error("unknown fluid '" + view.string("fluid") + "'");
        }
        ItemPredicate item = (ItemPredicate)VanillaData.parseField(ItemPredicate.CODEC, view, "item");
        Amount amount = Amount.parse(view, "amount", 0);
        ArrayList<ItemStack> results = new ArrayList<ItemStack>();
        JsonElement rawResults = view.raw().get("results");
        if (rawResults != null && rawResults.isJsonArray()) {
            int index = 0;
            for (JsonElement element : rawResults.getAsJsonArray()) {
                results.add((ItemStack)VanillaData.parse(ItemStack.CODEC, element, view.path() + " > results[" + index++ + "]"));
            }
        }
        Optional<LootItemCondition> condition = view.has("condition") ? Optional.of((LootItemCondition)VanillaData.parse(LootItemCondition.DIRECT_CODEC, view.raw().get("condition"), view.path() + " > condition")) : Optional.empty();
        FluidInteraction.REGISTRY.register(id, new FluidInteraction(kind, item, fluid, amount, results, condition, view.integer("priority", 0)));
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf(46);
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}

