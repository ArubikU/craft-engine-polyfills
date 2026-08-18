/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.ContraptionPushSettings;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.contraption.player.PlayerCarry;
import dev.arubik.craftengine.contraption.render.ContraptionItemPickupSwarm;
import dev.arubik.craftengine.contraption.render.ContraptionShulkerColliderSwarm;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;

public final class ContraptionHitboxElement
implements ContraptionElement {
    private final Map<BlockPos, Slot> autoSlots = new HashMap<BlockPos, Slot>();
    private final List<Slot> customSlots = new ArrayList<Slot>();
    private final Set<UUID> currentRiders = new HashSet<UUID>();
    private final Map<UUID, double[]> stepState = new HashMap<UUID, double[]>();
    private static final double STEP_DISTANCE = 2.0;
    private static final double FALL_MIN_SPEED = 0.35;
    private final ContraptionShulkerColliderSwarm shulkerColliders = new ContraptionShulkerColliderSwarm();
    private final Map<BlockPos, CachedCell> cellCache = new HashMap<BlockPos, CachedCell>();
    private static final double LOD_CULL_RADIUS = 7.0;
    private static final ShulkerParams FULL_CUBE;
    private static final double FLOOR_CLEARANCE = 0.35;
    private static final double STATIC_DELTA_SQ = 4.0E-4;
    private static final double RIDER_HALF_WIDTH = 0.3;
    private static final double RIDER_HEIGHT = 1.8;
    private static final AtomicInteger ENTITY_COUNTER;

    @Override
    public Key type() {
        return ElementTypes.HITBOX;
    }

    @Override
    public Vec3 localOffset() {
        return Vec3.ZERO;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        return new int[0];
    }

    @Override
    public void tick(RenderContext ctx) {
        if (ctx.level() == null) {
            return;
        }
        this.rebuild(ctx.level(), ctx.viewers(), ctx.bearing());
    }

    @Override
    public void render(RenderContext ctx) {
        this.render(ctx.viewers(), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale(), ctx.moved());
    }

    @Override
    public void despawn(List<Player> viewers) {
        this.despawnAll(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    public void setColliderExcludedViewer(UUID viewer) {
        this.shulkerColliders.setExcludedViewer(viewer);
    }

    public void rebuild(ContraptionLevel level, List<Player> viewers, Vec3 bearingWorldPos) {
        this.cellCache.keySet().retainAll(level.localPositions());
        Set<BlockPos> allOffsets = level.localPositions();
        Iterator<Map.Entry<BlockPos, Slot>> it = this.autoSlots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Slot> e = it.next();
            if (allOffsets.contains(e.getKey())) continue;
            it.remove();
        }
        for (BlockPos offset : allOffsets) {
            ShulkerParams standParams = this.cellFor((ContraptionLevel)level, (BlockPos)offset).params;
            double standBottomY = standParams != null ? standParams.yOffset : 0.0;
            double standTopY = standParams != null ? standParams.yOffset + (double)standParams.scale : 1.0;
            boolean hasCollision = standParams != null;
            Slot slot = this.autoSlots.computeIfAbsent(offset, o -> new Slot((double)o.getX() + 0.5, o.getY(), (double)o.getZ() + 0.5, 1.0f, 1.0f, standBottomY, standTopY));
            slot.standBottomY = standBottomY;
            slot.standTopY = standTopY;
            slot.hasCollision = hasCollision;
        }
        List<Vec3> viewerPositions = ContraptionHitboxElement.resolveViewerPositions(viewers);
        if (viewerPositions.isEmpty()) {
            this.shulkerColliders.prune(Collections.emptySet(), viewers);
            return;
        }
        double cullRadiusSq = 49.0;
        HashSet<Object> shulkerKeys = new HashSet<Object>();
        for (BlockPos offset : allOffsets) {
            if (!ContraptionHitboxElement.anyWithinSq(viewerPositions, level.realWorldPositionOf(offset), cullRadiusSq)) continue;
            CachedCell cell = this.cellFor(level, offset);
            if (cell.boxes == null) continue;
            shulkerKeys.add(offset);
            this.shulkerColliders.setCell(offset, cell.boxes, viewers);
        }
        this.shulkerColliders.prune(shulkerKeys, viewers);
    }

    private CachedCell cellFor(ContraptionLevel level, BlockPos offset) {
        BlockState state = level.getBlockState(offset);
        CachedCell cached = this.cellCache.get(offset);
        if (cached != null && cached.state == state) {
            return cached;
        }
        CachedCell fresh = ContraptionHitboxElement.computeCell(level, offset, state);
        this.cellCache.put(offset, fresh);
        return fresh;
    }

    private static List<Vec3> resolveViewerPositions(List<Player> viewers) {
        ArrayList<Vec3> positions = new ArrayList<Vec3>(viewers.size());
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player)) continue;
            org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player)pp;
            Location loc = bukkitPlayer.getLocation();
            positions.add(new Vec3(loc.getX(), loc.getY(), loc.getZ()));
        }
        return positions;
    }

    private static boolean anyWithinSq(List<Vec3> positions, Vec3 point, double radiusSq) {
        for (Vec3 pos : positions) {
            double dx = pos.x - point.x;
            double dy = pos.y - point.y;
            double dz = pos.z - point.z;
            if (!(dx * dx + dy * dy + dz * dz <= radiusSq)) continue;
            return true;
        }
        return false;
    }

    private static CachedCell computeCell(ContraptionLevel level, BlockPos offset, BlockState state) {
        if (state.isAir()) {
            return ContraptionHitboxElement.noCollision(state);
        }
        try {
            Block block = state.getBlock();
            if (block instanceof DoorBlock && state.hasProperty((Property)DoorBlock.OPEN) && ((Boolean)state.getValue((Property)DoorBlock.OPEN)).booleanValue()) {
                return ContraptionHitboxElement.noCollision(state);
            }
            if (block instanceof TrapDoorBlock && state.hasProperty((Property)TrapDoorBlock.OPEN) && ((Boolean)state.getValue((Property)TrapDoorBlock.OPEN)).booleanValue()) {
                return ContraptionHitboxElement.noCollision(state);
            }
            VoxelShape shape = state.getCollisionShape((BlockGetter)level.serverLevel(), offset);
            if (shape.isEmpty()) {
                return ContraptionHitboxElement.noCollision(state);
            }
            AABB bounds = shape.bounds();
            double height = bounds.maxY - bounds.minY;
            if (height <= 0.0 || height > 1.0) {
                return ContraptionHitboxElement.fullCube(state, offset);
            }
            ArrayList<AABB> boxes = new ArrayList<AABB>();
            for (AABB box : shape.optimize().toAabbs()) {
                boxes.add(box.move((double)offset.getX(), (double)offset.getY(), (double)offset.getZ()));
            }
            if (boxes.isEmpty()) {
                return ContraptionHitboxElement.noCollision(state);
            }
            return new CachedCell(state, new ShulkerParams((float)height, Direction.DOWN, 0, bounds.minY), List.copyOf(boxes));
        }
        catch (Throwable t) {
            return ContraptionHitboxElement.fullCube(state, offset);
        }
    }

    private static CachedCell noCollision(BlockState state) {
        return new CachedCell(state, null, null);
    }

    private static CachedCell fullCube(BlockState state, BlockPos offset) {
        return new CachedCell(state, FULL_CUBE, List.of(new AABB((double)offset.getX(), (double)offset.getY(), (double)offset.getZ(), (double)offset.getX() + 1.0, (double)offset.getY() + 1.0, (double)offset.getZ() + 1.0)));
    }

    public void addCustomSlot(double localX, double localY, double localZ, float width, float height) {
        this.customSlots.add(new Slot(localX, localY, localZ, width, height));
    }

    public void clearCustomSlots() {
        this.customSlots.clear();
    }

    public void addShulkerSlot(Object key, double localX, double localY, double localZ, float scale) {
        this.shulkerColliders.addSlot(key, localX, localY, localZ, scale);
    }

    public void pruneShulkerSlots(Set<Object> liveKeys, List<Player> viewers) {
        this.shulkerColliders.prune(liveKeys, viewers);
    }

    public int shulkerColliderCount() {
        return this.shulkerColliders.slotCount();
    }

    private List<Slot> allSlots() {
        if (this.customSlots.isEmpty()) {
            return new ArrayList<Slot>(this.autoSlots.values());
        }
        ArrayList<Slot> all = new ArrayList<Slot>(this.autoSlots.size() + this.customSlots.size());
        all.addAll(this.autoSlots.values());
        all.addAll(this.customSlots);
        return all;
    }

    private List<Slot> topSlotsOnly() {
        Set<BlockPos> topOffsets = ContraptionHitboxElement.topCellsOf(this.autoSlots.keySet());
        ArrayList<Slot> top = new ArrayList<Slot>(topOffsets.size() + this.customSlots.size());
        for (BlockPos offset : topOffsets) {
            Slot slot = this.autoSlots.get(offset);
            if (slot == null) continue;
            top.add(slot);
        }
        top.addAll(this.customSlots);
        return top;
    }

    public static Set<BlockPos> topCellsOf(Set<BlockPos> occupied) {
        HashSet<BlockPos> top = new HashSet<BlockPos>();
        for (BlockPos offset : occupied) {
            if (occupied.contains(offset.above())) continue;
            top.add(offset);
        }
        return top;
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, 0.0, 1.0, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, 0.0, scale, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double scale, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale, boolean moved) {
        this.shulkerColliders.render(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, moved);
    }

    public void despawnAll(List<Player> viewers) {
        this.autoSlots.clear();
        this.customSlots.clear();
        this.currentRiders.clear();
        this.stepState.clear();
        this.cellCache.clear();
        this.shulkerColliders.clear(viewers);
    }

    public Set<UUID> currentRiderIds() {
        return Collections.unmodifiableSet(this.currentRiders);
    }

    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        this.carryRiders(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, null, yaw, 0.0);
    }

    private static Vec3 riderCarryDelta(Vec3 riderPos, Vec3 bearingNew, double dx, double dy, double dz, double yawNew, double yawDelta) {
        if (yawDelta == 0.0) {
            return new Vec3(dx, dy, dz);
        }
        Vec3 local = ContraptionMath.realToLocal(riderPos, bearingNew, yawNew);
        Vec3 bearingOld = new Vec3(bearingNew.x - dx, bearingNew.y - dy, bearingNew.z - dz);
        Vec3 oldPoint = ContraptionMath.renderPosition(local, bearingOld, yawNew - yawDelta);
        return new Vec3(riderPos.x - oldPoint.x, riderPos.y - oldPoint.y, riderPos.z - oldPoint.z);
    }

    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, ContraptionLevel level, double yaw, double yawDelta) {
        List<Slot> slots = this.topSlotsOnly();
        if (slots.isEmpty()) {
            this.releaseAll();
            this.stepState.clear();
            return;
        }
        HashSet<UUID> ridingNow = new HashSet<UUID>();
        for (ServerPlayer sp : candidates) {
            if (!ContraptionHitboxElement.isStandingOnFootprint(sp, bearingWorldPos, slots, deltaY, yaw)) continue;
            UUID id = sp.getUUID();
            boolean wasRiding = this.currentRiders.contains(id);
            ridingNow.add(id);
            if (level != null) {
                this.maybePlayStepSound(sp, bearingWorldPos, level, wasRiding, yaw);
            }
            Vec3 riderPos = sp.position();
            Vec3 carryVec = ContraptionHitboxElement.riderCarryDelta(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, yawDelta);
            Vec3 clamped = this.clampDeltaForSideCollision(riderPos, bearingWorldPos, carryVec.x, carryVec.y, carryVec.z, yaw);
            PlayerCarry.carry(sp, clamped.x, clamped.y, clamped.z);
        }
        for (UUID left : this.currentRiders) {
            if (ridingNow.contains(left)) continue;
            PlayerCarry.release(left);
            this.stepState.remove(left);
        }
        this.currentRiders.clear();
        this.currentRiders.addAll(ridingNow);
    }

    private void maybePlayStepSound(ServerPlayer sp, Vec3 bearingWorldPos, ContraptionLevel level, boolean wasRiding, double yaw) {
        BlockPos cell = this.standingCellOf(sp.position(), bearingWorldPos, yaw);
        if (cell == null) {
            return;
        }
        BlockState state = level.getBlockState(cell);
        if (state.isAir()) {
            return;
        }
        SoundType soundType = state.getSoundType();
        Vec3 pos = sp.position();
        UUID id = sp.getUUID();
        if (!wasRiding) {
            SoundEvent fall;
            double downSpeed = -sp.getDeltaMovement().y;
            if (downSpeed >= 0.35 && (fall = soundType.getFallSound()) != null) {
                ContraptionHitboxElement.playAt(level, cell, fall, 0.5f, 1.0f);
            }
            this.stepState.put(id, new double[]{pos.x, pos.z});
            return;
        }
        double[] last = this.stepState.get(id);
        if (last == null) {
            this.stepState.put(id, new double[]{pos.x, pos.z});
            return;
        }
        double dx = pos.x - last[0];
        double dz = pos.z - last[1];
        if (dx * dx + dz * dz >= 4.0) {
            SoundEvent step = soundType.getStepSound();
            if (step != null) {
                ContraptionHitboxElement.playAt(level, cell, step, soundType.getVolume() * 0.15f, soundType.getPitch());
            }
            last[0] = pos.x;
            last[1] = pos.z;
        }
    }

    private static void playAt(ContraptionLevel level, BlockPos cell, SoundEvent sound, float volume, float pitch) {
        try {
            level.playSeededSound(null, (double)cell.getX() + 0.5, (double)cell.getY() + 1.0, (double)cell.getZ() + 0.5, (Holder<SoundEvent>)Holder.direct(sound), SoundSource.BLOCKS, volume, pitch, 0L);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private BlockPos standingCellOf(Vec3 pos, Vec3 bearingWorldPos, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        Set<BlockPos> topOffsets = ContraptionHitboxElement.topCellsOf(this.autoSlots.keySet());
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (BlockPos offset : topOffsets) {
            double ddz;
            double ddx;
            double distSq;
            Slot slot = this.autoSlots.get(offset);
            if (slot == null || !slot.hasCollision) continue;
            double half = (double)slot.width / 2.0;
            double cx = slot.lx;
            double cz = slot.lz;
            double topY = slot.ly + slot.standTopY;
            if (!(local.x >= cx - half - 0.3) || !(local.x <= cx + half + 0.3) || !(local.z >= cz - half - 0.3) || !(local.z <= cz + half + 0.3) || !(local.y >= topY - 0.5) || !(local.y <= topY + 1.0) || !((distSq = (ddx = local.x - cx) * ddx + (ddz = local.z - cz) * ddz) < bestDistSq)) continue;
            bestDistSq = distSq;
            best = offset;
        }
        return best;
    }

    private Vec3 clampDeltaForSideCollision(Vec3 riderPos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        return ContraptionHitboxElement.clampDeltaForSideCollisionGeneric(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, this.allSlots(), 0.3, 1.8, yaw);
    }

    private static Vec3 clampDeltaForSideCollisionGeneric(Vec3 pos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, List<Slot> solids, double halfWidth, double height, double yaw) {
        double[][] candidates;
        if (deltaX == 0.0 && deltaZ == 0.0) {
            return new Vec3(deltaX, deltaY, deltaZ);
        }
        if (solids.isEmpty()) {
            return new Vec3(deltaX, deltaY, deltaZ);
        }
        for (double[] candidate : candidates = new double[][]{{deltaX, deltaZ}, {0.0, deltaZ}, {deltaX, 0.0}, {0.0, 0.0}}) {
            double cx = candidate[0];
            double cz = candidate[1];
            Vec3 targetPos = new Vec3(pos.x + cx, pos.y + deltaY, pos.z + cz);
            if (ContraptionHitboxElement.overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw)) continue;
            return new Vec3(cx, deltaY, cz);
        }
        return new Vec3(0.0, deltaY, 0.0);
    }

    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        return ContraptionHitboxElement.overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0);
    }

    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw, double pitch, double roll, double scale) {
        Vec3 local = ContraptionMath.realToLocal(targetPos, bearingWorldPos, yaw, pitch, roll, scale <= 0.0 ? 1.0 : scale);
        double rMinX = local.x - halfWidth;
        double rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth;
        double rMaxZ = local.z + halfWidth;
        double rMinY = local.y + 0.35;
        double rMaxY = local.y + height;
        for (Slot slot : solids) {
            if (!slot.hasCollision) continue;
            double half = (double)slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean overlaps = rMaxX > minX && rMinX < maxX && rMaxZ > minZ && rMinZ < maxZ && rMaxY > minY && rMinY < maxY;
            if (!overlaps) continue;
            return true;
        }
        return false;
    }

    private void releaseAll() {
        for (UUID id : this.currentRiders) {
            PlayerCarry.release(id);
        }
        this.currentRiders.clear();
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return ContraptionHitboxElement.isStandingOnFootprint(sp.position(), bearingWorldPos, slots, 0.0, yaw);
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        return ContraptionHitboxElement.isStandingOnFootprint(sp.position(), bearingWorldPos, slots, deltaY, yaw);
    }

    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return ContraptionHitboxElement.isStandingOnFootprint(pos, bearingWorldPos, slots, 0.0, yaw);
    }

    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double verticalSlack = Math.min(Math.abs(deltaY), 1.0);
        for (Slot slot : slots) {
            if (!slot.hasCollision) continue;
            double minX = slot.lx - (double)slot.width / 2.0;
            double maxX = slot.lx + (double)slot.width / 2.0;
            double minZ = slot.lz - (double)slot.width / 2.0;
            double maxZ = slot.lz + (double)slot.width / 2.0;
            double topY = slot.ly + slot.standTopY;
            double localFeetY = local.y;
            if (!(local.x >= minX - 0.3) || !(local.x <= maxX + 0.3) || !(local.z >= minZ - 0.3) || !(local.z <= maxZ + 0.3) || !(localFeetY >= topY - 0.3 - verticalSlack) || !(localFeetY <= topY + 0.9 + verticalSlack)) continue;
            return true;
        }
        return false;
    }

    public void carryEntities(List<Entity> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = this.topSlotsOnly();
        if (slots.isEmpty()) {
            return;
        }
        List<Slot> solids = this.allSlots();
        for (Entity entity : candidates) {
            if (entity == null || entity.isRemoved() || !ContraptionHitboxElement.isStandingOnFootprint(entity.position(), bearingWorldPos, slots, yaw)) continue;
            double halfWidth = (double)entity.getBbWidth() / 2.0;
            double height = entity.getBbHeight();
            Vec3 pos = entity.position();
            Vec3 clamped = ContraptionHitboxElement.clampDeltaForSideCollisionGeneric(pos, bearingWorldPos, deltaX, deltaY, deltaZ, solids, halfWidth, height, yaw);
            entity.setPos(entity.getX() + clamped.x, entity.getY() + clamped.y, entity.getZ() + clamped.z);
            entity.setOldPosAndRot();
        }
    }

    public void carryNearbyEntities(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, UUID anchorEntityId) {
        List<Entity> nearby;
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = this.topSlotsOnly();
        if (slots.isEmpty() || !(realLevel instanceof ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x;
        double maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y;
        double maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z;
        double maxZ = bearingWorldPos.z;
        for (Slot slot : slots) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - (double)slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + (double)slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + (double)slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - (double)slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + (double)slot.width / 2.0);
        }
        double rotationMargin = (maxX - minX + (maxZ - minZ)) / 2.0;
        AABB bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.0 + rotationMargin);
        try {
            nearby = realLevel.getEntities((Entity)null, bounds, e -> true);
        }
        catch (Throwable t) {
            return;
        }
        ArrayList<Entity> candidates = new ArrayList<Entity>();
        for (Entity entity : nearby) {
            if (entity instanceof ServerPlayer || entity instanceof ItemEntity && ContraptionItemPickupSwarm.isMirror(entity.getUUID()) || entity.getUUID().equals(anchorEntityId)) continue;
            candidates.add(entity);
        }
        this.carryEntities(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, yaw);
    }

    public void pushBackNearbyBystanders(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        this.pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT);
    }

    public void pushBackNearbyBystanders(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings) {
        this.pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings);
    }

    public void pushBackNearbyBystanders(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale, ContraptionPushSettings settings) {
        List<ServerPlayer> nearby;
        List<Slot> solids;
        if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < 4.0E-4) {
            return;
        }
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        if ((solids = this.allSlots()).isEmpty() || !(realLevel instanceof ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x;
        double maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y;
        double maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z;
        double maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - (double)slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + (double)slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + (double)slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - (double)slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + (double)slot.width / 2.0);
        }
        double rotationMargin = (maxX - minX + (maxZ - minZ)) / 2.0;
        AABB bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(2.0 + rotationMargin);
        try {
            nearby = realLevel.getEntitiesOfClass(ServerPlayer.class, bounds, p -> true);
        }
        catch (Throwable t) {
            return;
        }
        List<Slot> topSlots = this.topSlotsOnly();
        for (ServerPlayer sp : nearby) {
            if (this.currentRiders.contains(sp.getUUID()) || !topSlots.isEmpty() && ContraptionHitboxElement.isStandingOnFootprint(sp, bearingWorldPos, topSlots, yaw)) continue;
            Vec3 playerPos = sp.position();
            Vec3 push = ContraptionHitboxElement.computeSolidPush(playerPos, bearingWorldPos, solids, 0.3, 1.8, yaw, pitch, roll, scale, deltaX, deltaY, deltaZ);
            push = ContraptionHitboxElement.applyPushSettings(push, playerPos, bearingWorldPos, solids, 0.3, 1.8, yaw, settings, false);
            if (push.x == 0.0 && push.z == 0.0) continue;
            PlayerCarry.carry(sp, push.x, 0.0, push.z);
        }
    }

    public void pushBackNearbyEntities(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        this.pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT, null);
    }

    public void pushBackNearbyEntities(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings, UUID anchorEntityId) {
        this.pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings, anchorEntityId);
    }

    public void pushBackNearbyEntities(Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale, ContraptionPushSettings settings, UUID anchorEntityId) {
        List<Entity>   nearby;
        List<Slot> solids;
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        if ((solids = this.allSlots()).isEmpty() || !(realLevel instanceof ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x;
        double maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y;
        double maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z;
        double maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - (double)slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + (double)slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + (double)slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - (double)slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + (double)slot.width / 2.0);
        }
        double rotationMargin = (maxX - minX + (maxZ - minZ)) / 2.0;
        AABB bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(2.0 + rotationMargin);
        try {
            nearby = realLevel.getEntities((Entity)null, bounds, e -> true);
        }
        catch (Throwable t) {
            return;
        }
        List<Slot> topSlots = this.topSlotsOnly();
        for (Entity entity : nearby) {
            if (entity == null || entity.isRemoved() || entity instanceof ServerPlayer || entity instanceof ItemEntity && ContraptionItemPickupSwarm.isMirror(entity.getUUID()) || entity.getUUID().equals(anchorEntityId) || !topSlots.isEmpty() && ContraptionHitboxElement.isStandingOnFootprint(entity.position(), bearingWorldPos, topSlots, yaw)) continue;
            double halfWidth = (double)entity.getBbWidth() / 2.0;
            double height = entity.getBbHeight();
            Vec3 push = ContraptionHitboxElement.computeSolidPush(entity.position(), bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale, deltaX, deltaY, deltaZ);
            if ((push = ContraptionHitboxElement.applyPushSettings(push, entity.position(), bearingWorldPos, solids, halfWidth, height, yaw, settings, true)).equals(Vec3.ZERO)) continue;
            entity.setPos(entity.getX() + push.x, entity.getY() + push.y, entity.getZ() + push.z);
            entity.setOldPosAndRot();
            Vec3 vel = entity.getDeltaMovement();
            double nvx = ContraptionHitboxElement.knockbackComponent(vel.x, push.x);
            double nvz = ContraptionHitboxElement.knockbackComponent(vel.z, push.z);
            if (nvx == vel.x && nvz == vel.z) continue;
            entity.setDeltaMovement(nvx, vel.y, nvz);
        }
    }

    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw, double dx, double dy, double dz) {
        return ContraptionHitboxElement.computeSolidPush(pos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0, dx, dy, dz);
    }

    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw, double pitch, double roll, double scale, double dx, double dy, double dz) {
        Vec3 target = new Vec3(pos.x + dx, pos.y + dy, pos.z + dz);
        boolean now = ContraptionHitboxElement.overlapsAnySolid(pos, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        boolean next = ContraptionHitboxElement.overlapsAnySolid(target, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        if (now || next) {
            Vec3 push = ContraptionHitboxElement.resolvePushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw);
            if (!push.equals(Vec3.ZERO)) {
                return push;
            }
            return new Vec3(dx, 0.0, dz);
        }
        return ContraptionHitboxElement.resolveSweptPushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw, dx, dy, dz);
    }

    private static double knockbackComponent(double vel, double push) {
        if (push == 0.0) {
            return vel;
        }
        double dir = Math.signum(push);
        double sameWay = dir == Math.signum(vel) ? Math.abs(vel) : 0.0;
        return dir * Math.max(Math.abs(push), sameWay);
    }

    private static Vec3 applyPushSettings(Vec3 push, Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw, ContraptionPushSettings settings, boolean allowCarryUp) {
        double lip;
        boolean carryUp;
        if (push.equals(Vec3.ZERO)) {
            return push;
        }
        double px = push.x * settings.pushStrength;
        double pz = push.z * settings.pushStrength;
        double py = push.y;
        boolean bl = carryUp = allowCarryUp && settings.carryEntities;
        if ((settings.pushUpEnabled || carryUp) && (px != 0.0 || pz != 0.0) && (lip = ContraptionHitboxElement.lipHeightAboveFeet(pos, bearingWorldPos, solids, halfWidth, yaw)) > 0.0 && lip <= settings.maxStepUpHeight) {
            py += lip * settings.pushUpStrength;
            if (carryUp) {
                px = 0.0;
                pz = 0.0;
            }
        }
        return new Vec3(px, py, pz);
    }

    private static double lipHeightAboveFeet(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double yaw) {
        double feetY;
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth;
        double rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth;
        double rMaxZ = local.z + halfWidth;
        double bestTop = feetY = local.y;
        for (Slot slot : solids) {
            double top;
            if (!slot.hasCollision) continue;
            double half = (double)slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            if (!(rMaxX > minX) || !(rMinX < maxX) || !(rMaxZ > minZ) || !(rMinZ < maxZ) || !((top = slot.ly + slot.standTopY) > bestTop)) continue;
            bestTop = top;
        }
        return bestTop - feetY;
    }

    private static Vec3 resolveSweptPushOut(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw, double dx, double dy, double dz) {
        if (dx == 0.0 && dz == 0.0) {
            return Vec3.ZERO;
        }
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        Vec3 ld = ContraptionMath.rotateYaw(new Vec3(dx, dy, dz), -yaw);
        double rMinX = local.x - halfWidth;
        double rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth;
        double rMaxZ = local.z + halfWidth;
        double rMinY = local.y + 0.35;
        double rMaxY = local.y + height;
        double bestPushX = 0.0;
        double bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            double lead;
            boolean swept;
            if (!slot.hasCollision) continue;
            double half = (double)slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            double sMinX = minX - Math.max(0.0, ld.x);
            double sMaxX = maxX - Math.min(0.0, ld.x);
            double sMinZ = minZ - Math.max(0.0, ld.z);
            double sMaxZ = maxZ - Math.min(0.0, ld.z);
            double sMinY = minY - Math.max(0.0, ld.y);
            double sMaxY = maxY - Math.min(0.0, ld.y);
            boolean bl = swept = rMaxX > sMinX && rMinX < sMaxX && rMaxZ > sMinZ && rMinZ < sMaxZ && rMaxY > sMinY && rMinY < sMaxY;
            if (!swept) continue;
            any = true;
            if (Math.abs(ld.x) >= Math.abs(ld.z) && ld.x != 0.0) {
                double d = ld.x > 0.0 ? maxX + halfWidth - local.x : minX - halfWidth - local.x;
                lead = d;
                if (!(Math.abs(lead) > Math.abs(bestPushX))) continue;
                bestPushX = lead;
                continue;
            }
            if (ld.z == 0.0) continue;
            double d = ld.z > 0.0 ? maxZ + halfWidth - local.z : minZ - halfWidth - local.z;
            lead = d;
            if (!(Math.abs(lead) > Math.abs(bestPushZ))) continue;
            bestPushZ = lead;
        }
        if (!any) {
            return Vec3.ZERO;
        }
        return ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    private static Vec3 resolvePushOut(Vec3 playerPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(playerPos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth;
        double rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth;
        double rMaxZ = local.z + halfWidth;
        double rMinY = local.y;
        double rMaxY = local.y + height;
        double bestPushX = 0.0;
        double bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            double signZ;
            boolean overlaps;
            if (!slot.hasCollision) continue;
            double half = (double)slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean bl = overlaps = rMaxX > minX && rMinX < maxX && rMaxZ > minZ && rMinZ < maxZ && rMaxY > minY && rMinY < maxY;
            if (!overlaps) continue;
            any = true;
            double penX = Math.min(rMaxX - minX, maxX - rMinX);
            double penZ = Math.min(rMaxZ - minZ, maxZ - rMinZ);
            double signX = local.x >= (minX + maxX) / 2.0 ? 1.0 : -1.0;
            double d = signZ = local.z >= (minZ + maxZ) / 2.0 ? 1.0 : -1.0;
            if (penX <= penZ) {
                if (!(Math.abs(penX * signX) > Math.abs(bestPushX))) continue;
                bestPushX = penX * signX;
                continue;
            }
            if (!(Math.abs(penZ * signZ) > Math.abs(bestPushZ))) continue;
            bestPushZ = penZ * signZ;
        }
        if (!any) {
            return Vec3.ZERO;
        }
        return ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    public int cellCount() {
        return this.autoSlots.size() + this.customSlots.size();
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger counter;
        FULL_CUBE = new ShulkerParams(1.0f, Direction.DOWN, 0);
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger)f.get(null);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static final class CachedCell {
        final BlockState state;
        final ShulkerParams params;
        final List<AABB> boxes;

        CachedCell(BlockState state, ShulkerParams params, List<AABB> boxes) {
            this.state = state;
            this.params = params;
            this.boxes = boxes;
        }
    }

    private static final class ShulkerParams {
        final float scale;
        final Direction attachFace;
        final int peek;
        final double yOffset;

        ShulkerParams(float scale, Direction attachFace, int peek) {
            this(scale, attachFace, peek, 0.0);
        }

        ShulkerParams(float scale, Direction attachFace, int peek, double yOffset) {
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.yOffset = yOffset;
        }
    }

    static final class Slot {
        final double lx;
        final double ly;
        final double lz;
        final float width;
        final float height;
        double standBottomY;
        double standTopY;
        boolean hasCollision = true;

        Slot(double lx, double ly, double lz, float width, float height) {
            this(lx, ly, lz, width, height, 0.0, height);
        }

        Slot(double lx, double ly, double lz, float width, float height, double standBottomY, double standTopY) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.width = width;
            this.height = height;
            this.standBottomY = standBottomY;
            this.standTopY = standTopY;
        }
    }
}

