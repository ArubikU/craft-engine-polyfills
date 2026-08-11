package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.player.PlayerCarry;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Throwaway Phase-0 spike (see CONTRAPTIONS.md §4, item #1): spawns N packet-only fake
 * entities and repositions all of them every tick, to measure server-side reposition cost
 * and let a human judge client-side visual jitter — the two things that gate max viable
 * contraption size and hitbox granularity. Not production code; delete once the real
 * ContraptionDisplaySwarm/ContraptionHitboxSwarm classes exist and are load-tested for real.
 */
public final class SwarmSpike {

    private static final int DURATION_TICKS = 300; // ~15s at 20 TPS
    private static final float AMPLITUDE = 3.0f; // blocks, oscillation half-width

    /** One running spike per player (keyed by uuid), so re-running cleans up the last one. */
    private static final Map<UUID, Run> ACTIVE = new HashMap<>();

    private SwarmSpike() {
    }

    public static void start(Plugin plugin, Player bukkitPlayer, int n, String kind) {
        stop(bukkitPlayer);
        boolean shulker = "shulker".equalsIgnoreCase(kind);
        n = Math.max(1, Math.min(n, 2000)); // sanity clamp

        ServerPlayer sp = ((CraftPlayer) bukkitPlayer).getHandle();
        Location loc = bukkitPlayer.getLocation();
        // Float the swarm in open air a few blocks in front of the player (not embedded in
        // terrain under their feet) so it's unambiguous to see/walk onto regardless of the
        // ground shape where the command was run.
        org.bukkit.util.Vector fwd = loc.getDirection().setY(0).normalize();
        double baseX = loc.getX() + fwd.getX() * 4;
        double baseY = loc.getY() + (shulker ? -1.0 : 3.0);
        double baseZ = loc.getZ() + fwd.getZ() * 4;

        int side = (int) Math.ceil(Math.sqrt(n));
        List<Integer> ids = new ArrayList<>(n);
        List<double[]> localOffsets = new ArrayList<>(n); // [dx, dz] grid offset, y stays flat for both modes

        int spawned = 0;
        List<Object> spawnPackets = new ArrayList<>(n * 2);
        for (int row = 0; row < side && spawned < n; row++) {
            for (int col = 0; col < side && spawned < n; col++) {
                int id = nextEntityId();
                double dx = (col - side / 2.0) * 1.0;
                double dz = (row - side / 2.0) * 1.0;
                ids.add(id);
                localOffsets.add(new double[] { dx, dz });

                double x = baseX + dx, y = baseY, z = baseZ + dz;
                EntityType<?> type = shulker ? EntityType.SHULKER : EntityType.ITEM_DISPLAY;
                spawnPackets.add(dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                        id, UUID.randomUUID(), x, y, z, 0f, 0f, type, 0, Vec3.ZERO, 0));
                spawnPackets.add(dev.arubik.craftengine.util.MNms.INSTANCE
                        .constructor$ClientboundSetEntityDataPacket(id, metadata(shulker)));
                spawned++;
            }
        }
        for (Object p : spawnPackets) {
            send(sp, p);
        }

        Run run = new Run();
        run.ids = ids;
        run.offsets = localOffsets;
        run.baseX = baseX;
        run.baseY = baseY;
        run.baseZ = baseZ;
        run.shulker = shulker;
        run.player = bukkitPlayer;

        bukkitPlayer.sendMessage("§b[spike-swarm] §7spawned §f" + spawned + "§7 " + (shulker ? "shulker" : "item_display")
                + " entities ~4 blocks ahead of you, moving for " + (DURATION_TICKS / 20)
                + "s. " + (shulker ? "Fly/walk onto the platform to test standing on it."
                        : "Look toward it to judge jitter."));

        run.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> tick(run), 0L, 1L);
        ACTIVE.put(bukkitPlayer.getUniqueId(), run);
    }

    public static void stop(Player bukkitPlayer) {
        Run run = ACTIVE.remove(bukkitPlayer.getUniqueId());
        if (run == null)
            return;
        if (run.task != null)
            run.task.cancel();
        PlayerCarry.release(bukkitPlayer.getUniqueId());
        despawn(bukkitPlayer, run.ids);
        if (run.tickCount > 0) {
            report(bukkitPlayer, run);
        }
    }

    private static void tick(Run run) {
        run.tickCount++;
        if (run.tickCount > DURATION_TICKS) {
            stop(run.player);
            return;
        }
        long t0 = System.nanoTime();
        double phase = run.tickCount / 20.0;
        double offset = Math.sin(phase) * AMPLITUDE;
        double deltaX = offset - run.lastOffset; // this tick's world-space movement (blocks/tick)
        run.lastOffset = offset;

        // Carry the controlling player via PlayerCarry — see that class's javadoc for why
        // velocity nudges, Entity#move(), and ArmorStand-passenger mounting were all dead
        // ends (fighting client movement authority, or an unacceptably restrictive
        // passenger-ride feel). This sends ack-gated RELATIVE position corrections (add
        // this tick's delta to wherever the player currently is), never more than one
        // outstanding correction at a time, so the player still walks/jumps freely on top.
        if (run.shulker) {
            double side = Math.ceil(Math.sqrt(run.ids.size()));
            double minX = run.baseX + offset - side / 2.0 - 0.5;
            double maxX = run.baseX + offset + side / 2.0 + 0.5;
            double minZ = run.baseZ - side / 2.0 - 0.5;
            double maxZ = run.baseZ + side / 2.0 + 0.5;
            double platformTopY = run.baseY + 1.0; // closed shulker top face

            Location l = run.player.getLocation();
            boolean onPlatform = l.getX() >= minX && l.getX() <= maxX && l.getZ() >= minZ && l.getZ() <= maxZ
                    && l.getY() >= platformTopY - 0.3 && l.getY() <= platformTopY + 0.9;
            if (onPlatform) {
                run.carrying = true;
                ServerPlayer sp = ((CraftPlayer) run.player).getHandle();
                PlayerCarry.carry(sp, deltaX, 0, 0);
            } else if (run.carrying) {
                run.carrying = false;
                PlayerCarry.release(run.player.getUniqueId());
            }
        }

        // Bundle every entity's position update into ONE packet so the client applies them
        // all within the same frame — sent unbundled, netty/render-thread staggering across
        // dozens of individual packets is exactly what reads as jitter for a swarm (a single
        // conveyor item never hit this because it's only ever one entity).
        ServerPlayer sp = ((CraftPlayer) run.player).getHandle();
        List<Object> batch = new ArrayList<>(run.ids.size());
        for (int i = 0; i < run.ids.size(); i++) {
            int id = run.ids.get(i);
            double[] o = run.offsets.get(i);
            double x = run.baseX + o[0] + offset;
            double y = run.baseY;
            double z = run.baseZ + o[1];
            batch.add(dev.arubik.craftengine.util.MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(id, x, y, z, 0f, 0f, false));
        }
        send(sp, dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundBundlePacket(batch));
        long elapsed = System.nanoTime() - t0;
        run.totalNanos += elapsed;
        run.maxNanos = Math.max(run.maxNanos, elapsed);
        run.samples++;

        if (run.tickCount % 20 == 0) {
            double avgMs = (run.totalNanos / (double) run.samples) / 1_000_000.0;
            double maxMs = run.maxNanos / 1_000_000.0;
            run.player.sendMessage(String.format(
                    "§7[spike-swarm] §f%d§7 entities: avg §f%.3fms§7/tick, max §f%.3fms§7 (of a 50ms budget)",
                    run.ids.size(), avgMs, maxMs));
        }
    }

    private static void report(Player bukkitPlayer, Run run) {
        double avgMs = (run.totalNanos / (double) Math.max(1, run.samples)) / 1_000_000.0;
        double maxMs = run.maxNanos / 1_000_000.0;
        bukkitPlayer.sendMessage(String.format(
                "§b[spike-swarm] §7done. §f%d§7 entities, §f%d§7 ticks measured: avg §f%.3fms§7/tick, max §f%.3fms§7.",
                run.ids.size(), run.samples, avgMs, maxMs));
        Bukkit.getLogger().info(String.format(
                "[SwarmSpike] n=%d shulker=%b avgMs=%.3f maxMs=%.3f samples=%d",
                run.ids.size(), run.shulker, avgMs, maxMs, run.samples));
    }

    private static void despawn(Player bukkitPlayer, List<Integer> ids) {
        if (ids.isEmpty())
            return;
        ServerPlayer sp = ((CraftPlayer) bukkitPlayer).getHandle();
        Object packet = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(ids.stream().mapToInt(Integer::intValue).toArray()));
        send(sp, packet);
    }

    private static void send(ServerPlayer sp, Object packet) {
        ServerGamePacketListenerImpl connection = sp.connection;
        connection.send((Packet<?>) packet);
    }

    /**
     * Shulkers are left fully visible (no Invisible flag) for this spike — you need to SEE
     * the platform to judge standing-on-it feel. Item displays get a real item + scale, or
     * they render as nothing (an item_display with no ItemStack metadata is blank).
     */
    private static List<Object> metadata(boolean shulker) {
        List<Object> values = new ArrayList<>();
        if (shulker) {
            // NoGravity + Silent: this is a packet-only entity (no real server-side Entity/AI
            // ticking it), so these mostly guard against client-side prediction fighting our
            // position sync (falling/settling animations) rather than any real physics.
            net.momirealms.craftengine.bukkit.entity.data.BaseEntityData.NoGravity.addEntityData(true, values);
            net.momirealms.craftengine.bukkit.entity.data.BaseEntityData.Silent.addEntityData(true, values);
            net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData.AttachFace
                    .addEntityData(net.minecraft.core.Direction.DOWN, values);
            net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData.RawPeekAmount.addEntityData((byte) 0, values);
            net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData.Color.addEntityData((byte) 14, values); // red
        } else {
            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(
                    net.minecraft.world.item.Items.DIAMOND_BLOCK);
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.ItemDisplayData.ItemStack.addEntityData(stack, values);
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.Scale
                    .addEntityData(new org.joml.Vector3f(0.9f, 0.9f, 0.9f), values);
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.BrightnessOverride
                    .addEntityData((15 << 4) | (15 << 20), values);
            // Wider than Conveyor's duration=1: that's tuned for ~1-4 items in flight at once,
            // where packet delivery is very regular. A 60-entity swarm bundled every tick has
            // more per-tick work, so a slightly wider window (3 ticks) buffers minor scheduling
            // jitter instead of the client snapping hard whenever a packet lands a hair late.
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.PosRotInterpolationDuration.addEntityData(4, values);
            net.momirealms.craftengine.bukkit.entity.data.DisplayData.TransformationInterpolationDuration
                    .addEntityData(4, values);
        }
        return values;
    }

    private static final class Run {
        List<Integer> ids;
        List<double[]> offsets;
        double baseX, baseY, baseZ;
        boolean shulker;
        Player player;
        BukkitTask task;
        int tickCount = 0;
        long totalNanos = 0;
        long maxNanos = 0;
        int samples = 0;
        double lastOffset = 0;
        boolean carrying = false;
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
