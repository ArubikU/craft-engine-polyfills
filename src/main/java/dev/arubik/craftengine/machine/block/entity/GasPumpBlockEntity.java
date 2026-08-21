/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.graph.GasEngine;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.gas.behavior.GasProviderBehavior;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.DirectionalIOHelper;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GasPumpBlockEntity
extends AbstractMachineBlockEntity {
    public static final int UPGRADE_SLOTS = 9;
    private static final int BASE_UNLOCKED = 3;
    public static final double MAX_POINTS_PER_PUMP = 8.0;
    private static final int MAX_VEIN_BLOCKS = 512;
    private static final String GAS_PUMP_ID = "cml:gas_pump";
    private final int capacity;
    private final int mbPerPoint;
    private final int basePressure;
    private final int baseExtractTickRate;
    private final int[] fuelSlots;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;
    private static final ItemStack FILLER = MenuText.emptyFiller();
    private float overclock = 0.0f;
    private double curOverclockLimit = 0.0;
    private double curGeneration = 0.0;
    private double curPressure = 0.0;
    private int curUnlocked = 3;
    private double lastVeinPoints = 0.0;
    private double lastDrawPoints = 0.0;
    private GasType lastVeinGas = GasType.EMPTY;
    private boolean lastActiveOwner = true;
    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;
    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "gas_pump_overclock", NbtType.FLOAT);
    private int opCooldown = 0;
    private int upgradeRecomputeCd = 0;
    private static final String WELL_CORE_ID = "cml:pressurizer_well";

    public GasPumpBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new HashMap<Key, List<MachineAttributes.Mod>>(), new ArrayList<MachineBar>(), GasPumpBlockEntity.defaultMenuConfig(), 4000, 10, 8, 20);
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(9, null, new int[0], new int[0], new int[0], new ArrayList<MachineMenuConfig.Button>(), 4);
    }

    public GasPumpBlockEntity(BlockEntity blockEntity, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig, int capacity, int mbPerPoint, int pressure, int extractTickRate) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.bars = bars == null ? new ArrayList() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : GasPumpBlockEntity.defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 9;
        this.capacity = capacity;
        this.mbPerPoint = Math.max(1, mbPerPoint);
        this.basePressure = pressure;
        this.baseExtractTickRate = Math.max(1, extractTickRate);
        this.fuelSlots = this.menuConfig.fuelSlots != null ? this.menuConfig.fuelSlots : new int[]{};
        this.setMaxStackSize(64);
        this.addGasTank(new GasTank("internal", capacity));
        this.setIOConfiguration(this.buildIO());
    }

    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        cfg.addOutput(IOConfiguration.IOType.GAS, RelativeDirection.UP);
        if (this.fuelSlots.length > 0) {
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.FRONT);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.BACK);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.LEFT);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.RIGHT);
            cfg.setSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL, this.fuelSlots);
        }
        return cfg;
    }

    private int effInterval() {
        double f = Math.max(0.05, 1.0 + (double)this.overclock);
        return Math.max(1, (int)Math.round((double)this.baseExtractTickRate / f));
    }

    private int effCapacity() {
        double g = GasPumpBlockEntity.clamp(this.curGeneration, 0.0, 2.0);
        return (int)Math.round((double)this.capacity * (1.0 + g));
    }

    private int effPressure() {
        return Math.max(0, (int)Math.round((double)this.basePressure + this.curPressure));
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private MachineDefinition machineDefinition() {
        return MachineDefinition.byName(this.getMachineId());
    }

    private int upgradeSlotCount() {
        MachineDefinition d = this.machineDefinition();
        return d != null && d.upgrades().size() > 0 ? d.upgrades().size() : 9;
    }

    private int baseUnlockedCount() {
        MachineDefinition d = this.machineDefinition();
        return d != null && d.upgrades().size() > 0 ? d.upgrades().baseUnlocked() : 3;
    }

    @Override
    public int[] getUpgradeSlots() {
        int count = this.upgradeSlotCount();
        int[] s = new int[count];
        for (int i = 0; i < count; ++i) {
            s[i] = i;
        }
        return s;
    }

    private Key itemId(int slot) {
        net.minecraft.world.item.ItemStack nms = this.getItem(slot);
        if (nms == null || nms.isEmpty()) {
            return null;
        }
        ItemStack b = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
        Key custom = CraftEngineItems.getCustomItemId((ItemStack)b);
        if (custom != null) {
            return custom;
        }
        NamespacedKey nk = b.getType().getKey();
        return Key.of((String)nk.getNamespace(), (String)nk.getKey());
    }

    private List<MachineAttributes.Mod> modsOf(int slot) {
        Key id = this.itemId(slot);
        return id == null ? null : this.upgradeDefs.get(id);
    }

    @Override
    protected void recomputeUpgrades() {
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(i);
            if (m == null) continue;
            all.addAll(m);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        int count = this.upgradeSlotCount();
        int base = this.baseUnlockedCount();
        this.curUnlocked = Math.max(base, Math.min(count, base + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(i);
            if (m == null) continue;
            active.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        this.curOverclockLimit = GasPumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curGeneration = GasPumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), 0.0, 2.0);
        this.curPressure = GasPumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.PRESSURE, 0.0), 0.0, 256.0);
        this.overclock = (float)GasPumpBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
    }

    @Override
    protected boolean requiresFuel() {
        return true;
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.active != null) {
            this.active.tick();
        }
        super.tick(level, pos, state);
    }

    @Override
    protected void processTick(Level level) {
        int free;
        GasStack out;
        int inserted;
        BlockPos up;
        GasCarrier carrier;
        GasStack stored;
        if (level.isClientSide()) {
            return;
        }
        if (this.upgradeRecomputeCd-- <= 0) {
            this.recomputeUpgrades();
            this.upgradeRecomputeCd = 10;
        }
        this.refreshUpgradePageIfNeeded();
        if (this.requiresRedstone && !this.isRedstoneEnabled(level)) {
            return;
        }
        BlockPos pos = this.getMachinePos();
        try {
            GasEngine.registerSeed(pos);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        GasTank tank = (GasTank)this.gasTanks.get(0);
        int cap = this.effCapacity();
        int pressure = this.effPressure();
        Direction facing = this.getFacing(level);
        ConnectableBlockBehavior cbb = this.getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldDown = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN, DirectionalIOHelper.toHorizontalDirection(facing));
        Direction worldUp = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP, DirectionalIOHelper.toHorizontalDirection(facing));
        int ioCd = this.getOrDefault(GasKeys.GAS_IO_COOLDOWN, 0);
        if (ioCd > 0) {
            this.set(GasKeys.GAS_IO_COOLDOWN, ioCd - 1);
        }
        if ((stored = tank.getGas(level, pos)) == null) {
            stored = GasStack.EMPTY;
        }
        if (!GasEngine.ENABLED && ioCd <= 0 && !stored.isEmpty() && (carrier = (GasCarrier)GasTransferHelper.getCarrier(level, up = pos.relative(worldUp)).orElse(null)) != null && (inserted = carrier.insertGas(level, up, out = new GasStack(stored.getType(), stored.getAmount(), Math.max(0, stored.getPressure() - 1)), worldUp.getOpposite())) > 0) {
            tank.extract(level, pos, inserted, null);
            this.set(GasKeys.GAS_IO_COOLDOWN, 2);
        }
        if ((stored = tank.getGas(level, pos)) == null) {
            stored = GasStack.EMPTY;
        }
        boolean tankFull = stored.getAmount() >= cap;
        BlockPos inPos = pos.relative(worldDown);
        VeinScan vein = this.scanVein(level, inPos);
        this.lastVeinPoints = vein.points;
        this.lastVeinGas = vein.gas;
        boolean haveSource = vein.points > 0.0 && vein.gas != GasType.EMPTY;
        this.lastActiveOwner = haveSource && this.isActivePump(level, vein, pos, worldUp);
        double d = this.lastDrawPoints = haveSource && this.lastActiveOwner ? Math.min(8.0, vein.points) : 0.0;
        if (tankFull || !haveSource || !this.lastActiveOwner) {
            this.isProcessing = false;
            return;
        }
        if (this.burnTime > 0) {
            --this.burnTime;
        }
        if (this.burnTime <= 0) {
            if (this.hasFuel(level)) {
                this.consumeFuel(level);
                this.setChanged();
            } else {
                this.isProcessing = false;
                return;
            }
        }
        this.isProcessing = true;
        if (this.opCooldown > 0) {
            --this.opCooldown;
            return;
        }
        this.opCooldown = this.effInterval();
        if (!stored.isEmpty() && stored.getPressure() != pressure) {
            this.writeBuffer(level, new GasStack(stored.getType(), stored.getAmount(), pressure));
            stored = tank.getGas(level, pos);
            if (stored == null) {
                stored = GasStack.EMPTY;
            }
        }
        if ((free = cap - stored.getAmount()) <= 0) {
            return;
        }
        if (!stored.isEmpty() && stored.getType() != this.lastVeinGas) {
            return;
        }
        int amount = Math.min(free, (int)Math.round(this.lastDrawPoints * (double)this.mbPerPoint));
        if (amount <= 0) {
            return;
        }
        tank.insert(level, pos, new GasStack(this.lastVeinGas, amount, pressure));
    }

    private void writeBuffer(Level level, GasStack s) {
        BlockPos pos = this.getMachinePos();
        PersistentBlockEntity.executeAt(level, pos, be -> {
            if (s == null || s.isEmpty()) {
                be.remove(((GasTank)this.gasTanks.get(0)).getKey());
            } else {
                be.set(((GasTank)this.gasTanks.get(0)).getKey(), s);
            }
        });
    }

    public static GasProviderBehavior providerAt(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (s == null || s.behavior() == null) {
            return null;
        }
        BlockBehavior b = s.behavior();
        if (b instanceof GasProviderBehavior) {
            GasProviderBehavior g = (GasProviderBehavior)b;
            return g;
        }
        return (GasProviderBehavior)(b.getFirst(GasProviderBehavior.class));
    }

    private static String customId(Level level, BlockPos pos) {
        ImmutableBlockState s = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        return s == null ? null : ((BlockDefinition)s.owner().value()).id().toString();
    }

    private VeinScan scanVein(Level level, BlockPos start) {
        GasType gas;
        VeinScan scan = new VeinScan();
        GasProviderBehavior startProv = GasPumpBlockEntity.providerAt(level, start);
        if (startProv == null || startProv.extractionPoints() <= 0.0 || startProv.gasType() == GasType.EMPTY) {
            return scan;
        }
        scan.gas = gas = startProv.gasType();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        HashSet<Long> seen = new HashSet<Long>();
        queue.add(start);
        seen.add(start.asLong());
        while (!queue.isEmpty() && scan.blocks.size() < 512) {
            BlockPos p = (BlockPos)queue.poll();
            GasProviderBehavior prov = GasPumpBlockEntity.providerAt(level, p);
            if (prov == null || prov.gasType() != gas || prov.extractionPoints() <= 0.0) continue;
            scan.points += prov.extractionPoints();
            scan.blocks.add(p);
            for (Direction d : Direction.values()) {
                GasProviderBehavior np;
                BlockPos n = p.relative(d);
                if (!seen.add(n.asLong()) || (np = GasPumpBlockEntity.providerAt(level, n)) == null || np.gasType() != gas || !(np.extractionPoints() > 0.0)) continue;
                queue.add(n);
            }
        }
        return scan;
    }

    private int veinPumpLimit(Level level, VeinScan vein) {
        for (BlockPos calPos : vein.blocks) {
            BlockPos above = calPos.relative(Direction.UP);
            if (!WELL_CORE_ID.equals(GasPumpBlockEntity.customId(level, above))) continue;
            PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, above);
            TypedKey wellFlag = TypedKey.of("polyfills", "flag_well_active", NbtType.INTEGER);
            int active = be != null ? (int) be.getOrDefault(wellFlag, 0) : 0;
            if (active != 1) continue;
            return 5;
        }
        return 1;
    }

    private boolean isActivePump(Level level, VeinScan vein, BlockPos selfPos, Direction up) {
        int limit = this.veinPumpLimit(level, vein);
        long selfKey = selfPos.asLong();
        int rank = 0;
        for (BlockPos calPos : vein.blocks) {
            BlockPos above = calPos.relative(up);
            if (!GAS_PUMP_ID.equals(GasPumpBlockEntity.customId(level, above))) continue;
            if (above.asLong() < selfKey) {
                ++rank;
            }
            if (rank < limit) continue;
            return false;
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

    private GasStack storedGas() {
        GasStack s = ((GasTank)this.gasTanks.get(0)).getGas(this.getNMSLevel(), this.getMachinePos());
        return s == null ? GasStack.EMPTY : s;
    }

    @Override
    public double[] barStat(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "internal".equals(id)) {
            GasStack s = this.storedGas();
            return new double[]{s.isEmpty() ? 0.0 : (double)s.getAmount(), Math.max(1, this.effCapacity())};
        }
        if ("fuel".equals(id)) {
            return new double[]{this.burnTime, Math.max(1, this.maxBurnTime)};
        }
        if ("progress".equals(id)) {
            int total = this.effInterval();
            int done = Math.max(0, total - this.opCooldown);
            return new double[]{done, Math.max(1, total)};
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "internal".equals(id)) {
            GasStack s = this.storedGas();
            return s.isEmpty() ? "" : s.getType().name().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    @Override
    public MachineLayout getLayout() {
        return switch (this.page) {
            case 1 -> this.buildUpgradeLayout();
            case 2 -> this.buildOverclockLayout();
            default -> this.buildMainLayout();
        };
    }

    @Override
    public MachineMenu getMenu() {
        if (this.active == null) {
            this.active = new MachineMenu(this, this.getLayout());
            this.active.syncFromMachine();
        }
        return this.active;
    }

    @Override
    public void openMenu(Player player) {
        this.openPage((org.bukkit.entity.Player)player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
        this.shownUnlocked = this.curUnlocked;
    }

    private void refreshUpgradePageIfNeeded() {
        if (this.page != 1 || this.active == null || this.shownUnlocked == this.curUnlocked) {
            return;
        }
        ArrayList<HumanEntity> viewers = new ArrayList<>(this.active.getInventory().getViewers());
        this.shownUnlocked = this.curUnlocked;
        for (HumanEntity h : viewers) {
            if (!(h instanceof org.bukkit.entity.Player)) continue;
            org.bukkit.entity.Player p = (org.bukkit.entity.Player)h;
            this.openPage(p, 1);
        }
    }

    private MachineLayout buildMainLayout() {
        int infoSlot;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, this.menuSize, "Gas Pump");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.gas_pump_title", NamedTextColor.AQUA)));
        for (int s : this.fuelSlots) {
            l.addSlot(s, MenuSlotType.FUEL);
        }
        for (MachineMenuConfig.Button b : this.menuConfig.buttons) {
            this.installButton(l, b);
        }
        MachineBars.install(l, this.bars);
        int n = infoSlot = this.menuConfig.infoSlot >= 0 ? this.menuConfig.infoSlot : -1;
        if (infoSlot >= 0) {
            l.setDynamicProvider(infoSlot, (m, t) -> ((GasPumpBlockEntity)m).infoIcon());
        }
        this.fillRest(l);
        return l;
    }

    private static String fmtPoints(double v) {
        if (v == Math.rint(v)) {
            return String.valueOf((long)v);
        }
        return String.valueOf((double)Math.round(v * 100.0) / 100.0);
    }

    private ItemStack infoIcon() {
        GasStack stored = this.storedGas();
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS);
        ItemMeta meta = stack.getItemMeta();
        NamedTextColor GRAY = NamedTextColor.GRAY;
        NamedTextColor WHITE = NamedTextColor.WHITE;
        NamedTextColor AQUA = NamedTextColor.AQUA;
        Component gasName = stored.isEmpty() ? MenuText.tr("polyfill.gas.empty", WHITE) : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.gas", AQUA).append((Component)Component.text((String)": ", (TextColor)GRAY)).append(gasName)));
        meta.lore(List.of(MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY, (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + this.effCapacity() + " mB", WHITE)), MenuText.noI(MenuText.kv("polyfill.attr.extraction_points", GRAY, GasPumpBlockEntity.fmtPoints(this.lastDrawPoints) + " / " + GasPumpBlockEntity.fmtPoints(this.lastVeinPoints), WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.pressure", GRAY, String.valueOf(stored.isEmpty() ? this.effPressure() : stored.getPressure()), WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            GasPumpBlockEntity s = (GasPumpBlockEntity)m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(GasPumpBlockEntity.parseKey(iconSpec), Material.PAPER, GasPumpBlockEntity.label(b.name, NamedTextColor.AQUA), GasPumpBlockEntity.lore(b.lore));
        }, (m, p) -> {
            GasPumpBlockEntity s = (GasPumpBlockEntity)m;
            if (s.isButtonLocked(b)) {
                return;
            }
            switch (b.action.kind) {
                case OPEN_PAGE: {
                    s.openPage((org.bukkit.entity.Player)p, b.action.page);
                    break;
                }
                case DEPLETE_GAS: {
                    ((GasTank)s.gasTanks.get(0)).deplete(s.getNMSLevel(), s.getMachinePos());
                    s.setChanged();
                    break;
                }
            }
        });
    }

    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && this.curOverclockLimit <= 0.0;
    }

    private static Component label(String s, NamedTextColor color) {
        return MenuText.textOrTranslatable(s, color);
    }

    private static Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return new Component[0];
        }
        Component[] out = new Component[lines.size()];
        for (int i = 0; i < lines.size(); ++i) {
            out[i] = GasPumpBlockEntity.label(lines.get(i), NamedTextColor.GRAY);
        }
        return out;
    }

    private static Key parseKey(String spec) {
        if (spec == null) {
            return Key.of((String)"cml", (String)"gui_empty");
        }
        int i = spec.indexOf(58);
        return i < 0 ? Key.of((String)"cml", (String)spec) : Key.of((String)spec.substring(0, i), (String)spec.substring(i + 1));
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = this.curUnlocked;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, 18, "Upgrades");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.AQUA)));
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
                continue;
            }
            l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(MenuText.tr("polyfill.ui.locked", NamedTextColor.RED), MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(), (m, p) -> ((GasPumpBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0));
        this.fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = OverclockMenu.build(this.getMachineId(), NamedTextColor.AQUA, () -> this.overclock, () -> this.curOverclockLimit, (up, c) -> this.bumpOverclock((boolean)up, (ClickType)c), p -> this.openPage((org.bukkit.entity.Player)p, 0));
        this.fillRest(l);
        return l;
    }

    public void bumpOverclock(boolean up, ClickType c) {
        float delta = OverclockMenu.step(c);
        this.overclock += up ? delta : -delta;
        this.overclock = (float)GasPumpBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeRecomputeCd = 0;
        this.setChanged();
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); ++i) {
            if (l.getSlotType(i) != MenuSlotType.BACKGROUND) continue;
            l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    @Override
    public void saveCustomData(CompoundTag tag) {
        this.set(KEY_OC, Float.valueOf(this.overclock));
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = this.getOrDefault(KEY_OC, Float.valueOf(0.0f)).floatValue();
    }

    private static final class VeinScan {
        double points;
        GasType gas = GasType.EMPTY;
        final List<BlockPos> blocks = new ArrayList<BlockPos>();

        private VeinScan() {
        }
    }
}

