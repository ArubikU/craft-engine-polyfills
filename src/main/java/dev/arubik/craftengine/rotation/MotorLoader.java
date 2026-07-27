package dev.arubik.craftengine.rotation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.MachineDefinitionLoader;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code motors/*.json} into {@link MotorDefinition#REGISTRY}.
 *
 * <pre>{@code
 * {
 *   "id": "polyfills:gas_motor_mk1",
 *   "buffer": 10000,                  // fuel buffer for gas/fluid fuels, mB
 *   "output_faces": ["front"],        // where the shaft drives, facing-relative
 *   "upgrade_slots": 9,
 *   "base_unlocked": 3,
 *   "base_overclock": 2.0,            // headroom over a fuel's base rpm
 *
 *   "fuels": {
 *     "gas":   { "polyfills:steam": { "rpm": 32, "su": 512, "per_tick": 20 } },
 *     "fluid": { "polyfills:lava":  { "rpm": 24, "su": 384, "per_tick": 5 } },
 *     "item":  { "minecraft:coal":  { "rpm": 16, "su": 256, "burn_time": 1600 } }
 *   },
 *
 *   "machine": { ...the machines/*.json body: slots, bars, buttons... }
 * }
 * }</pre>
 *
 * <p>
 * A motor does not name its block — the block config points at the motor, the
 * way it already points at a machine. Its menu comes from the embedded
 * {@code machine} body, because a motor's UI is a machine's UI.
 */
public final class MotorLoader {

    private MotorLoader() {
    }

    /** Registers this loader with the central load pipeline. */
    public static void bootstrap() {
        // PHASE_DEFINITIONS: the fuel table names gases, which load in PHASE_TYPES.
        Registries.addLoader("motors", Registries.PHASE_DEFINITIONS, MotorLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("motors", MotorLoader::apply);
        if (count > 0)
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + MotorDefinition.REGISTRY.size() + " motor definitions.");
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName));

        // Fuels are grouped by kind: a gas or liquid burns out of the buffer, an item
        // burns out of a slot, so the two carry different numbers.
        Map<Key, MotorDefinition.FuelOutput> fuels = new LinkedHashMap<>();
        JsonView fuelSection = view.object("fuels");
        for (MotorDefinition.FuelKind kind : MotorDefinition.FuelKind.values()) {
            String section = kind.name().toLowerCase(java.util.Locale.ROOT);
            if (!fuelSection.has(section))
                continue;
            for (var entry : fuelSection.objectMap(section)) {
                String defaultNamespace = kind == MotorDefinition.FuelKind.ITEM ? "minecraft" : "polyfills";
                Key fuelId = JsonView.parseKey(entry.getKey(), defaultNamespace,
                        fuelSection.path() + " > " + section + " > " + entry.getKey());
                JsonView spec = entry.getValue();
                float rpm = spec.floating("rpm", 0f);
                float su = spec.floating("su", 0f);
                if (rpm <= 0 || su <= 0)
                    throw spec.error("a fuel must yield both rpm and su above zero");
                fuels.put(fuelId, new MotorDefinition.FuelOutput(kind, rpm, su,
                        spec.rangedInt("per_tick", 20, 1, 100000),
                        spec.rangedInt("burn_time", 1600, 1, 1_000_000)));
            }
        }
        if (fuels.isEmpty())
            throw view.error("declares no fuels, so it could never turn");

        // Facing-relative, so one definition works at every rotation.
        List<RelativeDirection> outputFaces = new java.util.ArrayList<>();
        for (String face : view.stringList("output_faces")) {
            RelativeDirection dir = null;
            for (RelativeDirection candidate : RelativeDirection.values())
                if (candidate.name().equalsIgnoreCase(face))
                    dir = candidate;
            if (dir == null)
                throw view.error("unknown output face '" + face + "'; expected one of "
                        + java.util.Arrays.toString(RelativeDirection.values()));
            outputFaces.add(dir);
        }
        if (outputFaces.isEmpty())
            outputFaces.add(RelativeDirection.FRONT);

        MachineDefinition machine = view.has("machine")
                ? MachineDefinitionLoader.parse(view.object("machine"), id)
                : null;
        if (machine != null)
            MachineDefinition.REGISTRY.register(id, machine);

        int upgradeSlots = view.rangedInt("upgrade_slots", 9, 0, 54);
        MotorDefinition.REGISTRY.register(id, new MotorDefinition(id, machine,
                view.rangedInt("buffer", 10000, 1, Integer.MAX_VALUE),
                fuels, List.copyOf(outputFaces), upgradeSlots,
                view.rangedInt("base_unlocked", Math.min(3, upgradeSlots), 0, Math.max(1, upgradeSlots)),
                (float) view.rangedDouble("base_overclock", 2.0, 0.0, 64.0)));
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
