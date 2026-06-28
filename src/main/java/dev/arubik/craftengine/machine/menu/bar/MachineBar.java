package dev.arubik.craftengine.machine.menu.bar;

import java.util.List;
import java.util.Map;

/**
 * A reusable, config-driven "bar" for a machine menu: a run of slots that visually fills
 * according to a named machine stat (e.g. {@code water}, {@code steam}, {@code progress}).
 *
 * <p>A bar has a {@code model} ({@code row} or {@code column} for now — more shapes can be added
 * later) and is split into THREE parts: the first slot is the {@code start}, the last is the
 * {@code end}, and everything in between is {@code middle}. Each part declares its own ordered
 * list of {@link BarState}s (item + name + lore) chosen by the part-slot's local fill %.</p>
 *
 * <p>Lore/name support placeholders {@code %value%}, {@code %max%}, {@code %percent%} (the bar's
 * overall figures) and {@code %seg%} (this segment's local %). A {@code lang:some.key} string is
 * rendered as a translatable component (client i18n).</p>
 *
 * <pre>
 * bars:
 *   water:
 *     model: column                 # row | column
 *     slots: [45, 36, 27, 18, 9]    # start (first) -> end (last); the rest are middle
 *     start:                        # states for the start slot, ascending by `max` (local %)
 *       - { max: 0,   item: minecraft:gray_stained_glass_pane,  name: "lang:polyfill.ui.water", lore: ["%value%/%max% mB"] }
 *       - { max: 100, item: minecraft:blue_stained_glass_pane,  name: "lang:polyfill.ui.water", lore: ["%percent%%"] }
 *     middle: [ ... ]
 *     end:    [ ... ]
 * </pre>
 */
public final class MachineBar {

    public enum Part { START, MIDDLE, END }

    public final String id;
    public final String model;            // "row" | "column" | "fluid" (per-level fill models)
    public final int[] slots;             // start (first) -> end (last)
    public final Map<Part, List<BarState>> states; // per-part, ascending by maxLocalPercent
    public final String family;           // fluid model family prefix (e.g. "water"); subType may override
    public final String name;             // tooltip name (e.g. "lang:polyfill.ui.fluid"); nullable

    /**
     * Optional PER-SLOT state declaration: {@code segments[k]} is the ordered state list for the
     * bar's k-th slot (same slot order as {@link #slots}). When present, the renderer maps each
     * slot's LOCAL fill % to a state in its own list (subtype-aware), instead of the shared
     * start/middle/end parts. This is how the fluid bars declare their per-level fill models by
     * RANGES + TYPE in config (no hardcoded fluid mapping). Null/empty = use {@link #states}.
     */
    public final List<List<BarState>> segments;

    public MachineBar(String id, String model, int[] slots, Map<Part, List<BarState>> states) {
        this(id, model, slots, states, null, null, null);
    }

    public MachineBar(String id, String model, int[] slots, Map<Part, List<BarState>> states,
            String family, String name) {
        this(id, model, slots, states, family, name, null);
    }

    public MachineBar(String id, String model, int[] slots, Map<Part, List<BarState>> states,
            String family, String name, List<List<BarState>> segments) {
        this.id = id;
        this.model = model;
        this.slots = slots;
        this.states = states;
        this.family = family;
        this.name = name;
        this.segments = segments;
    }

