package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.List;
import java.util.UUID;

/**
 * Seat element: an invisible ArmorStand mount that carries a rider with the contraption.
 * Pure NMS — spawns a real ArmorStand in the real world (riders need real entities for
 * vanilla passenger mechanics). Repositions mount every tick to track bearing transform.
 */
public final class ContraptionSeatElement implements ContraptionElement {

    private final Vec3 localOffset;
    private final float yawOffsetDegrees;
    private final SeatSource source;

    // Live state
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
        return localOffset;
    }

    @Override
    public boolean isValid() {
        return source != null;
    }

    @Override
    public int[] entityIds() {
        if (mount != null) {
            return new int[]{mount.getId()};
        }
        return new int[0];
    }

    /** 0.8×0.8×0.8 cube at local seat offset — standard click detection area for a seat. */
    @Override
    public List<AABB> interactionBounds() {
        double x = localOffset.x, y = localOffset.y, z = localOffset.z;
        return List.of(new AABB(x - 0.4, y - 0.4, z - 0.4, x + 0.4, y + 0.4, z + 0.4));
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        if (!rightClick) return false;
        ServerLevel level;
        try {
            level = (ServerLevel) player.level();
        } catch (ClassCastException ignored) {
            return false;
        }
        // Dismount if already seated here
        if (player.getUUID().equals(occupantId)) {
            ejectRider();
            return true;
        }
        // Mount at world position
        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, bearing,
                state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
        return mount(player, level, worldPos);
    }

    public boolean isOccupied() {
        return occupantId != null;
    }

    public UUID occupantId() {
        return occupantId;
    }

    @Override
    public void tick(RenderContext ctx) {
        if (mount == null || occupantId == null) return;

        if (!mount.isAlive()) {
            mount = null;
            occupantId = null;
            return;
        }

        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float worldYaw = (float) ctx.yawDegrees() + yawOffsetDegrees;
        float worldPitch = (float) ctx.pitchDegrees();

        mount.setPos(worldPos.x, worldPos.y - SEAT_HEIGHT, worldPos.z);
        mount.setYRot(worldYaw);
        mount.setXRot(worldPitch);
    }

    @Override
    public void render(RenderContext ctx) {
        // Seats are invisible — mount entity is real, not packet-only.
    }

    @Override
    public void despawn(List<Player> viewers) {
        ejectRider();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        ejectRider();
    }

    // ---- seat operations ----

    public boolean mount(ServerPlayer player, ServerLevel level, Vec3 worldPos) {
        if (isOccupied()) return false;

        ArmorStand stand = new ArmorStand(EntityType.ARMOR_STAND, level);
        stand.setPos(worldPos.x, worldPos.y - SEAT_HEIGHT, worldPos.z);
        stand.setYRot(yawOffsetDegrees);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setSilent(true);
        stand.setInvulnerable(true);
        stand.setSmall(true);

        if (!level.addFreshEntity(stand)) {
            return false;
        }

        player.startRiding(stand, true, true);

        this.mount = stand;
        this.occupantId = player.getUUID();
        return true;
    }

    public void ejectRider() {
        if (mount != null) {
            mount.ejectPassengers();
            mount.discard();
            mount = null;
        }
        occupantId = null;
    }

    // ---- seat source ----

    public sealed interface SeatSource permits BlockSeatSource, FurnitureSeatSource {
    }

    public record BlockSeatSource(BlockPos localBlockPos) implements SeatSource {
    }

    public record FurnitureSeatSource(UUID furnitureElementId, int seatIndex) implements SeatSource {
    }
}
