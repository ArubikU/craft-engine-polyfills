package dev.arubik.craftengine.pipe;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.MachineDefinitionLoader;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code pumps/*.json}: one file per pump, describing it whole.
 *
 * <p>
 * A pump has two halves — the extraction numbers that drive its world scan, and
 * the machine surface it shares with every other machine (fuel, upgrades,
 * overclock, tanks, buttons). Those were briefly split across {@code pump_types/}
 * and {@code machines/}, which meant two files repeating the same id and free to
 * drift apart. One file now feeds both registries under a single id.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:machine_pump",
 *   "block": "cml:iron_pump",
 *   "resource": "fluid",              // fluid | gas
 *
 *   "pump": {                         // how it draws from the world
 *     "extract_per_tick": 1000,        // buffer size comes from the machine tank below
 *     "push_per_tick": 1000,
 *     "pressure": 10,                 // head added, i.e. how high it lifts
 *     "extract_tick_rate": 10,        // ticks between draws
 *     "max_points": 8.0,              // extraction points one pump may claim
 *     "max_vein_blocks": 512,         // safety cap on a vein scan
 *     "mb_per_point": 10
 *   },
 *
 *   "machine": {                      // exactly the machines/*.json shape
 *     "title": "Pump",
 *     "menu_size": 54,
 *     "fuel_required": true,
 *     "slots": { "fuel": [11], "info": 4,
 *                "upgrade": { "count": 9, "base_unlocked": 3 } },
 *     "buttons": [ ... ],
 *     "fluid_tanks": [ { "name": "buffer", "capacity": 8000 } ]
 *   }
 * }
 * }</pre>
 */
public final class PumpLoader {

    private PumpLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        // PHASE_DEFINITIONS because it also produces a MachineDefinition, which sits in
        // that phase; the pump numbers have no dependency that needs PHASE_TYPES.
        Registries.addLoader("pumps", Registries.PHASE_DEFINITIONS, PumpLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("pumps", PumpLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + count + " pumps (" + PumpType.REGISTRY.size() + " pump types).");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", PumpType.NAMESPACE)
                : Key.of(PumpType.NAMESPACE, stripExtension(fileName));

        PumpType type = PumpType.getOrCreate(id);
        if (type == null)
            throw view.error("pump registry is frozen; '" + id + "' cannot be defined now");

        // The machine half is parsed first because the pump's internal buffer IS its
        // tank — declaring a `capacity` alongside a tank of the same size would be the
        // same number written twice, free to drift. The tank is the authority; a
        // `pump.capacity` is only needed for a pump that declares no tank at all.
        MachineDefinition machine = view.has("machine")
                ? MachineDefinitionLoader.parse(view.object("machine"), id)
                : null;

        PumpType.PumpProperties base = type.properties();
        JsonView pump = view.has("pump") ? view.object("pump") : view;
        int tankCapacity = tankCapacity(machine, base.capacity());
        type.applyProperties(new PumpType.PumpProperties(
                view.key("block", "cml", base.blockId()),
                view.enumValue("resource", PipeType.Resource.class, base.resource()),
                pump.rangedInt("capacity", tankCapacity, 1, Integer.MAX_VALUE),
                pump.rangedInt("extract_per_tick", base.extractPerTick(), 1, Integer.MAX_VALUE),
                pump.rangedInt("push_per_tick", base.pushPerTick(), 1, Integer.MAX_VALUE),
                pump.rangedInt("pressure", base.pressure(), 0, 4096),
                pump.rangedInt("extract_tick_rate", base.extractTickRate(), 1, 1200),
                pump.rangedDouble("max_points", base.maxPoints(), 0.0, 4096.0),
                pump.rangedInt("max_vein_blocks", base.maxVeinBlocks(), 1, 1_000_000),
                pump.rangedInt("mb_per_point", base.mbPerPoint(), 1, 100_000)));

        // The machine half lands in the normal machine registry under the SAME id, so a
        // pump block can point at it with `machine:` exactly like any other machine.
        if (machine != null)
            MachineDefinition.REGISTRY.register(id, machine);
    }

    /** The buffer size a pump's own tank declares, or {@code fallback} if it has none. */
    private static int tankCapacity(MachineDefinition machine, int fallback) {
        if (machine == null)
            return fallback;
        if (!machine.fluidTanks().isEmpty())
            return machine.fluidTanks().get(0).capacity();
        if (!machine.gasTanks().isEmpty())
            return machine.gasTanks().get(0).capacity();
        return fallback;
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