    /** Pick the state for a slot whose LOCAL fill is {@code localPercent}, from an explicit list. */
    public BarState stateForList(List<BarState> list, double localPercent, String subType) {
        if (list == null || list.isEmpty())
            return null;
        BarState chosen = null;
        double bestDist = Double.MAX_VALUE;
        for (boolean typedPass : new boolean[] { true, false }) {
            for (BarState s : list) {
                boolean typeOk = typedPass
                        ? (s.type != null && subType != null && s.type.equalsIgnoreCase(subType))
                        : (s.type == null);
                if (!typeOk)
                    continue;
                if (localPercent >= s.min && localPercent <= s.max)
                    return s;
                // Out of range but with SOME fill: keep the NEAREST state (not the last iterated), so an
                // almost-empty piece shows the lowest fill state instead of the full one. A piece whose
                // LOCAL fill is 0 stays empty (chosen=null -> invisible), so upper slots of a column don't
                // light up until the fluid actually reaches them.
                if (localPercent > 0) {
                    double d = localPercent < s.min ? s.min - localPercent : localPercent - s.max;
                    if (d < bestDist) {
                        bestDist = d;
                        chosen = s;
                    }
                }
            }
            // Break as soon as ANY pass found a candidate — the TYPED pass runs first, so a typed
            // (e.g. experience) fallback wins over the untyped (water) one even when the fill is
            // out of every range (e.g. 0%). Previously only the untyped pass could break, so the
            // untyped fallback always overwrote the typed one and the bar reverted to water.
            if (chosen != null)
                break;
        }
        return chosen;
    }

    /** One appearance of a bar piece, chosen when the piece's LOCAL fill % falls in [{@link #min}, {@link #max}]. */
    public static final class BarState {
        public final double min;
        public final double max;
        public final String item; // vanilla Material name or a CraftEngine item id
        public final String name; // nullable
        public final List<String> lore; // possibly empty
        public final String type;  // optional sub-type filter (e.g. fluid/gas type); null = any

        public BarState(double min, double max, String item, String name, List<String> lore, String type) {
            this.min = min;
            this.max = max;
            this.item = item;
            this.name = name;
            this.lore = lore;
            this.type = type;
        }
    }

    /** Which part a slot at {@code segIndex} (0..n-1) belongs to. A 1-slot bar is all START. */
    public Part partOf(int segIndex, int n) {
        if (segIndex == 0)
            return Part.START;
        if (segIndex == n - 1)
            return Part.END;
        return Part.MIDDLE;
    }

    /** Pick the state for a piece whose LOCAL fill is {@code localPercent}, honoring {@code subType}. */
    public BarState stateFor(Part part, double localPercent, String subType) {
        List<BarState> list = states.get(part);
        if (list == null || list.isEmpty()) {
            list = states.get(Part.MIDDLE);
            if (list == null || list.isEmpty())
                for (List<BarState> any : states.values())
                    if (any != null && !any.isEmpty()) {
                        list = any;
                        break;
                    }
        }
        if (list == null || list.isEmpty())
            return null;
        // Pass 1: states whose type matches the current sub-type (specific wins). Pass 2: untyped.
        BarState chosen = null;
        double bestDist = Double.MAX_VALUE;
        for (boolean typedPass : new boolean[] { true, false }) {
            for (BarState s : list) {
                boolean typeOk = typedPass
                        ? (s.type != null && subType != null && s.type.equalsIgnoreCase(subType))
                        : (s.type == null);
                if (!typeOk)
                    continue;
                if (localPercent >= s.min && localPercent <= s.max)
                    return s;
                // Out of range but with SOME fill: keep the NEAREST state (not the last iterated), so an
                // almost-empty piece shows the lowest fill state instead of the full one. A piece whose
                // LOCAL fill is 0 stays empty (chosen=null -> invisible), so upper slots of a column don't
                // light up until the fluid actually reaches them.
                if (localPercent > 0) {
                    double d = localPercent < s.min ? s.min - localPercent : localPercent - s.max;
                    if (d < bestDist) {
                        bestDist = d;
                        chosen = s;
                    }
                }
            }
            // Break as soon as ANY pass found a candidate — the TYPED pass runs first, so a typed
            // (e.g. experience) fallback wins over the untyped (water) one even when the fill is
            // out of every range (e.g. 0%). Previously only the untyped pass could break, so the
            // untyped fallback always overwrote the typed one and the bar reverted to water.
            if (chosen != null)
                break;
        }
        return chosen;
    }
}
