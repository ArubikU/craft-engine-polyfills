/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.Event
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.ContraptionAccessor;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.MiningMath;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.event.ContraptionBlockBreakEvent;
import dev.arubik.craftengine.rotation.RpmConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;

public final class MinerBehavior
implements MovementBehavior,
RpmConsumer {
    private final BlockPos targetOffset;
    private final double gearRatio;
    private final double minRpm;
    private final double suCost;
    private final List<ItemStack> virtualInventory = new ArrayList<ItemStack>();
    private float inputRpm;
    private double accumulatedDamage;
    private boolean stalled;

    public MinerBehavior(BlockPos targetOffset, double gearRatio, double minRpm, double suCost) {
        this.targetOffset = targetOffset;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
    }

    public MinerBehavior(BlockPos targetOffset, double gearRatio) {
        this(targetOffset, gearRatio, 0.0, 0.0);
    }

    public List<ItemStack> virtualInventory() {
        return this.virtualInventory;
    }

    public double accumulatedDamage() {
        return this.accumulatedDamage;
    }

    @Override
    public void tick(MovementContext ctx) {
        ServerLevel level = ctx.level();
        if (level == null) {
            this.stalled = false;
            return;
        }
        BlockPos bearing = ContraptionMath.gridSnap(new Vec3(ctx.state().x(), ctx.state().y(), ctx.state().z()));
        BlockPos worldPos = ContraptionMath.toWorld(this.targetOffset, bearing);
        if (ContraptionAccessor.isAir(level, worldPos)) {
            this.stalled = false;
            this.accumulatedDamage = 0.0;
            return;
        }
        float hardness = ContraptionAccessor.hardnessAt(level, worldPos);
        if (hardness < 0.0f) {
            this.stalled = true;
            return;
        }
        float rpmToUse = this.inputRpm > 0.0f ? this.inputRpm : ctx.state().globalRpm();
        double effectiveRpm = (double)rpmToUse * this.gearRatio;
        if (Math.abs(effectiveRpm) <= 0.0 || Math.abs(effectiveRpm) < this.minRpm) {
            this.stalled = false;
            return;
        }
        ctx.state().addSuDemand((float)this.suCost);
        this.stalled = true;
        this.accumulatedDamage += MiningMath.damagePerTick(Math.abs(effectiveRpm), hardness);
        if (MiningMath.isBroken(this.accumulatedDamage, hardness)) {
            if (!MinerBehavior.fireBlockBreakCancelled(ctx, level, worldPos)) {
                ContraptionAccessor.breakBlockAndCollect(level, worldPos, this.virtualInventory);
            }
            this.accumulatedDamage = 0.0;
            this.stalled = false;
        }
    }

    private static boolean fireBlockBreakCancelled(MovementContext ctx, ServerLevel level, BlockPos worldPos) {
        try {
            ContraptionEntity entity = ContraptionManager.get(ctx.state().id());
            if (entity == null) {
                return false;
            }
            BlockState state = level.getBlockState(worldPos);
            ContraptionBlockBreakEvent event = new ContraptionBlockBreakEvent(entity, (World)level.getWorld(), worldPos, state);
            Bukkit.getPluginManager().callEvent((Event)event);
            return event.isCancelled();
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public boolean isStalled() {
        return this.stalled;
    }

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return this.inputRpm;
    }
}

