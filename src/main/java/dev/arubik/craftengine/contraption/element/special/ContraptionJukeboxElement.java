package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.ContraptionInteractionListener;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionJukeboxElement implements ContraptionElement {

    private final BlockPos localPos;
    private BlockState blockState;

    private final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;
    private double lastScale = 1.0;

    public ContraptionJukeboxElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        this.localPos = localPos;
        this.blockState = blockState;
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    @Override public Key type() { return ElementTypes.JUKEBOX; }
    @Override public Vec3 localOffset() { return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5); }
    @Override public boolean isValid() { return blockState != null && !blockState.isAir(); }
    @Override public int[] entityIds() { return new int[]{entityId}; }

    @Override
    public List<AABB> interactionBounds() {
        return List.of(new AABB(localPos.getX(), localPos.getY(), localPos.getZ(),
                localPos.getX() + 1.0, localPos.getY() + 1.0, localPos.getZ() + 1.0));
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;
        BlockState live = ctx.level().getBlockState(localPos);
        if (!live.equals(blockState)) { blockState = live; metaDirty = true; }
        if (ctx.scale() != lastScale) { lastScale = ctx.scale(); metaDirty = true; }
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float) ctx.yawDegrees();
        for (Player viewer : ctx.viewers()) {
            if (shownTo.add(viewer.uuid())) spawn(viewer, worldPos, yawDeg);
            else if (ctx.moved()) viewer.sendPacket(
                    MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(entityId, worldPos.x, worldPos.y, worldPos.z, yawDeg, 0f, false), false);
        }
        if (metaDirty) { sendMeta(ctx.viewers()); metaDirty = false; }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player p : viewers) { if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false); }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        BlockPos worldPos = ContraptionMath.toWorld(rotateLocal(localPos, quarterTurns), bearingPos);
        level.setBlock(worldPos, blockState.rotate(rotationFromQuarterTurns(quarterTurns)), 3);
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos, InteractionHand hand, boolean rightClick) {
        Direction face = nearestFace(hitPos);
        ContraptionInteractionListener.Hit hit = new ContraptionInteractionListener.Hit(state, localPos, hitPos, face);
        if (rightClick) {
            ContraptionInteractionListener.forward(player, hit);
            // Vanilla forward plays the disc sound into ContraptionLevel — mirror it to the real world
            playDiscSoundInRealWorld(state);
        } else {
            ContraptionInteractionListener.forwardAttack(player, hit);
        }
        return true;
    }

    private void playDiscSoundInRealWorld(ContraptionState state) {
        // Called after forward() — check if a disc is now playing in the contraption level
        // and mirror the sound to the real world at the bearing world position
        try {
            if (!(state.level().realLevel() instanceof ServerLevel sl)) return;
            net.minecraft.world.item.ItemStack disc = net.minecraft.world.item.ItemStack.EMPTY;
            // Read disc from contraption level BE
            var be = state.level().getBlockEntity(localPos);
            if (be instanceof net.minecraft.world.level.block.entity.BaseContainerBlockEntity container) {
                disc = container.getItem(0);
            }
            Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
            Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), bearing,
                    state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
            if (disc.isEmpty()) {
                // Ejected — stop music for all players in the world
                Object stopPkt = new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                        null, net.minecraft.sounds.SoundSource.RECORDS);
                for (net.minecraft.server.level.ServerPlayer sp : sl.players()) {
                    sp.connection.send((net.minecraft.network.protocol.Packet<?>) stopPkt);
                }
            } else {
                // Disc inserted — play via JUKEBOX_PLAYABLE component
                var playable = disc.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
                if (playable != null) {
                    net.minecraft.sounds.SoundEvent sound = resolveDiscSound(disc, sl);
                    if (sound == null) return;
                    sl.playSeededSound(null, worldPos.x, worldPos.y, worldPos.z,
                            sound, net.minecraft.sounds.SoundSource.RECORDS, 4f, 1f, 0L);
                }
            }
        } catch (Throwable ignored) {}
    }

    private void spawn(Player viewer, Vec3 pos, float yaw) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid, pos.x, pos.y, pos.z,
                        0f, yaw, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, yaw),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta())
        ), false);
    }

    private void sendMeta(List<Player> viewers) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta());
        for (Player p : viewers) { if (shownTo.contains(p.uuid())) p.sendPacket(pkt, false); }
    }

    private List<Object> buildMeta() {
        List<Object> meta = new ArrayList<>();
        DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, meta);
        float s = (float) lastScale;
        DisplayData.Translation.addEntityData(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), meta);
        if (s != 1f) DisplayData.Scale.addEntityData(new Vector3f(s, s, s), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    private Direction nearestFace(Vec3 hitPos) {
        double bx = localPos.getX() + 0.5, by = localPos.getY() + 0.5, bz = localPos.getZ() + 0.5;
        double dx = hitPos.x - bx, dy = hitPos.y - by, dz = hitPos.z - bz;
        double ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        if (ax >= ay && ax >= az) return dx > 0 ? Direction.EAST : Direction.WEST;
        if (ay >= az) return dy > 0 ? Direction.UP : Direction.DOWN;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static net.minecraft.sounds.SoundEvent resolveDiscSound(net.minecraft.world.item.ItemStack disc, ServerLevel sl) {
        try {
            var playable = disc.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
            if (playable == null) return null;
            // JukeboxPlayable.song() returns EitherHolder<JukeboxSong>
            // Try direct value first (inline song), then registry lookup by key
            var song = playable.song();
            try {
                // direct().value() path for inline songs
                return song.contents().left()
                        .map(h -> h.value().soundEvent().value())
                        .orElse(null);
            } catch (Throwable ignored) {}
            // Registry key path
            return song.key()
                    .flatMap(k -> sl.registryAccess()
                            .lookupOrThrow(net.minecraft.core.registries.Registries.JUKEBOX_SONG)
                            .get(k))
                    .map(h -> h.value().soundEvent().value())
                    .orElse(null);
        } catch (Throwable ignored) { return null; }
    }

    private static BlockPos rotateLocal(BlockPos local, int q) {
        return switch (q & 3) {
            case 1 -> new BlockPos(-local.getZ(), local.getY(), local.getX());
            case 2 -> new BlockPos(-local.getX(), local.getY(), -local.getZ());
            case 3 -> new BlockPos(local.getZ(), local.getY(), -local.getX());
            default -> local;
        };
    }

    private static net.minecraft.world.level.block.Rotation rotationFromQuarterTurns(int q) {
        return switch (q & 3) {
            case 1 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
            case 2 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
            case 3 -> net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;
            default -> net.minecraft.world.level.block.Rotation.NONE;
        };
    }
}
