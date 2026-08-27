package dev.arubik.craftengine.script;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * Whole-{@code def} compiler: turns each top-level {@code def} in a {@code .pf} file's statement
 * tree into a REAL method on a real generated class, instead of a lambda-closure body walked
 * statement-by-statement by {@link ScriptProgram#runStatements} on every call. Unlike {@link
 * ScriptBytecodeCompiler} (which JITs individual pure-numeric/boolean EXPRESSIONS and is invoked
 * from {@link ScriptFormula#compile}), this compiles STATEMENT SEQUENCES — assignments, if-chains,
 * loops, returns — reusing {@link ScriptFormula#compile} for every embedded expression string
 * rather than re-implementing expression codegen, and {@link ScriptProgram#resolveForRows} for a
 * {@code for}-loop's iterable resolution (Map destructuring, guard clauses — the exact semantics
 * {@code runStatements}' own {@code ForStatement} case used to inline directly, now shared so the
 * two implementations can't drift apart). That split matters: an inline expression is already
 * cheap once {@code ScriptFormula}'s own string cache has it (a single hashmap lookup), so JITting
 * every tiny sub-expression into its own class was mostly wasted class-loading overhead (see
 * {@code ScriptFormula.JIT_ENABLED}'s doc and the literal-bailout in {@code
 * ScriptBytecodeCompiler}); the real cost this class targets is the STATEMENT DISPATCH itself — a
 * {@code switch} per statement plus exception-based {@code return}/{@code break}/{@code continue}
 * signaling — which becomes plain JVM branches, a real {@code GOTO} for break/continue, and a real
 * {@code ARETURN} for return, once compiled.
 *
 * <p><b>Naming.</b> The generated class lives in a package that mirrors the {@code .pf} file's
 * folder under {@code scripts/} (e.g. {@code kinetics/generators/windmill.pf} → package {@code
 * dev.arubik.craftengine.script.gen.kinetics.generators}), named in PascalCase from the file's own
 * name ({@code windmill} → {@code Windmill}). Each compiled {@code def} becomes a method named in
 * camelCase from the def's own name ({@code _sail_count} → {@code _sailCount} — a leading
 * underscore, the convention this codebase uses for a "private" helper, is kept as-is since it's
 * still a legal Java identifier prefix). {@code __init__}/{@code __unload__} (the {@code
 * ScriptRegistry} lifecycle hooks — see its own doc) are deliberately NEVER compiled: they run
 * once per load/reload, never on a hot path, so there's nothing to gain and no reason to widen this
 * compiler's surface for them.
 *
 * <p><b>Scope — per-DEF bail, not whole-file.</b> A real file commonly mixes a def this compiler
 * can fully represent right next to one it can't (windmill.pf's own {@code _sail_count} scans
 * {@code contraption.blocks()} with a {@code for}; its five sibling defs are all straight-line) —
 * {@link #tryCompile} checks each def's body independently ({@link #isSupported}, a pure dry run
 * that touches no bytecode) and only emits the ones that fully qualify; a def that doesn't simply
 * isn't in the generated class at all, and whatever wires this compiler in falls back to
 * interpreting THAT function only, unaffected by however many of its siblings did compile. A def
 * containing a nested {@code def}, an {@code import}, or a {@code break}/{@code continue} with no
 * enclosing loop is out of scope the same way.
 *
 * <p><b>Variable storage stays {@link ScriptContext.Builder}</b> — deliberately NOT reallocated
 * into real JVM locals. Every compiled method takes the same {@code ScriptContext.Builder} the
 * interpreter already threads through {@code runStatements}, and every embedded expression is
 * still evaluated via the real {@link ScriptFormula#compile}/{@code evaluate}/{@code
 * evaluateBool} — this compiler only replaces the STATEMENT-SEQUENCING code around those calls
 * with real bytecode, inheriting variable-lookup/closure correctness directly from that
 * already-tested contract instead of re-deriving it.
 */
final class ScriptClassCompiler {

    private ScriptClassCompiler() {}

    /** Kill switch for wiring this compiler's output into LIVE execution — same convention as
     *  {@code ScriptFormula.JIT_ENABLED}. {@link ScriptProgram} checks this flag on EVERY call (in
     *  {@code compiledMethodFor}/{@code compiledMainMethod}, not just once at compile time), so
     *  flipping it to {@code false} instantly reverts every already-compiled file back to the
     *  interpreter too, no restart or reload needed — {@code ensureCompiled}'s own memoized
     *  compile result is left alone (compiling is still cheap to skip re-doing), only whether it's
     *  ever CONSULTED is gated. Defaults {@code true}: this compiler's output has been validated
     *  against the interpreter across every real def in the shipped {@code .pf} scripts (see
     *  {@code ScriptClassCompilerCoverageTest}) with zero discrepancies, the same bar {@code
     *  ScriptBytecodeCompiler}'s own {@code JIT_ENABLED} was held to before shipping enabled. */
    static volatile boolean CLASS_JIT_ENABLED = true;

    private static final String BUILDER = "dev/arubik/craftengine/script/ScriptContext$Builder";
    private static final String CTX = "dev/arubik/craftengine/script/ScriptContext";
    private static final String FORMULA = "dev/arubik/craftengine/script/ScriptFormula";
    private static final String VALUE = "dev/arubik/craftengine/script/ScriptValue";
    private static final String PROGRAM = "dev/arubik/craftengine/script/ScriptProgram";
    private static final String LIST = "java/util/List";
    private static final String ITERATOR = "java/util/Iterator";

    /** Loads every class this compiler ever generates. One loader for the whole plugin lifetime —
     *  these classes are permanent (one per distinct .pf file that qualifies), same lifetime
     *  argument {@code ScriptFormula.CACHE}'s own doc already makes for individually-cached
     *  formulas. A plain {@code ClassLoader} (not a hidden class) specifically because hidden
     *  classes are forced into the SAME package as their defining lookup, which would make the
     *  per-folder package naming this class exists to provide impossible. */
    private static final class GenLoader extends ClassLoader {
        GenLoader() { super(ScriptClassCompiler.class.getClassLoader()); }
        Class<?> define(String name, byte[] bytes) { return defineClass(name, bytes, 0, bytes.length); }
    }
    private static final GenLoader LOADER = new GenLoader();

    /** {@code classBytes} is the exact raw bytecode {@link #tryCompile} generated and handed to
     *  the {@link GenLoader} — kept here (not just discarded after {@code define}) specifically so
     *  a test can disassemble it directly; a dynamically-defined class has no {@code .class}
     *  resource discoverable via {@code getResourceAsStream}/{@code ClassLoader.getSystemResource},
     *  so re-reading it back through the class object alone isn't possible. */
    /** {@code mainMethod} is {@code null} when the file's top-level "run" body didn't qualify
     *  (a top-level {@code import}/{@code static}/{@code final}, or an unsupported construct
     *  somewhere in it) — a file can still have compiled {@code def}s with no compiled main body,
     *  or vice versa; the two are independent. */
    record Compiled(Class<?> generatedClass, Map<String, Method> methodsByDefName, Method mainMethod, byte[] classBytes) {}

    /** Per-method codegen state: the next free local-variable slot (0 is always the {@code
     *  ScriptContext.Builder} parameter), the stack of enclosing loops' continue/break targets
     *  (innermost last — a bare {@code break}/{@code continue} always targets {@link
     *  Deque#peekLast}), and whether this method is the file's top-level "run" body rather than a
     *  {@code def}. That last flag changes exactly one thing — {@code return} — because the
     *  interpreter itself treats the two differently: {@code UserFunction.call} propagates a
     *  {@code def}'s {@code ReturnSignal} value straight back to ITS caller, but {@code
     *  ScriptProgram.evaluate}'s OWN catch for a top-level {@code ReturnSignal} does something
     *  else entirely — it stashes the value into the resulting context's {@code __return__} var
     *  and stops (skipping any later top-level statements), never handing it back as evaluate()'s
     *  own return value. A compiled {@code def} method emits a real {@code ARETURN}; the compiled
     *  "run" method emits {@code b.val("__return__", value); return;} instead — see {@code
     *  emitBody}'s {@code ReturnStatement} case. */
    private static final class MethodCtx {
        int nextSlot = 1;
        final Deque<Loop> loops = new ArrayDeque<>();
        final boolean isMain;
        final ScriptBytecodeCompiler.LocalCallResolver resolver;
        MethodCtx(boolean isMain, ScriptBytecodeCompiler.LocalCallResolver resolver) {
            this.isMain = isMain;
            this.resolver = resolver;
        }
        int alloc() { return nextSlot++; }
    }
    private record Loop(Label continueLabel, Label breakLabel) {}

    /** Attempts to compile every eligible top-level {@code def} in {@code statements} (parsed
     *  from the file at {@code originPath}, e.g. {@code "kinetics/generators/windmill"} — no
     *  leading slash, no {@code .pf} suffix, {@code /}-separated) into one real class. Returns
     *  {@code null} if NO def in the file qualifies — see class doc for the per-def bail model. */
    static Compiled tryCompile(String originPath, List<ScriptProgram.Statement> statements) {
        try {
            List<ScriptProgram.Statement.FunctionDef> defs = new ArrayList<>();
            for (ScriptProgram.Statement s : statements) {
                if (s instanceof ScriptProgram.Statement.FunctionDef fd) {
                    if ("__init__".equals(fd.name()) || "__unload__".equals(fd.name())) continue;
                    defs.add(fd);
                }
                // Assign/ExprStatement/StaticDecl/IfChain/ReturnStatement/Import at top level are
                // fine either way; they simply aren't compiled by THIS pass (it only ever compiles
                // def bodies), so an Import elsewhere in the file doesn't disqualify a def that
                // doesn't itself need one.
            }
            // Filter to only the defs whose ENTIRE body (recursively) this compiler can represent
            // — checked as a pure dry run, no bytecode touched yet, so a def that fails this check
            // is simply left out of the class rather than corrupting an already-half-emitted
            // method (ASM's ClassWriter has no "undo a method visit" operation).
            defs.removeIf(fd -> !isSupported(fd.body(), 0));

            // A def calling ANOTHER def in this file is only safe to compile when that callee is
            // ALSO going to be compiled onto this same class — see ScriptBytecodeCompiler.P
            // #localCall for the real INVOKESTATIC dispatch this now emits. A call to a def that
            // stays INTERPRETED can't be resolved from compiled code: nothing binds an interpreted
            // sibling as a UserFunction anywhere in the isolated Builder a compiled call site
            // builds (that only happens when ScriptProgram.runStatements actually executes that
            // def's own FunctionDef statement, which compiled code never does). So the eligible set
            // is a FIXED POINT: start from every def whose own body is representable at all
            // (isSupported, above), then repeatedly drop any def that calls a name from
            // allDefNames which ISN'T (yet) in the surviving set, until nothing more drops out —
            // mutual recursion between two defs that both survive is fine (real Java recursion,
            // same as any other pair of methods calling each other).
            java.util.Set<String> allDefNames = new java.util.HashSet<>();
            for (ScriptProgram.Statement s : statements) {
                if (s instanceof ScriptProgram.Statement.FunctionDef fd) allDefNames.add(fd.name());
            }
            java.util.Set<String> compilable = new java.util.HashSet<>();
            for (ScriptProgram.Statement.FunctionDef fd : defs) compilable.add(fd.name());
            boolean changed = true;
            while (changed) {
                changed = false;
                java.util.Set<String> uncompilable = new java.util.HashSet<>(allDefNames);
                uncompilable.removeAll(compilable);
                java.util.Iterator<ScriptProgram.Statement.FunctionDef> it = defs.iterator();
                while (it.hasNext()) {
                    ScriptProgram.Statement.FunctionDef fd = it.next();
                    if (callsAnyOf(fd.body(), uncompilable)) {
                        it.remove();
                        compilable.remove(fd.name());
                        changed = true;
                    }
                }
            }

            String[] pkgAndClass = deriveName(originPath);
            String pkgPath = pkgAndClass[0];
            String simpleClassName = pkgAndClass[1];
            String internalName = pkgPath.isEmpty() ? simpleClassName : pkgPath + "/" + simpleClassName;
            String binaryName = internalName.replace('/', '.');

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cw.visit(V21, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, internalName, null, "java/lang/Object", null);

            MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            ctor.visitCode();
            ctor.visitVarInsn(ALOAD, 0);
            ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            ctor.visitInsn(RETURN);
            ctor.visitMaxs(0, 0);
            ctor.visitEnd();

            Map<String, String> methodNames = new LinkedHashMap<>(); // defName -> generated method name
            int anon = 0;
            for (ScriptProgram.Statement.FunctionDef fd : defs) {
                String mName = camelCase(fd.name());
                if (mName.isEmpty() || !Character.isJavaIdentifierStart(mName.charAt(0))) mName = "fn" + (anon++);
                methodNames.put(fd.name(), mName);
            }

            // Every def that survived the fixed point above is reachable via a direct INVOKESTATIC
            // from any other compiled def/main-body in this same class — see ScriptBytecodeCompiler
            // .LocalTarget's own doc.
            Map<String, ScriptBytecodeCompiler.LocalTarget> localTargets = new LinkedHashMap<>();
            for (ScriptProgram.Statement.FunctionDef fd : defs) {
                localTargets.put(fd.name(),
                        new ScriptBytecodeCompiler.LocalTarget(internalName, methodNames.get(fd.name()), fd.params()));
            }
            ScriptBytecodeCompiler.LocalCallResolver resolver = localTargets::get;

            for (ScriptProgram.Statement.FunctionDef fd : defs) {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, methodNames.get(fd.name()),
                        "(L" + BUILDER + ";)L" + VALUE + ";", null, null);
                mv.visitCode();
                MethodCtx mc = new MethodCtx(false, resolver);
                if (!emitBody(mv, fd.body(), mc)) return null; // isSupported/emitBody drifted — bail defensively
                // Fell off the end without an explicit return — matches the interpreter's
                // UserFunction.call, which yields NULL when the body never hits ReturnStatement.
                mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // The top-level "run" body — everything action_script actually invokes when called
            // with no ":func" (ScriptCall.execute: funcName==null -> prog.evaluate(ctx), which
            // runs the FULL top-level statement list in file order). Collected as every top-level
            // statement EXCEPT the FunctionDefs themselves (already compiled as methods above, or
            // left for the interpreter to bind as a UserFunction — either way nothing to emit here
            // for a FunctionDef marker) — this correctly includes BOTH a file's leading constants
            // (windmill.pf's SAILS_PER_RPM etc., which run unconditionally regardless of their
            // position relative to any def) and its trailing tick logic, in original order. Bails
            // (compiles NO "run" method, main body stays fully interpreted) on a top-level Import
            // or StaticDecl — StaticDecl needs the OWNING ScriptProgram instance's staticStore,
            // which a static generated method has no reference to; out of scope for now, see
            // Statement.StaticDecl's own doc for why that sharing has to go through the instance.
            List<ScriptProgram.Statement> mainBody = new ArrayList<>();
            boolean mainEligible = true;
            for (ScriptProgram.Statement s : statements) {
                if (s instanceof ScriptProgram.Statement.FunctionDef) continue;
                if (s instanceof ScriptProgram.Statement.Import || s instanceof ScriptProgram.Statement.StaticDecl) {
                    mainEligible = false;
                    break;
                }
                mainBody.add(s);
            }
            java.util.Set<String> uncompilableForMain = new java.util.HashSet<>(allDefNames);
            uncompilableForMain.removeAll(compilable);
            String mainMethodName = null;
            if (mainEligible && !mainBody.isEmpty() && isSupported(mainBody, 0)
                    && !callsAnyOf(mainBody, uncompilableForMain)) {
                mainMethodName = "run";
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, mainMethodName, "(L" + BUILDER + ";)V", null, null);
                mv.visitCode();
                MethodCtx mc = new MethodCtx(true, resolver);
                if (!emitBody(mv, mainBody, mc)) return null; // isSupported/emitBody drifted — bail defensively
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            if (defs.isEmpty() && mainMethodName == null) return null; // nothing eligible in this file

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            Class<?> generated = LOADER.define(binaryName, bytes);

            Map<String, Method> byDefName = new LinkedHashMap<>();
            for (var e : methodNames.entrySet()) {
                byDefName.put(e.getKey(), generated.getMethod(e.getValue(), ScriptContext.Builder.class));
            }
            Method mainMethod = mainMethodName != null
                    ? generated.getMethod(mainMethodName, ScriptContext.Builder.class) : null;
            return new Compiled(generated, byDefName, mainMethod, bytes);
        } catch (Throwable ignored) {
            // Anything at all — an unsupported shape that slipped past a bail check, a verifier
            // rejection, a real bug — degrades to the interpreter for the WHOLE file, never a
            // broken script.
            return null;
        }
    }

    /** {@code true} if any embedded expression string anywhere in {@code stmts} (recursively —
     *  Assign RHS, ExprStatement, ReturnStatement, if/loop conditions, a for-loop's iterable/guard)
     *  calls one of {@code defNames} as a function — {@code name(} with a word boundary before it,
     *  so {@code my_run(x)} doesn't false-positive on a def literally named {@code run}. A dry
     *  text scan, not a real parse: correctness only needs it to never MISS a real call (a false
     *  positive just excludes a def/main-body that would have been fine, not a silent wrong
     *  result), so this errs conservative on purpose. */
    private static boolean callsAnyOf(List<ScriptProgram.Statement> stmts, java.util.Set<String> defNames) {
        if (defNames.isEmpty()) return false;
        for (String expr : collectFormulaStrings(stmts)) {
            for (String name : defNames) {
                if (java.util.regex.Pattern.compile("(?<![A-Za-z0-9_.])" + java.util.regex.Pattern.quote(name) + "\\s*\\(")
                        .matcher(expr).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> collectFormulaStrings(List<ScriptProgram.Statement> stmts) {
        List<String> out = new ArrayList<>();
        for (ScriptProgram.Statement stmt : stmts) {
            switch (stmt) {
                case ScriptProgram.Statement.Assign a -> out.add(a.formula().toString());
                case ScriptProgram.Statement.ExprStatement es -> out.add(es.formula().toString());
                case ScriptProgram.Statement.ReturnStatement rs -> { if (rs.expr() != null) out.add(rs.expr()); }
                case ScriptProgram.Statement.IfChain chain -> {
                    for (ScriptProgram.Clause clause : chain.clauses()) {
                        if (clause.condition() != null) out.add(clause.condition().toString());
                        out.addAll(collectFormulaStrings(clause.body()));
                    }
                }
                case ScriptProgram.Statement.ForStatement fs -> {
                    out.add(fs.iterExpr());
                    if (fs.guardExpr() != null) out.add(fs.guardExpr());
                    out.addAll(collectFormulaStrings(fs.body()));
                }
                case ScriptProgram.Statement.WhileStatement ws -> {
                    out.add(ws.condExpr());
                    out.addAll(collectFormulaStrings(ws.body()));
                }
                default -> { /* StaticDecl/FunctionDef/Import/Break/Continue carry no expr string here */ }
            }
        }
        return out;
    }

    /** Pure dry run of {@link #emitBody}'s own eligibility switch — same statement kinds accepted/
     *  rejected, but touches no {@link MethodVisitor} at all. {@code loopDepth} tracks how many
     *  enclosing loops this position is nested inside, so a {@code break}/{@code continue} with no
     *  enclosing loop is correctly rejected (the interpreter's own equivalent — an unguarded
     *  BreakSignal/ContinueSignal propagating out of {@code runStatements} entirely — isn't a
     *  behavior worth replicating here). Keep in sync with {@link #emitBody}'s own switch by
     *  construction — same statement kinds, same recursion. */
    private static boolean isSupported(List<ScriptProgram.Statement> stmts, int loopDepth) {
        for (ScriptProgram.Statement stmt : stmts) {
            switch (stmt) {
                case ScriptProgram.Statement.Assign ignored -> { }
                case ScriptProgram.Statement.ExprStatement ignored -> { }
                case ScriptProgram.Statement.ReturnStatement ignored -> { }
                case ScriptProgram.Statement.BreakStatement ignored -> { if (loopDepth == 0) return false; }
                case ScriptProgram.Statement.ContinueStatement ignored -> { if (loopDepth == 0) return false; }
                case ScriptProgram.Statement.IfChain chain -> {
                    for (ScriptProgram.Clause clause : chain.clauses()) {
                        if (!isSupported(clause.body(), loopDepth)) return false;
                    }
                }
                case ScriptProgram.Statement.ForStatement fs -> {
                    if (!isSupported(fs.body(), loopDepth + 1)) return false;
                }
                case ScriptProgram.Statement.WhileStatement ws -> {
                    if (!isSupported(ws.body(), loopDepth + 1)) return false;
                }
                default -> { return false; } // nested FunctionDef, Import, StaticDecl (top-level only)
            }
        }
        return true;
    }

    /** Emits {@code stmts} into the current method; only ever called after {@link #isSupported}
     *  has already confirmed the whole body is representable, so the {@code false} return here is
     *  a defensive fallback (the two checks drifting out of sync would be a real bug), not the
     *  primary bail path. */
    private static boolean emitBody(MethodVisitor mv, List<ScriptProgram.Statement> stmts, MethodCtx mc) {
        for (ScriptProgram.Statement stmt : stmts) {
            switch (stmt) {
                case ScriptProgram.Statement.Assign a -> {
                    emitEvaluate(mv, mc, a.formula().toString(), false);
                    int valueSlot = mc.alloc();
                    mv.visitVarInsn(ASTORE, valueSlot);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitLdcInsn(a.name());
                    mv.visitVarInsn(ALOAD, valueSlot);
                    mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val",
                            "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
                    mv.visitInsn(POP);
                }
                case ScriptProgram.Statement.ExprStatement es -> {
                    emitEvaluate(mv, mc, es.formula().toString(), false);
                    mv.visitInsn(POP);
                }
                case ScriptProgram.Statement.ReturnStatement rs -> {
                    // See MethodCtx#isMain's own doc: a def's `return` propagates a real value to
                    // its caller (ARETURN); the top-level "run" method's `return` instead stashes
                    // into __return__ and just stops, matching evaluate()'s own ReturnSignal catch.
                    if (mc.isMain) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitLdcInsn("__return__");
                        if (rs.expr() == null || rs.expr().isEmpty()) {
                            mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
                        } else {
                            emitEvaluate(mv, mc, rs.expr(), false);
                        }
                        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val",
                                "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
                        mv.visitInsn(POP);
                        mv.visitInsn(RETURN);
                    } else {
                        if (rs.expr() == null || rs.expr().isEmpty()) {
                            mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
                        } else {
                            emitEvaluate(mv, mc, rs.expr(), false);
                        }
                        mv.visitInsn(ARETURN);
                    }
                }
                case ScriptProgram.Statement.BreakStatement ignored -> {
                    Loop l = mc.loops.peekLast();
                    if (l == null) return false;
                    mv.visitJumpInsn(GOTO, l.breakLabel());
                }
                case ScriptProgram.Statement.ContinueStatement ignored -> {
                    Loop l = mc.loops.peekLast();
                    if (l == null) return false;
                    mv.visitJumpInsn(GOTO, l.continueLabel());
                }
                case ScriptProgram.Statement.IfChain chain -> {
                    Label endLabel = new Label();
                    for (ScriptProgram.Clause clause : chain.clauses()) {
                        Label nextLabel = new Label();
                        if (!clause.isElse()) {
                            emitEvaluate(mv, mc, clause.condition().toString(), true);
                            mv.visitJumpInsn(IFEQ, nextLabel);
                        }
                        if (!emitBody(mv, clause.body(), mc)) return false;
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(nextLabel);
                        if (clause.isElse()) break; // an else arm is always last; nothing follows it
                    }
                    mv.visitLabel(endLabel);
                }
                case ScriptProgram.Statement.ForStatement fs -> {
                    if (!emitFor(mv, fs, mc)) return false;
                }
                case ScriptProgram.Statement.WhileStatement ws -> {
                    if (!emitWhile(mv, ws, mc)) return false;
                }
                default -> {
                    // FunctionDef (nested), Import, StaticDecl (nested — top-level only) — out of
                    // scope, see class doc.
                    return false;
                }
            }
        }
        return true;
    }

    /** {@code for <vars> in <iterExpr> [where <guardExpr>] { body }} — resolves rows via {@link
     *  ScriptProgram#resolveForRows} (shared with the interpreter, see that method's own doc),
     *  then a real bytecode iterator loop: {@code continue}/a failed guard both jump to the SAME
     *  label the loop's own "next iteration" fallthrough does (re-checking {@code hasNext()}),
     *  {@code break} jumps past the whole loop — matching the interpreter's {@code outer:} labeled
     *  break/continue exactly, just as real branches instead of caught exceptions. */
    private static boolean emitFor(MethodVisitor mv, ScriptProgram.Statement.ForStatement fs, MethodCtx mc) {
        List<String> vars = fs.vars();
        int rowsSlot = mc.alloc();
        int iterSlot = mc.alloc();
        int rowSlot = mc.alloc();

        mv.visitLdcInsn(fs.iterExpr());
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "peek", "()L" + CTX + ";", false);
        emitIntConst(mv, vars.size());
        mv.visitMethodInsn(INVOKESTATIC, PROGRAM, "resolveForRows",
                "(Ljava/lang/String;L" + CTX + ";I)L" + LIST + ";", false);
        mv.visitVarInsn(ASTORE, rowsSlot);

        Label skipAll = new Label();
        mv.visitVarInsn(ALOAD, rowsSlot);
        mv.visitJumpInsn(IFNULL, skipAll);

        mv.visitVarInsn(ALOAD, rowsSlot);
        mv.visitMethodInsn(INVOKEINTERFACE, LIST, "iterator", "()L" + ITERATOR + ";", true);
        mv.visitVarInsn(ASTORE, iterSlot);

        Label continueLabel = new Label();
        Label breakLabel = new Label();
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(ALOAD, iterSlot);
        mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR, "hasNext", "()Z", true);
        mv.visitJumpInsn(IFEQ, breakLabel);

        mv.visitVarInsn(ALOAD, iterSlot);
        mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR, "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, "[L" + VALUE + ";");
        mv.visitVarInsn(ASTORE, rowSlot);

        for (int i = 0; i < vars.size(); i++) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(vars.get(i));
            Label useNull = new Label(), haveVal = new Label();
            mv.visitVarInsn(ALOAD, rowSlot);
            mv.visitInsn(ARRAYLENGTH);
            emitIntConst(mv, i);
            mv.visitJumpInsn(IF_ICMPLE, useNull); // row.length <= i -> out of bounds -> NULL
            mv.visitVarInsn(ALOAD, rowSlot);
            emitIntConst(mv, i);
            mv.visitInsn(AALOAD);
            mv.visitJumpInsn(GOTO, haveVal);
            mv.visitLabel(useNull);
            mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
            mv.visitLabel(haveVal);
            mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val",
                    "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
            mv.visitInsn(POP);
        }

        if (fs.guardExpr() != null) {
            emitEvaluate(mv, mc, fs.guardExpr(), true);
            mv.visitJumpInsn(IFEQ, continueLabel); // guard failed -> skip this row, next iteration
        }

        mc.loops.addLast(new Loop(continueLabel, breakLabel));
        boolean ok = emitBody(mv, fs.body(), mc);
        mc.loops.removeLast();
        if (!ok) return false;

        mv.visitJumpInsn(GOTO, continueLabel);
        mv.visitLabel(breakLabel);
        mv.visitLabel(skipAll);
        return true;
    }

    /** {@code while <cond> { body }} — replicates the interpreter's own runaway-loop guard exactly
     *  (bounded by {@code ws.maxIter()}, currently always 1000 — see {@code ScriptProgram.Parser
     *  #parseWhileLoop}), as a real counted+conditioned loop instead of a Java-side {@code while}
     *  re-evaluating a lambda each pass. */
    private static boolean emitWhile(MethodVisitor mv, ScriptProgram.Statement.WhileStatement ws, MethodCtx mc) {
        int iterSlot = mc.alloc();
        emitIntConst(mv, 0);
        mv.visitVarInsn(ISTORE, iterSlot);

        Label continueLabel = new Label();
        Label breakLabel = new Label();
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(ILOAD, iterSlot);
        emitIntConst(mv, ws.maxIter());
        mv.visitJumpInsn(IF_ICMPGE, breakLabel);
        mv.visitIincInsn(iterSlot, 1);

        emitEvaluate(mv, mc, ws.condExpr(), true);
        mv.visitJumpInsn(IFEQ, breakLabel);

        mc.loops.addLast(new Loop(continueLabel, breakLabel));
        boolean ok = emitBody(mv, ws.body(), mc);
        mc.loops.removeLast();
        if (!ok) return false;

        mv.visitJumpInsn(GOTO, continueLabel);
        mv.visitLabel(breakLabel);
        return true;
    }

    private static void emitIntConst(MethodVisitor mv, int v) {
        if (v >= -1 && v <= 5) mv.visitInsn(ICONST_0 + v);
        else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) mv.visitIntInsn(BIPUSH, v);
        else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) mv.visitIntInsn(SIPUSH, v);
        else mv.visitLdcInsn(v);
    }

    /** Emits {@code expr}'s VALUE onto the stack — a real {@code ScriptValue} normally, or a raw
     *  {@code boolean} when {@code asBool} (an if-chain/loop condition, consumed directly by a
     *  branch instruction).
     *
     *  <p>Tries {@link ScriptBytecodeCompiler#tryParse} FIRST: on success, the expression's own
     *  {@code Expr} tree is emitted DIRECTLY into this method — a bare literal like {@code "0"}
     *  becomes one {@code LDC}, {@code n + 1} becomes real {@code DADD}, a dot-access becomes a
     *  real {@code memberGet} call — with NO runtime {@code ScriptFormula.compile}/{@code
     *  evaluate} round-trip. Only when that fails (a construct outside that grammar — a string
     *  literal, {@code ??}, ...) does this fall back to the old {@code
     *  ScriptFormula.compile(expr).evaluate(b.peek())} pattern, and even then ONLY for that one
     *  expression, not the whole statement/def. A fresh {@code ScriptContext} snapshot
     *  (from {@code Builder.peek()}) is taken right before EVERY expression, inline path or not —
     *  never cached across statements, since an earlier statement's {@code Assign} may have just
     *  mutated the builder and the next expression must see that. */
    private static void emitEvaluate(MethodVisitor mv, MethodCtx mc, String expr, boolean asBool) {
        ScriptBytecodeCompiler.Expr parsed = ScriptBytecodeCompiler.tryParse(expr, mc.resolver);
        if (parsed != null) {
            int ctxSlot = mc.alloc();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "peek", "()L" + CTX + ";", false);
            mv.visitVarInsn(ASTORE, ctxSlot);
            ScriptBytecodeCompiler.Ctx ec = new ScriptBytecodeCompiler.Ctx(ctxSlot, mc.nextSlot);
            ScriptBytecodeCompiler.Expr finalExpr = asBool
                    ? ScriptBytecodeCompiler.toBool(parsed)
                    : ScriptBytecodeCompiler.toAny(parsed);
            finalExpr.emit(mv, ec);
            mc.nextSlot = ec.next; // don't let this expression's scratch slots collide with later statements'
            return;
        }
        mv.visitLdcInsn(expr);
        mv.visitMethodInsn(INVOKESTATIC, FORMULA, "compile", "(Ljava/lang/String;)L" + FORMULA + ";", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "peek", "()L" + CTX + ";", false);
        if (asBool) {
            mv.visitMethodInsn(INVOKEVIRTUAL, FORMULA, "evaluateBool", "(L" + CTX + ";)Z", false);
        } else {
            mv.visitMethodInsn(INVOKEVIRTUAL, FORMULA, "evaluate", "(L" + CTX + ";)L" + VALUE + ";", false);
        }
    }

    /** {@code "kinetics/generators/windmill"} -> {@code {"dev/arubik/craftengine/script/gen/kinetics/generators", "Windmill"}}. */
    private static String[] deriveName(String originPath) {
        String norm = originPath.replace('\\', '/');
        int slash = norm.lastIndexOf('/');
        String folder = slash >= 0 ? norm.substring(0, slash) : "";
        String base = slash >= 0 ? norm.substring(slash + 1) : norm;

        StringBuilder pkg = new StringBuilder("dev/arubik/craftengine/script/gen");
        if (!folder.isEmpty()) {
            for (String seg : folder.split("/")) {
                String clean = sanitizeIdentifier(seg.toLowerCase(java.util.Locale.ROOT));
                if (!clean.isEmpty()) pkg.append('/').append(clean);
            }
        }
        return new String[]{pkg.toString(), pascalCase(base)};
    }

    private static String sanitizeIdentifier(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c)) out.append(c);
            else out.append('_');
        }
        if (out.isEmpty() || !Character.isJavaIdentifierStart(out.charAt(0))) out.insert(0, '_');
        return out.toString();
    }

    private static String pascalCase(String snake) {
        String[] parts = snake.split("_");
        StringBuilder out = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            out.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        String result = sanitizeIdentifier(out.toString());
        return result.isEmpty() ? "Script" : result;
    }

    /** {@code "_sail_count"} -> {@code "_sailCount"}; {@code "on_break"} -> {@code "onBreak"} —
     *  a leading underscore (this codebase's "private helper" convention) is kept as-is since
     *  it's still a legal Java identifier prefix, everything after it is camelCased normally. */
    private static String camelCase(String snake) {
        StringBuilder prefix = new StringBuilder();
        int i = 0;
        while (i < snake.length() && snake.charAt(i) == '_') { prefix.append('_'); i++; }
        String rest = snake.substring(i);
        String[] parts = rest.split("_");
        StringBuilder out = new StringBuilder(prefix);
        for (int j = 0; j < parts.length; j++) {
            String p = parts[j];
            if (p.isEmpty()) continue;
            if (j == 0) out.append(p);
            else out.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sanitizeIdentifier(out.toString());
    }
}
