package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix3d;
import org.joml.Vector3d;

/**
 * The physics solver: substepped XPBD (Extended Position Based Dynamics) over a flat list of
 * {@link PhysBody}.
 *
 * <h2>Why XPBD</h2>
 * This is the algorithm family Valkyrien Skies runs. Its Krunch solver is a closed-source native
 * binary, but its settings surface is an unambiguous fingerprint — {@code collisionCompliance},
 * {@code positionIterations} separate from {@code velocityIterations}, and
 * {@code solverType ∈ {JACOBI, GAUSS_SEIDEL, COLORED_GAUSS_SEIDEL}} are XPBD's exact vocabulary
 * (Müller et al. 2020, <i>Detailed Rigid Body Simulation with Extended Position Based Dynamics</i>).
 * VS overrides the defaults to {@code subSteps = 20, iterations = 2}, which is that paper's central
 * result: <b>many substeps beat many iterations</b>. Halving the substep is a genuinely smaller
 * problem; another iteration only polishes the same one.
 *
 * <h2>The loop</h2>
 * Per substep: integrate → <b>solve positions</b> → recover velocities <i>from the position change</i>
 * → solve velocities. Reading velocity back out of the position solve is what makes XPBD stable at
 * rest: penetration is corrected as position, so there is no stiff spring to overshoot, and no
 * Baumgarte bias term feeding energy back in.
 *
 * <h2>Where tipping comes from</h2>
 * Nowhere in this file is there a tip, tilt, lean, or topple. {@link #solvePositions} applies each
 * contact impulse at its own lever arm {@code r} and rotates the body by
 * {@code I⁻¹·(r × p)}. A body resting flat gets symmetric corner impulses whose torques cancel — it
 * stays put, correctly. Move the center of mass toward one edge and the far corners unload, the near
 * ones do not, the torques stop cancelling, and the body rotates. That is the whole mechanism: an
 * unbalanced body tips because its support impulses are unbalanced, which is also why the answer to
 * "does iron weigh more" is now yes and it matters.
 */
public final class XpbdSolver {

    private XpbdSolver() {
    }

    /**
     * Substeps per game tick. VS2 uses 20 at 60 Hz; we run at 20 Hz over far fewer bodies, so 8
     * substeps per tick is a comparable inner {@code h} at a fraction of the cost. This is the primary
     * stability knob — raise it before touching iteration counts.
     */
    public static final int SUB_STEPS = 8;

    /** Position-solve iterations per substep, matching VS2's {@code iterations = 2}. */
    public static final int POSITION_ITERATIONS = 2;

    /**
     * Gravity, blocks per tick². Matches vanilla {@code FallingBlockEntity}, which is the closest
     * vanilla analogue to a falling structure, so a dropped contraption falls at a familiar rate.
     */
    public static final double GRAVITY = -0.04;

    /**
     * Contacts are generated for geometry within this distance <i>before</i> it touches. These
     * speculative contacts are how tunnelling is prevented without continuous collision detection: a
     * fast body's contact is already in the manifold when the substep begins, so the position solve
     * stops it at the surface instead of after it has passed through. VS2 ships the same 0.05.
     */
    public static final double SPECULATIVE_DISTANCE = 0.05;

    /**
     * Cap on how fast a contact may push a body out, in blocks per substep. Without it, a body that
     * spawns deep inside terrain converts its entire penetration into one frame of separation and is
     * launched across the map. VS2 clamps the same quantity ({@code maxDePenetrationSpeed}, lowered
     * from Krunch's 10000 to 10).
     */
    public static final double MAX_DEPENETRATION_PER_SUBSTEP = 0.05;

    /**
     * Coefficient of restitution. Zero — blocks do not bounce. Create likewise has no general
     * restitution, treating bounce as a per-block special case (slime, beds) rather than a material
     * property.
     */
    public static final double RESTITUTION = 0.0;

    /**
     * Closing speed (blocks/tick) below which restitution is suppressed — a contact this slow is a body
     * settling, not an impact. Without the gate a bouncy body resting on slime would never come to rest:
     * each substep of gravity (~0.005 blocks/tick of closing speed) would be handed back as a tiny hop and
     * pumped straight back in next substep. {@code 0.1} is well above resting jitter and well below any real
     * impact (a 1-block drop closes at ~0.28), so a genuine landing still bounces while a rest still rests.
     * This is the same threshold idea Box2D uses ({@code b2_velocityThreshold}).
     */
    public static final double RESTITUTION_MIN_SPEED = 0.1;

    /** Coulomb friction coefficient at contacts. High enough that a resting body does not creep. */
    public static final double FRICTION = 0.7;

