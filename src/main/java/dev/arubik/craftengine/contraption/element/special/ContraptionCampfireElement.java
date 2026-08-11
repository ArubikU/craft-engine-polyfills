package dev.arubik.craftengine.contraption.element.special;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContraptionCampfireElement extends ContraptionBlockElement {

    private static final int SLOTS = 4;
    private static final Vec3[] ITEM_OFFSETS = {
        new Vec3( 0.0,  0.31, -0.3125),
        new Vec3( 0.0,  0.31,  0.3125),
        new Vec3(-0.3125, 0.31, 0.0),
        new Vec3( 0.3125, 0.31, 0.0),
    };
    private static final int DAMAGE_INTERVAL_TICKS = 20;

    private final boolean isSoul;
    private final ItemStack[] slots = new ItemStack[SLOTS];
    private int damageTimer = 0;

    // 4 ITEM_DISPLAY entities for cooking items
    private final int[] itemEntityIds = new int[SLOTS];
    private final UUID[] itemUuids = new UUID[SLOTS];
    private final Object[] itemRemovePackets = new Object[SLOTS];
    @SuppressWarnings("unchecked")
    private final Set<Player>[] itemShownTo = new Set[SLOTS];
    private final boolean[] itemDirty = new boolean[SLOTS];

    public ContraptionCampfireElement(BlockPos localPos, BlockState blockState) {
        super(localPos, blockState, null, false, 0f);
        this.isSoul = blockState.is(Blocks.SOUL_CAMPFIRE);
        Arrays.fill(this.slots, ItemStack.EMPTY);
        for (int i = 0; i < SLOTS; i++) {
            itemEntityIds[i] = net.minecraft.world.entity.Entity.nextEntityId();
            itemUuids[i] = UUID.randomUUID();
            itemRemovePackets[i] = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(itemEntityIds[i]));
            itemShownTo[i] = ConcurrentHashMap.newKeySet();
            itemDirty[i] = true;
        }
    }

    @Override public Key type() { return ElementTypes.CAMPFIRE; }

    @Override
    public int[] entityIds() {
        int[] base = super.entityIds(); // BLOCK_DISPLAY id
        int[] ids = new int[base.length + SLOTS];
        System.arraycopy(base, 0, ids, 0, base.length);
        System.arraycopy(itemEntityIds, 0, ids, base.length, SLOTS);
        return ids;
    }

    @Override
    public void tick(RenderContext ctx) {
        super.tick(ctx); // syncs blockState from level

        // Re-read cooking items from level BE — vanilla forward handles placement/removal
        if (ctx.level() != null) {
            var be = ctx.level().getBlockEntity(localPos());
            if (be instanceof net.minecraft.world.level.block.entity.CampfireBlockEntity campfire) {
                for (int i = 0; i < SLOTS; i++) {
                    ItemStack live = campfire.getItems().get(i);
                    if (!live.equals(slots[i])) {
                        slots[i] = live.copy();
                        itemDirty[i] = true;
                    }
                }
            }
        }

        // Fire damage to entities on footprint
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
        super.render(ctx); // BLOCK_DISPLAY via inherited ContraptionBlockElement.render()

        // Cooking item ITEM_DISPLAY entities
        float yawDeg = (float) ctx.yawDegrees();
        for (int i = 0; i < SLOTS; i++) {
            Vec3 slotLocal = new Vec3(
                    localPos().getX() + 0.5 + ITEM_OFFSETS[i].x,
                    localPos().getY()       + ITEM_OFFSETS[i].y,
                    localPos().getZ() + 0.5 + ITEM_OFFSETS[i].z);
            Vec3 itemWorld = ContraptionMath.renderPosition(slotLocal, ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
            for (Player viewer : ctx.viewers()) {
                if (itemShownTo[i].add(viewer)) spawnItem(viewer, i, itemWorld, yawDeg);
                else if (ctx.moved()) viewer.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                        itemEntityIds[i], itemWorld.x, itemWorld.y, itemWorld.z, yawDeg, 0f, false), false);
            }
            if (itemDirty[i]) { sendItemMeta(ctx.viewers(), i); itemDirty[i] = false; }
        }

        spawnParticles(ctx);
    }

    @Override
    public void despawn(List<Player> viewers) {
        super.despawn(viewers); // despawn BLOCK_DISPLAY
        for (Player p : viewers) {
            for (int i = 0; i < SLOTS; i++) {
                if (itemShownTo[i].remove(p)) p.sendPacket(itemRemovePackets[i], false);
            }
        }
    }

    @Override
    public void disassemble(net.minecraft.server.level.ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        super.disassemble(level, bearingPos, quarterTurns); // places block
        // Restore cooking items to real-world campfire BE
        BlockPos worldPos = ContraptionMath.toWorld(rotateLocalPos(localPos(), quarterTurns), bearingPos);
        var be = level.getBlockEntity(worldPos);
        if (be instanceof net.minecraft.world.level.block.entity.CampfireBlockEntity campfire) {
            for (int i = 0; i < SLOTS; i++) {
                if (!slots[i].isEmpty()) campfire.getItems().set(i, slots[i].copy());
            }
        }
    }

    // ---- internals ----

    private boolean isLit() {
        try { return blockState().hasProperty(CampfireBlock.LIT) && blockState().getValue(CampfireBlock.LIT); }
        catch (Throwable ignored) { return false; }
    }

    private void applyFireDamage(RenderContext ctx) {
        if (!(ctx.realLevel() instanceof ServerLevel sl)) return;
        Vec3 worldCenter = ContraptionMath.renderPosition(localOffset(), ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        double r = 0.7 * ctx.scale();
        var box = new net.minecraft.world.phys.AABB(worldCenter.x - r, worldCenter.y, worldCenter.z - r,
                worldCenter.x + r, worldCenter.y + 1.5 * ctx.scale(), worldCenter.z + r);
        try {
            for (var entity : sl.getEntities((net.minecraft.world.entity.Entity) null, box,
                    e -> !(e instanceof net.minecraft.world.entity.player.Player))) {
                entity.hurtServer(sl, sl.damageSources().inFire(), isSoul ? 2f : 1f);
            }
        } catch (Throwable ignored) {}
    }

    private void spawnParticles(RenderContext ctx) {
        if (!isLit() || !(ctx.realLevel() instanceof ServerLevel sl)) return;
        var smokeType = isSoul
                ? net.minecraft.core.particles.ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                : net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE;
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset(), ctx.bearing(),
                ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
        try {
            sl.sendParticles(smokeType, worldPos.x, worldPos.y + 1.0, worldPos.z, 1, 0.2, 0, 0.2, 0.01);
            for (int i = 0; i < SLOTS; i++) {
                if (!slots[i].isEmpty()) {
                    Vec3 slotLocal = new Vec3(
                            localPos().getX() + 0.5 + ITEM_OFFSETS[i].x,
                            localPos().getY() + ITEM_OFFSETS[i].y,
                            localPos().getZ() + 0.5 + ITEM_OFFSETS[i].z);
                    Vec3 itemWorld = ContraptionMath.renderPosition(slotLocal, ctx.bearing(),
                            ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                    sl.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            itemWorld.x, itemWorld.y + 0.3, itemWorld.z, 1, 0.05, 0.05, 0.05, 0.01);
                }
            }
        } catch (Throwable ignored) {}
    }

    private void spawnItem(Player viewer, int slot, Vec3 pos, float yaw) {
        viewer.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(itemEntityIds[slot], itemUuids[slot],
                        pos.x, pos.y, pos.z, 0f, yaw, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityIds[slot], buildItemMeta(slot))
        ), false);
    }

    private void sendItemMeta(List<Player> viewers, int slot) {
        Object pkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(itemEntityIds[slot], buildItemMeta(slot));
        for (Player p : viewers) { if (itemShownTo[slot].contains(p)) p.sendPacket(pkt, false); }
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

    private static BlockPos rotateLocalPos(BlockPos local, int q) {
        return switch (q & 3) {
            case 1 -> new BlockPos(-local.getZ(), local.getY(), local.getX());
            case 2 -> new BlockPos(-local.getX(), local.getY(), -local.getZ());
            case 3 -> new BlockPos(local.getZ(), local.getY(), -local.getX());
            default -> local;
        };
    }
}
