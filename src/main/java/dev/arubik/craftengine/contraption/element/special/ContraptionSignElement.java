package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sign element — inherits BLOCK_DISPLAY (sign model) from ContraptionBlockElement,
 * adds TEXT_DISPLAY for front and back text. Waxed signs block editing via vanilla.
 * Supports: sign, wall_sign, hanging_sign, ceiling_hanging_sign, wall_hanging_sign.
 */
public final class ContraptionSignElement extends ContraptionBlockElement {

    // Front text entity
    private final int frontId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID frontUuid = UUID.randomUUID();
    private final Object frontRemove;
    private final Set<UUID> frontShown = ConcurrentHashMap.newKeySet();

    // Back text entity
    private final int backId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID backUuid = UUID.randomUUID();
    private final Object backRemove;
    private final Set<UUID> backShown = ConcurrentHashMap.newKeySet();

    private Component frontText = Component.empty();
    private Component backText = Component.empty();
    private boolean textDirty = true;

    public ContraptionSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, false, 0f);
        this.frontRemove = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(frontId));
        this.backRemove  = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(backId));
        readText(beTag);
    }

    @Override public Key type() { return ElementTypes.SIGN; }

    @Override
    public int[] entityIds() {
        int[] base = super.entityIds();
        return new int[]{base[0], frontId, backId};
    }

    @Override
    public void tick(RenderContext ctx) {
        super.tick(ctx);
        if (ctx.level() == null) return;
        var be = ctx.level().getBlockEntity(localPos());
        if (be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
            Component lf = buildTextComponent(sign.getFrontText());
            Component lb = buildTextComponent(sign.getBackText());
            // Force dirty if we previously had empty text — ensures spawned viewers get text
            if (!lf.equals(frontText) || !lb.equals(backText)
                    || frontText.getString().isEmpty() || backText.getString().isEmpty()) {
                frontText = lf;
                backText  = lb;
                textDirty = true;
            }
        }
    }

    @Override
    public void render(RenderContext ctx) {
        super.render(ctx); // BLOCK_DISPLAY via inherited render

        Direction facing = getFacing();
        float contraptionYaw = (float) ctx.yawDegrees();
        // Pitch+roll tilt so text entity follows contraption orientation
        org.joml.Quaternionf tiltQ = (ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0) ? null
                : new org.joml.Quaternionf().rotateX((float) ctx.pitchRadians()).rotateZ((float) ctx.rollRadians());

        Vec3 frontPos = textPos(ctx, facing, false);
        Vec3 backPos  = textPos(ctx, facing, true);
        float frontYaw = getTextEntityYaw(contraptionYaw, false);
        float backYaw  = getTextEntityYaw(contraptionYaw, true);

        for (Player viewer : ctx.viewers()) {
            if (frontShown.add(viewer.uuid())) spawnText(viewer, frontId, frontUuid, frontPos, contraptionYaw, false, tiltQ);
            else if (ctx.moved()) syncPos(viewer, frontId, frontPos, frontYaw);

            if (backShown.add(viewer.uuid())) spawnText(viewer, backId, backUuid, backPos, contraptionYaw, true, tiltQ);
            else if (ctx.moved()) syncPos(viewer, backId, backPos, backYaw);
        }

        if (textDirty) {
            sendTextMeta(ctx.viewers(), frontId, frontShown, frontText, tiltQ);
            sendTextMeta(ctx.viewers(), backId,  backShown,  backText, tiltQ);
            textDirty = false;
        }
    }

    @Override
    public void despawn(List<Player> viewers) {
        super.despawn(viewers);
        for (Player p : viewers) {
            if (frontShown.remove(p.uuid())) p.sendPacket(frontRemove, false);
            if (backShown.remove(p.uuid()))  p.sendPacket(backRemove,  false);
        }
    }

    // ---- sign-specific helpers ----

    /**
     * Yaw in degrees the text entity should face, matching vanilla sign rendering.
     * Wall sign: from FACING direction. Floor sign: rotation * 22.5 (same formula as skull, SkullBlock.getYaw()).
     * back=true: opposite face (+180°).
     */
    private float getTextEntityYaw(float contraptionYaw, boolean back) {
        float base;
        if (blockState().getBlock() instanceof WallSignBlock) {
            base = blockState().getValue(WallSignBlock.FACING).toYRot();
        } else if (blockState().getBlock() instanceof WallHangingSignBlock) {
            base = blockState().getValue(WallHangingSignBlock.FACING).toYRot();
        } else if (blockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16)) {
            // Floor sign: rotation 0-15, each step = 22.5° — same formula as skull/standing sign renderer
            base = blockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16) * 22.5f;
        } else if (blockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16)) {
            base = blockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16) * 22.5f;
        } else {
            base = 0f;
        }
        return base + (back ? 180f : 0f) + contraptionYaw;
    }

    /** Direction the sign faces — used only for position offset. */
    private Direction getFacing() {
        if (blockState().getBlock() instanceof WallSignBlock)        return blockState().getValue(WallSignBlock.FACING);
        if (blockState().getBlock() instanceof WallHangingSignBlock) return blockState().getValue(WallHangingSignBlock.FACING);
        // Floor/ceiling: derive direction from rotation (0=S, 4=W, 8=N, 12=E)
        int rot = 0;
        if (blockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16))
            rot = blockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16);
        else if (blockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16))
            rot = blockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16);
        return Direction.fromYRot(rot * 22.5f);
    }

    private Vec3 textPos(RenderContext ctx, Direction facing, boolean back) {
        // 0.44 = just past the sign face (7/16 = 0.4375) to avoid Z-fighting with BLOCK_DISPLAY
        double outward = back ? -0.44 : 0.44;
        double yCenter = isHangingSign() ? 0.25 : 0.5;
        Vec3 local = new Vec3(
                localPos().getX() + 0.5 + facing.getStepX() * outward,
                localPos().getY() + yCenter,
                localPos().getZ() + 0.5 + facing.getStepZ() * outward);
        return ContraptionMath.renderPosition(local, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
    }

    private boolean isHangingSign() {
        return blockState().getBlock() instanceof CeilingHangingSignBlock
                || blockState().getBlock() instanceof WallHangingSignBlock;
    }

    private void spawnText(Player viewer, int eid, UUID uuid, Vec3 pos, float contraptionYaw, boolean back,
                           org.joml.Quaternionf tiltQ) {
        float textYaw = getTextEntityYaw(contraptionYaw, back);
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(eid, uuid, pos.x, pos.y, pos.z,
                        0f, textYaw, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid,
                        buildTextMeta(back ? backText : frontText, tiltQ))
        ), false);
    }

    private void sendTextMeta(List<Player> viewers, int eid, Set<UUID> shown, Component text,
                               org.joml.Quaternionf tiltQ) {
        List<Object> meta = buildTextMeta(text, tiltQ);
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid, meta);
        for (Player p : viewers) { if (shown.contains(p.uuid())) p.sendPacket(pkt, false); }
    }

    private void syncPos(Player viewer, int eid, Vec3 pos, float yaw) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                eid, pos.x, pos.y, pos.z, yaw, 0f, false), false);
    }

    private List<Object> buildTextMeta(Component text) {
        return buildTextMeta(text, null);
    }

    private List<Object> buildTextMeta(Component text, org.joml.Quaternionf tiltQ) {
        List<Object> meta = new ArrayList<>();
        if (text != null && !text.getString().isEmpty()) {
            DisplayData.TextDisplayData.Text.addEntityData(text, meta);
        }
        DisplayData.TextDisplayData.BackgroundColor.addEntityData(0x00000000, meta);
        // Apply pitch/roll tilt so text follows contraption orientation
        if (tiltQ != null) {
            DisplayData.LeftRotation.addEntityData(tiltQ, meta);
        }
        DisplayData.Scale.addEntityData(new org.joml.Vector3f(0.45f, 0.45f, 0.45f), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    private static Component buildTextComponent(net.minecraft.world.level.block.entity.SignText signText) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                if (i > 0) sb.append("\n");
                Component line = signText.getMessage(i, false);
                if (line != null) sb.append(line.getString());
            }
            return Component.literal(sb.toString());
        } catch (Throwable ignored) { return Component.empty(); }
    }

    private void readText(CompoundTag nbt) {
        if (nbt == null) return;
        try {
            var server = net.minecraft.server.MinecraftServer.getServer();
            // Parse sign text from NBT via a temporary SignBlockEntity
            var be = net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                    BlockPos.ZERO, blockState(), nbt, server.registryAccess());
            if (be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
                frontText = buildTextComponent(sign.getFrontText());
                backText  = buildTextComponent(sign.getBackText());
            }
        } catch (Throwable ignored) {}
    }
}
