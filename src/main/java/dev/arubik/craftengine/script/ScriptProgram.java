package dev.arubik.craftengine.script;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

/**
 * A loaded {@code .pf} script file — the PolyFill interpreter.
 *
 * <p>Supports: assignments, if/else-if/else, for-in loops, while loops,
 * break, continue, i++/i--, i+=/-=/*=, comments (#).</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * speed = abs(rpm) / 10
 * if speed > 0 {
 *     for item in Machine.nearby_items(3) {
 *         Machine.push_item_to_inventory(item)
 *     }
 * }
 * }</pre>
 *
 * Port of PolyScript to the new ScriptFormula / ScriptContext system.
 */
public final class ScriptProgram {

    private static final Logger LOG_ERR = Logger.getLogger("CraftEnginePolyfills");
    /** Every distinct (script, statement) pair whose evaluation has already logged a thrown
     *  exception — a statement runs every tick, so without this ONE broken line would spam a log
     *  entry per tick forever. Logged once per distinct key for the life of the JVM (matches
     *  {@code ScriptBytecodeCompiler}'s own logging convention for the same reason). */
    private static final java.util.Set<String> LOGGED_STMT_FAIL = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private void logStatementFailure(String kind, String detail, Throwable t) {
        String key = name + "|" + kind + "|" + detail;
        if (LOGGED_STMT_FAIL.add(key)) {
            LOG_ERR.log(java.util.logging.Level.WARNING,
                "[CEPolyfills] " + name + ".pf: " + kind + " '" + detail + "' threw "
                    + t.getClass().getSimpleName() + (t.getMessage() != null ? ": " + t.getMessage() : "")
                    + " (further occurrences of this exact line are suppressed)", t);
        }
    }

    private final String name;
    private final Map<String, ScriptFormula> topLevel;
    private final List<Statement> statements;

    /** Lazily/once-computed via {@link #ensureCompiled()} — {@code null} means either "not
     *  attempted yet" (check {@link #compileAttempted}) or "attempted and nothing in this file
     *  qualified" (see {@link ScriptClassCompiler#tryCompile}'s own doc for what disqualifies a
     *  whole file). Uses this program's bare {@link #name} as the compiler's originPath rather
     *  than the folder-relative path {@code ScriptRegistry} computes at load time (not threaded
     *  through to {@code ScriptProgram} today) — the ONLY effect of that is cosmetic (the
     *  generated class's package won't mirror the file's real folder the way {@code
     *  ScriptClassCompilerTest}'s dedicated naming assertions describe), not a correctness
     *  concern: two DIFFERENT files sharing the same bare name would collide on the SAME
     *  generated binary name, but {@code GenLoader.define}'s resulting {@code LinkageError} is
     *  already caught by {@code tryCompile}'s own catch-all, degrading that second file back to
     *  the interpreter exactly as if it had never compiled — never a crash. */
    private volatile ScriptClassCompiler.Compiled compiledClass;
    private volatile boolean compileAttempted;

    private static final java.util.Set<String> LOGGED_COMPILED_DEF_FAIL = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /** Every distinct (script, def) pair whose compiled-method invocation has already logged a
     *  thrown exception — same dedup convention as {@link #logStatementFailure}, for the same
     *  reason (a def can run every tick). Deliberately does NOT retry the interpreter on failure
     *  — see the {@code FunctionDef} case's own comment in {@link #runStatements} for why a
     *  partially-mutated {@code resultB} makes that unsafe; this only logs so a real problem is
     *  visible, then degrades that ONE call to {@code NULL}. */
    private void logCompiledDefFailure(String defName, Throwable t) {
        String key = name + "|" + defName;
        if (LOGGED_COMPILED_DEF_FAIL.add(key)) {
            LOG_ERR.log(java.util.logging.Level.WARNING,
                "[CEPolyfills] " + name + ".pf: compiled def '" + defName + "' threw "
                    + t.getClass().getSimpleName() + (t.getMessage() != null ? ": " + t.getMessage() : "")
                    + " — falling back to NULL for this call (further occurrences are suppressed)", t);
        }
    }

    /** Compiles this file's {@code def}s exactly ONCE (memoized — {@link ScriptClassCompiler}'s
     *  {@code GenLoader} can't redefine the same class twice, so a second attempt would only ever
     *  throw), on the FIRST call that needs it. Never re-attempted even if it returns {@code
     *  null} (nothing in this file qualified) — same one-shot contract {@code
     *  ScriptClassCompiler.tryCompile}'s own doc already describes. */
    private volatile boolean compiling;

    private ScriptClassCompiler.Compiled ensureCompiled() {
        if (compileAttempted) return compiledClass;
        synchronized (this) {
            if (compileAttempted) return compiledClass;
            // Re-entrancy guard for an import CYCLE: compiling A now asks B to compile so A can
            // call into it directly, and B may import A right back. Reporting "not compiled" to the
            // inner request breaks the loop — that call site just keeps the interpreted path.
            // Without this the same thread re-enters and recurses until the stack gives out.
            if (compiling) return null;
            compiling = true;
            try { compiledClass = ScriptClassCompiler.tryCompile(name, statements); }
            catch (Throwable ignored) { compiledClass = null; }
            finally { compiling = false; }
            compileAttempted = true;
            return compiledClass;
        }
    }

    /**
     * A direct-call target for {@code defName} in THIS file, for a compiled caller in a DIFFERENT
     * file that imported it — or null when a direct call would not be equivalent.
     *
     * <p>The interpreter reaches an imported function through {@code UserFunction.call}, which
     * layers this file's own defining context UNDER the caller's before running the body. A plain
     * {@code INVOKESTATIC} passing only the caller's builder skips that layer, which is fine for
     * most functions — a sibling {@code def} it calls is resolved statically at compile time, so it
     * never needed the context for that — but NOT for one that reads a file-level name of its own.
     * {@code kinetics/tree_utils.pf} is exactly that case: {@code _find_tree} reads the top-level
     * {@code OFFSETS6}, which the importing file's context has never heard of, and the failure would
     * be a silent NULL rather than an error.
     *
     * <p>So the check is: refuse if any of this file's top-level assigned names appears anywhere in
     * the def's body. Deliberately a coarse textual test — over-refusing only costs the caller its
     * old interpreted path, while under-refusing would silently change behaviour.
     */
    ScriptBytecodeCompiler.LocalTarget crossFileTargetFor(String defName) {
        ScriptClassCompiler.Compiled c = ensureCompiled();
        if (c == null) return null;
        java.lang.reflect.Method m = c.methodsByDefName().get(defName);
        if (m == null) return null;

        Statement.FunctionDef def = null;
        java.util.Set<String> fileLevelNames = new java.util.HashSet<>();
        for (Statement s : statements) {
            if (s instanceof Statement.FunctionDef fd) {
                if (fd.name().equals(defName)) def = fd;
            } else if (s instanceof Statement.Assign a) {
                fileLevelNames.add(a.name());
            } else if (s instanceof Statement.StaticDecl sd) {
                fileLevelNames.add(sd.name());
            }
        }
        if (def == null) return null;
        if (!fileLevelNames.isEmpty() && ScriptClassCompiler.bodyMentionsAnyName(def.body(), fileLevelNames)) {
            return null;
        }
        return new ScriptBytecodeCompiler.LocalTarget(
                m.getDeclaringClass().getName().replace('.', '/'), m.getName(), def.params());
    }

