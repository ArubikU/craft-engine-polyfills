package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TextTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts a {@code "buttons": "file.pf:func"} / {@code "layout": "file.pf:func"} generator ref
 * (see {@code MachineDefinition.PageDef#buttonsGenerator}/{@code #layoutGenerator}) into the SAME
 * {@link MachineDefinition.ButtonSpec}/{@link MachineDefinition.PageDef.StaticSlot} shapes the
 * static JSON array form produces — shared by every page-rendering pipeline that supports the
 * generator form ({@code DataMachineBlockEntity}, {@code DataMultiBlockMachineBlockEntity},
 * {@code dev.arubik.craftengine.item.menu.ItemMenu}) so there's ONE reading of the descriptor
 * shape, not three drifting copies.
 *
 * <p>The generator function takes no arguments and returns an {@code Array} of {@code Map}
 * (built with the {@code make_map(...)} builtin) descriptors:
 * <pre>
 *   def generate_buttons():
 *       out = []
 *       out = push(out, make_map("slot", 10, "icon", "cml:teleporter_core",
 *           "name", "<yellow>Some Destination", "lore", ["<gray>line one"],
 *           "action", "some.pf:do_thing:5"))
 *       return out
 *   end
 * </pre>
 * A malformed/missing generator or a non-Array return yields an empty list — the page just shows
 * nothing extra rather than throwing mid-render.
 */
public final class GeneratedPageContent {
    private GeneratedPageContent() {}

    public static List<MachineDefinition.ButtonSpec> buttons(String generatorRef, ScriptContext ctx) {
        List<MachineDefinition.ButtonSpec> out = new ArrayList<>();
        for (Map<String, ScriptValue> m : entries(generatorRef, ctx)) {
            org.bukkit.inventory.ItemStack customIcon = customIcon(m, "icon");
            out.add(new MachineDefinition.ButtonSpec(
                    (int) num(m, "slot", 0),
                    customIcon != null ? "cml:gui_empty" : str(m, "icon", "cml:gui_empty"),
                    str(m, "action", "none"),
                    strOrNull(m, "name"),
                    strList(m, "lore"),
                    strOrNull(m, "locked_icon"),
                    str(m, "locked_when", "never"),
                    customIcon));
        }
        return out;
    }

    public static List<MachineDefinition.PageDef.StaticSlot> layout(String generatorRef, ScriptContext ctx) {
        List<MachineDefinition.PageDef.StaticSlot> out = new ArrayList<>();
        for (Map<String, ScriptValue> m : entries(generatorRef, ctx)) {
            org.bukkit.inventory.ItemStack customIcon = customIcon(m, "item");
            out.add(new MachineDefinition.PageDef.StaticSlot(
                    (int) num(m, "slot", -1),
                    customIcon != null ? null : strOrNull(m, "item"),
                    strOrNull(m, "name"),
                    strList(m, "lore"),
                    strOrNull(m, "action"),
                    bool(m, "locked", false),
                    customIcon));
        }
        return out;
    }

    /**
     * If {@code m.get(key)} is a full {@link ScriptValue.Item} (e.g. from a script's {@code
     * Item.create(...).with_profile(...)}) rather than a plain string id, extract and return its
     * {@code ItemStack} as a Bukkit copy so callers can populate {@code ButtonSpec#customIcon}/
     * {@code StaticSlot#customIcon} — preserving the exact stack (skin profile, custom model data,
     * ...) instead of stringifying it into a lookup id. Returns null for a plain string/absent value.
     */
    private static org.bukkit.inventory.ItemStack customIcon(Map<String, ScriptValue> m, String key) {
        ScriptValue v = m.get(key);
        if (v instanceof ScriptValue.Item item && item.stack() != null && !item.stack().isEmpty()) {
            try {
                return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(item.stack());
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private static List<Map<String, ScriptValue>> entries(String generatorRef, ScriptContext ctx) {
        List<Map<String, ScriptValue>> out = new ArrayList<>();
        if (generatorRef == null || ctx == null) return out;
        try {
            ScriptValue result = TextTemplate.callPfFunc(generatorRef, ctx);
            if (!(result instanceof ScriptValue.Array arr)) return out;
            for (ScriptValue v : arr.elements()) {
                Map<String, ScriptValue> m = asMap(v);
                if (m != null) out.add(m);
            }
        } catch (Throwable ignored) {}
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ScriptValue> asMap(ScriptValue v) {
        if (v instanceof ScriptValue.Obj o && "Map".equals(o.typeName()) && o.instance() instanceof Map) {
            return (Map<String, ScriptValue>) o.instance();
        }
        return null;
    }

    private static double num(Map<String, ScriptValue> m, String key, double def) {
        ScriptValue v = m.get(key);
        return v == null ? def : v.asNum();
    }

    private static boolean bool(Map<String, ScriptValue> m, String key, boolean def) {
        ScriptValue v = m.get(key);
        return v == null ? def : v.asBool();
    }

    private static String str(Map<String, ScriptValue> m, String key, String def) {
        ScriptValue v = m.get(key);
        return v == null ? def : v.asStr();
    }

    private static String strOrNull(Map<String, ScriptValue> m, String key) {
        ScriptValue v = m.get(key);
        return v == null ? null : v.asStr();
    }

    private static List<String> strList(Map<String, ScriptValue> m, String key) {
        ScriptValue v = m.get(key);
        if (!(v instanceof ScriptValue.Array arr)) return List.of();
        List<String> out = new ArrayList<>();
        for (ScriptValue e : arr.elements()) out.add(e.asStr());
        return out;
    }
}
