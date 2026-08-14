package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code workbenches/*.json} into {@link WorkbenchDefinition#REGISTRY}.
 *
 * <p>
 * A workbench file declares only its UI and its footprint; recipes are separate
 * files that name this id.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:forge",
 *   "title": "<gold>Forge",
 *   "size": 54,                     // container slots, must be a multiple of 9
 *   "grid": { "width": 3, "height": 2 },
 *   "slots": {
 *     "input":  [21, 22, 23, 30, 31, 32],   // in reading order, mapped onto the grid
 *     "output": [25],
 *     "tool":   [19],
 *     "custom": [10],
 *     "render": [ { "slot": 19, "position": [4, 13.5, 4], "scale": 0.5 } ]
 *   },
 *   "structure": { "type": "horizontal_double" }
 *   // or  "structure": { "type": "multiblock", "multiblock": "polyfills:industrial_smelter" }
 *   // or  "structure": { "type": "single" }
 * }
 * }</pre>
 *
 * <p>
 * {@code input} is listed in reading order and mapped onto the grid row by row,
 * so its length must equal {@code width * height}. Every slot not otherwise named
 * is background.
 */
public final class WorkbenchLoader {

    private WorkbenchLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("workbenches", Registries.PHASE_DEFINITIONS, WorkbenchLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("workbenches", WorkbenchLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + WorkbenchDefinition.REGISTRY.size() + " workbench definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));

        int size = view.rangedInt("size", 54, 9, 54);
        if (size % 9 != 0)
            throw view.error("size must be a multiple of 9, found " + size);

        JsonView grid = view.has("grid") ? view.object("grid") : null;
        int width = grid == null ? 3 : grid.rangedInt("width", 3, 1, 9);
        int height = grid == null ? 3 : grid.rangedInt("height", 3, 1, 6);

        JsonView slots = view.has("slots") ? view.object("slots") : view;
        List<Integer> inputs = slots.intList("input");
        List<Integer> outputs = slots.intList("output");
        List<Integer> tools = slots.intList("tool");
        List<Integer> customs = slots.intList("custom");


        if (!inputs.isEmpty() && inputs.size() != width * height)
            throw view.error("'input' lists " + inputs.size() + " slots but the grid is "
                    + width + "x" + height + " (" + (width * height) + ")");

        SlotLayout.Builder builder = SlotLayout.builder(size).grid(width, height);
        for (int i = 0; i < inputs.size(); i++) {
            int slot = requireInRange(view, inputs.get(i), size, "input");
            builder.input(slot, i % width, i / width);
        }
        for (int slot : outputs)
            builder.output(requireInRange(view, slot, size, "output"));
        // A tool is a CUSTOM slot to the menu framework — not a grid ingredient — but
        // the definition remembers which ones they are so recipes can demand a tool.
        for (int slot : tools)
            builder.custom(requireInRange(view, slot, size, "tool"));
        for (int slot : customs)
            builder.custom(requireInRange(view, slot, size, "custom"));

        WorkbenchDefinition.Structure structure = WorkbenchDefinition.Structure.SINGLE;
        Key multiblock = null;
        if (view.has("structure")) {
            JsonView structureView = view.object("structure");
            structure = structureView.enumValue("type", WorkbenchDefinition.Structure.class,
                    WorkbenchDefinition.Structure.SINGLE);
            if (structure == WorkbenchDefinition.Structure.MULTIBLOCK) {
                if (!structureView.has("multiblock"))
                    throw structureView.error("structure type 'multiblock' needs a 'multiblock' id");
                multiblock = structureView.key("multiblock", "polyfills");
            }
        }

        // Each render entry carries the same params as an item renderer, minus the item.
        List<WorkbenchDefinition.RenderSlot> renderSlots = new ArrayList<>();
        for (JsonView entry : slots.objectList("render")) {
            int slot = requireInRange(view, entry.integer("slot"), size, "render.slot");
            renderSlots.add(new WorkbenchDefinition.RenderSlot(slot,
                    vec(entry, "position", new float[] { 4f, 13.5f, 4f }),
                    vec(entry, "rotation", new float[] { 0f, 0f, 0f }),
                    entry.floating("scale", 0.375f),
                    // A horizontal-double station has two cells; `half: right` draws on the
                    // second one. Ignored by single-block and multiblock stations.
                    "right".equalsIgnoreCase(entry.string("half", "master"))));
        }

        // ---- RendererSpec system (new format) ----------------------------------------
        // Parse "variables" block — a named map of VariableSpec entries.
        // Values may be a plain string (inline PolyFormula expression) or an object (typed spec).
        Map<String, dev.arubik.craftengine.machine.render.variable.VariableSpec> variables = new LinkedHashMap<>();
        if (view.has("variables")) {
            com.google.gson.JsonObject varObj = view.raw().get("variables").getAsJsonObject();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : varObj.entrySet()) {
                String varName = entry.getKey();
                com.google.gson.JsonElement rawVal = entry.getValue();
                dev.arubik.craftengine.machine.render.variable.VariableSpec spec;
                if (rawVal.isJsonPrimitive() && rawVal.getAsJsonPrimitive().isString()) {
                    spec = new dev.arubik.craftengine.machine.render.variable.VariableSpec.Formula(
                            rawVal.getAsString());
                } else if (rawVal.isJsonObject()) {
                    JsonView varView = JsonView.of(rawVal.getAsJsonObject(),
                            view.path() + " > variables > " + varName);
                    String varType = varView.string("type", "bool");
                    spec = switch (varType) {
                        case "bool" -> varView.has("source")
                                ? new dev.arubik.craftengine.machine.render.variable.VariableSpec.BoolSource(
                                        varView.string("source"))
                                : new dev.arubik.craftengine.machine.render.variable.VariableSpec.BoolExpr(
                                        varView.string("expr"));
                        case "num" -> new dev.arubik.craftengine.machine.render.variable.VariableSpec.NumExpr(
                                varView.string("expr"));
                        case "item_slot" -> new dev.arubik.craftengine.machine.render.variable.VariableSpec.ItemSlot(
                                varView.integer("slot"));
                        default -> new dev.arubik.craftengine.machine.render.variable.VariableSpec.BoolSource("always");
                    };
                } else {
                    continue;
                }
                variables.put(varName, spec);
            }
        }

        // Parse "renderers" array — an ordered list of RendererSpec entries.
        List<dev.arubik.craftengine.machine.render.RendererSpec> renderers = new ArrayList<>();
        for (JsonView r : view.objectList("renderers")) {
            String rType = r.string("type", "item_display");
            String whenExpr = r.raw().has("when") ? parseWhen(r.raw().get("when")) : "always";
            String updateWhen = r.string("update_when", "always");
            String scriptRef = r.string("run", null);
            dev.arubik.craftengine.machine.render.RendererSpec spec = switch (rType) {
                case "bettermodel" -> new dev.arubik.craftengine.machine.render.RendererSpec.BetterModelSpec(
                        r.string("model_id"),
                        r.string("animation", null),
                        r.string("speed", null),
                        whenExpr, updateWhen, scriptRef);
                case "modelengine" -> new dev.arubik.craftengine.machine.render.RendererSpec.ModelEngineSpec(
                        r.string("model_id"),
                        r.string("animation", null),
                        r.string("speed", null),
                        whenExpr, updateWhen, scriptRef);
                case "item_display" -> {
                    float[] rot3w = parseFloatArray(r, "rotation", 3, new float[]{0f, 0f, 0f});
                    String rxw = r.raw().has("rot_x") ? r.string("rot_x") : String.valueOf(rot3w[0]);
                    String ryw = r.raw().has("rot_y") ? r.string("rot_y") : String.valueOf(rot3w[1]);
                    String rzw = r.raw().has("rot_z") ? r.string("rot_z") : String.valueOf(rot3w[2]);
                    String scw = r.raw().has("scale") && r.raw().get("scale").isJsonPrimitive()
                            && r.raw().get("scale").getAsJsonPrimitive().isString()
                            ? r.string("scale") : String.valueOf(r.floating("scale", 1.0f));
                    yield new dev.arubik.craftengine.machine.render.RendererSpec.ItemDisplaySpec(
                            r.string("item"),
                            parseLocationExpr(r),
                            scw, rxw, ryw, rzw,
                            r.string("billboard", "none"), whenExpr, updateWhen, scriptRef);
                }
                case "particle" -> {
                    int interval = r.rangedInt("interval", 1, 1, 200);
                    yield new dev.arubik.craftengine.machine.render.RendererSpec.ParticleSpec(
                            r.string("particle", "SMOKE_NORMAL"),
                            r.string("count", "1"),
                            parseLocationExpr(r),
                            r.string("spread_x", "0"), r.string("spread_y", "0"), r.string("spread_z", "0"),
                            r.string("speed", "0.05"),
                            r.string("dir_x", "0"), r.string("dir_y", "0"), r.string("dir_z", "0"),
                            r.string("shape", "point"),
                            r.string("direction_mode", "random"),
                            interval, whenExpr, updateWhen, scriptRef);
                }
                case "fluid_tank", "gas_tank" -> new dev.arubik.craftengine.machine.render.RendererSpec.FluidTankSpec(
                        r.string("tank", ""),
                        "gas_tank".equals(rType),
                        parseLocationExpr(r),
                        r.floating("max_height", 0.875f),
                        whenExpr, updateWhen, scriptRef);
                case "text_display" -> {
                    float[] rot = parseFloatArray(r, "rotation", 3, new float[]{0f, 0f, 0f});
                    String rx = r.raw().has("rot_x") ? r.string("rot_x") : String.valueOf(rot[0]);
                    String ry = r.raw().has("rot_y") ? r.string("rot_y") : String.valueOf(rot[1]);
                    String rz = r.raw().has("rot_z") ? r.string("rot_z") : String.valueOf(rot[2]);
                    String sc = r.raw().has("scale") ? r.string("scale") : "0.1";
                    yield new dev.arubik.craftengine.machine.render.RendererSpec.TextDisplaySpec(
                            r.string("text", ""),
                            parseLocationExpr(r),
                            sc, rx, ry, rz,
                            r.string("billboard", "center"),
                            r.rangedInt("line_width", 200, 1, 1000),
                            r.string("background", "0"),
                            r.bool("shadow", false),
                            r.bool("see_through", false),
                            r.string("alignment", "center"),
                            r.rangedInt("opacity", 255, 0, 255),
                            whenExpr, updateWhen, scriptRef);
                }
                case "sound" -> new dev.arubik.craftengine.machine.render.RendererSpec.SoundSpec(
                        r.string("sound", "minecraft:block.note_block.pling"),
                        readExprPrimitive(r, "volume", "1.0"),
                        readExprPrimitive(r, "pitch", "1.0"),
                        r.rangedInt("interval", 20, 0, 6000),
                        whenExpr, updateWhen, scriptRef);
                case "armor_stand" -> new dev.arubik.craftengine.machine.render.RendererSpec.ArmorStandSpec(
                        r.string("head_item", null),
                        r.string("body_item", null),
                        parseLocationExpr(r),
                        readExprPrimitive(r, "rot_x"), readExprPrimitive(r, "rot_y"),
                        readExprPrimitive(r, "rot_z"),
                        r.bool("small", false), r.bool("invisible", true), r.bool("marker", true),
                        whenExpr, updateWhen, scriptRef);
                case "block_display" -> new dev.arubik.craftengine.machine.render.RendererSpec.BlockDisplaySpec(
                        r.string("block", "minecraft:stone"),
                        parseLocationExpr(r),
                        readExprPrimitive(r, "scale", "1.0"),
                        readExprPrimitive(r, "rot_x"), readExprPrimitive(r, "rot_y"),
                        readExprPrimitive(r, "rot_z"),
                        whenExpr, updateWhen, scriptRef);
                default -> null;
            };
            if (spec != null) {
                String posExpr = r.raw().has("positions") ? r.string("positions")
                               : r.raw().has("locations") ? r.string("locations") : null;
                if (posExpr != null) {
                    spec = new dev.arubik.craftengine.machine.render.RendererSpec.PositionedSpec(spec, posExpr, null);
                }
                renderers.add(spec);
            }
        }

        // Migration: if the JSON has legacy slots.render but no explicit renderers block,
        // auto-convert each render entry to an ItemDisplaySpec + ItemSlot variable.
        if (renderers.isEmpty() && !renderSlots.isEmpty()) {
            for (WorkbenchDefinition.RenderSlot rs : renderSlots) {
                String varName = "slot_" + rs.slot() + "_item";
                variables.putIfAbsent(varName,
                        new dev.arubik.craftengine.machine.render.variable.VariableSpec.ItemSlot(rs.slot()));
                // Encode the legacy [x, y, z] position as a locationExpr array string.
                String legacyLoc = "[" + rs.position()[0] + ", " + rs.position()[1] + ", " + rs.position()[2] + "]";
                renderers.add(new dev.arubik.craftengine.machine.render.RendererSpec.ItemDisplaySpec(
                        "$" + varName,
                        legacyLoc,
                        String.valueOf(rs.scale()),
                        String.valueOf(rs.rotation()[0]), String.valueOf(rs.rotation()[1]), String.valueOf(rs.rotation()[2]),
                        "none", "always", "inventory", null));
            }
        }

        WorkbenchDefinition.REGISTRY.register(id, new WorkbenchDefinition(id,
                view.string("title", id.value()), size, builder.build(), structure, multiblock,
                tools, renderSlots, variables, renderers));
    }

