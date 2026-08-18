/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.machine;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.momirealms.craftengine.core.util.Key;

public final class MachineDefinition {
    public static final Registry<MachineDefinition> REGISTRY = Registries.create("machine");
    private final Key id;
    private final String recipeType;
    private final String title;
    private final int menuSize;
    private final int[] inputSlots;
    private final int[] outputSlots;
    private final int[] fuelSlots;
    private final UpgradeSpec upgrades;
    private final int infoSlot;
    private final InfoSpec info;
    private final PagingSpec paging;
    private final List<TankSpec> fluidTanks;
    private final List<TankSpec> gasTanks;
    private final boolean fuelRequired;
    private boolean continuousFuel;
    private final IOConfiguration io;
    private final List<ButtonSpec> buttons;
    private final PowerSpec power;
    private final List<BarRef> bars;
    private final Map<String, VariableSpec> variables;
    private final List<RendererSpec> renderers;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final String actionScript;
    private final int actionInterval;
    private boolean openUi = true;
    private boolean noProcessing = false;
    private Set<String> rpmInputFacesRaw = Set.of();
    private Map<String, Set<String>> rpmInputBlockFilter = Map.of();
    private Map<String, Set<String>> rpmOutputBlockFilter = Map.of();
    private Set<String> rpmOutputFacesRaw = Set.of();
    private boolean rpmOutputDeclared = false;
    private float rpmRatio = 1.0f;
    private boolean isSail = false;
    private float sailRpmBonus = 1.0f;
    private boolean rpmLargeCog = false;
    private Set<String> rpmOutputInvertedRaw = Set.of();
    private String interactScript = null;
    private String attackScript = null;

    public MachineDefinition(Key id, String recipeType, String title, int menuSize, int[] inputSlots, int[] outputSlots, int[] fuelSlots, UpgradeSpec upgrades, int infoSlot, List<TankSpec> fluidTanks, List<TankSpec> gasTanks, boolean fuelRequired, IOConfiguration io, List<ButtonSpec> buttons, PowerSpec power, List<BarRef> bars, InfoSpec info, PagingSpec paging, Map<String, VariableSpec> variables, List<RendererSpec> renderers, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, String actionScript, int actionInterval) {
        this.id = id;
        this.recipeType = recipeType;
        this.title = title;
        this.menuSize = menuSize;
        this.inputSlots = (int[])inputSlots.clone();
        this.outputSlots = (int[])outputSlots.clone();
        this.fuelSlots = (int[])fuelSlots.clone();
        this.upgrades = upgrades;
        this.infoSlot = infoSlot;
        this.fluidTanks = List.copyOf(fluidTanks);
        this.gasTanks = List.copyOf(gasTanks);
        this.fuelRequired = fuelRequired;
        this.io = io;
        this.buttons = List.copyOf(buttons);
        this.power = power == null ? PowerSpec.none() : power;
        this.bars = bars == null ? List.of() : List.copyOf(bars);
        this.info = info == null ? InfoSpec.none() : info;
        this.paging = paging == null ? PagingSpec.none() : paging;
        this.variables = variables == null ? Map.of() : Map.copyOf(variables);
        this.renderers = renderers == null ? List.of() : List.copyOf(renderers);
        this.upgradeDefs = upgradeDefs == null ? Map.of() : Map.copyOf(upgradeDefs);
        this.actionScript = actionScript;
        this.actionInterval = actionInterval <= 0 ? 20 : actionInterval;
    }

    public Key id() {
        return this.id;
    }

    public String recipeType() {
        return this.recipeType;
    }

    public String title() {
        return this.title;
    }

    public int menuSize() {
        return this.menuSize;
    }

    public int[] inputSlots() {
        return (int[])this.inputSlots.clone();
    }

    public int[] outputSlots() {
        return (int[])this.outputSlots.clone();
    }

    public int[] fuelSlots() {
        return (int[])this.fuelSlots.clone();
    }

    public UpgradeSpec upgrades() {
        return this.upgrades;
    }

    public PagingSpec paging() {
        return this.paging;
    }

    public InfoSpec info() {
        return this.info;
    }

    public int infoSlot() {
        return this.infoSlot;
    }

    public List<TankSpec> fluidTanks() {
        return this.fluidTanks;
    }

    public List<TankSpec> gasTanks() {
        return this.gasTanks;
    }

    public boolean fuelRequired() {
        return this.fuelRequired;
    }

    public boolean continuousFuel() {
        return this.continuousFuel;
    }

    public MachineDefinition withContinuousFuel(boolean v) {
        this.continuousFuel = v;
        return this;
    }

    public IOConfiguration io() {
        return this.io;
    }

    public List<ButtonSpec> buttons() {
        return this.buttons;
    }

    public PowerSpec power() {
        return this.power;
    }

    public List<BarRef> bars() {
        return this.bars;
    }

    public Map<String, VariableSpec> variables() {
        return this.variables;
    }

