package dev.arubik.craftengine.machine.render.variable;

import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptFormula;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MachineRenderContext {
    private final double rpm;
    private final double overclock;
    private final double efficiency;
    private final double progress;
    private final double maxProgress;
    private final double tier;
    private final boolean processing;
    private final boolean powered;
    private final boolean overclocked;
    private final boolean hasFuel;
    private final Inventory container;
    private final Map<String, Integer> upgradesByType;
    private final int redstonePower;
    private final Map<String, double[]> fluidTankData;
    private final Map<String, double[]> gasTankData;
    private ScriptContext scriptContextCache;

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier,
                                boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, null, Map.of(), Map.of());
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier,
                                boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container,
                                Map<String, Integer> upgradesByType) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, upgradesByType, Map.of(), Map.of());
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier,
                                boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container,
                                Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, null, fluidTankData, gasTankData);
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier,
                                boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container,
                                Map<String, Integer> upgradesByType, Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, upgradesByType, fluidTankData, gasTankData, 0);
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier,
                                boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container,
                                Map<String, Integer> upgradesByType, Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData,
                                int redstonePower) {
        this.rpm = rpm;
        this.overclock = overclock;
        this.efficiency = efficiency;
        this.progress = progress;
        this.maxProgress = maxProgress;
        this.tier = tier;
        this.processing = processing;
        this.powered = powered;
        this.overclocked = overclocked;
        this.hasFuel = hasFuel;
        this.container = container;
        this.upgradesByType = upgradesByType;
        this.fluidTankData = fluidTankData != null ? fluidTankData : Map.of();
        this.gasTankData = gasTankData != null ? gasTankData : Map.of();
        this.redstonePower = redstonePower;
    }

    public ScriptContext toScriptContext() {
        if (scriptContextCache == null) {
            ScriptContext.Builder b = ScriptContext.builder()
                .machine(rpm, overclock, efficiency, progress, maxProgress, tier,
                         processing, powered, overclocked, hasFuel);
            // NOTE: bound via typed()/classInstances, not val()/vars — the bare-function shortcuts
            // that read these (ScriptFormula's "slot"/"slots"/"FluidTank"/"GasTank" cases) look
            // them up via ctx.getClassInstance(...) specifically, the same convention Machine/
            // Player/World/Server use. Binding them as plain vars left slot(n) (and FluidTank(...)/
            // GasTank(...)) always resolving to NULL — e.g. depot's item_display renderers
            // ("item": "slot(0)") never showing the items actually sitting in the block.
            if (!fluidTankData.isEmpty())
                b.typed("FluidTanks", fluidTankData);
            if (!gasTankData.isEmpty())
                b.typed("GasTanks", gasTankData);
            if (container != null)
                b.typed("Inventory", toNmsSlots(container));
            if (upgradesByType != null && !upgradesByType.isEmpty()) {
                upgradesByType.forEach((k, v) -> b.num("upgrade_" + k, v != null ? v : 0));
                b.num("upgrade_count", upgradesByType.values().stream().mapToInt(i -> i != null ? i : 0).sum());
            }
            if (redstonePower != 0) b.redstone(redstonePower);
            scriptContextCache = b.buildOnce();
        }
        return scriptContextCache;
    }

    public MachineRenderContext augmented(ScriptContext ctx) {
        MachineRenderContext copy = new MachineRenderContext(rpm, overclock, efficiency, progress, maxProgress, tier,
            processing, powered, overclocked, hasFuel, container, upgradesByType, fluidTankData, gasTankData, redstonePower);
        copy.scriptContextCache = ctx;
        return copy;
    }

    @Deprecated
    public Map<String, Double> numericVars() {
        HashMap<String, Double> m = new HashMap<>(16);
        m.put("rpm", rpm); m.put("overclock", overclock); m.put("efficiency", efficiency);
        m.put("progress", progress); m.put("max_progress", maxProgress); m.put("tier", tier);
        m.put("generation", tier); m.put("processing", processing ? 1.0 : 0.0);
        m.put("powered", powered ? 1.0 : 0.0); m.put("overclocked", overclocked ? 1.0 : 0.0);
        m.put("has_fuel", hasFuel ? 1.0 : 0.0);
        return m;
    }

    public boolean evalBool(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        if (exprOrRef == null) return false;
        return switch (exprOrRef) {
            case "always", "true" -> true;
            case "never", "false" -> false;
            default -> {
                if (exprOrRef.startsWith("$")) {
                    String name = exprOrRef.substring(1);
                    VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
                    yield spec != null && evalSpecBool(spec, varSpecs);
                }
                if (varSpecs != null && varSpecs.containsKey(exprOrRef)) {
                    VariableSpec vs = varSpecs.get(exprOrRef);
                    if (vs instanceof VariableSpec.Formula f) {
                        try { yield ScriptFormula.compile(f.expr()).evaluateBool(contextWithVars(varSpecs)); }
                        catch (Throwable ignored) { yield false; }
                    }
                    yield evalSpecBool(vs, varSpecs);
                }
                try { yield ScriptFormula.compile(exprOrRef).evaluateBool(contextWithVars(varSpecs)); }
                catch (Throwable ignored) { yield false; }
            }
        };
    }

    public double evalNum(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        if (exprOrRef == null) return 1.0;
        if (exprOrRef.startsWith("$")) {
            String name = exprOrRef.substring(1);
            VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec instanceof VariableSpec.NumExpr ne) {
                try { return ScriptFormula.compile(ne.expr()).evaluateNum(toScriptContext()); } catch (Throwable ignored) { return 1.0; }
            }
            if (spec instanceof VariableSpec.TankVar tv) return resolveTankValue(tv).asNum();
            if (spec instanceof VariableSpec.Formula f) {
                try { return ScriptFormula.compile(f.expr()).evaluateNum(toScriptContext()); } catch (Throwable ignored) { return 1.0; }
            }
            return 1.0;
        }
        if (varSpecs != null && varSpecs.containsKey(exprOrRef) && varSpecs.get(exprOrRef) instanceof VariableSpec.Formula f) {
            try { return ScriptFormula.compile(f.expr()).evaluateNum(contextWithVars(varSpecs)); } catch (Throwable ignored) {}
        }
        try { return Double.parseDouble(exprOrRef); }
        catch (NumberFormatException nfe) {
            try { return ScriptFormula.compile(exprOrRef).evaluateNum(contextWithVars(varSpecs)); }
            catch (Throwable ignored) { return 1.0; }
        }
    }

    public ItemStack evalItem(String ref, Map<String, VariableSpec> varSpecs) {
        if (ref == null) return null;
        if (ref.startsWith("$")) {
            String name = ref.substring(1);
            VariableSpec spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec instanceof VariableSpec.ItemSlot is) {
                try {
                    if (container == null) return null;
                    org.bukkit.inventory.ItemStack bukkit = container.getItem(is.slot());
                    return bukkit != null ? CraftItemStack.asNMSCopy(bukkit) : null;
                } catch (Throwable ignored) {}
            }
            if (spec instanceof VariableSpec.Formula f) {
                try {
                    ScriptValue sv = ScriptFormula.compile(f.expr()).evaluate(toScriptContext());
                    return scriptValueToNms(sv);
                } catch (Throwable ignored) {}
            }
            return null;
        }
        try {
            ScriptValue sv = ScriptFormula.compile(ref).evaluate(toScriptContext());
            return scriptValueToNms(sv);
        } catch (Throwable ignored) { return null; }
    }

    private ScriptContext contextWithVars(Map<String, VariableSpec> varSpecs) {
        if (varSpecs == null || varSpecs.isEmpty()) return toScriptContext();
        ScriptContext.Builder b1 = ScriptContext.builder().copyFrom(toScriptContext());
        for (Map.Entry<String, VariableSpec> e : varSpecs.entrySet()) {
            if (e.getValue() instanceof VariableSpec.Formula f) {
                try { b1.val(e.getKey(), ScriptFormula.compile(f.expr()).evaluate(toScriptContext())); }
                catch (Throwable ignored) {}
            }
        }
        ScriptContext pass1 = b1.build();
        ScriptContext.Builder b2 = ScriptContext.builder().copyFrom(pass1);
        for (Map.Entry<String, VariableSpec> e : varSpecs.entrySet()) {
            if (e.getValue() instanceof VariableSpec.Formula f) {
                try { b2.val(e.getKey(), ScriptFormula.compile(f.expr()).evaluate(pass1)); }
                catch (Throwable ignored) {}
            }
        }
        return b2.build();
    }

    private boolean evalSpecBool(VariableSpec spec, Map<String, VariableSpec> varSpecs) {
        Objects.requireNonNull(spec);
        if (spec instanceof VariableSpec.BoolSource bs) {
            return switch (bs.source()) {
                case "processing" -> processing;
                case "powered" -> powered;
                case "overclocked" -> overclocked;
                case "has_fuel" -> hasFuel;
                case "always" -> true;
                case "never" -> false;
                default -> false;
            };
        }
        if (spec instanceof VariableSpec.BoolExpr be) {
            try { return ScriptFormula.compile(be.expr()).evaluateBool(toScriptContext()); } catch (Throwable ignored) { return false; }
        }
        if (spec instanceof VariableSpec.NumExpr ne) {
            try { return ScriptFormula.compile(ne.expr()).evaluateNum(toScriptContext()) != 0.0; } catch (Throwable ignored) { return false; }
        }
        if (spec instanceof VariableSpec.ItemSlot is) return container != null && container.getItem(is.slot()) != null;
        if (spec instanceof VariableSpec.TankVar tv) return resolveTankValue(tv).asBool();
        if (spec instanceof VariableSpec.Formula f) {
            try { return ScriptFormula.compile(f.expr()).evaluateBool(toScriptContext()); } catch (Throwable ignored) { return false; }
        }
        return false;
    }

    private ScriptValue resolveTankValue(VariableSpec.TankVar tv) {
        ScriptValue tanksVal = toScriptContext().getVar(tv.isGas() ? "GasTanks" : "FluidTanks");
        if (tanksVal instanceof ScriptValue.Obj o) {
            return tanksVal.callMethod("get", List.of(ScriptValue.of(tv.tankName()), ScriptValue.of(tv.property())));
        }
        return ScriptValue.NULL;
    }

    private static ItemStack scriptValueToNms(ScriptValue sv) {
        if (sv instanceof ScriptValue.Item i) return i.stack();
        return null;
    }

    private static net.minecraft.world.item.ItemStack[] toNmsSlots(Inventory inv) {
        if (inv == null) return new net.minecraft.world.item.ItemStack[0];
        net.minecraft.world.item.ItemStack[] slots = new net.minecraft.world.item.ItemStack[inv.getSize()];
        for (int i = 0; i < inv.getSize(); i++) {
            org.bukkit.inventory.ItemStack b = inv.getItem(i);
            slots[i] = b != null ? CraftItemStack.asNMSCopy(b) : net.minecraft.world.item.ItemStack.EMPTY;
        }
        return slots;
    }

    // Getters
    public double rpm() { return rpm; }
    public double overclock() { return overclock; }
    public double efficiency() { return efficiency; }
    public double progress() { return progress; }
    public double maxProgress() { return maxProgress; }
    public double tier() { return tier; }
    public boolean processing() { return processing; }
    public boolean powered() { return powered; }
    public boolean overclocked() { return overclocked; }
    public boolean hasFuel() { return hasFuel; }
    public Inventory container() { return container; }
    public Map<String, Integer> upgradesByType() { return upgradesByType; }
    public Map<String, double[]> fluidTankData() { return fluidTankData; }
    public Map<String, double[]> gasTankData() { return gasTankData; }
}
