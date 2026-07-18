package dev.arubik.craftengine.contraption.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix3d;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;

/**
 * The aggregate mass model of one PhysContraption: total {@link #totalMass}, the mass-weighted
 * {@link #centerOfMass} (in the contraption's LOCAL yaw-0 frame), and the full 3&times;3
 * <b>inertia tensor about the COM</b>, stored as its six independent components (the tensor is
 * symmetric).
 *
 * <p>Per-cell mass comes from {@link WeightBlockBehavior#weightOf(BlockState)} — a captured
 * {@code polyfills:weight_block}'s configured {@code weight:}, else the vanilla weight table, else
 * {@link #BASELINE_BLOCK_MASS}, so every cell carries a nonzero mass and any phys contraption has a
 * strictly positive total.
 *
 * <h2>Why a real tensor, and why this replaced three scalars</h2>
 * This record previously held three <i>independent scalar</i> inertias (yaw/pitch/roll), matching a
 * render pipeline that carried three independent Euler angles. That model cannot tip: with no
 * products of inertia there is no coupling between axes, and — more fundamentally — nothing in the
 * old collision path ever produced a torque to feed them, so {@link #centerOfMass} was computed and
 * then ignored. A heavier block changed the COM and changed nothing else.
 *
 * <p>Tipping is not a special case to be detected and applied; it is what falls out of applying
 * contact impulses at points away from the COM. That requires the true tensor, because
 * {@code ω += I⁻¹·(r × p)} couples all three axes through the off-diagonal terms.
 *
 * <h2>Per-cell inertia: I = m/6</h2>
 * Each cell contributes its own inertia about its own centre plus a parallel-axis transfer to the
 * body COM:
 * <pre>
 *   I += I_cell + m·(|d|²·δ_ij − d_i·d_j),   d = cellCentre − COM
 * </pre>
 * {@code I_cell} is a solid unit cube's tensor: {@code (1/6)·m·s²} on each diagonal with side
 * {@code s = 1}, hence {@code m/6}, isotropic. Omitting it (as the old scalars did) makes a
 * single-cell body have exactly zero inertia — it would spin infinitely fast under any impulse.
 *
 * <p><b>Deliberately NOT the sphere-MOI shortcut.</b> Valkyrien Skies feeds its solver an isotropic
 * {@code avg(I)·Identity} for stability across huge ships, which costs all rotational realism — a
 * long beam would resist roll exactly as much as yaw, and a lopsided body would tip at the same rate
 * in every direction. Our bodies are small, so we ship the true tensor.
 */
public record MassModel(double totalMass, Vec3 centerOfMass, int cellCount,
        double ixx, double iyy, double izz, double ixy, double ixz, double iyz) {

    /**
     * Baseline mass a cell contributes when {@link WeightBlockBehavior#weightOf} cannot resolve
     * anything better. Chosen at {@code 1.0} so a weight block's default
     * {@link WeightBlockBehavior#DEFAULT_WEIGHT} (10.0) reads as "ten normal blocks of ballast."
     */
    public static final double BASELINE_BLOCK_MASS = 1.0;

    /** An empty contraption's degenerate model — mass {@code 0}, COM at the local origin, no inertia. */
    public static final MassModel EMPTY = new MassModel(0.0, Vec3.ZERO, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    /**
     * Aggregates the mass model over a captured level's CURRENT cells, each cell's mass placed at its
     * block CENTRE ({@code offset + (0.5,0.5,0.5)}) in the local yaw-0 frame. A {@code null} level
     * (pure-JVM test / unloaded world) or one with no cells yields {@link #EMPTY}.
     *
     * <p>Two passes: the COM must be known before the tensor can be accumulated about it. The old
     * single-pass {@code Σw·x² − M·com²} identity works for a bare diagonal but is numerically worse
     * (it is a difference of two large nearly-equal sums) and does not extend cleanly to the products
     * of inertia, so with a real tensor the honest second pass is preferred.
     */
    public static MassModel of(ContraptionLevel level) {
        if (level == null) {
            return EMPTY;
        }
        double mass = 0.0;
        double cx = 0.0, cy = 0.0, cz = 0.0;
        int cells = 0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                continue; // defensive — localPositions() already excludes air
            }
            double w = WeightBlockBehavior.weightOf(state);
            mass += w;
            cx += w * (local.getX() + 0.5);
            cy += w * (local.getY() + 0.5);
            cz += w * (local.getZ() + 0.5);
            cells++;
        }
        if (mass <= 0.0 || cells == 0) {
            return EMPTY;
        }
        double comX = cx / mass, comY = cy / mass, comZ = cz / mass;

        double ixx = 0.0, iyy = 0.0, izz = 0.0, ixy = 0.0, ixz = 0.0, iyz = 0.0;
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                continue;
            }
            double w = WeightBlockBehavior.weightOf(state);
            double dx = local.getX() + 0.5 - comX;
            double dy = local.getY() + 0.5 - comY;
            double dz = local.getZ() + 0.5 - comZ;
            double self = w / 6.0; // solid unit cube about its own centre, isotropic
            ixx += self + w * (dy * dy + dz * dz);
            iyy += self + w * (dx * dx + dz * dz);
            izz += self + w * (dx * dx + dy * dy);
            ixy -= w * dx * dy;
            ixz -= w * dx * dz;
            iyz -= w * dy * dz;
        }
        return new MassModel(mass, new Vec3(comX, comY, comZ), cells, ixx, iyy, izz, ixy, ixz, iyz);
    }

    /** The inertia tensor about the COM, in the LOCAL (yaw-0) frame, as a JOML matrix. */
    public Matrix3d inertiaTensor() {
        return new Matrix3d(
                ixx, ixy, ixz,
                ixy, iyy, iyz,
                ixz, iyz, izz);
    }

    /**
     * The INVERSE inertia tensor about the COM in the local frame — what the solver actually needs
     * ({@code ω += I⁻¹·τ}). Returns a zero matrix for {@link #EMPTY} (infinite inertia, i.e. an
     * unrotatable body), which is the correct degenerate answer: no impulse can spin nothing.
     */
    public Matrix3d inverseInertiaTensor() {
        if (cellCount == 0 || totalMass <= 0.0) {
            return new Matrix3d().zero();
        }
        Matrix3d inertia = inertiaTensor();
        double det = inertia.determinant();
        if (Math.abs(det) < 1.0E-9) {
            // Degenerate (a single cell, or all mass collinear). The self term (m/6) keeps this from
            // happening for any real body, but guard rather than emit NaNs into the integrator.
            return new Matrix3d().zero();
        }
        return inertia.invert(new Matrix3d());
    }

    /** {@code 1/mass}, or {@code 0} (infinite mass — immovable) for {@link #EMPTY}. */
    public double inverseMass() {
        return totalMass > 0.0 ? 1.0 / totalMass : 0.0;
    }

    /** Rotational inertia about the vertical (yaw / Y) axis through the COM. */
    public double momentOfInertiaYaw() {
        return iyy;
    }

    /** Rotational inertia about the pitch (X) axis through the COM. */
    public double momentOfInertiaPitch() {
        return ixx;
    }

    /** Rotational inertia about the roll (Z) axis through the COM. */
    public double momentOfInertiaRoll() {
        return izz;
    }
}
