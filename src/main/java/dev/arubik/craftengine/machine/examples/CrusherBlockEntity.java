package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import dev.arubik.craftengine.util.NbtType;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
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
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.TypedKey;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * [TASK-02] Crusher — RPM-powered processing machine with an attribute-based upgrade
 * system (same definition form as the advanced vapor motor's {@code upgrades:} config).
 *
 * <p>Upgrade attributes are reinterpreted for a consumer machine:</p>
 * <ul>
 *   <li>{@code polyfill:generation} &mdash; raises BASE speed. Does NOT change the rpm/SU it needs.</li>
 *   <li>{@code polyfill:overclock_limit} &mdash; headroom for the player-tunable OVERCLOCK. Base 0
 *       (speed cannot be pushed past base). When &gt; 0, an Overclock button/menu unlocks; pushing
 *       overclock raises speed AND the rpm required AND the SU drawn.</li>
 *   <li>{@code polyfill:fuel_efficiency} &mdash; reduces the rpm the recipe demands (NOT the SU).</li>
 *   <li>{@code polyfill:extra_slots} &mdash; unlocks more upgrade slots.</li>
 * </ul>
 *
 * <p>Wide engineer-style main page: upgrade storage lives in container slots 0..8 (hidden on the
 * main page, shown on the Upgrades page), a 2x2 input grid, a 2x2 output grid, a center progress
 * item, plus status / info / upgrades / overclock buttons.</p>
 */
public class CrusherBlockEntity extends AbstractMachineBlockEntity implements RpmConsumer,
        dev.arubik.craftengine.machine.render.BetterModelDriven {

    // Fallback menu size if config omits menu_size. MUST stay 54.
    private static final int DEFAULT_MENU_SIZE = 54; // 6 rows; slots 0..8 reserved for upgrades
    public static final int UPGRADE_SLOTS = 9; // container indices 0..8
    private static final int BASE_UNLOCKED = 3;

    /** Belt-style vacuum radius for dropped item entities sitting on top of the crusher. */
    private static final double ITEM_PICKUP_RADIUS = 0.75;

    // Slot numbers are now CONFIG-DRIVEN (read from MachineMenuConfig); these arrays are populated
    // from the config in the constructor and used by the IO config + recipe loop. No hardcoded slots.
    private final int[] inputSlots;
    private final int[] outputSlots;

    private static final org.bukkit.inventory.ItemStack FILLER = buildFiller();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;

    private float inputRpm = 0f;
    private int activeSu = 0;
    private RpmProvider activeMotor;

    // Continuous-stress grace: keep reporting the SU load for a few ticks after demand drops so the
    // network load doesn't flicker off between items (which would defeat overstress enforcement).
    private static final int STRESS_GRACE_TICKS = 20;
    private int stressGrace = 0;
    private int lastSuLoad = 0;

    // Player-tunable overclock fraction in [0, overclockLimit]. 0 = base speed only.
    private float overclock = 0f;

    // Derived each processTick from the installed upgrades + overclock setting.
    private double curGeneration = 0;
    private double curOverclockLimit = 0;
    private double curFuelEff = 0;
    private int curUnlocked = BASE_UNLOCKED;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private MachineMenu active;

    // Deterministic GENERATION output buffer: each finished craft adds curGeneration; once it reaches
    // 1.0 it spends 1.0 and that craft dispenses one extra full output set (so +35% gen ≈ a bonus
    // output every ~3 crafts, averaging 1.35× output). Persisted so the partial buffer survives reloads.
    private double genBuffer = 0.0;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "crusher_overclock",
            NbtType.FLOAT);
    private static final TypedKey<Float> KEY_GEN_BUFFER = TypedKey.of("craftengine", "crusher_gen_buffer",
            NbtType.FLOAT);

    public CrusherBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>(), new ArrayList<>(), defaultMenuConfig());
    }

    /** Fallback config (used only by the no-arg ctor); the real layout always comes from yml. */
    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(DEFAULT_MENU_SIZE, "cml:copper_crusher_gui",
                new int[] { 12 }, new int[] { 14 }, new int[0], new ArrayList<>(), 4);
    }

    public CrusherBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, MachineMenuConfig menuConfig) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : DEFAULT_MENU_SIZE);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : DEFAULT_MENU_SIZE;
        this.inputSlots = this.menuConfig.inputSlots;
        this.outputSlots = this.menuConfig.outputSlots;

        // Top-in, bottom-out ONLY. A hopper/dropper above pushes into the input slots through the
        // TOP face; a hopper below pulls from the output slots through the DOWN face. No horizontal
        // item faces. (WorldlyContainer face filtering is enforced by the base via the IOConfig.)
        // Slot NUMBERS come from config (input/output); only the FACE behaviour is fixed here.
        IOConfiguration.Simple cfg = new IOConfiguration.Simple();
        cfg.addInput(IOType.ITEM, Direction.UP);
        cfg.addOutput(IOType.ITEM, Direction.DOWN);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, inputSlots);
        cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, outputSlots);
        setIOConfiguration(cfg);

        // Internal input AND output slots hold up to 64 each.
        setMaxStackSize(64);
    }

    /**
     * Guarded BetterModel render driver (Task C). The BetterModel "crusher" model is the ONLY
     * processing visual: the CE block itself is invisible (empty model over a translucent proxy),
     * so this model is shown for the whole lifetime of the block (idle + processing) and only the
     * "on" animation loops while processing. Speed multiplier = (1+overclock) (generation is an
     * output bonus, not speed). Null-safe + no-op when BetterModel is absent.
     */
    private final dev.arubik.craftengine.machine.render.BetterModelMachineRenderer renderer =
            new dev.arubik.craftengine.machine.render.BetterModelMachineRenderer(
                    "crusher", () -> (1.0 + overclock));

    @Override
    public dev.arubik.craftengine.machine.render.BetterModelMachineRenderer betterModelRenderer() {
        return renderer;
    }

    // Invisible, tooltip-less filler so only the UI image shows in background slots (like the workbench).
    private static org.bukkit.inventory.ItemStack buildFiller() {
        return MenuText.emptyFiller();
    }

    // ---------------- upgrades (attribute system, like the advanced motor) ----------------

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
        Key custom = CraftEngineItems.getCustomItemId(b);
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

    /** Re-reads the upgrade slots and folds the attribute modifiers; feeds the speed multiplier
     *  back into {@link AbstractMachineBlockEntity}'s progress loop via {@code upgradeModifiers}. */
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
        List<Mod> active = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                active.addAll(m);
        }
        var attrs = MachineAttributes.compute(active);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);

        // Overclock setting can never exceed the current limit.
        // Overclock range is symmetric [-limit, +limit] (the limit also enables UNDERCLOCK),
        // with the negative side hard-capped at -99% (speed never hits zero).
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);

        // Speed = (1 + overclock) ONLY. GENERATION is an OUTPUT bonus (handled in process() via the
        // deterministic generation buffer), NOT a speed bonus. Overclock costs rpm/SU; generation is free.
        double speed = (1.0 + overclock);
        this.upgradeModifiers = new dev.arubik.craftengine.machine.upgrade.UpgradeModifiers(speed, 1.0, 0.0);
    }

    /** rpm the matched recipe actually demands after fuel-efficiency + overclock. */
    private float effMinRpm(AbstractProcessingRecipe recipe) {
        return (float) (recipe.getMinRpm() * (1.0 + overclock) * (1.0 - curFuelEff));
    }

    /** SU drawn after overclock (generation/efficiency do not change SU). The power cost rises
     *  SLIGHTLY super-linearly with overclock — (1+oc)^1.25 — so pushing speed past +100% costs
     *  disproportionately more stress (e.g. +100% draws ~2.38x, +300% ~5.66x vs the flat 2x/4x),
     *  without being punishing. Underclock (1+oc<1) likewise cuts SU a touch more than linearly. */
    private int effSu(AbstractProcessingRecipe recipe) {
        double factor = Math.pow(Math.max(0.0, 1.0 + overclock), 1.25);
        return (int) Math.round(recipe.getSuCost() * factor);
    }

    @Override
    public int effectiveRpm(AbstractProcessingRecipe r) {
        return r == null ? 0 : Math.round(effMinRpm(r));
    }

    @Override
    public int effectiveSu(AbstractProcessingRecipe r) {
        return r == null ? 0 : effSu(r);
    }

    /**
     * Binary "powered" check for the RPM gauge: true when there's a matched recipe AND the crusher
     * currently has enough rpm AND su to run it (same condition the recipe loop's {@link #canProcess}
     * uses). When the recipe needs no rpm, any connected source counts; with no recipe loaded we
     * report "powered" iff rpm is flowing so the gauge lights up when a motor is attached.
     */
    private boolean hasPower() {
        AbstractProcessingRecipe r = currentRecipe();
        if (r == null)
            return inputRpm > 0f;
        return canProcess(getNMSLevel(), r);
    }

    @Override
    public double[] barStat(String id) {
        switch (id) {
            case "progress":
                return new double[] { getProgress(), Math.max(1, getMaxProgress()) };
            case "rpm":
                // Burning fire only while actually RUNNING a recipe (or at least powered+ready).
                return new double[] { (isProcessing() || hasPower()) ? 100 : 0, 100 };
            default:
                return super.barStat(id);
        }
    }

    @Override
    public java.util.Map<String, String> barPlaceholders(String id) {
        if ("rpm".equals(id)) {
            AbstractProcessingRecipe r = currentRecipe();
            int req = r != null ? (int) effMinRpm(r) : 0;
            int su = r != null ? effSu(r) : 0;
            java.util.Map<String, String> m = new java.util.HashMap<>();
            m.put("rpm", String.valueOf((int) inputRpm));
            m.put("req", String.valueOf(req));
            m.put("su", String.valueOf(su));
            return m;
        }
        return super.barPlaceholders(id);
    }

    // ---------------- recipe loop ----------------

    private int matchingInputSlot(AbstractProcessingRecipe recipe) {
        for (int s : inputSlots) {
            ItemStack in = getItem(s);
            if (in == null || in.isEmpty())
                continue;
            if (matchesInputs(recipe, in))
                return s;
        }
        return -1;
    }

    private boolean matchesInputs(AbstractProcessingRecipe recipe, ItemStack input) {
        boolean any = false;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                if (!in.matches(input))
                    return false;
                any = true;
            }
        }
        return any;
    }

    private AbstractProcessingRecipe currentRecipe() {
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            if (matchingInputSlot(r) >= 0)
                return r;
        }
        return null;
    }

    @Override
    protected boolean requiresFuel() {
        return false; // mechanical power
    }

    @Override
    protected String getMachineId() {
        return "crusher";
    }

    @Override
    public int[] getOutputSlots() {
        return outputSlots;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return currentRecipe();
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe))
            return false;
        this.activeSu = effSu(recipe);
        return recipe.getMinRpm() <= 0 || inputRpm >= effMinRpm(recipe);
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
        return true;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int slot = matchingInputSlot(recipe);
        if (slot < 0)
            return;
        for (RecipeInput in : recipe.getInputs()) {
            if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                removeItem(slot, in.getAmount());
            }
        }
    }

    /**
     * Consume the inputs once, then dispense the outputs {@code 1 + bonus} times, where {@code bonus}
     * comes from the deterministic GENERATION buffer: each craft adds {@code curGeneration} to the
     * buffer and every whole 1.0 it accumulates yields one extra full output set this craft. So +35%
     * generation gives a bonus set roughly every third craft (≈1.35× output on average), with the
     * fractional remainder carried over. Each dispensed set still honours per-output chance.
     */
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
        for (int t = 0; t < 1 + extra; t++) {
            for (RecipeOutput output : recipe.getOutputs()) {
                output.dispense(level, this);
            }
        }
        if (extra > 0)
            setChanged();
    }

    @Override
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick(); // keep the multi-page UI (progress/info/rpm) live
        if (!level.isClientSide()) {
            // PULL rpm from the strongest adjacent motor. Select by POTENTIAL rpm (rpm ignoring
            // overstress) so we still find — and keep reporting our SU load to — a motor that is
            // currently stalled at rpm 0 BECAUSE of our load. Selecting by live getRpm() would lose the
            // motor the instant it overstresses, dropping our load, letting it recover -> flicker.
            this.activeMotor = null;
            float bestPot = 0f;
            float actualRpm = 0f;
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(getMachinePos().getX(),
                            getMachinePos().getY(), getMachinePos().getZ());
            for (Direction d : Direction.values()) {
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, getMachinePos().relative(d));
                if (be != null && be.controller instanceof RpmProvider p && p.isRpmSource()
                        && p.potentialRpm() > bestPot) {
                    if (!p.rpmReaches(cePos))
                        continue;
                    bestPot = p.potentialRpm();
                    actualRpm = p.getRpm(); // ACTUAL delivered rpm (0 while overstressed) -> gates canProcess
                    this.activeMotor = p;
                }
            }
            this.inputRpm = actualRpm;
        }
        super.tick(level, pos, state); // recomputeUpgrades + recipe loop (reads inputRpm)
        if (!level.isClientSide()) {
            // A cast just unlocked (or a removed one re-locked) upgrade slots: rebuild the open
            // Upgrades page so the newly INPUT slots appear live instead of only after reopening.
            refreshUpgradePageIfNeeded();
            // (1) Hopper-style auto-pickup: pull ONE item per tick from a container directly above.
            pullFromAbove(level);
            // (1b) Belt-style auto-pickup: vacuum dropped ITEM ENTITIES floating above the crusher.
            pickupItemEntitiesAbove(level);
            // (2) If there's no container below, drop finished outputs into the world below.
            dropOutputsIfNoContainerBelow(level);
            // Continuous SU draw with a grace window: report the recipe's SU demand to the motor every
            // tick while we have a matching recipe + items, and keep reporting it for STRESS_GRACE_TICKS
            // across item transitions / overstress stalls. Otherwise the load flickers off the moment we
            // stall, the motor un-overstresses, and an UNDERPOWERED motor would briefly run us anyway.
            // With a sustained load the motor's overstress (rpm->0) actually stops the machine.
            AbstractProcessingRecipe demandR = currentRecipe();
            int demand = (demandR != null && matchingInputSlot(demandR) >= 0) ? effSu(demandR) : 0;
            if (demand > 0) {
                lastSuLoad = demand;
                stressGrace = STRESS_GRACE_TICKS;
            }
            if (stressGrace > 0 && lastSuLoad > 0 && activeMotor != null) {
                activeMotor.reportStressLoad(lastSuLoad);
                stressGrace--;
            }
            // (3) Drive the block's visual state + BetterModel animation.
            updateActivation(level, pos, state);
        }
    }

    // ---------------- top-in auto-pickup / bottom drop ----------------

    /** Pull one item per tick from an inventory directly ABOVE us (like a hopper pulling up). */
    private void pullFromAbove(Level level) {
        net.minecraft.core.BlockPos above = getMachinePos().above();
        net.minecraft.world.level.block.entity.BlockEntity nmsBe = level.getBlockEntity(above);
        net.minecraft.world.Container src = null;
        if (nmsBe instanceof net.minecraft.world.Container c) {
            src = c;
        } else {
            // Custom machine container (another machine, depot, etc.) exposed via our bridge.
            net.momirealms.craftengine.core.block.entity.BlockEntity ceBe =
                    BukkitBlockEntityTypes.getIfLoaded(level, above);
            if (ceBe != null && ceBe.controller instanceof net.minecraft.world.Container c)
                src = c;
        }
        if (src == null)
            return;
        int[] srcSlots;
        if (src instanceof net.minecraft.world.WorldlyContainer wc)
            srcSlots = wc.getSlotsForFace(Direction.DOWN); // pulling out the bottom of the source
        else {
            srcSlots = new int[src.getContainerSize()];
            for (int i = 0; i < srcSlots.length; i++)
                srcSlots[i] = i;
        }
        for (int s : srcSlots) {
            ItemStack in = src.getItem(s);
            if (in == null || in.isEmpty())
                continue;
            if (src instanceof net.minecraft.world.WorldlyContainer wc
                    && !wc.canTakeItemThroughFace(s, in, Direction.DOWN))
                continue;
            ItemStack one = in.copy();
            one.setCount(1);
            if (insertIntoInputs(one)) {
                src.removeItem(s, 1);
                src.setChanged();
                setChanged();
                return; // one item per tick
            }
        }
    }

    /**
     * Vacuum dropped item entities floating in the space ABOVE the crusher (and slightly around the
     * top, like the conveyor belt's pickup), pulling them into the INPUT slot. Only items that are a
     * valid recipe input are grabbed (if NO recipes define item inputs, any item is accepted). One
     * item per tick keeps it tidy. Mirrors {@link dev.arubik.craftengine.conveyor.ConveyorBlockEntity}'s
     * getNearbyEntities pickup approach.
     */
    private void pickupItemEntitiesAbove(Level level) {
        try {
            org.bukkit.World bw = level.getWorld();
            if (bw == null)
                return;
            net.minecraft.core.BlockPos pos = getMachinePos();
            // Center the scan on the block face directly above us.
            org.bukkit.Location center = new org.bukkit.Location(bw,
                    pos.getX() + 0.5, pos.getY() + 1.0 + ITEM_PICKUP_RADIUS, pos.getZ() + 0.5);
            org.bukkit.entity.Item nearest = null;
            double best = Double.MAX_VALUE;
            for (org.bukkit.entity.Entity e : bw.getNearbyEntities(center,
                    ITEM_PICKUP_RADIUS, ITEM_PICKUP_RADIUS, ITEM_PICKUP_RADIUS)) {
                if (!(e instanceof org.bukkit.entity.Item it))
                    continue;
                if (it.isDead() || !it.isValid() || it.getPickupDelay() > 0)
                    continue;
                org.bukkit.inventory.ItemStack bukkit = it.getItemStack();
                if (bukkit == null || bukkit.getType().isAir())
                    continue;
                ItemStack one = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                if (one == null || one.isEmpty())
                    continue;
                one = one.copy();
                one.setCount(1);
                if (!acceptsAsInput(one))
                    continue;
                double d = it.getLocation().distanceSquared(center);
                if (d < best) {
                    best = d;
                    nearest = it;
                }
            }
            if (nearest == null)
                return;
            org.bukkit.inventory.ItemStack bukkit = nearest.getItemStack();
            ItemStack one = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
            one = one.copy();
            one.setCount(1);
            if (insertIntoInputs(one)) {
                org.bukkit.inventory.ItemStack rem = bukkit.clone();
                rem.setAmount(rem.getAmount() - 1);
                if (rem.getAmount() <= 0)
                    nearest.remove();
                else
                    nearest.setItemStack(rem);
                setChanged();
            }
        } catch (Throwable ignored) {
        }
    }

    /** True if the stack can be a recipe input. If no recipe declares item inputs, accept anything. */
    private boolean acceptsAsInput(ItemStack stack) {
        boolean anyRecipeHasItemInput = false;
        for (AbstractProcessingRecipe r : RecipeManager.getRecipes(getMachineId())) {
            for (RecipeInput in : r.getInputs()) {
                if (in instanceof ItemInput || in instanceof CraftEngineItemInput || in instanceof TagInput) {
                    anyRecipeHasItemInput = true;
                    if (in.matches(stack))
                        return true;
                }
            }
        }
        return !anyRecipeHasItemInput; // no filter defined -> accept any item
    }

    /** Add a single item into the input slots respecting the 64 cap; true if it fit. */
    private boolean insertIntoInputs(ItemStack one) {
        for (int slot : inputSlots) {
            ItemStack cur = getItem(slot);
            if (cur == null || cur.isEmpty()) {
                setItem(slot, one.copy());
                return true;
            }
            if (ItemStack.isSameItemSameComponents(cur, one)
                    && cur.getCount() < Math.min(64, cur.getMaxStackSize())) {
                cur.grow(1);
                return true;
            }
        }
        return false;
    }

    /**
     * Eject output-slot items into the world below ONLY when the block directly below is passable
     * (air / replaceable / non-solid) and is NOT a container. If a container is below, vanilla pulls
     * from us. If a solid (or otherwise occupied / "tapado") block is below and it's not a container,
     * we HOLD the items until air or a container becomes available — never drop into a wall.
     */
    private void dropOutputsIfNoContainerBelow(Level level) {
        net.minecraft.core.BlockPos below = getMachinePos().below();
        if (level.getBlockEntity(below) instanceof net.minecraft.world.Container)
            return; // a hopper/chest below pulls instead (handled by vanilla via our bridge)
        net.momirealms.craftengine.core.block.entity.BlockEntity ceBe =
                BukkitBlockEntityTypes.getIfLoaded(level, below);
        if (ceBe != null && ceBe.controller instanceof net.minecraft.world.Container)
            return;
        // A conveyor belt below counts as an "empty" target while it has room: drop onto it and the
        // belt vacuums the item up to carry it away.
        boolean beltBelow = ceBe != null
                && ceBe.controller instanceof dev.arubik.craftengine.conveyor.ConveyorReceiver r
                && !r.isFull();
        // Below is not a container: drop only if the space is passable (air / replaceable) OR a belt
        // with room. A solid/covered block (or a full belt) holds the items instead.
        net.minecraft.world.level.block.state.BlockState belowState = level.getBlockState(below);
        boolean passable = belowState.isAir()
                || belowState.canBeReplaced()
                || belowState.getCollisionShape(level, below).isEmpty();
        if (!passable && !beltBelow)
            return; // solid block / covered / full belt below -> hold items, don't drop
        for (int slot : outputSlots) {
            ItemStack out = getItem(slot);
            if (out == null || out.isEmpty())
                continue;
            org.bukkit.World bw = level.getWorld();
            // For a belt, drop just above its top so its pickup grabs it; otherwise drop in the cell.
            double dropY = beltBelow ? below.getY() + 0.55 : below.getY() + 0.5;
            org.bukkit.Location loc = new org.bukkit.Location(bw,
                    below.getX() + 0.5, dropY, below.getZ() + 0.5);
            bw.dropItemNaturally(loc, dev.arubik.craftengine.util.BridgeUtils.toBukkit(out.copy()));
            setItem(slot, ItemStack.EMPTY);
            setChanged();
            return; // one stack per tick keeps it tidy
        }
    }

    // ---------------- visual state (block property + BetterModel) ----------------

    private Boolean lastActivated = null;
    /**
     * Grace window (ticks) the BetterModel stays SHOWN + looping after the last processing tick, so
     * back-to-back recipes don't despawn/respawn the model between them (no flicker). The model is only
     * closed once the crusher has been idle for this whole window (truly stopped: no items / no rpm).
     */
    private static final int RENDER_GRACE_TICKS = 30;
    /** Ticks remaining in the grace window; >0 means "keep the model shown + animating". */
    private int renderGrace = 0;

    /**
     * Drive the dual-render with HYSTERESIS so the model does not flicker at recipe boundaries.
     * <ul>
     *   <li>processing: refresh the grace window to full, keep the block activated, show the model and
     *       loop the "on" animation (a single continuous LOOP — see {@link BetterModelMachineRenderer}).</li>
     *   <li>just-stopped: while the grace window counts down the model STAYS shown and looping and the
     *       block STAYS activated, so the next recipe reuses the same continuous model.</li>
     *   <li>truly idle (grace elapsed, no items / no rpm): close the model and switch back to the
     *       vanilla {@code crusher_off} model.</li>
     * </ul>
     */
    private void updateActivation(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        boolean processing = isProcessing();
        if (processing)
            renderGrace = RENDER_GRACE_TICKS; // keep alive while running
        else if (renderGrace > 0)
            renderGrace--; // counting down after the last processing tick

        // "shown" = processing OR still inside the grace window -> one continuous looping model.
        boolean shown = processing || renderGrace > 0;
        maybeUpdateActivated(level, pos, state, shown);

        if (shown) {
            net.minecraft.core.Direction facing = getFacing(level);
            float yaw = facing == null ? 0f : facing.toYRot();
            renderer.setLocation(level.getWorld(), pos.getX(), pos.getY(), pos.getZ(), yaw);
            renderer.show();
            renderer.playLoop("on"); // continuous LOOP; playLoop is idempotent so it never restarts
        } else {
            renderer.close(); // fully idle -> vanilla crusher_off model is the only visual
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void maybeUpdateActivated(Level level, BlockPos pos,
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
                    net.momirealms.craftengine.core.block.ImmutableBlockState.with(state, p, (Comparable) val);
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

    /** Close the BetterModel tracker on break so no ghost model is left behind. */
    @Override
    public void dropAllContents(Level level, BlockPos pos) {
        renderer.close();
        super.dropAllContents(level, pos);
    }

    @Override
    public void unregister() {
        renderer.close();
        super.unregister();
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
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize, "Crusher");
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.crusher_title", NamedTextColor.GOLD)));

        // Functional slots — numbers all come from config (no hardcoded constants).
        for (int s : inputSlots)
            l.addSlot(s, MenuSlotType.INPUT);
        for (int s : outputSlots)
            l.addSlot(s, MenuSlotType.OUTPUT);

        // Config-driven buttons (upgrades / overclock). No hardcoded buttons.
        for (MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        // Config-driven bars: cook-progress gauge + binary rpm/power indicator (all by ranges).
        MachineBars.install(l, bars);

        // Reusable recipe-info icon at the configurable info slot (default 4; -1 = none).
        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : 4;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((CrusherBlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    /** Render + wire one config-declared button generically (icon, name/lore, action, lock state). */
    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.GOLD), lore(b.lore));
        }, (m, p) -> {
            CrusherBlockEntity s = (CrusherBlockEntity) m;
            if (s.isButtonLocked(b))
                return; // locked: swallow the click
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_FLUID, DEPLETE_GAS, NONE -> {
                }
            }
        });
    }

    /** Locked iff the button declares {@code locked_when: no_overclock} and no overclock is unlocked. */
    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && curOverclockLimit <= 0;
    }

    /** {@code lang:key} or bare dotted lang key -> translatable; otherwise literal text. Null -> empty. */
    private static Component label(String s, NamedTextColor color) {
        if (s == null)
            return Component.empty();
        String key = s.startsWith("lang:") ? s.substring(5) : s;
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

    /** Recipe-info icon via the shared {@link dev.arubik.craftengine.machine.menu.RecipeInfoIcon}.
     *  Crusher speed is (1+overclock); generation is a deterministic output bonus, so it's passed
     *  as the generationBonus (weights the items/min). */
    private org.bukkit.inventory.ItemStack infoIcon() {
        return dev.arubik.craftengine.machine.menu.RecipeInfoIcon.build(this, getMachineId(),
                (1.0 + overclock), curGeneration);
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
                (m, p) -> ((CrusherBlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), NamedTextColor.RED,
                () -> this.overclock, () -> this.curOverclockLimit,
                (up, c) -> bumpOverclock(up, c),
                p -> openPage(p, 0));
        fillRest(l);
        return l;
    }

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

    // ---------------- RpmConsumer ----------------
    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return inputRpm;
    }
}
