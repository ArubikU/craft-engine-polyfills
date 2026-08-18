/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionSeatElement
implements ContraptionElement {
    private final Vec3 localOffset;
    private final float yawOffsetDegrees;
    private final SeatSource source;
    private UUID occupantId;
    private ArmorStand mount;
    private static final double SEAT_HEIGHT = 0.6;

    public ContraptionSeatElement(Vec3 localOffset, float yawOffsetDegrees, SeatSource source) {
        this.localOffset = localOffset;
        this.yawOffsetDegrees = yawOffsetDegrees;
        this.source = source;
    }

    @Override
    public Key type() {
        return ElementTypes.SEAT;
    }

    @Override
    public Vec3 localOffset() {
        return this.localOffset;
    }

    @Override
    public boolean isValid() {
        return this.source != null;
    }

    @Override
    public int[] entityIds() {
        if (this.mount != null) {
            return new int[]{this.mount.getId()};
        }
        return new int[0];
    }

    @Override
    public List<AABB> interactionBounds() {
        double x = this.localOffset.x;
        double y = this.localOffset.y;
        double z = this.localOffset.z;
        return List.of(new AABB(x - 0.4, y - 0.4, z - 0.4, x + 0.4, y + 0.4, z + 0.4));
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        ServerLevel level;
        if (!rightClick) {
            return false;
        }
        try {
            level = player.level();
        }
        catch (ClassCastException ignored) {
            return false;
        }
        if (player.getUUID().equals(this.occupantId)) {
            this.ejectRider();
            return true;
        }
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset, bearing, state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
        return this.mount(player, level, worldPos);
    }

    public boolean isOccupied() {
        return this.occupantId != null;
    }

    public UUID occupantId() {
        return this.occupantId;
    }

    @Override
    public void tick(RenderContext ctx) {
        if (this.mount == null || this.occupantId == null) {
            return;
        }
        if (!this.mount.isAlive()) {
            this.mount = null;
            this.occupantId = null;
            return;
        }
        Vec3 worldPos = ContraptionMath.renderPosition(this.localOffset, ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float worldYaw = (float)ctx.yawDegrees() + this.yawOffsetDegrees;
        float worldPitch = (float)ctx.pitchDegrees();
        this.mount.setPos(worldPos.x, worldPos.y - 0.6, worldPos.z);
        this.mount.setYRot(worldYaw);
        this.mount.setXRot(worldPitch);
    }

    @Override
    public void render(RenderContext ctx) {
    }

    @Override
    public void despawn(List<Player> viewers) {
        this.ejectRider();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        this.ejectRider();
    }

    public boolean mount(ServerPlayer player, ServerLevel level, Vec3 worldPos) {
        if (this.isOccupied()) {
            return false;
        }
        ArmorStand stand = new ArmorStand(EntityType.ARMOR_STAND, (Level)level);
        stand.setPos(worldPos.x, worldPos.y - 0.6, worldPos.z);
        stand.setYRot(this.yawOffsetDegrees);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setSilent(true);
        stand.setInvulnerable(true);
        stand.setSmall(true);
        if (!level.addFreshEntity((Entity)stand)) {
            return false;
        }
        player.startRiding((Entity)stand, true, true);
        this.mount = stand;
        this.occupantId = player.getUUID();
        return true;
    }

    public void ejectRider() {
        if (this.mount != null) {
            this.mount.ejectPassengers();
            this.mount.discard();
            this.mount = null;
        }
        this.occupantId = null;
    }

    public static sealed interface SeatSource
    permits BlockSeatSource, FurnitureSeatSource {
    }

    public record FurnitureSeatSource(UUID furnitureElementId, int seatIndex) implements SeatSource
    {
    }

    public record BlockSeatSource(BlockPos localBlockPos) implements SeatSource
    {
    }
}

