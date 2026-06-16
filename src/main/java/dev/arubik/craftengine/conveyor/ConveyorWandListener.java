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
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
            return;
        // Only the main hand — PlayerInteractEvent fires once per hand.
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND)
            return;
        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        // The wand is "any custom item whose block carries a ConveyorBehavior" — we
        // must NOT compare against the behavior key (polyfills:conveyor); the real
        // item/block id comes from config (e.g. demo:conveyor).
        Key handId = CraftEngineItems.getCustomItemId(hand);
        if (handId == null)
            return;
        BlockDefinition conveyorDef = CraftEngineBlocks.byId(handId);
        if (!isConveyorBlock(conveyorDef))
            return;

        // RIGHT-click: the conveyor item never drops loose blocks — always cancel the
        // vanilla placement. If the clicked block IS a belt end, extend the line by one
        // segment (END grows forward, START grows backward).
        if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            ImmutableBlockState clickedState = CraftEngineBlocks.getCustomBlockState(clicked);
            if (!isConveyorState(clickedState))
                return;
            CEWorld w = new BukkitWorld(clicked.getWorld()).storageWorld();
            if (w == null)
                return;
            BlockPos cp = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
            ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(w, cp);
            if (seg == null)
                return;
            boolean grew;
            if (seg.part() == ConveyorPart.START)
                grew = seg.extendStart(w, cp, seg.facing(), seg.slope());
            else
                grew = seg.extend(w, cp, seg.facing(), seg.slope());
            if (grew) {
                org.bukkit.entity.Player pl = event.getPlayer();
                if (pl.getGameMode() != org.bukkit.GameMode.CREATIVE) {
                    ItemStack h = pl.getInventory().getItemInMainHand();
                    h.setAmount(h.getAmount() - 1);
                }
            } else {
                event.getPlayer().sendMessage("§cCannot extend belt here (blocked or max length).");
            }
            return;
        }

        // It's our wand item: take over the left-click entirely.
        event.setCancelled(true);

        org.bukkit.entity.Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        // Place against the clicked FACE (the adjacent cell), like normal block
        // placement — otherwise the belt is placed inside the block that was hit.
        org.bukkit.block.BlockFace face = event.getBlockFace();
        Block target = face != null ? clicked.getRelative(face) : clicked;
        BlockPos clickedPos = new BlockPos(target.getX(), target.getY(), target.getZ());

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

        // Belt placement no longer requires a vapor motor — the motor can be added
        // later and the belt starts driving once one feeds RPM into the line.

        BlockDefinition def = conveyorDef;
        if (def == null) {
            player.sendMessage("§cCannot place belt: conveyor block not registered.");
            return;
        }

        Direction facing = travelDirection(plan.steps().get(0).stepX, plan.steps().get(0).stepZ);

        // In survival, the belt costs one item per segment — cap the run at how many
        // belts the player is holding (and consume exactly that many).
        boolean creative = player.getGameMode() == org.bukkit.GameMode.CREATIVE;
        int budget = creative ? Integer.MAX_VALUE : hand.getAmount();
        if (budget <= 0) {
            player.sendMessage("§cCannot place belt: out of conveyor items.");
            return;
        }

        BlockPos prev = null;
        int placedCount = 0;
        for (ConveyorPath.Step s : plan.steps()) {
            if (placedCount >= budget)
                break;
            ImmutableBlockState newState = stateWith(def.defaultState(), facing, s.slope, s.part);
            Location loc = new Location(bukkitWorld, s.x, s.y, s.z);
            boolean placed = CraftEngineBlocks.place(loc, newState, UpdateFlags.UPDATE_ALL, false);
            if (!placed)
                continue;
            placedCount++;
            // Wipe any stale belt data (items/prevPos from a previously broken belt at
            // this position) so the new segment starts clean.
            try {
                dev.arubik.craftengine.util.CustomBlockData.from(bukkitWorld.getBlockAt(s.x, s.y, s.z)).clear();
            } catch (Throwable ignored) {
            }
            BlockPos here = new BlockPos(s.x, s.y, s.z);
            ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(world, here);
            if (seg != null && prev != null)
                seg.setPrevPos(prev);
            prev = here;
        }
        if (!creative && placedCount > 0)
            hand.setAmount(hand.getAmount() - placedCount);
        player.sendMessage("§aPlaced conveyor belt: " + placedCount + " segments.");
    }

    /** True when {@code def}'s default state carries a {@link ConveyorBehavior} (id-agnostic). */
    private static boolean isConveyorBlock(BlockDefinition def) {
        if (def == null)
            return false;
        return isConveyorState(def.defaultState());
    }

    /** True when {@code state}'s behavior is (or wraps) a {@link ConveyorBehavior}. */
    private static boolean isConveyorState(ImmutableBlockState state) {
        if (state == null)
            return false;
        Object b = state.behavior();
        if (b instanceof ConveyorBehavior)
            return true;
        // CE wraps behaviors in DualBlockBehavior / CompositeBlockBehavior; both expose
        // getFirst(Class) but only on the concrete type (not the shared interface).
        if (b instanceof net.momirealms.craftengine.bukkit.block.behavior.DualBlockBehavior dual)
            return dual.getFirst(ConveyorBehavior.class) != null;
        if (b instanceof net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior comp)
            return comp.getFirst(ConveyorBehavior.class) != null;
        return false;
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
                    || !(be.controller instanceof dev.arubik.craftengine.rotation.GasMotorMk1BlockEntity motor))
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
