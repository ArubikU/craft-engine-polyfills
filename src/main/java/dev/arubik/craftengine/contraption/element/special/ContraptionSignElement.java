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
    private boolean frontGlowing = false;
    private boolean backGlowing = false;
    private boolean textDirty = true;

    public ContraptionSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, false, standingSignYawOffset(blockState));
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
        if (be != null && !(be instanceof net.minecraft.world.level.block.entity.SignBlockEntity)) {
            org.bukkit.Bukkit.getLogger().info("[Sign] BE at " + localPos() + " is " + be.getClass().getSimpleName() + " not SignBlockEntity");
        }
        if (be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
            Component lf = buildTextComponent(sign.getFrontText());
            Component lb = buildTextComponent(sign.getBackText());
            boolean fg = sign.getFrontText().hasGlowingText();
            boolean bg = sign.getBackText().hasGlowingText();
            if (!lf.equals(frontText) || !lb.equals(backText)
                    || frontText.getString().isEmpty() || backText.getString().isEmpty()
                    || fg != frontGlowing || bg != backGlowing) {
                frontText = lf; backText = lb;
                frontGlowing = fg; backGlowing = bg;
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
            sendTextMeta(ctx.viewers(), frontId, frontShown, frontText, tiltQ, frontGlowing);
            sendTextMeta(ctx.viewers(), backId,  backShown,  backText, tiltQ, backGlowing);
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
                               org.joml.Quaternionf tiltQ, boolean glowing) {
        List<Object> meta = buildTextMeta(text, tiltQ, glowing);
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid, meta);
        for (Player p : viewers) { if (shown.contains(p.uuid())) p.sendPacket(pkt, false); }
    }

    private void syncPos(Player viewer, int eid, Vec3 pos, float yaw) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                eid, pos.x, pos.y, pos.z, yaw, 0f, false), false);
    }

    private List<Object> buildTextMeta(Component text, org.joml.Quaternionf tiltQ) {
        return buildTextMeta(text, tiltQ, false);
    }

    private List<Object> buildTextMeta(Component text, org.joml.Quaternionf tiltQ, boolean glowing) {
        List<Object> meta = new ArrayList<>();
        if (text != null && !text.getString().isEmpty()) {
            DisplayData.TextDisplayData.Text.addEntityData(text, meta);
        }
        DisplayData.TextDisplayData.BackgroundColor.addEntityData(0x00000000, meta);
        if (tiltQ != null) {
            DisplayData.LeftRotation.addEntityData(tiltQ, meta);
        }
        // Glowing text = full brightness override (self-lit, like vanilla glow ink effect)
        if (glowing) {
            DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        }
        DisplayData.Scale.addEntityData(new org.joml.Vector3f(0.45f, 0.45f, 0.45f), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    private static Component buildTextComponent(net.minecraft.world.level.block.entity.SignText signText) {
        try {
            // Build multi-line component preserving each line's formatting
            net.minecraft.network.chat.MutableComponent result = null;
            for (int i = 0; i < 4; i++) {
                Component line = signText.getMessage(i, false);
                if (line == null) line = Component.empty();
                if (result == null) {
                    result = line.copy();
                } else {
                    result = result.append(Component.literal("\n")).append(line);
                }
            }
            if (result == null) return Component.empty();
            // Apply sign dye color
            net.minecraft.world.item.DyeColor dyeColor = signText.getColor();
            int rgb = dyeColor.getTextColor();
            result = result.withStyle(s -> s.withColor(rgb));
            return result;
        } catch (Throwable ignored) { return Component.empty(); }
    }

    @Override
    public boolean onInteract(net.minecraft.server.level.ServerPlayer player,
                               dev.arubik.craftengine.contraption.core.ContraptionState state,
                               net.minecraft.world.phys.Vec3 hitPos,
                               net.minecraft.world.InteractionHand hand,
                               boolean rightClick) {
        if (!rightClick) {
            return super.onInteract(player, state, hitPos, hand, false);
        }
        // Right-shift with empty hand → fall through to super (vanilla block interact)
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            return super.onInteract(player, state, hitPos, hand, true);
        }

        var be = state.level() != null ? state.level().getBlockEntity(localPos()) : null;
        if (!(be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign)) return false;

        net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);

        // Wax: honeycomb waxes the sign (prevents future editing)
        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.HONEYCOMB)) {
            if (!sign.isWaxed()) {
                sign.setWaxed(true);
                textDirty = true;
                if (!player.isCreative()) held.shrink(1);
                if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                    net.minecraft.world.phys.Vec3 wp = worldPos(state);
                    sl.playSound(null, net.minecraft.core.BlockPos.containing(wp),
                            net.minecraft.sounds.SoundEvents.HONEYCOMB_WAX_ON,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.WAX_ON,
                            wp.x, wp.y + 0.5, wp.z, 7, 0.4, 0.4, 0.4, 0);
                }
            }
            return true;
        }

        // Determine which face the player is looking at using hit position relative to sign facing
        boolean isFront = isFrontFace(hitPos);

        // Dye: change text color
        if (!held.isEmpty() && held.getItem() instanceof net.minecraft.world.item.DyeItem dye) {
            net.minecraft.world.item.DyeColor color = dye.getDyeColor();
            try {
                java.lang.reflect.Method setter = isFront
                        ? net.minecraft.world.level.block.entity.SignBlockEntity.class.getDeclaredMethod("setFrontText",
                            net.minecraft.world.level.block.entity.SignText.class)
                        : net.minecraft.world.level.block.entity.SignBlockEntity.class.getDeclaredMethod("setBackText",
                            net.minecraft.world.level.block.entity.SignText.class);
                setter.setAccessible(true);
                var signText = isFront ? sign.getFrontText() : sign.getBackText();
                setter.invoke(sign, signText.setColor(color));
            } catch (Throwable ignored) {}
            if (!player.isCreative()) held.shrink(1);
            textDirty = true;
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.DYE_USE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }

        // Glow ink sac: toggle glowing text
        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.GLOW_INK_SAC)) {
            toggleGlow(sign, isFront, true);
            if (!player.isCreative()) held.shrink(1);
            textDirty = true;
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.GLOW_INK_SAC_USE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }

        // Ink sac: remove glow
        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.INK_SAC)) {
            toggleGlow(sign, isFront, false);
            if (!player.isCreative()) held.shrink(1);
            textDirty = true;
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.INK_SAC_USE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }

        // Open sign editor (unless waxed)
        // NOTE: ServerboundSignUpdatePacket interceptor needed to route text back to ContraptionLevel BE
        if (!sign.isWaxed()) {
            try {
                player.openTextEdit(sign, isFront);
            } catch (Throwable ignored) {}
            return true;
        }

        return false;
    }

    /** For standing signs, returns the ROTATION_16 yaw so BLOCK_DISPLAY entity yaw handles facing.
     *  Wall/hanging signs: 0 (blockstate FACING already encodes direction correctly). */
    private static float standingSignYawOffset(BlockState blockState) {
        if (blockState.getBlock() instanceof WallSignBlock) return 0f;
        if (blockState.getBlock() instanceof WallHangingSignBlock) return 0f;
        if (blockState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16)) {
            return blockState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.ROTATION_16) * 22.5f;
        }
        return 0f;
    }

    private net.minecraft.world.phys.Vec3 worldPos(dev.arubik.craftengine.contraption.core.ContraptionState state) {
        return dev.arubik.craftengine.contraption.assembly.ContraptionMath.renderPosition(
                localOffset(),
                new net.minecraft.world.phys.Vec3(state.x(), state.y(), state.z()),
                state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
    }

    /** True if hit position is on the front face of the sign (player looking at same side as sign text). */
    private boolean isFrontFace(net.minecraft.world.phys.Vec3 hitPos) {
        Direction facing = getFacing();
        double bx = localPos().getX() + 0.5, bz = localPos().getZ() + 0.5;
        double dot = facing.getStepX() * (hitPos.x - bx) + facing.getStepZ() * (hitPos.z - bz);
        return dot >= 0;
    }

    private static void toggleGlow(net.minecraft.world.level.block.entity.SignBlockEntity sign,
                                    boolean front, boolean glow) {
        try {
            String methodName = front ? "setFrontText" : "setBackText";
            String getMethodName = front ? "getFrontText" : "getBackText";
            var getText = net.minecraft.world.level.block.entity.SignBlockEntity.class
                    .getMethod(getMethodName);
            var setText = net.minecraft.world.level.block.entity.SignBlockEntity.class
                    .getDeclaredMethod(methodName, net.minecraft.world.level.block.entity.SignText.class);
            setText.setAccessible(true);
            var signText = (net.minecraft.world.level.block.entity.SignText) getText.invoke(sign);
            setText.invoke(sign, signText.setHasGlowingText(glow));
        } catch (Throwable ignored) {}
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
