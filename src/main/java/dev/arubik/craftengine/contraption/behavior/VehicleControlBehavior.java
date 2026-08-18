/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Input
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.VehicleDriverRegistry;
import dev.arubik.craftengine.contraption.behavior.PhysicsBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import java.util.UUID;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Input;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class VehicleControlBehavior
extends PhysicsBehavior {
    private static final double THRUST = 2.0;
    private static final double LIFT = 1.5;
    private static final double YAW_GAIN = 0.35;
    private static final double YAW_DEADZONE = 0.06;
    private static final int CHUNK_RADIUS = 2;
    private int lastCx;
    private int lastCz;
    private boolean holdsChunks;

    @Override
    public void tick(MovementContext ctx) {
        Vector look;
        Vector3d lookH;
        super.tick(ctx);
        ContraptionState state = ctx.state();
        CraftWorld world = null;
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(state.worldId());
            world = level != null ? level.getWorld() : null;
        }
        catch (Throwable server) {
            // empty catch block
        }
        UUID driverId = VehicleDriverRegistry.driverOf(state.id());
        if (driverId == null) {
            this.releaseChunks((World)world);
            return;
        }
        Player bukkit = Bukkit.getPlayer((UUID)driverId);
        if (bukkit == null) {
            this.releaseChunks((World)world);
            return;
        }
        this.keepChunksLoaded((World)world, state);
        ServerPlayer sp = ((CraftPlayer)bukkit).getHandle();
        Input in = sp.getLastClientInput();
        if (in == null) {
            return;
        }
        double f = (in.forward() ? 1.0 : 0.0) - (in.backward() ? 1.0 : 0.0);
        double v = (in.jump() ? 1.0 : 0.0) - (in.sprint() ? 1.0 : 0.0);
        double yaw = state.yawRadians();
        Vector3d heading = new Vector3d(-Math.sin(yaw), 0.0, Math.cos(yaw));
        double steer = 0.0;
        if (f != 0.0 && (lookH = new Vector3d((look = bukkit.getEyeLocation().getDirection()).getX(), 0.0, look.getZ())).lengthSquared() >= 1.0E-6) {
            lookH.normalize();
            double targetYaw = Math.atan2(-lookH.x, lookH.z);
            double err = VehicleControlBehavior.shortestAngle(yaw, targetYaw);
            if (Math.abs(err) > 0.06) {
                steer = 0.35 * err * Math.min(1.0, Math.abs(f));
            }
        }
        PhysicsWorld.setYawRate(state.id(), steer);
        if (f == 0.0 && v == 0.0) {
            return;
        }
        Vector3d impulse = new Vector3d();
        impulse.fma(f * 2.0, (Vector3dc)heading);
        impulse.y += v * 1.5;
        PhysicsWorld.applyThrustCentral(state.id(), impulse);
    }

    private static double shortestAngle(double from, double to) {
        double d = (to - from) % (Math.PI * 2);
        if (d > Math.PI) {
            d -= Math.PI * 2;
        } else if (d < -Math.PI) {
            d += Math.PI * 2;
        }
        return d;
    }

    private void keepChunksLoaded(World world, ContraptionState state) {
        if (world == null) {
            return;
        }
        int cx = (int)Math.floor(state.x()) >> 4;
        int cz = (int)Math.floor(state.z()) >> 4;
        if (this.holdsChunks && cx == this.lastCx && cz == this.lastCz) {
            return;
        }
        if (this.holdsChunks) {
            VehicleControlBehavior.forEachRing(this.lastCx, this.lastCz, (x, z) -> world.removePluginChunkTicket(x, z, (Plugin)CraftEnginePolyfills.instance()));
        }
        VehicleControlBehavior.forEachRing(cx, cz, (x, z) -> world.addPluginChunkTicket(x, z, (Plugin)CraftEnginePolyfills.instance()));
        this.lastCx = cx;
        this.lastCz = cz;
        this.holdsChunks = true;
    }

    private void releaseChunks(World world) {
        if (!this.holdsChunks || world == null) {
            this.holdsChunks = false;
            return;
        }
        VehicleControlBehavior.forEachRing(this.lastCx, this.lastCz, (x, z) -> world.removePluginChunkTicket(x, z, (Plugin)CraftEnginePolyfills.instance()));
        this.holdsChunks = false;
    }

    private static void forEachRing(int cx, int cz, ChunkOp op) {
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                op.at(cx + dx, cz + dz);
            }
        }
    }

    private static interface ChunkOp {
        public void at(int var1, int var2);
    }
}