    /** The compiled static method for {@code defName}, or {@code null} when the JIT kill switch
     *  is off, this file didn't compile at all, or this SPECIFIC def wasn't eligible (see {@link
     *  ScriptClassCompiler}'s per-def bail model). Checked on every {@code FunctionDef} binding —
     *  see {@link ScriptClassCompiler#CLASS_JIT_ENABLED}'s own doc for why that's deliberate (an
     *  instant, no-reload kill switch). */
    private java.lang.reflect.Method compiledMethodFor(String defName) {
        if (!ScriptClassCompiler.CLASS_JIT_ENABLED) return null;
        ScriptClassCompiler.Compiled c = ensureCompiled();
        return c == null ? null : c.methodsByDefName().get(defName);
    }

    /** True when every top-level statement is a {@code def}/{@code import} — no top-level
     *  {@code Assign}/{@code ExprStatement}/control-flow that could read the CALLER's context.
     *  Covers the simplest machine .pf files (a flat collection of {@code def foo() {...}} blocks).
     *  {@code topLevelCacheable} (below) subsumes this for the common case that ALSO has top-level
     *  constant {@code Assign}s (e.g. {@code saw.pf}'s {@code BASE_RPM = 32}) — kept as a separate,
     *  eagerly-known flag only because it's free to compute at construction time (no ctx needed),
     *  unlike the dependency-tracked check. */
    private final boolean pureDefs;
    private volatile ScriptContext cachedDefsResult;

    /** Backing storage for {@code static}/{@code final} top-level declarations (see {@link
     *  Statement.StaticDecl}) — one entry per declared name, computed the first time its {@code
     *  StaticDecl} statement runs and shared by every future {@link #evaluate} call against THIS
     *  {@code ScriptProgram} instance (which is itself already the single shared object for every
     *  machine/instance referencing this file — see {@code ScriptRegistry}). A plain {@code NAME =
     *  expr} reassignment elsewhere in the file targets this store instead of the caller's local
     *  context whenever {@code NAME} is a known static name (see {@code runStatements}' {@code
     *  Assign} case) — that's what makes it genuinely mutable shared state, not a one-shot
     *  constant. {@code ConcurrentHashMap} since a shared script can run on multiple machine
     *  instances within the same tick. */
    private final java.util.concurrent.ConcurrentHashMap<String, ScriptValue> staticStore = new java.util.concurrent.ConcurrentHashMap<>();

    /** Null until the first {@link #evaluate} call determines it (needs a real {@link ScriptContext}
     *  to test top-level {@code Assign} formulas against — see {@link #determineTopLevelCacheable}).
     *  TRUE means the ENTIRE top level (defs, imports, AND any top-level Assigns) is safe to
     *  register once and reuse forever, going beyond {@link #pureDefs}: a top-level {@code Assign}
     *  is allowed as long as dependency-tracking (the same mechanism {@code RendererManager}'s
     *  shared-formula cache uses) proves its formula reads ONLY names already established as safe —
     *  an earlier top-level Assign/def/import in the SAME file, or nothing at all (a bare numeric
     *  literal). This is exactly what lets {@code saw.pf}-style files (imports + a few constant
     *  Assigns like {@code BASE_RPM = 32} + many defs) get the same one-time registration
     *  {@code pureDefs} files already enjoyed, instead of re-registering every function closure on
     *  every single tick just because a handful of harmless constants sit above the defs. ANY other
     *  top-level statement kind (ExprStatement, if/for/while, ...), or an Assign that reads
     *  anything NOT already proven safe (a real {@code Machine.*}/{@code World.*} read, a builtin
     *  whose result could vary, ...), disqualifies the WHOLE file — falls back to today's correct
     *  per-call re-evaluation, unchanged. */
    private volatile Boolean topLevelCacheable;

    // ---- Statement model -----------------------------------------------------

    sealed interface Statement {
        record Assign(String name, ScriptFormula formula) implements Statement {}
        record ExprStatement(ScriptFormula formula) implements Statement {}
        record IfChain(List<Clause> clauses) implements Statement {}
        /** {@code vars} holds one name for a plain {@code for x in arr}, or two for Polyloft-style
         *  destructuring ({@code for key, value in map}). {@code guardExpr} is the optional
         *  Polyloft {@code where <cond>} clause — null when absent, re-checked every iteration and
         *  skipping (not stopping) elements that fail it. */
        record ForStatement(List<String> vars, String iterExpr, String guardExpr, List<Statement> body) implements Statement {}
        record WhileStatement(String condExpr, List<Statement> body, int maxIter) implements Statement {}
        record BreakStatement() implements Statement {}
        record ContinueStatement() implements Statement {}
        record ReturnStatement(String expr) implements Statement {}
        record FunctionDef(String name, List<String> params, List<Statement> body) implements Statement {}
        /** {@code import "path"} (namespace, default name = last path segment),
         *  {@code import "path" as alias} (namespace, custom name), or
         *  {@code import "path" { name1, name2 }} (selective — binds those names directly into
         *  the importing script's own scope, no namespace object at all). {@code path} is resolved
         *  relative to the scripts/ folder — see {@link ScriptRegistry#getOrLoadByPath}. */
        record Import(String path, String alias, List<String> symbols) implements Statement {}
        /** {@code static NAME = expr} / {@code final NAME = expr} — a value computed ONCE (the
         *  first time this exact statement runs, ever) and shared by EVERY future evaluation of
         *  this {@code ScriptProgram} — not per-call, and not per-block-instance either: since
         *  {@code ScriptRegistry} loads one {@code ScriptProgram} per {@code .pf} file and reuses
         *  it for every machine/renderer/script-call that references that file, a static var here
         *  is genuinely global across every instance running this script. {@code isFinal} only
         *  affects {@link ScriptLinter} (flags a later plain reassignment of the same name as a
         *  lint warning) — both kinds are equally shared/write-once-computed at runtime; the
         *  distinction is purely "should reassigning this ever be considered a mistake". See
         *  {@link #staticStore}. */
        record StaticDecl(String name, ScriptFormula formula, boolean isFinal) implements Statement {}
    }

    record Clause(ScriptFormula condition, List<Statement> body) {
        boolean isElse() { return condition == null; }
    }

    /** Signal thrown to implement break/continue without exceptions-as-flow-control overhead. */
    private static final class BreakSignal extends RuntimeException {
        BreakSignal() { super(null, null, true, false); }
    }
    private static final class ContinueSignal extends RuntimeException {
        ContinueSignal() { super(null, null, true, false); }
    }
    private static final class ReturnSignal extends RuntimeException {
        final ScriptValue value;
        ReturnSignal(ScriptValue v) { super(null, null, true, false); this.value = v; }
    }

    // ---- Construction --------------------------------------------------------

    private ScriptProgram(String name, Map<String, ScriptFormula> topLevel, List<Statement> statements) {
        this.name = name;
        this.topLevel = topLevel;
        this.statements = statements;
        boolean allDefsOrImports = true;
        for (Statement s : statements) {
            if (!(s instanceof Statement.FunctionDef) && !(s instanceof Statement.Import)) {
                allDefsOrImports = false;
                break;
            }
        }
        this.pureDefs = allDefsOrImports;
    }

