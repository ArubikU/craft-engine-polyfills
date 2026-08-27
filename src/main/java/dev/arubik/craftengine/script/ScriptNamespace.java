package dev.arubik.craftengine.script;

import java.util.List;

/**
 * Runtime backing for a namespace-style {@code import "path"} / {@code import "path" as alias} —
 * wraps the imported .pf file's own evaluated top-level context so its defs/values are addressable
 * as {@code alias.func(args)} / {@code alias.CONST} from the importing script, the same dotted
 * dispatch {@code Machine.*}/{@code Server.*} already get (see {@link PolyClass}, which
 * {@link ScriptValue#callMethod}/{@link ScriptValue#getProperty} check before anything else).
 */
public final class ScriptNamespace implements PolyClass {
    private final ScriptContext ctx;

    public ScriptNamespace(ScriptContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public ScriptValue get(String property) {
        return ctx.getVar(property);
    }

    @Override
    public ScriptValue call(String method, List<ScriptValue> args) {
        ScriptValue fnVal = ctx.getVar(method);
        if (fnVal instanceof ScriptValue.Obj fnObj && UserFunction.TYPE.equals(fnObj.typeName())
                && fnObj.instance() instanceof UserFunction fn) {
            return fn.call(args, ctx);
        }
        return ScriptValue.NULL;
    }
}
