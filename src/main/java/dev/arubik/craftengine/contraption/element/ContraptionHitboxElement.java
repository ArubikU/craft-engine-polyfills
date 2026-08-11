package dev.arubik.craftengine.contraption.element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import dev.arubik.craftengine.contraption.ContraptionPushSettings;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.player.PlayerCarry;
import dev.arubik.craftengine.contraption.render.ContraptionItemPickupSwarm;
import dev.arubik.craftengine.contraption.render.ContraptionShulkerColliderSwarm;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

/** Packet-only hitbox + carry swarm for a moving contraption. */
public final class ContraptionHitboxElement implements ContraptionElement {

    // ---- ContraptionElement interface ----

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
        if (ctx.level() == null) return;
        rebuild(ctx.level(), ctx.viewers(), ctx.bearing());
    }

    @Override
    public void render(RenderContext ctx) {
        render(ctx.viewers(), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(),
                ctx.rollRadians(), ctx.scale(), ctx.moved());
    }

    @Override
    public void despawn(List<Player> viewers) {
        despawnAll(viewers);
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        // Hitboxes are ephemeral packet entities — nothing to place in the world
    }

    // ---- original hitbox swarm fields ----

    private final Map<BlockPos, Slot> autoSlots = new HashMap<>();
    private final List<Slot> customSlots = new ArrayList<>();
    private final Set<UUID> currentRiders = new HashSet<>();

    // Per-player last-step XZ position; cleared when player leaves footprint.
    private final Map<UUID, double[]> stepState = new HashMap<>();

    // ~2 blocks matches vanilla's walking cadence (moveDist-based, ~1.6-2 blocks/step)
    private static final double STEP_DISTANCE = 2.0;

    // Below this downward speed at landing, treat as step-down not fall
    private static final double FALL_MIN_SPEED = 0.35;

    private final ContraptionShulkerColliderSwarm shulkerColliders = new ContraptionShulkerColliderSwarm();

    // INTERACTION slots have no canBeCollidedWith override; only shulkers need viewer exclusion
    public void setColliderExcludedViewer(java.util.UUID viewer) {
        shulkerColliders.setExcludedViewer(viewer);
    }

    public void rebuild(ContraptionLevel level, List<Player> viewers, Vec3 bearingWorldPos) {
        // Drop stale cache entries before any cell reads them
        cellCache.keySet().retainAll(level.localPositions());
        // Every cell gets click/collision hitboxes; topCellsOf only for carry footprint
        Set<BlockPos> allOffsets = level.localPositions();
        java.util.Iterator<Map.Entry<BlockPos, Slot>> it = autoSlots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Slot> e = it.next();
            if (!allOffsets.contains(e.getKey())) {
                it.remove();
            }
        }
        for (BlockPos offset : allOffsets) {
            // +0.5 centres the INTERACTION box on the block footprint (offset is the block corner)
            ShulkerParams standParams = cellFor(level, offset).params;
            double standBottomY = standParams != null ? standParams.yOffset : 0.0;
            double standTopY = standParams != null ? standParams.yOffset + standParams.scale : 1.0;
            // null params means no real collision (torch, plant, etc.)
            boolean hasCollision = standParams != null;
            Slot slot = autoSlots.computeIfAbsent(offset,
                    o -> new Slot(o.getX() + 0.5, o.getY(), o.getZ() + 0.5, 1f, 1f, standBottomY, standTopY));
            // Refresh shape-derived window even on reused slots — block type can change in place
            slot.standBottomY = standBottomY;
            slot.standTopY = standTopY;
            slot.hasCollision = hasCollision;
        }

        // One shulker per cell, LOD-gated per cell (not a single centre-radius) — viewer positions resolved once
        List<Vec3> viewerPositions = resolveViewerPositions(viewers);
        if (viewerPositions.isEmpty()) {
            shulkerColliders.prune(java.util.Collections.emptySet(), viewers);
            return;
        }
        double cullRadiusSq = LOD_CULL_RADIUS * LOD_CULL_RADIUS;
        Set<Object> shulkerKeys = new HashSet<>();
        for (BlockPos offset : allOffsets) {
            if (!anyWithinSq(viewerPositions, level.realWorldPositionOf(offset), cullRadiusSq)) {
                continue; // out of every viewer's LOD range — no collider needed; prune removes any stale one
            }
            CachedCell cell = cellFor(level, offset);
            if (cell.boxes == null) {
                // No real collision (torch, plant, etc.) — also prune any stale slot on this offset
                continue;
            }
            shulkerKeys.add(offset);
            shulkerColliders.setCell(offset, cell.boxes, viewers);
        }
        shulkerColliders.prune(shulkerKeys, viewers);
    }

    private static final class CachedCell {
        final net.minecraft.world.level.block.state.BlockState state;
        final ShulkerParams params;
        final List<net.minecraft.world.phys.AABB> boxes;

        CachedCell(net.minecraft.world.level.block.state.BlockState state, ShulkerParams params,
                List<net.minecraft.world.phys.AABB> boxes) {
            this.state = state;
            this.params = params;
            this.boxes = boxes;
        }
    }

    // Geometry cache; BlockState identity comparison is exact (vanilla interns every state instance)
    private final Map<BlockPos, CachedCell> cellCache = new HashMap<>();

    private CachedCell cellFor(ContraptionLevel level, BlockPos offset) {
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(offset);
        CachedCell cached = cellCache.get(offset);
        if (cached != null && cached.state == state) {
            return cached;
        }
        CachedCell fresh = computeCell(level, offset, state);
        cellCache.put(offset, fresh);
        return fresh;
    }

    // Slightly beyond FAR_EXIT so the swarm gets cells before they're collision-range; avoids pop-in
    private static final double LOD_CULL_RADIUS = ContraptionShulkerColliderSwarm.FAR_EXIT + 2.0;

    private static List<Vec3> resolveViewerPositions(List<Player> viewers) {
        List<Vec3> positions = new ArrayList<>(viewers.size());
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (pp instanceof org.bukkit.entity.Player bukkitPlayer) {
                org.bukkit.Location loc = bukkitPlayer.getLocation();
                positions.add(new Vec3(loc.getX(), loc.getY(), loc.getZ()));
            }
        }
        return positions;
    }

    private static boolean anyWithinSq(List<Vec3> positions, Vec3 point, double radiusSq) {
        for (Vec3 pos : positions) {
            double dx = pos.x - point.x;
            double dy = pos.y - point.y;
            double dz = pos.z - point.z;
            if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                return true;
            }
        }
        return false;
    }

    private static final class ShulkerParams {
        final float scale;
        final net.minecraft.core.Direction attachFace;
        final int peek;
        // Extra Y offset so the shulker box sits at the shape's real minY (e.g. 0.5 for upper slab)
        final double yOffset;

        ShulkerParams(float scale, net.minecraft.core.Direction attachFace, int peek) {
            this(scale, attachFace, peek, 0.0);
        }

        ShulkerParams(float scale, net.minecraft.core.Direction attachFace, int peek, double yOffset) {
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.yOffset = yOffset;
        }
    }

    /** {@code scale=1, attachFace=DOWN, peek=0, yOffset=0} — the previous always-full-cube fallback. */
    private static final ShulkerParams FULL_CUBE = new ShulkerParams(1f, net.minecraft.core.Direction.DOWN, 0);

    private static CachedCell computeCell(ContraptionLevel level, BlockPos offset,
            net.minecraft.world.level.block.state.BlockState state) {
        if (state.isAir()) {
            return noCollision(state);
        }
        try {
            // Open door/trapdoor: shape.isEmpty() doesn't catch it (open door still has a thin slab volume);
            // must check the OPEN property directly to treat open doors as passable like torches

            net.minecraft.world.level.block.Block block = state.getBlock();
            if (block instanceof net.minecraft.world.level.block.DoorBlock
                    && state.hasProperty(net.minecraft.world.level.block.DoorBlock.OPEN)
                    && state.getValue(net.minecraft.world.level.block.DoorBlock.OPEN)) {
                return noCollision(state);
            }
            if (block instanceof net.minecraft.world.level.block.TrapDoorBlock
                    && state.hasProperty(net.minecraft.world.level.block.TrapDoorBlock.OPEN)
                    && state.getValue(net.minecraft.world.level.block.TrapDoorBlock.OPEN)) {
                return noCollision(state);
            }
            net.minecraft.world.phys.shapes.VoxelShape shape = state.getCollisionShape(level.serverLevel(), offset);
            if (shape.isEmpty()) {
                // Genuinely passable (torch, rail, button, etc.) — no collider
                return noCollision(state);
            }
            net.minecraft.world.phys.AABB bounds = shape.bounds();
            double height = bounds.maxY - bounds.minY;
            if (height <= 0.0 || height > 1.0) {
                return fullCube(state, offset); // degenerate/oversized — safe fallback
            }
            // optimize() merges voxels before toAabbs(); translate to bearing-local frame
            List<net.minecraft.world.phys.AABB> boxes = new ArrayList<>();
            for (net.minecraft.world.phys.AABB box : shape.optimize().toAabbs()) {
                boxes.add(box.move(offset.getX(), offset.getY(), offset.getZ()));
            }
            if (boxes.isEmpty()) {
                return noCollision(state);
            }
            return new CachedCell(state,
                    new ShulkerParams((float) height, net.minecraft.core.Direction.DOWN, 0, bounds.minY),
                    List.copyOf(boxes));
        } catch (Throwable t) {
            return fullCube(state, offset); // best-effort — a shape-query failure shouldn't block hitbox population
        }
    }

    /** A cell that is genuinely passable — no stand window, no collider (see {@link #computeCell}). */
    private static CachedCell noCollision(net.minecraft.world.level.block.state.BlockState state) {
        return new CachedCell(state, null, null);
    }

    /** The safe fallback cell: a solid full-cell cube, matching {@link #FULL_CUBE}'s stand window. */
    private static CachedCell fullCube(net.minecraft.world.level.block.state.BlockState state, BlockPos offset) {
        return new CachedCell(state, FULL_CUBE, List.of(new net.minecraft.world.phys.AABB(
                offset.getX(), offset.getY(), offset.getZ(),
                offset.getX() + 1.0, offset.getY() + 1.0, offset.getZ() + 1.0)));
    }

    public void addCustomSlot(double localX, double localY, double localZ, float width, float height) {
        customSlots.add(new Slot(localX, localY, localZ, width, height));
    }

    public void clearCustomSlots() {
        customSlots.clear();
    }

    public void addShulkerSlot(Object key, double localX, double localY, double localZ, float scale) {
        shulkerColliders.addSlot(key, localX, localY, localZ, scale);
    }

    /** Drops any shulker-collider slot not present in {@code liveKeys} (see {@link #addShulkerSlot}). */
    public void pruneShulkerSlots(Set<Object> liveKeys, List<Player> viewers) {
        shulkerColliders.prune(liveKeys, viewers);
    }

    public int shulkerColliderCount() {
        return shulkerColliders.slotCount();
    }

    private List<Slot> allSlots() {
        if (customSlots.isEmpty()) {
            return new ArrayList<>(autoSlots.values());
        }
        List<Slot> all = new ArrayList<>(autoSlots.size() + customSlots.size());
        all.addAll(autoSlots.values());
        all.addAll(customSlots);
        return all;
    }

    // Exposed-top cells only for carry — see carryRiders for why narrower than allSlots
    private List<Slot> topSlotsOnly() {
        Set<BlockPos> topOffsets = topCellsOf(autoSlots.keySet());
        List<Slot> top = new ArrayList<>(topOffsets.size() + customSlots.size());
        for (BlockPos offset : topOffsets) {
            Slot slot = autoSlots.get(offset);
            if (slot != null) {
                top.add(slot);
            }
        }
        top.addAll(customSlots);
        return top;
    }

    static Set<BlockPos> topCellsOf(Set<BlockPos> occupied) {
        Set<BlockPos> top = new HashSet<>();
        for (BlockPos offset : occupied) {
            if (!occupied.contains(offset.above())) {
                top.add(offset);
            }
        }
        return top;
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 1.0, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, scale, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale, boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, moved);
    }

    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, boolean moved) {
        // INTERACTION entities removed — each element manages its own via interactionBounds()
        shulkerColliders.render(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, moved);
    }

    public void despawnAll(List<Player> viewers) {
        // Slots are geometry-only — no packet entities to despawn
        autoSlots.clear();
        customSlots.clear();
        currentRiders.clear();
        stepState.clear();
        cellCache.clear();
        shulkerColliders.clear(viewers);
    }

    public Set<UUID> currentRiderIds() {
        return java.util.Collections.unmodifiableSet(currentRiders);
    }

    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        carryRiders(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, null, yaw, 0.0);
    }

    // Rotation-aware carry delta: arc + translation; fast-paths to (dx,dy,dz) when yawDelta==0
    private static Vec3 riderCarryDelta(Vec3 riderPos, Vec3 bearingNew, double dx, double dy, double dz,
            double yawNew, double yawDelta) {
        if (yawDelta == 0.0) {
            return new Vec3(dx, dy, dz);
        }
        Vec3 local = ContraptionMath.realToLocal(riderPos, bearingNew, yawNew);
        Vec3 bearingOld = new Vec3(bearingNew.x - dx, bearingNew.y - dy, bearingNew.z - dz);
        Vec3 oldPoint = ContraptionMath.renderPosition(local, bearingOld, yawNew - yawDelta);
        return new Vec3(riderPos.x - oldPoint.x, riderPos.y - oldPoint.y, riderPos.z - oldPoint.z);
    }

    public void carryRiders(List<ServerPlayer> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ,
            ContraptionLevel level, double yaw, double yawDelta) {
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty()) {
            releaseAll();
            stepState.clear();
            return;
        }
        Set<UUID> ridingNow = new HashSet<>();
        for (ServerPlayer sp : candidates) {
            if (isStandingOnFootprint(sp, bearingWorldPos, slots, deltaY, yaw)) {
                UUID id = sp.getUUID();
                boolean wasRiding = currentRiders.contains(id);
                ridingNow.add(id);
                if (level != null) {
                    maybePlayStepSound(sp, bearingWorldPos, level, wasRiding, yaw);
                }
                Vec3 riderPos = sp.position();
                Vec3 carryVec = riderCarryDelta(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, yawDelta);
                Vec3 clamped = clampDeltaForSideCollision(riderPos, bearingWorldPos, carryVec.x, carryVec.y, carryVec.z, yaw);
                PlayerCarry.carry(sp, clamped.x, clamped.y, clamped.z);
            }
        }
        for (UUID left : currentRiders) {
            if (!ridingNow.contains(left)) {
                PlayerCarry.release(left);
                stepState.remove(left); // left the footprint — drop stale step-timing state
            }
        }
        currentRiders.clear();
        currentRiders.addAll(ridingNow);
    }

    private void maybePlayStepSound(ServerPlayer sp, Vec3 bearingWorldPos, ContraptionLevel level, boolean wasRiding, double yaw) {
        BlockPos cell = standingCellOf(sp.position(), bearingWorldPos, yaw);
        if (cell == null) {
            return;
        }
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(cell);
        if (state.isAir()) {
            return;
        }
        net.minecraft.world.level.block.SoundType soundType = state.getSoundType();
        Vec3 pos = sp.position();
        UUID id = sp.getUUID();

        if (!wasRiding) {
            double downSpeed = -sp.getDeltaMovement().y;
            if (downSpeed >= FALL_MIN_SPEED) {
                net.minecraft.sounds.SoundEvent fall = soundType.getFallSound();
                if (fall != null) {
                    playAt(level, cell, fall, 0.5F, 1.0F);
                }
            }
            stepState.put(id, new double[] {pos.x, pos.z});
            return;
        }

        double[] last = stepState.get(id);
        if (last == null) {
            stepState.put(id, new double[] {pos.x, pos.z});
            return;
        }
        double dx = pos.x - last[0];
        double dz = pos.z - last[1];
        if (dx * dx + dz * dz >= STEP_DISTANCE * STEP_DISTANCE) {
            net.minecraft.sounds.SoundEvent step = soundType.getStepSound();
            if (step != null) {
                playAt(level, cell, step, soundType.getVolume() * 0.15F, soundType.getPitch());
            }
            last[0] = pos.x;
            last[1] = pos.z;
        }
    }

    /** Plays {@code sound} at the given local cell centre through {@link ContraptionLevel}'s real-world-redirecting override. */
    private static void playAt(ContraptionLevel level, BlockPos cell, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        try {
            level.playSeededSound(null, cell.getX() + 0.5, cell.getY() + 1.0, cell.getZ() + 0.5,
                    net.minecraft.core.Holder.direct(sound), net.minecraft.sounds.SoundSource.BLOCKS, volume, pitch, 0L);
        } catch (Throwable ignored) {
            // best-effort — a missing/odd sound should never break the carry loop
        }
    }

    private BlockPos standingCellOf(Vec3 pos, Vec3 bearingWorldPos, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        Set<BlockPos> topOffsets = topCellsOf(autoSlots.keySet());
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (BlockPos offset : topOffsets) {
            Slot slot = autoSlots.get(offset);
            if (slot == null || !slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double cx = slot.lx;
            double cz = slot.lz;
            double topY = slot.ly + slot.standTopY;
            if (local.x >= cx - half - 0.3 && local.x <= cx + half + 0.3
                    && local.z >= cz - half - 0.3 && local.z <= cz + half + 0.3
                    && local.y >= topY - 0.5 && local.y <= topY + 1.0) {
                double ddx = local.x - cx, ddz = local.z - cz;
                double distSq = ddx * ddx + ddz * ddz;
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    best = offset;
                }
            }
        }
        return best;
    }

    // Just past the 0.3 below-top standing tolerance so "resting on top" != "embedded in"
    private static final double FLOOR_CLEARANCE = 0.35;
    // Below this delta (blocks/tick)^2 the contraption is static — skip bystander shove
    private static final double STATIC_DELTA_SQ = 0.02 * 0.02;
    private static final double RIDER_HALF_WIDTH = 0.3;
    private static final double RIDER_HEIGHT = 1.8;

    private Vec3 clampDeltaForSideCollision(Vec3 riderPos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        return clampDeltaForSideCollisionGeneric(riderPos, bearingWorldPos, deltaX, deltaY, deltaZ, allSlots(), RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw);
    }

    private static Vec3 clampDeltaForSideCollisionGeneric(Vec3 pos, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ,
            List<Slot> solids, double halfWidth, double height, double yaw) {
        if (deltaX == 0.0 && deltaZ == 0.0) {
            return new Vec3(deltaX, deltaY, deltaZ); // vertical-only motion never causes a SIDE collision
        }
        if (solids.isEmpty()) {
            return new Vec3(deltaX, deltaY, deltaZ);
        }
        // Candidates in priority order: full delta, then X-only-blocked (keep Z), then
        // Z-only-blocked (keep X), then fully blocked (X/Z both zeroed, still carried vertically).
        double[][] candidates = {
                {deltaX, deltaZ},
                {0.0, deltaZ},
                {deltaX, 0.0},
                {0.0, 0.0},
        };
        for (double[] candidate : candidates) {
            double cx = candidate[0], cz = candidate[1];
            Vec3 targetPos = new Vec3(pos.x + cx, pos.y + deltaY, pos.z + cz);
            if (!overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw)) {
                return new Vec3(cx, deltaY, cz);
            }
        }
        return new Vec3(0.0, deltaY, 0.0); // every candidate overlapped — carry vertically only, don't push sideways at all
    }

    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        return overlapsAnySolid(targetPos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0);
    }

    // Full orientation un-rotation so "inside block" is tested in the actual tilted frame
    private static boolean overlapsAnySolid(Vec3 targetPos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double height, double yaw, double pitch, double roll, double scale) {
        Vec3 local = ContraptionMath.realToLocal(
                targetPos, bearingWorldPos, yaw, pitch, roll, scale <= 0 ? 1.0 : scale);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        // Raise floor above standing tolerance so "resting on top" doesn't read as "embedded"
        double rMinY = local.y + FLOOR_CLEARANCE, rMaxY = local.y + height;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — never a "solid" to embed in
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean overlaps = rMaxX > minX && rMinX < maxX
                    && rMaxZ > minZ && rMinZ < maxZ
                    && rMaxY > minY && rMinY < maxY;
            if (overlaps) {
                return true;
            }
        }
        return false;
    }

    private void releaseAll() {
        for (UUID id : currentRiders) {
            PlayerCarry.release(id);
        }
        currentRiders.clear();
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return isStandingOnFootprint(sp.position(), bearingWorldPos, slots, 0.0, yaw);
    }

    private static boolean isStandingOnFootprint(ServerPlayer sp, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        return isStandingOnFootprint(sp.position(), bearingWorldPos, slots, deltaY, yaw);
    }

    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double yaw) {
        return isStandingOnFootprint(pos, bearingWorldPos, slots, 0.0, yaw);
    }

    // deltaY widens the Y window so a rider trailing the platform by ~1 tick stays classified as riding
    private static boolean isStandingOnFootprint(Vec3 pos, Vec3 bearingWorldPos, List<Slot> slots, double deltaY, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double verticalSlack = Math.min(Math.abs(deltaY), 1.0); // cap: never trust more than 1 block/tick of slack
        for (Slot slot : slots) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — nothing to stand ON
            }
            double minX = slot.lx - slot.width / 2.0;
            double maxX = slot.lx + slot.width / 2.0;
            double minZ = slot.lz - slot.width / 2.0;
            double maxZ = slot.lz + slot.width / 2.0;
            double topY = slot.ly + slot.standTopY;
            double localFeetY = local.y;
            if (local.x >= minX - 0.3 && local.x <= maxX + 0.3
                    && local.z >= minZ - 0.3 && local.z <= maxZ + 0.3
                    && localFeetY >= topY - 0.3 - verticalSlack && localFeetY <= topY + 0.9 + verticalSlack) {
                return true;
            }
        }
        return false;
    }

    public void carryEntities(List<net.minecraft.world.entity.Entity> candidates, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty()) {
            return;
        }
        List<Slot> solids = allSlots();
        for (net.minecraft.world.entity.Entity entity : candidates) {
            if (entity == null || entity.isRemoved()) {
                continue;
            }
            if (isStandingOnFootprint(entity.position(), bearingWorldPos, slots, yaw)) {
                double halfWidth = entity.getBbWidth() / 2.0;
                double height = entity.getBbHeight();
                Vec3 pos = entity.position();
                Vec3 clamped = clampDeltaForSideCollisionGeneric(pos, bearingWorldPos, deltaX, deltaY, deltaZ, solids, halfWidth, height, yaw);
                entity.setPos(entity.getX() + clamped.x, entity.getY() + clamped.y, entity.getZ() + clamped.z);
                entity.setOldPosAndRot();
            }
        }
    }

    public void carryNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos, double deltaX, double deltaY, double deltaZ, double yaw, java.util.UUID anchorEntityId) {
        if (deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0) {
            return;
        }
        List<Slot> slots = topSlotsOnly();
        if (slots.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : slots) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        // Inflate gather box for yaw rotation — local footprint under-covers a rotated contraption
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.0 + rotationMargin);
        List<net.minecraft.world.entity.Entity> nearby;
        try {
            nearby = realLevel.getEntities((net.minecraft.world.entity.Entity) null, bounds, e -> true);
        } catch (Throwable t) {
            return; // best-effort — same defensive shape ContraptionFurnitureCapture#captureNear uses
        }
        List<net.minecraft.world.entity.Entity> candidates = new ArrayList<>();
        for (net.minecraft.world.entity.Entity entity : nearby) {
            if (entity instanceof ServerPlayer) {
                continue; // real players carried separately via carryRiders (client-prediction-aware)
            }
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity
                    && ContraptionItemPickupSwarm.isMirror(entity.getUUID())) {
                continue; // already position-synced by ContraptionItemPickupSwarm — avoid double-carry
            }
            if (entity.getUUID().equals(anchorEntityId)) {
                continue; // the anchor drives this contraption; carrying it would double its own delta
            }
            candidates.add(entity);
        }
        carryEntities(candidates, bearingWorldPos, deltaX, deltaY, deltaZ, yaw);
    }

    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw) {
        pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT);
    }

    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings) {
        pushBackNearbyBystanders(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings);
    }

    /** Pitch/roll/scale-aware {@link #pushBackNearbyBystanders} — orientation-aware push detection (2026-07-17). */
    public void pushBackNearbyBystanders(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale,
            ContraptionPushSettings settings) {
        // Static contraption: skip player shove (shulkers handle it); non-players still pushed via pushBackNearbyEntities
        if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < STATIC_DELTA_SQ) {
            return;
        }
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        List<Slot> solids = allSlots();
        if (solids.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(2.0 + rotationMargin);
        List<ServerPlayer> nearby;
        try {
            nearby = realLevel.getEntitiesOfClass(ServerPlayer.class, bounds, p -> true);
        } catch (Throwable t) {
            return; // best-effort — same defensive shape ContraptionHitboxElement#carryNearbyEntities uses
        }
        List<Slot> topSlots = topSlotsOnly();
        for (ServerPlayer sp : nearby) {
            if (currentRiders.contains(sp.getUUID())) {
                continue; // already carried via carryRiders — that path owns this rider's delta
            }
            if (!topSlots.isEmpty() && isStandingOnFootprint(sp, bearingWorldPos, topSlots, yaw)) {
                continue; // standing on top but not yet in currentRiders (same-tick race) — carryRiders owns it
            }
            Vec3 playerPos = sp.position();
            Vec3 push = computeSolidPush(playerPos, bearingWorldPos, solids, RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw,
                    pitch, roll, scale, deltaX, deltaY, deltaZ);
            push = applyPushSettings(push, playerPos, bearingWorldPos, solids, RIDER_HALF_WIDTH, RIDER_HEIGHT, yaw,
                    settings, false);
            // Walls only — never lift a bystander (carryRiders owns vertical support)
            if (push.x == 0.0 && push.z == 0.0) {
                continue;
            }
            PlayerCarry.carry(sp, push.x, 0.0, push.z);
        }
    }

    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw) {
        pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, ContraptionPushSettings.DEFAULT,
                null);
    }

    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, ContraptionPushSettings settings,
            java.util.UUID anchorEntityId) {
        pushBackNearbyEntities(realLevel, bearingWorldPos, deltaX, deltaY, deltaZ, yaw, 0.0, 0.0, 1.0, settings,
                anchorEntityId);
    }

    /** Pitch/roll/scale-aware {@link #pushBackNearbyEntities} — orientation-aware push detection (2026-07-17). */
    public void pushBackNearbyEntities(net.minecraft.world.level.Level realLevel, Vec3 bearingWorldPos,
            double deltaX, double deltaY, double deltaZ, double yaw, double pitch, double roll, double scale,
            ContraptionPushSettings settings, java.util.UUID anchorEntityId) {
        if (settings == null) {
            settings = ContraptionPushSettings.DEFAULT;
        }
        List<Slot> solids = allSlots();
        if (solids.isEmpty() || !(realLevel instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        double minX = bearingWorldPos.x, maxX = bearingWorldPos.x;
        double minY = bearingWorldPos.y, maxY = bearingWorldPos.y;
        double minZ = bearingWorldPos.z, maxZ = bearingWorldPos.z;
        for (Slot slot : solids) {
            minX = Math.min(minX, bearingWorldPos.x + slot.lx - slot.width / 2.0);
            maxX = Math.max(maxX, bearingWorldPos.x + slot.lx + slot.width / 2.0);
            minY = Math.min(minY, bearingWorldPos.y + slot.ly);
            maxY = Math.max(maxY, bearingWorldPos.y + slot.ly + slot.height);
            minZ = Math.min(minZ, bearingWorldPos.z + slot.lz - slot.width / 2.0);
            maxZ = Math.max(maxZ, bearingWorldPos.z + slot.lz + slot.width / 2.0);
        }
        double rotationMargin = ((maxX - minX) + (maxZ - minZ)) / 2.0;
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ)
                .inflate(2.0 + rotationMargin);
        List<net.minecraft.world.entity.Entity> nearby;
        try {
            nearby = realLevel.getEntities((net.minecraft.world.entity.Entity) null, bounds, e -> true);
        } catch (Throwable t) {
            return;
        }
        List<Slot> topSlots = topSlotsOnly();
        for (net.minecraft.world.entity.Entity entity : nearby) {
            if (entity == null || entity.isRemoved() || entity instanceof ServerPlayer) {
                continue; // players handled by pushBackNearbyBystanders
            }
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity
                    && ContraptionItemPickupSwarm.isMirror(entity.getUUID())) {
                continue; // owned by the pickup swarm
            }
            if (entity.getUUID().equals(anchorEntityId)) {
                continue; // flies inside its own structure by design — see the javadoc
            }
            if (!topSlots.isEmpty() && isStandingOnFootprint(entity.position(), bearingWorldPos, topSlots, yaw)) {
                continue; // standing on top — carryNearbyEntities owns it
            }
            double halfWidth = entity.getBbWidth() / 2.0;
            double height = entity.getBbHeight();
            Vec3 push = computeSolidPush(entity.position(), bearingWorldPos, solids, halfWidth, height, yaw,
                    pitch, roll, scale, deltaX, deltaY, deltaZ);
            push = applyPushSettings(push, entity.position(), bearingWorldPos, solids, halfWidth, height, yaw,
                    settings, true);
            if (push.equals(Vec3.ZERO)) {
                continue;
            }
            entity.setPos(entity.getX() + push.x, entity.getY() + push.y, entity.getZ() + push.z);
            entity.setOldPosAndRot();
            // Transfer momentum: rammed entity keeps the shove speed so fast structures fling, not scrape
            Vec3 vel = entity.getDeltaMovement();
            double nvx = knockbackComponent(vel.x, push.x);
            double nvz = knockbackComponent(vel.z, push.z);
            if (nvx != vel.x || nvz != vel.z) {
                entity.setDeltaMovement(nvx, vel.y, nvz);
            }
        }
    }

    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double dx, double dy, double dz) {
        return computeSolidPush(pos, bearingWorldPos, solids, halfWidth, height, yaw, 0.0, 0.0, 1.0, dx, dy, dz);
    }

    private static Vec3 computeSolidPush(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double pitch, double roll, double scale, double dx, double dy, double dz) {
        Vec3 target = new Vec3(pos.x + dx, pos.y + dy, pos.z + dz);
        boolean now = overlapsAnySolid(pos, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        boolean next = overlapsAnySolid(target, bearingWorldPos, solids, halfWidth, height, yaw, pitch, roll, scale);
        if (now || next) {
            Vec3 push = resolvePushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw);
            if (!push.equals(Vec3.ZERO)) {
                return push;
            }
            return new Vec3(dx, 0.0, dz); // touching a closing wall — shove along its travel
        }
        return resolveSweptPushOut(pos, bearingWorldPos, solids, halfWidth, height, yaw, dx, dy, dz);
    }

    // Shove speed in shove direction, but never slows an entity already moving faster that way
    private static double knockbackComponent(double vel, double push) {
        if (push == 0.0) {
            return vel;
        }
        double dir = Math.signum(push);
        double sameWay = dir == Math.signum(vel) ? Math.abs(vel) : 0.0;
        return dir * Math.max(Math.abs(push), sameWay);
    }

    private static Vec3 applyPushSettings(Vec3 push, Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double height, double yaw, ContraptionPushSettings settings, boolean allowCarryUp) {
        if (push.equals(Vec3.ZERO)) {
            return push; // nothing to push — leave it alone (and skip the lip query)
        }
        double px = push.x * settings.pushStrength;
        double pz = push.z * settings.pushStrength;
        double py = push.y;
        boolean carryUp = allowCarryUp && settings.carryEntities;
        if ((settings.pushUpEnabled || carryUp) && (px != 0.0 || pz != 0.0)) {
            double lip = lipHeightAboveFeet(pos, bearingWorldPos, solids, halfWidth, yaw);
            if (lip > 0.0 && lip <= settings.maxStepUpHeight) {
                py += lip * settings.pushUpStrength;
                if (carryUp) {
                    // Carried UP onto the step, not shoved: drop the horizontal component.
                    px = 0.0;
                    pz = 0.0;
                }
            }
        }
        return new Vec3(px, py, pz);
    }

    private static double lipHeightAboveFeet(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids,
            double halfWidth, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double feetY = local.y;
        double bestTop = feetY;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half, maxX = slot.lx + half;
            double minZ = slot.lz - half, maxZ = slot.lz + half;
            if (rMaxX > minX && rMinX < maxX && rMaxZ > minZ && rMinZ < maxZ) {
                double top = slot.ly + slot.standTopY;
                if (top > bestTop) {
                    bestTop = top;
                }
            }
        }
        return bestTop - feetY;
    }

    private static Vec3 resolveSweptPushOut(Vec3 pos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth,
            double height, double yaw, double dx, double dy, double dz) {
        if (dx == 0.0 && dz == 0.0) {
            return Vec3.ZERO;
        }
        Vec3 local = ContraptionMath.realToLocal(pos, bearingWorldPos, yaw);
        // Platform delta expressed in the local slot frame (inverse of renderPosition's +yaw rotation).
        Vec3 ld = ContraptionMath.rotateYaw(new Vec3(dx, dy, dz), -yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double rMinY = local.y + FLOOR_CLEARANCE, rMaxY = local.y + height;
        double bestPushX = 0.0, bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue;
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half, maxX = slot.lx + half;
            double minZ = slot.lz - half, maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY, maxY = slot.ly + slot.standTopY;
            // Swept box: extend each solid backward along -ld to cover where the wall came FROM.
            double sMinX = minX - Math.max(0.0, ld.x), sMaxX = maxX - Math.min(0.0, ld.x);
            double sMinZ = minZ - Math.max(0.0, ld.z), sMaxZ = maxZ - Math.min(0.0, ld.z);
            double sMinY = minY - Math.max(0.0, ld.y), sMaxY = maxY - Math.min(0.0, ld.y);
            boolean swept = rMaxX > sMinX && rMinX < sMaxX && rMaxZ > sMinZ && rMinZ < sMaxZ
                    && rMaxY > sMinY && rMinY < sMaxY;
            if (!swept) {
                continue;
            }
            any = true;
            // Shove to just ahead of the wall's LEADING face along the dominant motion axis.
            if (Math.abs(ld.x) >= Math.abs(ld.z) && ld.x != 0.0) {
                double lead = ld.x > 0.0 ? (maxX + halfWidth) - local.x : (minX - halfWidth) - local.x;
                if (Math.abs(lead) > Math.abs(bestPushX)) {
                    bestPushX = lead;
                }
            } else if (ld.z != 0.0) {
                double lead = ld.z > 0.0 ? (maxZ + halfWidth) - local.z : (minZ - halfWidth) - local.z;
                if (Math.abs(lead) > Math.abs(bestPushZ)) {
                    bestPushZ = lead;
                }
            }
        }
        if (!any) {
            return Vec3.ZERO;
        }
        return ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    private static Vec3 resolvePushOut(Vec3 playerPos, Vec3 bearingWorldPos, List<Slot> solids, double halfWidth, double height, double yaw) {
        Vec3 local = ContraptionMath.realToLocal(playerPos, bearingWorldPos, yaw);
        double rMinX = local.x - halfWidth, rMaxX = local.x + halfWidth;
        double rMinZ = local.z - halfWidth, rMaxZ = local.z + halfWidth;
        double rMinY = local.y, rMaxY = local.y + height;
        double bestPushX = 0.0, bestPushZ = 0.0;
        boolean any = false;
        for (Slot slot : solids) {
            if (!slot.hasCollision) {
                continue; // no real collision at all — e.g. a captured torch — never pushes anyone out
            }
            double half = slot.width / 2.0;
            double minX = slot.lx - half;
            double maxX = slot.lx + half;
            double minZ = slot.lz - half;
            double maxZ = slot.lz + half;
            double minY = slot.ly + slot.standBottomY;
            double maxY = slot.ly + slot.standTopY;
            boolean overlaps = rMaxX > minX && rMinX < maxX
                    && rMaxZ > minZ && rMinZ < maxZ
                    && rMaxY > minY && rMinY < maxY;
            if (!overlaps) {
                continue;
            }
            any = true;
            double penX = Math.min(rMaxX - minX, maxX - rMinX);
            double penZ = Math.min(rMaxZ - minZ, maxZ - rMinZ);
            double signX = (local.x >= (minX + maxX) / 2.0) ? 1.0 : -1.0;
            double signZ = (local.z >= (minZ + maxZ) / 2.0) ? 1.0 : -1.0;
            // Prefer whichever axis has the smaller penetration for THIS slot (least-displacement
            // resolution), then keep the largest push seen across all overlapping slots so a
            // player straddling multiple cells is fully cleared of all of them.
            if (penX <= penZ) {
                if (Math.abs(penX * signX) > Math.abs(bestPushX)) {
                    bestPushX = penX * signX;
                }
            } else {
                if (Math.abs(penZ * signZ) > Math.abs(bestPushZ)) {
                    bestPushZ = penZ * signZ;
                }
            }
        }
        if (!any) {
            return Vec3.ZERO;
        }
        // push computed in local frame → rotate back to world (yaw about Y; Y push is always 0).
        return ContraptionMath.rotateYaw(new Vec3(bestPushX, 0.0, bestPushZ), yaw);
    }

    public int cellCount() {
        return autoSlots.size() + customSlots.size();
    }

    /** Geometry-only slot for carry/pushback math — no packet entity. */
    static final class Slot {
        final double lx, ly, lz;
        final float width, height;
        // Shape-derived stand window — mutable so rebuild can refresh on in-place block swap
        double standBottomY, standTopY;
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

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
