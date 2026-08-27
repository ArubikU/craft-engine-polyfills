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

/**
 * Sweeps every REAL shipped {@code .pf} file through {@link ScriptClassCompiler#tryCompile}, the
 * same "does it hold up against the whole real script set" methodology {@link JitDifferentialTest}
 * already uses for {@link ScriptBytecodeCompiler}. Two things this checks that {@code
 * ScriptClassCompilerTest} (a single hand-picked file, asserted in detail) can't:
 *
 * <ul>
 *   <li>{@code tryCompile} never THROWS past its own catch-all for any real file — a crash here
 *       would still degrade to "compile nothing" for that file (see {@code tryCompile}'s own
 *       doc), never break the interpreter path, but a crash IS still a bug worth surfacing rather
 *       than silently swallowing forever.</li>
 *   <li>How much of the real script set actually becomes eligible now that local dispatch and the
 *       string/array/subscript/bitwise/pow/cross-file-call grammar gaps are closed — logged as a
 *       per-file def/main-body coverage summary, not asserted on (coverage is expected to vary
 *       file to file depending on what constructs each one uses; a LOW-coverage file is a
 *       DIAGNOSTIC signal for what to look at next, not a test failure).</li>
 * </ul>
 */
class ScriptClassCompilerCoverageTest {

    private static final Logger LOG = Logger.getLogger("test");
    private static final Path SCRIPTS_DIR = Path.of("src/main/resources/scripts");

    private static Stream<Path> pfFiles() throws IOException {
        try (var s = Files.walk(SCRIPTS_DIR)) {
            return s.filter(p -> p.toString().endsWith(".pf")).toList().stream();
        }
    }

    @Test
    void tryCompileNeverThrowsAndCoverageIsLogged() throws IOException {
        List<Path> files = pfFiles().toList();
        assertTrue(files.size() > 50, "expected a substantial real script set, got " + files.size());

        int filesWithAnyCompiledOutput = 0;
        int totalDefs = 0;
        int totalDefsCompiled = 0;
        int filesWithMainCompiled = 0;
        List<String> crashed = new ArrayList<>();
        List<String> summary = new ArrayList<>();

        for (Path p : files) {
            String rel = SCRIPTS_DIR.relativize(p).toString().replace('\\', '/');
            // "coverage/" prefix keeps this sweep's generated classes in their own namespace —
            // ScriptClassCompiler's GenLoader is a per-JVM singleton (real usage compiles each
            // distinct file exactly once, ever) that throws on a duplicate class definition, so
            // sharing an originPath with ScriptClassCompilerTest's own dedicated windmill.pf
            // compile (same JVM, same test run) would make whichever test class runs second get
            // null back — caught live via exactly that failure before this prefix was added.
            String originPath = "coverage/" + (rel.endsWith(".pf") ? rel.substring(0, rel.length() - 3) : rel);
            String src;
            try { src = Files.readString(p); } catch (IOException e) { continue; }

            ScriptProgram prog;
            try { prog = ScriptProgram.parse(rel, src, LOG); }
            catch (Throwable ignored) { continue; } // a genuinely malformed file isn't this test's concern

            int defsInFile = 0;
            for (ScriptProgram.Statement s : prog.statementsForCompiler()) {
                if (s instanceof ScriptProgram.Statement.FunctionDef fd
                        && !"__init__".equals(fd.name()) && !"__unload__".equals(fd.name())) {
                    defsInFile++;
                }
            }
            totalDefs += defsInFile;

            ScriptClassCompiler.Compiled compiled;
            try {
                compiled = ScriptClassCompiler.tryCompile(originPath, prog.statementsForCompiler());
            } catch (Throwable t) {
                crashed.add(originPath + " — " + t.getClass().getSimpleName() + ": " + t.getMessage());
                continue;
            }

            if (compiled == null) {
                summary.add(originPath + ": 0/" + defsInFile + " defs, main=NO");
                continue;
            }
            filesWithAnyCompiledOutput++;
            int defsCompiled = compiled.methodsByDefName().size();
            totalDefsCompiled += defsCompiled;
            boolean mainCompiled = compiled.mainMethod() != null;
            if (mainCompiled) filesWithMainCompiled++;
            summary.add(originPath + ": " + defsCompiled + "/" + defsInFile + " defs, main="
                    + (mainCompiled ? "YES" : "no"));
        }

        LOG.info("[ScriptClassCompiler coverage] " + files.size() + " files swept, "
                + filesWithAnyCompiledOutput + " produced a compiled class, "
                + totalDefsCompiled + "/" + totalDefs + " defs compiled overall, "
                + filesWithMainCompiled + " files got a compiled main body.");
        for (String line : summary) LOG.info("[ScriptClassCompiler coverage]   " + line);

        assertTrue(crashed.isEmpty(), "tryCompile crashed (past its own catch-all) on "
                + crashed.size() + " file(s):\n" + String.join("\n", crashed));
    }
}
