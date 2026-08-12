package dev.arubik.craftengine.contraption.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.craftbukkit.CraftWorld;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;

/**
 * Drives the physics solve for every PhysContraption, once per game tick, grouped by world.
 *
 * <h2>Why this is a world-level driver and not a per-behavior tick</h2>
 * Contraption-vs-contraption collision requires every body in a world to be solved in ONE
 * {@link XpbdSolver#step} call: two bodies resolving against each other independently would each
 * push off a stale copy of the other and inject energy. So this collects the bodies, bakes their
 * terrain, steps them together, and writes the results back — rather than each
 * {@link PhysicsBehavior} solving itself in isolation.
 *
 * <h2>The body is the source of truth</h2>
 * {@link RigidBody} holds a quaternion and a COM-origin position; {@link ContraptionState} holds
 * Euler angles and a bearing-origin position. Round-tripping through Euler every tick would be lossy
 * near gimbal lock, so the body is authoritative and the state is written from it. The reverse sync
 * happens only when something OUTSIDE physics has moved the contraption — the wand, a teleport, an
 * assembly. That is detected rather than announced: if the state's transform no longer matches what
 * was last written, someone else changed it, and the body resyncs. This is self-healing, so no
 * external mutator needs to remember to notify us.
 */
public final class PhysicsWorld {

    private PhysicsWorld() {
    }

    /**
     * How far past the body's own bounds to bake terrain. Must cover one tick of motion plus the
     * speculative margin, or a fast body could move into a block that was never baked and tunnel.
     */
    private static final double BAKE_MARGIN = 2.0;

    /** Transform divergence beyond this means an external mutator moved the contraption — resync. */
    private static final double RESYNC_EPSILON = 1.0E-6;

    /** Max local COM move (blocks) still handled by the SMOOTH position shift; beyond it (a split/fracture) the
     *  body is re-anchored to its state bearing instead, so a fragment can't be flung by a huge COM jump. */
    private static final double COM_SMOOTH_SHIFT_MAX = 1.0;

    private static final Map<UUID, Entry> ENTRIES = new HashMap<>();

    /**
     * Contraptions pinned kinematic by something OUTSIDE the stall system — today the creative phys wand
     * holding one in the air (2026-07-17 — "cuando uso una creative phys wand ... el contraption aun tiene
     * gravedad"). Needed because {@code sync}/{@code syncMain} recompute {@link PhysBody#kinematic} from
     * {@code state.isStalled()} EVERY tick, which silently clobbered a flag the wand set directly a moment
     * earlier — so gravity resumed on the very next pump. The kinematic flag is now
     * {@code isStalled() || isHeld(id)}, so a wand grab survives the sync. Concurrent because the wand
     * (game thread) writes it while the physics thread's commands read the resulting flag.
     */
    private static final java.util.Set<UUID> HELD = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /** Pins/unpins a contraption kinematic independently of the stall system. See {@link #HELD}. */
    public static void setHeld(UUID contraptionId, boolean held) {
        if (contraptionId == null) {
            return;
        }
        if (held) {
            HELD.add(contraptionId);
        } else {
            HELD.remove(contraptionId);
        }
    }

    public static boolean isHeld(UUID contraptionId) {
        return HELD.contains(contraptionId);
    }

    /** One tracked contraption: its body, plus the bookkeeping needed to detect external moves. */
    private static final class Entry {
        final PhysBody physBody = new PhysBody();
        MassModel massModel = MassModel.EMPTY;
        /** Hash of the cell SET last observed — see the shape check in {@link PhysicsWorld#sync}. */
        int lastShapeHash;
        /**
         * Whether {@link #lastShapeHash} is primed. A flag, not a sentinel value: an empty set hashes to 0
         * and any set could hash to 0 by coincidence, so a magic number would mean "never rebuild this body".
         */
        boolean shapeObserved;
        double lastScale = Double.NaN;
        /** The state transform this driver last wrote — compared against to detect foreign mutation. */
        Vec3 lastWrittenPosition;
        double lastWrittenYaw, lastWrittenPitch, lastWrittenRoll;
        boolean initialized;

        /**
         * The body's transform as last PUBLISHED by the physics thread, for the main thread to read
         * (2026-07-17 — "el async debe ser quien corre las fisicas ... no lockear el main thread"). The
         * physics thread owns {@link PhysBody#body} and mutates it inside the step; the main thread must
         * never read that field directly (it would tear against a running step), so after each step the
         * physics thread copies the transform here and the main thread's writeBack/rebake/detonation read
         * only this. {@code null} until the first step publishes.
         */
        volatile BodyTransform published;

        /**
         * The terrain region the main thread last BAKED for this body — its own mirror, since the real
         * {@link PhysBody#bakedRegion} is installed on the physics thread (via a command) a step later.
         * The main thread decides when to re-bake off this mirror plus the published position, so it never
         * touches physics-owned fields to make the decision.
         */
        net.minecraft.world.phys.AABB mainBakedRegion;
    }

    /**
     * An immutable snapshot of a body's post-step transform, handed from the physics thread to the main
     * thread. Immutable so the main thread always reads a consistent pose, never a half-updated one.
     */
    private record BodyTransform(Vector3d position, Quaterniond orientation, Vector3d linearVelocity,
            double maxImpactSpeed, Vector3d impactPoint) {
    }

    // ===== Async physics thread (free-running, decoupled from the game tick) =====

    /**
     * Master switch for off-thread physics. When true, the solve runs on {@link #PHYS_THREAD} and the game
     * thread only feeds terrain snapshots and reads results — it never blocks on the solve, which is what a
     * TNT blast waking a dozen bodies used to do. When false, {@link #stepAllSync} runs the solve inline on
     * the game thread (the original path), kept as a one-line fallback if the async path ever misbehaves.
     */
    private static volatile boolean ASYNC = true;

    /** Guards the brief handoff between game thread and physics thread. The heavy step runs OUTSIDE it. */
    private static final java.util.concurrent.locks.ReentrantLock LOCK = new java.util.concurrent.locks.ReentrantLock();

    /**
     * Mutations from the game thread to apply on the physics thread at a step boundary — impulses, wakes,
     * mass/shape/scale changes, transform adoptions, terrain-snapshot swaps, adds and removes. Everything
     * that writes {@link PhysBody#body}/{@link PhysBody#world} goes through here so only the physics thread
     * ever writes them, which is what makes the lock-free step safe.
     */
    private static final java.util.Queue<Runnable> COMMANDS = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** The active bodies grouped by world, republished by the game thread every tick for the thread to solve. */
    private static volatile Map<ResourceKey<Level>, List<PhysBody>> ACTIVE = Map.of();

    /** Presence-LOD radius: a body with no player this close is frozen (not solved) this tick — see the pumps. */
    private static final double PHYS_LOD_RANGE = 96.0;
    private static final double PHYS_LOD_RANGE_SQ = PHYS_LOD_RANGE * PHYS_LOD_RANGE;

    /**
     * Whether any player in {@code level} is within {@link #PHYS_LOD_RANGE} of the contraption — the presence
     * gate for the physics LOD (inspired by BlockShips' idle/unmanned tiers). Nobody near ⇒ nobody can see it
     * move, so freezing it wastes no fidelity; a driver or a passing player is within range, so a piloted or
     * observed contraption always simulates. Cheap: one squared-distance test per online player in the world.
     */
    private static boolean anyPlayerNear(ServerLevel level, ContraptionState state) {
        double x = state.x(), y = state.y(), z = state.z();
        for (net.minecraft.server.level.ServerPlayer p : level.players()) {
            double dx = p.getX() - x, dy = p.getY() - y, dz = p.getZ() - z;
            if (dx * dx + dy * dy + dz * dz <= PHYS_LOD_RANGE_SQ) {
                return true;
            }
        }
        return false;
    }

    private static volatile Thread PHYS_THREAD;
    private static volatile boolean RUNNING;

    /** Physics step period — matches the 20 TPS game tick (dt = 1.0), just on its own thread. */
    private static final long STEP_NANOS = 50_000_000L;