    /** Reads a 3-component vector, defaulting component-wise. */
    private static float[] vec(JsonView view, String field, float[] fallback) {
        if (!view.has(field))
            return fallback;
        var raw = view.raw().get(field);
        // Pixel coordinates are routinely fractional (13.5), so these are read as floats
        // rather than through the integer list helper.
        if (raw.isJsonArray() && raw.getAsJsonArray().size() == 3) {
            var array = raw.getAsJsonArray();
            return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat() };
        }
        throw view.error("'" + field + "' must be [x, y, z]");
    }

    private static int requireInRange(JsonView view, int slot, int size, String field) {
        if (slot < 0 || slot >= size)
            throw view.error("'" + field + "' slot " + slot + " is outside the container (size " + size + ")");
        return slot;
    }

    /**
     * Recursively converts a {@code "when"} JSON element to a flat string
     * expression that {@code MachineRenderContext.evalBool} can evaluate.
     */
    private static String parseWhen(com.google.gson.JsonElement el) {
        if (el == null || el.isJsonNull()) return "always";
        if (el.isJsonPrimitive()) return el.getAsString();
        if (el.isJsonObject()) {
            com.google.gson.JsonObject obj = el.getAsJsonObject();
            if (obj.has("not"))
                return "!(" + parseWhen(obj.get("not")) + ")";
            if (obj.has("and")) {
                com.google.gson.JsonArray arr = obj.get("and").getAsJsonArray();
                return "(" + parseWhen(arr.get(0)) + " && " + parseWhen(arr.get(1)) + ")";
            }
            if (obj.has("or")) {
                com.google.gson.JsonArray arr = obj.get("or").getAsJsonArray();
                return "(" + parseWhen(arr.get(0)) + " || " + parseWhen(arr.get(1)) + ")";
            }
        }
        return "always";
    }

    /**
     * Reads a JSON float array field (e.g. {@code "offset": [0, 0.5, 0]}) from a
     * view, returning a clone of {@code defaults} when the field is absent or not
     * an array. Extra elements beyond {@code size} are ignored; missing elements
     * retain the default value.
     */
    private static float[] parseFloatArray(JsonView view, String field, int size, float[] defaults) {
        float[] result = defaults.clone();
        if (!view.has(field)) return result;
        com.google.gson.JsonElement el = view.raw().get(field);
        if (!el.isJsonArray()) return result;
        com.google.gson.JsonArray arr = el.getAsJsonArray();
        for (int i = 0; i < Math.min(arr.size(), size); i++)
            result[i] = arr.get(i).getAsFloat();
        return result;
    }

    /**
     * Read a JSON field that may be a string expression or a numeric literal, returning it as a
     * String suitable for PolyFormula evaluation. Returns {@code defaultVal} when the field is absent.
     */
    private static String readExprPrimitive(JsonView view, String field, String defaultVal) {
        if (!view.raw().has(field) || view.raw().get(field).isJsonNull()) return defaultVal;
        com.google.gson.JsonElement el = view.raw().get(field);
        if (!el.isJsonPrimitive()) return defaultVal;
        com.google.gson.JsonPrimitive p = el.getAsJsonPrimitive();
        return p.isString() ? p.getAsString() : String.valueOf(p.getAsDouble());
    }

    /** See MachineDefinitionLoader.parseLocationExpr for full documentation. */
    private static String parseLocationExpr(JsonView r) {
        if (r.raw().has("location")) {
            com.google.gson.JsonElement locEl = r.raw().get("location");
            if (locEl.isJsonObject()) {
                com.google.gson.JsonObject o = locEl.getAsJsonObject();
                String lx = o.has("x") ? o.get("x").getAsString() : "0";
                String ly = o.has("y") ? o.get("y").getAsString() : "0";
                String lz = o.has("z") ? o.get("z").getAsString() : "0";
                return "[" + lx + ", " + ly + ", " + lz + "]";
            }
            if (locEl.isJsonArray()) return locEl.toString();
            if (locEl.isJsonPrimitive()) return locEl.getAsString();
        }
        boolean hasOffset = r.raw().has("offset") || r.raw().has("offset_x")
                         || r.raw().has("offset_y") || r.raw().has("offset_z");
        if (hasOffset) {
            float[] off = parseFloatArray(r, "offset", 3, new float[]{0f, 0f, 0f});
            String ox = r.raw().has("offset_x") ? r.string("offset_x") : String.valueOf(off[0]);
            String oy = r.raw().has("offset_y") ? r.string("offset_y") : String.valueOf(off[1]);
            String oz = r.raw().has("offset_z") ? r.string("offset_z") : String.valueOf(off[2]);
            return "[" + ox + ", " + oy + ", " + oz + "]";
        }
        return null;
    }

    private static String readExprPrimitive(JsonView view, String field) {
        return readExprPrimitive(view, field, "0");
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
