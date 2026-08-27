package dev.arubik.craftengine.script;

import java.util.Arrays;
import java.util.List;

/**
 * Parsed reference to a script call.
 * Syntax: "filename[.pf][:funcname[:arg1:arg2...]]"
 *
 * The separator is always ":" — if the string contains ".pf:", everything before is the
 * script file and everything after is funcname[:args...].
 *
 * Examples:
 *   "gas_motor"                   → execute whole gas_motor.pf
 *   "gas_motor.pf"                → same
 *   "gas_motor.pf:on_break"       → load gas_motor.pf, register defs, call on_break()
 *   "gas_motor.pf:increase_rpm:8" → call increase_rpm("8")
 *   "cogwheel.pf:tick_large"      → call tick_large() from cogwheel.pf
 */
public record ScriptCall(String scriptName, String funcName, List<String> args) {

    public static ScriptCall parse(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim();

        // Primary format: "filename.pf:funcname[:args]"
        int pfColon = s.indexOf(".pf:");
        if (pfColon >= 0) {
            String scriptPart = s.substring(0, pfColon); // strip .pf suffix below
            String rest = s.substring(pfColon + 4);      // after ".pf:"
            String[] parts = rest.split(":", -1);
            String funcName = parts[0].trim();
            List<String> args = parts.length > 1 ? Arrays.asList(parts).subList(1, parts.length) : List.of();
            return new ScriptCall(scriptPart, funcName.isBlank() ? null : funcName, args);
        }

        // No function — plain "filename" or "filename.pf"
        String scriptPart = s.endsWith(".pf") ? s.substring(0, s.length() - 3) : s;
        return new ScriptCall(scriptPart, null, List.of());
    }

    /** Execute against ctx. Returns resulting context. */
    public ScriptContext execute(ScriptContext ctx) {
        ScriptProgram prog = ScriptRegistry.get(scriptName);
        if (prog == null) return ctx;
        if (funcName == null || funcName.isBlank()) {
            return prog.evaluate(ctx);
        }
        ScriptContext withDefs = prog.evaluate(ctx);
        ScriptValue fnVal = withDefs.getVar(funcName);
        if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
            UserFunction fn = (UserFunction) fnObj.instance();
            // Convert string args to ScriptValues and call via fn.call() so params are bound by name.
            // This ensures function params (e.g. "amount") are set correctly, not just arg0/arg1.
            java.util.List<ScriptValue> svArgs = new java.util.ArrayList<>(args.size());
            for (String a : args) svArgs.add(ScriptValue.of(a));
            fn.call(svArgs, withDefs);
            // Return context after execution (withDefs + any side effects via Machine methods)
            return withDefs;
        }
        return withDefs;
    }

    /**
     * Like {@link #execute}, but appends {@code extraArgs} after the ref's own static args instead
     * of relying only on what was written into the action-string at parse time — for a caller that
     * only learns part of the argument list at runtime (e.g. a Dialog's accept callback appending
     * whatever the player just typed/selected, which obviously can't be baked into the ref string).
     */
    public ScriptContext executeWithExtraArgs(ScriptContext ctx, List<ScriptValue> extraArgs) {
        ScriptProgram prog = ScriptRegistry.get(scriptName);
        if (prog == null) return ctx;
        ScriptContext withDefs = prog.evaluate(ctx);
        if (funcName == null || funcName.isBlank()) return withDefs;
        ScriptValue fnVal = withDefs.getVar(funcName);
        if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
            UserFunction fn = (UserFunction) fnObj.instance();
            List<ScriptValue> svArgs = new java.util.ArrayList<>(args.size() + extraArgs.size());
            for (String a : args) svArgs.add(ScriptValue.of(a));
            svArgs.addAll(extraArgs);
            fn.call(svArgs, withDefs);
        }
        return withDefs;
    }

    /**
     * Like {@link #execute}, but returns the called function's own return value instead of the
     * post-execution context — for predicate-style scripts (e.g. a storage-slot filter) that
     * report a result rather than mutate state. {@code ScriptValue.NULL} if the ref names no
     * function (a plain script file) or the function isn't found.
     */
    public ScriptValue evaluate(ScriptContext ctx) {
        return evaluate(ctx, ScriptValue::of);
    }

    /**
     * Like {@link #evaluate(ScriptContext)}, but converts each raw string arg via {@code
     * argConverter} instead of always wrapping it as a plain Str — e.g. TextTemplate's own auto
     * Num/Bool/Str detection for name/lore templates, where a literal {@code "8"} should reach the
     * callee as a Num, not a Str. This (plus {@link #parse}) is the single canonical place that
     * parses/calls a {@code "file.pf:func:args"} reference — every spot that used to hand-roll its
     * own {@code indexOf(':')}/{@code split(":")} for this same convention (TextTemplate,
     * DynamicText, ScriptFormula's inline "file.pf:func:args" primary) now goes through here
     * instead of drifting copies of the same parsing/calling logic.
     */
    public ScriptValue evaluate(ScriptContext ctx, java.util.function.Function<String, ScriptValue> argConverter) {
        ScriptProgram prog = ScriptRegistry.get(scriptName);
        if (prog == null || funcName == null || funcName.isBlank()) return ScriptValue.NULL;
        ScriptContext withDefs = prog.evaluate(ctx);
        ScriptValue fnVal = withDefs.getVar(funcName);
        if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
            UserFunction fn = (UserFunction) fnObj.instance();
            java.util.List<ScriptValue> svArgs = new java.util.ArrayList<>(args.size());
            for (String a : args) svArgs.add(argConverter.apply(a));
            return fn.call(svArgs, withDefs);
        }
        return ScriptValue.NULL;
    }

    /** Like {@link #evaluate}, but appends {@code extraArgs} after the ref's own static args —
     *  the return-a-value counterpart of {@link #executeWithExtraArgs}, for a caller (e.g. a
     *  registered PlaceholderAPI expansion — see {@code PlaceholderSupport#registerPlaceholder})
     *  that needs to hand the script a value only known at request time (the placeholder's
     *  {@code params} text) and read back what the script computed from it. */
    public ScriptValue evaluateWithExtraArgs(ScriptContext ctx, List<ScriptValue> extraArgs) {
        ScriptProgram prog = ScriptRegistry.get(scriptName);
        if (prog == null || funcName == null || funcName.isBlank()) return ScriptValue.NULL;
        ScriptContext withDefs = prog.evaluate(ctx);
        ScriptValue fnVal = withDefs.getVar(funcName);
        if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
            UserFunction fn = (UserFunction) fnObj.instance();
            List<ScriptValue> svArgs = new java.util.ArrayList<>(args.size() + extraArgs.size());
            for (String a : args) svArgs.add(ScriptValue.of(a));
            svArgs.addAll(extraArgs);
            return fn.call(svArgs, withDefs);
        }
        return ScriptValue.NULL;
    }
}
