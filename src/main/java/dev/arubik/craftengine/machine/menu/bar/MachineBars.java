package dev.arubik.craftengine.machine.menu.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/** Parses {@code bars:} config and installs the bars onto a {@link MachineLayout}. */
public final class MachineBars {

    private MachineBars() {
    }

    /** Parse a {@code bars:} config object (a map of barId -> {model, slots, start/middle/end}). */
    public static List<MachineBar> parse(Object barsObj) {
        List<MachineBar> out = new ArrayList<>();
        if (!(barsObj instanceof Map<?, ?> barsMap))
            return out;
        for (Map.Entry<?, ?> e : barsMap.entrySet()) {
            String id = String.valueOf(e.getKey());
            if (!(e.getValue() instanceof Map<?, ?> def))
                continue;
            int[] slots = parseSlots(def.get("slots"));
            if (slots.length == 0)
                continue;
            String model = def.get("model") == null ? "column" : String.valueOf(def.get("model"));
            String family = def.get("family") == null ? null : String.valueOf(def.get("family"));
            String name = def.get("name") == null ? null : String.valueOf(def.get("name"));

            java.util.EnumMap<MachineBar.Part, List<MachineBar.BarState>> states =
                    new java.util.EnumMap<>(MachineBar.Part.class);
            states.put(MachineBar.Part.START, parseStates(statesNode(def.get("start"))));
            states.put(MachineBar.Part.MIDDLE, parseStates(statesNode(def.get("middle"))));
            states.put(MachineBar.Part.END, parseStates(statesNode(def.get("end"))));
            // A bar with a single shared `states:` list applies to all three parts.
            List<MachineBar.BarState> shared = parseStates(def.get("states"));
            if (!shared.isEmpty()) {
                for (MachineBar.Part p : MachineBar.Part.values())
                    if (states.get(p).isEmpty())
                        states.put(p, shared);
            }
            // Optional PER-SLOT segments: one entry per slot, each a {states:[...]} list. When
            // present, every slot maps its local fill % to a fill-model item via its own ranges
            // (type-aware). This makes the fluid/fuel/progress bars fully declarative (no hardcoding).
            List<List<MachineBar.BarState>> segments = parseSegments(def.get("segments"));
            // `generate:` builds those segments from the fluid/gas registry instead of
            // listing every level of every type by hand — that listing ran to ~290 lines
            // per machine and had to be duplicated, in full, for each machine showing the
            // same gauge.
            if (segments == null || segments.isEmpty())
                segments = generateSegments(def.get("generate"), name);

            boolean any = states.values().stream().anyMatch(l -> !l.isEmpty())
                    || (segments != null && !segments.isEmpty());
            if (!any)
                continue;
            out.add(new MachineBar(id, model, slots, states, family, name, segments));
        }
        return out;
    }

    /**
     * Builds per-slot segments from the fluid or gas registry.
     *
     * <pre>{@code
     * generate:
     *   source: fluid                            # fluid | gas | none
     *   item: "cml:%family%_%suffix%_%level%"    # every item id this gauge uses
     *   lore: ["%value%/%max% mB", "%percent%%"]
     *   parts:                                   # one per slot, in slot order
     *     - { suffix: bottom, levels: 16 }
     *     - { suffix: midbot, levels: 18 }
     * }</pre>
     *
     * <p>
     * Each level's range is {@code round(i * 100 / levels)}, which reproduces the
     * hand-written ranges exactly. The item id comes from the {@code item} template
     * — the models a gauge uses are stated in the file rather than assembled in
     * code — with {@code %family%} filled in per registered type, so adding a liquid
     * gives it a gauge with no edit at all. {@code source: none} skips the registry
     * and emits one untyped run, which is what the fuel and progress gauges need.
     */
    private static List<List<MachineBar.BarState>> generateSegments(Object o, String barName) {
        if (!(o instanceof Map<?, ?> gen))
            return null;
        String source = str(gen.get("source"), "fluid");
        boolean gas = "gas".equalsIgnoreCase(source);
        boolean typed = !"none".equalsIgnoreCase(source);
        String template = str(gen.get("item"), null);
        if (template == null)
            return null; // a gauge must say which items it draws
        String emptyIcon = str(gen.get("empty_icon"), "cml:gui_empty");
        List<String> lore = new ArrayList<>();
        if (gen.get("lore") instanceof List<?> ll)
            for (Object l : ll)
                lore.add(String.valueOf(l));
        if (!(gen.get("parts") instanceof List<?> parts) || parts.isEmpty())
            return null;

        // Family -> the type whose name labels it, plus every other type sharing it.
        record Variant(String family, String type) {
        }
        List<Variant> variants = new ArrayList<>();
        if (typed && gas) {
            for (var t : dev.arubik.craftengine.gas.GasType.REGISTRY.values())
                if (!t.isEmpty())
                    variants.add(new Variant(t.id().value(), t.name().toLowerCase(java.util.Locale.ROOT)));
        } else if (typed) {
            for (var t : dev.arubik.craftengine.fluid.FluidType.REGISTRY.values())
                if (t != dev.arubik.craftengine.fluid.FluidType.EMPTY)
                    variants.add(new Variant(t.renderFamily(), t.name().toLowerCase(java.util.Locale.ROOT)));
        }
        String defaultFamily = variants.isEmpty() ? str(gen.get("family"), "")
                : variants.get(0).family();

        List<List<MachineBar.BarState>> out = new ArrayList<>();
        for (Object partObj : parts) {
            if (!(partObj instanceof Map<?, ?> part))
                continue;
            String suffix = str(part.get("suffix"), "");
            int levels = part.get("levels") == null ? 16 : ((Number) part.get("levels")).intValue();
            List<MachineBar.BarState> states = new ArrayList<>();
            states.add(new MachineBar.BarState(0, 0, emptyIcon, null, List.of(), null));

            // The default (untyped) run first, matching how the hand-written bars put the
            // fallback family ahead of the type-qualified entries.
            appendLevels(states, template, defaultFamily, suffix, levels, barName, lore, null);
            for (Variant v : variants)
                appendLevels(states, template, v.family(), suffix, levels, barName, lore, v.type());
            out.add(states);
        }
        return out;
    }