    /** Per-tick velocity damping, standing in for air resistance and unmodelled losses. */
    public static final double LINEAR_DAMPING = 0.02;
    public static final double ANGULAR_DAMPING = 0.05;

    /**
     * Ceiling on total angular velocity (rad/tick) — the fan-flip stability knob (2026-07-18). A sustained
     * off-centre thrust still accumulates rotation up to this cap and flips the contraption over deliberately
     * (~1 s for a half-turn at this value), but a numerical spike or jitter burst can never launch it into a
     * chaotic tumble. So a fan CAN flip the structure, it just can't do it "porque sí".
     */
    public static final double MAX_ANGULAR_VELOCITY = 0.15;

    /**
     * Floor on total angular velocity (rad/tick) — the fan-flip NOISE GATE (2026-07-18, "4 fans simétricos
     * hacia abajo y se voltea ... solo ejecutar movimientos reales"). A perfectly balanced fan array's torques
     * sum to zero on paper, but floating-point residue leaks a sliver of spin every tick that integrates into a
     * slow, unwanted flip. Below this threshold the spin is that residue — not a real movement — so it is zeroed.
     * A genuine off-centre thrust builds spin well past this within a tick and still flips the body, so this
     * suppresses the "porque sí" drift without disarming a deliberate flip. Sits just under a deliberate flip's
     * per-tick spin and well over the numerical noise floor.
     */
    public static final double ANGULAR_NOISE_EPS = 0.0025;

    /**
     * Fluid drag per unit of submerged volume. This is what settles a floating body instead of leaving
     * it bobbing: buoyancy alone is a spring, and a spring with no losses oscillates forever.
     */
    /**
     * How much lift a fully buoyant body gets per unit of submersion, as a multiple of its own weight
     * (2026-07-16 — "la lana ya no tiene flotabilidad suficiente para quedar encima del agua si no que se
     * hunde 1 a 2 bloques").
     *
     * <p>A body settles where lift cancels weight, i.e. at {@code submerged = 1 / (GAIN x (B - D))} for
     * fluid buoyancy {@code B} and floatability {@code D}. At {@code GAIN = 1} wool ({@code D = 0}) in
     * water ({@code B = 1}) balances only at {@code submerged = 1} — a lift of exactly 1x its weight
     * merely CANCELS gravity, which is neutral buoyancy, not flotation. Neutral buoyancy has no restoring
     * force, so wool sank until it was fully under and then simply hung there. Floating requires net lift
     * while fully submerged, so that rising until it is only PARTLY submerged is what brings it back into
     * balance — which is exactly what a gain above 1 buys.
     *
     * <p>{@code 2.0} means a fully buoyant body rides half out of the water, and it keeps every case
     * {@link dev.arubik.craftengine.contraption.physics.FloatabilityTable} documents intact:
     * <pre>
     *   wool      D=0    in water B=1  -> settles at 1/2 submerged   (floats, half out)
     *   stone     D=1    in water B=1  -> B-D=0, full gravity        (sinks — the table's stated meaning of 1.0)
     *   stone     D=1    in lava  B=2  -> settles at 1/2 submerged   (rides on lava)
     *   ice/glass D=0.8  in water B=1  -> no solution; 0.6x gravity  (sinks, but lazily — as documented)
     *   netherite D=2.5  in lava  B=2  -> B-D&lt;0, faster than gravity (sinks through both)
     * </pre>
     */
    public static final double BUOYANCY_GAIN = 2.0;

    public static final double WATER_DRAG = 0.8;

    /** Lava is viscous — things dropped in it slow to a crawl rather than splashing about. */
    public static final double LAVA_DRAG = 3.0;

    /** Below these, a body is a candidate for sleeping. */
    public static final double REST_LINEAR_EPSILON = 0.001;
    public static final double REST_ANGULAR_EPSILON = 0.001;

    /**
     * Consecutive ticks under the rest thresholds before a body sleeps. Sleeping is not just an
     * optimisation: it is what stops a settled body from accumulating floating-point noise into a slow
     * drift across the world.
     */
    public static final int SLEEP_TICKS = 20;

