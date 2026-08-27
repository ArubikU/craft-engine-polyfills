package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Differential test: for every distinct expression string that appears anywhere in the SHIPPED
 * {@code .pf} scripts, compiles it BOTH with the JIT forced on and forced off (bypassing {@link
 * ScriptFormula}'s cache via {@link ScriptFormula#recompileForTest}), evaluates both against
 * several representative contexts, and asserts they agree.
 *
 * <p>This exists specifically because every other diagnostic added while chasing the "renderers/
 * menus silently do nothing" report (log-once WARNINGs on every previously-swallowed catch block,
 * JIT class-registration/bail logging) has come back clean on real boots and real interactions —
 * meaning if the JIT is at fault, it isn't THROWING, it's computing a plain WRONG VALUE for some
 * real formula shape that isn't in the hand-written unit tests. A wrong boolean flips an {@code if}
 * branch silently; nothing ever logs that. This sweeps every real formula string the shipped
 * scripts actually use, not synthetic examples, which is exactly what none of the earlier
 * correctness fixes (precedence, ==/!=, +, divide-by-zero, classInstance resolution) were checked
 * against at that scale.
 */
class JitDifferentialTest {

    private static final Logger LOG = Logger.getLogger("test");
    private static final Path SCRIPTS_DIR = Path.of("src/main/resources/scripts");

    /** A handful of representative contexts covering: fully empty (exercises NULL-coercion paths
     *  hardest), common numeric/boolean vars bound (the ordinary case), and Machine/Player/World
     *  bound as real non-null class instances (exercises the dot-access classInstance-resolution
     *  path end to end, not short-circuited to NULL). */
    private static List<ScriptContext> testContexts() {
        List<ScriptContext> out = new ArrayList<>();
        out.add(ScriptContext.builder().build());
        out.add(ScriptContext.builder()
                .num("rpm", 5).num("facing_dx", 1).num("facing_dy", 0).num("facing_dz", 0)
                .num("progress", 3).num("max_progress", 10).num("tier", 2)
                .bool("processing", true).bool("powered", false).bool("has_fuel", true)
                .num("redstone", 7).str("name", "steam")
                .build());
        out.add(ScriptContext.builder()
                .num("rpm", -4).num("facing_dx", 0).num("facing_dy", -1).num("facing_dz", 0)
                .bool("processing", false).bool("powered", true).bool("has_fuel", false)
                .num("redstone", 0)
                .typed("Machine", new Object())
                .typed("Player", new Object())
                .typed("World", new Object())
                .typed("Block", new Object())
                .build());
        return out;
    }

    private static Stream<Path> pfFiles() throws IOException {
        try (var s = Files.walk(SCRIPTS_DIR)) {
            return s.filter(p -> p.toString().endsWith(".pf")).toList().stream();
        }
    }

    @Test
    void jitAgreesWithInterpreterOnEveryShippedFormula() throws IOException {
        // Parsing every real script eagerly compiles (and thus caches) every formula string it
        // contains — see ScriptProgram.parse/compile's own doc.
        for (Path p : pfFiles().toList()) {
            String src = Files.readString(p);
            try { ScriptProgram.parse(p.toString(), src, LOG); }
            catch (Throwable ignored) { /* a genuinely malformed file isn't this test's concern */ }
        }

        var exprs = ScriptFormula.compiledExpressionsForTest();
        assertTrue(exprs.size() > 50, "expected a substantial number of real formulas to have been compiled, got " + exprs.size());

        List<ScriptContext> ctxs = testContexts();
        List<String> mismatches = new ArrayList<>();
        int checked = 0;

        for (String expr : exprs) {
            // Genuinely non-deterministic builtins can't agree with themselves across two
            // SEPARATE invocations (one per compiled path) by definition — that's not a JIT
            // correctness bug, it's the whole point of randomness. Excluded from the comparison
            // rather than special-cased per-value.
            if (expr.contains("random(") || expr.contains("rand_int(") || expr.contains("uuid(")) continue;
            ScriptFormula jitForm;
            ScriptFormula interpForm;
            try {
                jitForm = ScriptFormula.recompileForTest(expr, true);
                interpForm = ScriptFormula.recompileForTest(expr, false);
            } catch (Throwable t) {
                continue; // a malformed expr throws identically regardless of JIT — not what this test checks
            }
            checked++;
            for (ScriptContext ctx : ctxs) {
                String jitResult;
                String interpResult;
                boolean jitThrew;
                boolean interpThrew;
                try { jitResult = describe(jitForm.evaluate(ctx)); jitThrew = false; }
                catch (Throwable t) { jitResult = "THREW:" + t.getClass().getSimpleName(); jitThrew = true; }
                try { interpResult = describe(interpForm.evaluate(ctx)); interpThrew = false; }
                catch (Throwable t) { interpResult = "THREW:" + t.getClass().getSimpleName(); interpThrew = true; }

                // Both sides throwing is fine (same malformed-runtime-input behavior either way) —
                // only flag when they DISAGREE, which is the actual "computes a different value"
                // bug this test exists to catch.
                if (jitThrew && interpThrew) continue;
                if (!jitResult.equals(interpResult)) {
                    mismatches.add("expr=\"" + expr + "\" jit=" + jitResult + " interp=" + interpResult);
                }
            }
        }

        if (!mismatches.isEmpty()) {
            fail("JIT/interpreter disagreed on " + mismatches.size() + " (expr, context) pair(s) out of "
                + checked + " distinct real formulas checked:\n" + String.join("\n", mismatches));
        }
    }

    private static String describe(ScriptValue v) {
        return switch (v) {
            case ScriptValue.Num n -> "Num(" + n.value() + ")";
            case ScriptValue.Bool b -> "Bool(" + b.value() + ")";
            case ScriptValue.Str s -> "Str(" + s.value() + ")";
            case ScriptValue.Null ignored -> "Null";
            case ScriptValue.Item i -> "Item(" + (i.stack() == null || i.stack().isEmpty()) + ")";
            case ScriptValue.Array a -> "Array(size=" + a.elements().size() + ")";
            case ScriptValue.Obj o -> "Obj(" + o.typeName() + ")";
        };
    }
}
