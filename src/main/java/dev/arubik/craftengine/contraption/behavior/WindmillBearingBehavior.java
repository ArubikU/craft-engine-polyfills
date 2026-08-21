/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;

public class WindmillBearingBehavior
implements MovementBehavior {
    private final BlockPos bearingPos;
    private final Axis axis;

    public WindmillBearingBehavior(BlockPos bearingPos, Vec3 facingVec) {
        this.bearingPos = bearingPos;
        this.axis = WindmillBearingBehavior.axisFor(facingVec);
    }

    public static Axis axisFor(Vec3 facing) {
        double ax = Math.abs(facing.x);
        double ay = Math.abs(facing.y);
        double az = Math.abs(facing.z);
        if (ay >= ax && ay >= az) {
            return Axis.YAW;
        }
        if (ax >= az) {
            return Axis.PITCH;
        }
        return Axis.ROLL;
    }

    @Override
    public void tick(MovementContext ctx) {
        float rpm = this.readRpm(ctx);
        if (rpm == 0.0f) {
            return;
        }
        double radiansPerTick = (double)rpm * 2.0 * Math.PI / 60.0 / 20.0;
        switch (this.axis.ordinal()) {
            case 0: {
                ctx.state().setYawRadians(ctx.state().yawRadians() + radiansPerTick);
                break;
            }
            case 1: {
                ctx.state().setPitchRadians(ctx.state().pitchRadians() + radiansPerTick);
                break;
            }
            case 2: {
                ctx.state().setRollRadians(ctx.state().rollRadians() + radiansPerTick);
            }
        }
        ctx.state().setGlobalRpm(rpm);
    }

    private float readRpm(MovementContext ctx) {
        try {
            BlockEntityController blockEntityController;
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)ctx.level(), this.bearingPos);
            if (be != null && (blockEntityController = be.controller) instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm = (DataMachineBlockEntity)blockEntityController;
                return dm.getRpm();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return 0.0f;
    }

    @Override
    public boolean isStalled() {
        return false;
    }

    public static enum Axis {
        YAW,
        PITCH,
        ROLL;

    }
}

