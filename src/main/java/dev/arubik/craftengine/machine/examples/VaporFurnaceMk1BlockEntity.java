package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.persistence.PersistentDataType;

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
import dev.arubik.craftengine.util.TypedKey;
import io.papermc.paper.adventure.PaperAdventure;

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
 *
 * <h2>Steam economy — single unit: mB per tick (1 second = 20 ticks)</h2>
 * <p>All gas/steam amounts are measured in <b>mB</b>, and rates are expressed as <b>mB per tick</b>.
 * The water&rarr;steam recipe outputs {@code N} mB over {@code T} ticks, so the effective steam
 * production rate is {@code N/T mB/tick}. At BASE (no upgrades) the furnace produces
 * <b>600 mB / 50 ticks = 12 mB/tick</b> of steam; {@code speed = (1+generation)*(1+overclock)}
 * shortens the effective time and so raises mB/tick linearly. A base {@code gas_motor_mk1} running
 * on steam consumes <b>~12 mB/tick</b> to spin at base RPM while carrying one crusher's SU load
 * (crusher recipe rpm 16 / su 8). So a BASE furnace feeds exactly one base motor+crusher; generation
 * upgrades (and player-tuned overclock) give clear headroom for more. To re-tune the chain in the
 * future you only ever match these two mB/tick figures.</p>
 */
public class VaporFurnaceMk1BlockEntity extends AbstractMachineBlockEntity {

    // Fallback menu size if config omits menu_size. MUST stay 54 (6 rows; the 6th row is grace margin).
    private static final int DEFAULT_MENU_SIZE = 54;
    public static final int UPGRADE_SLOTS = 9; // container indices 0..8 (submenu only)
    private static final int BASE_UNLOCKED = 3;

    // Slot numbers are now CONFIG-DRIVEN (read from MachineMenuConfig); these arrays are populated
    // from the config in the constructor and used by the IO config + recipe loop. No hardcoded slots.
    private final int[] inputSlots;
    private final int[] outputSlots;
    private final int[] fuelSlots;

    public static final int WATER_CAP = 8000;
    public static final int STEAM_CAP = 8000;

    private static final org.bukkit.inventory.ItemStack FILLER = buildFiller();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final dev.arubik.craftengine.machine.menu.MachineMenuConfig menuConfig;
    private final int menuSize;

    // Derived from installed upgrades each process tick.
    private double curGeneration = 0;
    private double curFuelEff = 0;
    private double curOverclockLimit = 0;
    private int curUnlocked = BASE_UNLOCKED;

