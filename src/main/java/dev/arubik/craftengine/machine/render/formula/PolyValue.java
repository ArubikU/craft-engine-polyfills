package dev.arubik.craftengine.machine.render.formula;

/**
 * Typed union for all values that flow through a {@link PolyFormula} expression.
 *
 * <p>The sealed hierarchy covers every kind of data a machine can expose:
 * numbers, booleans, strings, single item-stacks, item-stack lists, and null.
 * Coercion methods ({@link #asBool()}, {@link #asNum()}, {@link #asStr()})
 * let arithmetic and comparison operators work across types without explicit
 * casts in the parser.</p>
 */
public sealed interface PolyValue {

    record Num(double value) implements PolyValue {}
    record Bool(boolean value) implements PolyValue {}
    /** stack may be null — represents an empty slot. */
    record Item(org.bukkit.inventory.ItemStack stack) implements PolyValue {}
    /** Generic typed array — holds any PolyValue elements. Supports .length, [n], .get(n). */
    record Array(java.util.List<PolyValue> elements) implements PolyValue {}
    record Str(String value) implements PolyValue {}
    /** Wraps a PolyClass instance so member access (.property / .method()) chains work. */
    record Obj(PolyClass inner) implements PolyValue {}
    record Null() implements PolyValue {}

    // ---- Coercions ---------------------------------------------------------

    /** Truthy if the value is non-zero / non-empty / non-null. */
    default boolean asBool() {
        return switch (this) {
            case Bool b    -> b.value();
            case Num n     -> n.value() != 0;
            case Item i    -> i.stack() != null && !i.stack().getType().isAir();
            case Array a   -> !a.elements().isEmpty();
            case Str s     -> !s.value().isEmpty();
            case Obj o     -> o.inner() != null;
            case Null ignored -> false;
        };
    }

    /** Numeric coercion.  Items and ItemLists resolve to their total count. */
    default double asNum() {
        return switch (this) {
            case Num n     -> n.value();
            case Bool b    -> b.value() ? 1.0 : 0.0;
            case Item i    -> (i.stack() != null && !i.stack().getType().isAir()) ? i.stack().getAmount() : 0;
            case Array a   -> a.elements().size();
            case Str s     -> { try { yield Double.parseDouble(s.value()); } catch (Exception e) { yield 0; } }
            case Obj ignored -> 0;
            case Null ignored -> 0;
        };
    }

    /** String coercion. */
    default String asStr() {
        return switch (this) {
            case Str s     -> s.value();
            case Num n     -> String.valueOf(n.value());
            case Bool b    -> String.valueOf(b.value());
            case Null ignored -> "null";
            default        -> "?";
        };
    }

    // ---- Factories ---------------------------------------------------------

    static PolyValue of(double v)  { return new Num(v); }
    static PolyValue of(boolean v) { return new Bool(v); }
    static PolyValue of(String v)  { return v == null ? new Null() : new Str(v); }
    static PolyValue ofItem(org.bukkit.inventory.ItemStack s) { return new Item(s); }

    PolyValue NULL = new Null();
}
