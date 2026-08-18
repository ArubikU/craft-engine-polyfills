package dev.arubik.craftengine.machine.render.formula;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Typed expression engine for CraftEngine Polyfills machine/workbench renderers.
 *
 * <p>Extends the original {@link dev.arubik.craftengine.machine.render.SpeedFormula}
 * grammar with:
 * <ul>
 *   <li>String literals — {@code "text"} or {@code 'text'}</li>
 *   <li>Variable references — {@code $name} or bare {@code name}</li>
 *   <li>Class member access — {@code Inventory.size}, {@code Inventory.slot(9)}</li>
 *   <li>Built-in functions — {@code slot(n)}, {@code is_empty(item)},
 *       {@code has_item(item,"id")}, {@code item_count(x)}, {@code item_id(item)},
 *       {@code matches(item,"id")}, {@code not(x)}</li>
 *   <li>Range notation inside argument lists — {@code slots(9..15)}</li>
 *   <li>Null-coalescing operator — {@code slot(9) ?? "air"} (higher precedence
 *       than comparisons)</li>
 * </ul>
 *
 * <p>Operator precedence (lowest to highest):
 * <pre>
 *   || &gt; &amp;&amp; &gt; ! &gt; compare &gt; ?? &gt; +/- &gt; * / &gt; ^ &gt; unary- &gt; primary
 * </pre>
 *
 * <p>Compile once per expression string, evaluate many times per tick:
 * <pre>{@code
 * PolyFormula f = PolyFormula.compile("is_empty(slot(0)) || rpm > 200");
 * boolean ok = f.evaluateBool(ctx);
 * }</pre>
 */
public final class PolyFormula {

    // ---- Public API --------------------------------------------------------

    /**
     * Compile an expression string into an evaluable formula.
     *
     * @param expr expression text (may not be {@code null})
     * @return compiled formula
     * @throws IllegalArgumentException on parse error
     */
    public static PolyFormula compile(String expr) {
        if (expr == null) throw new IllegalArgumentException("PolyFormula: expression must not be null");
        Parser p = new Parser(expr.trim());
        Node root = p.parseExpr();
        p.skipSpaces();
        if (p.pos < p.src.length()) {
            throw new IllegalArgumentException(
                    "PolyFormula: unexpected '" + p.src.charAt(p.pos)
                    + "' at pos " + p.pos + " in: " + expr);
        }
        return new PolyFormula(expr, root);
    }

    /** Evaluate the expression, returning a {@link PolyValue}. */
    public PolyValue evaluate(PolyContext ctx) {
        return root.eval(ctx);
    }

    /** Evaluate and coerce to boolean. */
    public boolean evaluateBool(PolyContext ctx) {
        return root.eval(ctx).asBool();
    }

    /** Evaluate and coerce to double. */
    public double evaluateNum(PolyContext ctx) {
        return root.eval(ctx).asNum();
    }

    /** Evaluate and coerce to String. */
    public String evaluateStr(PolyContext ctx) {
        return root.eval(ctx).asStr();
    }

    /**
     * Evaluate and extract the underlying {@link ItemStack} if the result is
     * an {@link PolyValue.Item}; returns {@code null} for any other type.
     */
    public ItemStack evaluateItem(PolyContext ctx) {
        PolyValue v = root.eval(ctx);
        return (v instanceof PolyValue.Item i) ? i.stack() : null;
    }

    @Override
    public String toString() { return rawExpr; }

    // ---- Internals ---------------------------------------------------------

    private final String rawExpr;
    private final Node   root;

    private PolyFormula(String rawExpr, Node root) {
        this.rawExpr = rawExpr;
        this.root    = root;
    }

    @FunctionalInterface
    private interface Node {
        PolyValue eval(PolyContext ctx);
    }

    // ---- Built-in functions ------------------------------------------------

    /**
     * Evaluate a built-in function call.
     * Returns {@link PolyValue#NULL} for unrecognised names.
     */
    private static PolyValue callBuiltin(String name, List<PolyValue> args, PolyContext ctx) {
        return switch (name) {

            // slot(n) — shorthand for Inventory.slot(n)
            case "slot" -> {
                PolyClass inv = ctx.getClass("Inventory");
                yield inv != null ? inv.call("slot", args) : PolyValue.NULL;
            }

            // slots(a, b, …) — shorthand for Inventory.slots(…)
            case "slots" -> {
                PolyClass inv = ctx.getClass("Inventory");
                yield inv != null ? inv.call("slots", args) : PolyValue.NULL;
            }

            // is_empty(item) → bool
            case "is_empty" -> PolyValue.of(args.isEmpty() || isNullOrEmpty(args.get(0)));

            // is_not_empty(item) → bool
            case "is_not_empty" -> PolyValue.of(!args.isEmpty() && !isNullOrEmpty(args.get(0)));

            // has_item(item, "id") → bool
            case "has_item" -> {
                if (args.size() < 2) yield PolyValue.of(false);
                yield PolyValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
            }

            // item_count(item | itemlist) → num  (delegates to asNum coercion)
            case "item_count" -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum());

            // item_id(item) → str material key
            case "item_id" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                PolyValue v = args.get(0);
                if (!(v instanceof PolyValue.Item i)) yield PolyValue.NULL;
                if (i.stack() == null || i.stack().getType().isAir()) yield PolyValue.of("minecraft:air");
                yield PolyValue.of(i.stack().getType().getKey().toString());
            }

            // matches(item, "id") → bool
            case "matches" -> {
                if (args.size() < 2) yield PolyValue.of(false);
                yield PolyValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
            }

            // not(x) → bool negation
            case "not" -> PolyValue.of(args.isEmpty() || !args.get(0).asBool());

            // CraftEngineItem("namespace:id") → resolves a CE custom item by ID
            case "CraftEngineItem" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                String itemId = args.get(0).asStr();
                try {
                    var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(
                            net.momirealms.craftengine.core.util.Key.of(itemId));
                    if (def != null) {
                        ItemStack bukkit = def.buildBukkitItem();
                        yield bukkit != null ? PolyValue.ofItem(bukkit) : PolyValue.NULL;
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.NULL;
            }

            // MinecraftItem / VanillaItem("namespace:id") → vanilla bukkit item
            case "MinecraftItem", "VanillaItem" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                String id  = args.get(0).asStr();
                Material mat = Material.matchMaterial(id);
                yield mat != null ? PolyValue.ofItem(new ItemStack(mat)) : PolyValue.NULL;
            }

