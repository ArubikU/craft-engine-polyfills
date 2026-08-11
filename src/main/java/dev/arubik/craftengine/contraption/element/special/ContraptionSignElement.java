package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Abstract base for all sign element types. Handles text entities, interact, tick. */
public abstract class ContraptionSignElement extends ContraptionBlockElement {

    private final int frontId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID frontUuid = UUID.randomUUID();
    private final Object frontRemove;
    private final Set<UUID> frontShown = ConcurrentHashMap.newKeySet();

    private final int backId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID backUuid = UUID.randomUUID();
    private final Object backRemove;
    private final Set<UUID> backShown = ConcurrentHashMap.newKeySet();

    private Component frontText = Component.empty();
    private Component backText = Component.empty();
    private boolean frontGlowing = false;
    private boolean backGlowing = false;
    protected boolean textDirty = true;

    protected ContraptionSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag,
                                      float signYawOffset) {
        super(localPos, blockState, beTag, false, signYawOffset);
        this.frontRemove = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(frontId));
        this.backRemove  = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(backId));
        readText(beTag);
    }

    // --- abstract: each sign type implements its own rotation/position ---

    /** Entity yaw for the TEXT_DISPLAY facing the correct direction. */
    protected abstract float getTextEntityYaw(float contraptionYaw, boolean back);

    /** Direction the sign faces (used for text position offset). */
    protected abstract Direction getFacing();

    /** Y center of the text in local block coords (0.5 for floor/wall, 0.25 for hanging). */
    protected float textYCenter() { return 0.5f; }

    // --- shared ---

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
        super.render(ctx);

        Direction facing = getFacing();
        float contraptionYaw = (float) ctx.yawDegrees();
        org.joml.Quaternionf tiltQ = (ctx.pitchRadians() == 0.0 && ctx.rollRadians() == 0.0) ? null
                : new org.joml.Quaternionf().rotateX((float) ctx.pitchRadians()).rotateZ((float) ctx.rollRadians());

        Vec3 frontPos = textPos(ctx, facing, false);
        Vec3 backPos  = textPos(ctx, facing, true);
        float frontYaw = getTextEntityYaw(contraptionYaw, false);
        float backYaw  = getTextEntityYaw(contraptionYaw, true);

        for (Player viewer : ctx.viewers()) {
            if (frontShown.add(viewer.uuid())) spawnText(viewer, frontId, frontUuid, frontPos, frontYaw, false, tiltQ);
            else if (ctx.moved()) syncPos(viewer, frontId, frontPos, frontYaw);

            if (backShown.add(viewer.uuid())) spawnText(viewer, backId, backUuid, backPos, backYaw, true, tiltQ);
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

    private Vec3 textPos(RenderContext ctx, Direction facing, boolean back) {
        double outward = back ? -0.38 : 0.38;
        Vec3 local = new Vec3(
                localPos().getX() + 0.5 + facing.getStepX() * outward,
                localPos().getY() + textYCenter(),
                localPos().getZ() + 0.5 + facing.getStepZ() * outward);
        return ContraptionMath.renderPosition(local, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
    }

    private void spawnText(Player viewer, int eid, UUID uuid, Vec3 pos, float yaw, boolean back,
                           org.joml.Quaternionf tiltQ) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(eid, uuid, pos.x, pos.y, pos.z,
                        0f, yaw, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid,
                        buildTextMeta(back ? backText : frontText, tiltQ,
                                back ? backGlowing : frontGlowing))
        ), false);
    }

    private void sendTextMeta(List<Player> viewers, int eid, Set<UUID> shown, Component text,
                               org.joml.Quaternionf tiltQ, boolean glowing) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(eid,
                buildTextMeta(text, tiltQ, glowing));
        for (Player p : viewers) { if (shown.contains(p.uuid())) p.sendPacket(pkt, false); }
    }

    private void syncPos(Player viewer, int eid, Vec3 pos, float yaw) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                eid, pos.x, pos.y, pos.z, yaw, 0f, false), false);
    }

    private List<Object> buildTextMeta(Component text, org.joml.Quaternionf tiltQ, boolean glowing) {
        List<Object> meta = new ArrayList<>();
        if (text != null && !text.getString().isEmpty()) {
            DisplayData.TextDisplayData.Text.addEntityData(text, meta);
        }
        DisplayData.TextDisplayData.BackgroundColor.addEntityData(0x00000000, meta);
        if (tiltQ != null) DisplayData.LeftRotation.addEntityData(tiltQ, meta);
        if (glowing) DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        DisplayData.Scale.addEntityData(new org.joml.Vector3f(0.45f, 0.45f, 0.45f), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    protected static Component buildTextComponent(net.minecraft.world.level.block.entity.SignText signText) {
        try {
            net.minecraft.network.chat.MutableComponent result = null;
            for (int i = 0; i < 4; i++) {
                Component line = signText.getMessage(i, false);
                if (line == null) line = Component.empty();
                result = result == null ? line.copy()
                        : result.append(Component.literal("\n")).append(line);
            }
            if (result == null) return Component.empty();
            int rgb = signText.getColor().getTextColor();
            return result.withStyle(s -> s.withColor(rgb));
        } catch (Throwable ignored) { return Component.empty(); }
    }

    @Override
    public boolean onInteract(net.minecraft.server.level.ServerPlayer player, ContraptionState state,
                               Vec3 hitPos, net.minecraft.world.InteractionHand hand, boolean rightClick) {
        if (!rightClick) return super.onInteract(player, state, hitPos, hand, false);
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty())
            return super.onInteract(player, state, hitPos, hand, true);

        var be = state.level() != null ? state.level().getBlockEntity(localPos()) : null;
        if (!(be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign)) return false;

        net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);
        boolean isFront = isFrontFace(hitPos);

        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.HONEYCOMB)) {
            if (!sign.isWaxed()) {
                sign.setWaxed(true); textDirty = true;
                if (!player.isCreative()) held.shrink(1);
                if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                    Vec3 wp = worldPos(state);
                    sl.playSound(null, net.minecraft.core.BlockPos.containing(wp),
                            net.minecraft.sounds.SoundEvents.HONEYCOMB_WAX_ON,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.WAX_ON,
                            wp.x, wp.y + 0.5, wp.z, 7, 0.4, 0.4, 0.4, 0);
                }
            }
            return true;
        }
        if (!held.isEmpty() && held.getItem() instanceof net.minecraft.world.item.DyeItem dye) {
            setSignText(sign, isFront, t -> t.setColor(dye.getDyeColor())); textDirty = true;
            if (!player.isCreative()) held.shrink(1);
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.DYE_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.GLOW_INK_SAC)) {
            setSignText(sign, isFront, t -> t.setHasGlowingText(true)); textDirty = true;
            if (!player.isCreative()) held.shrink(1);
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.GLOW_INK_SAC_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        if (!held.isEmpty() && held.is(net.minecraft.world.item.Items.INK_SAC)) {
            setSignText(sign, isFront, t -> t.setHasGlowingText(false)); textDirty = true;
            if (!player.isCreative()) held.shrink(1);
            player.level().playSound(null, player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.INK_SAC_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        if (!sign.isWaxed()) {
            try { player.openTextEdit(sign, isFront); } catch (Throwable ignored) {}
            return true;
        }
        return false;
    }

    private static void setSignText(net.minecraft.world.level.block.entity.SignBlockEntity sign,
                                     boolean front, java.util.function.UnaryOperator<net.minecraft.world.level.block.entity.SignText> op) {
        try {
            String m = front ? "setFrontText" : "setBackText";
            String g = front ? "getFrontText" : "getBackText";
            var get = sign.getClass().getMethod(g);
            var set = sign.getClass().getDeclaredMethod(m, net.minecraft.world.level.block.entity.SignText.class);
            set.setAccessible(true);
            set.invoke(sign, op.apply((net.minecraft.world.level.block.entity.SignText) get.invoke(sign)));
        } catch (Throwable ignored) {}
    }

    private boolean isFrontFace(Vec3 hitPos) {
        Direction facing = getFacing();
        double dot = facing.getStepX() * (hitPos.x - localPos().getX() - 0.5)
                   + facing.getStepZ() * (hitPos.z - localPos().getZ() - 0.5);
        return dot >= 0;
    }

    private Vec3 worldPos(ContraptionState state) {
        return ContraptionMath.renderPosition(localOffset(),
                new Vec3(state.x(), state.y(), state.z()),
                state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
    }

    protected void readText(CompoundTag nbt) {
        if (nbt == null) return;
        try {
            var server = net.minecraft.server.MinecraftServer.getServer();
            var be = net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                    BlockPos.ZERO, blockState(), nbt, server.registryAccess());
            if (be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
                frontText = buildTextComponent(sign.getFrontText());
                backText  = buildTextComponent(sign.getBackText());
            }
        } catch (Throwable ignored) {}
    }

    public static final int SIGN_ROTATION_16 = 0; // placeholder, accessed via property
}
