package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionSkullElement implements ContraptionElement {

    private final BlockPos localPos;
    private BlockState blockState;
    private final CompoundTag skullNbt;

    private final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private boolean metaDirty = true;
    private double lastPitch, lastRoll, lastScale = 1.0;

    public ContraptionSkullElement(BlockPos localPos, BlockState blockState, CompoundTag skullNbt) {
        this.localPos = localPos;
        this.blockState = blockState;
        this.skullNbt = skullNbt;
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    @Override public Key type() { return ElementTypes.SKULL; }
    @Override public Vec3 localOffset() { return new Vec3(localPos.getX() + 0.5, localPos.getY() + 0.5, localPos.getZ() + 0.5); }
    @Override public boolean isValid() { return blockState != null && !blockState.isAir(); }
    @Override public int[] entityIds() { return new int[]{entityId}; }

    @Override
    public List<AABB> interactionBounds() {
        return List.of(new AABB(localPos.getX() + 0.25, localPos.getY(), localPos.getZ() + 0.25,
                localPos.getX() + 0.75, localPos.getY() + 0.5, localPos.getZ() + 0.75));
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) return;
        BlockState live = ctx.level().getBlockState(localPos);
        if (!live.equals(blockState)) { blockState = live; metaDirty = true; }
        if (ctx.pitchRadians() != lastPitch || ctx.rollRadians() != lastRoll || ctx.scale() != lastScale) {
            lastPitch = ctx.pitchRadians(); lastRoll = ctx.rollRadians(); lastScale = ctx.scale();
            metaDirty = true;
        }
    }

    @Override
    public void render(RenderContext ctx) {
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        float yawDeg = (float) ctx.yawDegrees();
        for (Player viewer : ctx.viewers()) {
            if (shownTo.add(viewer.uuid())) spawn(viewer, worldPos, yawDeg);
            else if (ctx.moved()) viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                    entityId, worldPos.x, worldPos.y, worldPos.z, yawDeg, 0f, false), false);
        }
        if (metaDirty) { sendMeta(ctx.viewers()); metaDirty = false; }
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Player p : viewers) { if (shownTo.remove(p.uuid())) p.sendPacket(despawnPacket, false); }
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        BlockPos worldPos = net.minecraft.core.BlockPos.containing(bearingPos.getX() + localPos.getX(),
                bearingPos.getY() + localPos.getY(), bearingPos.getZ() + localPos.getZ());
        level.setBlock(worldPos, blockState, 3);
        if (skullNbt != null) {
            var be = net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                    worldPos, blockState, skullNbt, level.registryAccess());
            if (be != null) { be.setLevel(level); level.setBlockEntity(be); }
        }
    }

    private void spawn(Player viewer, Vec3 pos, float yaw) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid, pos.x, pos.y, pos.z,
                        0f, yaw, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta())
        ), false);
    }

    private void sendMeta(List<Player> viewers) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, buildMeta());
        for (Player p : viewers) { if (shownTo.contains(p.uuid())) p.sendPacket(pkt, false); }
    }

    private List<Object> buildMeta() {
        List<Object> meta = new ArrayList<>();
        ItemStack item = resolveSkullItem();
        Object nms = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(
                org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(item));
        DisplayData.ItemDisplayData.ItemStack.addEntityData(nms, meta);
        // Skull face rotation: wall skulls face outward, floor skulls use rotation property (0-15)
        Quaternionf rot = buildSkullRotation();
        DisplayData.LeftRotation.addEntityData(rot, meta);
        float s = (float) lastScale * 0.625f; // skull is 10/16 of a block
        DisplayData.Scale.addEntityData(new Vector3f(s, s, s), meta);
        DisplayData.Translation.addEntityData(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), meta);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), meta);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, meta);
        return meta;
    }

    private ItemStack resolveSkullItem() {
        if (skullNbt != null && skullNbt.contains("profile")) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.set(net.minecraft.core.component.DataComponents.PROFILE,
                    net.minecraft.world.item.component.ResolvableProfile.CODEC
                            .parse(net.minecraft.nbt.NbtOps.INSTANCE, skullNbt.get("profile"))
                            .result().orElse(null));
            return head;
        }
        var skullType = blockState.getBlock() instanceof SkullBlock skull ? skull.getType() : null;
        if (skullType == null) return new ItemStack(Items.SKELETON_SKULL);
        return switch (skullType.toString().toLowerCase()) {
            case "wither_skeleton" -> new ItemStack(Items.WITHER_SKELETON_SKULL);
            case "zombie"          -> new ItemStack(Items.ZOMBIE_HEAD);
            case "creeper"         -> new ItemStack(Items.CREEPER_HEAD);
            case "piglin"          -> new ItemStack(Items.PIGLIN_HEAD);
            case "dragon"          -> new ItemStack(Items.DRAGON_HEAD);
            default                -> new ItemStack(Items.SKELETON_SKULL);
        };
    }

    private Quaternionf buildSkullRotation() {
        if (blockState.getBlock() instanceof WallSkullBlock) {
            Direction facing = blockState.getValue(WallSkullBlock.FACING);
            return switch (facing) {
                case NORTH -> new Quaternionf();
                case SOUTH -> new Quaternionf().rotateY((float) Math.toRadians(180));
                case WEST  -> new Quaternionf().rotateY((float) Math.toRadians(90));
                case EAST  -> new Quaternionf().rotateY((float) Math.toRadians(270));
                default    -> new Quaternionf();
            };
        }
        // Floor skull: rotation property 0-15, each step = 22.5 deg
        int rot = blockState.hasProperty(SkullBlock.ROTATION) ? blockState.getValue(SkullBlock.ROTATION) : 0;
        return new Quaternionf().rotateY((float) Math.toRadians(rot * 22.5));
    }
}