    public String name() { return name; }
    public Map<String, ScriptFormula> entries() { return topLevel; }
    public ScriptFormula get(String name) { return topLevel.get(name); }

    /** Package-private: lets {@code ScriptClassCompiler} inspect the parsed statement tree
     *  without re-parsing. Not part of the public API — {@code Statement} itself is
     *  package-private. */
    List<Statement> statementsForCompiler() { return statements; }

    // ---- Loading -------------------------------------------------------------

    public static ScriptProgram load(File file) {
        Logger log = java.util.logging.Logger.getLogger("CraftEnginePolyfills");
        String scriptName = file.getName().replaceAll("\\.pf$", "");
        String src;
        try {
            src = new String(Files.readAllBytes(file.toPath()));
        } catch (Throwable ex) {
            log.warning("[CEPolyfills] Failed to load script " + file + ": " + ex.getMessage());
            return new ScriptProgram(scriptName, Map.of(), List.of());
        }
        return parse(scriptName, src, log);
    }

    public static ScriptProgram parse(String name, String src, Logger log) {
        try { ScriptLinter.lint(name, src, log); } catch (Throwable ignored) {
            // Lint failures must never block a script from loading — it's a hint pass, not a gate.
        }
        Tokenizer tok = new Tokenizer(src);
        Parser par = new Parser(name, tok, log);
        List<Statement> stmts = par.parseBlock(false);
        Map<String, ScriptFormula> entries = new LinkedHashMap<>();
        for (Statement s : stmts)
            if (s instanceof Statement.Assign a) entries.put(a.name(), a.formula());
        return new ScriptProgram(name, Collections.unmodifiableMap(entries), stmts);
    }

    // ---- Evaluation ----------------------------------------------------------

    /**
     * Run the script against {@code ctx}, returning an updated context with new variable values.
     */
    public ScriptContext evaluate(ScriptContext ctx) {
        if (pureDefs || isTopLevelCacheable(ctx)) {
            ScriptContext defs = cachedDefsResult;
            if (defs != null) {
                return ScriptContext.builder().copyFrom(ctx)
                        .valsAll(defs.vars())
                        .typedAll(defs.classInstances())
                        .build();
            }
        }
        ScriptContext.Builder b = ScriptContext.builder().copyFrom(ctx);
        try { runStatements(statements, b); }
        catch (ReturnSignal rs) { b.val("__return__", rs.value); }
        return b.build();
    }

    /** Determines (once, lazily — see {@link #topLevelCacheable}'s own doc) whether this file's
     *  ENTIRE top level is safe to register once and reuse forever, and if so populates {@link
     *  #cachedDefsResult} as a side effect of that same determination pass (no separate second
     *  evaluation needed). Walks {@link #statements} in declaration order, maintaining a growing
     *  {@code safeNames} set: a {@code FunctionDef}'s or {@code Import}'s bound name(s) are always
     *  safe (registering them never executes anything ctx-dependent); an {@code Assign} is safe
     *  ONLY if {@link ScriptContext#beginTracking dependency-tracking} its formula's real
     *  evaluation (against the SAME accumulating builder every other top-level statement writes
     *  into) shows it read nothing outside {@code safeNames} — after which its own name joins the
     *  set too, so LATER assigns may safely reference it. Any other statement kind, or an Assign
     *  that fails this check, aborts immediately and returns false — {@link #cachedDefsResult}
     *  stays whatever it already was (null, if this is the first/only determination attempt). */
    private boolean isTopLevelCacheable(ScriptContext ctx) {
        Boolean known = topLevelCacheable;
        if (known != null) return known;
        synchronized (this) {
            known = topLevelCacheable;
            if (known != null) return known;
            java.util.Set<String> safeNames = new java.util.HashSet<>();
            ScriptContext.Builder b = ScriptContext.builder();
            boolean cacheable = true;
            for (Statement s : statements) {
                if (s instanceof Statement.FunctionDef fd) {
                    runStatements(java.util.List.of(s), b);
                    safeNames.add(fd.name());
                } else if (s instanceof Statement.Import imp) {
                    runStatements(java.util.List.of(s), b);
                    if (!imp.symbols().isEmpty()) safeNames.addAll(imp.symbols());
                    else safeNames.add(imp.alias() != null ? imp.alias() : defaultNamespaceName(imp.path()));
                } else if (s instanceof Statement.Assign a) {
                    java.util.Set<String> tracked = ScriptContext.beginTracking();
                    ScriptValue val;
                    try { val = a.formula().evaluate(b.peek()); }
                    catch (Throwable ignored) { cacheable = false; break; }
                    finally { ScriptContext.endTracking(); }
                    if (!safeNames.containsAll(tracked)) { cacheable = false; break; }
                    b.val(a.name(), val);
                    safeNames.add(a.name());
                } else {
                    // Any other top-level statement kind (ExprStatement, if/for/while, return,
                    // break, continue) is too complex to prove side-effect-free/ctx-independent
                    // here — disqualify the WHOLE file rather than risk it.
                    cacheable = false;
                    break;
                }
            }
            if (cacheable) cachedDefsResult = b.build();
            topLevelCacheable = cacheable;
            return cacheable;
        }
    }

    /**
     * Run the script and return the final value of a named variable, or NULL.
     */
    public ScriptValue evaluateVar(ScriptContext ctx, String varName) {
        ScriptContext result = evaluate(ctx);
        return result.getVar(varName);
    }

    /** Resolves a {@code for <vars> in <iterExpr>} header's iterable into rows ready for binding —
     *  Map destructuring (one var walks keys, two walk key+value), Array/single-value/failure
     *  handling, all exactly as {@code runStatements}' own {@code ForStatement} case used to inline
     *  here directly. Extracted so {@link ScriptClassCompiler}'s generated bytecode can call this
     *  SAME method (real {@code for}/{@code while} loop support there is built on top of it) rather
     *  than re-deriving these semantics in raw bytecode, where the two implementations could
     *  silently drift apart. Returns {@code null} when there's nothing to iterate (a failed
     *  evaluation, or a {@code Null} iterable) — the caller skips the loop entirely, never NPEs.
     *  {@code public} (not package-private): {@code ScriptClassCompiler} generates classes in a
     *  package that mirrors the originating {@code .pf} file's folder — never {@code
     *  dev.arubik.craftengine.script} itself — loaded by its own {@code ClassLoader}, so a
     *  package-private method here is a different runtime package by both measures and would fail
     *  with a real {@code IllegalAccessError} at the first actual for-loop call. */
    public static List<ScriptValue[]> resolveForRows(String iterExpr, ScriptContext ctx, int varCount) {
        try {
            ScriptValue iterable = ScriptFormula.compile(iterExpr).evaluate(ctx);
            if (iterable instanceof ScriptValue.Obj o && "Map".equals(o.typeName())
                    && o.instance() instanceof Map<?, ?> rawMap) {
                List<ScriptValue[]> rows = new ArrayList<>();
                for (Map.Entry<?, ?> e : rawMap.entrySet()) {
                    ScriptValue key = ScriptValue.of(String.valueOf(e.getKey()));
                    ScriptValue val = (ScriptValue) e.getValue();
                    rows.add(varCount >= 2 ? new ScriptValue[]{key, val} : new ScriptValue[]{key});
                }
                return rows;
            }
            List<ScriptValue> elems = switch (iterable) {
                case ScriptValue.Array a -> a.elements();
                case ScriptValue.Null ignored -> null;
                default -> List.of(iterable);
            };
            return elems == null ? null : elems.stream().map(v -> new ScriptValue[]{v}).toList();
        } catch (Throwable ignored) {
            return null;
        }
    }

