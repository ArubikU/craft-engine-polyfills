package dev.arubik.craftengine.conveyor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * Two-point wand placement for conveyor belts.
 *
 * <p>While holding the conveyor block item, a player LEFT-clicks one block to set
 * point A and a second block to set point B. The line A-&gt;B must be a straight
 * single-axis horizontal run (optionally a consistent 45-degree slope via Y),
 * every intermediate block must be air/replaceable, and a {@code polyfills:vapor_motor}
 * must sit adjacent to A facing into A. On success the whole belt line is placed
 * with linked {@code prevPos}.</p>
 *
 * <p>Registered (no-arg ctor) by {@code CraftEnginePolyfills}.</p>
 */
public class ConveyorWandListener implements Listener {

    /** Per-player first selection (point A) awaiting a second click. */
    private final Map<UUID, BlockPos> selections = new HashMap<>();

    public ConveyorWandListener() {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        Key handId = CraftEngineItems.getCustomItemId(hand);
        if (!ConveyorBehavior.POLYFILL_CONVEYOR.equals(handId))
            return;

        // It's our wand item: take over the left-click entirely.
        event.setCancelled(true);

        org.bukkit.entity.Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        BlockPos clickedPos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());

        BlockPos a = selections.get(id);
        if (a == null) {
            selections.put(id, clickedPos);
            player.sendMessage("§aConveyor point A set. Left-click point B to place the belt.");
            return;
        }

        // Second click: clear selection regardless of outcome.
        selections.remove(id);
        BlockPos b = clickedPos;

        ConveyorPath.Result plan = ConveyorPath.plan(a.x(), a.y(), a.z(), b.x(), b.y(), b.z());
        if (!plan.isValid()) {
            player.sendMessage("§cCannot place belt: " + plan.error());
            return;
        }

        CEWorld world = new BukkitWorld(clicked.getWorld()).storageWorld();
        if (world == null) {
            player.sendMessage("§cCannot place belt: world not loaded.");
            return;
        }
        org.bukkit.World bukkitWorld = clicked.getWorld();

        // Every position except A itself must be air/replaceable (A holds the start
        // segment too, but typically the clicked face block; we require all to be free).
        for (ConveyorPath.Step s : plan.steps()) {
            Block block = bukkitWorld.getBlockAt(s.x, s.y, s.z);
            if (!ConveyorBlockEntity.isReplaceable(block)) {
                player.sendMessage("§cCannot place belt: blocks are in the way at "
                        + s.x + "," + s.y + "," + s.z + ".");
                return;
            }
        }

        // Require a vapor motor adjacent to A, facing into A.
        if (!hasMotorFacingInto(world, bukkitWorld, a)) {
            player.sendMessage("§cCannot place belt: no vapor motor facing point A.");
            return;
        }

        BlockDefinition def = CraftEngineBlocks.byId(ConveyorBehavior.POLYFILL_CONVEYOR);
        if (def == null) {
            player.sendMessage("§cCannot place belt: conveyor block not registered.");
            return;
        }

        Direction facing = travelDirection(plan.steps().get(0).stepX, plan.steps().get(0).stepZ);

        BlockPos prev = null;
        int placedCount = 0;
        for (ConveyorPath.Step s : plan.steps()) {
            ImmutableBlockState newState = stateWith(def.defaultState(), facing, s.slope, s.part);
            Location loc = new Location(bukkitWorld, s.x, s.y, s.z);
            boolean placed = CraftEngineBlocks.place(loc, newState, UpdateFlags.UPDATE_ALL, false);
            if (!placed)
                continue;
            placedCount++;
            BlockPos here = new BlockPos(s.x, s.y, s.z);
            ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(world, here);
            if (seg != null && prev != null)
                seg.setPrevPos(prev);
            prev = here;
        }
        player.sendMessage("§aPlaced conveyor belt: " + placedCount + " segments.");
    }

    /** True when a vapor motor sits adjacent to A and points (facing) at A. */
    private boolean hasMotorFacingInto(CEWorld world, org.bukkit.World bukkitWorld, BlockPos a) {
        Object nmsLevel;
        try {
            nmsLevel = new BukkitWorld(bukkitWorld).minecraftWorld();
        } catch (Throwable t) {
            nmsLevel = null;
        }
        for (Direction d : Direction.values()) {
            BlockPos neighbor = a.relative(d);
            BlockEntity be = world.getBlockEntityAtIfLoaded(neighbor);
            if (be == null
                    || !(be.controller instanceof dev.arubik.craftengine.rotation.VaporMotorBlockEntity motor))
                continue;
            net.minecraft.core.Direction mf = null;
            try {
                mf = motor.facing((net.minecraft.world.level.Level) nmsLevel);
            } catch (Throwable ignored) {
            }
            if (mf == null)
                continue;
            // Motor delivers RPM to motorPos.relative(facing). It faces A when that
            // equals A: neighborPos + motorFacing == A.
            if (neighbor.x() + mf.getStepX() == a.x()
                    && neighbor.y() + mf.getStepY() == a.y()
                    && neighbor.z() + mf.getStepZ() == a.z())
                return true;
        }
        return false;
    }

    private static Direction travelDirection(int stepX, int stepZ) {
        if (stepX > 0)
            return Direction.EAST;
        if (stepX < 0)
            return Direction.WEST;
        if (stepZ > 0)
            return Direction.SOUTH;
        return Direction.NORTH;
    }

    private static ImmutableBlockState stateWith(ImmutableBlockState base, Direction facing,
            ConveyorSlope slope, ConveyorPart part) {
        ImmutableBlockState s = base;
        s = withEnum(s, ConveyorBlockEntity.PROP_FACING, facing.name());
        s = withEnum(s, ConveyorBlockEntity.PROP_SLOPE, slope.name());
        s = withEnum(s, ConveyorBlockEntity.PROP_PART, part.name());
        return s;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null)
            return state;
        try {
            Object value = p.valueByName(valueName.toLowerCase());
            if (value == null)
                value = p.valueByName(valueName);
            if (value == null)
                return state;
            return ImmutableBlockState.with(state, p, value);
        } catch (Throwable t) {
            return state;
        }
    }
}