    private static String str(Object value, String fallback) {
        return value == null ? fallback : String.valueOf(value);
    }

    private static void appendLevels(List<MachineBar.BarState> states, String template, String family,
            String suffix, int levels, String barName, List<String> lore, String type) {
        int previous = 0;
        for (int i = 1; i <= levels; i++) {
            int max = (int) Math.round(i * 100.0 / levels);
            String item = template.replace("%family%", family)
                    .replace("%suffix%", suffix)
                    .replace("%level%", Integer.toString(i));
            states.add(new MachineBar.BarState(previous + 1, max, item, barName, lore, type));
            previous = max;
        }
    }

    /** Parse a per-slot {@code segments:} list — each element is {@code {states: [...]}} (or a bare list). */
    private static List<List<MachineBar.BarState>> parseSegments(Object o) {
        if (!(o instanceof List<?> list))
            return null;
        List<List<MachineBar.BarState>> out = new ArrayList<>();
        for (Object e : list)
            out.add(parseStates(statesNode(e)));
        return out;
    }

    private static int[] parseSlots(Object o) {
        if (!(o instanceof List<?> list))
            return new int[0];
        int[] s = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            s[i] = ((Number) list.get(i)).intValue();
        return s;
    }

    /** A part node may be the states list directly, or a map wrapping {@code states: [...]}. */
    private static Object statesNode(Object o) {
        if (o instanceof Map<?, ?> m && m.get("states") != null)
            return m.get("states");
        return o;
    }

    private static List<MachineBar.BarState> parseStates(Object o) {
        List<MachineBar.BarState> out = new ArrayList<>();
        if (!(o instanceof List<?> list))
            return out;
        for (Object so : list) {
            if (!(so instanceof Map<?, ?> m))
                continue;
            double min = m.get("min") == null ? 0 : ((Number) m.get("min")).doubleValue();
            double max = m.get("max") == null ? 100 : ((Number) m.get("max")).doubleValue();
            String item = m.get("item") == null ? "minecraft:gray_stained_glass_pane" : String.valueOf(m.get("item"));
            String name = m.get("name") == null ? null : String.valueOf(m.get("name"));
            String type = m.get("type") == null ? null : String.valueOf(m.get("type"));
            List<String> lore = new ArrayList<>();
            if (m.get("lore") instanceof List<?> ll)
                for (Object l : ll)
                    lore.add(String.valueOf(l));
            out.add(new MachineBar.BarState(min, max, item, name, lore, type));
        }
        out.sort((a, b) -> Double.compare(a.min, b.min));
        return out;
    }

    /** Install every bar's segments as dynamic providers reading {@code machine.barStat(id)}. */
    public static void install(MachineLayout layout, List<MachineBar> bars) {
        if (bars == null)
            return;
        for (MachineBar bar : bars) {
            int n = bar.slots.length;
            for (int k = 0; k < n; k++) {
                final int segIndex = k;
                final MachineBar b = bar;
                layout.setDynamicProvider(b.slots[k], (machine, tick) -> {
                    AbstractMachineBlockEntity m = (AbstractMachineBlockEntity) machine;
                    double[] vm = m.barStat(b.id);
                    return renderSegment(b, segIndex, n, vm[0], vm[1], m.barSubtype(b.id), m.barPlaceholders(b.id));
                });
            }
        }
    }

