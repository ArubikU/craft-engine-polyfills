package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.GasInput;
import dev.arubik.craftengine.machine.recipe.GasOutput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * [TASK-01] Vapor Furnace Mk1 — fuel-driven cooking machine ({@code polyfills:vapor_furnace_mk1}).
 *
 * <p>Single block. Runs purely on combustible FUEL (like a vanilla furnace, no rpm/no gas input).
 * Its primary job is to cook WATER (fluid input, any side) into VAPOR/STEAM (gas output, TOP face)
 * via the recipe system; it can also run smelting-style ITEM recipes whose results drop out the
 * BOTTOM face. All recipes share the {@code vapor_furnace_mk1} machine id and the same fuel.</p>
 *
 * <p>Engineer-style UI (like the crusher): a water tank bar + a steam tank bar, a fuel slot, item
 * input/output slots, a progress indicator, an info icon and an Upgrades sub-page. Upgrades use the
 * same attribute system as the crusher / gas motor (generation = faster, fuel_efficiency = burns
 * less fuel, extra_slots = more upgrade slots).</p>
 */
public class VaporFurnaceMk1BlockEntity extends AbstractMachineBlockEntity {

    private static final int MENU_SIZE = 54; // 6 rows; container slots 0..8 reserved for upgrades
    public static final int UPGRADE_SLOTS = 9; // container indices 0..8
    private static final int BASE_UNLOCKED = 3;

    public static final int SLOT_FUEL = 11;
    public static final int[] INPUT_SLOTS = { 20, 29 };
    public static final int[] OUTPUT_SLOTS = { 24, 33 };
    private static final int SLOT_PROGRESS = 22;
    private static final int SLOT_INFO = 13;
    private static final int SLOT_UPGRADES_BTN = 15;

    public static final int WATER_CAP = 8000;
    public static final int STEAM_CAP = 8000;

    private static final org.bukkit.inventory.ItemStack FILLER = buildFiller();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;

    // Derived from installed upgrades each process tick.
    private double curGeneration = 0;
    private double curFuelEff = 0;
    private int curUnlocked = BASE_UNLOCKED;

    private int page = 0; // 0 main, 1 upgrades
    private MachineMenu active;

