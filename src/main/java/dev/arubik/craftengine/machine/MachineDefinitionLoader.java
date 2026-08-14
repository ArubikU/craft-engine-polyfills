package dev.arubik.craftengine.machine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MultiBlockDefinition;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code machines/*.json} into {@link MachineDefinition#REGISTRY}.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:grinder",
 *   "recipe_type": "grinder",       // which recipes/*.json entries it runs
 *   "title": "Grinder",
 *   "menu_size": 54,
 *   "slots": {
 *     "input":  [20],
 *     "output": [24, 25],
 *     "fuel":   [11],
 *     "info":   4,                          // recipe readout; or
 *     // "info": { "slot": 4, "type": "tank", "source": "fluid:buffer" }
 *     "upgrade": { "count": 9, "base_unlocked": 3 }   // reserved indices 0..8, own page
 *   },
 *   "fluid_tanks": [ { "name": "input", "capacity": 8000, "filter": "polyfills:water" } ],
 *   "gas_tanks":   [ { "name": "output", "capacity": 8000 } ],
 *   "fuel_required": true,
 *   "paging": {                     // storage spanning several screens
 *     "pages": 3, "slots": 45,
 *     "prev_slot": 45, "next_slot": 53, "indicator_slot": 49
 *   },
 *   "bars": [                       // gauges from bars/*.json, positioned here
 *     { "id": "polyfills:fuel",     "slots": [40] },
 *     { "id": "polyfills:progress", "slots": [13] },
 *     { "id": "polyfills:fluid",    "slots": [27, 18, 9, 0], "source": "fluid:water" },
 *     { "id": "polyfills:gas",      "slots": [35, 26, 17, 8], "source": "gas:steam" }
 *   ],
 *   "power": {                      // rotational power, if this machine uses any
 *     "consumes_stress": true,      // needs an rpm/SU supply (recipes carry the amounts)
 *     "su_exponent": 1.25,          // SU = suCost * (1+overclock)^exponent
 *     "stress_grace_ticks": 20      // keeps running this long through a supply dip
 *   },
 *   "io": {
 *     "input":  { "types": ["item"], "faces": ["all_but_down"] },
 *     "output": { "types": ["item"], "faces": ["down"] }
 *   }
 * }
 * }</pre>
 *
 * <p>
 * The {@code io} block reuses the face-group vocabulary of the multiblock loader
 * ({@code all}, {@code horizontal}, {@code vertical}, {@code all_but_down},
 * {@code all_but_up}, or a bare direction), so both places read the same.
 */
public final class MachineDefinitionLoader {

