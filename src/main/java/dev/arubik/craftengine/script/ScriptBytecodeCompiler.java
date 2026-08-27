package dev.arubik.craftengine.script;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.objectweb.asm.Opcodes.*;

/**
 * JIT backend for {@link ScriptFormula}: compiles a broad subset of the {@code .pf} expression
 * grammar — arithmetic, comparisons, {@code !}/{@code &&}/{@code ||}, ternary, a handful of
 * {@code Math}-backed builtins, plain variable reads, {@code Name.property}/{@code
 * Name.method(args)} member access, and any other function call (including a user-defined {@code
 * def}) via a fallback into {@link ScriptFormula#callBuiltin} — straight to a real JVM class
 * implementing {@link ScriptFormula.Node}, instead of walking a tree of lambda closures on every
 * evaluation. This is exactly the shape of the hottest formulas flagged by profiling — kinetics
 * rot_y/rpm expressions, renderer conditions — which read a couple of {@code Machine.*}/context
 * values (often {@code Map}/{@code Obj}-backed, see the ANY type below) and do arithmetic/
 * comparisons over them.
 *
 * <p>Deliberately fail-soft: {@link #tryCompile} runs its own small recursive-descent parser
 * SEPARATELY from {@link ScriptFormula}'s real one, and bails (returns {@code null}) the instant
 * it sees a construct it doesn't model at all — {@code $var}, {@code ??} — OR whenever it
 * recognizes a construct but can't
 * PROVE its narrow (numeric/boolean) codegen would match the interpreter's actual runtime
 * dispatch for that specific operand shape (see the {@code ==}/{@code !=}/{@code +} bail rules
 * below). {@link ScriptFormula#doCompile} tries this first and falls straight through to the
 * always-correct lambda-tree interpreter on any {@code null} or thrown exception, so a formula
 * this compiler can't (or incorrectly tries to) handle degrades to identical behavior, never wrong
 * behavior — bailing more than strictly necessary is always the safe direction; miscompiling a
 * construct it DOES accept is not.
 *
 * <p>Grammar precedence deliberately mirrors {@code ScriptFormula.Parser} EXACTLY for every level
 * both recognize (confirmed by reading that Parser's actual method chain, not assumed):
 * {@code ternary > || > && > ! > bitwise/shift > compare > + - > * / % (incl. ** //) > ^ (pow) >
 * unary- > primary}. The real chain also has a null-coalesce layer (between compare and
 * {@code + -}) that this compiler still omits entirely — {@code ??} has zero real usage across
 * the shipped {@code .pf} scripts as of this writing, so it isn't worth the grammar weight yet.
 * Omitting a layer is always SAFE here (not a silent misparse): any operator from an omitted
 * layer is simply left unconsumed, which either aborts that operand's own bail chain or trips
 * {@link #tryCompile}'s final "did we consume the whole string" check, never gets miscompiled as
 * something else.
 *
 * <h3>Typed micro-AST</h3>
 * Every {@link Expr} carries one of three {@link Type}s:
 * <ul>
 *   <li>{@code NUM} — a raw JVM {@code double} on the operand stack (arithmetic, Math calls,
 *       numeric literals).</li>
 *   <li>{@code BOOL} — a raw JVM {@code int} 0/1 (comparisons, {@code &&}/{@code ||}/{@code !},
 *       boolean literals).</li>
 *   <li>{@code ANY} — an actual boxed {@link ScriptValue} reference on the stack (a bare variable
 *       read, {@code Name.prop}/{@code Name.method(...)}, or any function-call fallback through
 *       {@code callBuiltin}) — needed because these can genuinely hold anything at runtime (an
 *       Item, a Map/Obj, a Numeric {@code Obj}, a String, ...), so eagerly narrowing them to a
 *       double/boolean would silently corrupt a formula whose final result is meant to stay that
 *       original type (e.g. a bare {@code Machine.gas_tanks} formula evaluated via a caller that
 *       wants the real Map/Array/Item back, not a coerced number). ANY round-trips through {@link
 *       ScriptFormula#memberGet}/{@link ScriptFormula#memberCall}/{@link
 *       ScriptFormula#callBuiltin} exactly as the interpreter would, so a Map lookup, a
 *       user-defined function call, or any other object-shaped result comes back byte-identical —
 *       this compiler only ever narrows an ANY value to NUM/BOOL where the grammar position
 *       PROVABLY requires a scalar (an arithmetic/compare operand, a condition), via {@link
 *       #toNum}/{@link #toBool}, which call the real {@code asNum()}/{@code asBool()} default
 *       methods rather than guessing.</li>
 * </ul>
 * The compiled method's TOP-level result is boxed via {@code ScriptValue.of(...)} only when it's
 * NUM/BOOL; an ANY result is returned as-is, unboxed and untouched.
 *
 * <h3>Why {@code ==}/{@code !=} and {@code +} need a SEPARATE codegen path for an ANY operand</h3>
 * Both are type-polymorphic in the real interpreter: {@code ==}/{@code !=} special-case a
 * {@code Null} operand (reference-style null equality) and a {@code Str} operand (string
 * equality) before falling back to numeric; {@code +} does string concatenation whenever either
 * side is a {@code Str}. An ANY-typed operand could be exactly those types at runtime, so blindly
 * coercing both sides to {@code double} for these two operators would silently diverge from the
 * interpreter (e.g. {@code name == null} or {@code "a" + b}) — so when either operand IS ANY,
 * {@link P#parseCompare}/{@link P#parseAdd} route through {@link ScriptFormula#valuesEqual}/
 * {@link ScriptFormula#addPolymorphic} instead: the SAME polymorphic logic the interpreter's own
 * {@code Node} lambdas call, shared rather than re-derived, so the two can never drift apart.
 * When NEITHER operand is ANY the value can never actually be a Null/Str at runtime, so both stay
 * on the plain numeric fast path. Every OTHER comparison ({@code >}/{@code <}/{@code >=}/{@code
 * <=}) and the other arithmetic operators ({@code -}/{@code *}/{@code /}/{@code %}) are
 * unconditionally numeric in the real interpreter regardless of operand type, so those stay on
 * the numeric path unconditionally via {@link #toNum} coercion.
 */
final class ScriptBytecodeCompiler {

    private ScriptBytecodeCompiler() {}

    private static final Logger LOG = Logger.getLogger("CraftEnginePolyfills");
    /** Every distinct expression string this process has already logged a codegen/registration
     *  outcome for — a formula is re-evaluated every tick, so without this a single bad/good
     *  expression would spam one log line per tick forever. Logged exactly once per distinct
     *  {@code expr} for the lifetime of the JVM (a config reload re-parses formulas but they're
     *  the same strings, so this intentionally does NOT reset on reload — the first reload's
     *  worth of log lines already told the story). */
    private static final Set<String> LOGGED_COMPILE = ConcurrentHashMap.newKeySet();
    private static final Set<String> LOGGED_FAIL = ConcurrentHashMap.newKeySet();

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final String NODE_IFACE = "dev/arubik/craftengine/script/ScriptFormula$Node";
    private static final String FORMULA = "dev/arubik/craftengine/script/ScriptFormula";
    private static final String CTX = "dev/arubik/craftengine/script/ScriptContext";
    private static final String VALUE = "dev/arubik/craftengine/script/ScriptValue";
    private static final String BUILDER = "dev/arubik/craftengine/script/ScriptContext$Builder";

    /** Resolves a bare call name to a LOCALLY COMPILED {@code def} on the SAME generated class, so
     *  {@link P#genericCall} can emit a direct {@code INVOKESTATIC} to it instead of routing
     *  through {@link ScriptFormula#callBuiltin} (which can never find a compiled def — nothing
     *  binds it as a {@code UserFunction} anywhere). {@code null} for anything else (a builtin, an
     *  interpreted sibling, an unrelated name) — {@link P#genericCall} falls back to the ordinary
     *  {@code callBuiltin} path for those, unchanged. Supplied only by {@link ScriptClassCompiler},
     *  which is the only caller that ever knows a whole file's def set at once; {@link #tryCompile}
     *  (single standalone expressions, no file context) always passes {@code null}. */
    interface LocalCallResolver {
        LocalTarget resolve(String name);
    }

    /** {@code internalClassName}/{@code methodName}: the generated class (JVM internal name, e.g.
     *  {@code "dev/arubik/craftengine/script/gen/kinetics/generators/Windmill"}) and method a local
     *  call should invoke directly. {@code paramNames}: the def's own parameter names, in order —
     *  needed to bind each argument into the ISOLATED per-call {@code Builder} (see {@link
     *  P#localCall}) the same way {@link UserFunction#call} binds them for an interpreted call. */
    /**
     * {@code crossFile}: this target lives in ANOTHER file's generated class, reached by an
     * ordinary {@code INVOKESTATIC}. Such a call must first layer that file's OWN scope under the
     * caller's, because the callee can read its file's top-level values — {@code tree_utils}'
     * {@code _find_tree} reads the top-level {@code OFFSETS6}, which the importing file has never
     * heard of. That mirrors what {@code UserFunction.call} does (definingCtx first, caller on top),
     * and the generated class exposes it as a static {@code fileScope()}.
     */
    record LocalTarget(String internalClassName, String methodName, List<String> paramNames, boolean crossFile) {
        LocalTarget(String internalClassName, String methodName, List<String> paramNames) {
            this(internalClassName, methodName, paramNames, false);
        }
    }

    /** Resolves a bare variable name to a JVM local slot ALREADY holding its raw (unboxed)
     *  {@code double}/{@code boolean} value, when {@link ScriptClassCompiler} knows one is
     *  currently valid — the direct answer to "why does reading {@code n} inside a loop go through
     *  {@code ScriptContext.getVar} every single time instead of just being a local variable":
     *  when the assignment that produced this value was itself provably NUM/BOOL (see {@code
     *  ScriptClassCompiler#emitAssign}), a plain bare-identifier read of that SAME name — as long
     *  as nothing has invalidated the cache since (a branch, a loop, anything not statically
     *  provable to preserve it — see that method's own doc for the exact invalidation rule) — skips
     *  {@code getClassInstance}/{@code getVar}/boxing ENTIRELY and just loads the local. Supplied
     *  only by {@link ScriptClassCompiler}; {@link #tryCompile}'s standalone expressions (no
     *  enclosing method, no notion of "the previous statement just assigned this") always pass
     *  {@code null}. */
    interface VarTypeHint {
        CachedVarRef get(String name);
    }

    // NOT DONE, deliberately: caching a resolved dot-access receiver across statements
    // (windmill.pf resolves "Machine" 36 times in one file, and each one repeats
    // getClassInstance + getVar). It was implemented and reverted — it produced
    // "VerifyError: Bad local variable type" on 33 of the 103 shipped scripts.
    //
    // The reason is structural, not a slip: emitResolveInstanceOrVar also runs from inside
    // expressions with CONDITIONAL execution — the right operand of a short-circuit && / ||, a
    // ternary arm. A slot written there does not dominate later reads, so a subsequent statement
    // can load it on a path where it was never stored. cachedVars avoids this only because
    // emitAssign always writes at statement level, where the store does dominate.
    //
    // Making it sound needs dominance tracking inside expression emission. That is a real piece of
    // machinery, and the payoff is two map lookups per call site — not worth it at that price.

    /** {@code slot}: the JVM local holding the value — {@code DLOAD}/{@code ILOAD}/{@code ALOAD}
     *  depending on {@code type}. Every {@link Type} is cacheable: NUM/BOOL hold a raw unboxed
     *  primitive, ANY holds the already-boxed {@code ScriptValue} reference (a string, array, map,
     *  object, whatever it evaluated to) — either way, {@code getClassInstance}/{@code getVar}'s
     *  {@code ScriptContext} round-trip is skipped, not just the primitive boxing. */
    record CachedVarRef(int slot, Type type) {}
    private static final String MATH = "java/lang/Math";
    private static final String LIST = "java/util/List";
    private static final String ARRAYLIST = "java/util/ArrayList";
    private static final String ARRAY_VALUE = "dev/arubik/craftengine/script/ScriptValue$Array";
    private static final String SCRIPT_CALL = "dev/arubik/craftengine/script/ScriptCall";
    private static final String OBJ_VALUE = "dev/arubik/craftengine/script/ScriptValue$Obj";
    private static final String POLY_CLASS = "dev/arubik/craftengine/script/PolyClass";
    private static final String POLY_TYPE_REGISTRY = "dev/arubik/craftengine/script/PolyTypeRegistry";
    private static final String POLY_TYPE = "dev/arubik/craftengine/script/PolyType";
    private static final String METHOD_HANDLER = "dev/arubik/craftengine/script/PolyType$MethodHandler";
    private static final String PROPERTY_HANDLER = "dev/arubik/craftengine/script/PolyType$PropertyHandler";
    /** {@link PolyDispatch}'s bootstraps. Every member access whose receiver type ISN'T known at
     *  compile time — a plain variable, or any chained hop — becomes an {@code invokedynamic}
     *  against one of these instead of a direct {@code ScriptFormula.memberCall}/{@code memberGet}.
     *  The call site then links itself into a guarded direct call on the receiver type it actually
     *  observes, so the registry lookup happens once per site rather than once per evaluation. See
     *  {@link PolyDispatch} for the guard, the invalidation, and the megamorphic cutoff. */
    private static final String POLY_DISPATCH = "dev/arubik/craftengine/script/PolyDispatch";
    private static final String BSM_DESC =
            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;"
                    + "Ljava/lang/String;)Ljava/lang/invoke/CallSite;";
    private static final Handle BSM_CALL =
            new Handle(H_INVOKESTATIC, POLY_DISPATCH, "bootstrapCall", BSM_DESC, false);
    private static final Handle BSM_GET =
            new Handle(H_INVOKESTATIC, POLY_DISPATCH, "bootstrapGet", BSM_DESC, false);

    /** How one arg of a PolyClass-dispatch call site is held in a local, per {@link
     *  #dotMethodCall}'s typed path. {@code NUM_RAW}/{@code BOOL_RAW}: the raw (pre-{@code toAny})
     *  arg expression is ALREADY exactly the native shape the generated method's parameter wants (a
     *  NUM expr into a {@code double} param, a BOOL expr into a {@code boolean} param) — kept as a
     *  genuinely unboxed JVM primitive, so no {@code ScriptValue.of(...)} box followed by an
     *  immediate {@code asNum}/{@code asBool} unbox ever happens. {@code ANY_BOXED}: everything else
     *  — a String/ScriptValue parameter, or a type mismatch (e.g. a STRING expr feeding a
     *  {@code double} param) — held as the boxed {@code ScriptValue} {@code toAny} already produces,
     *  decoded to native via {@link #emitDecodeToNative} on the way in. */
    private enum ArgSlotKind { NUM_RAW, BOOL_RAW, STR_RAW, ANY_BOXED }

