package dev.arubik.craftengine.machine.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.momirealms.craftengine.core.util.Key;

/**
 * A small, Mojang-style attribute system for machines. Machines expose named
 * {@link #base(Key) attributes} (e.g. {@code polyfill:generation}); upgrades contribute
 * {@link Mod modifiers} with one of the three vanilla {@link Operation operations}, and
 * {@link #compute} folds them into final values using exactly Mojang's math:
 *
 * <pre>
 *   x = base + Σ ADD_VALUE
 *   x = x × (1 + Σ ADD_MULTIPLIED_BASE)
 *   for each ADD_MULTIPLIED_TOTAL m: x = x × (1 + m)
 * </pre>
 *
 * This makes positive / negative / stacked modifiers trivial and lets a single upgrade
 * buff one attribute while debuffing another (a "scrapped" upgrade).
 */
public final class MachineAttributes {

    private MachineAttributes() {
    }

    /** Vanilla-equivalent modifier operations. */
    public enum Operation {
        ADD_VALUE,            // flat add
        ADD_MULTIPLIED_BASE,  // × (1 + Σ) of base
        ADD_MULTIPLIED_TOTAL  // × (1 + each) of running total
    }

    /** One modifier: bump {@code attribute} by {@code amount} via {@code operation}. */
    public record Mod(Key attribute, Operation operation, double amount) {
    }

    // ---- standard machine attributes (base values) ----
    public static final Key OVERCLOCK_LIMIT = Key.of("polyfill", "overclock_limit"); // extra oc headroom (frac)
    /** Reduces fuel/gas/energy consumption (fraction). Generic so one upgrade fits any machine. */
    public static final Key FUEL_EFFICIENCY = Key.of("polyfill", "fuel_efficiency");
    public static final Key GENERATION = Key.of("polyfill", "generation");           // output bonus (frac)
    public static final Key EXTRA_SLOTS = Key.of("polyfill", "extra_slots");         // unlocked upgrade slots
    /** Flat pressure bonus added to the pump's stamped fluid pressure (lifts fluid higher). */
    public static final Key PRESSURE = Key.of("polyfill", "pressure");

    private static final Map<Key, Double> BASES = new HashMap<>();
    static {
        BASES.put(OVERCLOCK_LIMIT, 0.0);
        BASES.put(FUEL_EFFICIENCY, 0.0);
        BASES.put(GENERATION, 0.0);
        BASES.put(EXTRA_SLOTS, 0.0);
        BASES.put(PRESSURE, 0.0);
    }

    /** Register/override a machine attribute's base value. */
    public static void register(Key attribute, double base) {
        BASES.put(attribute, base);
    }

    public static double base(Key attribute) {
        return BASES.getOrDefault(attribute, 0.0);
    }

    public static boolean isAttribute(Key attribute) {
        return BASES.containsKey(attribute);
    }

    /** Fold all modifiers into a final value per attribute (Mojang math). */
    public static Map<Key, Double> compute(List<Mod> modifiers) {
        Map<Key, Double> flat = new HashMap<>();      // Σ ADD_VALUE
        Map<Key, Double> mulBase = new HashMap<>();   // Σ ADD_MULTIPLIED_BASE
        Map<Key, List<Double>> mulTotal = new HashMap<>();
        if (modifiers != null) {
            for (Mod m : modifiers) {
                switch (m.operation()) {
                    case ADD_VALUE -> flat.merge(m.attribute(), m.amount(), Double::sum);
                    case ADD_MULTIPLIED_BASE -> mulBase.merge(m.attribute(), m.amount(), Double::sum);
                    case ADD_MULTIPLIED_TOTAL -> mulTotal.computeIfAbsent(m.attribute(), k -> new java.util.ArrayList<>())
                            .add(m.amount());
                }
            }
        }
        Map<Key, Double> out = new HashMap<>();
        java.util.Set<Key> keys = new java.util.HashSet<>(BASES.keySet());
        keys.addAll(flat.keySet());
        keys.addAll(mulBase.keySet());
        keys.addAll(mulTotal.keySet());
        for (Key k : keys) {
            double x = base(k) + flat.getOrDefault(k, 0.0);
            x *= (1.0 + mulBase.getOrDefault(k, 0.0));
            for (double m : mulTotal.getOrDefault(k, List.of())) {
                x *= (1.0 + m);
            }
            out.put(k, x);
        }
        return out;
    }

    public static Operation parseOperation(String s) {
        if (s == null) {
            return Operation.ADD_VALUE;
        }
        switch (s.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "add", "add_value", "addition" -> {
                return Operation.ADD_VALUE;
            }
            case "multiply_base", "add_multiplied_base", "multiplybase" -> {
                return Operation.ADD_MULTIPLIED_BASE;
            }
            case "multiply_total", "add_multiplied_total", "multiplytotal", "multiply" -> {
                return Operation.ADD_MULTIPLIED_TOTAL;
            }
            default -> {
                return Operation.ADD_VALUE;
            }
        }
    }
}
