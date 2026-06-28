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
        float innerW = Math.max(0.01f, width - 2 * HULL);
        int layerIdx = 0;
        for (int y = 0; y < height; y++) {
            double layerFill = Math.max(0.0, Math.min(1.0, fluidBlocks - y)); // 0..1 within this block layer
            if (layerFill <= 0.001)
                break; // no fluid above here
            ItemStack item = levelItem(type, layerFill);
            if (item == null)
                continue;
            // The level model already has height ~layerFill of ONE block — NO vertical scale (no stretch).
            // ItemDisplay centers the item at the entity, so translate +0.5 on every axis to drop the model
            // into [0,1]³ of THIS block (centered at x/z = 0.5, lifted up out of the floor).
            Location loc = new Location(world, controller.getX(), controller.getY() + y, controller.getZ());
            Transformation t = new Transformation(
                    new Vector3f(0.5f, 0.5f, 0.5f),
                    new Quaternionf(),
                    new Vector3f(innerW, 1f, innerW),
                    new Quaternionf());
            ItemDisplay box = layerIdx < displays.size() ? validDisplay(world, displays.get(layerIdx)) : null;
            if (box == null) {
                ItemStack fi = item;
                box = world.spawn(loc, ItemDisplay.class, e -> {
                    e.addScoreboardTag("cml_fluidbox");
                    e.setItemStack(fi);
                    e.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                    e.setPersistent(true);
                    e.setTransformation(t);
                });
                if (layerIdx < displays.size())
                    displays.set(layerIdx, box.getUniqueId());
                else
                    displays.add(box.getUniqueId());
            } else {
                box.setItemStack(item);
                box.teleport(loc);
                box.setTransformation(t);
            }
            layerIdx++;
        }
        // Remove surplus layer displays (fluid level dropped).
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

    /** Build the level item (cml:fluidlvl_&lt;type&gt;_&lt;0..15&gt;) for this layer's fill fraction. */
    private static ItemStack levelItem(FluidType type, double layerFill) {
        String tn = switch (type) {
            case WATER, MILK, POWDER_SNOW -> "water";
            case LAVA, HONEY, SLIME -> "lava";
            case EXPERIENCE -> "xp";
            default -> "water";
        };
        int lvl = Math.max(0, Math.min(15, (int) Math.round(layerFill * 15)));
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
