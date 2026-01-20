package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMachineBlockEntity extends PersistentWorldlyBlockEntity {

    protected int progress = 0;
    protected int maxProgress = 0;
    protected int burnTime = 0;
    protected int maxBurnTime = 0;
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

    // Constructor updated: No Level, uses ImmutableBlockState
    public AbstractMachineBlockEntity(int size, net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        super(pos, state, size);
    }

    public net.minecraft.core.BlockPos getMachinePos() {
        return net.minecraft.core.BlockPos.of(this.pos.asLong());
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
        // If using RelativeIO, configure facing from block state using
        // getMachineLevel() if available?
        // Actually, getFacing() needs level.
        if (ioConfiguration instanceof dev.arubik.craftengine.multiblock.IOConfiguration.RelativeIO relativeIO) {
            // Need level to resolve facing for RelativeIO
            // This is tricky without Level passed to getIOConfiguration
            // But usually IOConfig is set up once or we need to change how IOConfig works
            // if it depends on Level dynamically
            // For now, if we can't get Level, we can't resolve RelativeIO facing here
            // easily without changing signature
            // But wait, getIOConfiguration is often called without Level.
            // Let's defer this or require Level.
            // Actually, for RelativeIO, the facing should probably be resolved when the
            // machine loads or state changes, not on every get.
        }
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

        // Try full direction first (6-way)
        try {
            @SuppressWarnings("unchecked")
            net.momirealms.craftengine.core.block.properties.Property<net.minecraft.core.Direction> fullFacingProp = (net.momirealms.craftengine.core.block.properties.Property<net.minecraft.core.Direction>) customState
                    .customBlockState().getProperty("facing");

            if (fullFacingProp != null) {
                return customState.get(fullFacingProp);
            }
        } catch (Exception e) {
        }

        // Try horizontal direction (4-way)
        try {
            @SuppressWarnings("unchecked")
            net.momirealms.craftengine.core.block.properties.Property<net.momirealms.craftengine.core.util.HorizontalDirection> horizontalFacingProp = customState
                    .customBlockState().getProperty("facing");

            if (horizontalFacingProp != null) {
                net.momirealms.craftengine.core.util.HorizontalDirection horizontalFacing = customState
                        .get(horizontalFacingProp);
                return dev.arubik.craftengine.multiblock.DirectionalIOHelper.fromHorizontalDirection(horizontalFacing);
            }
        } catch (Exception e) {
        }

        return null;
    }

    // Updated: fillTank requires Level
    public boolean fillTank(Level level, FluidStack fluid) {
        boolean changed = false;
        for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
            int accepted = tank.insert(level, BlockPos.of(pos.asLong()), fluid);
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
            if (!getIOConfiguration().acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    side)) {
                return 0;
            }
        }

        FluidStack copy = stack.copy();
        int accepted = 0;
        boolean changed = false;

        int targetSlot = slot;
        if (targetSlot == -1 && ioConfiguration != null) {
            targetSlot = getIOConfiguration()
                    .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, side);
        }

        if (targetSlot != -1) {
            if (targetSlot >= 0 && targetSlot < fluidTanks.size()) {
                accepted = fluidTanks.get(targetSlot).insert(level, BlockPos.of(pos.asLong()), copy);
                if (accepted > 0)
                    changed = true;
            }
        } else {
            for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
                int moved = tank.insert(level, BlockPos.of(pos.asLong()), copy);
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

    // Updated: extractFluid requires Level
    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side) {
        return extractFluid(level, max, drained, side, -1);
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side,
            int slot) {
        if (ioConfiguration != null) {
            if (!getIOConfiguration().providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    side)) {
                return 0;
            }
        }

        final boolean[] changed = { false };
        Consumer<FluidStack> hookDrained = (s) -> {
            if (drained != null)
                drained.accept(s);
            changed[0] = true;
        };

        int targetSlot = slot;
        if (targetSlot == -1 && ioConfiguration != null) {
            targetSlot = getIOConfiguration()
                    .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID, side);
        }

        if (targetSlot != -1) {
            if (targetSlot >= 0 && targetSlot < fluidTanks.size()) {
                int extracted = fluidTanks.get(targetSlot).extract(level, BlockPos.of(pos.asLong()), max, hookDrained);
                if (changed[0])
                    setChanged();
                return extracted;
            }
            return 0;
        }

        for (dev.arubik.craftengine.fluid.FluidTank tank : fluidTanks) {
            int extracted = tank.extract(level, BlockPos.of(pos.asLong()), max, hookDrained);
            if (extracted > 0) {
                if (changed[0])
                    setChanged();
                return extracted;
            }
        }

        return 0;
    }

    // Updated: fillGasTank requires Level
    public void fillGasTank(Level level, dev.arubik.craftengine.gas.GasStack gas) {
        boolean changed = false;
        for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
            int accepted = tank.insert(level, BlockPos.of(pos.asLong()), gas);
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
            if (!getIOConfiguration().acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    side)) {
                return 0;
            }
        }

        if (stack == null || stack.isEmpty())
            return 0;

        dev.arubik.craftengine.gas.GasStack copy = new dev.arubik.craftengine.gas.GasStack(stack.getType(),
                stack.getAmount());
        int originalAmount = copy.getAmount();
        boolean changed = false;

        int targetSlot = slot;
        if (targetSlot == -1 && ioConfiguration != null) {
            targetSlot = getIOConfiguration()
                    .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, side);
        }

        if (targetSlot != -1) {
            if (targetSlot >= 0 && targetSlot < gasTanks.size()) {
                int accepted = gasTanks.get(targetSlot).insert(level, BlockPos.of(pos.asLong()), copy);
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

    // Updated: extractGas requires Level
    public int extractGas(Level level, int max, Consumer<dev.arubik.craftengine.gas.GasStack> drained,
            net.minecraft.core.Direction side) {
        return extractGas(level, max, drained, side, -1);
    }

    public int extractGas(Level level, int max, Consumer<dev.arubik.craftengine.gas.GasStack> drained,
            net.minecraft.core.Direction side, int slot) {
        if (ioConfiguration != null) {
            if (!getIOConfiguration().providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    side)) {
                return 0;
            }
        }

        final boolean[] changed = { false };
        Consumer<dev.arubik.craftengine.gas.GasStack> hookDrained = (s) -> {
            if (drained != null)
                drained.accept(s);
            changed[0] = true;
        };

        int targetSlot = slot;
        if (targetSlot == -1 && ioConfiguration != null) {
            targetSlot = getIOConfiguration()
                    .getTargetSlot(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS, side);
        }

        if (targetSlot != -1) {
            if (targetSlot >= 0 && targetSlot < gasTanks.size()) {
                int extracted = gasTanks.get(targetSlot).extract(level, BlockPos.of(pos.asLong()), max, hookDrained);
                if (changed[0])
                    setChanged();
                return extracted;
            }
            return 0;
        }

        for (dev.arubik.craftengine.gas.GasTank tank : gasTanks) {
            int extracted = tank.extract(level, BlockPos.of(pos.asLong()), max, hookDrained);
            if (extracted > 0) {
                if (changed[0])
                    setChanged();
                return extracted;
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
                progress++;
                if (maxProgress == 0)
                    maxProgress = recipe.getProcessTime();

                if (progress >= maxProgress) {
                    process(level, recipe);
                    progress = 0;
                    isProcessing = false;
                }
                setChanged();
            }
        } else {
            isProcessing = false;
            progress = 0;
            setChanged();
        }
    }

    // Updated abstract methods to take Level if needed
    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level level);

    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (recipe == null)
            return false;

        for (dev.arubik.craftengine.machine.recipe.condition.RecipeCondition condition : recipe.getConditions()) {
            if (!condition.test(level, this)) {
                return false;
            }
        }

        for (RecipeOutput output : recipe.getOutputs()) {
            if (!canFitOutput(level, output))
                return false;
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
        boolean hasSignal = level.hasNeighborSignal(BlockPos.of(pos.asLong()));
        return invertRedstone ? !hasSignal : hasSignal;
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

    public int[] getFuelSlots() {
        if (ioConfiguration != null) {
            return ioConfiguration.getSlots(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM,
                    dev.arubik.craftengine.multiblock.IOConfiguration.IORole.FUEL);
        }
        return new int[0];
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

    @Override
    protected void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_PROGRESS, progress);
        set(KEY_MAX_PROGRESS, maxProgress);
        set(KEY_BURN_TIME, burnTime);
        set(KEY_MAX_BURN_TIME, maxBurnTime);
        set(KEY_XP, storedXp);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.progress = getOrDefault(KEY_PROGRESS, 0);
        this.maxProgress = getOrDefault(KEY_MAX_PROGRESS, 0);
        this.burnTime = getOrDefault(KEY_BURN_TIME, 0);
        this.maxBurnTime = getOrDefault(KEY_MAX_BURN_TIME, 0);
        this.storedXp = getOrDefault(KEY_XP, 0f);
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
                int burn = recipe.getBurnTime();

                boolean consumedInPlace = placeReplacement(level, recipe, slot);
                if (!consumedInPlace) {
                    removeItem(slot, recipe.getInput().getAmount());
                }

                this.burnTime += burn;
                this.maxBurnTime = burn;
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

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        if (ioConfiguration == null)

        {
            int[] all = new int[getContainerSize()];
            for (int i = 0; i < all.length; i++)
                all[i] = i;
            return all;
        }

        boolean acceptsInput = ioConfiguration
                .acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
        boolean providesOutput = ioConfiguration
                .providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);

        if (!acceptsInput && !providesOutput) {
            return new int[0];
        }

        List<Integer> slots = new ArrayList<>();
        int[] inputs = getInputSlots();
        int[] outputs = getOutputSlots();
        int[] fuels = getFuelSlots();

        if (acceptsInput) {
            for (int i : inputs)
                slots.add(i);
            for (int i : fuels)
                slots.add(i);
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
        return ioConfiguration.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction side) {
        if (ioConfiguration == null) {
            return true;
        }
        return ioConfiguration.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, side);
    }

    public void openMenu(Player player) {
        getMenu().open((org.bukkit.entity.Player) player.getBukkitEntity());
    }

    // Tick updated: accepts Level
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (this.menu != null) {
            this.menu.tick();
        }
        processTick(level);
    }

    @Override
    public void setChanged() {
        if (this.menu != null) {
            this.menu.syncFromMachine();
        }
    }

    public boolean canOutputRedstone(Direction dir) {
        IOConfiguration config = getIOConfiguration();
        return config != null && config.providesOutput(IOConfiguration.IOType.REDSTONE, dir);
    }
}
