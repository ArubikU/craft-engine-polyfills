package dev.arubik.craftengine.contraption.behavior;

import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Vector3d;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.VehicleDriverRegistry;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import net.minecraft.server.level.ServerPlayer;

/**
 * The steer-vehicle brain (VEHICLE type). It IS a {@link PhysicsBehavior} — a free rigid body under
 * gravity, collision and buoyancy, solved by {@code PhysicsWorld}/{@code XpbdSolver} exactly like any PHYS
 * contraption — and adds driving on top: each tick it reads its driver's live input and turns it into motion.
 *
 * <h2>Steering — like a horse</h2>
 * The hull turns to FOLLOW THE DRIVER'S LOOK (a horse turns where the rider looks): the driver's horizontal
 * gaze is the target heading, and {@link PhysicsWorld#setYawRate} rotates the body toward it every tick. W/S
 * then thrust the whole hull forward/back along the gaze through the center of mass ({@link
 * PhysicsWorld#applyThrustCentral} — pure translation, no spurious roll), jump/sprint lift/drop it. Input comes
 * from the vanilla per-tick {@code ServerPlayer#getLastClientInput()} (no packets, same source PlayerCarry
 * uses). Mass-scaling makes a heavy vehicle sluggish (power-to-mass); self-levelling keeps the deck flat.
 *
 * <h2>Keeping the world loaded under it</h2>
 * A moving contraption is packet-rendered and its physics reads real terrain, but it is NOT a real entity, so
 * nothing keeps the chunks it flies into loaded — drive far and the body reads air for terrain and everything
 * bugs out (user report). While driven, this pins a small ring of plugin chunk tickets around the hull, moved
 * as it crosses chunk borders and released the moment nobody is driving.
 */
public class VehicleControlBehavior extends PhysicsBehavior {

    /** Forward/reverse impulse per tick at full throttle (clamped downstream by PhysicsWorld's cap). */
    private static final double THRUST = 2.0;
    /** Vertical impulse per tick for ascend/descend (jump / sprint). */
    private static final double LIFT = 1.5;
    /** Proportional gain turning heading error (radians) into a yaw rate — higher = snappier turns. */
    private static final double YAW_GAIN = 0.35;
    /** Heading-error deadzone (radians ~3.4°): below it, don't steer — so tiny camera jitter never wiggles the hull. */
    private static final double YAW_DEADZONE = 0.06;
    /** Chunk-ticket radius (in chunks) kept loaded around a driven vehicle. */
    private static final int CHUNK_RADIUS = 2;

    /** Last chunk the loaded ring was centred on, and whether we currently hold tickets (for release). */
    private int lastCx, lastCz;
    private boolean holdsChunks;

    @Override
    public void tick(MovementContext ctx) {
        // Apply the solver's resolved rotation/position first (the inherited PHYS behaviour).
        super.tick(ctx);

        ContraptionState state = ctx.state();
        World world = null;
        try {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(state.worldId());
            world = level != null ? level.getWorld() : null;
        } catch (Throwable ignored) {
        }
        UUID driverId = VehicleDriverRegistry.driverOf(state.id());
        if (driverId == null) {
            releaseChunks(world);
            return;
        }
        Player bukkit = Bukkit.getPlayer(driverId);
        if (bukkit == null) {
            releaseChunks(world);
            return;
        }

        // Keep the terrain under the hull loaded while it's driven (see class javadoc "Keeping the world loaded").
        keepChunksLoaded(world, state);

        ServerPlayer sp = ((CraftPlayer) bukkit).getHandle();
        net.minecraft.world.entity.player.Input in = sp.getLastClientInput();
        if (in == null) {
            return;
        }

        double f = (in.forward() ? 1.0 : 0.0) - (in.backward() ? 1.0 : 0.0);
        double v = (in.jump() ? 1.0 : 0.0) - (in.sprint() ? 1.0 : 0.0);

        // The hull drives along its OWN heading (car/boat-like), not the camera — the ship goes where it POINTS.
        double yaw = state.yawRadians();
        Vector3d heading = new Vector3d(-Math.sin(yaw), 0.0, Math.cos(yaw));

        // Smart steering (user: "no solo girar por girar ... solo después de moverse"): the hull turns toward the
        // driver's look ONLY while there is throttle, and by an amount scaled by that throttle — so looking around
        // while parked never spins it, and the harder you drive the sharper it comes about. A deadzone kills tiny
        // camera jitter. When not throttling we firmly hold the current heading (yaw rate 0).
        double steer = 0.0;
        if (f != 0.0) {
            org.bukkit.util.Vector look = bukkit.getEyeLocation().getDirection();
            Vector3d lookH = new Vector3d(look.getX(), 0.0, look.getZ());
            if (lookH.lengthSquared() >= 1.0e-6) {
                lookH.normalize();
                double targetYaw = Math.atan2(-lookH.x, lookH.z);
                double err = shortestAngle(yaw, targetYaw);
                if (Math.abs(err) > YAW_DEADZONE) {
                    steer = YAW_GAIN * err * Math.min(1.0, Math.abs(f)); // turn toward look, scaled by throttle
                }
            }
        }
        PhysicsWorld.setYawRate(state.id(), steer);

        if (f == 0.0 && v == 0.0) {
            return;
        }
        Vector3d impulse = new Vector3d();
        impulse.fma(f * THRUST, heading);
        impulse.y += v * LIFT;
        PhysicsWorld.applyThrustCentral(state.id(), impulse);
    }

    /** Shortest signed angle from {@code from} to {@code to} (radians), in (-PI, PI]. */
    private static double shortestAngle(double from, double to) {
        double d = (to - from) % (Math.PI * 2);
        if (d > Math.PI) {
            d -= Math.PI * 2;
        } else if (d < -Math.PI) {
            d += Math.PI * 2;
        }
        return d;
    }

    /** Pins a {@link #CHUNK_RADIUS} ring of plugin chunk tickets centred on the hull, moving it as it crosses borders. */
    private void keepChunksLoaded(World world, ContraptionState state) {
        if (world == null) {
            return;
        }
        int cx = (int) Math.floor(state.x()) >> 4;
        int cz = (int) Math.floor(state.z()) >> 4;
        if (holdsChunks && cx == lastCx && cz == lastCz) {
            return; // still centred on the same chunk — nothing to move
        }
        if (holdsChunks) {
            forEachRing(lastCx, lastCz, (x, z) -> world.removePluginChunkTicket(x, z, CraftEnginePolyfills.instance()));
        }
        forEachRing(cx, cz, (x, z) -> world.addPluginChunkTicket(x, z, CraftEnginePolyfills.instance()));
        lastCx = cx;
        lastCz = cz;
        holdsChunks = true;
    }

    /** Releases any chunk tickets this vehicle was holding (driver left / vehicle gone). */
    private void releaseChunks(World world) {
        if (!holdsChunks || world == null) {
            holdsChunks = false;
            return;
        }
        forEachRing(lastCx, lastCz, (x, z) -> world.removePluginChunkTicket(x, z, CraftEnginePolyfills.instance()));
        holdsChunks = false;
    }

    private interface ChunkOp {
        void at(int x, int z);
    }

    private static void forEachRing(int cx, int cz, ChunkOp op) {
        for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
            for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
                op.at(cx + dx, cz + dz);
            }
        }
    }
}
