/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.MachineDefinitionLoader;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.rotation.MotorDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.momirealms.craftengine.core.util.Key;

public final class MotorLoader {
    private MotorLoader() {
    }

    public static void bootstrap() {
        Registries.addLoader("motors", 100, MotorLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("motors", MotorLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger().info("Loaded " + MotorDefinition.REGISTRY.size() + " motor definitions.");
        }
    }

    private static void apply(JsonView view, String fileName) {
        MachineDefinition machine;
        Key id = view.has("id") ? view.key("id", "polyfills") : Key.of((String)"polyfills", (String)MotorLoader.stripExtension(fileName));
        LinkedHashMap<Key, MotorDefinition.FuelOutput> fuels = new LinkedHashMap<Key, MotorDefinition.FuelOutput>();
        JsonView fuelSection = view.object("fuels");
        for (MotorDefinition.FuelKind kind : MotorDefinition.FuelKind.values()) {
            String section = kind.name().toLowerCase(Locale.ROOT);
            if (!fuelSection.has(section)) continue;
            for (Map.Entry<String, JsonView> entry : fuelSection.objectMap(section)) {
                String defaultNamespace = kind == MotorDefinition.FuelKind.ITEM ? "minecraft" : "polyfills";
                Key fuelId = JsonView.parseKey(entry.getKey(), defaultNamespace, fuelSection.path() + " > " + section + " > " + entry.getKey());
                JsonView spec = entry.getValue();
                float rpm = spec.floating("rpm", 0.0f);
                float su = spec.floating("su", 0.0f);
                if (rpm <= 0.0f || su <= 0.0f) {
                    throw spec.error("a fuel must yield both rpm and su above zero");
                }
                fuels.put(fuelId, new MotorDefinition.FuelOutput(kind, rpm, su, spec.rangedInt("per_tick", 20, 1, 100000), spec.rangedInt("burn_time", 1600, 1, 1000000)));
            }
        }
        if (fuels.isEmpty()) {
            throw view.error("declares no fuels, so it could never turn");
        }
        ArrayList<RelativeDirection> outputFaces = new ArrayList<RelativeDirection>();
        for (String face : view.stringList("output_faces")) {
            RelativeDirection dir = null;
            for (RelativeDirection candidate : RelativeDirection.values()) {
                if (!candidate.name().equalsIgnoreCase(face)) continue;
                dir = candidate;
            }
            if (dir == null) {
                throw view.error("unknown output face '" + face + "'; expected one of " + Arrays.toString((Object[])RelativeDirection.values()));
            }
            outputFaces.add(dir);
        }
        if (outputFaces.isEmpty()) {
            outputFaces.add(RelativeDirection.FRONT);
        }
        MachineDefinition machineDefinition = machine = view.has("machine") ? MachineDefinitionLoader.parse(view.object("machine"), id) : null;
        if (machine != null) {
            MachineDefinition.REGISTRY.register(id, machine);
        }
        int upgradeSlots = view.rangedInt("upgrade_slots", 9, 0, 54);
        MotorDefinition.REGISTRY.register(id, new MotorDefinition(id, machine, fuels, List.copyOf(outputFaces), upgradeSlots, view.rangedInt("base_unlocked", Math.min(3, upgradeSlots), 0, Math.max(1, upgradeSlots)), (float)view.rangedDouble("base_overclock", 2.0, 0.0, 64.0)));
    }

    private static String stripExtension(String fileName) {
        String name = fileName.substring(fileName.lastIndexOf(47) + 1);
        int dot = name.lastIndexOf(46);
        return dot < 0 ? name : name.substring(0, dot);
    }
}