    // Instance method (not static) specifically so a FunctionDef's captured executor closure and
    // every nested block below keep resolving to THIS ScriptProgram's own staticStore — a `def`
    // declared in file A that reads/writes a `static` var must always hit file A's shared slot,
    // never some other file's, even when called indirectly (imported, or invoked as a callback).
    private void runStatements(List<Statement> stmts, ScriptContext.Builder b) {
        for (Statement stmt : stmts) {
            switch (stmt) {
                case Statement.Assign a -> {
                    // peek(), not build() — this snapshot is read once, right here, then thrown
                    // away before the next mutation (b.val below). A real defensive copy is wasted
                    // work for something never held past this one synchronous read.
                    ScriptContext snap = b.peek();
                    try {
                        ScriptValue v = a.formula().evaluate(snap);
                        // A plain reassignment of a name already claimed by a `static`/`final`
                        // top-level declaration (see Statement.StaticDecl below) writes THROUGH to
                        // the shared store, not just this call's local context — that's what makes
                        // `static` genuinely mutable shared state instead of a one-shot constant.
                        if (staticStore.containsKey(a.name())) staticStore.put(a.name(), v);
                        b.val(a.name(), v);
                    } catch (Throwable t) { logStatementFailure("assign", a.name() + " = " + a.formula(), t); }
                }
                case Statement.StaticDecl sd -> {
                    // Computed once per ScriptProgram (ever) via computeIfAbsent's atomicity, then
                    // just re-bound into the local context on every later run — see staticStore's
                    // own doc for why this is the right scope (shared across every instance running
                    // this file, not per-call and not per-block).
                    ScriptValue v = staticStore.computeIfAbsent(sd.name(), n -> {
                        ScriptContext snap = b.peek();
                        try { return sd.formula().evaluate(snap); }
                        catch (Throwable t) {
                            logStatementFailure(sd.isFinal() ? "final" : "static", sd.name() + " = " + sd.formula(), t);
                            return ScriptValue.NULL;
                        }
                    });
                    b.val(sd.name(), v);
                }
                case Statement.ExprStatement es -> {
                    ScriptContext snap = b.peek();
                    try { es.formula().evaluate(snap); }
                    catch (Throwable t) { logStatementFailure("expr", es.formula().toString(), t); }
                }
                case Statement.IfChain chain -> {
                    // peek() — every clause condition in this chain is evaluated against the SAME
                    // snapshot before any mutation happens (the taken clause's body only runs AFTER
                    // the loop below breaks), so nothing here survives past this statement either.
                    ScriptContext snap = b.peek();
                    for (Clause clause : chain.clauses()) {
                        boolean taken;
                        if (clause.isElse()) {
                            taken = true;
                        } else {
                            try { taken = clause.condition().evaluateBool(snap); }
                            catch (Throwable t) {
                                logStatementFailure("if-condition", clause.condition().toString(), t);
                                taken = false;
                            }
                        }
                        if (taken) { runStatements(clause.body(), b); break; }
                    }
                }
                case Statement.ForStatement fs -> {
                    ScriptContext snap = b.peek();
                    // Array evaluation genuinely wants to swallow errors (a bad iterExpr just
                    // skips the loop) — but that catch must NOT also wrap the loop body, or a
                    // `return` inside the loop throws ReturnSignal straight into this same
                    // catch(Throwable) and gets silently discarded instead of exiting the
                    // function (the loop looks like it "ran to the end" even after returning).
                    //
                    // Two loop vars = Polyloft-style destructuring: `for key, value in map` walks
                    // entries, binding both; `for x in map` (one var) walks its KEYS, matching the
                    // common for-in-dict convention. One var over anything else walks elements as
                    // before (a Map is otherwise opaque to plain iteration).
                    List<String> vars = fs.vars();
                    List<ScriptValue[]> rows = resolveForRows(fs.iterExpr(), snap, vars.size());
                    if (rows != null) {
                        outer:
                        for (ScriptValue[] row : rows) {
                            for (int i = 0; i < vars.size(); i++)
                                b.val(vars.get(i), i < row.length ? row[i] : ScriptValue.NULL);
                            if (fs.guardExpr() != null) {
                                boolean pass;
                                try { pass = ScriptFormula.compile(fs.guardExpr()).evaluateBool(b.peek()); }
                                catch (Throwable ignored) { pass = false; }
                                if (!pass) continue;
                            }
                            try { runStatements(fs.body(), b); }
                            catch (BreakSignal ignored) { break outer; }
                            catch (ContinueSignal ignored) { /* next iteration */ }
                        }
                    }
                }
                case Statement.WhileStatement ws -> {
                    int iters = 0;
                    outer:
                    while (iters++ < ws.maxIter()) {
                        ScriptContext snap = b.peek();
                        boolean cond;
                        try { cond = ScriptFormula.compile(ws.condExpr()).evaluateBool(snap); }
                        catch (Throwable ignored) { break; }
                        if (!cond) break;
                        try { runStatements(ws.body(), b); }
                        catch (BreakSignal ignored) { break outer; }
                        catch (ContinueSignal ignored) { /* next iteration */ }
                    }
                }
                case Statement.BreakStatement ignored -> throw new BreakSignal();
                case Statement.ContinueStatement ignored -> throw new ContinueSignal();
                case Statement.ReturnStatement rs -> {
                    ScriptValue val = ScriptValue.NULL;
                    if (!rs.expr().isEmpty()) {
                        try { val = ScriptFormula.compile(rs.expr()).evaluate(b.peek()); }
                        catch (Throwable ignored) {}
                    }
                    throw new ReturnSignal(val);
                }
                case Statement.FunctionDef fd -> {
                    // Capture body and params — build a UserFunction and store as var. definingCtx
                    // is a snapshot of this file's OWN scope right up to this point (every sibling
                    // def/const/import declared earlier in the same file) — see UserFunction.call's
                    // javadoc for why this function needs it, not just the eventual caller's scope.
                    List<Statement> capturedBody = fd.body();
                    List<String> capturedParams = fd.params();
                    ScriptContext definingCtx = b.build();
                    java.lang.reflect.Method compiledMethod = compiledMethodFor(fd.name());
                    UserFunction fn = new UserFunction(fd.name(), capturedParams, definingCtx,
                        compiledMethod != null
                            ? (callerCtx, resultB) -> {
                                try {
                                    ScriptValue result = (ScriptValue) compiledMethod.invoke(null, resultB);
                                    resultB.val("__return__", result);
                                } catch (Throwable t) {
                                    // Deliberately NOT re-run via the interpreter here: resultB may
                                    // already be partially mutated by whatever the compiled method
                                    // did before throwing, and re-running the WHOLE body on top of
                                    // that risks double-applying a real side effect (a Machine.*
                                    // call, a report_su, ...). Same fail-soft-to-NULL convention as
                                    // every other JIT fallback in this file/ScriptFormula — logged so
                                    // it's visible, never silently wrong or a crashed tick.
                                    logCompiledDefFailure(fd.name(), t);
                                    resultB.val("__return__", ScriptValue.NULL);
                                }
                            }
                            : (callerCtx, resultB) -> {
                                try { runStatements(capturedBody, resultB); }
                                catch (ReturnSignal rs) { resultB.val("__return__", rs.value); }
                            });
                    b.val(fd.name(), ScriptValue.ofObj(UserFunction.TYPE, fn));
                }
                case Statement.Import imp -> applyImport(b, imp.path(), imp.alias(), imp.symbols());
            }
        }
    }

