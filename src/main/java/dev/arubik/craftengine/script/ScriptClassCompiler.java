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

    /** Disambiguates a reload's binary-name collision — see {@code tryCompile}'s own comment at
     *  its call site for why this exists at all. */
    private static final java.util.concurrent.atomic.AtomicInteger RELOAD_COUNTER =
            new java.util.concurrent.atomic.AtomicInteger();

    private static boolean isAlreadyDefined(String binaryName) {
        try { Class.forName(binaryName, false, LOADER); return true; }
        catch (Throwable notDefined) { return false; }
    }

    /** {@code classBytes} is the exact raw bytecode {@link #tryCompile} generated and handed to
     *  the {@link GenLoader} — kept here (not just discarded after {@code define}) specifically so
     *  a test can disassemble it directly; a dynamically-defined class has no {@code .class}
     *  resource discoverable via {@code getResourceAsStream}/{@code ClassLoader.getSystemResource},
     *  so re-reading it back through the class object alone isn't possible. */
    /** {@code mainMethod} is {@code null} when the file's top-level "run" body didn't qualify
     *  (a top-level {@code import}/{@code static}/{@code final}, or an unsupported construct
     *  somewhere in it) — a file can still have compiled {@code def}s with no compiled main body,
     *  or vice versa; the two are independent. */
    /** {@code mainSkipReason} is null when the top-level body DID compile, and otherwise a short
     *  tag naming why it didn't. The top-level body is the per-tick hot path for every machine
     *  script, so "did main compile" is the single most important coverage number this compiler
     *  has — and without a reason recorded, a file that silently falls back to the interpreter is
     *  indistinguishable from one that had nothing to compile. */
    record Compiled(Class<?> generatedClass, Map<String, Method> methodsByDefName, Method mainMethod,
                     byte[] classBytes, String mainSkipReason) {}

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
        /** name -> a JVM local currently holding that variable's raw (unboxed) NUM/BOOL value —
         *  see {@link #emitAssign}'s own doc for how it's populated and {@link
         *  ScriptBytecodeCompiler.VarTypeHint}'s doc for how a bare-identifier read consumes it.
         *  Cleared (never selectively — see the invalidation comment at every clear() call site)
         *  the instant execution crosses into or back out of any {@code if}/{@code for}/{@code
         *  while}, since a single static local slot can't represent "whichever branch/iteration
         *  actually ran" without real per-branch merging this compiler doesn't attempt. */
        final Map<String, ScriptBytecodeCompiler.CachedVarRef> cachedVars = new java.util.HashMap<>();
        /**
         * Names whose value is kept in ONE fixed JVM local for the whole of an enclosing loop, so a
         * variable the loop accumulates into is read from that local rather than looked up in the
         * ScriptContext on every iteration. See {@link #pinLoopCarriedVars}.
         *
         * <p>Only the READ side is pinned — {@link #emitAssign} still writes the Builder every time,
         * so anything that observes the context (a callee, a nested def, the file's own return
         * inspection) sees the same value it always did.
         */
        final Map<String, Integer> pinnedSlots = new java.util.HashMap<>();
        final ScriptBytecodeCompiler.VarTypeHint varHint = cachedVars::get;
        /** name -> a JVM local holding the ScriptValue that {@code getClassInstance}/{@code getVar}
         *  already resolved for that name earlier in this method. Same invalidation points as
         *  {@link #cachedVars} (plus an assignment to that name, and any import) — see
         *  {@link ScriptBytecodeCompiler.ReceiverHint}'s doc for why what's cached is the value
         *  BEFORE the type guard rather than after. */
        /**
         * The ONE {@code Builder.peek()} result for this whole method, emitted in the prologue.
         *
         * <p>Every expression used to take a fresh snapshot, on the belief that an earlier
         * statement's assignment must be visible to the next expression. But {@code peek()} is not a
         * snapshot: it wraps the builder's own {@code vars}/{@code classes} maps — {@code private
         * final}, never reassigned, only mutated in place — in {@code unmodifiableMap}, which is a
         * LIVE VIEW. So a context taken once at entry sees every later {@code val()} write exactly
         * as a freshly-taken one would; the two are behaviourally indistinguishable. ({@code
         * build()} is the real defensive copy, and stays untouched.) That made the per-expression
         * call pure waste: three objects allocated and discarded per expression, per tick — 585
         * sites across just eight of the shipped scripts.
         *
         * <p>Only the OBJECT IDENTITY of the context differs, and nothing compares contexts by
         * identity; dependency tracking is thread-local static state on {@code ScriptContext}, not
         * per-instance, so it is unaffected too.
         *
         * <p>Depends on {@code Builder.vars}/{@code classes} staying final and never being replaced.
         * A future scope push/pop that swapped the map instance would break this.
         */
        int sharedCtxSlot = -1;
        MethodCtx(boolean isMain, ScriptBytecodeCompiler.LocalCallResolver resolver) {
            this.isMain = isMain;
            this.resolver = resolver;
        }
        int alloc() { return nextSlot++; }
        /** Doubles occupy 2 consecutive local slots on the JVM — a plain {@link #alloc()} would
         *  hand out a slot some OTHER value could still overlap into. */
        int allocD() { int s = nextSlot; nextSlot += 2; return s; }
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
            // A reload (ScriptRegistry.loadAll/reloadOne) builds a BRAND NEW ScriptProgram for the
            // SAME file and calls tryCompile with the SAME originPath again — but LOADER is a
            // plugin-lifetime singleton whose already-defined classes never unload (the OLD
            // ScriptProgram instance is garbage, its generated CLASS isn't), so defining the exact
            // same binary name twice throws. Left unhandled, that LinkageError would be swallowed
            // by this method's own outer catch-all and silently, PERMANENTLY degrade this file back
            // to the interpreter after the very first reload, for the rest of the plugin's
            // lifetime — never a crash, but a real, silent loss of the JIT. Detecting the
            // collision up front and suffixing the name instead keeps every reload getting its own
            // fresh class. (Safe to do unconditionally: every real path in this codebase that can
            // change a PolyType handler — ScriptBootstrap.reload() — is always immediately followed
            // by ScriptRegistry.loadAll, so there's never a live window where an OLD compiled class
            // observes a handler that's since changed without also being recompiled itself.)
            if (isAlreadyDefined(binaryName)) {
                String suffix = "$" + RELOAD_COUNTER.incrementAndGet();
                simpleClassName += suffix;
                internalName += suffix;
                binaryName += suffix;
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cw.visit(V21, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, internalName, null, "java/lang/Object", null);

            MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            ctor.visitCode();
            ctor.visitVarInsn(ALOAD, 0);
            ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            ctor.visitInsn(RETURN);
            ctor.visitMaxs(0, 0);
            ctor.visitEnd();

            // This file's own scope, as a static on its generated class, so a compiled call from
            // ANOTHER file can layer it the way UserFunction.call layers definingCtx. Populated by
            // run() below when the file's top-level executes (that is where a top-level constant
            // like tree_utils' OFFSETS6 gets its value). Empty until then, which is why a caller
            // layers it UNDER its own context rather than trusting it.
            cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, "FILE_SCOPE", "L" + CTX + ";", null, null).visitEnd();
            MethodVisitor fsm = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "fileScope", "()L" + CTX + ";", null, null);
            fsm.visitCode();
            fsm.visitFieldInsn(GETSTATIC, internalName, "FILE_SCOPE", "L" + CTX + ";");
            Label haveScope = new Label();
            fsm.visitInsn(DUP);
            fsm.visitJumpInsn(IFNONNULL, haveScope);
            fsm.visitInsn(POP);
            fsm.visitMethodInsn(INVOKESTATIC, CTX, "builder", "()L" + BUILDER + ";", false);
            fsm.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "build", "()L" + CTX + ";", false);
            fsm.visitLabel(haveScope);
            fsm.visitInsn(ARETURN);
            fsm.visitMaxs(0, 0);
            fsm.visitEnd();

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
            // An `import "x.pf" { a, b }` makes a and b callable here. Resolve each to a direct
            // INVOKESTATIC on the IMPORTED file's own generated class, so a cross-file call becomes
            // a real Java call instead of a name lookup plus a reflective invoke through
            // UserFunction. Only where that is equivalent — crossFileTargetFor refuses when the
            // callee reads a file-level name of its own file, which a direct call would not supply
            // (see its doc, and tree_utils' OFFSETS6 for the concrete case). Anything it refuses,
            // or any file that didn't compile, simply keeps the existing interpreted path.
            for (ScriptProgram.Statement s : statements) {
                if (!(s instanceof ScriptProgram.Statement.Import imp)) continue;
                if (imp.symbols() == null || imp.symbols().isEmpty()) continue;
                ScriptProgram imported;
                try { imported = ScriptRegistry.getOrLoadByPath(imp.path()); }
                catch (Throwable ignored) { continue; }
                if (imported == null) continue;
                for (String sym : imp.symbols()) {
                    if (localTargets.containsKey(sym)) continue; // a local def of the same name wins
                    try {
                        ScriptBytecodeCompiler.LocalTarget t = imported.crossFileTargetFor(sym);
                        if (t != null) localTargets.put(sym, t);
                    } catch (Throwable ignored) { /* keep the interpreted path for this symbol */ }
                }
            }

            ScriptBytecodeCompiler.LocalCallResolver resolver = localTargets::get;

            for (ScriptProgram.Statement.FunctionDef fd : defs) {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, methodNames.get(fd.name()),
                        "(L" + BUILDER + ";)L" + VALUE + ";", null, null);
                mv.visitCode();
                MethodCtx mc = new MethodCtx(false, resolver);
                emitCtxPrologue(mv, mc);
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
            String mainSkipReason = null;
            for (ScriptProgram.Statement s : statements) {
                if (s instanceof ScriptProgram.Statement.FunctionDef) continue;
                // A top-level `import` used to disqualify the whole body. That was costly out of
                // proportion: the top-level body is a machine script's PER-TICK path, so one import
                // anywhere in the file left the entire tick path interpreted. Its runtime effect is
                // self-contained (see ScriptProgram.applyImport) and is now emitted as a call.
                // StaticDecl still bails — it needs the owning ScriptProgram instance's staticStore,
                // which a static generated method has no reference to.
                if (s instanceof ScriptProgram.Statement.StaticDecl) {
                    mainEligible = false;
                    mainSkipReason = "top-level static";
                    break;
                }
                mainBody.add(s);
            }
            java.util.Set<String> uncompilableForMain = new java.util.HashSet<>(allDefNames);
            uncompilableForMain.removeAll(compilable);
            if (mainEligible) {
                if (mainBody.isEmpty()) mainSkipReason = "no top-level body";
                else if (!isSupported(mainBody, 0)) mainSkipReason = "unsupported construct in body";
                else if (callsAnyOf(mainBody, uncompilableForMain)) mainSkipReason = "calls an uncompiled def";
            }
            String mainMethodName = null;
            if (mainSkipReason == null && mainEligible && !mainBody.isEmpty()) {
                mainMethodName = "run";
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, mainMethodName, "(L" + BUILDER + ";)V", null, null);
                mv.visitCode();
                MethodCtx mc = new MethodCtx(true, resolver);
                emitCtxPrologue(mv, mc);
                if (!emitBody(mv, mainBody, mc)) return null; // isSupported/emitBody drifted — bail defensively
                // Publish this file's scope for cross-file callers (see FILE_SCOPE above). build(),
                // not peek(): this is retained indefinitely, so it needs a real copy rather than a
                // live view of a builder that keeps mutating.
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "build", "()L" + CTX + ";", false);
                mv.visitFieldInsn(PUTSTATIC, internalName, "FILE_SCOPE", "L" + CTX + ";");
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
            return new Compiled(generated, byDefName, mainMethod, bytes, mainSkipReason);
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
    /** Whether any expression in {@code stmts} mentions one of {@code names} as a bare token. Used
     *  by {@link ScriptProgram#crossFileTargetFor} to decide whether an imported def can be called
     *  directly; see that method for why a coarse textual test is the right conservatism here. */
    static boolean bodyMentionsAnyName(List<ScriptProgram.Statement> stmts, java.util.Set<String> names) {
        for (String expr : collectFormulaStrings(stmts)) {
            for (String n : names) {
                if (java.util.regex.Pattern.compile("(?<![A-Za-z0-9_.])" + java.util.regex.Pattern.quote(n)
                        + "(?![A-Za-z0-9_])").matcher(expr).find()) {
                    return true;
                }
            }
        }
        return false;
    }

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
                // An import's runtime effect only ever touches the builder (see
                // ScriptProgram.applyImport), so it is representable wherever a builder is in scope.
                case ScriptProgram.Statement.Import ignored -> { }
                default -> { return false; } // nested FunctionDef, StaticDecl (needs the owning instance)
            }
        }
        return true;
    }

    /**
     * Every variable name a statement list could write, searched recursively.
     *
     * <p>This is what lets a branch or loop invalidate the variable cache PRECISELY instead of
     * wiping it. Entries are only ever added by {@link #emitAssign}, so an entry created inside a
     * construct always belongs to a name this returns — removing exactly these names at the merge
     * point removes exactly the entries the construct could have added or made stale, and leaves
     * untouched the ones that provably still hold.
     *
     * <p>Soundness rests on this being the complete set of writers reachable from inside: an
     * {@code Assign}, and a {@code for}'s own loop variables. Everything else that could write is
     * excluded elsewhere — an {@code import} binds names not visible here, so
     * {@link #bindsUnknownNames} forces a full clear instead; a {@code return} in a main body
     * writes {@code __return__} and then terminates; and a call cannot write back, because a local
     * {@code def} call {@code copyFrom}s a FRESH builder and a builtin only ever receives the
     * read-only {@code ScriptContext}.
     */
    private static void collectAssignedNames(List<ScriptProgram.Statement> stmts, java.util.Set<String> out) {
        for (ScriptProgram.Statement s : stmts) {
            switch (s) {
                case ScriptProgram.Statement.Assign a -> out.add(a.name());
                case ScriptProgram.Statement.IfChain chain -> {
                    for (ScriptProgram.Clause clause : chain.clauses()) collectAssignedNames(clause.body(), out);
                }
                case ScriptProgram.Statement.ForStatement fs -> {
                    out.addAll(fs.vars());
                    collectAssignedNames(fs.body(), out);
                }
                case ScriptProgram.Statement.WhileStatement ws -> collectAssignedNames(ws.body(), out);
                default -> { }
            }
        }
    }

    /** Whether this statement list contains an {@code import} at any depth. An import binds names
     *  that aren't syntactically visible, so {@link #collectAssignedNames} can't see them and the
     *  cache has to be dropped wholesale. */
    private static boolean bindsUnknownNames(List<ScriptProgram.Statement> stmts) {
        for (ScriptProgram.Statement s : stmts) {
            switch (s) {
                case ScriptProgram.Statement.Import ignored -> { return true; }
                case ScriptProgram.Statement.IfChain chain -> {
                    for (ScriptProgram.Clause clause : chain.clauses()) {
                        if (bindsUnknownNames(clause.body())) return true;
                    }
                }
                case ScriptProgram.Statement.ForStatement fs -> { if (bindsUnknownNames(fs.body())) return true; }
                case ScriptProgram.Statement.WhileStatement ws -> { if (bindsUnknownNames(ws.body())) return true; }
                default -> { }
            }
        }
        return false;
    }

    /** The cache state that survives a construct: everything cached before it, minus every name the
     *  construct could write. A full clear when an import makes the written set unknowable. */
    private static Map<String, ScriptBytecodeCompiler.CachedVarRef> survivingCache(
            Map<String, ScriptBytecodeCompiler.CachedVarRef> before, List<ScriptProgram.Statement> body) {
        if (bindsUnknownNames(body)) return new java.util.HashMap<>();
        java.util.Set<String> assigned = new java.util.HashSet<>();
        collectAssignedNames(body, assigned);
        Map<String, ScriptBytecodeCompiler.CachedVarRef> out = new java.util.HashMap<>(before);
        out.keySet().removeAll(assigned);
        return out;
    }

    /** Emits the single Builder.peek() this method will reuse — see MethodCtx#sharedCtxSlot.
     *  Must run in the PROLOGUE, before any branch, so every use is dominated by the store. */
    private static void emitCtxPrologue(MethodVisitor mv, MethodCtx mc) {
        mc.sharedCtxSlot = mc.alloc();
        mv.visitVarInsn(ALOAD, 0); // the Builder parameter
        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "peek", "()L" + CTX + ";", false);
        mv.visitVarInsn(ASTORE, mc.sharedCtxSlot);
    }

    /** Emits {@code stmts} into the current method; only ever called after {@link #isSupported}
     *  has already confirmed the whole body is representable, so the {@code false} return here is
     *  a defensive fallback (the two checks drifting out of sync would be a real bug), not the
     *  primary bail path. */
    private static boolean emitBody(MethodVisitor mv, List<ScriptProgram.Statement> stmts, MethodCtx mc) {
        for (ScriptProgram.Statement stmt : stmts) {
            switch (stmt) {
                case ScriptProgram.Statement.Import imp -> emitImport(mv, imp);
                case ScriptProgram.Statement.Assign a -> emitAssign(mv, mc, a.name(), a.formula().toString());
                case ScriptProgram.Statement.ExprStatement es -> {
                    emitEvaluateDiscarding(mv, mc, es.formula().toString());
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
                    // Everything cached before the chain. Conditions still see it (nothing has
                    // branched yet), and each clause body RESTORES it rather than starting empty —
                    // restoring is what keeps sibling clauses from seeing each other's entries,
                    // which is the only thing the old blanket clear was actually protecting.
                    Map<String, ScriptBytecodeCompiler.CachedVarRef> before =
                            new java.util.HashMap<>(mc.cachedVars);
                    java.util.List<ScriptProgram.Statement> allBodies = new ArrayList<>();
                    for (ScriptProgram.Clause clause : chain.clauses()) allBodies.addAll(clause.body());

                    for (ScriptProgram.Clause clause : chain.clauses()) {
                        Label nextLabel = new Label();
                        if (!clause.isElse()) {
                            mc.cachedVars.clear();
                            mc.cachedVars.putAll(before);
                            emitEvaluate(mv, mc, clause.condition().toString(), true);
                            mv.visitJumpInsn(IFEQ, nextLabel);
                        }
                        mc.cachedVars.clear();
                        mc.cachedVars.putAll(before);
                        if (!emitBody(mv, clause.body(), mc)) return false;
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(nextLabel);
                        if (clause.isElse()) break; // an else arm is always last; nothing follows it
                    }
                    // Merge point: keep only what NO clause could have written. A name any clause
                    // assigns is dropped — its pre-chain slot still holds the pre-chain value, and
                    // a clause that ran has since allocated a different slot for it.
                    Map<String, ScriptBytecodeCompiler.CachedVarRef> surviving = survivingCache(before, allBodies);
                    mc.cachedVars.clear();
                    mc.cachedVars.putAll(surviving);
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
        // A single-variable for iterates elements directly (no per-element ScriptValue[] wrapper);
        // multi-variable (map destructuring) still needs rows. The inline-iterable path is the only
        // one that can choose — the text fallback goes through resolveForRows, which always rows.
        boolean singleVar = vars.size() == 1;
        int rowsSlot = mc.alloc();
        int iterSlot = mc.alloc();
        int rowSlot = mc.alloc();

        // Compile the iterable expression INLINE where we can, so `for r in Machine.recipes` goes
        // through the PolyClass / inline-cache paths like any other expression, instead of handing
        // the raw source text to resolveForRows and re-entering ScriptFormula.compile + evaluate on
        // every single execution of the loop.
        ScriptBytecodeCompiler.Expr iter =
                ScriptBytecodeCompiler.tryParse(fs.iterExpr(), mc.resolver, mc.varHint);
        String elementType = null;
        // Only the inline path can hand back bare elements; the text fallback goes through
        // resolveForRows, which always produces rows.
        boolean elementsDirect = singleVar && iter != null;
        if (iter != null) {
            ScriptBytecodeCompiler.Ctx ic = new ScriptBytecodeCompiler.Ctx(mc.sharedCtxSlot, mc.nextSlot);
            // A property registered with a listOf(...) codec has a List-returning accessor on its
            // PolyClass. Reading through it makes the whole iterable ONE typed call — the elements
            // come out directly, instead of the handler's real List<T> being wrapped in a
            // ScriptValue.Array that elementsOf immediately takes apart again on every execution.
            boolean listDirect = singleVar
                    && iter instanceof ScriptBytecodeCompiler.PropRead pr && pr.listJavaName != null;
            elementType = singleVar && iter instanceof ScriptBytecodeCompiler.PropRead pr2
                    ? pr2.elementPolyType : null;
            if (listDirect) {
                ((ScriptBytecodeCompiler.PropRead) iter).emitAsList(mv, ic);
                mc.nextSlot = ic.next;
            } else {
                ScriptBytecodeCompiler.toAny(iter).emit(mv, ic);
                mc.nextSlot = ic.next;
                if (singleVar) {
                    // One loop variable: iterate the elements directly. rowsOf would wrap each one
                    // in a throwaway ScriptValue[] — an array allocation per element per tick.
                    mv.visitMethodInsn(INVOKESTATIC, PROGRAM, "elementsOf",
                            "(L" + VALUE + ";)L" + LIST + ";", false);
                } else {
                    emitIntConst(mv, vars.size());
                    mv.visitMethodInsn(INVOKESTATIC, PROGRAM, "rowsOf",
                            "(L" + VALUE + ";I)L" + LIST + ";", false);
                }
            }
        } else {
            // The compiler can't represent this iterable expression — hand the source text to the
            // interpreter's own resolver, exactly as before.
            mv.visitLdcInsn(fs.iterExpr());
            mv.visitVarInsn(ALOAD, mc.sharedCtxSlot);
            emitIntConst(mv, vars.size());
            mv.visitMethodInsn(INVOKESTATIC, PROGRAM, "resolveForRows",
                    "(Ljava/lang/String;L" + CTX + ";I)L" + LIST + ";", false);
        }
        mv.visitVarInsn(ASTORE, rowsSlot);

        // Seeded BEFORE the loop-top label so each seed runs once, and before the null-list jump
        // below: the pinned slot has to be definitely assigned on EVERY path reaching the code after
        // the loop, including the one that skips it entirely.
        Map<String, ScriptBytecodeCompiler.CachedVarRef> pins =
                pinLoopCarriedVars(mv, mc, fs.body(), vars);

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
        // elementsOf yields the values themselves; rowsOf yields a ScriptValue[] per row.
        mv.visitTypeInsn(CHECKCAST, elementsDirect ? VALUE : "[L" + VALUE + ";");
        mv.visitVarInsn(ASTORE, rowSlot);

        // The loop runs a dynamic number of times, so the body's own writes can't be assumed — but
        // a name the body NEVER writes still holds whatever it held before the loop, on every
        // iteration and after it. Keep exactly those. (A cache entry can only be created by an
        // emitAssign, so anything the body adds is in the removed set by construction, which is
        // what makes this safe across the back edge.)
        Map<String, ScriptBytecodeCompiler.CachedVarRef> loopSurviving = survivingCache(mc.cachedVars, fs.body());
        loopSurviving.keySet().removeAll(vars); // the loop variables are rebound every iteration
        // A pinned name survives the back edge after all: its assignments all write the one slot
        // these entries point at, so the value read at the top of any iteration is the one the
        // previous iteration left. If the loop never runs, the slot still holds the seed.
        loopSurviving.putAll(pins);
        mc.cachedVars.clear();
        mc.cachedVars.putAll(loopSurviving);

        if (elementsDirect) {
            // The element IS the value — no array, no bounds check.
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(vars.get(0));
            mv.visitVarInsn(ALOAD, rowSlot);
            mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val",
                    "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
            mv.visitInsn(POP);
            // The element is already in a local, and we are the ones who just bound the name to it,
            // so reading it back out of the ScriptContext is a map lookup for a value we are holding.
            // Cached exactly like an assignment's value: same guarantee, same invalidation rules.
            // (The Builder write above still happens — anything that observes the context by name,
            // a callee or a nested def, keeps seeing the binding.)
            // When the iterable was a list-typed member, its registration already declares what one
            // element IS — so the loop variable carries that PolyType and the body's member accesses
            // on it specialize, instead of dispatching generically once per element per tick.
            mc.cachedVars.put(vars.get(0),
                    new ScriptBytecodeCompiler.CachedVarRef(rowSlot, ScriptBytecodeCompiler.Type.ANY,
                            elementType));
        }
        for (int i = 0; !elementsDirect && i < vars.size(); i++) {
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
        // Exiting: the same set that was valid at the head is valid here — the body may have added
        // entries during emission, and those belong to names it writes, so they go. The pins go too:
        // their CACHE entries stay valid (the slot holds the last assigned value), but the name must
        // stop being pinned, so a later loop pins a slot of its own rather than reusing this one.
        mc.pinnedSlots.keySet().removeAll(pins.keySet());
        mc.cachedVars.clear();
        mc.cachedVars.putAll(loopSurviving);
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

        // Seeded from the cache as it stands NOW, before survivingCache drops the written names.
        Map<String, ScriptBytecodeCompiler.CachedVarRef> pins =
                pinLoopCarriedVars(mv, mc, ws.body(), java.util.List.of());

        // Same reasoning as emitFor: keep only what the body provably cannot write, plus the pins.
        Map<String, ScriptBytecodeCompiler.CachedVarRef> loopSurviving = survivingCache(mc.cachedVars, ws.body());
        loopSurviving.putAll(pins);
        mc.cachedVars.clear();
        mc.cachedVars.putAll(loopSurviving);

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
        // Exiting: the same set that was valid at the head is valid here — the body may have added
        // entries during emission, and those belong to names it writes, so they go. See emitFor for
        // why the pins are released while their cache entries stay.
        mc.pinnedSlots.keySet().removeAll(pins.keySet());
        mc.cachedVars.clear();
        mc.cachedVars.putAll(loopSurviving);
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
    /**
     * Emits {@code expr} for its SIDE EFFECT only, leaving nothing on the stack.
     *
     * <p>A bare expression statement — {@code Machine.set_rpm_output(rpm)} — discards its value, so
     * boxing that value first is pure waste. The old path went through {@link #emitEvaluate}, which
     * coerces to ANY, and then {@code POP}'d: a {@code ScriptValue.of(boolean)} allocation per
     * statement, per tick, immediately thrown away. Now the raw expression is emitted and popped at
     * its own width, so a NUM/BOOL statement never allocates at all.
     */
    private static void emitEvaluateDiscarding(MethodVisitor mv, MethodCtx mc, String expr) {
        ScriptBytecodeCompiler.Expr parsed = ScriptBytecodeCompiler.tryParse(expr, mc.resolver, mc.varHint);
        if (parsed == null) {
            // No parse: the ScriptFormula.compile path always yields a ScriptValue reference.
            emitEvaluate(mv, mc, expr, false);
            mv.visitInsn(POP);
            return;
        }
        int ctxSlot = mc.sharedCtxSlot;
        ScriptBytecodeCompiler.Ctx ec = new ScriptBytecodeCompiler.Ctx(ctxSlot, mc.nextSlot);
        parsed.emit(mv, ec); // NOT toAny — that box is exactly what we're avoiding
        mc.nextSlot = ec.next;
        // A double occupies two stack words; a boolean (int) and a ScriptValue reference occupy one.
        mv.visitInsn(parsed.type() == ScriptBytecodeCompiler.Type.NUM ? POP2 : POP);
    }

    private static void emitEvaluate(MethodVisitor mv, MethodCtx mc, String expr, boolean asBool) {
        ScriptBytecodeCompiler.Expr parsed = ScriptBytecodeCompiler.tryParse(expr, mc.resolver, mc.varHint);
        if (parsed != null) {
            int ctxSlot = mc.sharedCtxSlot;
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
        mv.visitVarInsn(ALOAD, mc.sharedCtxSlot);
        if (asBool) {
            mv.visitMethodInsn(INVOKEVIRTUAL, FORMULA, "evaluateBool", "(L" + CTX + ";)Z", false);
        } else {
            mv.visitMethodInsn(INVOKEVIRTUAL, FORMULA, "evaluate", "(L" + CTX + ";)L" + VALUE + ";", false);
        }
    }

    /** Emits an {@code Assign}'s full effect, caching the value in a fresh JVM local (see {@link
     *  MethodCtx#cachedVars}'s own doc) regardless of its inferred {@link
     *  ScriptBytecodeCompiler.Type} — NUM/BOOL cache a raw unboxed primitive, ANY (a string, array,
     *  map, object — whatever a dot-call/subscript/function-call/literal evaluated to) caches the
     *  already-boxed {@code ScriptValue} reference directly, since there's nothing narrower to
     *  unbox in the first place. Either way, the very NEXT statement's bare read of this SAME name
     *  becomes a plain {@code DLOAD}/{@code ILOAD}/{@code ALOAD} — no {@code
     *  getClassInstance}/{@code getVar} round-trip through the {@code ScriptContext} — as long as
     *  nothing invalidated it in between (see the {@code cachedVars.clear()} call sites in {@link
     *  #emitBody}/{@link #emitFor}/{@link #emitWhile} for exactly when that happens).
     *
     *  <p>The boxed {@code ScriptValue} is ALWAYS ALSO written to the {@code Builder} regardless —
     *  this is a READ-side optimization only, never a write-side omission. Anything this compiler
     *  doesn't statically see through — a {@code for}-loop this variable feeds via {@code
     *  resolveForRows}, a call to another compiled or interpreted {@code def}, the file's own
     *  {@code __return__} inspection, {@code ScriptCall}'s cross-file dispatch — only ever
     *  observes the {@code Builder}'s copy, never this method's own JVM locals, so that write can
     *  never be the thing that gets skipped. */
    /** {@code import "path" { a, b }} — a direct call to {@link ScriptProgram#applyImport}, the same
     *  routine the interpreter's own Import case runs. Emitting this (rather than bailing the whole
     *  body) is what lets a file with imports compile its top-level, per-tick path at all. */
    private static void emitImport(MethodVisitor mv, ScriptProgram.Statement.Import imp) {
        mv.visitVarInsn(ALOAD, 0); // the Builder
        mv.visitLdcInsn(imp.path());
        if (imp.alias() != null) mv.visitLdcInsn(imp.alias()); else mv.visitInsn(ACONST_NULL);
        List<String> symbols = imp.symbols();
        if (symbols == null || symbols.isEmpty()) {
            mv.visitMethodInsn(INVOKESTATIC, LIST, "of", "()L" + LIST + ";", true);
        } else {
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            for (String sym : symbols) {
                mv.visitInsn(DUP);
                mv.visitLdcInsn(sym);
                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                mv.visitInsn(POP);
            }
        }
        mv.visitMethodInsn(INVOKESTATIC, PROGRAM, "applyImport",
                "(L" + BUILDER + ";Ljava/lang/String;Ljava/lang/String;L" + LIST + ";)V", false);
    }

    private static void emitAssign(MethodVisitor mv, MethodCtx mc, String name, String formula) {
        // A PINNED name keeps its entry: the pinned slot still holds the OLD value at this point
        // (the new one is stored below, after the right-hand side has been emitted), so the RHS
        // reads the correct value from the local instead of going back to the ScriptContext — which
        // is the whole point of pinning `count = count + 1`.
        if (!mc.pinnedSlots.containsKey(name)) mc.cachedVars.remove(name);

        ScriptBytecodeCompiler.Expr parsed = ScriptBytecodeCompiler.tryParse(formula, mc.resolver, mc.varHint);
        if (parsed == null) {
            // tryParse itself failed, falling all the way to ScriptFormula.compile — the value
            // isn't known until a REAL evaluate() call happens, nothing to cache from here.
            emitEvaluate(mv, mc, formula, false);
            int valueSlot = mc.alloc();
            mv.visitVarInsn(ASTORE, valueSlot);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(name);
            mv.visitVarInsn(ALOAD, valueSlot);
            mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val", "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
            mv.visitInsn(POP);
            return;
        }

        ScriptBytecodeCompiler.Type type = parsed.type();
        int ctxSlot = mc.sharedCtxSlot;
        ScriptBytecodeCompiler.Ctx ec = new ScriptBytecodeCompiler.Ctx(ctxSlot, mc.nextSlot);
        parsed.emit(mv, ec); // raw NUM double / raw BOOL int / already-boxed ANY reference
        mc.nextSlot = ec.next;

        int primSlot;
        int valueSlot;
        switch (type) {
            case NUM -> {
                primSlot = mc.allocD();
                mv.visitInsn(DUP2);
                mv.visitVarInsn(DSTORE, primSlot);
                mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                valueSlot = mc.alloc();
                mv.visitVarInsn(ASTORE, valueSlot);
            }
            case BOOL -> {
                primSlot = mc.alloc();
                mv.visitInsn(DUP);
                mv.visitVarInsn(ISTORE, primSlot);
                mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
                valueSlot = mc.alloc();
                mv.visitVarInsn(ASTORE, valueSlot);
            }
            default -> { // ANY — already a boxed ScriptValue; one slot serves both the cache AND the builder write
                primSlot = mc.alloc();
                mv.visitVarInsn(ASTORE, primSlot);
                valueSlot = primSlot;
            }
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(name);
        mv.visitVarInsn(ALOAD, valueSlot);
        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val", "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
        mv.visitInsn(POP);

        // What the right-hand side is KNOWN to evaluate to, when its registration declares it —
        // `target = Machine.block` makes `target` a Block, so `target.property(...)` in a later
        // statement compiles to PolyClass dispatch instead of a generic memberGet. Nothing is
        // assumed: a hint only ever selects a guarded fast path, and a receiver that turns out not
        // to be that type takes the same generic arm it would have taken anyway.
        String rhsPolyType = ScriptBytecodeCompiler.polyTypeOf(parsed);

        Integer pinned = mc.pinnedSlots.get(name);
        if (pinned != null) {
            // Loop-carried: the ONE local the loop reads this name from has to hold the new value
            // too, or the next iteration would read a stale one. Cached as ANY because the pinned
            // slot is a ScriptValue reference — assignments in different branches of the loop need
            // not agree on a narrower type.
            mv.visitVarInsn(ALOAD, valueSlot);
            mv.visitVarInsn(ASTORE, pinned);
            mc.cachedVars.put(name, new ScriptBytecodeCompiler.CachedVarRef(
                    pinned, ScriptBytecodeCompiler.Type.ANY, rhsPolyType));
            return;
        }
        mc.cachedVars.put(name, new ScriptBytecodeCompiler.CachedVarRef(primSlot, type, rhsPolyType));
    }

    /**
     * Pins every name a loop body assigns to a fixed JVM local, seeded with the value the name holds
     * on entry, and returns the cache entries that make the body read it from there.
     *
     * <p>Without this, a name the body writes is dropped from the cache across the back edge — it
     * has to be, since a fresh {@code emitAssign} slot is not the slot the previous iteration wrote.
     * So {@code count = count + 1} re-read {@code count} out of the ScriptContext map on every
     * single iteration. Pinning gives all of a name's assignments one slot, which the loop top can
     * then rely on.
     *
     * <p>Why this is sound. A cache entry can only ever be created by an {@code emitAssign}, and
     * every {@code emitAssign} for a pinned name writes the pinned slot — so the slot tracks the
     * variable exactly. A branch that does not run leaves the slot holding the previous value, which
     * IS the current value. Nothing else can change a variable behind this compiler's back: a callee
     * receives a COPIED Builder, and the {@code ScriptContext} it is handed is read-only. The one
     * construct that can bind names this compiler cannot see is an import, which
     * {@link #bindsUnknownNames} already refuses — so pinning is skipped entirely for such a body.
     *
     * <p>Only names that are read somewhere in the body are worth pinning; a write-only name would
     * pay for the seed load and never use it.
     */
    private static Map<String, ScriptBytecodeCompiler.CachedVarRef> pinLoopCarriedVars(
            MethodVisitor mv, MethodCtx mc, List<ScriptProgram.Statement> body,
            java.util.Collection<String> excluded) {
        Map<String, ScriptBytecodeCompiler.CachedVarRef> pins = new java.util.HashMap<>();
        if (bindsUnknownNames(body)) return pins;
        java.util.Set<String> assigned = new java.util.HashSet<>();
        collectAssignedNames(body, assigned);
        assigned.removeAll(excluded);
        for (String name : assigned) {
            if (mc.pinnedSlots.containsKey(name)) continue; // an outer loop already pinned it
            if (!bodyMentionsAnyName(body, java.util.Set.of(name))) continue;
            int slot = mc.alloc();
            // Seed from the cache when the value is already in a local, otherwise read it once —
            // one lookup before the loop in place of one per iteration.
            ScriptBytecodeCompiler.CachedVarRef existing = mc.cachedVars.get(name);
            if (existing != null) {
                ScriptBytecodeCompiler.emitCachedAsValue(mv, existing);
            } else {
                mv.visitVarInsn(ALOAD, mc.sharedCtxSlot);
                mv.visitLdcInsn(name);
                mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getClassOrVar",
                        "(Ljava/lang/String;)L" + VALUE + ";", false);
            }
            mv.visitVarInsn(ASTORE, slot);
            mc.pinnedSlots.put(name, slot);
            pins.put(name, new ScriptBytecodeCompiler.CachedVarRef(slot, ScriptBytecodeCompiler.Type.ANY));
        }
        return pins;
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
