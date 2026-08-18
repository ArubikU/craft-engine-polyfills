/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.momirealms.craftengine.core.util.Key;

public record MotorDefinition(Key id, MachineDefinition machine, Map<Key, FuelOutput> fuels, List<RelativeDirection> outputFaces, int upgradeSlots, int baseUnlocked, float baseOverclock) {
    public static final Registry<MotorDefinition> REGISTRY = Registries.create("motor");

    public FuelOutput fuel(Key fuelId) {
        return this.fuels.get(fuelId);
    }

    public Map<Key, FuelOutput> fuelsOfKind(FuelKind kind) {
        LinkedHashMap<Key, FuelOutput> out = new LinkedHashMap<Key, FuelOutput>();
        for (Map.Entry<Key, FuelOutput> e : this.fuels.entrySet()) {
            if (e.getValue().kind() != kind) continue;
            out.put(e.getKey(), e.getValue());
        }
        return out;
    }

    public static MotorDefinition byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(58) >= 0 ? Key.of((String)trimmed) : Key.of((String)"polyfills", (String)trimmed));
    }

    @Override
    public String toString() {
        return String.valueOf(this.id) + "(" + this.fuels.size() + " fuel(s))";
    }

    public record FuelOutput(FuelKind kind, float rpm, float su, int perTick, int burnTime) {
    }

    public static enum FuelKind {
        GAS,
        FLUID,
        ITEM;

    }
}

