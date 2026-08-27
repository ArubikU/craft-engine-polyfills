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
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.LinearActuatorBehavior;
import dev.arubik.craftengine.contraption.behavior.MinerBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.fluid.behavior.FluidTankRender;
import java.util.HashMap;
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
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.plugin.Plugin;

public final class HologramTest {
    private static final Map<UUID, Run> ACTIVE = new HashMap<UUID, Run>();

    private HologramTest() {
    }

    public static void start(Plugin plugin, World bukkitWorld, BlockPos bearing) {
        HologramTest.stop(bukkitWorld);
        ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
        Set<BlockPos> structure = GlueRegistry.structureAt((ResourceKey<Level>)level.dimension(), bearing);
        structure = ContraptionAssembler.expandMultiblockMembers((Level)level, structure);
        ContraptionCapture.Result captured = ContraptionCapture.capture((Level)level, structure, bearing);
        ContraptionCapture.captureGlueEdges((ResourceKey<Level>)level.dimension(), captured.level(), bearing);
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear((Level)level, structure, bearing, captured.level());
        ContraptionCapture.removeFromWorld((Level)level, structure);
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)((CraftWorld)bukkitWorld).getHandle().dimension(), captured.level(), bearing.getX(), bearing.getY(), bearing.getZ());
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
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        Run run = new Run();
        run.bukkitWorld = bukkitWorld;
        run.bearing = bearing;
        run.entity = entity;
        ACTIVE.put(bukkitWorld.getUID(), run);
    }

    public static boolean setLinearVelocity(World bukkitWorld, double dxPerSec, double dyPerSec, double dzPerSec) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        run.entity.state().behaviors().clear();
        run.entity.state().addBehavior(LinearActuatorBehavior.blocksPerSecond(dxPerSec, dyPerSec, dzPerSec));
        return true;
    }

    public static boolean nudgePosition(World bukkitWorld, double dx, double dy, double dz) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        ContraptionState state = run.entity.state();
        state.setPosition(state.x() + dx, state.y() + dy, state.z() + dz);
        return true;
    }

    public static boolean rotateYawDegrees(World bukkitWorld, double degrees) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        ContraptionState state = run.entity.state();
        state.setYawRadians(state.yawRadians() + Math.toRadians(degrees));
        return true;
    }

    public static boolean addMiner(World bukkitWorld, BlockPos targetOffset, double rpm) {
        Run run = ACTIVE.get(bukkitWorld.getUID());
        if (run == null) {
            return false;
        }
        MinerBehavior miner = new MinerBehavior(targetOffset, 1.0);
        miner.setInputRpm((float)rpm);
        run.entity.state().addBehavior(miner);
        return true;
    }

    public static void stop(World bukkitWorld) {
        Run run = ACTIVE.remove(bukkitWorld.getUID());
        if (run == null) {
            return;
        }
        Set<UUID> riderIds = run.entity.currentRiderIds();
        List<Player> viewers = CePlayers.resolve(run.bukkitWorld.getPlayers());
        run.entity.despawnRest(viewers);
        ContraptionManager.remove(run.entity.state().id());
        ServerLevel level = ((CraftWorld)run.bukkitWorld).getHandle();
        ContraptionState state = run.entity.state();
        BlockPos snappedBearing = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        ContraptionCapture.restoreGlue((ResourceKey<Level>)level.dimension(), state.level(), state.originBearingBlockPos(), snappedBearing, quarterTurns);
        ContraptionCapture.restoreRotated((Level)level, state.level(), snappedBearing, quarterTurns);
        ContraptionFurnitureCapture.restoreFurniture(run.bukkitWorld, state.furniture(), snappedBearing, quarterTurns);
        ContraptionAssembler.nudgeRidersUp(riderIds);
        run.entity.despawnHitboxesOnly(viewers);
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
    }

    private static final class Run {
        World bukkitWorld;
        BlockPos bearing;
        ContraptionEntity entity;

        private Run() {
        }
    }
}

