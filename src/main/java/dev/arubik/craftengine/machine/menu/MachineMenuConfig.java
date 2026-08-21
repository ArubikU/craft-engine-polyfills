/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.menu;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MachineMenuConfig {
    public final int menuSize;
    public final String guiImage;
    public final int[] inputSlots;
    public final int[] outputSlots;
    public final int[] fuelSlots;
    public final List<Button> buttons;
    public final int infoSlot;

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<Button> buttons) {
        this(menuSize, guiImage, inputSlots, outputSlots, fuelSlots, buttons, -1);
    }

    public MachineMenuConfig(int menuSize, String guiImage, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<Button> buttons, int infoSlot) {
        this.menuSize = menuSize;
        this.guiImage = guiImage;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fuelSlots = fuelSlots;
        this.buttons = buttons;
        this.infoSlot = infoSlot;
    }

    public static MachineMenuConfig parse(Function<String, Object> args) {
        int menuSize = MachineMenuConfig.intOr(args.apply("menu_size"), 54);
        String guiImage = args.apply("gui_image") == null ? null : String.valueOf(args.apply("gui_image"));
        int[] inputs = MachineMenuConfig.slots(args.apply("input_slots"));
        int[] outputs = MachineMenuConfig.slots(args.apply("output_slots"));
        int[] fuels = args.apply("fuel_slots") != null ? MachineMenuConfig.slots(args.apply("fuel_slots")) : (args.apply("fuel_slot") != null ? new int[]{MachineMenuConfig.intOr(args.apply("fuel_slot"), 0)} : new int[]{});
        List<Button> buttons = MachineMenuConfig.buttons(args.apply("buttons"));
        int infoSlot = MachineMenuConfig.intOr(args.apply("info_slot"), -1);
        return new MachineMenuConfig(menuSize, guiImage, inputs, outputs, fuels, buttons, infoSlot);
    }

    private static List<Button> buttons(Object o) {
        ArrayList<Button> out = new ArrayList<Button>();
        if (!(o instanceof List)) {
            return out;
        }
        List list = (List)o;
        for (Object e : list) {
            Map m;
            int slot;
            if (!(e instanceof Map) || (slot = MachineMenuConfig.intOr((m = (Map)e).get("slot"), -1)) < 0) continue;
            String icon = m.get("icon") == null ? null : String.valueOf(m.get("icon"));
            Action action = Action.parse(m.get("action") == null ? null : String.valueOf(m.get("action")));
            String name = m.get("name") == null ? null : String.valueOf(m.get("name"));
            ArrayList<String> lore = new ArrayList<String>();
            Object object = m.get("lore");
            if (object instanceof List) {
                List ll = (List)object;
                for (Object l : ll) {
                    lore.add(String.valueOf(l));
                }
            }
            String lockedIcon = m.get("locked_icon") == null ? null : String.valueOf(m.get("locked_icon"));
            LockedWhen lockedWhen = LockedWhen.parse(m.get("locked_when") == null ? null : String.valueOf(m.get("locked_when")));
            out.add(new Button(slot, icon, action, name, lore, lockedIcon, lockedWhen));
        }
        return out;
    }

    private static int[] slots(Object o) {
        if (!(o instanceof List)) {
            return new int[0];
        }
        List list = (List)o;
        int[] s = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            s[i] = ((Number)list.get(i)).intValue();
        }
        return s;
    }

    private static int intOr(Object o, int def) {
        int n;
        if (o instanceof Number) {
            Number n2 = (Number)o;
            n = n2.intValue();
        } else {
            n = def;
        }
        return n;
    }

    public static final class Action {
        public final Kind kind;
        public final int page;
        public final String target;
        /** Optional arguments passed to script/function actions. */
        public final List<String> args;

        private Action(Kind kind, int page) {
            this(kind, page, "all", List.of());
        }

        private Action(Kind kind, int page, String target) {
            this(kind, page, target, List.of());
        }

        private Action(Kind kind, int page, String target, List<String> args) {
            this.kind = kind;
            this.page = page;
            this.target = target == null || target.isBlank() ? "all" : target.trim();
            this.args = args != null ? List.copyOf(args) : List.of();
        }

        public boolean targets(String tankName) {
            return "all".equalsIgnoreCase(this.target) || this.target.equalsIgnoreCase(tankName);
        }

        private static String suffix(String t) {
            int i = t.indexOf(58);
            return i < 0 ? "all" : t.substring(i + 1);
        }

        public static Action parse(String s) {
            if (s == null) return new Action(Kind.NONE, 0);
            String t = s.trim().toLowerCase(Locale.ROOT);
            if (t.startsWith("open_page")) {
                int page = 0;
                int i = t.indexOf(58);
                if (i >= 0) {
                    try { page = Integer.parseInt(t.substring(i + 1).trim()); }
                    catch (NumberFormatException ignored) {}
                }
                return new Action(Kind.OPEN_PAGE, page);
            }
            if (t.startsWith("deplete_fluid")) return new Action(Kind.DEPLETE_FLUID, 0, Action.suffix(t));
            if (t.startsWith("deplete_gas"))   return new Action(Kind.DEPLETE_GAS, 0, Action.suffix(t));
            // bump_overclock:delta[:click_step_override] — e.g. "bump_overclock:0.01" or "bump_overclock:-0.01"
            if (t.startsWith("bump_overclock")) return new Action(Kind.BUMP_OVERCLOCK, 0, Action.suffix(t));
            // {filename}.pf:{function_name}:{arg1}:{arg2}...
            // e.g. "gas_motor.pf:increase_rpm:10"  or  "gas_motor.pf:increase_rpm"
            if (t.contains(".pf:")) {
                String raw = s.trim();
                String[] parts = raw.split(":", -1);
                // parts[0] = "filename.pf", parts[1] = funcname, parts[2..] = args
                String scriptFile = parts[0]; // e.g. "gas_motor.pf"
                String funcName = parts.length > 1 ? parts[1] : "";
                List<String> args = parts.length > 2 ? Arrays.asList(parts).subList(2, parts.length) : List.of();
                return new Action(Kind.SCRIPT, 0, scriptFile + ":" + funcName, args);
            }
            // Legacy: pf:{function_name}:{arg1}:{arg2}... — DEPRECATED, use {filename}.pf:{func} instead
            if (t.startsWith("pf:")) {
                String raw = s.trim().substring(3);
                String[] parts = raw.split(":", -1);
                String fnName = parts[0];
                List<String> args = parts.length > 1 ? Arrays.asList(parts).subList(1, parts.length) : List.of();
                return new Action(Kind.PF_FUNCTION, 0, fnName, args);
            }
            return new Action(Kind.NONE, 0);
        }

        public static enum Kind {
            OPEN_PAGE,
            DEPLETE_FLUID,
            DEPLETE_GAS,
            SCRIPT,
            PF_FUNCTION,
            BUMP_OVERCLOCK,
            NONE;
        }
    }

    public static enum LockedWhen {
        NEVER,
        NO_OVERCLOCK;

        public String expr;

        public static LockedWhen parse(String s) {
            if (s == null) {
                return NEVER;
            }
            if ("no_overclock".equalsIgnoreCase(s.trim())) {
                return NO_OVERCLOCK;
            }
            if ("never".equalsIgnoreCase(s.trim())) {
                return NEVER;
            }
            LockedWhen lw = NEVER;
            lw.expr = null;
            LockedWhen custom = NEVER;
            try {
                ScriptFormula.compile(s.trim());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            LockedWhen result = NO_OVERCLOCK;
            result.expr = s.trim();
            return result;
        }

        public boolean isLocked(ScriptContext ctx, double curOverclockLimit) {
            if (this == NEVER) {
                return false;
            }
            if (this.expr != null) {
                try {
                    return ScriptFormula.compile(this.expr).evaluateBool(ctx);
                }
                catch (Throwable ignored) {
                    return false;
                }
            }
            return curOverclockLimit <= 0.0;
        }
    }

    public static final class Button {
        public final int slot;
        public final String icon;
        public final Action action;
        public final String name;
        public final List<String> lore;
        public final String lockedIcon;
        public final LockedWhen lockedWhen;

        public Button(int slot, String icon, Action action, String name, List<String> lore, String lockedIcon, LockedWhen lockedWhen) {
            this.slot = slot;
            this.icon = icon;
            this.action = action;
            this.name = name;
            this.lore = lore;
            this.lockedIcon = lockedIcon;
            this.lockedWhen = lockedWhen;
        }

        /** Evaluate name with ${expr} inline scripts + MiniMessage parsing. */
        public String evaluateName(ScriptContext ctx) {
            return evaluateInline(name, ctx);
        }

        /**
         * Evaluate name with ${expr} substitution ONLY — returns the raw MiniMessage string
         * so callers can parse it with MiniMessage.miniMessage().deserialize() themselves,
         * preserving TranslatableComponent (i18n) and other MiniMessage tags.
         */
        public String evaluateNameRaw(ScriptContext ctx) {
            if (name == null) return null;
            if (!name.contains("${")) return name; // no substitution needed
            // Same ${expr} substitution as evaluateInline but WITHOUT the MiniMessage→legacy step
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < name.length()) {
                int start = name.indexOf("${", i);
                if (start < 0) { sb.append(name, i, name.length()); break; }
                sb.append(name, i, start);
                int end = name.indexOf('}', start + 2);
                if (end < 0) { sb.append(name, start, name.length()); break; }
                String expr = name.substring(start + 2, end);
                if (ctx != null) {
                    try {
                        sb.append(ScriptFormula.compile(expr).evaluate(ctx).asStr());
                    } catch (Throwable ignored) { sb.append('?'); }
                } else { sb.append('?'); }
                i = end + 1;
            }
            return sb.toString();
        }

        /**
         * Evaluate lore lines with ${expr} substitution ONLY — returns raw MiniMessage strings
         * (no MiniMessage→legacy conversion). Callers should parse with MiniMessage.deserialize().
         * Script functions (.pf:) return their strings as-is (they already use MiniMessage format).
         */
        public List<String> evaluateLoreRaw(ScriptContext ctx) {
            if (lore == null || lore.isEmpty()) return lore;
            List<String> result = new ArrayList<>();
            for (String line : lore) {
                if (line != null && line.contains(".pf:") && ctx != null) {
                    try {
                        String raw = line.trim();
                        int colon = raw.indexOf(':');
                        String scriptFile = raw.substring(0, colon);
                        String funcName = raw.substring(colon + 1);
                        String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
                        dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(lookupKey);
                        if (prog != null) {
                            dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(ctx);
                            dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
                            if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                                    && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                                dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                                dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                                fn.executor().accept(withDefs, rb);
                                dev.arubik.craftengine.script.ScriptValue retVal = rb.build().getVar("__return__");
                                if (retVal instanceof dev.arubik.craftengine.script.ScriptValue.Array arr) {
                                    for (dev.arubik.craftengine.script.ScriptValue elem : arr.elements())
                                        result.add(elem.asStr()); // raw string, no conversion
                                    continue;
                                }
                                result.add(retVal.asStr());
                                continue;
                            }
                        }
                    } catch (Throwable ignored) {}
                }
                // ${expr} substitution only — NO MiniMessage→legacy conversion
                if (line != null && line.contains("${")) {
                    result.add(evaluateNameRaw(ctx) != null ? evaluateFieldRaw(line, ctx) : line);
                } else {
                    result.add(line);
                }
            }
            return result;
        }

        private static String evaluateFieldRaw(String template, ScriptContext ctx) {
            if (template == null || !template.contains("${")) return template;
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < template.length()) {
                int start = template.indexOf("${", i);
                if (start < 0) { sb.append(template, i, template.length()); break; }
                sb.append(template, i, start);
                int end = template.indexOf('}', start + 2);
                if (end < 0) { sb.append(template, start, template.length()); break; }
                String expr = template.substring(start + 2, end);
                if (ctx != null) {
                    try { sb.append(ScriptFormula.compile(expr).evaluate(ctx).asStr()); }
                    catch (Throwable ignored) { sb.append('?'); }
                } else { sb.append('?'); }
                i = end + 1;
            }
            return sb.toString();
        }

        /** Evaluate lore lines with ${expr} inline scripts + MiniMessage parsing.
         *  A lore entry of "{file}.pf:{func}" calls that function which must return an Array of Str.
         */
        public List<String> evaluateLore(ScriptContext ctx) {
            if (lore == null || lore.isEmpty()) return lore;
            List<String> result = new ArrayList<>();
            for (String line : lore) {
                if (line != null && line.contains(".pf:") && ctx != null) {
                    // Try to call as a script function returning array of strings
                    try {
                        String raw = line.trim();
                        int colon = raw.indexOf(':');
                        String scriptFile = raw.substring(0, colon);
                        String funcName = raw.substring(colon + 1);
                        String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
                        dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(lookupKey);
                        if (prog != null) {
                            dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(ctx);
                            dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
                            if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                                    && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                                dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                                dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                                fn.executor().accept(withDefs, rb);
                                dev.arubik.craftengine.script.ScriptValue retVal = rb.build().getVar("__return__");
                                if (retVal instanceof dev.arubik.craftengine.script.ScriptValue.Array arr) {
                                    for (dev.arubik.craftengine.script.ScriptValue elem : arr.elements())
                                        result.add(evaluateInline(elem.asStr(), ctx));
                                    continue;
                                }
                                result.add(evaluateInline(retVal.asStr(), ctx));
                                continue;
                            }
                        }
                    } catch (Throwable ignored) {}
                }
                result.add(evaluateInline(line, ctx));
            }
            return result;
        }

        /** Public entry point for callers outside this class (e.g. buildPageLayout). */
        public static String evaluateInlineScriptStatic(String template, ScriptContext ctx) {
            return evaluateInline(template, ctx);
        }

        private static String evaluateInline(String template, ScriptContext ctx) {
            if (template == null) return null;
            // Replace ${expr} with evaluated script value
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < template.length()) {
                int start = template.indexOf("${", i);
                if (start < 0) { sb.append(template, i, template.length()); break; }
                sb.append(template, i, start);
                int end = template.indexOf('}', start + 2);
                if (end < 0) { sb.append(template, start, template.length()); break; }
                String expr = template.substring(start + 2, end);
                if (ctx != null) {
                    try {
                        ScriptValue val = ScriptFormula.compile(expr).evaluate(ctx);
                        sb.append(val.asStr());
                    } catch (Throwable ignored) { sb.append('?'); }
                } else {
                    sb.append('?');
                }
                i = end + 1;
            }
            // MiniMessage → legacy colour codes for Bukkit ItemMeta
            try {
                net.kyori.adventure.text.Component comp = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(sb.toString());
                return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(comp);
            } catch (Throwable ignored) {
                return sb.toString();
            }
        }
    }
}

