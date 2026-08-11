package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionCampfireElement implements ContraptionElement {

    private static final int SLOTS = 4;
    private static final Vec3[] ITEM_OFFSETS = {
        new Vec3( 0.0,  0.31, -0.3125),
        new Vec3( 0.0,  0.31,  0.3125),
        new Vec3(-0.3125, 0.31,  0.0),
        new Vec3( 0.3125, 0.31,  0.0),
    };
    private static final int DAMAGE_INTERVAL_TICKS = 20;

    private final BlockPos localPos;
    private BlockState blockState;
    private final boolean isSoul;

    private final ItemStack[] slots = new ItemStack[SLOTS];
    private int damageTimer = 0;

    private final int blockEntityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID blockUuid = UUID.randomUUID();
    private final Object blockRemovePacket;
    private final Set<net.momirealms.craftengine.core.entity.player.Player> blockShownTo = ConcurrentHashMap.newKeySet();
    private boolean blockMetaDirty = true;

    private final int[] itemEntityIds = new int[SLOTS];
    private final UUID[] itemUuids = new UUID[SLOTS];
    private final Object[] itemRemovePackets = new Object[SLOTS];
    private final Set<net.momirealms.craftengine.core.entity.player.Player>[] itemShownTo;
    private final boolean[] itemDirty = new boolean[SLOTS];

    @SuppressWarnings("unchecked")
    public ContraptionCampfireElement(BlockPos localPos, BlockState blockState) {
        this.localPos = localPos;
        this.blockState = blockState;
        this.isSoul = blockState.is(Blocks.SOUL_CAMPFIRE);
        Arrays.fill(this.slots, ItemStack.EMPTY);
        this.blockRemovePacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(blockEntityId));
        this.itemShownTo = new Set[SLOTS];
        for (int i = 0; i < SLOTS; i++) {
            itemEntityIds[i] = net.minecraft.world.entity.Entity.nextEntityId();
            itemUuids[i] = UUID.randomUUID();
            itemRemovePackets[i] = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(itemEntityIds[i]));
            itemShownTo[i] = ConcurrentHashMap.newKeySet();
            itemDirty[i] = true;
        }
    }

    @Override public Key type() { return dev.arubik.craftengine.contraption.element.ElementTypes.CAMPFIRE; }
    @Override public Vec3 localOffset() { return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5); }
    @Override public boolean isValid() { return blockState != null && !blockState.isAir(); }

    @Override
    public int[] entityIds() {
        int[] ids = new int[1 + SLOTS];
        ids[0] = blockEntityId;
        System.arraycopy(itemEntityIds, 0, ids, 1, SLOTS);
        return ids;
    }

    @Override
    public List<AABB> interactionBounds() {
        return List.of(new AABB(localPos.getX(), localPos.getY(), localPos.getZ(),
                localPos.getX() + 1.0, localPos.getY() + 1.0, localPos.getZ() + 1.0));
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;

        BlockState live = ctx.level().getBlockState(localPos);
        if (!live.equals(blockState)) {
            blockState = live;
            blockMetaDirty = true;
        }

        if (isLit() && ctx.realLevel() != null) {
            damageTimer++;
            if (damageTimer >= DAMAGE_INTERVAL_TICKS) {
                damageTimer = 0;
                applyFireDamage(ctx);
            }
        } else {
            damageTimer = 0;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 center = localOffset();
        Vec3 worldPos = ContraptionMath.renderPosition(center, ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float) ctx.yawDegrees();

        for (net.momirealms.craftengine.core.entity.player.Player viewer : ctx.viewers()) {
            if (blockShownTo.add(viewer)) spawnBlock(viewer, worldPos, yawDeg);
            else if (ctx.moved()) syncPos(viewer, blockEntityId, worldPos, yawDeg);
        }
        if (blockMetaDirty) {
            sendBlockMeta(ctx.viewers());
            blockMetaDirty = false;
        }

        for (int i = 0; i < SLOTS; i++) {
            Vec3 slotLocal = new Vec3(
                    localPos.getX() + 0.5 + ITEM_OFFSETS[i].x,
                    localPos.getY()       + ITEM_OFFSETS[i].y,
                    localPos.getZ() + 0.5 + ITEM_OFFSETS[i].z);
            Vec3 itemWorld = ContraptionMath.renderPosition(slotLocal, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            for (net.momirealms.craftengine.core.entity.player.Player viewer : ctx.viewers()) {
                if (itemShownTo[i].add(viewer)) spawnItem(viewer, i, itemWorld, yawDeg);
                else if (ctx.moved()) syncPos(viewer, itemEntityIds[i], itemWorld, yawDeg);
            }
            if (itemDirty[i]) {
                sendItemMeta(ctx.viewers(), i);
                itemDirty[i] = false;
            }
        }

        spawnParticles(ctx, worldPos);
    }

    private void spawnParticles(RenderContext ctx, Vec3 worldPos) {
        if (!isLit() || !(ctx.realLevel() instanceof ServerLevel sl)) return;
        net.minecraft.core.particles.ParticleOptions smokeType = isSoul
                ? net.minecraft.core.particles.ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                : net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE;
        try {
            sl.sendParticles(smokeType, worldPos.x, worldPos.y + 1.0, worldPos.z, 1, 0.2, 0, 0.2, 0.01);
            for (int i = 0; i < SLOTS; i++) {
                if (!slots[i].isEmpty()) {
                    Vec3 slotLocal = new Vec3(
                            localPos.getX() + 0.5 + ITEM_OFFSETS[i].x,
                            localPos.getY() + ITEM_OFFSETS[i].y,
                            localPos.getZ() + 0.5 + ITEM_OFFSETS[i].z);
                    Vec3 itemWorld = ContraptionMath.renderPosition(slotLocal, ctx.bearing(),
                            ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            itemWorld.x, itemWorld.y + 0.3, itemWorld.z, 1, 0.05, 0.05, 0.05, 0.01);
                }
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void despawn(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            if (blockShownTo.remove(p)) p.sendPacket(blockRemovePacket, false);
            for (int i = 0; i < SLOTS; i++) {
                if (itemShownTo[i].remove(p)) p.sendPacket(itemRemovePackets[i], false);
            }
        }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        BlockPos worldPos = ContraptionMath.toWorld(
                rotateLocal(localPos, quarterTurns), bearingPos);
        level.setBlock(worldPos, blockState.rotate(rotationFromQuarterTurns(quarterTurns)), 3);
        var be = level.getBlockEntity(worldPos);
        if (be instanceof net.minecraft.world.level.block.entity.CampfireBlockEntity campfireBE) {
            for (int i = 0; i < SLOTS; i++) {
                if (!slots[i].isEmpty()) campfireBE.getItems().set(i, slots[i].copy());
            }
        }
    }

    @Override
    public boolean onInteract(ServerPlayer player, ContraptionState state, Vec3 hitPos,
                               InteractionHand hand, boolean rightClick) {
        if (!rightClick) {
            for (int i = 0; i < SLOTS; i++) {
                if (!slots[i].isEmpty()) {
                    ItemStack drop = slots[i].copy();
                    slots[i] = ItemStack.EMPTY;
                    itemDirty[i] = true;
                    if (player.level() instanceof ServerLevel sl) {
                        Vec3 bearing = new Vec3(state.x(), state.y(), state.z());
                        Vec3 dropPos = ContraptionMath.renderPosition(
                                new Vec3(localPos.getX() + 0.5, localPos.getY() + 1.0, localPos.getZ() + 0.5),
                                bearing, state.yawRadians(), state.pitchRadians(), state.rollRadians(), state.scale());
                        sl.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                                sl, dropPos.x, dropPos.y, dropPos.z, drop));
                    }
                    playSound(player, net.minecraft.sounds.SoundEvents.CAMPFIRE_CRACKLE);
                    return true;
                }
            }
            return false;
        }

        ItemStack held = player.getItemInHand(hand);

        if (!held.isEmpty() && held.getItem() instanceof net.minecraft.world.item.ShovelItem) {
            if (isLit()) {
                blockState = blockState.setValue(CampfireBlock.LIT, false);
                blockMetaDirty = true;
                playSound(player, net.minecraft.sounds.SoundEvents.GENERIC_EXTINGUISH_FIRE);
            }
            return true;
        }

        if (held.isEmpty()) return false;

        for (int i = 0; i < SLOTS; i++) {
            if (slots[i].isEmpty()) {
                slots[i] = held.copyWithCount(1);
                if (!player.isCreative()) held.shrink(1);
                itemDirty[i] = true;
                playSound(player, net.minecraft.sounds.SoundEvents.CAMPFIRE_CRACKLE);
                return true;
            }
        }
        return false;
    }

    private boolean isLit() {
        try {
            return blockState.hasProperty(CampfireBlock.LIT) && blockState.getValue(CampfireBlock.LIT);
        } catch (Throwable ignored) { return false; }
    }

    private void spawnBlock(net.momirealms.craftengine.core.entity.player.Player viewer, Vec3 pos, float yaw) {
        List<Object> meta = new ArrayList<>();
        DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, meta);
        DisplayData.Translation.addEntityData(new Vector3f(-0.5f, -0.5f, -0.5f), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(blockEntityId, blockUuid,
                        pos.x, pos.y, pos.z, 0f, yaw, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, yaw),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(blockEntityId, meta)
        ), false);
    }

    private void sendBlockMeta(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        List<Object> meta = new ArrayList<>();
        DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(blockEntityId, meta);
        for (var p : viewers) { if (blockShownTo.contains(p)) p.sendPacket(pkt, false); }
    }

    private void spawnItem(net.momirealms.craftengine.core.entity.player.Player viewer, int slot, Vec3 pos, float yaw) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(itemEntityIds[slot], itemUuids[slot],
                        pos.x, pos.y, pos.z, 0f, yaw, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityIds[slot], buildItemMeta(slot))
        ), false);
    }

    private void sendItemMeta(List<net.momirealms.craftengine.core.entity.player.Player> viewers, int slot) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityIds[slot], buildItemMeta(slot));
        for (var p : viewers) { if (itemShownTo[slot].contains(p)) p.sendPacket(pkt, false); }
    }

    private List<Object> buildItemMeta(int slot) {
        List<Object> meta = new ArrayList<>();
        if (!slots[slot].isEmpty()) {
            Object nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(slots[slot]));
            DisplayData.ItemDisplayData.ItemStack.addEntityData(nms, meta);
        }
        DisplayData.LeftRotation.addEntityData(new Quaternionf().rotateX((float) Math.toRadians(-90)), meta);
        DisplayData.Scale.addEntityData(new Vector3f(0.375f, 0.375f, 0.375f), meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    private void syncPos(net.momirealms.craftengine.core.entity.player.Player viewer, int eid, Vec3 pos, float yaw) {
        viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                eid, pos.x, pos.y, pos.z, yaw, 0f, false), false);
    }

    private void applyFireDamage(RenderContext ctx) {
        if (!(ctx.realLevel() instanceof ServerLevel sl)) return;
        Vec3 bearing = ctx.bearing();
        Vec3 worldCenter = ContraptionMath.renderPosition(localOffset(), bearing,
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        double r = 0.7 * ctx.scale();
        AABB box = new AABB(worldCenter.x - r, worldCenter.y, worldCenter.z - r,
                worldCenter.x + r, worldCenter.y + 1.5 * ctx.scale(), worldCenter.z + r);
        float dmg = isSoul ? 2.0f : 1.0f;
        try {
            for (net.minecraft.world.entity.Entity entity : sl.getEntities(
                    (net.minecraft.world.entity.Entity) null, box,
                    e -> !(e instanceof Player))) {
                entity.hurtServer(sl, sl.damageSources().inFire(), dmg);
            }
        } catch (Throwable ignored) {}
    }

    private static void playSound(ServerPlayer player, net.minecraft.sounds.SoundEvent event) {
        try {
            player.level().playSound(null, player.blockPosition(),
                    event, net.minecraft.sounds.SoundSource.BLOCKS, 1f, 1f);
        } catch (Throwable ignored) {}
    }

    private static BlockPos rotateLocal(BlockPos local, int quarterTurns) {
        return switch (quarterTurns & 3) {
            case 0 -> local;
            case 1 -> new BlockPos(-local.getZ(), local.getY(), local.getX());
            case 2 -> new BlockPos(-local.getX(), local.getY(), -local.getZ());
            case 3 -> new BlockPos(local.getZ(), local.getY(), -local.getX());
            default -> local;
        };
    }

    private static net.minecraft.world.level.block.Rotation rotationFromQuarterTurns(int quarterTurns) {
        return switch (quarterTurns & 3) {
            case 1 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
            case 2 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
            case 3 -> net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;
            default -> net.minecraft.world.level.block.Rotation.NONE;
        };
    }
}