    /**
     * Advances every body by one game tick.
     *
     * @param bodies all bodies to simulate. Bodies in the same list collide with each other, so pass
     *        every contraption in a world together for contraption-vs-contraption to work.
     * @param dt tick length in ticks — normally {@code 1.0}
     */
    public static void step(List<PhysBody> bodies, double dt) {
        List<PhysBody> active = new ArrayList<>(bodies.size());
        for (PhysBody b : bodies) {
            b.resetImpact();
            if (!b.kinematic && !b.body.isStatic() && !b.shape.isEmpty()) {
                active.add(b);
            }
        }
        if (active.isEmpty()) {
            return;
        }
        // A Contact names RigidBodies, not PhysBodies, and impacts are recorded on the PhysBody — so the
        // link back is built once per step. Identity, because RigidBody does not override equals.
        Map<RigidBody, PhysBody> owners = new IdentityHashMap<>(active.size() * 2);
        for (PhysBody b : active) {
            owners.put(b.body, b);
        }
        double h = dt / SUB_STEPS;
        List<Contact> contacts = new ArrayList<>();
        for (int s = 0; s < SUB_STEPS; s++) {
            for (PhysBody b : active) {
                if (b.isAsleep()) {
                    continue;
                }
                integrate(b, h);
            }
            contacts.clear();
            generateContacts(active, contacts);
            recordImpacts(contacts, owners);
            // The normal impulse each contact accumulates across the position iterations — this is the
            // SUSTAINED contact force (weight, per substep), and it is what friction is capped against so a
            // resting body actually resists sliding. Without it friction was proportional to the velocity
            // solve's normal impulse, which is ~0 for a body already at rest, so sliding friction did
            // essentially nothing (2026-07-17 — "falta friccion").
            double[] normalImpulse = new double[contacts.size()];
            // The closing speed of each contact BEFORE the position solve absorbs it — captured now, while
            // the velocities are still the freshly integrated (pre-solve) values. Restitution needs this:
            // XPBD reads velocity back out of the position correction (see recoverVelocities), so by the time
            // solveVelocities runs the approach velocity has already been resolved to ~0 and a bounce computed
            // from it would be nearly nothing. This is the pre-solve relative normal velocity the restitution
            // target is sized from (Müller et al. 2020, §3.5).
            double[] approachSpeed = new double[contacts.size()];
            for (int i = 0; i < contacts.size(); i++) {
                approachSpeed[i] = approachNormalSpeed(contacts.get(i));
            }
            for (int i = 0; i < POSITION_ITERATIONS; i++) {
                solvePositions(contacts, h, normalImpulse);
            }
            for (PhysBody b : active) {
                if (b.isAsleep()) {
                    continue;
                }
                recoverVelocities(b, h);
            }
            solveVelocities(contacts, normalImpulse, approachSpeed);
        }
        for (PhysBody b : active) {
            finishTick(b, dt);
        }
    }

    /** Semi-implicit Euler: velocity first, then position from the NEW velocity. */
    private static void integrate(PhysBody b, double h) {
        RigidBody body = b.body;
        body.linearVelocity.y += GRAVITY * h;
        applyBuoyancy(b, h);
        b.previousPosition.set(body.position);
        body.position.fma(h, body.linearVelocity);
        b.previousOrientation.set(body.orientation);
        body.integrateOrientation(h);
    }