    public List<RendererSpec> renderers() {
        return this.renderers;
    }

    public Map<Key, List<MachineAttributes.Mod>> upgradeDefs() {
        return this.upgradeDefs;
    }

    public String actionScript() {
        return this.actionScript;
    }

    public int actionInterval() {
        return this.actionInterval;
    }

    public boolean openUi() {
        return this.openUi;
    }

    public void setOpenUi(boolean openUi) {
        this.openUi = openUi;
    }

    public boolean noProcessing() {
        return this.noProcessing;
    }

    public void setNoProcessing(boolean noProcessing) {
        this.noProcessing = noProcessing;
    }

    public Set<String> rpmInputFacesRaw() {
        return this.rpmInputFacesRaw;
    }

    public void setRpmInputFacesRaw(Set<String> f) {
        this.rpmInputFacesRaw = Set.copyOf(f);
    }

    public Map<String, Set<String>> rpmInputBlockFilter() {
        return this.rpmInputBlockFilter;
    }

    public void setRpmInputBlockFilter(Map<String, Set<String>> m) {
        this.rpmInputBlockFilter = Map.copyOf(m);
    }

    public Map<String, Set<String>> rpmOutputBlockFilter() {
        return this.rpmOutputBlockFilter;
    }

    public void setRpmOutputBlockFilter(Map<String, Set<String>> m) {
        this.rpmOutputBlockFilter = Map.copyOf(m);
    }

    public Set<String> rpmOutputFacesRaw() {
        return this.rpmOutputFacesRaw;
    }

    public void setRpmOutputFacesRaw(Set<String> f) {
        this.rpmOutputFacesRaw = Set.copyOf(f);
    }

    public boolean rpmOutputDeclared() {
        return this.rpmOutputDeclared;
    }

    public void setRpmOutputDeclared(boolean v) {
        this.rpmOutputDeclared = v;
    }

    public float rpmRatio() {
        return this.rpmRatio;
    }

    public void setRpmRatio(float r) {
        this.rpmRatio = r;
    }

    public boolean isSail() {
        return this.isSail;
    }

    public void setSail(boolean v) {
        this.isSail = v;
    }

    public float sailRpmBonus() {
        return this.sailRpmBonus;
    }

    public void setSailRpmBonus(float v) {
        this.sailRpmBonus = v;
    }

    public boolean rpmLargeCog() {
        return this.rpmLargeCog;
    }

    public void setRpmLargeCog(boolean v) {
        this.rpmLargeCog = v;
    }

    public Set<String> rpmOutputInvertedRaw() {
        return this.rpmOutputInvertedRaw;
    }

    public void setRpmOutputInvertedRaw(Set<String> f) {
        this.rpmOutputInvertedRaw = Set.copyOf(f);
    }

    public String interactScript() {
        return this.interactScript;
    }

    public void setInteractScript(String s) {
        this.interactScript = s;
    }

    public String attackScript() {
        return this.attackScript;
    }

    public void setAttackScript(String s) {
        this.attackScript = s;
    }

    public static MachineDefinition byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(58) >= 0 ? Key.of((String)trimmed) : Key.of((String)"polyfills", (String)trimmed));
    }

    public String toString() {
        return String.valueOf(this.id) + "(recipes=" + this.recipeType + ")";
    }

    public record UpgradeSpec(int count, int baseUnlocked, int[] explicitSlots) {
        public UpgradeSpec(int count, int baseUnlocked) {
            this(count, baseUnlocked, null);
        }

        public boolean isInline() {
            return this.explicitSlots != null;
        }

        public int[] slots() {
            if (this.explicitSlots != null) {
                return (int[])this.explicitSlots.clone();
            }
            int[] out = new int[this.count];
            for (int i = 0; i < this.count; ++i) {
                out[i] = i;
            }
            return out;
        }

        public int size() {
            return this.explicitSlots != null ? this.explicitSlots.length : this.count;
        }
    }

    public record PowerSpec(boolean consumesStress, double suExponent, int stressGraceTicks, float generatesRpm, int generatesSu, float baseOverclock) {
        public static PowerSpec none() {
            return new PowerSpec(false, 1.25, 20, 0.0f, 0, 2.0f);
        }
    }

    public record InfoSpec(int slot, String type, String source) {
        public static InfoSpec none() {
            return new InfoSpec(-1, "recipe", "");
        }

        public boolean isTank() {
            return "tank".equalsIgnoreCase(this.type);
        }
    }

    public record PagingSpec(int pages, int slots, int prevSlot, int nextSlot, int indicator) {
        public static PagingSpec none() {
            return new PagingSpec(1, 0, -1, -1, -1);
        }

        public boolean isPaged() {
            return this.pages > 1;
        }
    }

    public record TankSpec(String name, int capacity, Key filter) {
    }

    public record BarRef(Key bar, int[] slots, String source) {
    }

    public record ButtonSpec(int slot, String icon, String action, String name, List<String> lore, String lockedIcon, String lockedWhen) {
    }
}

