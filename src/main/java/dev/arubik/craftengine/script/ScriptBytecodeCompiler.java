package dev.arubik.craftengine.script;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
 * it sees a construct it doesn't model at all — a string literal, {@code $var}, {@code ??},
 * ranges/array literals, bitwise/shift operators, {@code **}/{@code //}/{@code ^}, the
 * {@code "file.pf:func"} cross-file call form — OR whenever it recognizes a construct but can't
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
 * {@code ternary > || > && > ! > compare > + - > * / % > unary- > primary}. The real chain also
 * has a bitwise/shift layer (between {@code !} and compare) and a null-coalesce layer (between
 * compare and {@code + -}) and a {@code **}/{@code //}/{@code ^} pow layer (inside {@code * / %})
 * that this compiler omits entirely — omitting a layer is always SAFE here (not a silent
 * misparse): any operator from an omitted layer is simply left unconsumed, which either aborts
 * that operand's own bail chain or trips {@link #tryCompile}'s final "did we consume the whole
 * string" check, never gets miscompiled as something else.
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
 * <h3>Why {@code ==}/{@code !=} and {@code +} bail on an ANY operand</h3>
 * Both are type-polymorphic in the real interpreter: {@code ==}/{@code !=} special-case a
 * {@code Null} operand (reference-style null equality) and a {@code Str} operand (string
 * equality) before falling back to numeric; {@code +} does string concatenation whenever either
 * side is a {@code Str}. An ANY-typed operand could be exactly those types at runtime, so blindly
 * coercing both sides to {@code double} for these two operators would silently diverge from the
 * interpreter (e.g. {@code name == null} or {@code "a" + b}). Every OTHER comparison ({@code
 * >}/{@code <}/{@code >=}/{@code <=}) and the other arithmetic operators ({@code -}/{@code *}/
 * {@code /}/{@code %}) are unconditionally numeric in the real interpreter regardless of operand
 * type, so those stay safe to compile with an ANY operand via {@link #toNum} coercion.
 */
final class ScriptBytecodeCompiler {

    private ScriptBytecodeCompiler() {}

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final String NODE_IFACE = "dev/arubik/craftengine/script/ScriptFormula$Node";
    private static final String FORMULA = "dev/arubik/craftengine/script/ScriptFormula";
    private static final String CTX = "dev/arubik/craftengine/script/ScriptContext";
    private static final String VALUE = "dev/arubik/craftengine/script/ScriptValue";
    private static final String MATH = "java/lang/Math";
    private static final String LIST = "java/util/List";
    private static final String ARRAYLIST = "java/util/ArrayList";

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

    // ---- Entry point -----------------------------------------------------

    static ScriptFormula.Node tryCompile(String expr) {
        try {
            P p = new P(expr);
            Expr root = p.parseTernary();
            p.skipSpaces();
            if (root == null || p.pos != expr.length()) return null;

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
            Ctx c = new Ctx();
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
            return (ScriptFormula.Node) defined.getDeclaredConstructor().newInstance();
        } catch (Throwable ignored) {
            // Anything at all — an unsupported construct that slipped past a bail check, a real
            // bug in the generator, a verifier rejection — degrades to the interpreter, never a
            // broken script.
            return null;
        }
    }

    // ---- Typed micro-AST + codegen ----------------------------------------

    private enum Type { NUM, BOOL, ANY }

    /** Per-formula codegen state — just a local-variable-slot allocator. Slot 0 is {@code this},
     *  slot 1 is the {@code ctx} parameter; every dot-access/function-call/div-by-zero-guard needs
     *  its own scratch slot(s) for intermediate values, allocated fresh (and never reused) per
     *  compiled formula — formulas are short, so a little slot waste is a non-issue and far
     *  simpler than trying to free/reuse slots correctly. */
    private static final class Ctx {
        int next = 2;
        int allocRef() { int s = next; next += 1; return s; }
        int allocD()   { int s = next; next += 2; return s; }
    }

    private interface Expr {
        Type type();
        void emit(MethodVisitor mv, Ctx c);
    }

    private abstract static class BaseExpr implements Expr {
        final Type type;
        BaseExpr(Type type) { this.type = type; }
        @Override public Type type() { return type; }
    }

    // ---- Coercions — mirror ScriptValue#asNum()/#asBool() exactly ----

    private static Expr toNum(Expr e) {
        if (e.type() == Type.NUM) return e;
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

    private static Expr toBool(Expr e) {
        if (e.type() == Type.BOOL) return e;
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

    private static Expr toAny(Expr e) {
        if (e.type() == Type.ANY) return e;
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

    private static final class P {
        final String src;
        int pos;
        P(String src) { this.src = src; this.pos = 0; }

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

        /** {@code '!' parseNot | parseCompare} — matches ScriptFormula.Parser#parseNot exactly
         *  (right-recursive so {@code !!x} works), sitting ABOVE compare, not near primary: real
         *  grammar precedence means {@code !a == b} parses as {@code !(a == b)}, not {@code (!a)
         *  == b}. The omitted bitwise/shift layer sits between this and compare in the real
         *  grammar; skipping it is safe (see class doc). */
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
            return parseCompare();
        }

        /** Exactly ONE optional relational operator — chained compares aren't part of the real
         *  grammar either (ScriptFormula.Parser#parseCompare never loops).
         *
         *  <p>{@code ==}/{@code !=} bail whenever either operand is ANY-typed — see class doc for
         *  why (Null/Str special-casing in the interpreter this compiler can't safely predict
         *  statically). {@code >}/{@code <}/{@code >=}/{@code <=} are unconditionally numeric in
         *  the interpreter regardless of operand type, so they stay safe with an ANY operand via
         *  {@link #toNum}. */
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
            if (equality && (left.type() == Type.ANY || right.type() == Type.ANY)) return null;

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

        /** {@code +} bails on an ANY operand (string-concat special case — see class doc);
         *  {@code -} is unconditionally numeric in the interpreter, safe with ANY via
         *  {@link #toNum} either way. */
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
                if (c == '+' && (left.type() == Type.ANY || right.type() == Type.ANY)) return null;
                Expr l = toNum(left), r = toNum(right);
                int insn = c == '+' ? DADD : DSUB;
                left = new BaseExpr(Type.NUM) {
                    @Override public void emit(MethodVisitor mv, Ctx c2) { l.emit(mv, c2); r.emit(mv, c2); mv.visitInsn(insn); }
                };
            }
            return left;
        }

        /** {@code *} is a plain DMUL. {@code /} and {@code %} replicate the interpreter's
         *  divide-by-zero guard EXACTLY (returns {@code 0.0} instead of the raw IEEE
         *  Infinity/NaN a bare DDIV/DREM would produce) — see {@link #divOrMod}. */
        Expr parseMul() {
            Expr left = parseUnary();
            if (left == null) return null;
            while (true) {
                skipSpaces();
                char c = pos < src.length() ? src.charAt(pos) : 0;
                if (c != '*' && c != '/' && c != '%') break;
                pos++;
                Expr right = parseUnary();
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

        Expr parsePrimary() {
            skipSpaces();
            if (pos >= src.length()) return null;
            char c = src.charAt(pos);

            if (c == '(') {
                pos++;
                Expr inner = parseTernary();
                if (inner == null || !match(")")) return null;
                return inner;
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

                // Cross-file ".pf:" call reference — not modeled here, bail.
                if (src.startsWith(".pf:", pos)) return null;

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

                // Plain variable read: ctx.getVar(name) — kept as ANY (not eagerly asNum()'d) so
                // a formula that's just a bare passthrough (a Map/array/Item variable evaluated
                // via a caller that wants the real value, not a coerced number) keeps its actual
                // type; NUM/BOOL contexts coerce it via toNum()/toBool() same as the interpreter's
                // implicit asNum()/asBool().
                String varName = name;
                return new BaseExpr(Type.ANY) {
                    @Override public void emit(MethodVisitor mv, Ctx c) {
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitLdcInsn(varName);
                        mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getVar", "(Ljava/lang/String;)L" + VALUE + ";", false);
                    }
                };
            }

            return null; // string literal, '$var', '[', or anything else unsupported
        }

        /** Parses a parenthesized, comma-separated arg list whose opening '(' has already been
         *  consumed. Returns null (bail) on any malformed arg — matches ScriptFormula.Parser's
         *  own parseArgs shape, minus its ".." range-literal sugar (unsupported here; a range arg
         *  just bails the whole formula to the interpreter). */
        List<Expr> parseArgList() {
            List<Expr> args = new ArrayList<>();
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == ')') { pos++; return args; }
            while (true) {
                Expr a = parseTernary();
                if (a == null) return null;
                args.add(a);
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ',') { pos++; continue; }
                if (pos < src.length() && src.charAt(pos) == ')') { pos++; break; }
                return null;
            }
            return args;
        }

        private static Expr numLit(double v) {
            return new BaseExpr(Type.NUM) {
                @Override public void emit(MethodVisitor mv, Ctx c) { mv.visitLdcInsn(v); }
            };
        }

        private static Expr boolLit(boolean v) {
            return new BaseExpr(Type.BOOL) {
                @Override public void emit(MethodVisitor mv, Ctx c) { mv.visitInsn(v ? ICONST_1 : ICONST_0); }
            };
        }

        /** {@code Name.prop} — mirrors the interpreter's dot-access property branch exactly:
         *  {@code sv = ctx.getClassInstance(name); if (sv==NULL) sv = ctx.getVar(name); return
         *  sv!=NULL ? memberGet(sv, prop, ctx) : NULL;} */
        private static Expr dotPropertyGet(String name, String prop) {
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int svSlot = c.allocRef();
                    emitResolveInstanceOrVar(mv, name, svSlot);
                    mv.visitVarInsn(ALOAD, svSlot);
                    emitGetNull(mv);
                    Label isNullL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IF_ACMPEQ, isNullL);
                    mv.visitVarInsn(ALOAD, svSlot);
                    mv.visitLdcInsn(prop);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "memberGet",
                            "(L" + VALUE + ";Ljava/lang/String;L" + CTX + ";)L" + VALUE + ";", false);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(isNullL);
                    emitGetNull(mv);
                    mv.visitLabel(endL);
                }
            };
        }

        /** {@code Name.method(args)} — same resolve-then-null-guard shape as {@link
         *  #dotPropertyGet}, but via {@code memberCall} with an evaluated arg list; args are only
         *  evaluated once {@code sv} is confirmed non-null, matching the interpreter's own
         *  short-circuit order. */
        private static Expr dotMethodCall(String name, String method, List<Expr> rawArgs) {
            List<Expr> args = rawArgs.stream().map(ScriptBytecodeCompiler::toAny).toList();
            return new BaseExpr(Type.ANY) {
                @Override public void emit(MethodVisitor mv, Ctx c) {
                    int svSlot = c.allocRef();
                    emitResolveInstanceOrVar(mv, name, svSlot);
                    mv.visitVarInsn(ALOAD, svSlot);
                    emitGetNull(mv);
                    Label isNullL = new Label(), endL = new Label();
                    mv.visitJumpInsn(IF_ACMPEQ, isNullL);
                    mv.visitVarInsn(ALOAD, svSlot);
                    mv.visitLdcInsn(method);
                    int listSlot = c.allocRef();
                    emitBuildArgsList(mv, c, args, listSlot);
                    mv.visitVarInsn(ALOAD, listSlot);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "memberCall",
                            "(L" + VALUE + ";Ljava/lang/String;L" + LIST + ";L" + CTX + ";)L" + VALUE + ";", false);
                    mv.visitJumpInsn(GOTO, endL);
                    mv.visitLabel(isNullL);
                    emitGetNull(mv);
                    mv.visitLabel(endL);
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
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, FORMULA, "callBuiltin",
                            "(Ljava/lang/String;L" + LIST + ";L" + CTX + ";)L" + VALUE + ";", false);
                }
            };
        }

        /** {@code sv = ctx.getClassInstance(name); if (sv==NULL) sv = ctx.getVar(name);} — stores
         *  the result in {@code svSlot}. */
        private static void emitResolveInstanceOrVar(MethodVisitor mv, String name, int svSlot) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getClassInstance", "(Ljava/lang/String;)L" + VALUE + ";", false);
            mv.visitVarInsn(ASTORE, svSlot);
            mv.visitVarInsn(ALOAD, svSlot);
            emitGetNull(mv);
            Label haveInstanceL = new Label();
            mv.visitJumpInsn(IF_ACMPNE, haveInstanceL);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEVIRTUAL, CTX, "getVar", "(Ljava/lang/String;)L" + VALUE + ";", false);
            mv.visitVarInsn(ASTORE, svSlot);
            mv.visitLabel(haveInstanceL);
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

        private static void emitGetNull(MethodVisitor mv) {
            mv.visitFieldInsn(GETSTATIC, VALUE, "NULL", "L" + VALUE + ";");
        }
    }
}
