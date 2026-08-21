package dev.arubik.craftengine.script;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A named script-visible type. Defines properties and methods accessible via
 * PolyFormula expressions. Supports single-parent inheritance — a child type
 * inherits all parent properties/methods, but may override any of them.
 *
 * Types are identified by string name (e.g. "Entity", "Player", "Block").
 * Register them via {@link PolyTypeRegistry}.
 */
public final class PolyType {

    private final String name;
    private final PolyType parent;
    private final Map<String, PropertyHandler> properties = new ConcurrentHashMap<>();
    private final Map<String, MethodHandler> methods = new ConcurrentHashMap<>();
    private DefaultPropertyHandler defaultProperty = null;
    private DefaultMethodHandler defaultMethod = null;

    PolyType(String name, PolyType parent) {
        this.name = name;
        this.parent = parent;
    }

    public String name() { return name; }
    public PolyType parent() { return parent; }

    public PolyType property(String name, PropertyHandler handler) {
        properties.put(name, handler);
        return this;
    }

    public PolyType method(String name, MethodHandler handler) {
        methods.put(name, handler);
        return this;
    }

    public PolyType replaceMethod(String name, MethodHandler handler) {
        methods.put(name, handler);
        return this;
    }

    public PolyType replaceProperty(String name, PropertyHandler handler) {
        properties.put(name, handler);
        return this;
    }

    /** Fallback handler called when no named property matches. */
    public PolyType defaultProperty(DefaultPropertyHandler handler) {
        this.defaultProperty = handler;
        return this;
    }

    /** Fallback handler called when no named method matches. */
    public PolyType defaultMethod(DefaultMethodHandler handler) {
        this.defaultMethod = handler;
        return this;
    }

    public PropertyHandler resolveProperty(String prop) {
        PropertyHandler h = properties.get(prop);
        if (h != null) return h;
        if (defaultProperty != null) {
            DefaultPropertyHandler dp = defaultProperty;
            return instance -> dp.get(instance, prop);
        }
        if (parent != null) return parent.resolveProperty(prop);
        return null;
    }

    public MethodHandler resolveMethod(String method) {
        MethodHandler h = methods.get(method);
        if (h != null) return h;
        if (defaultMethod != null) {
            DefaultMethodHandler dm = defaultMethod;
            return (instance, args) -> dm.call(instance, method, args);
        }
        if (parent != null) return parent.resolveMethod(method);
        return null;
    }

    public Set<String> allPropertyNames() {
        Set<String> result = new LinkedHashSet<>();
        if (parent != null) result.addAll(parent.allPropertyNames());
        result.addAll(properties.keySet());
        return result;
    }

    public Set<String> allMethodNames() {
        Set<String> result = new LinkedHashSet<>();
        if (parent != null) result.addAll(parent.allMethodNames());
        result.addAll(methods.keySet());
        return result;
    }

    @FunctionalInterface
    public interface PropertyHandler {
        ScriptValue get(Object instance);
    }

    @FunctionalInterface
    public interface MethodHandler {
        ScriptValue call(Object instance, List<ScriptValue> args);
    }

    @FunctionalInterface
    public interface DefaultPropertyHandler {
        ScriptValue get(Object instance, String propertyName);
    }

    @FunctionalInterface
    public interface DefaultMethodHandler {
        ScriptValue call(Object instance, String methodName, List<ScriptValue> args);
    }
}
