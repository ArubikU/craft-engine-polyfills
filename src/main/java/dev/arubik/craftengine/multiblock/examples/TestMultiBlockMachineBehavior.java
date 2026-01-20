package dev.arubik.craftengine.multiblock.examples;

import java.util.Map;

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
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IORole;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.multiblock.IOConfigurationProvider;
import dev.arubik.craftengine.multiblock.MultiBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiBlockMachineBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockPartBlockEntity;
import dev.arubik.craftengine.multiblock.MultiBlockSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Test MultiBlock Machine - 3x3x3 structure with full machine capability
 * Features:
 * - 2 Fluid Tanks: Water Input, Lava Output
 * - 2 Gas Tanks: Steam Input, Steam Output
 * - Processing: Water → Lava + Steam (requires fuel)
 * - Part-specific I/O using RelativeIO
 */
public class TestMultiBlockMachineBehavior extends MultiBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:test_multiblock_machine");
    public static final Factory FACTORY = new Factory();

    public TestMultiBlockMachineBehavior(CustomBlock customBlock, MultiBlockSchema schema, String partBlockId) {
        super(customBlock, schema, partBlockId);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            String partBlockId = (String) arguments.getOrDefault("part_block_id", "craftengine:multiblock_part");

            // Define 3x3x3 Schema - Core at center (1,1,1)
            BlockPos coreOffset = new BlockPos(1, 1, 1);
            MultiBlockSchema schema = new MultiBlockSchema(coreOffset);

            // All positions must be Iron Blocks
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        schema.addPart(x, y, z, (state) -> state.is(BlockTags.LOGS));
                    }
                }
            }

            TestMultiBlockMachineBehavior behavior = new TestMultiBlockMachineBehavior(block, schema, partBlockId);

            // Set IO configuration provider for part-specific I/O
            behavior.withIOProvider(new IOConfigurationProvider() {
                @Override
                public IOConfiguration configurePartIO(BlockPos relativePos) {
                    // relativePos is relative to core (0,0,0 = core position)
                    int y = relativePos.getY();

                    // Core (0,0,0) - no direct IO
                    if (relativePos.equals(BlockPos.ZERO)) {
                        return new IOConfiguration.Closed();
                    }

                    IOConfiguration.Simple config = new IOConfiguration.Simple();

                    // Top layer (y > 0) - Water input
                    if (y > 0) {
                        for (Direction dir : Direction.values()) {
                            if (dir == Direction.DOWN)
                                continue;
                            config.withInputSlot(IOType.FLUID, 0, dir);
                            config.addInput(IOType.FLUID, dir);
                        }
                    }
                    // Middle layer (y == 0) - Item I/O and fuel
                    else if (y == 0) {
                        for (Direction dir : Direction.values()) {
                            if (dir == Direction.DOWN || dir == Direction.UP)
                                continue;
                            // Items
                            config.addInput(IOType.ITEM, dir);
                            config.addOutput(IOType.ITEM, dir);
                        }
                        // Configure slots
                        config.setSlots(IOType.ITEM, IORole.INPUT, 0);
                        config.setSlots(IOType.ITEM, IORole.OUTPUT, 1);
                        config.setSlots(IOType.ITEM, IORole.FUEL, 2);
                    }
                    // Bottom layer (y < 0) - Lava and Steam output + Redstone signal
                    else {
                        for (Direction dir : Direction.values()) {
                            if (dir != Direction.DOWN)
                                continue;
                            // Lava output (fluid tank 1)
                            config.withOutputSlot(IOType.FLUID, 1, dir);
                            config.addOutput(IOType.FLUID, dir);
                            // Steam output (gas tank 1)
                            config.withOutputSlot(IOType.GAS, 1, dir);
                            config.addOutput(IOType.GAS, dir);
                            // Redstone signal output (when processing)
                            config.addOutput(IOType.REDSTONE, dir);
                        }
                    }

                    return config;
                }
            });

            return behavior;
        }
    }

    @Override
    protected MultiBlockMachineBlockEntity createMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        return new TestMultiBlockMachineBlockEntity(pos, state, schema);
    }

    /**
     * Machine Block Entity for TestMultiBlockMachine
     */
    public static class TestMultiBlockMachineBlockEntity extends MultiBlockMachineBlockEntity {

        private final MachineLayout layout;

        public TestMultiBlockMachineBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
                ImmutableBlockState state, MultiBlockSchema schema) {
            super(3, pos, state, schema); // 3 slots: input, output, fuel

            // Initialize Fluid Tanks
            addFluidTank(new FluidTank("water_input", 10000, FluidType.WATER));
            addFluidTank(new FluidTank("lava_output", 10000, FluidType.LAVA));

            // Initialize Gas Tanks
            addGasTank(new GasTank("steam_input", 10000, GasType.STEAM));
            addGasTank(new GasTank("steam_output", 10000, GasType.STEAM));

            // Configure IO
            IOConfiguration.Simple config = new IOConfiguration.Simple();
            config.setSlots(IOType.ITEM, IORole.INPUT, 0);
            config.setSlots(IOType.ITEM, IORole.OUTPUT, 1);
            config.setSlots(IOType.ITEM, IORole.FUEL, 2);
            config.setSlots(IOType.FLUID, IORole.INPUT, 0);
            config.setSlots(IOType.FLUID, IORole.OUTPUT, 1);
            config.setSlots(IOType.GAS, IORole.INPUT, 0);
            config.setSlots(IOType.GAS, IORole.OUTPUT, 1);
            this.setIOConfiguration(config);

            // Setup layout
            this.layout = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 9,
                    "Test MultiBlock Machine")
                    .addSlot(0, MenuSlotType.INPUT)
                    .addSlot(1, MenuSlotType.OUTPUT)
                    .addSlot(2, MenuSlotType.BURNING);

            // Dynamic title
            this.layout.setDynamicTitle((machine) -> {
                TestMultiBlockMachineBlockEntity mb = (TestMultiBlockMachineBlockEntity) machine;
                return "MultiBlock Machine (" + (mb.isProcessing() ? "Running" : "Idle") + ")";
            });

            // Tank displays
            this.layout.setDynamicProvider(3, (machine, tick) -> {
                TestMultiBlockMachineBlockEntity mb = (TestMultiBlockMachineBlockEntity) machine;
                FluidTank tank = mb.fluidTanks.get(0);
                org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                        org.bukkit.Material.WATER_BUCKET);
                org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(
                        "§bWater: " + mb.get(tank.getKey()).getAmount() + " / " + tank.getCapacity() + " mB");
                stack.setItemMeta(meta);
                return stack;
            });

            this.layout.setDynamicProvider(4, (machine, tick) -> {
                TestMultiBlockMachineBlockEntity mb = (TestMultiBlockMachineBlockEntity) machine;
                FluidTank tank = mb.fluidTanks.get(1);
                org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                        org.bukkit.Material.LAVA_BUCKET);
                org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(
                        "§6Lava: " + mb.get(tank.getKey()).getAmount() + " / " + tank.getCapacity() + " mB");
                stack.setItemMeta(meta);
                return stack;
            });

            this.layout.setDynamicProvider(5, (machine, tick) -> {
                TestMultiBlockMachineBlockEntity mb = (TestMultiBlockMachineBlockEntity) machine;
                if (mb.gasTanks.isEmpty())
                    return new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
                GasTank tank = mb.gasTanks.get(0);
                org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                        org.bukkit.Material.WHITE_STAINED_GLASS);
                org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(
                        "§f Steam Output: " + mb.get(tank.getKey()).getAmount() + " / " + tank.getCapacity() + " mB");
                stack.setItemMeta(meta);
                return stack;
            });

            // Status indicator
            this.layout.setDynamicProvider(8, (machine, tick) -> {
                TestMultiBlockMachineBlockEntity mb = (TestMultiBlockMachineBlockEntity) machine;
                boolean processing = mb.isProcessing();
                org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(
                        processing ? org.bukkit.Material.LIME_STAINED_GLASS_PANE
                                : org.bukkit.Material.RED_STAINED_GLASS_PANE);
                org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(processing ? "§aProcessing" : "§cIdle");
                stack.setItemMeta(meta);
                return stack;
            });
        }

        @Override
        public MachineLayout getLayout() {
            return layout;
        }

        @Override
        protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
            // Check if we have enough water
            if (!fluidTanks.isEmpty()
                    && fluidTanks.get(0).getFluid(level, getMachinePos()).getAmount() >= 1000) {
                // Water → Lava + Steam
                return AbstractProcessingRecipe.builder()
                        .addFluidInput(FluidType.WATER, 1000)
                        .addFluidOutput(FluidType.LAVA, 500)
                        .addGasOutput(GasType.STEAM, 1000)
                        .setProcessTime(100)
                        .setFuelRequired(true)
                        .build();
            }
            return null;
        }

        @Override
        protected boolean canFitOutput(Level level, RecipeOutput output) {
            // Check lava capacity
            if (!fluidTanks.isEmpty() && fluidTanks.size() > 1) {
                FluidTank lavaTank = fluidTanks.get(1);
                int lavaAmount = lavaTank.getFluid(level, getMachinePos()).getAmount();
                if (lavaAmount + 500 > lavaTank.getCapacity())
                    return false;
            }
            // Check steam capacity
            if (!gasTanks.isEmpty()) {
                GasTank steamTank = gasTanks.get(0);
                int steamAmount = steamTank.getGas(level, getMachinePos()).getAmount();
                if (steamAmount + 1000 > steamTank.getCapacity())
                    return false;
            }
            return true;
        }

        @Override
        protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
            // Consume water
            if (!fluidTanks.isEmpty()) {
                fluidTanks.get(0).extract(level, getMachinePos(), 1000, null);
            }
        }

        @Override
        protected String getMachineId() {
            return "test_multiblock_machine";
        }

        /**
         * Redstone output: bottom layer emits signal (strength 15) when processing
         */
        @Override
        public int getRedstoneOutput(BlockPos relativePos, net.minecraft.core.Direction side) {
            // Only bottom layer (y < 0) outputs redstone downward
            if (relativePos.getY() < 0 && side == net.minecraft.core.Direction.DOWN) {
                return isProcessing() ? 15 : 0;
            }
            return 0;
        }
    }
}
