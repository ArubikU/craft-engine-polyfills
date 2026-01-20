package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.Arrays;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.FluidInput;
import dev.arubik.craftengine.machine.recipe.GasOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

public class TestMachineBlockEntity extends AbstractMachineBlockEntity {

    private final MachineLayout layout = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 9,
            "Test Machine")
            .addSlot(0, MenuSlotType.INPUT)
            .addSlot(1, MenuSlotType.OUTPUT);

    public TestMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        super(2, pos, state);

        // Initialize Tanks
        // Tank 0: Water only (10B)
        addFluidTank(new FluidTank("water_input", 10000, FluidType.WATER));
        // Tank 1: Lava only (10B)
        addFluidTank(new FluidTank("lava_output", 10000, FluidType.LAVA));

        // Gas Tank 0: Steam only (10B)
        addGasTank(new GasTank("steam_io", 10000, GasType.STEAM));

        // Configure Default IO
        IOConfiguration.Simple config = new IOConfiguration.Simple();

        // Fluid: In West (Slot 0), Out East (Slot 1)
        config.withInputSlot(IOType.FLUID, 0, Direction.WEST);
        config.addInput(IOType.FLUID, Direction.WEST);

        config.withOutputSlot(IOType.FLUID, 1, Direction.EAST);
        config.addOutput(IOType.FLUID, Direction.EAST);

        // Gas: In Up, Out Down (Both Slot 0)
        config.addInput(IOType.GAS, Direction.UP);
        config.addOutput(IOType.GAS, Direction.DOWN); // Default slot -1 -> 0

        // Item: In North, Out South
        config.addInput(IOType.ITEM, Direction.NORTH);
        config.addOutput(IOType.ITEM, Direction.SOUTH);

        config.setSlots(IOType.ITEM, IOConfiguration.IORole.INPUT, 0);
        config.setSlots(IOType.ITEM, IOConfiguration.IORole.OUTPUT, 1);
        config.setSlots(IOType.ITEM, IOConfiguration.IORole.FUEL, new int[] {});

        this.setIOConfiguration(config);

        // Dynamic Layout Configuration
        this.layout.setDynamicTitle((machine) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            return "Test Machine (" + (testMachine.isProcessing() ? "Running" : "Idle") + ")";
        });

        // Tank Readout Slots (2: Water, 3: Lava, 4: Steam)
        this.layout.setDynamicProvider(2, (machine, tick) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            dev.arubik.craftengine.fluid.FluidTank tank = testMachine.fluidTanks.get(0);
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.WATER_BUCKET);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(
                    "§bWater: " + testMachine.get(tank.getKey()).getAmount()
                            + " / " + tank.getCapacity() + " mB");
            stack.setItemMeta(meta);
            return stack;
        });

        this.layout.setDynamicProvider(3, (machine, tick) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            dev.arubik.craftengine.fluid.FluidTank tank = testMachine.fluidTanks.get(0);
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.LAVA_BUCKET);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(
                    "§6Lava: " + testMachine.get(tank.getKey()).getAmount()
                            + " / " + tank.getCapacity() + " mB");
            stack.setItemMeta(meta);
            return stack;
        });

        this.layout.setDynamicProvider(4, (machine, tick) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            if (testMachine.gasTanks.isEmpty())
                return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
            dev.arubik.craftengine.gas.GasTank tank = testMachine.gasTanks.get(0);
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    org.bukkit.Material.WHITE_STAINED_GLASS);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(
                    "§fSteam: " + testMachine.get(tank.getKey()).getAmount()
                            + " / " + tank.getCapacity() + " mB");
            stack.setItemMeta(meta);
            return stack;
        });

        // Status Slot (Slot 8)
        this.layout.setDynamicProvider(8, (machine, tick) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            boolean processing = testMachine.isProcessing();
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    processing ? org.bukkit.Material.LIME_STAINED_GLASS_PANE
                            : org.bukkit.Material.RED_STAINED_GLASS_PANE);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(processing ? "§aProcessing" : "§cIdle");
            stack.setItemMeta(meta);
            return stack;
        });

        // Toggle Button (Slot 7)
        this.layout.addButton(7, (machine, tick) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            boolean debug = testMachine.debugMode;
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                    debug ? org.bukkit.Material.LEVER : org.bukkit.Material.STICK);
            org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("§eDebug Mode: " + (debug ? "ON" : "OFF"));
            stack.setItemMeta(meta);
            return stack;
        }, (machine, player) -> {
            TestMachineBlockEntity testMachine = (TestMachineBlockEntity) machine;
            testMachine.debugMode = !testMachine.debugMode;
            testMachine.setChanged();
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1f, 1f);
        });
    }

    private boolean debugMode = false;

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        if (!fluidTanks.isEmpty()
                && fluidTanks.get(0).getFluid(level, getMachinePos()).getAmount() >= 1000) {
            return AbstractProcessingRecipe.builder().addFluidInput(FluidType.WATER, 1000)
                    .addGasOutput(GasType.STEAM, 1000).setProcessTime(100)
                    .setFuelRequired(true)
                    .setProcessTime(100)
                    .build();
        }

        return null;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        // Check gas steam capacity
        if (!gasTanks.isEmpty()) {
            return gasTanks.get(0).getCapacity()
                    - gasTanks.get(0).getGas(level, getMachinePos()).getAmount() >= 1000;
        }
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        if (!fluidTanks.isEmpty()) {
            fluidTanks.get(0).extract(level, getMachinePos(), 1000, null);
        }
    }

    @Override
    protected String getMachineId() {
        return "test_machine";
    }
}
