package dev.arubik.craftengine.script;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Typed expression engine for the PolyFill scripting system.
 * Completely NMS-based — no Bukkit types anywhere.
 *
 * <p>Grammar supports:
 * <ul>
 *   <li>String literals — {@code "text"} or {@code 'text'}</li>
 *   <li>Variable references — {@code $name} or bare {@code name}</li>
 *   <li>Class member access — {@code Machine.rpm}, {@code Machine.slot(9)}</li>
 *   <li>Built-in functions — {@code is_empty(item)}, {@code item_id(item)}, math, etc.</li>
 *   <li>Range notation — {@code slots(9..15)}</li>
 *   <li>Null-coalescing — {@code slot(9) ?? "air"}</li>
 *   <li>Ternary — {@code condition ? a : b}</li>
 * </ul>
 *
 * <p>Compile once, evaluate many times:
 * <pre>{@code
 * ScriptFormula f = ScriptFormula.compile("is_empty(slot(0)) || rpm > 200");
 * boolean ok = f.evaluateBool(ctx);
 * }</pre>
 */
public final class ScriptFormula {

    // Formula strings come from static machine/renderer/item JSON config and are re-evaluated
    // every tick per active instance (a renderer's rot_x/location/when, an action script's
    // conditions, ...) — without this, "compile once, evaluate many times" (see class docs) was
    // violated by every caller that didn't hand-roll its own cache (MachineRenderContext.evalNum/
    // evalBool included), meaning the SAME expression string got fully re-parsed into a fresh AST
    // on every single tick, for every machine/renderer instance using it. Node is a pure closure
    // over ScriptContext (no mutable instance state), so a compiled ScriptFormula is safe to share
    // across calls and threads — caching by the exact string is a correct, unbounded-but-small
    // cache since the set of distinct expressions in use is fixed by config, not by tick count.
    private static final java.util.concurrent.ConcurrentHashMap<String, ScriptFormula> CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public static ScriptFormula compile(String expr) {
        if (expr == null) throw new IllegalArgumentException("ScriptFormula: expression must not be null");
        ScriptFormula cached = CACHE.get(expr);
        if (cached != null) return cached;
        ScriptFormula compiled = doCompile(expr);
        CACHE.put(expr, compiled);
        return compiled;
    }

    /** Kill switch for the whole ScriptBytecodeCompiler JIT path. The JIT has already needed
     *  several live-testing-driven correctness fixes (precedence, ==/!= and + type-polymorphism,
     *  divide-by-zero semantics, bare-identifier classInstance resolution) and may still have more
     *  latent bugs affecting renderer/menu formulas specifically (evaluated far more often, and
     *  with far more Machine dot-access variety, than what's been directly stress-tested so far).
     *  Flip to {@code false} to fall back to the always-correct interpreter for every formula if a
     *  new correctness issue is suspected. */
    static volatile boolean JIT_ENABLED = true;

    /** Test-only: compiles {@code expr} FRESH (bypassing {@link #CACHE} entirely) with the JIT
     *  forced on or off, so a test can get both the JIT'd and the interpreted {@link ScriptFormula}
     *  for the exact same string and compare their evaluation results directly — a differential
     *  check for exactly the class of bug that produces NO exception and NO log line anywhere (the
     *  JIT computing a plain wrong VALUE for some real formula), which every other diagnostic added
     *  this session is blind to. */
    static ScriptFormula recompileForTest(String expr, boolean jit) {
        boolean prev = JIT_ENABLED;
        JIT_ENABLED = jit;
        try { return doCompile(expr); }
        finally { JIT_ENABLED = prev; }
    }

    /** Test-only: every distinct expression string compiled (via either path) since the JVM
     *  started — {@link #CACHE} already accumulates exactly this as a side effect of normal
     *  operation. */
    static java.util.Set<String> compiledExpressionsForTest() { return CACHE.keySet(); }

    private static ScriptFormula doCompile(String expr) {
        // The lambda-tree interpreter is built HERE, eagerly, because it is also the validator: a
        // malformed expression must still throw at compile time, where every caller already handles
        // it. The bytecode JIT is NOT attempted here — see the class doc's "When the JIT runs".
        Parser p = new Parser(expr.trim());
        Node root = p.parseExpr();
        p.skipSpaces();
        if (p.pos < p.src.length()) {
            throw new IllegalArgumentException(
                    "ScriptFormula: unexpected '" + p.src.charAt(p.pos)
                    + "' at pos " + p.pos + " in: " + expr);
        }
        return new ScriptFormula(expr, root);
    }

    public ScriptValue evaluate(ScriptContext ctx) {
        return node().eval(ctx);
    }

    public boolean evaluateBool(ScriptContext ctx) {
        return node().eval(ctx).asBool();
    }

    public double evaluateNum(ScriptContext ctx) {
        return node().eval(ctx).asNum();
    }

    public String evaluateStr(ScriptContext ctx) {
        return node().eval(ctx).asStr();
    }

    public ItemStack evaluateItem(ScriptContext ctx) {
        ScriptValue v = node().eval(ctx);
        return (v instanceof ScriptValue.Item i) ? i.stack() : null;
    }

    /**
     * The node to evaluate: the JIT-generated one once it exists, otherwise the interpreter tree.
     *
     * <p>The JIT is attempted on FIRST EVALUATION rather than at compile time, and that timing is
     * the point. {@code ScriptProgram}'s parser compiles a {@link ScriptFormula} for every
     * expression in a {@code .pf} file as it builds the statements — so compiling eagerly generated
     * a hidden class per expression for the whole script tree at load, roughly two thousand of them
     * on a real server, and {@link ScriptClassCompiler} then compiled those very same expressions
     * AGAIN into one class per file. Every one of those hidden classes was dead weight: a file the
     * class JIT handles never evaluates its statements' formulas at all.
     *
     * <p>Deferring costs nothing where the JIT is still wanted. An expression that IS evaluated
     * directly — a renderer, a machine-definition field, anything embedded in config rather than in
     * a {@code .pf} — JITs on its first evaluation and is cached from then on, exactly as before.
     * A {@code .pf} file the class JIT declines does the same for its statements.
     *
     * <p>Any failure still falls through to the interpreter tree, which is why a codegen fault can
     * never surface as a script failure.
     */
    private Node node() {
        Node n = jit;
        if (n != null) return n;
        if (jitAttempted) return root;
        synchronized (this) {
            if (!jitAttempted) {
                Node compiled = null;
                if (JIT_ENABLED) {
                    try { compiled = ScriptBytecodeCompiler.tryCompile(rawExpr.trim()); }
                    catch (Throwable ignored) { compiled = null; }
                }
                jit = compiled;
                jitAttempted = true;
            }
        }
        Node compiled = jit;
        return compiled != null ? compiled : root;
    }

    @Override
    public String toString() { return rawExpr; }

    private final String rawExpr;
    /** The lambda-tree interpreter — always present, always correct, the fallback for everything. */
    private final Node root;
    /** The bytecode-JIT node once {@link #node()} has produced one; null if it never will. */
    private volatile Node jit;
    private volatile boolean jitAttempted;

    private ScriptFormula(String rawExpr, Node root) {
        this.rawExpr = rawExpr;
        this.root = root;
    }

    // Package-private (not private): ScriptBytecodeCompiler needs to generate real classes that
    // implement this interface directly, bypassing the lambda-closure tree entirely for the
    // pure-numeric/boolean subset of the grammar it recognizes — see that class's doc for why.
    @FunctionalInterface
    interface Node {
        ScriptValue eval(ScriptContext ctx);
    }

    // ---- Built-in functions ------------------------------------------------

