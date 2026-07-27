package dev.arubik.craftengine.machine.block.entity;

import java.util.List;

import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.kyori.adventure.text.Component;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * The block entity behind every data-defined machine that must be assembled.
 *
 * <p>
 * The single-block {@link DataMachineBlockEntity} and this one need the same
 * recipe behaviour but cannot share a superclass, so the shared half lives in
 * {@link DataMachineSupport} and both delegate to it.
 */
public class DataMultiBlockMachineBlockEntity extends MultiBlockMachineBlockEntity {

    private final MachineDefinition definition;
    private MachineMenuConfig menuConfig = MachineMenuConfig.parse(key -> null);
    private List<MachineBar> bars = List.of();
    private java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs =
            java.util.Map.of();

    // ---- paged storage: the menu shows one page, the rest lives in persistence ----

    private final net.minecraft.world.item.ItemStack[][] pages;
    private int currentPage = 0;
    private boolean pagesLoaded = false;

    public DataMultiBlockMachineBlockEntity(BlockEntity blockEntity, MultiBlockSchema schema,
            MachineDefinition definition) {
        super(definition.menuSize(), blockEntity, schema);
        this.definition = definition;

        var paging = definition.paging();
        if (paging.isPaged()) {
            pages = new net.minecraft.world.item.ItemStack[paging.pages()][paging.slots()];
            for (var page : pages)
                java.util.Arrays.fill(page, net.minecraft.world.item.ItemStack.EMPTY);
        } else {
            pages = null;
        }

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

    public void setMenuConfig(MachineMenuConfig config) {
        this.menuConfig = config != null ? config : MachineMenuConfig.parse(key -> null);
    }

    public void setBars(List<MachineBar> bars) {
        this.bars = bars != null ? bars : List.of();
    }

    public void setUpgradeDefs(
            java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> defs) {
        this.upgradeDefs = defs == null ? java.util.Map.of() : defs;
    }

    // --------------------------------------------------------------- paging

    private static dev.arubik.craftengine.util.TypedKey<net.minecraft.world.item.ItemStack[]> pageKey(int page) {
        return dev.arubik.craftengine.util.TypedKey.of("craftengine", "paged_storage_" + page,
                dev.arubik.craftengine.util.CustomDataType.ITEM_ARRAY_CODEC_TYPE);
    }

    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_CURRENT_PAGE =
            dev.arubik.craftengine.util.TypedKey.of("craftengine", "paged_storage_current",
                    dev.arubik.craftengine.util.NbtType.INTEGER);

    /**
     * Loads the pages on first access.
     *
     * <p>
     * Not in the constructor: CraftEngine builds a controller before its block entity
     * has a world, and persistence needs one.
     */
    private void ensurePagesLoaded() {
        if (pages == null || pagesLoaded || blockEntity().world() == null)
            return;
        for (int p = 0; p < pages.length; p++) {
            var stored = get(pageKey(p));
            if (stored != null)
                for (int i = 0; i < pages[p].length && i < stored.length; i++)
                    pages[p][i] = stored[i] == null ? net.minecraft.world.item.ItemStack.EMPTY : stored[i];
        }
        Integer saved = get(KEY_CURRENT_PAGE);
        currentPage = saved == null ? 0 : Math.max(0, Math.min(pages.length - 1, saved));
        pagesLoaded = true;
        showPage();
    }

    /** Copies the visible container back into the page it belongs to. */
    private void stashPage() {
        if (pages == null)
            return;
        for (int i = 0; i < pages[currentPage].length; i++)
            pages[currentPage][i] = getItem(i);
    }

    /** Copies the current page into the visible container. */
    private void showPage() {
        if (pages == null)
            return;
        for (int i = 0; i < pages[currentPage].length; i++)
            setItem(i, pages[currentPage][i]);
    }

    private void savePages() {
        if (pages == null)
            return;
        for (int p = 0; p < pages.length; p++)
            set(pageKey(p), pages[p]);
        set(KEY_CURRENT_PAGE, currentPage);
        setChanged();
    }

    /** Moves by {@code delta} pages, clamped, persisting what was on screen. */
    public void turnPage(int delta) {
        if (pages == null)
            return;
        int target = Math.max(0, Math.min(pages.length - 1, currentPage + delta));
        if (target == currentPage)
            return;
        stashPage();
        currentPage = target;
        showPage();
        savePages();
        if (getMenu() != null)
            getMenu().syncFromMachine();
    }

    public int currentPage() {
        return currentPage;
    }

    public int pageCount() {
        return pages == null ? 1 : pages.length;
    }

    // ------------------------------------------------------------- recipes

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

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return DataMachineSupport.matchingRecipe(this, definition, getMachineId());
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return DataMachineSupport.canFitOutput(this, definition, output);
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        DataMachineSupport.consumeInputs(this, definition, recipe);
    }

