package dev.arubik.craftengine.machine.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
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
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * Gas pump (the {@code cml:gas_pump} block). Same machine pattern as the {@code cml:iron_pump}
 * (fuel-driven, attribute upgrades, player overclock, multi-page menu), but it pumps NITROGEN GAS
 * out of a "nitrogenated cal" VEIN instead of fluid out of a water/lava source.
 *
 * <p>The pump sits ON TOP of a nitrogenated-cal block (its IN face = LOCAL DOWN). On extract it
 * flood-fills the orthogonally-adjacent vein of cal blocks and sums their EXTRACTION POINTS
 * (full = 4, mid = 2, empty = 1). A single pump draws {@code min(8, veinPoints)} points worth of
 * nitrogen per operation — extra vein richness past 8 points is wasted on one pump. A vein allows
 * only ONE active pump by default; a Pressurizer Well raises that limit (see {@link #veinPumpLimit}).
 * The nitrogen is buffered in an internal {@link GasTank} and pushed out the LOCAL UP face into a
 * connected gas carrier.</p>
 */
public class GasPumpBlockEntity extends AbstractMachineBlockEntity {

    public static final int UPGRADE_SLOTS = 9; // container indices 0..8
    private static final int BASE_UNLOCKED = 3;

    /** Hard cap on extraction points a single pump can draw, regardless of vein size. */
    public static final double MAX_POINTS_PER_PUMP = 8.0;
    /** Safety cap on the vein flood-fill (blocks visited). */
    private static final int MAX_VEIN_BLOCKS = 512;
    private static final String GAS_PUMP_ID = "cml:gas_pump";

    // ---- config knobs ----
    private final int capacity;        // internal gas buffer size (mB)
    private final int mbPerPoint;      // mB extracted PER extraction point PER operation
    private final int basePressure;    // base pressure stamped on pushed gas
    private final int baseExtractTickRate; // ticks between operations; overclock shortens it
    private final int[] fuelSlots;

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;

    private static final org.bukkit.inventory.ItemStack FILLER = MenuText.emptyFiller();

    private float overclock = 0f;
    private double curOverclockLimit = 0;
    private double curGeneration = 0;   // capacity headroom
    private double curPressure = 0;      // flat extra pressure
    private int curUnlocked = BASE_UNLOCKED;

    // Last computed vein stats (for the menu readout + extraction).
    private double lastVeinPoints = 0;
    private double lastDrawPoints = 0;
    private GasType lastVeinGas = GasType.EMPTY;
    private boolean lastActiveOwner = true;

    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "gas_pump_overclock",
            PersistentDataType.FLOAT);

    public GasPumpBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new java.util.HashMap<>(), new ArrayList<>(), defaultMenuConfig(),
                4000, 10, 8, 20);
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(9, null, new int[0], new int[0], new int[0], new ArrayList<>(), 4);
    }

    public GasPumpBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, MachineMenuConfig menuConfig,
            int capacity, int mbPerPoint, int pressure, int extractTickRate) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 9;
        this.capacity = capacity;
        this.mbPerPoint = Math.max(1, mbPerPoint);
        this.basePressure = pressure;
        this.baseExtractTickRate = Math.max(1, extractTickRate);
        this.fuelSlots = this.menuConfig.fuelSlots != null ? this.menuConfig.fuelSlots : new int[0];

        setMaxStackSize(64);
        // Accept ANY gas — the vein's gas_provider blocks decide which gas is pumped (data-driven).
        addGasTank(new GasTank("internal", capacity));
        setIOConfiguration(buildIO());
    }

    /**
     * Directional like the iron pump: gas OUTPUT on the LOCAL UP face (a pipe links only there); the cal
     * vein is read off the LOCAL DOWN face. Resolved to world faces via the block's 6-dir facing.
     */
    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        cfg.addOutput(IOType.GAS, RelativeDirection.UP);
        if (fuelSlots.length > 0)
            cfg.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, fuelSlots);
        return cfg;
    }

    // ---------------- effective rates ----------------

    private int effInterval() {
        double f = Math.max(0.05, 1.0 + overclock);
        return Math.max(1, (int) Math.round(baseExtractTickRate / f));
    }

    private int effCapacity() {
        double g = clamp(curGeneration, 0.0, 2.0);
        return (int) Math.round(capacity * (1.0 + g));
    }

    private int effPressure() {
        return Math.max(0, (int) Math.round(basePressure + curPressure));
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---------------- upgrades ----------------

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[UPGRADE_SLOTS];
        for (int i = 0; i < UPGRADE_SLOTS; i++)
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
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(BASE_UNLOCKED, Math.min(UPGRADE_SLOTS, BASE_UNLOCKED + extra));

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

    // ---------------- pump core ----------------

    @Override
    protected boolean requiresFuel() {
        return true;
    }

    private int opCooldown = 0;
    private int upgradeRecomputeCd = 0;

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (active != null)
            active.tick();
        super.tick(level, pos, state);
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;

        if (upgradeRecomputeCd-- <= 0) {
            recomputeUpgrades();
            upgradeRecomputeCd = 10;
        }
        refreshUpgradePageIfNeeded();

        if (requiresRedstone && !isRedstoneEnabled(level))
            return;

        BlockPos pos = getMachinePos();
        // Register this gas network with the hydraulic engine (it expands via BFS to pipes/tanks). The
        // vein intake below STILL runs under the engine (it fills this pump's tank from gas source blocks);
        // only the manual OUT push is handed to the engine.
        try {
            dev.arubik.craftengine.fluid.graph.GasEngine.registerSeed(pos);
        } catch (Throwable ignored) {
        }
        GasTank tank = gasTanks.get(0);
        int cap = effCapacity();
        int pressure = effPressure();

        // World IN (vein) / OUT (pipe) faces from the 6-dir facing — directional like the iron pump.
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

        int ioCd = getOrDefault(GasKeys.GAS_IO_COOLDOWN, 0);
        if (ioCd > 0)
            set(GasKeys.GAS_IO_COOLDOWN, ioCd - 1);

        // PUSH EVERY TICK: the buffer drains into a connected gas carrier above. Under the engine this is
        // the engine's job (it moves pump->pipe->tank), so the manual push is disabled when ENABLED.
        GasStack stored = tank.getGas(level, pos);
        if (stored == null)
            stored = GasStack.EMPTY;
        if (!dev.arubik.craftengine.fluid.graph.GasEngine.ENABLED && ioCd <= 0 && !stored.isEmpty()) {
            BlockPos up = pos.relative(worldUp);
            GasCarrier carrier = GasTransferHelper.getCarrier(level, up).orElse(null);
            if (carrier != null) {
                GasStack out = new GasStack(stored.getType(), stored.getAmount(),
                        Math.max(0, stored.getPressure() - 1));
                int inserted = carrier.insertGas(level, up, out, worldUp.getOpposite());
                if (inserted > 0) {
                    tank.extract(level, pos, inserted, null);
                    set(GasKeys.GAS_IO_COOLDOWN, 2);
                }
            }
        }

        // --- FUEL-DRIVEN EXTRACTION ---
        stored = tank.getGas(level, pos);
        if (stored == null)
            stored = GasStack.EMPTY;
        boolean tankFull = stored.getAmount() >= cap;

        // Source vein: the cal block on the IN face, flood-filled.
        BlockPos inPos = pos.relative(worldDown);
        VeinScan vein = scanVein(level, inPos);
        this.lastVeinPoints = vein.points;
        this.lastVeinGas = vein.gas;
        boolean haveSource = vein.points > 0 && vein.gas != GasType.EMPTY;

        // Vein ownership: only the first N pumps (N = veinPumpLimit) on the vein run.
        this.lastActiveOwner = haveSource && isActivePump(level, vein, pos, worldUp);
        this.lastDrawPoints = (haveSource && lastActiveOwner)
                ? Math.min(MAX_POINTS_PER_PUMP, vein.points) : 0;

        if (tankFull || !haveSource || !lastActiveOwner) {
            isProcessing = false;
            return; // paused: no fuel burn, no progress
        }

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

        if (opCooldown > 0) {
            opCooldown--;
            return;
        }
        opCooldown = effInterval();

        // Stamp pressure on the existing buffer when it differs.
        if (!stored.isEmpty() && stored.getPressure() != pressure) {
            writeBuffer(level, new GasStack(stored.getType(), stored.getAmount(), pressure));
            stored = tank.getGas(level, pos);
            if (stored == null)
                stored = GasStack.EMPTY;
        }

        int free = cap - stored.getAmount();
        if (free <= 0)
            return;
        // A buffer already holding a DIFFERENT gas blocks the new vein's gas (no mixing).
        if (!stored.isEmpty() && stored.getType() != lastVeinGas)
            return;
        int amount = Math.min(free, (int) Math.round(lastDrawPoints * mbPerPoint));
        if (amount <= 0)
            return;
        tank.insert(level, pos, new GasStack(lastVeinGas, amount, pressure));
    }

    /** Overwrite the buffer contents (used to re-stamp pressure). */
    private void writeBuffer(Level level, GasStack s) {
        BlockPos pos = getMachinePos();
        var data = dev.arubik.craftengine.util.CustomBlockData.from(level, pos);
        if (s == null || s.isEmpty())
            data.remove(gasTanks.get(0).getKey());
        else
            data.set(gasTanks.get(0).getKey(), s);
    }

    // ---------------- vein flood-fill ----------------

    /** The {@link GasProviderBehavior} on the block at {@code pos}, or null if it isn't a gas vein node. */
    public static dev.arubik.craftengine.gas.behavior.GasProviderBehavior providerAt(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (s == null || s.behavior() == null)
            return null;
        var b = s.behavior();
        if (b instanceof dev.arubik.craftengine.gas.behavior.GasProviderBehavior g)
            return g;
        return b.getFirst(dev.arubik.craftengine.gas.behavior.GasProviderBehavior.class);
    }

    /** Custom-block id string at a position, or null when it isn't a CraftEngine block. */
    private static String customId(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        return s == null ? null : s.owner().value().id().toString();
    }

    private static final class VeinScan {
        double points;
        GasType gas = GasType.EMPTY;
        final java.util.List<BlockPos> blocks = new ArrayList<>();
    }

    /**
     * BFS the orthogonally-connected vein of {@code gas_provider} blocks SHARING THE SAME gas as the
     * start block; sum their extraction points (data-driven — no hardcoded block ids).
     */
    private VeinScan scanVein(Level level, BlockPos start) {
        VeinScan scan = new VeinScan();
        var startProv = providerAt(level, start);
        if (startProv == null || startProv.extractionPoints() <= 0 || startProv.gasType() == GasType.EMPTY)
            return scan;
        GasType gas = startProv.gasType();
        scan.gas = gas;
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        java.util.HashSet<Long> seen = new java.util.HashSet<>();
        queue.add(start);
        seen.add(start.asLong());
        while (!queue.isEmpty() && scan.blocks.size() < MAX_VEIN_BLOCKS) {
            BlockPos p = queue.poll();
            var prov = providerAt(level, p);
            if (prov == null || prov.gasType() != gas || prov.extractionPoints() <= 0)
                continue;
            scan.points += prov.extractionPoints();
            scan.blocks.add(p);
            for (Direction d : Direction.values()) {
                BlockPos n = p.relative(d);
                if (seen.add(n.asLong())) {
                    var np = providerAt(level, n);
                    if (np != null && np.gasType() == gas && np.extractionPoints() > 0)
                        queue.add(n);
                }
            }
        }
        return scan;
    }

    private static final String WELL_CORE_ID = "cml:pressurizer_well";

    /**
     * Vein pump limit (how many pumps may run on one vein). Default 1. Raised to
     * {@link GasKeys#WELL_PUMP_LIMIT} when an ACTIVE Pressurizer Well core sits directly on a cal block
     * of this vein (the well anchors to the cal beneath its central-bottom core; that core writes the
     * {@link GasKeys#WELL_ACTIVE} flag while it is formed + steam-fed).
     */
    private int veinPumpLimit(Level level, VeinScan vein) {
        for (BlockPos calPos : vein.blocks) {
            BlockPos above = calPos.relative(Direction.UP);
            if (WELL_CORE_ID.equals(customId(level, above))) {
                int active = dev.arubik.craftengine.util.CustomBlockData.from(level, above)
                        .getOrDefault(GasKeys.WELL_ACTIVE, 0);
                if (active == 1)
                    return GasKeys.WELL_PUMP_LIMIT;
            }
        }
        return 1;
    }

    /**
     * True when THIS pump is one of the {@code veinPumpLimit} active pumps on the vein. Every gas pump
     * sitting on top of a vein block is a candidate; they are ranked by block position and only the
     * lowest-ranked {@code limit} pumps run, so a vein deterministically runs exactly one pump (or up
     * to the well-raised limit) with no shared registry.
     */
    private boolean isActivePump(Level level, VeinScan vein, BlockPos selfPos, Direction up) {
        int limit = veinPumpLimit(level, vein);
        long selfKey = selfPos.asLong();
        int rank = 0;
        for (BlockPos calPos : vein.blocks) {
            BlockPos above = calPos.relative(up);
            if (GAS_PUMP_ID.equals(customId(level, above))) {
                if (above.asLong() < selfKey)
                    rank++;
                if (rank >= limit)
                    return false;
            }
        }
        return rank < limit;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null;
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
        return "gas_pump";
    }

    // ---------------- bars ----------------

    private GasStack storedGas() {
        GasStack s = gasTanks.get(0).getGas(getNMSLevel(), getMachinePos());
        return s == null ? GasStack.EMPTY : s;
    }

    @Override
    public double[] barStat(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "internal".equals(id)) {
            GasStack s = storedGas();
            return new double[] { s.isEmpty() ? 0 : s.getAmount(), Math.max(1, effCapacity()) };
        }
        if ("fuel".equals(id)) {
            return new double[] { burnTime, Math.max(1, maxBurnTime) };
        }
        if ("progress".equals(id)) {
            int total = effInterval();
            int done = Math.max(0, total - opCooldown);
            return new double[] { done, Math.max(1, total) };
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "internal".equals(id)) {
            GasStack s = storedGas();
            return s.isEmpty() ? "" : s.getType().name().toLowerCase(java.util.Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    // ---------------- menu ----------------

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
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize, "Gas Pump");
        net.kyori.adventure.text.Component title =
                dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.gas_pump_title", NamedTextColor.AQUA)));

        for (int s : fuelSlots)
            l.addSlot(s, MenuSlotType.FUEL);

        for (MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        MachineBars.install(l, bars);

        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : -1;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((GasPumpBlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    /** Trim trailing ".0" so whole point totals show as "2" not "2.0"; keep one decimal otherwise. */
    private static String fmtPoints(double v) {
        if (v == Math.rint(v))
            return String.valueOf((long) v);
        return String.valueOf(Math.round(v * 100.0) / 100.0);
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        GasStack stored = storedGas();
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(Material.WHITE_STAINED_GLASS);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        var GRAY = NamedTextColor.GRAY;
        var WHITE = NamedTextColor.WHITE;
        var AQUA = NamedTextColor.AQUA;
        net.kyori.adventure.text.Component gasName = stored.isEmpty()
                ? MenuText.tr("polyfill.gas.empty", WHITE)
                : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.gas", AQUA)
                .append(net.kyori.adventure.text.Component.text(": ", GRAY)).append(gasName)));
        meta.lore(java.util.List.of(
                MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY,
                        (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + effCapacity() + " mB", WHITE)),
                MenuText.noI(MenuText.kv("polyfill.attr.extraction_points", GRAY,
                        fmtPoints(lastDrawPoints) + " / " + fmtPoints(lastVeinPoints), WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.pressure", GRAY,
                        String.valueOf(stored.isEmpty() ? effPressure() : stored.getPressure()), WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            GasPumpBlockEntity s = (GasPumpBlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.AQUA), lore(b.lore));
        }, (m, p) -> {
            GasPumpBlockEntity s = (GasPumpBlockEntity) m;
            if (s.isButtonLocked(b))
                return;
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_GAS -> {
                    s.gasTanks.get(0).deplete(s.getNMSLevel(), s.getMachinePos());
                    s.setChanged();
                }
                case DEPLETE_FLUID, NONE -> {
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
                (m, p) -> ((GasPumpBlockEntity) m).openPage(p, 0));
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
        this.upgradeRecomputeCd = 0;
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