    /**
     * Performs an {@code import}'s runtime effect: load the referenced program, evaluate it, and
     * bind either the named symbols or a namespace into {@code b}.
     *
     * <p>Extracted from the interpreter's own {@code Import} case (which now just calls this) so
     * {@link ScriptClassCompiler} can emit a call to it. Before that, a top-level {@code import}
     * disqualified a whole file's top-level body from compiling — and since that body is the
     * per-tick hot path for a machine script, an import anywhere in the file meant the entire tick
     * path stayed interpreted. Nothing about the effect itself needs the interpreter; it only ever
     * touched the builder.
     *
     * <p>Public and static because generated bytecode calls it by name from another package/loader.
     */
    public static void applyImport(ScriptContext.Builder b, String path, String alias, List<String> symbols) {
        ScriptProgram prog = ScriptRegistry.getOrLoadByPath(path);
        if (prog == null) return;
        try {
            // Evaluated against the common bootstrap context, NOT the importing script's own live
            // vars — an imported utility file is meant to be a self-contained library, not one that
            // silently inherits whatever the importer happened to have in scope at the point of
            // import.
            ScriptContext importedCtx = prog.evaluate(ScriptBootstrap.commonContext());
            if (symbols != null && !symbols.isEmpty()) {
                for (String sym : symbols) b.val(sym, importedCtx.getVar(sym));
            } else {
                String nsName = alias != null ? alias : defaultNamespaceName(path);
                b.typed(nsName, new ScriptNamespace(importedCtx));
            }
        } catch (Throwable ignored) {}
    }

    /** Default namespace name for a bare {@code import "path"} (no {@code as alias}) — the last
     *  path segment, minus its ".pf" extension: "utils/math(.pf)" → "math". */
    private static String defaultNamespaceName(String path) {
        String p = path.replace('\\', '/');
        int slash = p.lastIndexOf('/');
        String base = slash >= 0 ? p.substring(slash + 1) : p;
        return base.endsWith(".pf") ? base.substring(0, base.length() - 3) : base;
    }

    // =========================================================================
    // Tokenizer
    // =========================================================================

    private static final class Tokenizer {
        enum TT { IDENT, ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN,
                  PERCENT_ASSIGN, PLUSPLUS, MINUSMINUS, COLON, LBRACE, RBRACE, EOL, EOF }

        record Token(TT type, String text, int pos) {}

        private final String src;
        private int pos;

        Tokenizer(String src) { this.src = src; this.pos = 0; }

        private boolean atEnd() { return pos >= src.length(); }
        private char ch()       { return src.charAt(pos); }
        private void skipHSpace() { while (!atEnd() && (ch() == ' ' || ch() == '\t')) pos++; }

