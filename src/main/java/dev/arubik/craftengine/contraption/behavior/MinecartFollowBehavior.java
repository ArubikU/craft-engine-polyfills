/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Entity
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import java.util.UUID;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;

public final class MinecartFollowBehavior
implements MovementBehavior {
    private static final double Y_OFFSET = 1.0;
    private static final double MINECART_RAIL_OFFSET = 0.0625;
    private static final double MAX_YAW_STEP_RADIANS = Math.toRadians(9.0);
    private static final double QUARTER_TURN = 1.5707963267948966;
    private static final int ANCHOR_LOST_GRACE_TICKS = 100;
    private final UUID entityId;
    private boolean missing;
    private int missingTicks;
    private boolean wantsDisassembleInPlace;
    private boolean hasLastSample;
    private double lastX;
    private double lastY;
    private double lastZ;
    private double lastYawRadians;
    private Vec3 pendingVelocity = Vec3.ZERO;
    private double rawAccumYaw;
    private double refAccumYaw;

    public MinecartFollowBehavior(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID entityId() {
        return this.entityId;
    }

    @Override
    public void tick(MovementContext ctx) {
        Entity entity = Bukkit.getEntity((UUID)this.entityId);
        if (entity == null || !entity.isValid()) {
            this.missing = true;
            this.pendingVelocity = Vec3.ZERO;
            if (this.hasLastSample && this.missingTicks < 100 && ++this.missingTicks >= 100) {
                this.wantsDisassembleInPlace = true;
            }
            return;
        }
        this.missing = false;
        this.missingTicks = 0;
        if (!((CraftWorld)entity.getWorld()).getHandle().dimension().equals(ctx.state().worldId())) {
            ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
            if (facade != null) {
                Location at = entity.getLocation();
                facade.teleport(at.getWorld(), at.getX() - 0.5, at.getY() + 1.0 - 0.0625, at.getZ() - 0.5, ctx.state().yawRadians());
            }
            this.hasLastSample = false;
            this.pendingVelocity = Vec3.ZERO;
            return;
        }
        Location loc = entity.getLocation();
        double x = loc.getX();
        double y = loc.getY() + 1.0 - 0.0625;
        double z = loc.getZ();
        double yawRadians = Math.toRadians(-loc.getYaw());
        if (!this.hasLastSample) {
            this.hasLastSample = true;
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.lastYawRadians = yawRadians;
            this.refAccumYaw = this.rawAccumYaw = ctx.state().yawRadians();
            this.pendingVelocity = new Vec3(x - 0.5 - ctx.state().x(), y - ctx.state().y(), z - 0.5 - ctx.state().z());
            return;
        }
        this.pendingVelocity = new Vec3(x - this.lastX, y - this.lastY, z - this.lastZ);
        this.rawAccumYaw += MinecartFollowBehavior.shortestAngleDelta(this.lastYawRadians, yawRadians);
        this.driveYawTowards(ctx, this.correctedYaw());
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
        this.lastYawRadians = yawRadians;
    }

    private double correctedYaw() {
        double rel = this.rawAccumYaw - this.refAccumYaw;
        double modHalf = Math.abs(rel) % Math.PI;
        boolean perpendicular = Math.abs(modHalf - 1.5707963267948966) < 0.7853981633974483;
        return perpendicular ? this.refAccumYaw - rel : this.rawAccumYaw;
    }

    private void driveYawTowards(MovementContext ctx, double targetYaw) {
        double need = MinecartFollowBehavior.shortestAngleDelta(ctx.state().yawRadians(), targetYaw);
        double step = Math.max(-MAX_YAW_STEP_RADIANS, Math.min(MAX_YAW_STEP_RADIANS, need));
        if (step == 0.0) {
            return;
        }
        ctx.state().setYawRadians(ctx.state().yawRadians() + step);
    }

    private static double shortestAngleDelta(double from, double to) {
        double delta = (to - from) % (Math.PI * 2);
        if (delta > Math.PI) {
            delta -= Math.PI * 2;
        } else if (delta < -Math.PI) {
            delta += Math.PI * 2;
        }
        return delta;
    }

    @Override
    public boolean isStalled() {
        return this.missing;
    }

    public boolean wantsDisassembleInPlace() {
        return this.wantsDisassembleInPlace;
    }

    @Override
    public Vec3 velocityThisTick() {
        return this.pendingVelocity;
    }
}

