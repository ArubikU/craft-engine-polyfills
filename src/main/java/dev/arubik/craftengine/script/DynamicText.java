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
                ScriptValue retVal = callPfFunc(line, ctx);
                if (retVal != null) {
                    if (retVal instanceof ScriptValue.Array arr) {
                        for (ScriptValue elem : arr.elements()) result.add(elem.asStr());
                    } else {
                        result.add(retVal.asStr());
                    }
                    continue;
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

    /** Calls {@code "file.pf:func[:args]"} and returns its return value, or null if the
     *  script/function can't be resolved (or it genuinely returned null — not distinguished, same
     *  as {@link TextTemplate#callPfFunc}). Parsing/dispatch delegates to {@link ScriptCall} — the
     *  one canonical place for this convention — instead of DynamicText hand-rolling its own
     *  {@code indexOf(':')} split (which, before this, silently broke on any {@code :arg} suffix by
     *  treating the whole "func:arg" tail as one bogus function name, and bypassed {@link
     *  UserFunction#call}'s normal param-binding/depth-guard path via direct {@code executor()}
     *  access). */
    private static ScriptValue callPfFunc(String ref, ScriptContext ctx) {
        try {
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) return null;
            ScriptValue result = call.evaluate(ctx);
            return result instanceof ScriptValue.Null ? null : result;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String callPfFuncStr(String ref, ScriptContext ctx) {
        ScriptValue ret = callPfFunc(ref, ctx);
        return ret == null ? null : ret.asStr();
    }
}
