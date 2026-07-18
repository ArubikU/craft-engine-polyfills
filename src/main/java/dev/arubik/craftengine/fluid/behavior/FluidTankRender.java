package dev.arubik.craftengine.fluid.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.contraption.level.ContraptionBoundary;
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
        // Parallel to `live`: each display's LOCAL (pre-bearing-transform) target, so flush() can
        // re-resolve the real-world position every tick — update() only recomputes this on fluid/
        // topology change, but a contraption's bearing moves/rotates every tick regardless, so
        // baking the transform once inside update() left the fluid frozen at its capture-time spot.
        final List<double[]> localTargets = new ArrayList<>();
        /**
         * Parallel to {@code live}: each display's UNSCALED cell box size in block units — the size the
         * slab has on a free-standing (non-contraption) tank. Kept separate from the size actually sent
         * for the same reason {@link #localTargets} is kept separate from the sent position: a
         * contraption's {@code scale} is a LIVE value (the Creative Phys Wand resizes a rendered body),
         * so the final size must be recomputed from this base every {@link #flush}, and multiplying the
         * stored value in place would compound the factor on every tick.
         */
        final List<float[]> localScales = new ArrayList<>();
    }

    /**
     * Multiplies an unscaled cell box size by the contraption's uniform {@code scale} (roadmap item #9),
     * yielding the {@code DisplayData.Scale} an {@code ITEM_DISPLAY} slab must carry to stay glued to its
     * (already scale-projected) tank block.
     *
     * <p>Scaling all three components by the SAME factor is what keeps this correct in the presence of the
     * per-axis hull inset baked into {@code base} (x/z are {@code < 1} so the fluid sits inside the frame,
     * y is a full block): a {@code Display} places a model vertex {@code v} at
     * {@code Translation + LeftRotation·(Scale·(RightRotation·v))}, and this renderer's {@code LeftRotation}
     * is the bearing's orientation. Since {@code s} is uniform,
     * {@code LeftRotation·((s·Base)·v) == s·(LeftRotation·(Base·v))} — the inset box is still shaped and
     * oriented exactly as before, just grown about the entity position, which
     * {@code ContraptionBoundary#realWorldPositionOf} has already put at the scaled place. Adjacent slabs
     * therefore keep abutting seamlessly at any scale, exactly as they do at {@code s == 1}.
     */
    static float[] scaledCellBox(float[] base, double scale) {
        float s = (float) scale;
        return new float[] { base[0] * s, base[1] * s, base[2] * s };
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
            // Frame caps clip the level: a layer with ONE cap (bottom-only or top-only) maxes at _11; a
            // single block (height==1, BOTH caps) maxes at _7; uncapped middle layers use the full 0..15.
            boolean cap0 = (y == 0), capT = (y == height - 1);
            int hi = (cap0 && capT) ? 7 : ((cap0 || capT) ? 11 : 15);
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
                    // LOCAL (pre-bearing-transform) position — flush() re-resolves the real-world
                    // position from this every tick, so a moving/rotating contraption's fluid keeps
                    // following the bearing instead of freezing at capture-time's position.
                    double lx = controller.getX() + dx + (ax + bx) / 2f;
                    double ly = controller.getY() + y + 0.5f + yoff;
                    double lz = controller.getZ() + dz + (az + bz) / 2f;
                    FluidDisplay d = idx < g.live.size() ? g.live.get(idx) : null;
                    double[] local;
                    float[] baseScale;
                    if (d == null) {
                        d = new FluidDisplay();
                        g.live.add(d);
                        local = new double[3];
                        g.localTargets.add(local);
                        baseScale = new float[3];
                        g.localScales.add(baseScale);
                    } else {
                        local = idx < g.localTargets.size() ? g.localTargets.get(idx) : new double[3];
                        if (idx >= g.localTargets.size())
                            g.localTargets.add(local);
                        baseScale = idx < g.localScales.size() ? g.localScales.get(idx) : new float[3];
                        if (idx >= g.localScales.size())
                            g.localScales.add(baseScale);
                    }
                    local[0] = lx;
                    local[1] = ly;
                    local[2] = lz;
                    // UNSCALED box size only — flush() applies the contraption's live scale to it and is
                    // what actually pushes the size to the display (see GroupBoxes#localScales).
                    baseScale[0] = bx - ax;
                    baseScale[1] = 1f;
                    baseScale[2] = bz - az;
                    d.setNmsItem(nms);
                    idx++;
                }
        }
        // Surplus displays (level dropped / group shrank) -> despawn on the next flush.
        for (int i = g.live.size() - 1; i >= idx; i--) {
            g.dead.add(g.live.remove(i));
            if (i < g.localTargets.size())
                g.localTargets.remove(i);
            if (i < g.localScales.size())
                g.localScales.remove(i);
        }
    }

    /**
     * Broadcast a group's displays to the players currently tracking the controller's chunk (per
     * tick) — ALSO re-resolves each display's real-world position/rotation/SIZE from its stored LOCAL
     * target every call, not just when {@link #update} last ran, so a moving/rotating/resizing
     * contraption keeps the fluid glued to the bearing instead of leaving it frozen at capture-time's
     * spot.
     *
     * <p><b>Why SIZE is resolved here and not in {@link #update} (2026-07-16 fix — "the liquid render on
     * fluid block tank and outside of it when in a contraption dont inherit the scale").</b> The tank's
     * liquid is the one tank visual this project renders itself; its SHELL is CraftEngine's own
     * {@code entity-renderer: { item: cml:fbt_* } }, mirrored and already scaled by
     * {@code ContraptionBlockEntityElementMirror}/{@code ContraptionRenderScale}. So on a scaled
     * contraption the shell grew and the liquid did not: each slab's POSITION was always correct (it goes
     * through {@code ContraptionBoundary#realWorldPositionOf}, whose projection scales the offset from the
     * bearing pivot) but its {@code DisplayData.Scale} stayed at the unscaled cell box — leaving the liquid
     * as detached 1x cubes with gaps inside an enlarged tank at {@code scale > 1}, and protruding out
     * through the tank's walls at {@code scale < 1} (the "and outside of it" half of the report).
     *
     * <p>{@link #update} is the wrong place to fix it because it is EVENT-driven (fluid amount / group
     * topology change only). A contraption's scale is LIVE — the Creative Phys Wand resizes a body that is
     * already rendered and whose fluid is not changing — so a size baked at update time would only ever be
     * right until the next resize. This method already re-resolves position and rotation every tick for
     * exactly that reason; size now rides the same path, and {@code FluidDisplay#setScale}'s epsilon gate
     * keeps it a no-op packet-wise until the factor actually moves.
     */
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
        ContraptionBoundary contraption = ContraptionBoundary.of(level).orElse(null);
        org.joml.Quaternionf rotation = contraption != null ? contraption.realOrientationOf(null) : null;
        double scale = contraption != null ? contraption.realScaleFactor() : 1.0;
        for (int i = 0; i < g.live.size(); i++) {
            FluidDisplay d = g.live.get(i);
            double[] local = i < g.localTargets.size() ? g.localTargets.get(i) : null;
            float[] baseScale = i < g.localScales.size() ? g.localScales.get(i) : null;
            if (baseScale != null) {
                float[] box = scaledCellBox(baseScale, scale);
                d.setScale(box[0], box[1], box[2]);
            }
            if (local != null) {
                double wx = local[0], wy = local[1], wz = local[2];
                if (contraption != null) {
                    Vec3 real = contraption.realWorldPositionOf(new Vec3(wx, wy, wz));
                    wx = real.x;
                    wy = real.y;
                    wz = real.z;
                }
                d.setTarget(wx, wy, wz);
                d.setRotation(rotation); // null -> identity, matches a non-contraption tank
            }
            // Consumed AFTER the setters above, never before: setScale/setRotation are themselves what
            // raise the dirty flag, so reading it first would defer every size/orientation change to the
            // NEXT flush — i.e. resend it a tick late, against a body that has since moved on again.
            boolean metaDirty = d.consumeMetaDirty();
            d.render(viewers, metaDirty);
        }
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
            // A contraption level is a real, separate, hidden dimension with zero real players
            // ever inside it — chunk-tracking against it is always empty. Redirect to the real
            // world at the bearing's live transform instead (see ContraptionBoundary#realViewers).
            ContraptionBoundary contraption = ContraptionBoundary.of(level).orElse(null);
            if (contraption != null) {
                return contraption.realViewers(controller);
            }
            org.bukkit.World bw = ((ServerLevel) level).getWorld();
            return dev.arubik.craftengine.util.CeWorlds.of(bw).getTrackedBy(new ChunkPos(controller.getX() >> 4, controller.getZ() >> 4));
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
