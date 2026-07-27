package dev.arubik.craftengine.machine;

import java.util.ArrayList;
import java.util.List;

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
 *     "info":   4,
 *     "upgrade": { "count": 9, "base_unlocked": 3 }   // reserved indices 0..8, own page
 *   },
 *   "fluid_tanks": [ { "name": "input", "capacity": 8000, "filter": "polyfills:water" } ],
 *   "gas_tanks":   [ { "name": "output", "capacity": 8000 } ],
 *   "fuel_required": true,
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
        int info = slots.has("info") ? slots.rangedInt("info", -1, 0, menuSize - 1) : -1;

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
        // their own page, never the main one. A main-page slot pointing into that range
        // would silently share storage with an upgrade module, so reject it loudly.
        int reserved = upgrades.isInline() ? 0 : upgrades.count();
        rejectReserved(view, inputs, reserved, "input");
        rejectReserved(view, outputs, reserved, "output");
        rejectReserved(view, fuels, reserved, "fuel");
        if (info >= 0 && info < reserved)
            throw view.error("'info' slot " + info + " collides with the " + reserved
                    + " reserved upgrade slots (0.." + (reserved - 1) + ")");

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

        return new MachineDefinition(id,
                view.string("recipe_type", id.value()), view.string("title", id.value()), menuSize,
                inputs, outputs, fuels, upgrades, info, fluidTanks, gasTanks,
                view.bool("fuel_required", true), io, buttons, power);
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

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
