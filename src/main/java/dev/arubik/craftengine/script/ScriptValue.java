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

    // ---- Dynamic-constant bootstraps, for compiled code's literals ----
    //
    // A literal that has to reach something as a ScriptValue used to compile to LDC + ScriptValue.of
    // at EVERY occurrence, allocating a fresh Str/Num each time the line ran — for a value that can
    // never change. As a `condy` (JVMS 4.4.10 dynamically-computed constant) it is one LDC of a
    // constant-pool entry the JVM resolves once, on first execution, and caches forever: no call, no
    // allocation, and one instruction less of bytecode at each of the ~2000 sites in a real script
    // corpus.
    //
    // Sharing one instance across every occurrence is safe because these records are immutable and
    // nothing compares ScriptValues by identity — the same reasoning that already lets NULL be a
    // singleton and Cache intern small Nums.

    /** Bootstrap for a constant {@link Str}. The value rides as a static bootstrap argument rather
     *  than in the constant's name, so no assumption is made about what a name may contain. */
    static ScriptValue constStr(java.lang.invoke.MethodHandles.Lookup lookup, String name,
                                Class<?> type, String value) {
        return of(value);
    }

    /** Bootstrap for a constant {@link Num}. */
    static ScriptValue constNum(java.lang.invoke.MethodHandles.Lookup lookup, String name,
                                Class<?> type, double value) {
        return of(value);
    }

    /** Bootstrap for a constant {@link Bool}. A condy static argument cannot be a boolean, so it
     *  arrives as an int — 0 is false, anything else true. */
    static ScriptValue constBool(java.lang.invoke.MethodHandles.Lookup lookup, String name,
                                 Class<?> type, int value) {
        return of(value != 0);
    }

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

    /**
     * Interned {@link Num}s for small integral values, and the two {@link Bool}s.
     *
     * <p>The compiled scripts deal almost entirely in small integers — redstone levels 0-15, mode
     * flags, booleans-as-numbers, loop counters. Across eight decompiled generated scripts there
     * were 387 {@code ScriptValue.of(double)} sites and 258 of them were literally 0, 1 or 2. Most
     * of those allocations cannot be removed by any compiler transformation, because the value
     * genuinely escapes into the context's {@code Map<String, ScriptValue>} — but they can be made
     * free.
     *
     * <p>Safe because {@code Num}/{@code Bool} are immutable records with value-based equality, and
     * nothing in this codebase compares a {@code ScriptValue} by reference except against
     * {@code NULL}, which is already a singleton.
     */
    final class Cache {
        private Cache() {}
        static final int LOW = -128, HIGH = 1024;
        static final Num[] NUMS = new Num[HIGH - LOW + 1];
        static final Bool TRUE = new Bool(true), FALSE = new Bool(false);
        static {
            for (int i = 0; i < NUMS.length; i++) NUMS[i] = new Num(LOW + i);
        }
    }

    static ScriptValue of(double v) {
        int i = (int) v;
        // Bit-exact comparison, not `i == v`: that would also match -0.0 (whose record equality and
        // reciprocal sign differ from 0.0) and would need a separate NaN guard.
        if (i >= Cache.LOW && i <= Cache.HIGH
                && Double.doubleToRawLongBits(v) == Double.doubleToRawLongBits((double) i)) {
            return Cache.NUMS[i - Cache.LOW];
        }
        return new Num(v);
    }
    static ScriptValue of(boolean v) { return v ? Cache.TRUE : Cache.FALSE; }
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
            // Shared with the compiler's string-concat path, which renders a raw double without
            // allocating a Num first — see ScriptFormula.numToStr.
            case Num n -> ScriptFormula.numToStr(n.value());
            case Bool b -> String.valueOf(b.value());
            case Null ignored -> "null";
            default -> "?";
        };
    }

    default ScriptValue getProperty(String prop) {
        if (this instanceof Obj o && o.instance() != null) {
            // A raw PolyClass instance (e.g. FormConditionClass, bound via .typed(...) under a name
            // that ISN'T its own registered PolyType — "World" here is already taken by WorldType)
            // owns its own dispatch and was never meant to go through the registry at all.
            if (o.instance() instanceof PolyClass pc) {
                try { return pc.get(prop); } catch (Throwable ignored) { return NULL; }
            }
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
            // See getProperty's PolyClass branch above — same reasoning, same dead-code bug: this
            // fell through to a PolyTypeRegistry lookup for "World" (the UNRELATED WorldType, which
            // has no is_gas_provider/block_id), so every FormConditionClass-bound World.*(...) call
            // in a multiblock's can_form silently returned NULL — a can_form condition that always
            // reported "not met" no matter what the world actually looked like.
            if (o.instance() instanceof PolyClass pc) {
                try { return pc.call(method, args); } catch (Throwable ignored) { return NULL; }
            }
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
