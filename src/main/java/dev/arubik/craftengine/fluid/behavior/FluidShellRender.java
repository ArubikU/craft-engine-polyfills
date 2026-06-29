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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Per-face display-entity SHELL for the Create-style multiblock fluid tank. The block model itself is empty
 * (invisible) so the multiblock's interior is SEE-THROUGH; this renderer draws only the EXTERIOR faces of the
 * group with flat {@link ItemDisplay} quads — one quad per face whose neighbour is NOT in the same group.
 *
 * <p>Mirrors {@link FluidTankRender}'s lifecycle: displays are tracked per controller (controller pos asLong
 * -&gt; List&lt;UUID&gt;), reused by index, surplus removed, and tagged {@code cml_fluidbox} so they share the
 * existing cleanup path.</p>
 *
 * <p>Geometry: each quad model ({@code cml:block/fluid_shell/window} or {@code .../cap}) is a flat 16×16 quad
 * whose visible face is the model's NORTH face (outward normal -Z). An {@link ItemDisplay} centres the model on
 * the entity, so placing the entity at the block centre (cell + 0.5 on each axis) with IDENTITY rotation lands
 * the quad exactly on that block's north face plane. The other five faces are obtained by rotating about the
 * block centre so the quad's -Z face points outward in that direction:
 * NORTH=identity, SOUTH=rotateY(180°), EAST=rotateY(-90°), WEST=rotateY(90°), UP=rotateX(-90°), DOWN=rotateX(90°).
 * No extra translation is needed — rotating about the centre keeps the quad on the correct face plane.</p>
 */
public final class FluidShellRender {

    private FluidShellRender() {
    }

    /** controller pos (asLong) -> per-face display entity UUIDs. */
    private static final Map<Long, java.util.List<UUID>> SHELLS = new ConcurrentHashMap<>();

    /**
     * Spawn/update/remove the exterior shell for ONE group.
     *
     * @param level the world
     * @param owner solved cell -> [controllerPos, w, h] map (from solveComponent); membership of the SAME
     *              controller marks a cell as part of this group
     * @param ctrl  the controller pos (asLong) of the group to (re)render
     */
    public static void update(Level level, java.util.Map<Long, long[]> owner, long ctrl) {
        if (!(level instanceof ServerLevel server))
            return;
        org.bukkit.World world = server.getWorld();
        java.util.List<UUID> displays = SHELLS.computeIfAbsent(ctrl, k -> new java.util.ArrayList<>());

        int idx = 0;
        for (java.util.Map.Entry<Long, long[]> e : owner.entrySet()) {
            if (e.getValue()[0] != ctrl)
                continue; // only THIS group's cells
            BlockPos cell = BlockPos.of(e.getKey());
            Location center = new Location(world, cell.getX() + 0.5, cell.getY() + 0.5, cell.getZ() + 0.5);
            for (Direction d : Direction.values()) {
                BlockPos np = cell.relative(d);
                long[] na = owner.get(np.asLong());
                boolean interior = na != null && na[0] == ctrl; // neighbour in SAME group -> hidden
                if (interior)
                    continue;
                ItemStack item = faceItem(d);
                if (item == null)
                    continue;
                Transformation t = new Transformation(new Vector3f(0f, 0f, 0f), rotationFor(d),
                        new Vector3f(1f, 1f, 1f), new Quaternionf());
                ItemDisplay disp = idx < displays.size() ? validDisplay(world, displays.get(idx)) : null;
                if (disp == null) {
                    ItemStack fi = item;
                    Transformation ft = t;
                    disp = world.spawn(center, ItemDisplay.class, ent -> {
                        ent.addScoreboardTag("cml_fluidbox");
                        ent.setItemStack(fi);
                        ent.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                        ent.setPersistent(true);
                        ent.setTransformation(ft);
                    });
                    if (idx < displays.size())
                        displays.set(idx, disp.getUniqueId());
                    else
                        displays.add(disp.getUniqueId());
                } else {
                    disp.setItemStack(item);
                    disp.teleport(center);
                    disp.setTransformation(t);
                }
                idx++;
            }
        }
        // Remove surplus displays (group shrank / a face became interior).
        for (int i = displays.size() - 1; i >= idx; i--) {
            UUID id = displays.remove(i);
            Entity ent = id != null ? world.getEntity(id) : null;
            if (ent != null)
                ent.remove();
        }
        if (displays.isEmpty())
            SHELLS.remove(ctrl);
    }

    /** Rotation that orients the north-facing quad to point outward along {@code d}. */
    private static Quaternionf rotationFor(Direction d) {
        return switch (d) {
            case NORTH -> new Quaternionf();
            case SOUTH -> new Quaternionf().rotateY((float) Math.toRadians(180));
            case EAST -> new Quaternionf().rotateY((float) Math.toRadians(-90));
            case WEST -> new Quaternionf().rotateY((float) Math.toRadians(90));
            case UP -> new Quaternionf().rotateX((float) Math.toRadians(-90));
            case DOWN -> new Quaternionf().rotateX((float) Math.toRadians(90));
        };
    }

    /** Side faces use the window quad; top/bottom use the cap quad. */
    private static ItemStack faceItem(Direction d) {
        String id = (d == Direction.UP || d == Direction.DOWN) ? "fluidshell_cap" : "fluidshell_window";
        try {
            var def = CraftEngineItems.byId(Key.of("cml", id));
            return def != null ? def.buildBukkitItem() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static ItemDisplay validDisplay(org.bukkit.World world, UUID id) {
        Entity e = id != null ? world.getEntity(id) : null;
        return e instanceof ItemDisplay d && d.isValid() ? d : null;
    }

    /** Remove ALL of the group's shell displays (group emptied / controller demoted). */
    public static void remove(org.bukkit.World world, long controllerKey) {
        java.util.List<UUID> ids = SHELLS.remove(controllerKey);
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
