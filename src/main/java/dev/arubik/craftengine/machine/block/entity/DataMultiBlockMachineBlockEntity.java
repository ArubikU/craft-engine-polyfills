/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.DataMachineSupport;
import dev.arubik.craftengine.machine.menu.GuiTitles;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.RecipeInfoIcon;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.MachineFuelRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyScript;
import dev.arubik.craftengine.machine.render.formula.PolyScriptRegistry;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class DataMultiBlockMachineBlockEntity
extends MultiBlockMachineBlockEntity
implements ModelRendersDriven {
    private final MachineDefinition definition;
    private RendererManager rendererManager;
    private MachineMenuConfig menuConfig = MachineMenuConfig.parse(key -> null);
    private List<MachineBar> bars = List.of();
    private Map<Key, List<MachineAttributes.Mod>> upgradeDefs = Map.of();
    private int machinePageIndex = 0;
    private MachineMenu activeMenu;
    private int curUnlocked = -1;
    private float overclock = 0.0f;
    private double curOverclockLimit = 0.0;
    private final net.minecraft.world.item.ItemStack[][] pages;
    private int currentPage = 0;
    private boolean pagesLoaded = false;
    private static final TypedKey<Integer> KEY_CURRENT_PAGE = TypedKey.of("craftengine", "paged_storage_current", NbtType.INTEGER);
    private static final AbstractProcessingRecipe CONTINUOUS_FUEL_RECIPE = new AbstractProcessingRecipe(List.of(), List.of(), 1, true, false, List.of());

    public DataMultiBlockMachineBlockEntity(BlockEntity blockEntity, MultiBlockSchema schema, MachineDefinition definition) {
        super(definition.menuSize(), blockEntity, schema);
        this.definition = definition;
        MachineDefinition.PagingSpec paging = definition.paging();
        if (paging.isPaged()) {
            this.pages = new net.minecraft.world.item.ItemStack[paging.pages()][paging.slots()];
            for (net.minecraft.world.item.ItemStack[] page : this.pages) {
                Arrays.fill(page, net.minecraft.world.item.ItemStack.EMPTY);
            }
        } else {
            this.pages = null;
        }
        for (MachineDefinition.TankSpec spec : definition.fluidTanks()) {
            FluidType filter = spec.filter() == null ? null : FluidType.REGISTRY.get(spec.filter());
            this.addFluidTank(filter == null ? new FluidTank(spec.name(), spec.capacity()) : new FluidTank(spec.name(), spec.capacity(), filter));
        }
        for (MachineDefinition.TankSpec spec : definition.gasTanks()) {
            GasType filter = spec.filter() == null ? null : GasType.REGISTRY.get(spec.filter());
            this.addGasTank(filter == null ? new GasTank(spec.name(), spec.capacity()) : new GasTank(spec.name(), spec.capacity(), filter));
        }
        if (definition.io() != null) {
            this.setIOConfiguration(definition.io());
        }
        if (!definition.renderers().isEmpty()) {
            this.rendererManager = new RendererManager(definition.renderers(), definition.variables());
        }
    }

    @Override
    public RendererManager rendererManager() {
        return this.rendererManager;
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        super.tick(level, pos, state);
        if (this.rendererManager != null && level instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)level;
            try {
                float f;
                LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
                for (Object tank : this.fluidTanks) {
                    try {
                        FluidStack stored = ((FluidTank)tank).getFluid((Level)sl, pos);
                        fluidTankData.put(((FluidTank)tank).getName(), new double[]{stored.getAmount(), ((FluidTank)tank).getCapacity()});
                    }
                    catch (Throwable stored) {}
                }
                LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
                for (Object tank : this.gasTanks) {
                    try {
                        GasStack stored = ((GasTank)tank).getGas((Level)sl, pos);
                        gasTankData.put(((GasTank)tank).getName(), new double[]{stored.getAmount(), ((GasTank)tank).getCapacity()});
                    }
                    catch (Throwable stored) {}
                }
                LinkedHashMap<String, Integer> upgradesByType = new LinkedHashMap<String, Integer>();
                if (!this.upgradeDefs.isEmpty()) {
                    for (Object upSlot : this.definition.upgrades().slots()) {
                        net.minecraft.world.item.ItemStack nmsItem = this.getItem((int)upSlot);
                        if (nmsItem.isEmpty()) continue;
                        try {
                            BukkitItemDefinition ce = CraftEngineItems.byItemStack((ItemStack)CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nmsItem));
                            String uid = ce != null ? ce.id().namespace() + ":" + ce.id().value() : BuiltInRegistries.ITEM.getKey(nmsItem.getItem()).toString();
                            upgradesByType.merge(uid, 1, Integer::sum);
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                }
                int redstonePower = 0;
                try {
                    redstonePower = sl.getBestNeighborSignal(pos);
                }
                catch (Throwable stored) {
                    // empty catch block
                }
                MachineRenderContext ctx = new MachineRenderContext(0.0, 0.0, 0.0, this.progress, this.maxProgress, 0.0, this.isProcessing(), redstonePower > 0, false, this.burnTime > 0, null, upgradesByType, fluidTankData, gasTankData, redstonePower);
                Direction facing = this.getFacing(level);
                if (facing == null) {
                    f = 0.0f;
                } else {
                    switch (facing) {
                        case SOUTH: {
                            f = 0.0f;
                            break;
                        }
                        case WEST: {
                            f = 90.0f;
                            break;
                        }
                        case NORTH: {
                            f = 180.0f;
                            break;
                        }
                        case EAST: {
                            f = 270.0f;
                            break;
                        }
                        default: {
                            f = 0.0f;
                        }
                    }
                }
                float yaw = f;
                PolyContext machineCtx = this.buildEvalContext();
                if (machineCtx != null) {
                    ctx = ctx.augmented(machineCtx);
                }
                this.rendererManager.tick(ctx, sl, pos.getX(), pos.getY(), pos.getZ(), yaw);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public PolyContext buildEvalContext() {
        try {
            float f;
            String facingName;
            Level level = this.getNMSLevel();
            BlockPos pos = this.getMachinePos();
            if (!(level instanceof ServerLevel)) {
                return null;
            }
            ServerLevel sl = (ServerLevel)level;
            LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
            for (Object tank : this.fluidTanks) {
                try {
                    FluidStack stored = ((FluidTank)tank).getFluid((Level)sl, pos);
                    fluidTankData.put(((FluidTank)tank).getName(), new double[]{stored.getAmount(), ((FluidTank)tank).getCapacity()});
                }
                catch (Throwable stored) {}
            }
            LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
            for (Object tank : this.gasTanks) {
                try {
                    GasStack stored = ((GasTank)tank).getGas((Level)sl, pos);
                    gasTankData.put(((GasTank)tank).getName(), new double[]{stored.getAmount(), ((GasTank)tank).getCapacity()});
                }
                catch (Throwable stored) {}
            }
            LinkedHashMap<String, Integer> upgradesByType = new LinkedHashMap<String, Integer>();
            if (!this.upgradeDefs.isEmpty()) {
                for (Object upSlot : this.definition.upgrades().slots()) {
                    net.minecraft.world.item.ItemStack nmsItem = this.getItem((int)upSlot);
                    if (nmsItem.isEmpty()) continue;
                    try {
                        BukkitItemDefinition ce = CraftEngineItems.byItemStack((ItemStack)CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nmsItem));
                        String uid = ce != null ? ce.id().namespace() + ":" + ce.id().value() : BuiltInRegistries.ITEM.getKey(nmsItem.getItem()).toString();
                        upgradesByType.merge(uid, 1, Integer::sum);
                    }
                    catch (Throwable ce) {
                        // empty catch block
                    }
                }
            }
            int redstonePower = 0;
            try {
                redstonePower = sl.getBestNeighborSignal(pos);
            }
            catch (Throwable stored) {
                // empty catch block
            }
            MachineRenderContext mrc = new MachineRenderContext(0.0, 0.0, 0.0, this.progress, this.maxProgress, 0.0, this.isProcessing(), redstonePower > 0, false, this.burnTime > 0, null, upgradesByType, fluidTankData, gasTankData, redstonePower);
            Direction facing = this.getFacing(level);
            String string = facingName = facing != null ? facing.getName().toLowerCase() : "north";
            if (facing == null) {
                f = 0.0f;
            } else {
                switch (facing) {
                    case SOUTH: {
                        f = 0.0f;
                        break;
                    }
                    case WEST: {
                        f = 90.0f;
                        break;
                    }
                    case NORTH: {
                        f = 180.0f;
                        break;
                    }
                    case EAST: {
                        f = 270.0f;
                        break;
                    }
                    default: {
                        f = 0.0f;
                    }
                }
            }
            float yaw = f;
            int partCount = 0;
            try {
                partCount = this.getSchema().getParts().size();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return PolyContext.builder().copyFrom(mrc.toPolyContext()).machinePos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, facingName, yaw, (World)sl.getWorld(), this).contraption(sl).world(sl).multiBlock(0, 0, 0, true, partCount, true).build();
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    public MachineDefinition definition() {
        return this.definition;
    }

    public void setMenuConfig(MachineMenuConfig config) {
        this.menuConfig = config != null ? config : MachineMenuConfig.parse(key -> null);
    }

    public void setBars(List<MachineBar> bars) {
        this.bars = bars != null ? bars : List.of();
    }

    public void setUpgradeDefs(Map<Key, List<MachineAttributes.Mod>> defs) {
        this.upgradeDefs = defs == null ? Map.of() : defs;
    }

    private List<MachineAttributes.Mod> modsOf(int slot) {
        Key id = this.upgradeItemId(this.getItem(slot));
        return id == null ? null : this.upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        if (this.upgradeDefs.isEmpty()) {
            super.recomputeUpgrades();
            return;
        }
        int count = this.definition.upgrades().size();
        int[] slots = this.definition.upgrades().slots();
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < count; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            all.addAll(m);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(this.definition.upgrades().baseUnlocked(), Math.min(count, this.definition.upgrades().baseUnlocked() + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            active.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        double gen = DataMultiBlockMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = DataMultiBlockMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.overclock = (float)DataMultiBlockMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeModifiers = new UpgradeModifiers(1.0 + (double)this.overclock, 1.0 - gen, 0.0);
    }

    private int unlockedSlots() {
        if (this.curUnlocked < 0) {
            this.curUnlocked = this.definition.upgrades().baseUnlocked();
        }
        return Math.min(this.definition.upgrades().size(), Math.max(0, this.curUnlocked));
    }

    private static TypedKey<net.minecraft.world.item.ItemStack[]> pageKey(int page) {
        return TypedKey.of("craftengine", "paged_storage_" + page, CustomDataType.ITEM_ARRAY_CODEC_TYPE);
    }

    private void ensurePagesLoaded() {
        if (this.pages == null || this.pagesLoaded || this.blockEntity().world() == null) {
            return;
        }
        for (int p = 0; p < this.pages.length; ++p) {
            net.minecraft.world.item.ItemStack[] stored = this.get(DataMultiBlockMachineBlockEntity.pageKey(p));
            if (stored == null) continue;
            for (int i = 0; i < this.pages[p].length && i < stored.length; ++i) {
                this.pages[p][i] = stored[i] == null ? net.minecraft.world.item.ItemStack.EMPTY : stored[i];
            }
        }
        Integer saved = this.get(KEY_CURRENT_PAGE);
        this.currentPage = saved == null ? 0 : Math.max(0, Math.min(this.pages.length - 1, saved));
        this.pagesLoaded = true;
        this.showPage();
    }

    private void stashPage() {
        if (this.pages == null) {
            return;
        }
        for (int i = 0; i < this.pages[this.currentPage].length; ++i) {
            this.pages[this.currentPage][i] = this.getItem(i);
        }
    }

    private void showPage() {
        if (this.pages == null) {
            return;
        }
        for (int i = 0; i < this.pages[this.currentPage].length; ++i) {
            this.setItem(i, this.pages[this.currentPage][i]);
        }
    }

    private void savePages() {
        if (this.pages == null) {
            return;
        }
        for (int p = 0; p < this.pages.length; ++p) {
            this.set(DataMultiBlockMachineBlockEntity.pageKey(p), this.pages[p]);
        }
        this.set(KEY_CURRENT_PAGE, this.currentPage);
        this.setChanged();
    }

    public void turnPage(int delta) {
        if (this.pages == null) {
            return;
        }
        int target = Math.max(0, Math.min(this.pages.length - 1, this.currentPage + delta));
        if (target == this.currentPage) {
            return;
        }
        this.stashPage();
        this.currentPage = target;
        this.showPage();
        this.savePages();
        if (this.getMenu() != null) {
            this.getMenu().syncFromMachine();
        }
    }

    public int currentPage() {
        return this.currentPage;
    }

    public int pageCount() {
        return this.pages == null ? 1 : this.pages.length;
    }

    @Override
    protected String getMachineId() {
        return this.definition.recipeType();
    }

    @Override
    protected boolean requiresFuel() {
        return this.definition.fuelRequired();
    }

    @Override
    protected boolean hasFuel(Level level) {
        for (int slot : this.definition.fuelSlots()) {
            MachineFuelRecipe recipe;
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            if (stack == null || stack.isEmpty() || (recipe = RecipeManager.getFuel(this.getMachineId(), stack)) == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void consumeFuel(Level level) {
        for (int slot : this.definition.fuelSlots()) {
            MachineFuelRecipe recipe;
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            if (stack == null || stack.isEmpty() || (recipe = RecipeManager.getFuel(this.getMachineId(), stack)) == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = (int)Math.round((double)recipe.getBurnTime() / this.upgradeModifiers.fuelMultiplier());
            boolean consumedInPlace = this.placeReplacement(level, recipe, slot);
            if (!consumedInPlace) {
                this.removeItem(slot, recipe.getInput().getAmount());
            }
            this.burnTime = burn;
            this.maxBurnTime = burn;
            this.setChanged();
            return;
        }
    }

    @Override
    public int[] getInputSlots() {
        return this.definition.inputSlots();
    }

    @Override
    public int[] getOutputSlots() {
        return this.definition.outputSlots();
    }

    @Override
    public int[] getFuelSlots() {
        return this.definition.fuelSlots();
    }

    @Override
    public int[] getUpgradeSlots() {
        return this.definition.upgrades().slots();
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        if (this.definition.continuousFuel()) {
            return CONTINUOUS_FUEL_RECIPE;
        }
        return DataMachineSupport.matchingRecipe(this, this.definition, this.getMachineId());
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return DataMachineSupport.canFitOutput(this, this.definition, output);
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        DataMachineSupport.consumeInputs(this, this.definition, recipe);
    }

    @Override
    public MachineMenu getMenu() {
        if (this.activeMenu == null) {
            this.activeMenu = new MachineMenu(this, this.getLayout());
            this.activeMenu.syncFromMachine();
            this.menu = this.activeMenu;
        }
        return this.activeMenu;
    }

    @Override
    public void openMenu(Player player) {
        this.openPage((org.bukkit.entity.Player)player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int pageIndex) {
        this.machinePageIndex = pageIndex;
        this.activeMenu = new MachineMenu(this, this.getLayout());
        this.activeMenu.syncFromMachine();
        this.menu = this.activeMenu;
        this.activeMenu.open(player);
    }

    public void bumpOverclock(boolean up, ClickType click) {
        float delta = OverclockMenu.step(click);
        this.overclock += up ? delta : -delta;
        this.overclock = (float)DataMultiBlockMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.setChanged();
    }

    @Override
    public MachineLayout getLayout() {
        this.ensurePagesLoaded();
        if (!this.definition.upgrades().isInline()) {
            if (this.machinePageIndex == 1) {
                return this.buildUpgradeLayout();
            }
            if (this.machinePageIndex == 2) {
                return this.buildOverclockLayout();
            }
        }
        return this.buildMainLayout();
    }

    private MachineLayout buildUpgradeLayout() {
        int count = this.definition.upgrades().size();
        int unlocked = this.unlockedSlots();
        MachineLayout l = new MachineLayout(InventoryType.CHEST, 18, "Upgrades");
        Component title = GuiTitles.title(this.getMachineId(), "upgrade");
        if (title != null) {
            l.setTitleComponent(title);
        }
        for (int i = 0; i < count; ++i) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
                continue;
            }
            l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(MenuText.tr("polyfill.ui.locked", NamedTextColor.RED), MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(), (m, p) -> ((DataMultiBlockMachineBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0));
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        return OverclockMenu.build(this.getMachineId(), NamedTextColor.RED, () -> this.overclock, () -> (float)this.curOverclockLimit, this::bumpOverclock, p -> this.openPage((org.bukkit.entity.Player)p, 0));
    }

    private MachineLayout buildMainLayout() {
        int infoSlot;
        MachineLayout layout = new MachineLayout(InventoryType.CHEST, this.definition.menuSize(), this.definition.title());
        Component title = GuiTitles.title(this.getMachineId(), "main");
        if (title != null) {
            layout.setTitleComponent(title);
        }
        for (int slot : this.definition.inputSlots()) {
            layout.addSlot(slot, MenuSlotType.INPUT);
        }
        for (int slot : this.definition.outputSlots()) {
            layout.addSlot(slot, MenuSlotType.OUTPUT);
        }
        for (int slot : this.definition.fuelSlots()) {
            layout.addSlot(slot, MenuSlotType.FUEL);
        }
        if (this.definition.upgrades().isInline()) {
            for (int slot : this.definition.upgrades().slots()) {
                layout.addSlot(slot, MenuSlotType.UPGRADE);
            }
        }
        MachineBars.install(layout, DataMachineSupport.resolveBars(this.definition, this.bars));
        MachineDefinition.PagingSpec paging = this.definition.paging();
        if (paging.isPaged()) {
            for (int i = 0; i < paging.slots(); ++i) {
                layout.addSlot(i, MenuSlotType.INPUT);
            }
            if (paging.prevSlot() >= 0) {
                layout.addButton(paging.prevSlot(), (m, t) -> MenuText.iconItem(Key.of((String)"cml", (String)"gui_empty"), Material.ARROW, (Component)Component.text((String)"\u00a7aPrevious Page"), new Component[0]), (m, p) -> ((DataMultiBlockMachineBlockEntity)m).turnPage(-1));
            }
            if (paging.nextSlot() >= 0) {
                layout.addButton(paging.nextSlot(), (m, t) -> MenuText.iconItem(Key.of((String)"cml", (String)"gui_empty"), Material.ARROW, (Component)Component.text((String)"\u00a7aNext Page"), new Component[0]), (m, p) -> ((DataMultiBlockMachineBlockEntity)m).turnPage(1));
            }
            if (paging.indicator() >= 0) {
                layout.setDynamicProvider(paging.indicator(), (m, t) -> {
                    DataMultiBlockMachineBlockEntity self = (DataMultiBlockMachineBlockEntity)m;
                    ItemStack paper = new ItemStack(Material.PAPER);
                    paper.editMeta(meta -> meta.displayName(MenuText.noI((Component)Component.text((String)("\u00a7ePage " + (self.currentPage() + 1) + "/" + self.pageCount())))));
                    return paper;
                });
            }
        }
        int n = infoSlot = this.definition.infoSlot() >= 0 ? this.definition.infoSlot() : this.menuConfig.infoSlot;
        if (infoSlot >= 0) {
            layout.setDynamicProvider(infoSlot, (machine, tick) -> RecipeInfoIcon.build(machine, this.getMachineId(), machine.getUpgradeModifiers().speedMultiplier(), 0.0));
        }
        Iterator<MachineDefinition.ButtonSpec> iterator = this.definition.buttons().iterator();
        while (iterator.hasNext()) {
            MachineDefinition.ButtonSpec spec;
            MachineDefinition.ButtonSpec s = spec = iterator.next();
            try {
                Key iconKey = DataMultiBlockMachineBlockEntity.parseKey(s.icon());
                Key lockedKey = DataMultiBlockMachineBlockEntity.parseKey(s.lockedIcon());
                MachineMenuConfig.LockedWhen lw = MachineMenuConfig.LockedWhen.parse(s.lockedWhen());
                layout.addButton(s.slot(), (machine, tick) -> {
                    double d;
                    PolyContext evalCtx2 = machine.buildEvalContext();
                    if (machine instanceof DataMultiBlockMachineBlockEntity) {
                        DataMultiBlockMachineBlockEntity mb2 = (DataMultiBlockMachineBlockEntity)machine;
                        d = mb2.curOverclockLimit;
                    } else {
                        d = 0.0;
                    }
                    double ocLimit2 = d;
                    boolean locked = lw.isLocked(evalCtx2, ocLimit2);
                    Key key = locked ? lockedKey : iconKey;
                    return MenuText.iconItem(key, Material.PAPER, (Component)Component.text((String)(s.name() == null ? "" : s.name())), new Component[0]);
                }, (machine, player) -> {
                    String scriptName;
                    PolyScript script;
                    if (!(machine instanceof DataMultiBlockMachineBlockEntity)) {
                        return;
                    }
                    DataMultiBlockMachineBlockEntity self = (DataMultiBlockMachineBlockEntity)machine;
                    PolyContext evalCtx = self.buildEvalContext();
                    if (lw.isLocked(evalCtx, self.curOverclockLimit)) {
                        return;
                    }
                    String action = s.action();
                    if (action != null && action.startsWith("open_page:")) {
                        try {
                            int pageIndex = Integer.parseInt(action.substring(10));
                            self.openPage((org.bukkit.entity.Player)player, pageIndex);
                        }
                        catch (Throwable pageIndex) {}
                    } else if (action != null && (action.startsWith("script:") || action.startsWith("run:")) && (script = PolyScriptRegistry.get(scriptName = action.startsWith("script:") ? action.substring(7) : action.substring(4))) != null && evalCtx != null) {
                        script.evaluate(evalCtx);
                    }
                });
            }
            catch (Throwable throwable) {}
        }
        return layout;
    }

    private static Key parseKey(String spec) {
        if (spec == null) {
            return Key.of((String)"cml", (String)"gui_empty");
        }
        int i = spec.indexOf(58);
        return i < 0 ? Key.of((String)"cml", (String)spec) : Key.of((String)spec.substring(0, i), (String)spec.substring(i + 1));
    }

    private String barSource(String barId) {
        for (MachineDefinition.BarRef ref : this.definition.bars()) {
            if (!ref.bar().value().equals(barId)) continue;
            return ref.source();
        }
        return barId;
    }

    @Override
    public double[] barStat(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid")) {
            double[] dArray;
            Object tank = this.tankByName(source, true);
            if (tank == null) {
                double[] dArray2 = new double[2];
                dArray2[0] = 0.0;
                dArray = dArray2;
                dArray2[1] = 0.0;
            } else {
                double[] dArray3 = new double[2];
                dArray3[0] = ((FluidTank)tank).getFluid(this.getNMSLevel(), this.getMachinePos()).getAmount();
                dArray = dArray3;
                dArray3[1] = ((FluidTank)tank).getCapacity();
            }
            return dArray;
        }
        if (source.startsWith("gas")) {
            double[] dArray;
            Object tank = this.tankByName(source, false);
            if (tank == null) {
                double[] dArray4 = new double[2];
                dArray4[0] = 0.0;
                dArray = dArray4;
                dArray4[1] = 0.0;
            } else {
                double[] dArray5 = new double[2];
                dArray5[0] = ((GasTank)tank).getGas(this.getNMSLevel(), this.getMachinePos()).getAmount();
                dArray = dArray5;
                dArray5[1] = ((GasTank)tank).getCapacity();
            }
            return dArray;
        }
        if (source.equals("fuel")) {
            return new double[]{this.burnTime, Math.max(1, this.maxBurnTime)};
        }
        if (source.equals("progress")) {
            return new double[]{this.getProgress(), Math.max(1, this.getMaxProgress())};
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid")) {
            FluidTank tank = (FluidTank)this.tankByName(source, true);
            if (tank == null) {
                return "";
            }
            FluidStack stored = tank.getFluid(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        if (source.startsWith("gas")) {
            GasTank tank = (GasTank)this.tankByName(source, false);
            if (tank == null) {
                return "";
            }
            GasStack stored = tank.getGas(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    private Object tankByName(String source, boolean fluid) {
        String name;
        String string = name = source.contains(":") ? source.substring(source.indexOf(58) + 1) : "";
        if (fluid) {
            for (FluidTank t : this.fluidTanks) {
                if (!t.getName().equalsIgnoreCase(name)) continue;
                return t;
            }
            return this.fluidTanks.isEmpty() ? null : this.fluidTanks.get(0);
        }
        for (GasTank t : this.gasTanks) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return t;
        }
        return this.gasTanks.isEmpty() ? null : this.gasTanks.get(0);
    }
}