            // FluidTank(name) → fill fraction of the named fluid tank
            // FluidTank(name, property) → named property: level, capacity, fraction, percent, is_empty, is_full
            case "FluidTank" -> {
                PolyClass tanksClass = ctx.getClass("FluidTanks");
                if (tanksClass == null || args.isEmpty()) yield PolyValue.NULL;
                String tankName = args.get(0).asStr();
                String property = args.size() >= 2 ? args.get(1).asStr() : "fraction";
                yield ((FluidTanksClass) tanksClass).forTank(tankName).get(property);
            }

            // GasTank(name) → fill fraction of the named gas tank
            // GasTank(name, property) → named property: level, capacity, fraction, percent, is_empty, is_full
            case "GasTank" -> {
                PolyClass tanksClass = ctx.getClass("GasTanks");
                if (tanksClass == null || args.isEmpty()) yield PolyValue.NULL;
                String tankName = args.get(0).asStr();
                String property = args.size() >= 2 ? args.get(1).asStr() : "fraction";
                yield ((GasTanksClass) tanksClass).forTank(tankName).get(property);
            }

            // player_facing("face") / player_facing("face", "precision")
            // Shorthand for Machine.player_facing(…) — returns false when no Machine class is present.
            case "player_facing" -> {
                PolyClass machine = ctx.getClass("Machine");
                if (machine == null) yield PolyValue.of(false);
                yield machine.call("player_facing", args);
            }

            // player_in_range(dist)
            // Shorthand for Machine.player_in_range(dist) — returns false when no Machine class is present.
            case "player_in_range" -> {
                PolyClass machine = ctx.getClass("Machine");
                if (machine == null) yield PolyValue.of(false);
                yield machine.call("player_in_range", args);
            }

            // player_above() / player_below()
            // Shorthands for Machine.player_above() / Machine.player_below().
            case "player_above" -> {
                PolyClass machine = ctx.getClass("Machine");
                if (machine == null) yield PolyValue.of(false);
                yield machine.call("player_above", args);
            }
            case "player_below" -> {
                PolyClass machine = ctx.getClass("Machine");
                if (machine == null) yield PolyValue.of(false);
                yield machine.call("player_below", args);
            }

            // input(n) / output(n) / tool(n) — shorthands for Workbench.input/output/tool(n)
            case "input" -> {
                PolyClass wb = ctx.getClass("Workbench");
                yield wb != null ? wb.call("input", args) : PolyValue.NULL;
            }
            case "output" -> {
                PolyClass wb = ctx.getClass("Workbench");
                yield wb != null ? wb.call("output", args) : PolyValue.NULL;
            }
            case "tool" -> {
                PolyClass wb = ctx.getClass("Workbench");
                yield wb != null ? wb.call("tool", args) : PolyValue.NULL;
            }

            // ---- Trig (args in radians) ----------------------------------------
            case "sin"   -> PolyValue.of(args.isEmpty() ? 0 : Math.sin(args.get(0).asNum()));
            case "cos"   -> PolyValue.of(args.isEmpty() ? 0 : Math.cos(args.get(0).asNum()));
            case "tan"   -> PolyValue.of(args.isEmpty() ? 0 : Math.tan(args.get(0).asNum()));
            case "asin"  -> PolyValue.of(args.isEmpty() ? 0 : Math.asin(args.get(0).asNum()));
            case "acos"  -> PolyValue.of(args.isEmpty() ? 0 : Math.acos(args.get(0).asNum()));
            case "atan"  -> PolyValue.of(args.isEmpty() ? 0 : Math.atan(args.get(0).asNum()));
            case "atan2" -> PolyValue.of(args.size() < 2 ? 0 : Math.atan2(args.get(0).asNum(), args.get(1).asNum()));
            case "deg"   -> PolyValue.of(args.isEmpty() ? 0 : Math.toDegrees(args.get(0).asNum()));
            case "rad"   -> PolyValue.of(args.isEmpty() ? 0 : Math.toRadians(args.get(0).asNum()));

            // ---- Math ----------------------------------------------------------
            case "abs"   -> PolyValue.of(args.isEmpty() ? 0 : Math.abs(args.get(0).asNum()));
            case "sqrt"  -> PolyValue.of(args.isEmpty() ? 0 : Math.sqrt(args.get(0).asNum()));
            case "pow"   -> PolyValue.of(args.size() < 2 ? 0 : Math.pow(args.get(0).asNum(), args.get(1).asNum()));
            case "log"   -> PolyValue.of(args.isEmpty() ? 0 : Math.log(args.get(0).asNum()));
            case "log10" -> PolyValue.of(args.isEmpty() ? 0 : Math.log10(args.get(0).asNum()));
            case "exp"   -> PolyValue.of(args.isEmpty() ? 0 : Math.exp(args.get(0).asNum()));
            case "sign"  -> PolyValue.of(args.isEmpty() ? 0 : Math.signum(args.get(0).asNum()));

            // ---- Rounding ------------------------------------------------------
            case "floor" -> PolyValue.of(args.isEmpty() ? 0 : Math.floor(args.get(0).asNum()));
            case "ceil"  -> PolyValue.of(args.isEmpty() ? 0 : Math.ceil(args.get(0).asNum()));
            case "round" -> PolyValue.of(args.isEmpty() ? 0 : Math.round(args.get(0).asNum()));
            case "trunc" -> PolyValue.of(args.isEmpty() ? 0 : (long) args.get(0).asNum());