    // Package-private (not private): ScriptBytecodeCompiler's generated bytecode calls this
    // directly (INVOKESTATIC) as its fallback for any function-call name it doesn't recognize as
    // one of its own fast Math-backed builtins — this is what lets a compiled numeric/boolean
    // formula still call a real builtin (item_count(...), etc.) or a user-defined function (a
    // top-level `def` bound via UserFunction) without having to bail on the WHOLE formula.
    // Public (not package-private): ScriptClassCompiler's generated classes live in a per-.pf-file
    // PACKAGE OF THEIR OWN (dev.arubik.craftengine.script.gen.<folder> — see that class's own doc
    // for why), a genuinely different runtime package from dev.arubik.craftengine.script regardless
    // of the shared prefix, so package-private access from generated bytecode throws a real
    // IllegalAccessError — caught live via a compiled-def-calling-another-def test, same class of
    // fix as ScriptProgram#resolveForRows.
    /**
     * Arity-specialized entry points for a compiled builtin call.
     *
     * <p>A call site used to build an {@code ArrayList} and {@code add} each argument into it, so
     * {@code size(ins)} — a one-argument read — cost an ArrayList plus its backing array, allocated
     * and thrown away on every evaluation, plus nine bytes of setup and eight per argument at each
     * of the ~480 builtin call sites in a real script corpus. {@code List.of} builds the small
     * fixed-size list directly from the operand stack: one object, no backing array, no adds.
     *
     * <p>These are thin forwarders, NOT a second implementation — every one of them ends up in the
     * same {@link #callBuiltin}, so the user-function override probe and every builtin behave
     * identically however the call site was compiled.
     */
    /**
     * A CraftEngine item by id, built once and copied thereafter.
     *
     * <p>Building one is a registry lookup, a full Bukkit ItemStack construction with every NBT
     * component the definition declares, and a conversion to NMS. It is also entirely determined by
     * the id — so a renderer whose {@code item} is {@code CraftEngineItem("cml:shaft_render")} was
     * doing all of that repeatedly for a value that cannot differ. In a server profile that build
     * was the single largest thing under the renderer's formula evaluation.
     *
     * <p>Callers get a {@code copy()}, never the cached stack: a ScriptValue.Item flows into script
     * code that may well mutate it, and handing out the shared instance would let one renderer's
     * edit appear in every other machine using the same id.
     *
     * <p>Cleared on reload, since that is when an item definition can change.
     */
    private static final java.util.concurrent.ConcurrentHashMap<String, ItemStack> CE_ITEM_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

