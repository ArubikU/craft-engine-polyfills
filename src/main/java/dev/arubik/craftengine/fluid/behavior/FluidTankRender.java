package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Server-side recreation of Create's {@code FluidTankRenderer#renderFluidBox} — there is no client mod, so
 * the fluid is an {@link ItemDisplay} the plugin spawns at the group controller, showing one of the
 * per-level fluid models (cml:fluidlvl_&lt;type&gt;_0..15, textured water/lava/xp like the personal tank) and
 * scaled to the inner volume (inset from the walls so it sits inside the window). One display per group,
 * keyed by controller position; updated on every store change, removed when empty.
 */
public final class FluidTankRender {

    private FluidTankRender() {
    }

    /** controller pos (asLong) -> per-layer display entity UUIDs (bottom..top). */
    private static final Map<Long, java.util.List<UUID>> BOXES = new ConcurrentHashMap<>();

    // Side wall inset (block units) so the fluid sits inside the frame/window.
    private static final float HULL = 1f / 16f + 1f / 128f;

    /**
     * Spawn/update/remove the fluid display for a tank group — ONE display PER BLOCK LAYER, STACKED (not a
     * single stretched box). Each layer shows the level model matching how full that layer is, at its own
     * block, so the texture is never stretched vertically and the column fills bottom-up.
     *
     * @param controller min-corner (bottom) block of the group
     * @param width      footprint side (1..3)
     * @param height     group height in blocks
     * @param type       fluid type (EMPTY removes everything)
     * @param fill       0..1 fill fraction of the whole group
     */
    public static void update(Level level, BlockPos controller, int width, int height, FluidType type, double fill) {
        if (!(level instanceof ServerLevel server))
            return;
        org.bukkit.World world = server.getWorld();
        long key = controller.asLong();
        java.util.List<UUID> displays = BOXES.computeIfAbsent(key, k -> new java.util.ArrayList<>());

        if (type == FluidType.EMPTY || fill <= 0.0) {
            remove(world, key);
            return;
        }

        double fluidBlocks = fill * height; // total fluid column height in blocks
        // ONE native 1×1 display PER CELL (footprint w×w × each filled layer): NO x/z scaling, so cells abut
        // seamlessly — no stretch, no gaps, no misalignment. 2×2 floor = 4 displays, 3×3 floor = 9, etc.
        // ItemDisplay centres the model on the entity, so place at the cell centre (+0.5) with scale 1.
        int idx = 0;
        for (int y = 0; y < height; y++) {
            double layerFill = Math.max(0.0, Math.min(1.0, fluidBlocks - y)); // 0..1 within this block layer
            // The bottom layer (y==0) ALWAYS renders while the group holds any fluid (caller already
            // returned for EMPTY/0). Higher layers stop once empty.
            if (y > 0 && layerFill <= 0.001)
                break; // no fluid above here
            int rawLevel = (int) Math.round(layerFill * 15);
            if (y > 0 && rawLevel <= 0)
                break; // negligible sliver above the bottom
            // Cap clearance: the bottom and top capped layers max out at level _11 (the taller levels poke
            // through the frame caps); middle layers use the full 0..15. The bottom layer is additionally
            // lifted +4px in Y (see yoff below) so it clears the bottom cap — min level is _0.
            int lo = 0;
            int hi = (y == 0 || y == height - 1) ? 11 : 15;
            int lvl = Math.max(lo, Math.min(hi, rawLevel));
            float yoff = (y == 0) ? 4f / 16f : 0f; // ONLY this tank's bottom fluid layer gets the 4px lift
            ItemStack item = levelItem(type, lvl);
            if (item == null)
                continue;
            for (int dx = 0; dx < width; dx++)
                for (int dz = 0; dz < width; dz++) {
                    Location loc = new Location(world, controller.getX() + dx, controller.getY() + y,
                            controller.getZ() + dz);
                    // Inset ONLY the footprint-boundary faces by HULL (so the fluid sits inside the walls and
                    // doesn't clip through), full on interior faces (so neighbouring cells abut with no gap).
                    float ax = dx == 0 ? HULL : 0f, bx = 1f - (dx == width - 1 ? HULL : 0f);
                    float az = dz == 0 ? HULL : 0f, bz = 1f - (dz == width - 1 ? HULL : 0f);
                    Transformation t = new Transformation(
                            new Vector3f((ax + bx) / 2f, 0.5f + yoff, (az + bz) / 2f), new Quaternionf(),
                            new Vector3f(bx - ax, 1f, bz - az), new Quaternionf());
                    ItemDisplay box = idx < displays.size() ? validDisplay(world, displays.get(idx)) : null;
                    if (box == null) {
                        ItemStack fi = item;
                        box = world.spawn(loc, ItemDisplay.class, e -> {
                            e.addScoreboardTag("cml_fluidbox");
                            e.setItemStack(fi);
                            e.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                            e.setPersistent(true);
                            e.setTransformation(t);
                        });
                        if (idx < displays.size())
                            displays.set(idx, box.getUniqueId());
                        else
                            displays.add(box.getUniqueId());
                    } else {
                        box.setItemStack(item);
                        box.teleport(loc);
                        box.setTransformation(t);
                    }
                    idx++;
                }
        }
        int layerIdx = idx;
        // Remove surplus displays (fluid level dropped / group shrank).
        for (int i = displays.size() - 1; i >= layerIdx; i--) {
            UUID id = displays.remove(i);
            Entity e = id != null ? world.getEntity(id) : null;
            if (e != null)
                e.remove();
        }
    }

    private static ItemDisplay validDisplay(org.bukkit.World world, UUID id) {
        Entity e = id != null ? world.getEntity(id) : null;
        return e instanceof ItemDisplay d && d.isValid() ? d : null;
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

    /** Remove ALL of the group's layer displays (block broken / group emptied). */
    public static void remove(org.bukkit.World world, long controllerKey) {
        java.util.List<UUID> ids = BOXES.remove(controllerKey);
        if (ids != null && world != null)
            for (UUID id : ids) {
                Entity e = id != null ? world.getEntity(id) : null;
                if (e != null)
                    e.remove();
            }
    }

    public static void remove(Level level, BlockPos controller) {
        if (level instanceof ServerLevel server)
            remove(server.getWorld(), controller.asLong());
    }
}
