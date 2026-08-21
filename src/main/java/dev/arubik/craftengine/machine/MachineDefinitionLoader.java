/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.core.Direction
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.behavior.DataMachineBehavior;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MultiBlockDefinition;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.util.Key;

public final class MachineDefinitionLoader {
    private MachineDefinitionLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("machines", 100, MachineDefinitionLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("machines", MachineDefinitionLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger().info("Loaded " + MachineDefinition.REGISTRY.size() + " machine definitions.");
        }
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills") : Key.of((String)"polyfills", (String)MachineDefinitionLoader.stripExtension(fileName).replace('/', '_'));
        MachineDefinition.REGISTRY.register(id, MachineDefinitionLoader.parse(view, id));
    }

    public static MachineDefinition parse(JsonView view, Key id) {
        int menuSize = view.rangedInt("menu_size", 54, 9, 54);
        if (menuSize % 9 != 0) {
            throw view.error("menu_size must be a multiple of 9, found " + menuSize);
        }
        JsonView slots = view.has("slots") ? view.object("slots") : view;
        int[] inputs = MachineDefinitionLoader.toArray(slots.intList("input"), view, menuSize, "input");
        int[] outputs = MachineDefinitionLoader.toArray(slots.intList("output"), view, menuSize, "output");
        int[] fuels = MachineDefinitionLoader.toArray(slots.intList("fuel"), view, menuSize, "fuel");
        int info = -1;
        MachineDefinition.InfoSpec infoSpec = MachineDefinition.InfoSpec.none();
        if (slots.has("info")) {
            JsonElement rawInfo = slots.raw().get("info");
            if (rawInfo.isJsonObject()) {
                JsonView iv = slots.object("info");
                info = iv.rangedInt("slot", -1, 0, menuSize - 1);
                infoSpec = new MachineDefinition.InfoSpec(info, iv.string("type", "recipe"), iv.string("source", ""));
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
            if (hasSlots && hasCount) {
                throw up.error("declare either 'slots' (inline on the main page) or 'count' (reserved indices with their own page), not both");
            }
            if (hasSlots) {
                int[] upSlots = MachineDefinitionLoader.toArray(up.intList("slots"), view, menuSize, "upgrade.slots");
                upgrades = new MachineDefinition.UpgradeSpec(0, up.rangedInt("base_unlocked", upSlots.length, 0, Math.max(1, upSlots.length)), upSlots);
            } else {
                int count = up.rangedInt("count", 0, 0, menuSize);
                upgrades = new MachineDefinition.UpgradeSpec(count, up.rangedInt("base_unlocked", count, 0, Math.max(1, count)));
            }
        }
        // Also check top-level "upgrades" (plural) key used by paged machines
        if (upgrades.count() == 0 && !upgrades.isInline() && view.raw().has("upgrades")
                && view.raw().get("upgrades").isJsonObject()) {
            JsonView up2 = JsonView.of(view.raw().get("upgrades").getAsJsonObject(), view.path() + " > upgrades");
            if (up2.has("count")) {
                int c2 = up2.rangedInt("count", 0, 0, menuSize);
                upgrades = new MachineDefinition.UpgradeSpec(c2, up2.rangedInt("base_unlocked", c2, 0, Math.max(1, c2)));
            }
        }
        int reserved = upgrades.isInline() ? 0 : upgrades.count();
        MachineDefinitionLoader.rejectReserved(view, inputs, reserved, "input");
        MachineDefinitionLoader.rejectReserved(view, outputs, reserved, "output");
        MachineDefinitionLoader.rejectReserved(view, fuels, reserved, "fuel");
        List<MachineDefinition.TankSpec> fluidTanks = MachineDefinitionLoader.parseTanks(view, "fluid_tanks");
        List<MachineDefinition.TankSpec> gasTanks = MachineDefinitionLoader.parseTanks(view, "gas_tanks");
        IOConfiguration io = view.has("io") ? MachineDefinitionLoader.parseIO(view.object("io")) : null;
        ArrayList<MachineDefinition.ButtonSpec> buttons = new ArrayList<MachineDefinition.ButtonSpec>();
        for (JsonView b : view.objectList("buttons")) {
            buttons.add(new MachineDefinition.ButtonSpec(b.rangedInt("slot", 0, 0, menuSize - 1), b.string("icon", "cml:gui_empty"), b.string("action", "none"), b.string("name", null), b.stringList("lore"), b.string("locked_icon", null), b.string("locked_when", "never")));
        }
        MachineDefinition.PowerSpec power = MachineDefinition.PowerSpec.none();
        if (view.has("power")) {
            JsonView p = view.object("power");
            power = new MachineDefinition.PowerSpec(p.bool("consumes_stress", false), p.rangedDouble("su_exponent", 1.25, 0.1, 8.0), p.rangedInt("stress_grace_ticks", 20, 0, 1200), (float)p.rangedDouble("generates_rpm", 0.0, 0.0, 100000.0), p.rangedInt("generates_su", 0, 0, 1000000), (float)p.rangedDouble("base_overclock", 2.0, 0.0, 64.0));
        }
        MachineDefinition.EnergySpec energy = MachineDefinition.EnergySpec.none();
        if (view.has("energy")) {
            JsonView e = view.object("energy");
            int cap = e.rangedInt("capacity", 10000, 1, Integer.MAX_VALUE);
            energy = new MachineDefinition.EnergySpec(cap,
                    e.rangedInt("max_input", cap, 0, Integer.MAX_VALUE),
                    e.rangedInt("max_output", cap, 0, Integer.MAX_VALUE),
                    e.rangedInt("generation_per_tick", 0, 0, Integer.MAX_VALUE));
        }
        ArrayList<MachineDefinition.BarRef> bars = new ArrayList<MachineDefinition.BarRef>();
        for (JsonView b : view.objectList("bars")) {
            List<Integer> slotList = b.intList("slots");
            int[] barSlotsArr = new int[slotList.size()];
            for (int i = 0; i < barSlotsArr.length; ++i) {
                barSlotsArr[i] = MachineDefinitionLoader.requireInMenu(view, slotList.get(i), menuSize, "bars.slots");
            }
            Key barId = b.key("id", "polyfills");
            bars.add(new MachineDefinition.BarRef(barId, barSlotsArr, b.string("source", barId.value())));
        }
        MachineDefinition.PagingSpec paging = MachineDefinition.PagingSpec.none();
        if (view.has("paging")) {
            JsonView pg = view.object("paging");
            paging = new MachineDefinition.PagingSpec(pg.rangedInt("pages", 1, 1, 64), pg.rangedInt("slots", menuSize, 1, menuSize), pg.rangedInt("prev_slot", -1, -1, menuSize - 1), pg.rangedInt("next_slot", -1, -1, menuSize - 1), pg.rangedInt("indicator_slot", -1, -1, menuSize - 1));
        }
        LinkedHashMap<String, VariableSpec> variables = new LinkedHashMap<String, VariableSpec>();
        if (view.has("variables")) {
            JsonObject varObj = view.raw().get("variables").getAsJsonObject();
            var varIter = varObj.entrySet().iterator();
            while (varIter.hasNext()) {
                Record spec;
                Map.Entry<String, JsonElement> entry = varIter.next();
                String varName = (String)entry.getKey();
                JsonElement rawVal = (JsonElement)entry.getValue();
                if (rawVal.isJsonPrimitive() && rawVal.getAsJsonPrimitive().isString()) {
                    spec = new VariableSpec.Formula(rawVal.getAsString());
                } else {
                    String varType;
                    if (!rawVal.isJsonObject()) continue;
                    JsonView varView = JsonView.of(rawVal.getAsJsonObject(), view.path() + " > variables > " + varName);
                    spec = switch (varType = varView.string("type", "bool")) {
                        case "bool" -> {
                            if (varView.has("source")) {
                                yield new VariableSpec.BoolSource(varView.string("source"));
                            }
                            yield new VariableSpec.BoolExpr(varView.string("expr"));
                        }
                        case "num" -> new VariableSpec.NumExpr(varView.string("expr"));
                        case "item_slot" -> new VariableSpec.ItemSlot(varView.integer("slot"));
                        case "tank" -> new VariableSpec.TankVar(varView.string("tank", ""), varView.bool("gas", false), varView.string("property", "fraction"));
                        default -> new VariableSpec.BoolSource("always");
                    };
                }
                variables.put(varName, (VariableSpec)(spec));
            }
        }
        ArrayList<RendererSpec> renderers = new ArrayList<RendererSpec>();
        var rendererIter = view.objectList("renderers").iterator();
        while (rendererIter.hasNext()) {
            String posExpr;
            Record spec;
            JsonView r = rendererIter.next();
            String rType = r.string("type", "bettermodel");
            String whenExpr = r.raw().has("when") ? MachineDefinitionLoader.parseWhen(r.raw().get("when")) : "always";
            String updateWhen = r.string("update_when", "always");
            String scriptRef = r.string("run", null);
            if ((spec = (switch (rType) {
                case "bettermodel" -> new RendererSpec.BetterModelSpec(r.string("model_id"), r.string("animation", null), r.string("speed", null), whenExpr, updateWhen, scriptRef, r.integer("margin", 0));
                case "modelengine" -> new RendererSpec.ModelEngineSpec(r.string("model_id"), r.string("animation", null), r.string("speed", null), whenExpr, updateWhen, scriptRef);
                case "item_display" -> {
                    float[] rot3 = MachineDefinitionLoader.parseFloatArray(r, "rotation", 3, new float[]{0.0f, 0.0f, 0.0f});
                    String rx = r.raw().has("rot_x") ? r.string("rot_x") : String.valueOf(rot3[0]);
                    String ry = r.raw().has("rot_y") ? r.string("rot_y") : String.valueOf(rot3[1]);
                    String rz = r.raw().has("rot_z") ? r.string("rot_z") : String.valueOf(rot3[2]);
                    String sc = r.raw().has("scale") && r.raw().get("scale").isJsonPrimitive() && r.raw().get("scale").getAsJsonPrimitive().isString() ? r.string("scale") : String.valueOf(r.floating("scale", 1.0f));
                    yield new RendererSpec.ItemDisplaySpec(r.string("item"), MachineDefinitionLoader.parseLocationExpr(r), sc, rx, ry, rz, r.string("billboard", "none"), whenExpr, updateWhen, r.bool("global", false), scriptRef);
                }
                case "particle" -> {
                    int interval = r.rangedInt("interval", 1, 1, 200);
                    yield new RendererSpec.ParticleSpec(r.string("particle", "SMOKE_NORMAL"), r.string("count", "1"), MachineDefinitionLoader.parseLocationExpr(r), r.string("spread_x", "0"), r.string("spread_y", "0"), r.string("spread_z", "0"), r.string("speed", "0.05"), r.string("dir_x", "0"), r.string("dir_y", "0"), r.string("dir_z", "0"), r.string("shape", "point"), r.string("direction_mode", "random"), interval, whenExpr, updateWhen, scriptRef);
                }
                case "slot_display" -> {
                    int slotIdx = r.integer("slot", 0);
                    float[] offsets = MachineDefinitionLoader.parseFloatArray(r, "offset", 3, new float[]{0.0f, 0.44f, 0.0f});
                    float slotScale = r.floating("scale", 0.5f);
                    yield new RendererSpec.SlotDisplaySpec(slotIdx, offsets[0], offsets[1], offsets[2], slotScale, whenExpr, updateWhen, scriptRef);
                }
                case "fluid_tank", "gas_tank" -> new RendererSpec.FluidTankSpec(r.string("tank", ""), "gas_tank".equals(rType), MachineDefinitionLoader.parseLocationExpr(r), r.floating("max_height", 0.875f), whenExpr, updateWhen, scriptRef);
                case "text_display" -> {
                    float[] rot = MachineDefinitionLoader.parseFloatArray(r, "rotation", 3, new float[]{0.0f, 0.0f, 0.0f});
                    String rx = r.raw().has("rot_x") ? MachineDefinitionLoader.readExprPrimitive(r, "rot_x") : String.valueOf(rot[0]);
                    String ry = r.raw().has("rot_y") ? MachineDefinitionLoader.readExprPrimitive(r, "rot_y") : String.valueOf(rot[1]);
                    String rz = r.raw().has("rot_z") ? MachineDefinitionLoader.readExprPrimitive(r, "rot_z") : String.valueOf(rot[2]);
                    String sc = MachineDefinitionLoader.readExprPrimitive(r, "scale", "0.1");
                    yield new RendererSpec.TextDisplaySpec(r.string("text", ""), MachineDefinitionLoader.parseLocationExpr(r), sc, rx, ry, rz, r.string("billboard", "center"), r.rangedInt("line_width", 200, 1, 1000), r.string("background", "0"), r.bool("shadow", false), r.bool("see_through", false), r.string("alignment", "center"), r.rangedInt("opacity", 255, 0, 255), whenExpr, updateWhen, r.bool("global", false), scriptRef);
                }
                case "sound" -> new RendererSpec.SoundSpec(r.string("sound", "minecraft:block.note_block.pling"), MachineDefinitionLoader.readExprPrimitive(r, "volume", "1.0"), MachineDefinitionLoader.readExprPrimitive(r, "pitch", "1.0"), r.rangedInt("interval", 20, 0, 6000), whenExpr, updateWhen, scriptRef);
                case "armor_stand" -> new RendererSpec.ArmorStandSpec(r.string("head_item", null), r.string("body_item", null), MachineDefinitionLoader.parseLocationExpr(r), MachineDefinitionLoader.readExprPrimitive(r, "rot_x"), MachineDefinitionLoader.readExprPrimitive(r, "rot_y"), MachineDefinitionLoader.readExprPrimitive(r, "rot_z"), r.bool("small", false), r.bool("invisible", true), r.bool("marker", true), whenExpr, updateWhen, scriptRef);
                case "block_display" -> new RendererSpec.BlockDisplaySpec(r.string("block", "minecraft:stone"), MachineDefinitionLoader.parseLocationExpr(r), MachineDefinitionLoader.readExprPrimitive(r, "scale", "1.0"), MachineDefinitionLoader.readExprPrimitive(r, "rot_x"), MachineDefinitionLoader.readExprPrimitive(r, "rot_y"), MachineDefinitionLoader.readExprPrimitive(r, "rot_z"), whenExpr, updateWhen, scriptRef);
                default -> null;
            })) == null) continue;
            posExpr = r.raw().has("positions") ? r.string("positions") : (r.raw().has("locations") ? r.string("locations") : null);
            if (posExpr != null) {
                spec = new RendererSpec.PositionedSpec((RendererSpec)(spec), posExpr, null);
            }
            renderers.add((RendererSpec)(spec));
        }
        Map<Key, List<MachineAttributes.Mod>> upgradeDefs = Map.of();
        JsonObject raw = view.raw();
        JsonElement defEl = null;
        try {
            JsonElement upEl;
            JsonElement slotsEl = raw.get("slots");
            if (slotsEl != null && slotsEl.isJsonObject() && (upEl = slotsEl.getAsJsonObject().get("upgrade")) != null && upEl.isJsonObject()) {
                defEl = upEl.getAsJsonObject().get("definitions");
            }
        }
        catch (Throwable slotsEl) {
            // empty catch block
        }
        if (defEl == null) {
            try {
                JsonElement upEl = raw.get("upgrades");
                if (upEl != null && upEl.isJsonObject()) {
                    defEl = upEl.getAsJsonObject().get("definitions");
                }
            }
            catch (Throwable upEl) {
                // empty catch block
            }
        }
        if (defEl != null && defEl.isJsonObject()) {
            upgradeDefs = DataMachineBehavior.parseUpgradeDefsFromJson(defEl.getAsJsonObject());
        }
        String actionScript = view.has("action_script") ? view.string("action_script") : null;
        int actionInterval = view.has("action_interval") ? view.integer("action_interval", 20) : 20;
        String interactScript = view.has("on_right_click") ? view.string("on_right_click") : null;
        String attackScript = view.has("on_left_click") ? view.string("on_left_click") : null;
        MachineFlags flags = parseFlags(view, power);
        MachineDefinition def = new MachineDefinition(id, view.string("recipe_type", id.value()), view.string("title", id.value()), menuSize, inputs, outputs, fuels, upgrades, info, fluidTanks, gasTanks, flags.fuel(), io, buttons, power, bars, infoSpec, paging, variables, renderers, upgradeDefs, actionScript, actionInterval);
        def.setFlags(flags);
        def.setInteractScript(interactScript);
        def.setAttackScript(attackScript);
        if (view.has("status"))    def.setStatusScript(view.string("status", null));
        if (view.has("on_place"))        def.setOnPlaceScript(view.string("on_place", null));
        if (view.has("on_break"))        def.setOnBreakScript(view.string("on_break", null));
        if (view.has("on_state_change")) def.setOnStateChangeScript(view.string("on_state_change", null));
        if (view.has("on_pipe_transfer")) def.setOnTransferScript(view.string("on_pipe_transfer", null));
        if (view.has("rpm_ratio")) {
            def.setRpmRatio((float)view.decimal("rpm_ratio", 1.0));
        }
        if (view.bool("is_sail", false)) {
            def.setSail(true);
            def.setSailRpmBonus((float)view.decimal("sail_rpm_bonus", 1.0));
            // sail block ids used directly via block.id.contains("sail") in .pf scripts
        }
        JsonObject rpmIoObj = null;
        try {
            JsonElement ioEl;
            if (view.raw().has("io") && (ioEl = view.raw().get("io")).isJsonObject() && ioEl.getAsJsonObject().has("rpm")) {
                rpmIoObj = ioEl.getAsJsonObject().get("rpm").getAsJsonObject();
            }
            if (rpmIoObj == null && view.raw().has("rpm_io")) {
                rpmIoObj = view.raw().get("rpm_io").getAsJsonObject();
            }
        }
        catch (Throwable ioEl) {
            // empty catch block
        }
        if (rpmIoObj != null) {
            try {
                Map<String, Set<String>> filter;
                String sameKey;
                Set<String> in;
                if (rpmIoObj.has("input") && !(in = MachineDefinitionLoader.parseRawFaces(rpmIoObj.get("input"))).isEmpty()) {
                    def.setRpmInputFacesRaw(in);
                }
                String string = sameKey = rpmIoObj.has("output_same") ? "output_same" : "output";
                if (rpmIoObj.has(sameKey)) {
                    Set<String> out = MachineDefinitionLoader.parseRawFaces(rpmIoObj.get(sameKey));
                    def.setRpmOutputFacesRaw(out);
                    def.setRpmOutputDeclared(true);
                }
                if (rpmIoObj.has("output_inverted")) {
                    Set<String> inv = MachineDefinitionLoader.parseRawFaces(rpmIoObj.get("output_inverted"));
                    def.setRpmOutputInvertedRaw(inv);
                    def.setRpmOutputDeclared(true);
                }
                if (rpmIoObj.has("output_relative") && rpmIoObj.get("output_relative").getAsBoolean()) {
                    // Gearbox behaviour: the sign follows the driven face at runtime.
                    def.setRpmOutputRelative(true);
                    def.setRpmOutputDeclared(true);
                }
                if (rpmIoObj.has("input_block_filter") && !(filter = MachineDefinitionLoader.parseBlockFilter(rpmIoObj.get("input_block_filter"))).isEmpty()) {
                    def.setRpmInputBlockFilter(filter);
                }
                if (rpmIoObj.has("output_block_filter") && !(filter = MachineDefinitionLoader.parseBlockFilter(rpmIoObj.get("output_block_filter"))).isEmpty()) {
                    def.setRpmOutputBlockFilter(filter);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        // Parse pages[] — new configurable menu system
        if (view.raw().has("pages") && view.raw().get("pages").isJsonArray()) {
            List<MachineDefinition.PageDef> pages = new ArrayList<>();
            for (JsonElement pageEl : view.raw().get("pages").getAsJsonArray()) {
                if (!pageEl.isJsonObject()) continue;
                pages.add(MachineDefinitionLoader.parsePage(pageEl.getAsJsonObject(), view));
            }
            def.setPages(pages);
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    private static MachineDefinition.PageDef parsePage(JsonObject obj, JsonView parent) {
        JsonView p = JsonView.of(obj, parent.path() + " > pages[]");
        String title = p.string("title", null);
        String sizeOrType = p.has("size") ? String.valueOf(p.raw().get("size").isJsonPrimitive() ? p.raw().get("size").getAsString() : "54") : null;
        if (sizeOrType == null && p.has("type")) sizeOrType = p.string("type", "54");
        String specialType = p.string("special", null);

        // Static layout items
        List<MachineDefinition.PageDef.StaticSlot> layout = new ArrayList<>();
        if (p.raw().has("layout") && p.raw().get("layout").isJsonArray()) {
            for (JsonElement el : p.raw().get("layout").getAsJsonArray()) {
                if (!el.isJsonObject()) continue;
                JsonView sv = JsonView.of(el.getAsJsonObject(), p.path() + " > layout[]");
                int slot = sv.integer("slot", -1);
                if (slot < 0) continue;
                String item = sv.string("item", null);
                String name = sv.string("name", null);
                List<String> lore = sv.has("lore") ? sv.stringList("lore") : List.of();
                String action = sv.string("action", null);
                boolean locked = sv.bool("locked", false);
                layout.add(new MachineDefinition.PageDef.StaticSlot(slot, item, name, lore, action, locked));
            }
        }

        // Slots
        JsonView slots = p.has("slots") ? p.object("slots") : p;
        int[] inputs  = MachineDefinitionLoader.toArraySafe(slots.intList("input"));
        int[] outputs = MachineDefinitionLoader.toArraySafe(slots.intList("output"));
        int[] fuels   = MachineDefinitionLoader.toArraySafe(slots.intList("fuel"));

        // Buttons
        List<MachineDefinition.ButtonSpec> buttons = new ArrayList<>();
        for (JsonView b : p.objectList("buttons")) {
            buttons.add(new MachineDefinition.ButtonSpec(b.integer("slot", 0), b.string("icon", "cml:gui_empty"),
                b.string("action", "none"), b.string("name", null), b.stringList("lore"),
                b.string("locked_icon", null), b.string("locked_when", "never")));
        }

        // Bars
        List<MachineDefinition.BarRef> bars = new ArrayList<>();
        for (JsonView b : p.objectList("bars")) {
            List<Integer> slotList = b.intList("slots");
            int[] barSlots = new int[slotList.size()];
            for (int i = 0; i < barSlots.length; i++) barSlots[i] = slotList.get(i);
            net.momirealms.craftengine.core.util.Key barId = b.key("id", "polyfills");
            bars.add(new MachineDefinition.BarRef(barId, barSlots, b.string("source", barId.value())));
        }

        int infoSlot = p.has("info_slot") ? p.integer("info_slot", -1) : -1;
        String guiImage = p.has("gui_image") ? p.string("gui_image", null) : null;
        int guiImageShift = p.has("gui_image_shift") ? p.integer("gui_image_shift", -8) : -8;

        // Parse special-page item overrides: "items": { "locked": {...}, "filler": {...}, ... }
        java.util.Map<String, MachineDefinition.ItemSpec> specialItems = new java.util.LinkedHashMap<>();
        if (p.has("items") && p.raw().get("items").isJsonObject()) {
            for (java.util.Map.Entry<String, com.google.gson.JsonElement> e
                    : p.raw().get("items").getAsJsonObject().entrySet()) {
                MachineDefinition.ItemSpec spec = parseItemSpec(e.getValue(), p.path() + " > items." + e.getKey());
                if (spec != null) specialItems.put(e.getKey(), spec);
            }
        }

        return new MachineDefinition.PageDef(title, sizeOrType, List.copyOf(layout),
            inputs, outputs, fuels, List.copyOf(buttons), List.copyOf(bars), infoSlot, specialType,
            guiImage, guiImageShift, java.util.Map.copyOf(specialItems));
    }

    /** Parse an ItemSpec from a JsonElement. Supports short form ("icon_key") or object form. */
    static MachineDefinition.ItemSpec parseItemSpec(com.google.gson.JsonElement el, String path) {
        if (el == null || el.isJsonNull()) return null;
        if (el.isJsonPrimitive()) {
            return MachineDefinition.ItemSpec.ofIcon(el.getAsString());
        }
        if (!el.isJsonObject()) return null;
        JsonView sv = JsonView.of(el.getAsJsonObject(), path);
        String icon = sv.string("icon", null);
        String name = sv.string("name", null);
        List<String> lore = sv.has("lore") ? sv.stringList("lore") : List.of();
        java.util.Map<String, Object> components = new java.util.LinkedHashMap<>();
        if (sv.has("components") && sv.raw().get("components").isJsonObject()) {
            for (java.util.Map.Entry<String, com.google.gson.JsonElement> ce
                    : sv.raw().get("components").getAsJsonObject().entrySet()) {
                com.google.gson.JsonElement cv = ce.getValue();
                if (cv.isJsonPrimitive()) {
                    com.google.gson.JsonPrimitive prim = cv.getAsJsonPrimitive();
                    if (prim.isBoolean()) components.put(ce.getKey(), prim.getAsBoolean());
                    else if (prim.isNumber()) components.put(ce.getKey(), prim.getAsNumber().doubleValue());
                    else components.put(ce.getKey(), prim.getAsString());
                } else if (cv.isJsonObject()) {
                    // complex component — store as marker
                    components.put(ce.getKey(), Boolean.TRUE);
                }
            }
        }
        return new MachineDefinition.ItemSpec(icon, name, lore, java.util.Map.copyOf(components));
    }

    private static int[] toArraySafe(List<Integer> list) {
        if (list == null || list.isEmpty()) return new int[0];
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
    }

    private static Map<String, Set<String>> parseBlockFilter(JsonElement el) {
        LinkedHashMap<String, Set<String>> map = new LinkedHashMap<String, Set<String>>();
        if (el == null || !el.isJsonObject()) {
            return map;
        }
        for (Map.Entry entry : el.getAsJsonObject().entrySet()) {
            String faceGroup = ((String)entry.getKey()).toLowerCase();
            LinkedHashSet<String> blocks = new LinkedHashSet<String>();
            if (((JsonElement)entry.getValue()).isJsonArray()) {
                for (JsonElement b : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                    try {
                        blocks.add(b.getAsString());
                    }
                    catch (Throwable throwable) {}
                }
            } else if (((JsonElement)entry.getValue()).isJsonPrimitive()) {
                blocks.add(((JsonElement)entry.getValue()).getAsString());
            }
            if (blocks.isEmpty()) continue;
            map.put(faceGroup, Set.copyOf(blocks));
        }
        return map;
    }

    private static Set<String> parseRawFaces(JsonElement el) {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        if (el == null) {
            return set;
        }
        if (el.isJsonArray()) {
            for (JsonElement e : el.getAsJsonArray()) {
                try {
                    set.add(e.getAsString().toLowerCase());
                }
                catch (Throwable throwable) {}
            }
        } else if (el.isJsonPrimitive()) {
            set.add(el.getAsString().toLowerCase());
        }
        return set;
    }

    private static Set<Direction> parseFaces(JsonElement el) {
        EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
        if (el == null) {
            return set;
        }
        if (el.isJsonArray()) {
            for (JsonElement e : el.getAsJsonArray()) {
                try {
                    set.add(Direction.byName((String)e.getAsString().toLowerCase()));
                }
                catch (Throwable throwable) {}
            }
        } else if (!el.isJsonPrimitive() || "all".equalsIgnoreCase(el.getAsString())) {
            // empty if block
        }
        return set;
    }

    /**
     * Builds the machine's {@link MachineFlags} from its {@code "flags"} block.
     *
     * <p>Every field has a default (see {@link MachineFlags#DEFAULT}), so a machine JSON only names
     * what it changes:
     * <pre>
     *   "flags": { "recipes": false, "kinetics": true }
     * </pre>
     *
     * <p>{@code kinetics} defaults to whether the machine consumes stress, which is the only part
     * of the old behaviour that was ever derivable — the rest was spelled out by the now-removed
     * {@code no_processing} / {@code fuel_required} / {@code open_ui} / {@code continuous_fuel}
     * keys, which every shipped machine JSON has been migrated off.
     */
    private static MachineFlags parseFlags(JsonView view, MachineDefinition.PowerSpec power) {
        MachineFlags defaults = MachineFlags.DEFAULT
                .withKinetics(power != null && power.consumesStress());
        if (!view.has("flags")) return defaults;

        JsonView f = view.object("flags");
        return new MachineFlags(
                f.bool("recipes", defaults.recipes()),
                f.bool("fuel", defaults.fuel()),
                f.bool("continuous_fuel", defaults.continuousFuel()),
                f.bool("ui", defaults.ui()),
                f.bool("ui_tick", defaults.uiTick()),
                f.bool("kinetics", defaults.kinetics()),
                f.bool("io_pull", defaults.ioPull()),
                f.bool("renderers", defaults.renderers()),
                f.bool("scripts", defaults.scripts()),
                f.bool("animations", defaults.animations()),
                f.bool("redstone", defaults.redstone()));
    }

    private static List<MachineDefinition.TankSpec> parseTanks(JsonView view, String field) {
        ArrayList<MachineDefinition.TankSpec> out = new ArrayList<MachineDefinition.TankSpec>();
        for (JsonView tank : view.objectList(field)) {
            out.add(new MachineDefinition.TankSpec(tank.string("name", "tank" + out.size()), tank.rangedInt("capacity", 8000, 1, Integer.MAX_VALUE), tank.has("filter") ? tank.key("filter", "polyfills") : null));
        }
        return out;
    }

    private static IOConfiguration parseIO(JsonView view) {
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        MachineDefinitionLoader.applyGrants(view, "input", config, true);
        MachineDefinitionLoader.applyGrants(view, "output", config, false);
        return config;
    }

    private static void applyGrants(JsonView view, String field, IOConfiguration.Simple config, boolean input) {
        if (!view.has(field)) {
            return;
        }
        ArrayList<JsonView> entries = new ArrayList<JsonView>();
        if (view.raw().get(field).isJsonArray()) {
            entries.addAll(view.objectList(field));
        } else {
            entries.add(view.object(field));
        }
        for (JsonView entry : entries) {
            ArrayList<IOConfiguration.IOType> types = new ArrayList<IOConfiguration.IOType>();
            for (String typeName : entry.stringList("types")) {
                IOConfiguration.IOType type = null;
                for (IOConfiguration.IOType candidate : IOConfiguration.IOType.values()) {
                    if (!candidate.name().equalsIgnoreCase(typeName)) continue;
                    type = candidate;
                }
                if (type == null) {
                    throw entry.error("unknown io type '" + typeName + "'");
                }
                types.add(type);
            }
            ArrayList<Direction> faces = new ArrayList<Direction>();
            for (String faceName : entry.stringList("faces")) {
                List<Direction> group = MultiBlockDefinition.IOSpec.faceGroup(faceName);
                if (group == null) {
                    throw entry.error("unknown face or face group '" + faceName + "'");
                }
                faces.addAll(group);
            }
            if (faces.isEmpty()) {
                faces.addAll(List.of(Direction.values()));
            }
            for (IOConfiguration.IOType type : types) {
                for (Direction face : faces) {
                    if (input) {
                        config.addInput(type, face);
                        continue;
                    }
                    config.addOutput(type, face);
                }
            }
        }
    }

    private static int requireInMenu(JsonView view, int slot, int menuSize, String field) {
        if (slot < 0 || slot >= menuSize) {
            throw view.error("'" + field + "' slot " + slot + " is outside the menu (size " + menuSize + ")");
        }
        return slot;
    }

    private static void rejectReserved(JsonView view, int[] slots, int reserved, String field) {
        for (int slot : slots) {
            if (slot >= reserved) continue;
            throw view.error("'" + field + "' slot " + slot + " collides with the " + reserved + " reserved upgrade slots (0.." + (reserved - 1) + ")");
        }
    }

    private static int[] toArray(List<Integer> values, JsonView view, int menuSize, String field) {
        int[] out = new int[values.size()];
        for (int i = 0; i < values.size(); ++i) {
            int slot = values.get(i);
            if (slot < 0 || slot >= menuSize) {
                throw view.error("'" + field + "' slot " + slot + " is outside the menu (size " + menuSize + ")");
            }
            out[i] = slot;
        }
        return out;
    }

    private static String parseWhen(JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return "always";
        }
        if (el.isJsonPrimitive()) {
            return el.getAsString();
        }
        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("not")) {
                return "!(" + MachineDefinitionLoader.parseWhen(obj.get("not")) + ")";
            }
            if (obj.has("and")) {
                JsonArray arr = obj.get("and").getAsJsonArray();
                return "(" + MachineDefinitionLoader.parseWhen(arr.get(0)) + " && " + MachineDefinitionLoader.parseWhen(arr.get(1)) + ")";
            }
            if (obj.has("or")) {
                JsonArray arr = obj.get("or").getAsJsonArray();
                return "(" + MachineDefinitionLoader.parseWhen(arr.get(0)) + " || " + MachineDefinitionLoader.parseWhen(arr.get(1)) + ")";
            }
        }
        return "always";
    }

    private static float[] parseFloatArray(JsonView view, String field, int size, float[] defaults) {
        float[] result = (float[])defaults.clone();
        if (!view.has(field)) {
            return result;
        }
        JsonElement el = view.raw().get(field);
        if (!el.isJsonArray()) {
            return result;
        }
        JsonArray arr = el.getAsJsonArray();
        for (int i = 0; i < Math.min(arr.size(), size); ++i) {
            result[i] = arr.get(i).getAsFloat();
        }
        return result;
    }

    private static String readExprPrimitive(JsonView view, String field, String defaultVal) {
        if (!view.raw().has(field) || view.raw().get(field).isJsonNull()) {
            return defaultVal;
        }
        JsonElement el = view.raw().get(field);
        if (!el.isJsonPrimitive()) {
            return defaultVal;
        }
        JsonPrimitive p = el.getAsJsonPrimitive();
        return p.isString() ? p.getAsString() : String.valueOf(p.getAsDouble());
    }

    private static String parseLocationExpr(JsonView r) {
        boolean hasOffset;
        if (r.raw().has("location")) {
            JsonElement locEl = r.raw().get("location");
            if (locEl.isJsonObject()) {
                JsonObject o = locEl.getAsJsonObject();
                String lx = o.has("x") ? o.get("x").getAsString() : "0";
                String ly = o.has("y") ? o.get("y").getAsString() : "0";
                String lz = o.has("z") ? o.get("z").getAsString() : "0";
                return "[" + lx + ", " + ly + ", " + lz + "]";
            }
            if (locEl.isJsonArray()) {
                return locEl.toString();
            }
            if (locEl.isJsonPrimitive()) {
                return locEl.getAsString();
            }
        }
        boolean bl = hasOffset = r.raw().has("offset") || r.raw().has("offset_x") || r.raw().has("offset_y") || r.raw().has("offset_z");
        if (hasOffset) {
            float[] off = MachineDefinitionLoader.parseFloatArray(r, "offset", 3, new float[]{0.0f, 0.0f, 0.0f});
            String ox = r.raw().has("offset_x") ? r.string("offset_x") : String.valueOf(off[0]);
            String oy = r.raw().has("offset_y") ? r.string("offset_y") : String.valueOf(off[1]);
            String oz = r.raw().has("offset_z") ? r.string("offset_z") : String.valueOf(off[2]);
            return "[" + ox + ", " + oy + ", " + oz + "]";
        }
        return null;
    }

    private static String readExprPrimitive(JsonView view, String field) {
        return MachineDefinitionLoader.readExprPrimitive(view, field, "0");
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf(46);
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}

