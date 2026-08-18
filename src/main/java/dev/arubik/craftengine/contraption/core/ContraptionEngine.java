/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.arubik.craftengine.contraption.core;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.ContraptionPerf;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.behavior.ContraptionHopperBridge;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.event.ContraptionMoveEvent;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.physics.ContraptionSplitter;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.contraption.type.GhastContraptionType;
import dev.arubik.craftengine.contraption.type.LinearContraptionType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ContraptionEngine {
    public static volatile boolean ENABLED = true;
    private static final int FOLLOW_RADIUS = 4;
    private static final Map<UUID, Set<Long>> FOLLOW_CHUNKS = new HashMap<UUID, Set<Long>>();
    private static int ambientTickCounter = 0;
    private static final Random RANDOM = new Random();

    private ContraptionEngine() {
    }

    public static void tickAll() {
        World w;
        if (!ENABLED) {
            return;
        }
        boolean perf = ContraptionPerf.enabled();
        if (perf) {
            ContraptionPerf.tickStart();
        }
        HashMap<UUID, List<Player>> viewerCache = new HashMap<UUID, List<Player>>();
        HashMap<UUID, List<ServerPlayer>> carryCache = new HashMap<UUID, List<ServerPlayer>>();
        try {
            LinearContraptionType.tickExtendedSolids();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        long physStart = perf ? ContraptionPerf.begin() : 0L;
        try {
            PhysicsWorld.stepAll();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.PHYSICS, physStart);
        }
        ArrayList<ContraptionEntity> disassembleAtEnd = null;
        ArrayList<ContraptionEntity> minecartDeadAtEnd = null;
        ArrayList<ContraptionEntity> ghastFractureAtEnd = null;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            World bukkitWorld = ContraptionEngine.safeGetWorld(entity.state());
            ServerLevel level = bukkitWorld != null ? ((CraftWorld)bukkitWorld).getHandle() : null;
            ContraptionState state = entity.state();
            if (state.level() != null) {
                state.level().tickBlockEntities();
            }
            Vec3 posBefore = new Vec3(state.x(), state.y(), state.z());
            double yawBefore = state.yawRadians();
            long behaviorStart = perf ? ContraptionPerf.begin() : 0L;
            ContraptionEngine.stepKinematics(state, level);
            if (perf) {
                ContraptionPerf.end(ContraptionPerf.Phase.BEHAVIORS, behaviorStart);
            }
            boolean crossedWorld = false;
            World worldNow = ContraptionEngine.safeGetWorld(state);
            if (worldNow != bukkitWorld) {
                crossedWorld = true;
                bukkitWorld = worldNow;
                ServerLevel serverLevel = level = bukkitWorld != null ? ((CraftWorld)bukkitWorld).getHandle() : null;
            }
            if (!(bukkitWorld == null || crossedWorld || state.x() == posBefore.x && state.y() == posBefore.y && state.z() == posBefore.z && state.yawRadians() == yawBefore)) {
                ContraptionEngine.fireMove(entity, bukkitWorld, posBefore, new Vec3(state.x(), state.y(), state.z()), state.yawRadians() - yawBefore);
            }
            ContraptionEngine.followRealChunks(entity, bukkitWorld);
            if (bukkitWorld == null) continue;
            boolean chunkLoaded = PhysicsWorld.isHeld(state.id()) || ContraptionEngine.isAtLoadedChunk(bukkitWorld, state);
            try {
                ContraptionEngine.render(entity, bukkitWorld, level, viewerCache, carryCache, chunkLoaded, perf);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (chunkLoaded) {
                try {
                    ContraptionHopperBridge.tick(entity);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            if (ContraptionEngine.pistonWantsDisassemble(entity)) {
                if (disassembleAtEnd == null) {
                    disassembleAtEnd = new ArrayList<ContraptionEntity>();
                }
                disassembleAtEnd.add(entity);
            }
            if (ContraptionEngine.ghastWantsFractureCheck(entity)) {
                if (ghastFractureAtEnd == null) {
                    ghastFractureAtEnd = new ArrayList<ContraptionEntity>();
                }
                ghastFractureAtEnd.add(entity);
            }
            if (!ContraptionEngine.minecartAnchorDead(entity) && !ContraptionEngine.ghastAnchorDead(entity)) continue;
            if (minecartDeadAtEnd == null) {
                minecartDeadAtEnd = new ArrayList<ContraptionEntity>();
            }
            minecartDeadAtEnd.add(entity);
        }
        if (ghastFractureAtEnd != null) {
            for (ContraptionEntity entity : ghastFractureAtEnd) {
                try {
                    ContraptionSplitter.splitIfDisconnected(entity);
                    ContraptionEngine.resaveGhastStructure(entity);
                }
                catch (Throwable t) {
                    Bukkit.getLogger().warning("[Contraption] ghast fracture split failed: " + String.valueOf(t));
                }
            }
        }
        if (minecartDeadAtEnd != null) {
            for (ContraptionEntity entity : minecartDeadAtEnd) {
                w = ContraptionEngine.safeGetWorld(entity.state());
                if (w == null) continue;
                try {
                    if (ContraptionEngine.ghastAnchorDead(entity)) {
                        GhastContraptionType.disassembleInPlace(w, null, entity);
                    } else {
                        MinecartBearing.disassembleInPlace(w, entity);
                    }
                    BearingHammerListener.forgetAssembled(entity.state().id());
                }
                catch (Throwable t) {
                    Bukkit.getLogger().warning("[Contraption] dead-anchor disassemble failed: " + String.valueOf(t));
                }
            }
        }
        if (disassembleAtEnd != null) {
            for (ContraptionEntity entity : disassembleAtEnd) {
                w = ContraptionEngine.safeGetWorld(entity.state());
                if (w == null) continue;
                try {
                    ContraptionEngine.recordEulerDrop(entity);
                    ContraptionAssembler.disassemble(w, entity);
                    BearingHammerListener.forgetAssembled(entity.state().id());
                }
                catch (Throwable t) {
                    Bukkit.getLogger().warning("[Contraption] piston disassemble-at-end failed: " + String.valueOf(t));
                }
            }
        }
        if (perf) {
            ContraptionPerf.tickEnd();
        }
    }

    private static void followRealChunks(ContraptionEntity entity, World world) {
        long key;
        boolean ridden;
        UUID id = entity.state().id();
        boolean bl = ridden = !entity.state().seatedRiders().isEmpty();
        if (world == null || !ridden) {
            ContraptionEngine.clearFollowChunks(id, world);
            return;
        }
        JavaPlugin plugin = JavaPlugin.getPlugin(CraftEnginePolyfills.class);
        int cx = Mth.floor((double)entity.state().x()) >> 4;
        int cz = Mth.floor((double)entity.state().z()) >> 4;
        HashSet<Long> desired = new HashSet<Long>();
        for (int dx = -4; dx <= 4; ++dx) {
            for (int dz = -4; dz <= 4; ++dz) {
                desired.add(ChunkPos.asLong((int)(cx + dx), (int)(cz + dz)));
            }
        }
        Set current = FOLLOW_CHUNKS.computeIfAbsent(id, k -> new HashSet());
        Iterator dz = desired.iterator();
        while (dz.hasNext()) {
            key = (Long)dz.next();
            if (!current.add(key)) continue;
            try {
                world.addPluginChunkTicket(ChunkPos.getX((long)key), ChunkPos.getZ((long)key), (Plugin)plugin);
            }
            catch (Throwable throwable) {}
        }
        Iterator it = current.iterator();
        while (it.hasNext()) {
            key = (Long)it.next();
            if (desired.contains(key)) continue;
            try {
                world.removePluginChunkTicket(ChunkPos.getX((long)key), ChunkPos.getZ((long)key), (Plugin)plugin);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            it.remove();
        }
    }

    private static void clearFollowChunks(UUID id, World world) {
        Set<Long> held = FOLLOW_CHUNKS.remove(id);
        if (held == null || held.isEmpty() || world == null) {
            return;
        }
        JavaPlugin plugin = JavaPlugin.getPlugin(CraftEnginePolyfills.class);
        for (long key : held) {
            try {
                world.removePluginChunkTicket(ChunkPos.getX((long)key), ChunkPos.getZ((long)key), (Plugin)plugin);
            }
            catch (Throwable throwable) {}
        }
    }

    private static boolean isAtLoadedChunk(World world, ContraptionState state) {
        try {
            return world.isChunkLoaded(Mth.floor((double)state.x()) >> 4, Mth.floor((double)state.z()) >> 4);
        }
        catch (Throwable ignored) {
            return true;
        }
    }

    private static void recordEulerDrop(ContraptionEntity entity) {
        ContraptionState state = entity.state();
        PistonBearingBehavior piston = null;
        for (MovementBehavior b : state.behaviors()) {
            PistonBearingBehavior p;
            if (!(b instanceof PistonBearingBehavior)) continue;
            piston = p = (PistonBearingBehavior)b;
            break;
        }
        if (piston == null || state.level() == null) {
            return;
        }
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        HashSet<BlockPos> restored = new HashSet<BlockPos>();
        for (BlockPos local : state.level().localPositions()) {
            restored.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snapped));
        }
        if (restored.isEmpty()) {
            return;
        }
        HashSet<BlockPos> shaftPositions = new HashSet<BlockPos>();
        try {
            World w = ContraptionEngine.safeGetWorld(state);
            if (w != null) {
                ServerLevel level = ((CraftWorld)w).getHandle();
                BlockPos body = state.originBearingBlockPos();
                Vec3 f = piston.direction();
                int dx = (int)Math.round(f.x);
                int dy = (int)Math.round(f.y);
                int dz = (int)Math.round(f.z);
                int dist = piston.maxDistance();
                BlockState pipe = ContraptionEngine.bearingPartState(level, body, 3);
                BlockState head = ContraptionEngine.bearingPartState(level, body, 2);
                for (int i = 1; i <= dist; ++i) {
                    BlockState st;
                    BlockPos p = body.offset(dx * i, dy * i, dz * i);
                    BlockState blockState = st = i == dist ? head : pipe;
                    if (st == null || !level.getBlockState(p).isAir()) continue;
                    level.setBlock(p, st, 3);
                    shaftPositions.add(p.immutable());
                }
            }
        }
        catch (Throwable w) {
            // empty catch block
        }
        boolean initialRedstone = false;
        try {
            World w = ContraptionEngine.safeGetWorld(state);
            if (w != null) {
                initialRedstone = ((CraftWorld)w).getHandle().hasNeighborSignal(state.originBearingBlockPos());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        LinearContraptionType.recordExtendedSolid(state.worldId(), state.originBearingBlockPos(), piston.direction(), piston.maxDistance(), piston.baseSpeedBlocksPerSec(), piston.suPerBlock(), piston.mode(), piston.roundRobinDelayTicks(), restored, shaftPositions, initialRedstone);
    }

    private static BlockState bearingPartState(ServerLevel level, BlockPos bodyPos, int part) {
        try {
            BlockState real = level.getBlockState(bodyPos);
            ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(real).orElse(null);
            if (ce == null || ce.isEmpty()) {
                return null;
            }
            Property partProp = ce.getProperty("part");
            if (partProp == null) {
                return null;
            }
            return (BlockState)ImmutableBlockState.with((ImmutableBlockState)ce, (Property)partProp, part).customBlockState().minecraftState();
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static void resaveGhastStructure(ContraptionEntity entity) {
        for (MovementBehavior behavior : entity.state().behaviors()) {
            if (!(behavior instanceof GhastFollowBehavior)) continue;
            GhastFollowBehavior follow = (GhastFollowBehavior)behavior;
            Entity ghast = Bukkit.getEntity((UUID)follow.entityId());
            if (ghast != null) {
                GhastContraptionType.saveStructure(ghast, entity.state());
            }
            return;
        }
    }

    private static boolean ghastWantsFractureCheck(ContraptionEntity entity) {
        for (MovementBehavior behavior : entity.state().behaviors()) {
            if (!(behavior instanceof GhastFollowBehavior)) continue;
            GhastFollowBehavior follow = (GhastFollowBehavior)behavior;
            return follow.consumeFractureCheck();
        }
        return false;
    }

    private static boolean ghastAnchorDead(ContraptionEntity entity) {
        for (MovementBehavior b : entity.state().behaviors()) {
            GhastFollowBehavior follow;
            if (!(b instanceof GhastFollowBehavior) || !(follow = (GhastFollowBehavior)b).wantsDisassembleInPlace()) continue;
            return true;
        }
        return false;
    }

    private static boolean minecartAnchorDead(ContraptionEntity entity) {
        for (MovementBehavior b : entity.state().behaviors()) {
            MinecartFollowBehavior follow;
            if (!(b instanceof MinecartFollowBehavior) || !(follow = (MinecartFollowBehavior)b).wantsDisassembleInPlace()) continue;
            return true;
        }
        return false;
    }

    private static boolean pistonWantsDisassemble(ContraptionEntity entity) {
        for (MovementBehavior b : entity.state().behaviors()) {
            PistonBearingBehavior piston;
            if (!(b instanceof PistonBearingBehavior) || !(piston = (PistonBearingBehavior)b).wantsDisassembleAtEnd()) continue;
            return true;
        }
        return false;
    }

    private static World safeGetWorld(ContraptionState state) {
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel serverLevel = server.getLevel(state.worldId());
            return serverLevel != null ? serverLevel.getWorld() : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void fireMove(ContraptionEntity entity, World world, Vec3 from, Vec3 to, double yawDelta) {
        try {
            Bukkit.getPluginManager().callEvent((Event)new ContraptionMoveEvent(entity, world, from, to, yawDelta));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void stepKinematics(ContraptionState state, ServerLevel level) {
        MovementContext ctx = new MovementContext(state, level);
        state.resetSuDemand();
        double yawBefore = state.yawRadians();
        double pitchBefore = state.pitchRadians();
        double rollBefore = state.rollRadians();
        boolean stalled = false;
        Vec3 velocity = Vec3.ZERO;
        for (MovementBehavior behavior : state.behaviors()) {
            behavior.tick(ctx);
            if (behavior.isStalled()) {
                stalled = true;
            }
            velocity = velocity.add(behavior.velocityThisTick());
        }
        state.setStalled(stalled);
        state.setLastYawDelta(state.yawRadians() - yawBefore);
        state.setLastPitchDelta(state.pitchRadians() - pitchBefore);
        state.setLastRollDelta(state.rollRadians() - rollBefore);
        if (!stalled && !velocity.equals(Vec3.ZERO)) {
            state.setPosition(state.x() + velocity.x, state.y() + velocity.y, state.z() + velocity.z);
            state.setLastDelta(velocity.x, velocity.y, velocity.z);
        } else {
            state.setLastDelta(0.0, 0.0, 0.0);
        }
    }

    private static List<Player> viewersOf(World world, Map<UUID, List<Player>> cache) {
        return cache.computeIfAbsent(world.getUID(), k -> CePlayers.resolve(world.getPlayers()));
    }

    private static List<ServerPlayer> carryCandidatesOf(World world, Map<UUID, List<ServerPlayer>> cache) {
        return cache.computeIfAbsent(world.getUID(), k -> {
            ArrayList<ServerPlayer> candidates = new ArrayList<ServerPlayer>();
            for (org.bukkit.entity.Player p : world.getPlayers()) {
                candidates.add(((CraftPlayer)p).getHandle());
            }
            return candidates;
        });
    }

    private static void render(ContraptionEntity entity, World world, ServerLevel realLevel, Map<UUID, List<Player>> viewerCache, Map<UUID, List<ServerPlayer>> carryCache, boolean chunkLoaded, boolean perf) {
        List<Player> viewers = ContraptionEngine.viewersOf(world, viewerCache);
        if (perf) {
            ContraptionPerf.countContraption(entity.cellCount());
        }
        if (!chunkLoaded || viewers.isEmpty()) {
            if (!entity.renderSuspended()) {
                Bukkit.getLogger().warning("[Contraption] suspendRender id=" + String.valueOf(entity.state().id()) + " chunkLoaded=" + chunkLoaded + " viewers=" + viewers.size() + " pos=(" + String.format("%.1f,%.1f,%.1f", entity.state().x(), entity.state().y(), entity.state().z()) + ") held=" + PhysicsWorld.isHeld(entity.state().id()));
                entity.suspendRender(viewers);
            }
            if (perf) {
                if (!chunkLoaded) {
                    ContraptionPerf.countSkippedUnloaded();
                } else {
                    ContraptionPerf.countSkippedNoViewers();
                }
            }
            return;
        }
        if (entity.renderSuspended()) {
            entity.resumeRender();
        }
        if (perf) {
            ContraptionPerf.countRendered();
        }
        long rebuildStart = perf ? ContraptionPerf.begin() : 0L;
        entity.rebuildSwarm(viewers);
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.REBUILD, rebuildStart);
        }
        long renderStart = perf ? ContraptionPerf.begin() : 0L;
        entity.render(viewers, realLevel);
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.RENDER, renderStart);
        }
        long carryStart = perf ? ContraptionPerf.begin() : 0L;
        entity.carryRiders(ContraptionEngine.carryCandidatesOf(world, carryCache));
        entity.carrySeatedRiders();
        entity.carryEntities((Level)((CraftWorld)world).getHandle());
        entity.pushBackBystanders((Level)((CraftWorld)world).getHandle());
        entity.pushBackEntities((Level)((CraftWorld)world).getHandle());
        if (perf) {
            ContraptionPerf.end(ContraptionPerf.Phase.CARRY, carryStart);
        }
        ContraptionEngine.emitAmbientParticles(entity);
    }

    private static void emitAmbientParticles(ContraptionEntity entity) {
        if ((++ambientTickCounter & 3) != 0) {
            return;
        }
        ContraptionLevel level = entity.state().level();
        if (level == null) {
            return;
        }
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            if (!state.hasProperty((net.minecraft.world.level.block.state.properties.Property)BlockStateProperties.LIT) || !((Boolean)state.getValue((net.minecraft.world.level.block.state.properties.Property)BlockStateProperties.LIT)).booleanValue()) continue;
            double x = (double)local.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.6;
            double y = (double)local.getY() + 0.9;
            double z = (double)local.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.6;
            level.addParticle((ParticleOptions)ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            if (RANDOM.nextInt(3) != 0) continue;
            level.addParticle((ParticleOptions)ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}