    /** The active bodies grouped by world as ENTRIES, so the thread can publish each one's transform. */
    private static volatile Map<ResourceKey<Level>, List<Entry>> ACTIVE_ENTRIES = Map.of();

    /** Starts the physics thread if async is on and it isn't already running. Idempotent; call on enable. */
    public static synchronized void startThread() {
        if (!ASYNC || RUNNING) {
            return;
        }
        RUNNING = true;
        Thread t = new Thread(PhysicsWorld::runPhysics, "cep-physics");
        t.setDaemon(true);
        t.setPriority(Thread.NORM_PRIORITY);
        PHYS_THREAD = t;
        t.start();
        org.bukkit.Bukkit.getLogger().info("[Contraption] async physics thread started (cep-physics)");
    }

    private static void runPhysics() {
        while (RUNNING) {
            long t0 = System.nanoTime();
            Map<ResourceKey<Level>, List<Entry>> active;
            LOCK.lock();
            try {
                Runnable c;
                while ((c = COMMANDS.poll()) != null) {
                    try {
                        c.run(); // owns body.body while the lock is held and the step is not running
                    } catch (Throwable ignored) {
                    }
                }
                active = ACTIVE_ENTRIES;
            } finally {
                LOCK.unlock();
            }

            // The step runs OUTSIDE the lock: the physics thread has exclusive ownership of every
            // PhysBody#body here (the game thread only reads published transforms and writes via COMMANDS),
            // so no lock is needed to protect it, and the game thread is never blocked by the solve.
            for (List<Entry> group : active.values()) {
                List<PhysBody> bodies = new ArrayList<>(group.size());
                for (Entry e : group) {
                    bodies.add(e.physBody);
                }
                try {
                    solveInParallel(bodies);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            // Publish each body's post-step transform for the game thread to read.
            LOCK.lock();
            try {
                for (List<Entry> group : active.values()) {
                    for (Entry e : group) {
                        RigidBody b = e.physBody.body;
                        Vector3d ip = e.physBody.impactPoint();
                        e.published = new BodyTransform(
                                new Vector3d(b.position), new Quaterniond(b.orientation),
                                new Vector3d(b.linearVelocity),
                                e.physBody.maxImpactSpeed(), ip == null ? null : new Vector3d(ip));
                    }
                }
            } finally {
                LOCK.unlock();
            }

            long sleep = STEP_NANOS - (System.nanoTime() - t0);
            if (sleep > 0) {
                java.util.concurrent.locks.LockSupport.parkNanos(sleep);
            }
        }
    }

    /** The body backing a contraption, or {@code null} if it is not a PhysContraption. */
    public static PhysBody bodyOf(UUID contraptionId) {
        Entry entry = ENTRIES.get(contraptionId);
        return entry == null ? null : entry.physBody;
    }

    /** Drops a contraption's body. Call on disassembly so a torn-down contraption does not leak. */
    public static void remove(UUID contraptionId) {
        ENTRIES.remove(contraptionId);
    }

    /**
     * The body for {@code state}, creating and priming it if this driver has not seen the
     * contraption yet — mass, shape, scale and transform are all populated on return.
     *
     * <p>Exists for {@link ContraptionSplitter}: a fragment spawned mid-tick must have its inherited
     * velocity written onto a body that already knows where its center of mass is, and waiting for
     * the next {@link #stepAll} to lazily build one would mean the fragment spent a tick with the
     * wrong velocity. Idempotent — a contraption already tracked here just gets re-synced.
     */
    public static PhysBody ensureBody(ContraptionState state) {
        Entry entry = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
        sync(entry, state);
        return entry.physBody;
    }

    /**
     * How far beyond a body's own bounds a world change still counts as relevant. One block covers
     * the ground directly under a resting body, which is the case that matters: that block is outside
     * the body's bounds, and removing it is exactly what should drop the body.
     */
    private static final double WAKE_MARGIN = 1.5;

    /**
     * Wakes every phys body near a world change.
     *
     * <h2>Why this exists</h2>
     * A settled body sleeps, and a sleeping body is skipped entirely by the solver — it re-bakes no
     * terrain and generates no contacts. That is what sleeping is FOR (it stops a resting body
     * accumulating floating-point noise into a slow drift), but it also means the body has no way to
     * notice that the ground it was resting on is gone: mine out the blocks underneath and it hangs
     * in mid-air forever, because nothing ever asks it to look again.
     *
     * <p>Sleeping is only sound if something wakes the body when the world it fell asleep against
     * changes. This is that something.
     */
    public static void wakeNear(ResourceKey<Level> worldId, double x, double y, double z) {
        for (Map.Entry<UUID, Entry> e : ENTRIES.entrySet()) {
            ContraptionEntity entity = ContraptionManager.get(e.getKey());
            if (entity == null || !entity.state().worldId().equals(worldId)) {
                continue;
            }
            Entry entry = e.getValue();
            PhysBody body = entry.physBody;
            if (body.shape.isEmpty()) {
                continue;
            }
            if (!ASYNC) {
                // Two separate radii: invalidate the (wider) baked snapshot if the change is inside it,
                // wake only if the change is within the (narrower) reach of the body itself.
                if (body.bakedRegion != null && body.bakedRegion.contains(x, y, z)) {
                    body.invalidateBake();
                }
                if (body.worldBounds(WAKE_MARGIN).contains(x, y, z)) {
                    body.wakeUp();
                }
                continue;
            }
            // Async: decide off the game-thread-owned mirror + published position, install on the body via
            // a command. Clearing mainBakedRegion here is what makes the game thread re-bake next tick.
            if (entry.mainBakedRegion != null && entry.mainBakedRegion.contains(x, y, z)) {
                entry.mainBakedRegion = null;
                enqueue(body::invalidateBake);
            }
            BodyTransform pub = entry.published;
            if (pub != null) {
                double r = body.shape.boundingRadius() * (entry.lastScale > 0 ? entry.lastScale : 1.0) + WAKE_MARGIN;
                if (Math.abs(pub.position().x - x) <= r && Math.abs(pub.position().y - y) <= r
                        && Math.abs(pub.position().z - z) <= r) {
                    enqueue(body::wakeUp);
                }
            }
        }
    }

    /**
     * Applies an explosion to every phys body in range, <b>per cell</b>.
     *
     * <h2>Why per cell and not per body</h2>
     * Vanilla explosions know nothing about our bodies — they are not real entities, so
     * {@code Explosion} never considers them and a blast passed through a phys contraption as if it
     * were not there.
     *
     * <p>The first fix for that was one impulse for the whole body, aimed at its center of mass and
     * sized by the center of mass's distance. That is a blast hitting a point mass: a 30-block barge
     * with TNT going off against one corner drifted away in a straight line, because the entire
     * structure was told the same thing about the same explosion.
     *
     * <p>A real explosion pushes on every exposed surface separately, and the near side is pushed far
     * harder than the far side. So each of the body's merged boxes gets its OWN impulse, from its OWN
     * distance and direction, applied at its OWN position. Nothing sums them explicitly — the net
     * force and the spin are simply what all those impulses add up to. That is what makes a blast
     * against one end kick that end up and rotate the body, and a blast underneath the middle launch
     * it flat.
     *
     * <p>Impulse, not velocity: dividing by mass is what makes a blast fling a light frame across the
     * map while barely nudging a heavy one, which is what people expect from TNT. Each box's share is
     * scaled by its volume, so a full block catches more of the blast than a slab.
     */
    public static void applyExplosion(ResourceKey<Level> worldId, double x, double y, double z, double power) {
        // EVERY contraption in the world is subject to the blast, not just phys bodies (minecart/ghast/linear
        // contraptions are contraptions too): each has its cells carved by resistance; only the phys ones ALSO
        // take a push impulse (the others are entity/block-driven and don't have a rigid body to push).
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState st = entity.state();
            if (!st.worldId().equals(worldId)) {
                continue;
            }
            Entry e = ENTRIES.get(st.id());
            if (e != null && e.physBody != null && !e.physBody.shape.isEmpty()) {
                PhysBody physBody = e.physBody;
                // The impulse math reads/writes the physics-owned RigidBody, so on the async path it runs as a
                // command on the physics thread; entity resolution above stays on the game thread.
                if (ASYNC) {
                    enqueue(() -> explodeBody(physBody, x, y, z, power));
                } else {
                    explodeBody(physBody, x, y, z, power);
                }
            }
            // Carve cells by resistance unless fully blast-immune (explosionProof == 1). Game thread here — it
            // touches the hidden level; shape/mass re-derive next syncMain.
            if (st.explosionProof() < 1.0) {
                ServerLevel real = resolveLevel(st);
                if (real != null) {
                    carveExplosion(st, x, y, z, power, real);
                }
            }
        }
    }

    /** Distance to which a blast of {@code power} reaches — matches {@link #explodeBody}'s own radius. */
    // (EXPLOSION_RADIUS_PER_POWER is defined near the impulse constants below.)

    /**
     * Removes the captured cells a real-world blast reaches, by explosion resistance. A cell inside the radius is
     * consumed when its block's {@code getExplosionResistance()} is below {@code power ·
     * SURVIVE_RESISTANCE_PER_POWER · falloff} (falloff = {@code 1 − d/radius}, so the core carves hardest and the
     * rim barely) — obsidian/netherite ride any blast out, stone/wood near the centre don't, exactly like a real
     * explosion. Each broken cell drops its item in the real world at its live position.
     */
    private static void carveExplosion(ContraptionState state, double x, double y, double z, double power,
            ServerLevel realLevel) {
        ContraptionLevel level = state.level();
        if (level == null) {
            return;
        }
        double radius = power * EXPLOSION_RADIUS_PER_POWER;
        Vec3 blast = new Vec3(x, y, z);
        // Break threshold, SCALED DOWN by (1 - explosionProof): a cell breaks when its resistance is under it, so
        // proof 0 leaves the full threshold (breaks normally), proof 1 makes it 0 (nothing breaks), and 0.5 halves
        // it (harder — fewer/tougher cells break). "Si está a la mitad se usa mates."
        double cutoffBase = power
                * dev.arubik.craftengine.contraption.explosive.ContraptionExplosives.SURVIVE_RESISTANCE_PER_POWER
                * (1.0 - state.explosionProof());
        java.util.List<net.minecraft.core.BlockPos> toBreak = new java.util.ArrayList<>();
        for (net.minecraft.core.BlockPos local : level.localPositions()) {
            Vec3 world = level.realWorldPositionOf(
                    new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
            double d = world.distanceTo(blast);
            if (d > radius) {
                continue;
            }
            net.minecraft.world.level.block.state.BlockState bs = level.getBlockState(local);
            if (bs.isAir()) {
                continue;
            }
            double falloff = 1.0 - d / radius;
            if (bs.getBlock().getExplosionResistance() < cutoffBase * falloff) {
                toBreak.add(local.immutable());
            }
        }
        if (toBreak.isEmpty()) {
            return;
        }
        int quiet = net.minecraft.world.level.block.Block.UPDATE_CLIENTS
                | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE;
        for (net.minecraft.core.BlockPos local : toBreak) {
            net.minecraft.world.level.block.state.BlockState bs = level.getBlockState(local);
            if (bs.isAir()) {
                continue;
            }
            Vec3 world = level.realWorldPositionOf(
                    new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5));
            net.minecraft.core.BlockPos wp = net.minecraft.core.BlockPos.containing(world.x, world.y, world.z);
            try {
                for (net.minecraft.world.item.ItemStack drop : net.minecraft.world.level.block.Block.getDrops(
                        bs, realLevel, local, level.getBlockEntity(local))) {
                    net.minecraft.world.level.block.Block.popResource(realLevel, wp, drop);
                }
            } catch (Throwable ignored) {
                // no-loot / context-needing drop — still remove the cell below
            }
            level.setBlock(local, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), quiet);
        }
        level.markCellsDirty();
        level.refreshLocalPositions();
    }

