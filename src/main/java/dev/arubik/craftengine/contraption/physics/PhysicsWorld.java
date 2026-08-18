/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.physics;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.behavior.RestitutionBlockBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.explosive.ContraptionImpactDetonator;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.physics.CollisionShape;
import dev.arubik.craftengine.contraption.physics.ContactGenerator;
import dev.arubik.craftengine.contraption.physics.ContraptionSplitter;
import dev.arubik.craftengine.contraption.physics.ContraptionTransform;
import dev.arubik.craftengine.contraption.physics.FloatabilityModel;
import dev.arubik.craftengine.contraption.physics.FrictionModel;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.RigidBody;
import dev.arubik.craftengine.contraption.physics.WorldBlockCache;
import dev.arubik.craftengine.contraption.physics.XpbdSolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class PhysicsWorld {
    private static final double BAKE_MARGIN = 2.0;
    private static final double RESYNC_EPSILON = 1.0E-6;
    private static final double COM_SMOOTH_SHIFT_MAX = 1.0;
    private static final Map<UUID, Entry> ENTRIES = new HashMap<UUID, Entry>();
    private static final Set<UUID> HELD = ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<UUID, Integer> RELEASE_DAMPING = new ConcurrentHashMap();
    private static final int RELEASE_DAMPING_TICKS = 5;
    private static volatile boolean ASYNC = true;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Queue<Runnable> COMMANDS = new ConcurrentLinkedQueue<Runnable>();
    private static volatile Map<ResourceKey<Level>, List<PhysBody>> ACTIVE = Map.of();
    private static final double PHYS_LOD_RANGE = 96.0;
    private static final double PHYS_LOD_RANGE_SQ = 9216.0;
    private static volatile Thread PHYS_THREAD;
    private static volatile boolean RUNNING;
    private static final long STEP_NANOS = 50000000L;
    private static volatile Map<ResourceKey<Level>, List<Entry>> ACTIVE_ENTRIES;
    private static final double WAKE_MARGIN = 1.5;
    private static final double MAX_YAW_RATE = 0.22;
    private static final double MAX_FAN_IMPULSE = 3.0;
    private static final double MAX_FAN_SPIN = 0.03;
    private static final int SELF_RIGHT_LINGER_TICKS = 4;
    private static final double EXPLOSION_RADIUS_PER_POWER = 2.0;
    private static final double EXPLOSION_IMPULSE_PER_POWER = 3.0;
    private static final ExecutorService WORKERS;
    private static final double BAKE_LOOKAHEAD = 3.0;

    private PhysicsWorld() {
    }

    public static void setHeld(UUID contraptionId, boolean held) {
        if (contraptionId == null) {
            return;
        }
        if (held) {
            HELD.add(contraptionId);
            RELEASE_DAMPING.remove(contraptionId);
        } else {
            HELD.remove(contraptionId);
            RELEASE_DAMPING.put(contraptionId, 5);
        }
    }

    public static boolean isHeld(UUID contraptionId) {
        return HELD.contains(contraptionId);
    }

    private static boolean anyPlayerNear(ServerLevel level, ContraptionState state) {
        double x = state.x();
        double y = state.y();
        double z = state.z();
        for (ServerPlayer p : level.players()) {
            double dz;
            double dy;
            double dx = p.getX() - x;
            if (!(dx * dx + (dy = p.getY() - y) * dy + (dz = p.getZ() - z) * dz <= 9216.0)) continue;
            return true;
        }
        return false;
    }

    public static synchronized void startThread() {
        if (!ASYNC || RUNNING) {
            return;
        }
        RUNNING = true;
        Thread t = new Thread(PhysicsWorld::runPhysics, "cep-physics");
        t.setDaemon(true);
        t.setPriority(5);
        PHYS_THREAD = t;
        t.start();
        Bukkit.getLogger().info("[Contraption] async physics thread started (cep-physics)");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void runPhysics() {
        while (RUNNING) {
            Map<ResourceKey<Level>, List<Entry>> active;
            Runnable c;
            long t0 = System.nanoTime();
            LOCK.lock();
            try {
                while ((c = COMMANDS.poll()) != null) {
                    try {
                        c.run();
                    }
                    catch (Throwable throwable) {}
                }
                active = ACTIVE_ENTRIES;
            }
            finally {
                LOCK.unlock();
            }
            for (List<Entry> group : active.values()) {
                ArrayList<PhysBody> bodies = new ArrayList<PhysBody>(group.size());
                for (Entry e : group) {
                    bodies.add(e.physBody);
                }
                try {
                    PhysicsWorld.solveInParallel(bodies);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            LOCK.lock();
            try {
                Iterator<List<Entry>> iter = active.values().iterator();
                while (iter.hasNext()) {
                    List<Entry> group;
                    group = iter.next();
                    for (Entry e : group) {
                        RigidBody b = e.physBody.body;
                        Vector3d ip = e.physBody.impactPoint();
                        e.published = new BodyTransform(new Vector3d((Vector3dc)b.position), new Quaterniond((Quaterniondc)b.orientation), new Vector3d((Vector3dc)b.linearVelocity), e.physBody.maxImpactSpeed(), ip == null ? null : new Vector3d((Vector3dc)ip));
                    }
                }
            }
            finally {
                LOCK.unlock();
            }
            long sleep = 50000000L - (System.nanoTime() - t0);
            if (sleep <= 0L) continue;
            LockSupport.parkNanos(sleep);
        }
    }

    public static PhysBody bodyOf(UUID contraptionId) {
        Entry entry = ENTRIES.get(contraptionId);
        return entry == null ? null : entry.physBody;
    }

    public static void remove(UUID contraptionId) {
        ENTRIES.remove(contraptionId);
    }

    public static PhysBody ensureBody(ContraptionState state) {
        Entry entry = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
        PhysicsWorld.sync(entry, state);
        return entry.physBody;
    }

    public static void wakeNear(ResourceKey<Level> worldId, double x, double y, double z) {
        for (Map.Entry<UUID, Entry> e : ENTRIES.entrySet()) {
            BodyTransform pub;
            ContraptionEntity entity = ContraptionManager.get(e.getKey());
            if (entity == null || !entity.state().worldId().equals(worldId)) continue;
            Entry entry = e.getValue();
            PhysBody body = entry.physBody;
            if (body.shape.isEmpty()) continue;
            if (!ASYNC) {
                if (body.bakedRegion != null && body.bakedRegion.contains(x, y, z)) {
                    body.invalidateBake();
                }
                if (!body.worldBounds(1.5).contains(x, y, z)) continue;
                body.wakeUp();
                continue;
            }
            if (entry.mainBakedRegion != null && entry.mainBakedRegion.contains(x, y, z)) {
                entry.mainBakedRegion = null;
                PhysicsWorld.enqueue(body::invalidateBake);
            }
            if ((pub = entry.published) == null) continue;
            double r = body.shape.boundingRadius() * (entry.lastScale > 0.0 ? entry.lastScale : 1.0) + 1.5;
            if (!(Math.abs(pub.position().x - x) <= r) || !(Math.abs(pub.position().y - y) <= r) || !(Math.abs(pub.position().z - z) <= r)) continue;
            PhysicsWorld.enqueue(body::wakeUp);
        }
    }

    public static void applyExplosion(ResourceKey<Level> worldId, double x, double y, double z, double power) {
        for (ContraptionEntity entity : ContraptionManager.all()) {
            ServerLevel real;
            ContraptionState st = entity.state();
            if (!st.worldId().equals(worldId)) continue;
            Entry e = ENTRIES.get(st.id());
            if (e != null && e.physBody != null && !e.physBody.shape.isEmpty()) {
                PhysBody physBody = e.physBody;
                if (ASYNC) {
                    PhysicsWorld.enqueue(() -> PhysicsWorld.explodeBody(physBody, x, y, z, power));
                } else {
                    PhysicsWorld.explodeBody(physBody, x, y, z, power);
                }
            }
            if (!(st.explosionProof() < 1.0) || (real = PhysicsWorld.resolveLevel(st)) == null) continue;
            PhysicsWorld.carveExplosion(st, x, y, z, power, real);
        }
    }

    private static void carveExplosion(ContraptionState state, double x, double y, double z, double power, ServerLevel realLevel) {
        ContraptionLevel level = state.level();
        if (level == null) {
            return;
        }
        double radius = power * 2.0;
        Vec3 blast = new Vec3(x, y, z);
        double cutoffBase = power * 50.0 * (1.0 - state.explosionProof());
        ArrayList<BlockPos> toBreak = new ArrayList<BlockPos>();
        for (BlockPos local : level.localPositions()) {
            BlockState bs;
            Vec3 world = level.realWorldPositionOf(new Vec3((double)local.getX() + 0.5, (double)local.getY() + 0.5, (double)local.getZ() + 0.5));
            double d = world.distanceTo(blast);
            if (d > radius || (bs = level.getBlockState(local)).isAir()) continue;
            double falloff = 1.0 - d / radius;
            if (!((double)bs.getBlock().getExplosionResistance() < cutoffBase * falloff)) continue;
            toBreak.add(local.immutable());
        }
        if (toBreak.isEmpty()) {
            return;
        }
        int quiet = 18;
        for (BlockPos local : toBreak) {
            BlockState bs = level.getBlockState(local);
            if (bs.isAir()) continue;
            Vec3 world = level.realWorldPositionOf(new Vec3((double)local.getX() + 0.5, (double)local.getY() + 0.5, (double)local.getZ() + 0.5));
            BlockPos wp = BlockPos.containing((double)world.x, (double)world.y, (double)world.z);
            try {
                for (ItemStack drop : Block.getDrops((BlockState)bs, (ServerLevel)realLevel, (BlockPos)local, (BlockEntity)level.getBlockEntity(local))) {
                    Block.popResource((Level)realLevel, (BlockPos)wp, (ItemStack)drop);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            level.setBlock(local, Blocks.AIR.defaultBlockState(), quiet);
        }
        level.markCellsDirty();
        level.refreshLocalPositions();
    }

    private static void explodeBody(PhysBody physBody, double x, double y, double z, double power) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        double radius = power * 2.0;
        Vector3d blast = new Vector3d(x, y, z);
        RigidBody body = physBody.body;
        double reach = physBody.shape.boundingRadius() * body.scale() + radius;
        if (body.position.distanceSquared((Vector3dc)blast) > reach * reach) {
            return;
        }
        boolean hit = false;
        for (AABB box : physBody.shape.boxes()) {
            Vector3d localCentre = new Vector3d((box.minX + box.maxX) * 0.5, (box.minY + box.maxY) * 0.5, (box.minZ + box.maxZ) * 0.5);
            Vector3d at = body.toWorld(localCentre, new Vector3d());
            Vector3d away = new Vector3d((Vector3dc)at).sub((Vector3dc)blast);
            double dist = away.length();
            if (dist > radius) continue;
            if (dist < 1.0E-4) {
                away.set(0.0, 1.0, 0.0);
            } else {
                away.div(dist);
            }
            double scale3 = body.scale() * body.scale() * body.scale();
            double volume = (box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ) * scale3;
            double falloff = 1.0 - dist / radius;
            Vector3d impulse = new Vector3d((Vector3dc)away).mul(power * falloff * volume * 3.0);
            Vector3d r = new Vector3d((Vector3dc)at).sub((Vector3dc)body.position);
            body.linearVelocity.fma(body.inverseMass(), (Vector3dc)impulse);
            Vector3d torque = new Vector3d((Vector3dc)r).cross((Vector3dc)impulse);
            body.angularVelocity.add((Vector3dc)new Matrix3d((Matrix3dc)body.inverseInertiaWorld()).transform(torque));
            hit = true;
        }
        if (hit) {
            physBody.wakeUp();
        }
    }

    public static void applyThrust(UUID contraptionId, Vector3d worldPoint, Vector3d worldImpulse) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        Vector3d point = new Vector3d((Vector3dc)worldPoint);
        Vector3d impulse = new Vector3d((Vector3dc)worldImpulse);
        if (ASYNC) {
            PhysicsWorld.enqueue(() -> PhysicsWorld.thrustBody(body, point, impulse));
        } else {
            PhysicsWorld.thrustBody(body, point, impulse);
        }
    }

    public static void applyThrustCentral(UUID contraptionId, Vector3d worldImpulse) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        Vector3d impulse = new Vector3d((Vector3dc)worldImpulse);
        if (ASYNC) {
            PhysicsWorld.enqueue(() -> PhysicsWorld.thrustCentralBody(body, impulse));
        } else {
            PhysicsWorld.thrustCentralBody(body, impulse);
        }
    }

    public static void setLinearVelocity(UUID contraptionId, Vector3d velocity) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody phys = entry.physBody;
        Vector3d v = new Vector3d(velocity.x, velocity.y, velocity.z);
        if (ASYNC) {
            PhysicsWorld.enqueue(() -> {
                phys.body.linearVelocity.set((Vector3dc)v);
                phys.wakeUp();
            });
        } else {
            phys.body.linearVelocity.set((Vector3dc)v);
            phys.wakeUp();
        }
    }

    public static void syncBodyPosition(UUID contraptionId, double x, double y, double z) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody phys = entry.physBody;
        Vector3d com = PhysicsWorld.localCom(entry);
        Vector3d target = new Vector3d(x + com.x, y + com.y, z + com.z);
        if (ASYNC) {
            PhysicsWorld.enqueue(() -> {
                phys.body.position.set((Vector3dc)target);
                phys.wakeUp();
            });
        } else {
            phys.body.position.set((Vector3dc)target);
            phys.wakeUp();
        }
    }

    public static void dampAngularVelocity(UUID contraptionId, double factor) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody phys = entry.physBody;
        if (ASYNC) {
            PhysicsWorld.enqueue(() -> phys.body.angularVelocity.mul(factor));
        } else {
            phys.body.angularVelocity.mul(factor);
        }
    }

    public static void setYawRate(UUID contraptionId, double omegaY) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry == null || entry.physBody == null) {
            return;
        }
        PhysBody body = entry.physBody;
        double w = Math.max(-0.22, Math.min(0.22, omegaY));
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
            PhysicsWorld.enqueue(r);
        } else {
            r.run();
        }
    }

    private static void thrustCentralBody(PhysBody physBody, Vector3d impulse) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        double mag = impulse.length();
        if (mag > 3.0) {
            impulse = new Vector3d((Vector3dc)impulse).mul(3.0 / mag);
        }
        physBody.body.linearVelocity.fma(physBody.body.inverseMass(), (Vector3dc)impulse);
        physBody.selfRightTicks = 4;
        physBody.wakeUp();
    }

    private static void thrustBody(PhysBody physBody, Vector3d worldPoint, Vector3d impulse) {
        if (physBody.kinematic || physBody.shape.isEmpty() || physBody.body.isStatic()) {
            return;
        }
        RigidBody body = physBody.body;
        double mag = impulse.length();
        if (mag > 3.0) {
            impulse = new Vector3d((Vector3dc)impulse).mul(3.0 / mag);
        }
        body.linearVelocity.fma(body.inverseMass(), (Vector3dc)impulse);
        Vector3d r = new Vector3d((Vector3dc)worldPoint).sub((Vector3dc)body.position);
        Vector3d dOmega = new Matrix3d((Matrix3dc)body.inverseInertiaWorld()).transform(new Vector3d((Vector3dc)r).cross((Vector3dc)impulse));
        double spin = dOmega.length();
        if (spin > 0.03) {
            dOmega.mul(0.03 / spin);
        }
        body.angularVelocity.add((Vector3dc)dOmega);
        physBody.selfRightTicks = 4;
        physBody.wakeUp();
    }

    public static void resync(UUID contraptionId) {
        Entry entry = ENTRIES.get(contraptionId);
        if (entry != null) {
            entry.initialized = false;
            if (ASYNC) {
                PhysicsWorld.enqueue(entry.physBody::wakeUp);
            } else {
                entry.physBody.wakeUp();
            }
        }
    }

    public static void stepAll() {
        if (ASYNC) {
            if (!RUNNING) {
                PhysicsWorld.startThread();
            }
            PhysicsWorld.asyncPump();
        } else {
            PhysicsWorld.stepAllSync();
        }
    }

    private static void stepAllSync() {
        LinkedHashMap<ResourceKey, List> byWorld = new LinkedHashMap<ResourceKey, List>();
        HashMap<ResourceKey<Level>, ServerLevel> levels = new HashMap<ResourceKey<Level>, ServerLevel>();
        HashMap<Entry, ContraptionState> states = new HashMap<Entry, ContraptionState>();
        ArrayList<ContraptionEntity> splitCandidates = null;
        for (ContraptionEntity contraptionEntity : ContraptionManager.all()) {
            ContraptionState state = contraptionEntity.state();
            PhysicsBehavior physics = PhysicsWorld.physicsBehaviorOf(state);
            if (physics == null) continue;
            ServerLevel level = PhysicsWorld.resolveLevel(state);
            if (level == null) {
                physics.clearPendingMotion();
                continue;
            }
            Entry entry2 = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
            if (PhysicsWorld.sync(entry2, state)) {
                if (splitCandidates == null) {
                    splitCandidates = new ArrayList<ContraptionEntity>();
                }
                splitCandidates.add(contraptionEntity);
            }
            physics.attach(entry2.physBody, entry2.massModel);
            states.put(entry2, state);
            levels.put(state.worldId(), level);
            if (!PhysicsWorld.anyPlayerNear(level, state)) continue;
            byWorld.computeIfAbsent(state.worldId(), w -> new ArrayList()).add(entry2);
        }
        if (byWorld.isEmpty()) {
            return;
        }
        ENTRIES.keySet().removeIf(id -> ContraptionManager.get(id) == null);
        for (Map.Entry<?, ?> entry : byWorld.entrySet()) {
            ServerLevel level = (ServerLevel)levels.get(entry.getKey());
            @SuppressWarnings("unchecked") List<Entry> entryList = (List<Entry>)entry.getValue();
            ArrayList<PhysBody> bodies = new ArrayList<PhysBody>(entryList.size());
            for (Entry entry2 : entryList) {
                PhysBody body = entry2.physBody;
                if (!body.kinematic && !body.isAsleep()) {
                    PhysicsWorld.rebakeIfNeeded(body, level);
                }
                bodies.add(body);
            }
            PhysicsWorld.solveInParallel(bodies);
            for (Entry entry2 : entryList) {
                Integer dampTicks;
                PhysicsWorld.writeBack(entry2, (ContraptionState)states.get(entry2));
                ContraptionState relState = (ContraptionState)states.get(entry2);
                if (relState != null && (dampTicks = RELEASE_DAMPING.get(relState.id())) != null) {
                    if (dampTicks <= 0) {
                        RELEASE_DAMPING.remove(relState.id());
                    } else {
                        RELEASE_DAMPING.put(relState.id(), dampTicks - 1);
                        double factor = 0.2 + 0.16 * (double)(5 - dampTicks);
                        entry2.physBody.body.linearVelocity.mul(factor);
                        entry2.physBody.body.angularVelocity.mul(factor);
                    }
                }
                try {
                    ContraptionImpactDetonator.afterStep(entry2.physBody, (ContraptionState)states.get(entry2), level);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        if (splitCandidates != null) {
            for (ContraptionEntity contraptionEntity : splitCandidates) {
                try {
                    ContraptionSplitter.splitIfDisconnected(contraptionEntity);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private static void enqueue(Runnable command) {
        COMMANDS.add(command);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void asyncPump() {
        ServerLevel level;
        LinkedHashMap<ResourceKey<Level>, List<Entry>> byWorld = new LinkedHashMap<ResourceKey<Level>, List<Entry>>();
        HashMap<Entry, ContraptionState> states = new HashMap<Entry, ContraptionState>();
        HashMap<ResourceKey<Level>, ServerLevel> levels = new HashMap<ResourceKey<Level>, ServerLevel>();
        ArrayList<ContraptionEntity> splitCandidates = null;
        for (ContraptionEntity contraptionEntity : ContraptionManager.all()) {
            ContraptionState state = contraptionEntity.state();
            PhysicsBehavior physics = PhysicsWorld.physicsBehaviorOf(state);
            if (physics == null) continue;
            ServerLevel level2 = PhysicsWorld.resolveLevel(state);
            if (level2 == null) {
                physics.clearPendingMotion();
                continue;
            }
            Entry entry = ENTRIES.computeIfAbsent(state.id(), id -> new Entry());
            if (PhysicsWorld.syncMain(entry, state)) {
                if (splitCandidates == null) {
                    splitCandidates = new ArrayList<ContraptionEntity>();
                }
                splitCandidates.add(contraptionEntity);
            }
            physics.attach(entry.physBody, entry.massModel);
            states.put(entry, state);
            levels.put(state.worldId(), level2);
            if (!PhysicsWorld.anyPlayerNear(level2, state)) continue;
            byWorld.computeIfAbsent(state.worldId(), w -> new ArrayList()).add(entry);
        }
        ENTRIES.keySet().removeIf(id -> ContraptionManager.get(id) == null);
        for (Map.Entry<?, ?> entry : byWorld.entrySet()) {
            level = (ServerLevel)levels.get(entry.getKey());
            @SuppressWarnings("unchecked") List<Entry> entryList2 = (List<Entry>)entry.getValue();
            for (Entry entry2 : entryList2) {
                PhysBody body = entry2.physBody;
                if (body.kinematic || body.isAsleep()) continue;
                PhysicsWorld.rebakeMain(entry2, level);
            }
        }
        LOCK.lock();
        try {
            ACTIVE_ENTRIES = byWorld;
        }
        finally {
            LOCK.unlock();
        }
        for (Map.Entry<?, ?> entry : byWorld.entrySet()) {
            level = (ServerLevel)levels.get(entry.getKey());
            @SuppressWarnings("unchecked") List<Entry> entryList3 = (List<Entry>)entry.getValue();
            for (Entry entry2 : entryList3) {
                BodyTransform pub = entry2.published;
                if (pub == null) continue;
                ContraptionState state = (ContraptionState)states.get(entry2);
                PhysicsWorld.writeBackAsync(entry2, state, pub);
                try {
                    ContraptionImpactDetonator.afterStep(pub.maxImpactSpeed(), pub.impactPoint(), state, level);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        if (splitCandidates != null) {
            for (ContraptionEntity contraptionEntity : splitCandidates) {
                try {
                    ContraptionSplitter.splitIfDisconnected(contraptionEntity);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private static boolean syncMain(Entry entry, ContraptionState state) {
        boolean foreignMove;
        boolean kin;
        double scale;
        boolean cellsChanged;
        ContraptionLevel contraptionLevel = state.level();
        PhysBody body = entry.physBody;
        int shape = contraptionLevel == null ? 0 : contraptionLevel.localPositions().hashCode();
        boolean firstObservation = !entry.shapeObserved;
        boolean bl = cellsChanged = firstObservation || shape != entry.lastShapeHash;
        if (cellsChanged) {
            boolean bigComJump;
            MassModel mm;
            Vector3d oldCom = new Vector3d(entry.massModel.centerOfMass().x, entry.massModel.centerOfMass().y, entry.massModel.centerOfMass().z);
            entry.lastShapeHash = shape;
            entry.shapeObserved = true;
            entry.massModel = mm = MassModel.of(contraptionLevel);
            Vector3d com = new Vector3d(mm.centerOfMass().x, mm.centerOfMass().y, mm.centerOfMass().z);
            CollisionShape newShape = CollisionShape.of(contraptionLevel, com);
            double floatability = FloatabilityModel.of(contraptionLevel).floatability();
            double friction = FrictionModel.of(contraptionLevel).friction();
            ToDoubleFunction<Vector3d> restitutionField = PhysicsWorld.buildRestitutionField(contraptionLevel, com, body.body);
            double scaleNow = entry.lastScale > 0.0 ? entry.lastScale : 1.0;
            Vector3d comDelta = firstObservation ? null : new Vector3d((Vector3dc)com).sub((Vector3dc)oldCom);
            boolean bl2 = bigComJump = comDelta != null && comDelta.length() > 1.0;
            if (bigComJump) {
                comDelta = null;
            }
            Vector3d comShift = comDelta;
            PhysicsWorld.enqueue(() -> {
                boolean settled;
                boolean blSettled = settled = body.isAsleep() || body.body.linearVelocity.length() < 0.001 && body.body.angularVelocity.length() < 0.001;
                if (comShift != null && comShift.lengthSquared() > 1.0E-12) {
                    Vector3d worldShift = new Vector3d((Vector3dc)comShift).mul(scaleNow).rotate((Quaterniondc)body.body.orientation);
                    body.body.position.add((Vector3dc)worldShift);
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
            if (firstObservation || bigComJump) {
                entry.initialized = false;
            }
        }
        if ((scale = state.scale()) != entry.lastScale) {
            entry.lastScale = scale;
            PhysicsWorld.enqueue(() -> {
                body.body.setScale(scale);
                body.wakeUp();
            });
            entry.initialized = false;
        }
        boolean bl3 = kin = state.isStalled() || PhysicsWorld.isHeld(state.id());
        if (body.kinematic != kin) {
            PhysicsWorld.enqueue(() -> {
                body.kinematic = kin;
            });
        }
        boolean bl4 = foreignMove = !entry.initialized || entry.lastWrittenPosition == null || Math.abs(state.x() - entry.lastWrittenPosition.x) > 1.0E-6 || Math.abs(state.y() - entry.lastWrittenPosition.y) > 1.0E-6 || Math.abs(state.z() - entry.lastWrittenPosition.z) > 1.0E-6 || Math.abs(state.yawRadians() - entry.lastWrittenYaw) > 1.0E-6 || Math.abs(state.pitchRadians() - entry.lastWrittenPitch) > 1.0E-6 || Math.abs(state.rollRadians() - entry.lastWrittenRoll) > 1.0E-6;
        if (foreignMove) {
            Quaterniond orientation = ContraptionTransform.fromEuler(state.yawRadians(), state.pitchRadians(), state.rollRadians());
            Vector3d com = PhysicsWorld.localCom(entry);
            Vector3d comWorld = ContraptionTransform.comWorld(new Vec3(state.x(), state.y(), state.z()), orientation, com, state.scale());
            PhysicsWorld.enqueue(() -> {
                body.body.orientation.set((Quaterniondc)orientation);
                body.body.position.set((Vector3dc)comWorld);
                body.wakeUp();
            });
            entry.initialized = true;
            if (entry.published == null) {
                entry.published = new BodyTransform(new Vector3d((Vector3dc)comWorld), new Quaterniond((Quaterniondc)orientation), new Vector3d(), 0.0, null);
            }
        }
        return cellsChanged;
    }

    private static void rebakeMain(Entry entry, ServerLevel level) {
        BodyTransform pub = entry.published;
        if (pub == null) {
            return;
        }
        Vector3d pos = pub.position();
        double scale = entry.lastScale > 0.0 ? entry.lastScale : 1.0;
        double radius = entry.physBody.shape.boundingRadius() * scale;
        double margin = pub.linearVelocity().length() + 0.05 + 2.0;
        AABB needed = PhysicsWorld.boxAround(pos, radius + margin);
        if (entry.mainBakedRegion != null && PhysicsWorld.encloses(entry.mainBakedRegion, needed)) {
            return;
        }
        AABB region = PhysicsWorld.boxAround(pos, radius + margin + 3.0);
        WorldBlockCache cache = WorldBlockCache.bake(level, region);
        PhysBody body = entry.physBody;
        PhysicsWorld.enqueue(() -> {
            body.world = cache;
            body.bakedRegion = region;
        });
        entry.mainBakedRegion = region;
    }

    private static AABB boxAround(Vector3d c, double r) {
        return new AABB(c.x - r, c.y - r, c.z - r, c.x + r, c.y + r, c.z + r);
    }

    private static void writeBackAsync(Entry entry, ContraptionState state, BodyTransform pub) {
        if (state == null) {
            return;
        }
        double[] euler = ContraptionTransform.eulerYXZ(pub.orientation());
        Vec3 bearing = ContraptionTransform.bearingOrigin(pub.position(), pub.orientation(), PhysicsWorld.localCom(entry), state.scale());
        PhysicsBehavior behavior = PhysicsWorld.physicsBehaviorOf(state);
        if (behavior != null) {
            behavior.setPendingMotion(new Vec3(bearing.x - state.x(), bearing.y - state.y(), bearing.z - state.z()), euler[0], euler[1], euler[2]);
            BearingHammerListener.markAssembled(state.worldId(), BlockPos.containing((double)bearing.x, (double)bearing.y, (double)bearing.z), state.id());
        }
        entry.lastWrittenPosition = bearing;
        entry.lastWrittenYaw = euler[0];
        entry.lastWrittenPitch = euler[1];
        entry.lastWrittenRoll = euler[2];
        entry.initialized = true;
    }

    private static void solveInParallel(List<PhysBody> bodies) {
        if (bodies.isEmpty()) {
            return;
        }
        List<List<PhysBody>> islands = PhysicsWorld.islands(bodies);
        if (islands.size() < 2) {
            for (List<PhysBody> island : islands) {
                XpbdSolver.step(island, 1.0);
            }
            return;
        }
        ArrayList<Future<?>> pending = new ArrayList<>(islands.size());
        for (List<PhysBody> island : islands) {
            pending.add(WORKERS.submit(() -> XpbdSolver.step(island, 1.0)));
        }
        for (Future<?> future : pending) {
            try {
                future.get();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<List<PhysBody>> islands(List<PhysBody> bodies) {
        int i;
        int n = bodies.size();
        int[] parent = new int[n];
        for (i = 0; i < n; ++i) {
            parent[i] = i;
        }
        for (i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                PhysBody a = bodies.get(i);
                PhysBody b = bodies.get(j);
                if (!ContactGenerator.broadphaseOverlap(a.body, a.shape, b.body, b.shape)) continue;
                PhysicsWorld.union(parent, i, j);
            }
        }
        LinkedHashMap<Integer, List<PhysBody>> grouped = new LinkedHashMap<>();
        for (int i2 = 0; i2 < n; ++i2) {
            grouped.computeIfAbsent(PhysicsWorld.find(parent, i2), k -> new ArrayList<>()).add(bodies.get(i2));
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
        int rb;
        int ra = PhysicsWorld.find(parent, a);
        if (ra != (rb = PhysicsWorld.find(parent, b))) {
            parent[rb] = ra;
        }
    }

    public static void shutdown() {
        RUNNING = false;
        Thread t = PHYS_THREAD;
        if (t != null) {
            LockSupport.unpark(t);
            try {
                t.join(1000L);
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        WORKERS.shutdownNow();
    }

    private static void rebakeIfNeeded(PhysBody body, ServerLevel level) {
        double speed = body.body.linearVelocity.length();
        double margin = speed + 0.05 + 2.0;
        AABB needed = body.worldBounds(margin);
        if (body.bakedRegion != null && PhysicsWorld.encloses(body.bakedRegion, needed)) {
            return;
        }
        AABB region = body.worldBounds(margin + 3.0);
        body.world = WorldBlockCache.bake(level, region);
        body.bakedRegion = region;
    }

    private static boolean encloses(AABB outer, AABB inner) {
        return outer.minX <= inner.minX && outer.maxX >= inner.maxX && outer.minY <= inner.minY && outer.maxY >= inner.maxY && outer.minZ <= inner.minZ && outer.maxZ >= inner.maxZ;
    }

    private static boolean sync(Entry entry, ContraptionState state) {
        boolean foreignMove;
        double scale;
        boolean cellsChanged;
        PhysBody body = entry.physBody;
        ContraptionLevel contraptionLevel = state.level();
        int shape = contraptionLevel == null ? 0 : contraptionLevel.localPositions().hashCode();
        boolean bl = cellsChanged = !entry.shapeObserved || shape != entry.lastShapeHash;
        if (cellsChanged) {
            entry.lastShapeHash = shape;
            entry.shapeObserved = true;
            entry.massModel = MassModel.of(contraptionLevel);
            Vector3d com = new Vector3d(entry.massModel.centerOfMass().x, entry.massModel.centerOfMass().y, entry.massModel.centerOfMass().z);
            body.shape = CollisionShape.of(contraptionLevel, com);
            body.body.setMassProperties(entry.massModel.inverseMass(), entry.massModel.inverseInertiaTensor());
            body.floatability = FloatabilityModel.of(contraptionLevel).floatability();
            body.body.setFriction(FrictionModel.of(contraptionLevel).friction());
            body.body.setRestitutionField(PhysicsWorld.buildRestitutionField(contraptionLevel, com, body.body));
            entry.initialized = false;
            body.wakeUp();
        }
        if ((scale = state.scale()) != entry.lastScale) {
            entry.lastScale = scale;
            body.body.setScale(scale);
            entry.initialized = false;
            body.wakeUp();
        }
        body.kinematic = state.isStalled() || PhysicsWorld.isHeld(state.id());
        boolean bl2 = foreignMove = !entry.initialized || entry.lastWrittenPosition == null || Math.abs(state.x() - entry.lastWrittenPosition.x) > 1.0E-6 || Math.abs(state.y() - entry.lastWrittenPosition.y) > 1.0E-6 || Math.abs(state.z() - entry.lastWrittenPosition.z) > 1.0E-6 || Math.abs(state.yawRadians() - entry.lastWrittenYaw) > 1.0E-6 || Math.abs(state.pitchRadians() - entry.lastWrittenPitch) > 1.0E-6 || Math.abs(state.rollRadians() - entry.lastWrittenRoll) > 1.0E-6;
        if (foreignMove) {
            PhysicsWorld.adoptStateTransform(entry, state);
        }
        return cellsChanged;
    }

    private static void adoptStateTransform(Entry entry, ContraptionState state) {
        RigidBody body = entry.physBody.body;
        Quaterniond orientation = ContraptionTransform.fromEuler(state.yawRadians(), state.pitchRadians(), state.rollRadians());
        body.orientation.set((Quaterniondc)orientation);
        Vector3d com = PhysicsWorld.localCom(entry);
        Vector3d comWorld = ContraptionTransform.comWorld(new Vec3(state.x(), state.y(), state.z()), orientation, com, state.scale());
        body.position.set((Vector3dc)comWorld);
        entry.initialized = true;
        entry.physBody.wakeUp();
    }

    private static void writeBack(Entry entry, ContraptionState state) {
        if (state == null) {
            return;
        }
        RigidBody body = entry.physBody.body;
        double[] euler = ContraptionTransform.eulerYXZ(body.orientation);
        Vec3 bearing = ContraptionTransform.bearingOrigin(body.position, body.orientation, PhysicsWorld.localCom(entry), state.scale());
        PhysicsBehavior behavior = PhysicsWorld.physicsBehaviorOf(state);
        if (behavior != null) {
            behavior.setPendingMotion(new Vec3(bearing.x - state.x(), bearing.y - state.y(), bearing.z - state.z()), euler[0], euler[1], euler[2]);
        }
        if (behavior != null) {
            BearingHammerListener.markAssembled(state.worldId(), BlockPos.containing((double)bearing.x, (double)bearing.y, (double)bearing.z), state.id());
        }
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

    private static ToDoubleFunction<Vector3d> buildRestitutionField(ContraptionLevel level, Vector3d com, RigidBody body) {
        HashMap<Long, Float> restMap = new HashMap<Long, Float>();
        for (BlockPos local : level.localPositions()) {
            double r;
            BlockState state = level.getBlockState(local);
            if (state.isAir() || !((r = RestitutionBlockBehavior.restitutionOf(state)) > 0.0)) continue;
            restMap.put(local.asLong(), Float.valueOf((float)r));
        }
        if (restMap.isEmpty()) {
            return worldPoint -> 0.0;
        }
        Vector3d comSnapshot = new Vector3d((Vector3dc)com);
        return worldPoint -> PhysicsWorld.restitutionAtWorld(worldPoint, body, comSnapshot, restMap);
    }

    private static double restitutionAtWorld(Vector3d worldPoint, RigidBody body, Vector3d com, Map<Long, Float> restMap) {
        Vector3d d = new Vector3d((Vector3dc)worldPoint).sub((Vector3dc)body.position);
        new Quaterniond((Quaterniondc)body.orientation).conjugate().transform(d);
        double scale = body.scale();
        if (scale > 1.0E-9) {
            d.div(scale);
        }
        double lx = d.x + com.x;
        double ly = d.y + com.y;
        double lz = d.z + com.z;
        double best = 0.0;
        double eps = 0.02;
        for (double ox = -eps; ox <= eps; ox += 2.0 * eps) {
            for (double oy = -eps; oy <= eps; oy += 2.0 * eps) {
                for (double oz = -eps; oz <= eps; oz += 2.0 * eps) {
                    long key = BlockPos.asLong((int)((int)Math.floor(lx + ox)), (int)((int)Math.floor(ly + oy)), (int)((int)Math.floor(lz + oz)));
                    Float r = restMap.get(key);
                    if (r == null || !((double)r.floatValue() > best)) continue;
                    best = r.floatValue();
                }
            }
        }
        return best;
    }

    private static PhysicsBehavior physicsBehaviorOf(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (!(behavior instanceof PhysicsBehavior)) continue;
            PhysicsBehavior physics = (PhysicsBehavior)behavior;
            return physics;
        }
        return null;
    }

    private static ServerLevel resolveLevel(ContraptionState state) {
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel serverLevel = server.getLevel(state.worldId());
            CraftWorld world = serverLevel != null ? serverLevel.getWorld() : null;
            return world == null ? null : world.getHandle();
        }
        catch (Throwable t) {
            return null;
        }
    }

    static {
        ACTIVE_ENTRIES = Map.of();
        WORKERS = Executors.newFixedThreadPool(Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() - 2)), r -> {
            Thread t = new Thread(r, "contraption-physics");
            t.setDaemon(true);
            return t;
        });
    }

    private static final class Entry {
        final PhysBody physBody = new PhysBody();
        MassModel massModel = MassModel.EMPTY;
        int lastShapeHash;
        boolean shapeObserved;
        double lastScale = Double.NaN;
        Vec3 lastWrittenPosition;
        double lastWrittenYaw;
        double lastWrittenPitch;
        double lastWrittenRoll;
        boolean initialized;
        volatile BodyTransform published;
        AABB mainBakedRegion;

        private Entry() {
        }
    }

    private record BodyTransform(Vector3d position, Quaterniond orientation, Vector3d linearVelocity, double maxImpactSpeed, Vector3d impactPoint) {
    }
}

