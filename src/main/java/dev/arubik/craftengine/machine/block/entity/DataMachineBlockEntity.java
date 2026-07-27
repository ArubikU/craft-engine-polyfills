package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import net.kyori.adventure.text.Component;

/**
 * The one block entity behind every data-defined machine.
 *
 * <p>
 * Implements the five abstract hooks of {@link AbstractMachineBlockEntity} by
 * reading a {@link MachineDefinition} rather than by hardcoding them, which is
 * what previously forced a class per machine. The logic here is the same
 * item-in/recipe/item-out loop the existing Java machines already share; the
 * bespoke ones (pumps scanning for a vein, the fan's process families) keep
 * their own classes.
 */
public class DataMachineBlockEntity extends AbstractMachineBlockEntity {

    private final MachineDefinition definition;

    public DataMachineBlockEntity(net.momirealms.craftengine.core.block.entity.BlockEntity blockEntity,
            MachineDefinition definition) {
        super(blockEntity, definition.menuSize());
        this.definition = definition;

        for (MachineDefinition.TankSpec spec : definition.fluidTanks()) {
            FluidType filter = spec.filter() == null ? null : FluidType.REGISTRY.get(spec.filter());
            addFluidTank(filter == null ? new FluidTank(spec.name(), spec.capacity())
                    : new FluidTank(spec.name(), spec.capacity(), filter));
        }
        for (MachineDefinition.TankSpec spec : definition.gasTanks()) {
            GasType filter = spec.filter() == null ? null : GasType.REGISTRY.get(spec.filter());
            addGasTank(filter == null ? new GasTank(spec.name(), spec.capacity())
                    : new GasTank(spec.name(), spec.capacity(), filter));
        }
        if (definition.io() != null)
            setIOConfiguration(definition.io());
    }

    public MachineDefinition definition() {
        return definition;
    }

    @Override
    protected String getMachineId() {
        return definition.recipeType();
    }

    @Override
    public int[] getInputSlots() {
        return definition.inputSlots();
    }

    @Override
    public int[] getOutputSlots() {
        return definition.outputSlots();
    }

    @Override
    public int[] getFuelSlots() {
        return definition.fuelSlots();
    }

    @Override
    public int[] getUpgradeSlots() {
        return definition.upgrades().slots();
    }

