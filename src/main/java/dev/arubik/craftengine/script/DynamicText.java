package dev.arubik.craftengine.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared "dynamic text" evaluator: a MiniMessage string that may contain {@code ${expr}}
 * inline-script substitutions, OR be a bare {@code "file.pf:funcname"} reference whose return
 * value supplies the WHOLE field (a string for a name, an array of strings for lore).
 *
 * <p>Extracted from {@link dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button}'s
 * name/lore evaluation (which now delegates here) so any other data-driven text field —
 * item names/lore, anything else that wants "MiniMessage + script" — can reuse the exact same
 * two conventions instead of re-implementing them.
 *
 * <p>Everything here returns RAW MiniMessage strings (no MiniMessage→legacy-ampersand
 * conversion) — callers parse the result with {@code MiniMessage.miniMessage().deserialize(...)}
 * themselves, which is required to preserve tags a legacy round-trip would mangle or drop
 * (translatable components, hover events, etc).
 */
public final class DynamicText {
    private DynamicText() {}

    /** Evaluates a name template: a bare {@code "file.pf:func"} ref calls that function for the
     *  whole string; otherwise {@code ${expr}} substitutions run against {@code ctx}. */
    public static String evaluateNameRaw(String template, ScriptContext ctx) {
        if (template == null) return null;
        if (template.contains(".pf:") && ctx != null) {
            String viaScript = callPfFuncStr(template, ctx);
            if (viaScript != null) return viaScript;
        }
        return evaluateFieldRaw(template, ctx);
    }

    /** Evaluates a lore template list: any line containing {@code ".pf:"} is called as a script
     *  function — if it returns an Array, every element becomes its own output line (so ONE line
     *  in the config can be a single {@code "file.pf:func"} entry that expands into the whole
     *  lore); every other line just gets {@code ${expr}} substitution. */
    public static List<String> evaluateLoreRaw(List<String> lines, ScriptContext ctx) {
        if (lines == null || lines.isEmpty()) return lines;
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line != null && line.contains(".pf:") && ctx != null) {
                try {
                    String raw = line.trim();
                    int colon = raw.indexOf(':');
                    String scriptFile = raw.substring(0, colon);
                    String funcName = raw.substring(colon + 1);
                    String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
                    ScriptProgram prog = ScriptRegistry.get(lookupKey);
                    if (prog != null) {
                        ScriptContext withDefs = prog.evaluate(ctx);
                        ScriptValue fnVal = withDefs.getVar(funcName);
                        if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
                            UserFunction fn = (UserFunction) fnObj.instance();
                            ScriptContext.Builder rb = ScriptContext.builder().copyFrom(withDefs);
                            fn.executor().accept(withDefs, rb);
                            ScriptValue retVal = rb.build().getVar("__return__");
                            if (retVal instanceof ScriptValue.Array arr) {
                                for (ScriptValue elem : arr.elements()) result.add(elem.asStr());
                                continue;
                            }
                            result.add(retVal.asStr());
                            continue;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            result.add(line != null && line.contains("${") ? evaluateFieldRaw(line, ctx) : line);
        }
        return result;
    }

    /** {@code ${expr}} substitution only, no script-ref whole-field handling. */
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
            } else {
                sb.append('?');
            }
            i = end + 1;
        }
        return sb.toString();
    }

    /** Calls {@code "file.pf:func"} with no arguments and returns its {@code __return__} as a raw
     *  string, or null if the script/function can't be resolved. */
    private static String callPfFuncStr(String ref, ScriptContext ctx) {
        try {
            String raw = ref.trim();
            int colon = raw.indexOf(':');
            String scriptFile = raw.substring(0, colon);
            String funcName = raw.substring(colon + 1);
            String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
            ScriptProgram prog = ScriptRegistry.get(lookupKey);
            if (prog == null) return null;
            ScriptContext withDefs = prog.evaluate(ctx);
            ScriptValue fnVal = withDefs.getVar(funcName);
            if (!(fnVal instanceof ScriptValue.Obj fnObj) || !fnObj.typeName().equals(UserFunction.TYPE))
                return null;
            UserFunction fn = (UserFunction) fnObj.instance();
            ScriptContext.Builder rb = ScriptContext.builder().copyFrom(withDefs);
            fn.executor().accept(withDefs, rb);
            return rb.build().getVar("__return__").asStr();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
