package dev.arubik.craftengine.machine.recipe.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.recipe.*;
import dev.arubik.craftengine.machine.recipe.condition.BlockBelowCondition;
import dev.arubik.craftengine.machine.recipe.condition.RecipeCondition;
import io.papermc.paper.registry.tag.TagKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;

public class RecipeManager {

    private static final Map<String, List<MachineFuelRecipe>> FUELS = new HashMap<>();
    private static final Map<String, List<AbstractProcessingRecipe>> RECIPES = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadRecipes() {
        RECIPES.clear();
        FUELS.clear();

        // DataFiles handles seeding, recursion and per-file error isolation; a malformed
        // file is reported with its name and field path and only that file is skipped.
        dev.arubik.craftengine.data.DataFiles.loadDirectory("recipes",
                (view, name) -> parseRecipe(view.raw(), name));
        dev.arubik.craftengine.data.DataFiles.loadDirectory("fuels",
                (view, name) -> parseFuel(view.raw(), name));

        CraftEnginePolyfills.instance().getLogger()
                .info("Loaded " + RECIPES.values().stream().mapToInt(List::size).sum() + " recipes.");
        CraftEnginePolyfills.instance().getLogger()
                .info("Loaded " + FUELS.values().stream().mapToInt(List::size).sum() + " fuel types.");
    }

    private static void parseFuel(JsonObject json, String filename) {
        String type = json.get("type").getAsString();
        int burnTime = json.get("burnTime").getAsInt();
        int overclockedTime = json.has("overclockedTime") ? json.get("overclockedTime").getAsInt() : 0;

        RecipeInput input = parseInput(json.getAsJsonObject("input"));
        RecipeOutput replacement = null;

        if (json.has("output")) {
            replacement = parseOutput(json.getAsJsonObject("output"));
        } else if (json.has("replacement")) {
            replacement = parseOutput(json.getAsJsonObject("replacement"));
        }

        MachineFuelRecipe recipe = new MachineFuelRecipe(input, burnTime, replacement, overclockedTime);
        FUELS.computeIfAbsent(type, k -> new ArrayList<>()).add(recipe);
    }

    private static void parseRecipe(JsonObject json, String filename) {
        String type = json.get("type").getAsString();
        int time = json.get("time").getAsInt();
        boolean fuelRequired = json.has("fuelRequired") ? json.get("fuelRequired").getAsBoolean() : true;
        boolean requireOverclocked = json.has("requireOverclocked") ? json.get("requireOverclocked").getAsBoolean()
                : false;

        List<RecipeInput> inputs = new ArrayList<>();
        List<RecipeOutput> outputs = new ArrayList<>();
        List<RecipeCondition> conditions = new ArrayList<>();

        // Inputs
        if (json.has("inputs")) {
            for (JsonElement e : json.getAsJsonArray("inputs")) {
                inputs.add(parseInput(e.getAsJsonObject()));
            }
        }

        // Outputs
        if (json.has("outputs")) {
            for (JsonElement e : json.getAsJsonArray("outputs")) {
                outputs.add(parseOutput(e.getAsJsonObject()));
            }
        }

        // Conditions
        if (json.has("conditions")) {
            for (JsonElement e : json.getAsJsonArray("conditions")) {
                conditions.add(parseCondition(e.getAsJsonObject()));
            }
        }

        AbstractProcessingRecipe recipe;
        if ("fan".equals(type)) {
            // Fan recipes carry two extra gates (gas type + process family) expressed either as
            // top-level fields or as conditions{ type: gas|process, value: ... }.
            dev.arubik.craftengine.machine.recipe.FanProcess process =
                    dev.arubik.craftengine.machine.recipe.FanProcess.NONE;
            dev.arubik.craftengine.gas.GasType reqGas = null;
            if (json.has("process"))
                process = dev.arubik.craftengine.machine.recipe.FanProcess.parse(json.get("process").getAsString());
            if (json.has("gas")) {
                String g = json.get("gas").getAsString();
                if (!"any".equalsIgnoreCase(g))
                    reqGas = GasType.valueOf(g.toUpperCase());
            }
            if (json.has("conditions")) {
                for (JsonElement e : json.getAsJsonArray("conditions")) {
                    JsonObject o = e.getAsJsonObject();
                    String ct = o.get("type").getAsString();
                    if ("process".equals(ct) || "heat".equals(ct)) {
                        process = dev.arubik.craftengine.machine.recipe.FanProcess.parse(o.get("value").getAsString());
                    } else if ("gas".equals(ct)) {
                        String g = o.get("value").getAsString();
                        reqGas = "any".equalsIgnoreCase(g) ? null : GasType.valueOf(g.toUpperCase());
                    }
                }
            }
            dev.arubik.craftengine.machine.recipe.FanRecipe fan =
                    new dev.arubik.craftengine.machine.recipe.FanRecipe(inputs, outputs, time, process, reqGas);
            RECIPES.computeIfAbsent("fan", k -> new ArrayList<>()).add(fan);
            return;
        }

        recipe = new AbstractProcessingRecipe(inputs, outputs, time);
        recipe.setFuelRequired(fuelRequired);
        recipe.setRequireOverclocked(requireOverclocked);
        recipe.setMechanical(json.has("rpm") ? json.get("rpm").getAsInt() : 0,
                json.has("su") ? json.get("su").getAsInt() : 0);
        for (RecipeCondition c : conditions) {
            recipe.addCondition(c);
        }

        RECIPES.computeIfAbsent(type, k -> new ArrayList<>()).add(recipe);
    }

