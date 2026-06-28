package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Server-side recreation of Create's {@code FluidTankRenderer#renderFluidBox} — there is no client mod, so
 * the fluid "box" is a {@link BlockDisplay} the plugin spawns at the group controller and scales to the
 * inner volume × fill height (inset from the walls so it shows through the window). One display per group,
 * keyed by controller position; updated on every store change, removed when empty.
 */
public final class FluidTankRender {

    private FluidTankRender() {
    }

    /** controller pos (asLong) -> display entity UUID. */
    private static final Map<Long, UUID> BOXES = new ConcurrentHashMap<>();

    // Create's insets (block units): side wall + bottom margin so the fluid sits inside the frame/window.
    private static final float HULL = 1f / 16f + 1f / 128f;
    private static final float BOTTOM_MARGIN = 1f / 8f;
    private static final float TOP_MARGIN = 1f / 8f;

    /**
     * Spawn/update/remove the fluid box for a tank group.
     *
     * @param controller min-corner block of the group
     * @param width      footprint side (1..3)
     * @param height     group height in blocks
     * @param type       fluid type (EMPTY removes the box)
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

        Entity existing = world.getEntity(BOXES.getOrDefault(key, new UUID(0, 0)));
        BlockDisplay box = existing instanceof BlockDisplay bd && bd.isValid() ? bd : null;

        BlockData data = blockFor(type);
        // Inner box (Create geometry): inset sides by HULL, leave top/bottom margins; height scales with fill.
        float innerW = Math.max(0.01f, width - 2 * HULL);
        float innerH = Math.max(0.01f, (float) (height - BOTTOM_MARGIN - TOP_MARGIN));
        float fluidH = (float) Math.max(0.02f, fill * innerH);
        // Entity sits at the controller block corner; translation + scale place the box in the interior.
        Location loc = new Location(world, controller.getX(), controller.getY(), controller.getZ());
        Transformation t = new Transformation(
                new Vector3f(HULL, BOTTOM_MARGIN, HULL),
                new Quaternionf(),
                new Vector3f(innerW, fluidH, innerW),
                new Quaternionf());

        if (box == null) {
            box = world.spawn(loc, BlockDisplay.class, e -> {
                e.addScoreboardTag("cml_fluidbox");
                e.setBlock(data);
                e.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                e.setPersistent(true);
                e.setTransformation(t);
            });
            BOXES.put(key, box.getUniqueId());
        } else {
            if (!box.getBlock().equals(data))
                box.setBlock(data);
            box.teleport(loc);
            box.setTransformation(t);
        }
    }

    /** Remove the group's box (block broken / group emptied). */
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

    /** The block a fluid type renders as inside the tank window. Water/lava use the real (animated) fluid. */
    private static BlockData blockFor(FluidType type) {
        Material m = switch (type) {
            case WATER -> Material.WATER;
            case LAVA -> Material.LAVA;
            case SLIME -> Material.SLIME_BLOCK;
            case HONEY -> Material.HONEY_BLOCK;
            case POWDER_SNOW -> Material.POWDER_SNOW;
            case MILK -> Material.WHITE_CONCRETE;
            case EXPERIENCE -> Material.LIME_STAINED_GLASS;
            default -> Material.WATER;
        };
        return m.createBlockData();
    }
}
