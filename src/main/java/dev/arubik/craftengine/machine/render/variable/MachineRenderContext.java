package dev.arubik.craftengine.machine.render.variable;

import dev.arubik.craftengine.machine.render.formula.FluidTanksClass;
import dev.arubik.craftengine.machine.render.formula.GasTanksClass;
import dev.arubik.craftengine.machine.render.formula.PolyClass;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyFormula;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-tick evaluated snapshot of a machine's state, passed to the RendererManager.
 *
 * <p>The name {@code MachineRenderContext} is intentional — it avoids collision with
 * the contraption package's {@code RenderContext} type.</p>
 *
 * <p>Callers construct this once per tick from the machine's live fields and hand it
 * to {@code RendererManager.tick(context, varSpecs)}. The context is immutable after
 * construction; all evaluation helpers are pure given the same {@code varSpecs} map.</p>
 *
 * <p>Expression evaluation is now backed by {@link PolyFormula} via
 * {@link #toPolyContext()}.  All eval methods catch parse/runtime errors and return
 * safe defaults rather than throwing.</p>
 */
public final class MachineRenderContext {

    // Numeric built-in sources
    private final double rpm;
    private final double overclock;
    private final double efficiency;
    private final double progress;
    private final double maxProgress;
    private final double tier;

    // Boolean built-in sources
    private final boolean processing;
    private final boolean powered;
    private final boolean overclocked;
    private final boolean hasFuel;

    // Container for item-slot variables (may be null when the machine has no inventory)
    private final Inventory container;

    // Upgrade counts by type (may be null / empty)
    private final Map<String, Integer> upgradesByType;

    // Redstone power level at the machine's position (0-15)
    private final int redstonePower;

    // Tank data maps: tank name → [level, capacity].  Empty when the machine has no tanks.
    private final Map<String, double[]> fluidTankData;
    private final Map<String, double[]> gasTankData;

    // Lazily built PolyContext; rebuilt if null (context is immutable so we build once)
    private PolyContext polyContextCache;

    /** Minimal constructor — no upgrades, no tank data. */
    public MachineRenderContext(
            double rpm,
            double overclock,
            double efficiency,
            double progress,
            double maxProgress,
            double tier,
            boolean processing,
            boolean powered,
            boolean overclocked,
            boolean hasFuel,
            Inventory container) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel, container,
                null, Map.of(), Map.of());
    }

    /** Constructor with upgrade map but no tank data. */
    public MachineRenderContext(
            double rpm,
            double overclock,
            double efficiency,
            double progress,
            double maxProgress,
            double tier,
            boolean processing,
            boolean powered,
            boolean overclocked,
            boolean hasFuel,
            Inventory container,
            Map<String, Integer> upgradesByType) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel, container,
                upgradesByType, Map.of(), Map.of());
    }

    /**
     * Constructor with fluid and gas tank data but no upgrade map.
     *
     * <p>The tank maps are keyed by tank name and each value is a two-element
     * array {@code [level, capacity]}.  Pass {@link Map#of()} for machines that
     * have no tanks of a given kind.</p>
     */
    public MachineRenderContext(
            double rpm,
            double overclock,
            double efficiency,
            double progress,
            double maxProgress,
            double tier,
            boolean processing,
            boolean powered,
            boolean overclocked,
            boolean hasFuel,
            Inventory container,
            Map<String, double[]> fluidTankData,
            Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel, container,
                null, fluidTankData, gasTankData);
    }

    /**
     * Full constructor — upgrades and tank data. Redstone defaults to 0.
     */
    public MachineRenderContext(
            double rpm,
            double overclock,
            double efficiency,
            double progress,
            double maxProgress,
            double tier,
            boolean processing,
            boolean powered,
            boolean overclocked,
            boolean hasFuel,
            Inventory container,
            Map<String, Integer> upgradesByType,
            Map<String, double[]> fluidTankData,
            Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel, container,
                upgradesByType, fluidTankData, gasTankData, 0);
    }

    /**
     * Full constructor — upgrades, tank data, and redstone power.
     *
     * @param redstonePower redstone signal strength at the machine's position (0-15)
     */
    public MachineRenderContext(
            double rpm,
            double overclock,
            double efficiency,
            double progress,
            double maxProgress,
            double tier,
            boolean processing,
            boolean powered,
            boolean overclocked,
            boolean hasFuel,
            Inventory container,
            Map<String, Integer> upgradesByType,
            Map<String, double[]> fluidTankData,
            Map<String, double[]> gasTankData,
            int redstonePower) {
        this.rpm          = rpm;
        this.overclock    = overclock;
        this.efficiency   = efficiency;
        this.progress     = progress;
        this.maxProgress  = maxProgress;
        this.tier         = tier;
        this.processing   = processing;
        this.powered      = powered;
        this.overclocked  = overclocked;
        this.hasFuel      = hasFuel;
        this.container    = container;
        this.upgradesByType  = upgradesByType;
        this.fluidTankData   = fluidTankData  != null ? fluidTankData  : Map.of();
        this.gasTankData     = gasTankData    != null ? gasTankData    : Map.of();
        this.redstonePower   = redstonePower;
    }

    // ---- PolyContext bridge -------------------------------------------------

    /**
     * Build (and cache) the full {@link PolyContext} for this machine snapshot.
     *
     * <p>The context exposes all numeric/boolean machine variables, an
     * {@code Inventory} class when a container is present, and an {@code Upgrades}
     * class when upgrade counts have been supplied.</p>
     */
    public PolyContext toPolyContext() {
        if (polyContextCache == null) {
            PolyContext base = PolyContext.forMachine(
                    rpm, overclock, efficiency, progress, maxProgress, tier,
                    processing, powered, overclocked, hasFuel,
                    container, upgradesByType, fluidTankData, gasTankData);
            if (redstonePower != 0) {
                // Augment with redstone variables
                polyContextCache = PolyContext.builder()
                        .copyFrom(base)
                        .redstone(redstonePower)
                        .build();
            } else {
                polyContextCache = base;
            }
        }
        return polyContextCache;
    }

    /**
     * Return a copy of this context backed by the supplied (pre-augmented)
     * {@link PolyContext}.  All {@code eval*} methods on the returned instance
     * will use {@code poly} rather than lazily building their own context.
     *
     * <p>Used by {@link dev.arubik.craftengine.machine.render.RendererManager}
     * to apply a {@link dev.arubik.craftengine.machine.render.formula.PolyScript}
     * before evaluating renderer expressions.</p>
     */
    public MachineRenderContext augmented(PolyContext poly) {
        MachineRenderContext copy = new MachineRenderContext(
                rpm, overclock, efficiency, progress, maxProgress, tier,
                processing, powered, overclocked, hasFuel,
                container, upgradesByType, fluidTankData, gasTankData, redstonePower);
        copy.polyContextCache = poly;
        return copy;
    }

    // ---- Variable map (legacy, kept for SpeedFormula call sites) -----------

    /**
     * Build the numeric variable map consumed by
     * {@link dev.arubik.craftengine.machine.render.SpeedFormula#evaluate(Map)}.
     * Booleans are included as {@code 1.0} / {@code 0.0}.
     *
     * @deprecated Prefer {@link #toPolyContext()} and {@link PolyFormula}.
     */
    @Deprecated
    public Map<String, Double> numericVars() {
        Map<String, Double> m = new HashMap<>(16);
        m.put("rpm",          rpm);
        m.put("overclock",    overclock);
        m.put("efficiency",   efficiency);
        m.put("progress",     progress);
        m.put("max_progress", maxProgress);
        m.put("tier",         tier);
        m.put("generation",   tier);  // alias: curGeneration is passed as tier
        m.put("processing",   processing   ? 1.0 : 0.0);
        m.put("powered",      powered      ? 1.0 : 0.0);
        m.put("overclocked",  overclocked  ? 1.0 : 0.0);
        m.put("has_fuel",     hasFuel      ? 1.0 : 0.0);
        return m;
    }

    // ---- Evaluation helpers ------------------------------------------------

    /**
     * Evaluate a condition expression or {@code "$varname"} reference as a boolean.
     *
     * <ul>
     *   <li>{@code "always"} / {@code "true"}  → {@code true}</li>
     *   <li>{@code "never"}  / {@code "false"} → {@code false}</li>
     *   <li>{@code "$name"}  → looks up the named spec in {@code varSpecs} and evaluates it</li>
     *   <li>anything else    → compiled as a {@link PolyFormula} expression</li>
     * </ul>
     *
     * Never throws; parse or evaluation errors return {@code false}.
     */
    public boolean evalBool(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        if (exprOrRef == null) return false;
        switch (exprOrRef) {
            case "always", "true"  -> { return true; }
            case "never",  "false" -> { return false; }
        }
        if (exprOrRef.startsWith("$")) {
            String name = exprOrRef.substring(1);
            VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec == null) return false;
            return evalSpecBool(spec, varSpecs);
        }
        // Inline PolyFormula expression
        try {
            return PolyFormula.compile(exprOrRef).evaluateBool(toPolyContext());
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Evaluate a numeric expression or {@code "$varname"} reference.
     *
     * <ul>
     *   <li>{@code null}    → {@code 1.0}</li>
     *   <li>{@code "$name"} → looks up a {@link VariableSpec.NumExpr} spec and evaluates it;
     *       non-numeric specs fall back to {@code 1.0}</li>
     *   <li>bare number literal → parsed directly</li>
     *   <li>anything else   → compiled as a {@link PolyFormula} expression</li>
     * </ul>
     *
     * Never throws; parse or evaluation errors return {@code 1.0}.
     */
    public double evalNum(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        if (exprOrRef == null) return 1.0;
        if (exprOrRef.startsWith("$")) {
            String name = exprOrRef.substring(1);
            VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec instanceof VariableSpec.NumExpr ne) {
                try {
                    return PolyFormula.compile(ne.expr()).evaluateNum(toPolyContext());
                } catch (Throwable ignored) {
                    return 1.0;
                }
            }
            if (spec instanceof VariableSpec.TankVar tv) {
                return resolveTankValue(tv).asNum();
            }
            if (spec instanceof VariableSpec.Formula f) {
                try {
                    return PolyFormula.compile(f.expr()).evaluateNum(toPolyContext());
                } catch (Throwable ignored) {
                    return 1.0;
                }
            }
            return 1.0;
        }
        // Fast path: bare literal
        try {
            return Double.parseDouble(exprOrRef);
        } catch (NumberFormatException ignored) { /* fall through */ }
        // Inline PolyFormula expression
        try {
            return PolyFormula.compile(exprOrRef).evaluateNum(toPolyContext());
        } catch (Throwable ignored) {
            return 1.0;
        }
    }

    /**
     * Resolve an expression to a live {@link ItemStack}.
     *
     * <p>Accepts either:
     * <ul>
     *   <li>{@code "$varname"} — looks up an {@link VariableSpec.ItemSlot} spec and
     *       returns {@code container.getItem(slot)}</li>
     *   <li>A full {@link PolyFormula} expression that evaluates to a
     *       {@link dev.arubik.craftengine.machine.render.formula.PolyValue.Item} —
     *       e.g. {@code "slot(9)"} or {@code "CraftEngineItem('polyfills:copper_ingot')"}</li>
     * </ul>
     *
     * Returns {@code null} when the result is absent or cannot be resolved.
     * Never throws.
     */
    public ItemStack evalItem(String ref, Map<String, VariableSpec> varSpecs) {
        if (ref == null) return null;
        if (ref.startsWith("$")) {
            String name = ref.substring(1);
            VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec instanceof VariableSpec.ItemSlot is) {
                try {
                    return container != null ? container.getItem(is.slot()) : null;
                } catch (Throwable ignored) { /* fall through */ }
            }
            if (spec instanceof VariableSpec.Formula f) {
                try {
                    return PolyFormula.compile(f.expr()).evaluateItem(toPolyContext());
                } catch (Throwable ignored) { /* fall through */ }
            }
            return null;
        }
        // Inline PolyFormula expression (e.g. slot(9), CraftEngineItem("id"))
        try {
            return PolyFormula.compile(ref).evaluateItem(toPolyContext());
        } catch (Throwable ignored) {
            return null;
        }
    }

    // ---- Internal spec evaluator -------------------------------------------

    private boolean evalSpecBool(VariableSpec spec, Map<String, VariableSpec> varSpecs) {
        return switch (spec) {
            case VariableSpec.BoolSource bs -> switch (bs.source()) {
                case "processing"  -> processing;
                case "powered"     -> powered;
                case "overclocked" -> overclocked;
                case "has_fuel"    -> hasFuel;
                case "always"      -> true;
                case "never"       -> false;
                default            -> false;
            };
            case VariableSpec.BoolExpr be -> {
                try {
                    yield PolyFormula.compile(be.expr()).evaluateBool(toPolyContext());
                } catch (Throwable ignored) {
                    yield false;
                }
            }
            case VariableSpec.NumExpr ne -> {
                try {
                    yield PolyFormula.compile(ne.expr()).evaluateNum(toPolyContext()) != 0.0;
                } catch (Throwable ignored) {
                    yield false;
                }
            }
            case VariableSpec.ItemSlot is ->
                container != null && container.getItem(is.slot()) != null;
            case VariableSpec.TankVar tv ->
                resolveTankValue(tv).asBool();
            case VariableSpec.Formula f -> {
                try {
                    yield PolyFormula.compile(f.expr()).evaluateBool(toPolyContext());
                } catch (Throwable ignored) {
                    yield false;
                }
            }
        };
    }

    /** Resolve a {@link VariableSpec.TankVar} to a {@link PolyValue} using the cached context. */
    private PolyValue resolveTankValue(VariableSpec.TankVar tv) {
        PolyClass cls = toPolyContext().getClass(tv.isGas() ? "GasTanks" : "FluidTanks");
        if (cls instanceof FluidTanksClass ftc)
            return ftc.forTank(tv.tankName()).get(tv.property());
        if (cls instanceof GasTanksClass gtc)
            return gtc.forTank(tv.tankName()).get(tv.property());
        return PolyValue.NULL;
    }

    // ---- Getters -----------------------------------------------------------

    public double rpm()          { return rpm; }
    public double overclock()    { return overclock; }
    public double efficiency()   { return efficiency; }
    public double progress()     { return progress; }
    public double maxProgress()  { return maxProgress; }
    public double tier()         { return tier; }
    public boolean processing()  { return processing; }
    public boolean powered()     { return powered; }
    public boolean overclocked() { return overclocked; }
    public boolean hasFuel()     { return hasFuel; }
    public Inventory container() { return container; }
    public Map<String, Integer> upgradesByType() { return upgradesByType; }
    public Map<String, double[]> fluidTankData() { return fluidTankData; }
    public Map<String, double[]> gasTankData()   { return gasTankData; }
}
