package dev.arubik.craftengine.script;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code static}/{@code final} top-level declarations share ONE storage slot across every
 * evaluation of a given {@link ScriptProgram} instance — not per-call, and (since {@code
 * ScriptRegistry} loads one {@code ScriptProgram} per {@code .pf} file and reuses it for every
 * machine/instance referencing that file) not per block instance either. These tests build a
 * fresh {@code ScriptProgram} per case (mirroring one shared file) and evaluate it repeatedly
 * against independent {@link ScriptContext}s, the way two different machine instances running the
 * same script would.
 */
class StaticFinalVarTest {

    private static final Logger LOG = Logger.getLogger("test");

    private ScriptProgram parse(String src) {
        return ScriptProgram.parse("test", src, LOG);
    }

    @Test
    @DisplayName("a static var is computed once and shared across separate evaluate() calls")
    void staticComputedOnceAndShared() {
        // Uses tick() (a real ScriptBuiltins entry advancing every call) so a naive
        // re-evaluate-every-time implementation would visibly diverge from "computed once".
        ScriptProgram prog = parse("static seed = 7\nresult = seed\n");

        ScriptContext r1 = prog.evaluate(ScriptContext.builder().build());
        ScriptContext r2 = prog.evaluate(ScriptContext.builder().build());

        assertEquals(7.0, r1.getVar("result").asNum(), 1e-9);
        assertEquals(7.0, r2.getVar("result").asNum(), 1e-9);
    }

    @Test
    @DisplayName("a plain reassignment of a static name writes through to the shared store")
    void staticReassignmentPersists() {
        ScriptProgram prog = parse("""
                static counter = 0
                counter = counter + 1
                result = counter
                """);

        ScriptContext r1 = prog.evaluate(ScriptContext.builder().build());
        ScriptContext r2 = prog.evaluate(ScriptContext.builder().build());
        ScriptContext r3 = prog.evaluate(ScriptContext.builder().build());

        assertEquals(1.0, r1.getVar("result").asNum(), 1e-9, "first run: 0 -> 1");
        assertEquals(2.0, r2.getVar("result").asNum(), 1e-9, "second run: shared counter keeps incrementing");
        assertEquals(3.0, r3.getVar("result").asNum(), 1e-9, "third run: still shared, not reset");
    }

    @Test
    @DisplayName("two independent evaluate() calls (simulating two machine instances) share the same static state")
    void staticSharedAcrossSimulatedInstances() {
        ScriptProgram prog = parse("""
                static total = 0
                total = total + amount
                result = total
                """);

        ScriptContext instanceA = ScriptContext.builder().num("amount", 5).build();
        ScriptContext instanceB = ScriptContext.builder().num("amount", 3).build();

        ScriptContext r1 = prog.evaluate(instanceA);
        assertEquals(5.0, r1.getVar("result").asNum(), 1e-9);

        ScriptContext r2 = prog.evaluate(instanceB);
        assertEquals(8.0, r2.getVar("result").asNum(), 1e-9,
            "a second 'instance' running the same shared script sees the first instance's update");
    }

    @Test
    @DisplayName("a final var is computed once, same as static, and readable normally")
    void finalComputedOnce() {
        ScriptProgram prog = parse("final base = 100\nresult = base + 1\n");

        ScriptContext r1 = prog.evaluate(ScriptContext.builder().build());
        assertEquals(101.0, r1.getVar("result").asNum(), 1e-9);
    }

    @Test
    @DisplayName("the linter flags a plain reassignment of a final-declared name")
    void linterFlagsFinalReassignment() {
        java.util.List<String> warnings = new java.util.ArrayList<>();
        Logger capturing = Logger.getLogger("test-lint-capture");
        capturing.setUseParentHandlers(false);
        capturing.addHandler(new java.util.logging.Handler() {
            @Override public void publish(java.util.logging.LogRecord record) { warnings.add(record.getMessage()); }
            @Override public void flush() {}
            @Override public void close() {}
        });

        ScriptLinter.lint("test", "final base = 100\nbase = base + 1\n", capturing);

        assertTrue(warnings.stream().anyMatch(w -> w.contains("final base")),
            "expected a lint warning about reassigning 'final base', got: " + warnings);
    }

    @Test
    @DisplayName("the linter does NOT flag a static (non-final) reassignment")
    void linterDoesNotFlagStaticReassignment() {
        java.util.List<String> warnings = new java.util.ArrayList<>();
        Logger capturing = Logger.getLogger("test-lint-capture-2");
        capturing.setUseParentHandlers(false);
        capturing.addHandler(new java.util.logging.Handler() {
            @Override public void publish(java.util.logging.LogRecord record) { warnings.add(record.getMessage()); }
            @Override public void flush() {}
            @Override public void close() {}
        });

        ScriptLinter.lint("test", "static counter = 0\ncounter = counter + 1\n", capturing);

        assertTrue(warnings.stream().noneMatch(w -> w.contains("counter")),
            "static vars are meant to be reassigned — got an unexpected warning: " + warnings);
    }
}