            // ---- Min / max / clamp / lerp / map --------------------------------
            case "min"   -> PolyValue.of(args.size() < 2 ? 0 : Math.min(args.get(0).asNum(), args.get(1).asNum()));
            case "max"   -> PolyValue.of(args.size() < 2 ? 0 : Math.max(args.get(0).asNum(), args.get(1).asNum()));
            case "clamp" -> {
                if (args.size() < 3) yield PolyValue.of(0);
                double v = args.get(0).asNum(), lo = args.get(1).asNum(), hi = args.get(2).asNum();
                yield PolyValue.of(Math.max(lo, Math.min(hi, v)));
            }
            case "lerp" -> {
                if (args.size() < 3) yield PolyValue.of(0);
                double a = args.get(0).asNum(), b = args.get(1).asNum(), t = args.get(2).asNum();
                yield PolyValue.of(a + (b - a) * t);
            }
            case "map" -> {
                // map(array, expr_str) — evaluate expr for each element, return new array
                if (args.size() == 2 && args.get(0) instanceof PolyValue.Array a0) {
                    String mapExpr = args.get(1).asStr();
                    java.util.List<PolyValue> mapped = new java.util.ArrayList<>(a0.elements().size());
                    for (PolyValue elem : a0.elements()) {
                        try {
                            PolyContext elemCtx = PolyContext.builder().copyFrom(ctx)
                                    .val("e", elem).val("element", elem).build();
                            mapped.add(PolyFormula.compile(mapExpr).evaluate(elemCtx));
                        } catch (Throwable ignored) { mapped.add(PolyValue.NULL); }
                    }
                    yield new PolyValue.Array(mapped);
                }
                // map(v, fromLow, fromHigh, toLow, toHigh) — range mapping
                if (args.size() < 5) yield PolyValue.of(0);
                double v = args.get(0).asNum(), fLo = args.get(1).asNum(), fHi = args.get(2).asNum();
                double tLo = args.get(3).asNum(), tHi = args.get(4).asNum();
                double t = fHi != fLo ? (v - fLo) / (fHi - fLo) : 0;
                yield PolyValue.of(tLo + (tHi - tLo) * t);
            }

            // filter(array, condition_expr) — keep elements where condition is truthy
            case "filter" -> {
                if (args.size() < 2) yield args.isEmpty() ? PolyValue.NULL : args.get(0);
                PolyValue arrF = args.get(0);
                String filterExpr = args.get(1).asStr();
                if (!(arrF instanceof PolyValue.Array af)) yield arrF;
                java.util.List<PolyValue> filtered = new java.util.ArrayList<>();
                for (PolyValue elem : af.elements()) {
                    try {
                        PolyContext elemCtx = PolyContext.builder().copyFrom(ctx)
                                .val("e", elem).val("element", elem).build();
                        if (PolyFormula.compile(filterExpr).evaluateBool(elemCtx)) filtered.add(elem);
                    } catch (Throwable ignored) {}
                }
                yield new PolyValue.Array(filtered);
            }

            // reduce(array, initial, expr) — fold with acc (accumulator) and e (current)
            case "reduce" -> {
                if (args.size() < 3) yield PolyValue.NULL;
                PolyValue arrR = args.get(0);
                PolyValue acc = args.get(1);
                String reduceExpr = args.get(2).asStr();
                if (!(arrR instanceof PolyValue.Array ar)) yield acc;
                for (PolyValue elem : ar.elements()) {
                    try {
                        PolyContext elemCtx = PolyContext.builder().copyFrom(ctx)
                                .val("acc", acc).val("accumulator", acc)
                                .val("e", elem).val("element", elem).build();
                        acc = PolyFormula.compile(reduceExpr).evaluate(elemCtx);
                    } catch (Throwable ignored) {}
                }
                yield acc;
            }

