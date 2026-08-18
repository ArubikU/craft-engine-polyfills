/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WorldBlockCache {
    public static final double FLOW_SPEED = 0.14;
    public static final double WATER_BUOYANCY = 1.0;
    public static final double LAVA_BUOYANCY = 2.0;
    private final Map<Long, List<AABB>> boxes;
    private final Map<Long, Fluid> fluids;
    public static final WorldBlockCache EMPTY = new WorldBlockCache(Map.of(), Map.of());

    private WorldBlockCache(Map<Long, List<AABB>> boxes, Map<Long, Fluid> fluids) {
        this.boxes = boxes;
        this.fluids = fluids;
    }

    public static WorldBlockCache bake(ServerLevel level, AABB region) {
        if (level == null) {
            return EMPTY;
        }
        HashMap<Long, List<AABB>> map = new HashMap<Long, List<AABB>>();
        HashMap<Long, Fluid> fluidMap = new HashMap<Long, Fluid>();
        int minX = (int)Math.floor(region.minX);
        int maxX = (int)Math.floor(region.maxX);
        int minY = (int)Math.floor(region.minY);
        int maxY = (int)Math.floor(region.maxY);
        int minZ = (int)Math.floor(region.minZ);
        int maxZ = (int)Math.floor(region.maxZ);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        long lastChunkKey = Long.MIN_VALUE;
        LevelChunk lastChunk = null;
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                long ck = ChunkPos.asLong((int)(x >> 4), (int)(z >> 4));
                if (lastChunk == null || ck != lastChunkKey) {
                    lastChunkKey = ck;
                    lastChunk = level.getChunkSource().getChunkNow(x >> 4, z >> 4);
                }
                if (lastChunk == null) continue;
                for (int y = minY; y <= maxY; ++y) {
                    VoxelShape shape;
                    cursor.set(x, y, z);
                    BlockState state = lastChunk.getBlockState((BlockPos)cursor);
                    if (state.isAir()) continue;
                    try {
                        FluidState fluid = state.getFluidState();
                        if (!fluid.isEmpty()) {
                            boolean lava = fluid.is(FluidTags.LAVA);
                            double height = fluid.getHeight((BlockGetter)level, (BlockPos)cursor);
                            Vec3 flow = fluid.getFlow((BlockGetter)level, (BlockPos)cursor);
                            fluidMap.put(BlockPos.asLong((int)x, (int)y, (int)z), new Fluid(lava ? 2.0 : 1.0, (double)y + height, lava, flow.lengthSqr() < 1.0E-12 ? Vec3.ZERO : flow.normalize().scale(0.14)));
                        }
                    }
                    catch (Exception fluid) {
                        // empty catch block
                    }
                    try {
                        shape = state.getCollisionShape((BlockGetter)level, (BlockPos)cursor);
                    }
                    catch (Exception e) {
                        continue;
                    }
                    if (shape.isEmpty()) continue;
                    ArrayList<AABB> cell = new ArrayList<AABB>();
                    for (AABB box : shape.toAabbs()) {
                        cell.add(box.move((double)x, (double)y, (double)z));
                    }
                    if (cell.isEmpty()) continue;
                    map.put(BlockPos.asLong((int)x, (int)y, (int)z), cell);
                }
            }
        }
        return map.isEmpty() && fluidMap.isEmpty() ? EMPTY : new WorldBlockCache(map, fluidMap);
    }

    public static WorldBlockCache solidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        HashMap<Long, List<AABB>> map = new HashMap<Long, List<AABB>>();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    map.put(BlockPos.asLong((int)x, (int)y, (int)z), List.of(new AABB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1))));
                }
            }
        }
        return map.isEmpty() ? EMPTY : new WorldBlockCache(map, Map.of());
    }

    public static WorldBlockCache fluidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double surfaceY, boolean lava) {
        return WorldBlockCache.fluidRegion(minX, minY, minZ, maxX, maxY, maxZ, surfaceY, lava, Vec3.ZERO);
    }

    public static WorldBlockCache fluidRegion(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double surfaceY, boolean lava, Vec3 flow) {
        HashMap<Long, Fluid> map = new HashMap<Long, Fluid>();
        Fluid fluid = new Fluid(lava ? 2.0 : 1.0, surfaceY, lava, flow);
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    map.put(BlockPos.asLong((int)x, (int)y, (int)z), fluid);
                }
            }
        }
        return map.isEmpty() ? EMPTY : new WorldBlockCache(Map.of(), map);
    }

    public List<AABB> at(int x, int y, int z) {
        List<AABB> cell = this.boxes.get(BlockPos.asLong((int)x, (int)y, (int)z));
        return cell == null ? List.of() : cell;
    }

    public Fluid fluidAt(int x, int y, int z) {
        return this.fluids.get(BlockPos.asLong((int)x, (int)y, (int)z));
    }

    public boolean hasNoFluid() {
        return this.fluids.isEmpty();
    }

    public boolean isEmpty() {
        return this.boxes.isEmpty();
    }

    public record Fluid(double buoyancy, double topY, boolean lava, Vec3 flow) {
    }
}

