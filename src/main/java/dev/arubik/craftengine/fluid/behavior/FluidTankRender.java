package dev.arubik.craftengine.fluid.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.ChunkPos;

/**
 * Server-side recreation of Create's fluid box — there is no client mod, so the fluid is a stack of fake
 * {@code minecraft:item_display} entities sent by PACKET (never real Bukkit entities). ONE display per
 * footprint cell per filled layer, showing the matching level model (cml:fluidlvl_&lt;type&gt;_0..15).
 *
 * <p>{@link #update} (event-driven, on fluid/topology change) recomputes the desired display set for a
 * group; {@link #flush} (called every tick by the tank controller) broadcasts to the players currently
 * tracking the controller's chunk — so anyone who loads the chunk / joins / widens render distance gets it
 * next tick, and players who leave drop it. {@link #remove} despawns the whole group.</p>
 */
public final class FluidTankRender {

    private FluidTankRender() {
    }

    /** controller pos (asLong) -> its live + pending-despawn displays. */
    private static final Map<Long, GroupBoxes> RENDERS = new ConcurrentHashMap<>();

    // Side wall inset (block units) so the fluid sits inside the frame/window.
    private static final float HULL = 1f / 16f + 1f / 128f;

    private static final class GroupBoxes {
        final List<FluidDisplay> live = new ArrayList<>();
        final List<FluidDisplay> dead = new ArrayList<>(); // removed slots awaiting a despawn broadcast
    }

    /**
     * Recompute the desired fluid displays for a group (no packets here — {@link #flush} sends them).
     * ONE native 1×1 display PER CELL per filled layer, stacked, so cells abut seamlessly (no stretch).
     *
     * @param controller min-corner (bottom) block of the group
     * @param width      footprint side (1..3)
     * @param height     group height in blocks
     * @param type       fluid type (EMPTY clears everything)
     * @param fill       0..1 fill fraction of the whole group
     */
    public static void update(Level level, BlockPos controller, int width, int height, FluidType type, double fill) {
        if (!(level instanceof ServerLevel))
            return;
        long key = controller.asLong();
        if (type == FluidType.EMPTY || fill <= 0.0) {
            remove(level, controller);
            return;
        }
        GroupBoxes g = RENDERS.computeIfAbsent(key, k -> new GroupBoxes());

        double fluidBlocks = fill * height; // total fluid column height in blocks
        int idx = 0;
        for (int y = 0; y < height; y++) {
            double layerFill = Math.max(0.0, Math.min(1.0, fluidBlocks - y));
            if (y > 0 && layerFill <= 0.001)
                break;
            int rawLevel = (int) Math.round(layerFill * 15);
            if (y > 0 && rawLevel <= 0)
                break;
            // Capped layers max at _11 (taller pokes the frame caps); the bottom layer is lifted +4px so it
            // clears the bottom cap; min level _0.
            int hi = (y == 0 || y == height - 1) ? 11 : 15;
            int lvl = Math.max(0, Math.min(hi, rawLevel));
            float yoff = (y == 0) ? 4f / 16f : 0f;
            ItemStack bukkit = levelItem(type, lvl);
            Object nms = bukkit != null ? CraftItemStack.asNMSCopy(bukkit) : null;
            if (nms == null)
                continue;
            for (int dx = 0; dx < width; dx++)
                for (int dz = 0; dz < width; dz++) {
                    float ax = dx == 0 ? HULL : 0f, bx = 1f - (dx == width - 1 ? HULL : 0f);
                    float az = dz == 0 ? HULL : 0f, bz = 1f - (dz == width - 1 ? HULL : 0f);
                    double wx = controller.getX() + dx + (ax + bx) / 2f;
                    double wy = controller.getY() + y + 0.5f + yoff;
                    double wz = controller.getZ() + dz + (az + bz) / 2f;
                    FluidDisplay d = idx < g.live.size() ? g.live.get(idx) : null;
                    if (d == null) {
                        d = new FluidDisplay();
                        g.live.add(d);
                    }
                    d.setNmsItem(nms);
                    d.setScale(bx - ax, 1f, bz - az);
                    d.setTarget(wx, wy, wz);
                    idx++;
                }
        }
        // Surplus displays (level dropped / group shrank) -> despawn on the next flush.
        for (int i = g.live.size() - 1; i >= idx; i--)
            g.dead.add(g.live.remove(i));
    }

    /** Broadcast a group's displays to the players currently tracking the controller's chunk (per tick). */
    public static void flush(Level level, BlockPos controller) {
        GroupBoxes g = RENDERS.get(controller.asLong());
        if (g == null)
            return;
        List<Player> viewers = viewersOf(level, controller);
        if (!g.dead.isEmpty()) {
            for (FluidDisplay d : g.dead)
                d.despawnAll(viewers);
            g.dead.clear();
        }
        for (FluidDisplay d : g.live)
            d.render(viewers, d.consumeMetaDirty());
    }

    /** Despawn ALL of the group's displays (block broken / group emptied). */
    public static void remove(Level level, BlockPos controller) {
        GroupBoxes g = RENDERS.remove(controller.asLong());
        if (g == null)
            return;
        List<Player> viewers = viewersOf(level, controller);
        for (FluidDisplay d : g.live)
            d.despawnAll(viewers);
        for (FluidDisplay d : g.dead)
            d.despawnAll(viewers);
    }

    private static List<Player> viewersOf(Level level, BlockPos controller) {
        try {
            org.bukkit.World bw = ((ServerLevel) level).getWorld();
            return new BukkitWorld(bw).getTrackedBy(new ChunkPos(controller.getX() >> 4, controller.getZ() >> 4));
        } catch (Throwable t) {
            return List.of();
        }
    }

    /** Build the level item cml:fluidlvl_&lt;type&gt;_&lt;0..15&gt; for an explicit level index. */
    private static ItemStack levelItem(FluidType type, int lvl) {
        String tn = switch (type) {
            case WATER, MILK, POWDER_SNOW -> "water";
            case LAVA, HONEY, SLIME -> "lava";
            case EXPERIENCE -> "xp";
            default -> "water";
        };
        lvl = Math.max(0, Math.min(15, lvl));
        try {
            var d = CraftEngineItems.byId(Key.of("cml", "fluidlvl_" + tn + "_" + lvl));
            return d != null ? d.buildBukkitItem() : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