    /** name -> java.lang.Math method of the same (double)->double shape. Only pure, total (no
     *  exceptions) single-argument math functions — everything else falls through to {@code
     *  callBuiltin} via the generic function-call path instead of bailing the whole formula. */
    private static final Map<String, String> UNARY_MATH = Map.ofEntries(
            Map.entry("sin", "sin"), Map.entry("cos", "cos"), Map.entry("tan", "tan"),
            Map.entry("asin", "asin"), Map.entry("acos", "acos"), Map.entry("atan", "atan"),
            Map.entry("abs", "abs"), Map.entry("sqrt", "sqrt"),
            Map.entry("floor", "floor"), Map.entry("ceil", "ceil"),
            Map.entry("sign", "signum"), Map.entry("exp", "exp"), Map.entry("log", "log"),
            Map.entry("log10", "log10"), Map.entry("deg", "toDegrees"), Map.entry("rad", "toRadians"));

    /** name -> java.lang.Math method of the (double,double)->double shape. */
    private static final Map<String, String> BINARY_MATH = Map.of(
            "min", "min", "max", "max", "atan2", "atan2", "pow", "pow");

    /** name -> java.lang.String instance method of the same (no-arg)->String shape — a
     *  DELIBERATE hand-ported duplicate of {@code ScriptBuiltins}' own one-line registrations
     *  ({@code register("upper", (args,ctx) -> ScriptValue.of(args.get(0).asStr().toUpperCase(...
     *  ))}), same trade-off {@link #UNARY_MATH} already accepts for {@code java.lang.Math}: these
     *  three are simple enough, and stable enough (they wrap a JDK method, not this codebase's own
     *  logic that could get extended later), to be worth a second, faster implementation here
     *  rather than routing every call through {@code ScriptFormula.callBuiltin}'s name-keyed
     *  registry lookup. Anything NOT in this set still falls through to {@code callBuiltin}
     *  exactly as before — this is a narrow, deliberately small allowlist, not an attempt to
     *  hand-port the whole builtin registry. */
    private static final Map<String, String> STRING_UNARY = Map.of(
            "upper", "toUpperCase", "lower", "toLowerCase", "trim", "trim");

    // ---- Entry point -----------------------------------------------------

