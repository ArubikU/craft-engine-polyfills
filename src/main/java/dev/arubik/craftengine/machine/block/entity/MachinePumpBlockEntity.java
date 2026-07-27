package dev.arubik.craftengine.machine.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import dev.arubik.craftengine.util.NbtType;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.DirectionalIOHelper;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.TypedKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * Full-machine PUMP (the {@code cml:iron_pump} block). Migrated from the simple {@code PumpBehavior}
 * to the Crusher/Vapor-Furnace machine pattern: a config-driven multi-page menu
 * (main fluid bar + Upgrades page + Overclock page), an attribute-based upgrade system (casts unlock
 * slots; iron/gold/diamond upgrades raise capacity / pressure / rate) and a player-tunable overclock
 * that scales the extract/push RATE.
 *
 * <p>The pump consumes NO power and NO fuel: it pulls fluid from the block on its IN face
 * ({@code worldDown}) — cauldron / water source / lava lake / a carrier below — into one internal
 * tank, stamps a configurable PRESSURE on it, and pushes it out its OUT face ({@code worldUp}) into
 * a pipe network. IN/OUT faces are derived from the block's facing, and the pump only accepts pipe
 * connections on those two faces (see {@link #buildIO()} + the RelativeIO config).</p>
 */
public class MachinePumpBlockEntity extends AbstractMachineBlockEntity {

    public static final int UPGRADE_SLOTS = 9; // container indices 0..8
    private static final int BASE_UNLOCKED = 3;
    /** Temporary: log every push attempt to diagnose pump->pipe transfer. */
    public static final boolean DEBUG_PUMP = false;

    // ---- config knobs (set from the behavior yml; sane fast-but-finite defaults) ----
    // capacity:        internal tank size (mB). default 8000 mB = 8 buckets.
    // extract_per_tick: base mB pulled from the source each tick. default 1000 mB/t (water ~instant:
    //                  a full source block is 1000 mB, so ~1 block/tick).
    // push_per_tick:    base mB pushed into the pipe network each tick. default 1000 mB/t.
    // pressure:         base pressure stamped on pushed fluid. default 10 (matches the old iron_pump).
    private final int capacity;
    private final int baseExtractPerTick;
    private final int basePushPerTick;
    private final int basePressure;
    // Base ticks between pump operations (extract+push). Overclock shortens this interval.
    private final int baseExtractTickRate;
    // Fuel slot numbers (config-driven, like the vapor furnace). The pump only runs while it has burn time.
    private final int[] fuelSlots;

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;

    private static final org.bukkit.inventory.ItemStack FILLER = MenuText.emptyFiller();

    // Player-tunable overclock fraction in [-limit, +limit]. Scales the per-tick extract/push RATE.
    private float overclock = 0f;

    // Derived each tick from the installed upgrades + overclock setting.
    private double curOverclockLimit = 0;
    private double curGeneration = 0;   // raises capacity headroom (frac)
    private double curPressure = 0;      // flat extra pressure from upgrades
    private int curUnlocked = BASE_UNLOCKED;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private MachineMenu active;
    private int shownUnlocked = -1;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "pump_overclock",
            NbtType.FLOAT);

    public MachinePumpBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>(), new ArrayList<>(), defaultMenuConfig(),
                8000, 1000, 1000, 10, 10);
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(9, null, new int[0], new int[0], new int[0], new ArrayList<>(), 4);
    }

    public MachinePumpBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, MachineMenuConfig menuConfig,
            int capacity, int extractPerTick, int pushPerTick, int pressure, int extractTickRate) {
        // Container must span the WHOLE menu (upgrades 0..8 + fuel slot + buttons), else the fuel slot
        // (e.g. 13) falls outside the inventory and its item is never stored (vanishes on relog) nor seen
        // by hasFuel(). Size it from the menu config like the crusher/furnace.
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 9;
        this.capacity = capacity;
        this.baseExtractPerTick = extractPerTick;
        this.basePushPerTick = pushPerTick;
        this.basePressure = pressure;
        this.baseExtractTickRate = Math.max(1, extractTickRate);
        this.fuelSlots = this.menuConfig.fuelSlots != null ? this.menuConfig.fuelSlots : new int[0];

        setMaxStackSize(64);

        addFluidTank(new FluidTank("internal", capacity));
        setIOConfiguration(buildIO());
    }

    /**
     * FLUID IO is directional and FACING-relative: input on the LOCAL DOWN face, output on the LOCAL
     * UP face. {@link RelativeIO} is evaluated against the LOCAL direction the pipe-connection /
     * insert-extract code computes via {@code toLocalDirection(worldFace, facing)}, so a pipe only
     * links on the pump's two actual world IN/OUT faces (derived from 6-direction {@code facing}).
     */
    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        cfg.addInput(IOType.FLUID, RelativeDirection.DOWN);
        cfg.addOutput(IOType.FLUID, RelativeDirection.UP);
        // Fuel can be inserted (hopper/funnel) from EVERY face that isn't the fluid IN (DOWN) / OUT (UP):
        // the 4 side faces accept ITEM, which getSlotsForFace/canPlaceItemThroughFace route to the fuel slots.
        if (fuelSlots.length > 0) {
            cfg.addInput(IOType.ITEM, RelativeDirection.FRONT);
            cfg.addInput(IOType.ITEM, RelativeDirection.BACK);
            cfg.addInput(IOType.ITEM, RelativeDirection.LEFT);
            cfg.addInput(IOType.ITEM, RelativeDirection.RIGHT);
            cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, fuelSlots);
        }
        return cfg;
    }

    // ---------------- effective (overclock-scaled) rates + pressure ----------------

    // The amounts moved PER OPERATION are the configured base amounts (overclock no longer scales them —
    // instead it scales how OFTEN an operation runs, via effInterval()).
    private int effExtractPerTick() {
        return Math.max(1, baseExtractPerTick);
    }

    private int effPushPerTick() {
        return Math.max(1, basePushPerTick);
    }

    /**
     * Ticks between pump operations. Overclock shortens the interval so extract AND push happen more
     * often; underclock lengthens it. effInterval = max(1, round(baseInterval / (1 + overclock))).
     */
    private int effInterval() {
        double f = Math.max(0.05, 1.0 + overclock);
        return Math.max(1, (int) Math.round(baseExtractTickRate / f));
    }

    /** Capacity grows modestly with GENERATION upgrades (caps at +200%). */
    private int effCapacity() {
        double g = clamp(curGeneration, 0.0, 2.0);
        return (int) Math.round(capacity * (1.0 + g));
    }

    /**
     * Pressure stamped on pushed fluid. Base from config, raised ONLY by the PRESSURE upgrade attribute
     * (overclock no longer affects pressure — it only affects the RATE via effInterval()). Pipes spend
     * 1 pressure per upward hop, so pressure ≈ the number of blocks the column can climb.
     */
    private int effPressure() {
        return Math.max(0, (int) Math.round(basePressure + curPressure));
    }

    // ---- hydraulic graph hooks: pump OUT face + lift (emf in blocks) ----
    public net.minecraft.core.Direction graphOutFace(Level level) {
        return getFacing(level); // OUT = facing (worldUp); IN = opposite
    }

    public int graphPressure() {
        return effPressure();
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---------------- upgrades (attribute system, like the crusher) ----------------

    /**
     * The pump's machine half, from {@code pumps/*.json}.
     *
     * <p>
     * A pump's world scan stays in Java, but its upgrade grid is ordinary machine
     * surface and now comes from data like every other machine's. Falls back to the
     * historical 9/3 when no definition is present.
     */
    private dev.arubik.craftengine.machine.MachineDefinition machineDefinition() {
        return dev.arubik.craftengine.machine.MachineDefinition.byName(getMachineId());
    }

    private int upgradeSlotCount() {
        var d = machineDefinition();
        return d != null && d.upgrades().size() > 0 ? d.upgrades().size() : UPGRADE_SLOTS;
    }

    private int baseUnlockedCount() {
        var d = machineDefinition();
        return d != null && d.upgrades().size() > 0 ? d.upgrades().baseUnlocked() : BASE_UNLOCKED;
    }

    @Override
    public int[] getUpgradeSlots() {
        int count = upgradeSlotCount();
        int[] s = new int[count];
        for (int i = 0; i < count; i++)
            s[i] = i;
        return s;
    }

    private Key itemId(int slot) {
        net.minecraft.world.item.ItemStack nms = getItem(slot);
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

    @Override
    protected void recomputeUpgrades() {
        // Pass 1: unlocked slot count from EXTRA_SLOTS across ALL installed upgrades.
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < upgradeSlotCount(); i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        int count = upgradeSlotCount();
        int base = baseUnlockedCount();
        this.curUnlocked = Math.max(base, Math.min(count, base + extra));

        // Pass 2: effect attributes only from upgrades in unlocked slots.
        List<Mod> active = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                active.addAll(m);
        }
        var attrs = MachineAttributes.compute(active);
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), 0.0, 2.0);
        this.curPressure = clamp(attrs.getOrDefault(MachineAttributes.PRESSURE, 0.0), 0.0, 256.0);

        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
    }

    // ---------------- pump core (fuel-driven extract/push) ----------------

    @Override
    protected boolean requiresFuel() {
        return true;
    }

    // Counts down ticks until the next pump operation (extract+push). Reset to effInterval() after each op.
    private int opCooldown = 0;
    // Counts down to the next upgrade recompute (upgrades change rarely; no need to recompute per tick).
    private int upgradeRecomputeCd = 0;

    // The pump opens its menu via the `active` field (not the base `this.menu`), so the base tick()
    // never ticks it. Tick `active` here so its dynamic gauges (fuel / progress / fluid) refresh live.
    @Override
    public void tick(Level level, net.minecraft.core.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick();
        super.tick(level, pos, state);
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;
        try {
            dev.arubik.craftengine.fluid.graph.FluidEngine.registerSeed(getMachinePos());
        } catch (Throwable ignored) {
        }
        if (dev.arubik.craftengine.fluid.graph.FluidEngine.ENABLED) {
            // The engine distributes between CE blocks, but world fluids (a lava/water source on the IN
            // face) are OUTSIDE the graph. The pump still INJECTS them into its own tank here; the engine
            // then moves that into the network.
            try {
                pumpWorldIntake(level);
            } catch (Throwable ignored) {
            }
            // If the OUT face has NO carrier (no pipe/tank), the engine can't move fluid out — so the pump
            // EXPELS to the world: place a source/material block, or award XP orbs for experience.
            try {
                pumpWorldOutput(level);
            } catch (Throwable ignored) {
            }
            return;
        }

        // Upgrades change rarely (player edits the slots): recompute on a 10-tick cadence instead of
        // every tick (avoids the per-tick slot reads / item-id round-trips). bumpOverclock recomputes
        // immediately, so overclock still applies instantly.
        if (upgradeRecomputeCd-- <= 0) {
            recomputeUpgrades();
            upgradeRecomputeCd = 10;
        }
        refreshUpgradePageIfNeeded();
        // (storedFluid() below is null-safe; the raw tank value is null until first filled.)

        if (requiresRedstone && !isRedstoneEnabled(level))
            return;

        BlockPos pos = getMachinePos();
        FluidTank tank = fluidTanks.get(0);
        FluidStack stored = storedFluid();

        int cap = effCapacity();
        int pressure = effPressure();

        // (Re)stamp the configured pressure onto stored fluid so it can climb pipes — but only when it
        // actually differs, to avoid a PDC write every tick when pressure is stable.
        if (!stored.isEmpty() && stored.getPressure() != pressure) {
            writeTank(level, new FluidStack(stored.getType(), stored.getAmount(), pressure));
        }

        // World IN (DOWN) / OUT (UP) faces from facing — same derivation as the old pump.
        Direction facing = getFacing(level);
        ConnectableBlockBehavior cbb = getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldDown = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN,
                        DirectionalIOHelper.toHorizontalDirection(facing));
        Direction worldUp = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP,
                        DirectionalIOHelper.toHorizontalDirection(facing));

        // Cooldowns (block collect / carrier IO) keep extraction from spamming events.
        int blockCd = getOrDefault(FluidKeys.FLUID_BLOCK_COOLDOWN, 0);
        int ioCd = getOrDefault(FluidKeys.FLUID_IO_COOLDOWN, 0);
        if (blockCd > 0)
            set(FluidKeys.FLUID_BLOCK_COOLDOWN, blockCd - 1);
        if (ioCd > 0)
            set(FluidKeys.FLUID_IO_COOLDOWN, ioCd - 1);

        // PUSH EVERY TICK so the internal tank acts as a BUFFER that empties: pushing continuously
        // outpaces the once-per-interval extract, so a connected pipe/tank eventually fully drains it.
        stored = storedFluid();
        if (ioCd <= 0 && !stored.isEmpty()) {
            BlockPos up = pos.relative(worldUp);
            // Detect the carrier the way FluidTransferHelper does: the block's behavior is a COMPOSITE,
            // so `instanceof FluidCarrier` is false — must extract it via getFirst(FluidCarrier.class).
            dev.arubik.craftengine.fluid.behavior.FluidCarrier carrier =
                    FluidTransferHelper.getCarrier(level, up).orElse(null);
            if (DEBUG_PUMP)
                System.out.println("[PumpPush] pos=" + pos.toShortString() + " facing=" + facing
                        + " worldUp(OUT)=" + worldUp + " target=" + up.toShortString()
                        + " targetBlock=" + level.getBlockState(up).getBlock()
                        + " carrier=" + (carrier == null ? "NULL" : carrier.getClass().getSimpleName())
                        + " stored=" + stored.getAmount() + "mB");
            if (carrier != null) {
                // Push the WHOLE tank into the pipe (the pipe's free capacity caps how much it takes).
                // The tank is a BUFFER: when connected it drains, not just passes the per-op extraction.
                int send = stored.getAmount();
                // Gravity: pushing DOWN needs no pressure — stamp 0 so the receiving pipe enters its
                // gravity branch (pull-from-above + push-down) instead of the pressure branch that would
                // try to shove the fluid right back UP into this pump (the recirculation that pinned the
                // pump full / the pipe empty). Only UPWARD output keeps pressure (fluid climbs on pressure).
                int outPressure = (worldUp == Direction.DOWN) ? 0 : Math.max(0, stored.getPressure() - 1);
                FluidStack out = new FluidStack(stored.getType(), send, outPressure);
                int inserted = carrier.insertFluid(level, up, out, worldUp.getOpposite());
                if (DEBUG_PUMP)
                    System.out.println("[PumpPush]   -> insertFluid side=" + worldUp.getOpposite()
                            + " sent=" + send + " inserted=" + inserted);
                if (inserted > 0) {
                    int left = stored.getAmount() - inserted;
                    writeTank(level, left <= 0 ? FluidStack.EMPTY
                            : new FluidStack(stored.getType(), left, stored.getPressure()));
                    if (DEBUG_PUMP)
                        System.out.println("[PumpPush]   wrote left=" + left + " reread="
                                + storedFluid().getAmount() + "mB ioDelay="
                                + FluidType.carrierIODelay(stored.getType()));
                    int delay = FluidType.carrierIODelay(stored.getType());
                    if (delay > 1)
                        set(FluidKeys.FLUID_IO_COOLDOWN, delay);
                }
            } else if (stored.getType() == FluidType.EXPERIENCE) {
                // XP isn't a placeable world fluid — emit it as experience orbs into the OUT-face air.
                if (level.getBlockState(up).isAir()) {
                    int amount = stored.getAmount();
                    if (amount > 0) {
                        // Same XP<->orb convention as TankBlockBehavior: ~7 mB per orb, spawned in chunks.
                        int orbs = (int) Math.ceil(amount / 7.0);
                        while (orbs > 0) {
                            int toSpawn = Math.min(orbs, 10);
                            orbs -= toSpawn;
                            level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                                    level, up.getX() + 0.5, up.getY() + 0.5, up.getZ() + 0.5, toSpawn));
                        }
                        writeTank(level, FluidStack.EMPTY);
                    }
                }
            } else if (stored.getAmount() >= stored.getType().mbPerFullBlock()) {
                // No carrier above: PLACE the fluid as a world block (like the old pump). Needs a full
                // block's worth; cauldrons/water/lava/slime/honey/powder_snow are placeable.
                int full = stored.getType().mbPerFullBlock();
                FluidStack one = new FluidStack(stored.getType(), full, stored.getPressure());
                if (dev.arubik.craftengine.fluid.FluidPlacer.place(one, up, level)) {
                    int left = stored.getAmount() - full;
                    writeTank(level, left <= 0 ? FluidStack.EMPTY
                            : new FluidStack(stored.getType(), left, stored.getPressure()));
                }
            }
        }

        // --- FUEL-DRIVEN EXTRACTION (the push above is free and runs regardless) ---
        // Pause when there's no extraction work: the tank is FULL, or there's nothing pumpable below.
        // While paused the pump burns NO fuel and makes NO progress (it would be pointless).
        stored = storedFluid();
        boolean tankFull = stored.getAmount() >= cap;
        BlockPos inPos = pos.relative(worldDown);
        FluidType belowType = FluidType.getFluidTypeAt(inPos, level);
        net.minecraft.world.level.block.state.BlockState belowState = level.getBlockState(inPos);
        boolean pumpableBelow = belowType != FluidType.EMPTY
                || belowState.is(net.minecraft.world.level.block.Blocks.WATER_CAULDRON)
                || belowState.is(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON)
                || FluidTransferHelper.getCarrier(level, inPos).isPresent();
        if (tankFull || !pumpableBelow) {
            isProcessing = false;
            return; // paused: no fuel burn, no progress
        }

        // Burn the current fuel charge; when it runs out, consume one fuel item, else idle (no work).
        if (burnTime > 0)
            burnTime--;
        if (burnTime <= 0) {
            if (hasFuel(level)) {
                consumeFuel(level);
                setChanged();
            } else {
                isProcessing = false;
                return;
            }
        }
        isProcessing = true;

        // EXTRACT only once per (overclock-shortened) interval. Refilling slower than the per-tick push
        // means the buffer stays low / empties when an output is connected.
        if (opCooldown > 0) {
            opCooldown--;
            return;
        }
        opCooldown = effInterval();
        stored = storedFluid();
        if (blockCd <= 0 && stored.getAmount() < cap) {
            BlockPos target = pos.relative(worldDown);
            FluidType base = FluidType.getFluidTypeAt(target, level);
            int free = cap - (stored.isEmpty() ? 0 : stored.getAmount());
            // collectAt drains a SOURCE block only if maxMb >= its full-block size (1000 for water/lava),
            // so always request at least a full block when there's room — else sources are never pumped.
            int fullBlock = base != null && base != FluidType.EMPTY ? base.mbPerFullBlock() : 1000;
            int extract = Math.min(free, Math.max(effExtractPerTick(), fullBlock));
            net.minecraft.world.level.block.state.BlockState tb = level.getBlockState(target);
            boolean isCauldron = tb.is(net.minecraft.world.level.block.Blocks.WATER_CAULDRON)
                    || tb.is(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON);
            FluidStack collected = (base == FluidType.LAVA && !isCauldron)
                    ? FluidType.collectArea(target, level, 32, extract, stored.getType())
                    : FluidType.collectAt(target, level, extract, stored.getType());
            if (DEBUG_PUMP)
                System.out.println("[PumpExtract] from=" + target.toShortString() + " base=" + base
                        + " before=" + stored.getAmount() + " collected=" + collected.getAmount()
                        + " interval=" + effInterval());
            if (!collected.isEmpty() && (stored.isEmpty() || stored.getType() == collected.getType())) {
                int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + collected.getAmount();
                writeTank(level, new FluidStack(collected.getType(), Math.min(cap, newAmt), pressure));
                int delay = FluidType.blockCollectDelay(collected.getType());
                if (delay > 1)
                    set(FluidKeys.FLUID_BLOCK_COOLDOWN, delay);
            } else if (collected.isEmpty()) {
                // Source is a CE carrier (tank/pipe), not a world fluid. FluidTransferHelper.pull goes
                // through transfer() which needs BOTH ends to be FluidCarriers — the pump is a machine,
                // not a carrier, so it always failed. Pull from the carrier straight into our tank.
                dev.arubik.craftengine.fluid.behavior.FluidCarrier src =
                        FluidTransferHelper.getCarrier(level, target).orElse(null);
                if (src != null) {
                    FluidStack srcStored = src.getStored(level, target);
                    int free2 = cap - (stored.isEmpty() ? 0 : stored.getAmount());
                    // Only pull when types are compatible — else extractFluid would remove fluid we can't
                    // store and it would vanish.
                    if (!srcStored.isEmpty() && free2 > 0
                            && (stored.isEmpty() || stored.getType() == srcStored.getType())) {
                        final FluidStack[] got = { null };
                        src.extractFluid(level, target, Math.min(extract, free2), f -> got[0] = f,
                                worldDown.getOpposite());
                        if (got[0] != null && !got[0].isEmpty()) {
                            int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + got[0].getAmount();
                            writeTank(level, new FluidStack(got[0].getType(), Math.min(cap, newAmt), pressure));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null; // manual processing
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
    }

    @Override
    protected String getMachineId() {
        return "machine_pump";
    }

    /**
     * Inject WORLD fluid on the IN face into the pump's tank (the engine handles CE-block transport, but
     * world sources are outside the graph). Lava is collected over a radius and the drained blocks become
     * stone (anti-lag); water/cauldrons via the point collector. Fuel-driven, capped by tank room.
     */
    private void pumpWorldIntake(Level level) {
        BlockPos pos = getMachinePos();
        Direction facing = getFacing(level);
        ConnectableBlockBehavior cbb = getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldDown = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN,
                        DirectionalIOHelper.toHorizontalDirection(facing));
        int cap = effCapacity();
        FluidStack stored = storedFluid();
        if (!stored.isEmpty() && stored.getAmount() >= cap)
            return;
        BlockPos target = pos.relative(worldDown);
        FluidType base = FluidType.getFluidTypeAt(target, level);
        net.minecraft.world.level.block.state.BlockState tb = level.getBlockState(target);
        boolean cauldron = tb.is(net.minecraft.world.level.block.Blocks.WATER_CAULDRON)
                || tb.is(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON);
        if (dev.arubik.craftengine.fluid.graph.FluidEngine.DEBUG)
            System.out.println("[PumpIntake] worldDown(IN)=" + worldDown + " target=" + target.toShortString()
                    + " base=" + base + " block=" + tb.getBlock() + " cauldron=" + cauldron + " stored="
                    + storedFluid().getAmount() + " burnTime=" + burnTime + " hasFuel=" + hasFuel(level));
        if (base == FluidType.EMPTY && !cauldron)
            return; // IN is a CE carrier (engine handles it) or empty
        // Fuel-driven, like the old pump.
        if (burnTime <= 0) {
            if (hasFuel(level)) {
                consumeFuel(level);
                setChanged();
            } else {
                return;
            }
        }
        burnTime--;
        int free = cap - (stored.isEmpty() ? 0 : stored.getAmount());
        int fullBlock = base != FluidType.EMPTY ? base.mbPerFullBlock() : 1000;
        int extract = Math.min(free, Math.max(effExtractPerTick(), fullBlock));
        // When the tank is empty, tell the collector WHAT to pull (the world block's type) — passing the
        // empty tank's type made collectArea/collectAt find nothing (the "lava not pumped" bug).
        // 1:1 with the old working pump: lava sweeps the area (SOURCE blocks only, replaced with air),
        // everything else collects the single IN block. collectArea now includes the center, so a single
        // lava source directly in front IS collected. Pass stored.getType() (collectArea maps EMPTY->base).
        // radius 1 (3x3x3 = 27 = the collector's per-tick iteration cap): a larger radius starts the scan
        // at the far -radius corner and the 27-iter cap means it never reaches the center block. r=1 keeps
        // the center (the source in front) in range; the pool drains outward over successive ticks.
        FluidStack collected = (base == FluidType.LAVA && !cauldron)
                ? FluidType.collectArea(target, level, 1, extract, stored.getType())
                : FluidType.collectAt(target, level, extract, stored.getType());
        if (dev.arubik.craftengine.fluid.graph.FluidEngine.DEBUG)
            System.out.println("[PumpIntake] collected=" + (collected.isEmpty() ? "0" : collected.getType() + ":"
                    + collected.getAmount()) + " extract=" + extract + " cap=" + cap
                    + " srcAtTarget=" + level.getFluidState(target).isSource());
        if (!collected.isEmpty() && (stored.isEmpty() || stored.getType() == collected.getType())) {
            int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + collected.getAmount();
            writeTank(level, new FluidStack(collected.getType(), Math.min(cap, newAmt), effPressure()));
        }
    }

    /**
     * Expel the internal tank to the WORLD on the OUT face when there's no carrier to receive it. Fluids
     * and material types place a block (into air only); experience is awarded as XP orbs. Bounded per tick.
     */
    private void pumpWorldOutput(Level level) {
        FluidStack stored = storedFluid();
        if (stored.isEmpty())
            return;
        BlockPos pos = getMachinePos();
        Direction facing = getFacing(level);
        ConnectableBlockBehavior cbb = getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldUp = type == DirectionType.FULL
                ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing)
                : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP,
                        DirectionalIOHelper.toHorizontalDirection(facing));
        BlockPos out = pos.relative(worldUp);
        // OUT neighbour is a CE carrier (pipe/tank) -> the engine moves fluid there; don't dump to world.
        if (dev.arubik.craftengine.fluid.FluidTransferHelper.getCarrier(level, out).isPresent())
            return;
        FluidType t = stored.getType();
        if (t == FluidType.EXPERIENCE) {
            // 1:1 with the old pump: XP isn't a placeable world fluid — emit as orbs (~7 mB per orb).
            if (!level.getBlockState(out).isAir())
                return;
            int amount = stored.getAmount();
            if (amount <= 0)
                return;
            int orbs = (int) Math.ceil(amount / 7.0);
            while (orbs > 0) {
                int toSpawn = Math.min(orbs, 10);
                orbs -= toSpawn;
                level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                        level, out.getX() + 0.5, out.getY() + 0.5, out.getZ() + 0.5, toSpawn));
            }
            writeTank(level, FluidStack.EMPTY);
            return;
        }
        // Placeable fluids/materials: need a full block's worth; FluidPlacer handles the block kind.
        int full = Math.max(1, t.mbPerFullBlock());
        if (stored.getAmount() < full)
            return;
        FluidStack one = new FluidStack(t, full, stored.getPressure());
        if (dev.arubik.craftengine.fluid.FluidPlacer.place(one, out, level)) {
            int left = stored.getAmount() - full;
            writeTank(level, left <= 0 ? FluidStack.EMPTY : new FluidStack(t, left, stored.getPressure()));
        }
    }

    // ---------------- bars (main-page fluid readout) ----------------

    /**
     * Stored fluid read from the SINGLE shared store ({@link FluidTank#getFluid}, the block entity's own
     * data), the SAME place the pipes/carriers read/write.
     */
    private FluidStack storedFluid() {
        FluidStack s = fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos());
        return s == null ? FluidStack.EMPTY : s;
    }

    /**
     * Disable the generic all-faces auto-pull for the pump. The pump does its OWN directional intake
     * (extract on the IN face in the pump core). The generic puller checks {@code acceptsInput} with the
     * ITEM local direction, which mis-maps the fluid faces and sucks fluid back out of the OUT neighbour
     * (the tank we just pushed into) — a pump<->tank recirculation loop.
     */
    @Override
    protected void pullFromInputFaces(Level level) {
        // no-op: pump handles its own fluid intake directionally
    }


    /** Write the internal tank to the shared block-entity store (same store the pipes use). */
    private void writeTank(Level level, FluidStack s) {
        net.minecraft.core.BlockPos pos = getMachinePos();
        dev.arubik.craftengine.block.entity.PersistentBlockEntity.executeAt(level, pos, be -> {
            if (s == null || s.isEmpty())
                be.remove(fluidTanks.get(0).getKey());
            else
                be.set(fluidTanks.get(0).getKey(), s);
        });
    }

    @Override
    public double[] barStat(String id) {
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = storedFluid();
            return new double[] { s.isEmpty() ? 0 : s.getAmount(), Math.max(1, effCapacity()) };
        }
        if ("fuel".equals(id)) {
            return new double[] { burnTime, Math.max(1, maxBurnTime) }; // remaining burn of current fuel
        }
        if ("progress".equals(id)) {
            // Fills as opCooldown counts down toward the next extract/push operation.
            int total = effInterval();
            int done = Math.max(0, total - opCooldown);
            return new double[] { done, Math.max(1, total) };
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = storedFluid();
            // Short enum name (water/lava/experience) to match the bar states' `type:` tags — NOT
            // toString(), which returns the translation key and never matched.
            return s.isEmpty() ? "" : s.getType().name().toLowerCase(java.util.Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    @Override
    public java.util.Map<String, String> barPlaceholders(String id) {
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = storedFluid();
            java.util.Map<String, String> m = new java.util.HashMap<>();
            m.put("pressure", String.valueOf(s.isEmpty() ? effPressure() : s.getPressure()));
            return m;
        }
        return super.barPlaceholders(id);
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
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize, "Pump");
        net.kyori.adventure.text.Component title =
                dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.pump_title", NamedTextColor.AQUA)));

        // Fuel slot(s) — players load combustible fuel here; the pump only runs while it has burn time.
        for (int s : fuelSlots)
            l.addSlot(s, MenuSlotType.FUEL);

        for (MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        MachineBars.install(l, bars);

        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : -1;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((MachinePumpBlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    /** Status icon: shows the stored fluid, amount and current pressure. */
    private org.bukkit.inventory.ItemStack infoIcon() {
        FluidStack stored = storedFluid();
        Material material = Material.BUCKET;
        if (!stored.isEmpty()) {
            if (stored.getType() == FluidType.LAVA)
                material = Material.LAVA_BUCKET;
            else if (stored.getType() == FluidType.WATER)
                material = Material.WATER_BUCKET;
        }
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        var GRAY = NamedTextColor.GRAY;
        var WHITE = NamedTextColor.WHITE;
        var AQUA = NamedTextColor.AQUA;
        net.kyori.adventure.text.Component fluidName = stored.isEmpty()
                ? MenuText.tr("polyfill.liquid.empty", WHITE)
                : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.fluid", AQUA)
                .append(net.kyori.adventure.text.Component.text(": ", GRAY)).append(fluidName)));
        meta.lore(java.util.List.of(
                MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY,
                        (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + effCapacity() + " mB", WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.pressure", GRAY,
                        String.valueOf(stored.isEmpty() ? effPressure() : stored.getPressure()), WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            MachinePumpBlockEntity s = (MachinePumpBlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.AQUA), lore(b.lore));
        }, (m, p) -> {
            MachinePumpBlockEntity s = (MachinePumpBlockEntity) m;
            if (s.isButtonLocked(b))
                return;
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_FLUID -> {
                    // Clear the shared block-entity fluid store (same one pipes/carriers use).
                    s.writeTank(s.getNMSLevel(), FluidStack.EMPTY);
                    s.setChanged();
                }
                case DEPLETE_GAS, NONE -> {
                }
            }
        });
    }

    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && curOverclockLimit <= 0;
    }

    private static net.kyori.adventure.text.Component label(String s, NamedTextColor color) {
        if (s == null)
            return net.kyori.adventure.text.Component.empty();
        String key = s.startsWith("lang:") ? s.substring(5) : s;
        if (key.contains(".") && !key.contains(" "))
            return MenuText.tr(key, color);
        return MenuText.lit(s, color);
    }

    private static net.kyori.adventure.text.Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty())
            return new net.kyori.adventure.text.Component[0];
        net.kyori.adventure.text.Component[] out = new net.kyori.adventure.text.Component[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            out[i] = label(lines.get(i), NamedTextColor.GRAY);
        return out;
    }

    private static Key parseKey(String spec) {
        if (spec == null)
            return Key.of("cml", "gui_empty");
        int i = spec.indexOf(':');
        return i < 0 ? Key.of("cml", spec) : Key.of(spec.substring(0, i), spec.substring(i + 1));
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = curUnlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        net.kyori.adventure.text.Component title =
                dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "upgrade");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.AQUA)));
        for (int i = 0; i < upgradeSlotCount(); i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(
                        MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(),
                (m, p) -> ((MachinePumpBlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), NamedTextColor.AQUA,
                () -> this.overclock, () -> this.curOverclockLimit,
                (up, c) -> bumpOverclock(up, c),
                p -> openPage(p, 0));
        fillRest(l);
        return l;
    }

    public void bumpOverclock(boolean up, ClickType c) {
        float delta = dev.arubik.craftengine.machine.menu.OverclockMenu.step(c);
        this.overclock += up ? delta : -delta;
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
        this.upgradeRecomputeCd = 0; // apply the new overclock on the next tick, not after the cadence
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
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = getOrDefault(KEY_OC, 0f);
    }
}
