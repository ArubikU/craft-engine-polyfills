/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.machine.render.variable;

import dev.arubik.craftengine.machine.render.formula.FluidTanksClass;
import dev.arubik.craftengine.machine.render.formula.GasTanksClass;
import dev.arubik.craftengine.machine.render.formula.PolyClass;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyFormula;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
    private PolyContext polyContextCache;

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, null, Map.of(), Map.of());
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container, Map<String, Integer> upgradesByType) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, upgradesByType, Map.of(), Map.of());
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container, Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, null, fluidTankData, gasTankData);
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container, Map<String, Integer> upgradesByType, Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData) {
        this(rpm, overclock, efficiency, progress, maxProgress, tier, processing, powered, overclocked, hasFuel, container, upgradesByType, fluidTankData, gasTankData, 0);
    }

    public MachineRenderContext(double rpm, double overclock, double efficiency, double progress, double maxProgress, double tier, boolean processing, boolean powered, boolean overclocked, boolean hasFuel, Inventory container, Map<String, Integer> upgradesByType, Map<String, double[]> fluidTankData, Map<String, double[]> gasTankData, int redstonePower) {
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

    public PolyContext toPolyContext() {
        if (this.polyContextCache == null) {
            PolyContext base = PolyContext.forMachine(this.rpm, this.overclock, this.efficiency, this.progress, this.maxProgress, this.tier, this.processing, this.powered, this.overclocked, this.hasFuel, this.container, this.upgradesByType, this.fluidTankData, this.gasTankData);
            this.polyContextCache = this.redstonePower != 0 ? PolyContext.builder().copyFrom(base).redstone(this.redstonePower).build() : base;
        }
        return this.polyContextCache;
    }

    public MachineRenderContext augmented(PolyContext poly) {
        MachineRenderContext copy = new MachineRenderContext(this.rpm, this.overclock, this.efficiency, this.progress, this.maxProgress, this.tier, this.processing, this.powered, this.overclocked, this.hasFuel, this.container, this.upgradesByType, this.fluidTankData, this.gasTankData, this.redstonePower);
        copy.polyContextCache = poly;
        return copy;
    }

    @Deprecated
    public Map<String, Double> numericVars() {
        HashMap<String, Double> m = new HashMap<String, Double>(16);
        m.put("rpm", this.rpm);
        m.put("overclock", this.overclock);
        m.put("efficiency", this.efficiency);
        m.put("progress", this.progress);
        m.put("max_progress", this.maxProgress);
        m.put("tier", this.tier);
        m.put("generation", this.tier);
        m.put("processing", this.processing ? 1.0 : 0.0);
        m.put("powered", this.powered ? 1.0 : 0.0);
        m.put("overclocked", this.overclocked ? 1.0 : 0.0);
        m.put("has_fuel", this.hasFuel ? 1.0 : 0.0);
        return m;
    }

    public boolean evalBool(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        if (exprOrRef == null) {
            return false;
        }
        switch (exprOrRef) {
            case "always": 
            case "true": {
                return true;
            }
            case "never": 
            case "false": {
                return false;
            }
        }
        if (exprOrRef.startsWith("$")) {
            VariableSpec spec;
            String name = exprOrRef.substring(1);
            VariableSpec variableSpec = spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec == null) {
                return false;
            }
            return this.evalSpecBool(spec, varSpecs);
        }
        if (varSpecs != null && varSpecs.containsKey(exprOrRef)) {
            VariableSpec vs = varSpecs.get(exprOrRef);
            if (vs instanceof VariableSpec.Formula) {
                VariableSpec.Formula f = (VariableSpec.Formula)vs;
                try {
                    return PolyFormula.compile(f.expr()).evaluateBool(this.contextWithVars(varSpecs));
                }
                catch (Throwable ig) {
                    return false;
                }
            }
            return this.evalSpecBool(vs, varSpecs);
        }
        try {
            return PolyFormula.compile(exprOrRef).evaluateBool(this.contextWithVars(varSpecs));
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    public double evalNum(String exprOrRef, Map<String, VariableSpec> varSpecs) {
        VariableSpec spec3;
        if (exprOrRef == null) {
            return 1.0;
        }
        if (exprOrRef.startsWith("$")) {
            VariableSpec spec2;
            String name = exprOrRef.substring(1);
            VariableSpec variableSpec = spec2 = varSpecs != null ? varSpecs.get(name) : null;
            if (spec2 instanceof VariableSpec.NumExpr) {
                VariableSpec.NumExpr ne = (VariableSpec.NumExpr)spec2;
                try {
                    return PolyFormula.compile(ne.expr()).evaluateNum(this.toPolyContext());
                }
                catch (Throwable ignored) {
                    return 1.0;
                }
            }
            if (spec2 instanceof VariableSpec.TankVar) {
                VariableSpec.TankVar tv = (VariableSpec.TankVar)spec2;
                return this.resolveTankValue(tv).asNum();
            }
            if (spec2 instanceof VariableSpec.Formula) {
                VariableSpec.Formula f = (VariableSpec.Formula)spec2;
                try {
                    return PolyFormula.compile(f.expr()).evaluateNum(this.toPolyContext());
                }
                catch (Throwable ignored) {
                    return 1.0;
                }
            }
            return 1.0;
        }
        if (varSpecs != null && varSpecs.containsKey(exprOrRef) && (spec3 = varSpecs.get(exprOrRef)) instanceof VariableSpec.Formula) {
            VariableSpec.Formula f = (VariableSpec.Formula)spec3;
            try {
                return PolyFormula.compile(f.expr()).evaluateNum(this.contextWithVars(varSpecs));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        try {
            return Double.parseDouble(exprOrRef);
        }
        catch (NumberFormatException nfe) {
            try {
                return PolyFormula.compile(exprOrRef).evaluateNum(this.contextWithVars(varSpecs));
            }
            catch (Throwable ignored) {
                return 1.0;
            }
        }
    }

    public net.minecraft.world.item.ItemStack evalItem(String ref, Map<String, VariableSpec> varSpecs) {
        if (ref == null) {
            return null;
        }
        if (ref.startsWith("$")) {
            VariableSpec spec;
            String name = ref.substring(1);
            VariableSpec variableSpec = spec = varSpecs != null ? varSpecs.get(name) : null;
            if (spec instanceof VariableSpec.ItemSlot) {
                VariableSpec.ItemSlot is = (VariableSpec.ItemSlot)spec;
                try {
                    if (this.container == null) {
                        return null;
                    }
                    ItemStack bukkit = this.container.getItem(is.slot());
                    return bukkit != null ? CraftItemStack.asNMSCopy((ItemStack)bukkit) : null;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (spec instanceof VariableSpec.Formula) {
                VariableSpec.Formula f = (VariableSpec.Formula)spec;
                try {
                    org.bukkit.inventory.ItemStack bi = PolyFormula.compile(f.expr()).evaluateItem(this.toPolyContext());
                    return bi != null ? CraftItemStack.asNMSCopy(bi) : null;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return null;
        }
        try {
            org.bukkit.inventory.ItemStack bi = PolyFormula.compile(ref).evaluateItem(this.toPolyContext());
            return bi != null ? CraftItemStack.asNMSCopy(bi) : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private PolyContext contextWithVars(Map<String, VariableSpec> varSpecs) {
        if (varSpecs == null || varSpecs.isEmpty()) {
            return this.toPolyContext();
        }
        PolyContext.Builder builder1 = PolyContext.builder().copyFrom(this.toPolyContext());
        for (Map.Entry<String, VariableSpec> e : varSpecs.entrySet()) {
            try {
                VariableSpec variableSpec = e.getValue();
                if (!(variableSpec instanceof VariableSpec.Formula)) continue;
                VariableSpec.Formula f = (VariableSpec.Formula)variableSpec;
                try {
                    builder1.val(e.getKey(), PolyFormula.compile(f.expr()).evaluate(this.toPolyContext()));
                }
                catch (Throwable throwable) {
                }
            }
            catch (Throwable throwable) {}
        }
        PolyContext pass1 = builder1.build();
        PolyContext.Builder builder2 = PolyContext.builder().copyFrom(pass1);
        for (Map.Entry<String, VariableSpec> e : varSpecs.entrySet()) {
            try {
                VariableSpec variableSpec = e.getValue();
                if (!(variableSpec instanceof VariableSpec.Formula)) continue;
                VariableSpec.Formula f = (VariableSpec.Formula)variableSpec;
                try {
                    builder2.val(e.getKey(), PolyFormula.compile(f.expr()).evaluate(pass1));
                }
                catch (Throwable throwable) {
                }
            }
            catch (Throwable throwable) {}
        }
        return builder2.build();
    }

    private boolean evalSpecBool(VariableSpec spec, Map<String, VariableSpec> varSpecs) {
        Objects.requireNonNull(spec);
        if (spec instanceof VariableSpec.BoolSource bs) {
            return switch (bs.source()) {
                case "processing" -> this.processing;
                case "powered" -> this.powered;
                case "overclocked" -> this.overclocked;
                case "has_fuel" -> this.hasFuel;
                case "always" -> true;
                case "never" -> false;
                default -> false;
            };
        }
        if (spec instanceof VariableSpec.BoolExpr be) {
            try {
                return PolyFormula.compile(be.expr()).evaluateBool(this.toPolyContext());
            } catch (Throwable ignored) {
                return false;
            }
        }
        if (spec instanceof VariableSpec.NumExpr ne) {
            try {
                return PolyFormula.compile(ne.expr()).evaluateNum(this.toPolyContext()) != 0.0;
            } catch (Throwable ignored) {
                return false;
            }
        }
        if (spec instanceof VariableSpec.ItemSlot is) {
            return this.container != null && this.container.getItem(is.slot()) != null;
        }
        if (spec instanceof VariableSpec.TankVar tv) {
            return this.resolveTankValue(tv).asBool();
        }
        if (spec instanceof VariableSpec.Formula f) {
            try {
                return PolyFormula.compile(f.expr()).evaluateBool(this.toPolyContext());
            } catch (Throwable ignored) {
                return false;
            }
        }
        return false;
    }

    private PolyValue resolveTankValue(VariableSpec.TankVar tv) {
        PolyClass cls = this.toPolyContext().getClass(tv.isGas() ? "GasTanks" : "FluidTanks");
        if (cls instanceof FluidTanksClass ftc) {
            return ftc.forTank(tv.tankName()).get(tv.property());
        }
        if (cls instanceof GasTanksClass gtc) {
            return gtc.forTank(tv.tankName()).get(tv.property());
        }
        return PolyValue.NULL;
    }

    public double rpm() {
        return this.rpm;
    }

    public double overclock() {
        return this.overclock;
    }

    public double efficiency() {
        return this.efficiency;
    }

    public double progress() {
        return this.progress;
    }

    public double maxProgress() {
        return this.maxProgress;
    }

    public double tier() {
        return this.tier;
    }

    public boolean processing() {
        return this.processing;
    }

    public boolean powered() {
        return this.powered;
    }

    public boolean overclocked() {
        return this.overclocked;
    }

    public boolean hasFuel() {
        return this.hasFuel;
    }

    public Inventory container() {
        return this.container;
    }

    public Map<String, Integer> upgradesByType() {
        return this.upgradesByType;
    }

    public Map<String, double[]> fluidTankData() {
        return this.fluidTankData;
    }

    public Map<String, double[]> gasTankData() {
        return this.gasTankData;
    }
}

