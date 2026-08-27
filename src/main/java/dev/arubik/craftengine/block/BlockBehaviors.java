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
        public static final Key POLYFILL_FLUID_BLOCK_TANK = Key.of("polyfills:fluid_block_tank");
        public static final Key POLYFILL_SPIKE_BLOCK = Key.of("polyfills:spike_block");

        public static final Key POLYFILL_GAS_PUMP_BLOCK = Key.of("polyfills:gas_pump_block");
        public static final Key POLYFILL_GAS_VALVE_BLOCK = Key.of("polyfills:gas_valve_block");
        public static final Key POLYFILL_GAS_TANK_BLOCK = Key.of("polyfills:gas_tank_block");
        public static final Key POLYFILL_GAS_PIPE_BLOCK = Key.of("polyfills:gas_pipe_block");
        public static final Key POLYFILL_GAS_PROVIDER = Key.of("polyfills:gas_provider");

        public static final Key POLYFILL_ENERGY_CABLE_BLOCK = Key.of("polyfills:energy_cable_block");
        public static final Key POLYFILL_ITEM_PIPE_BLOCK = Key.of("polyfills:item_pipe_block");

        /** Generalized "place one block, the rest auto-places" multi-cell behavior — see
         * {@code dev.arubik.craftengine.multiblock.MultiCellBlockBehavior}. Additive: does not
         * replace {@code polyfills:workbench}'s fixed 2-cell {@code HorizontalDoubleBlockBehavior}. */

        public static final Key POLYFILL_SHULKER_BOX_HITBOX = Key.of("polyfills:shulker_box_hitbox");

        public static final Key POLYFILL_CHAINERY_BLOCK = Key.of("polyfills:chainery_block");

        public static final Key POLYFILL_RENDERER = Key.of("polyfills:renderer");

        public static final Key POLYFILL_FLUID_DISPLAY = Key.of("polyfills:fluid_display");

        public static void register() {
                // One generic machine behavior for every machines/*.json definition, so a new
                // machine needs a data file and a block config rather than a Java class.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.block.behavior.DataMachineBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.machine.block.behavior.DataMachineBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.block.behavior.DataMultiBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.machine.block.behavior.DataMultiBlockBehavior.FACTORY);
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
                RegistryUtils.registerBlockBehavior(POLYFILL_FLUID_BLOCK_TANK,
                                dev.arubik.craftengine.fluid.behavior.FluidBlockTankBehavior.FACTORY);
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

                // CraftEnergy (Forge-Energy-alike) — cable network. The battery/storage endpoint is
                // a machine (polyfills:energy_cell, machines/energy_cell.json), not a bespoke Java
                // behavior — MachineBlockBehavior already implements EnergyCarrier, so a plain
                // polyfills:data_machine block IS a valid network node with zero extra Java.
                RegistryUtils.registerBlockBehavior(POLYFILL_ENERGY_CABLE_BLOCK,
                                dev.arubik.craftengine.energy.behavior.EnergyCableBehavior.FACTORY);

                // Item pipe (v1 — plain conduit, no per-side filter UI yet; see pipe.item.ItemPipeBehavior).
                RegistryUtils.registerBlockBehavior(POLYFILL_ITEM_PIPE_BLOCK,
                                dev.arubik.craftengine.pipe.item.ItemPipeBehavior.FACTORY);

                // Auto-placing multi-cell structures (arbitrary shape, e.g. the 4-tall energy windmill):
                // a SEPARATE behavior from polyfills:data_machine (not folded into the class every
                // ordinary single-block machine already uses) so that shared class can't regress from
                // this. The shape is declared as "cells" on the BLOCK's own config (see
                // MultiCellGeometry#parseCells + CelledDataMachineBehavior), never the machine JSON.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.machine.block.behavior.CelledDataMachineBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.machine.block.behavior.CelledDataMachineBehavior.FACTORY);

                // Chainery — endpoint block for a rendered chain span (see chainery.ChaineryBlockBehavior)
                RegistryUtils.registerBlockBehavior(POLYFILL_CHAINERY_BLOCK,
                                dev.arubik.craftengine.chainery.ChaineryBlockBehavior.FACTORY);

                // Renderer — data-driven renderers (particles, displays, models) on any CE block
                RegistryUtils.registerBlockBehavior(POLYFILL_RENDERER,
                                dev.arubik.craftengine.block.behavior.RendererBehavior.FACTORY);

                // Machine Examples

                // MultiBlock Examples
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.multiblock.examples.TestMultiBlockMachineBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.multiblock.examples.TestMultiBlockMachineBehavior.FACTORY);

                net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfigs.register(
                                POLYFILL_SHULKER_BOX_HITBOX,
                                dev.arubik.craftengine.machine.render.element.ShulkerBoxHitboxElementConfig.FACTORY);

                net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElementConfigs.register(
                                POLYFILL_FLUID_DISPLAY,
                                dev.arubik.craftengine.fluid.render.FluidDisplayElementConfig.FACTORY);

                // Motors migrated to machines/*.json + scripts — no separate behavior needed.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.belt.ConveyorBehavior.POLYFILL_CONVEYOR,
                                dev.arubik.craftengine.conveyor.belt.ConveyorBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.routing.MergerBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.routing.MergerBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.conveyor.routing.SplitterBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.conveyor.routing.SplitterBehavior.FACTORY);
                // Depot and Funnel/FloorFunnel/CeilingFunnel migrated to machines/*.json + scripts —
                // conveyor.depot/conveyor.funnel removed entirely, no separate behavior needed.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.crafting.CraftingTableBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.crafting.CraftingTableBehavior.FACTORY);
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.crafting.WorkbenchBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.crafting.WorkbenchBehavior.FACTORY);
                // Contraption bearing block (CONTRAPTIONS.md §5 Phase 6 follow-up): real
                // detection replacing the old bearing-test-register command/BearingAnchorRegistry
                // placeholder. Still blocked on a real .yml block config referencing this key —
                // see CONTRAPTIONS.md for the exact YAML.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior.FACTORY);

                // Contraption miner/drill block (this session): a real CraftEngine block
                // behavior mirroring BearingBlockBehavior's shape. Also wired into the
                // contraption auto-attach path (MovementBehaviorRegistry) right here so both
                // registrations stay next to each other for discoverability — see
                // MinerBlockBehavior#buildMovementBehavior's javadoc for what gets built.

                // Contraption mover/propulsion block (roadmap item #4): a real CraftEngine block
                // behavior mirroring MinerBlockBehavior's shape. Auto-attached into a captured
                // contraption via the SAME MovementBehaviorRegistry path the miner uses, so a
                // mover block glued inside any bearing-assembled structure contributes propulsion —
                // see MoverBlockBehavior#buildMovementBehavior's javadoc for what gets built.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.MoverBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.MoverBlockBehavior.FACTORY);
                dev.arubik.craftengine.contraption.behavior.MovementBehaviorRegistry.register(
                                dev.arubik.craftengine.contraption.behavior.MoverBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.MoverBlockBehavior::buildMovementBehavior);

                // Contraption weight/ballast block (roadmap item #9 — PhysContraption): a real
                // CraftEngine block behavior mirroring MoverBlockBehavior's shape, carrying a `weight:`
                // config value. UNLIKE the miner/mover it does NOT register a MovementBehaviorRegistry
                // factory — it contributes no movement of its own; instead PhysicsBehavior aggregates the
                // mass of all captured weight blocks (via MassModel/WeightBlockBehavior#weightOf) into
                // the contraption's mass model. See WeightBlockBehavior's javadoc.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.WeightBlockBehavior.FACTORY);

                // Floatability: the per-block gravity multiplier the mass model averages into a body's
                // own gravity (1 = normal, 0 = weightless, -1 = rises). Data only, exactly like the
                // weight block above — it contributes no movement behavior of its own. See
                // FloatabilityBlockBehavior's javadoc for why it is mass-weighted and how it differs
                // from buoyancy.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.FloatabilityBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.FloatabilityBlockBehavior.FACTORY);

                // Per-block surface grip (polyfills:friction_block) — the tangential material property, layered
                // like mass/floatability over friction.yml + a built-in family table. See FrictionTable.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.FrictionBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.FrictionBlockBehavior.FACTORY);

                // Per-block bounce (polyfills:restitution_block) — the normal-direction material property,
                // the twin of friction, layered over restitution.yml + a built-in family table. See
                // RestitutionTable.
                RegistryUtils.registerBlockBehavior(
                                dev.arubik.craftengine.contraption.behavior.RestitutionBlockBehavior.FACTORY_KEY,
                                dev.arubik.craftengine.contraption.behavior.RestitutionBlockBehavior.FACTORY);

                // (CraftingSamples deleted — real recipes are CraftEngine-native YAML on the vanilla table.)
                // Populate sample workbench station recipes (3x2 + tool + condition/executor).
                dev.arubik.craftengine.crafting.WorkbenchSamples.registerDefaults();
        }
}
