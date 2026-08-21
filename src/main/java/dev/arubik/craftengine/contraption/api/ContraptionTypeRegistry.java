/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.api;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;
import dev.arubik.craftengine.contraption.type.LinearContraptionType;
import dev.arubik.craftengine.contraption.type.MachineContraptionType;
import dev.arubik.craftengine.contraption.type.MinecartContraptionType;
import dev.arubik.craftengine.contraption.type.PhysContraptionType;
import dev.arubik.craftengine.contraption.type.RotationalContraptionType;
import dev.arubik.craftengine.contraption.type.VehicleContraptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionTypeRegistry {
    private static final Map<Key, ContraptionType> TYPES = new HashMap<Key, ContraptionType>();

    private ContraptionTypeRegistry() {
    }

    public static void register(Key id, ContraptionType type) {
        if (TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Contraption type already registered: " + String.valueOf(id));
        }
        TYPES.put(id, type);
    }

    public static ContraptionType get(Key id) {
        return TYPES.get(id);
    }

    public static Set<Key> getRegisteredTypes() {
        return Set.copyOf(TYPES.keySet());
    }

    public static boolean isRegistered(Key id) {
        return TYPES.containsKey(id);
    }

    public static Collection<ContraptionType> getAllTypes() {
        return List.copyOf(TYPES.values());
    }

    public static void registerBuiltinTypes() {
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"linear"), LinearContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"rotational"), RotationalContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"phys"), PhysContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"vehicle"), VehicleContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"minecart"), MinecartContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"ghast"), GhastContraptionType.INSTANCE);
        ContraptionTypeRegistry.register(Key.of((String)"polyfills", (String)"machine"), MachineContraptionType.INSTANCE);
    }
}

