/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package dev.arubik.craftengine.contraption.assembly;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.MultiblockMembershipRegistry;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBlockEntity;
import dev.arubik.craftengine.contraption.behavior.RealMotorLink;
import dev.arubik.craftengine.contraption.behavior.RotationalBearingBehavior;
import dev.arubik.craftengine.contraption.behavior.VehicleControlBehavior;
import dev.arubik.craftengine.contraption.behavior.WindmillBearingBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.event.ContraptionAssembleEvent;
import dev.arubik.craftengine.contraption.event.ContraptionAssembledEvent;
import dev.arubik.craftengine.contraption.event.ContraptionDisassembleEvent;
import dev.arubik.craftengine.contraption.event.ContraptionDisassembledEvent;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.contraption.protection.ContraptionProtectionRegistry;
import dev.arubik.craftengine.conveyor.ConveyorBlockEntity;
import dev.arubik.craftengine.fluid.behavior.FluidTankRender;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Event;

public final class ContraptionAssembler {
    public static final double DEFAULT_LINEAR_SPEED = 1.0;
    public static final double DEFAULT_ROTATIONAL_RPM = 5.0;

    private ContraptionAssembler() {
    }

    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, Key type) {
        return ContraptionAssembler.assemble(bukkitWorld, bearing, type, 5.0, 2.0);
    }

    @Deprecated
    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, Key type, double rotationalRpm) {
        return ContraptionAssembler.assemble(bukkitWorld, bearing, type, rotationalRpm, 2.0);
    }

    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, Key type, double rotationalRpm, double suPerBlock) {
        return ContraptionAssembler.assemble(bukkitWorld, bearing, type, rotationalRpm, suPerBlock, null);
    }

    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearing, Key type, double rotationalRpm, double suPerBlock, UUID assembler) {
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        Set<BlockPos> structure = GlueRegistry.structureAt((ResourceKey<Level>)level.dimension(), bearing);
        if (structure.isEmpty()) {
            return null;
        }
        structure = ContraptionAssembler.expandMultiblockMembers((Level)level, structure);
        if (Key.of((String)"polyfills", (String)"linear").equals(type) || Key.of((String)"polyfills", (String)"windmill").equals(type)) {
            structure = new HashSet<BlockPos>(structure);
            structure.remove(bearing);
        }
        if (ContraptionAssembler.fireAssembleCancelled(bukkitWorld, bearing, type, structure, null)) {
            return null;
        }
        if (!ContraptionAssembler.mayCaptureAll(bukkitWorld, structure, assembler)) {
            org.bukkit.entity.Player p;
            org.bukkit.entity.Player player = p = assembler == null ? null : Bukkit.getPlayer((UUID)assembler);
            if (p != null) {
                p.sendMessage("\u00a7cYou can't assemble here \u2014 part of this structure is protected.");
            }
            return null;
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture((Level)level, structure, bearing);
        ContraptionCapture.captureGlueEdges((ResourceKey<Level>)level.dimension(), captured.level(), bearing);
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear((Level)level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld((Level)level, structure);
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)level.dimension(), captured.level(), bearing.getX(), bearing.getY(), bearing.getZ());
        state.setOwner(assembler);
        state.setFurniture(furnitureResult.furniture());
        for (Map.Entry<UUID, Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId == null) continue;
            state.setSeatedRiderMount(e.getKey(), mountId);
        }
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        ContraptionAssembler.attachDefaultBehavior((Level)level, bearing, state, type, rotationalRpm, suPerBlock);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        BlockAnchoredContraptionStore.save(state, bearing, type, rotationalRpm, suPerBlock);
        ContraptionAssembler.fireAssembled(entity);
        return entity;
    }

    private static boolean mayCaptureAll(World bukkitWorld, Set<BlockPos> structure, UUID assembler) {
        if (assembler == null) {
            return true;
        }
        for (BlockPos pos : structure) {
            if (ContraptionProtectionRegistry.canBreak(assembler, bukkitWorld, pos)) continue;
            return false;
        }
        return true;
    }

    public static ContraptionEntity assemblePhysics(World bukkitWorld, BlockPos physAnchor) {
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        double rpm = BearingBlockBehavior.rpmAt((Level)level, physAnchor);
        double suPerBlock = BearingBlockBehavior.suPerBlockAt((Level)level, physAnchor);
        return ContraptionAssembler.assemble(bukkitWorld, physAnchor, Key.of((String)"polyfills", (String)"phys"), rpm, suPerBlock, null);
    }

    public static ContraptionEntity assemblePiston(World bukkitWorld, BlockPos bearing) {
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        ResourceKey worldKey = level.dimension();
        Vec3 facing = BearingBlockBehavior.facingVecAt((Level)level, bearing);
        BlockPos front = bearing.offset((int)Math.round(facing.x), (int)Math.round(facing.y), (int)Math.round(facing.z));
        HashSet<BlockPos> structure = new HashSet<BlockPos>(GlueRegistry.structureAt((ResourceKey<Level>)worldKey, front));
        structure.addAll(GlueRegistry.structureAt((ResourceKey<Level>)worldKey, bearing));
        structure = new HashSet<BlockPos>(ContraptionAssembler.expandMultiblockMembers((Level)level, structure));
        structure.remove(bearing);
        structure.removeIf(arg_0 -> ContraptionAssembler.lambda$assemblePiston$0((Level)level, arg_0));
        if (structure.isEmpty()) {
            return null;
        }
        if (ContraptionAssembler.fireAssembleCancelled(bukkitWorld, bearing, Key.of((String)"polyfills", (String)"linear"), structure, null)) {
            return null;
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture((Level)level, structure, bearing);
        ContraptionCapture.captureGlueEdges((ResourceKey<Level>)worldKey, captured.level(), bearing);
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear((Level)level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld((Level)level, structure);
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)worldKey, captured.level(), bearing.getX(), bearing.getY(), bearing.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        double suPerBlock = BearingBlockBehavior.suPerBlockAt((Level)level, bearing);
        ContraptionAssembler.attachDefaultBehavior((Level)level, bearing, state, Key.of((String)"polyfills", (String)"linear"), 5.0, suPerBlock);
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        BlockAnchoredContraptionStore.save(state, bearing, Key.of((String)"polyfills", (String)"linear"), 5.0, suPerBlock);
        ContraptionAssembler.fireAssembled(entity);
        return entity;
    }

    public static ContraptionEntity assembleExplicitRetracting(World bukkitWorld, BlockPos bodyPos, Set<BlockPos> worldPositions, Vec3 facing, int distance, double speed, double suPerBlock, PistonBearingBehavior.Mode mode, long delay) {
        if (worldPositions == null || worldPositions.isEmpty()) {
            return null;
        }
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        ContraptionCapture.Result captured = ContraptionCapture.capture((Level)level, worldPositions, bodyPos);
        ContraptionCapture.captureGlueEdges((ResourceKey<Level>)level.dimension(), captured.level(), bodyPos);
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear((Level)level, worldPositions, bodyPos, captured.level());
        ContraptionCapture.removeFromWorld((Level)level, worldPositions);
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)level.dimension(), captured.level(), bodyPos.getX(), bodyPos.getY(), bodyPos.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        BlockPos motorPos = RealMotorLink.findAdjacentMotor((Level)level, bodyPos);
        state.addBehavior(PistonBearingBehavior.startRetracting(facing, bodyPos, motorPos, distance, speed, suPerBlock, mode, delay));
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        BlockAnchoredContraptionStore.save(state, bodyPos, Key.of((String)"polyfills", (String)"linear"), speed, suPerBlock);
        ContraptionAssembler.fireAssembled(entity);
        return entity;
    }

    public static Set<BlockPos> expandMultiblockMembers(Level level, Set<BlockPos> structure) {
        HashSet<BlockPos> expanded = new HashSet<BlockPos>(structure);
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>(structure);
        while (!queue.isEmpty()) {
            BlockPos pos = (BlockPos)queue.poll();
            Set<BlockPos> members = MultiblockMembershipRegistry.membersOf(level, pos);
            for (BlockPos member : members) {
                if (!expanded.add(member)) continue;
                queue.add(member);
            }
        }
        return expanded;
    }

    public static void attachDefaultBehavior(Level level, BlockPos bearing, ContraptionState state, Key type, double rotationalRpm, double suPerBlock) {
        state.setBearingType(type);
        state.setExplosionProof(BearingBlockBehavior.explosionProofAt(level, bearing));
        BlockPos motorPos = RealMotorLink.findAdjacentMotor(level, bearing);
        if (Key.of((String)"polyfills", (String)"linear").equals(type)) {
            double finalSuPerBlock;
            long rrDelay;
            double baseSpeed;
            int distance;
            PistonBearingBehavior.Mode mode;
            PistonBearingBlockEntity be = BearingBlockBehavior.controllerAt(level, bearing);
            if (be != null) {
                UpgradeModifiers mods = be.upgradeModifiers();
                mode = be.mode();
                distance = be.distance();
                baseSpeed = be.speedBlocksPerSec() * mods.speedMultiplier();
                rrDelay = be.roundRobinDelayTicks();
                finalSuPerBlock = suPerBlock * mods.fuelMultiplier();
            } else {
                mode = BearingBlockBehavior.pistonModeAt(level, bearing);
                distance = BearingBlockBehavior.distanceAt(level, bearing);
                baseSpeed = BearingBlockBehavior.speedAt(level, bearing);
                rrDelay = BearingBlockBehavior.roundRobinDelayAt(level, bearing);
                finalSuPerBlock = suPerBlock;
            }
            Vec3 facing = BearingBlockBehavior.facingVecAt(level, bearing);
            state.addBehavior(new PistonBearingBehavior(facing, bearing, motorPos, distance, baseSpeed, finalSuPerBlock, mode, rrDelay));
        } else if (Key.of((String)"polyfills", (String)"windmill").equals(type)) {
            Vec3 facingVec = BearingBlockBehavior.facingVecAt(level, bearing);
            state.addBehavior(new WindmillBearingBehavior(bearing, facingVec));
        } else if (Key.of((String)"polyfills", (String)"rotational").equals(type)) {
            state.addBehavior(new RotationalBearingBehavior(motorPos, rotationalRpm, suPerBlock));
        } else if (Key.of((String)"polyfills", (String)"phys").equals(type)) {
            state.addBehavior(new PhysicsBehavior());
        } else if (Key.of((String)"polyfills", (String)"vehicle").equals(type)) {
            state.addBehavior(new VehicleControlBehavior());
        } else if (Key.of((String)"polyfills", (String)"minecart").equals(type) || Key.of((String)"polyfills", (String)"ghast").equals(type)) {
            // empty if block
        }
    }

    public static void disassemble(World bukkitWorld, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        if (ContraptionAssembler.fireDisassembleCancelled(entity)) {
            return;
        }
        BlockAnchoredContraptionStore.delete(state.id());
        Set<UUID> riderIds = entity.currentRiderIds();
        List<Player> viewers = CePlayers.resolve(bukkitWorld.getPlayers());
        entity.despawnRest(viewers);
        ContraptionManager.remove(state.id());
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        BlockPos snappedBearing = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        ContraptionCapture.restoreGlue((ResourceKey<Level>)level.dimension(), state.level(), state.originBearingBlockPos(), snappedBearing, quarterTurns);
        ContraptionCapture.restoreRotated((Level)level, state.level(), snappedBearing, quarterTurns);
        HashSet<BlockPos> restingPositions = new HashSet<BlockPos>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snappedBearing));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snappedBearing, quarterTurns);
        ContraptionAssembler.nudgeRidersUp(riderIds);
        entity.despawnHitboxesOnly(viewers);
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                try {
                    FluidTankRender.remove((Level)state.level().serverLevel(), local);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    PersistentBlockEntity pbe = PersistentBlockEntity.getIfLoaded((Level)state.level().serverLevel(), local);
                    if (!(pbe instanceof ConveyorBlockEntity)) continue;
                    ConveyorBlockEntity conveyor = (ConveyorBlockEntity)pbe;
                    conveyor.despawnRender();
                }
                catch (Throwable throwable) {}
            }
            state.level().transferRemainingEntitiesToRealWorld();
            state.level().dispose();
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snappedBearing, restingPositions, quarterTurns);
    }

    public static void nudgeRidersUp(Set<UUID> riderIds) {
        if (riderIds.isEmpty()) {
            return;
        }
        double NUDGE = 0.05;
        for (UUID id : riderIds) {
            org.bukkit.entity.Player bukkit = Bukkit.getPlayer((UUID)id);
            if (bukkit == null) continue;
            bukkit.teleport(bukkit.getLocation().add(0.0, 0.05, 0.0));
        }
    }

    static void translateGlueToRestorePosition(ResourceKey<Level> worldKey, ContraptionState state, BlockPos snappedBearing, int quarterTurns) {
        if (state.level() == null) {
            return;
        }
        BlockPos origin = state.originBearingBlockPos();
        if (origin.equals(snappedBearing) && quarterTurns == 0) {
            return;
        }
        HashMap<BlockPos, BlockPos> move = new HashMap<BlockPos, BlockPos>();
        for (BlockPos local : state.level().localPositions()) {
            BlockPos rotatedLocal = ContraptionCapture.rotateLocal(local, quarterTurns);
            move.put(ContraptionMath.toWorld(local, origin), ContraptionMath.toWorld(rotatedLocal, snappedBearing));
        }
        GlueRegistry.graphFor(worldKey).translateAll(move);
    }

    public static boolean fireAssembleCancelled(World world, BlockPos bearing, Key type, Set<BlockPos> structure, org.bukkit.entity.Player cause) {
        try {
            ContraptionAssembleEvent event = new ContraptionAssembleEvent(world, bearing, type, new HashSet<BlockPos>(structure), cause);
            Bukkit.getPluginManager().callEvent((Event)event);
            return event.isCancelled();
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    public static void fireAssembled(ContraptionEntity entity) {
        try {
            Bukkit.getPluginManager().callEvent((Event)new ContraptionAssembledEvent(entity));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean fireDisassembleCancelled(ContraptionEntity entity) {
        try {
            ContraptionDisassembleEvent event = new ContraptionDisassembleEvent(entity);
            Bukkit.getPluginManager().callEvent((Event)event);
            return event.isCancelled();
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    public static void fireDisassembled(UUID contraptionId, World world, BlockPos snappedBearing, Set<BlockPos> restingPositions, int quarterTurns) {
        try {
            Bukkit.getPluginManager().callEvent((Event)new ContraptionDisassembledEvent(contraptionId, world, snappedBearing, restingPositions, quarterTurns));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static /* synthetic */ boolean lambda$assemblePiston$0(Level level, BlockPos p) {
        return level.getBlockState(p).isAir();
    }
}

