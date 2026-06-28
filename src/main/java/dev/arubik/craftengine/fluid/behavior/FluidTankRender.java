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

    /** controller pos (asLong) -> display entity UUID. */
    private static final Map<Long, UUID> BOXES = new ConcurrentHashMap<>();

    // Insets (block units): side wall + top/bottom margins so the fluid sits inside the frame/window.
    private static final float HULL = 1f / 16f + 1f / 128f;
    private static final float BOTTOM_MARGIN = 1f / 8f;
    private static final float TOP_MARGIN = 1f / 8f;

    /**
     * Spawn/update/remove the fluid display for a tank group.
     *
     * @param controller min-corner block of the group
     * @param width      footprint side (1..3)
     * @param height     group height in blocks
     * @param type       fluid type (EMPTY removes the display)
     * @param fill       0..1 fill fraction of the whole group
     */
    public static void update(Level level, BlockPos controller, int width, int height, FluidType type, double fill) {
        if (!(level instanceof ServerLevel server))
            return;
        org.bukkit.World world = server.getWorld();
        long key = controller.asLong();

        if (type == FluidType.EMPTY || fill <= 0.0) {
            remove(world, key);
            return;
        }

        ItemStack item = levelItem(type, fill);
        if (item == null) {
            remove(world, key);
            return;
        }

        // The level model already encodes the surface height (~fill of one block); scaling Y by the group
        // height makes the column ~fill*height tall. Sides inset by HULL so it shows through the window.
        float innerW = Math.max(0.01f, width - 2 * HULL);
        float innerH = Math.max(0.01f, height - BOTTOM_MARGIN - TOP_MARGIN);
        Location loc = new Location(world, controller.getX(), controller.getY(), controller.getZ());
        Transformation t = new Transformation(
                new Vector3f(HULL, BOTTOM_MARGIN, HULL),
                new Quaternionf(),
                new Vector3f(innerW, innerH, innerW),
                new Quaternionf());

        Entity existing = world.getEntity(BOXES.getOrDefault(key, new UUID(0, 0)));
        ItemDisplay box = existing instanceof ItemDisplay d && d.isValid() ? d : null;
        if (box == null) {
            box = world.spawn(loc, ItemDisplay.class, e -> {
                e.addScoreboardTag("cml_fluidbox");
                e.setItemStack(item);
                e.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                e.setPersistent(true);
                e.setTransformation(t);
            });
            BOXES.put(key, box.getUniqueId());
        } else {
            box.setItemStack(item);
            box.teleport(loc);
            box.setTransformation(t);
        }
    }

    /** Build the level item (cml:fluidlvl_&lt;type&gt;_&lt;0..15&gt;) for this fill fraction. */
    private static ItemStack levelItem(FluidType type, double fill) {
        String tn = switch (type) {
            case WATER, MILK, POWDER_SNOW -> "water";
            case LAVA, HONEY, SLIME -> "lava";
            case EXPERIENCE -> "xp";
            default -> "water";
        };
        int lvl = Math.max(0, Math.min(15, (int) Math.round(fill * 15)));
        try {
            var d = CraftEngineItems.byId(Key.of("cml", "fluidlvl_" + tn + "_" + lvl));
            return d != null ? d.buildBukkitItem() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    /** Remove the group's display (block broken / group emptied). */
    public static void remove(org.bukkit.World world, long controllerKey) {
        UUID id = BOXES.remove(controllerKey);
        if (id != null && world != null) {
            Entity e = world.getEntity(id);
            if (e != null)
                e.remove();
        }
    }

    public static void remove(Level level, BlockPos controller) {
        if (level instanceof ServerLevel server)
            remove(server.getWorld(), controller.asLong());
    }
}
