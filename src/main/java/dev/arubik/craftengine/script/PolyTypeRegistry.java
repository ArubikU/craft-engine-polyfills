package dev.arubik.craftengine.script;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Global registry of all script-visible types. Supports:
 * <ul>
 *   <li>Named registration: {@code define("Player").parent("Entity")}</li>
 *   <li>Inheritance: child types inherit parent properties/methods</li>
 *   <li>Extension: {@code extend("Entity", t -> t.property("custom", ...))} adds to existing types</li>
 *   <li>Replacement: {@code extend("Entity", t -> t.replaceMethod("kill", ...))} overrides</li>
 * </ul>
 *
 * Thread-safe. Types are resolved lazily with cached inheritance chains.
 */
public final class PolyTypeRegistry {

    private static final Map<String, PolyType> TYPES = new ConcurrentHashMap<>();

    private PolyTypeRegistry() {}

    /**
     * Define a new root type (no parent).
     */
    public static PolyType define(String name) {
        PolyType type = new PolyType(name, null);
        TYPES.put(name, type);
        return type;
    }

    /**
     * Define a new type that inherits from an existing parent.
     * Parent must be registered before the child.
     */
    public static PolyType define(String name, String parentName) {
        PolyType parent = TYPES.get(parentName);
        if (parent == null) {
            throw new IllegalStateException("Parent type '" + parentName + "' not registered. Register parents first.");
        }
        PolyType type = new PolyType(name, parent);
        TYPES.put(name, type);
        return type;
    }

    /**
     * Extend an existing type — add properties, methods, or replace them.
     */
    public static void extend(String name, Consumer<PolyType> configurator) {
        PolyType type = TYPES.get(name);
        if (type == null) {
            throw new IllegalStateException("Type '" + name + "' not registered.");
        }
        configurator.accept(type);
    }

    /**
     * Get a type by name. Returns null if not registered.
     */
    public static PolyType get(String name) {
        return TYPES.get(name);
    }

    /**
     * Check if a type is registered.
     */
    public static boolean has(String name) {
        return TYPES.containsKey(name);
    }

    /**
     * Get all registered type names.
     */
    public static Set<String> typeNames() {
        return Collections.unmodifiableSet(TYPES.keySet());
    }

    /**
     * Create a ScriptValue wrapping an object as a known type.
     */
    public static ScriptValue wrap(String typeName, Object instance) {
        return ScriptValue.ofObj(typeName, instance);
    }

    /**
     * Resolve property on an object given its type name.
     */
    public static ScriptValue getProperty(String typeName, Object instance, String property) {
        if (instance == null) return ScriptValue.NULL;
        PolyType type = TYPES.get(typeName);
        if (type == null) return ScriptValue.NULL;
        PolyType.PropertyHandler h = type.resolveProperty(property);
        if (h == null) return ScriptValue.NULL;
        try { return h.get(instance); } catch (Throwable t) { return ScriptValue.NULL; }
    }

    /**
     * Call method on an object given its type name.
     */
    public static ScriptValue callMethod(String typeName, Object instance, String method, List<ScriptValue> args) {
        if (instance == null) return ScriptValue.NULL;
        PolyType type = TYPES.get(typeName);
        if (type == null) return ScriptValue.NULL;
        PolyType.MethodHandler h = type.resolveMethod(method);
        if (h == null) return ScriptValue.NULL;
        try { return h.call(instance, args); } catch (Throwable t) { return ScriptValue.NULL; }
    }

    /**
     * Reset all types. Called on plugin reload.
     */
    public static void clear() {
        TYPES.clear();
    }
}
