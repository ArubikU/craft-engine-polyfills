/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.animal.happyghast.HappyGhast
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.entity.CraftLivingEntity
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.bearing.GhastHarness;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import java.lang.reflect.Method;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public final class GhastFollowBehavior
implements MovementBehavior {
    private static final double MAX_YAW_STEP_RADIANS = Math.toRadians(9.0);
    private static final int ANCHOR_LOST_GRACE_TICKS = 100;
    private static final double SCALE_EPSILON = 1.0E-4;
    private final UUID entityId;
    private final Vec3 anchorOffset;
    private boolean missing;
    private int missingTicks;
    private boolean wantsDisassembleInPlace;
    private boolean hasLastSample;
    private Vec3 lastOrigin = Vec3.ZERO;
    private double yawOffset;
    private boolean hasYawOffset;
    private int lastShapeHash;
    private boolean shapeObserved;
    private boolean wantsFractureCheck;
    private Vec3 pendingVelocity = Vec3.ZERO;
    private static final int STILL_TIMEOUT_TICKS = 10;
    private static final Method SET_SERVER_STILL_TIMEOUT;

    public GhastFollowBehavior(UUID entityId, Vec3 anchorOffset, OptionalDouble yawOffset) {
        this.entityId = entityId;
        this.anchorOffset = anchorOffset;
        this.hasYawOffset = yawOffset.isPresent();
        this.yawOffset = yawOffset.orElse(0.0);
    }

    public UUID entityId() {
        return this.entityId;
    }

    public Vec3 anchorOffset() {
        return this.anchorOffset;
    }

    public OptionalDouble yawOffset() {
        return this.hasYawOffset ? OptionalDouble.of(this.yawOffset) : OptionalDouble.empty();
    }

    @Override
    public void tick(MovementContext ctx) {
        this.checkFracture(ctx);
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
                Vec3 origin = this.originOf(entity, ctx.state().yawRadians());
                facade.teleport(entity.getWorld(), origin.x, origin.y, origin.z, ctx.state().yawRadians());
            }
            this.hasLastSample = false;
            this.pendingVelocity = Vec3.ZERO;
            return;
        }
        this.syncScale(ctx, entity);
        this.enforceStillness(ctx, entity);
        float ghastYawDegrees = entity.getLocation().getYaw();
        if (!this.hasLastSample) {
            this.hasLastSample = true;
            if (!this.hasYawOffset) {
                this.yawOffset = GhastHarness.captureYawOffset(ctx.state().yawRadians(), ghastYawDegrees);
                this.hasYawOffset = true;
            }
            ctx.state().setYawRadians(GhastHarness.followTargetYaw(this.yawOffset, ghastYawDegrees));
            this.lastOrigin = this.originOf(entity, ctx.state().yawRadians());
            this.pendingVelocity = this.lastOrigin.subtract(new Vec3(ctx.state().x(), ctx.state().y(), ctx.state().z()));
            return;
        }
        this.driveYawTowards(ctx, GhastHarness.followTargetYaw(this.yawOffset, ghastYawDegrees));
        Vec3 origin = this.originOf(entity, ctx.state().yawRadians());
        this.pendingVelocity = origin.subtract(this.lastOrigin);
        this.lastOrigin = origin;
    }

    private Vec3 originOf(Entity entity, double yawRadians) {
        Location loc = entity.getLocation();
        return GhastHarness.bearingOrigin(new Vec3(loc.getX(), loc.getY(), loc.getZ()), GhastFollowBehavior.centreHeightOf(entity), this.anchorOffset, yawRadians);
    }

    private static double centreHeightOf(Entity entity) {
        return ((CraftEntity)entity).getHandle().getBoundingBox().getYsize() / 2.0;
    }

    private void syncScale(MovementContext ctx, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity)entity;
        double ghastScale = ((CraftLivingEntity)living).getHandle().getScale();
        ghastScale = Math.max(0.1, Math.min(10.0, ghastScale));
        if (Math.abs(ghastScale - ctx.state().scale()) < 1.0E-4) {
            return;
        }
        ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
        if (facade != null) {
            facade.setScale(ghastScale);
        } else {
            ctx.state().setScale(ghastScale);
        }
    }

    private void enforceStillness(MovementContext ctx, Entity entity) {
        if (SET_SERVER_STILL_TIMEOUT == null) {
            return;
        }
        net.minecraft.world.entity.Entity entity2 = ((CraftEntity)entity).getHandle();
        if (!(entity2 instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)entity2;
        if (ghast.getFirstPassenger() instanceof Player) {
            return;
        }
        ContraptionEntity facade = ContraptionWorlds.owning(ctx.state().level()).orElse(null);
        if (facade == null || !GhastFollowBehavior.anyStandingOnDeck(facade.currentRiderIds())) {
            return;
        }
        try {
            SET_SERVER_STILL_TIMEOUT.invoke(ghast, 10);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static boolean anyStandingOnDeck(Set<UUID> riderIds) {
        for (UUID id : riderIds) {
            org.bukkit.entity.Player player = Bukkit.getPlayer((UUID)id);
            if (player == null || player.getGameMode() == GameMode.SPECTATOR) continue;
            return true;
        }
        return false;
    }

    private void driveYawTowards(MovementContext ctx, double targetYaw) {
        double need = GhastHarness.shortestAngleDelta(ctx.state().yawRadians(), targetYaw);
        double step = Math.max(-MAX_YAW_STEP_RADIANS, Math.min(MAX_YAW_STEP_RADIANS, need));
        if (step == 0.0) {
            return;
        }
        ctx.state().setYawRadians(ctx.state().yawRadians() + step);
    }

    private void checkFracture(MovementContext ctx) {
        if (ctx.state().level() == null) {
            return;
        }
        int shape = ctx.state().level().localPositions().hashCode();
        if (this.shapeObserved && shape != this.lastShapeHash) {
            this.wantsFractureCheck = true;
        }
        this.lastShapeHash = shape;
        this.shapeObserved = true;
    }

    public boolean consumeFractureCheck() {
        boolean want = this.wantsFractureCheck;
        this.wantsFractureCheck = false;
        return want;
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

    static {
        Method method = null;
        try {
            method = HappyGhast.class.getDeclaredMethod("setServerStillTimeout", Integer.TYPE);
            method.setAccessible(true);
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] HappyGhast#setServerStillTimeout is unavailable; a harnessed ghast will wander while players stand on its contraption: " + String.valueOf(e));
        }
        SET_SERVER_STILL_TIMEOUT = method;
    }
}

