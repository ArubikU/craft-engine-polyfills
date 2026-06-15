package dev.arubik.craftengine.multiblock.impl;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.GasInput;
import dev.arubik.craftengine.machine.recipe.GasOutput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class IndustrialSmelterBlockEntity extends MultiBlockMachineBlockEntity {

    // Inventory Slots
    private static final int[] INPUT_SLOTS = { 0, 1, 2 };
    private static final int[] FUEL_SLOTS = { 3, 4 };
    private static final int[] OUTPUT_SLOTS = { 5, 6, 7, 8 };

    private static final int WATER_CAPACITY = 10000;
    private static final int STEAM_CAPACITY = 10000;

    // Logic
    private int progress = 0;
    private int maxProgress = 100;
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    private final MachineLayout layout;

    public IndustrialSmelterBlockEntity(net.momirealms.craftengine.core.block.entity.BlockEntity blockEntity,
            MultiBlockSchema schema) {
        super(9, blockEntity, schema);

        // Add tanks using base class methods
        addFluidTank(new FluidTank("water_input", WATER_CAPACITY, FluidType.WATER));
        addGasTank(new GasTank("steam_output", STEAM_CAPACITY, GasType.STEAM));

        // Create layout
        this.layout = new MachineLayout(InventoryType.CHEST, 27, "Industrial Smelter");
        setupLayout();
    }

    private void setupLayout() {
        // visualize slots
        for (int i : INPUT_SLOTS)
            layout.addSlot(i, MenuSlotType.INPUT);
        for (int i : FUEL_SLOTS)
            layout.addSlot(i, MenuSlotType.FUEL);
        for (int i : OUTPUT_SLOTS)
            layout.addSlot(i, MenuSlotType.OUTPUT);

        // Decoration
        layout.fillBackground(new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        // Water Tank Display (Slot 9)
        layout.setDynamicProvider(9, (machine, tick) -> {
            IndustrialSmelterBlockEntity smelter = (IndustrialSmelterBlockEntity) machine;
            FluidTank tank = smelter.fluidTanks.get(0);
            FluidStack fluid = smelter.get(tank.getKey());

            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    fluid.isEmpty() ? Material.BUCKET : Material.WATER_BUCKET);
            stack.editMeta(meta -> meta.displayName(dev.arubik.craftengine.machine.menu.MenuText.noI(
                    dev.arubik.craftengine.machine.menu.MenuText.kv("polyfill.liquid.water",
                            net.kyori.adventure.text.format.NamedTextColor.AQUA,
                            fluid.getAmount() + " / " + WATER_CAPACITY + " mB",
                            net.kyori.adventure.text.format.NamedTextColor.WHITE))));
            return stack;
        });

        // Steam Tank Display (Slot 10)
        layout.setDynamicProvider(10, (machine, tick) -> {
            IndustrialSmelterBlockEntity smelter = (IndustrialSmelterBlockEntity) machine;
            GasTank tank = smelter.gasTanks.get(0);
            GasStack gas = smelter.get(tank.getKey());

            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(Material.WHITE_STAINED_GLASS);
            stack.editMeta(meta -> meta.displayName(dev.arubik.craftengine.machine.menu.MenuText.noI(
                    dev.arubik.craftengine.machine.menu.MenuText.kv("polyfill.gas.steam",
                            net.kyori.adventure.text.format.NamedTextColor.WHITE,
                            gas.getAmount() + " / " + STEAM_CAPACITY + " mB",
                            net.kyori.adventure.text.format.NamedTextColor.WHITE))));
            return stack;
        });

        // Progress Display (Slot 11)
        layout.setDynamicProvider(11, (machine, tick) -> {
            IndustrialSmelterBlockEntity smelter = (IndustrialSmelterBlockEntity) machine;
            int percent = smelter.maxProgress > 0 ? (smelter.progress * 100 / smelter.maxProgress) : 0;
            boolean processing = smelter.progress > 0;

            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    processing ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
            stack.editMeta(meta -> {
                meta.setDisplayName(processing ? "§aProcessing: " + percent + "%" : "§cIdle");
            });
            return stack;
        });

        // Fuel Display (Slot 12)
        layout.setDynamicProvider(12, (machine, tick) -> {
            IndustrialSmelterBlockEntity smelter = (IndustrialSmelterBlockEntity) machine;
            int percent = smelter.maxFuelTime > 0 ? (smelter.fuelTime * 100 / smelter.maxFuelTime) : 0;

            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    smelter.fuelTime > 0 ? Material.BLAZE_POWDER : Material.COAL);
            stack.editMeta(meta -> {
                meta.setDisplayName("§6Fuel: " + percent + "%");
            });
            return stack;
        });
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;

        boolean dirty = false;

        // Fuel Logic
        if (fuelTime > 0) {
            fuelTime--;
            dirty = true;
        }

        if (fuelTime <= 0) {
            // Try consume fuel
            if (canProcess(level)) {
                if (consumeFuel()) {
                    dirty = true;
                }
            }
        }

        // Processing Logic
        if (fuelTime > 0 && canProcess(level)) {
            progress += 2; // 2x Speed
            if (progress >= maxProgress) {
                process(level);
                progress = 0;
            }
            dirty = true;
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 2);
                dirty = true;
            }
        }

        if (dirty) {
            setChanged();
        }
    }

    private boolean canProcess(Level level) {
        AbstractProcessingRecipe recipe = getMatchingRecipe(level);
        if (recipe == null)
            return false;

        // Check outputs
        for (RecipeOutput out : recipe.getOutputs()) {
            if (!canFitOutput(level, out))
                return false;
        }

        // Update maxProgress
        this.maxProgress = recipe.getProcessTime();

        return true;
    }

    private void process(Level level) {
        AbstractProcessingRecipe recipe = getMatchingRecipe(level);
        if (recipe != null) {
            consumeInputs(level, recipe);

            // Craft outputs
            for (RecipeOutput out : recipe.getOutputs()) {
                addOutput(level, out);
            }
        }
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes("industrial_smelter")) {
            if (matchesRecipe(recipe, level)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matchesRecipe(AbstractProcessingRecipe recipe, Level level) {
        BlockPos pos = getMachinePos();

        for (RecipeInput input : recipe.getInputs()) {
            if (input instanceof FluidInput fluidInput) {
                // Check Fluid Tank
                if (fluidTanks.isEmpty())
                    return false;
                FluidStack inTank = fluidTanks.get(0).getFluid(level, pos);
                if (!fluidInput.matches(inTank)) {
                    return false;
                }
            } else if (input instanceof GasInput) {
                // No Gas Input support currently
                return false;
            } else {
                // Item Input
                boolean found = false;
                for (int slot : INPUT_SLOTS) {
                    ItemStack stack = getItem(slot);
                    if (!stack.isEmpty() && input.matches(stack)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return false;
            }
        }

        return true;
    }

    private boolean consumeFuel() {
        Level level = (Level) blockEntity().world().world.minecraftWorld();
        for (int slot : FUEL_SLOTS) {
            ItemStack stack = getItem(slot);
            dev.arubik.craftengine.machine.recipe.MachineFuelRecipe recipe = RecipeManager.getFuel("solid_fuel",
                    stack);

            if (recipe != null && stack.getCount() >= recipe.getInput().getAmount()
                    && canFitReplacement(level, recipe, slot)) {
                int burn = recipe.getBurnTime();

                boolean consumedInPlace = placeReplacement(level, recipe, slot);
                if (!consumedInPlace) {
                    removeItem(slot, recipe.getInput().getAmount());
                }

                fuelTime += burn;
                maxFuelTime = burn;
                return true;
            }
        }
        return false;
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
            for (int slot : OUTPUT_SLOTS) {
                ItemStack slotStack = getItem(slot);
                if (slotStack.isEmpty())
                    return true;
                if (ItemStack.isSameItem(slotStack, itemOutput)) {
                    if (slotStack.getCount() + itemOutput.getCount() <= slotStack.getMaxStackSize()) {
                        return true;
                    }
                }
            }

            // Also check FUEL_SLOTS for placement
            for (int slot : FUEL_SLOTS) {
                if (slot == fuelSlot)
                    continue; // Skip current slot
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
                // Must go to output or fuel slot
                for (int slot : OUTPUT_SLOTS) {
                    ItemStack slotStack = getItem(slot);
                    if (slotStack.isEmpty()) {
                        setItem(slot, itemOutput.copy());
                        return false;
                    }
                    if (ItemStack.isSameItem(slotStack, itemOutput)) {
                        if (slotStack.getCount() + itemOutput.getCount() <= slotStack.getMaxStackSize()) {
                            slotStack.grow(itemOutput.getCount());
                            return false;
                        }
                    }
                }

                // Try FUEL_SLOTS
                for (int slot : FUEL_SLOTS) {
                    if (slot == fuelSlot)
                        continue;
                    ItemStack slotStack = getItem(slot);
                    if (slotStack.isEmpty()) {
                        setItem(slot, itemOutput.copy());
                        return false;
                    }
                    if (ItemStack.isSameItem(slotStack, itemOutput)) {
                        if (slotStack.getCount() + itemOutput.getCount() <= slotStack.getMaxStackSize()) {
                            slotStack.grow(itemOutput.getCount());
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (output instanceof dev.arubik.craftengine.machine.recipe.FluidOutput) {
            return false; // No Fluid Output support
        }

        if (output instanceof GasOutput gasOut) {
            if (gasTanks.isEmpty())
                return false;
            GasStack current = gasTanks.get(0).getGas(level, getMachinePos());
            if (current.isEmpty())
                return true;
            GasStack outputGas = (GasStack) gasOut.getOutput();
            return current.getType() == outputGas.getType()
                    && current.getAmount() + outputGas.getAmount() <= gasTanks.get(0).getCapacity();
        }

        ItemStack outStack = (ItemStack) output.getOutput();
        if (outStack == null || outStack.isEmpty())
            return true;

        for (int slot : OUTPUT_SLOTS) {
            ItemStack cur = getItem(slot);
            if (cur.isEmpty())
                return true;
            if (ItemStack.isSameItemSameComponents(cur, outStack)
                    && cur.getCount() + outStack.getCount() <= cur.getMaxStackSize())
                return true;
        }
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        BlockPos pos = getMachinePos();

        for (RecipeInput input : recipe.getInputs()) {
            if (input instanceof FluidInput fluidInput) {
                // Consume Fluid
                if (!fluidTanks.isEmpty()) {
                    // Extract the amount from the fluid input
                    fluidTanks.get(0).extract(level, pos, 100, null);
                }
            } else if (input instanceof GasInput) {
                // Ignore
            } else {
                // Item consume
                for (int slot : INPUT_SLOTS) {
                    ItemStack inSlot = getItem(slot);
                    if (!inSlot.isEmpty() && input.matches(inSlot)) {
                        removeItem(slot, 1);
                        break;
                    }
                }
            }
        }
    }

    private void addOutput(Level level, RecipeOutput output) {
        if (output instanceof GasOutput gasOut) {
            if (!gasTanks.isEmpty()) {
                GasStack outputGas = (GasStack) gasOut.getOutput();
                gasTanks.get(0).insert(level, getMachinePos(), outputGas.copy());
            }
            return;
        }

        if (output instanceof dev.arubik.craftengine.machine.recipe.FluidOutput)
            return;

        ItemStack outStack = (ItemStack) output.getOutput();
        if (outStack == null || outStack.isEmpty())
            return;

        for (int slot : OUTPUT_SLOTS) {
            ItemStack cur = getItem(slot);
            if (cur.isEmpty()) {
                setItem(slot, outStack.copy());
                return;
            }
            if (ItemStack.isSameItemSameComponents(cur, outStack)
                    && cur.getCount() + outStack.getCount() <= cur.getMaxStackSize()) {
                cur.grow(outStack.getCount());
                return;
            }
        }
    }

    @Override
    protected String getMachineId() {
        return "industrial_smelter";
    }
}
