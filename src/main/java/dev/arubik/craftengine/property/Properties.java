package dev.arubik.craftengine.property;

import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.momirealms.craftengine.core.block.properties.EnumProperty;
import net.momirealms.craftengine.core.block.properties.Property;
import net.momirealms.craftengine.core.util.Key;

public class Properties {

    public static final Key REDSTONE_SIDE = Key.of("polyfills:redstone_side");
    public static final Key CONNECTED_FACE = Key.of("polyfills:connected_face");
    public static final Key FLUID_TYPE = Key.of("polyfills:fluid_type");
    public static final Key MULTIBLOCK_ROLE = Key.of("polyfills:multiblock_role");
    public static final Key MACHINE_MODE = Key.of("polyfills:machine_mode");

    // floor wall ceiling
    public static void register() {
        net.momirealms.craftengine.core.block.properties.Properties.register(REDSTONE_SIDE,
                EnumProperty.factory(RedstoneSide.class));
        net.momirealms.craftengine.core.block.properties.Properties.register(CONNECTED_FACE,
                EnumProperty.factory(ConnectedFace.class));
        net.momirealms.craftengine.core.block.properties.Properties.register(FLUID_TYPE,
                EnumProperty.factory(FluidType.class));
        net.momirealms.craftengine.core.block.properties.Properties.register(MULTIBLOCK_ROLE,
                EnumProperty.factory(dev.arubik.craftengine.multiblock.MultiBlockRole.class));
        net.momirealms.craftengine.core.block.properties.Properties.register(MACHINE_MODE,
                EnumProperty.factory(dev.arubik.craftengine.multiblock.MachineMode.class));
    }
}