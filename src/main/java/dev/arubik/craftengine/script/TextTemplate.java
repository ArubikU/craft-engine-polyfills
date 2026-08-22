package dev.arubik.craftengine.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared "${expr} + .pf:func[:args]" template/condition evaluation, returning raw MiniMessage
 * strings (NOT MiniMessage→legacy converted) so callers parse the result themselves with
 * {@code MiniMessage.miniMessage().deserialize(...)}. Originally lived duplicated inside
 * {@code MachineMenuConfig.Button} (name/lore templates for machine menu buttons); pulled out
 * here so {@code ItemDefinition}'s own name/lore templates (see {@code ItemType.update}) AND its
 * {@code place_block.fill_*}/{@code pickup} script-toggle conditions (see
 * {@code ItemDefinition.ScriptToggle}) use the EXACT same conventions instead of drifting copies:
 * <ul>
 *   <li>A bare {@code "file.pf:func"} string (no {@code ${}} wrapper) calls that function and uses
 *       its {@code __return__} directly — a Str for a name, an Array of Str for lore, a Bool for a
 *       condition. Extra {@code :arg} segments after the function name are passed as positional
 *       arguments (number/bool literals auto-detected, everything else a Str) — e.g.
 *       {@code "backpack.pf:tier_name:2"}.</li>
 *   <li>Otherwise, every {@code ${expr}} span is substituted via {@link ScriptFormula}.</li>
 * </ul>
 */
public final class TextTemplate {
    private TextTemplate() {}

    /** Evaluate a single template (e.g. an item/button display name) against {@code ctx}. */
    public static String evaluateRaw(String template, ScriptContext ctx) {
        if (template == null) return null;
        if (template.contains(".pf:") && ctx != null) {
            ScriptValue viaScript = callPfFunc(template, ctx);
            if (viaScript != null && !(viaScript instanceof ScriptValue.Array)) return viaScript.asStr();
        }
        if (!template.contains("${")) return template;
        return substitute(template, ctx);
    }

    /** Evaluate a list of lore-style lines against {@code ctx}; a line may itself be a
     *  {@code "file.pf:func[:args]"} call returning an Array of Str, which expands into multiple lines. */
    public static List<String> evaluateLoreRaw(List<String> lines, ScriptContext ctx) {
        if (lines == null || lines.isEmpty()) return lines;
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line != null && line.contains(".pf:") && ctx != null) {
                ScriptValue viaScript = callPfFunc(line, ctx);
                if (viaScript instanceof ScriptValue.Array arr) {
                    for (ScriptValue elem : arr.elements()) result.add(elem.asStr());
                    continue;
                } else if (viaScript != null) {
                    result.add(viaScript.asStr());
                    continue;
                }
            }
            result.add(line != null && line.contains("${") ? substitute(line, ctx) : line);
        }
        return result;
    }

    /** Evaluate a {@code "file.pf:func[:args]"} reference (or a plain {@code "true"}/{@code "false"}
     *  literal) as a boolean condition — the primitive behind {@code ItemDefinition.ScriptToggle}. */
    public static boolean evaluateCondition(String ref, ScriptContext ctx, boolean fallback) {
        if (ref == null) return fallback;
        if (ref.equalsIgnoreCase("true")) return true;
        if (ref.equalsIgnoreCase("false")) return false;
        if (!ref.contains(".pf:") || ctx == null) return fallback;
        try {
            ScriptValue ret = callPfFunc(ref, ctx);
            return ret != null ? ret.asBool() : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static String substitute(String template, ScriptContext ctx) {
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

    /** {@code "file.pf:func:arg1:arg2"} → (scriptFile, funcName, [arg1, arg2]). */
    private record ParsedRef(String scriptFile, String funcName, List<String> rawArgs) {}

    private static ParsedRef parseRef(String ref) {
        String raw = ref.trim();
        int firstColon = raw.indexOf(':');
        String scriptFile = raw.substring(0, firstColon);
        String rest = raw.substring(firstColon + 1);
        String[] parts = rest.split(":");
        String funcName = parts[0];
        List<String> args = parts.length > 1 ? List.of(parts).subList(1, parts.length) : List.of();
        return new ParsedRef(scriptFile, funcName, args);
    }

    /** Auto-detects a raw ":"-separated arg literal as Num/Bool/Str. */
    private static ScriptValue argValue(String raw) {
        if (raw.equalsIgnoreCase("true")) return ScriptValue.of(true);
        if (raw.equalsIgnoreCase("false")) return ScriptValue.of(false);
        try { return ScriptValue.of(Double.parseDouble(raw)); }
        catch (NumberFormatException ignored) { return ScriptValue.of(raw); }
    }

    /** Resolves and calls "{file}.pf:{func}[:args]" with {@code ctx} as the caller context (so the
     *  called function sees the same {@code item}/{@code player}/{@code event} bindings), via
     *  {@link UserFunction#call} — the same depth-guarded, param-binding entry point normal script
     *  calls use. Returns {@code null} if the script/function can't be resolved. */
    public static ScriptValue callPfFunc(String ref, ScriptContext ctx) {
        try {
            ParsedRef parsed = parseRef(ref);
            String lookupKey = parsed.scriptFile().endsWith(".pf")
                    ? parsed.scriptFile().substring(0, parsed.scriptFile().length() - 3) : parsed.scriptFile();
            ScriptProgram prog = ScriptRegistry.get(lookupKey);
            if (prog == null) return null;
            ScriptContext withDefs = prog.evaluate(ctx);
            ScriptValue fnVal = withDefs.getVar(parsed.funcName());
            if (!(fnVal instanceof ScriptValue.Obj fnObj) || !fnObj.typeName().equals(UserFunction.TYPE)) return null;
            UserFunction fn = (UserFunction) fnObj.instance();
            List<ScriptValue> args = new ArrayList<>();
            for (String rawArg : parsed.rawArgs()) args.add(argValue(rawArg));
            return fn.call(args, withDefs);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** Calls "{file}.pf:{func}[:args]"; returns its raw string return value, or {@code null} if the
     *  script/function isn't found or it returned an Array. */
    public static String callPfFuncStr(String ref, ScriptContext ctx) {
        ScriptValue ret = callPfFunc(ref, ctx);
        return ret == null || ret instanceof ScriptValue.Array ? null : ret.asStr();
    }

    /** Calls "{file}.pf:{func}[:args]", expecting its return to be an Array of Str. */
    public static List<String> callPfFuncArray(String ref, ScriptContext ctx) {
        ScriptValue ret = callPfFunc(ref, ctx);
        if (!(ret instanceof ScriptValue.Array arr)) return null;
        List<String> out = new ArrayList<>();
        for (ScriptValue elem : arr.elements()) out.add(elem.asStr());
        return out;
    }
}