    /**
     * Pushes up on whatever part of the body is underwater (or under lava), box by box.
     *
     * <h2>Why this is per box and not per body</h2>
     * Buoyancy applied at the center of mass could only ever make a body rise or sink as a unit. Real
     * flotation is an argument between gravity pulling down at the center of MASS and the fluid pushing
     * up at the center of the displaced VOLUME, and everything interesting happens because those two
     * points are not the same. Applying each box's lift at its own position means the disagreement
     * resolves itself as torque: a raft with an anvil on one end lists toward the anvil, a top-heavy
     * hull rolls over, and a barge that dips one corner is pushed back level. None of that is written
     * anywhere — it is the same {@code r × p} that makes an unbalanced body tip on land.
     *
     * <p>Archimedes gives the whole rule: lift equals the WEIGHT of the fluid displaced,
     * {@code density × volume × g}. So a box floats when the fluid it displaces outweighs it, which is
     * why {@link WorldBlockCache#WATER_DENSITY} alone decides that planks float and iron does not — no
     * per-block float flag, no special cases.
     *
     * <p>Partial submersion matters: a box straddling the surface displaces only the fraction actually
     * under it. Without that a floating body would oscillate forever, snapping between full lift and
     * none as its centre crossed the waterline.
     */
    private static void applyBuoyancy(PhysBody b, double h) {
        if (b.world.hasNoFluid() || b.shape.isEmpty()) {
            return;
        }
        RigidBody body = b.body;
        double scale = body.scale();
        double scale3 = scale * scale * scale;
        // The lift is shared across the body's boxes by volume, so a half-submerged hull gets half the
        // lift — which is what holds a floating body AT the surface instead of bobbing through it.
        double bodyMass = body.inverseMass() <= 0.0 ? 0.0 : 1.0 / body.inverseMass();
        double totalVolume = 0.0;
        for (net.minecraft.world.phys.AABB box : b.shape.boxes()) {
            totalVolume += (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
        }
        for (net.minecraft.world.phys.AABB box : b.shape.boxes()) {
            Vector3d localCentre = new Vector3d(
                    (box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5);
            Vector3d at = body.toWorld(localCentre, new Vector3d());
            WorldBlockCache.Fluid fluid = b.world.fluidAt(
                    (int) Math.floor(at.x), (int) Math.floor(at.y), (int) Math.floor(at.z));
            if (fluid == null) {
                continue;
            }
            double height = (box.maxY - box.minY) * scale;
            double submerged = submergedFraction(at.y, height, fluid.topY());
            if (submerged <= 0.0) {
                continue;
            }
            double volume = (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
            // lift = (fluidBuoyancy - floatability) x thisBox'sWeight x g, upward.
            //
            // Buoyancy is decided by FLOATABILITY, not by mass. Mass appears only as the weight the lift
            // is measured in — which is what makes the result mass-independent in the right way: the lift
            // is a multiple of the body's OWN weight, whether it is a raft or a battleship. Sizing the
            // lift from mass instead (the old density-x-volume Archimedes form) meant mass silently
            // decided flotation, so a heavy block could never be made to float. See FloatabilityModel.
            //
            // Summed over the boxes this is  net = -g + GAIN x (B - D) x submerged x g,  so a body settles
            // where the lift matches its weight: submerged = 1 / (GAIN x (B - D)). See BUOYANCY_GAIN for
            // why the gain is not 1 — without it the only solution for wool is submerged = 1, i.e. resting
            // fully underwater rather than floating on it.
            double weightShare = totalVolume <= 0.0 ? 0.0 : bodyMass * (volume / totalVolume);
            double lift = BUOYANCY_GAIN * (fluid.buoyancy() - b.floatability) * weightShare * submerged * -GRAVITY;
            Vector3d impulse = new Vector3d(0.0, lift * h, 0.0);
            Vector3d r = new Vector3d(at).sub(body.position);

            body.linearVelocity.fma(body.inverseMass(), impulse);
            Vector3d torque = new Vector3d(r).cross(impulse);
            body.angularVelocity.add(new Matrix3d(body.inverseInertiaWorld()).transform(torque));

            // Fluid drag, also at this box's own position, and measured against the fluid's OWN
            // velocity rather than against the world.
            //
            // That difference is the entire current: drag pulls a box toward whatever the fluid around
            // it is doing, so a body sitting in still water is dragged toward zero (it settles), while
            // the same body in a river is dragged toward the river's velocity — it accelerates
            // downstream until it matches the flow, then stops accelerating on its own. No separate
            // push force, and no terminal-speed clamp: the drag already is both.
            //
            // Per box, so a raft with one end in the fast channel and one end in an eddy yaws, exactly
            // as buoyancy makes a lopsided raft list. Drag is also what stops a floating body being a
            // spring that bobs forever with nothing to take the energy out.
            Vector3d pointVelocity = body.velocityAt(r, new Vector3d());
            Vector3d relative = pointVelocity.sub(fluid.flow().x, fluid.flow().y, fluid.flow().z);
            double drag = (fluid.lava() ? LAVA_DRAG : WATER_DRAG) * submerged * h;
            Vector3d resist = new Vector3d(relative).mul(-drag * volume);
            body.linearVelocity.fma(body.inverseMass(), resist);
            body.angularVelocity.add(new Matrix3d(body.inverseInertiaWorld())
                    .transform(new Vector3d(r).cross(resist)));
        }
    }

    /**
     * How much of a box of {@code height}, centred at {@code centreY}, sits below {@code surfaceY} —
     * clamped to {@code [0,1]}.
     *
     * <p>Approximated against the box's vertical span only, ignoring how the body is rotated. A tilted
     * box's true submerged volume is a clipped polyhedron; the error is a fraction of one block's lift
     * on one box, which the surrounding solve absorbs, and it is not worth the polygon clipping.
     */
    private static double submergedFraction(double centreY, double height, double surfaceY) {
        if (height <= 1.0E-9) {
            return centreY <= surfaceY ? 1.0 : 0.0;
        }
        double bottom = centreY - height * 0.5;
        double depth = surfaceY - bottom;
        return Math.max(0.0, Math.min(1.0, depth / height));
    }

    /** World contacts for every body, plus body-vs-body for every unordered pair, sampled both ways. */
    private static void generateContacts(List<PhysBody> active, List<Contact> out) {
        for (PhysBody b : active) {
            if (b.isAsleep()) {
                continue;
            }
            ContactGenerator.worldContacts(b.body, b.shape, b.world, out);
        }
        for (int i = 0; i < active.size(); i++) {
            for (int j = i + 1; j < active.size(); j++) {
                PhysBody a = active.get(i);
                PhysBody c = active.get(j);
                if (a.isAsleep() && c.isAsleep()) {
                    continue;
                }
                // Sampled in both directions: a sparse cloud on one body can miss a thin protrusion
                // on the other, and a one-sided manifold pushes only one of the pair.
                ContactGenerator.bodyContacts(a.body, a.shape, c.body, c.shape, out);
                ContactGenerator.bodyContacts(c.body, c.shape, a.body, a.shape, out);
            }
        }
    }

    /**
     * Notes the closing speed of every contact onto the bodies involved — the one piece of information a
     * caller needs to know a crash happened, recorded as a plain number so this file still touches no
     * level, no Bukkit handle, and nothing outside {@link PhysBody}. Acting on it (an explosion, a sound,
     * a break) belongs to whoever reads it after the step, on the main thread.
     *
     * <h2>Why here and not in {@link #solveVelocities}</h2>
     * {@code solveVelocities} also has {@code vn} in hand, but by then it is no longer the impact speed.
     * The substep order is integrate → <b>solve positions</b> → recover velocities → solve velocities,
     * and {@link #recoverVelocities} re-derives velocity from how far the position solve actually moved
     * the body — which {@link #MAX_DEPENETRATION_PER_SUBSTEP} caps. A body falling at {@code 0.75} enters
     * the ground by {@code 0.094} in a substep and is pushed back out by at most {@code 0.05}, so the
     * velocity {@code solveVelocities} sees is a fraction of the real one, and the harder the hit the more
     * of it the clamp hides — exactly backwards for a threshold on impact severity.
     *
     * <p>Called immediately after {@link #generateContacts}, none of that has happened yet: {@code integrate}
     * only moved the body, so the velocities are still the approach velocities, and the manifold that just
     * came into existence is the frame of first penetration. That is the moment the impact speed is on the
     * table, and it is read once, before anything bleeds it off.
     *
     * <p>A body at rest is not a false positive. Its contacts persist every substep, but its velocity has
     * already been solved to zero and one substep of gravity restores only {@code GRAVITY·h ≈ 0.005}
     * blocks/tick of closing speed — orders of magnitude below any threshold worth having.
     */
    private static void recordImpacts(List<Contact> contacts, Map<RigidBody, PhysBody> owners) {
        for (Contact c : contacts) {
            Contact.Evaluation eval = c.evaluate();
            if (eval.depth() < -SPECULATIVE_DISTANCE) {
                continue; // speculative contact still out of range — nothing is converging on anything
            }
            RigidBody a = c.bodyA();
            RigidBody b = c.bodyB();
            Vector3d rA = Contact.rA(a, eval.point());
            Vector3d rB = b == null ? null : new Vector3d(eval.point()).sub(b.position);
            double vn = relativeVelocity(a, b, rA, rB).dot(eval.worldNormal());
            if (vn >= 0.0) {
                continue; // separating
            }
            // Closing speed is a property of the PAIR, so both sides of a contraption-vs-contraption hit
            // record the same number: ramming and being rammed are the same collision.
            double closing = -vn;
            PhysBody ownerA = owners.get(a);
            if (ownerA != null) {
                ownerA.recordImpact(closing, eval.point());
            }
            PhysBody ownerB = b == null ? null : owners.get(b);
            if (ownerB != null) {
                ownerB.recordImpact(closing, eval.point());
            }
        }
    }

    /**
     * The XPBD position solve. For each contact, with {@code α = 0} (perfectly rigid, no compliance):
     *
     * <pre>
     *   w  = 1/m + (r × n)ᵀ · I⁻¹ · (r × n)      generalized inverse mass along n at r
     *   Δλ = depth / (w₁ + w₂)
     *   p  = Δλ · n
     *   x += p/m ;  q += ½·quat(I⁻¹(r × p), 0)·q
     * </pre>
     *
     * The {@code (r × n)ᵀ I⁻¹ (r × n)} term is the whole point: it says how much of a push at this
     * lever arm turns into rotation instead of translation. Push a body near its COM and it mostly
     * slides; push it at a far corner and it mostly spins.
     */
    private static void solvePositions(List<Contact> contacts, double h, double[] normalImpulse) {
        for (int idx = 0; idx < contacts.size(); idx++) {
            Contact c = contacts.get(idx);
            // Re-evaluated against the CURRENT transform, never a cached depth: a resting body has
            // dozens of simultaneous ground contacts, and if each applied a correction sized from its
            // own stale measurement — blind to the fact that an earlier one already lifted the body
            // clear — the body would be pushed out dozens of times over and launched.
            Contact.Evaluation eval = c.evaluate();
            double depth = Math.min(eval.depth(), MAX_DEPENETRATION_PER_SUBSTEP);
            if (depth <= 0.0) {
                continue; // already satisfied by an earlier contact in this manifold
            }
            RigidBody a = c.bodyA();
            RigidBody b = c.bodyB();
            Vector3d n = eval.worldNormal();
            Vector3d rA = Contact.rA(a, eval.point());
            Vector3d rB = b == null ? null : new Vector3d(eval.point()).sub(b.position);
            double wA = generalizedInverseMass(a, rA, n);
            double wB = b == null ? 0.0 : generalizedInverseMass(b, rB, n);
            double wSum = wA + wB;
            if (wSum <= 1.0E-12) {
                continue;
            }
            double lambda = depth / wSum;
            normalImpulse[idx] += lambda; // accumulate the sustained contact force for friction (see solveVelocities)
            Vector3d p = new Vector3d(n).mul(lambda);
            applyPositionalImpulse(a, rA, p, 1.0);
            if (b != null) {
                applyPositionalImpulse(b, rB, p, -1.0);
            }
        }
    }

    /** {@code w = 1/m + (r × n)ᵀ · I⁻¹_world · (r × n)}. */
    private static double generalizedInverseMass(RigidBody body, Vector3d r, Vector3d n) {
        Vector3d rn = new Vector3d(r).cross(n);
        Matrix3d invI = new Matrix3d(body.inverseInertiaWorld());
        Vector3d transformed = invI.transform(new Vector3d(rn));
        return body.inverseMass() + rn.dot(transformed);
    }

    /** Applies {@code sign·p} at {@code r}: translates by {@code p/m} and rotates by {@code I⁻¹(r × p)}. */
    private static void applyPositionalImpulse(RigidBody body, Vector3d r, Vector3d p, double sign) {
        double invMass = body.inverseMass();
        body.position.x += p.x * invMass * sign;
        body.position.y += p.y * invMass * sign;
        body.position.z += p.z * invMass * sign;

        Vector3d torque = new Vector3d(r).cross(new Vector3d(p).mul(sign));
        Matrix3d invI = new Matrix3d(body.inverseInertiaWorld());
        Vector3d dOmega = invI.transform(torque);
        // q += ½·(dOmega, 0)·q, then renormalize — the same first-order quaternion update the
        // integrator uses, applied as a position-level correction.
        org.joml.Quaterniond dq = new org.joml.Quaterniond(dOmega.x * 0.5, dOmega.y * 0.5, dOmega.z * 0.5, 0.0)
                .mul(body.orientation);
        body.orientation.x += dq.x;
        body.orientation.y += dq.y;
        body.orientation.z += dq.z;
        body.orientation.w += dq.w;
        body.orientation.normalize();
    }

    /**
     * How much velocity DEPENETRATION alone may inject per substep, linear (blocks/tick) and angular
     * (rad/tick). This is the whole fix for "varios phys block cerca ... colisionan tanto que terminan
     * saliendo volando" (2026-07-17).
     *
     * <p>XPBD reads velocity back from how far the position solve moved a body. That is exactly right for
     * real motion (gravity, buoyancy, a blast), but a body wedged between several neighbours gets shoved
     * OUT by the overlap-resolution, and that shove — pure position correction, not momentum — is read back
     * as a separating velocity the velocity pass never damps (it only kills APPROACHING velocity). With a
     * dense pile the shoves compound and the body launches. Capping only the correction part leaves genuine
     * momentum untouched (so an explosion still flings blocks and free-fall is unchanged) while a pile can
     * no longer pump itself apart — it just firmly separates and settles.
     */
    public static final double MAX_DEPEN_LINEAR = 1.5;
    public static final double MAX_DEPEN_ANGULAR = 1.5;

    /**
     * Velocities are READ BACK from how far the position solve actually moved the body — but the part of
     * that motion which came from DEPENETRATION (not from the integrated momentum) is capped, so a wedged
     * body cannot be launched. See {@link #MAX_DEPEN_LINEAR}.
     *
     * <p>At recovery time {@code body.linearVelocity}/{@code angularVelocity} still hold the INTEGRATED
     * values (gravity/buoyancy/blast applied in {@link #integrate}; the position solve touches only
     * position/orientation, never velocity), so they are exactly the pre-solve momentum. The recovered
     * value minus that momentum is the depenetration contribution, which is what gets clamped.
     */
    private static void recoverVelocities(PhysBody b, double h) {
        RigidBody body = b.body;

        Vector3d preLinear = new Vector3d(body.linearVelocity);
        Vector3d recovered = new Vector3d(body.position).sub(b.previousPosition).div(h);
        Vector3d depenLinear = recovered.sub(preLinear); // recovered mutated into the correction part
        double dl = depenLinear.length();
        if (dl > MAX_DEPEN_LINEAR) {
            depenLinear.mul(MAX_DEPEN_LINEAR / dl);
        }
        body.linearVelocity.set(preLinear).add(depenLinear);

        Vector3d preAngular = new Vector3d(body.angularVelocity);
        body.recoverAngularVelocity(new org.joml.Quaterniond(b.previousOrientation), h); // sets angularVelocity
        Vector3d depenAngular = new Vector3d(body.angularVelocity).sub(preAngular);
        double da = depenAngular.length();
        if (da > MAX_DEPEN_ANGULAR) {
            depenAngular.mul(MAX_DEPEN_ANGULAR / da);
        }
        body.angularVelocity.set(preAngular).add(depenAngular);
    }

    /**
     * The velocity pass: kill approaching normal velocity (plus restitution) and apply Coulomb
     * friction, both as impulses at the contact's lever arm — so friction on a far corner also
     * produces torque, which is what lets a body topple rather than skid.
     */
    /** The relative normal velocity of a contact right now (negative = the two surfaces are approaching). */
    private static double approachNormalSpeed(Contact c) {
        Contact.Evaluation eval = c.evaluate();
        if (eval.depth() < -SPECULATIVE_DISTANCE) {
            return 0.0;
        }
        RigidBody a = c.bodyA();
        RigidBody b = c.bodyB();
        Vector3d rA = Contact.rA(a, eval.point());
        Vector3d rB = b == null ? null : new Vector3d(eval.point()).sub(b.position);
        return relativeVelocity(a, b, rA, rB).dot(eval.worldNormal());
    }

    private static void solveVelocities(List<Contact> contacts, double[] normalImpulse, double[] approachSpeed) {
        for (int idx = 0; idx < contacts.size(); idx++) {
            Contact c = contacts.get(idx);
            RigidBody a = c.bodyA();
            RigidBody b = c.bodyB();
            Contact.Evaluation eval = c.evaluate();
            if (eval.depth() < -SPECULATIVE_DISTANCE) {
                continue; // never came into range this substep — nothing is touching
            }
            Vector3d n = eval.worldNormal();
            Vector3d rA = Contact.rA(a, eval.point());
            Vector3d rB = b == null ? null : new Vector3d(eval.point()).sub(b.position);
            Vector3d relative = relativeVelocity(a, b, rA, rB);
            double vn = relative.dot(n);
            double wA = generalizedInverseMass(a, rA, n);
            double wB = b == null ? 0.0 : generalizedInverseMass(b, rB, n);
            double wSum = wA + wB;
            if (wSum <= 1.0E-12) {
                continue;
            }
            // Normal impulse only when APPROACHING — a separating/resting contact needs none. But do NOT
            // skip the whole contact when vn >= 0 (the old bug): a body sliding along a surface it already
            // rests on has vn ~ 0, and it still has to be braked by friction. Friction below runs regardless,
            // capped by the SUSTAINED normal impulse the position solve accumulated.
            // Per-contact restitution — the material bounce, resolved CELL-LOCALLY: it is the bounciness of the
            // CELL at this contact point (per body), not a body average, so a slime cell bounces where it lands
            // while a stone cell beside it does not (2026-07-17 — "bounciness ... cell prefered"). Combined by
            // taking the BOUNCIER surface (standard restitution-combine). A bare-terrain contact (b == null)
            // has only this body's own cell. The bounce TARGET is sized from the PRE-solve closing speed
            // (approachSpeed) — not the current normal velocity, which the position solve has already resolved
            // to ~0 (see the substep loop). Below RESTITUTION_MIN_SPEED a contact is settling, not an impact,
            // so the target stays 0 and a body on slime comes to rest instead of jittering forever.
            double e = a.restitutionAt(eval.point());
            if (b != null) {
                e = Math.max(e, b.restitutionAt(eval.point()));
            }
            double vnPrev = approachSpeed[idx];
            double target = (e > 0.0 && vnPrev < -RESTITUTION_MIN_SPEED) ? -e * vnPrev : 0.0;
            // Bring the normal velocity UP to the target: for e == 0 (or a gentle contact) target is 0, so this
            // is the ordinary dead-stop of an approaching/penetrating contact (vn < 0); for a real bounce the
            // target is a positive separating speed and jn adds the rebound on top of the dead stop. Never a
            // negative impulse — a contact already separating faster than the target is left alone.
            double jn = 0.0;
            if (vn < target) {
                jn = (target - vn) / wSum;
                applyVelocityImpulse(a, rA, new Vector3d(n).mul(jn), 1.0);
                if (b != null) {
                    applyVelocityImpulse(b, rB, new Vector3d(n).mul(jn), -1.0);
                }
            }

            Vector3d post = relativeVelocity(a, b, rA, rB);
            Vector3d tangent = new Vector3d(post).sub(new Vector3d(n).mul(post.dot(n)));
            double tangentSpeed = tangent.length();
            if (tangentSpeed <= 1.0E-9) {
                continue;
            }
            tangent.div(tangentSpeed);
            double wtA = generalizedInverseMass(a, rA, tangent);
            double wtB = b == null ? 0.0 : generalizedInverseMass(b, rB, tangent);
            double wtSum = wtA + wtB;
            if (wtSum <= 1.0E-12) {
                continue;
            }
            // Coulomb: the friction impulse can never exceed μ·|normal impulse|, which is what makes
            // a body slide once pushed hard enough sideways rather than being glued in place. μ is now
            // per-contact — the GEOMETRIC MEAN of the two surfaces' friction (a's mass-weighted mean and
            // b's, or a's again against terrain, since a bare-terrain contact has no b) — so an icy raft
            // slides on stone AND a stone raft slides on ice, neither surface alone deciding it. Falls back
            // to the global FRICTION only if a body never had its material set.
            double muA = a.friction();
            double muB = b == null ? muA : b.friction();
            double mu = Math.sqrt(Math.max(0.0, muA) * Math.max(0.0, muB));
            // Cap against the SUSTAINED normal impulse (accumulated by the position solve — the weight the
            // contact holds up every substep) plus this substep's impact impulse, so friction bites on a
            // resting/sliding body, not only during a hard landing.
            double normalForce = normalImpulse[idx] + Math.abs(jn);
            double jt = Math.max(-tangentSpeed / wtSum, -mu * normalForce);
            applyVelocityImpulse(a, rA, new Vector3d(tangent).mul(jt), 1.0);
            if (b != null) {
                applyVelocityImpulse(b, rB, new Vector3d(tangent).mul(jt), -1.0);
            }
        }
    }

    private static Vector3d relativeVelocity(RigidBody a, RigidBody b, Vector3d rA, Vector3d rB) {
        Vector3d va = a.velocityAt(rA, new Vector3d());
        if (b == null) {
            return va; // the world is immovable — its contact point velocity is zero
        }
        return va.sub(b.velocityAt(rB, new Vector3d()));
    }

    private static void applyVelocityImpulse(RigidBody body, Vector3d r, Vector3d impulse, double sign) {
        double invMass = body.inverseMass();
        body.linearVelocity.x += impulse.x * invMass * sign;
        body.linearVelocity.y += impulse.y * invMass * sign;
        body.linearVelocity.z += impulse.z * invMass * sign;
        Vector3d torque = new Vector3d(r).cross(new Vector3d(impulse).mul(sign));
        Matrix3d invI = new Matrix3d(body.inverseInertiaWorld());
        body.angularVelocity.add(invI.transform(torque));
    }

    /** Applies damping and updates the sleep counter once per game tick. */
    private static void finishTick(PhysBody b, double dt) {
        RigidBody body = b.body;
        body.linearVelocity.mul(Math.max(0.0, 1.0 - LINEAR_DAMPING * dt));
        body.angularVelocity.mul(Math.max(0.0, 1.0 - ANGULAR_DAMPING * dt));
        // Angular NOISE GATE (2026-07-18 — "4 fans simétricos hacia abajo y se voltea ... solo ejecutar los
        // movimientos reales, no el ruido"). A balanced fan array's torques cancel to ~0 in theory, but
        // floating-point residue leaks a hair of spin every tick that, unchecked, integrates into a slow flip.
        // Below this threshold the spin is NOT a real movement, it is that residue, so it is zeroed — a genuine
        // off-centre thrust produces spin well above it and still flips the body. This is what stops a symmetric
        // setup drifting over "porque sí" while leaving a deliberate flip intact.
        double spin = body.angularVelocity.length();
        if (spin < ANGULAR_NOISE_EPS) {
            body.angularVelocity.zero();
        } else if (spin > MAX_ANGULAR_VELOCITY) {
            // Spin clamp: a sustained off-centre thrust still flips the body, but capped so a single numerical
            // spike can't snap it into a chaotic tumble.
            body.angularVelocity.mul(MAX_ANGULAR_VELOCITY / spin);
        }
        boolean resting = body.linearVelocity.length() < REST_LINEAR_EPSILON
                && body.angularVelocity.length() < REST_ANGULAR_EPSILON;
        if (resting) {
            b.restTicks++;
            if (b.isAsleep()) {
                // Zero rather than leave dust in the velocities, or a sleeping body wakes with a
                // stale nudge and drifts.
                body.linearVelocity.zero();
                body.angularVelocity.zero();
            }
        } else {
            b.restTicks = 0;
        }
    }
}