    public static MachineFuelRecipe getFuel(String type, net.minecraft.world.item.ItemStack stack) {
        List<MachineFuelRecipe> fuels = FUELS.get(type);
        if (fuels == null)
            return null;
        for (MachineFuelRecipe recipe : fuels) {
            if (recipe.getInput().matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static MachineFuelRecipe getFuel(String type, FluidStack stack) {
        List<MachineFuelRecipe> fuels = FUELS.get(type);
        if (fuels == null)
            return null;
        for (MachineFuelRecipe recipe : fuels) {
            if (recipe.getInput().matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static MachineFuelRecipe getFuel(String type, GasStack stack) {
        List<MachineFuelRecipe> fuels = FUELS.get(type);
        if (fuels == null)
            return null;
        for (MachineFuelRecipe recipe : fuels) {
            if (recipe.getInput().matches(stack)) {
                return recipe;
            }
        }
        return null;
    }

    /** The item id field, which existing data files spell either `id` or `item`. */
    private static String inputId(JsonObject obj) {
        JsonElement id = obj.has("id") ? obj.get("id") : obj.get("item");
        if (id == null)
            throw new IllegalArgumentException("input needs an 'id' (or 'item')");
        return id.getAsString();
    }

    private static RecipeInput parseInput(JsonObject obj) {
        String type = obj.get("type").getAsString();
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;

        if ("item".equals(type)) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM
                    .get(Identifier.parse(inputId(obj))).get().value();
            return new ItemInput(new net.minecraft.world.item.ItemStack(item, amount), false);
        } else if ("custom_item".equals(type)) {
            return new CraftEngineItemInput(inputId(obj), amount);
        } else if ("item_tag".equals(type)) {
            String tagString = obj.get("tag").getAsString();
            net.minecraft.tags.TagKey<Item> key = (net.minecraft.tags.TagKey<Item>) net.momirealms.craftengine.bukkit.util.ItemTags
                    .getOrCreate(Key.of(tagString));
            return new TagInput(key, amount);
        } else if ("fluid".equals(type)) {
            // valueOf accepts both the legacy name ("WATER") and a namespaced id
            // ("polyfills:my_acid"), so data-defined fluids work here too.
            FluidType fType = FluidType.valueOf(inputId(obj));
            return new FluidInput(new FluidStack(fType, amount), false);
        } else if ("gas".equals(type)) {
            GasType gType = GasType.valueOf(inputId(obj));
            return new GasInput(new GasStack(gType, amount));
        }
        throw new IllegalArgumentException("Unknown input type: " + type);
    }

    private static RecipeOutput parseOutput(JsonObject obj) {
        String type = obj.get("type").getAsString();
        // `amount` may be a plain number, a vanilla number provider, or a list of them
        // summed — so an output can be "1 to 3" without inventing chance-output pairs.
        dev.arubik.craftengine.data.Amount amount = dev.arubik.craftengine.data.Amount
                .parse(dev.arubik.craftengine.data.JsonView.of(obj, "output"), "amount", 1);
        int flatAmount = amount.isConstant() ? amount.constant() : 1;
        float chance = obj.has("chance") ? obj.get("chance").getAsFloat() : 1.0f;

        if ("item".equals(type)) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM
                    .get(Identifier.parse(obj.get("id").getAsString())).get().value();
            return new ItemOutput(new net.minecraft.world.item.ItemStack(item, flatAmount), chance, amount);
        } else if ("custom_item".equals(type)) {
            String id = obj.get("id").getAsString();
            net.minecraft.world.item.ItemStack stack = ((CraftItemStack) CraftEngineItems.byId(Key.of(id))
                    .buildBukkitItem()).handle;
            stack.setCount(flatAmount);
            return new ItemOutput(stack, chance, amount);
        } else if ("gas".equals(type)) {
            GasType gType = GasType.valueOf(obj.get("id").getAsString());
            return new GasOutput(new GasStack(gType, flatAmount), chance, amount);
        } else if ("fluid".equals(type)) {
            FluidType fType = FluidType.valueOf(obj.get("id").getAsString());
            return new FluidOutput(new FluidStack(fType, flatAmount), chance, amount);
        } else if ("xp".equals(type)) {
            return new XpOutput(flatAmount, amount);
        }
        throw new IllegalArgumentException("Unknown output type: " + type);
    }

    private static RecipeCondition parseCondition(JsonObject obj) {
        String type = obj.get("type").getAsString();
        if ("block_below".equals(type)) {
            Material mat = Material.matchMaterial(obj.get("block").getAsString());
            return new BlockBelowCondition(mat);
        }
        throw new IllegalArgumentException("Unknown condition type: " + type);
    }

    public static List<AbstractProcessingRecipe> getRecipes(String type) {
        return RECIPES.getOrDefault(type, Collections.emptyList());
    }

    /** All loaded {@code fan} recipes (machine id {@code "fan"}), already cast. */
    public static List<dev.arubik.craftengine.machine.recipe.FanRecipe> getFanRecipes() {
        List<dev.arubik.craftengine.machine.recipe.FanRecipe> out = new ArrayList<>();
        for (AbstractProcessingRecipe r : RECIPES.getOrDefault("fan", Collections.emptyList())) {
            if (r instanceof dev.arubik.craftengine.machine.recipe.FanRecipe fr)
                out.add(fr);
        }
        return out;
    }

    private static void createDefaults(File root) {
        // Optional: Generate example files
    }
}
