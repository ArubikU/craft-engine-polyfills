package dev.arubik.craftengine.contraption.api;

import net.momirealms.craftengine.core.util.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Central registry for custom contraption types.
 *
 * <p>
 * External plugins register custom contraption types (submarines, airships, mechs) here.
 * Bearing blocks reference these types by ID, and the contraption system instantiates
 * the registered type when assembling.
 *
 * <p>
 * <b>Built-in types</b>: LINEAR, ROTATIONAL, VEHICLE, MINECART, GHAST_HARNESS (registered automatically)
 *
 * <p>
 * <b>Example</b>:
 * <pre>{@code
 * // Plugin onEnable:
 * ContraptionTypeRegistry.register(Key.of("myplugin", "submarine"), new SubmarineType());
 *
 * // Bearing block config:
 * behavior:
 *   type: polyfills:bearing_block
 *   contraption_type: myplugin:submarine
 * }</pre>
 */
public final class ContraptionTypeRegistry {

    private static final Map<Key, ContraptionType> TYPES = new HashMap<>();

    private ContraptionTypeRegistry() {
    }

    /**
     * Registers a custom contraption type.
     *
     * @param id   unique identifier (e.g., "myplugin:submarine")
     * @param type the type implementation
     * @throws IllegalArgumentException if id is already registered
     */
    public static void register(Key id, ContraptionType type) {
        if (TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Contraption type already registered: " + id);
        }
        TYPES.put(id, type);
    }

    /**
     * Gets a registered contraption type by ID.
     *
     * @param id the type identifier
     * @return the type, or null if not registered
     */
    public static ContraptionType get(Key id) {
        return TYPES.get(id);
    }

    /**
     * All registered contraption type IDs.
     *
     * @return immutable set of registered IDs
     */
    public static Set<Key> getRegisteredTypes() {
        return Set.copyOf(TYPES.keySet());
    }

    /**
     * Checks if a type is registered.
     *
     * @param id the type identifier
     * @return true if registered
     */
    public static boolean isRegistered(Key id) {
        return TYPES.containsKey(id);
    }

    /**
     * All registered contraption types.
     *
     * @return immutable collection of registered types
     */
    public static java.util.Collection<ContraptionType> getAllTypes() {
        return java.util.List.copyOf(TYPES.values());
    }

    /**
     * Registers all built-in contraption types.
     * Called automatically during plugin initialization.
     */
    public static void registerBuiltinTypes() {
        register(Key.of("polyfills", "linear"), dev.arubik.craftengine.contraption.type.LinearContraptionType.INSTANCE);
        register(Key.of("polyfills", "rotational"), dev.arubik.craftengine.contraption.type.RotationalContraptionType.INSTANCE);
        register(Key.of("polyfills", "phys"), dev.arubik.craftengine.contraption.type.PhysContraptionType.INSTANCE);
        register(Key.of("polyfills", "vehicle"), dev.arubik.craftengine.contraption.type.VehicleContraptionType.INSTANCE);
        register(Key.of("polyfills", "minecart"), dev.arubik.craftengine.contraption.type.MinecartContraptionType.INSTANCE);
        register(Key.of("polyfills", "ghast"), dev.arubik.craftengine.contraption.type.GhastContraptionType.INSTANCE);
    }
}
