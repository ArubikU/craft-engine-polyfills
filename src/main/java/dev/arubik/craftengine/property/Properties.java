package dev.arubik.craftengine.property;

import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.util.Key;

public class Properties {

        public static final Key REDSTONE_SIDE = Key.of("polyfills:redstone_side");
        public static final Key CONNECTED_FACE = Key.of("polyfills:connected_face");
        public static final Key FLUID_TYPE = Key.of("polyfills:fluid_type");
        public static final Key MULTIBLOCK_ROLE = Key.of("polyfills:multiblock_role");
        public static final Key MACHINE_MODE = Key.of("polyfills:machine_mode");
        public static final Key MACHINE_TYPE = Key.of("polyfills:machine_type");
        public static final Key TANK_SHAPE = Key.of("polyfills:tank_shape");
        public static final Key TANK_FACING = Key.of("polyfills:tank_facing");

        // floor wall ceiling
        public static void register() {
                net.momirealms.craftengine.core.block.property.Properties.register(REDSTONE_SIDE,
                                EnumProperty.factory(RedstoneSide.class));
                net.momirealms.craftengine.core.block.property.Properties.register(CONNECTED_FACE,
                                EnumProperty.factory(ConnectedFace.class));
                // NOTE: polyfills:fluid_type is deliberately NOT registered any more. Tanks now
                // declare their fluid appearance with CraftEngine's built-in `string` property,
                // whose value set lives in the block config:
                //   fluidtype: { type: string, default: empty, values: [empty, water, ...] }
                // A custom enum property would have forced the set of liquids closed in Java,
                // which is exactly what the data-driven FluidType registry exists to avoid.
                net.momirealms.craftengine.core.block.property.Properties.register(MULTIBLOCK_ROLE,
                                EnumProperty.factory(dev.arubik.craftengine.multiblock.MultiBlockRole.class));
                net.momirealms.craftengine.core.block.property.Properties.register(MACHINE_MODE,
                                EnumProperty.factory(dev.arubik.craftengine.multiblock.MachineMode.class));
                net.momirealms.craftengine.core.block.property.Properties.register(MACHINE_TYPE,
                                EnumProperty.factory(dev.arubik.craftengine.multiblock.impl.MachineType.class));
                net.momirealms.craftengine.core.block.property.Properties.register(TANK_SHAPE,
                                EnumProperty.factory(TankShape.class));
                net.momirealms.craftengine.core.block.property.Properties.register(TANK_FACING,
                                EnumProperty.factory(TankFacing.class));
        }
}