            // flat(array) / flatten(array) — flatten one level of nested arrays
            case "flat", "flatten" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                if (!(args.get(0) instanceof PolyValue.Array aFlat)) yield args.get(0);
                java.util.List<PolyValue> flat = new java.util.ArrayList<>();
                for (PolyValue elem : aFlat.elements()) {
                    if (elem instanceof PolyValue.Array inner) flat.addAll(inner.elements());
                    else flat.add(elem);
                }
                yield new PolyValue.Array(flat);
            }

            // range(end) / range(start, end) / range(start, end, step) — numeric range array
            case "range" -> {
                if (args.isEmpty()) yield new PolyValue.Array(java.util.List.of());
                double rStart = args.size() > 1 ? args.get(0).asNum() : 0;
                double rEnd   = args.size() > 1 ? args.get(1).asNum() : args.get(0).asNum();
                double rStep  = args.size() > 2 ? args.get(2).asNum() : 1;
                if (rStep == 0 || Math.abs((rEnd - rStart) / rStep) > 10_000) yield new PolyValue.Array(java.util.List.of());
                java.util.List<PolyValue> rangeList = new java.util.ArrayList<>();
                if (rStep > 0) { for (double rv = rStart; rv < rEnd; rv += rStep) rangeList.add(PolyValue.of(rv)); }
                else           { for (double rv = rStart; rv > rEnd; rv += rStep) rangeList.add(PolyValue.of(rv)); }
                yield new PolyValue.Array(rangeList);
            }

            // zip(a, b) — interleave two arrays element-wise into [[a0,b0],[a1,b1],...]
            case "zip" -> {
                if (args.size() < 2) yield PolyValue.NULL;
                if (!(args.get(0) instanceof PolyValue.Array az) || !(args.get(1) instanceof PolyValue.Array bz))
                    yield PolyValue.NULL;
                int zLen = Math.min(az.elements().size(), bz.elements().size());
                java.util.List<PolyValue> zipped = new java.util.ArrayList<>(zLen);
                for (int zi = 0; zi < zLen; zi++)
                    zipped.add(new PolyValue.Array(java.util.List.of(az.elements().get(zi), bz.elements().get(zi))));
                yield new PolyValue.Array(zipped);
            }

            // unique(array) — deduplicate preserving order (string-equality)
            case "unique" -> {
                if (args.isEmpty() || !(args.get(0) instanceof PolyValue.Array au)) yield args.isEmpty() ? PolyValue.NULL : args.get(0);
                java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
                java.util.List<PolyValue> unique = new java.util.ArrayList<>();
                for (PolyValue elem : au.elements()) {
                    if (seen.add(elem.asStr())) unique.add(elem);
                }
                yield new PolyValue.Array(unique);
            }

            // array(a, b, ...) — construct an Array from arguments
            case "array" -> new PolyValue.Array(new java.util.ArrayList<>(args));

            // size(x) / length(x) — length of array or string
            case "size", "length" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                PolyValue av = args.get(0);
                if (av instanceof PolyValue.Array a) yield PolyValue.of(a.elements().size());
                if (av instanceof PolyValue.Str s)   yield PolyValue.of(s.value().length());
                yield PolyValue.of(0);
            }
            case "mod"   -> PolyValue.of(args.size() < 2 || args.get(1).asNum() == 0 ? 0 : args.get(0).asNum() % args.get(1).asNum());
            case "frac"  -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum() - Math.floor(args.get(0).asNum()));
            case "step"  -> PolyValue.of(args.size() < 2 ? 0 : (args.get(1).asNum() >= args.get(0).asNum() ? 1.0 : 0.0));
            case "smoothstep" -> {
                // smoothstep(edge0, edge1, x) — smooth Hermite interpolation
                if (args.size() < 3) yield PolyValue.of(0);
                double e0 = args.get(0).asNum(), e1 = args.get(1).asNum(), x = args.get(2).asNum();
                double t = Math.max(0, Math.min(1, (x - e0) / (e1 - e0)));
                yield PolyValue.of(t * t * (3 - 2 * t));
            }

            // ---- Type coercions ------------------------------------------------
            case "int"   -> PolyValue.of(args.isEmpty() ? 0L : (long) args.get(0).asNum());
            case "float" -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum());
            case "bool"  -> PolyValue.of(args.isEmpty() ? false : args.get(0).asBool());
            case "str"   -> PolyValue.of(args.isEmpty() ? "" : args.get(0).asStr());

            // ---- Control -------------------------------------------------------
            case "if" -> {
                // if(condition, trueVal, falseVal) — ternary as a function
                if (args.size() < 3) yield PolyValue.NULL;
                yield args.get(0).asBool() ? args.get(1) : args.get(2);
            }

            // ---- Array operations ---------------------------------------------
            case "count" -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum());
            case "sum" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                PolyValue v = args.get(0);
                if (v instanceof PolyValue.Array a) {
                    yield PolyValue.of(a.elements().stream().mapToDouble(PolyValue::asNum).sum());
                }
                yield PolyValue.of(v.asNum());
            }
            case "any", "some" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                if (args.get(0) instanceof PolyValue.Array a) {
                    yield PolyValue.of(a.elements().stream().anyMatch(PolyValue::asBool));
                }
                yield PolyValue.of(args.get(0).asBool());
            }
            case "all" -> {
                if (args.isEmpty()) yield PolyValue.of(true);
                if (args.get(0) instanceof PolyValue.Array a) {
                    yield PolyValue.of(a.elements().stream().allMatch(PolyValue::asBool));
                }
                yield PolyValue.of(args.get(0).asBool());
            }
            case "none" -> {
                if (args.isEmpty()) yield PolyValue.of(true);
                if (args.get(0) instanceof PolyValue.Array a) {
                    yield PolyValue.of(a.elements().stream().noneMatch(PolyValue::asBool));
                }
                yield PolyValue.of(!args.get(0).asBool());
            }

            // ---- String operations --------------------------------------------
            case "format" -> {
                if (args.isEmpty()) yield PolyValue.of("");
                String fmt = args.get(0).asStr();
                Object[] fmtArgs = args.subList(1, args.size()).stream()
                    .map(a -> a instanceof PolyValue.Num n ?  n.value()
                            : a instanceof PolyValue.Bool b ?  b.value()
                            : a.asStr())
                    .toArray();
                try { yield PolyValue.of(String.format(fmt, fmtArgs)); }
                catch (Throwable ignored) { yield PolyValue.of(fmt); }
            }
            case "concat" -> {
                StringBuilder sb = new StringBuilder();
                for (PolyValue a : args) sb.append(a.asStr());
                yield PolyValue.of(sb.toString());
            }
            case "substring" -> {
                if (args.isEmpty()) yield PolyValue.of("");
                String s = args.get(0).asStr();
                int start = args.size() > 1 ? (int) args.get(1).asNum() : 0;
                int end = args.size() > 2 ? (int) args.get(2).asNum() : s.length();
                try { yield PolyValue.of(s.substring(Math.max(0, start), Math.min(s.length(), end))); }
                catch (Throwable ignored) { yield PolyValue.of(""); }
            }
            case "replace" -> {
                if (args.size() < 3) yield PolyValue.of(args.isEmpty() ? "" : args.get(0).asStr());
                yield PolyValue.of(args.get(0).asStr().replace(args.get(1).asStr(), args.get(2).asStr()));
            }
            case "contains" -> {
                if (args.size() < 2) yield PolyValue.of(false);
                PolyValue container = args.get(0);
                if (container instanceof PolyValue.Str s) {
                    yield PolyValue.of(s.value().contains(args.get(1).asStr()));
                }
                if (container instanceof PolyValue.Array a) {
                    String target = args.get(1).asStr();
                    yield PolyValue.of(a.elements().stream().anyMatch(e -> e.asStr().equals(target)));
                }
                yield PolyValue.of(false);
            }

            // ---- Time/tick operations -----------------------------------------
            case "tick" -> {
                // Get game time from World or Machine context if available
                PolyClass worldCls = ctx.getClass("World");
                if (worldCls instanceof WorldClass wc) yield PolyValue.of(wc.level().getGameTime());
                PolyClass machineCls = ctx.getClass("Machine");
                if (machineCls instanceof MachineClass mc) {
                    PolyValue wv = mc.get("world");
                    if (wv instanceof PolyValue.Obj o && o.inner() instanceof WorldClass wc2)
                        yield PolyValue.of(wc2.level().getGameTime());
                }
                // Fallback: server main tick
                try { yield PolyValue.of(net.minecraft.server.MinecraftServer.getServer().getTickCount()); }
                catch (Throwable ignored) {}
                yield PolyValue.of(0);
            }
            case "seconds" -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum() / 20.0);
            case "ticks"   -> PolyValue.of(args.isEmpty() ? 0 : args.get(0).asNum() * 20.0);

            // random() → 0..1, random(max) → 0..max, random(min, max) → min..max
            case "random" -> {
                var rng = java.util.concurrent.ThreadLocalRandom.current();
                if (args.isEmpty()) yield PolyValue.of(rng.nextDouble());
                if (args.size() == 1) yield PolyValue.of(rng.nextDouble() * args.get(0).asNum());
                double lo = args.get(0).asNum(), hi = args.get(1).asNum();
                yield PolyValue.of(lo + rng.nextDouble() * (hi - lo));
            }
            case "random_int" -> {
                var rng = java.util.concurrent.ThreadLocalRandom.current();
                if (args.isEmpty()) yield PolyValue.of(0);
                int hi = (int) args.get(args.size()-1).asNum();
                int lo = args.size() > 1 ? (int) args.get(0).asNum() : 0;
                yield hi <= lo ? PolyValue.of(lo) : PolyValue.of(lo + rng.nextInt(hi - lo));
            }

            // distance(x1,y1,z1, x2,y2,z2) / distance_sq
            case "distance" -> {
                if (args.size() < 6) yield PolyValue.of(0);
                double dx=args.get(0).asNum()-args.get(3).asNum(),
                       dy=args.get(1).asNum()-args.get(4).asNum(),
                       dz=args.get(2).asNum()-args.get(5).asNum();
                yield PolyValue.of(Math.sqrt(dx*dx+dy*dy+dz*dz));
            }
            case "distance_sq" -> {
                if (args.size() < 6) yield PolyValue.of(0);
                double dx=args.get(0).asNum()-args.get(3).asNum(),
                       dy=args.get(1).asNum()-args.get(4).asNum(),
                       dz=args.get(2).asNum()-args.get(5).asNum();
                yield PolyValue.of(dx*dx+dy*dy+dz*dz);
            }

            // world_time() / world_day()
            case "world_time" -> {
                PolyClass w = ctx.getClass("World");
                yield w != null ? w.get("time") : PolyValue.of(6000);
            }
            case "world_day" -> {
                PolyClass w = ctx.getClass("World");
                yield w != null ? w.get("is_day") : PolyValue.of(true);
            }

            // item_has_tag(item, "tag") shorthand
            case "item_has_tag" -> {
                if (args.size() < 2) yield PolyValue.of(false);
                if (!(args.get(0) instanceof PolyValue.Item i) || i.stack() == null)
                    yield PolyValue.of(false);
                yield new ItemClass(i.stack()).call("is_tagged", java.util.List.of(args.get(1)));
            }

            // coalesce(a, b, ...) → first non-null
            case "coalesce" -> {
                for (PolyValue a : args) if (!(a instanceof PolyValue.Null)) yield a;
                yield PolyValue.NULL;
            }

            // within(value, center, range) → |value-center| <= range
            case "within" -> {
                if (args.size() < 3) yield PolyValue.of(false);
                yield PolyValue.of(Math.abs(args.get(0).asNum()-args.get(1).asNum()) <= args.get(2).asNum());
            }

            // ping(player_obj) → latency ms
            case "ping" -> {
                if (!args.isEmpty() && args.get(0) instanceof PolyValue.Obj o
                        && o.inner() instanceof PlayerClass pc) {
                    try { yield PolyValue.of(pc.player().connection.latency()); } catch (Throwable t) {
                        try { yield PolyValue.of(pc.player().getBukkitEntity().getPing()); } catch (Throwable ignored) {}
                    }
                }
                yield PolyValue.of(0);
            }

            // ---- Color utilities (for text_display background, particle colors) --
            case "rgb" -> {
                if (args.size() < 3) yield PolyValue.of(0);
                int r = (int) clampD(args.get(0).asNum(), 0, 255);
                int g = (int) clampD(args.get(1).asNum(), 0, 255);
                int b = (int) clampD(args.get(2).asNum(), 0, 255);
                yield PolyValue.of((0xFF000000L | ((long) r << 16) | ((long) g << 8) | b));
            }
            case "rgba" -> {
                if (args.size() < 4) yield PolyValue.of(0);
                int r = (int) clampD(args.get(0).asNum(), 0, 255);
                int g = (int) clampD(args.get(1).asNum(), 0, 255);
                int b = (int) clampD(args.get(2).asNum(), 0, 255);
                int a = (int) clampD(args.get(3).asNum(), 0, 255);
                yield PolyValue.of(((long) a << 24) | ((long) r << 16) | ((long) g << 8) | b);
            }

            // ---- Existence / type checking ------------------------------------
            case "type_of" -> {
                if (args.isEmpty()) yield PolyValue.of("null");
                yield PolyValue.of(switch (args.get(0)) {
                    case PolyValue.Num ignored -> "num";
                    case PolyValue.Bool ignored -> "bool";
                    case PolyValue.Str ignored -> "str";
                    case PolyValue.Item ignored -> "item";
                    case PolyValue.Array ignored -> "array";
                    case PolyValue.Obj ignored -> "obj";
                    case PolyValue.Null ignored -> "null";
                });
            }
            case "is_null"  -> PolyValue.of(args.isEmpty() || args.get(0) instanceof PolyValue.Null);
            case "is_num"   -> PolyValue.of(!args.isEmpty() && args.get(0) instanceof PolyValue.Num);
            case "is_str"   -> PolyValue.of(!args.isEmpty() && args.get(0) instanceof PolyValue.Str);
            case "is_item"  -> PolyValue.of(!args.isEmpty() && args.get(0) instanceof PolyValue.Item);
            case "is_array" -> PolyValue.of(!args.isEmpty() && args.get(0) instanceof PolyValue.Array);

            default -> ctx.get(name); // fall back to variable lookup
        };
    }

    /** Clamp a double to the range [lo, hi]. */
    private static double clampD(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---- Member access helpers (used by suffix chain in Parser) ---------------

    /**
     * Dispatch a property access on a runtime {@link PolyValue}.
     * Handles {@link PolyValue.Item} via {@link ItemClass}, string length, etc.
     * Falls back to {@link PolyValue#NULL} for unrecognised combinations.
     */
    static PolyValue memberGet(PolyValue obj, String prop, PolyContext ctx) {
        return switch (obj) {
            case PolyValue.Obj o    -> o.inner() != null ? o.inner().get(prop) : PolyValue.NULL;
            case PolyValue.Item i   -> new ItemClass(i.stack()).get(prop);
            case PolyValue.Array a  -> switch (prop) {
                case "size", "length", "count" -> PolyValue.of(a.elements().size());
                case "is_empty" -> PolyValue.of(a.elements().isEmpty());
                case "first" -> a.elements().isEmpty() ? PolyValue.NULL : a.elements().get(0);
                case "last"  -> a.elements().isEmpty() ? PolyValue.NULL : a.elements().get(a.elements().size() - 1);
                default -> PolyValue.NULL;
            };
            case PolyValue.Str s -> switch (prop) {
                case "length" -> PolyValue.of(s.value().length());
                case "upper"  -> PolyValue.of(s.value().toUpperCase(java.util.Locale.ROOT));
                case "lower"  -> PolyValue.of(s.value().toLowerCase(java.util.Locale.ROOT));
                default -> PolyValue.NULL;
            };
            case PolyValue.Num n -> switch (prop) {
                case "int"          -> PolyValue.of((long) n.value());
                case "abs"          -> PolyValue.of(Math.abs(n.value()));
                case "sign"         -> PolyValue.of(Math.signum(n.value()));
                case "floor"        -> PolyValue.of(Math.floor(n.value()));
                case "ceil"         -> PolyValue.of(Math.ceil(n.value()));
                case "round"        -> PolyValue.of(Math.round(n.value()));
                case "sqrt"         -> PolyValue.of(Math.sqrt(n.value()));
                case "sq", "squared"-> PolyValue.of(n.value() * n.value());
                case "is_nan"       -> PolyValue.of(Double.isNaN(n.value()));
                case "is_infinite"  -> PolyValue.of(Double.isInfinite(n.value()));
                default -> PolyValue.NULL;
            };
            default -> {
                PolyClass cls = ctx.getClass(obj.asStr());
                yield cls != null ? cls.get(prop) : PolyValue.NULL;
            }
        };
    }

    /**
     * Dispatch a method call on a runtime {@link PolyValue}.
     * Handles {@link PolyValue.Item} via {@link ItemClass}, {@link PolyValue.ItemList}
     * indexing, etc.
     */
    static PolyValue memberCall(PolyValue obj, String method, List<PolyValue> args, PolyContext ctx) {
        return switch (obj) {
            case PolyValue.Obj o    -> o.inner() != null ? o.inner().call(method, args) : PolyValue.NULL;
            case PolyValue.Item i   -> new ItemClass(i.stack()).call(method, args);
            case PolyValue.Num n -> switch (method) {
                case "int"          -> PolyValue.of((long) n.value());
                case "abs"          -> PolyValue.of(Math.abs(n.value()));
                case "floor"        -> PolyValue.of(Math.floor(n.value()));
                case "ceil"         -> PolyValue.of(Math.ceil(n.value()));
                case "round"        -> PolyValue.of(Math.round(n.value()));
                case "sqrt"         -> PolyValue.of(Math.sqrt(n.value()));
                case "sq", "squared"-> PolyValue.of(n.value() * n.value());
                case "sign"         -> PolyValue.of(Math.signum(n.value()));
                case "clamp" -> args.size() >= 2
                    ? PolyValue.of(Math.max(args.get(0).asNum(), Math.min(args.get(1).asNum(), n.value())))
                    : PolyValue.of(n.value());
                case "pow"   -> PolyValue.of(args.isEmpty() ? n.value() : Math.pow(n.value(), args.get(0).asNum()));
                case "max"   -> PolyValue.of(args.isEmpty() ? n.value() : Math.max(n.value(), args.get(0).asNum()));
                case "min"   -> PolyValue.of(args.isEmpty() ? n.value() : Math.min(n.value(), args.get(0).asNum()));
                case "lerp"  -> args.size() >= 2
                    ? PolyValue.of(n.value() + (args.get(0).asNum() - n.value()) * args.get(1).asNum())
                    : PolyValue.of(n.value());
                default -> PolyValue.NULL;
            };
            case PolyValue.Array a -> switch (method) {
                case "get", "at" -> {
                    int n = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                    yield (n >= 0 && n < a.elements().size()) ? a.elements().get(n) : PolyValue.NULL;
                }
                case "first" -> a.elements().isEmpty() ? PolyValue.NULL : a.elements().get(0);
                case "last"  -> a.elements().isEmpty() ? PolyValue.NULL : a.elements().get(a.elements().size() - 1);
                case "filter_type" -> {
                    if (args.isEmpty()) yield new PolyValue.Array(a.elements());
                    String typeId = args.get(0).asStr();
                    yield new PolyValue.Array(a.elements().stream()
                        .filter(e -> e instanceof PolyValue.Obj o && o.inner() instanceof EntityClass ec
                            && ec.get("type").asStr().contains(typeId))
                        .collect(java.util.stream.Collectors.toList()));
                }
                case "map_prop" -> {
                    if (args.isEmpty()) yield new PolyValue.Array(List.of());
                    String prop = args.get(0).asStr();
                    yield new PolyValue.Array(a.elements().stream()
                        .map(e -> e instanceof PolyValue.Obj o && o.inner() != null
                            ? o.inner().get(prop) : PolyValue.NULL)
                        .collect(java.util.stream.Collectors.toList()));
                }
                default -> PolyValue.NULL;
            };
            default -> PolyValue.NULL;
        };
    }

    /**
     * Subscript access: {@code obj[idx]}.
     * <ul>
     *   <li>{@link PolyValue.ItemList} — returns item at position {@code n}.</li>
     *   <li>{@link PolyValue.Str} — splits on {@code \n} and returns line {@code n};
     *       useful for {@code item.lore[0]}.</li>
     * </ul>
     */
    static PolyValue subscriptGet(PolyValue obj, PolyValue idx) {
        int n = (int) idx.asNum();
        return switch (obj) {
            case PolyValue.Array a -> (n >= 0 && n < a.elements().size())
                    ? a.elements().get(n)
                    : PolyValue.NULL;
            case PolyValue.Str s -> {
                String[] lines = s.value().split("\n", -1);
                yield (n >= 0 && n < lines.length) ? PolyValue.of(lines[n]) : PolyValue.NULL;
            }
            default -> PolyValue.NULL;
        };
    }

    /** True if {@code v} represents an absent/empty item. */
    private static boolean isNullOrEmpty(PolyValue v) {
        return switch (v) {
            case PolyValue.Null ignored -> true;
            case PolyValue.Item i -> i.stack() == null || i.stack().getType().isAir();
            default -> false;
        };
    }

    /** True if {@code v} is an Item whose material matches {@code id}. */
    private static boolean itemMatchesId(PolyValue v, String id) {
        if (!(v instanceof PolyValue.Item i)) return false;
        ItemStack stack = i.stack();
        if (stack == null || stack.getType().isAir()) return false;
        Material mat = Material.matchMaterial(id);
        return mat != null ? stack.getType() == mat
                           : stack.getType().getKey().toString().equals(id);
    }

    // ---- Recursive-descent parser ------------------------------------------

    private static final class Parser {

        final String src;
        int pos;

        Parser(String src) {
            this.src = src;
            this.pos = 0;
        }

        // Grammar (lowest → highest precedence):
        //  parseExpr        = parseOr
        //  parseOr          = parseAnd ('||' parseAnd)*
        //  parseAnd         = parseNot ('&&' parseNot)*
        //  parseNot         = '!' parseNot | parseCompare
        //  parseCompare     = parseNullCoalesce (op parseNullCoalesce)?
        //  parseNullCoalesce= parseAdd ('??' parseAdd)*
        //  parseAdd         = parseMul (('+' | '−') parseMul)*
        //  parseMul         = parsePow (('*' | '/') parsePow)*
        //  parsePow         = parseUnary ('^' parsePow)?   right-assoc
        //  parseUnary       = '−' parseUnary | parsePrimary
        //  parsePrimary     = NUMBER | STRING | '$'IDENT | IDENT ('.' IDENT ('(' args ')')?)? | IDENT '(' args ')' | '(' parseExpr ')'

        Node parseExpr() { return parseTernary(); }

        // Ternary: parseOr ( '?' parseExpr ':' parseTernary )?
        // '?' is only consumed when the next char is NOT '?' (which belongs to ??).
        Node parseTernary() {
            Node condition = parseOr();
            skipSpaces();
            // Match '?' but NOT '??'
            if (pos < src.length() && src.charAt(pos) == '?'
                    && (pos + 1 >= src.length() || src.charAt(pos + 1) != '?')) {
                pos++; // consume '?'
                Node thenBranch = parseExpr();
                skipSpaces();
                if (pos < src.length() && src.charAt(pos) == ':') {
                    pos++; // consume ':'
                } else {
                    throw new IllegalArgumentException(
                            "PolyFormula: expected ':' after '?' at pos " + pos + " in: " + src);
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
                left = ctx -> PolyValue.of(l.eval(ctx).asBool() || r.eval(ctx).asBool());
            }
            return left;
        }

        Node parseAnd() {
            Node left = parseNot();
            while (match("&&")) {
                Node right = parseNot();
                Node l = left, r = right;
                left = ctx -> PolyValue.of(l.eval(ctx).asBool() && r.eval(ctx).asBool());
            }
            return left;
        }

        Node parseNot() {
            if (match("!")) {
                Node inner = parseNot();
                return ctx -> PolyValue.of(!inner.eval(ctx).asBool());
            }
            return parseCompare();
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
                PolyValue lv = l.eval(ctx), rv = r.eval(ctx);
                boolean b;
                if (fop.equals("==") || fop.equals("!=")) {
                    // Use string equality when at least one side is a Str
                    if (lv instanceof PolyValue.Str || rv instanceof PolyValue.Str) {
                        b = fop.equals("==") ? lv.asStr().equals(rv.asStr())
                                              : !lv.asStr().equals(rv.asStr());
                    } else if (lv instanceof PolyValue.Null && rv instanceof PolyValue.Null) {
                        b = fop.equals("==");
                    } else {
                        double dl = lv.asNum(), dr = rv.asNum();
                        b = fop.equals("==") ? dl == dr : dl != dr;
                    }
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
                return PolyValue.of(b);
            };
        }

        /**
         * Null-coalescing: {@code a ?? b} returns {@code a} unless it is null/empty,
         * in which case {@code b} is returned.
         * "null/empty" means {@link PolyValue.Null} or an empty/AIR {@link PolyValue.Item}.
         * Numeric zero is NOT considered null — {@code 0 ?? 5} returns {@code 0}.
         */
        Node parseNullCoalesce() {
            Node left = parseAdd();
            while (match("??")) {
                Node right = parseAdd();
                Node l = left, r = right;
                left = ctx -> {
                    PolyValue lv = l.eval(ctx);
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
                    left = ctx -> {
                        PolyValue lv = l.eval(ctx), rv = r.eval(ctx);
                        if (lv instanceof PolyValue.Str || rv instanceof PolyValue.Str) {
                            return PolyValue.of(lv.asStr() + rv.asStr());
                        }
                        return PolyValue.of(lv.asNum() + rv.asNum());
                    };
                } else if (matchAt("-")) {
                    pos++;
                    Node r = parseMul();
                    Node l = left;
                    left = ctx -> PolyValue.of(l.eval(ctx).asNum() - r.eval(ctx).asNum());
                } else break;
            }
            return left;
        }

        Node parseMul() {
            Node left = parsePow();
            while (true) {
                skipSpaces();
                if (matchAt("*")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> PolyValue.of(l.eval(ctx).asNum() * r.eval(ctx).asNum());
                } else if (matchAt("/")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> {
                        double d = r.eval(ctx).asNum();
                        return PolyValue.of(d == 0.0 ? 0.0 : l.eval(ctx).asNum() / d);
                    };
                } else if (matchAt("%")) {
                    pos++;
                    Node r = parsePow();
                    Node l = left;
                    left = ctx -> {
                        double d = r.eval(ctx).asNum();
                        return PolyValue.of(d == 0.0 ? 0.0 : l.eval(ctx).asNum() % d);
                    };
                } else break;
            }
            return left;
        }

        // Right-associative
        Node parsePow() {
            Node base = parseUnary();
            skipSpaces();
            if (matchAt("^")) {
                pos++;
                Node exp = parsePow();
                return ctx -> PolyValue.of(Math.pow(base.eval(ctx).asNum(), exp.eval(ctx).asNum()));
            }
            return base;
        }

        Node parseUnary() {
            skipSpaces();
            if (matchAt("-")) {
                pos++;
                Node inner = parseUnary();
                return ctx -> PolyValue.of(-inner.eval(ctx).asNum());
            }
            return parsePrimary();
        }

        Node parsePrimary() {
            skipSpaces();
            if (pos >= src.length())
                throw new IllegalArgumentException(
                        "PolyFormula: unexpected end of expression in: " + src);

            char c = src.charAt(pos);
            Node base;

            // ---- Parenthesized sub-expression --------------------------------
            if (c == '(') {
                pos++;
                base = parseExpr();
                skipSpaces();
                if (pos >= src.length() || src.charAt(pos) != ')')
                    throw new IllegalArgumentException(
                            "PolyFormula: missing ')' at pos " + pos + " in: " + src);
                pos++;
                return parseSuffixChain(base);
            }

            // ---- String literal  "..." or '...' ------------------------------
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
                if (pos < src.length()) pos++; // consume closing quote
                String val = sb.toString();
                base = ctx -> PolyValue.of(val);
                return parseSuffixChain(base);
            }

            // ---- $variable reference -----------------------------------------
            if (c == '$') {
                pos++;
                int start = pos;
                while (pos < src.length()
                        && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                String name = src.substring(start, pos);
                base = ctx -> ctx.get(name);
                return parseSuffixChain(base);
            }

            // ---- Number literal (integer or float with optional exponent) ----
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
                    return ctx -> PolyValue.of(v); // numbers don't need suffix chain
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "PolyFormula: invalid number '" + token + "' in: " + src);
                }
            }

            // ---- Identifier: variable, function call, or class access --------
            if (Character.isLetter(c) || c == '_') {
                int start = pos;
                while (pos < src.length()
                        && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                String name = src.substring(start, pos);
                skipSpaces();

                // Class member access: Name.property or Name.method(args)
                // Uses ctx.getClass() so the first-level dot is a compile-time class lookup.
                if (pos < src.length() && src.charAt(pos) == '.') {
                    pos++; // consume '.'
                    skipSpaces();
                    int propStart = pos;
                    while (pos < src.length()
                            && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    String member = src.substring(propStart, pos);
                    skipSpaces();

                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++; // consume '('
                        List<Node> argNodes = parseArgs();
                        String clsName = name, methodName = member;
                        base = ctx -> {
                            PolyClass cls = ctx.getClass(clsName);
                            if (cls == null) return PolyValue.NULL;
                            List<PolyValue> args = argNodes.stream()
                                    .map(n -> n.eval(ctx))
                                    .collect(Collectors.toList());
                            return cls.call(methodName, args);
                        };
                    } else {
                        String clsName = name, propName = member;
                        base = ctx -> {
                            PolyClass cls = ctx.getClass(clsName);
                            return cls != null ? cls.get(propName) : PolyValue.NULL;
                        };
                    }
                    return parseSuffixChain(base);
                }

                // Function call: name(args)
                if (pos < src.length() && src.charAt(pos) == '(') {
                    pos++; // consume '('
                    List<Node> argNodes = parseArgs();
                    String funcName = name;
                    base = ctx -> {
                        List<PolyValue> args = argNodes.stream()
                                .map(n -> n.eval(ctx))
                                .collect(Collectors.toList());
                        return callBuiltin(funcName, args, ctx);
                    };
                    return parseSuffixChain(base);
                }

                // Named constants — intercepted before variable lookup
                if ("PI".equals(name) || "Math_PI".equals(name)) return ctx -> PolyValue.of(Math.PI);
                if ("TAU".equals(name))  return ctx -> PolyValue.of(Math.PI * 2);
                if ("E".equals(name))    return ctx -> PolyValue.of(Math.E);
                if ("INF".equals(name))  return ctx -> PolyValue.of(Double.POSITIVE_INFINITY);
                if ("NAN".equals(name))  return ctx -> PolyValue.of(Double.NaN);
                if ("TRUE".equals(name)  || "true".equals(name))  return ctx -> PolyValue.of(true);
                if ("FALSE".equals(name) || "false".equals(name)) return ctx -> PolyValue.of(false);

                // Plain variable reference (bare name, no $ required)
                base = ctx -> ctx.get(name);
                return parseSuffixChain(base);
            }

            throw new IllegalArgumentException(
                    "PolyFormula: unexpected character '" + c
                    + "' at pos " + pos + " in: " + src);
        }

        /**
         * Consume a chain of {@code .member}, {@code .method(args)}, and {@code [index]}
         * suffix operations on top of an already-parsed base node.
         *
         * <p>Each iteration wraps the current {@code base} node in a new dispatch node
         * that calls {@link PolyFormula#memberGet}, {@link PolyFormula#memberCall}, or
         * {@link PolyFormula#subscriptGet} at evaluation time.</p>
         *
         * <p>Example chains:
         * <ul>
         *   <li>{@code slot(9).amount}     — Item property access</li>
         *   <li>{@code slot(9).lore[0]}    — Item lore, then subscript the first line</li>
         *   <li>{@code Workbench.input(0).type} — Class method + property</li>
         * </ul>
         * </p>
         */
        Node parseSuffixChain(Node base) {
            while (true) {
                skipSpaces();
                if (pos >= src.length()) break;
                char ch = src.charAt(pos);
                if (ch == '.') {
                    pos++; // consume '.'
                    skipSpaces();
                    int mStart = pos;
                    while (pos < src.length()
                            && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) pos++;
                    if (pos == mStart) break; // empty member name — stop chain
                    String member = src.substring(mStart, pos);
                    skipSpaces();
                    if (pos < src.length() && src.charAt(pos) == '(') {
                        pos++; // consume '('
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
                    pos++; // consume '['
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

        /**
         * Parse a comma-separated argument list up to (and consuming) the closing ')'.
         * Supports range notation {@code a..b} which expands to individual integer nodes.
         */
        List<Node> parseArgs() {
            List<Node> args = new ArrayList<>();
            skipSpaces();
            if (pos < src.length() && src.charAt(pos) == ')') {
                pos++;
                return args;
            }
            while (true) {
                skipSpaces();
                // Check for integer range: DIGITS..DIGITS
                int savedPos = pos;
                if (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                    int numStart = pos;
                    while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                    // Is the next thing ".." (and not a single "." which would make a float)?
                    if (pos + 1 < src.length()
                            && src.charAt(pos) == '.' && src.charAt(pos + 1) == '.') {
                        int from = Integer.parseInt(src.substring(numStart, pos));
                        pos += 2; // skip ".."
                        int toStart = pos;
                        while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
                        if (pos == toStart)
                            throw new IllegalArgumentException(
                                    "PolyFormula: expected integer after '..' at pos " + pos + " in: " + src);
                        int to = Integer.parseInt(src.substring(toStart, pos));
                        int step = from <= to ? 1 : -1;
                        for (int i = from; i != to + step; i += step) {
                            final double v = i;
                            args.add(ctx -> PolyValue.of(v));
                        }
                    } else {
                        // Not a range — backtrack and parse as full expression
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

        // ---- Helpers -------------------------------------------------------

        void skipSpaces() {
            while (pos < src.length() && src.charAt(pos) == ' ') pos++;
        }

        /** Skip spaces, consume {@code token} (if present), return true if consumed. */
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