    // Player-tunable overclock fraction in [0, curOverclockLimit]. 0 = base speed only (never auto-set).
    private float overclock = 0f;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private MachineMenu active;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "vapor_furnace_overclock",
            PersistentDataType.FLOAT);
    private static final TypedKey<Float> KEY_GEN_BUFFER = TypedKey.of("craftengine", "vapor_furnace_gen_buffer",
            PersistentDataType.FLOAT);

    // Deterministic GENERATION buffer for ITEM outputs only: each finished craft adds curGeneration; on
    // every whole 1.0 it dispenses one extra set of the recipe's ITEM outputs. Gas/fluid outputs are NOT
    // doubled here — for those, generation already scales the RATE via speed (more mB/tick). Persisted.
    private double genBuffer = 0.0;

    public VaporFurnaceMk1BlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>(), new ArrayList<>(), defaultMenuConfig());
    }

    /** Fallback config (used only by the no-arg ctor); the real layout always comes from yml. */
    private static dev.arubik.craftengine.machine.menu.MachineMenuConfig defaultMenuConfig() {
        return new dev.arubik.craftengine.machine.menu.MachineMenuConfig(
                DEFAULT_MENU_SIZE, "cml:copper_furnace_gui",
                new int[] { 11, 12 }, new int[] { 14, 15 }, new int[] { 31 },
                new ArrayList<>());
    }

    public VaporFurnaceMk1BlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, dev.arubik.craftengine.machine.menu.MachineMenuConfig menuConfig) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : DEFAULT_MENU_SIZE);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : DEFAULT_MENU_SIZE;
        this.inputSlots = this.menuConfig.inputSlots;
        this.outputSlots = this.menuConfig.outputSlots;
        this.fuelSlots = this.menuConfig.fuelSlots;

        // Single UNFILTERED fluid tank: it holds WATER (for water->steam) OR LAVA (for the
        // steel recipe). Because the tank has no FluidType filter, a connected pipe/tank can pump
        // either liquid in, and recipe matching below picks the recipe whose fluid id matches the
        // fluid currently stored (see fluidType()/matchesAll). LAVA acts as the heat source for the
        // steel recipe, which is why that recipe is fuelRequired=false — the lava IS the fuel.
        addFluidTank(new FluidTank("water", WATER_CAP));
        addGasTank(new GasTank("steam", STEAM_CAP));

        // IO (LOCAL dirs, rotated to world via `facing`):
        //   WATER fluid IN  -> any horizontal side (water can enter from anywhere)
        //   VAPOR gas  OUT  -> UP (top face only)
        //   ITEM  IN        -> NORTH (back)
        //   ITEM  OUT       -> DOWN (bottom face, so a hopper pulls results)
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        // FLUID (water/lava) IN: the 4 horizontal facings + the bottom. No fluid output.
        cfg.addInput(IOType.FLUID, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.NORTH);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.SOUTH);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.EAST);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.WEST);
        cfg.withInputSlot(IOType.FLUID, 0, Direction.DOWN);

        // GAS (steam) OUT: top only.
        cfg.addOutput(IOType.GAS, Direction.UP);
        cfg.withOutputSlot(IOType.GAS, 0, Direction.UP);

        // ITEM IN: 4 horizontal facings (not the bottom — bottom is item-output only).
        // Slot NUMBERS come from config (input/output/fuel); only the FACE behaviour is fixed here.
        cfg.addInput(IOType.ITEM, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, inputSlots);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, fuelSlots);
        // ITEM OUT: 4 horizontal facings + the bottom.
        cfg.addOutput(IOType.ITEM, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, outputSlots);
        setIOConfiguration(cfg);
    }

    // Invisible, tooltip-less filler so only the UI image shows in background slots (like the workbench).
    private static org.bukkit.inventory.ItemStack buildFiller() {
        org.bukkit.inventory.ItemStack s = null;
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(Key.of("cml", "gui_empty"));
            if (def != null)
                s = def.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        if (s == null || s.getType() == Material.AIR)
            s = new org.bukkit.inventory.ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = s.getItemMeta();
        if (meta != null) {
            try {
                meta.setHideTooltip(true);
            } catch (Throwable ignored) {
            }
            meta.displayName(Component.empty());
            s.setItemMeta(meta);
        }
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
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);

        // Overclock is ONLY ever set by the player (bumpOverclock); here we just keep it within the
        // current limit. It is never auto-applied: default is 0 and clamps down if the limit drops.
        // Overclock range is symmetric [-limit, +limit] (the limit also enables UNDERCLOCK),
        // with the negative side hard-capped at -99% (speed never hits zero).
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);

        // generation -> faster processing; fuel_efficiency -> stretches each fuel item's burn time;
        // overclock multiplies speed the same way as generation (so steam mB/tick rises too).
        double speed = (1.0 + curGeneration) * (1.0 + overclock);
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

    /** Type of the fluid currently in the (unfiltered) tank, or EMPTY. Used to distinguish a
     *  water recipe (water->steam) from a lava recipe (lava+iron+coal->steel). */
    private dev.arubik.craftengine.fluid.FluidType fluidType() {
        try {
            return fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos()).getType();
        } catch (Throwable t) {
            return dev.arubik.craftengine.fluid.FluidType.EMPTY;
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
    private static String recipeDesc(AbstractProcessingRecipe r) {
        StringBuilder sb = new StringBuilder("in[");
        for (RecipeInput in : r.getInputs()) {
            if (in instanceof FluidInput fi)
                sb.append("fluid:").append(fi.getFluid().getType()).append('x').append(fi.getAmount());
            else if (in instanceof GasInput gi)
                sb.append("gas:").append(gi.getGas().getType()).append('x').append(gi.getAmount());
            else
                sb.append("item:").append(in).append('x').append(in.getAmount());
            sb.append(' ');
        }
        return sb.append(']').toString();
    }

    private boolean matchesAll(AbstractProcessingRecipe recipe) {
        boolean[] used = new boolean[inputSlots.length];
        boolean anyInput = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                int slot = -1;
                for (int i = 0; i < inputSlots.length; i++) {
                    if (!used[i] && in.matches(getItem(inputSlots[i]))) {
                        slot = i;
                        break;
                    }
                }
                if (slot < 0)
                    return false;
                used[slot] = true;
                anyInput = true;
            } else if (in instanceof FluidInput fi) {
                // The recipe names the exact fluid it needs (water OR lava); the tank is shared and
                // unfiltered, so we must match BOTH the type and the amount currently stored.
                if (fluidType() != fi.getFluid().getType())
                    return false;
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
        // Among matching recipes, prefer the MOST SPECIFIC item recipe — the one with the most inputs.
        // Otherwise a generic 1-input recipe (raw_iron -> ingot) shadows a 3-input one (raw_iron + coal +
        // lava -> steel) that ALSO matches, and the wrong (often fuel-gated, idle) recipe is picked.
        // Item recipes still beat fluid-only cooking (water -> steam); fall back to fluid/gas when none.
        AbstractProcessingRecipe bestItem = null;
        int bestInputs = -1;
        AbstractProcessingRecipe fluidOnly = null;
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (!matchesAll(r))
                continue;
            boolean hasItemInput = false;
            for (RecipeInput in : r.getInputs())
                if (isItem(in)) {
                    hasItemInput = true;
                    break;
                }
            if (hasItemInput) {
                int n = r.getInputs().size();
                if (n > bestInputs) {
                    bestInputs = n;
                    bestItem = r;
                }
            } else if (fluidOnly == null) {
                fluidOnly = r;
            }
        }
        return bestItem != null ? bestItem : fluidOnly;
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
        return outputSlots;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof ItemOutput io) {
            ItemStack out = (ItemStack) io.getOutput();
            for (int s : outputSlots) {
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

    /**
     * Consume inputs once + dispense outputs once, then the GENERATION buffer dispenses EXTRA ITEM
     * outputs (gas/fluid are excluded — generation scales their rate via speed instead). So a furnace
     * with +generation cooking an item recipe (e.g. steel) yields bonus items, while its steam recipes
     * just run faster.
     */
    @Override
    protected void process(Level level, AbstractProcessingRecipe recipe) {
        consumeInputs(level, recipe);
        for (RecipeOutput o : recipe.getOutputs())
            o.dispense(level, this);
        if (curGeneration > 0.0) {
            genBuffer += curGeneration;
            int extra = 0;
            while (genBuffer >= 1.0) {
                genBuffer -= 1.0;
                extra++;
            }
            for (int t = 0; t < extra; t++)
                for (RecipeOutput o : recipe.getOutputs())
                    if (o instanceof ItemOutput)
                        o.dispense(level, this);
            if (extra > 0)
                setChanged();
        }
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        boolean[] used = new boolean[inputSlots.length];
        for (RecipeInput in : recipe.getInputs()) {
            if (isItem(in)) {
                for (int i = 0; i < inputSlots.length; i++) {
                    if (!used[i] && in.matches(getItem(inputSlots[i]))) {
                        removeItem(inputSlots[i], in.getAmount());
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
            case "water": {
                // The fluid tank accepts ANY fluid (water, lava, ...); report the ACTUAL type so the
                // bar can render it (e.g. blue water vs orange lava) instead of always "water".
                dev.arubik.craftengine.fluid.FluidStack f =
                        fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos());
                return (f != null && !f.isEmpty())
                        ? f.getType().name().toLowerCase(java.util.Locale.ROOT) : "";
            }
            case "steam": {
                dev.arubik.craftengine.gas.GasStack g =
                        gasTanks.get(0).getGas(getNMSLevel(), getMachinePos());
                return (g != null && !g.isEmpty())
                        ? g.getType().name().toLowerCase(java.util.Locale.ROOT) : "";
            }
            default:
                return super.barSubtype(id);
        }
    }

    // ---------------- multi-page menu ----------------

    @Override
    public MachineLayout getLayout() {
        return switch (page) {
            case 1 -> buildUpgradeLayout();
            case 2 -> buildOverclockLayout();
            default -> buildMainLayout();
        };
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
        this.shownUnlocked = curUnlocked;
    }

    // The unlocked-slot count the open Upgrades page was last built with (-1 = not built).
    private int shownUnlocked = -1;

    /** Rebuild the open Upgrades page when the unlocked-slot count changes (cast in/out). */
    private void refreshUpgradePageIfNeeded() {
        if (page != 1 || active == null || shownUnlocked == curUnlocked)
            return;
        java.util.List<org.bukkit.entity.HumanEntity> viewers =
                new ArrayList<>(active.getInventory().getViewers());
        shownUnlocked = curUnlocked;
        for (org.bukkit.entity.HumanEntity h : viewers) {
            if (h instanceof org.bukkit.entity.Player p)
                openPage(p, 1);
        }
    }

    private MachineLayout buildMainLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize,
                "Copper Furnace");
        // Title image resolved by id from the central polyfills_gui.yml (machine "vapor_furnace_mk1",
        // menu "main"); falls back to a plain-text title when no image is configured / it fails to resolve.
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.vapor_furnace_title", NamedTextColor.GOLD)));

        // Functional slots — numbers all come from config (no hardcoded constants).
        for (int s : fuelSlots)
            l.addSlot(s, MenuSlotType.FUEL);
        for (int s : inputSlots)
            l.addSlot(s, MenuSlotType.INPUT);
        for (int s : outputSlots)
            l.addSlot(s, MenuSlotType.OUTPUT);

        // Config-driven buttons (upgrades / overclock / deplete). No hardcoded buttons.
        for (dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        // Config-driven bars: water/steam fluid columns + fuel/progress gauges (all by ranges).
        MachineBars.install(l, bars);

        // Reusable recipe-info icon at the configurable info slot (default 5; -1 = none).
        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : 5;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((VaporFurnaceMk1BlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    /** Render + wire one config-declared button generically (icon, name/lore, action, lock state). */
    private void installButton(MachineLayout l,
            dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            VaporFurnaceMk1BlockEntity s = (VaporFurnaceMk1BlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.GOLD), lore(b.lore));
        }, (m, p) -> {
            VaporFurnaceMk1BlockEntity s = (VaporFurnaceMk1BlockEntity) m;
            if (s.isButtonLocked(b))
                return; // locked: swallow the click
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_FLUID -> s.depleteFluid();
                case DEPLETE_GAS -> s.depleteGas();
                case NONE -> {
                }
            }
        });
    }

    /** Locked iff the button declares {@code locked_when: no_overclock} and no overclock is unlocked. */
    private boolean isButtonLocked(dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button b) {
        return b.lockedWhen == dev.arubik.craftengine.machine.menu.MachineMenuConfig.LockedWhen.NO_OVERCLOCK
                && curOverclockLimit <= 0;
    }

    /** {@code lang:key} or bare lang key -> translatable; otherwise literal text. Null -> empty. */
    private static Component label(String s, NamedTextColor color) {
        if (s == null)
            return Component.empty();
        String key = s.startsWith("lang:") ? s.substring(5) : s;
        // Treat "namespaced.dotted.keys" as lang keys (the project convention, e.g. polyfill.ui.*).
        if (key.contains(".") && !key.contains(" "))
            return MenuText.tr(key, color);
        return MenuText.lit(s, color);
    }

    private static Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty())
            return new Component[0];
        Component[] out = new Component[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            out[i] = label(lines.get(i), NamedTextColor.GRAY);
        return out;
    }

    /** Parse a {@code namespace:path} item id (defaults to the cml namespace). */
    private static Key parseKey(String spec) {
        if (spec == null)
            return Key.of("cml", "gui_empty");
        int i = spec.indexOf(':');
        return i < 0 ? Key.of("cml", spec) : Key.of(spec.substring(0, i), spec.substring(i + 1));
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

    /** Recipe-info icon via the shared {@link dev.arubik.craftengine.machine.menu.RecipeInfoIcon}.
     *  Furnace speed folds generation into the process time, so generationBonus is 0 here. */
    private org.bukkit.inventory.ItemStack infoIcon() {
        double speed = (1.0 + curGeneration) * (1.0 + overclock);
        // generationBonus feeds the items/min line (the ITEM output buffer); gas/fluid mB/tick ignore it
        // and scale via `speed` only. So item recipes show the bonus, steam recipes don't double-count.
        return dev.arubik.craftengine.machine.menu.RecipeInfoIcon.build(this, getMachineId(), speed, curGeneration);
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = curUnlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "upgrade");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.GOLD)));
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(
                        MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(),
                (m, p) -> ((VaporFurnaceMk1BlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    // ---------------- overclock submenu (copied from the crusher, adapted to the furnace) ----------------

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), NamedTextColor.RED,
                () -> this.overclock, () -> this.curOverclockLimit,
                (up, c) -> bumpOverclock(up, c),
                p -> openPage(p, 0));
        fillRest(l);
        return l;
    }

    /** Player-only overclock tuning, clamped to the installed OVERCLOCK_LIMIT (never auto-applied). */
    public void bumpOverclock(boolean up, ClickType c) {
        float delta = dev.arubik.craftengine.machine.menu.OverclockMenu.step(c);
        this.overclock += up ? delta : -delta;
        // Overclock range is symmetric [-limit, +limit] (the limit also enables UNDERCLOCK),
        // with the negative side hard-capped at -99% (speed never hits zero).
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
        setChanged();
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); i++) {
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    // ---------------- persistence ----------------
    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_OC, overclock);
        set(KEY_GEN_BUFFER, (float) genBuffer);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = getOrDefault(KEY_OC, 0f);
        this.genBuffer = getOrDefault(KEY_GEN_BUFFER, 0f);
    }

    @Override
    public void tick(Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick();
        super.tick(level, pos, state);
        if (!level.isClientSide()) {
            refreshUpgradePageIfNeeded();
            maybeUpdateActivated(level, pos, state, isProcessing());
        }
    }

    // Last pushed 'activated' blockstate (null = unknown). Drives the LIT (animated) appearance.
    private Boolean lastActivated = null;

    /** Flip the block's {@code activated} property so CE swaps to the lit appearance while processing. */
    private void maybeUpdateActivated(Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state, boolean activeState) {
        if (lastActivated != null && lastActivated == activeState)
            return;
        try {
            net.momirealms.craftengine.core.block.property.Property p =
                    (state == null) ? null : state.getProperty("activated");
            if (p == null) {
                lastActivated = activeState;
                return;
            }
            Object val = p.valueByName(String.valueOf(activeState));
            if (val == null)
                return;
            net.momirealms.craftengine.core.block.ImmutableBlockState ns =
                    net.momirealms.craftengine.core.block.ImmutableBlockState.with(state, p,
                            (Comparable) val);
            if (ns == state) {
                lastActivated = activeState;
                return;
            }
            level.setBlock(pos,
                    (net.minecraft.world.level.block.state.BlockState) ns.customBlockState().minecraftState(), 2);
            lastActivated = activeState;
        } catch (Throwable ignored) {
        }
    }
}