    private static ItemStack craftEngineItem(String itemId) {
        ItemStack cached = CE_ITEM_CACHE.get(itemId);
        if (cached != null) return cached;
        try {
            // TODO: Use NMS-only CE item resolution when available
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(
                    net.momirealms.craftengine.core.util.Key.of(itemId));
            if (def != null) {
                org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                if (bukkit != null) {
                    ItemStack nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                    if (nms != null) {
                        CE_ITEM_CACHE.put(itemId, nms);
                        return nms;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return null; // not cached: a miss now may resolve after the item registry finishes loading
    }

    /** Drops the built-item cache. Called on reload, when a definition can genuinely change. */
    public static void clearItemCache() {
        CE_ITEM_CACHE.clear();
    }

    public static ScriptValue callBuiltin0(String name, ScriptContext ctx) {
        return callBuiltin(name, List.of(), ctx);
    }

    public static ScriptValue callBuiltin1(String name, ScriptValue a0, ScriptContext ctx) {
        return callBuiltin(name, List.of(a0), ctx);
    }

    public static ScriptValue callBuiltin2(String name, ScriptValue a0, ScriptValue a1, ScriptContext ctx) {
        return callBuiltin(name, List.of(a0, a1), ctx);
    }

    public static ScriptValue callBuiltin3(String name, ScriptValue a0, ScriptValue a1, ScriptValue a2,
                                            ScriptContext ctx) {
        return callBuiltin(name, List.of(a0, a1, a2), ctx);
    }

    public static ScriptValue callBuiltin(String name, List<ScriptValue> args, ScriptContext ctx) {
        // Check user-defined functions first (stored as ScriptValue.Obj with type "__func__").
        // peekVar, not getVar — this is an override PROBE, not a real read of `name` as data; see
        // ScriptContext#peekVar's javadoc for why routing it through getVar corrupts dependency
        // tracking.
        ScriptValue userFuncVal = ctx.peekVar(name);
        if (userFuncVal instanceof ScriptValue.Obj uo && UserFunction.TYPE.equals(uo.typeName())
                && uo.instance() instanceof UserFunction fn) {
            return fn.call(args, ctx);
        }

        // Check ScriptBuiltins registry
        ScriptBuiltins.BuiltinFunction registeredFn = ScriptBuiltins.get(name);
        if (registeredFn != null) {
            try { return registeredFn.call(args, ctx); } catch (Throwable ignored) {}
        }

        return switch (name) {

            case "slot" -> {
                ScriptValue inv = ctx.getClassInstance("Inventory");
                yield inv != ScriptValue.NULL ? inv.callMethod("slot", args) : ScriptValue.NULL;
            }

            case "slots" -> {
                ScriptValue inv = ctx.getClassInstance("Inventory");
                yield inv != ScriptValue.NULL ? inv.callMethod("slots", args) : ScriptValue.NULL;
            }

            case "is_empty" -> ScriptValue.of(args.isEmpty() || isNullOrEmpty(args.get(0)));
            case "is_not_empty" -> ScriptValue.of(!args.isEmpty() && !isNullOrEmpty(args.get(0)));

            case "has_item" -> {
                if (args.size() < 2) yield ScriptValue.of(false);
                yield ScriptValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
            }

            case "item_count" -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum());

            case "item_id" -> {
                if (args.isEmpty()) yield ScriptValue.NULL;
                ScriptValue v = args.get(0);
                if (!(v instanceof ScriptValue.Item i)) yield ScriptValue.NULL;
                if (i.stack() == null || i.stack().isEmpty()) yield ScriptValue.of("minecraft:air");
                yield ScriptValue.of(BuiltInRegistries.ITEM.getKey(i.stack().getItem()).toString());
            }

            case "matches" -> {
                if (args.size() < 2) yield ScriptValue.of(false);
                yield ScriptValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
            }

            case "not" -> ScriptValue.of(args.isEmpty() || !args.get(0).asBool());

            case "CraftEngineItem" -> {
                if (args.isEmpty()) yield ScriptValue.NULL;
                ItemStack built = craftEngineItem(args.get(0).asStr());
                yield built == null ? ScriptValue.NULL : ScriptValue.ofItem(built.copy());
            }

            case "MinecraftItem", "VanillaItem" -> {
                if (args.isEmpty()) yield ScriptValue.NULL;
                String id = args.get(0).asStr();
                try {
                    Identifier loc = Identifier.parse(id.contains(":") ? id : "minecraft:" + id);
                    var item = (net.minecraft.world.item.Item) BuiltInRegistries.ITEM.getValue(loc);
                    if (item != null && item != net.minecraft.world.item.Items.AIR) {
                        yield ScriptValue.ofItem(new ItemStack(item, 1));
                    }
                } catch (Throwable ignored) {}
                yield ScriptValue.NULL;
            }

            case "FluidTank" -> {
                ScriptValue tanksObj = ctx.getClassInstance("FluidTanks");
                if (tanksObj == ScriptValue.NULL || args.isEmpty()) yield ScriptValue.NULL;
                String tankName = args.get(0).asStr();
                String property = args.size() >= 2 ? args.get(1).asStr() : "fraction";
                ScriptValue tank = tanksObj.callMethod("get", List.of(ScriptValue.of(tankName)));
                yield tank.getProperty(property);
            }

            case "GasTank" -> {
                ScriptValue tanksObj = ctx.getClassInstance("GasTanks");
                if (tanksObj == ScriptValue.NULL || args.isEmpty()) yield ScriptValue.NULL;
                String tankName = args.get(0).asStr();
                String property = args.size() >= 2 ? args.get(1).asStr() : "fraction";
                ScriptValue tank = tanksObj.callMethod("get", List.of(ScriptValue.of(tankName)));
                yield tank.getProperty(property);
            }

            case "player_facing" -> {
                ScriptValue machine = ctx.getClassInstance("Machine");
                yield machine != ScriptValue.NULL ? machine.callMethod("player_facing", args) : ScriptValue.of(false);
            }
            case "player_in_range" -> {
                ScriptValue machine = ctx.getClassInstance("Machine");
                yield machine != ScriptValue.NULL ? machine.callMethod("player_in_range", args) : ScriptValue.of(false);
            }
            case "player_above" -> {
                ScriptValue machine = ctx.getClassInstance("Machine");
                yield machine != ScriptValue.NULL ? machine.callMethod("player_above", args) : ScriptValue.of(false);
            }
            case "player_below" -> {
                ScriptValue machine = ctx.getClassInstance("Machine");
                yield machine != ScriptValue.NULL ? machine.callMethod("player_below", args) : ScriptValue.of(false);
            }

            case "input" -> {
                ScriptValue wb = ctx.getClassInstance("Workbench");
                yield wb != ScriptValue.NULL ? wb.callMethod("input", args) : ScriptValue.NULL;
            }
            case "output" -> {
                ScriptValue wb = ctx.getClassInstance("Workbench");
                yield wb != ScriptValue.NULL ? wb.callMethod("output", args) : ScriptValue.NULL;
            }
            case "tool" -> {
                ScriptValue wb = ctx.getClassInstance("Workbench");
                yield wb != ScriptValue.NULL ? wb.callMethod("tool", args) : ScriptValue.NULL;
            }

            // ---- Trig ----
            case "sin"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.sin(args.get(0).asNum()));
            case "cos"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.cos(args.get(0).asNum()));
            case "tan"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.tan(args.get(0).asNum()));
            case "asin"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.asin(args.get(0).asNum()));
            case "acos"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.acos(args.get(0).asNum()));
            case "atan"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.atan(args.get(0).asNum()));
            case "atan2" -> ScriptValue.of(args.size() < 2 ? 0 : Math.atan2(args.get(0).asNum(), args.get(1).asNum()));
            case "deg"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.toDegrees(args.get(0).asNum()));
            case "rad"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.toRadians(args.get(0).asNum()));

            // ---- Math ----
            case "abs"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.abs(args.get(0).asNum()));
            case "sqrt"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.sqrt(args.get(0).asNum()));
            case "pow"   -> ScriptValue.of(args.size() < 2 ? 0 : Math.pow(args.get(0).asNum(), args.get(1).asNum()));
            case "log"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.log(args.get(0).asNum()));
            case "log10" -> ScriptValue.of(args.isEmpty() ? 0 : Math.log10(args.get(0).asNum()));
            case "exp"   -> ScriptValue.of(args.isEmpty() ? 0 : Math.exp(args.get(0).asNum()));
            case "sign"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.signum(args.get(0).asNum()));

            // ---- Rounding ----
            case "floor" -> ScriptValue.of(args.isEmpty() ? 0 : Math.floor(args.get(0).asNum()));
            case "ceil"  -> ScriptValue.of(args.isEmpty() ? 0 : Math.ceil(args.get(0).asNum()));
            case "round" -> ScriptValue.of(args.isEmpty() ? 0 : Math.round(args.get(0).asNum()));
            case "trunc" -> ScriptValue.of(args.isEmpty() ? 0 : (long) args.get(0).asNum());

            // ---- Min / max / clamp / lerp / map ----
            case "min"   -> ScriptValue.of(args.size() < 2 ? 0 : Math.min(args.get(0).asNum(), args.get(1).asNum()));
            case "max"   -> ScriptValue.of(args.size() < 2 ? 0 : Math.max(args.get(0).asNum(), args.get(1).asNum()));
            case "clamp" -> {
                if (args.size() < 3) yield ScriptValue.of(0);
                double v = args.get(0).asNum(), lo = args.get(1).asNum(), hi = args.get(2).asNum();
                yield ScriptValue.of(Math.max(lo, Math.min(hi, v)));
            }
            case "lerp" -> {
                if (args.size() < 3) yield ScriptValue.of(0);
                double a = args.get(0).asNum(), b = args.get(1).asNum(), t = args.get(2).asNum();
                yield ScriptValue.of(a + (b - a) * t);
            }
            case "map" -> {
                if (args.size() == 2 && args.get(0) instanceof ScriptValue.Array a0) {
                    String mapExpr = args.get(1).asStr();
                    List<ScriptValue> mapped = new ArrayList<>(a0.elements().size());
                    for (ScriptValue elem : a0.elements()) {
                        try {
                            ScriptContext elemCtx = ScriptContext.builder().copyFrom(ctx)
                                    .val("e", elem).val("element", elem).build();
                            mapped.add(ScriptFormula.compile(mapExpr).evaluate(elemCtx));
                        } catch (Throwable ignored) { mapped.add(ScriptValue.NULL); }
                    }
                    yield new ScriptValue.Array(mapped);
                }
                if (args.size() < 5) yield ScriptValue.of(0);
                double v = args.get(0).asNum(), fLo = args.get(1).asNum(), fHi = args.get(2).asNum();
                double tLo = args.get(3).asNum(), tHi = args.get(4).asNum();
                double t = fHi != fLo ? (v - fLo) / (fHi - fLo) : 0;
                yield ScriptValue.of(tLo + (tHi - tLo) * t);
            }

            case "filter" -> {
                if (args.size() < 2) yield args.isEmpty() ? ScriptValue.NULL : args.get(0);
                ScriptValue arrF = args.get(0);
                String filterExpr = args.get(1).asStr();
                if (!(arrF instanceof ScriptValue.Array af)) yield arrF;
                List<ScriptValue> filtered = new ArrayList<>();
                for (ScriptValue elem : af.elements()) {
                    try {
                        ScriptContext elemCtx = ScriptContext.builder().copyFrom(ctx)
                                .val("e", elem).val("element", elem).build();
                        if (ScriptFormula.compile(filterExpr).evaluateBool(elemCtx)) filtered.add(elem);
                    } catch (Throwable ignored) {}
                }
                yield new ScriptValue.Array(filtered);
            }

            case "reduce" -> {
                if (args.size() < 3) yield ScriptValue.NULL;
                ScriptValue arrR = args.get(0);
                ScriptValue acc = args.get(1);
                String reduceExpr = args.get(2).asStr();
                if (!(arrR instanceof ScriptValue.Array ar)) yield acc;
                for (ScriptValue elem : ar.elements()) {
                    try {
                        ScriptContext elemCtx = ScriptContext.builder().copyFrom(ctx)
                                .val("acc", acc).val("accumulator", acc)
                                .val("e", elem).val("element", elem).build();
                        acc = ScriptFormula.compile(reduceExpr).evaluate(elemCtx);
                    } catch (Throwable ignored) {}
                }
                yield acc;
            }

            case "flat", "flatten" -> {
                if (args.isEmpty()) yield ScriptValue.NULL;
                if (!(args.get(0) instanceof ScriptValue.Array aFlat)) yield args.get(0);
                List<ScriptValue> flat = new ArrayList<>();
                for (ScriptValue elem : aFlat.elements()) {
                    if (elem instanceof ScriptValue.Array inner) flat.addAll(inner.elements());
                    else flat.add(elem);
                }
                yield new ScriptValue.Array(flat);
            }

            case "range" -> {
                if (args.isEmpty()) yield new ScriptValue.Array(List.of());
                double rStart = args.size() > 1 ? args.get(0).asNum() : 0;
                double rEnd = args.size() > 1 ? args.get(1).asNum() : args.get(0).asNum();
                double rStep = args.size() > 2 ? args.get(2).asNum() : 1;
                if (rStep == 0 || Math.abs((rEnd - rStart) / rStep) > 10_000) yield new ScriptValue.Array(List.of());
                List<ScriptValue> rangeList = new ArrayList<>();
                if (rStep > 0) { for (double rv = rStart; rv < rEnd; rv += rStep) rangeList.add(ScriptValue.of(rv)); }
                else           { for (double rv = rStart; rv > rEnd; rv += rStep) rangeList.add(ScriptValue.of(rv)); }
                yield new ScriptValue.Array(rangeList);
            }

            case "zip" -> {
                if (args.size() < 2) yield ScriptValue.NULL;
                if (!(args.get(0) instanceof ScriptValue.Array az) || !(args.get(1) instanceof ScriptValue.Array bz))
                    yield ScriptValue.NULL;
                int zLen = Math.min(az.elements().size(), bz.elements().size());
                List<ScriptValue> zipped = new ArrayList<>(zLen);
                for (int zi = 0; zi < zLen; zi++)
                    zipped.add(new ScriptValue.Array(List.of(az.elements().get(zi), bz.elements().get(zi))));
                yield new ScriptValue.Array(zipped);
            }

            case "unique" -> {
                if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array au)) yield args.isEmpty() ? ScriptValue.NULL : args.get(0);
                java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
                List<ScriptValue> unique = new ArrayList<>();
                for (ScriptValue elem : au.elements()) {
                    if (seen.add(elem.asStr())) unique.add(elem);
                }
                yield new ScriptValue.Array(unique);
            }

            case "array" -> new ScriptValue.Array(new ArrayList<>(args));

            case "size", "length" -> {
                if (args.isEmpty()) yield ScriptValue.of(0);
                ScriptValue av = args.get(0);
                if (av instanceof ScriptValue.Array a) yield ScriptValue.of(a.elements().size());
                if (av instanceof ScriptValue.Str s) yield ScriptValue.of(s.value().length());
                yield ScriptValue.of(0);
            }
            case "mod"  -> ScriptValue.of(args.size() < 2 || args.get(1).asNum() == 0 ? 0 : args.get(0).asNum() % args.get(1).asNum());
            case "frac" -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum() - Math.floor(args.get(0).asNum()));
            case "step" -> ScriptValue.of(args.size() < 2 ? 0 : (args.get(1).asNum() >= args.get(0).asNum() ? 1.0 : 0.0));
            case "smoothstep" -> {
                if (args.size() < 3) yield ScriptValue.of(0);
                double e0 = args.get(0).asNum(), e1 = args.get(1).asNum(), x = args.get(2).asNum();
                double t = Math.max(0, Math.min(1, (x - e0) / (e1 - e0)));
                yield ScriptValue.of(t * t * (3 - 2 * t));
            }

            case "int"   -> ScriptValue.of(args.isEmpty() ? 0L : (long) args.get(0).asNum());
            case "float" -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum());
            case "bool"  -> ScriptValue.of(args.isEmpty() ? false : args.get(0).asBool());
            case "str"   -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr());

            case "if" -> {
                if (args.size() < 3) yield ScriptValue.NULL;
                yield args.get(0).asBool() ? args.get(1) : args.get(2);
            }

            case "count" -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum());
            case "sum" -> {
                if (args.isEmpty()) yield ScriptValue.of(0);
                ScriptValue v = args.get(0);
                if (v instanceof ScriptValue.Array a) {
                    yield ScriptValue.of(a.elements().stream().mapToDouble(ScriptValue::asNum).sum());
                }
                yield ScriptValue.of(v.asNum());
            }
            case "any", "some" -> {
                if (args.isEmpty()) yield ScriptValue.of(false);
                if (args.get(0) instanceof ScriptValue.Array a) {
                    yield ScriptValue.of(a.elements().stream().anyMatch(ScriptValue::asBool));
                }
                yield ScriptValue.of(args.get(0).asBool());
            }
            case "all" -> {
                if (args.isEmpty()) yield ScriptValue.of(true);
                if (args.get(0) instanceof ScriptValue.Array a) {
                    yield ScriptValue.of(a.elements().stream().allMatch(ScriptValue::asBool));
                }
                yield ScriptValue.of(args.get(0).asBool());
            }
            case "none" -> {
                if (args.isEmpty()) yield ScriptValue.of(true);
                if (args.get(0) instanceof ScriptValue.Array a) {
                    yield ScriptValue.of(a.elements().stream().noneMatch(ScriptValue::asBool));
                }
                yield ScriptValue.of(!args.get(0).asBool());
            }

            case "format" -> {
                if (args.isEmpty()) yield ScriptValue.of("");
                String fmt = args.get(0).asStr();
                Object[] fmtArgs = args.subList(1, args.size()).stream()
                    .map(a -> a instanceof ScriptValue.Num n ? (Object) n.value()
                            : a instanceof ScriptValue.Bool b ? (Object) b.value()
                            : (Object) a.asStr())
                    .toArray();
                try { yield ScriptValue.of(String.format(fmt, fmtArgs)); }
                catch (Throwable ignored) { yield ScriptValue.of(fmt); }
            }
            case "concat" -> {
                StringBuilder sb = new StringBuilder();
                for (ScriptValue a : args) sb.append(a.asStr());
                yield ScriptValue.of(sb.toString());
            }
            case "substring" -> {
                if (args.isEmpty()) yield ScriptValue.of("");
                String s = args.get(0).asStr();
                int start = args.size() > 1 ? (int) args.get(1).asNum() : 0;
                int end = args.size() > 2 ? (int) args.get(2).asNum() : s.length();
                try { yield ScriptValue.of(s.substring(Math.max(0, start), Math.min(s.length(), end))); }
                catch (Throwable ignored) { yield ScriptValue.of(""); }
            }
            case "replace" -> {
                if (args.size() < 3) yield ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr());
                yield ScriptValue.of(args.get(0).asStr().replace(args.get(1).asStr(), args.get(2).asStr()));
            }
            case "contains" -> {
                if (args.size() < 2) yield ScriptValue.of(false);
                ScriptValue container = args.get(0);
                if (container instanceof ScriptValue.Str s) {
                    yield ScriptValue.of(s.value().contains(args.get(1).asStr()));
                }
                if (container instanceof ScriptValue.Array a) {
                    String target = args.get(1).asStr();
                    yield ScriptValue.of(a.elements().stream().anyMatch(e -> e.asStr().equals(target)));
                }
                yield ScriptValue.of(false);
            }

            // split(str, delim) -> Array<Str>, join(array, delim) -> Str. The generic pair every
            // *_flag store (Machine/Entity/World/Server) needed and lacked: those are flat
            // string->string maps, so anything shaped like a LIST (linked positions, a frequency's
            // members, ...) has to be encoded into one string by the script itself — this is the
            // encode/decode half of that, previously a documented gap ("no string-split builtin").
            case "split" -> {
                if (args.size() < 2) yield new ScriptValue.Array(java.util.List.of());
                String s = args.get(0).asStr();
                String delim = args.get(1).asStr();
                if (s.isEmpty() || delim.isEmpty()) yield new ScriptValue.Array(java.util.List.of());
                String[] parts = s.split(java.util.regex.Pattern.quote(delim), -1);
                java.util.List<ScriptValue> out = new java.util.ArrayList<>(parts.length);
                for (String p : parts) out.add(ScriptValue.of(p));
                yield new ScriptValue.Array(out);
            }
            case "join" -> {
                if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array a)) yield ScriptValue.of("");
                String delim = args.size() >= 2 ? args.get(1).asStr() : ",";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < a.elements().size(); i++) {
                    if (i > 0) sb.append(delim);
                    sb.append(a.elements().get(i).asStr());
                }
                yield ScriptValue.of(sb.toString());
            }

            // world(name) -> World, resolving a dimension key string (e.g. "minecraft:the_nether",
            // matching World.name's own format) back to a live World object — the counterpart any
            // cross-dimension feature needs once it has stored a world name as plain text (see
            // Server.*_flag) and wants to act on it later (build a Location, teleport into it, ...).
            case "world" -> {
                if (args.isEmpty()) yield ScriptValue.NULL;
                try {
                    String dimName = args.get(0).asStr();
                    net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(dimName);
                    net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> key =
                            net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);
                    net.minecraft.server.level.ServerLevel level =
                            net.minecraft.server.MinecraftServer.getServer().getLevel(key);
                    yield level != null ? dev.arubik.craftengine.script.types.world.WorldType.wrap(level) : ScriptValue.NULL;
                } catch (Throwable ignored) {
                    yield ScriptValue.NULL;
                }
            }

            case "tick" -> {
                try { yield ScriptValue.of(net.minecraft.server.MinecraftServer.getServer().getTickCount()); }
                catch (Throwable ignored) {}
                yield ScriptValue.of(0);
            }
            case "seconds" -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum() / 20.0);
            case "ticks"   -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum() * 20.0);

            case "random" -> {
                var rng = java.util.concurrent.ThreadLocalRandom.current();
                if (args.isEmpty()) yield ScriptValue.of(rng.nextDouble());
                if (args.size() == 1) yield ScriptValue.of(rng.nextDouble() * args.get(0).asNum());
                double lo = args.get(0).asNum(), hi = args.get(1).asNum();
                yield ScriptValue.of(lo + rng.nextDouble() * (hi - lo));
            }
            case "random_int" -> {
                var rng = java.util.concurrent.ThreadLocalRandom.current();
                if (args.isEmpty()) yield ScriptValue.of(0);
                int hi = (int) args.get(args.size()-1).asNum();
                int lo = args.size() > 1 ? (int) args.get(0).asNum() : 0;
                yield hi <= lo ? ScriptValue.of(lo) : ScriptValue.of(lo + rng.nextInt(hi - lo));
            }

            case "distance" -> {
                if (args.size() < 6) yield ScriptValue.of(0);
                double dx=args.get(0).asNum()-args.get(3).asNum(),
                       dy=args.get(1).asNum()-args.get(4).asNum(),
                       dz=args.get(2).asNum()-args.get(5).asNum();
                yield ScriptValue.of(Math.sqrt(dx*dx+dy*dy+dz*dz));
            }
            case "distance_sq" -> {
                if (args.size() < 6) yield ScriptValue.of(0);
                double dx=args.get(0).asNum()-args.get(3).asNum(),
                       dy=args.get(1).asNum()-args.get(4).asNum(),
                       dz=args.get(2).asNum()-args.get(5).asNum();
                yield ScriptValue.of(dx*dx+dy*dy+dz*dz);
            }

            case "world_time" -> {
                ScriptValue w = ctx.getClassInstance("World");
                yield w != ScriptValue.NULL ? w.getProperty("time") : ScriptValue.of(6000);
            }
            case "world_day" -> {
                ScriptValue w = ctx.getClassInstance("World");
                yield w != ScriptValue.NULL ? w.getProperty("is_day") : ScriptValue.of(true);
            }

            case "item_has_tag" -> {
                if (args.size() < 2) yield ScriptValue.of(false);
                if (!(args.get(0) instanceof ScriptValue.Item i) || i.stack() == null)
                    yield ScriptValue.of(false);
                // Delegate to Item type's is_tagged method
                ScriptValue itemObj = ScriptValue.ofObj("Item", i.stack());
                yield itemObj.callMethod("is_tagged", List.of(args.get(1)));
            }

            case "coalesce" -> {
                for (ScriptValue a : args) if (!(a instanceof ScriptValue.Null)) yield a;
                yield ScriptValue.NULL;
            }

            case "within" -> {
                if (args.size() < 3) yield ScriptValue.of(false);
                yield ScriptValue.of(Math.abs(args.get(0).asNum()-args.get(1).asNum()) <= args.get(2).asNum());
            }

            case "ping" -> {
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Obj o
                        && o.instance() instanceof net.minecraft.server.level.ServerPlayer sp) {
                    try { yield ScriptValue.of(sp.connection.latency()); } catch (Throwable ignored) {}
                }
                yield ScriptValue.of(0);
            }

            case "rgb" -> {
                if (args.size() < 3) yield ScriptValue.of(0);
                int r = (int) clampD(args.get(0).asNum(), 0, 255);
                int g = (int) clampD(args.get(1).asNum(), 0, 255);
                int b = (int) clampD(args.get(2).asNum(), 0, 255);
                yield ScriptValue.of((0xFF000000L | ((long) r << 16) | ((long) g << 8) | b));
            }
            case "rgba" -> {
                if (args.size() < 4) yield ScriptValue.of(0);
                int r = (int) clampD(args.get(0).asNum(), 0, 255);
                int g = (int) clampD(args.get(1).asNum(), 0, 255);
                int b = (int) clampD(args.get(2).asNum(), 0, 255);
                int a = (int) clampD(args.get(3).asNum(), 0, 255);
                yield ScriptValue.of(((long) a << 24) | ((long) r << 16) | ((long) g << 8) | b);
            }

            case "type_of" -> {
                if (args.isEmpty()) yield ScriptValue.of("null");
                yield ScriptValue.of(switch (args.get(0)) {
                    case ScriptValue.Num ignored -> "num";
                    case ScriptValue.Bool ignored -> "bool";
                    case ScriptValue.Str ignored -> "str";
                    case ScriptValue.Item ignored -> "item";
                    case ScriptValue.Array ignored -> "array";
                    case ScriptValue.Obj ignored -> "obj";
                    case ScriptValue.Null ignored -> "null";
                });
            }
            case "is_null"  -> ScriptValue.of(args.isEmpty() || args.get(0) instanceof ScriptValue.Null);
            case "is_num"   -> ScriptValue.of(!args.isEmpty() && args.get(0) instanceof ScriptValue.Num);
            case "is_str"   -> ScriptValue.of(!args.isEmpty() && args.get(0) instanceof ScriptValue.Str);
            case "is_item"  -> ScriptValue.of(!args.isEmpty() && args.get(0) instanceof ScriptValue.Item);
            case "is_array" -> ScriptValue.of(!args.isEmpty() && args.get(0) instanceof ScriptValue.Array);

            // make_map(k1,v1,k2,v2,...) — script-level map constructor
            case "make_map" -> {
                java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                for (int i = 0; i + 1 < args.size(); i += 2) {
                    map.put(args.get(i).asStr(), args.get(i + 1));
                }
                yield ScriptValue.ofObj("Map", map);
            }

            case "num"  -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum());

            default -> ctx.getVar(name);
        };
    }

    private static double clampD(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /** Characters allowed in the "func:arg1:arg2" tail of an inline "file.pf:func:args" script-call
     *  reference — deliberately narrow (identifier chars, ':', '.', '-') so it stops at whitespace,
     *  parens/brackets, and comparison/ternary operators rather than swallowing the rest of the
     *  surrounding expression. */
    private static boolean isScriptCallArgChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '.' || c == '-';
    }

    // ---- Polymorphic operator helpers ----
    //
    // Both of these used to be inlined separately as the Parser's own '+'/'=='/'!=' lambdas AND
    // (for '+') duplicated again as ScriptBytecodeCompiler's justification for bailing on an ANY
    // operand entirely. Extracted here as the single shared implementation BOTH the interpreter
    // (below) and the JIT (ScriptBytecodeCompiler's addExpr/equalityExpr) call, so a compiled
    // formula's '+'/'=='/'!=' on an ANY-typed operand no longer has to bail to the interpreter at
    // all — it emits a direct call to these same methods instead, never re-deriving the logic in
    // bytecode where it could silently drift from this one.

    /** {@code +}'s real polymorphic semantics: string concatenation if EITHER side is a {@link
     *  ScriptValue.Str}, numeric addition otherwise. */
    /**
     * The string rendering of a number, split out of {@link ScriptValue#asStr} so a compiled
     * string concatenation can render a {@code double} operand WITHOUT allocating the
     * {@link ScriptValue.Num} that would otherwise exist only to have {@code asStr()} called on it.
     *
     * <p>Must stay byte-identical to {@code asStr}'s {@code Num} branch — that branch now delegates
     * here, so there is one implementation rather than two that could drift.
     */
    public static String numToStr(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v) && Math.abs(v) < 1e15) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    public static ScriptValue addPolymorphic(ScriptValue lv, ScriptValue rv) {
        if (lv instanceof ScriptValue.Str || rv instanceof ScriptValue.Str) {
            return ScriptValue.of(lv.asStr() + rv.asStr());
        }
        return ScriptValue.of(lv.asNum() + rv.asNum());
    }

    /** {@code ==}/{@code !=}'s real polymorphic semantics, checked in order: if EITHER side is
     *  {@link ScriptValue.Null}, true only when BOTH are Null (reference-style null equality,
     *  never numeric/string coercion of a null); else if EITHER side is a {@link ScriptValue.Str},
     *  string equality; otherwise numeric equality. Returns the {@code ==} answer — a {@code !=}
     *  caller just negates it. */
    public static boolean valuesEqual(ScriptValue lv, ScriptValue rv) {
        if (lv instanceof ScriptValue.Null || rv instanceof ScriptValue.Null) {
            return lv instanceof ScriptValue.Null && rv instanceof ScriptValue.Null;
        }
        if (lv instanceof ScriptValue.Str || rv instanceof ScriptValue.Str) {
            return lv.asStr().equals(rv.asStr());
        }
        return lv.asNum() == rv.asNum();
    }

    /**
     * {@link #valuesEqual} specialised for a right-hand side that is a STRING LITERAL, so the
     * compiler doesn't have to allocate a {@code ScriptValue} wrapper just to compare against a
     * constant ({@code id == "minecraft:warped_stem"} runs per block, per tick).
     *
     * <p>Exactly equivalent, by construction: a string literal is always a {@code Str}, never
     * {@code Null}, so {@code valuesEqual}'s null branch can only return false, and its
     * either-side-is-Str branch is the one that always applies.
     */
    public static boolean valuesEqualStr(ScriptValue lv, String literal) {
        if (lv instanceof ScriptValue.Null) return false;
        return lv.asStr().equals(literal);
    }

    // ---- Member access helpers ----

    public static ScriptValue memberGet(ScriptValue obj, String prop, ScriptContext ctx) {
        return switch (obj) {
            case ScriptValue.Obj o -> o.instance() != null ? obj.getProperty(prop) : ScriptValue.NULL;
            case ScriptValue.Item i -> {
                ScriptValue itemObj = ScriptValue.ofObj("Item", i.stack());
                yield itemObj.getProperty(prop);
            }
            case ScriptValue.Array a -> switch (prop) {
                case "size", "length", "count" -> ScriptValue.of(a.elements().size());
                case "is_empty" -> ScriptValue.of(a.elements().isEmpty());
                case "first" -> a.elements().isEmpty() ? ScriptValue.NULL : a.elements().get(0);
                case "last"  -> a.elements().isEmpty() ? ScriptValue.NULL : a.elements().get(a.elements().size() - 1);
                default -> ScriptValue.NULL;
            };
            case ScriptValue.Str s -> switch (prop) {
                case "length" -> ScriptValue.of(s.value().length());
                case "upper"  -> ScriptValue.of(s.value().toUpperCase(java.util.Locale.ROOT));
                case "lower"  -> ScriptValue.of(s.value().toLowerCase(java.util.Locale.ROOT));
                default -> ScriptValue.NULL;
            };
            case ScriptValue.Num n -> switch (prop) {
                case "int"          -> ScriptValue.of((long) n.value());
                case "abs"          -> ScriptValue.of(Math.abs(n.value()));
                case "sign"         -> ScriptValue.of(Math.signum(n.value()));
                case "floor"        -> ScriptValue.of(Math.floor(n.value()));
                case "ceil"         -> ScriptValue.of(Math.ceil(n.value()));
                case "round"        -> ScriptValue.of(Math.round(n.value()));
                case "sqrt"         -> ScriptValue.of(Math.sqrt(n.value()));
                case "sq", "squared"-> ScriptValue.of(n.value() * n.value());
                case "is_nan"       -> ScriptValue.of(Double.isNaN(n.value()));
                case "is_infinite"  -> ScriptValue.of(Double.isInfinite(n.value()));
                default -> ScriptValue.NULL;
            };
            default -> {
                ScriptValue cls = ctx.getClassInstance(obj.asStr());
                yield cls != ScriptValue.NULL ? cls.getProperty(prop) : ScriptValue.NULL;
            }
        };
    }

    public static ScriptValue memberCall(ScriptValue obj, String method, List<ScriptValue> args, ScriptContext ctx) {
        return switch (obj) {
            case ScriptValue.Obj o -> o.instance() != null ? obj.callMethod(method, args) : ScriptValue.NULL;
            case ScriptValue.Item i -> {
                ScriptValue itemObj = ScriptValue.ofObj("Item", i.stack());
                yield itemObj.callMethod(method, args);
            }
            case ScriptValue.Num n -> switch (method) {
                case "int"          -> ScriptValue.of((long) n.value());
                case "abs"          -> ScriptValue.of(Math.abs(n.value()));
                case "floor"        -> ScriptValue.of(Math.floor(n.value()));
                case "ceil"         -> ScriptValue.of(Math.ceil(n.value()));
                case "round"        -> ScriptValue.of(Math.round(n.value()));
                case "sqrt"         -> ScriptValue.of(Math.sqrt(n.value()));
                case "sq", "squared"-> ScriptValue.of(n.value() * n.value());
                case "sign"         -> ScriptValue.of(Math.signum(n.value()));
                case "clamp" -> args.size() >= 2
                    ? ScriptValue.of(Math.max(args.get(0).asNum(), Math.min(args.get(1).asNum(), n.value())))
                    : ScriptValue.of(n.value());
                case "pow"   -> ScriptValue.of(args.isEmpty() ? n.value() : Math.pow(n.value(), args.get(0).asNum()));
                case "max"   -> ScriptValue.of(args.isEmpty() ? n.value() : Math.max(n.value(), args.get(0).asNum()));
                case "min"   -> ScriptValue.of(args.isEmpty() ? n.value() : Math.min(n.value(), args.get(0).asNum()));
                case "lerp"  -> args.size() >= 2
                    ? ScriptValue.of(n.value() + (args.get(0).asNum() - n.value()) * args.get(1).asNum())
                    : ScriptValue.of(n.value());
                default -> ScriptValue.NULL;
            };
            case ScriptValue.Array a -> switch (method) {
                case "get", "at" -> {
                    int idx = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                    yield (idx >= 0 && idx < a.elements().size()) ? a.elements().get(idx) : ScriptValue.NULL;
                }
                case "first" -> a.elements().isEmpty() ? ScriptValue.NULL : a.elements().get(0);
                case "last"  -> a.elements().isEmpty() ? ScriptValue.NULL : a.elements().get(a.elements().size() - 1);
                case "filter_type" -> {
                    if (args.isEmpty()) yield new ScriptValue.Array(a.elements());
                    String typeId = args.get(0).asStr();
                    yield new ScriptValue.Array(a.elements().stream()
                        .filter(e -> e instanceof ScriptValue.Obj o
                            && o.getProperty("type").asStr().contains(typeId))
                        .collect(Collectors.toList()));
                }
                case "map_prop" -> {
                    if (args.isEmpty()) yield new ScriptValue.Array(List.of());
                    String prop = args.get(0).asStr();
                    yield new ScriptValue.Array(a.elements().stream()
                        .map(e -> e.getProperty(prop))
                        .collect(Collectors.toList()));
                }
                default -> ScriptValue.NULL;
            };
            default -> ScriptValue.NULL;
        };
    }

    public static ScriptValue subscriptGet(ScriptValue obj, ScriptValue idx) {
        int n = (int) idx.asNum();
        return switch (obj) {
            case ScriptValue.Array a -> (n >= 0 && n < a.elements().size())
                    ? a.elements().get(n)
                    : ScriptValue.NULL;
            case ScriptValue.Str s -> {
                String[] lines = s.value().split("\n", -1);
                yield (n >= 0 && n < lines.length) ? ScriptValue.of(lines[n]) : ScriptValue.NULL;
            }
            default -> ScriptValue.NULL;
        };
    }

    private static boolean isNullOrEmpty(ScriptValue v) {
        return switch (v) {
            case ScriptValue.Null ignored -> true;
            case ScriptValue.Item i -> i.stack() == null || i.stack().isEmpty();
            default -> false;
        };
    }

    private static boolean itemMatchesId(ScriptValue v, String id) {
        if (!(v instanceof ScriptValue.Item i)) return false;
        ItemStack stack = i.stack();
        if (stack == null || stack.isEmpty()) return false;
        String actualId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return actualId.equals(id) || actualId.equals("minecraft:" + id);
    }

    // ---- Parser ----

    private static final class Parser {

        final String src;
        int pos;

        Parser(String src) {
            this.src = src;
            this.pos = 0;
        }

        Node parseExpr() { return parseTernary(); }

        Node parseTernary() {
            Node condition = parseOr();
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == '?'
                    && (pos + 1 >= src.length() || src.charAt(pos + 1) != '?')) {
                pos++;
                Node thenBranch = parseExpr();
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ':') {
                    pos++;
                } else {
                    throw new IllegalArgumentException(
                            "ScriptFormula: expected ':' after '?' at pos " + pos + " in: " + src);
                }
                Node elseBranch = parseTernary();
                Node cond = condition, t = thenBranch, e = elseBranch;
                return ctx -> cond.eval(ctx).asBool() ? t.eval(ctx) : e.eval(ctx);
            }
            return condition;
        }

        Node parseOr() {
            Node left = parseAnd();
            while (match("||")) {
                Node right = parseAnd();
                Node l = left, r = right;
                left = ctx -> ScriptValue.of(l.eval(ctx).asBool() || r.eval(ctx).asBool());
            }
            return left;
        }

        Node parseAnd() {
            Node left = parseNot();
            while (match("&&")) {
                Node right = parseNot();
                Node l = left, r = right;
                left = ctx -> ScriptValue.of(l.eval(ctx).asBool() && r.eval(ctx).asBool());
            }
            return left;
        }

        Node parseNot() {
            if (match("!")) {
                Node inner = parseNot();
                return ctx -> ScriptValue.of(!inner.eval(ctx).asBool());
            }
            return parseBitwise();
        }

        Node parseBitwise() {
            Node left = parseCompare();
            while (true) {
                skipSpaces();
                // << >> shifts (check before single < >)
                if (matchAt("<<") && (pos + 2 >= src.length() || src.charAt(pos + 2) != '<')) {
                    pos += 2;
                    Node r = parseCompare(); Node l = left;
                    left = ctx -> ScriptValue.of((double)((long)l.eval(ctx).asNum() << (long)r.eval(ctx).asNum()));
                } else if (matchAt(">>") && (pos + 2 >= src.length() || src.charAt(pos + 2) != '>')) {
                    pos += 2;
                    Node r = parseCompare(); Node l = left;
                    left = ctx -> ScriptValue.of((double)((long)l.eval(ctx).asNum() >> (long)r.eval(ctx).asNum()));
                // & bitwise AND (not &&)
                } else if (matchAt("&") && (pos + 1 >= src.length() || src.charAt(pos + 1) != '&')) {
                    pos++;
                    Node r = parseCompare(); Node l = left;
                    left = ctx -> ScriptValue.of((double)((long)l.eval(ctx).asNum() & (long)r.eval(ctx).asNum()));
                // | bitwise OR (not ||)
                } else if (matchAt("|") && (pos + 1 >= src.length() || src.charAt(pos + 1) != '|')) {
                    pos++;
                    Node r = parseCompare(); Node l = left;
                    left = ctx -> ScriptValue.of((double)((long)l.eval(ctx).asNum() | (long)r.eval(ctx).asNum()));
                } else break;
            }
            return left;
        }

        Node parseCompare() {
            Node left = parseNullCoalesce();
            skipSpaces();

            String op = null;
            if      (matchAt(">=")) { op = ">="; pos += 2; }
            else if (matchAt("<=")) { op = "<="; pos += 2; }
            else if (matchAt("==")) { op = "=="; pos += 2; }
            else if (matchAt("!=")) { op = "!="; pos += 2; }
            else if (matchAt(">"))  { op = ">";  pos += 1; }
            else if (matchAt("<"))  { op = "<";  pos += 1; }
            if (op == null) return left;

            skipSpaces();
            Node right = parseNullCoalesce();
            String fop = op;
            Node l = left, r = right;
            return ctx -> {
                ScriptValue lv = l.eval(ctx), rv = r.eval(ctx);
                boolean b;
                if (fop.equals("==") || fop.equals("!=")) {
                    boolean eq = valuesEqual(lv, rv);
                    b = fop.equals("==") ? eq : !eq;
                } else {
                    double dl = lv.asNum(), dr = rv.asNum();
                    b = switch (fop) {
                        case ">"  -> dl >  dr;
                        case "<"  -> dl <  dr;
                        case ">=" -> dl >= dr;
                        case "<=" -> dl <= dr;
                        default   -> false;
                    };
                }
                return ScriptValue.of(b);
            };
        }

        Node parseNullCoalesce() {
            Node left = parseAdd();
            while (match("??")) {
                Node right = parseAdd();
                Node l = left, r = right;
                left = ctx -> {
                    ScriptValue lv = l.eval(ctx);
                    return isNullOrEmpty(lv) ? r.eval(ctx) : lv;
                };
            }
            return left;
        }

        Node parseAdd() {
            Node left = parseMul();
            while (true) {
                skipSpaces();
                if (matchAt("+")) {
                    pos++;
                    Node r = parseMul();
                    Node l = left;
                    left = ctx -> addPolymorphic(l.eval(ctx), r.eval(ctx));
                } else if (matchAt("-")) {
                    pos++;
                    Node r = parseMul();
                    Node l = left;
                    left = ctx -> ScriptValue.of(l.eval(ctx).asNum() - r.eval(ctx).asNum());
                } else break;
            }
            return left;
        }

        Node parseMul() {
            Node left = parsePow();
            while (true) {
                skipSpaces();
                if (matchAt("**")) {               // ** power (higher priority than *)
                    pos += 2;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> ScriptValue.of(Math.pow(l.eval(ctx).asNum(), r.eval(ctx).asNum()));
                } else if (matchAt("//")) {         // // integer division
                    pos += 2;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> {
                        double d = r.eval(ctx).asNum();
                        return ScriptValue.of(d == 0.0 ? 0.0 : Math.floor(l.eval(ctx).asNum() / d));
                    };
                } else if (matchAt("*")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> ScriptValue.of(l.eval(ctx).asNum() * r.eval(ctx).asNum());
                } else if (matchAt("/")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> {
                        double d = r.eval(ctx).asNum();
                        return ScriptValue.of(d == 0.0 ? 0.0 : l.eval(ctx).asNum() / d);
                    };
                } else if (matchAt("%")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> {
                        double d = r.eval(ctx).asNum();
                        return ScriptValue.of(d == 0.0 ? 0.0 : l.eval(ctx).asNum() % d);
                    };
                } else break;
            }
            return left;
        }

        Node parsePow() {
            Node base = parseUnary();
            skipSpaces();
            if (matchAt("^")) {
                pos++;
                Node exp = parsePow();
                return ctx -> ScriptValue.of(Math.pow(base.eval(ctx).asNum(), exp.eval(ctx).asNum()));
            }
            return base;
        }

        Node parseUnary() {
            skipSpaces();
            if (matchAt("-")) {
                pos++;
                Node inner = parseUnary();
                return ctx -> ScriptValue.of(-inner.eval(ctx).asNum());
            }
            if (matchAt("~")) {  // bitwise NOT
                pos++;
                Node inner = parseUnary();
                return ctx -> ScriptValue.of((double)(~(long)inner.eval(ctx).asNum()));
            }
            return parsePrimary();
        }

        Node parsePrimary() {
            skipSpaces();
            if (pos >= src.length())
                throw new IllegalArgumentException(
                        "ScriptFormula: unexpected end of expression in: " + src);

            char c = src.charAt(pos);
            Node base;

            // Array literal: [elem, elem, ...]
            if (c == '[') {
                pos++;
                List<Node> elems = new ArrayList<>();
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ']') {
                    pos++;
                } else {
                    while (pos < src.length()) {
                        skipSpaces();
                        if (pos < src.length() && src.charAt(pos) == ']') { pos++; break; }
                        elems.add(parseExpr());
                        skipSpaces();
                        if (pos < src.length() && src.charAt(pos) == ',') { pos++; continue; }
                        if (pos < src.length() && src.charAt(pos) == ']') { pos++; break; }
                        break;
                    }
                }
                List<Node> captured = elems;
                base = ctx -> {
                    List<ScriptValue> vals = new ArrayList<>(captured.size());
                    for (Node n : captured) vals.add(n.eval(ctx));
                    return new ScriptValue.Array(vals);
                };
                return parseSuffixChain(base);
            }

            if (c == '(') {
                pos++;
                base = parseExpr();
                skipSpaces();
                if (pos >= src.length() || src.charAt(pos) != ')')
                    throw new IllegalArgumentException(
                            "ScriptFormula: missing ')' at pos " + pos + " in: " + src);
                pos++;
                return parseSuffixChain(base);
            }

            if (c == '"' || c == '\'') {
                char quote = c;
                pos++;
                StringBuilder sb = new StringBuilder();
                while (pos < src.length() && src.charAt(pos) != quote) {
                    if (src.charAt(pos) == '\\' && pos + 1 < src.length()) {
                        pos++;
                        char esc = src.charAt(pos);
                        sb.append(switch (esc) {
                            case 'n'  -> '\n';
                            case 't'  -> '\t';
                            case 'r'  -> '\r';
                            case '\\' -> '\\';
                            default   -> esc;
                        });
                    } else {
                        sb.append(src.charAt(pos));
                    }
                    pos++;
                }
                if (pos < src.length()) pos++;
                String val = sb.toString();
                base = ctx -> ScriptValue.of(val);
                return parseSuffixChain(base);
            }

            if (c == '$') {
                pos++;
                int start = pos;
                while (pos < src.length()
                        && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                String varName = src.substring(start, pos);
                base = ctx -> ctx.getVar(varName);
                return parseSuffixChain(base);
            }

            if (Character.isDigit(c)
                    || (c == '.' && pos + 1 < src.length() && Character.isDigit(src.charAt(pos + 1)))) {
                int start = pos;
                while (pos < src.length()
                        && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) pos++;
                if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
                    pos++;
                    if (pos < src.length()
                            && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
                    while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                }
                String token = src.substring(start, pos);
                try {
                    double v = Double.parseDouble(token);
                    return ctx -> ScriptValue.of(v);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "ScriptFormula: invalid number '" + token + "' in: " + src);
                }
            }

            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < src.length()
                        && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                String name = src.substring(start, pos);

                // Cross-file script-call reference: "file.pf:func[:arg1:arg2...]" — the SAME
                // whole-string shape ScriptCall already parses for config fields that take an
                // entire string as one call (on_get_container, page item/name, ...), now usable
                // as one atom INSIDE a larger formula — a renderer's rot_x/location/when/item, or
                // (since TextTemplate's "${expr}" substitution already runs its expr through this
                // same ScriptFormula) a name/lore template. Deliberately checked before the '.'
                // member-access branch below, which would otherwise treat "pf" as a bogus property
                // name and choke on the trailing ':'. Args are passed through as literal strings,
                // same as ScriptCall's own convention — not sub-expressions — so no need to parse
                // them as anything more (matches "gas_motor.pf:increase_rpm:8").
                if (src.startsWith(".pf:", pos)) {
                    int refStart = start;
                    int p = pos + 4; // past ".pf:"
                    while (p < src.length() && isScriptCallArgChar(src.charAt(p))) p++;
                    String ref = src.substring(refStart, p);
                    pos = p;
                    skipSpaces();
                    base = ctx -> {
                        ScriptCall call = ScriptCall.parse(ref);
                        if (call == null) return ScriptValue.NULL;
                        try { return call.evaluate(ctx); } catch (Throwable ignored) { return ScriptValue.NULL; }
                    };
                    return parseSuffixChain(base);
                }
                skipSpaces();

                // Dot access: Name.property or Name.method(args)
                // Try as class instance first; if not found, fall back to local variable.
                if (pos < src.length() && src.charAt(pos) == '.') {
                    pos++;
                    skipSpaces();
                    int propStart = pos;
                    while (pos < src.length()
                            && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    String member = src.substring(propStart, pos);
                    skipSpaces();

                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++;
                        List<Node> argNodes = parseArgs();
                        String resolvedName = name, methodName = member;
                        base = ctx -> {
                            ScriptValue sv = ctx.getClassInstance(resolvedName);
                            // Fall back to variable if not a class instance
                            if (sv == ScriptValue.NULL) sv = ctx.getVar(resolvedName);
                            if (sv == ScriptValue.NULL) return ScriptValue.NULL;
                            List<ScriptValue> evalArgs = argNodes.stream()
                                    .map(n -> n.eval(ctx))
                                    .collect(Collectors.toList());
                            return memberCall(sv, methodName, evalArgs, ctx);
                        };
                    } else {
                        String resolvedName = name, propName = member;
                        base = ctx -> {
                            ScriptValue sv = ctx.getClassInstance(resolvedName);
                            // Fall back to variable if not a class instance
                            if (sv == ScriptValue.NULL) sv = ctx.getVar(resolvedName);
                            return sv != ScriptValue.NULL ? memberGet(sv, propName, ctx) : ScriptValue.NULL;
                        };
                    }
                    return parseSuffixChain(base);
                }

                // Function call
                if (pos < src.length() && src.charAt(pos) == '(') {
                    pos++;
                    List<Node> argNodes = parseArgs();
                    String funcName = name;
                    base = ctx -> {
                        List<ScriptValue> evalArgs = argNodes.stream()
                                .map(n -> n.eval(ctx))
                                .collect(Collectors.toList());
                        return callBuiltin(funcName, evalArgs, ctx);
                    };
                    return parseSuffixChain(base);
                }

                // Named constants
                if ("PI".equals(name) || "Math_PI".equals(name)) return ctx -> ScriptValue.of(Math.PI);
                if ("TAU".equals(name))  return ctx -> ScriptValue.of(Math.PI * 2);
                if ("E".equals(name))    return ctx -> ScriptValue.of(Math.E);
                if ("INF".equals(name))  return ctx -> ScriptValue.of(Double.POSITIVE_INFINITY);
                if ("NAN".equals(name))  return ctx -> ScriptValue.of(Double.NaN);
                if ("TRUE".equals(name)  || "true".equals(name))  return ctx -> ScriptValue.of(true);
                if ("FALSE".equals(name) || "false".equals(name)) return ctx -> ScriptValue.of(false);

                // Variable reference — check class instances (Player, Machine, World, ...) first,
                // same fallback order as the dotted-access path above, since a bare identifier used
                // as a plain value (e.g. Dialog.show(Player)) previously only ever checked ctx.getVar
                // and silently resolved to NULL for any name that lived in ctx's classes map instead.
                String bareName = name;
                base = ctx -> {
                    ScriptValue sv = ctx.getClassInstance(bareName);
                    return sv != ScriptValue.NULL ? sv : ctx.getVar(bareName);
                };
                return parseSuffixChain(base);
            }

            throw new IllegalArgumentException(
                    "ScriptFormula: unexpected character '" + c
                    + "' at pos " + pos + " in: " + src);
        }

        Node parseSuffixChain(Node base) {
            while (true) {
                skipSpaces();
                if (pos >= src.length()) break;
                char ch = src.charAt(pos);
                if (ch == '.') {
                    pos++;
                    skipSpaces();
                    int mStart = pos;
                    while (pos < src.length()
                            && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    if (pos == mStart) break;
                    String member = src.substring(mStart, pos);
                    skipSpaces();
                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++;
                        List<Node> argNodes = parseArgs();
                        Node obj = base;
                        String m = member;
                        base = ctx -> memberCall(obj.eval(ctx), m,
                                argNodes.stream().map(n -> n.eval(ctx)).collect(Collectors.toList()), ctx);
                    } else {
                        Node obj = base;
                        String p = member;
                        base = ctx -> memberGet(obj.eval(ctx), p, ctx);
                    }
                } else if (ch == '[') {
                    pos++;
                    Node idxNode = parseExpr();
                    skipSpaces();
                    if (pos < src.length() && src.charAt(pos) == ']') pos++;
                    Node obj = base;
                    base = ctx -> subscriptGet(obj.eval(ctx), idxNode.eval(ctx));
                } else {
                    break;
                }
            }
            return base;
        }

        List<Node> parseArgs() {
            List<Node> args = new ArrayList<>();
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == ')') {
                pos++;
                return args;
            }
            while (true) {
                skipSpaces();
                int savedPos = pos;
                if (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                    int numStart = pos;
                    while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                    if (pos + 1 < src.length()
                            && src.charAt(pos) == '.' && src.charAt(pos + 1) == '.') {
                        int from = Integer.parseInt(src.substring(numStart, pos));
                        pos += 2;
                        int toStart = pos;
                        while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                        if (pos == toStart)
                            throw new IllegalArgumentException(
                                    "ScriptFormula: expected integer after '..' at pos " + pos + " in: " + src);
                        int to = Integer.parseInt(src.substring(toStart, pos));
                        int step = from <= to ? 1 : -1;
                        for (int i = from; i != to + step; i += step) {
                            // Each range element is a compile-time constant (from/to/step are all
                            // parsed integers) — box it ONCE here rather than re-allocating a fresh
                            // ScriptValue.Num on every single call this formula is evaluated. The
                            // record is immutable, so sharing one instance across every future call
                            // is exactly as safe as re-boxing it each time.
                            final ScriptValue boxed = ScriptValue.of((double) i);
                            args.add(ctx -> boxed);
                        }
                    } else {
                        pos = savedPos;
                        args.add(parseExpr());
                    }
                } else {
                    args.add(parseExpr());
                }

                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ',') {
                    pos++;
                } else {
                    break;
                }
            }
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == ')') pos++;
            return args;
        }

        void skipSpaces() {
            while (pos < src.length() && src.charAt(pos) == ' ') pos++;
        }

        boolean match(String token) {
            skipSpaces();
            if (matchAt(token)) {
                pos += token.length();
                return true;
            }
            return false;
        }

        boolean matchAt(String token) {
            if (pos + token.length() > src.length()) return false;
            return src.startsWith(token, pos);
        }
    }
}
