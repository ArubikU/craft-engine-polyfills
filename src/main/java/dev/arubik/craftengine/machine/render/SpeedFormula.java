package dev.arubik.craftengine.machine.render;

import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyFormula;

import java.util.Map;

/**
 * Legacy numeric/boolean math expression wrapper.
 *
 * @deprecated Use {@link PolyFormula} directly for all new code.
 *     {@code SpeedFormula} now delegates to {@link PolyFormula} internally.
 *     Existing call sites continue to work without changes; the {@code evaluate}
 *     and {@code evaluateBool} signatures are unchanged.
 *
 * <p>Migration: replace
 * <pre>{@code
 * SpeedFormula f = SpeedFormula.compile(expr);
 * double result = f.evaluate(vars);
 * }</pre>
 * with
 * <pre>{@code
 * PolyFormula f = PolyFormula.compile(expr);
 * double result = f.evaluateNum(ctx);
 * }</pre>
 */
@Deprecated
public final class SpeedFormula {

    public static final SpeedFormula ONE  = compile("1.0");
    public static final SpeedFormula ZERO = compile("0.0");

    private final PolyFormula inner;

    private SpeedFormula(PolyFormula f) {
        this.inner = f;
    }

    /**
     * Compile an expression string.
     *
     * @param expr the expression to parse
     * @return compiled SpeedFormula
     * @throws IllegalArgumentException on parse error
     */
    public static SpeedFormula compile(String expr) {
        return new SpeedFormula(PolyFormula.compile(expr));
    }

    /**
     * Evaluate the expression using the given variable map.
     * Unknown variables resolve to {@code 0.0}.
     */
    public double evaluate(Map<String, Double> vars) {
        PolyContext.Builder b = PolyContext.builder();
        vars.forEach(b::num);
        return inner.evaluateNum(b.build());
    }

    /** Convenience: evaluate and return {@code true} iff result is non-zero. */
    public boolean evaluateBool(Map<String, Double> vars) {
        PolyContext.Builder b = PolyContext.builder();
        vars.forEach(b::num);
        return inner.evaluateBool(b.build());
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
