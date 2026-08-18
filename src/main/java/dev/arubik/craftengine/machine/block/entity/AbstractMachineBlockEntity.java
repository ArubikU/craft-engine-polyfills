/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.api.ContraptionTickable;
import dev.arubik.craftengine.conveyor.ConveyorBlockEntity;
import dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.ConveyorMath;
import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.MachineFuelRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.condition.RecipeCondition;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.machine.upgrade.UpgradeRegistry;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MachineMode;
import dev.arubik.craftengine.util.BridgeUtils;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class AbstractMachineBlockEntity
extends PersistentWorldlyBlockEntity
implements ContraptionTickable,
ConveyorDisplayReceiver {
    private UUID ownerUuid;
    protected int progress = 0;
    protected double progressCarry = 0.0;
    protected int maxProgress = 0;
    protected int burnTime = 0;
    protected int maxBurnTime = 0;
    protected int overclockedTicks = 0;
    protected boolean isProcessing = false;
    protected IOConfiguration ioConfiguration;
    protected boolean requiresRedstone = false;
    protected boolean invertRedstone = false;
    protected float storedXp = 0.0f;
    private static final TypedKey<Float> KEY_XP = TypedKey.of("craftengine", "machine_xp", NbtType.FLOAT);
    protected final List<FluidTank> fluidTanks = new ArrayList<FluidTank>();
    protected final List<GasTank> gasTanks = new ArrayList<GasTank>();
    protected UpgradeModifiers upgradeModifiers = UpgradeModifiers.NONE;
    private static final TypedKey<Integer> KEY_PROGRESS = TypedKey.of("craftengine", "machine_progress", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_MAX_PROGRESS = TypedKey.of("craftengine", "machine_max_progress", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_BURN_TIME = TypedKey.of("craftengine", "machine_burn_time", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_MAX_BURN_TIME = TypedKey.of("craftengine", "machine_max_burn_time", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_OVERCLOCKED_TICKS = TypedKey.of("craftengine", "machine_overclocked_ticks", NbtType.INTEGER);
    private static final TypedKey<String> KEY_OWNER_UUID = TypedKey.of("polyfills", "machine_owner_uuid", NbtType.STRING);
    protected MachineMenu menu;
    public static boolean DEBUG_IO = false;
    private CEWorld ceWorld;
    private final Map<net.minecraft.core.Direction, FunnelTransit> funnelTransits = new EnumMap<net.minecraft.core.Direction, FunnelTransit>(net.minecraft.core.Direction.class);
    private Boolean lastActivated = null;

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(UUID uuid) {
        this.ownerUuid = uuid;
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public AbstractMachineBlockEntity(BlockEntity blockEntity, int size) {
        super(blockEntity, size);
    }

    @Override
    public net.momirealms.craftengine.core.world.BlockPos pos() {
        return this.blockEntity().pos();
    }

    public BlockPos getMachinePos() {
        return BlockPos.of((long)this.blockEntity().pos().asLong());
    }

    public float getStoredXp() {
        return this.storedXp;
    }

    public void accumulateXp(float amount) {
        this.storedXp += amount;
        this.setChanged();
    }

    public void awardXp(Player player) {
        if (this.storedXp > 0.0f) {
            player.giveExperiencePoints((int)this.storedXp);
            this.storedXp = 0.0f;
            this.setChanged();
        }
    }

    public List<GasTank> gasTankList() {
        return this.gasTanks;
    }

    public FluidStack getFluidInSlot(int slot) {
        return this.get(this.fluidTanks.get(slot).getKey());
    }

    public FluidStack getStoredFluidForCarrier() {
        if (this.fluidTanks.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidStack s = this.fluidTanks.get(0).getFluid(this.getNMSLevel(), this.getMachinePos());
        return s == null ? FluidStack.EMPTY : s;
    }

    public long getFluidCapacityForCarrier() {
        return this.fluidTanks.isEmpty() ? 0L : (long)this.fluidTanks.get(0).getCapacity();
    }

    public void setStoredFluidRaw(Level level, FluidStack stack) {
        if (this.fluidTanks.isEmpty()) {
            return;
        }
        TypedKey<FluidStack> key = this.fluidTanks.get(0).getKey();
        PersistentBlockEntity.executeAt(level, this.getMachinePos(), be -> {
            if (stack == null || stack.isEmpty()) {
                be.remove(key);
            } else {
                be.set(key, stack);
            }
        });
    }

    public GasStack getGasInSlot(int slot) {
        return this.get(this.gasTanks.get(slot).getKey());
    }

    public int insertFluidInSlot(int slot, FluidStack stack, Level level) {
        return this.fluidTanks.get(slot).insert(level, this.getMachinePos(), stack);
    }

    public int extractFluidInSlot(int slot, int amount, Level level, Consumer<FluidStack> drained) {
        return this.fluidTanks.get(slot).extract(level, this.getMachinePos(), amount, drained);
    }

    public int insertGasInSlot(int slot, GasStack stack, Level level) {
        return this.gasTanks.get(slot).insert(level, this.getMachinePos(), stack);
    }

    public int extractGasInSlot(int slot, int amount, Level level, Consumer<GasStack> drained) {
        return this.gasTanks.get(slot).extract(level, this.getMachinePos(), amount, drained);
    }

    public void addFluidTank(FluidTank tank) {
        this.fluidTanks.add(tank);
    }

    public void addGasTank(GasTank tank) {
        this.gasTanks.add(tank);
    }

    public void setIOConfiguration(IOConfiguration config) {
        this.ioConfiguration = config;
    }

    public IOConfiguration getIOConfiguration() {
        return this.ioConfiguration;
    }

    protected net.minecraft.core.Direction getFacing(Level level) {
        ConnectableBlockBehavior cbb;
        ConnectableBlockBehavior c;
        net.minecraft.core.Direction d;
        if (level == null) {
            return null;
        }
        BlockPos pos = this.getMachinePos();
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState == null) {
            return null;
        }
        try {
            String v;
            Property facingProp = ((BlockDefinition)customState.owner().value()).getProperty("facing");
            if (facingProp != null && (d = net.minecraft.core.Direction.byName((String)(v = String.valueOf(customState.get(facingProp)).toLowerCase()))) != null) {
                return d;
            }
        }
        catch (Throwable facingProp) {
            // empty catch block
        }
        BlockBehavior beh = customState.behavior();
        cbb = beh instanceof ConnectableBlockBehavior ? (ConnectableBlockBehavior) beh : (beh != null ? (ConnectableBlockBehavior)(beh.getFirst(ConnectableBlockBehavior.class)) : null);
        if (cbb != null && (d = cbb.toDirection(state)) != null) {
            return d;
        }
        return net.minecraft.core.Direction.NORTH;
    }

    public boolean fillTank(Level level, FluidStack fluid) {
        boolean changed = false;
        for (FluidTank tank : this.fluidTanks) {
            int accepted = tank.insert(level, this.getMachinePos(), fluid);
            if (accepted <= 0) continue;
            fluid.removeAmount(accepted);
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
        return fluid.isEmpty();
    }

    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side) {
        return this.insertFluid(level, stack, side, -1);
    }

    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        block8: {
            boolean changed;
            int accepted;
            block10: {
                FluidStack copy;
                block9: {
                    if (this.ioConfiguration == null) break block8;
                    net.minecraft.core.Direction localDir = side;
                    DirectionType dirType = DirectionType.HORIZONTAL;
                    BlockState state = level.getBlockState(this.getMachinePos());
                    Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
                    if (customState.isPresent()) {
                        CompositeBlockBehavior compositeBlockBehavior;
                        ConnectableBlockBehavior cbb;
                        BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                        if (behavior instanceof ConnectableBlockBehavior) {
                            ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                            localDir = connectableBlockBehavior.toLocalDirection(side, state);
                        }
                        if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                            localDir = cbb.toLocalDirection(side, state);
                        }
                    }
                    if (!this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.FLUID, localDir)) {
                        return 0;
                    }
                    int targetSlot = slot;
                    if (targetSlot == -1) {
                        targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, localDir);
                    }
                    copy = stack.copy();
                    accepted = 0;
                    changed = false;
                    if (targetSlot == -1) break block9;
                    if (targetSlot < 0 || targetSlot >= this.fluidTanks.size() || (accepted = this.fluidTanks.get(targetSlot).insert(level, this.getMachinePos(), copy)) <= 0) break block10;
                    changed = true;
                    break block10;
                }
                for (FluidTank tank : this.fluidTanks) {
                    int moved = tank.insert(level, this.getMachinePos(), copy);
                    if (moved > 0) {
                        copy.removeAmount(moved);
                        accepted += moved;
                        changed = true;
                    }
                    if (!copy.isEmpty()) continue;
                    break;
                }
            }
            if (changed) {
                this.setChanged();
            }
            return accepted;
        }
        return 0;
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side) {
        return this.extractFluid(level, max, drained, side, -1);
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().providesOutput(IOConfiguration.IOType.FLUID, localDir)) {
                return 0;
            }
            boolean[] changed = new boolean[]{false};
            Consumer<FluidStack> hookDrained = s -> {
                if (drained != null) {
                    drained.accept((FluidStack)s);
                }
                changed[0] = true;
            };
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, localDir);
            }
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.fluidTanks.size()) {
                    int extracted = this.fluidTanks.get(targetSlot).extract(level, this.getMachinePos(), max, hookDrained);
                    if (changed[0]) {
                        this.setChanged();
                    }
                    return extracted;
                }
                return 0;
            }
            for (FluidTank tank : this.fluidTanks) {
                int extracted = tank.extract(level, this.getMachinePos(), max, hookDrained);
                if (extracted <= 0) continue;
                if (changed[0]) {
                    this.setChanged();
                }
                return extracted;
            }
        }
        return 0;
    }

    public void fillGasTank(Level level, GasStack gas) {
        boolean changed = false;
        for (GasTank tank : this.gasTanks) {
            int accepted = tank.insert(level, this.getMachinePos(), gas);
            if (accepted <= 0) continue;
            gas.shrink(accepted);
            changed = true;
            if (!gas.isEmpty()) continue;
            break;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public int insertGas(Level level, GasStack stack, net.minecraft.core.Direction side) {
        return this.insertGas(level, stack, side, -1);
    }

    public int insertGas(Level level, GasStack stack, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.GAS, localDir)) {
                return 0;
            }
            if (stack == null || stack.isEmpty()) {
                return 0;
            }
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, localDir);
            }
            GasStack copy = new GasStack(stack.getType(), stack.getAmount());
            int originalAmount = copy.getAmount();
            boolean changed = false;
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.gasTanks.size()) {
                    int accepted = this.gasTanks.get(targetSlot).insert(level, this.getMachinePos(), copy);
                    if (accepted > 0) {
                        changed = true;
                    }
                    return accepted;
                }
                return 0;
            }
            this.fillGasTank(level, copy);
            if (originalAmount != copy.getAmount()) {
                changed = true;
            }
            if (changed) {
                this.setChanged();
            }
            return originalAmount - copy.getAmount();
        }
        return 0;
    }

    public int extractGas(Level level, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side) {
        return this.extractGas(level, max, drained, side, -1);
    }

    public int extractGas(Level level, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().providesOutput(IOConfiguration.IOType.GAS, localDir)) {
                return 0;
            }
            boolean[] changed = new boolean[]{false};
            Consumer<GasStack> hookDrained = s -> {
                if (drained != null) {
                    drained.accept((GasStack)s);
                }
                changed[0] = true;
            };
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, localDir);
            }
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.gasTanks.size()) {
                    int extracted = this.gasTanks.get(targetSlot).extract(level, this.getMachinePos(), max, hookDrained);
                    if (changed[0]) {
                        this.setChanged();
                    }
                    return extracted;
                }
                return 0;
            }
            for (GasTank tank : this.gasTanks) {
                int extracted = tank.extract(level, this.getMachinePos(), max, hookDrained);
                if (extracted <= 0) continue;
                if (changed[0]) {
                    this.setChanged();
                }
                return extracted;
            }
        }
        return 0;
    }

    protected boolean requiresFuel() {
        return true;
    }

    protected void processTick(Level level) {
        if (this.requiresFuel() && this.burnTime > 0) {
            --this.burnTime;
        }
        if (level.isClientSide()) {
            return;
        }
        this.recomputeUpgrades();
        if (this.requiresRedstone && !this.isRedstoneEnabled(level)) {
            if (this.progress > 0) {
                this.progress = Math.max(0, this.progress - 1);
            }
            this.isProcessing = false;
            return;
        }
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(level);
        if (this.canProcess(level, recipe)) {
            boolean needsFuel;
            boolean bl = needsFuel = this.requiresFuel() && recipe.isFuelRequired();
            if (needsFuel && this.burnTime <= 0) {
                if (this.hasFuel(level)) {
                    this.consumeFuel(level);
                    this.setChanged();
                } else {
                    if (this.progress > 0) {
                        this.progress = Math.max(0, this.progress - 2);
                    }
                    this.isProcessing = false;
                    return;
                }
            }
            if (!needsFuel || this.burnTime > 0) {
                this.isProcessing = true;
                if (this.maxProgress == 0) {
                    this.maxProgress = recipe.getProcessTime();
                }
                this.progressCarry += Math.max(0.0, this.upgradeModifiers.speedMultiplier());
                int adv = (int)this.progressCarry;
                if (adv > 0) {
                    this.progress += adv;
                    this.progressCarry -= (double)adv;
                }
                if (this.progress >= this.maxProgress) {
                    this.process(level, recipe);
                    this.progress = 0;
                    this.progressCarry = 0.0;
                    this.isProcessing = false;
                }
                this.setChanged();
            }
        } else {
            this.isProcessing = false;
            this.progress = 0;
            this.progressCarry = 0.0;
            this.setChanged();
        }
    }

    public Map<String, String> barPlaceholders(String id) {
        return Collections.emptyMap();
    }

    public int effectiveRpm(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getMinRpm();
    }

    public int effectiveSu(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getSuCost();
    }

    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level var1);

    public AbstractProcessingRecipe getCurrentRecipe() {
        try {
            Level l = this.getNMSLevel();
            return l == null ? null : this.getMatchingRecipe(l);
        }
        catch (Throwable t) {
            return null;
        }
    }

    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        if (recipe.isRequireOverclocked() && !this.isOverclocked()) {
            return false;
        }
        for (RecipeCondition condition : recipe.getConditions()) {
            if (condition.test(level, this)) continue;
            return false;
        }
        for (RecipeOutput output : recipe.getOutputs()) {
            if (this.canFitOutput(level, output)) continue;
            return false;
        }
        return true;
    }

    protected abstract boolean canFitOutput(Level var1, RecipeOutput var2);

    protected void process(Level level, AbstractProcessingRecipe recipe) {
        this.consumeInputs(level, recipe);
        for (RecipeOutput output : recipe.getOutputs()) {
            output.dispense(level, this);
        }
    }

    protected abstract void consumeInputs(Level var1, AbstractProcessingRecipe var2);

    protected boolean isRedstoneEnabled(Level level) {
        if (!this.requiresRedstone) {
            return true;
        }
        boolean hasSignal = level.hasNeighborSignal(this.getMachinePos());
        return this.invertRedstone ? !hasSignal : hasSignal;
    }

    public boolean isOverclocked() {
        return this.overclockedTicks > 0;
    }

    public void setOverclockedTicks(int ticks) {
        this.overclockedTicks = Math.max(0, ticks);
        this.setChanged();
    }

    public void incrementOverclocked(int ticks) {
        this.overclockedTicks += ticks;
        this.setChanged();
    }

    public int getOverclockedTicks() {
        return this.overclockedTicks;
    }

    public boolean addOutput(net.minecraft.world.item.ItemStack stack) {
        int[] slots;
        for (int i : slots = this.getOutputSlots()) {
            int space;
            int toAdd;
            net.minecraft.world.item.ItemStack current = this.getItem(i);
            if (current.isEmpty()) {
                this.setItem(i, stack);
                return true;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)current, (net.minecraft.world.item.ItemStack)stack) || (toAdd = Math.min(space = this.getMaxStackSize() - current.getCount(), stack.getCount())) <= 0) continue;
            current.grow(toAdd);
            stack.shrink(toAdd);
            if (!stack.isEmpty()) continue;
            return true;
        }
        return false;
    }

    public void addXp(float amount) {
        this.accumulateXp(amount);
    }

    public double[] barStat(String id) {
        if ("progress".equals(id)) {
            return new double[]{this.progress, this.maxProgress};
        }
        return new double[]{0.0, 0.0};
    }

    public String barSubtype(String id) {
        return "";
    }

    public void dropAllContents(Level level, BlockPos pos) {
        try {
            CraftWorld bw = level.getWorld();
            Location loc = new Location((World)bw, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
            for (int i = 0; i < this.inventory.length; ++i) {
                net.minecraft.world.item.ItemStack st = this.inventory[i];
                if (st == null || st.isEmpty()) continue;
                bw.dropItemNaturally(loc, BridgeUtils.toBukkit(st));
            }
            for (FunnelTransit t : this.funnelTransits.values()) {
                if (t.item == null || t.item.getType().isAir()) continue;
                bw.dropItemNaturally(loc, t.item.clone());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.despawnAllFunnelTransits();
        this.clearContent();
    }

    public int[] getOutputSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT);
        }
        return new int[0];
    }

    public int[] getInputSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.INPUT);
        }
        return new int[0];
    }

    public GasStack getStoredGasForCarrier() {
        for (GasTank tank : this.gasTanks) {
            GasStack gs = tank.getGas(this.getNMSLevel(), this.getMachinePos());
            if (gs == null || gs.isEmpty()) continue;
            return gs;
        }
        return GasStack.EMPTY;
    }

    public long getGasCapacityForCarrier() {
        return this.gasTanks.isEmpty() ? 0L : (long)this.gasTanks.get(0).getCapacity();
    }

    public void setStoredGasRaw(Level level, GasStack stack) {
        if (this.gasTanks.isEmpty()) {
            return;
        }
        TypedKey<GasStack> key = this.gasTanks.get(0).getKey();
        PersistentBlockEntity.executeAt(level, this.getMachinePos(), be -> {
            if (stack == null || stack.isEmpty()) {
                be.remove(key);
            } else {
                be.set(key, stack);
            }
        });
    }

    public boolean isFuelItem(ItemStack bukkit) {
        if (bukkit == null || bukkit.getType().isAir()) {
            return false;
        }
        return RecipeManager.getFuel(this.getMachineId(), CraftItemStack.asNMSCopy((ItemStack)bukkit)) != null;
    }

    public int[] getFuelSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL);
        }
        return new int[0];
    }

    public int[] getUpgradeSlots() {
        return new int[0];
    }

    protected UpgradeRegistry upgradeRegistry() {
        return UpgradeRegistry.global();
    }

    public UpgradeModifiers getUpgradeModifiers() {
        return this.upgradeModifiers;
    }

    protected void recomputeUpgrades() {
        int[] slots = this.getUpgradeSlots();
        if (slots.length == 0) {
            this.upgradeModifiers = UpgradeModifiers.NONE;
            return;
        }
        HashMap<Key, Integer> counts = new HashMap<Key, Integer>();
        for (int slot : slots) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            Key id = this.upgradeItemId(stack);
            if (id == null) continue;
            counts.merge(id, Math.max(1, stack.getCount()), Integer::sum);
        }
        this.upgradeModifiers = UpgradeModifiers.compute(counts, this.upgradeRegistry());
    }

    protected Key upgradeItemId(net.minecraft.world.item.ItemStack nms) {
        if (nms == null || nms.isEmpty()) {
            return null;
        }
        ItemStack bukkit = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
        Key custom = CraftEngineItems.getCustomItemId((ItemStack)bukkit);
        if (custom != null) {
            return custom;
        }
        NamespacedKey nk = bukkit.getType().getKey();
        return Key.of((String)nk.getNamespace(), (String)nk.getKey());
    }

    @Override
    public void saveCustomData(CompoundTag tag) {
        super.saveCustomData(tag);
        tag.putInt("progress", this.progress);
        tag.putInt("max_progress", this.maxProgress);
        tag.putInt("burn_time", this.burnTime);
        tag.putInt("max_burn_time", this.maxBurnTime);
        tag.putInt("overclocked_ticks", this.overclockedTicks);
        tag.putFloat("stored_xp", this.storedXp);
        if (this.ownerUuid != null) {
            this.set(KEY_OWNER_UUID, this.ownerUuid.toString());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("max_progress");
        this.burnTime = tag.getInt("burn_time");
        this.maxBurnTime = tag.getInt("max_burn_time");
        this.overclockedTicks = tag.getInt("overclocked_ticks");
        this.storedXp = tag.getFloat("stored_xp");
        String ownerStr = this.getOrDefault(KEY_OWNER_UUID, null);
        if (ownerStr != null) {
            try {
                this.ownerUuid = UUID.fromString(ownerStr);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        if (this.menu != null) {
            this.menu = null;
        }
    }

    public abstract MachineLayout getLayout();

    public MachineMenu getMenu() {
        if (this.menu == null) {
            this.menu = new MachineMenu(this, this.getLayout());
            this.menu.syncFromMachine();
        }
        return this.menu;
    }

    public boolean isValidInput(int slot, net.minecraft.world.item.ItemStack stack) {
        return this.getLayout().getSlotType(slot) == MenuSlotType.INPUT;
    }

    public boolean isValidFuel(net.minecraft.world.item.ItemStack stack) {
        return this.getBurnDuration(stack) > 0;
    }

    protected int getBurnDuration(net.minecraft.world.item.ItemStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    protected abstract String getMachineId();

    public boolean isValidFuel(FluidStack stack) {
        return RecipeManager.getFuel(this.getMachineId(), stack) != null;
    }

    protected int getFluidBurnTime(FluidStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    public boolean isValidFuel(GasStack stack) {
        return RecipeManager.getFuel(this.getMachineId(), stack) != null;
    }

    protected int getGasBurnTime(GasStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    protected boolean canFitReplacement(Level level, MachineFuelRecipe recipe, int fuelSlot) {
        if (recipe.getReplacement() == null) {
            return true;
        }
        Object output = recipe.getReplacement().getOutput();
        if (output instanceof net.minecraft.world.item.ItemStack) {
            int[] outputSlots;
            net.minecraft.world.item.ItemStack itemOutput = (net.minecraft.world.item.ItemStack)output;
            net.minecraft.world.item.ItemStack currentStack = this.getItem(fuelSlot);
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                return true;
            }
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT)) {
                net.minecraft.world.item.ItemStack slotStack = this.getItem(slot);
                if (slotStack.isEmpty()) {
                    return true;
                }
                if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)slotStack, (net.minecraft.world.item.ItemStack)itemOutput) || slotStack.getCount() + itemOutput.getCount() > slotStack.getMaxStackSize()) continue;
                return true;
            }
            return false;
        }
        if (output instanceof FluidStack) {
            int[] outputSlots;
            FluidStack fluidOutput = (FluidStack)output;
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.OUTPUT)) {
                FluidTank tank;
                if (slot < 0 || slot >= this.fluidTanks.size() || (tank = this.fluidTanks.get(slot)).getCapacity() < fluidOutput.getAmount() || !tank.allows(fluidOutput)) continue;
                return true;
            }
            return false;
        }
        if (output instanceof GasStack) {
            int[] outputSlots;
            GasStack gasOutput = (GasStack)output;
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.OUTPUT)) {
                GasTank tank;
                if (slot < 0 || slot >= this.gasTanks.size() || (tank = this.gasTanks.get(slot)).getCapacity() < gasOutput.getAmount() || !tank.allows(gasOutput)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    protected boolean placeReplacement(Level level, MachineFuelRecipe recipe, int fuelSlot) {
        if (recipe.getReplacement() == null) {
            return false;
        }
        Object output = recipe.getReplacement().getOutput();
        if (output instanceof net.minecraft.world.item.ItemStack) {
            int[] outputSlots;
            net.minecraft.world.item.ItemStack itemOutput = (net.minecraft.world.item.ItemStack)output;
            net.minecraft.world.item.ItemStack currentStack = this.getItem(fuelSlot);
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                this.setItem(fuelSlot, itemOutput.copy());
                return true;
            }
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT)) {
                net.minecraft.world.item.ItemStack slotStack = this.getItem(slot);
                if (slotStack.isEmpty()) {
                    this.setItem(slot, itemOutput.copy());
                    return false;
                }
                if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)currentStack, (net.minecraft.world.item.ItemStack)itemOutput) || slotStack.getCount() + itemOutput.getCount() > slotStack.getMaxStackSize()) continue;
                slotStack.grow(itemOutput.getCount());
                return false;
            }
        } else {
            if (output instanceof FluidStack) {
                int[] outputSlots;
                FluidStack fluidOutput = (FluidStack)output;
                for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.OUTPUT)) {
                    FluidTank tank;
                    if (slot < 0 || slot >= this.fluidTanks.size() || (tank = this.fluidTanks.get(slot)).insert(level, this.getMachinePos(), fluidOutput.copy()) <= 0) continue;
                    return false;
                }
                return true;
            }
            if (output instanceof GasStack) {
                int[] outputSlots;
                GasStack gasOutput = (GasStack)output;
                for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.OUTPUT)) {
                    GasTank tank;
                    if (slot < 0 || slot >= this.gasTanks.size() || (tank = this.gasTanks.get(slot)).insert(level, this.getMachinePos(), gasOutput.copy()) <= 0) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    protected boolean hasFuel(Level level) {
        int[] gasFuelSlots;
        int[] fluidFuelSlots;
        int[] itemFuelSlots;
        if (this.ioConfiguration == null) {
            return false;
        }
        for (int slot : itemFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL)) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
            if (recipe == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        for (int slot : fluidFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.fluidTanks.size()) continue;
            FluidTank tank = this.fluidTanks.get(slot);
            FluidStack fluid = tank.getFluid(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), fluid);
            if (recipe == null || fluid.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        for (int slot : gasFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.gasTanks.size()) continue;
            GasTank tank = this.gasTanks.get(slot);
            GasStack gas = tank.getGas(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), gas);
            if (recipe == null || gas.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        return false;
    }

    protected void consumeFuel(Level level) {
        int[] gasFuelSlots;
        int[] fluidFuelSlots;
        int[] itemFuelSlots;
        if (this.ioConfiguration == null) {
            return;
        }
        for (int slot : itemFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL)) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
            if (recipe == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = (int)Math.round((double)recipe.getBurnTime() / this.upgradeModifiers.fuelMultiplier());
            boolean consumedInPlace = this.placeReplacement(level, recipe, slot);
            if (!consumedInPlace) {
                this.removeItem(slot, recipe.getInput().getAmount());
            }
            this.burnTime += burn;
            this.maxBurnTime = burn;
            if (recipe.getOverclockedTime() > 0) {
                this.incrementOverclocked(recipe.getOverclockedTime());
            }
            return;
        }
        for (int slot : fluidFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.fluidTanks.size()) continue;
            FluidTank tank = this.fluidTanks.get(slot);
            FluidStack fluid = tank.getFluid(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), fluid);
            if (recipe == null || fluid.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = recipe.getBurnTime();
            int toConsume = recipe.getInput().getAmount();
            this.placeReplacement(level, recipe, slot);
            tank.extract(level, this.getMachinePos(), toConsume, s -> {});
            this.burnTime += burn;
            this.maxBurnTime = burn;
            return;
        }
        for (int slot : gasFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.gasTanks.size()) continue;
            GasTank tank = this.gasTanks.get(slot);
            GasStack gas = tank.getGas(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), gas);
            if (recipe == null || gas.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = recipe.getBurnTime();
            int toConsume = recipe.getInput().getAmount();
            this.placeReplacement(level, recipe, slot);
            tank.extract(level, this.getMachinePos(), toConsume, s -> {});
            this.burnTime += burn;
            this.maxBurnTime = burn;
            return;
        }
    }

    protected void dbgIO(String msg) {
        if (DEBUG_IO) {
            System.out.println("[MachineIO] " + this.getMachineId() + " @" + this.getMachinePos().toShortString() + " " + msg);
        }
    }

    protected net.minecraft.core.Direction toLocalItemDir(net.minecraft.core.Direction side) {
        try {
            Level level = this.getNMSLevel();
            if (level == null) {
                return side;
            }
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional cs = BlockStateUtils.getOptionalCustomBlockState(state);
            if (cs.isPresent()) {
                CompositeBlockBehavior comp;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)cs.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior cbb2 = (ConnectableBlockBehavior)behavior;
                    return cbb2.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((comp = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    return cbb.toLocalDirection(side, state);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return side;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction worldSide) {
        if (this.ioConfiguration == null) {
            int[] all = new int[this.getContainerSize()];
            for (int i2 = 0; i2 < all.length; ++i2) {
                all[i2] = i2;
            }
            return all;
        }
        net.minecraft.core.Direction side = this.toLocalItemDir(worldSide);
        boolean acceptsInput = this.ioConfiguration.acceptsInput(IOConfiguration.IOType.ITEM, side);
        boolean providesOutput = this.ioConfiguration.providesOutput(IOConfiguration.IOType.ITEM, side);
        int dbgTarget = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, side);
        this.dbgIO("getSlotsForFace world=" + String.valueOf(worldSide) + " -> local=" + String.valueOf(side) + " in=" + acceptsInput + " out=" + providesOutput + " target=" + dbgTarget);
        if (!acceptsInput && !providesOutput) {
            return new int[0];
        }
        ArrayList<Integer> slots = new ArrayList<Integer>();
        int[] inputs = this.getInputSlots();
        int[] outputs = this.getOutputSlots();
        int[] fuels = this.getFuelSlots();
        if (acceptsInput) {
            int target = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, side);
            if (target >= 0) {
                slots.add(target);
            } else {
                for (int i3 : inputs) {
                    slots.add(i3);
                }
                int[] nArray = fuels;
                int n = nArray.length;
                for (int j = 0; j < n; ++j) {
                    int i3;
                    i3 = nArray[j];
                    slots.add(i3);
                }
            }
        }
        if (providesOutput) {
            for (int i4 : outputs) {
                slots.add(i4);
            }
        }
        return slots.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null) {
            return true;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(side);
        boolean accepts = this.ioConfiguration.acceptsInput(IOConfiguration.IOType.ITEM, local);
        int target = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, local);
        this.dbgIO("canPlace slot=" + slot + " world=" + String.valueOf(side) + " -> local=" + String.valueOf(local) + " accepts=" + accepts + " target=" + target);
        if (!accepts) {
            return false;
        }
        if (target >= 0) {
            return slot == target;
        }
        for (int in : this.getInputSlots()) {
            if (in != slot) continue;
            return true;
        }
        for (int f : this.getFuelSlots()) {
            if (f != slot) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null) {
            return true;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(side);
        if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.ITEM, local)) {
            return false;
        }
        for (int out : this.getOutputSlots()) {
            if (out != slot) continue;
            return true;
        }
        return false;
    }

    public void openMenu(Player player) {
        this.getMenu().open((org.bukkit.entity.Player)player.getBukkitEntity());
    }

    public boolean canTakeFromSlot(int slot) {
        return true;
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.menu != null) {
            this.menu.tick();
        }
        this.processTick(level);
        if (!level.isClientSide()) {
            this.pushFunnelOutputs(level);
            this.pullFromInputFaces(level);
        }
        this.updateMachineModeProperty(level, pos, state);
    }

    protected void pullFromInputFaces(Level level) {
        if (this.ioConfiguration == null) {
            return;
        }
        BlockPos pos = this.getMachinePos();
        for (net.minecraft.core.Direction world : net.minecraft.core.Direction.values()) {
            net.minecraft.core.Direction local = this.toLocalItemDir(world);
            BlockPos src = pos.relative(world);
            net.minecraft.core.Direction sideFromSrc = world.getOpposite();
            if (this.ioConfiguration.acceptsInput(IOConfiguration.IOType.FLUID, local)) {
                this.pullFluidInto(level, src, sideFromSrc, this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.FLUID, local));
            }
            if (!this.ioConfiguration.acceptsInput(IOConfiguration.IOType.GAS, local)) continue;
            this.pullGasInto(level, src, sideFromSrc, this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.GAS, local));
        }
    }

    protected void pullFluidInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (this.fluidTanks.isEmpty()) {
            return;
        }
        int idx = tankIdx >= 0 && tankIdx < this.fluidTanks.size() ? tankIdx : 0;
        FluidTank tank = this.fluidTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getFluid(level, this.getMachinePos()).getAmount();
            if (space <= 0) {
                return;
            }
            FluidCarrier fc = AbstractMachineBlockEntity.fluidCarrierAt(level, src);
            if (DEBUG_IO) {
                System.out.println("[Pull] " + this.getMachineId() + " FLUID src=" + src.toShortString() + " carrier=" + (fc == null ? "null" : fc.getClass().getSimpleName()) + " space=" + space);
            }
            if (fc == null) {
                return;
            }
            int[] got = new int[]{0};
            fc.extractFluid(level, src, Math.min(space, 1000), (FluidStack f) -> {
                if (f != null && !f.isEmpty()) {
                    got[0] = got[0] + f.getAmount();
                    tank.insert(level, this.getMachinePos(), (FluidStack)f);
                }
            }, sideFromSrc);
            if (DEBUG_IO && got[0] > 0) {
                System.out.println("[Pull]   pulled " + got[0] + " mB into tank");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected void pullGasInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (this.gasTanks.isEmpty()) {
            return;
        }
        int idx = tankIdx >= 0 && tankIdx < this.gasTanks.size() ? tankIdx : 0;
        GasTank tank = this.gasTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getGas(level, this.getMachinePos()).getAmount();
            if (space <= 0) {
                return;
            }
            GasCarrier gc = AbstractMachineBlockEntity.gasCarrierAt(level, src);
            if (gc == null) {
                return;
            }
            gc.extractGas(level, src, Math.min(space, 1000), (GasStack g) -> {
                if (g != null && !g.isEmpty()) {
                    tank.insert(level, this.getMachinePos(), (GasStack)g);
                }
            }, sideFromSrc);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected static FluidCarrier fluidCarrierAt(Level level, BlockPos pos) {
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null) {
            return null;
        }
        BlockBehavior b = cs.behavior();
        if (b instanceof FluidCarrier) {
            FluidCarrier fc = (FluidCarrier)b;
            return fc;
        }
        return b == null ? null : (FluidCarrier)b.getFirst(FluidCarrier.class);
    }

    protected static GasCarrier gasCarrierAt(Level level, BlockPos pos) {
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null) {
            return null;
        }
        BlockBehavior b = cs.behavior();
        if (b instanceof GasCarrier) {
            GasCarrier gc = (GasCarrier)b;
            return gc;
        }
        return b == null ? null : (GasCarrier)b.getFirst(GasCarrier.class);
    }

    private static net.minecraft.core.Direction nmsDir(Direction d) {
        return net.minecraft.core.Direction.valueOf((String)d.name());
    }

    private static Direction coreDir(net.minecraft.core.Direction d) {
        return Direction.valueOf((String)d.name());
    }

    private boolean hasFunnelInput() {
        if (this.ioConfiguration == null) {
            return false;
        }
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            if (!this.ioConfiguration.acceptsInput(IOConfiguration.IOType.FUNNEL, d)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isFull() {
        if (!this.hasFunnelInput()) {
            return false;
        }
        for (int s : this.getInputSlots()) {
            net.minecraft.world.item.ItemStack cur = this.getItem(s);
            if (cur != null && !cur.isEmpty() && cur.getCount() >= cur.getMaxStackSize()) continue;
            return false;
        }
        return true;
    }

    private net.minecraft.core.Direction inputFaceFor(Direction sourceFacing) {
        net.minecraft.core.Direction worldFace = AbstractMachineBlockEntity.nmsDir(sourceFacing.opposite());
        if (this.ioConfiguration == null) {
            return null;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(worldFace);
        if (!this.ioConfiguration.acceptsInput(IOConfiguration.IOType.FUNNEL, local)) {
            return null;
        }
        return worldFace;
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing) {
        return this.startInputTransit(stack, sourceFacing, 0.0f, null, false);
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing, float jitter) {
        return this.startInputTransit(stack, sourceFacing, jitter, null, false);
    }

    @Override
    public boolean adoptConveyorItem(ItemStack stack, float jitter, ConveyorItemDisplay display, boolean spawned, Direction sourceFacing) {
        return this.startInputTransit(stack, sourceFacing, jitter, display, spawned);
    }

    private boolean startInputTransit(ItemStack stack, Direction sourceFacing, float jitter, ConveyorItemDisplay display, boolean spawned) {
        if (this.ioConfiguration == null || stack == null || stack.getType().isAir()) {
            return false;
        }
        net.minecraft.core.Direction face = this.inputFaceFor(sourceFacing);
        if (face == null) {
            return false;
        }
        if (this.funnelTransits.containsKey(face) || this.isFull()) {
            return false;
        }
        FunnelTransit t = new FunnelTransit();
        t.item = stack.clone();
        t.item.setAmount(1);
        t.jitter = jitter;
        t.out = false;
        t.progress = 0.0f;
        t.display = display;
        t.spawned = spawned;
        this.funnelTransits.put(face, t);
        return true;
    }

    private boolean addToInputs(net.minecraft.world.item.ItemStack stack) {
        boolean changed = false;
        for (int s : this.getInputSlots()) {
            int space;
            int move;
            if (stack.isEmpty()) break;
            net.minecraft.world.item.ItemStack cur = this.getItem(s);
            if (cur == null || cur.isEmpty()) {
                this.setItem(s, stack.copy());
                stack.setCount(0);
                changed = true;
                continue;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents((net.minecraft.world.item.ItemStack)cur, (net.minecraft.world.item.ItemStack)stack) || (move = Math.min(space = cur.getMaxStackSize() - cur.getCount(), stack.getCount())) <= 0) continue;
            cur.grow(move);
            stack.shrink(move);
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
        return stack.isEmpty();
    }

    protected void pushFunnelOutputs(Level level) {
        if (this.ioConfiguration == null || !this.providesAnyFunnelOutput()) {
            return;
        }
        block0: for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            ConveyorReceiver recv;
            Object object;
            BlockEntity be;
            net.minecraft.core.Direction local = this.toLocalItemDir(d);
            if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.FUNNEL, local) || this.funnelTransits.containsKey(d) || (be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d))) == null || !((object = be.controller) instanceof ConveyorReceiver) || (recv = (ConveyorReceiver)object) instanceof AbstractMachineBlockEntity || recv.isFull()) continue;
            int[] outputSlots = this.getOutputSlots();
            int n = outputSlots.length;
            for (int i = 0; i < n; ++i) {
                int s = outputSlots[i];
                net.minecraft.world.item.ItemStack cur = this.getItem(s);
                if (cur == null || cur.isEmpty()) continue;
                ItemStack one = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)cur).clone();
                one.setAmount(1);
                FunnelTransit t = new FunnelTransit();
                t.item = one;
                t.jitter = 0.0f;
                t.out = true;
                t.progress = 0.0f;
                this.funnelTransits.put(d, t);
                cur.shrink(1);
                this.setChanged();
                continue block0;
            }
        }
    }

    private void tickFunnelTransits(CEWorld world) {
        if (this.funnelTransits.isEmpty()) {
            return;
        }
        Level level = (Level)world.world.minecraftWorld();
        Iterator<Map.Entry<net.minecraft.core.Direction, FunnelTransit>> it = this.funnelTransits.entrySet().iterator();
        while (it.hasNext()) {
            ConveyorReceiver recv;
            ConveyorDisplayReceiver dr;
            BlockEntityController blockEntityController;
            ConveyorBlockEntity belt;
            BlockEntityController blockEntityController2;
            Map.Entry<net.minecraft.core.Direction, FunnelTransit> e = it.next();
            net.minecraft.core.Direction face = e.getKey();
            FunnelTransit t = e.getValue();
            float rpm = 64.0f;
            BlockEntity nbe = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(face));
            if (nbe != null && (blockEntityController2 = nbe.controller) instanceof ConveyorBlockEntity && (belt = (ConveyorBlockEntity)blockEntityController2).effectiveRpm() > 0.0f) {
                rpm = belt.effectiveRpm();
            }
            float inc = ConveyorMath.progressPerTick(rpm, 64.0f, 16);
            t.progress = Math.min(1.0f, t.progress + inc);
            this.renderFunnelTransit(world, face, t);
            if (t.progress < 1.0f) continue;
            if (!t.out) {
                net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy((ItemStack)t.item);
                if (!this.addToInputs(nms)) continue;
                this.despawnFunnelTransit(world, t);
                it.remove();
                continue;
            }
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(face));
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorBlockEntity) {
                ConveyorBlockEntity belt2 = (ConveyorBlockEntity)blockEntityController;
                if (!belt2.adoptFromFunnel(world, t.item, t.jitter, t.display, t.spawned, AbstractMachineBlockEntity.coreDir(face).opposite())) continue;
                t.display = null;
                it.remove();
                continue;
            }
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorDisplayReceiver && !((dr = (ConveyorDisplayReceiver)blockEntityController) instanceof AbstractMachineBlockEntity)) {
                if (dr.isFull() || !dr.adoptConveyorItem(t.item, t.jitter, t.display, t.spawned, AbstractMachineBlockEntity.coreDir(face))) continue;
                t.display = null;
                it.remove();
                continue;
            }
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorReceiver && !((recv = (ConveyorReceiver)blockEntityController) instanceof AbstractMachineBlockEntity)) {
                if (recv.isFull() || !recv.receiveConveyorItem(t.item, AbstractMachineBlockEntity.coreDir(face), t.jitter)) continue;
                this.despawnFunnelTransit(world, t);
                it.remove();
                continue;
            }
            this.addToInputs(CraftItemStack.asNMSCopy((ItemStack)t.item));
            this.despawnFunnelTransit(world, t);
            it.remove();
        }
    }

    private void renderFunnelTransit(CEWorld world, net.minecraft.core.Direction face, FunnelTransit t) {
        BlockPos pos = this.getMachinePos();
        List viewers = world.world().getTrackedBy(new ChunkPos(new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ())));
        if (t.display == null) {
            t.display = new ConveyorItemDisplay();
        }
        t.display.setNmsItem(CraftItemStack.asNMSCopy((ItemStack)t.item));
        Vector3f center = new Vector3f(0.5f, 0.28f, 0.5f);
        Vector3f faceEdge = new Vector3f(0.5f + (float)face.getStepX() * 0.5f, 0.28f, 0.5f + (float)face.getStepZ() * 0.5f);
        Vector3f rel = t.out ? ConveyorMath.interpolate(center, faceEdge, t.progress) : ConveyorMath.interpolate(faceEdge, center, t.progress);
        int mx = t.out ? face.getStepX() : -face.getStepX();
        int mz = t.out ? face.getStepZ() : -face.getStepZ();
        Quaternionf rot = ConveyorMath.itemRotation(mx, mz, 0);
        rot.rotateY(t.jitter);
        t.display.setRotation(rot);
        t.display.render(viewers, (float)pos.getX() + rel.x, (float)pos.getY() + rel.y, (float)pos.getZ() + rel.z, !t.spawned);
        t.display.consumeRotationDirty();
        t.spawned = true;
    }

    private void despawnFunnelTransit(CEWorld world, FunnelTransit t) {
        if (t.display == null) {
            return;
        }
        for (net.momirealms.craftengine.core.entity.player.Player p : world.world().getTrackedBy(new ChunkPos(new net.momirealms.craftengine.core.world.BlockPos(this.getMachinePos().getX(), this.getMachinePos().getY(), this.getMachinePos().getZ())))) {
            t.display.despawn(p);
        }
        t.display.clearShown();
        t.display = null;
        t.spawned = false;
    }

    protected void despawnAllFunnelTransits() {
        if (this.ceWorld == null || this.funnelTransits.isEmpty()) {
            return;
        }
        for (FunnelTransit t : this.funnelTransits.values()) {
            this.despawnFunnelTransit(this.ceWorld, t);
        }
        this.funnelTransits.clear();
    }

    private boolean providesAnyFunnelOutput() {
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.FUNNEL, d)) continue;
            return true;
        }
        return false;
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(AbstractMachineBlockEntity::tickController);
    }

    public static void tickController(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state, AbstractMachineBlockEntity self) {
        Level level = (Level)world.world.minecraftWorld();
        self.ceWorld = world;
        self.tick(level, BlockPos.of((long)pos.asLong()), state);
        if (!level.isClientSide()) {
            self.tickFunnelTransits(world);
        }
    }

    private void updateMachineModeProperty(Level level, BlockPos pos, ImmutableBlockState state) {
        MachineMode newMode;
        BlockBehavior behavior = state.behavior();
        if (!(behavior instanceof MachineBlockBehavior)) {
            return;
        }
        MachineBlockBehavior machineBehavior = (MachineBlockBehavior)behavior;
        Property<MachineMode> machineModeProp = machineBehavior.MACHINE_MODE;
        if (machineModeProp == null) {
            return;
        }
        MachineMode currentMode = (MachineMode)(state.get(machineModeProp));
        if (currentMode != (newMode = this.burnTime > 0 ? (this.isOverclocked() ? MachineMode.OVERCLOCKING : MachineMode.WORKING) : MachineMode.IDLE)) {
            ImmutableBlockState newState = state.with(machineModeProp, newMode);
            level.setBlock(pos, (BlockState)newState.customBlockState().minecraftState(), 11);
        }
    }

    protected void maybeUpdateActivated(Level level, BlockPos pos, ImmutableBlockState state, boolean active) {
        if (this.lastActivated != null && this.lastActivated == active) {
            return;
        }
        try {
            Property p;
            Property property = p = state == null ? null : state.getProperty("activated");
            if (p == null) {
                this.lastActivated = active;
                return;
            }
            Comparable val = p.valueByName(String.valueOf(active));
            if (val == null) {
                return;
            }
            ImmutableBlockState ns = ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, val);
            if (ns == state) {
                this.lastActivated = active;
                return;
            }
            level.setBlock(pos, (BlockState)ns.customBlockState().minecraftState(), 2);
            this.lastActivated = active;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void setChanged() {
        if (this.menu != null && !this.menu.getInventory().getViewers().isEmpty()) {
            this.menu.syncFromMachine();
        }
    }

    public boolean canOutputRedstone(net.minecraft.core.Direction dir) {
        IOConfiguration config = this.getIOConfiguration();
        return config != null && config.providesOutput(IOConfiguration.IOType.REDSTONE, dir);
    }

    public PolyContext buildEvalContext() {
        return null;
    }

    private static final class FunnelTransit {
        ItemStack item;
        float progress;
        float jitter;
        boolean out;
        ConveyorItemDisplay display;
        boolean spawned;

        private FunnelTransit() {
        }
    }
}

