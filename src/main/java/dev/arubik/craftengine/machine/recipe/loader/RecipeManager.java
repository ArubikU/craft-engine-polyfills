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

        File dataFolder = CraftEnginePolyfills.instance().getDataFolder();

        File recipeFolder = new File(dataFolder, "recipes");
        if (!recipeFolder.exists()) {
            recipeFolder.mkdirs();
        }
        loadRecursive(recipeFolder, false);

        File fuelFolder = new File(dataFolder, "fuels");
        if (!fuelFolder.exists()) {
            fuelFolder.mkdirs();
            // createDefaults(fuelFolder);
        }
        loadRecursive(fuelFolder, true);

        CraftEnginePolyfills.instance().getLogger()
                .info("Loaded " + RECIPES.values().stream().mapToInt(List::size).sum() + " recipes.");
        CraftEnginePolyfills.instance().getLogger()
                .info("Loaded " + FUELS.values().stream().mapToInt(List::size).sum() + " fuel types.");
    }

    private static void loadRecursive(File directory, boolean isFuel) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadRecursive(file, isFuel);
            } else if (file.getName().endsWith(".json")) {
                try (FileReader reader = new FileReader(file)) {
                    JsonObject json = GSON.fromJson(reader, JsonObject.class);
                    if (isFuel) {
                        parseFuel(json, file.getName());
                    } else {
                        parseRecipe(json, file.getName());
                    }
                } catch (Exception e) {
                    CraftEnginePolyfills.instance().getLogger().log(Level.SEVERE,
                            "Failed to load " + (isFuel ? "fuel" : "recipe") + ": " + file.getName(),
                            e);
                }
            }
        }
    }

    private static void parseFuel(JsonObject json, String filename) {
        String type = json.get("type").getAsString();
        int burnTime = json.get("burnTime").getAsInt();

        RecipeInput input = parseInput(json.getAsJsonObject("input"));
        RecipeOutput replacement = null;

        if (json.has("output")) {
            replacement = parseOutput(json.getAsJsonObject("output"));
        } else if (json.has("replacement")) {
            replacement = parseOutput(json.getAsJsonObject("replacement"));
        }

        MachineFuelRecipe recipe = new MachineFuelRecipe(input, burnTime, replacement);
        FUELS.computeIfAbsent(type, k -> new ArrayList<>()).add(recipe);
    }

    private static void parseRecipe(JsonObject json, String filename) {
        String type = json.get("type").getAsString();
        int time = json.get("time").getAsInt();
        boolean fuelRequired = json.has("fuelRequired") ? json.get("fuelRequired").getAsBoolean() : true;

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

        AbstractProcessingRecipe recipe = new AbstractProcessingRecipe(inputs, outputs, time);
        recipe.setFuelRequired(fuelRequired);
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

    private static RecipeInput parseInput(JsonObject obj) {
        String type = obj.get("type").getAsString();
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;

        if ("item".equals(type)) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM
                    .get(Identifier.parse(obj.get("id").getAsString())).get().value();
            return new ItemInput(new net.minecraft.world.item.ItemStack(item, amount), false);
        } else if ("custom_item".equals(type)) {
            String id = obj.get("id").getAsString();
            return new CraftEngineItemInput(id, amount);
        } else if ("item_tag".equals(type)) {
            String tagString = obj.get("tag").getAsString();
            net.minecraft.tags.TagKey<Item> key = (net.minecraft.tags.TagKey<Item>) net.momirealms.craftengine.bukkit.util.ItemTags
                    .getOrCreate(Key.of(tagString));
            return new TagInput(key, amount);
        } else if ("fluid".equals(type)) {
            FluidType fType = FluidType.valueOf(obj.get("id").getAsString().toUpperCase());
            return new FluidInput(new FluidStack(fType, amount), false);
        }
        throw new IllegalArgumentException("Unknown input type: " + type);
    }

    private static RecipeOutput parseOutput(JsonObject obj) {
        String type = obj.get("type").getAsString();
        int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        float chance = obj.has("chance") ? obj.get("chance").getAsFloat() : 1.0f;

        if ("item".equals(type)) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM
                    .get(Identifier.parse(obj.get("id").getAsString())).get().value();
            return new ItemOutput(new net.minecraft.world.item.ItemStack(item, amount), chance);
        } else if ("custom_item".equals(type)) {
            String id = obj.get("id").getAsString();
            net.minecraft.world.item.ItemStack stack = ((CraftItemStack) CraftEngineItems.byId(Key.of(id))
                    .buildItemStack()).handle;
            stack.setCount(amount);
            return new ItemOutput(stack, chance);
        } else if ("gas".equals(type)) {
            GasType gType = GasType.valueOf(obj.get("id").getAsString().toUpperCase());
            return new GasOutput(new GasStack(gType, amount), chance);
        } else if ("xp".equals(type)) {
            return new XpOutput((float) amount);
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

    private static void createDefaults(File root) {
        // Optional: Generate example files
    }
}
