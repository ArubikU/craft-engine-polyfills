/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import java.util.Map;

@Deprecated
public final class SpeedFormula {
    public static final SpeedFormula ONE = SpeedFormula.compile("1.0");
    public static final SpeedFormula ZERO = SpeedFormula.compile("0.0");
    private final ScriptFormula inner;

    private SpeedFormula(ScriptFormula f) {
        this.inner = f;
    }

    public static SpeedFormula compile(String expr) {
        return new SpeedFormula(ScriptFormula.compile(expr));
    }

    public double evaluate(Map<String, Double> vars) {
        ScriptContext.Builder b = ScriptContext.builder();
        vars.forEach(b::num);
        return this.inner.evaluateNum(b.build());
    }

    public boolean evaluateBool(Map<String, Double> vars) {
        ScriptContext.Builder b = ScriptContext.builder();
        vars.forEach(b::num);
        return this.inner.evaluateBool(b.build());
    }

    public String toString() {
        return this.inner.toString();
    }
}