    /** The per-body explosion impulse. Touches only {@link PhysBody} state — safe on the physics thread. */
    private static void explodeBody(PhysBody physBody, double x, double y, double z, double power) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        double radius = power * EXPLOSION_RADIUS_PER_POWER;
        Vector3d blast = new Vector3d(x, y, z);
        RigidBody body = physBody.body;
        double reach = physBody.shape.boundingRadius() * body.scale() + radius;
        if (body.position.distanceSquared(blast) > reach * reach) {
            return;
        }
        boolean hit = false;
        for (net.minecraft.world.phys.AABB box : physBody.shape.boxes()) {
            Vector3d localCentre = new Vector3d(
                    (box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5);
            Vector3d at = body.toWorld(localCentre, new Vector3d());
            Vector3d away = new Vector3d(at).sub(blast);
            double dist = away.length();
            if (dist > radius) {
                continue;
            }
            if (dist < 1.0E-4) {
                away.set(0.0, 1.0, 0.0);
            } else {
                away.div(dist);
            }
            double scale3 = body.scale() * body.scale() * body.scale();
            double volume = (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
            double falloff = 1.0 - dist / radius;
            Vector3d impulse = new Vector3d(away).mul(power * falloff * volume * EXPLOSION_IMPULSE_PER_POWER);

            Vector3d r = new Vector3d(at).sub(body.position);
            body.linearVelocity.fma(body.inverseMass(), impulse);
            Vector3d torque = new Vector3d(r).cross(impulse);
            body.angularVelocity.add(new org.joml.Matrix3d(body.inverseInertiaWorld()).transform(torque));
            hit = true;
        }
        if (hit) {
            physBody.wakeUp();
        }
    }

    /**
     * Applies a single external impulse to a PhysContraption at a WORLD point — a THRUSTER (2026-07-18 — a
     * copper fan mounted in a phys contraption reacts against the gas it expels to propel the structure). The
     * impulse is applied at {@code worldPoint} so an off-centre thruster also produces torque (the same lever-arm
     * math the explosion uses), which is what lets a ring of fans steer as well as push. No-op for a non-phys
     * contraption. Runs on the physics thread on the async path — the caller resolves the id/point/force on the
     * game thread, this only touches {@link PhysBody} state.
     */
    public static void applyThrust(UUID contraptionId, Vector3d worldPoint, Vector3d worldImpulse) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        Vector3d point = new Vector3d(worldPoint);
        Vector3d impulse = new Vector3d(worldImpulse);
        if (ASYNC) {
            enqueue(() -> thrustBody(body, point, impulse));
        } else {
            thrustBody(body, point, impulse);
        }
    }

