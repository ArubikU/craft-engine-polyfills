package dev.arubik.craftengine.script;

import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Typed value union for the PolyFill expression/script system.
 * Unlike the old PolyValue, this never touches Bukkit — ItemStack is always NMS.
 */
public sealed interface ScriptValue {

    record Num(double value) implements ScriptValue {}
    record Bool(boolean value) implements ScriptValue {}
    record Str(String value) implements ScriptValue {}
    record Item(ItemStack stack) implements ScriptValue {}
    record Array(List<ScriptValue> elements) implements ScriptValue {}
    record Obj(String typeName, Object instance) implements ScriptValue {}
    record Null() implements ScriptValue {}

    ScriptValue NULL = new Null();

    /**
     * Implemented by an {@link Obj} instance that has a meaningful scalar reading, so the object
     * can be used directly in arithmetic and comparisons.
     *
     * <p>This lets a property grow from a bare number into a real object without breaking the
     * scripts that already treat it as a number: {@code Machine.redstone} returns a Redstone
     * object exposing {@code .input}/{@code .output}/{@code .set(n)}, yet {@code Machine.redstone > 0}
     * still reads its input level.
     */
    interface Numeric {
        double numericValue();
    }

    static ScriptValue of(double v) { return new Num(v); }
    static ScriptValue of(boolean v) { return new Bool(v); }
    static ScriptValue of(String v) { return v == null ? NULL : new Str(v); }
    static ScriptValue ofItem(ItemStack s) { return s == null || s.isEmpty() ? NULL : new Item(s); }
    static ScriptValue ofObj(String typeName, Object instance) { return instance == null ? NULL : new Obj(typeName, instance); }

    default boolean asBool() {
        return switch (this) {
            case Bool b -> b.value();
            case Num n -> n.value() != 0;
            case Item i -> i.stack() != null && !i.stack().isEmpty();
            case Array a -> !a.elements().isEmpty();
            case Str s -> !s.value().isEmpty();
            case Obj o -> o.instance() instanceof Numeric n ? n.numericValue() != 0 : o.instance() != null;
            case Null ignored -> false;
        };
    }

    default double asNum() {
        return switch (this) {
            case Num n -> n.value();
            case Bool b -> b.value() ? 1.0 : 0.0;
            case Item i -> (i.stack() != null && !i.stack().isEmpty()) ? i.stack().getCount() : 0;
            case Array a -> a.elements().size();
            case Str s -> { try { yield Double.parseDouble(s.value()); } catch (Exception e) { yield 0; } }
            case Obj o -> o.instance() instanceof Numeric n ? n.numericValue() : 0;
            case Null ignored -> 0;
        };
    }

    default String asStr() {
        return switch (this) {
            case Str s -> s.value();
            case Num n -> {
                double v = n.value();
                if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15)
                    yield String.valueOf((long) v);
                yield String.valueOf(v);
            }
            case Bool b -> String.valueOf(b.value());
            case Null ignored -> "null";
            default -> "?";
        };
    }

    default ScriptValue getProperty(String prop) {
        if (this instanceof Obj o && o.instance() != null) {
            PolyType type = PolyTypeRegistry.get(o.typeName());
            if (type != null) {
                PolyType.PropertyHandler h = type.resolveProperty(prop);
                if (h != null) {
                    try { return h.get(o.instance()); } catch (Throwable ignored) {}
                }
            }
        }
        if (this instanceof Array a) {
            if ("length".equals(prop) || "size".equals(prop)) return of(a.elements().size());
        }
        return NULL;
    }

    /**
     * Check if this value is an instance of the given type name,
     * respecting the PolyType inheritance chain.
     * e.g. a "Player" Obj will return true for both "Player" and "Entity".
     */
    default boolean isInstanceOf(String typeName) {
        if (!(this instanceof Obj o)) return false;
        PolyType type = PolyTypeRegistry.get(o.typeName());
        while (type != null) {
            if (type.name().equals(typeName)) return true;
            type = type.parent();
        }
        return false;
    }

    default ScriptValue callMethod(String method, List<ScriptValue> args) {
        if (this instanceof Obj o && o.instance() != null) {
            PolyType type = PolyTypeRegistry.get(o.typeName());
            if (type != null) {
                PolyType.MethodHandler h = type.resolveMethod(method);
                if (h != null) {
                    try { return h.call(o.instance(), args); } catch (Throwable ignored) {}
                }
            }
        }
        if (this instanceof Array a) {
            if ("get".equals(method) && !args.isEmpty()) {
                int idx = (int) args.get(0).asNum();
                if (idx >= 0 && idx < a.elements().size()) return a.elements().get(idx);
            }
        }
        return NULL;
    }
}
