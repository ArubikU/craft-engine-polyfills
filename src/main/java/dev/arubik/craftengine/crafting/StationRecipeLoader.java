package dev.arubik.craftengine.crafting;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.CraftEnginePolyfills;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@link StationRecipe}s (engineer's-workbench recipes) from reloadable JSON,
 * mirroring the machine {@code RecipeManager} pattern: files live in
 * {@code plugins/CraftEnginePolyfills/workbench_recipes/*.json}, are (re)loaded on
 * server start and whenever CraftEngine reloads. A starter set is written on first run.
 *
 * <p>JSON format (shaped):
 * <pre>{@code
 * {
 *   "tool": "demo:conveyor_blueprint",   // required tool item (whitelisted in the tool slot)
 *   "tool_uses": 1,                       // durability per craft (ignored for non-damageable tools)
 *   "pattern": ["KKK", "ICI"],            // up to 3 wide x 2 tall; ' ' = empty cell
 *   "keys": { "K": "minecraft:dried_kelp", "I": "minecraft:iron_ingot", "C": "minecraft:copper_ingot" },
 *   "outputs": [ {"id":"demo:conveyor","count":4}, {"id":"demo:brass_shavings","chance":30} ]
 * }
 * }</pre>
 */
public final class StationRecipeLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private StationRecipeLoader() {
    }

    public static synchronized void load() {
        StationRecipeRegistry registry = StationRecipeRegistry.global();
        registry.clear();

        File dir = new File(CraftEnginePolyfills.instance().getDataFolder(), "workbench_recipes");
        if (!dir.exists()) {
            dir.mkdirs();
            // Seed from the bundled resources in src/main/resources/workbench_recipes.
            for (String res : CraftEnginePolyfills.instance().listBundledResources("workbench_recipes", ".json")) {
                CraftEnginePolyfills.instance().saveDefaultResource(res);
            }
        }

        int count = loadRecursive(dir, registry);
        CraftEnginePolyfills.instance().getLogger().info("Loaded " + count + " workbench recipes.");
    }

    private static int loadRecursive(File dir, StationRecipeRegistry registry) {
        File[] files = dir.listFiles();
        if (files == null) {
            return 0;
        }
        int n = 0;
        for (File f : files) {
            if (f.isDirectory()) {
                n += loadRecursive(f, registry);
            } else if (f.getName().endsWith(".json")) {
                try (FileReader r = new FileReader(f)) {
                    JsonObject json = GSON.fromJson(r, JsonObject.class);
                    StationRecipe recipe = parse(json, f.getName().replaceAll("\\.json$", ""));
                    if (recipe != null) {
                        registry.register(recipe);
                        n++;
                    }
                } catch (Exception e) {
                    CraftEnginePolyfills.instance().getLogger()
                            .warning("Failed to load workbench recipe " + f.getName() + ": " + e.getMessage());
                }
            }
        }
        return n;
    }

    private static StationRecipe parse(JsonObject json, String name) {
        Key id = Key.of("polyfills", "station_" + name);
        StationRecipe.Builder b = StationRecipe.builder(id)
                .requiredTool(key(json.get("tool").getAsString()))
                .toolUsesPerCraft(json.has("tool_uses") ? json.get("tool_uses").getAsInt() : 1);

        CraftingRecipe.ShapedBuilder shaped = b.shaped();
        JsonArray pattern = json.getAsJsonArray("pattern");
        for (JsonElement row : pattern) {
            shaped.row(row.getAsString());
        }
        JsonObject keys = json.getAsJsonObject("keys");
        for (Map.Entry<String, JsonElement> e : keys.entrySet()) {
            char c = e.getKey().charAt(0);
            shaped.define(c, key(e.getValue().getAsString()));
        }

        JsonArray outputs = json.getAsJsonArray("outputs");
        int index = 0;
        for (JsonElement oe : outputs) {
            JsonObject o = oe.getAsJsonObject();
            int cnt = o.has("count") ? o.get("count").getAsInt() : 1;
            shaped.output(key(o.get("id").getAsString()), cnt);
            if (o.has("chance")) {
                b.chance(index, o.get("chance").getAsInt());
            }
            index++;
        }
        return b.build();
    }

    /** "ns:path" -> Key; bare "path" defaults to minecraft. */
    private static Key key(String s) {
        int i = s.indexOf(':');
        return (i < 0) ? Key.of("minecraft", s) : Key.of(s.substring(0, i), s.substring(i + 1));
    }
}