    // ------------------------------------------------------------- recipes

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(getMachineId()))
            if (matchingInputSlot(recipe) >= 0)
                return recipe;
        return null;
    }

    /**
     * The input slot holding the item this recipe wants, or -1.
     *
     * <p>
     * Recipes with no item input (a pure fluid or gas conversion) match on slot 0
     * of the input list so the caller still gets a non-negative answer.
     */
    protected int matchingInputSlot(AbstractProcessingRecipe recipe) {
        boolean wantsItem = false;
        for (RecipeInput input : recipe.getInputs())
            if (isItemInput(input))
                wantsItem = true;
        if (!wantsItem)
            return definition.inputSlots().length > 0 ? definition.inputSlots()[0] : 0;

        for (int slot : definition.inputSlots()) {
            ItemStack stack = getItem(slot);
            if (stack == null || stack.isEmpty())
                continue;
            boolean all = true;
            for (RecipeInput input : recipe.getInputs()) {
                if (!isItemInput(input))
                    continue;
                if (!input.matches(stack) || stack.getCount() < input.getAmount()) {
                    all = false;
                    break;
                }
            }
            if (all)
                return slot;
        }
        return -1;
    }

    private static boolean isItemInput(RecipeInput input) {
        return input instanceof ItemInput || input instanceof CraftEngineItemInput || input instanceof TagInput;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (!(output instanceof ItemOutput itemOutput))
            return true; // fluid/gas/xp outputs check their own tanks when dispensed
        ItemStack produced = (ItemStack) itemOutput.getOutput();
        for (int slot : definition.outputSlots()) {
            ItemStack current = getItem(slot);
            if (current == null || current.isEmpty())
                return true;
            if (ItemStack.isSameItem(current, produced)
                    && current.getCount() + produced.getCount() <= current.getMaxStackSize())
                return true;
        }
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int slot = matchingInputSlot(recipe);
        if (slot < 0)
            return;
        for (RecipeInput input : recipe.getInputs())
            if (isItemInput(input))
                removeItem(slot, input.getAmount());
    }

    // ---------------------------------------------------------------- menu

    /**
     * Menu visuals, supplied by the block config rather than the machine JSON.
     *
     * <p>
     * Buttons, bars/gauges and the GUI image were already config-driven through
     * {@link MachineMenuConfig} / {@link MachineBars} / {@code GuiTitles}, so a data
     * machine reuses that machinery instead of growing a second, parallel way to
     * describe the same things. The JSON definition owns what the machine *is*
     * (recipes, tanks, slot roles, IO); the block config owns what it *looks like*.
     */
    private MachineMenuConfig menuConfig = MachineMenuConfig.parse(key -> null);
    private List<MachineBar> bars = List.of();

    public void setMenuConfig(MachineMenuConfig config) {
        this.menuConfig = config != null ? config : MachineMenuConfig.parse(key -> null);
    }

    public void setBars(List<MachineBar> bars) {
        this.bars = bars != null ? bars : List.of();
    }

    /** Which page the open menu is showing: 0 main, 1 upgrades, 2 overclock. */
    private int page = 0;
    private float overclock = 0f;
    private MachineMenu active;

    /** Unlocked upgrade slots, recomputed from EXTRA_SLOTS like the Java machines do. */
    private int curUnlocked = -1;
    private double curOverclockLimit = 0.0;
    private double curFuelEff = 0.0;
    private double curGeneration = 0.0;

    /**
     * item id -> attribute modifiers, from the machine definition's {@code upgrades}.
     *
     * <p>
     * The modern attribute system the newer machines use, as opposed to the legacy
     * global {@code UpgradeRegistry}. Empty means this machine has no attribute
     * upgrades and the inherited legacy path applies.
     */
    private java.util.Map<net.momirealms.craftengine.core.util.Key,
            java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs =
                    java.util.Map.of();

    public void setUpgradeDefs(java.util.Map<net.momirealms.craftengine.core.util.Key,
            java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> defs) {
        this.upgradeDefs = defs == null ? java.util.Map.of() : defs;
    }

    private java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> modsOf(int slot) {
        net.momirealms.craftengine.core.util.Key id = upgradeItemId(getItem(slot));
        return id == null ? null : upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /**
     * Folds attribute upgrades, mirroring what the Java machines do: a first pass
     * over every installed module decides how many slots are unlocked, a second pass
     * over only the unlocked ones decides the effects.
     */
    @Override
    protected void recomputeUpgrades() {
        if (upgradeDefs.isEmpty()) {
            super.recomputeUpgrades(); // legacy UpgradeRegistry path
            return;
        }
        int count = definition.upgrades().size();
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> all = new java.util.ArrayList<>();
        int[] slots = definition.upgrades().slots();
        for (int i = 0; i < count; i++) {
            var m = modsOf(slots[i]);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(all)
                .getOrDefault(dev.arubik.craftengine.machine.attribute.MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(definition.upgrades().baseUnlocked(),
                Math.min(count, definition.upgrades().baseUnlocked() + extra));

        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> active = new java.util.ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            var m = modsOf(slots[i]);
            if (m != null)
                active.addAll(m);
        }
        var attrs = dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(active);
        this.curGeneration = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);

        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
        // Speed is (1 + overclock) alone; GENERATION is an output bonus, not a speed one.
        this.upgradeModifiers = new dev.arubik.craftengine.machine.upgrade.UpgradeModifiers(
                1.0 + overclock, 1.0, 0.0);
    }

    private int unlockedSlots() {
        if (curUnlocked < 0)
            curUnlocked = definition.upgrades().baseUnlocked();
        return Math.min(definition.upgrades().size(), Math.max(0, curUnlocked));
    }

    @Override
    public MachineMenu getMenu() {
        if (active == null) {
            active = new MachineMenu(this, getLayout());
            active.syncFromMachine();
        }
        return active;
    }

    @Override
    public void openMenu(net.minecraft.world.entity.player.Player player) {
        openPage((org.bukkit.entity.Player) player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        this.active = new MachineMenu(this, getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
    }

    @Override
    public MachineLayout getLayout() {
        // An inline upgrade grid has no separate page — the slots live on the main one.
        if (definition.upgrades().isInline())
            return buildMainLayout();
        return switch (page) {
            case 1 -> buildUpgradeLayout();
            case 2 -> buildOverclockLayout();
            default -> buildMainLayout();
        };
    }

    /**
     * The upgrade page: the reserved container indices 0..count-1 on their own
     * screen, with the still-locked ones shown as a lock icon.
     */
    private MachineLayout buildUpgradeLayout() {
        int count = definition.upgrades().size();
        int unlocked = unlockedSlots();
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "upgrade");
        if (title != null)
            l.setTitleComponent(title);
        for (int i = 0; i < count; i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> dev.arubik.craftengine.machine.menu.MenuText.lockedIcon(
                        dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.locked",
                                net.kyori.adventure.text.format.NamedTextColor.RED),
                        dev.arubik.craftengine.machine.menu.MenuText.tr("polyfill.ui.locked_desc",
                                net.kyori.adventure.text.format.NamedTextColor.GRAY)));
            }
        }
        l.addButton(17, (m, t) -> dev.arubik.craftengine.machine.menu.MenuText.backIcon(),
                (m, p) -> ((DataMachineBlockEntity) m).openPage(p, 0));
        return l;
    }

    /** The overclock page, built by the shared {@code OverclockMenu} component. */
    private MachineLayout buildOverclockLayout() {
        return dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), net.kyori.adventure.text.format.NamedTextColor.RED,
                () -> this.overclock,
                () -> (float) this.curOverclockLimit,
                this::bumpOverclock,
                p -> openPage(p, 0));
    }

    /**
     * Carried fraction of the GENERATION attribute.
     *
     * <p>
     * Generation is a deterministic output bonus, not a speed one: each craft adds
     * {@code curGeneration} here and every whole 1.0 accumulated yields one extra
     * full output set, with the remainder carried. +35% generation therefore gives a
     * bonus set roughly every third craft rather than a 35% chance each time.
     */
    private double genBuffer = 0.0;

    @Override
    protected void process(Level level, AbstractProcessingRecipe recipe) {
        consumeInputs(level, recipe);
        int extra = 0;
        if (curGeneration > 0.0) {
            genBuffer += curGeneration;
            while (genBuffer >= 1.0) {
                genBuffer -= 1.0;
                extra++;
            }
        }
        for (int set = 0; set < 1 + extra; set++)
            for (RecipeOutput output : recipe.getOutputs())
                output.dispense(level, this);
        if (extra > 0)
            setChanged();
    }

    /** rpm the matched recipe demands after fuel efficiency and overclock. */
    @Override
    public int effectiveRpm(AbstractProcessingRecipe recipe) {
        if (recipe == null)
            return 0;
        return Math.round((float) (recipe.getMinRpm() * (1.0 + overclock) * (1.0 - curFuelEff)));
    }

    /**
     * SU drawn after overclock. The cost rises slightly super-linearly —
     * {@code (1+oc)^1.25} — so pushing speed past +100% costs disproportionately more
     * stress without being punishing. Generation and efficiency do not change SU.
     */
    @Override
    public int effectiveSu(AbstractProcessingRecipe recipe) {
        if (recipe == null)
            return 0;
        double factor = Math.pow(Math.max(0.0, 1.0 + overclock), definition.power().suExponent());
        return (int) Math.round(recipe.getSuCost() * factor);
    }

    /**
     * Also true while the player has the overclock slider above zero, not only during
     * a fuel-driven overclock burst.
     */
    @Override
    public boolean isOverclocked() {
        return super.isOverclocked() || overclock > 0f;
    }

    public void bumpOverclock(boolean up, org.bukkit.event.inventory.ClickType click) {
        float delta = dev.arubik.craftengine.machine.menu.OverclockMenu.step(click);
        this.overclock += up ? delta : -delta;
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
        setChanged();
    }

    private MachineLayout buildMainLayout() {
        MachineLayout layout = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST,
                definition.menuSize(), definition.title());
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        if (title != null)
            layout.setTitleComponent(title);

        for (int slot : definition.inputSlots())
            layout.addSlot(slot, MenuSlotType.INPUT);
        for (int slot : definition.outputSlots())
            layout.addSlot(slot, MenuSlotType.OUTPUT);
        for (int slot : definition.fuelSlots())
            layout.addSlot(slot, MenuSlotType.FUEL);
        // Reserved upgrade slots are shown on their own page, not here; inline ones
        // belong on the main screen.
        if (definition.upgrades().isInline())
            for (int slot : definition.upgrades().slots())
                layout.addSlot(slot, MenuSlotType.UPGRADE);

        // Buttons come from the machine definition when it declares any (so the
        // upgrades / overclock entry points travel with the machine), otherwise from
        // the block config, which is how the pre-existing machines declare them.
        if (!definition.buttons().isEmpty())
            for (MachineDefinition.ButtonSpec spec : definition.buttons())
                installButton(layout, toButton(spec));
        else
            for (MachineMenuConfig.Button button : menuConfig.buttons)
                installButton(layout, button);
        MachineBars.install(layout, bars);

        // Recipe readout icon, same shared component the Java machines use.
        int infoSlot = definition.infoSlot() >= 0 ? definition.infoSlot() : menuConfig.infoSlot;
        if (infoSlot >= 0)
            layout.setDynamicProvider(infoSlot,
                    (machine, tick) -> dev.arubik.craftengine.machine.menu.RecipeInfoIcon.build(machine,
                            getMachineId(), machine.getUpgradeModifiers().speedMultiplier(),
                            this.curGeneration));
        return layout;
    }

    private static MachineMenuConfig.Button toButton(MachineDefinition.ButtonSpec spec) {
        return new MachineMenuConfig.Button(spec.slot(), spec.icon(),
                MachineMenuConfig.Action.parse(spec.action()), spec.name(), spec.lore(),
                spec.lockedIcon(), MachineMenuConfig.LockedWhen.parse(spec.lockedWhen()));
    }

    private void installButton(MachineLayout layout, MachineMenuConfig.Button button) {
        layout.addButton(button.slot,
                (machine, tick) -> dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        parseKey(button.icon), org.bukkit.Material.PAPER,
                        net.kyori.adventure.text.Component.text(button.name == null ? "" : button.name)),
                (machine, player) -> {
                    switch (button.action.kind) {
                        case DEPLETE_FLUID -> {
                            for (var t : machine.fluidTanks)
                                if (button.action.targets(t.getName()))
                                    t.extract(machine.getNMSLevel(), machine.getMachinePos(),
                                            t.getCapacity(), null);
                        }
                        case DEPLETE_GAS -> {
                            for (var t : machine.gasTanks)
                                if (button.action.targets(t.getName()))
                                    t.extract(machine.getNMSLevel(), machine.getMachinePos(),
                                            t.getCapacity(), null);
                        }
                        case OPEN_PAGE -> {
                            if (machine instanceof DataMachineBlockEntity self)
                                self.openPage(player, button.action.page);
                        }
                        case NONE -> {
                        }
                    }
                });
    }

    private static net.momirealms.craftengine.core.util.Key parseKey(String spec) {
        if (spec == null)
            return net.momirealms.craftengine.core.util.Key.of("cml", "gui_empty");
        int i = spec.indexOf(':');
        return i < 0 ? net.momirealms.craftengine.core.util.Key.of("cml", spec)
                : net.momirealms.craftengine.core.util.Key.of(spec.substring(0, i), spec.substring(i + 1));
    }
}
