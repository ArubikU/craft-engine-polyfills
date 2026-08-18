/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.EmptyBlockGetter
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Vector3d
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

public final class CollisionShape {
    public static final double SAMPLE_SPACING = 0.5;
    public static final int MAX_SAMPLE_POINTS = 2048;
    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<BlockState, VoxelShape>();
    private final List<AABB> boxes;
    private final List<Vector3d> samplePoints;
    private final double boundingRadius;
    public static final CollisionShape EMPTY = new CollisionShape(List.of(), List.of(), 0.0);
    private static final double INSIDE_EPS = 1.0E-4;

    private static VoxelShape collisionShapeOf(BlockState state) {
        return SHAPE_CACHE.computeIfAbsent(state, s -> {
            if (s.getBlock() instanceof LiquidBlock) {
                return Shapes.empty();
            }
            try {
                return s.getCollisionShape((BlockGetter)EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            }
            catch (Exception e) {
                return Shapes.block();
            }
        });
    }

    private CollisionShape(List<AABB> boxes, List<Vector3d> samplePoints, double boundingRadius) {
        this.boxes = boxes;
        this.samplePoints = samplePoints;
        this.boundingRadius = boundingRadius;
    }

    public static CollisionShape of(ContraptionLevel level, Vector3d centerOfMass) {
        if (level == null) {
            return EMPTY;
        }
        ServerLevel sl = null;
        try {
            sl = level.serverLevel();
        }
        catch (Throwable ignored) {
            sl = null;
        }
        long lastChunkKey = Long.MIN_VALUE;
        LevelChunk lastChunk = null;
        VoxelShape combined = Shapes.empty();
        boolean any = false;
        for (BlockPos local : level.localPositions()) {
            VoxelShape shape;
            BlockState state;
            if (sl != null) {
                long ck = ChunkPos.asLong((int)(local.getX() >> 4), (int)(local.getZ() >> 4));
                if (lastChunk == null || ck != lastChunkKey) {
                    lastChunkKey = ck;
                    lastChunk = sl.getChunk(local.getX() >> 4, local.getZ() >> 4);
                }
                state = lastChunk.getBlockState(local);
            } else {
                state = level.getBlockState(local);
            }
            if (state.isAir() || (shape = CollisionShape.collisionShapeOf((BlockState)state)).isEmpty()) continue;
            combined = Shapes.joinUnoptimized((VoxelShape)combined, (VoxelShape)shape.move((double)local.getX(), (double)local.getY(), (double)local.getZ()), (BooleanOp)BooleanOp.OR);
            any = true;
        }
        if (!any) {
            BlockState state;
            VoxelShape outlineCombined = Shapes.empty();
            boolean anyOutline = false;
            for (BlockPos local : level.localPositions()) {
                state = level.getBlockState(local);
                if (state.isAir() || state.getBlock() instanceof LiquidBlock) continue;
                try {
                    VoxelShape outline = state.getShape((BlockGetter)EmptyBlockGetter.INSTANCE, local);
                    if (outline.isEmpty()) continue;
                    outlineCombined = Shapes.joinUnoptimized((VoxelShape)outlineCombined, (VoxelShape)outline.move((double)local.getX(), (double)local.getY(), (double)local.getZ()), (BooleanOp)BooleanOp.OR);
                    anyOutline = true;
                }
                catch (Throwable outline) {}
            }
            if (!anyOutline) {
                return EMPTY;
            }
            List<AABB> merged = outlineCombined.optimize().toAabbs();
            ArrayList<AABB> boxes = new ArrayList<AABB>(merged.size());
            for (AABB box : merged) {
                boxes.add(box.move(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z));
            }
            List<Vector3d> samples = CollisionShape.buildSamplePoints(boxes);
            double radius = 0.0;
            for (Vector3d p : samples) {
                radius = Math.max(radius, p.length());
            }
            return new CollisionShape(List.copyOf(boxes), List.copyOf(samples), radius);
        }
        List<AABB> merged = combined.optimize().toAabbs();
        ArrayList<AABB> boxes = new ArrayList<AABB>(merged.size());
        for (AABB box : merged) {
            boxes.add(box.move(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z));
        }
        List<Vector3d> samples = CollisionShape.buildSamplePoints(boxes);
        double radius = 0.0;
        for (Vector3d p : samples) {
            radius = Math.max(radius, p.length());
        }
        return new CollisionShape(List.copyOf(boxes), List.copyOf(samples), radius);
    }

    public static CollisionShape ofBoxes(List<AABB> localBoxes, Vector3d centerOfMass) {
        if (localBoxes.isEmpty()) {
            return EMPTY;
        }
        ArrayList<AABB> boxes = new ArrayList<AABB>(localBoxes.size());
        for (AABB box : localBoxes) {
            boxes.add(box.move(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z));
        }
        List<Vector3d> samples = CollisionShape.buildSamplePoints(boxes);
        double radius = 0.0;
        for (Vector3d p : samples) {
            radius = Math.max(radius, p.length());
        }
        return new CollisionShape(List.copyOf(boxes), List.copyOf(samples), radius);
    }

    private static List<Vector3d> buildSamplePoints(List<AABB> boxes) {
        ArrayList<Vector3d> corners = new ArrayList<Vector3d>();
        ArrayList<Vector3d> faces = new ArrayList<Vector3d>();
        for (AABB box : boxes) {
            for (int i = 0; i < 8; ++i) {
                corners.add(new Vector3d((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY, (i & 4) == 0 ? box.minZ : box.maxZ));
            }
            for (int axis = 0; axis < 3; ++axis) {
                for (int side = 0; side < 2; ++side) {
                    CollisionShape.addFaceSamples(faces, box, axis, side);
                }
            }
        }
        ArrayList<Vector3d> out = new ArrayList<Vector3d>(corners.size() + faces.size());
        for (Vector3d p : corners) {
            if (CollisionShape.strictlyInsideAny(p, boxes)) continue;
            out.add(p);
        }
        for (Vector3d p : faces) {
            if (out.size() >= 2048) break;
            if (CollisionShape.strictlyInsideAny(p, boxes)) continue;
            out.add(p);
        }
        return out;
    }

    private static boolean strictlyInsideAny(Vector3d p, List<AABB> boxes) {
        for (AABB b : boxes) {
            if (!(p.x > b.minX + 1.0E-4) || !(p.x < b.maxX - 1.0E-4) || !(p.y > b.minY + 1.0E-4) || !(p.y < b.maxY - 1.0E-4) || !(p.z > b.minZ + 1.0E-4) || !(p.z < b.maxZ - 1.0E-4)) continue;
            return true;
        }
        return false;
    }

    private static void addFaceSamples(List<Vector3d> out, AABB box, int axis, int side) {
        double[] min = new double[]{box.minX, box.minY, box.minZ};
        double[] max = new double[]{box.maxX, box.maxY, box.maxZ};
        int u = (axis + 1) % 3;
        int v = (axis + 2) % 3;
        double fixed = side == 0 ? min[axis] : max[axis];
        for (double a = min[u]; a <= max[u] + 1.0E-9; a += 0.5) {
            for (double b = min[v]; b <= max[v] + 1.0E-9; b += 0.5) {
                boolean vEdge;
                double ua = Math.min(a, max[u]);
                double vb = Math.min(b, max[v]);
                boolean uEdge = ua <= min[u] + 1.0E-9 || ua >= max[u] - 1.0E-9;
                boolean bl = vEdge = vb <= min[v] + 1.0E-9 || vb >= max[v] - 1.0E-9;
                if (uEdge && vEdge) continue;
                double[] p = new double[3];
                p[axis] = fixed;
                p[u] = ua;
                p[v] = vb;
                out.add(new Vector3d(p[0], p[1], p[2]));
            }
        }
    }

    public List<AABB> boxes() {
        return this.boxes;
    }

    public List<Vector3d> samplePoints() {
        return this.samplePoints;
    }

    public double boundingRadius() {
        return this.boundingRadius;
    }

    public boolean isEmpty() {
        return this.boxes.isEmpty();
    }
}

