package dev.arubik.craftengine.script;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code Machine.redstone} grew from a bare int into a Redstone object with methods. That is only
 * safe because an {@link ScriptValue.Obj} whose instance implements {@link ScriptValue.Numeric}
 * still coerces to a number, so the nine shipped scripts that treat it as one keep working:
 * {@code Machine.redstone > 0}, {@code power = Machine.redstone}, {@code power / 15.0}.
 *
 * <p>Every comparison and arithmetic operator in ScriptFormula routes through {@code asNum()},
 * so pinning the coercion pins all of them.
 */
class ScriptValueNumericTest {

    private static final Logger LOG = Logger.getLogger("test");

    /** Stand-in for RedstoneType.RedstoneRef, which needs a live level to construct. */
    record FakePort(double level) implements ScriptValue.Numeric {
        @Override public double numericValue() { return level; }
    }

    private static ScriptValue port(double level) {
        return ScriptValue.ofObj("FakePort", new FakePort(level));
    }

    @Test
    @DisplayName("a Numeric object coerces to its scalar")
    void numericObjectCoercesToNumber() {
        assertEquals(9.0, port(9).asNum(), 1e-9);
        assertEquals(0.0, port(0).asNum(), 1e-9);
    }

    @Test
    @DisplayName("a Numeric object is truthy exactly when non-zero")
    void numericObjectCoercesToBool() {
        assertTrue(port(1).asBool());
        assertTrue(port(15).asBool());
        assertFalse(port(0).asBool(), "an unpowered port must be falsy, like the old int 0");
    }

    @Test
    @DisplayName("a plain object with no Numeric keeps the old behaviour")
    void nonNumericObjectUnchanged() {
        ScriptValue plain = ScriptValue.ofObj("Thing", new Object());
        assertEquals(0.0, plain.asNum(), 1e-9);
        assertTrue(plain.asBool(), "a non-null instance stays truthy");
    }

    @Test
    @DisplayName("comparisons against a Numeric object behave like the old int")
    void comparisonsWorkThroughTheScript() {
        // Mirrors pulser.pf / redstone_latch.pf: `cur = Machine.redstone > 0 ? 1 : 0`
        assertEquals(1.0, evalWithPort("result = port > 0 ? 1 : 0", 12), 1e-9);
        assertEquals(0.0, evalWithPort("result = port > 0 ? 1 : 0", 0), 1e-9);
    }

    @Test
    @DisplayName("arithmetic against a Numeric object behaves like the old int")
    void arithmeticWorksThroughTheScript() {
        // Mirrors fan.pf: `power = Machine.redstone` then `strength = power / 15.0`
        assertEquals(1.0, evalWithPort("result = port / 15.0", 15), 1e-9);
        assertEquals(0.5, evalWithPort("result = port / 15.0", 7.5), 1e-9);
    }

    private double evalWithPort(String script, double level) {
        ScriptProgram prog = ScriptProgram.parse("numeric_test", script, LOG);
        ScriptContext ctx = prog.evaluate(
                ScriptContext.builder().val("port", port(level)).build());
        return ctx.getVar("result").asNum();
    }
}
