package dev.arubik.craftengine.block;

import dev.arubik.craftengine.block.behavior.*;
import dev.arubik.craftengine.fluid.behavior.*;
import dev.arubik.craftengine.util.RegistryUtils;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;

public class BlockBehaviors {
        public static final Key POLYFILL_BUBBLE_BLOCK = Key.of("polyfills:bubble_block");
        public static final Key POLYFILL_TEARING_CROP_BLOCK = Key.of("polyfills:tearing_crop_block");
        public static final Key POLYFILL_TEARING_BLOCK_SPAWN = Key.of("polyfills:tearing_block_spawn");
        public static final Key POLYFILL_VERTICAL_CROP_BLOCK = Key.of("polyfills:vertical_crop_block");
        public static final Key POLYFILL_BUSH_BLOCK = Key.of("polyfills:bush_block");
        public static final Key POLYFILL_CHANGE_OVER_TIME_BLOCK = Key.of("polyfills:change_over_time_block");
        public static final Key POLYFILL_FAN_BLOCK = Key.of("polyfills:fan_block");
        public static final Key POLYFILL_CUSTOM_CRAFTER = Key.of("polyfills:custom_crafter");
        public static final Key POLYFILL_STORAGE_BLOCK = Key.of("polyfills:storage_block");
        public static final Key POLYFILL_REDSTONE_OPERATOR = Key.of("polyfills:redstone_operator");
        public static final Key POLYFILL_REDSTONE_CONTROLLER = Key.of("polyfills:redstone_controller");
        public static final Key POLYFILL_MAGNET_BLOCK = Key.of("polyfills:magnet_block");
        public static final Key POLYFILL_SPREADING_BLOCK = Key.of("polyfills:spreading_block");

        public static final Key POLYFILL_PIPE_BLOCK = Key.of("polyfills:pipe_block");
        public static final Key POLYFILL_PUMP_BLOCK = Key.of("polyfills:pump_block");
        public static final Key POLYFILL_VALVE_BLOCK = Key.of("polyfills:valve_block");
        public static final Key POLYFILL_FLUID_TANK_BLOCK = Key.of("polyfills:fluid_tank_block");
        public static final Key POLYFILL_SPIKE_BLOCK = Key.of("polyfills:spike_block");

        public static final Key POLYFILL_GAS_PUMP_BLOCK = Key.of("polyfills:gas_pump_block");
        public static final Key POLYFILL_GAS_VALVE_BLOCK = Key.of("polyfills:gas_valve_block");
        public static final Key POLYFILL_GAS_TANK_BLOCK = Key.of("polyfills:gas_tank_block");
        public static final Key POLYFILL_GAS_PIPE_BLOCK = Key.of("polyfills:gas_pipe_block");
        public static final Key POLYFILL_GAS_PROVIDER = Key.of("polyfills:gas_provider");

        public static final Key POLYFILL_SHULKER_BOX_HITBOX = Key.of("polyfills:shulker_box_hitbox");

        public static void register() {
                RegistryUtils.registerBlockBehavior(POLYFILL_BUBBLE_BLOCK, BubbleBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_TEARING_CROP_BLOCK, TearingCropBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_TEARING_BLOCK_SPAWN, TearingBlockSpawnBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_VERTICAL_CROP_BLOCK, VerticalCropBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_CHANGE_OVER_TIME_BLOCK,
                                ChangeOverTimeBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_BUSH_BLOCK, BushBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_FAN_BLOCK, FanMachineBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_CUSTOM_CRAFTER,
                                dev.arubik.craftengine.block.behavior.CustomCrafterBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_STORAGE_BLOCK, StorageBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_REDSTONE_OPERATOR, RedstoneOperator.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_REDSTONE_CONTROLLER, RedstoneController.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_MAGNET_BLOCK, MagnetBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_SPREADING_BLOCK, SpreadingBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_PIPE_BLOCK, PipeBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_PUMP_BLOCK, PumpBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.fluid.behavior.MachinePumpBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.fluid.behavior.MachinePumpBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.block.behavior.GasPumpBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.machine.block.behavior.GasPumpBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_VALVE_BLOCK, ValveBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_FLUID_TANK_BLOCK, TankBlockBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_SPIKE_BLOCK, SpikeBlockBehavior.FACTORY);

                // Gas Blocks
                RegistryUtils.registerBlockBehavior(POLYFILL_GAS_PUMP_BLOCK,
                                dev.arubik.craftengine.gas.behavior.GasPumpBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_GAS_VALVE_BLOCK,
                                dev.arubik.craftengine.gas.behavior.GasValveBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_GAS_TANK_BLOCK,
                                dev.arubik.craftengine.gas.behavior.GasTankBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_GAS_PIPE_BLOCK,
                                dev.arubik.craftengine.gas.behavior.GasPipeBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(POLYFILL_GAS_PROVIDER,
                                dev.arubik.craftengine.gas.behavior.GasProviderBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.gas.behavior.CreativeGasTankBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.gas.behavior.CreativeGasTankBehavior.FACTORY);

                // Machine Examples
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.TestMachineBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.machine.examples.TestMachineBehavior.FACTORY);

                // MultiBlock Examples
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.multiblock.examples.TestMultiBlockMachineBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.multiblock.examples.TestMultiBlockMachineBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.multiblock.examples.MultiPageChestBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.multiblock.examples.MultiPageChestBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.multiblock.impl.MachineCoreT1Behavior.FACTORY_KEY,
                                dev.arubik.craftengine.multiblock.impl.MachineCoreT1Behavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.multiblock.impl.PressurizerWellBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.multiblock.impl.PressurizerWellBehavior.FACTORY);

                net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfigs.register(
                                POLYFILL_SHULKER_BOX_HITBOX,
                                dev.arubik.craftengine.machine.render.element.ShulkerBoxHitboxElementConfig.FACTORY);

                // New features: upgrade machine, vapor motor, conveyor, JIT crafting
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.UpgradeableFurnaceBehavior.POLYFILL_UPGRADEABLE_FURNACE,
                                dev.arubik.craftengine.machine.examples.UpgradeableFurnaceBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.rotation.GasMotorMk1Behavior.POLYFILL_GAS_MOTOR_MK1,
                                dev.arubik.craftengine.rotation.GasMotorMk1Behavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.VaporFurnaceMk1Behavior.POLYFILL_VAPOR_FURNACE_MK1,
                                dev.arubik.craftengine.machine.examples.VaporFurnaceMk1Behavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.CrusherBehavior.POLYFILL_CRUSHER,
                                dev.arubik.craftengine.machine.examples.CrusherBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.SmelteryBehavior.POLYFILL_SMELTERY,
                                dev.arubik.craftengine.machine.examples.SmelteryBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.examples.RefineryBehavior.POLYFILL_REFINERY,
                                dev.arubik.craftengine.machine.examples.RefineryBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.ConveyorBehavior.POLYFILL_CONVEYOR,
                                dev.arubik.craftengine.conveyor.ConveyorBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.MergerBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.MergerBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.SplitterBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.SplitterBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.DepotBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.DepotBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.FunnelBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.FunnelBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.FloorFunnelBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.FloorFunnelBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.CeilingFunnelBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.CeilingFunnelBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.crafting.CraftingTableBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.crafting.CraftingTableBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.crafting.WorkbenchBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.crafting.WorkbenchBehavior.FACTORY);

                // (CraftingSamples deleted — real recipes are CraftEngine-native YAML on the vanilla table.)
                // Populate sample workbench station recipes (3x2 + tool + condition/executor).
                dev.arubik.craftengine.crafting.WorkbenchSamples.registerDefaults();
        }
}
