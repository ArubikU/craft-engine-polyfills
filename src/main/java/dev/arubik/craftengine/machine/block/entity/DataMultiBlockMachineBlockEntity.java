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
public class DataMultiBlockMachineBlockEntity extends MultiBlockMachineBlockEntity
        implements dev.arubik.craftengine.machine.render.ModelRendersDriven {

    private final MachineDefinition definition;
    private dev.arubik.craftengine.machine.render.RendererManager rendererManager;
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
        if (!definition.renderers().isEmpty()) {
            this.rendererManager = new dev.arubik.craftengine.machine.render.RendererManager(
                    definition.renderers(), definition.variables());
        }
    }

    @Override
    public dev.arubik.craftengine.machine.render.RendererManager rendererManager() {
        return rendererManager;
    }

    @Override
    public void tick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        super.tick(level, pos, state);
        if (rendererManager != null && level instanceof net.minecraft.server.level.ServerLevel sl) {
            try {
                // Snapshot fluid/gas tanks
                java.util.Map<String, double[]> fluidTankData = new java.util.LinkedHashMap<>();
                for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
                    try {
                        var stored = tank.getFluid(sl, pos);
                        fluidTankData.put(tank.getName(), new double[]{ stored.getAmount(), tank.getCapacity() });
                    } catch (Throwable ignored) {}
                }
                java.util.Map<String, double[]> gasTankData = new java.util.LinkedHashMap<>();
                for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
                    try {
                        var stored = tank.getGas(sl, pos);
                        gasTankData.put(tank.getName(), new double[]{ stored.getAmount(), tank.getCapacity() });
                    } catch (Throwable ignored) {}
                }
                // Snapshot upgrades
                java.util.Map<String, Integer> upgradesByType = new java.util.LinkedHashMap<>();
                if (!upgradeDefs.isEmpty()) {
                    for (int upSlot : definition.upgrades().slots()) {
                        net.minecraft.world.item.ItemStack nmsItem = getItem(upSlot);
                        if (nmsItem.isEmpty()) continue;
                        try {
                            var ce = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byItemStack(
                                org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nmsItem));
                            String uid = ce != null
                                ? ce.id().namespace() + ":" + ce.id().value()
                                : net.minecraft.core.registries.BuiltInRegistries.ITEM
                                    .getKey(nmsItem.getItem()).toString();
                            upgradesByType.merge(uid, 1, Integer::sum);
                        } catch (Throwable ignored) {}
                    }
                }
                int redstonePower = 0;
                try { redstonePower = sl.getBestNeighborSignal(pos); } catch (Throwable ignored) {}

                dev.arubik.craftengine.machine.render.variable.MachineRenderContext ctx =
                        new dev.arubik.craftengine.machine.render.variable.MachineRenderContext(
                                0, 0, 0,
                                progress, maxProgress, 0,
                                isProcessing(), redstonePower > 0, false, burnTime > 0,
                                null, upgradesByType, fluidTankData, gasTankData, redstonePower);

                net.minecraft.core.Direction facing = getFacing(level);
                String facingName = facing != null ? facing.getName().toLowerCase() : "north";
                float yaw = facing == null ? 0f : switch (facing) {
                    case SOUTH -> 0f; case WEST -> 90f; case NORTH -> 180f; case EAST -> 270f; default -> 0f;
                };
                // Augment with Machine position and facing for player_facing() etc.
                // MultiBlock context: rel pos always 0,0,0 for master, part count from schema
                int partCount = 0;
                try { partCount = getSchema().getParts().size(); } catch (Throwable ignored) {}
                dev.arubik.craftengine.machine.render.formula.PolyContext machineCtx =
                    dev.arubik.craftengine.machine.render.formula.PolyContext.builder()
                        .copyFrom(ctx.toPolyContext())
                        .machinePos(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, facingName, yaw,
                            ((org.bukkit.craftbukkit.CraftWorld) sl.getWorld()))
                        .contraption(sl)
                        .world(sl)
                        .multiBlock(0, 0, 0, true, partCount, true)
                        .build();
                ctx = ctx.augmented(machineCtx);

                rendererManager.tick(ctx, sl, pos.getX(), pos.getY(), pos.getZ(), yaw);
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        if (rendererManager != null) {
            rendererManager.close();
            rendererManager = null;
        }
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

    private java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> modsOf(int slot) {
        net.momirealms.craftengine.core.util.Key id = upgradeItemId(getItem(slot));
        return id == null ? null : upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        if (upgradeDefs.isEmpty()) {
            super.recomputeUpgrades();
            return;
        }
        int count = definition.upgrades().size();
        int[] slots = definition.upgrades().slots();
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> all = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            var m = modsOf(slots[i]);
            if (m != null) all.addAll(m);
        }
        int extra = (int) Math.round(dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(all)
                .getOrDefault(dev.arubik.craftengine.machine.attribute.MachineAttributes.EXTRA_SLOTS, 0.0));
        int unlocked = Math.max(definition.upgrades().baseUnlocked(),
                Math.min(count, definition.upgrades().baseUnlocked() + extra));
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> active = new java.util.ArrayList<>();
        for (int i = 0; i < unlocked; i++) {
            var m = modsOf(slots[i]);
            if (m != null) active.addAll(m);
        }
        var attrs = dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(active);
        double gen = clamp(attrs.getOrDefault(dev.arubik.craftengine.machine.attribute.MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        double overLimit = clamp(attrs.getOrDefault(dev.arubik.craftengine.machine.attribute.MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.upgradeModifiers = new dev.arubik.craftengine.machine.upgrade.UpgradeModifiers(1.0, 1.0 - gen, 0.0);
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
        // Install definition buttons (upgrade page nav, overclock, etc.)
        for (MachineDefinition.ButtonSpec spec : definition.buttons()) {
            final MachineDefinition.ButtonSpec s = spec;
            try {
                net.momirealms.craftengine.core.util.Key iconKey = s.icon() != null
                        ? net.momirealms.craftengine.core.util.Key.of(s.icon())
                        : net.momirealms.craftengine.core.util.Key.of("cml", "gui_empty");
                layout.addButton(s.slot(),
                    (machine, tick) -> dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                            iconKey, org.bukkit.Material.PAPER,
                            net.kyori.adventure.text.Component.text(s.name() == null ? "" : s.name())),
                    (machine, player) -> {
                        String action = s.action();
                        if (action != null && action.startsWith("open_page:")) {
                            try {
                                int page = Integer.parseInt(action.substring(10));
                                if (machine instanceof DataMultiBlockMachineBlockEntity mb)
                                    mb.turnPage(page - mb.currentPage() - 1);
                            } catch (Throwable ignored) {}
                        }
                    });
            } catch (Throwable ignored) {}
        }
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