    // ---------------------------------------------------------------- menu

    @Override
    public MachineLayout getLayout() {
        ensurePagesLoaded();
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
        if (definition.upgrades().isInline())
            for (int slot : definition.upgrades().slots())
                layout.addSlot(slot, MenuSlotType.UPGRADE);

        MachineBars.install(layout, DataMachineSupport.resolveBars(definition, bars));

        // Paged storage: the page's slots are ordinary input slots, plus the navigation.
        var paging = definition.paging();
        if (paging.isPaged()) {
            for (int i = 0; i < paging.slots(); i++)
                layout.addSlot(i, MenuSlotType.INPUT);
            if (paging.prevSlot() >= 0)
                layout.addButton(paging.prevSlot(),
                        (m, t) -> dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                                Key.of("cml", "gui_empty"), org.bukkit.Material.ARROW,
                                Component.text("§aPrevious Page")),
                        (m, p) -> ((DataMultiBlockMachineBlockEntity) m).turnPage(-1));
            if (paging.nextSlot() >= 0)
                layout.addButton(paging.nextSlot(),
                        (m, t) -> dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                                Key.of("cml", "gui_empty"), org.bukkit.Material.ARROW,
                                Component.text("§aNext Page")),
                        (m, p) -> ((DataMultiBlockMachineBlockEntity) m).turnPage(1));
            if (paging.indicator() >= 0)
                layout.setDynamicProvider(paging.indicator(), (m, t) -> {
                    var self = (DataMultiBlockMachineBlockEntity) m;
                    var paper = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PAPER);
                    paper.editMeta(meta -> meta.displayName(
                            dev.arubik.craftengine.machine.menu.MenuText.noI(Component.text(
                                    "§ePage " + (self.currentPage() + 1) + "/" + self.pageCount()))));
                    return paper;
                });
        }

        int infoSlot = definition.infoSlot() >= 0 ? definition.infoSlot() : menuConfig.infoSlot;
        if (infoSlot >= 0)
            layout.setDynamicProvider(infoSlot,
                    (machine, tick) -> dev.arubik.craftengine.machine.menu.RecipeInfoIcon.build(machine,
                            getMachineId(), machine.getUpgradeModifiers().speedMultiplier(), 0.0));
        return layout;
    }

    /** Which tank a gauge reads, from the definition's {@code source}. */
    private String barSource(String barId) {
        for (MachineDefinition.BarRef ref : definition.bars())
            if (ref.bar().value().equals(barId))
                return ref.source();
        return barId;
    }

    @Override
    public double[] barStat(String id) {
        String source = barSource(id);
        if (source.startsWith("fluid")) {
            var tank = tankByName(source, true);
            return tank == null ? new double[] { 0, 0 }
                    : new double[] { ((FluidTank) tank).getFluid(getNMSLevel(), getMachinePos()).getAmount(),
                            ((FluidTank) tank).getCapacity() };
        }
        if (source.startsWith("gas")) {
            var tank = tankByName(source, false);
            return tank == null ? new double[] { 0, 0 }
                    : new double[] { ((GasTank) tank).getGas(getNMSLevel(), getMachinePos()).getAmount(),
                            ((GasTank) tank).getCapacity() };
        }
        if (source.equals("fuel"))
            return new double[] { burnTime, Math.max(1, maxBurnTime) };
        if (source.equals("progress"))
            return new double[] { getProgress(), Math.max(1, getMaxProgress()) };
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        String source = barSource(id);
        if (source.startsWith("fluid")) {
            var tank = (FluidTank) tankByName(source, true);
            if (tank == null)
                return "";
            var stored = tank.getFluid(getNMSLevel(), getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(java.util.Locale.ROOT);
        }
        if (source.startsWith("gas")) {
            var tank = (GasTank) tankByName(source, false);
            if (tank == null)
                return "";
            var stored = tank.getGas(getNMSLevel(), getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(java.util.Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    /** The tank a {@code fluid:<name>} / {@code gas:<name>} source names, or the first one. */
    private Object tankByName(String source, boolean fluid) {
        String name = source.contains(":") ? source.substring(source.indexOf(':') + 1) : "";
        if (fluid) {
            for (var t : fluidTanks)
                if (t.getName().equalsIgnoreCase(name))
                    return t;
            return fluidTanks.isEmpty() ? null : fluidTanks.get(0);
        }
        for (var t : gasTanks)
            if (t.getName().equalsIgnoreCase(name))
                return t;
        return gasTanks.isEmpty() ? null : gasTanks.get(0);
    }
}
