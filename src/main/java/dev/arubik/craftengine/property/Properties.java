/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.RedstoneSide
 *  net.momirealms.craftengine.core.block.property.EnumProperty
 *  net.momirealms.craftengine.core.block.property.Properties
 *  net.momirealms.craftengine.core.block.property.PropertyFactory
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.property;

import dev.arubik.craftengine.multiblock.MachineMode;
import dev.arubik.craftengine.multiblock.MultiBlockRole;
import dev.arubik.craftengine.multiblock.impl.MachineType;
import dev.arubik.craftengine.property.ConnectedFace;
import dev.arubik.craftengine.property.TankFacing;
import dev.arubik.craftengine.property.TankShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.block.property.PropertyFactory;
import net.momirealms.craftengine.core.util.Key;

public class Properties {
    public static final Key REDSTONE_SIDE = Key.of((String)"polyfills:redstone_side");
    public static final Key CONNECTED_FACE = Key.of((String)"polyfills:connected_face");
    public static final Key FLUID_TYPE = Key.of((String)"polyfills:fluid_type");
    public static final Key MULTIBLOCK_ROLE = Key.of((String)"polyfills:multiblock_role");
    public static final Key MACHINE_MODE = Key.of((String)"polyfills:machine_mode");
    public static final Key MACHINE_TYPE = Key.of((String)"polyfills:machine_type");
    public static final Key TANK_SHAPE = Key.of((String)"polyfills:tank_shape");
    public static final Key TANK_FACING = Key.of((String)"polyfills:tank_facing");

    public static void register() {
        net.momirealms.craftengine.core.block.property.Properties.register((Key)REDSTONE_SIDE, (PropertyFactory)EnumProperty.factory(RedstoneSide.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)CONNECTED_FACE, (PropertyFactory)EnumProperty.factory(ConnectedFace.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)MULTIBLOCK_ROLE, (PropertyFactory)EnumProperty.factory(MultiBlockRole.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)MACHINE_MODE, (PropertyFactory)EnumProperty.factory(MachineMode.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)MACHINE_TYPE, (PropertyFactory)EnumProperty.factory(MachineType.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)TANK_SHAPE, (PropertyFactory)EnumProperty.factory(TankShape.class));
        net.momirealms.craftengine.core.block.property.Properties.register((Key)TANK_FACING, (PropertyFactory)EnumProperty.factory(TankFacing.class));
    }
}