    private static ItemStack renderSegment(MachineBar bar, int segIndex, int n, double value, double max, String subType,
            java.util.Map<String, String> ph) {
        double overall = max > 0 ? Math.max(0, Math.min(1.0, value / max)) : 0;
        // Any non-zero contents read as at least 1% global, so 1 mB still lights the FIRST slot's lowest
        // fill state instead of rounding away to nothing. Upper slots stay empty (their local fill is 0).
        if (value >= 1 && overall > 0 && overall < 0.01)
            overall = 0.01;
        // The segment covers [segIndex/n, (segIndex+1)/n] of the bar.
        double lo = (double) segIndex / n;
        double localFrac = Math.max(0, Math.min(1.0, (overall - lo) * n));
        double localPct = localFrac * 100;

        MachineBar.BarState st;
        if (bar.segments != null && segIndex < bar.segments.size() && bar.segments.get(segIndex) != null
                && !bar.segments.get(segIndex).isEmpty()) {
            // PER-SLOT declarative ranges (fluid/fuel/progress): pick this slot's state by local %.
            st = bar.stateForList(bar.segments.get(segIndex), localPct, subType);
        } else {
            // Shared start/middle/end parts (legacy stained-glass bars).
            st = bar.stateFor(bar.partOf(segIndex, n), localPct, subType);
        }
        if (st == null)
            return MenuText.emptyFiller();

        // cml:gui_empty = invisible icon. With NO name/lore -> a truly tooltip-less filler (empty fluid
        // segments). WITH a name/lore -> still invisible but keep the tooltip (e.g. an "off" rpm gauge
        // that must still show how much rpm/su the machine needs). So only strip the tooltip when blank.
        boolean isEmptyIcon = st.item != null && (st.item.endsWith(":gui_empty") || st.item.equals("cml:gui_empty"));
        if (isEmptyIcon && st.name == null && st.lore.isEmpty())
            return MenuText.emptyFiller();

        ItemStack item = resolveItem(st.item);
        int pct = (int) Math.round(overall * 100);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // If this bar has a fluid/gas family + a live subtype, title it with the ACTUAL type name
            // (Water / Lava / Steam / Heavy Steam) instead of the generic configured name.
            Component nameComp = null;
            if (bar.family != null && subType != null && !subType.isEmpty()) {
                boolean gas = bar.family.contains("steam") || bar.family.equals("gas");
                nameComp = Component.translatable("polyfill." + (gas ? "gas" : "liquid") + "."
                        + subType.toLowerCase(java.util.Locale.ROOT)).color(NamedTextColor.WHITE);
            } else if (st.name != null) {
                nameComp = component(st.name, value, max, pct, (int) Math.round(localPct), ph);
            }
            if (nameComp != null)
                meta.displayName(MenuText.noI(nameComp));
            if (!st.lore.isEmpty()) {
                List<Component> lore = new ArrayList<>();
                for (String line : st.lore)
                    lore.add(MenuText.noI(component(line, value, max, pct, (int) Math.round(localPct), ph)));
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack resolveItem(String spec) {
        // CraftEngine item id (namespace:path) takes priority when it resolves; else a vanilla Material.
        try {
            if (spec.contains(":") && !spec.startsWith("minecraft:")) {
                var def = CraftEngineItems.byId(Key.of(spec));
                if (def != null)
                    return def.buildBukkitItem();
            }
        } catch (Throwable ignored) {
        }
        Material mat = Material.matchMaterial(spec);
        return new ItemStack(mat != null ? mat : Material.GRAY_STAINED_GLASS_PANE);
    }

    /** {@code lang:key} -> translatable; otherwise literal text with %placeholders% substituted. */
    private static Component component(String s, double value, double max, int percent, int seg,
            java.util.Map<String, String> ph) {
        if (s.startsWith("lang:"))
            return Component.translatable(s.substring(5)).color(NamedTextColor.WHITE);
        String txt = s.replace("%value%", fmt(value))
                .replace("%max%", fmt(max))
                .replace("%percent%", String.valueOf(percent))
                .replace("%seg%", String.valueOf(seg));
        if (ph != null)
            for (java.util.Map.Entry<String, String> e : ph.entrySet())
                txt = txt.replace("%" + e.getKey() + "%", e.getValue());
        return Component.text(txt, NamedTextColor.GRAY);
    }

    private static String fmt(double v) {
        return (v == Math.rint(v)) ? String.valueOf((long) v) : String.format("%.1f", v);
    }
}