    private MachineDefinitionLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        Registries.addLoader("machines", Registries.PHASE_DEFINITIONS, MachineDefinitionLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("machines", MachineDefinitionLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + MachineDefinition.REGISTRY.size() + " machine definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));
        MachineDefinition.REGISTRY.register(id, parse(view, id));
    }

    /**
     * Builds a definition from a view, without registering it.
     *
     * <p>
     * Exposed because a pump is described by ONE file holding both its extraction
     * numbers and its machine half — see {@code PumpLoader}. Splitting those into
     * two files that repeat the same id was confusing and invited them to drift.
     */
    public static MachineDefinition parse(JsonView view, Key id) {

        int menuSize = view.rangedInt("menu_size", 54, 9, 54);
        if (menuSize % 9 != 0)
            throw view.error("menu_size must be a multiple of 9, found " + menuSize);

        JsonView slots = view.has("slots") ? view.object("slots") : view;
        int[] inputs = toArray(slots.intList("input"), view, menuSize, "input");
        int[] outputs = toArray(slots.intList("output"), view, menuSize, "output");
        int[] fuels = toArray(slots.intList("fuel"), view, menuSize, "fuel");
        // `info` is either a bare slot (a recipe readout, the common case) or an object
        // choosing the kind: { slot, type: recipe|tank, source: "fluid:<tank>" }.
        int info = -1;
        MachineDefinition.InfoSpec infoSpec = MachineDefinition.InfoSpec.none();
        if (slots.has("info")) {
            var rawInfo = slots.raw().get("info");
            if (rawInfo.isJsonObject()) {
                JsonView iv = slots.object("info");
                info = iv.rangedInt("slot", -1, 0, menuSize - 1);
                infoSpec = new MachineDefinition.InfoSpec(info, iv.string("type", "recipe"),
                        iv.string("source", ""));
            } else {
                info = slots.rangedInt("info", -1, 0, menuSize - 1);
                infoSpec = new MachineDefinition.InfoSpec(info, "recipe", "");
            }
        }

        MachineDefinition.UpgradeSpec upgrades = new MachineDefinition.UpgradeSpec(0, 0);
        if (slots.has("upgrade")) {
            JsonView up = slots.object("upgrade");
            boolean hasSlots = up.has("slots");
            boolean hasCount = up.has("count");
            if (hasSlots && hasCount)
                throw up.error("declare either 'slots' (inline on the main page) or 'count' "
                        + "(reserved indices with their own page), not both");
            if (hasSlots) {
                int[] upSlots = toArray(up.intList("slots"), view, menuSize, "upgrade.slots");
                upgrades = new MachineDefinition.UpgradeSpec(0,
                        up.rangedInt("base_unlocked", upSlots.length, 0, Math.max(1, upSlots.length)), upSlots);
            } else {
                int count = up.rangedInt("count", 0, 0, menuSize);
                upgrades = new MachineDefinition.UpgradeSpec(count,
                        up.rangedInt("base_unlocked", count, 0, Math.max(1, count)));
            }
        }

        // Upgrade slots live at reserved container indices 0..count-1 and are shown on
        // their own page. A STORAGE slot pointing into that range would share a stack
        // with an upgrade module, so reject those. The info slot is exempt: it renders a
        // computed icon rather than holding an item, and the real crusher genuinely puts
        // it at index 4 while reserving 0..8, because the two live on different pages.
        int reserved = upgrades.isInline() ? 0 : upgrades.count();
        rejectReserved(view, inputs, reserved, "input");
        rejectReserved(view, outputs, reserved, "output");
        rejectReserved(view, fuels, reserved, "fuel");

        List<MachineDefinition.TankSpec> fluidTanks = parseTanks(view, "fluid_tanks");
        List<MachineDefinition.TankSpec> gasTanks = parseTanks(view, "gas_tanks");

        IOConfiguration io = view.has("io") ? parseIO(view.object("io")) : null;

        List<MachineDefinition.ButtonSpec> buttons = new ArrayList<>();
        for (JsonView b : view.objectList("buttons"))
            buttons.add(new MachineDefinition.ButtonSpec(
                    b.rangedInt("slot", 0, 0, menuSize - 1),
                    b.string("icon", "cml:gui_empty"),
                    b.string("action", "none"),
                    b.string("name", null),
                    b.stringList("lore"),
                    b.string("locked_icon", null),
                    b.string("locked_when", "never")));

        MachineDefinition.PowerSpec power = MachineDefinition.PowerSpec.none();
        if (view.has("power")) {
            JsonView p = view.object("power");
            power = new MachineDefinition.PowerSpec(
                    p.bool("consumes_stress", false),
                    p.rangedDouble("su_exponent", 1.25, 0.1, 8.0),
                    p.rangedInt("stress_grace_ticks", 20, 0, 1200),
                    (float) p.rangedDouble("generates_rpm", 0.0, 0.0, 100000.0),
                    p.rangedInt("generates_su", 0, 0, 1_000_000),
                    (float) p.rangedDouble("base_overclock", 2.0, 0.0, 64.0));
        }

        // `bars` names definitions from bars/*.json and says where each one goes, so a
        // gauge shared by several machines is written once.
        List<MachineDefinition.BarRef> bars = new ArrayList<>();
        for (JsonView b : view.objectList("bars")) {
            List<Integer> slotList = b.intList("slots");
            int[] barSlots = new int[slotList.size()];
            for (int i = 0; i < barSlots.length; i++)
                barSlots[i] = requireInMenu(view, slotList.get(i), menuSize, "bars.slots");
            Key barId = b.key("id", "polyfills");
            bars.add(new MachineDefinition.BarRef(barId, barSlots,
                    b.string("source", barId.value())));
        }

        MachineDefinition.PagingSpec paging = MachineDefinition.PagingSpec.none();
        if (view.has("paging")) {
            JsonView pg = view.object("paging");
            paging = new MachineDefinition.PagingSpec(
                    pg.rangedInt("pages", 1, 1, 64),
                    pg.rangedInt("slots", menuSize, 1, menuSize),
                    pg.rangedInt("prev_slot", -1, -1, menuSize - 1),
                    pg.rangedInt("next_slot", -1, -1, menuSize - 1),
                    pg.rangedInt("indicator_slot", -1, -1, menuSize - 1));
        }

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
                    // Inline PolyFormula expression
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
                        case "tank" -> new dev.arubik.craftengine.machine.render.variable.VariableSpec.TankVar(
                                varView.string("tank", ""),
                                varView.bool("gas", false),
                                varView.string("property", "fraction"));
                        default -> new dev.arubik.craftengine.machine.render.variable.VariableSpec.BoolSource("always");
                    };
                } else {
                    continue; // skip non-string, non-object entries
                }
                variables.put(varName, spec);
            }
        }

        // Parse "renderers" array — an ordered list of RendererSpec entries.
        List<dev.arubik.craftengine.machine.render.RendererSpec> renderers = new ArrayList<>();
        for (JsonView r : view.objectList("renderers")) {
            String rType = r.string("type", "bettermodel");
            String whenExpr = r.raw().has("when") ? parseWhen(r.raw().get("when")) : "always";
            String updateWhen = r.string("update_when", "always");
            // Optional .pf script reference: "run": "crusher" -> scripts/crusher.pf
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
                    float[] rot3 = parseFloatArray(r, "rotation", 3, new float[]{0f, 0f, 0f});
                    String rx = r.raw().has("rot_x") ? r.string("rot_x") : String.valueOf(rot3[0]);
                    String ry = r.raw().has("rot_y") ? r.string("rot_y") : String.valueOf(rot3[1]);
                    String rz = r.raw().has("rot_z") ? r.string("rot_z") : String.valueOf(rot3[2]);
                    String sc = r.raw().has("scale") && r.raw().get("scale").isJsonPrimitive()
                            && r.raw().get("scale").getAsJsonPrimitive().isString()
                            ? r.string("scale") : String.valueOf(r.floating("scale", 1.0f));
                    yield new dev.arubik.craftengine.machine.render.RendererSpec.ItemDisplaySpec(
                            r.string("item"),
                            parseLocationExpr(r),
                            sc, rx, ry, rz,
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
                    String rx = r.raw().has("rot_x") ? readExprPrimitive(r, "rot_x") : String.valueOf(rot[0]);
                    String ry = r.raw().has("rot_y") ? readExprPrimitive(r, "rot_y") : String.valueOf(rot[1]);
                    String rz = r.raw().has("rot_z") ? readExprPrimitive(r, "rot_z") : String.valueOf(rot[2]);
                    String sc = readExprPrimitive(r, "scale", "0.1");
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
                // Wrap with PositionedSpec when multi-position ("positions"/"locations") key is present.
                String posExpr = r.raw().has("positions") ? r.string("positions")
                               : r.raw().has("locations") ? r.string("locations") : null;
                if (posExpr != null) {
                    spec = new dev.arubik.craftengine.machine.render.RendererSpec.PositionedSpec(spec, posExpr, null);
                }
                renderers.add(spec);
            }
        }

        // Parse "slots.upgrade.definitions" (or "upgrades.definitions" as fallback).
        // Supports both: { slots: { upgrade: { definitions: {...} } } }
        // and:           { upgrades: { definitions: {...} } }
        java.util.Map<net.momirealms.craftengine.core.util.Key,
                java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs =
                        java.util.Map.of();
        {
            com.google.gson.JsonObject raw = view.raw();
            // Primary path: slots.upgrade.definitions
            com.google.gson.JsonElement defEl = null;
            try {
                com.google.gson.JsonElement slotsEl = raw.get("slots");
                if (slotsEl != null && slotsEl.isJsonObject()) {
                    com.google.gson.JsonElement upEl = slotsEl.getAsJsonObject().get("upgrade");
                    if (upEl != null && upEl.isJsonObject()) {
                        defEl = upEl.getAsJsonObject().get("definitions");
                    }
                }
            } catch (Throwable ignored) {}
            // Fallback: top-level upgrades.definitions
            if (defEl == null) {
                try {
                    com.google.gson.JsonElement upEl = raw.get("upgrades");
                    if (upEl != null && upEl.isJsonObject()) {
                        defEl = upEl.getAsJsonObject().get("definitions");
                    }
                } catch (Throwable ignored) {}
            }
            if (defEl != null && defEl.isJsonObject()) {
                upgradeDefs = dev.arubik.craftengine.machine.block.behavior.DataMachineBehavior
                        .parseUpgradeDefsFromJson(defEl.getAsJsonObject());
            }
        }

        return new MachineDefinition(id,
                view.string("recipe_type", id.value()), view.string("title", id.value()), menuSize,
                inputs, outputs, fuels, upgrades, info, fluidTanks, gasTanks,
                view.bool("fuel_required", true), io, buttons, power, bars, infoSpec, paging,
                variables, renderers, upgradeDefs);
    }

    private static List<MachineDefinition.TankSpec> parseTanks(JsonView view, String field) {
        List<MachineDefinition.TankSpec> out = new ArrayList<>();
        for (JsonView tank : view.objectList(field)) {
            out.add(new MachineDefinition.TankSpec(
                    tank.string("name", "tank" + out.size()),
                    tank.rangedInt("capacity", 8000, 1, Integer.MAX_VALUE),
                    tank.has("filter") ? tank.key("filter", "polyfills") : null));
        }
        return out;
    }

    /** Single-cell IO: the same grant grammar the multiblock loader uses, without cell selectors. */
    private static IOConfiguration parseIO(JsonView view) {
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        applyGrants(view, "input", config, true);
        applyGrants(view, "output", config, false);
        return config;
    }

    private static void applyGrants(JsonView view, String field, IOConfiguration.Simple config, boolean input) {
        if (!view.has(field))
            return;
        List<JsonView> entries = new ArrayList<>();
        if (view.raw().get(field).isJsonArray())
            entries.addAll(view.objectList(field));
        else
            entries.add(view.object(field));

        for (JsonView entry : entries) {
            List<IOConfiguration.IOType> types = new ArrayList<>();
            for (String typeName : entry.stringList("types")) {
                IOConfiguration.IOType type = null;
                for (IOConfiguration.IOType candidate : IOConfiguration.IOType.values())
                    if (candidate.name().equalsIgnoreCase(typeName))
                        type = candidate;
                if (type == null)
                    throw entry.error("unknown io type '" + typeName + "'");
                types.add(type);
            }
            List<net.minecraft.core.Direction> faces = new ArrayList<>();
            for (String faceName : entry.stringList("faces")) {
                List<net.minecraft.core.Direction> group = MultiBlockDefinition.IOSpec.faceGroup(faceName);
                if (group == null)
                    throw entry.error("unknown face or face group '" + faceName + "'");
                faces.addAll(group);
            }
            if (faces.isEmpty())
                faces.addAll(List.of(net.minecraft.core.Direction.values()));
            for (IOConfiguration.IOType type : types)
                for (net.minecraft.core.Direction face : faces)
                    if (input)
                        config.addInput(type, face);
                    else
                        config.addOutput(type, face);
        }
    }

    private static int requireInMenu(JsonView view, int slot, int menuSize, String field) {
        if (slot < 0 || slot >= menuSize)
            throw view.error("'" + field + "' slot " + slot + " is outside the menu (size " + menuSize + ")");
        return slot;
    }

    private static void rejectReserved(JsonView view, int[] slots, int reserved, String field) {
        for (int slot : slots)
            if (slot < reserved)
                throw view.error("'" + field + "' slot " + slot + " collides with the " + reserved
                        + " reserved upgrade slots (0.." + (reserved - 1) + ")");
    }

    private static int[] toArray(List<Integer> values, JsonView view, int menuSize, String field) {
        int[] out = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            int slot = values.get(i);
            if (slot < 0 || slot >= menuSize)
                throw view.error("'" + field + "' slot " + slot + " is outside the menu (size " + menuSize + ")");
            out[i] = slot;
        }
        return out;
    }

    /**
     * Recursively converts a {@code "when"} JSON element to a flat string
     * expression that {@code MachineRenderContext.evalBool} can evaluate.
     *
     * <ul>
     *   <li>A string primitive is returned as-is (e.g. {@code "$running"}, {@code "always"}).</li>
     *   <li>{@code {"not": X}} becomes {@code "!(INNER)"}.</li>
     *   <li>{@code {"and": [A, B]}} becomes {@code "(A && B)"}.</li>
     *   <li>{@code {"or":  [A, B]}} becomes {@code "(A || B)"}.</li>
     *   <li>Anything else falls back to {@code "always"}.</li>
     * </ul>
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

    /**
     * Resolves a renderer entry's position to a single {@code locationExpr} string.
     * Supports the new unified {@code "location"} key (JSON object, array, or string formula)
     * as well as the legacy {@code "offset"} / {@code "offset_x/y/z"} backward-compat keys.
     * Returns {@code null} when no position field is present (render at machine centre).
     */
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
        // Backward compat: "offset" array or "offset_x/y/z" individual fields.
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
