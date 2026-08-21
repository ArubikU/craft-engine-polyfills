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
}
