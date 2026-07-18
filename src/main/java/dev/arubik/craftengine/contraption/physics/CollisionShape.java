package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * A contraption's collision geometry: its captured cells' REAL block shapes, merged into as few
 * boxes as possible, expressed COM-relative to match {@link RigidBody}'s origin.
 *
 * <h2>Real block shapes, not full cubes</h2>
 * Each cell contributes {@link BlockState#getCollisionShape} — so a slab collides as a slab, a
 * fence as a post, and a non-colliding block (torch, plant) contributes nothing at all. The previous
 * engine treated every captured cell as a solid 1×1×1 cube, which is why bodies rested on air above
 * a slab and why decorative blocks acted like walls.
 *
 * <h2>Merging</h2>
 * The per-cell shapes are boolean-OR'd into one {@link VoxelShape}, then {@code optimize()}d and
 * flattened via {@code toAabbs()}. This is Create's {@code Contraption#gatherBBsOffThread} trick, and
 * it is the highest value-per-line idea in either reference codebase: {@code optimize()} runs
 * vanilla's greedy box merging, so a 200-block platform collapses from 200 boxes to a handful of
 * slabs. Every downstream cost — contact sampling, broadphase, the shulker collider swarm — scales
 * with the merged box count, not the block count.
 *
 * <h2>Sample points</h2>
 * {@link #samplePoints()} is the contact point cloud, mirroring VS2's per-blockstate
 * {@code collisionPoints} (position + radius) rather than attempting box-vs-box SAT against the
 * world. Points are the merged boxes' corners plus a subdivision of their faces at
 * {@link #SAMPLE_SPACING}. Corners matter most and are never dropped: a resting body's support
 * polygon is its corner set, so corner contacts are precisely what generate the tipping torque when
 * the COM moves outside them.
 */
public final class CollisionShape {

    /**
     * Face subdivision spacing, in blocks. At {@code 0.5} no sample is ever more than half a block
     * from its neighbour, so a body cannot straddle a 1-block world pillar with no sample landing on
     * it. Smaller means better manifolds and more cost; this is the coarsest value that cannot miss a
     * full block.
     */
    public static final double SAMPLE_SPACING = 0.5;

    /**
     * Hard cap on the sample cloud. A pathological (huge, or heavily scaled) contraption would
     * otherwise generate points without bound and stall the solver, which runs this cloud once per
     * substep. Corners are emitted before face subdivisions, so a truncated cloud degrades to
     * "corners only" — still stable, just coarser.
     */
    public static final int MAX_SAMPLE_POINTS = 2048;

    private final List<AABB> boxes;
    private final List<Vector3d> samplePoints;
    private final double boundingRadius;

    private CollisionShape(List<AABB> boxes, List<Vector3d> samplePoints, double boundingRadius) {
        this.boxes = boxes;
        this.samplePoints = samplePoints;
        this.boundingRadius = boundingRadius;
    }

    /** An empty shape — no boxes, no samples. Collides with nothing. */
    public static final CollisionShape EMPTY = new CollisionShape(List.of(), List.of(), 0.0);

    /**
     * Builds the merged, COM-relative shape for a captured level.
     *
     * @param level the captured cells
     * @param centerOfMass the body's COM in the level's LOCAL frame — every box and sample is emitted
     *        relative to this, so the result drops straight into {@link RigidBody}'s COM-origin space
     */
    public static CollisionShape of(ContraptionLevel level, Vector3d centerOfMass) {
        if (level == null) {
            return EMPTY;
        }
        VoxelShape combined = Shapes.empty();
        boolean any = false;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                continue;
            }
            VoxelShape shape;
            try {
                shape = state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            } catch (Exception e) {
                // A block whose shape queries its own level/BE cannot be resolved against
                // EmptyBlockGetter. Fall back to a full cube rather than dropping the cell — a
                // missing collider is a hole the body falls through; an oversized one is merely blunt.
                shape = Shapes.block();
            }
            if (shape.isEmpty()) {
                continue; // genuinely non-colliding (torch, plant) — contributes no geometry
            }
            combined = Shapes.joinUnoptimized(combined,
                    shape.move(local.getX(), local.getY(), local.getZ()), BooleanOp.OR);
            any = true;
        }
        if (!any) {
            return EMPTY;
        }
        List<AABB> merged = combined.optimize().toAabbs();
        List<AABB> boxes = new ArrayList<>(merged.size());
        for (AABB box : merged) {
            boxes.add(box.move(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z));
        }
        List<Vector3d> samples = buildSamplePoints(boxes);
        double radius = 0.0;
        for (Vector3d p : samples) {
            radius = Math.max(radius, p.length());
        }
        return new CollisionShape(List.copyOf(boxes), List.copyOf(samples), radius);
    }

    /**
     * Builds a shape directly from boxes already expressed in the contraption's LOCAL frame, offsetting
     * them by {@code centerOfMass} exactly as {@link #of} does. Bypasses the block/VoxelShape lookup, so
     * a body's collision response can be exercised without a live world.
     */
    public static CollisionShape ofBoxes(List<AABB> localBoxes, Vector3d centerOfMass) {
        if (localBoxes.isEmpty()) {
            return EMPTY;
        }
        List<AABB> boxes = new ArrayList<>(localBoxes.size());
        for (AABB box : localBoxes) {
            boxes.add(box.move(-centerOfMass.x, -centerOfMass.y, -centerOfMass.z));
        }
        List<Vector3d> samples = buildSamplePoints(boxes);
        double radius = 0.0;
        for (Vector3d p : samples) {
            radius = Math.max(radius, p.length());
        }
        return new CollisionShape(List.copyOf(boxes), List.copyOf(samples), radius);
    }

    /**
     * Emits the contact cloud: every box's 8 corners first, then a {@link #SAMPLE_SPACING} grid over
     * each box face. Interior points are pointless — only the surface can touch anything — so faces
     * are walked rather than the volume.
     */
    private static List<Vector3d> buildSamplePoints(List<AABB> boxes) {
        List<Vector3d> corners = new ArrayList<>();
        List<Vector3d> faces = new ArrayList<>();
        for (AABB box : boxes) {
            for (int i = 0; i < 8; i++) {
                corners.add(new Vector3d(
                        (i & 1) == 0 ? box.minX : box.maxX,
                        (i & 2) == 0 ? box.minY : box.maxY,
                        (i & 4) == 0 ? box.minZ : box.maxZ));
            }
            for (int axis = 0; axis < 3; axis++) {
                for (int side = 0; side < 2; side++) {
                    addFaceSamples(faces, box, axis, side);
                }
            }
        }
        List<Vector3d> out = new ArrayList<>(corners.size() + faces.size());
        out.addAll(corners);
        for (Vector3d p : faces) {
            if (out.size() >= MAX_SAMPLE_POINTS) {
                break;
            }
            out.add(p);
        }
        return out;
    }

    /** Walks one face of {@code box} on a {@link #SAMPLE_SPACING} grid, skipping the corners already emitted. */
    private static void addFaceSamples(List<Vector3d> out, AABB box, int axis, int side) {
        double[] min = { box.minX, box.minY, box.minZ };
        double[] max = { box.maxX, box.maxY, box.maxZ };
        int u = (axis + 1) % 3;
        int v = (axis + 2) % 3;
        double fixed = side == 0 ? min[axis] : max[axis];
        for (double a = min[u]; a <= max[u] + 1.0E-9; a += SAMPLE_SPACING) {
            for (double b = min[v]; b <= max[v] + 1.0E-9; b += SAMPLE_SPACING) {
                double ua = Math.min(a, max[u]);
                double vb = Math.min(b, max[v]);
                boolean uEdge = ua <= min[u] + 1.0E-9 || ua >= max[u] - 1.0E-9;
                boolean vEdge = vb <= min[v] + 1.0E-9 || vb >= max[v] - 1.0E-9;
                if (uEdge && vEdge) {
                    continue; // already a corner
                }
                double[] p = new double[3];
                p[axis] = fixed;
                p[u] = ua;
                p[v] = vb;
                out.add(new Vector3d(p[0], p[1], p[2]));
            }
        }
    }

    /** The merged boxes, COM-relative, in the body's local frame. */
    public List<AABB> boxes() {
        return boxes;
    }

    /** The contact sample cloud, COM-relative, in the body's local frame. */
    public List<Vector3d> samplePoints() {
        return samplePoints;
    }

    /** Distance from the COM to the furthest sample — the broadphase sphere radius (before scale). */
    public double boundingRadius() {
        return boundingRadius;
    }

    public boolean isEmpty() {
        return boxes.isEmpty();
    }
}