    static ScriptFormula.Node tryCompile(String expr) {
        try {
            P p = new P(expr);
            Expr root = p.parseTernary();
            p.skipSpaces();
            if (root == null || p.pos != expr.length()) return null;
            // A bare literal needs no bytecode at all — see Literal's own doc for why generating
            // a whole hidden class here was pure waste. Falls to the interpreter, which handles a
            // literal with one trivial lambda anyway — identical cost, zero class overhead.
            if (root instanceof Literal) return null;

            // MUST be in the exact same package as this class — MethodHandles.lookup().
            // defineHiddenClass requires the generated class's package to match the lookup
            // class's package, or it throws IllegalArgumentException.
            String className = "dev/arubik/craftengine/script/CE$Gen" + COUNTER.incrementAndGet();
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            cw.visit(V21, ACC_FINAL | ACC_SUPER, className, null, "java/lang/Object",
                    new String[]{NODE_IFACE});

            MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            ctor.visitCode();
            ctor.visitVarInsn(ALOAD, 0);
            ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            ctor.visitInsn(RETURN);
            ctor.visitMaxs(0, 0);
            ctor.visitEnd();

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "eval", "(L" + CTX + ";)L" + VALUE + ";", null, null);
            mv.visitCode();
            Ctx c = new Ctx(1, 2); // ctx param at slot 1 (standalone eval(ScriptContext) classes), scratch from 2
            root.emit(mv, c);
            switch (root.type()) {
                case NUM  -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                case BOOL -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
                case ANY  -> { /* already a ScriptValue — return as-is, no re-boxing */ }
            }
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            cw.visitEnd();

            byte[] bytes = cw.toByteArray();
            Class<?> defined = MethodHandles.lookup().defineHiddenClass(bytes, true).lookupClass();
            ScriptFormula.Node node = (ScriptFormula.Node) defined.getDeclaredConstructor().newInstance();
            if (LOGGED_COMPILE.add(expr)) {
                LOG.log(Level.INFO, "[CEPolyfills] [JIT] registered class " + className
                        + " (hidden, in-JVM) for: " + expr);
            }
            return node;
        } catch (Throwable t) {
            if (LOGGED_FAIL.add(expr)) {
                LOG.log(Level.INFO, "[CEPolyfills] [JIT] bailed to the interpreter on '" + expr
                        + "' — " + t.getClass().getSimpleName()
                        + (t.getMessage() != null ? ": " + t.getMessage() : "")
                        + " (not a script failure by itself; the interpreter runs it instead)");
            }
            // Anything at all — an unsupported construct that slipped past a bail check, a real
            // bug in the generator, a verifier rejection — degrades to the interpreter, never a
            // broken script.
            return null;
        }
    }

    /** Parses {@code expr} for INLINE reuse by {@link ScriptClassCompiler} — same grammar, same
     *  bail rules as {@link #tryCompile}, but returns the parsed {@link Expr} tree directly
     *  instead of wrapping it in a standalone {@code eval(ScriptContext)} class. This is the
     *  actual fix for the "why does the compiled Windmill class still call {@code
     *  ScriptFormula.compile("0").evaluate(...)} for a bare literal" problem: {@code
     *  ScriptClassCompiler} calls this for every statement's embedded expression and, on success,
     *  emits the returned {@code Expr} straight into its OWN method body via {@link
     *  Expr#emit(MethodVisitor, Ctx)} — real inline bytecode (a literal becomes a bare {@code
     *  LDC}, a dot-access becomes a real {@code memberGet} call, arithmetic becomes real {@code
     *  DADD}/{@code DMUL}/...), with NO runtime {@code ScriptFormula.compile}/{@code evaluate}
     *  round-trip at all for anything this grammar covers. Returns {@code null} (caller falls back
     *  to the {@code ScriptFormula.compile(expr).evaluate(ctx)} pattern for just that one
     *  expression) for anything outside the grammar — {@code $var}, {@code ??}. The caller
     *  supplies its OWN {@link
     *  Ctx} (its own ctx-holding slot and scratch-slot allocator) rather than this class's
     *  fixed-slot-1 standalone convention — see {@link Ctx}'s own doc. */
    static Expr tryParse(String expr, LocalCallResolver resolver, VarTypeHint varHint) {
        try {
            P p = new P(expr, resolver, varHint);
            Expr root = p.parseTernary();
            p.skipSpaces();
            if (root == null || p.pos != expr.length()) return null;
            return root;
        } catch (Throwable ignored) {
            return null;
        }
    }

    // ---- Typed micro-AST + codegen ----------------------------------------

    enum Type { NUM, BOOL, ANY }

    /** Per-formula codegen state — a local-variable-slot allocator PLUS which slot holds the
     *  {@code ScriptContext} every emitted {@code getVar}/{@code getClassInstance} call reads
     *  from. When this class generates its own standalone {@code eval(ScriptContext)} classes,
     *  that's always slot 1 (0 is {@code this}) — but {@link ScriptClassCompiler} reuses this
     *  SAME expression codegen inline inside its own generated methods (a {@code static
     *  ScriptValue.Builder -> ScriptValue} method with no {@code this} at all), where the ctx has
     *  to be whatever slot IT freshly computed via {@code Builder.peek()} for that one statement —
     *  see that class's own doc for why a stale/cached ctx would be wrong there. Every dot-access/
     *  function-call/div-by-zero-guard needs its own scratch slot(s) for intermediate values,
     *  allocated fresh (and never reused) per compiled expression — expressions are short, so a
     *  little slot waste is a non-issue and far simpler than trying to free/reuse slots
     *  correctly. */
    static final class Ctx {
        final int ctxSlot;
        int next;
        Ctx(int ctxSlot, int firstScratchSlot) { this.ctxSlot = ctxSlot; this.next = firstScratchSlot; }
        int allocRef() { int s = next; next += 1; return s; }
        int allocD()   { int s = next; next += 2; return s; }
    }

    interface Expr {
        Type type();
        void emit(MethodVisitor mv, Ctx c);
    }

    abstract static class BaseExpr implements Expr {
        final Type type;
        BaseExpr(Type type) { this.type = type; }
        @Override public Type type() { return type; }
    }

    /** A bare literal (a numeric/boolean constant, no computation at all — {@code numLit}/{@code
     *  boolLit}'s own product). {@link #tryCompile} checks for this BEFORE doing any classfile
     *  work: a whole hidden JVM class generated just to push one constant is real, permanent
     *  metaspace overhead (the compiled Node lives forever in ScriptFormula's cache) for something
     *  the plain interpreter already does with a single trivial capturing lambda — every {@code
     *  .pf} file is FULL of bare-literal sub-expressions (array/range bounds, plain numeric
     *  constants used as-is), so this was generating one throwaway class per distinct literal
     *  value used ANYWHERE in the whole script set. */
    abstract static class Literal extends BaseExpr {
        Literal(Type type) { super(type); }
    }

    private static final String LOOKUP_DESC = "Ljava/lang/invoke/MethodHandles$Lookup;";

    private static Handle constBsm(String name, String argDesc) {
        return new Handle(H_INVOKESTATIC, VALUE, name,
                "(" + LOOKUP_DESC + "Ljava/lang/String;Ljava/lang/Class;" + argDesc + ")L" + VALUE + ";",
                true);
    }

    private static final Handle CONST_STR  = constBsm("constStr", "Ljava/lang/String;");
    private static final Handle CONST_NUM  = constBsm("constNum", "D");
    private static final Handle CONST_BOOL = constBsm("constBool", "I");

    /**
     * Pushes a literal already boxed as a {@link ScriptValue}, as a dynamic constant.
     *
     * <p>One LDC, resolved once by the JVM and cached in the constant pool, instead of LDC plus a
     * {@code ScriptValue.of} call that allocated a fresh record every time the line ran. See the
     * bootstraps' own comment in {@link ScriptValue} for why sharing the instance is safe.
     */
    static void emitConstValue(MethodVisitor mv, Object value) {
        String desc = "L" + VALUE + ";";
        ConstantDynamic c = switch (value) {
            case String s -> new ConstantDynamic("s", desc, CONST_STR, s);
            case Double d -> new ConstantDynamic("n", desc, CONST_NUM, d);
            case Boolean b -> new ConstantDynamic("b", desc, CONST_BOOL, b ? 1 : 0);
            default -> throw new IllegalArgumentException("not a literal: " + value);
        };
        mv.visitLdcInsn(c);
    }

    /** A string literal, which keeps its raw Java {@link String} rather than only knowing how to
     *  emit a boxed {@code ScriptValue}. That matters at a typed call site: a generated PolyClass
     *  method taking a native {@code String} parameter would otherwise be handed
     *  {@code ScriptValue.of("head_angle")} and immediately call {@code asStr()} back off it — a
     *  pure box-then-unbox round trip, on a pattern that is everywhere and hot
     *  ({@code Machine.get_typed("head_angle", "int")} runs per machine per tick). Holding the
     *  String lets {@link P#dotMethodCall} {@code LDC} it straight into the native parameter.
     *
     *  <p>Still a {@link Literal}, so {@link #tryCompile}'s "a bare literal needs no class at all"
     *  bail is unchanged. */
    /** A numeric literal. Named (rather than anonymous) so {@link #toAny} can recognise it and box
     *  it as a dynamic constant instead of emitting the primitive plus a {@code ScriptValue.of}. */
    static final class NumLiteral extends Literal {
        final double value;
        NumLiteral(double value) { super(Type.NUM); this.value = value; }
        @Override public void emit(MethodVisitor mv, Ctx c) { mv.visitLdcInsn(value); }
    }

    /** A boolean literal — see {@link NumLiteral}. */
    static final class BoolLiteral extends Literal {
        final boolean value;
        BoolLiteral(boolean value) { super(Type.BOOL); this.value = value; }
        @Override public void emit(MethodVisitor mv, Ctx c) { mv.visitInsn(value ? ICONST_1 : ICONST_0); }
    }

    static final class StrLiteral extends Literal {
        final String value;
        StrLiteral(String value) { super(Type.ANY); this.value = value; }
        @Override public void emit(MethodVisitor mv, Ctx c) { emitConstValue(mv, value); }
    }

    // ---- Coercions — mirror ScriptValue#asNum()/#asBool() exactly ----

    /**
     * A plain variable read, {@code ctx.getClassOrVar(name)}, kept as its own node so a coercion
     * applied to it can be FUSED instead of stacked.
     *
     * <p>Reading a variable to do arithmetic with it is one operation, and the generated code should
     * read that way: {@code double rpm = ctx.getNum("BASE_RPM")}, not a {@code ScriptValue} local
     * that exists solely to have {@code asNum()} called on it once. {@link #toNum}/{@link #toBool}
     * special-case this node into the fused {@code ScriptContext} accessor; anything else still
     * gets the ordinary boxed read.
     */
    static final class VarRead extends BaseExpr {
        final String name;
        VarRead(String name) { super(Type.ANY); this.name = name; }
        @Override public void emit(MethodVisitor mv, Ctx c) {
            mv.visitVarInsn(ALOAD, c.ctxSlot);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getClassOrVar", "(Ljava/lang/String;)L" + VALUE + ";", false);
        }
        /** Emits the fused accessor for one of {@code getNum}/{@code getBool}/{@code getStr}. */
        void emitFused(MethodVisitor mv, Ctx c, String accessor, String returnDesc) {
            mv.visitVarInsn(ALOAD, c.ctxSlot);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEVIRTUAL, CTX, accessor, "(Ljava/lang/String;)" + returnDesc, false);
        }
    }

    /**
     * {@code Name.prop} — the bare-identifier FIRST hop of a property read. Its declared type is
     * ANY, but when the property carries a scalar {@code propertyTyped} registration it can also
     * emit itself as a native {@code double}/{@code boolean}/{@code String}, which is what {@link
     * #toNum}/{@link #toBool}/{@code toStr} ask for. Same motivation as {@link VarRead}: a read that
     * feeds arithmetic should compile to {@code double rpm = m.rpm()}, not to a {@code ScriptValue}
     * local whose only use is one {@code asNum()}.
     *
     * <p>All three arms — null receiver, wrong receiver type, and the fast typed accessor — converge
     * on the native type, and the first two get there by the SAME {@code asNum}/{@code asBool}/{@code
     * asStr} the boxed form would have had applied to it. So the fused emission is equivalent to
     * {@code emit()} followed by that coercion, by construction rather than by assumption.
     */
    static final class PropRead extends BaseExpr {
        final String name, prop;
        private final String wrapperName, propJavaName;
        /** The native accessor, or null when this property has no scalar typed registration. */
        final PolyClassGenerator.TypedMemberRef nativeRef;
        /** The PolyType this read PRODUCES, when its codec says so — see {@link #polyTypeOf}. */
        final String resultPolyType;
        /** Non-null for a CHAINED hop, whose receiver is this expression rather than a name. */
        private final Expr base;
        /** The List-returning accessor for a list-typed property, or null. Only a `for` loop uses
         *  it — see {@link #emitAsList}. */
        final String listJavaName;

        /**
         * A chained hop {@code base.prop} whose receiver PolyType is known at compile time — because
         * {@code base} is itself a member read whose codec is {@code polyType(receiverType, …)}.
         * Compiles to the same guarded PolyClass dispatch a first hop gets, instead of the generic
         * {@code memberGet} that every chained hop used to fall back to.
         */
        static PropRead chained(Expr base, String receiverType, String prop) {
            PolyClassGenerator.GeneratedPolyClass g = PolyClassGenerator.getOrGenerate(receiverType);
            String propJavaName = g != null ? g.properties().get(prop) : null;
            return new PropRead(base, receiverType, prop,
                    propJavaName != null ? g.internalName() : null, propJavaName,
                    g != null ? g.typedProperties().get(prop) : null,
                    propertyResultPolyType(receiverType, prop),
                    g != null ? g.listProperties().get(prop) : null);
        }

        PropRead(String name, String prop, String wrapperName, String propJavaName,
                 PolyClassGenerator.TypedMemberRef nativeRef, String resultPolyType, String listJavaName) {
            this(null, name, prop, wrapperName, propJavaName, nativeRef, resultPolyType, listJavaName);
        }

        private PropRead(Expr base, String name, String prop, String wrapperName, String propJavaName,
                         PolyClassGenerator.TypedMemberRef nativeRef, String resultPolyType,
                         String listJavaName) {
            super(Type.ANY);
            this.base = base;
            this.listJavaName = listJavaName;
            this.name = name;
            this.prop = prop;
            this.wrapperName = wrapperName;
            this.propJavaName = propJavaName;
            this.nativeRef = nativeRef;
            this.resultPolyType = resultPolyType;
        }

        @Override public void emit(MethodVisitor mv, Ctx c) { emitAs(mv, c, null, null, null); }

        /** Emits the read with the native accessor {@link #nativeRef} names, coercing the two slow
         *  arms with {@code accessor} (one of {@code asNum}/{@code asBool}/{@code asStr}). */
        void emitFused(MethodVisitor mv, Ctx c, String accessor, String returnDesc) {
            emitAs(mv, c, nativeRef.javaName(), accessor, returnDesc);
        }

        /**
         * Leaves a {@code java.util.List} of the property's elements on the stack — the shape a
         * {@code for} loop actually consumes.
         *
         * <p>The slow arms use {@code ScriptProgram.elementsOf}, which is precisely what the loop
         * used to apply to this read's boxed result, so the three arms agree. The fast arm never
         * builds the {@link ScriptValue.Array} at all.
         */
        void emitAsList(MethodVisitor mv, Ctx c) {
            emitAs(mv, c, listJavaName, "elementsOf", "Ljava/util/List;");
        }

        private void emitAs(MethodVisitor mv, Ctx c, String javaName, String accessor, String returnDesc) {
            String desc = returnDesc != null ? returnDesc : "L" + VALUE + ";";
            int svSlot = c.allocRef();
            // A FIRST hop resolves a name and guards a NULL receiver, returning NULL for it. A
            // CHAINED hop evaluates its base expression and does NOT guard — that difference is the
            // interpreter's, not an oversight: parseSuffixChain calls memberGet unconditionally.
            Label isNullL = new Label(), endL = new Label();
            if (base == null) {
                P.emitResolveInstanceOrVar(mv, c, name, svSlot);
                mv.visitVarInsn(ALOAD, svSlot);
                P.emitGetNull(mv);
                mv.visitJumpInsn(IF_ACMPEQ, isNullL);
            } else {
                base.emit(mv, c);
                mv.visitVarInsn(ASTORE, svSlot);
            }

            if (propJavaName != null) {
                Label fallbackL = new Label(), fastL = new Label();
                // One call does the receiver check and the unboxing, and the local it lands in is
                // the PolyClass itself — `PolyClassMachine m = PolyClassMachine.ofGuarded(sv)` —
                // rather than a raw Object plus an inlined type-check chain. Null means "not this
                // type", which takes the generic path.
                int pcSlot = c.allocRef();
                mv.visitVarInsn(ALOAD, svSlot);
                mv.visitMethodInsn(INVOKESTATIC, wrapperName, "ofGuarded",
                        "(L" + VALUE + ";)L" + wrapperName + ";", false);
                mv.visitVarInsn(ASTORE, pcSlot);
                mv.visitVarInsn(ALOAD, pcSlot);
                mv.visitJumpInsn(IFNULL, fallbackL);
                mv.visitVarInsn(ALOAD, pcSlot);
                mv.visitMethodInsn(INVOKEVIRTUAL, wrapperName,
                        javaName != null ? javaName : propJavaName, "()" + desc, false);
                mv.visitJumpInsn(GOTO, fastL);
                mv.visitLabel(fallbackL);
                mv.visitVarInsn(ALOAD, svSlot);
                mv.visitVarInsn(ALOAD, c.ctxSlot);
                P.emitDynamicGet(mv, prop);
                emitCoerce(mv, accessor, desc);
                mv.visitLabel(fastL);
            } else {
                mv.visitVarInsn(ALOAD, svSlot);
                mv.visitVarInsn(ALOAD, c.ctxSlot);
                P.emitDynamicGet(mv, prop);
                emitCoerce(mv, accessor, desc);
            }

            if (base == null) {
                mv.visitJumpInsn(GOTO, endL);
                mv.visitLabel(isNullL);
                P.emitGetNull(mv);
                emitCoerce(mv, accessor, desc);
                mv.visitLabel(endL);
            }
        }

        /** Converts a boxed slow-arm result to whatever the fast arm returns. "elementsOf" is the
         *  one that is not a ScriptValue method — a list read converges on ScriptProgram.elementsOf,
         *  exactly the call the `for` loop used to make on this read's result. */
        private static void emitCoerce(MethodVisitor mv, String accessor, String desc) {
            if (accessor == null) return;
            if ("elementsOf".equals(accessor)) {
                mv.visitMethodInsn(INVOKESTATIC, "dev/arubik/craftengine/script/ScriptProgram",
                        "elementsOf", "(L" + VALUE + ";)Ljava/util/List;", false);
                return;
            }
            mv.visitMethodInsn(INVOKEINTERFACE, VALUE, accessor, "()" + desc, true);
        }
    }

    /**
     * A string concatenation whose parts are appended to one {@link StringBuilder} as native Java
     * Strings, with a single {@code ScriptValue.of} at the very end.
     *
     * <p>Equivalence with the nested {@code addPolymorphic} calls it replaces: once any operand is a
     * Str, {@code addPolymorphic} is {@code ScriptValue.of(lv.asStr() + rv.asStr())} and its result
     * is another Str, so the next {@code +} takes the same branch — the whole chain is
     * {@code asStr()} of each part, joined. Each part here is rendered by the SAME {@code asStr}
     * (fused to a native accessor where one exists, and via {@link ScriptFormula#numToStr} for a
     * statically-numeric part, which is that method's Num branch verbatim).
     *
     * <p>Parts are evaluated strictly left to right, exactly as the nested calls did — any side
     * effect in an operand keeps its order.
     */
    static final class StrConcat extends BaseExpr {
        private final List<Expr> parts;
        StrConcat(List<Expr> parts) { super(Type.ANY); this.parts = parts; }

        /** Flattens a nested concatenation so `a + b + c` builds ONE StringBuilder, not one per +. */
        static void flattenInto(Expr e, List<Expr> out) {
            if (e instanceof StrConcat sc) out.addAll(sc.parts);
            else out.add(e);
        }

        @Override public void emit(MethodVisitor mv, Ctx c) {
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            for (Expr part : parts) {
                emitPartAsString(mv, c, part);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                    "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Ljava/lang/String;)L" + VALUE + ";", true);
        }

        /** Leaves one native {@code String} on the stack — the part's {@code asStr()}, without
         *  boxing it into a ScriptValue wherever a fused accessor makes that possible. */
        private static void emitPartAsString(MethodVisitor mv, Ctx c, Expr part) {
            if (part instanceof StrLiteral lit) {
                mv.visitLdcInsn(lit.value);
                return;
            }
            if (part instanceof VarRead v) {
                v.emitFused(mv, c, "getStr", "Ljava/lang/String;");
                return;
            }
            if (part instanceof PropRead p && p.nativeRef != null
                    && p.nativeRef.retKind() == PolyClassGenerator.Kind.STRING) {
                p.emitFused(mv, c, "asStr", "Ljava/lang/String;");
                return;
            }
            if (part.type() == Type.NUM) {
                part.emit(mv, c);
                mv.visitMethodInsn(INVOKESTATIC, FORMULA, "numToStr", "(D)Ljava/lang/String;", false);
                return;
            }
            if (part.type() == Type.BOOL) {
                part.emit(mv, c);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                        "(Z)Ljava/lang/String;", false);
                return;
            }
            toAny(part).emit(mv, c);
            mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
        }
    }

    /**
     * The PolyType an expression is statically known to evaluate to, or null.
     *
     * <p>Only a member whose registered codec is {@code polyType(name, …)} qualifies — that codec is
     * a promise that the slot always holds one instance of exactly that type, which is precisely the
     * promise a compile-time specialization needs. Everything else (a RAW slot, a list, an untyped
     * member, a variable) returns null and keeps the generic path.
     *
     * <p>Being wrong here would still be SAFE — every specialized site guards with {@code ofGuarded},
     * which returns null for a receiver that is not that type and falls back — but it would be
     * pointless, so the promise is taken from the registration rather than guessed.
     */
    static String polyTypeOf(Expr e) {
        if (e instanceof PropRead p) return p.resultPolyType;
        if (e instanceof TypedResult t) return t.resultPolyType;
        return null;
    }

    /** An expression that evaluates exactly as {@code inner} does, but additionally carries the
     *  PolyType its value is known to be — the shape a method call needs, since its emission is
     *  built from a dozen captured locals and is not worth restructuring just to hold one field. */
    static final class TypedResult extends BaseExpr {
        private final Expr inner;
        final String resultPolyType;
        TypedResult(Expr inner, String resultPolyType) {
            super(Type.ANY);
            this.inner = inner;
            this.resultPolyType = resultPolyType;
        }
        @Override public void emit(MethodVisitor mv, Ctx c) { inner.emit(mv, c); }
    }

    /** The declared return codec of {@code type.prop}, resolved through the same registry the rest
     *  of this compiler consults at compile time. */
    static String methodResultPolyType(String typeName, String method) {
        PolyType t = typeName == null ? null : PolyTypeRegistry.get(typeName);
        if (t == null) return null;
        PolyType.TypedMethodDescriptor d = t.resolveTypedMethod(method);
        return d == null ? null : TypeCodecs.singlePolyTypeNameOf(d.returnType());
    }

    static String propertyResultPolyType(String typeName, String prop) {
        PolyType t = typeName == null ? null : PolyTypeRegistry.get(typeName);
        if (t == null) return null;
        PolyType.TypedPropertyDescriptor d = t.resolveTypedProperty(prop);
        return d == null ? null : TypeCodecs.singlePolyTypeNameOf(d.returnType());
    }

    static Expr toNum(Expr e) {
        if (e.type() == Type.NUM) return e;
        if (e instanceof PropRead p && p.nativeRef != null
                && p.nativeRef.retKind() == PolyClassGenerator.Kind.DOUBLE) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) { p.emitFused(mv, c, "asNum", "D"); }
            };
        }
        if (e instanceof VarRead v) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) { v.emitFused(mv, c, "getNum", "D"); }
            };
        }
        if (e.type() == Type.BOOL) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) { e.emit(mv, c); mv.visitInsn(I2D); }
            };
        }
        // ANY -> asNum(): identical to what the interpreter does the instant this value flows
        // into any unconditionally-numeric operator.
        return new BaseExpr(Type.NUM) {
            @Override public void emit(MethodVisitor mv, Ctx c) {
                e.emit(mv, c);
                mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asNum", "()D", true);
            }
        };
    }

    static Expr toBool(Expr e) {
        if (e.type() == Type.BOOL) return e;
        if (e instanceof PropRead p && p.nativeRef != null
                && p.nativeRef.retKind() == PolyClassGenerator.Kind.BOOL) {
            return new BaseExpr(Type.BOOL) {
                @Override public void emit(MethodVisitor mv, Ctx c) { p.emitFused(mv, c, "asBool", "Z"); }
            };
        }
        if (e instanceof VarRead v) {
            return new BaseExpr(Type.BOOL) {
                @Override public void emit(MethodVisitor mv, Ctx c) { v.emitFused(mv, c, "getBool", "Z"); }
            };
        }
        if (e.type() == Type.NUM) {
            return new BaseExpr(Type.BOOL) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    e.emit(mv, c);
                    mv.visitInsn(DCONST_0);
                    mv.visitInsn(DCMPL);
                    Label trueL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IFNE, trueL);
                    mv.visitInsn(ICONST_0);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(trueL);
                    mv.visitInsn(ICONST_1);
                    mv.visitLabel(endL);
                }
            };
        }
        return new BaseExpr(Type.BOOL) {
            @Override public void emit(MethodVisitor mv, Ctx c) {
                e.emit(mv, c);
                mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asBool", "()Z", true);
            }
        };
    }

    static Expr toAny(Expr e) {
        if (e.type() == Type.ANY) return e;
        // A literal that has to be boxed is a compile-time constant all the way through: emit the
        // boxed form as a dynamic constant rather than the primitive plus a ScriptValue.of call.
        Object literal = e instanceof NumLiteral n ? (Object) n.value
                : e instanceof BoolLiteral b ? (Object) b.value : null;
        if (literal != null) {
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) { emitConstValue(mv, literal); }
            };
        }
        if (e.type() == Type.NUM) {
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    e.emit(mv, c);
                    mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                }
            };
        }
        return new BaseExpr(Type.ANY) {
            @Override public void emit(MethodVisitor mv, Ctx c) {
                e.emit(mv, c);
                mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
            }
        };
    }

    /** Unifies two branches (ternary) to a common type: equal types stay as-is (keeps the
     *  primitive-stack fast path for the common all-NUM/all-BOOL case); otherwise both widen to
     *  ANY, which any type can represent losslessly. Bytecode verification requires both control
     *  paths into the merge point to agree on stack shape, so this MUST happen before emitting. */
    private static Type unify(Type a, Type b) { return a == b ? a : Type.ANY; }

    // ---- Recursive-descent parser — precedence mirrors ScriptFormula.Parser's REAL chain for
    // every level both recognize: ternary > || > && > ! > compare > + - > * / % > unary- >
    // primary (bitwise/shift, null-coalesce, and **//^  are omitted layers — see class doc for
    // why that's always safe). ----------------------------------------------

    static final class P {
        final String src;
        int pos;
        final LocalCallResolver resolver;
        final VarTypeHint varHint;
        P(String src) { this(src, null, null); }
        P(String src, LocalCallResolver resolver, VarTypeHint varHint) {
            this.src = src; this.pos = 0; this.resolver = resolver; this.varHint = varHint;
        }

        void skipSpaces() { while (pos < src.length() && src.charAt(pos) == ' ') pos++; }

        boolean matchAt(String tok) { return src.startsWith(tok, pos); }

        boolean match(String tok) {
            skipSpaces();
            if (matchAt(tok)) { pos += tok.length(); return true; }
            return false;
        }

        Expr parseTernary() {
            Expr cond = parseOr();
            if (cond == null) return null;
            skipSpaces();
            if (matchAt("??")) return null; // null-coalesce unsupported — bail
            if (!match("?")) return cond;
            Expr whenTrue = parseTernary();
            if (whenTrue == null || !match(":")) return null;
            Expr whenFalse = parseTernary();
            if (whenFalse == null) return null;
            Expr condB = toBool(cond);
            Type resultType = unify(whenTrue.type(), whenFalse.type());
            Expr t = resultType == whenTrue.type() ? whenTrue : toAny(whenTrue);
            Expr f = resultType == whenFalse.type() ? whenFalse : toAny(whenFalse);
            return new BaseExpr(resultType) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    condB.emit(mv, c);
                    Label elseL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IFEQ, elseL);
                    t.emit(mv, c);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(elseL);
                    f.emit(mv, c);
                    mv.visitLabel(endL);
                }
            };
        }

        Expr parseOr() {
            Expr left = parseAnd();
            if (left == null) return null;
            while (true) {
                int save = pos;
                if (!match("||")) { pos = save; break; }
                Expr right = parseAnd();
                if (right == null) return null;
                Expr l = toBool(left), r = toBool(right);
                left = new BaseExpr(Type.BOOL) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        Label trueL = new Label(), endL = new Label();
                        l.emit(mv, c);
                        mv.visitJumpInsn(IFNE, trueL);
                        r.emit(mv, c);
                        mv.visitJumpInsn(IFNE, trueL);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endL);
                        mv.visitLabel(trueL);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endL);
                    }
                };
            }
            return left;
        }

        Expr parseAnd() {
            Expr left = parseNot();
            if (left == null) return null;
            while (true) {
                int save = pos;
                if (!match("&&")) { pos = save; break; }
                Expr right = parseNot();
                if (right == null) return null;
                Expr l = toBool(left), r = toBool(right);
                left = new BaseExpr(Type.BOOL) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        Label falseL = new Label(), endL = new Label();
                        l.emit(mv, c);
                        mv.visitJumpInsn(IFEQ, falseL);
                        r.emit(mv, c);
                        mv.visitJumpInsn(IFEQ, falseL);
                        mv.visitInsn(ICONST_1);
                        mv.visitJumpInsn(GOTO, endL);
                        mv.visitLabel(falseL);
                        mv.visitInsn(ICONST_0);
                        mv.visitLabel(endL);
                    }
                };
            }
            return left;
        }

        /** {@code '!' parseNot | parseBitwise} — matches ScriptFormula.Parser#parseNot exactly
         *  (right-recursive so {@code !!x} works), sitting ABOVE bitwise/compare, not near primary:
         *  real grammar precedence means {@code !a == b} parses as {@code !(a == b)}, not
         *  {@code (!a) == b}. */
        Expr parseNot() {
            if (match("!")) {
                Expr inner = parseNot();
                if (inner == null) return null;
                Expr e = toBool(inner);
                return new BaseExpr(Type.BOOL) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        e.emit(mv, c);
                        mv.visitInsn(ICONST_1);
                        mv.visitInsn(IXOR);
                    }
                };
            }
            return parseBitwise();
        }

        /** {@code <<}/{@code >>}/{@code &}/{@code |} — matches ScriptFormula.Parser#parseBitwise
         *  exactly: left-associative chain, operand at each step is {@link #parseCompare}, so
         *  (perhaps counterintuitively) a bitwise op binds LOOSER than a comparison right below it
         *  in the chain — {@code a < b & c} parses as {@code (a < b) & c}, matching the real
         *  parser's own precedence rather than the "usual" C-family ordering. Every operand is
         *  coerced to a real JVM {@code long} (matching the interpreter's own {@code (long)
         *  asNum()} truncation) before the bitwise op, then back to {@code double} for the result —
         *  shift COUNTS additionally narrow {@code long -> int} first, since the JVM's own
         *  {@code lshl}/{@code lshr} opcodes require an int shift distance on the stack (this is
         *  exactly what {@code javac} itself emits for a Java {@code long << long} expression). */
        Expr parseBitwise() {
            Expr left = parseCompare();
            if (left == null) return null;
            while (true) {
                skipSpaces();
                String op;
                if (matchAt("<<") && (pos + 2 >= src.length() || src.charAt(pos + 2) != '<')) {
                    op = "<<"; pos += 2;
                } else if (matchAt(">>") && (pos + 2 >= src.length() || src.charAt(pos + 2) != '>')) {
                    op = ">>"; pos += 2;
                } else if (matchAt("&") && (pos + 1 >= src.length() || src.charAt(pos + 1) != '&')) {
                    op = "&"; pos += 1;
                } else if (matchAt("|") && (pos + 1 >= src.length() || src.charAt(pos + 1) != '|')) {
                    op = "|"; pos += 1;
                } else break;
                Expr right = parseCompare();
                if (right == null) return null;
                left = bitOp(left, right, op);
            }
            return left;
        }

        private static Expr bitOp(Expr leftD, Expr rightD, String op) {
            Expr l = toNum(leftD), r = toNum(rightD);
            boolean isShift = op.equals("<<") || op.equals(">>");
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    l.emit(mv, c);
                    mv.visitInsn(D2L);
                    r.emit(mv, c);
                    mv.visitInsn(D2L);
                    if (isShift) mv.visitInsn(L2I);
                    mv.visitInsn(switch (op) {
                        case "<<" -> LSHL;
                        case ">>" -> LSHR;
                        case "&"  -> LAND;
                        default   -> LOR; // "|"
                    });
                    mv.visitInsn(L2D);
                }
            };
        }

        /** Exactly ONE optional relational operator — chained compares aren't part of the real
         *  grammar either (ScriptFormula.Parser#parseCompare never loops).
         *
         *  <p>{@code ==}/{@code !=} route through {@link ScriptFormula#valuesEqual} whenever
         *  either operand is ANY-typed — real Null/Str-aware equality, not eagerly coerced to
         *  double, matching the interpreter exactly (see that method's own doc) instead of
         *  bailing the whole formula the way this used to. When NEITHER operand is ANY, the value
         *  can never actually BE a Null/Str at runtime, so the plain numeric fast path below stays
         *  available — same as {@code >}/{@code <}/{@code >=}/{@code <=}, which are
         *  unconditionally numeric in the interpreter regardless of operand type. */
        Expr parseCompare() {
            Expr left = parseAdd();
            if (left == null) return null;
            skipSpaces();
            String op;
            if      (matchAt(">=")) { op = ">=";  pos += 2; }
            else if (matchAt("<=")) { op = "<=";  pos += 2; }
            else if (matchAt("==")) { op = "==";  pos += 2; }
            else if (matchAt("!=")) { op = "!=";  pos += 2; }
            else if (matchAt(">"))  { op = ">";   pos += 1; }
            else if (matchAt("<"))  { op = "<";   pos += 1; }
            else return left;
            Expr right = parseAdd();
            if (right == null) return null;

            boolean equality = op.equals("==") || op.equals("!=");
            if (equality && (left.type() == Type.ANY || right.type() == Type.ANY)) {
                boolean negate = op.equals("!=");

                // Comparing against a string LITERAL — `id == "minecraft:warped_stem"`, which runs
                // per block per tick — does not need a ScriptValue allocated for the constant just
                // to hand it to valuesEqual. valuesEqualStr is exactly equivalent for this case (a
                // literal is always a Str, never Null, so only valuesEqual's Str branch can apply)
                // and takes the raw Java String, so the constant lives in the constant pool.
                StrLiteral lit = left instanceof StrLiteral sl ? sl
                        : right instanceof StrLiteral sr ? sr : null;
                if (lit != null) {
                    Expr otherRaw = left instanceof StrLiteral ? right : left;

                    // A plain variable compared to a string literal — `id == "minecraft:oak_log"`,
                    // which tree_utils' _is_log does seventeen times in one expression — is just a
                    // String comparison. Read it as a String and use String.equals: no ScriptValue
                    // for the variable, and no ScriptFormula call at all.
                    //
                    // Excludes the literal "null", where the two genuinely differ: valuesEqualStr
                    // reports false for a NULL receiver, while asStr() renders it as the string
                    // "null" and would report true.
                    // Same for a STRING-typed property read — `Block.id == "minecraft:oak_log"`.
                    // Its own null-receiver arm renders NULL via asStr() exactly as the boxed form
                    // would, so the "null" literal is excluded here for the same reason.
                    if (otherRaw instanceof PropRead p && p.nativeRef != null
                            && p.nativeRef.retKind() == PolyClassGenerator.Kind.STRING
                            && !"null".equals(lit.value)) {
                        return new BaseExpr(Type.BOOL) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                p.emitFused(mv, c, "asStr", "Ljava/lang/String;");
                                mv.visitLdcInsn(lit.value);
                                mv.visitMethodInsn(INVOKESTATIC, "java/util/Objects", "equals",
                                        "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
                                if (negate) {
                                    mv.visitInsn(ICONST_1);
                                    mv.visitInsn(IXOR);
                                }
                            }
                        };
                    }

                    if (otherRaw instanceof VarRead v && !"null".equals(lit.value)) {
                        return new BaseExpr(Type.BOOL) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                v.emitFused(mv, c, "getStr", "Ljava/lang/String;");
                                mv.visitLdcInsn(lit.value);
                                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals",
                                        "(Ljava/lang/Object;)Z", false);
                                if (negate) {
                                    mv.visitInsn(ICONST_1);
                                    mv.visitInsn(IXOR);
                                }
                            }
                        };
                    }

                    Expr other = toAny(otherRaw);
                    return new BaseExpr(Type.BOOL) {
                        @Override public void emit(MethodVisitor mv, Ctx c) {
                            other.emit(mv, c);
                            mv.visitLdcInsn(lit.value);
                            mv.visitMethodInsn(INVOKESTATIC, FORMULA, "valuesEqualStr",
                                    "(L" + VALUE + ";Ljava/lang/String;)Z", false);
                            if (negate) {
                                mv.visitInsn(ICONST_1);
                                mv.visitInsn(IXOR);
                            }
                        }
                    };
                }

                Expr l = toAny(left), r = toAny(right);
                return new BaseExpr(Type.BOOL) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        l.emit(mv, c);
                        r.emit(mv, c);
                        mv.visitMethodInsn(INVOKESTATIC, FORMULA, "valuesEqual",
                                "(L" + VALUE + ";L" + VALUE + ";)Z", false);
                        if (negate) {
                            mv.visitInsn(ICONST_1);
                            mv.visitInsn(IXOR);
                        }
                    }
                };
            }

            Expr l = toNum(left), r = toNum(right);
            return new BaseExpr(Type.BOOL) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    l.emit(mv, c);
                    r.emit(mv, c);
                    // Matches javac's own NaN-safe convention: DCMPG for < / <=, DCMPL for
                    // everything else, so a NaN operand makes every comparison false, exactly
                    // like the interpreter's plain `dl > dr` etc. on doubles.
                    boolean useG = op.equals("<") || op.equals("<=");
                    mv.visitInsn(useG ? DCMPG : DCMPL);
                    int jumpOp = switch (op) {
                        case ">"  -> IFGT;
                        case ">=" -> IFGE;
                        case "<"  -> IFLT;
                        case "<=" -> IFLE;
                        case "==" -> IFEQ;
                        default   -> IFNE; // "!="
                    };
                    Label trueL = new Label(), endL = new Label();
                    mv.visitJumpInsn(jumpOp, trueL);
                    mv.visitInsn(ICONST_0);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(trueL);
                    mv.visitInsn(ICONST_1);
                    mv.visitLabel(endL);
                }
            };
        }

        /** {@code +} routes through {@link ScriptFormula#addPolymorphic} whenever either operand
         *  is ANY-typed — real string-concat-or-numeric-addition semantics, matching the
         *  interpreter exactly, instead of bailing the whole formula the way this used to. When
         *  NEITHER operand is ANY, the fast all-NUM path (plain {@code DADD}) stays available, same
         *  reasoning as {@link #parseCompare}'s own ANY-vs-not split. {@code -} is unconditionally
         *  numeric in the interpreter, safe with ANY via {@link #toNum} either way. */
        Expr parseAdd() {
            Expr left = parseMul();
            if (left == null) return null;
            while (true) {
                skipSpaces();
                char c = pos < src.length() ? src.charAt(pos) : 0;
                if (c != '+' && c != '-') break;
                pos++;
                Expr right = parseMul();
                if (right == null) return null;
                if (c == '+') {
                    left = addExpr(left, right);
                } else {
                    Expr l = toNum(left), r = toNum(right);
                    left = new BaseExpr(Type.NUM) {
                        @Override public void emit(MethodVisitor mv, Ctx c2) { l.emit(mv, c2); r.emit(mv, c2); mv.visitInsn(DSUB); }
                    };
                }
            }
            return left;
        }

        /**
         * Is this expression's value certain to be a {@link ScriptValue.Str}? That is what makes
         * {@code addPolymorphic} statically decidable: its very first test is {@code lv instanceof
         * Str || rv instanceof Str}, so ONE known-Str operand fixes the whole operation as string
         * concatenation. Only shapes that can never produce anything else count — a literal, a
         * concatenation (Java {@code +} on Strings never yields null, so {@code ScriptValue.of}
         * always gives a Str), or a property whose registered codec is {@link TypeCodecs#STRING}.
         *
         * <p>A STRING codec CAN encode Java null to {@code NULL}, which is not a Str — so that case
         * is excluded by having {@link StrConcat} render its parts with {@code asStr()}, which is
         * exactly what {@code addPolymorphic} would have done to the same value.
         */
        private static boolean isKnownStr(Expr e) {
            return e instanceof StrLiteral || e instanceof StrConcat
                    || (e instanceof PropRead p && p.nativeRef != null
                        && p.nativeRef.retKind() == PolyClassGenerator.Kind.STRING);
        }

        private static Expr addExpr(Expr left, Expr right) {
            // `"prefix" + gid + ":" + x` is a chain of left-associative adds, every one of them
            // statically a concatenation. Compiling each as addPolymorphic means one ScriptValue for
            // every literal, every operand, and every intermediate result — for a string that only
            // ever becomes a key. One StringBuilder over the flattened parts is the same string with
            // none of that.
            if (isKnownStr(left) || isKnownStr(right)) {
                List<Expr> parts = new ArrayList<>();
                StrConcat.flattenInto(left, parts);
                StrConcat.flattenInto(right, parts);
                return new StrConcat(parts);
            }
            if (left.type() != Type.ANY && right.type() != Type.ANY) {
                Expr l = toNum(left), r = toNum(right);
                return new BaseExpr(Type.NUM) {
                    @Override public void emit(MethodVisitor mv, Ctx c) { l.emit(mv, c); r.emit(mv, c); mv.visitInsn(DADD); }
                };
            }
            Expr l = toAny(left), r = toAny(right);
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    l.emit(mv, c);
                    r.emit(mv, c);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "addPolymorphic",
                            "(L" + VALUE + ";L" + VALUE + ";)L" + VALUE + ";", false);
                }
            };
        }

        /** {@code *} is a plain DMUL. {@code /} and {@code %} replicate the interpreter's
         *  divide-by-zero guard EXACTLY (returns {@code 0.0} instead of the raw IEEE
         *  Infinity/NaN a bare DDIV/DREM would produce) — see {@link #divOrMod}. {@code **} (power)
         *  and {@code //} (floor division, same zero-guard as {@code /} plus a floor) sit at this
         *  SAME precedence tier in the real grammar (both checked inside {@code
         *  ScriptFormula.Parser#parseMul}'s own loop, operand from {@code parsePow} same as every
         *  other operator here) — {@code **} is NOT the same layer as the separate {@code ^} power
         *  operator {@link #parsePow} handles; the real parser has both spellings, at two different
         *  precedence tiers, and this mirrors that exactly rather than unifying them. */
        Expr parseMul() {
            Expr left = parsePow();
            if (left == null) return null;
            while (true) {
                skipSpaces();
                if (matchAt("**")) {
                    pos += 2;
                    Expr right = parsePow();
                    if (right == null) return null;
                    Expr l = toNum(left), r = toNum(right);
                    left = new BaseExpr(Type.NUM) {
                        @Override public void emit(MethodVisitor mv, Ctx c) {
                            l.emit(mv, c); r.emit(mv, c);
                            mv.visitMethodInsn(INVOKESTATIC, MATH, "pow", "(DD)D", false);
                        }
                    };
                    continue;
                }
                if (matchAt("//")) {
                    pos += 2;
                    Expr right = parsePow();
                    if (right == null) return null;
                    left = floorDiv(toNum(left), toNum(right));
                    continue;
                }
                char c = pos < src.length() ? src.charAt(pos) : 0;
                if (c != '*' && c != '/' && c != '%') break;
                pos++;
                Expr right = parsePow();
                if (right == null) return null;
                Expr l = toNum(left), r = toNum(right);
                if (c == '*') {
                    left = new BaseExpr(Type.NUM) {
                        @Override public void emit(MethodVisitor mv, Ctx c2) { l.emit(mv, c2); r.emit(mv, c2); mv.visitInsn(DMUL); }
                    };
                } else {
                    left = divOrMod(l, r, c == '/');
                }
            }
            return left;
        }

        /** {@code d = right; result = (d == 0.0) ? 0.0 : (left OP d)} — right (the divisor) is
         *  evaluated exactly once and stashed in a scratch double-slot, matching the interpreter's
         *  own "evaluate the divisor first, short-circuit before ever evaluating the dividend"
         *  order (real side-effecting sub-expressions on the left are skipped entirely on a
         *  zero divisor, same as {@code ScriptFormula.Parser#parseMul}). */
        private static Expr divOrMod(Expr left, Expr right, boolean isDiv) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int rSlot = c.allocD();
                    right.emit(mv, c);
                    mv.visitInsn(DUP2);
                    mv.visitVarInsn(DSTORE, rSlot);
                    mv.visitInsn(DCONST_0);
                    mv.visitInsn(DCMPL);
                    Label nonZeroL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IFNE, nonZeroL);
                    mv.visitInsn(DCONST_0);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(nonZeroL);
                    left.emit(mv, c);
                    mv.visitVarInsn(DLOAD, rSlot);
                    mv.visitInsn(isDiv ? DDIV : DREM);
                    mv.visitLabel(endL);
                }
            };
        }

        /** {@code //} — same zero-divisor guard as {@link #divOrMod}, plus {@code Math.floor} on
         *  the quotient, matching {@code ScriptFormula.Parser#parseMul}'s own {@code "//"} case
         *  exactly ({@code d == 0.0 ? 0.0 : Math.floor(left / d)}). */
        private static Expr floorDiv(Expr left, Expr right) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int rSlot = c.allocD();
                    right.emit(mv, c);
                    mv.visitInsn(DUP2);
                    mv.visitVarInsn(DSTORE, rSlot);
                    mv.visitInsn(DCONST_0);
                    mv.visitInsn(DCMPL);
                    Label nonZeroL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IFNE, nonZeroL);
                    mv.visitInsn(DCONST_0);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(nonZeroL);
                    left.emit(mv, c);
                    mv.visitVarInsn(DLOAD, rSlot);
                    mv.visitInsn(DDIV);
                    mv.visitMethodInsn(INVOKESTATIC, MATH, "floor", "(D)D", false);
                    mv.visitLabel(endL);
                }
            };
        }

        /** {@code base ('^' parsePow)?} — right-associative power operator, matching {@code
         *  ScriptFormula.Parser#parsePow} exactly. A DIFFERENT spelling of exponentiation from
         *  {@code **} (see {@link #parseMul}'s own doc for why both exist at different precedence
         *  tiers in the real grammar rather than being unified here). */
        Expr parsePow() {
            Expr base = parseUnary();
            if (base == null) return null;
            skipSpaces();
            if (matchAt("^")) {
                pos++;
                Expr exp = parsePow();
                if (exp == null) return null;
                Expr b = toNum(base), e = toNum(exp);
                return new BaseExpr(Type.NUM) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        b.emit(mv, c); e.emit(mv, c);
                        mv.visitMethodInsn(INVOKESTATIC, MATH, "pow", "(DD)D", false);
                    }
                };
            }
            return base;
        }

        Expr parseUnary() {
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == '-') {
                pos++;
                Expr e0 = parseUnary();
                if (e0 == null) return null;
                Expr e = toNum(e0);
                return new BaseExpr(Type.NUM) {
                    @Override public void emit(MethodVisitor mv, Ctx c) { e.emit(mv, c); mv.visitInsn(DNEG); }
                };
            }
            return parsePrimary();
        }

        /** {@code primary} followed by zero or more {@code [index]}/{@code .member}/{@code
         *  .method(args)} suffixes — mirrors {@code ScriptFormula.Parser#parseSuffixChain} in
         *  full now: a bare-identifier primary's OWN {@code Name.member}/{@code Name.method(args)}
         *  handling (in {@link #parsePrimaryCore}) still does the classInstance-then-var
         *  resolution and the interpreter's real null-guard for that FIRST hop (see that branch's
         *  own doc for why), but every hop AFTER that — {@code foo().bar()}, {@code warp_row(name)
         *  .get("id")}, {@code a[0].b} — chains here via {@link #chainedPropertyGet}/{@link
         *  #chainedMethodCall}, which do NOT null-guard, exactly matching {@code
         *  parseSuffixChain}'s own real behavior (it calls {@code memberGet}/{@code memberCall}
         *  unconditionally on whatever the base evaluated to, null or not). */
        Expr parsePrimary() {
            Expr base = parsePrimaryCore();
            if (base == null) return null;
            while (true) {
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == '[') {
                    pos++;
                    Expr idx = parseTernary();
                    if (idx == null) return null;
                    skipSpaces();
                    if (pos >= src.length() || src.charAt(pos) != ']') return null;
                    pos++;
                    base = subscriptExpr(base, idx);
                    continue;
                }
                if (pos < src.length() && src.charAt(pos) == '.') {
                    int save = pos;
                    pos++;
                    skipSpaces();
                    int mStart = pos;
                    while (pos < src.length() && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    if (pos == mStart) { pos = save; break; } // not actually a member access — leave it unconsumed
                    String member = src.substring(mStart, pos);
                    skipSpaces();
                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++;
                        List<Expr> args = parseArgList();
                        if (args == null) return null;
                        base = chainedMethodCall(base, member, args);
                    } else {
                        base = chainedPropertyGet(base, member);
                    }
                    continue;
                }
                break;
            }
            return base;
        }

        /** {@code base.prop} where {@code base} is an ALREADY-RESOLVED value (not a name to look
         *  up) — the generic suffix-chain hop, unlike {@link #dotPropertyGet}'s bare-identifier
         *  FIRST hop. No null-guard: matches {@code ScriptFormula.Parser#parseSuffixChain}'s own
         *  {@code base = ctx -> memberGet(obj.eval(ctx), p, ctx);} exactly — it calls {@code
         *  memberGet} unconditionally, null receiver or not (that's how a chain like {@code
         *  Machine.contraption.blah} already behaved even before this compiler existed). */
        private static Expr chainedPropertyGet(Expr base0, String prop) {
            // When the base's own registration promises one PolyType, this hop is no longer
            // "arbitrary expression, must dispatch generically" — it is exactly a first hop with a
            // different way of producing the receiver.
            String receiverType = polyTypeOf(base0);
            if (receiverType != null) return PropRead.chained(toAny(base0), receiverType, prop);
            Expr base = toAny(base0);
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    base.emit(mv, c);
                    mv.visitVarInsn(ALOAD, c.ctxSlot);
                    emitDynamicGet(mv, prop);
                }
            };
        }

        /** {@code base.method(args)} — chained hop counterpart to {@link #chainedPropertyGet}, via
         *  {@code memberCall} with an evaluated arg list. No compile-time {@link PolyType}
         *  specialization here (unlike {@link #dotMethodCall}) — {@code base} is an arbitrary
         *  expression, not a name {@code PolyTypeRegistry} can be consulted by; that specialization
         *  is deliberately scoped to the bare-identifier FIRST hop only. */
        private static Expr chainedMethodCall(Expr base0, String method, List<Expr> rawArgs) {
            String receiverType = polyTypeOf(base0);
            if (receiverType != null) {
                return dotMethodCall(toAny(base0), receiverType, method, rawArgs);
            }
            Expr base = toAny(base0);
            List<Expr> args = rawArgs.stream().map(ScriptBytecodeCompiler::toAny).toList();
            boolean nativeArgs = args.size() <= MAX_NATIVE_ARGS;
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    if (nativeArgs) {
                        // Receiver first, then arguments — the order the interpreter itself uses
                        // (memberCall(obj.eval(ctx), m, argNodes...) evaluates its own arguments
                        // left to right). Building the list first, as this used to, evaluated the
                        // arguments BEFORE the receiver.
                        base.emit(mv, c);
                        for (Expr a : args) a.emit(mv, c);
                        mv.visitVarInsn(ALOAD, c.ctxSlot);
                        emitDynamicCallN(mv, method, args.size());
                        return;
                    }
                    int listSlot = c.allocRef();
                    base.emit(mv, c);
                    emitBuildArgsList(mv, c, args, listSlot);
                    mv.visitVarInsn(ALOAD, listSlot);
                    mv.visitVarInsn(ALOAD, c.ctxSlot);
                    emitDynamicCall(mv, method);
                }
            };
        }

        Expr parsePrimaryCore() {
            skipSpaces();
            if (pos >= src.length()) return null;
            char c = src.charAt(pos);

            // Array literal: [elem, elem, ...] — mirrors ScriptFormula.Parser#parsePrimary's own
            // '[' branch exactly (each element is a full expression, comma-separated).
            if (c == '[') {
                pos++;
                List<Expr> elems = new ArrayList<>();
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ']') {
                    pos++;
                } else {
                    while (true) {
                        Expr e = parseTernary();
                        if (e == null) return null;
                        elems.add(toAny(e));
                        skipSpaces();
                        if (pos < src.length() && src.charAt(pos) == ',') { pos++; continue; }
                        if (pos < src.length() && src.charAt(pos) == ']') { pos++; break; }
                        return null;
                    }
                }
                return arrayLit(elems);
            }

            if (c == '(') {
                pos++;
                Expr inner = parseTernary();
                if (inner == null || !match(")")) return null;
                return inner;
            }

            // String literal — mirrors ScriptFormula.Parser's own string-literal branch exactly:
            // both ' and " as quote chars, \n \t \r \\ as recognized escapes, any OTHER escaped
            // char passes through as itself (so "\x" -> "x", not a bail). A [index] subscript
            // suffix after it ("abc"[0]) IS supported — see parsePrimary's own wrapper — but a
            // .method(...)/.property chain straight off a literal is NOT: this compiler's dot-
            // access grammar only ever recognizes Name.member on a bare IDENTIFIER primary (see
            // the identifier branch below), so "abc".upper() still bails to the interpreter.
            if (c == '"' || c == '\'') {
                char quote = c;
                pos++;
                StringBuilder sb = new StringBuilder();
                while (pos < src.length() && src.charAt(pos) != quote) {
                    char ch = src.charAt(pos);
                    if (ch == '\\' && pos + 1 < src.length()) {
                        pos++;
                        char esc = src.charAt(pos);
                        sb.append(switch (esc) {
                            case 'n' -> '\n';
                            case 't' -> '\t';
                            case 'r' -> '\r';
                            case '\\' -> '\\';
                            default -> esc;
                        });
                    } else {
                        sb.append(ch);
                    }
                    pos++;
                }
                if (pos >= src.length()) return null; // unterminated string literal — bail
                pos++; // consume closing quote
                return strLit(sb.toString());
            }

            if (Character.isDigit(c) || (c == '.' && pos + 1 < src.length() && Character.isDigit(src.charAt(pos + 1)))) {
                int start = pos;
                while (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) pos++;
                if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
                    pos++;
                    if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
                    while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                }
                String token = src.substring(start, pos);
                double v;
                try { v = Double.parseDouble(token); } catch (NumberFormatException e) { return null; }
                return numLit(v);
            }

            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < src.length() && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                String name = src.substring(start, pos);
                skipSpaces();

                // Cross-file "file.pf:func[:arg1:arg2...]" reference — mirrors ScriptFormula
                // .Parser's own ".pf:" branch exactly: same narrow arg-tail char class (identifier
                // chars, ':', '.', '-' — stops at whitespace/parens/operators), same whole-ref
                // string handed to ScriptCall.parse/evaluate rather than re-derived here. See
                // crossFileCall's own doc for the runtime behavior.
                if (src.startsWith(".pf:", pos)) {
                    int refStart = start;
                    int p = pos + 4; // past ".pf:"
                    while (p < src.length() && isScriptCallArgChar(src.charAt(p))) p++;
                    String ref = src.substring(refStart, p);
                    pos = p;
                    skipSpaces();
                    return crossFileCall(ref);
                }

                // Name.property / Name.method(args) — mirrors ScriptFormula.Parser's dot-access:
                // try as a class instance first, fall back to a plain variable, same as the
                // interpreter (see its dot-access branch in parsePrimary). Result type is ANY —
                // this is exactly the path a Map/array/Item-valued property comes through, and it
                // must round-trip untouched.
                if (pos < src.length() && src.charAt(pos) == '.') {
                    pos++;
                    skipSpaces();
                    int propStart = pos;
                    while (pos < src.length() && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    if (pos == propStart) return null;
                    String member = src.substring(propStart, pos);
                    skipSpaces();

                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++;
                        List<Expr> args = parseArgList();
                        if (args == null) return null;
                        return dotMethodCall(name, member, args);
                    }
                    return dotPropertyGet(name, member);
                }

                // Function call — a fast Math builtin compiles directly; anything else (a real
                // ScriptBuiltins entry, or a user-defined `def`) falls back to callBuiltin, which
                // already handles both — this is how a user-defined function stays callable from
                // a compiled formula without this compiler needing to know its body at all.
                if (pos < src.length() && src.charAt(pos) == '(') {
                    pos++;
                    List<Expr> args = parseArgList();
                    if (args == null) return null;

                    String unary = UNARY_MATH.get(name);
                    if (unary != null && args.size() == 1) {
                        Expr a = toNum(args.get(0));
                        return new BaseExpr(Type.NUM) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                a.emit(mv, c);
                                mv.visitMethodInsn(INVOKESTATIC, MATH, unary, "(D)D", false);
                            }
                        };
                    }
                    String binary = BINARY_MATH.get(name);
                    if (binary != null && args.size() == 2) {
                        Expr a = toNum(args.get(0)), b = toNum(args.get(1));
                        return new BaseExpr(Type.NUM) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                a.emit(mv, c);
                                b.emit(mv, c);
                                mv.visitMethodInsn(INVOKESTATIC, MATH, binary, "(DD)D", false);
                            }
                        };
                    }
                    // Guarded by ScriptBuiltins.get(name) != null — NOT just the name string alone.
                    // Caught live: in an environment where ScriptBuiltins' registrations haven't
                    // run yet (they're populated by an explicit init call, same timing story as
                    // PolyTypeRegistry — see ScriptProgram#ensureCompiled's own doc), "lower" isn't
                    // actually registered at all, and the real callBuiltin falls through to NULL —
                    // but this fast path, checking only the NAME, specialized it into a real
                    // toLowerCase() call regardless, diverging from the interpreter for that
                    // shape. Same registry-presence discipline as the PolyType specialization
                    // above: skip the check, and BOTH a not-yet-initialized environment AND a
                    // future rename/removal of the registered builtin can silently diverge.
                    String strMethod = STRING_UNARY.get(name);
                    if (strMethod != null && args.size() == 1 && ScriptBuiltins.get(name) != null) {
                        Expr a = toAny(args.get(0));
                        boolean needsLocale = !"trim".equals(strMethod);
                        return new BaseExpr(Type.ANY) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                a.emit(mv, c);
                                mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
                                if (needsLocale) {
                                    mv.visitFieldInsn(GETSTATIC, "java/util/Locale", "ROOT", "Ljava/util/Locale;");
                                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", strMethod,
                                            "(Ljava/util/Locale;)Ljava/lang/String;", false);
                                } else {
                                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", strMethod,
                                            "()Ljava/lang/String;", false);
                                }
                                mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Ljava/lang/String;)L" + VALUE + ";", true);
                            }
                        };
                    }
                    if ("contains".equals(name) && args.size() == 2 && ScriptBuiltins.get("contains") != null) {
                        Expr a = toAny(args.get(0)), b = toAny(args.get(1));
                        return new BaseExpr(Type.BOOL) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                a.emit(mv, c);
                                mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
                                b.emit(mv, c);
                                mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
                                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains",
                                        "(Ljava/lang/CharSequence;)Z", false);
                            }
                        };
                    }
                    // push(array, value) — args.get(0)'s RUNTIME shape decides everything (the
                    // real registration itself branches on "is arg0 actually an Array"), so unlike
                    // the dot-call PolyType specialization this needs a runtime guard, not a
                    // compile-time name lookup — general on purpose: works for push(rows, x),
                    // push(local_var, x), push(some_call(), x), any array-producing expression.
                    // Both args are evaluated into locals FIRST (matching callBuiltin's own
                    // eager-evaluate-every-arg order) so the guard check doesn't re-evaluate a0
                    // and risk a double side effect.
                    if ("push".equals(name) && args.size() == 2 && ScriptBuiltins.get("push") != null) {
                        Expr a0 = toAny(args.get(0)), a1 = toAny(args.get(1));
                        return new BaseExpr(Type.ANY) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                int a0Slot = c.allocRef(), a1Slot = c.allocRef();
                                a0.emit(mv, c);
                                mv.visitVarInsn(ASTORE, a0Slot);
                                a1.emit(mv, c);
                                mv.visitVarInsn(ASTORE, a1Slot);

                                Label fallbackL = new Label(), doneL = new Label();
                                mv.visitVarInsn(ALOAD, a0Slot);
                                mv.visitTypeInsn(INSTANCEOF, ARRAY_VALUE);
                                mv.visitJumpInsn(IFEQ, fallbackL);

                                mv.visitTypeInsn(NEW, ARRAYLIST);
                                mv.visitInsn(DUP);
                                mv.visitVarInsn(ALOAD, a0Slot);
                                mv.visitTypeInsn(CHECKCAST, ARRAY_VALUE);
                                mv.visitMethodInsn(INVOKEVIRTUAL, ARRAY_VALUE, "elements", "()L" + LIST + ";", false);
                                mv.visitMethodInsn(INVOKESPECIAL, ARRAYLIST, "<init>", "(Ljava/util/Collection;)V", false);
                                int newListSlot = c.allocRef();
                                mv.visitVarInsn(ASTORE, newListSlot);
                                mv.visitVarInsn(ALOAD, newListSlot);
                                mv.visitVarInsn(ALOAD, a1Slot);
                                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                                mv.visitInsn(POP);
                                mv.visitTypeInsn(NEW, ARRAY_VALUE);
                                mv.visitInsn(DUP);
                                mv.visitVarInsn(ALOAD, newListSlot);
                                mv.visitMethodInsn(INVOKESPECIAL, ARRAY_VALUE, "<init>", "(L" + LIST + ";)V", false);
                                mv.visitJumpInsn(GOTO, doneL);

                                mv.visitLabel(fallbackL);
                                int argsListSlot = c.allocRef();
                                mv.visitTypeInsn(NEW, ARRAYLIST);
                                mv.visitInsn(DUP);
                                mv.visitMethodInsn(INVOKESPECIAL, ARRAYLIST, "<init>", "()V", false);
                                mv.visitVarInsn(ASTORE, argsListSlot);
                                mv.visitVarInsn(ALOAD, argsListSlot);
                                mv.visitVarInsn(ALOAD, a0Slot);
                                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                                mv.visitInsn(POP);
                                mv.visitVarInsn(ALOAD, argsListSlot);
                                mv.visitVarInsn(ALOAD, a1Slot);
                                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                                mv.visitInsn(POP);
                                mv.visitLdcInsn("push");
                                mv.visitVarInsn(ALOAD, argsListSlot);
                                mv.visitVarInsn(ALOAD, c.ctxSlot);
                                mv.visitMethodInsn(INVOKESTATIC, FORMULA, "callBuiltin",
                                        "(Ljava/lang/String;L" + LIST + ";L" + CTX + ";)L" + VALUE + ";", false);
                                mv.visitLabel(doneL);
                            }
                        };
                    }
                    if (resolver != null) {
                        LocalTarget lt = resolver.resolve(name);
                        if (lt != null) return localCall(lt, args);
                    }
                    return genericCall(name, args);
                }

                // Named constants — same names/case ScriptFormula.Parser#parsePrimary recognizes.
                switch (name) {
                    case "PI": case "Math_PI": return numLit(Math.PI);
                    case "TAU":  return numLit(Math.PI * 2);
                    case "E":    return numLit(Math.E);
                    case "INF":  return numLit(Double.POSITIVE_INFINITY);
                    case "NAN":  return numLit(Double.NaN);
                    case "TRUE": case "true":  return boolLit(true);
                    case "FALSE": case "false": return boolLit(false);
                    default: break;
                }

                // Plain variable reference: mirrors ScriptFormula.Parser#parsePrimary's own bare-
                // identifier branch EXACTLY — checks ctx.getClassInstance(name) FIRST, falling
                // back to ctx.getVar(name) only if that's NULL. This matters: "Player", "Machine",
                // "World", and every other global namespace are bound as CLASS instances, never as
                // plain vars — a bare "Player" reference (e.g. `Dialog.base(...).show(Player)`)
                // that only checked getVar() would always resolve to NULL for these, which is
                // exactly the bug this comment used to have (getVar-only) before it was caught via
                // a live "playerOf() couldn't resolve a Bukkit Player" warning. Kept as ANY (not
                // eagerly asNum()'d) so a formula that's just a bare passthrough (a Map/array/Item/
                // Player/Machine value evaluated via a caller that wants the real value, not a
                // coerced number) keeps its actual type; NUM/BOOL contexts coerce it via
                // toNum()/toBool() same as the interpreter's implicit asNum()/asBool().
                String varName = name;

                // A currently-valid cached local (see VarTypeHint's own doc) skips
                // getClassInstance/getVar entirely — a plain DLOAD/ILOAD/ALOAD. Applies to EVERY
                // type, not just NUM/BOOL: a Str/Array/Obj/Map-valued assignment is already a
                // boxed ScriptValue reference by the time it's computed, so caching it is just
                // ASTORE/ALOAD — no boxing to skip, but the getClassInstance/getVar round-trip is
                // just as real a cost for a string/array/map as it is for a number. Only offered
                // when ScriptClassCompiler knows one, and only for the EXACT statement immediately
                // following the assignment that produced it (see ScriptClassCompiler#emitAssign
                // and its invalidation rule) — never stale by construction, not by trust.
                if (varHint != null) {
                    CachedVarRef cached = varHint.get(varName);
                    if (cached != null) {
                        return new BaseExpr(cached.type()) {
                            @Override public void emit(MethodVisitor mv, Ctx c) {
                                int op = switch (cached.type()) {
                                    case BOOL -> ILOAD;
                                    case NUM -> DLOAD;
                                    case ANY -> ALOAD;
                                };
                                mv.visitVarInsn(op, cached.slot());
                            }
                        };
                    }
                }

                return new VarRead(varName);
            }

            return null; // '$var' or anything else unsupported
        }

        /** Parses a parenthesized, comma-separated arg list whose opening '(' has already been
         *  consumed. Returns null (bail) on any malformed arg. Replicates ScriptFormula.Parser
         *  #parseArgs' ".." range sugar EXACTLY: a bare {@code N..M} argument (both sides literal,
         *  non-negative integers — nothing else is recognized as this sugar, matching the real
         *  parser's own digit-only lookahead) expands to MULTIPLE literal args at PARSE time
         *  ({@code slots(9..12)} parses as if it were written {@code slots(9,10,11,12)}), not a
         *  single Array value — same semantics, so a formula using it compiles identically to one
         *  that spells the numbers out by hand. */
        List<Expr> parseArgList() {
            List<Expr> args = new ArrayList<>();
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == ')') { pos++; return args; }
            while (true) {
                skipSpaces();
                boolean handledRange = false;
                if (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                    int saved = pos;
                    int numStart = pos;
                    while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                    if (pos + 1 < src.length() && src.charAt(pos) == '.' && src.charAt(pos + 1) == '.') {
                        int from = Integer.parseInt(src.substring(numStart, pos));
                        pos += 2;
                        int toStart = pos;
                        while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                        if (pos == toStart) return null; // "N.." with nothing after — malformed, bail
                        int to = Integer.parseInt(src.substring(toStart, pos));
                        int step = from <= to ? 1 : -1;
                        for (int i = from; i != to + step; i += step) args.add(numLit(i));
                        handledRange = true;
                    } else {
                        pos = saved;
                    }
                }
                if (!handledRange) {
                    Expr a = parseTernary();
                    if (a == null) return null;
                    args.add(a);
                }
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ',') { pos++; continue; }
                if (pos < src.length() && src.charAt(pos) == ')') { pos++; break; }
                return null;
            }
            return args;
        }

        private static Expr numLit(double v) { return new NumLiteral(v); }

        private static Expr boolLit(boolean v) { return new BoolLiteral(v); }

        /** A string literal — ANY-typed (like every other {@code ScriptValue}-boxed result here),
         *  since a string is exactly one of the runtime shapes an ANY value already has to
         *  represent. Still a {@link Literal} (a bare {@code LDC} + one {@code ScriptValue.of}
         *  call, no branching/computation), so {@link #tryCompile}'s own literal-bail check still
         *  skips generating a whole standalone class for a formula that's JUST a bare string. */
        private static Expr strLit(String v) {
            return new StrLiteral(v);
        }

        /** {@code [e1, e2, ...]} — builds a real {@code ScriptValue.Array} at runtime (each element
         *  already coerced to ANY, same as every other arg-list builder here). Not a {@link
         *  Literal} even when every element happens to itself be one — constructing the
         *  {@code ArrayList}/{@code Array} is still real per-call work, unlike a bare constant. */
        private static Expr arrayLit(List<Expr> elems) {
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int listSlot = c.allocRef();
                    emitBuildArgsList(mv, c, elems, listSlot);
                    mv.visitTypeInsn(NEW, ARRAY_VALUE);
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, listSlot);
                    mv.visitMethodInsn(INVOKESPECIAL, ARRAY_VALUE, "<init>", "(L" + LIST + ";)V", false);
                }
            };
        }

        /** Same narrow char class as {@code ScriptFormula#isScriptCallArgChar} — kept as its own
         *  copy rather than a shared/public helper since it's one line and this compiler's parser
         *  is deliberately a separate, self-contained implementation (see class doc). */
        private static boolean isScriptCallArgChar(char c) {
            return Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '.' || c == '-';
        }

        /** {@code "file.pf:func[:arg1:arg2...]"} — routes through the real {@link ScriptCall#parse}
         *  / {@link ScriptCall#evaluate(ScriptContext)}, the SAME two calls the interpreter's own
         *  inline ".pf:" primary makes ({@code ScriptCall.parse(ref)} is re-run on every evaluation
         *  rather than cached — matching that existing cost/behavior exactly rather than optimizing
         *  it here, since this compiler's whole contract is "compiles to the SAME behavior", not
         *  "compiles to different, faster behavior"). Wrapped in a real {@code try/catch
         *  (Throwable)} — via {@code visitTryCatchBlock}, not a Java {@code try} block, since this
         *  emits raw bytecode — mirroring the interpreter's own {@code catch (Throwable ignored) {
         *  return ScriptValue.NULL; }} around this exact call: a target script that fails to load,
         *  a missing function, a NullPointerException from {@code ScriptCall.parse} returning
         *  {@code null} for a malformed ref (blank after trimming — can't actually happen here,
         *  since the {@code ".pf:"} match already guarantees a non-blank {@code ref}, but the catch
         *  covers it anyway) — every failure mode degrades to NULL, never propagates out of the
         *  compiled method and crashes the whole call chain the way an uncaught exception here
         *  would. */
        private static Expr crossFileCall(String ref) {
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    Label start = new Label(), end = new Label(), handler = new Label(), done = new Label();
                    mv.visitTryCatchBlock(start, end, handler, "java/lang/Throwable");
                    mv.visitLabel(start);
                    mv.visitLdcInsn(ref);
                    mv.visitMethodInsn(INVOKESTATIC, SCRIPT_CALL, "parse",
                            "(Ljava/lang/String;)L" + SCRIPT_CALL + ";", false);
                    mv.visitVarInsn(ALOAD, c.ctxSlot);
                    mv.visitMethodInsn(INVOKEVIRTUAL, SCRIPT_CALL, "evaluate",
                            "(L" + CTX + ";)L" + VALUE + ";", false);
                    mv.visitLabel(end);
                    mv.visitJumpInsn(GOTO, done);
                    mv.visitLabel(handler);
                    mv.visitInsn(POP); // discard the caught Throwable
                    emitGetNull(mv);
                    mv.visitLabel(done);
                }
            };
        }

        /** {@code obj[idx]} — routes through the real {@link ScriptFormula#subscriptGet}, exactly
         *  what {@code ScriptFormula.Parser#parseSuffixChain}'s own {@code '['} case does, so array/
         *  Map/String indexing semantics (bounds handling, non-Array fallback, ...) come from that
         *  single already-tested implementation rather than being re-derived here. */
        private static Expr subscriptExpr(Expr obj0, Expr idx0) {
            Expr obj = toAny(obj0), idx = toAny(idx0);
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    obj.emit(mv, c);
                    idx.emit(mv, c);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "subscriptGet",
                            "(L" + VALUE + ";L" + VALUE + ";)L" + VALUE + ";", false);
                }
            };
        }

        /** {@code Name.prop} — mirrors the interpreter's dot-access property branch exactly:
         *  {@code sv = ctx.getClassInstance(name); if (sv==NULL) sv = ctx.getVar(name); return
         *  sv!=NULL ? memberGet(sv, prop, ctx) : NULL;}
         *
         *  <p>When {@code name} resolves (at COMPILE time — {@link PolyTypeRegistry} is already
         *  fully populated by the time any real script ever gets compiled, see {@code
         *  ScriptProgram#ensureCompiled}'s own doc for why that timing is safe) to a REGISTERED
         *  {@link PolyType} with this property, emits a DIRECT call to that type's resolved {@link
         *  PolyType.PropertyHandler} instead — no {@link ScriptFormula#memberGet} in that path at
         *  all. Still real, generic dispatch underneath (a fresh {@code PolyTypeRegistry.get}/
         *  {@code resolveMethod} lookup by the compile-time-known constant strings, not a cached
         *  handler reference — so a LATER {@code replaceProperty}/{@code extend} is picked up
         *  exactly like the generic path would), just with {@code ScriptFormula.memberGet}'s own
         *  switch-and-{@code PolyClass}-check layer skipped. Guarded by a runtime check that the
         *  receiver genuinely IS that exact type (see {@link #emitPolyTypeGuard}'s own doc for why
         *  that guard is load-bearing, not optional) — any mismatch falls back to the exact same
         *  generic {@code memberGet} call as before, so this can never diverge from the always-
         *  correct path, only skip redundant work on the way to it. */
        private static Expr dotPropertyGet(String name, String prop) {
            // Unbox the receiver into this type's generated PolyClass (see PolyClassGenerator) and
            // read the property through its own generated accessor — a plain INVOKEVIRTUAL against
            // an already-resolved handler, no PolyTypeRegistry lookup at this call site at all.
            PolyClassGenerator.GeneratedPolyClass generated = PolyClassGenerator.getOrGenerate(name);
            String propJavaName = generated != null ? generated.properties().get(prop) : null;
            String wrapperName = propJavaName != null ? generated.internalName() : null;
            // A propertyTyped registration with a scalar codec also generated a native-returning
            // accessor. It is not used by emit() — this node's declared type is still ANY — but
            // toNum/toBool/toStr fuse into it, which is where the boxing actually disappears.
            PolyClassGenerator.TypedMemberRef nativeRef =
                    generated != null ? generated.typedProperties().get(prop) : null;
            return new PropRead(name, prop, wrapperName, propJavaName, nativeRef,
                    propertyResultPolyType(name, prop),
                    generated != null ? generated.listProperties().get(prop) : null);
        }

        /** {@code Name.method(args)} — same resolve-then-null-guard shape as {@link
         *  #dotPropertyGet}, but via {@code memberCall} with an evaluated arg list; args are only
         *  evaluated once {@code sv} is confirmed non-null, matching the interpreter's own
         *  short-circuit order. Same compile-time {@link PolyType} specialization as {@link
         *  #dotPropertyGet} — see that method's own doc.
         *
         *  <p>When the resolved method ALSO has a {@code methodTypedN} registration (a {@link
         *  PolyType.TypedMethodDescriptor} via {@code resolveTypedMethod}) whose arity matches this
         *  call site and whose argument/return {@link PolyType.TypeCodec}s are all one of the four
         *  known {@link TypeCodecs} singletons, an EVEN faster tier is tried first: the typed
         *  handler is invoked directly ({@code INVOKEINTERFACE} on the specific
         *  {@code TypedMethodHandlerN}), with each arg decoded inline ({@code asNum}/{@code asBool}/
         *  {@code asStr}, or left as-is for {@code RAW}) instead of going through the untyped
         *  {@code MethodHandler} lambda that {@code methodTypedN} itself installs into {@code
         *  methods} — which would otherwise re-decode every arg through one interface dispatch per
         *  {@code TypeCodec.decode} call, plus its own {@code MethodHandler.call} dispatch, plus one
         *  {@code TypeCodec.encode} dispatch on the way out. The arg list is still built unconditionally
         *  up front (never re-evaluate the arg expressions on a second path — a real side-effect
         *  hazard if an arg is itself a call) and read back via {@code List.get(i)}, so this tier is
         *  purely about skipping dispatch layers, not about skipping the list allocation. Falls back
         *  to the untyped fast tier (and from there to {@code memberCall}) if {@code
         *  resolveTypedMethod} unexpectedly misses at runtime — structurally unreachable today (no
         *  API removes a {@code typedMethods} entry once registered) but kept as a real fallback,
         *  not a silent wrong-value shortcut. */
        /**
         * A chained hop {@code base.method(args)} whose receiver PolyType is known at compile time.
         * See {@link PropRead#chained} — same idea, and the payoff is bigger here: a specialized
         * call passes its arguments as native JVM values, so the {@code ArrayList} the generic path
         * builds for every call disappears along with one {@code ScriptValue} per argument.
         */
        private static Expr dotMethodCall(String name, String method, List<Expr> rawArgs) {
            return dotMethodCall(null, name, method, rawArgs);
        }

        private static Expr dotMethodCall(Expr base, String name, String method, List<Expr> rawArgs) {
            List<Expr> args = rawArgs.stream().map(ScriptBytecodeCompiler::toAny).toList();
            int arity = args.size();

            // Unbox the receiver into this type's generated PolyClass (see PolyClassGenerator) and
            // call the member's own generated Java method. EVERY registered method has one: a
            // genuinely native-signatured method when its methodTypedN codecs/arity are all known,
            // otherwise the erased ScriptValue(List) shape. Either way the PolyTypeRegistry lookup
            // this call would otherwise redo on EVERY invocation is gone — the wrapper holds the
            // resolved handler in a static field, kept current by PolyClassGenerator's refresh on
            // every registry mutation. Falls straight through to memberCall (exactly as if none of
            // this existed) when the type isn't registered, generation failed, or this method isn't
            // one of its members.
            PolyClassGenerator.GeneratedPolyClass generated = PolyClassGenerator.getOrGenerate(name);
            PolyClassGenerator.TypedMemberRef typedCandidate = null;
            String untypedCandidate = null;
            if (generated != null) {
                PolyClassGenerator.TypedMemberRef t = generated.typedMethods().get(method);
                // Arity must match the generated native signature exactly. A call site passing
                // FEWER args still needs the registration's own missing-args handling (a
                // methodTypedOptN's per-argument defaults, or methodTypedN's onMissingArgs), which
                // only the erased MethodHandler applies — so fall to the untyped shim, which every
                // typed method also generates, rather than all the way to generic memberCall.
                if (t != null && t.argKinds().length == arity) typedCandidate = t;
                else untypedCandidate = generated.untypedMethods().get(method);
            }
            PolyClassGenerator.TypedMemberRef typedRef = typedCandidate;
            String untypedJavaName = untypedCandidate;
            boolean specializeTyped = typedRef != null;
            boolean specializeUntyped = untypedJavaName != null;
            String wrapperName = (specializeTyped || specializeUntyped) ? generated.internalName() : null;

            // Per-arg slot representation for the typed path: when the RAW (pre-toAny) arg
            // expression is ALREADY the exact native shape the generated method's parameter wants
            // (a NUM expr into a double param, a BOOL expr into a boolean param), keep it as a
            // genuinely unboxed JVM primitive — no ScriptValue box/unbox round trip at all, since
            // the wrapper's parameter IS that primitive type. Anything else evaluates as a boxed
            // ScriptValue and is decoded to the native shape on the way in.
            ArgSlotKind[] argSlotKinds = null;
            if (specializeTyped) {
                argSlotKinds = new ArgSlotKind[arity];
                for (int i = 0; i < arity; i++) {
                    PolyClassGenerator.Kind k = typedRef.argKinds()[i];
                    Type rawType = rawArgs.get(i).type();
                    if (k == PolyClassGenerator.Kind.DOUBLE && rawType == Type.NUM) argSlotKinds[i] = ArgSlotKind.NUM_RAW;
                    else if (k == PolyClassGenerator.Kind.BOOL && rawType == Type.BOOL) argSlotKinds[i] = ArgSlotKind.BOOL_RAW;
                    // A string LITERAL into a native String parameter: hold the raw String, so the
                    // call site never boxes it just for the callee to asStr() it straight back.
                    else if (k == PolyClassGenerator.Kind.STRING && rawArgs.get(i) instanceof StrLiteral)
                        argSlotKinds[i] = ArgSlotKind.STR_RAW;
                    else argSlotKinds[i] = ArgSlotKind.ANY_BOXED;
                }
            }
            ArgSlotKind[] finalArgSlotKinds = argSlotKinds;

            Expr call = new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int svSlot = c.allocRef();
                    // First hop: resolve the name, and return NULL for a NULL receiver. Chained
                    // hop: evaluate the base, no guard — see PropRead#emitAs for why they differ.
                    Label isNullL = new Label(), endL = new Label();
                    if (base == null) {
                        emitResolveInstanceOrVar(mv, c, name, svSlot);
                        mv.visitVarInsn(ALOAD, svSlot);
                        emitGetNull(mv);
                        mv.visitJumpInsn(IF_ACMPEQ, isNullL);
                    } else {
                        base.emit(mv, c);
                        mv.visitVarInsn(ASTORE, svSlot);
                    }

                    if (specializeTyped) {
                        // Evaluate each arg EXACTLY ONCE into its own local — never re-run an arg
                        // expression to serve the fallback path too (a real side-effect hazard if an
                        // arg is itself a call). The boxed ArrayList the memberCall fallback needs is
                        // built only on that (type-guard-mismatch-only) path, never here.
                        int[] argSlots = new int[arity];
                        for (int i = 0; i < arity; i++) {
                            switch (finalArgSlotKinds[i]) {
                                case NUM_RAW -> {
                                    argSlots[i] = c.allocD();
                                    rawArgs.get(i).emit(mv, c);
                                    mv.visitVarInsn(DSTORE, argSlots[i]);
                                }
                                case BOOL_RAW -> {
                                    argSlots[i] = c.allocRef();
                                    rawArgs.get(i).emit(mv, c);
                                    mv.visitVarInsn(ISTORE, argSlots[i]);
                                }
                                case STR_RAW -> {
                                    argSlots[i] = c.allocRef();
                                    mv.visitLdcInsn(((StrLiteral) rawArgs.get(i)).value);
                                    mv.visitVarInsn(ASTORE, argSlots[i]);
                                }
                                case ANY_BOXED -> {
                                    argSlots[i] = c.allocRef();
                                    toAny(rawArgs.get(i)).emit(mv, c);
                                    mv.visitVarInsn(ASTORE, argSlots[i]);
                                }
                            }
                        }

                        Label fallbackL = new Label(), fastL = new Label();
                        int objSlot = c.allocRef(), instSlot = c.allocRef();
                        emitPolyTypeGuard(mv, c, svSlot, name, objSlot, instSlot, fallbackL);

                        // Unbox the receiver into its PolyClass and hold it in a NAMED local, so the
                        // generated code reads as `PolyClassMachine m = new PolyClassMachine(inst);
                        // m.foo(...)` instead of burying a `new` inside the call. One small,
                        // escape-analysis-friendly allocation plus a monomorphic INVOKEVIRTUAL.
                        mv.visitTypeInsn(NEW, wrapperName);
                        mv.visitInsn(DUP);
                        mv.visitVarInsn(ALOAD, instSlot);
                        mv.visitMethodInsn(INVOKESPECIAL, wrapperName, "<init>", "(Ljava/lang/Object;)V", false);
                        int pcSlot = c.allocRef();
                        mv.visitVarInsn(ASTORE, pcSlot);
                        mv.visitVarInsn(ALOAD, pcSlot);
                        for (int i = 0; i < arity; i++) {
                            switch (finalArgSlotKinds[i]) {
                                case NUM_RAW -> mv.visitVarInsn(DLOAD, argSlots[i]);
                                case BOOL_RAW -> mv.visitVarInsn(ILOAD, argSlots[i]);
                                case STR_RAW -> mv.visitVarInsn(ALOAD, argSlots[i]); // already a String
                                case ANY_BOXED -> {
                                    mv.visitVarInsn(ALOAD, argSlots[i]);
                                    emitDecodeToNative(mv, typedRef.argKinds()[i]);
                                }
                            }
                        }
                        mv.visitMethodInsn(INVOKEVIRTUAL, wrapperName, typedRef.javaName(),
                                typedRef.descriptor(), false);
                        emitBoxNativeToScriptValue(mv, typedRef.retKind());
                        mv.visitJumpInsn(GOTO, fastL);

                        mv.visitLabel(fallbackL);
                        if (arity <= MAX_NATIVE_ARGS) {
                            mv.visitVarInsn(ALOAD, svSlot);
                            emitSlotsAsValues(mv, argSlots, finalArgSlotKinds);
                            mv.visitVarInsn(ALOAD, c.ctxSlot);
                            emitDynamicCallN(mv, method, arity);
                        } else {
                            int listSlot = c.allocRef();
                            emitListFromSlots(mv, argSlots, finalArgSlotKinds, listSlot);
                            mv.visitVarInsn(ALOAD, svSlot);
                            mv.visitVarInsn(ALOAD, listSlot);
                            mv.visitVarInsn(ALOAD, c.ctxSlot);
                            emitDynamicCall(mv, method);
                        }
                        mv.visitLabel(fastL);
                    } else if (specializeUntyped) {
                        // The wrapper's erased shim still takes List<ScriptValue>, so the list IS
                        // built here — but the registry lookup is still gone.
                        int listSlot = c.allocRef();
                        emitBuildArgsList(mv, c, args, listSlot);

                        Label fallbackL = new Label(), fastL = new Label();
                        int objSlot = c.allocRef(), instSlot = c.allocRef();
                        emitPolyTypeGuard(mv, c, svSlot, name, objSlot, instSlot, fallbackL);
                        mv.visitTypeInsn(NEW, wrapperName);
                        mv.visitInsn(DUP);
                        mv.visitVarInsn(ALOAD, instSlot);
                        mv.visitMethodInsn(INVOKESPECIAL, wrapperName, "<init>", "(Ljava/lang/Object;)V", false);
                        mv.visitVarInsn(ALOAD, listSlot);
                        mv.visitMethodInsn(INVOKEVIRTUAL, wrapperName, untypedJavaName,
                                "(L" + LIST + ";)L" + VALUE + ";", false);
                        mv.visitJumpInsn(GOTO, fastL);
                        mv.visitLabel(fallbackL);
                        mv.visitVarInsn(ALOAD, svSlot);
                        mv.visitVarInsn(ALOAD, listSlot);
                        mv.visitVarInsn(ALOAD, c.ctxSlot);
                        emitDynamicCall(mv, method);
                        mv.visitLabel(fastL);
                    } else if (args.size() <= MAX_NATIVE_ARGS) {
                        // The receiver is already resolved into svSlot above, so only its LOAD moves
                        // here — argument evaluation order is untouched.
                        mv.visitVarInsn(ALOAD, svSlot);
                        for (Expr a : args) a.emit(mv, c);
                        mv.visitVarInsn(ALOAD, c.ctxSlot);
                        emitDynamicCallN(mv, method, args.size());
                    } else {
                        int listSlot = c.allocRef();
                        emitBuildArgsList(mv, c, args, listSlot);
                        mv.visitVarInsn(ALOAD, svSlot);
                        mv.visitVarInsn(ALOAD, listSlot);
                        mv.visitVarInsn(ALOAD, c.ctxSlot);
                        emitDynamicCall(mv, method);
                    }

                    if (base == null) {
                        mv.visitJumpInsn(GOTO, endL);
                        mv.visitLabel(isNullL);
                        emitGetNull(mv);
                        mv.visitLabel(endL);
                    }
                }
            };
            // A method whose return codec names one PolyType lets the NEXT hop specialize too, so
            // `Machine.contraption.origin.x` stays typed the whole way down instead of falling back
            // to generic dispatch at the first link.
            String resultType = methodResultPolyType(name, method);
            return resultType == null ? call : new TypedResult(call, resultType);
        }

        /** Builds a fresh {@code ArrayList<ScriptValue>} from already-evaluated arg locals (see
         *  {@link #dotMethodCall}'s typed tier) — never re-runs the arg expressions themselves.
         *  A {@code NUM_RAW}/{@code BOOL_RAW} slot holds a genuinely unboxed primitive (never
         *  boxed to {@code ScriptValue} at all on the typed tier's own path), so it's boxed HERE,
         *  on this miss-only path, via {@code ScriptValue.of(D/Z)} — an {@code ANY_BOXED} slot is
         *  already a {@code ScriptValue} reference and needs no conversion. */
        private static void emitListFromSlots(MethodVisitor mv, int[] argSlots, ArgSlotKind[] kinds, int listSlot) {
            mv.visitTypeInsn(NEW, ARRAYLIST);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, ARRAYLIST, "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, listSlot);
            for (int i = 0; i < argSlots.length; i++) {
                mv.visitVarInsn(ALOAD, listSlot);
                emitSlotAsValue(mv, argSlots[i], kinds[i]);
                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                mv.visitInsn(POP);
            }
        }

        /** Pushes every already-evaluated arg local as a {@code ScriptValue}, in order — the
         *  native-signature counterpart of {@link #emitListFromSlots}, for a fallback that hands its
         *  arguments to {@link #emitDynamicCallN} instead of to a list. */
        private static void emitSlotsAsValues(MethodVisitor mv, int[] argSlots, ArgSlotKind[] kinds) {
            for (int i = 0; i < argSlots.length; i++) emitSlotAsValue(mv, argSlots[i], kinds[i]);
        }

        private static void emitSlotAsValue(MethodVisitor mv, int slot, ArgSlotKind kind) {
            {
                switch (kind) {
                    case NUM_RAW -> {
                        mv.visitVarInsn(DLOAD, slot);
                        mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                    }
                    case BOOL_RAW -> {
                        mv.visitVarInsn(ILOAD, slot);
                        mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
                    }
                    case STR_RAW -> {
                        mv.visitVarInsn(ALOAD, slot);
                        mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Ljava/lang/String;)L" + VALUE + ";", true);
                    }
                    case ANY_BOXED -> mv.visitVarInsn(ALOAD, slot);
                }
            }
        }

        /** Stack: {@code ..., ScriptValue} -&gt; {@code ..., <boxed native>} — mirrors exactly what
         *  {@link TypeCodecs}' DOUBLE/BOOL/STRING/RAW {@code decode(...)} does, inlined so the JIT
         *  never pays for the {@code TypeCodec.decode} interface dispatch itself. The boxing
         *  (Double/Boolean) is required because {@code TypedMethodHandlerN.call}'s parameters erase
         *  to {@code Object} — a raw primitive can't be passed there directly. */
        private static void emitDecodeToNative(MethodVisitor mv, PolyClassGenerator.Kind kind) {
            switch (kind) {
                case DOUBLE -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asNum", "()D", true);
                case BOOL -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asBool", "()Z", true);
                case STRING -> mv.visitMethodInsn(INVOKEINTERFACE, VALUE, "asStr", "()Ljava/lang/String;", true);
                case RAW, LIST -> { /* already a ScriptValue at this call site — a LIST slot is decoded to a
                                       real List INSIDE the generated wrapper, not here */ }
                case UNKNOWN -> throw new IllegalStateException("emitDecodeToNative called with UNKNOWN");
            }
        }

        /** Stack: {@code ..., Object} (the typed handler's raw, erased return) -&gt;
         *  {@code ..., ScriptValue} — mirrors {@link TypeCodecs}' {@code encode(...)}, inlined for
         *  the same reason as {@link #emitCodecDecodeInline}. */
        private static void emitBoxNativeToScriptValue(MethodVisitor mv, PolyClassGenerator.Kind kind) {
            switch (kind) {
                case DOUBLE -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(D)L" + VALUE + ";", true);
                case BOOL -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Z)L" + VALUE + ";", true);
                case STRING -> mv.visitMethodInsn(INVOKESTATIC, VALUE, "of", "(Ljava/lang/String;)L" + VALUE + ";", true);
                case RAW, LIST -> { /* the wrapper already returns a ScriptValue (a LIST return was
                                       encoded back to an Array inside it) */ }
                case UNKNOWN -> throw new IllegalStateException("emitBoxNativeToScriptValue called with UNKNOWN");
            }
        }

        /** Emits: {@code sv instanceof ScriptValue.Obj o && o.instance() != null &&
         *  !(o.instance() instanceof PolyClass) && expectedTypeName.equals(o.typeName())} — jumps
         *  to {@code fallbackL} the instant any check fails, otherwise falls through with {@code
         *  objSlot}/{@code instSlot} populated. This exact guard (not just an {@code instanceof
         *  Obj} check) is load-bearing: {@link ScriptValue#callMethod}/{@code #getProperty} check
         *  {@code instance() instanceof PolyClass} FIRST, unconditionally, before ever consulting
         *  {@link PolyTypeRegistry} — a real, already-shipped precedent for why (see that method's
         *  own comment): a script-visible name can be bound to an instance that does NOT match the
         *  {@link PolyType} its OWN name would suggest (a {@code FormConditionClass} bound as
         *  "World", handled by its own dispatch, not {@code WorldType}'s). Skipping this check
         *  would silently call the WRONG handler with the wrong instance shape whenever that
         *  happens, instead of falling back like the generic path correctly does. */
        private static void emitPolyTypeGuard(MethodVisitor mv, Ctx c, int svSlot, String expectedTypeName,
                                               int objSlot, int instSlot, Label fallbackL) {
            mv.visitVarInsn(ALOAD, svSlot);
            mv.visitTypeInsn(INSTANCEOF, OBJ_VALUE);
            mv.visitJumpInsn(IFEQ, fallbackL);
            mv.visitVarInsn(ALOAD, svSlot);
            mv.visitTypeInsn(CHECKCAST, OBJ_VALUE);
            mv.visitVarInsn(ASTORE, objSlot);
            mv.visitVarInsn(ALOAD, objSlot);
            mv.visitMethodInsn(INVOKEVIRTUAL, OBJ_VALUE, "instance", "()Ljava/lang/Object;", false);
            mv.visitVarInsn(ASTORE, instSlot);
            mv.visitVarInsn(ALOAD, instSlot);
            mv.visitJumpInsn(IFNULL, fallbackL);
            mv.visitVarInsn(ALOAD, instSlot);
            mv.visitTypeInsn(INSTANCEOF, POLY_CLASS);
            mv.visitJumpInsn(IFNE, fallbackL);
            mv.visitVarInsn(ALOAD, objSlot);
            mv.visitMethodInsn(INVOKEVIRTUAL, OBJ_VALUE, "typeName", "()Ljava/lang/String;", false);
            mv.visitLdcInsn(expectedTypeName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFEQ, fallbackL);
        }

        /** A call to another {@code def} the SAME file's {@link ScriptClassCompiler} pass already
         *  compiled onto this generated class — real local dispatch, {@code INVOKESTATIC} straight
         *  to the target method, with an ISOLATED per-call {@code ScriptContext.Builder} built
         *  fresh for this one call (never the caller's own shared builder — mutating that would
         *  leak the callee's params back into the caller, which the interpreter's own {@link
         *  UserFunction#call} never does either). Mirrors {@code UserFunction.call}'s own binding
         *  exactly: start from a copy of the CURRENT live context (matching its {@code
         *  fb.copyFrom(callerCtx)} — every def visible at this call site is already bound as a var
         *  by the time compiled code runs, same as the interpreter), then overwrite each parameter
         *  name with its argument (missing trailing arguments bind to {@code ScriptValue.NULL},
         *  extra trailing arguments are simply never read — identical to {@code UserFunction.call}'s
         *  own {@code i < args.size() ? args.get(i) : ScriptValue.NULL} loop). */
        private static Expr localCall(LocalTarget lt, List<Expr> rawArgs) {
            List<Expr> args = rawArgs.stream().map(ScriptBytecodeCompiler::toAny).toList();
            List<String> params = lt.paramNames();
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int nbSlot = c.allocRef();
                    mv.visitMethodInsn(INVOKESTATIC, CTX, "builder", "()L" + BUILDER + ";", false);
                    if (lt.crossFile()) {
                        // The callee's own file scope goes UNDER the caller's, exactly as
                        // UserFunction.call layers definingCtx — that is what lets a compiled
                        // cross-file call read its own file's top-level values (tree_utils'
                        // OFFSETS6) which the caller has never heard of.
                        mv.visitMethodInsn(INVOKESTATIC, lt.internalClassName(), "fileScope",
                                "()L" + CTX + ";", false);
                        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "copyFrom", "(L" + CTX + ";)L" + BUILDER + ";", false);
                    }
                    mv.visitVarInsn(ALOAD, c.ctxSlot);
                    mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "copyFrom", "(L" + CTX + ";)L" + BUILDER + ";", false);
                    mv.visitVarInsn(ASTORE, nbSlot);
                    for (int i = 0; i < params.size(); i++) {
                        mv.visitVarInsn(ALOAD, nbSlot);
                        mv.visitLdcInsn(params.get(i));
                        if (i < args.size()) args.get(i).emit(mv, c);
                        else emitGetNull(mv);
                        mv.visitMethodInsn(INVOKEVIRTUAL, BUILDER, "val",
                                "(Ljava/lang/String;L" + VALUE + ";)L" + BUILDER + ";", false);
                        mv.visitInsn(POP);
                    }
                    mv.visitVarInsn(ALOAD, nbSlot);
                    mv.visitMethodInsn(INVOKESTATIC, lt.internalClassName(), lt.methodName(),
                            "(L" + BUILDER + ";)L" + VALUE + ";", false);
                }
            };
        }

        /** Any function-call name not recognized as a fast Math builtin — routes through the
         *  real {@code callBuiltin}, which itself checks for a user-defined function (a top-level
         *  {@code def}, bound via {@code UserFunction}) before falling into {@code
         *  ScriptBuiltins}/the hand-written builtin switch, exactly as the interpreter does. */
        private static Expr genericCall(String name, List<Expr> rawArgs) {
            List<Expr> args = rawArgs.stream().map(ScriptBytecodeCompiler::toAny).toList();
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int listSlot = c.allocRef();
                    emitBuildArgsList(mv, c, args, listSlot);
                    mv.visitLdcInsn(name);
                    mv.visitVarInsn(ALOAD, listSlot);
                    mv.visitVarInsn(ALOAD, c.ctxSlot);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "callBuiltin",
                            "(Ljava/lang/String;L" + LIST + ";L" + CTX + ";)L" + VALUE + ";", false);
                }
            };
        }

        /** {@code sv = ctx.getClassOrVar(name)} into {@code svSlot} — a single call. This used to be
         *  open-coded as getClassInstance, a NULL compare, a branch, and a getVar, which is four
         *  statements and a jump in the generated code for what is one question with one answer;
         *  {@link ScriptContext#getClassOrVar} is that exact sequence, moved where it belongs. */
        static void emitResolveInstanceOrVar(MethodVisitor mv, Ctx c, String name, int svSlot) {
            mv.visitVarInsn(ALOAD, c.ctxSlot);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getClassOrVar", "(Ljava/lang/String;)L" + VALUE + ";", false);
            mv.visitVarInsn(ASTORE, svSlot);
        }

        /** {@code new ArrayList<>()} in {@code listSlot}, then one {@code list.add(evaluatedArg)}
         *  per arg (each already coerced to ANY by the caller). */
        private static void emitBuildArgsList(MethodVisitor mv, Ctx c, List<Expr> args, int listSlot) {
            mv.visitTypeInsn(NEW, ARRAYLIST);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, ARRAYLIST, "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, listSlot);
            for (Expr a : args) {
                mv.visitVarInsn(ALOAD, listSlot);
                a.emit(mv, c);
                mv.visitMethodInsn(INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true);
                mv.visitInsn(POP);
            }
        }

        /** Stack: {@code ..., ScriptValue receiver, List args, ScriptContext} -&gt;
         *  {@code ..., ScriptValue}. An {@code invokedynamic} that links itself, on first execution,
         *  into a guarded direct call for the receiver type it observes — see {@link PolyDispatch}.
         *  Replaces what was an unconditional {@code ScriptFormula.memberCall} on every evaluation.
         *  The method NAME travels as a bootstrap constant, not a stack operand, which is what lets
         *  the call site specialise on it. */
        private static void emitDynamicCall(MethodVisitor mv, String method) {
            mv.visitInvokeDynamicInsn("memberCall",
                    "(L" + VALUE + ";L" + LIST + ";L" + CTX + ";)L" + VALUE + ";", BSM_CALL, method);
        }

        /** Above how many arguments a call site keeps building a real list. Past this the native
         *  signature stops paying for itself: the saved instructions no longer offset the widening
         *  MethodType, and no script method comes close anyway. */
        private static final int MAX_NATIVE_ARGS = 8;

        /** Stack: {@code ..., ScriptValue receiver, ScriptValue arg0..argN-1, ScriptContext} -&gt;
         *  {@code ..., ScriptValue}. Same {@link PolyDispatch} inline cache as {@link
         *  #emitDynamicCall}, but the arguments ride the operand stack instead of an
         *  {@code ArrayList} the call site has to build — nine bytes of setup plus eight per
         *  argument, and one allocation per invocation, at every generic call site in a script.
         *  {@code PolyDispatch.CallIC} collects them on the linking side instead. */
        private static void emitDynamicCallN(MethodVisitor mv, String method, int nargs) {
            StringBuilder desc = new StringBuilder("(L").append(VALUE).append(';');
            desc.append(("L" + VALUE + ";").repeat(nargs));
            desc.append('L').append(CTX).append(";)L").append(VALUE).append(';');
            mv.visitInvokeDynamicInsn("memberCall", desc.toString(), BSM_CALL, method);
        }

        /** Stack: {@code ..., ScriptValue receiver, ScriptContext} -&gt; {@code ..., ScriptValue}.
         *  The property counterpart of {@link #emitDynamicCall}. */
        static void emitDynamicGet(MethodVisitor mv, String prop) {
            mv.visitInvokeDynamicInsn("memberGet",
                    "(L" + VALUE + ";L" + CTX + ";)L" + VALUE + ";", BSM_GET, prop);
        }

        static void emitGetNull(MethodVisitor mv) {
            mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
        }
    }
}
