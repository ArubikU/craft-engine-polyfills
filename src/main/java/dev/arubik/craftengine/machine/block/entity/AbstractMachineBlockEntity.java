package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MachineMode;
import dev.arubik.craftengine.multiblock.impl.MachineType;
import dev.arubik.craftengine.util.DirectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.property.Property;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractMachineBlockEntity extends PersistentWorldlyBlockEntity
        implements dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver {

    protected int progress = 0;
    // Fractional carry so a speed multiplier BELOW 1.0 (underclock) actually slows processing:
    // each tick we add speedMultiplier (e.g. 0.5) and only advance whole progress when it accrues.
    // Transient — the lost fraction on reload is negligible.
    protected double progressCarry = 0.0;
    protected int maxProgress = 0;
    protected int burnTime = 0;
    protected int maxBurnTime = 0;
    protected int overclockedTicks = 0; // Ticks remaining in overclocked mode (0 = not overclocked)
    protected boolean isProcessing = false;

    public boolean isProcessing() {
        return isProcessing;
    }

    // I/O Configuration - New unified system
    protected dev.arubik.craftengine.multiblock.IOConfiguration ioConfiguration;

    // Redstone control
    protected boolean requiresRedstone = false; // If true, machine only runs with redstone signal
    protected boolean invertRedstone = false; // If true, machine runs when redstone is OFF

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getBurnTime() {
        return burnTime;
    }

    // Constructor updated for BlockEntityController composition model.
    public AbstractMachineBlockEntity(net.momirealms.craftengine.core.block.entity.BlockEntity blockEntity, int size) {
        super(blockEntity, size);
    }

    /** CE position of the backing block entity. */
    public net.momirealms.craftengine.core.world.BlockPos pos() {
        return blockEntity().pos();
    }

    public net.minecraft.core.BlockPos getMachinePos() {
        return net.minecraft.core.BlockPos.of(blockEntity().pos().asLong());
    }

    // --- XP System ---
    protected float storedXp = 0;
    private static final dev.arubik.craftengine.util.TypedKey<Float> KEY_XP = dev.arubik.craftengine.util.TypedKey.of(
            "craftengine", "machine_xp",
            org.bukkit.persistence.PersistentDataType.FLOAT);

    public float getStoredXp() {
        return storedXp;
    }

    public void accumulateXp(float amount) {
        this.storedXp += amount;
        setChanged();
    }

    public void awardXp(Player player) {
        if (storedXp > 0) {
            // Give XP to player (Paper/Spigot API)
            player.giveExperiencePoints((int) storedXp);
            storedXp = 0;
            setChanged();
        }
    }

    // --- Fluid & Gas System ---
    protected final List<dev.arubik.craftengine.fluid.FluidTank> fluidTanks = new ArrayList<>();
    protected final List<dev.arubik.craftengine.gas.GasTank> gasTanks = new ArrayList<>();

    public FluidStack getFluidInSlot(int slot) {
        return get(fluidTanks.get(slot).getKey());
    }

    // ---- hydraulic engine carrier hooks (the machine's REAL fluid store) ----
    public FluidStack getStoredFluidForCarrier() {
        // Read the SAME store that setStoredFluidRaw / writeTank write: the FluidTank's CustomBlockData
        // (PDC), NOT the in-memory get(key) container. They are SEPARATE stores — reading get(key) made
        // the engine see an empty tank while the pump's PDC tank actually held fluid (lava/xp never pushed).
        if (fluidTanks.isEmpty())
            return FluidStack.EMPTY;
        FluidStack s = fluidTanks.get(0).getFluid(getNMSLevel(), getMachinePos());
        return s == null ? FluidStack.EMPTY : s;
    }

    public long getFluidCapacityForCarrier() {
        return fluidTanks.isEmpty() ? 0L : fluidTanks.get(0).getCapacity();
    }

    public void setStoredFluidRaw(Level level, FluidStack stack) {
        if (fluidTanks.isEmpty())
            return;
        dev.arubik.craftengine.util.TypedKey<FluidStack> key = fluidTanks.get(0).getKey();
        if (stack == null || stack.isEmpty())
            dev.arubik.craftengine.util.CustomBlockData.from(level, getMachinePos()).remove(key);
        else
            dev.arubik.craftengine.util.CustomBlockData.from(level, getMachinePos()).set(key, stack);
    }

    public GasStack getGasInSlot(int slot) {
        return get(gasTanks.get(slot).getKey());
    }

    public int insertFluidInSlot(int slot, FluidStack stack, Level level) {
        return fluidTanks.get(slot).insert(level, getMachinePos(), stack);
    }

    public int extractFluidInSlot(int slot, int amount, Level level, Consumer<FluidStack> drained) {
        return fluidTanks.get(slot).extract(level, getMachinePos(), amount, drained);
    }

    public int insertGasInSlot(int slot, GasStack stack, Level level) {
        return gasTanks.get(slot).insert(level, getMachinePos(), stack);
    }

    public int extractGasInSlot(int slot, int amount, Level level, Consumer<GasStack> drained) {
        return gasTanks.get(slot).extract(level, getMachinePos(), amount, drained);
    }

    public void addFluidTank(dev.arubik.craftengine.fluid.FluidTank tank) {
        fluidTanks.add(tank);
    }

    public void addGasTank(dev.arubik.craftengine.gas.GasTank tank) {
        gasTanks.add(tank);
    }

    public void setIOConfiguration(dev.arubik.craftengine.multiblock.IOConfiguration config) {
        this.ioConfiguration = config;
    }

    public dev.arubik.craftengine.multiblock.IOConfiguration getIOConfiguration() {
        return ioConfiguration;
    }

    // Updated: getFacing requires Level
    protected net.minecraft.core.Direction getFacing(Level level) {
        if (level == null)
            return null;
        BlockPos pos = getMachinePos();

        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        net.momirealms.craftengine.core.block.ImmutableBlockState customState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(state).orElse(null);

        if (customState == null) {
            return null;
        }

        BlockBehavior beh = customState.behavior();
        ConnectableBlockBehavior cbb = beh instanceof ConnectableBlockBehavior c ? c
                : (beh != null ? beh.getFirst(ConnectableBlockBehavior.class) : null);
        if (cbb != null) {
            net.minecraft.core.Direction d = cbb.toDirection(state);
            if (d != null)
                return d;
        }

        return Direction.NORTH;
    }

    // Updated: fillTank requires Level
    public boolean fillTank(Level level, FluidStack fluid) {
        boolean changed = false;
        for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
            int accepted = tank.insert(level, getMachinePos(), fluid);
            if (accepted > 0) {
                fluid.removeAmount(accepted);
                changed = true;
            }
        }
        if (changed)
            setChanged();
        return fluid.isEmpty();
    }

    // Updated: insertFluid requires Level
    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side) {
        return insertFluid(level, stack, side, -1);
    }

    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        if (ioConfiguration != null) {
            Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;

            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(getMachinePos());
            Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> customState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                BlockBehavior behavior = customState.get().behavior();
                if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior compositeBlockBehavior) {
                    ConnectableBlockBehavior cbb = compositeBlockBehavior.getFirst(ConnectableBlockBehavior.class);
                    if (cbb != null) {
                        localDir = cbb.toLocalDirection(side, state);
                    }
                }
            }

            if (!getIOConfiguration().acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    localDir)) {
                return 0;
            }

            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = getIOConfiguration()
                        .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                                localDir);
            }

            FluidStack copy = stack.copy();
            int accepted = 0;
            boolean changed = false;

            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < fluidTanks.size()) {
                    accepted = fluidTanks.get(targetSlot).insert(level, getMachinePos(), copy);
                    if (accepted > 0)
                        changed = true;
                }
            } else {
                for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
                    int moved = tank.insert(level, getMachinePos(), copy);
                    if (moved > 0) {
                        copy.removeAmount(moved);
                        accepted += moved;
                        changed = true;
                    }
                    if (copy.isEmpty())
                        break;
                }
            }

            if (changed)
                setChanged();
            return accepted;
        }

        return 0;
    }

    // Updated: extractFluid requires Level
    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side) {
        return extractFluid(level, max, drained, side, -1);
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side,
            int slot) {
        if (ioConfiguration != null) {
            Direction localDir = side;

            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(getMachinePos());
            Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> customState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                BlockBehavior behavior = customState.get().behavior();
                if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior compositeBlockBehavior) {
                    ConnectableBlockBehavior cbb = compositeBlockBehavior.getFirst(ConnectableBlockBehavior.class);
                    if (cbb != null) {
                        localDir = cbb.toLocalDirection(side, state);
                    }
                }
            }

            if (!getIOConfiguration().providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    localDir)) {
                return 0;
            }

            final boolean[] changed = { false };
            Consumer<FluidStack> hookDrained = (s) -> {
                if (drained != null)
                    drained.accept(s);
                changed[0] = true;
            };

            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = getIOConfiguration()
                        .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                                localDir);
            }

            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < fluidTanks.size()) {
                    int extracted = fluidTanks.get(targetSlot).extract(level, getMachinePos(), max,
                            hookDrained);
                    if (changed[0])
                        setChanged();
                    return extracted;
                }
                return 0;
            }

            for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
                int extracted = tank.extract(level, getMachinePos(), max, hookDrained);
                if (extracted > 0) {
                    if (changed[0])
                        setChanged();
                    return extracted;
                }
            }
        }

        return 0;
    }

    // Updated: fillGasTank requires Level
    public void fillGasTank(Level level, dev.arubik.craftengine.gas.GasStack gas) {
        boolean changed = false;
        for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
            int accepted = tank.insert(level, getMachinePos(), gas);
            if (accepted > 0) {
                gas.shrink(accepted);
                changed = true;
                if (gas.isEmpty())
                    break;
            }
        }
        if (changed)
            setChanged();
    }

    // Updated: insertGas requires Level
    public int insertGas(Level level, dev.arubik.craftengine.gas.GasStack stack, net.minecraft.core.Direction side) {
        return insertGas(level, stack, side, -1);
    }

    public int insertGas(Level level, dev.arubik.craftengine.gas.GasStack stack, net.minecraft.core.Direction side,
            int slot) {
        if (ioConfiguration != null) {
            Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;

            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(getMachinePos());
            Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> customState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                BlockBehavior behavior = customState.get().behavior();
                if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior compositeBlockBehavior) {
                    ConnectableBlockBehavior cbb = compositeBlockBehavior.getFirst(ConnectableBlockBehavior.class);
                    if (cbb != null) {
                        localDir = cbb.toLocalDirection(side, state);
                    }
                }
            }

            if (!getIOConfiguration().acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    localDir)) {
                return 0;
            }

            if (stack == null || stack.isEmpty())
                return 0;

            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = getIOConfiguration()
                        .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, localDir);
            }

            dev.arubik.craftengine.gas.GasStack copy = new dev.arubik.craftengine.gas.GasStack(stack.getType(),
                    stack.getAmount());
            int originalAmount = copy.getAmount();
            boolean changed = false;

            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < gasTanks.size()) {
                    int accepted = gasTanks.get(targetSlot).insert(level, getMachinePos(), copy);
                    if (accepted > 0)
                        changed = true;
                    return accepted;
                }
                return 0;
            }

            fillGasTank(level, copy);
            if (originalAmount != copy.getAmount())
                changed = true;

            if (changed)
                setChanged();
            return originalAmount - copy.getAmount();
        }

        return 0;
    }

    // Updated: extractGas requires Level
    public int extractGas(Level level, int max, Consumer<dev.arubik.craftengine.gas.GasStack> drained,
            net.minecraft.core.Direction side) {
        return extractGas(level, max, drained, side, -1);
    }

    public int extractGas(Level level, int max, Consumer<dev.arubik.craftengine.gas.GasStack> drained,
            net.minecraft.core.Direction side, int slot) {
        if (ioConfiguration != null) {
            Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;

            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(getMachinePos());
            Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> customState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                BlockBehavior behavior = customState.get().behavior();
                if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior compositeBlockBehavior) {
                    ConnectableBlockBehavior cbb = compositeBlockBehavior.getFirst(ConnectableBlockBehavior.class);
                    if (cbb != null) {
                        localDir = cbb.toLocalDirection(side, state);
                    }
                }
            }

            if (!getIOConfiguration().providesOutput(
                    dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, localDir)) {
                return 0;
            }

            final boolean[] changed = { false };
            Consumer<dev.arubik.craftengine.gas.GasStack> hookDrained = (s) -> {
                if (drained != null)
                    drained.accept(s);
                changed[0] = true;
            };

            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = getIOConfiguration()
                        .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, localDir);
            }

            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < gasTanks.size()) {
                    int extracted = gasTanks.get(targetSlot).extract(level, getMachinePos(), max,
                            hookDrained);
                    if (changed[0])
                        setChanged();
                    return extracted;
                }
                return 0;
            }

            for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
                int extracted = tank.extract(level, getMachinePos(), max, hookDrained);
                if (extracted > 0) {
                    if (changed[0])
                        setChanged();
                    return extracted;
                }
            }
        }
        return 0;
    }

    // --- Logic Update (Logic requires Level now) ---

    protected boolean requiresFuel() {
        return true;
    }

    // Updated: processTick requires Level
    protected void processTick(Level level) {
        if (requiresFuel() && burnTime > 0) {
            burnTime--;
        }

        if (level.isClientSide())
            return;

        recomputeUpgrades();

        if (requiresRedstone && !isRedstoneEnabled(level)) {
            if (progress > 0) {
                progress = Math.max(0, progress - 1);
            }
            isProcessing = false;
            return;
        }

        AbstractProcessingRecipe recipe = getMatchingRecipe(level);

        if (canProcess(level, recipe)) {
            boolean needsFuel = requiresFuel() && recipe.isFuelRequired();

            if (needsFuel && burnTime <= 0) {
                if (hasFuel(level)) {
                    consumeFuel(level);
                    setChanged();
                } else {
                    if (progress > 0) {
                        progress = Math.max(0, progress - 2);
                    }
                    isProcessing = false;
                    return;
                }
            }

            if (!needsFuel || burnTime > 0) {
                isProcessing = true;
                if (maxProgress == 0)
                    maxProgress = recipe.getProcessTime();

                // Fractional advance: speedMultiplier may be <1 (underclock) or >1 (overclock).
                progressCarry += Math.max(0.0, upgradeModifiers.speedMultiplier());
                int adv = (int) progressCarry;
                if (adv > 0) {
                    progress += adv;
                    progressCarry -= adv;
                }

                if (progress >= maxProgress) {
                    process(level, recipe);
                    progress = 0;
                    progressCarry = 0.0;
                    isProcessing = false;
                }
                setChanged();
            }
        } else {
            isProcessing = false;
            progress = 0;
            progressCarry = 0.0;
            setChanged();
        }
    }

    /** Extra %placeholder% values for a bar's name/lore (e.g. live rpm/su). Default: none. */
    public java.util.Map<String, String> barPlaceholders(String id) {
        return java.util.Collections.emptyMap();
    }

    /** The rpm a recipe ACTUALLY demands after this machine's overclock/efficiency. Default: raw. */
    public int effectiveRpm(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getMinRpm();
    }

    /** The su a recipe ACTUALLY draws after this machine's overclock. Default: raw. */
    public int effectiveSu(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getSuCost();
    }

    // Updated abstract methods to take Level if needed
    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level level);

    /** The recipe currently matching the machine's inputs (for the info icon), or null. */
    public AbstractProcessingRecipe getCurrentRecipe() {
        try {
            Level l = getNMSLevel();
            return l == null ? null : getMatchingRecipe(l);
        } catch (Throwable t) {
            return null;
        }
    }

    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (recipe == null)
            return false;

        // Check if recipe requires overclocked mode
        if (recipe.isRequireOverclocked() && !isOverclocked()) {
            return false;
        }

        // Check conditions
        for (dev.arubik.craftengine.machine.recipe.condition.RecipeCondition condition : recipe.getConditions()) {
            if (!condition.test(level, this)) {
                return false;
            }
        }

        // Check outputs
        for (RecipeOutput output : recipe.getOutputs()) {
            if (!canFitOutput(level, output)) {
                return false;
            }
        }

        return true;
    }

    protected abstract boolean canFitOutput(Level level, RecipeOutput output);

    protected void process(Level level, AbstractProcessingRecipe recipe) {
        consumeInputs(level, recipe);
        for (RecipeOutput output : recipe.getOutputs()) {
            output.dispense(level, this);
        }
    }

    protected abstract void consumeInputs(Level level, AbstractProcessingRecipe recipe);

    // --- Redstone Support ---

    protected boolean isRedstoneEnabled(Level level) {
        if (!requiresRedstone) {
            return true;
        }
        boolean hasSignal = level.hasNeighborSignal(getMachinePos());
        return invertRedstone ? !hasSignal : hasSignal;
    }

    // --- Overclocking System ---

    /**
     * Gets whether this machine is currently in overclocked mode.
     */
    public boolean isOverclocked() {
        return overclockedTicks > 0;
    }

    /**
     * Sets the number of ticks to remain overclocked.
     * 
     * @param ticks Number of ticks (0 to disable)
     */
    public void setOverclockedTicks(int ticks) {
        this.overclockedTicks = Math.max(0, ticks);
        setChanged();
    }

    /**
     * Increments the overclocked ticks by the given amount.
     * Useful for fuel that grants overclocking.
     * 
     * @param ticks Number of ticks to add
     */
    public void incrementOverclocked(int ticks) {
        this.overclockedTicks += ticks;
        setChanged();
    }

    /**
     * Gets the remaining overclocked ticks.
     */
    public int getOverclockedTicks() {
        return overclockedTicks;
    }

    // --- Helpers ---
    public boolean addOutput(ItemStack stack) {
        int[] slots = getOutputSlots();
        for (int i : slots) {
            ItemStack current = getItem(i);
            if (current.isEmpty()) {
                setItem(i, stack);
                return true;
            } else if (ItemStack.isSameItem(current, stack)) {
                int space = getMaxStackSize() - current.getCount();
                int toAdd = Math.min(space, stack.getCount());
                if (toAdd > 0) {
                    current.grow(toAdd);
                    stack.shrink(toAdd);
                    if (stack.isEmpty())
                        return true;
                }
            }
        }
        return false;
    }

    public void addXp(float amount) {
        accumulateXp(amount);
    }

    /**
     * Current {@code value, max} for a named bar stat (see {@code bars:} config + MachineBars).
     * Default exposes {@code progress}; override to add machine-specific stats (water, steam, …).
     */
    public double[] barStat(String id) {
        if ("progress".equals(id))
            return new double[] { progress, maxProgress };
        return new double[] { 0, 0 };
    }

    /**
     * Current sub-type for a bar stat (e.g. the fluid/gas TYPE in the tank) so a bar can render
     * per content type via {@code type:} on its states. Default: none.
     */
    public String barSubtype(String id) {
        return "";
    }

    /** Drop every stored item into the world and clear the container (called on break). */
    public void dropAllContents(Level level, BlockPos pos) {
        try {
            org.bukkit.World bw = level.getWorld();
            org.bukkit.Location loc = new org.bukkit.Location(bw, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            for (int i = 0; i < inventory.length; i++) {
                ItemStack st = inventory[i];
                if (st != null && !st.isEmpty()) {
                    bw.dropItemNaturally(loc, dev.arubik.craftengine.util.BridgeUtils.toBukkit(st));
                }
            }
            // Drop any item caught mid-flight across a funnel face (it was reserved out of a slot).
            for (FunnelTransit t : funnelTransits.values()) {
                if (t.item != null && !t.item.getType().isAir())
                    bw.dropItemNaturally(loc, t.item.clone());
            }
        } catch (Throwable ignored) {
        }
        despawnAllFunnelTransits(); // kill in-flight displays so none ghost after the machine breaks
        clearContent();
    }

    // --- Slot Config (Delegated to IOConfiguration) ---
    public int[] getOutputSlots() {
        if (ioConfiguration != null) {
            return ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
        }
        return new int[0];
    }

    public int[] getInputSlots() {
        if (ioConfiguration != null) {
            return ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.INPUT);
        }
        return new int[0];
    }

    /** First non-empty gas tank's contents — what a gas pipe/pump reads when pulling from this machine. */
    public dev.arubik.craftengine.gas.GasStack getStoredGasForCarrier() {
        for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
            dev.arubik.craftengine.gas.GasStack gs = tank.getGas(getNMSLevel(), getMachinePos());
            if (gs != null && !gs.isEmpty())
                return gs;
        }
        return dev.arubik.craftengine.gas.GasStack.EMPTY;
    }

    public long getGasCapacityForCarrier() {
        return gasTanks.isEmpty() ? 0L : gasTanks.get(0).getCapacity();
    }

    /** Engine apply hook for gas: write the buffer tank's CustomBlockData store (the SAME key the gas tank
     * reads — gasTanks.get(0).getKey(), NOT the GasCarrier default GasKeys.GAS, which is a different key and
     * would make the engine's writes invisible to the machine, the gas equivalent of the fluid pump bug). */
    public void setStoredGasRaw(Level level, dev.arubik.craftengine.gas.GasStack stack) {
        if (gasTanks.isEmpty())
            return;
        dev.arubik.craftengine.util.TypedKey<dev.arubik.craftengine.gas.GasStack> key = gasTanks.get(0).getKey();
        if (stack == null || stack.isEmpty())
            dev.arubik.craftengine.util.CustomBlockData.from(level, getMachinePos()).remove(key);
        else
            dev.arubik.craftengine.util.CustomBlockData.from(level, getMachinePos()).set(key, stack);
    }

    /** True if {@code bukkit} is a valid fuel for THIS machine (per its registered fuel recipes). */
    public boolean isFuelItem(org.bukkit.inventory.ItemStack bukkit) {
        if (bukkit == null || bukkit.getType().isAir())
            return false;
        return dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(
                getMachineId(), org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit)) != null;
    }

    public int[] getFuelSlots() {
        if (ioConfiguration != null) {
            return ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        }
        return new int[0];
    }

    // --- Upgrade modules ---
    protected dev.arubik.craftengine.machine.upgrade.UpgradeModifiers upgradeModifiers =
            dev.arubik.craftengine.machine.upgrade.UpgradeModifiers.NONE;

    /** Item slots that accept upgrade modules. Override to enable upgrades. Default: none. */
    public int[] getUpgradeSlots() {
        return new int[0];
    }

    /** Registry used to resolve upgrade items. Override to expose a curated set. */
    protected dev.arubik.craftengine.machine.upgrade.UpgradeRegistry upgradeRegistry() {
        return dev.arubik.craftengine.machine.upgrade.UpgradeRegistry.global();
    }

    /** Current aggregate upgrade effect (speed / fuel / yield). */
    public dev.arubik.craftengine.machine.upgrade.UpgradeModifiers getUpgradeModifiers() {
        return upgradeModifiers;
    }

    /** Re-reads upgrade slots and recomputes {@link #upgradeModifiers}. Cheap; called each process tick. */
    protected void recomputeUpgrades() {
        int[] slots = getUpgradeSlots();
        if (slots.length == 0) {
            upgradeModifiers = dev.arubik.craftengine.machine.upgrade.UpgradeModifiers.NONE;
            return;
        }
        java.util.Map<net.momirealms.craftengine.core.util.Key, Integer> counts = new java.util.HashMap<>();
        for (int slot : slots) {
            ItemStack stack = getItem(slot);
            net.momirealms.craftengine.core.util.Key id = upgradeItemId(stack);
            if (id == null) continue;
            counts.merge(id, Math.max(1, stack.getCount()), Integer::sum);
        }
        upgradeModifiers = dev.arubik.craftengine.machine.upgrade.UpgradeModifiers.compute(counts, upgradeRegistry());
    }

    /** Resolves the craft-engine custom id (or vanilla id) of an upgrade-slot item, or null if empty. */
    protected net.momirealms.craftengine.core.util.Key upgradeItemId(ItemStack nms) {
        if (nms == null || nms.isEmpty()) return null;
        org.bukkit.inventory.ItemStack bukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        net.momirealms.craftengine.core.util.Key custom =
                net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(bukkit);
        if (custom != null) return custom;
        org.bukkit.NamespacedKey nk = bukkit.getType().getKey();
        return net.momirealms.craftengine.core.util.Key.of(nk.getNamespace(), nk.getKey());
    }

    // --- Persistence ---
    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_PROGRESS = dev.arubik.craftengine.util.TypedKey
            .of("craftengine", "machine_progress",
                    org.bukkit.persistence.PersistentDataType.INTEGER);
    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_MAX_PROGRESS = dev.arubik.craftengine.util.TypedKey
            .of("craftengine", "machine_max_progress",
                    org.bukkit.persistence.PersistentDataType.INTEGER);
    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_BURN_TIME = dev.arubik.craftengine.util.TypedKey
            .of("craftengine", "machine_burn_time",
                    org.bukkit.persistence.PersistentDataType.INTEGER);
    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_MAX_BURN_TIME = dev.arubik.craftengine.util.TypedKey
            .of("craftengine", "machine_max_burn_time",
                    org.bukkit.persistence.PersistentDataType.INTEGER);
    private static final dev.arubik.craftengine.util.TypedKey<Integer> KEY_OVERCLOCKED_TICKS = dev.arubik.craftengine.util.TypedKey
            .of("craftengine", "machine_overclocked_ticks",
                    org.bukkit.persistence.PersistentDataType.INTEGER);

    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        // Persist machine scalars into the CraftEngine-native block-entity NBT (the `tag`), NOT the Bukkit
        // PDC via set(): the PDC path was not surviving restarts ("machines/pumps don't save").
        super.saveCustomData(tag);
        tag.putInt("progress", progress);
        tag.putInt("max_progress", maxProgress);
        tag.putInt("burn_time", burnTime);
        tag.putInt("max_burn_time", maxBurnTime);
        tag.putInt("overclocked_ticks", overclockedTicks);
        tag.putFloat("stored_xp", storedXp);
        // Fluid tanks: persist into the NBT tag too (the live store is CustomBlockData, which wasn't
        // surviving restarts). On load we write these back into CustomBlockData so FluidTank sees them.
        for (int i = 0; i < fluidTanks.size(); i++) {
            try {
                dev.arubik.craftengine.fluid.FluidStack f = fluidTanks.get(i).getFluid(getNMSLevel(),
                        getMachinePos());
                if (f != null && !f.isEmpty()) {
                    tag.putString("ft" + i + "_t", f.getType().name());
                    tag.putInt("ft" + i + "_a", f.getAmount());
                    tag.putInt("ft" + i + "_p", f.getPressure());
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("max_progress");
        this.burnTime = tag.getInt("burn_time");
        this.maxBurnTime = tag.getInt("max_burn_time");
        this.overclockedTicks = tag.getInt("overclocked_ticks");
        this.storedXp = tag.getFloat("stored_xp");
        for (int i = 0; i < fluidTanks.size(); i++) {
            try {
                String tn = tag.getString("ft" + i + "_t");
                if (tn != null && !tn.isEmpty()) {
                    dev.arubik.craftengine.fluid.FluidType ty = dev.arubik.craftengine.fluid.FluidType.valueOf(tn);
                    int a = tag.getInt("ft" + i + "_a");
                    int p = tag.getInt("ft" + i + "_p");
                    dev.arubik.craftengine.util.CustomBlockData.from(getNMSLevel(), getMachinePos())
                            .set(fluidTanks.get(i).getKey(), new dev.arubik.craftengine.fluid.FluidStack(ty, a, p));
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        if (menu != null) {
            menu = null;
        }
    }

    // --- Menu System ---
    private dev.arubik.craftengine.machine.menu.MachineMenu menu;

    public abstract dev.arubik.craftengine.machine.menu.layout.MachineLayout getLayout();

    public dev.arubik.craftengine.machine.menu.MachineMenu getMenu() {
        if (this.menu == null) {
            this.menu = new dev.arubik.craftengine.machine.menu.MachineMenu(this, getLayout());
            this.menu.syncFromMachine();
        }
        return this.menu;
    }

    public boolean isValidInput(int slot, ItemStack stack) {
        if (getLayout().getSlotType(slot) == dev.arubik.craftengine.machine.menu.layout.MenuSlotType.INPUT) {
            return true;
        }
        return false;
    }

    public boolean isValidFuel(ItemStack stack) {
        return getBurnDuration(stack) > 0;
    }

    protected int getBurnDuration(ItemStack stack) {
        var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    /**
     * Returns the fuel type ID for this machine (e.g., "generator",
     * "chemical_reactor").
     * Used to look up valid fuels in RecipeManager.
     */
    protected abstract String getMachineId();

    public boolean isValidFuel(FluidStack stack) {
        return dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack) != null;
    }

    protected int getFluidBurnTime(FluidStack stack) {
        var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    public boolean isValidFuel(dev.arubik.craftengine.gas.GasStack stack) {
        return dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack) != null;
    }

    protected int getGasBurnTime(dev.arubik.craftengine.gas.GasStack stack) {
        var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    private boolean canFitReplacement(Level level, dev.arubik.craftengine.machine.recipe.MachineFuelRecipe recipe,
            int fuelSlot) {
        if (recipe.getReplacement() == null)
            return true;

        Object output = recipe.getReplacement().getOutput();

        if (output instanceof ItemStack itemOutput) {
            ItemStack currentStack = getItem(fuelSlot);
            // Case 1: In-place replacement (Empty bucket principle)
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                return true; // Replaces the item in the current slot
            }

            // Case 2: Stack > Required, look for output slot
            int[] outputSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);

            for (int slot : outputSlots) {
                ItemStack slotStack = getItem(slot);
                if (slotStack.isEmpty())
                    return true;
                if (ItemStack.isSameItem(slotStack, itemOutput)) {
                    if (slotStack.getCount() + itemOutput.getCount() <= slotStack.getMaxStackSize()) {
                        return true;
                    }
                }
            }
            return false;
        } else if (output instanceof FluidStack fluidOutput) {
            // Look for output tank
            int[] outputSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
            for (int slot : outputSlots) {
                if (slot >= 0 && slot < fluidTanks.size()) {
                    dev.arubik.craftengine.fluid.FluidTank tank = fluidTanks.get(slot);
                    if (tank.getCapacity() >= fluidOutput.getAmount() && tank.allows(fluidOutput)) {
                        return true;
                    }
                }
            }
            return false;
        } else if (output instanceof dev.arubik.craftengine.gas.GasStack gasOutput) {
            // Look for output tank
            int[] outputSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
            for (int slot : outputSlots) {
                if (slot >= 0 && slot < gasTanks.size()) {
                    dev.arubik.craftengine.gas.GasTank tank = gasTanks.get(slot);
                    if (tank.getCapacity() >= gasOutput.getAmount() && tank.allows(gasOutput)) { // Assuming GasTank has
                                                                                                 // canFill or similar
                        return true;
                    }
                }
            }
            return false;
        }

        return true;
    }

    private boolean placeReplacement(Level level, dev.arubik.craftengine.machine.recipe.MachineFuelRecipe recipe,
            int fuelSlot) {
        if (recipe.getReplacement() == null)
            return false;
        Object output = recipe.getReplacement().getOutput();

        if (output instanceof ItemStack itemOutput) {
            ItemStack currentStack = getItem(fuelSlot);
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                setItem(fuelSlot, itemOutput.copy());
                return true; // Input slot modified (consumed/replaced)
            } else {
                // Must go to output
                int[] outputSlots = ioConfiguration.getSlots(
                        dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                        dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
                for (int slot : outputSlots) {
                    ItemStack slotStack = getItem(slot);
                    if (slotStack.isEmpty()) {
                        setItem(slot, itemOutput.copy());
                        return false;
                    }
                    if (ItemStack.isSameItem(currentStack, itemOutput)) {
                        if (slotStack.getCount() + itemOutput.getCount() <= slotStack.getMaxStackSize()) {
                            slotStack.grow(itemOutput.getCount());
                            return false;
                        }
                    }
                }
            }
        } else if (output instanceof FluidStack fluidOutput) {
            int[] outputSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
            for (int slot : outputSlots) {
                if (slot >= 0 && slot < fluidTanks.size()) {
                    dev.arubik.craftengine.fluid.FluidTank tank = fluidTanks.get(slot);
                    if (tank.insert(level, getMachinePos(), fluidOutput.copy()) > 0) {
                        return false;
                    }
                }
            }
            return true;
        } else if (output instanceof dev.arubik.craftengine.gas.GasStack gasOutput) {
            int[] outputSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.OUTPUT);
            for (int slot : outputSlots) {
                if (slot >= 0 && slot < gasTanks.size()) {
                    dev.arubik.craftengine.gas.GasTank tank = gasTanks.get(slot);
                    if (tank.insert(level, getMachinePos(), gasOutput.copy()) > 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean hasFuel(Level level) {
        if (ioConfiguration == null)
            return false;

        // Check Items
        int[] itemFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : itemFuelSlots) {
            ItemStack stack = getItem(slot);
            var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack);
            if (recipe != null && stack.getCount() >= recipe.getInput().getAmount()
                    && canFitReplacement(level, recipe, slot))
                return true;
        }

        // Check Fluids
        int[] fluidFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : fluidFuelSlots) {
            if (slot >= 0 && slot < fluidTanks.size()) {
                dev.arubik.craftengine.fluid.FluidTank tank = fluidTanks.get(slot);
                FluidStack fluid = tank.getFluid(level, getMachinePos());
                var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), fluid);
                if (recipe != null && fluid.getAmount() >= recipe.getInput().getAmount()
                        && canFitReplacement(level, recipe, slot))
                    return true;
            }
        }

        // Check Gases
        int[] gasFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : gasFuelSlots) {
            if (slot >= 0 && slot < gasTanks.size()) {
                dev.arubik.craftengine.gas.GasTank tank = gasTanks.get(slot);
                dev.arubik.craftengine.gas.GasStack gas = tank.getGas(level, getMachinePos());
                var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), gas);
                if (recipe != null && gas.getAmount() >= recipe.getInput().getAmount()
                        && canFitReplacement(level, recipe, slot))
                    return true;
            }
        }

        return false;
    }

    protected void consumeFuel(Level level) {
        if (ioConfiguration == null)
            return;

        // Check Items
        int[] itemFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : itemFuelSlots) {
            ItemStack stack = getItem(slot);
            var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), stack);
            if (recipe != null && stack.getCount() >= recipe.getInput().getAmount()
                    && canFitReplacement(level, recipe, slot)) {
                // Efficiency upgrades stretch each fuel item's burn time (consume less fuel).
                int burn = (int) Math.round(recipe.getBurnTime() / upgradeModifiers.fuelMultiplier());

                boolean consumedInPlace = placeReplacement(level, recipe, slot);
                if (!consumedInPlace) {
                    removeItem(slot, recipe.getInput().getAmount());
                }

                this.burnTime += burn;
                this.maxBurnTime = burn;

                // Apply overclocked time if the fuel grants it
                if (recipe.getOverclockedTime() > 0) {
                    incrementOverclocked(recipe.getOverclockedTime());
                }

                return;
            }
        }

        // Check Fluids
        int[] fluidFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : fluidFuelSlots) {
            if (slot >= 0 && slot < fluidTanks.size()) {
                dev.arubik.craftengine.fluid.FluidTank tank = fluidTanks.get(slot);
                FluidStack fluid = tank.getFluid(level, getMachinePos());
                var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), fluid);
                if (recipe != null && fluid.getAmount() >= recipe.getInput().getAmount()
                        && canFitReplacement(level, recipe, slot)) {
                    int burn = recipe.getBurnTime();
                    int toConsume = recipe.getInput().getAmount();

                    placeReplacement(level, recipe, slot); // Handles output if any

                    tank.extract(level, getMachinePos(), toConsume, (s) -> {
                    });
                    this.burnTime += burn;
                    this.maxBurnTime = burn;
                    return;
                }
            }
        }

        // Check Gases
        int[] gasFuelSlots = ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        for (int slot : gasFuelSlots) {
            if (slot >= 0 && slot < gasTanks.size()) {
                dev.arubik.craftengine.gas.GasTank tank = gasTanks.get(slot);
                dev.arubik.craftengine.gas.GasStack gas = tank.getGas(level, getMachinePos());
                var recipe = dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFuel(getMachineId(), gas);
                if (recipe != null && gas.getAmount() >= recipe.getInput().getAmount()
                        && canFitReplacement(level, recipe, slot)) {
                    int burn = recipe.getBurnTime();
                    int toConsume = recipe.getInput().getAmount();

                    placeReplacement(level, recipe, slot); // Handles output if any

                    tank.extract(level, getMachinePos(), toConsume, (s) -> {
                    });
                    this.burnTime += burn;
                    this.maxBurnTime = burn;
                    return;
                }
            }
        }
    }

    /** When true, logs every IO face decision to console. */
    public static boolean DEBUG_IO = false;

    protected void dbgIO(String msg) {
        if (DEBUG_IO)
            System.out.println("[MachineIO] " + getMachineId() + " @" + getMachinePos().toShortString() + " " + msg);
    }

    /** Convert a WORLD-space face to the machine's LOCAL face, honoring its facing rotation. */
    protected net.minecraft.core.Direction toLocalItemDir(net.minecraft.core.Direction side) {
        try {
            Level level = getNMSLevel();
            if (level == null)
                return side;
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(getMachinePos());
            Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> cs =
                    net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(state);
            if (cs.isPresent()) {
                BlockBehavior behavior = cs.get().behavior();
                if (behavior instanceof ConnectableBlockBehavior cbb)
                    return cbb.toLocalDirection(side, state);
                if (behavior instanceof CompositeBlockBehavior comp) {
                    ConnectableBlockBehavior cbb = comp.getFirst(ConnectableBlockBehavior.class);
                    if (cbb != null)
                        return cbb.toLocalDirection(side, state);
                }
            }
        } catch (Throwable ignored) {
        }
        return side;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction worldSide) {
        if (ioConfiguration == null)

        {
            int[] all = new int[getContainerSize()];
            for (int i = 0; i < all.length; i++)
                all[i] = i;
            return all;
        }

        net.minecraft.core.Direction side = toLocalItemDir(worldSide);
        boolean acceptsInput = ioConfiguration
                .acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
        boolean providesOutput = ioConfiguration
                .providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
        int dbgTarget = ioConfiguration.getTargetSlot(
                dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
        dbgIO("getSlotsForFace world=" + worldSide + " -> local=" + side
                + " in=" + acceptsInput + " out=" + providesOutput + " target=" + dbgTarget);

        if (!acceptsInput && !providesOutput) {
            return new int[0];
        }

        List<Integer> slots = new ArrayList<>();
        int[] inputs = getInputSlots();
        int[] outputs = getOutputSlots();
        int[] fuels = getFuelSlots();

        if (acceptsInput) {
            // If this face is bound to a specific input slot (withInputSlot), expose only that one
            // so a hopper/funnel on that side feeds exactly that slot (e.g. NORTH=scraping, SOUTH=quartz).
            int target = ioConfiguration.getTargetSlot(
                    dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
            if (target >= 0) {
                slots.add(target);
            } else {
                for (int i : inputs)
                    slots.add(i);
                for (int i : fuels)
                    slots.add(i);
            }
        }

        if (providesOutput) {
            for (int i : outputs)
                slots.add(i);
        }

        return slots.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction side) {
        if (ioConfiguration == null) {
            return true;
        }
        // Only INPUT slots accept insertion, and only through an input face.
        net.minecraft.core.Direction local = toLocalItemDir(side);
        boolean accepts = ioConfiguration.acceptsInput(
                dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, local);
        int target = ioConfiguration.getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, local);
        dbgIO("canPlace slot=" + slot + " world=" + side + " -> local=" + local
                + " accepts=" + accepts + " target=" + target);
        if (!accepts)
            return false;
        // Face bound to a specific slot accepts only that slot.
        if (target >= 0)
            return slot == target;
        for (int in : getInputSlots())
            if (in == slot)
                return true;
        for (int f : getFuelSlots())
            if (f == slot)
                return true;
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction side) {
        if (ioConfiguration == null) {
            return true;
        }
        // Only OUTPUT slots can be pulled, and only through an output face.
        net.minecraft.core.Direction local = toLocalItemDir(side);
        if (!ioConfiguration.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, local))
            return false;
        for (int out : getOutputSlots())
            if (out == slot)
                return true;
        return false;
    }

    public void openMenu(Player player) {
        getMenu().open((org.bukkit.entity.Player) player.getBukkitEntity());
    }

    /**
     * Whether the player may currently MODIFY (take/swap) the item in menu {@code slot}.
     * Default yes; override to freeze a slot (e.g. an upgrade that is holding other slots
     * unlocked must not be pulled while those slots are occupied).
     */
    public boolean canTakeFromSlot(int slot) {
        return true;
    }

    // Tick updated: accepts Level
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (this.menu != null) {
            this.menu.tick();
        }
        processTick(level);

        if (!level.isClientSide()) {
            pushFunnelOutputs(level);
            pullFromInputFaces(level);
        }

        // Update MACHINE_MODE property if it exists
        updateMachineModeProperty(level, pos, state);
    }

    // ============ Fluid/Gas auto-pull on INPUT faces (IO-driven, no hardcoded positions) ============

    /** Pull fluid/gas from a connected carrier (pipe/tank) on every FLUID/GAS input face. */
    protected void pullFromInputFaces(Level level) {
        if (ioConfiguration == null)
            return;
        BlockPos pos = getMachinePos();
        for (net.minecraft.core.Direction world : net.minecraft.core.Direction.values()) {
            net.minecraft.core.Direction local = toLocalItemDir(world);
            BlockPos src = pos.relative(world);
            net.minecraft.core.Direction sideFromSrc = world.getOpposite();
            if (ioConfiguration.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, local))
                pullFluidInto(level, src, sideFromSrc, ioConfiguration.getTargetSlot(
                        dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, local));
            if (ioConfiguration.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, local))
                pullGasInto(level, src, sideFromSrc, ioConfiguration.getTargetSlot(
                        dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, local));
        }
    }

    /** Extract fluid from a carrier at {@code src} into our fluid tank {@code tankIdx} (-1 = first). */
    protected void pullFluidInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (fluidTanks.isEmpty())
            return;
        int idx = (tankIdx >= 0 && tankIdx < fluidTanks.size()) ? tankIdx : 0;
        dev.arubik.craftengine.fluid.FluidTank tank = fluidTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getFluid(level, getMachinePos()).getAmount();
            if (space <= 0)
                return;
            dev.arubik.craftengine.fluid.behavior.FluidCarrier fc = fluidCarrierAt(level, src);
            if (DEBUG_IO)
                System.out.println("[Pull] " + getMachineId() + " FLUID src=" + src.toShortString()
                        + " carrier=" + (fc == null ? "null" : fc.getClass().getSimpleName()) + " space=" + space);
            if (fc == null)
                return;
            int[] got = { 0 };
            fc.extractFluid(level, src, Math.min(space, 1000), f -> {
                if (f != null && !f.isEmpty()) {
                    got[0] += f.getAmount();
                    tank.insert(level, getMachinePos(), f);
                }
            }, sideFromSrc);
            if (DEBUG_IO && got[0] > 0)
                System.out.println("[Pull]   pulled " + got[0] + " mB into tank");
        } catch (Throwable ignored) {
        }
    }

    /** Extract gas from a carrier at {@code src} into our gas tank {@code tankIdx} (-1 = first). */
    protected void pullGasInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (gasTanks.isEmpty())
            return;
        int idx = (tankIdx >= 0 && tankIdx < gasTanks.size()) ? tankIdx : 0;
        dev.arubik.craftengine.gas.GasTank tank = gasTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getGas(level, getMachinePos()).getAmount();
            if (space <= 0)
                return;
            dev.arubik.craftengine.gas.GasCarrier gc = gasCarrierAt(level, src);
            if (gc == null)
                return;
            gc.extractGas(level, src, Math.min(space, 1000), g -> {
                if (g != null && !g.isEmpty())
                    tank.insert(level, getMachinePos(), g);
            }, sideFromSrc);
        } catch (Throwable ignored) {
        }
    }

    protected static dev.arubik.craftengine.fluid.behavior.FluidCarrier fluidCarrierAt(Level level, BlockPos pos) {
        var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null)
            return null;
        var b = cs.behavior();
        if (b instanceof dev.arubik.craftengine.fluid.behavior.FluidCarrier fc)
            return fc;
        return b == null ? null : b.getFirst(dev.arubik.craftengine.fluid.behavior.FluidCarrier.class);
    }

    protected static dev.arubik.craftengine.gas.GasCarrier gasCarrierAt(Level level, BlockPos pos) {
        var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                .getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null)
            return null;
        var b = cs.behavior();
        if (b instanceof dev.arubik.craftengine.gas.GasCarrier gc)
            return gc;
        return b == null ? null : b.getFirst(dev.arubik.craftengine.gas.GasCarrier.class);
    }

    // ============ FUNNEL IO (conveyor-belt bridge, mirrors the funnel block) ============

    private static net.minecraft.core.Direction nmsDir(net.momirealms.craftengine.core.util.Direction d) {
        return net.minecraft.core.Direction.valueOf(d.name());
    }

    private static net.momirealms.craftengine.core.util.Direction coreDir(net.minecraft.core.Direction d) {
        return net.momirealms.craftengine.core.util.Direction.valueOf(d.name());
    }

    /** Any local face configured as a FUNNEL input (belt feeds the machine). */
    private boolean hasFunnelInput() {
        if (ioConfiguration == null)
            return false;
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
            if (ioConfiguration.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FUNNEL, d))
                return true;
        return false;
    }

    /** Backpressure for a feeding belt: stall only when this is a funnel target and inputs are full. */
    @Override
    public boolean isFull() {
        if (!hasFunnelInput())
            return false; // not a funnel target -> belt won't stall (it'll drop instead)
        for (int s : getInputSlots()) {
            ItemStack cur = getItem(s);
            if (cur == null || cur.isEmpty() || cur.getCount() < cur.getMaxStackSize())
                return false;
        }
        return true;
    }

    // ---- funnel-IO transit: animate items across the funnel face (mirrors the funnel block) ----
    private net.momirealms.craftengine.core.world.CEWorld ceWorld; // cached each tick for rendering

    private static final class FunnelTransit {
        org.bukkit.inventory.ItemStack item;
        float progress;
        float jitter;
        boolean out; // true = center -> face (output to belt); false = face -> center (input to slots)
        dev.arubik.craftengine.conveyor.ConveyorItemDisplay display;
        boolean spawned;
    }

    /** One in-flight item per WORLD funnel face (the visible item crossing that face). */
    private final java.util.Map<net.minecraft.core.Direction, FunnelTransit> funnelTransits =
            new java.util.EnumMap<>(net.minecraft.core.Direction.class);

    /** The world face a belt feeds when its travel direction is {@code sourceFacing}. */
    private net.minecraft.core.Direction inputFaceFor(net.momirealms.craftengine.core.util.Direction sourceFacing) {
        net.minecraft.core.Direction worldFace = nmsDir(sourceFacing.opposite());
        if (ioConfiguration == null)
            return null;
        net.minecraft.core.Direction local = toLocalItemDir(worldFace);
        if (!ioConfiguration.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FUNNEL, local))
            return null;
        return worldFace;
    }

    /** A belt pushes one item into our FUNNEL-input face -> animate across the face into a slot. */
    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack,
            net.momirealms.craftengine.core.util.Direction sourceFacing) {
        return startInputTransit(stack, sourceFacing, 0f, null, false);
    }

    @Override
    public boolean receiveConveyorItem(org.bukkit.inventory.ItemStack stack,
            net.momirealms.craftengine.core.util.Direction sourceFacing, float jitter) {
        return startInputTransit(stack, sourceFacing, jitter, null, false);
    }

    /** Seamless: adopt the sender's live display so the item doesn't flicker entering the machine. */
    @Override
    public boolean adoptConveyorItem(org.bukkit.inventory.ItemStack stack, float jitter,
            dev.arubik.craftengine.conveyor.ConveyorItemDisplay display, boolean spawned,
            net.momirealms.craftengine.core.util.Direction sourceFacing) {
        return startInputTransit(stack, sourceFacing, jitter, display, spawned);
    }

    private boolean startInputTransit(org.bukkit.inventory.ItemStack stack,
            net.momirealms.craftengine.core.util.Direction sourceFacing, float jitter,
            dev.arubik.craftengine.conveyor.ConveyorItemDisplay display, boolean spawned) {
        if (ioConfiguration == null || stack == null || stack.getType().isAir())
            return false;
        net.minecraft.core.Direction face = inputFaceFor(sourceFacing);
        if (face == null)
            return false;
        if (funnelTransits.containsKey(face) || isFull())
            return false; // one item per face in flight; stall if slots are full
        FunnelTransit t = new FunnelTransit();
        t.item = stack.clone();
        t.item.setAmount(1);
        t.jitter = jitter;
        t.out = false;
        t.progress = 0f;
        t.display = display; // null -> we spawn our own on first render
        t.spawned = spawned;
        funnelTransits.put(face, t);
        return true;
    }

    /** Adds {@code stack} fully into the input slots; true only if everything fit. */
    private boolean addToInputs(ItemStack stack) {
        boolean changed = false;
        for (int s : getInputSlots()) {
            if (stack.isEmpty())
                break;
            ItemStack cur = getItem(s);
            if (cur == null || cur.isEmpty()) {
                setItem(s, stack.copy());
                stack.setCount(0);
                changed = true;
            } else if (ItemStack.isSameItemSameComponents(cur, stack)) {
                int space = cur.getMaxStackSize() - cur.getCount();
                int move = Math.min(space, stack.getCount());
                if (move > 0) {
                    cur.grow(move);
                    stack.shrink(move);
                    changed = true;
                }
            }
        }
        if (changed)
            setChanged();
        return stack.isEmpty();
    }

    /** Each tick, start a transit that carries an output item across the FUNNEL-output face. */
    protected void pushFunnelOutputs(Level level) {
        if (ioConfiguration == null || !providesAnyFunnelOutput())
            return;
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            net.minecraft.core.Direction local = toLocalItemDir(d);
            if (!ioConfiguration.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FUNNEL, local))
                continue;
            if (funnelTransits.containsKey(d))
                continue; // already carrying an item across this face
            // Only pull when a receiver is present and has room (else the item would have nowhere to go).
            net.momirealms.craftengine.core.block.entity.BlockEntity be =
                    dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level,
                            getMachinePos().relative(d));
            if (be == null || !(be.controller instanceof dev.arubik.craftengine.conveyor.ConveyorReceiver recv))
                continue;
            if (recv instanceof AbstractMachineBlockEntity)
                continue; // don't hand off to another machine via funnel
            if (recv.isFull())
                continue;
            for (int s : getOutputSlots()) {
                ItemStack cur = getItem(s);
                if (cur == null || cur.isEmpty())
                    continue;
                org.bukkit.inventory.ItemStack one =
                        org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(cur).clone();
                one.setAmount(1);
                FunnelTransit t = new FunnelTransit();
                t.item = one;
                t.jitter = 0f;
                t.out = true;
                t.progress = 0f;
                funnelTransits.put(d, t);
                cur.shrink(1); // reserved by the transit; it's now visually in flight
                setChanged();
                break;
            }
        }
    }

    /** Advance, render, and complete every funnel-face transit (input -> slot, output -> belt). */
    private void tickFunnelTransits(net.momirealms.craftengine.core.world.CEWorld world) {
        if (funnelTransits.isEmpty())
            return;
        Level level = (Level) world.world.minecraftWorld();
        java.util.Iterator<java.util.Map.Entry<net.minecraft.core.Direction, FunnelTransit>> it =
                funnelTransits.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<net.minecraft.core.Direction, FunnelTransit> e = it.next();
            net.minecraft.core.Direction face = e.getKey();
            FunnelTransit t = e.getValue();
            // Inherit the connected belt's speed on this face so the funnel keeps pace (no bottleneck).
            float rpm = dev.arubik.craftengine.conveyor.ConveyorBlockEntity.BASE_RPM;
            net.momirealms.craftengine.core.block.entity.BlockEntity nbe =
                    dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level,
                            getMachinePos().relative(face));
            if (nbe != null && nbe.controller instanceof dev.arubik.craftengine.conveyor.ConveyorBlockEntity belt
                    && belt.effectiveRpm() > 0f)
                rpm = belt.effectiveRpm();
            float inc = dev.arubik.craftengine.conveyor.ConveyorMath.progressPerTick(rpm,
                    dev.arubik.craftengine.conveyor.ConveyorBlockEntity.BASE_RPM,
                    dev.arubik.craftengine.conveyor.ConveyorBlockEntity.BASE_TRAVEL_TICKS);
            t.progress = Math.min(1f, t.progress + inc);
            renderFunnelTransit(world, face, t);
            if (t.progress < 1f)
                continue;
            if (!t.out) {
                // Input reached the centre -> drop into the input slots; stall if they filled up.
                ItemStack nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(t.item);
                if (addToInputs(nms)) {
                    despawnFunnelTransit(world, t);
                    it.remove();
                }
                // else keep at 1.0 (rendered at centre) until a slot frees
            } else {
                // Output reached the face -> hand to the belt (transfer the display = seamless).
                net.momirealms.craftengine.core.block.entity.BlockEntity be =
                        dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level,
                                getMachinePos().relative(face));
                if (be != null && be.controller instanceof dev.arubik.craftengine.conveyor.ConveyorBlockEntity belt) {
                    if (belt.adoptFromFunnel(world, t.item, t.jitter, t.display, t.spawned, coreDir(face).opposite())) {
                        t.display = null; // entity moved on
                        it.remove();
                    }
                } else if (be != null
                        && be.controller instanceof dev.arubik.craftengine.conveyor.ConveyorDisplayReceiver dr
                        && !(dr instanceof AbstractMachineBlockEntity)) {
                    // Router (splitter/merger/depot): transfer the display -> seamless.
                    if (!dr.isFull() && dr.adoptConveyorItem(t.item, t.jitter, t.display, t.spawned, coreDir(face))) {
                        t.display = null;
                        it.remove();
                    }
                } else if (be != null && be.controller instanceof dev.arubik.craftengine.conveyor.ConveyorReceiver recv
                        && !(recv instanceof AbstractMachineBlockEntity)) {
                    if (!recv.isFull() && recv.receiveConveyorItem(t.item, coreDir(face), t.jitter)) {
                        despawnFunnelTransit(world, t);
                        it.remove();
                    }
                } else {
                    // Belt gone -> drop the item back so it isn't lost, then clear the transit.
                    addToInputs(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(t.item));
                    despawnFunnelTransit(world, t);
                    it.remove();
                }
                // else (belt full) keep at 1.0 and retry next tick
            }
        }
    }

    private void renderFunnelTransit(net.momirealms.craftengine.core.world.CEWorld world,
            net.minecraft.core.Direction face, FunnelTransit t) {
        BlockPos pos = getMachinePos();
        java.util.List<net.momirealms.craftengine.core.entity.player.Player> viewers =
                world.world().getTrackedBy(new net.momirealms.craftengine.core.world.ChunkPos(
                        new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ())));
        if (t.display == null)
            t.display = new dev.arubik.craftengine.conveyor.ConveyorItemDisplay();
        t.display.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(t.item));
        org.joml.Vector3f center = new org.joml.Vector3f(0.5f,
                dev.arubik.craftengine.conveyor.ConveyorMath.BELT_TOP_Y, 0.5f);
        org.joml.Vector3f faceEdge = new org.joml.Vector3f(0.5f + face.getStepX() * 0.5f,
                dev.arubik.craftengine.conveyor.ConveyorMath.BELT_TOP_Y, 0.5f + face.getStepZ() * 0.5f);
        org.joml.Vector3f rel = t.out
                ? dev.arubik.craftengine.conveyor.ConveyorMath.interpolate(center, faceEdge, t.progress)
                : dev.arubik.craftengine.conveyor.ConveyorMath.interpolate(faceEdge, center, t.progress);
        // Face the travel direction (out -> toward the face; in -> toward the centre).
        int mx = t.out ? face.getStepX() : -face.getStepX();
        int mz = t.out ? face.getStepZ() : -face.getStepZ();
        org.joml.Quaternionf rot = dev.arubik.craftengine.conveyor.ConveyorMath.itemRotation(mx, mz, 0);
        rot.rotateY(t.jitter);
        t.display.setRotation(rot);
        t.display.render(viewers, pos.getX() + rel.x, pos.getY() + rel.y, pos.getZ() + rel.z, !t.spawned);
        t.display.consumeRotationDirty();
        t.spawned = true;
    }

    private void despawnFunnelTransit(net.momirealms.craftengine.core.world.CEWorld world, FunnelTransit t) {
        if (t.display == null)
            return;
        for (net.momirealms.craftengine.core.entity.player.Player p : world.world().getTrackedBy(
                new net.momirealms.craftengine.core.world.ChunkPos(new net.momirealms.craftengine.core.world.BlockPos(
                        getMachinePos().getX(), getMachinePos().getY(), getMachinePos().getZ()))))
            t.display.despawn(p);
        t.display.clearShown();
        t.display = null;
        t.spawned = false;
    }

    /** Despawn every in-flight funnel-transit display (call on break/unload to avoid ghosts). */
    protected void despawnAllFunnelTransits() {
        if (ceWorld == null || funnelTransits.isEmpty())
            return;
        for (FunnelTransit t : funnelTransits.values())
            despawnFunnelTransit(ceWorld, t);
        funnelTransits.clear();
    }

    private boolean providesAnyFunnelOutput() {
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values())
            if (ioConfiguration.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FUNNEL, d))
                return true;
        return false;
    }

    // --- Ticking attach (BlockEntityController model) ---
    @Override
    public <C extends net.momirealms.craftengine.core.block.entity.BlockEntityController> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<C> createBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        return net.momirealms.craftengine.core.block.entity.BlockEntityController.createTickerHelper(
                (net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<AbstractMachineBlockEntity>) AbstractMachineBlockEntity::tickController);
    }

    /** Static ticker bridge -> instance {@link #tick(Level, BlockPos, ImmutableBlockState)}. */
    public static void tickController(net.momirealms.craftengine.core.world.CEWorld world,
            net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state, AbstractMachineBlockEntity self) {
        Level level = (Level) world.world.minecraftWorld();
        self.ceWorld = world; // cache for funnel-transit rendering (needs CE viewers)
        self.tick(level, net.minecraft.core.BlockPos.of(pos.asLong()), state);
        if (!level.isClientSide())
            self.tickFunnelTransits(world);
    }

    /**
     * Updates the MACHINE_MODE block property based on machine state.
     * Only updates if the property exists in the block state.
     */
    private void updateMachineModeProperty(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        // Check if this block has MACHINE_MODE property via its behavior
        BlockBehavior behavior = state.behavior();
        if (!(behavior instanceof dev.arubik.craftengine.machine.block.MachineBlockBehavior machineBehavior)) {
            return; // Not a machine block
        }

        Property<MachineMode> machineModeProp = machineBehavior.MACHINE_MODE;
        if (machineModeProp == null) {
            return; // Block doesn't have this property
        }

        // Determine new mode based on burnTime
        MachineMode currentMode = state.get(machineModeProp);
        MachineMode newMode;

        if (burnTime > 0) {
            newMode = MachineMode.WORKING;
        } else {
            newMode = MachineMode.IDLE;
        }

        // Only update if mode changed
        if (currentMode != newMode) {
            net.momirealms.craftengine.core.block.ImmutableBlockState newState = state.with(machineModeProp, newMode);
            level.setBlock(pos, (BlockState) newState.customBlockState().minecraftState(),
                    UpdateFlags.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public void setChanged() {
        // Re-sync slots only when the menu is actually being viewed. setChanged() fires often (fuel
        // burn, progress) and the menu instance persists after the last viewer closes, so this would
        // otherwise re-read/convert every slot forever for nobody.
        if (this.menu != null && !this.menu.getInventory().getViewers().isEmpty()) {
            this.menu.syncFromMachine();
        }
    }

    public boolean canOutputRedstone(Direction dir) {
        IOConfiguration config = getIOConfiguration();
        return config != null && config.providesOutput(IOConfiguration.IOType.REDSTONE, dir);
    }
}
