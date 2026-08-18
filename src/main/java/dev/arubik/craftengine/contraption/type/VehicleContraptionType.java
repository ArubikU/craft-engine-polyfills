/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class VehicleContraptionType
implements ContraptionType {
    public static final VehicleContraptionType INSTANCE = new VehicleContraptionType();

    private VehicleContraptionType() {
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return true;
    }

    @Override
    public boolean hasPhysics() {
        return true;
    }
}

