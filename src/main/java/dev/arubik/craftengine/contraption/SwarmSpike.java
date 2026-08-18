/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.Direction
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.BaseEntityData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$ItemDisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.player.PlayerCarry;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.BaseEntityData;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public final class SwarmSpike {
    private static final int DURATION_TICKS = 300;
    private static final float AMPLITUDE = 3.0f;
    private static final Map<UUID, Run> ACTIVE;
    private static final AtomicInteger ENTITY_COUNTER;

    private SwarmSpike() {
    }

    public static void start(Plugin plugin, Player bukkitPlayer, int n, String kind) {
        SwarmSpike.stop(bukkitPlayer);
        boolean shulker = "shulker".equalsIgnoreCase(kind);
        n = Math.max(1, Math.min(n, 2000));
        ServerPlayer sp = ((CraftPlayer)bukkitPlayer).getHandle();
        Location loc = bukkitPlayer.getLocation();
        Vector fwd = loc.getDirection().setY(0).normalize();
        double baseX = loc.getX() + fwd.getX() * 4.0;
        double baseY = loc.getY() + (shulker ? -1.0 : 3.0);
        double baseZ = loc.getZ() + fwd.getZ() * 4.0;
        int side = (int)Math.ceil(Math.sqrt(n));
        ArrayList<Integer> ids = new ArrayList<Integer>(n);
        ArrayList<double[]> localOffsets = new ArrayList<double[]>(n);
        int spawned = 0;
        ArrayList<Object> spawnPackets = new ArrayList<Object>(n * 2);
        for (int row = 0; row < side && spawned < n; ++row) {
            for (int i = 0; i < side && spawned < n; ++spawned, ++i) {
                int id = SwarmSpike.nextEntityId();
                double dx = ((double)i - (double)side / 2.0) * 1.0;
                double dz = ((double)row - (double)side / 2.0) * 1.0;
                ids.add(id);
                localOffsets.add(new double[]{dx, dz});
                double x = baseX + dx;
                double y = baseY;
                double z = baseZ + dz;
                EntityType type = shulker ? EntityType.SHULKER : EntityType.ITEM_DISPLAY;
                spawnPackets.add(MNms.INSTANCE.constructor$ClientboundAddEntityPacket(id, UUID.randomUUID(), x, y, z, 0.0f, 0.0f, type, 0, Vec3.ZERO, 0.0));
                spawnPackets.add(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(id, SwarmSpike.metadata(shulker)));
            }
        }
        for (Object e : spawnPackets) {
            SwarmSpike.send(sp, e);
        }
        Run run = new Run();
        run.ids = ids;
        run.offsets = localOffsets;
        run.baseX = baseX;
        run.baseY = baseY;
        run.baseZ = baseZ;
        run.shulker = shulker;
        run.player = bukkitPlayer;
        bukkitPlayer.sendMessage("\u00a7b[spike-swarm] \u00a77spawned \u00a7f" + spawned + "\u00a77 " + (shulker ? "shulker" : "item_display") + " entities ~4 blocks ahead of you, moving for 15s. " + (shulker ? "Fly/walk onto the platform to test standing on it." : "Look toward it to judge jitter."));
        run.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> SwarmSpike.tick(run), 0L, 1L);
        ACTIVE.put(bukkitPlayer.getUniqueId(), run);
    }

    public static void stop(Player bukkitPlayer) {
        Run run = ACTIVE.remove(bukkitPlayer.getUniqueId());
        if (run == null) {
            return;
        }
        if (run.task != null) {
            run.task.cancel();
        }
        PlayerCarry.release(bukkitPlayer.getUniqueId());
        SwarmSpike.despawn(bukkitPlayer, run.ids);
        if (run.tickCount > 0) {
            SwarmSpike.report(bukkitPlayer, run);
        }
    }

    private static void tick(Run run) {
        ++run.tickCount;
        if (run.tickCount > 300) {
            SwarmSpike.stop(run.player);
            return;
        }
        long t0 = System.nanoTime();
        double phase = (double)run.tickCount / 20.0;
        double offset = Math.sin(phase) * 3.0;
        double deltaX = offset - run.lastOffset;
        run.lastOffset = offset;
        if (run.shulker) {
            boolean onPlatform;
            double side = Math.ceil(Math.sqrt(run.ids.size()));
            double minX = run.baseX + offset - side / 2.0 - 0.5;
            double maxX = run.baseX + offset + side / 2.0 + 0.5;
            double minZ = run.baseZ - side / 2.0 - 0.5;
            double maxZ = run.baseZ + side / 2.0 + 0.5;
            double platformTopY = run.baseY + 1.0;
            Location l = run.player.getLocation();
            boolean bl = onPlatform = l.getX() >= minX && l.getX() <= maxX && l.getZ() >= minZ && l.getZ() <= maxZ && l.getY() >= platformTopY - 0.3 && l.getY() <= platformTopY + 0.9;
            if (onPlatform) {
                run.carrying = true;
                ServerPlayer sp = ((CraftPlayer)run.player).getHandle();
                PlayerCarry.carry(sp, deltaX, 0.0, 0.0);
            } else if (run.carrying) {
                run.carrying = false;
                PlayerCarry.release(run.player.getUniqueId());
            }
        }
        ServerPlayer sp = ((CraftPlayer)run.player).getHandle();
        ArrayList<Object> batch = new ArrayList<Object>(run.ids.size());
        for (int i = 0; i < run.ids.size(); ++i) {
            int id = run.ids.get(i);
            double[] o = run.offsets.get(i);
            double x = run.baseX + o[0] + offset;
            double y = run.baseY;
            double z = run.baseZ + o[1];
            batch.add(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(id, x, y, z, 0.0f, 0.0f, false));
        }
        SwarmSpike.send(sp, MNms.INSTANCE.constructor$ClientboundBundlePacket(batch));
        long elapsed = System.nanoTime() - t0;
        run.totalNanos += elapsed;
        run.maxNanos = Math.max(run.maxNanos, elapsed);
        ++run.samples;
        if (run.tickCount % 20 == 0) {
            double avgMs = (double)run.totalNanos / (double)run.samples / 1000000.0;
            double maxMs = (double)run.maxNanos / 1000000.0;
            run.player.sendMessage(String.format("\u00a77[spike-swarm] \u00a7f%d\u00a77 entities: avg \u00a7f%.3fms\u00a77/tick, max \u00a7f%.3fms\u00a77 (of a 50ms budget)", run.ids.size(), avgMs, maxMs));
        }
    }

    private static void report(Player bukkitPlayer, Run run) {
        double avgMs = (double)run.totalNanos / (double)Math.max(1, run.samples) / 1000000.0;
        double maxMs = (double)run.maxNanos / 1000000.0;
        bukkitPlayer.sendMessage(String.format("\u00a7b[spike-swarm] \u00a77done. \u00a7f%d\u00a77 entities, \u00a7f%d\u00a77 ticks measured: avg \u00a7f%.3fms\u00a77/tick, max \u00a7f%.3fms\u00a77.", run.ids.size(), run.samples, avgMs, maxMs));
        Bukkit.getLogger().info(String.format("[SwarmSpike] n=%d shulker=%b avgMs=%.3f maxMs=%.3f samples=%d", run.ids.size(), run.shulker, avgMs, maxMs, run.samples));
    }

    private static void despawn(Player bukkitPlayer, List<Integer> ids) {
        if (ids.isEmpty()) {
            return;
        }
        ServerPlayer sp = ((CraftPlayer)bukkitPlayer).getHandle();
        Object packet = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int[])ids.stream().mapToInt(Integer::intValue).toArray()));
        SwarmSpike.send(sp, packet);
    }

    private static void send(ServerPlayer sp, Object packet) {
        ServerGamePacketListenerImpl connection = sp.connection;
        connection.send((Packet)packet);
    }

    private static List<Object> metadata(boolean shulker) {
        ArrayList<Object> values = new ArrayList<Object>();
        if (shulker) {
            BaseEntityData.NoGravity.addEntityData(true, values);
            BaseEntityData.Silent.addEntityData(true, values);
            ShulkerData.AttachFace.addEntityData(Direction.DOWN, values);
            ShulkerData.RawPeekAmount.addEntityData((byte)0, values);
            ShulkerData.Color.addEntityData((byte)14, values);
        } else {
            ItemStack stack = new ItemStack((ItemLike)Items.DIAMOND_BLOCK);
            DisplayData.ItemDisplayData.ItemStack.addEntityData(stack, values);
            DisplayData.Scale.addEntityData(new Vector3f(0.9f, 0.9f, 0.9f), values);
            DisplayData.BrightnessOverride.addEntityData(0xF000F0, values);
            DisplayData.PosRotInterpolationDuration.addEntityData(4, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(4, values);
        }
        return values;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger counter;
        ACTIVE = new HashMap<UUID, Run>();
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger)f.get(null);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static final class Run {
        List<Integer> ids;
        List<double[]> offsets;
        double baseX;
        double baseY;
        double baseZ;
        boolean shulker;
        Player player;
        BukkitTask task;
        int tickCount = 0;
        long totalNanos = 0L;
        long maxNanos = 0L;
        int samples = 0;
        double lastOffset = 0.0;
        boolean carrying = false;

        private Run() {
        }
    }
}