    /**
     * A PURE-TRANSLATION drive impulse through the body's center of mass — no lever arm, so it never spins the
     * body (unlike {@link #applyThrust}, whose off-centre point torques). This is what a VEHICLE's steering uses:
     * the driver pushes the whole hull, and self-levelling keeps it upright, instead of every keypress also
     * rolling it. Thread-safe the same way {@code applyThrust} is (enqueued on the async path); clamped and
     * mass-scaled, so a heavier vehicle accelerates slower off the same input (power-to-mass).
     */
    public static void applyThrustCentral(UUID contraptionId, Vector3d worldImpulse) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        Vector3d impulse = new Vector3d(worldImpulse);
        if (ASYNC) {
            enqueue(() -> thrustCentralBody(body, impulse));
        } else {
            thrustCentralBody(body, impulse);
        }
    }

    /** Directly sets the linear velocity of a body (thread-safe via command queue). */
    public static void setLinearVelocity(UUID contraptionId, org.joml.Vector3d velocity) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) return;
        PhysBody phys = entry.physBody;
        Vector3d v = new Vector3d(velocity.x, velocity.y, velocity.z);
        if (ASYNC) {
            enqueue(() -> { phys.body.linearVelocity.set(v); phys.wakeUp(); });
        } else {
            phys.body.linearVelocity.set(v); phys.wakeUp();
        }
    }

    /** Dampens angular velocity (wand grab — no spinning). */
    public static void dampAngularVelocity(UUID contraptionId, double factor) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) return;
        PhysBody phys = entry.physBody;
        if (ASYNC) {
            enqueue(() -> phys.body.angularVelocity.mul(factor));
        } else {
            phys.body.angularVelocity.mul(factor);
        }
    }

    /** Max yaw rate (rad/tick) a VEHICLE's steering may command — a firm but not teleporting turn. */
    private static final double MAX_YAW_RATE = 0.22;

    /**
     * Arcade yaw steering for a VEHICLE: SETS the body's world-frame yaw rate this tick (not an impulse), so the
     * hull turns to follow the driver like a horse follows the rider's look, ignoring the tiny incidental yaw a
     * free body would otherwise have. Roll/pitch are untouched — self-levelling still keeps the deck flat. Clamped
     * and thread-safe (enqueued on the async path). A rate of 0 also firmly stops yaw drift.
     */
    public static void setYawRate(UUID contraptionId, double omegaY) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        double w = Math.max(-MAX_YAW_RATE, Math.min(MAX_YAW_RATE, omegaY));
        Runnable r = () -> {
            if (body.kinematic || body.body.isStatic()) {
                return;
            }
            body.body.angularVelocity.y = w;
            if (w != 0.0) {
                body.wakeUp();
            }
        };
        if (ASYNC) {
            enqueue(r);
        } else {
            r.run();
        }
    }

    private static void thrustCentralBody(PhysBody physBody, Vector3d impulse) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        double mag = impulse.length();
        if (mag > MAX_FAN_IMPULSE) {
            impulse = new Vector3d(impulse).mul(MAX_FAN_IMPULSE / mag);
        }
        physBody.body.linearVelocity.fma(physBody.body.inverseMass(), impulse);
        physBody.selfRightTicks = SELF_RIGHT_LINGER_TICKS;
        physBody.wakeUp();
    }

    /** The thruster impulse — linear plus the torque from its lever arm. Touches only {@link PhysBody}, so it is physics-thread safe. */
    /** Hard cap on a single fan's per-tick LINEAR thrust impulse, so a big overclocked stack can't fling the body. */
    private static final double MAX_FAN_IMPULSE = 3.0;
    /** Hard cap on the per-tick ANGULAR velocity a single fan may inject — the flip stays deliberate, never a jolt. */
    private static final double MAX_FAN_SPIN = 0.03;
    /** Ticks self-levelling stays armed after a fan's last thrust, so a pulsing fan still holds the platform level. */
    private static final int SELF_RIGHT_LINGER_TICKS = 4;

    private static void thrustBody(PhysBody physBody, Vector3d worldPoint, Vector3d impulse) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        RigidBody body = physBody.body;
        // A fan MUST be able to flip the contraption (an off-centre thrust is a real torque), but the forces are
        // CLAMPED so it only tips when the thrust genuinely warrants it, never spuriously from a jitter spike
        // (2026-07-18 — "un fan sí debe ser capaz de voltear ... pero estabiliza las fuerzas para que no se
        // voltee porque sí"). Clamp the linear impulse, apply it at the fan's cell so it still torques, then
        // clamp the angular kick this one tick may add — a sustained off-centre push accumulates rotation across
        // ticks and rolls the body over deliberately, while a single noisy tick can't snap it.
        double mag = impulse.length();
        if (mag > MAX_FAN_IMPULSE) {
            impulse = new Vector3d(impulse).mul(MAX_FAN_IMPULSE / mag);
        }
        body.linearVelocity.fma(body.inverseMass(), impulse);
        Vector3d r = new Vector3d(worldPoint).sub(body.position);
        Vector3d dOmega = new org.joml.Matrix3d(body.inverseInertiaWorld())
                .transform(new Vector3d(r).cross(impulse));
        double spin = dOmega.length();
        if (spin > MAX_FAN_SPIN) {
            dOmega.mul(MAX_FAN_SPIN / spin);
        }
        body.angularVelocity.add(dOmega);
        // Arm active-thruster self-levelling for a few ticks (see PhysBody#selfRightTicks). Lingering a
        // handful of ticks past the last thrust means a fan pulsing on/off still keeps the platform level
        // instead of only levelling on the exact ticks a fan fired.
        physBody.selfRightTicks = SELF_RIGHT_LINGER_TICKS;
        physBody.wakeUp();
    }

    /** Vanilla's blast radius is roughly twice the power, which is what {@code Explosion} itself uses. */
    private static final double EXPLOSION_RADIUS_PER_POWER = 2.0;

    /**
     * Impulse per unit of explosion power at ground zero. Tuned so one TNT (power 4) gives a
     * baseline-mass single block a healthy toss rather than either a nudge or an orbital launch.
     */
    private static final double EXPLOSION_IMPULSE_PER_POWER = 3.0;

    /**
     * Forces the body to re-read its transform from the state on the next step, and wakes it.
     * Detection makes this unnecessary in the normal case; it exists for callers that want to be
     * explicit (and is harmless to call redundantly).
     */
    public static void resync(UUID contraptionId) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry != null) {
            entry.initialized = false; // game-thread mirror — triggers a foreign-move adopt next pump
            if (ASYNC) {
                enqueue(entry.physBody::wakeUp);
            } else {
                entry.physBody.wakeUp();
            }
        }
    }

    /**
     * Steps every PhysContraption by one tick. Called once from {@code ContraptionEngine#tickAll}
     * BEFORE per-contraption behaviors run, so the rest of the tick (render, hitboxes, rider carry)
     * observes this tick's resolved transform.
     */
    /** Game-tick entry point. Routes to the async feed (default) or the inline solve (fallback). */
    public static void stepAll() {
        if (ASYNC) {
            if (!RUNNING) {
                startThread(); // cheap volatile check keeps the common case off the synchronized path
            }
            asyncPump();
        } else {
            stepAllSync();
        }
    }

    private static void stepAllSync() {
        Map<ResourceKey<Level>, List<Entry>> byWorld = new LinkedHashMap<>();
        Map<ResourceKey<Level>, ServerLevel> levels = new HashMap<>();
        Map<Entry, ContraptionState> states = new HashMap<>();
        // Contraptions whose cell set changed this tick, and which may therefore have fractured.
        // Collected during the pass and split AFTER it, because splitting registers new contraptions
        // and this loop is iterating ContraptionManager's own set — the same defer-and-apply shape
        // ContraptionEngine#tickAll uses for its disassembles.
        List<ContraptionEntity> splitCandidates = null;

        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            PhysicsBehavior physics = physicsBehaviorOf(state);
            if (physics == null) {
                continue;
            }
            ServerLevel level = resolveLevel(state);
            if (level == null) {
                // World unloaded — the body keeps its velocity and resumes when it returns, but the
                // behavior must stop reporting last tick's motion or the contraption would keep being
                // translated by it every tick with no solver to correct it.
                physics.clearPendingMotion();
                continue;
            }
            Entry entry = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
            if (sync(entry, state)) {
                if (splitCandidates == null) {
                    splitCandidates = new ArrayList<>();
                }
                splitCandidates.add(entity);
            }
            physics.attach(entry.physBody, entry.massModel);
            states.put(entry, state);
            levels.put(state.worldId(), level);
            // Presence LOD — see #anyPlayerNear. Skip solving a body nobody is near this tick.
            if (!anyPlayerNear(level, state)) {
                continue;
            }
            byWorld.computeIfAbsent(state.worldId(), w -> new ArrayList<>()).add(entry);
        }
        if (byWorld.isEmpty()) {
            return;
        }
        // Retire bodies whose contraption is gone, or ENTRIES grows without bound across a session.
        ENTRIES.keySet().removeIf(id -> ContraptionManager.get(id) == null);

        for (Map.Entry<ResourceKey<Level>, List<Entry>> group : byWorld.entrySet()) {
            ServerLevel level = levels.get(group.getKey());
            List<PhysBody> bodies = new ArrayList<>(group.getValue().size());
            for (Entry entry : group.getValue()) {
                PhysBody body = entry.physBody;
                if (!body.kinematic && !body.isAsleep()) {
                    rebakeIfNeeded(body, level);
                }
                bodies.add(body);
            }
            solveInParallel(bodies);
            for (Entry entry : group.getValue()) {
                writeBack(entry, states.get(entry));
                // The solver only recorded that a crash happened; acting on it needs the world, and must
                // therefore happen out here on the main thread, after the step. See the detonator.
                try {
                    dev.arubik.craftengine.contraption.explosive.ContraptionImpactDetonator.afterStep(
                            entry.physBody, states.get(entry), level);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        // Fracture check LAST, against this tick's solved transform: a fragment is spawned at the
        // parent's current pose, so it must be the pose the parent actually ended the tick at.
        // Solving a just-disconnected body as one rigid piece for the single tick before the split is
        // the cost of that ordering, and is not observable.
        if (splitCandidates != null) {
            for (ContraptionEntity entity : splitCandidates) {
                try {
                    ContraptionSplitter.splitIfDisconnected(entity);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private static void enqueue(Runnable command) {
        COMMANDS.add(command);
    }

    /**
     * The game-thread half of async physics: it touches the level and Bukkit (which only the game thread
     * may), and hands everything else to {@link #PHYS_THREAD}. Per tick it (1) syncs each body's mass /
     * shape / scale / foreign-move from its state — computing on the game thread, INSTALLING via a command
     * so only the physics thread writes the body; (2) re-bakes terrain that moved, since block reads are
     * game-thread only, again installing via a command; (3) republishes the active set; (4) reads each
     * body's last PUBLISHED transform and applies it through the ordinary behavior path (rider carry),
     * plus impact detonation and fracture. It never blocks on the solve.
     */
    private static void asyncPump() {
        Map<ResourceKey<Level>, List<Entry>> byWorld = new LinkedHashMap<>();
        Map<Entry, ContraptionState> states = new HashMap<>();
        Map<ResourceKey<Level>, ServerLevel> levels = new HashMap<>();
        List<ContraptionEntity> splitCandidates = null;

        for (ContraptionEntity entity : ContraptionManager.all()) {
            ContraptionState state = entity.state();
            PhysicsBehavior physics = physicsBehaviorOf(state);
            if (physics == null) {
                continue;
            }
            ServerLevel level = resolveLevel(state);
            if (level == null) {
                physics.clearPendingMotion();
                continue;
            }
            Entry entry = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
            if (syncMain(entry, state)) {
                if (splitCandidates == null) {
                    splitCandidates = new ArrayList<>();
                }
                splitCandidates.add(entity);
            }
            physics.attach(entry.physBody, entry.massModel);
            states.put(entry, state);
            levels.put(state.worldId(), level);
            // Presence LOD — see #anyPlayerNear. A body with no player near is frozen at its last pose this
            // tick (not solved, not re-baked, not written back) and wakes the instant a player comes close.
            if (!anyPlayerNear(level, state)) {
                continue;
            }
            byWorld.computeIfAbsent(state.worldId(), w -> new ArrayList<>()).add(entry);
        }
        ENTRIES.keySet().removeIf(id -> ContraptionManager.get(id) == null);

        // Terrain bake (reads live blocks — game thread only), installed on the body via a command.
        for (Map.Entry<ResourceKey<Level>, List<Entry>> group : byWorld.entrySet()) {
            ServerLevel level = levels.get(group.getKey());
            for (Entry entry : group.getValue()) {
                PhysBody body = entry.physBody;
                if (body.kinematic || body.isAsleep()) {
                    continue;
                }
                rebakeMain(entry, level);
            }
        }

        // Hand the active set to the physics thread (brief lock; the solve itself runs lock-free).
        LOCK.lock();
        try {
            ACTIVE_ENTRIES = byWorld;
        } finally {
            LOCK.unlock();
        }

        // Apply each body's last published result on the game thread.
        for (Map.Entry<ResourceKey<Level>, List<Entry>> group : byWorld.entrySet()) {
            ServerLevel level = levels.get(group.getKey());
            for (Entry entry : group.getValue()) {
                BodyTransform pub = entry.published;
                if (pub == null) {
                    continue; // never stepped yet — nothing to apply this tick
                }
                ContraptionState state = states.get(entry);
                writeBackAsync(entry, state, pub);
                try {
                    dev.arubik.craftengine.contraption.explosive.ContraptionImpactDetonator.afterStep(
                            pub.maxImpactSpeed(), pub.impactPoint(), state, level);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        if (splitCandidates != null) {
            for (ContraptionEntity entity : splitCandidates) {
                try {
                    ContraptionSplitter.splitIfDisconnected(entity);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Game-thread sync: reads the contraption's level/state and, for anything that changed, INSTALLS the
     * change on the body via a command so the physics thread stays the only writer. Mirror fields the game
     * thread reads later ({@code massModel}, {@code lastScale}, {@code initialized}, {@code lastWritten*})
     * are updated here directly. Returns whether the cell set changed (a fracture candidate).
     */
    private static boolean syncMain(Entry entry, ContraptionState state) {
        var contraptionLevel = state.level();
        PhysBody body = entry.physBody;

        int shape = contraptionLevel == null ? 0 : contraptionLevel.localPositions().hashCode();
        boolean firstObservation = !entry.shapeObserved;
        boolean cellsChanged = firstObservation || shape != entry.lastShapeHash;
        if (cellsChanged) {
            // Old COM BEFORE we overwrite the mass model — needed to keep the STRUCTURE still across the change.
            Vector3d oldCom = new Vector3d(entry.massModel.centerOfMass().x, entry.massModel.centerOfMass().y,
                    entry.massModel.centerOfMass().z);
            entry.lastShapeHash = shape;
            entry.shapeObserved = true;
            MassModel mm = MassModel.of(contraptionLevel);
            entry.massModel = mm;
            Vector3d com = new Vector3d(mm.centerOfMass().x, mm.centerOfMass().y, mm.centerOfMass().z);
            CollisionShape newShape = CollisionShape.of(contraptionLevel, com);
            double floatability = FloatabilityModel.of(contraptionLevel).floatability();
            double friction = FrictionModel.of(contraptionLevel).friction();
            java.util.function.ToDoubleFunction<Vector3d> restitutionField =
                    buildRestitutionField(contraptionLevel, com, body.body);
            // SMOOTH mass/COM recompute (user: "que se recompute más suavemente la física para que no se aloque").
            // A cell coming or going (an anvil falling out, a block placed) moves the COM, and the body's pose is
            // stored COM-relative. The old path re-anchored the body to the STATE bearing every time, which pinned
            // the bearing cell and teleported the REST of the structure by the COM delta — a jolt each recompute,
            // and with a block bouncing in/out per tick that jolt loop is the "física loca". Instead, on a
            // cell-only change shift the body position by the WORLD-space COM delta so the structure stays exactly
            // where it is (only the internal COM reference moves), and DON'T force a bearing re-anchor. Velocity is
            // left untouched — a body losing a piece keeps its velocity (the piece carries its own away), which is
            // already correct, so no momentum fix is needed.
            double scaleNow = entry.lastScale > 0 ? entry.lastScale : 1.0;
            Vector3d comDelta = firstObservation ? null : new Vector3d(com).sub(oldCom);
            // A SPLIT/fracture drops a whole island of cells at once, so the COM jumps far. The smooth COM-shift
            // below is right for incremental edits, but on a big jump the entry's mass snapshot is briefly out of
            // sync with the splitter's own re-base (it clears cells AFTER this sync), and shifting by that huge
            // delta would fling the original piece (user: "al separar un contraption el COM se bugea y mueve mucho
            // el original"). So for a large COM move, fall back to the robust bearing re-anchor (foreignMove),
            // which pins the piece to its STATE position regardless of the mass snapshot.
            boolean bigComJump = comDelta != null && comDelta.length() > COM_SMOOTH_SHIFT_MAX;
            if (bigComJump) {
                comDelta = null;
            }
            final Vector3d comShift = comDelta;
            enqueue(() -> {
                // Preserve the sleep state (user: el contraption "sigue tilteando un momento ... se reajusta").
                // The tilt is the body being WOKEN on every edit and re-settling its resting pose under the new
                // mass. A settled (asleep) contraption should absorb a block add/remove silently — the new
                // mass/shape are stored, the structure is kept put by the COM shift below, and it is NOT woken, so
                // it never re-solves and never rocks. A genuinely moving (awake) body still re-solves as it should.
                // "Settled" is broader than fully-asleep: a body needs SLEEP_TICKS (20) consecutive at-rest ticks
                // to actually sleep, so one that just came to rest is still AWAKE with ~0 velocity. Waking it on an
                // edit still re-solves its pose (the small residual tilt). So treat any near-rest body as settled:
                // don't wake it, and zero its tiny velocities so the next solve starts from clean rest instead of
                // re-kicking. NOT force-slept — a real change (support removed) must still be able to make it fall.
                boolean settled = body.isAsleep()
                        || (body.body.linearVelocity.length() < XpbdSolver.REST_LINEAR_EPSILON
                                && body.body.angularVelocity.length() < XpbdSolver.REST_ANGULAR_EPSILON);
                if (comShift != null && comShift.lengthSquared() > 1.0e-12) {
                    Vector3d worldShift = new Vector3d(comShift).mul(scaleNow).rotate(body.body.orientation);
                    body.body.position.add(worldShift);
                }
                body.shape = newShape;
                body.body.setMassProperties(mm.inverseMass(), mm.inverseInertiaTensor());
                body.floatability = floatability;
                body.body.setFriction(friction);
                body.body.setRestitutionField(restitutionField);
                if (settled) {
                    body.body.linearVelocity.zero();
                    body.body.angularVelocity.zero();
                } else {
                    body.wakeUp();
                }
            });
            // The FIRST observation (initial placement) and a big COM jump (split/fracture) both need the robust
            // bearing re-anchor (foreignMove pins the piece to its STATE position); an ordinary incremental edit is
            // handled by the smooth COM shift above, so don't re-snap it.
            if (firstObservation || bigComJump) {
                entry.initialized = false;
            }
        }

        double scale = state.scale();
        if (scale != entry.lastScale) {
            entry.lastScale = scale;
            enqueue(() -> {
                body.body.setScale(scale);
                body.wakeUp();
            });
            entry.initialized = false;
        }

        boolean kin = state.isStalled() || isHeld(state.id());
        if (body.kinematic != kin) {
            enqueue(() -> body.kinematic = kin);
        }

        boolean foreignMove = !entry.initialized
                || entry.lastWrittenPosition == null
                || Math.abs(state.x() - entry.lastWrittenPosition.x) > RESYNC_EPSILON
                || Math.abs(state.y() - entry.lastWrittenPosition.y) > RESYNC_EPSILON
                || Math.abs(state.z() - entry.lastWrittenPosition.z) > RESYNC_EPSILON
                || Math.abs(state.yawRadians() - entry.lastWrittenYaw) > RESYNC_EPSILON
                || Math.abs(state.pitchRadians() - entry.lastWrittenPitch) > RESYNC_EPSILON
                || Math.abs(state.rollRadians() - entry.lastWrittenRoll) > RESYNC_EPSILON;
        if (foreignMove) {
            Quaterniond orientation = ContraptionTransform.fromEuler(
                    state.yawRadians(), state.pitchRadians(), state.rollRadians());
            Vector3d com = localCom(entry);
            Vector3d comWorld = ContraptionTransform.comWorld(
                    new Vec3(state.x(), state.y(), state.z()), orientation, com, state.scale());
            enqueue(() -> {
                body.body.orientation.set(orientation);
                body.body.position.set(comWorld);
                body.wakeUp();
            });
            entry.initialized = true;
            // Seed a published transform so this tick's rebake and writeBack have a position to work from
            // even before the physics thread has stepped this body once.
            if (entry.published == null) {
                entry.published = new BodyTransform(new Vector3d(comWorld), new Quaterniond(orientation),
                        new Vector3d(), 0.0, null);
            }
        }
        return cellsChanged;
    }

    /** Game-thread terrain bake for one body, keyed off its PUBLISHED position; result installed via command. */
    private static void rebakeMain(Entry entry, ServerLevel level) {
        BodyTransform pub = entry.published;
        if (pub == null) {
            return; // no position yet; syncMain seeds one on the first foreign-move, so this is transient
        }
        Vector3d pos = pub.position();
        double scale = entry.lastScale > 0 ? entry.lastScale : 1.0;
        double radius = entry.physBody.shape.boundingRadius() * scale;
        double margin = pub.linearVelocity().length() + XpbdSolver.SPECULATIVE_DISTANCE + BAKE_MARGIN;
        net.minecraft.world.phys.AABB needed = boxAround(pos, radius + margin);
        if (entry.mainBakedRegion != null && encloses(entry.mainBakedRegion, needed)) {
            return;
        }
        net.minecraft.world.phys.AABB region = boxAround(pos, radius + margin + BAKE_LOOKAHEAD);
        WorldBlockCache cache = WorldBlockCache.bake(level, region);
        PhysBody body = entry.physBody;
        enqueue(() -> {
            body.world = cache;
            body.bakedRegion = region;
        });
        entry.mainBakedRegion = region;
    }

    private static net.minecraft.world.phys.AABB boxAround(Vector3d c, double r) {
        return new net.minecraft.world.phys.AABB(c.x - r, c.y - r, c.z - r, c.x + r, c.y + r, c.z + r);
    }

    /** {@link #writeBack} against a published transform rather than the live (physics-owned) body. */
    private static void writeBackAsync(Entry entry, ContraptionState state, BodyTransform pub) {
        if (state == null) {
            return;
        }
        double[] euler = ContraptionTransform.eulerYXZ(pub.orientation());
        Vec3 bearing = ContraptionTransform.bearingOrigin(pub.position(), pub.orientation(), localCom(entry),
                state.scale());
        PhysicsBehavior behavior = physicsBehaviorOf(state);
        if (behavior != null) {
            behavior.setPendingMotion(
                    new Vec3(bearing.x - state.x(), bearing.y - state.y(), bearing.z - state.z()),
                    euler[0], euler[1], euler[2]);
            dev.arubik.craftengine.contraption.listener.BearingHammerListener.markAssembled(state.worldId(),
                    net.minecraft.core.BlockPos.containing(bearing.x, bearing.y, bearing.z), state.id());
        }
        entry.lastWrittenPosition = bearing;
        entry.lastWrittenYaw = euler[0];
        entry.lastWrittenPitch = euler[1];
        entry.lastWrittenRoll = euler[2];
        entry.initialized = true;
    }

    /**
     * Solves a world's bodies, splitting them into independent ISLANDS and stepping the islands on a
     * worker pool.
     *
     * <h2>Why this is safe when the previous off-thread attempt was not</h2>
     * The earlier parallel solver computed a tick's collision response against the PREVIOUS tick's
     * snapshot and applied it at the CURRENT origin — up to 0.6 blocks of error, which is what made
     * bodies sink. Nothing here spans a tick: the bake happens on the main thread, the solve runs and
     * is JOINED inside the same tick, and only then is the result applied. The work moves to other
     * cores; it does not move to another tick.
     *
     * <p>It is also safe because {@link XpbdSolver#step} is a pure function of {@link PhysBody} values
     * — {@link WorldBlockCache} is a snapshot with no reference back to the level, so a worker touches
     * no Bukkit or NMS state. That was a deliberate property of the design, and this is what it was for.
     *
     * <h2>Islands</h2>
     * Two bodies must be in the SAME {@code step} call to collide correctly — solved separately they
     * would each push off a stale copy of the other and inject energy. But bodies that cannot reach
     * each other at all are genuinely independent, so they can be solved concurrently. Contraptions are
     * usually far apart, so in practice almost every body is its own island and this parallelises fully;
     * when two do meet, they land in one island and are solved together exactly as before.
     */
    private static void solveInParallel(List<PhysBody> bodies) {
        if (bodies.isEmpty()) {
            return;
        }
        List<List<PhysBody>> islands = islands(bodies);
        if (islands.size() < 2) {
            // One island (or none): a pool round-trip would cost more than it saves.
            for (List<PhysBody> island : islands) {
                XpbdSolver.step(island, 1.0);
            }
            return;
        }
        List<java.util.concurrent.Future<?>> pending = new ArrayList<>(islands.size());
        for (List<PhysBody> island : islands) {
            pending.add(WORKERS.submit(() -> XpbdSolver.step(island, 1.0)));
        }
        for (java.util.concurrent.Future<?> f : pending) {
            try {
                f.get(); // joined within the tick — see the class javadoc on why this must not span ticks
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Partitions bodies into groups that could possibly touch each other, by union-find over the
     * broadphase test. A body alone in its group can be solved on any thread.
     */
    private static List<List<PhysBody>> islands(List<PhysBody> bodies) {
        int n = bodies.size();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                PhysBody a = bodies.get(i);
                PhysBody b = bodies.get(j);
                if (ContactGenerator.broadphaseOverlap(a.body, a.shape, b.body, b.shape)) {
                    union(parent, i, j);
                }
            }
        }
        Map<Integer, List<PhysBody>> grouped = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            grouped.computeIfAbsent(find(parent, i), k -> new ArrayList<>()).add(bodies.get(i));
        }
        return new ArrayList<>(grouped.values());
    }

    private static int find(int[] parent, int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]];
            i = parent[i];
        }
        return i;
    }

    private static void union(int[] parent, int a, int b) {
        int ra = find(parent, a);
        int rb = find(parent, b);
        if (ra != rb) {
            parent[rb] = ra;
        }
    }

    /**
     * Workers for the island solves. Daemon threads so they can never hold the server open, and sized
     * to leave the main thread and the server's own pools room to breathe.
     */
    private static final java.util.concurrent.ExecutorService WORKERS =
            java.util.concurrent.Executors.newFixedThreadPool(
                    Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() - 2)),
                    r -> {
                        Thread t = new Thread(r, "contraption-physics");
                        t.setDaemon(true);
                        return t;
                    });

    /** Stops the worker pool. Called from the plugin's disable path. */
    public static void shutdown() {
        RUNNING = false;
        Thread t = PHYS_THREAD;
        if (t != null) {
            java.util.concurrent.locks.LockSupport.unpark(t);
            try {
                t.join(1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        WORKERS.shutdownNow();
    }

    /**
     * Bakes this body's terrain, but only if the snapshot it already has cannot cover everywhere the
     * body can reach this tick.
     *
     * <h2>Why this is the optimisation that matters</h2>
     * A bake reads the block state, collision shape and fluid state of every cell of a region that
     * scales with the body's size CUBED — for a 4x4x4 shell that is over a thousand world lookups.
     * Doing that per body per tick, on the main thread, is the single largest cost physics imposes,
     * and it is almost all waste: terrain does not change most ticks, and a body has usually not left
     * the region it was baked against.
     *
     * <p>So the snapshot is kept until it is genuinely stale, which happens two ways and only two: the
     * body can now reach outside the baked region (checked here), or something changed the world
     * inside it ({@link #wakeNear} invalidates it, driven by the block/explosion listener). A resting
     * or slowly drifting body bakes once and reuses it; a body falling at speed still re-bakes as often
     * as it truly must.
     *
     * <p>The margin scales with the body's current speed, so the region always covers a full tick of
     * motion plus the speculative band — a body can never move into terrain that was never baked,
     * which is what tunnelling would look like.
     */
    private static void rebakeIfNeeded(PhysBody body, ServerLevel level) {
        double speed = body.body.linearVelocity.length();
        double margin = speed + XpbdSolver.SPECULATIVE_DISTANCE + BAKE_MARGIN;
        net.minecraft.world.phys.AABB needed = body.worldBounds(margin);
        if (body.bakedRegion != null && encloses(body.bakedRegion, needed)) {
            return; // the snapshot already covers everywhere the body can get to this tick
        }
        // Bake wider than this tick strictly needs: the read is expensive enough that over-covering
        // once beats repeating it, so a steadily drifting body re-bakes every several ticks.
        net.minecraft.world.phys.AABB region = body.worldBounds(margin + BAKE_LOOKAHEAD);
        body.world = WorldBlockCache.bake(level, region);
        body.bakedRegion = region;
    }

    /** Extra reach baked beyond this tick's need, so a moving body re-bakes every few ticks, not every tick. */
    private static final double BAKE_LOOKAHEAD = 3.0;

    private static boolean encloses(net.minecraft.world.phys.AABB outer, net.minecraft.world.phys.AABB inner) {
        return outer.minX <= inner.minX && outer.maxX >= inner.maxX
                && outer.minY <= inner.minY && outer.maxY >= inner.maxY
                && outer.minZ <= inner.minZ && outer.maxZ >= inner.maxZ;
    }

    /**
     * Pulls mass, shape, scale and (when externally changed) the transform from the state into the
     * body.
     *
     * @return whether the contraption's cell set changed — the only way it can have fractured
     */
    private static boolean sync(Entry entry, ContraptionState state) {
        PhysBody body = entry.physBody;
        var contraptionLevel = state.level();

        // The cell SET's shape, not its size. A piston inside the contraption moves a block by removing
        // one cell and adding another, leaving the COUNT identical while the structure — and therefore its
        // mass, its centre of mass, its inertia tensor and its collision geometry — genuinely changed. A
        // count-only check watched the one number a piston cannot alter, so a body kept simulating against
        // the shape it used to have, and a block a piston pushed off it never fractured away.
        int shape = contraptionLevel == null ? 0 : contraptionLevel.localPositions().hashCode();
        boolean cellsChanged = !entry.shapeObserved || shape != entry.lastShapeHash;
        if (cellsChanged) {
            entry.lastShapeHash = shape;
            entry.shapeObserved = true;
            entry.massModel = MassModel.of(contraptionLevel);
            Vector3d com = new Vector3d(
                    entry.massModel.centerOfMass().x,
                    entry.massModel.centerOfMass().y,
                    entry.massModel.centerOfMass().z);
            body.shape = CollisionShape.of(contraptionLevel, com);
            body.body.setMassProperties(entry.massModel.inverseMass(), entry.massModel.inverseInertiaTensor());
            body.floatability = FloatabilityModel.of(contraptionLevel).floatability();
            body.body.setFriction(FrictionModel.of(contraptionLevel).friction());
            body.body.setRestitutionField(buildRestitutionField(contraptionLevel, com, body.body));
            // The cell set changed, so the COM moved: the body's stored COM position now refers to a
            // different material point. Re-derive it from the state's (unchanged) bearing origin.
            entry.initialized = false;
            body.wakeUp();
        }

        double scale = state.scale();
        if (scale != entry.lastScale) {
            entry.lastScale = scale;
            body.body.setScale(scale);
            entry.initialized = false;
            body.wakeUp();
        }

        body.kinematic = state.isStalled() || isHeld(state.id());

        boolean foreignMove = !entry.initialized
                || entry.lastWrittenPosition == null
                || Math.abs(state.x() - entry.lastWrittenPosition.x) > RESYNC_EPSILON
                || Math.abs(state.y() - entry.lastWrittenPosition.y) > RESYNC_EPSILON
                || Math.abs(state.z() - entry.lastWrittenPosition.z) > RESYNC_EPSILON
                || Math.abs(state.yawRadians() - entry.lastWrittenYaw) > RESYNC_EPSILON
                || Math.abs(state.pitchRadians() - entry.lastWrittenPitch) > RESYNC_EPSILON
                || Math.abs(state.rollRadians() - entry.lastWrittenRoll) > RESYNC_EPSILON;
        if (foreignMove) {
            adoptStateTransform(entry, state);
        }
        return cellsChanged;
    }

    /** Rebuilds the body's COM-origin transform from the state's bearing-origin Euler transform. */
    private static void adoptStateTransform(Entry entry, ContraptionState state) {
        RigidBody body = entry.physBody.body;
        Quaterniond orientation = ContraptionTransform.fromEuler(
                state.yawRadians(), state.pitchRadians(), state.rollRadians());
        body.orientation.set(orientation);
        Vector3d com = localCom(entry);
        Vector3d comWorld = ContraptionTransform.comWorld(
                new Vec3(state.x(), state.y(), state.z()), orientation, com, state.scale());
        body.position.set(comWorld);
        entry.initialized = true;
        entry.physBody.wakeUp();
    }

    /**
     * Hands the solved transform to the contraption's {@link PhysicsBehavior} as PENDING motion,
     * rather than writing it onto the state here.
     *
     * <h2>Why this defers instead of applying</h2>
     * The engine's contract is that a behavior <i>reports</i> its motion and
     * {@code ContraptionEngine#stepKinematics} applies it — because that is also where the per-tick
     * deltas are derived: {@code setLastDelta(velocity)} from the summed {@code velocityThisTick()},
     * and {@code setLastYawDelta(yaw - yawBefore)} from a yaw captured before the behaviors run.
     * Those deltas are what {@code ContraptionEntity#carryRiders}, {@code carryNearbyEntities} and
     * {@code pushBackNearby*} feed on.
     *
     * <p>Writing the transform directly from here bypassed all of it: the solve happened before
     * {@code stepKinematics} even sampled {@code yawBefore}, and {@link PhysicsBehavior} reported no
     * velocity, so every phys contraption reported a zero delta and <b>carried nobody</b> — a player
     * standing on a falling or spinning phys body was simply left behind in the air.
     *
     * <p>So the solve stays here (it must: every body in a world is solved in one call for
     * contraption-vs-contraption to be correct), but the RESULT is applied through the ordinary
     * behavior path.
     */
    private static void writeBack(Entry entry, ContraptionState state) {
        if (state == null) {
            return;
        }
        RigidBody body = entry.physBody.body;
        double[] euler = ContraptionTransform.eulerYXZ(body.orientation);
        Vec3 bearing = ContraptionTransform.bearingOrigin(
                body.position, body.orientation, localCom(entry), state.scale());

        PhysicsBehavior behavior = physicsBehaviorOf(state);
        if (behavior != null) {
            behavior.setPendingMotion(
                    new Vec3(bearing.x - state.x(), bearing.y - state.y(), bearing.z - state.z()),
                    euler[0], euler[1], euler[2]);
        }

        // Re-anchor the persistence key onto the block the body is ACTUALLY at (2026-07-16 — "al reiniciar
        // el server todos los phys contraptions desaparecen").
        //
        // Every persistence path — save-on-chunk-unload, the onDisable shutdown flush, and
        // rehydrate-on-chunk-load — is driven off BearingHammerListener's assembled-anchor map, keyed by a
        // block position. For LINEAR/ROTATIONAL that key is the bearing block, which never moves, so it was
        // written once and forgotten. A PHYS body has no bearing block at all: its anchor is captured INTO
        // the structure and the body then FALLS AWAY from those coordinates. So the key has to follow it,
        // or "save when this chunk unloads" and "restore when this chunk loads" refer to a chunk the body
        // left long ago.
        //
        // Doing it here means the key is re-derived from the same `bearing` the state is about to be moved
        // to, so the two can never disagree. markAssembled drops the previous key, and is a no-op while the
        // body stays within one block — which is most ticks, and all of them once it is asleep.
        if (behavior != null) {
            dev.arubik.craftengine.contraption.listener.BearingHammerListener.markAssembled(state.worldId(),
                    net.minecraft.core.BlockPos.containing(bearing.x, bearing.y, bearing.z), state.id());
        }

        // What the state is EXPECTED to read once stepKinematics has applied the pending motion. The
        // foreign-move check compares against this, so a stall (which zeroes the applied delta) or a
        // wand drag reads as a divergence and resyncs the body — self-healing, no notification needed.
        entry.lastWrittenPosition = bearing;
        entry.lastWrittenYaw = euler[0];
        entry.lastWrittenPitch = euler[1];
        entry.lastWrittenRoll = euler[2];
        entry.initialized = true;
    }

    private static Vector3d localCom(Entry entry) {
        Vec3 com = entry.massModel.centerOfMass();
        return new Vector3d(com.x, com.y, com.z);
    }

    /**
     * Builds the CELL-LOCAL bounciness lookup for a body (2026-07-17 — user: "bounciness ... cell prefered"):
     * a world contact point resolves to the coefficient of restitution of the CELL it fell on, so a slime cell
     * bounces where it lands while its neighbours do not. The bouncy cells are snapshotted HERE (main thread,
     * reading the live level) into an immutable packed-key map, so the returned function — invoked on the
     * physics thread every contact — only does arithmetic and a map lookup, never an off-thread block read.
     * Returns a constant-zero function when the body has no bouncy cell, so an ordinary structure pays nothing.
     */
    private static java.util.function.ToDoubleFunction<Vector3d> buildRestitutionField(
            ContraptionLevel level, Vector3d com, RigidBody body) {
        java.util.Map<Long, Float> restMap = new java.util.HashMap<>();
        for (net.minecraft.core.BlockPos local : level.localPositions()) {
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                continue;
            }
            double r = dev.arubik.craftengine.contraption.behavior.RestitutionBlockBehavior.restitutionOf(state);
            if (r > 0.0) {
                restMap.put(local.asLong(), (float) r);
            }
        }
        if (restMap.isEmpty()) {
            return worldPoint -> 0.0;
        }
        Vector3d comSnapshot = new Vector3d(com);
        return worldPoint -> restitutionAtWorld(worldPoint, body, comSnapshot, restMap);
    }

    /** Maps a WORLD contact point back to the body's level-local cell and returns that cell's restitution (0 if none). */
    private static double restitutionAtWorld(Vector3d worldPoint, RigidBody body, Vector3d com,
            java.util.Map<Long, Float> restMap) {
        // world -> COM-relative local: R⁻¹·(world - position)/scale
        Vector3d d = new Vector3d(worldPoint).sub(body.position);
        new org.joml.Quaterniond(body.orientation).conjugate().transform(d);
        double scale = body.scale();
        if (scale > 1.0E-9) {
            d.div(scale);
        }
        // COM-relative -> level-local
        double lx = d.x + com.x, ly = d.y + com.y, lz = d.z + com.z;
        // The contact point sits on a cell FACE, so probe the eight cells straddling it and take the bounciest
        // found — a point on TOP of a slime cell would otherwise floor to the air cell above it.
        double best = 0.0;
        double eps = 0.02;
        for (double ox = -eps; ox <= eps; ox += 2.0 * eps) {
            for (double oy = -eps; oy <= eps; oy += 2.0 * eps) {
                for (double oz = -eps; oz <= eps; oz += 2.0 * eps) {
                    long key = net.minecraft.core.BlockPos.asLong(
                            (int) Math.floor(lx + ox), (int) Math.floor(ly + oy), (int) Math.floor(lz + oz));
                    Float r = restMap.get(key);
                    if (r != null && r > best) {
                        best = r;
                    }
                }
            }
        }
        return best;
    }

    private static PhysicsBehavior physicsBehaviorOf(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof PhysicsBehavior physics) {
                return physics;
            }
        }
        return null;
    }

    private static ServerLevel resolveLevel(ContraptionState state) {
        try {
            net.minecraft.server.MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            net.minecraft.server.level.ServerLevel serverLevel = server.getLevel(state.worldId());
            org.bukkit.World world = serverLevel != null ? serverLevel.getWorld() : null;
            return world == null ? null : ((CraftWorld) world).getHandle();
        } catch (Throwable t) {
            return null;
        }
    }
}
