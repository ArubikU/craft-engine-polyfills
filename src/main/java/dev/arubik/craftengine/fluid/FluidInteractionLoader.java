package dev.arubik.craftengine.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.VanillaData;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code fluid_interactions/*.json} into {@link FluidInteraction#REGISTRY}.
 *
 * <p>
 * The {@code item}, {@code amount}, {@code results} and {@code condition} fields
 * are vanilla structures parsed with vanilla codecs, so anything a loot table or
 * predicate can express works here — item tags, data components, NBT, random
 * rolls.
 *
 * <pre>{@code
 * // Emptying a water bucket into a tank
 * { "kind": "fill",
 *   "item": { "items": "minecraft:water_bucket" },
 *   "fluid": "polyfills:water",
 *   "amount": 1000,
 *   "results": [ { "id": "minecraft:bucket" } ] }
 *
 * // Drawing experience out into a bottle, with a genuinely random yield
 * { "kind": "drain",
 *   "item": { "items": "minecraft:glass_bottle" },
 *   "fluid": "polyfills:experience",
 *   "amount": { "type": "minecraft:uniform", "min": 8, "max": 12 },
 *   "results": [ { "id": "minecraft:experience_bottle" } ] }
 *
 * // Lava reacting with a held water bucket
 * { "kind": "reaction",
 *   "item": { "items": "minecraft:water_bucket" },
 *   "fluid": "polyfills:lava",
 *   "amount": 1000,
 *   "results": [ { "id": "minecraft:bucket" }, { "id": "minecraft:obsidian" } ] }
 * }</pre>
 *
 * <p>
 * {@code priority} (default 0) breaks ties when several entries match the same
 * held item — a water bottle must beat the generic glass bottle.
 */
public final class FluidInteractionLoader {

    private FluidInteractionLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("fluid_interactions", Registries.PHASE_RECIPES, FluidInteractionLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("fluid_interactions", FluidInteractionLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + FluidInteraction.REGISTRY.size() + " fluid interactions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id")
                ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));

        FluidInteraction.Kind kind = view.enumValue("kind", FluidInteraction.Kind.class);

        FluidType fluid = FluidType.byName(view.string("fluid"));
        if (fluid == null)
            throw view.error("unknown fluid '" + view.string("fluid") + "'");

        ItemPredicate item = VanillaData.parseField(ItemPredicate.CODEC, view, "item");

        // `amount` is a vanilla number provider, or a list of them summed. See Amount.
        dev.arubik.craftengine.data.Amount amount = dev.arubik.craftengine.data.Amount.parse(view, "amount", 0);

        List<ItemStack> results = new ArrayList<>();
        JsonElement rawResults = view.raw().get("results");
        if (rawResults != null && rawResults.isJsonArray()) {
            int index = 0;
            for (JsonElement element : rawResults.getAsJsonArray())
                results.add(VanillaData.parse(ItemStack.CODEC, element,
                        view.path() + " > results[" + index++ + "]"));
        }

        Optional<LootItemCondition> condition = view.has("condition")
                ? Optional.of(VanillaData.parse(LootItemCondition.DIRECT_CODEC, view.raw().get("condition"),
                        view.path() + " > condition"))
                : Optional.empty();

        FluidInteraction.REGISTRY.register(id, new FluidInteraction(kind, item, fluid, amount, results, condition,
                view.integer("priority", 0)));
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