        Token next() {
            while (true) {
                skipHSpace();
                if (atEnd()) return new Token(TT.EOF, "", pos);
                char c = ch();
                if (c == '\n' || c == '\r') { while (!atEnd() && (ch() == '\n' || ch() == '\r')) pos++; return new Token(TT.EOL, "\n", pos); }
                if (c == '#') { while (!atEnd() && ch() != '\n') pos++; return new Token(TT.EOL, "#", pos); }
                if (c == ';') { pos++; return new Token(TT.EOL, ";", pos - 1); }
                if (c == '{') { pos++; return new Token(TT.LBRACE, "{", pos - 1); }
                if (c == '}') { pos++; return new Token(TT.RBRACE, "}", pos - 1); }
                if (c == ':') { pos++; return new Token(TT.COLON, ":", pos - 1); }
                if (c == '+') {
                    if (pos+1 < src.length()) {
                        if (src.charAt(pos+1) == '+') { pos+=2; return new Token(TT.PLUSPLUS, "++", pos-2); }
                        if (src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.PLUS_ASSIGN, "+=", pos-2); }
                    }
                    pos++; continue;
                }
                if (c == '-') {
                    if (pos+1 < src.length()) {
                        if (src.charAt(pos+1) == '-') { pos+=2; return new Token(TT.MINUSMINUS, "--", pos-2); }
                        if (src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.MINUS_ASSIGN, "-=", pos-2); }
                    }
                    pos++; continue;
                }
                if (c == '*') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.STAR_ASSIGN, "*=", pos-2); } pos++; continue; }
                if (c == '/') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.SLASH_ASSIGN, "/=", pos-2); } pos++; continue; }
                if (c == '%') { if (pos+1 < src.length() && src.charAt(pos+1) == '=') { pos+=2; return new Token(TT.PERCENT_ASSIGN, "%=", pos-2); } pos++; continue; }
                if (c == '=') { pos++; return new Token(TT.ASSIGN, "=", pos-1); }
                if (Character.isLetter(c) || c == '_') {
                    int start = pos;
                    while (!atEnd() && (Character.isLetterOrDigit(ch()) || ch() == '_')) pos++;
                    return new Token(TT.IDENT, src.substring(start, pos), start);
                }
                pos++;
            }
        }

        int mark()           { return pos; }
        void reset(int mark) { pos = mark; }

        /** Find the position of char c at or before startPos (scanning backwards). Returns -1 if not found. */
        int findCharBefore(char c, int startPos) {
            for (int i = startPos; i >= 0; i--) {
                if (i < src.length() && src.charAt(i) == c) return i;
            }
            return -1;
        }

        String readExprToEOL()   { return readExprUntil(false); }
        String readExprToBrace() { return readExprUntil(true); }

        private String readExprUntil(boolean stopAtBrace) {
            StringBuilder sb = new StringBuilder();
            int depth = 0;
            int bracketDepth = 0; // track [] so array literals don't get cut
            while (!atEnd()) {
                char c = ch();
                if (c == '"' || c == '\'') {
                    char q = c; sb.append(c); pos++;
                    while (!atEnd() && ch() != q) {
                        if (ch() == '\\' && pos+1 < src.length()) { sb.append(ch()); pos++; }
                        sb.append(ch()); pos++;
                    }
                    if (!atEnd()) { sb.append(ch()); pos++; }
                    continue;
                }
                if (c == '#') break;
                // Only stop at newline/semicolon when outside all brackets (allow multiline expressions inside parens/brackets)
                if ((c == '\n' || c == '\r' || c == ';') && depth == 0 && bracketDepth == 0) break;
                if (c == '\n' || c == '\r') { pos++; continue; } // inside parens: skip newline, don't append
                if (c == '(') { depth++; sb.append(c); pos++; continue; }
                if (c == ')') { depth--; sb.append(c); pos++; continue; }
                if (c == '[') { bracketDepth++; sb.append(c); pos++; continue; }
                if (c == ']') { bracketDepth--; sb.append(c); pos++; continue; }
                if (c == '{') { if (stopAtBrace && depth == 0 && bracketDepth == 0) break; sb.append(c); pos++; continue; }
                if (c == ':' && stopAtBrace && depth == 0 && bracketDepth == 0) { pos++; break; }
                if (c == '}') { if (depth == 0 && bracketDepth == 0) break; sb.append(c); pos++; continue; }
                sb.append(c); pos++;
            }
            return sb.toString().trim();
        }
    }

    // =========================================================================
    // Parser
    // =========================================================================

    private static final class Parser {
        private final String scriptName;
        private final Tokenizer tok;
        private final Logger log;
        private Tokenizer.Token lookahead;

        Parser(String scriptName, Tokenizer tok, Logger log) {
            this.scriptName = scriptName;
            this.tok = tok;
            this.log = log;
            this.lookahead = tok.next();
        }

        private Tokenizer.Token peek() { return lookahead; }
        private Tokenizer.Token consume() { Tokenizer.Token t = lookahead; lookahead = tok.next(); return t; }
        private void skipEOLs() { while (peek().type() == Tokenizer.TT.EOL) consume(); }

        /** Bare keywords that close a colon-style block body — checked only when
         *  {@code insideBlock}, so they stay ordinary identifiers at the top level, and ONLY
         *  passed for bodies actually opened with ':' — a brace-style body never treats "end"/
         *  "else" as special, so an existing script using either as a plain variable name (however
         *  unlikely) keeps working unchanged. */
        private static final Set<String> NONE = Set.of();
        private static final Set<String> END_ONLY = Set.of("end");
        private static final Set<String> END_OR_ELSE = Set.of("end", "else");

        List<Statement> parseBlock(boolean insideBlock) { return parseBlock(insideBlock, NONE); }

        List<Statement> parseBlock(boolean insideBlock, Set<String> stopWords) {
            List<Statement> out = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.EOF) break;
                if (t.type() == Tokenizer.TT.RBRACE) { if (insideBlock) break; consume(); continue; }
                // Colon-style closer (Polyloft "end", and "else" for if-chains) — left UN-consumed,
                // same as RBRACE above, so the caller (parseForLoop/parseIfChain/...) decides what
                // to do with it (plain consume, or branch into an else-clause).
                if (insideBlock && t.type() == Tokenizer.TT.IDENT && stopWords.contains(t.text())) break;
                Statement s = parseOneStatement();
                if (s != null) out.add(s);
            }
            return out;
        }

        /**
         * Parses exactly ONE statement at the current position and returns it (or null for a
         * malformed/empty one — matches each case's original behavior). Shared by
         * {@link #parseBlock}'s loop and inline colon bodies ({@code if x: return 1}, a single
         * statement with no {@code end} — see {@link #parseInlineOrBlockBody}), where reusing
         * this instead of duplicating the whole dispatch keeps the two forms from drifting apart.
         */
        private Statement parseOneStatement() {
            skipEOLs();
            Tokenizer.Token t = peek();
            if (t.type() != Tokenizer.TT.IDENT) {
                if (t.type() != Tokenizer.TT.EOF && t.type() != Tokenizer.TT.RBRACE) consume();
                return null;
            }
            switch (t.text()) {
                case "if"       -> { return parseIfChain(); }
                case "for"      -> { return parseForLoop(); }
                case "while"    -> { return parseWhileLoop(); }
                case "break"    -> { consume(); return new Statement.BreakStatement(); }
                case "continue" -> { consume(); return new Statement.ContinueStatement(); }
                case "return" -> {
                    int afterReturn = t.pos() + t.text().length();
                    consume();
                    tok.reset(afterReturn);
                    String retExpr = tok.readExprToEOL();
                    lookahead = tok.next();
                    return new Statement.ReturnStatement(retExpr);
                }
                case "def" -> { return parseFunctionDef(); }
                case "import" -> { return parseImport(); }
                case "static" -> { return parseStaticDecl(false); }
                case "final"  -> { return parseStaticDecl(true); }
            }

            String name = t.text();
            consume();
            Tokenizer.Token op = peek();

            if (op.type() == Tokenizer.TT.PLUSPLUS)     { consume(); Statement s = compile(name, name + " + 1"); skipLine(); return s; }
            if (op.type() == Tokenizer.TT.MINUSMINUS)   { consume(); Statement s = compile(name, name + " - 1"); skipLine(); return s; }
            if (op.type() == Tokenizer.TT.PLUS_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " + (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.MINUS_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " - (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.STAR_ASSIGN)  { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " * (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.SLASH_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " / (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }
            if (op.type() == Tokenizer.TT.PERCENT_ASSIGN) { int ps = op.pos()+2; consume(); tok.reset(ps); Statement s = compile(name, name + " % (" + tok.readExprToEOL() + ")"); lookahead = tok.next(); return s; }

            if (op.type() != Tokenizer.TT.ASSIGN) {
                // No '=' — treat as expression statement (e.g. Machine.set_typed(...), entity.remove())
                tok.reset(t.pos()); // reset to start of the IDENT name
                String callExpr = tok.readExprToEOL();
                lookahead = tok.next();
                if (callExpr.isEmpty()) return null;
                try { return new Statement.ExprStatement(ScriptFormula.compile(callExpr)); }
                catch (Throwable ignored) { return null; }
            }
            int exprStart = op.pos() + 1; // right after '='
            consume(); // consume "="
            tok.reset(exprStart); // reset to right after '=' — includes '(', '.', etc.
            String expr = tok.readExprToEOL();
            Statement s = !expr.isEmpty() ? compile(name, expr) : null;
            lookahead = tok.next();
            return s;
        }

        private void skipLine() { while (peek().type() != Tokenizer.TT.EOL && peek().type() != Tokenizer.TT.EOF) consume(); }

        /** Consumes whichever closer is actually present after a colon/brace body — {@code }}
         *  for brace-style, bare {@code end} for Polyloft colon-style. Never used where "else"
         *  must stay un-consumed (if-chains handle that themselves). */
        private void consumeGenericCloser() {
            skipEOLs();
            if (peek().type() == Tokenizer.TT.RBRACE) consume();
            else if (peek().type() == Tokenizer.TT.IDENT && peek().text().equals("end")) consume();
        }

        /** result of {@link #parseOpenedBody}: the parsed body, whether it opened with '{'
         *  (brace-style), and whether the caller still needs to consume a closer — an inline
         *  colon body (single statement, no "end") has nothing left to close. */
        private record OpenedBody(List<Statement> body, boolean brace, boolean needsCloser) {}

        /**
         * Parses whichever body form follows a header whose ':' or '{' has already been read
         * (or, for a bare "else"/"else if" with no header expression at all, is still sitting
         * un-consumed as a raw COLON token — handled here too):
         *
         * <ul>
         *   <li>{@code { ... }} — brace-style, needs its '}' consumed by the caller.</li>
         *   <li>{@code : <stmt>} on the SAME line — Polyloft-adjacent INLINE form, a single
         *       statement with no trailing "end" at all ({@code if x: return 1}). Nothing to
         *       close afterward.</li>
         *   <li>{@code :\n ... \nend} — Polyloft multi-line colon body, needs its "end" (or a
         *       chained "else"/"else if", handled by the if-chain caller) consumed.</li>
         * </ul>
         */
        private OpenedBody parseOpenedBody(Set<String> multilineStopWords) {
            if (peek().type() == Tokenizer.TT.LBRACE) {
                consume();
                return new OpenedBody(parseBlock(true, NONE), true, true);
            }
            // A bare "else:"/"end:" with no expression before it never runs its colon through
            // readExprToBrace (nothing to read as a header) — it's still sitting in the token
            // stream here as a raw COLON, so consume it explicitly instead of leaving it dangling.
            if (peek().type() == Tokenizer.TT.COLON) consume();
            if (peek().type() != Tokenizer.TT.EOL && peek().type() != Tokenizer.TT.EOF) {
                // Content right on the same physical line as the colon, no intervening EOL —
                // Polyloft never does this (always multi-line + "end"), but it's an unambiguous,
                // useful extra: a single-statement guard clause needs no block at all.
                Statement one = parseOneStatement();
                return new OpenedBody(one != null ? List.of(one) : List.of(), false, false);
            }
            skipEOLs();
            if (peek().type() == Tokenizer.TT.LBRACE) { // Allman-style brace on its own line
                consume();
                return new OpenedBody(parseBlock(true, NONE), true, true);
            }
            return new OpenedBody(parseBlock(true, multilineStopWords), false, true);
        }

        /** Finds a top-level (not inside quotes/parens/brackets) " where " in a for-header's
         *  post-"in" remainder, splitting the iterable from Polyloft's optional guard clause:
         *  {@code for i in range(0, 6) where i < 5}. -1 if absent. */
        private static int indexOfTopLevelWhere(String s) {
            int depth = 0;
            boolean inStr = false;
            char strCh = 0;
            for (int i = 0; i + 7 <= s.length(); i++) {
                char c = s.charAt(i);
                if (inStr) {
                    if (c == '\\') { i++; continue; }
                    if (c == strCh) inStr = false;
                    continue;
                }
                if (c == '"' || c == '\'') { inStr = true; strCh = c; continue; }
                if (c == '(' || c == '[') { depth++; continue; }
                if (c == ')' || c == ']') { depth--; continue; }
                if (depth == 0 && s.regionMatches(i, " where ", 0, 7)) return i;
            }
            return -1;
        }

        /**
         * Parses either style:
         *   for x in arr { ... }                          (brace)
         *   for x in arr:\n ... \nend                      (Polyloft colon/end)
         *   for k, v in map:\n ... \nend                    (Polyloft destructuring)
         *   for i in range(0,6) where i < 5:\n ... \nend    (Polyloft guard clause)
         */
        Statement parseForLoop() {
            consume(); // "for"
            skipEOLs();
            tok.reset(lookahead.pos());
            String forHeader = tok.readExprToBrace(); // stops at '{' or a top-level ':'
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            int inIdx = forHeader.indexOf(" in ");
            if (inIdx < 0) { log.warning("[CEPolyfills] 'for' missing 'in' in " + scriptName + ".pf"); return null; }
            String varsPart = forHeader.substring(0, inIdx).trim();
            String rest = forHeader.substring(inIdx + 4).trim();
            String guardExpr = null;
            int whereIdx = indexOfTopLevelWhere(rest);
            if (whereIdx >= 0) {
                guardExpr = rest.substring(whereIdx + 7).trim();
                rest = rest.substring(0, whereIdx).trim();
            }
            List<String> vars = new ArrayList<>();
            for (String v : varsPart.split(",")) {
                String vn = v.trim();
                if (!vn.isEmpty()) vars.add(vn);
            }
            if (vars.isEmpty() || rest.isEmpty()) { log.warning("[CEPolyfills] Bad 'for' header in " + scriptName + ".pf"); return null; }
            return new Statement.ForStatement(vars, rest, guardExpr, body);
        }

        Statement parseWhileLoop() {
            consume(); // "while"
            skipEOLs();
            tok.reset(lookahead.pos());
            String condExpr = tok.readExprToBrace();
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            if (condExpr.isEmpty()) { log.warning("[CEPolyfills] Empty 'while' condition in " + scriptName + ".pf"); return null; }
            return new Statement.WhileStatement(condExpr, body, 1000);
        }

        /**
         * Parses if/else-if/else in either style. Brace-style chains one clause per
         * {@code } else { }/{ } else if ... { }} pair, exactly as before. Colon-style has ONE
         * trailing {@code end} for the whole chain (Polyloft): {@code if c: ... else: ... end}.
         */
        Statement.IfChain parseIfChain() {
            List<Clause> clauses = new ArrayList<>();
            while (true) {
                skipEOLs();
                Tokenizer.Token t = peek();
                if (t.type() == Tokenizer.TT.IDENT && t.text().equals("if")) {
                    // Handles both the opening "if" AND an "else if" reached via loop-back below —
                    // deliberately does NOT consume its own closer (RBRACE or bare "end"/"else"):
                    // looping back to the top lets the RBRACE/"end"/"else" arms below decide what
                    // comes next, exactly how the original brace-only design chained clauses.
                    int afterIf = t.pos() + t.text().length();
                    consume();
                    tok.reset(afterIf);
                    String condExpr = tok.readExprToBrace();
                    lookahead = tok.next();
                    clauses.add(new Clause(compileFormula(condExpr), parseOpenedBody(END_OR_ELSE).body()));
                } else if (t.type() == Tokenizer.TT.RBRACE) {
                    // Brace-style clause just closed — consume it and look for a chained else.
                    consume();
                    skipEOLs();
                    Tokenizer.Token maybeElse = peek();
                    if (maybeElse.type() == Tokenizer.TT.IDENT && maybeElse.text().equals("else")) {
                        consume();
                        skipEOLs();
                        Tokenizer.Token afterElse = peek();
                        if (afterElse.type() == Tokenizer.TT.IDENT && afterElse.text().equals("if")) {
                            int afterElseIf = afterElse.pos() + afterElse.text().length();
                            consume();
                            tok.reset(afterElseIf);
                            String elseIfCond = tok.readExprToBrace();
                            lookahead = tok.next();
                            clauses.add(new Clause(compileFormula(elseIfCond), parseOpenedBody(END_OR_ELSE).body()));
                            // loop back — its closer is handled by the RBRACE/end arms next time around
                        } else {
                            clauses.add(new Clause(null, parseOpenedBody(END_OR_ELSE).body()));
                            // a plain else is always terminal — loop back just to consume its own
                            // closer (RBRACE or "end"), then the next iteration's "no else follows
                            // a plain else" check naturally ends the chain.
                        }
                    } else { break; }
                } else if (t.type() == Tokenizer.TT.IDENT && t.text().equals("end")) {
                    consume(); break; // colon-style chain fully closed, no trailing else
                } else if (t.type() == Tokenizer.TT.IDENT && t.text().equals("else")) {
                    // Colon-style continuation, reached via loop-back after a colon-style clause
                    // stopped at a bare "else" instead of "end" — same else/else-if shape as the
                    // RBRACE arm above, just with no brace to consume first.
                    consume();
                    skipEOLs();
                    Tokenizer.Token afterElse = peek();
                    if (afterElse.type() == Tokenizer.TT.IDENT && afterElse.text().equals("if")) {
                        int afterElseIf = afterElse.pos() + afterElse.text().length();
                        consume();
                        tok.reset(afterElseIf);
                        String elseIfCond = tok.readExprToBrace();
                        lookahead = tok.next();
                        clauses.add(new Clause(compileFormula(elseIfCond), parseOpenedBody(END_OR_ELSE).body()));
                    } else {
                        clauses.add(new Clause(null, parseOpenedBody(END_OR_ELSE).body()));
                    }
                } else { break; }
            }
            return new Statement.IfChain(clauses);
        }

        /**
         * Parses: def name(param1, param2, ...) { body }   or   def name(...): body end
         * Called after consuming the "def" token.
         */
        Statement parseFunctionDef() {
            consume(); // consume "def"
            skipEOLs();
            Tokenizer.Token nameTok = peek();
            if (nameTok.type() != Tokenizer.TT.IDENT) {
                log.warning("[CEPolyfills] 'def' missing function name in " + scriptName + ".pf");
                skipLine(); return null;
            }
            String funcName = nameTok.text();
            consume(); // consume name
            // Read param list: "(a, b, c)"
            // tok.next() skips '(' since it's not a tokenizer token, so lookahead.pos()
            // lands AFTER '('. We must reset to the '(' itself so that readExprToBrace
            // sees depth=0→1 (from '(') and correctly stops at the matching '{' or ':'.
            int openParen = tok.findCharBefore('(', lookahead.pos() - 1);
            tok.reset(openParen >= 0 ? openParen : lookahead.pos());
            String rawParams = tok.readExprToBrace(); // reads "(params)" up to '{' or ':'
            lookahead = tok.next();
            OpenedBody opened = parseOpenedBody(END_ONLY);
            if (opened.needsCloser()) consumeGenericCloser();
            List<Statement> body = opened.body();
            // Parse param names from rawParams: "(a, b, c)" → ["a","b","c"]
            List<String> params = new ArrayList<>();
            String paramStr = rawParams.trim();
            if (paramStr.startsWith("(")) paramStr = paramStr.substring(1);
            if (paramStr.endsWith(")")) paramStr = paramStr.substring(0, paramStr.length() - 1);
            for (String p : paramStr.split(",")) {
                String pn = p.trim();
                if (!pn.isEmpty()) params.add(pn);
            }
            return new Statement.FunctionDef(funcName, params, body);
        }

        /** Parses {@code static NAME = expr} / {@code final NAME = expr} — same {@code NAME =
         *  expr} shape a plain assignment uses, just with the leading keyword already consumed
         *  and tagged onto the resulting {@link Statement.StaticDecl}. */
        private Statement parseStaticDecl(boolean isFinal) {
            consume(); // "static" / "final"
            skipEOLs();
            Tokenizer.Token nameTok = peek();
            if (nameTok.type() != Tokenizer.TT.IDENT) {
                log.warning("[CEPolyfills] '" + (isFinal ? "final" : "static") + "' missing a name in " + scriptName + ".pf");
                skipLine();
                return null;
            }
            String declName = nameTok.text();
            consume();
            Tokenizer.Token op = peek();
            if (op.type() != Tokenizer.TT.ASSIGN) {
                log.warning("[CEPolyfills] '" + (isFinal ? "final" : "static") + " " + declName
                        + "' must be initialized with '=' in " + scriptName + ".pf");
                skipLine();
                return null;
            }
            int exprStart = op.pos() + 1;
            consume(); // "="
            tok.reset(exprStart);
            String expr = tok.readExprToEOL();
            lookahead = tok.next();
            if (expr.isEmpty()) return null;
            try { return new Statement.StaticDecl(declName, ScriptFormula.compile(expr), isFinal); }
            catch (IllegalArgumentException ex) {
                log.warning("[CEPolyfills] Bad expr for '" + declName + "' in " + scriptName + ".pf: " + ex.getMessage());
                return null;
            }
        }

        /**
         * Parses: {@code import "path"}, {@code import "path" as alias}, or
         * {@code import "path" { name1, name2 }}. Not a ScriptFormula expression (the trailing
         * "as alias" / "{ names }" forms have no formula equivalent), so the raw remainder of the
         * line is read manually — same pattern "return"'s header uses — and hand-parsed below.
         */
        private Statement parseImport() {
            Tokenizer.Token t = peek(); // "import", not yet consumed
            int afterImport = t.pos() + t.text().length();
            consume();
            tok.reset(afterImport);
            String raw = tok.readExprToEOL();
            lookahead = tok.next();
            return parseImportHeader(raw.trim());
        }

        private Statement parseImportHeader(String s) {
            if (!s.startsWith("\"") && !s.startsWith("'")) {
                log.warning("[CEPolyfills] 'import' expects a quoted path in " + scriptName + ".pf: " + s);
                return null;
            }
            char quote = s.charAt(0);
            int close = s.indexOf(quote, 1);
            if (close < 0) {
                log.warning("[CEPolyfills] 'import' has an unterminated path string in " + scriptName + ".pf");
                return null;
            }
            String path = s.substring(1, close);
            String rest = s.substring(close + 1).trim();
            if (rest.isEmpty()) return new Statement.Import(path, null, List.of());
            if (rest.startsWith("as ") || rest.startsWith("as\t")) {
                String alias = rest.substring(3).trim();
                return new Statement.Import(path, alias.isEmpty() ? null : alias, List.of());
            }
            if (rest.startsWith("{")) {
                // readExprToEOL (see its javadoc) already stops AT the closing '}' without
                // consuming/including it when it isn't looking for one, so `rest` here is just
                // "{ name1, name2" with no trailing brace — strip a leading '{' and an optional
                // trailing '}' defensively either way.
                String inner = rest.substring(1);
                int endBrace = inner.indexOf('}');
                if (endBrace >= 0) inner = inner.substring(0, endBrace);
                List<String> symbols = new ArrayList<>();
                for (String part : inner.split(",")) {
                    String sym = part.trim();
                    if (!sym.isEmpty()) symbols.add(sym);
                }
                return new Statement.Import(path, null, symbols);
            }
            log.warning("[CEPolyfills] Unrecognized 'import' trailer '" + rest + "' in " + scriptName + ".pf");
            return new Statement.Import(path, null, List.of());
        }

        private Statement.Assign compile(String name, String expr) {
            try { return new Statement.Assign(name, ScriptFormula.compile(expr)); }
            catch (IllegalArgumentException ex) { log.warning("[CEPolyfills] Bad expr '" + name + "' in " + scriptName + ".pf: " + ex.getMessage()); return null; }
        }

        private ScriptFormula compileFormula(String expr) {
            if (expr.isEmpty()) return ScriptFormula.compile("false");
            try { return ScriptFormula.compile(expr); }
            catch (IllegalArgumentException ex) { log.warning("[CEPolyfills] Bad condition in " + scriptName + ".pf: " + ex.getMessage()); return ScriptFormula.compile("false"); }
        }
    }
}
