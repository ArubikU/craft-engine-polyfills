package dev.arubik.craftengine.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * A quantity in a data file: a plain number, a vanilla number provider, or a
 * list of providers that are summed.
 *
 * <p>
 * Every count in this plugin's data goes through here — how much liquid a bucket
 * yields, how many ingots a recipe produces, how much XP a craft grants — so
 * "give 1–3, and 5% of the time a bonus" is expressible everywhere rather than
 * only where someone remembered to hardcode a roll.
 *
 * <p>
 * The list form exists because vanilla has no {@code sum} provider, and some
 * quantities genuinely need one: an experience bottle is worth
 * {@code 3 + rand(5) + rand(5)}, a triangular distribution no single
 * {@code uniform} reproduces. Each element is still an ordinary vanilla
 * provider; the addition is the only thing this plugin adds.
 *
 * <pre>{@code
 * "count": 4
 * "count": { "type": "minecraft:uniform", "min": 1, "max": 3 }
 * "count": [ { "type": "minecraft:constant", "value": 3 },
 *            { "type": "minecraft:uniform", "min": 0, "max": 4 } ]
 * }</pre>
 */
public final class Amount {

    private final List<NumberProvider> terms;
    private final int fallback;

    private Amount(List<NumberProvider> terms, int fallback) {
        this.terms = List.copyOf(terms);
        this.fallback = fallback;
    }

    /** A fixed quantity, for code paths that have no data behind them. */
    public static Amount of(int value) {
        return new Amount(List.of(), value);
    }

    /**
     * Reads a quantity from {@code field}, falling back to a constant when absent.
     *
     * @param fallback used both when the field is missing and when a provider needs
     *                 loot context this plugin cannot supply
     */
    public static Amount parse(JsonView view, String field, int fallback) {
        JsonElement raw = view.raw().get(field);
        if (raw == null || raw.isJsonNull())
            return of(fallback);
        String where = view.path() + " > " + field;

        List<NumberProvider> terms = new ArrayList<>();
        if (raw.isJsonArray()) {
            int index = 0;
            for (JsonElement term : raw.getAsJsonArray())
                terms.add(VanillaData.parse(NumberProviders.CODEC, term, where + "[" + index++ + "]"));
        } else {
            terms.add(VanillaData.parse(NumberProviders.CODEC, raw, where));
        }
        return new Amount(terms, fallback);
    }

    /** Rolls the quantity. Never negative. */
    public int roll() {
        if (terms.isEmpty())
            return Math.max(0, fallback);
        int total = 0;
        for (NumberProvider term : terms)
            total += VanillaData.roll(term, 0);
        return Math.max(0, total);
    }

    /** Whether this is a plain constant, so callers can skip re-rolling per use. */
    public boolean isConstant() {
        return terms.isEmpty();
    }

    /** The constant value, meaningful only when {@link #isConstant()}. */
    public int constant() {
        return fallback;
    }

    @Override
    public String toString() {
        return isConstant() ? Integer.toString(fallback) : terms.size() + " provider(s)";
    }
}
