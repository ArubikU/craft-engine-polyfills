/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
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
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidPlacer;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidTransferHelper;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.fluid.graph.FluidEngine;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
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

public class MachinePumpBlockEntity
extends AbstractMachineBlockEntity {
    public static final int UPGRADE_SLOTS = 9;
    private static final int BASE_UNLOCKED = 3;
    public static final boolean DEBUG_PUMP = false;
    private final int capacity;
    private final int baseExtractPerTick;
    private final int basePushPerTick;
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
    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;
    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "pump_overclock", NbtType.FLOAT);
    private int opCooldown = 0;
    private int upgradeRecomputeCd = 0;

    public MachinePumpBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, new HashMap<Key, List<MachineAttributes.Mod>>(), new ArrayList<MachineBar>(), MachinePumpBlockEntity.defaultMenuConfig(), 8000, 1000, 1000, 10, 10);
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(9, null, new int[0], new int[0], new int[0], new ArrayList<MachineMenuConfig.Button>(), 4);
    }

    public MachinePumpBlockEntity(BlockEntity blockEntity, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig, int capacity, int extractPerTick, int pushPerTick, int pressure, int extractTickRate) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.bars = bars == null ? new ArrayList() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : MachinePumpBlockEntity.defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 9;
        this.capacity = capacity;
        this.baseExtractPerTick = extractPerTick;
        this.basePushPerTick = pushPerTick;
        this.basePressure = pressure;
        this.baseExtractTickRate = Math.max(1, extractTickRate);
        this.fuelSlots = this.menuConfig.fuelSlots != null ? this.menuConfig.fuelSlots : new int[]{};
        this.setMaxStackSize(64);
        this.addFluidTank(new FluidTank("internal", capacity));
        this.setIOConfiguration(this.buildIO());
    }

    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        cfg.addInput(IOConfiguration.IOType.FLUID, RelativeDirection.DOWN);
        cfg.addOutput(IOConfiguration.IOType.FLUID, RelativeDirection.UP);
        if (this.fuelSlots.length > 0) {
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.FRONT);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.BACK);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.LEFT);
            cfg.addInput(IOConfiguration.IOType.ITEM, RelativeDirection.RIGHT);
            cfg.setSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL, this.fuelSlots);
        }
        return cfg;
    }

    private int effExtractPerTick() {
        return Math.max(1, this.baseExtractPerTick);
    }

    private int effPushPerTick() {
        return Math.max(1, this.basePushPerTick);
    }

    private int effInterval() {
        double f = Math.max(0.05, 1.0 + (double)this.overclock);
        return Math.max(1, (int)Math.round((double)this.baseExtractTickRate / f));
    }

    private int effCapacity() {
        double g = MachinePumpBlockEntity.clamp(this.curGeneration, 0.0, 2.0);
        return (int)Math.round((double)this.capacity * (1.0 + g));
    }

    private int effPressure() {
        return Math.max(0, (int)Math.round((double)this.basePressure + this.curPressure));
    }

    public Direction graphOutFace(Level level) {
        return this.getFacing(level);
    }

    public int graphPressure() {
        return this.effPressure();
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
        this.curOverclockLimit = MachinePumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curGeneration = MachinePumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), 0.0, 2.0);
        this.curPressure = MachinePumpBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.PRESSURE, 0.0), 0.0, 256.0);
        this.overclock = (float)MachinePumpBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
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
        boolean pumpableBelow;
        if (level.isClientSide()) {
            return;
        }
        try {
            FluidEngine.registerSeed(this.getMachinePos());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (FluidEngine.ENABLED) {
            try {
                this.pumpWorldIntake(level);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                this.pumpWorldOutput(level);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
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
        FluidTank tank = (FluidTank)this.fluidTanks.get(0);
        FluidStack stored = this.storedFluid();
        int cap = this.effCapacity();
        int pressure = this.effPressure();
        if (!stored.isEmpty() && stored.getPressure() != pressure) {
            this.writeTank(level, new FluidStack(stored.getType(), stored.getAmount(), pressure));
        }
        Direction facing = this.getFacing(level);
        ConnectableBlockBehavior cbb = this.getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldDown = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN, DirectionalIOHelper.toHorizontalDirection(facing));
        Direction worldUp = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP, DirectionalIOHelper.toHorizontalDirection(facing));
        int blockCd = this.getOrDefault(FluidKeys.FLUID_BLOCK_COOLDOWN, 0);
        int ioCd = this.getOrDefault(FluidKeys.FLUID_IO_COOLDOWN, 0);
        if (blockCd > 0) {
            this.set(FluidKeys.FLUID_BLOCK_COOLDOWN, blockCd - 1);
        }
        if (ioCd > 0) {
            this.set(FluidKeys.FLUID_IO_COOLDOWN, ioCd - 1);
        }
        stored = this.storedFluid();
        if (ioCd <= 0 && !stored.isEmpty()) {
            BlockPos up = pos.relative(worldUp);
            FluidCarrier carrier = FluidTransferHelper.getCarrier(level, up).orElse(null);
            if (carrier != null) {
                int send = stored.getAmount();
                int outPressure = worldUp == Direction.DOWN ? 0 : Math.max(0, stored.getPressure() - 1);
                FluidStack out = new FluidStack(stored.getType(), send, outPressure);
                int inserted = carrier.insertFluid(level, up, out, worldUp.getOpposite());
                if (inserted > 0) {
                    int left = stored.getAmount() - inserted;
                    this.writeTank(level, left <= 0 ? FluidStack.EMPTY : new FluidStack(stored.getType(), left, stored.getPressure()));
                    int delay = FluidType.carrierIODelay(stored.getType());
                    if (delay > 1) {
                        this.set(FluidKeys.FLUID_IO_COOLDOWN, delay);
                    }
                }
            } else if (stored.getType() == FluidType.EXPERIENCE) {
                int amount;
                if (level.getBlockState(up).isAir() && (amount = stored.getAmount()) > 0) {
                    int toSpawn;
                    for (int orbs = (int)Math.ceil((double)amount / 7.0); orbs > 0; orbs -= toSpawn) {
                        toSpawn = Math.min(orbs, 10);
                        level.addFreshEntity((Entity)new ExperienceOrb(level, (double)up.getX() + 0.5, (double)up.getY() + 0.5, (double)up.getZ() + 0.5, toSpawn));
                    }
                    this.writeTank(level, FluidStack.EMPTY);
                }
            } else if (stored.getAmount() >= stored.getType().mbPerFullBlock()) {
                int full = stored.getType().mbPerFullBlock();
                FluidStack one = new FluidStack(stored.getType(), full, stored.getPressure());
                if (FluidPlacer.place(one, up, level)) {
                    int left = stored.getAmount() - full;
                    this.writeTank(level, left <= 0 ? FluidStack.EMPTY : new FluidStack(stored.getType(), left, stored.getPressure()));
                }
            }
        }
        boolean tankFull = (stored = this.storedFluid()).getAmount() >= cap;
        BlockPos inPos = pos.relative(worldDown);
        FluidType belowType = FluidType.getFluidTypeAt(inPos, level);
        BlockState belowState = level.getBlockState(inPos);
        boolean bl = pumpableBelow = belowType != FluidType.EMPTY || belowState.is(Blocks.WATER_CAULDRON) || belowState.is(Blocks.LAVA_CAULDRON) || FluidTransferHelper.getCarrier(level, inPos).isPresent();
        if (tankFull || !pumpableBelow) {
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
        stored = this.storedFluid();
        if (blockCd <= 0 && stored.getAmount() < cap) {
            FluidCarrier src;
            FluidStack collected;
            BlockPos target = pos.relative(worldDown);
            FluidType base = FluidType.getFluidTypeAt(target, level);
            int free = cap - (stored.isEmpty() ? 0 : stored.getAmount());
            int fullBlock = base != null && base != FluidType.EMPTY ? base.mbPerFullBlock() : 1000;
            int extract = Math.min(free, Math.max(this.effExtractPerTick(), fullBlock));
            BlockState tb = level.getBlockState(target);
            boolean isCauldron = tb.is(Blocks.WATER_CAULDRON) || tb.is(Blocks.LAVA_CAULDRON);
            FluidStack fluidStack = collected = base == FluidType.LAVA && !isCauldron ? FluidType.collectArea(target, level, 32, extract, stored.getType()) : FluidType.collectAt(target, level, extract, stored.getType());
            if (!collected.isEmpty() && (stored.isEmpty() || stored.getType() == collected.getType())) {
                int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + collected.getAmount();
                this.writeTank(level, new FluidStack(collected.getType(), Math.min(cap, newAmt), pressure));
                int delay = FluidType.blockCollectDelay(collected.getType());
                if (delay > 1) {
                    this.set(FluidKeys.FLUID_BLOCK_COOLDOWN, delay);
                }
            } else if (collected.isEmpty() && (src = (FluidCarrier)FluidTransferHelper.getCarrier(level, target).orElse(null)) != null) {
                FluidStack srcStored = src.getStored(level, target);
                int free2 = cap - (stored.isEmpty() ? 0 : stored.getAmount());
                if (!srcStored.isEmpty() && free2 > 0 && (stored.isEmpty() || stored.getType() == srcStored.getType())) {
                    FluidStack[] got = new FluidStack[]{null};
                    src.extractFluid(level, target, Math.min(extract, free2), (FluidStack f) -> {
                        got[0] = f;
                    }, worldDown.getOpposite());
                    if (got[0] != null && !got[0].isEmpty()) {
                        int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + got[0].getAmount();
                        this.writeTank(level, new FluidStack(got[0].getType(), Math.min(cap, newAmt), pressure));
                    }
                }
            }
        }
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
        return "machine_pump";
    }

    private void pumpWorldIntake(Level level) {
        FluidStack collected;
        boolean cauldron;
        BlockPos pos = this.getMachinePos();
        Direction facing = this.getFacing(level);
        ConnectableBlockBehavior cbb = this.getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldDown = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.DOWN, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.DOWN, DirectionalIOHelper.toHorizontalDirection(facing));
        int cap = this.effCapacity();
        FluidStack stored = this.storedFluid();
        if (!stored.isEmpty() && stored.getAmount() >= cap) {
            return;
        }
        BlockPos target = pos.relative(worldDown);
        FluidType base = FluidType.getFluidTypeAt(target, level);
        BlockState tb = level.getBlockState(target);
        boolean bl = cauldron = tb.is(Blocks.WATER_CAULDRON) || tb.is(Blocks.LAVA_CAULDRON);
        if (FluidEngine.DEBUG) {
            System.out.println("[PumpIntake] worldDown(IN)=" + String.valueOf(worldDown) + " target=" + target.toShortString() + " base=" + String.valueOf(base) + " block=" + String.valueOf(tb.getBlock()) + " cauldron=" + cauldron + " stored=" + this.storedFluid().getAmount() + " burnTime=" + this.burnTime + " hasFuel=" + this.hasFuel(level));
        }
        if (base == FluidType.EMPTY && !cauldron) {
            return;
        }
        if (this.burnTime <= 0) {
            if (this.hasFuel(level)) {
                this.consumeFuel(level);
                this.setChanged();
            } else {
                return;
            }
        }
        --this.burnTime;
        int free = cap - (stored.isEmpty() ? 0 : stored.getAmount());
        int fullBlock = base != FluidType.EMPTY ? base.mbPerFullBlock() : 1000;
        int extract = Math.min(free, Math.max(this.effExtractPerTick(), fullBlock));
        FluidStack fluidStack = collected = base == FluidType.LAVA && !cauldron ? FluidType.collectArea(target, level, 1, extract, stored.getType()) : FluidType.collectAt(target, level, extract, stored.getType());
        if (FluidEngine.DEBUG) {
            System.out.println("[PumpIntake] collected=" + (String)(collected.isEmpty() ? "0" : String.valueOf(collected.getType()) + ":" + collected.getAmount()) + " extract=" + extract + " cap=" + cap + " srcAtTarget=" + level.getFluidState(target).isSource());
        }
        if (!collected.isEmpty() && (stored.isEmpty() || stored.getType() == collected.getType())) {
            int newAmt = (stored.isEmpty() ? 0 : stored.getAmount()) + collected.getAmount();
            this.writeTank(level, new FluidStack(collected.getType(), Math.min(cap, newAmt), this.effPressure()));
        }
    }

    private void pumpWorldOutput(Level level) {
        FluidStack stored = this.storedFluid();
        if (stored.isEmpty()) {
            return;
        }
        BlockPos pos = this.getMachinePos();
        Direction facing = this.getFacing(level);
        ConnectableBlockBehavior cbb = this.getBlockBehavior(ConnectableBlockBehavior.class);
        DirectionType type = cbb != null ? cbb.getDirectionType() : DirectionType.FULL;
        Direction worldUp = type == DirectionType.FULL ? DirectionalIOHelper.getVerticalWorldDirection(RelativeDirection.UP, facing) : DirectionalIOHelper.getHorizontalWorldDirection(RelativeDirection.UP, DirectionalIOHelper.toHorizontalDirection(facing));
        BlockPos out = pos.relative(worldUp);
        if (FluidTransferHelper.getCarrier(level, out).isPresent()) {
            return;
        }
        FluidType t = stored.getType();
        if (t == FluidType.EXPERIENCE) {
            int toSpawn;
            if (!level.getBlockState(out).isAir()) {
                return;
            }
            int amount = stored.getAmount();
            if (amount <= 0) {
                return;
            }
            for (int orbs = (int)Math.ceil((double)amount / 7.0); orbs > 0; orbs -= toSpawn) {
                toSpawn = Math.min(orbs, 10);
                level.addFreshEntity((Entity)new ExperienceOrb(level, (double)out.getX() + 0.5, (double)out.getY() + 0.5, (double)out.getZ() + 0.5, toSpawn));
            }
            this.writeTank(level, FluidStack.EMPTY);
            return;
        }
        int full = Math.max(1, t.mbPerFullBlock());
        if (stored.getAmount() < full) {
            return;
        }
        FluidStack one = new FluidStack(t, full, stored.getPressure());
        if (FluidPlacer.place(one, out, level)) {
            int left = stored.getAmount() - full;
            this.writeTank(level, left <= 0 ? FluidStack.EMPTY : new FluidStack(t, left, stored.getPressure()));
        }
    }

    private FluidStack storedFluid() {
        FluidStack s = ((FluidTank)this.fluidTanks.get(0)).getFluid(this.getNMSLevel(), this.getMachinePos());
        return s == null ? FluidStack.EMPTY : s;
    }

    @Override
    protected void pullFromInputFaces(Level level) {
    }

    private void writeTank(Level level, FluidStack s) {
        BlockPos pos = this.getMachinePos();
        PersistentBlockEntity.executeAt(level, pos, be -> {
            if (s == null || s.isEmpty()) {
                be.remove(((FluidTank)this.fluidTanks.get(0)).getKey());
            } else {
                be.set(((FluidTank)this.fluidTanks.get(0)).getKey(), s);
            }
        });
    }

    @Override
    public double[] barStat(String id) {
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = this.storedFluid();
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
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = this.storedFluid();
            return s.isEmpty() ? "" : s.getType().name().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    @Override
    public Map<String, String> barPlaceholders(String id) {
        if ("fluid".equals(id) || "internal".equals(id)) {
            FluidStack s = this.storedFluid();
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("pressure", String.valueOf(s.isEmpty() ? this.effPressure() : s.getPressure()));
            return m;
        }
        return super.barPlaceholders(id);
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
        MachineLayout l = new MachineLayout(InventoryType.CHEST, this.menuSize, "Pump");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.pump_title", NamedTextColor.AQUA)));
        for (int s : this.fuelSlots) {
            l.addSlot(s, MenuSlotType.FUEL);
        }
        var buttonIter = this.menuConfig.buttons.iterator();
        while (buttonIter.hasNext()) {
            MachineMenuConfig.Button b = buttonIter.next();
            this.installButton(l, b);
        }
        MachineBars.install(l, this.bars);
        int n = infoSlot = this.menuConfig.infoSlot >= 0 ? this.menuConfig.infoSlot : -1;
        if (infoSlot >= 0) {
            l.setDynamicProvider(infoSlot, (m, t) -> ((MachinePumpBlockEntity)m).infoIcon());
        }
        this.fillRest(l);
        return l;
    }

    private ItemStack infoIcon() {
        FluidStack stored = this.storedFluid();
        Material material = Material.BUCKET;
        if (!stored.isEmpty()) {
            if (stored.getType() == FluidType.LAVA) {
                material = Material.LAVA_BUCKET;
            } else if (stored.getType() == FluidType.WATER) {
                material = Material.WATER_BUCKET;
            }
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        NamedTextColor GRAY = NamedTextColor.GRAY;
        NamedTextColor WHITE = NamedTextColor.WHITE;
        NamedTextColor AQUA = NamedTextColor.AQUA;
        Component fluidName = stored.isEmpty() ? MenuText.tr("polyfill.liquid.empty", WHITE) : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.fluid", AQUA).append((Component)Component.text((String)": ", (TextColor)GRAY)).append(fluidName)));
        meta.lore(List.of(MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY, (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + this.effCapacity() + " mB", WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.pressure", GRAY, String.valueOf(stored.isEmpty() ? this.effPressure() : stored.getPressure()), WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            MachinePumpBlockEntity s = (MachinePumpBlockEntity)m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(MachinePumpBlockEntity.parseKey(iconSpec), Material.PAPER, MachinePumpBlockEntity.label(b.name, NamedTextColor.AQUA), MachinePumpBlockEntity.lore(b.lore));
        }, (m, p) -> {
            MachinePumpBlockEntity s = (MachinePumpBlockEntity)m;
            if (s.isButtonLocked(b)) {
                return;
            }
            switch (b.action.kind) {
                case OPEN_PAGE: {
                    s.openPage((org.bukkit.entity.Player)p, b.action.page);
                    break;
                }
                case DEPLETE_FLUID: {
                    s.writeTank(s.getNMSLevel(), FluidStack.EMPTY);
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
            out[i] = MachinePumpBlockEntity.label(lines.get(i), NamedTextColor.GRAY);
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
        l.addButton(17, (m, t) -> MenuText.backIcon(), (m, p) -> ((MachinePumpBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0));
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
        this.overclock = (float)MachinePumpBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
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
}