    public VaporFurnaceMk1BlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>(), new ArrayList<>());
    }

    public VaporFurnaceMk1BlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars) {
        super(blockEntity, MENU_SIZE);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;

        addFluidTank(new FluidTank("water", WATER_CAP));
        addGasTank(new GasTank("steam", STEAM_CAP));

        // IO (LOCAL dirs, rotated to world via `facing`):
        //   WATER fluid IN  -> any horizontal side (water can enter from anywhere)
        //   VAPOR gas  OUT  -> UP (top face only)
        //   ITEM  IN        -> NORTH (back)
        //   ITEM  OUT       -> DOWN (bottom face, so a hopper pulls results)
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOType.FLUID, Direction.NORTH);
        cfg.addInput(IOType.FLUID, Direction.SOUTH);
        cfg.addInput(IOType.FLUID, Direction.EAST);
        cfg.addInput(IOType.FLUID, Direction.WEST);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.NORTH);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.SOUTH);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.EAST);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.WEST);

        cfg.addOutput(IOType.GAS, Direction.UP);
        cfg.withOutputSlot(IOType.GAS, 0, Direction.UP);

        cfg.addInput(IOType.ITEM, Direction.NORTH);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, INPUT_SLOTS);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, SLOT_FUEL);
        cfg.addOutput(IOType.ITEM, Direction.DOWN);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, OUTPUT_SLOTS);
        setIOConfiguration(cfg);
    }

    private static org.bukkit.inventory.ItemStack buildFiller() {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = s.getItemMeta();
        meta.displayName(Component.text(" "));
        s.setItemMeta(meta);
        return s;
    }

    // ---------------- upgrades (attribute system, like the crusher) ----------------

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[UPGRADE_SLOTS];
        for (int i = 0; i < UPGRADE_SLOTS; i++)
            s[i] = i;
        return s;
    }

    private Key itemId(int slot) {
        ItemStack nms = getItem(slot);
        if (nms == null || nms.isEmpty())
            return null;
        org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        Key custom = net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(b);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = b.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }

    private List<Mod> modsOf(int slot) {
        Key id = itemId(slot);
        return id == null ? null : upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        // Pass 1: unlocked slot count from EXTRA_SLOTS across ALL installed upgrades.
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(BASE_UNLOCKED, Math.min(UPGRADE_SLOTS, BASE_UNLOCKED + extra));

        // Pass 2: effect attributes only from upgrades in unlocked slots.
        List<Mod> activeMods = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                activeMods.addAll(m);
        }
        var attrs = MachineAttributes.compute(activeMods);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curFuelEff = clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);

        // generation -> faster processing; fuel_efficiency -> stretches each fuel item's burn time.
        double speed = 1.0 + curGeneration;
        double fuelMul = 1.0 - curFuelEff; // <1 = burns slower (more efficient)
        this.upgradeModifiers = new dev.arubik.craftengine.machine.upgrade.UpgradeModifiers(
                speed, Math.max(0.05, fuelMul), 0.0);
    }

    // ---------------- recipe loop ----------------

    private static boolean isItem(RecipeInput in) {
        return in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput;
    }

    private int waterAmount() {
        try {
            return fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos()).getAmount();
        } catch (Throwable t) {
            return 0;
        }
    }

    private int steamAmount() {
        try {
            return gasTanks.get(0).getGas(getNMSLevel(), getMachinePos()).getAmount();
        } catch (Throwable t) {
            return 0;
        }
    }

    private int steamSpace() {
        return STEAM_CAP - steamAmount();
    }

    /** All item inputs map to distinct input slots; fluid inputs fit the tank; gas outputs fit. */
    private boolean matchesAll(AbstractProcessingRecipe recipe) {
        boolean[] used = new boolean[INPUT_SLOTS.length];
        boolean anyInput = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                int slot = -1;
                for (int i = 0; i < INPUT_SLOTS.length; i++) {
                    if (!used[i] && in.matches(getItem(INPUT_SLOTS[i]))) {
                        slot = i;
                        break;
                    }
                }
                if (slot < 0)
                    return false;
                used[slot] = true;
                anyInput = true;
            } else if (in instanceof FluidInput fi) {
                if (waterAmount() < fi.getAmount())
                    return false;
                anyInput = true;
            } else if (in instanceof GasInput gi) {
                if (steamAmount() < gi.getAmount())
                    return false;
                anyInput = true;
            }
        }
        return anyInput;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (matchesAll(r))
                return r;
        }
        return null;
    }

    @Override
    protected boolean requiresFuel() {
        return true; // fuel-driven, like a furnace
    }

    @Override
    protected String getMachineId() {
        return "vapor_furnace_mk1";
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput io) {
            ItemStack out = (ItemStack) io.getOutput();
            for (int s : OUTPUT_SLOTS) {
                ItemStack cur = getItem(s);
                if (cur == null || cur.isEmpty())
                    return true;
                if (ItemStack.isSameItem(cur, out)
                        && cur.getCount() + out.getCount() <= cur.getMaxStackSize())
                    return true;
            }
            return false;
        }
        if (output instanceof GasOutput go) {
            dev.arubik.craftengine.gas.GasStack gs = (dev.arubik.craftengine.gas.GasStack) go.getOutput();
            return steamSpace() >= gs.getAmount();
        }
        return true;
    }

    @Override
    protected void process(Level level, AbstractProcessingRecipe recipe) {
        net.momirealms.craftengine.core.plugin.CraftEngine.instance().logger().info(
                "[FURN] process outs=" + recipe.getOutputs().size() + " steamBefore=" + steamAmount()
                        + " waterBefore=" + waterAmount());
        super.process(level, recipe);
        net.momirealms.craftengine.core.plugin.CraftEngine.instance().logger().info(
                "[FURN] after steam=" + steamAmount() + " out0=" + getItem(OUTPUT_SLOTS[0])
                        + " out1=" + getItem(OUTPUT_SLOTS[1]));
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        boolean[] used = new boolean[INPUT_SLOTS.length];
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                for (int i = 0; i < INPUT_SLOTS.length; i++) {
                    if (!used[i] && in.matches(getItem(INPUT_SLOTS[i]))) {
                        removeItem(INPUT_SLOTS[i], in.getAmount());
                        used[i] = true;
                        break;
                    }
                }
            } else if (in instanceof FluidInput fi) {
                fluidTanks.get(0).extract(level, getMachinePos(), fi.getAmount(), null);
            } else if (in instanceof GasInput gi) {
                gasTanks.get(0).extract(level, getMachinePos(), gi.getAmount(), null);
            }
        }
    }

    @Override
    public double[] barStat(String id) {
        switch (id) {
            case "water":
                return new double[] { waterAmount(), WATER_CAP };
            case "steam":
                return new double[] { steamAmount(), STEAM_CAP };
            case "fuel":
                return new double[] { burnTime, Math.max(1, maxBurnTime) }; // remaining burn of current fuel
            case "progress":
                return new double[] { progress, Math.max(1, maxProgress) }; // current cook progress
            default:
                return super.barStat(id);
        }
    }

    @Override
    public String barSubtype(String id) {
        switch (id) {
            case "water":
                return waterAmount() > 0 ? "water" : "";
            case "steam":
                return steamAmount() > 0 ? "steam" : "";
            default:
                return super.barSubtype(id);
        }
    }

    // ---------------- multi-page menu ----------------

    @Override
    public MachineLayout getLayout() {
        return page == 1 ? buildUpgradeLayout() : buildMainLayout();
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

    private MachineLayout buildMainLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, MENU_SIZE,
                "Vapor Furnace Mk1");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.vapor_furnace_title", NamedTextColor.GOLD)));

        l.addSlot(SLOT_FUEL, MenuSlotType.FUEL);
        for (int s : INPUT_SLOTS)
            l.addSlot(s, MenuSlotType.INPUT);
        for (int s : OUTPUT_SLOTS)
            l.addSlot(s, MenuSlotType.OUTPUT);

        // Progress is shown by the config-driven "progress" bar (single slot) — no separate item here.

        // Info: recipes + items/min.
        l.setDynamicProvider(SLOT_INFO, (m, t) -> ((VaporFurnaceMk1BlockEntity) m).infoIcon());

        // Upgrades button.
        l.addButton(SLOT_UPGRADES_BTN,
                (m, t) -> MenuText.icon(Material.ANVIL, MenuText.tr("polyfill.ui.upgrades", NamedTextColor.GOLD),
                        MenuText.tr("polyfill.ui.upgrades_desc", NamedTextColor.GRAY)),
                (m, p) -> ((VaporFurnaceMk1BlockEntity) m).openPage(p, 1));

        // Config-driven side bars (water / steam).
        MachineBars.install(l, bars);

        // Deplete buttons under each bar (water column -> 45, steam column -> 53).
        l.addButton(45,
                (m, t) -> MenuText.icon(Material.BUCKET, MenuText.tr("polyfill.ui.deplete", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.fluid", NamedTextColor.GRAY)),
                (m, p) -> ((VaporFurnaceMk1BlockEntity) m).depleteFluid());
        l.addButton(53,
                (m, t) -> MenuText.icon(Material.BUCKET, MenuText.tr("polyfill.ui.deplete", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.gas", NamedTextColor.GRAY)),
                (m, p) -> ((VaporFurnaceMk1BlockEntity) m).depleteGas());

        fillRest(l);
        return l;
    }

    /** Empty the water tank (deplete button). */
    public void depleteFluid() {
        try {
            fluidTanks.get(0).extract(getNMSLevel(), getMachinePos(), WATER_CAP, null);
        } catch (Throwable ignored) {
        }
    }

    /** Empty the steam tank (deplete button). */
    public void depleteGas() {
        try {
            gasTanks.get(0).extract(getNMSLevel(), getMachinePos(), STEAM_CAP, null);
        } catch (Throwable ignored) {
        }
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        List<Component> lore = new ArrayList<>();
        int water = waterAmount(), steam = steamAmount();
        NamedTextColor fc = water > 0 ? NamedTextColor.AQUA : NamedTextColor.RED;
        lore.add(MenuText.kv("polyfill.ui.fluid", NamedTextColor.GRAY, water + "/" + WATER_CAP + " mB", fc));
        NamedTextColor sc = steam > 0 ? NamedTextColor.WHITE : NamedTextColor.GRAY;
        lore.add(MenuText.kv("polyfill.ui.gas", NamedTextColor.GRAY, steam + "/" + STEAM_CAP + " mB", sc));

        List<AbstractProcessingRecipe> recipes = RecipeManager.getRecipes(getMachineId());
        if (recipes == null || recipes.isEmpty()) {
            lore.add(MenuText.tr("polyfill.ui.no_recipe", NamedTextColor.GRAY));
        } else {
            lore.add(Component.empty());
            lore.add(MenuText.tr("polyfill.ui.recipes", NamedTextColor.GOLD));
            double speed = 1.0 + curGeneration;
            for (AbstractProcessingRecipe r : recipes) {
                int outCount = 0;
                String outName = "?";
                for (RecipeOutput o : r.getOutputs()) {
                    if (o instanceof ItemOutput io) {
                        ItemStack os = (ItemStack) io.getOutput();
                        outCount += os.getCount();
                        outName = os.getHoverName().getString();
                    } else if (o instanceof GasOutput go) {
                        dev.arubik.craftengine.gas.GasStack gs =
                                (dev.arubik.craftengine.gas.GasStack) go.getOutput();
                        outCount += gs.getAmount();
                        outName = gs.getType().name().toLowerCase() + " mB";
                    }
                }
                double effTicks = speed > 0 ? r.getProcessTime() / speed : r.getProcessTime();
                double perMin = effTicks > 0 ? (1200.0 / effTicks) * Math.max(1, outCount) : 0;
                lore.add(MenuText.noI(Component.text("→ " + outName + " x" + outCount, NamedTextColor.WHITE)));
                lore.add(MenuText.noI(Component.text(String.format("   %.1f/min", perMin), NamedTextColor.YELLOW)));
            }
        }
        return MenuText.icon(Material.BOOK, MenuText.tr("polyfill.ui.info", NamedTextColor.AQUA),
                lore.toArray(new Component[0]));
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = curUnlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        l.setTitleComponent(MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.DARK_AQUA)
                .append(Component.text(" (" + unlocked + "/" + UPGRADE_SLOTS + ")", NamedTextColor.GRAY))));
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> MenuText.icon(Material.BARRIER,
                        MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.addButton(17,
                (m, t) -> MenuText.icon(Material.ARROW, MenuText.tr("polyfill.ui.back", NamedTextColor.YELLOW)),
                (m, p) -> ((VaporFurnaceMk1BlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); i++) {
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    @Override
    public void tick(Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick();
        super.tick(level, pos, state);
    }
}
