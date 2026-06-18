package dev.arubik.craftengine.conveyor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;

/**
 * Conveyor placement WAND with two modes and a live glowing preview.
 *
 * <p><b>MAGIC mode</b> (default): LEFT-click point A then point B to place a
 * DYNAMIC route between the two points via {@link ConveyorRoute#plan} — the planner
 * auto-inserts an L-shaped CORNER on a direction change and SLOPES on a height
 * change (same pathfinder as POINTED, just 2 points). While point A is set, a
 * glowing preview of the A→aimed-cell route (corner + slopes included) is shown
 * live. RIGHT-click on a belt END/START still extends the line by one segment.</p>
 *
 * <p><b>POINTED mode</b>: LEFT-click 3+ waypoints; the route pathfinds through every
 * waypoint in order ({@link ConveyorRoute#plan}), auto-inserting corners on direction
 * changes and slopes on height changes. The whole multi-segment route previews live
 * as waypoints accumulate. RIGHT-click commits and places all belts.</p>
 *
 * <p>Mode toggle: SNEAK + LEFT-click with an empty selection. Clear selection:
 * SNEAK + RIGHT-click. RIGHT-click on a belt END/START (no sneak) still extends the
 * line by one segment (unchanged).</p>
 *
 * <p>Registered (no-arg ctor) by {@code CraftEnginePolyfills}.</p>
 */
public class ConveyorWandListener implements Listener {

    /** Wand mode, persisted per-player. */
    public enum Mode {
        MAGIC, POINTED
    }

    /** Per-player chosen mode (default MAGIC). */
    private final Map<UUID, Mode> modes = new HashMap<>();

    /** MAGIC mode: the pending point A (awaiting B). */
    private final Map<UUID, BlockPos> magicA = new HashMap<>();

    /** POINTED mode: the ordered waypoints accumulated so far. */
    private final Map<UUID, List<BlockPos>> pointed = new HashMap<>();

    /** Live preview displays currently shown to each player (despawned on change). */
    private final Map<UUID, List<ConveyorPreviewDisplay>> previews = new HashMap<>();

    /** The aimed cell last previewed per player (so we only refresh on change). */
    private final Map<UUID, BlockPos> lastAim = new HashMap<>();

    public ConveyorWandListener() {
    }

    /**
     * Start the live aim-tracking task: every few ticks, for each player holding the
     * wand with a pending MAGIC point A, raytrace where they look and refresh the
     * glowing preview of the A→aimed run. Called once at registration.
     */
    public void start(org.bukkit.plugin.Plugin plugin) {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::tickAim, 5L, 4L);
    }

    private void tickAim() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            BlockPos a = magicA.get(id);
            if (a == null) {
                lastAim.remove(id);
                continue;
            }
            // Only while still holding the wand.
            ItemStack hand = player.getInventory().getItemInMainHand();
            Key handId = CraftEngineItems.getCustomItemId(hand);
            if (handId == null || !isConveyorBlock(CraftEngineBlocks.byId(handId))) {
                continue; // selection cleared elsewhere when the slot changes
            }
            org.bukkit.block.Block looked = player.getTargetBlockExact(48);
            if (looked == null)
                continue;
            // Aim at the face cell, like a click would.
            org.bukkit.block.BlockFace face = player.getTargetBlockFace(48);
            org.bukkit.block.Block target = face != null ? looked.getRelative(face) : looked;
            BlockPos aimed = new BlockPos(target.getX(), target.getY(), target.getZ());
            BlockPos prevAim = lastAim.get(id);
            if (prevAim != null && prevAim.x() == aimed.x() && prevAim.y() == aimed.y()
                    && prevAim.z() == aimed.z())
                continue; // unchanged: no churn
            lastAim.put(id, aimed);
            // MAGIC is now fully DYNAMIC: the same 2-point pathfinder as POINTED, so the
            // live preview shows the auto-inserted corner (L-shape) and slopes, not just a
            // straight single-axis run.
            ConveyorRoute.Result plan = planTwoPoint(player.getWorld(), a, aimed);
            if (plan.isValid())
                showPreview(player, player.getWorld(), plan.steps());
            else
                showPreview(player, player.getWorld(), List.of(new ConveyorPath.Step(
                        a.x(), a.y(), a.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
        }
    }

    // ------------------------------------------------------------------ events

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND)
            return;
        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        org.bukkit.entity.Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == org.bukkit.Material.AIR)
            return;
        Key handId = CraftEngineItems.getCustomItemId(hand);
        if (handId == null)
            return;
        BlockDefinition conveyorDef = CraftEngineBlocks.byId(handId);
        if (!isConveyorBlock(conveyorDef))
            return;

        // The wand always takes over the click (never drops a loose block).
        event.setCancelled(true);

        UUID id = player.getUniqueId();
        Mode mode = modes.getOrDefault(id, Mode.MAGIC);
        boolean sneaking = player.isSneaking();

        // Place against the clicked FACE (adjacent cell), like normal placement.
        org.bukkit.block.BlockFace face = event.getBlockFace();
        Block target = face != null ? clicked.getRelative(face) : clicked;
        BlockPos aimed = new BlockPos(target.getX(), target.getY(), target.getZ());

        if (action == Action.RIGHT_CLICK_BLOCK) {
            // SNEAK + RIGHT: clear the current selection + preview.
            if (sneaking) {
                clearSelection(player);
                msg(player, NamedTextColor.GRAY, "polyfill.wand.cleared");
                return;
            }
            // POINTED: RIGHT commits the route.
            if (mode == Mode.POINTED) {
                commitPointed(player, clicked.getWorld(), hand, conveyorDef);
                return;
            }
            // MAGIC: RIGHT on a belt end extends it by one (original behaviour).
            extendBelt(event, clicked, hand);
            return;
        }

        // LEFT-click.
        boolean emptySelection = (mode == Mode.MAGIC && magicA.get(id) == null)
                || (mode == Mode.POINTED && pointed.getOrDefault(id, List.of()).isEmpty());

        // SNEAK + LEFT with nothing selected: toggle mode.
        if (sneaking && emptySelection) {
            Mode next = (mode == Mode.MAGIC) ? Mode.POINTED : Mode.MAGIC;
            modes.put(id, next);
            clearSelection(player);
            if (next == Mode.POINTED)
                msg(player, NamedTextColor.AQUA, "polyfill.wand.mode_pointed");
            else
                msg(player, NamedTextColor.AQUA, "polyfill.wand.mode_magic");
            return;
        }

        if (mode == Mode.MAGIC)
            leftClickMagic(player, clicked.getWorld(), hand, conveyorDef, aimed);
        else
            leftClickPointed(player, clicked.getWorld(), aimed);
    }

    /** While aiming, update the MAGIC preview to A→aimed (no placement). */
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        // Switching the held slot away from the wand clears the preview.
        org.bukkit.entity.Player player = event.getPlayer();
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        Key id = next != null ? CraftEngineItems.getCustomItemId(next) : null;
        if (id == null || !isConveyorBlock(CraftEngineBlocks.byId(id)))
            clearSelection(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearSelection(event.getPlayer());
        UUID id = event.getPlayer().getUniqueId();
        magicA.remove(id);
        pointed.remove(id);
        previews.remove(id);
    }

    // ------------------------------------------------------------------ MAGIC

    private void leftClickMagic(org.bukkit.entity.Player player, org.bukkit.World world, ItemStack hand,
            BlockDefinition def, BlockPos aimed) {
        UUID id = player.getUniqueId();
        BlockPos a = magicA.get(id);
        if (a == null) {
            magicA.put(id, aimed);
            // Show a single-cell preview at A so the player sees the anchor.
            showPreview(player, world, List.of(new ConveyorPath.Step(
                    aimed.x(), aimed.y(), aimed.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
            msg(player, NamedTextColor.GREEN, "polyfill.wand.point_a");
            return;
        }
        magicA.remove(id);
        // MAGIC now plans a DYNAMIC route (auto corner + slopes) through the same
        // ConveyorRoute pathfinder POINTED uses, not a straight-only ConveyorPath.
        ConveyorRoute.Result plan = planTwoPoint(world, a, aimed);
        if (!plan.isValid()) {
            clearPreview(player);
            msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        placeRoute(player, world, hand, def, plan.steps());
    }

    /**
     * MAGIC 2-point plan: route from A to the aimed cell via {@link ConveyorRoute}
     * (same pathfinder as POINTED), so corners and slopes are auto-inserted. Router
     * snapping is applied just like {@link #planPointed} so a belt anchored on a
     * splitter/merger snaps to the adjacent cell.
     */
    private ConveyorRoute.Result planTwoPoint(org.bukkit.World world, BlockPos a, BlockPos b) {
        List<BlockPos> pts = new ArrayList<>(2);
        pts.add(a);
        pts.add(b);
        return planPointed(world, pts);
    }

    // ----------------------------------------------------------------- POINTED

    private void leftClickPointed(org.bukkit.entity.Player player, org.bukkit.World world, BlockPos aimed) {
        UUID id = player.getUniqueId();
        List<BlockPos> pts = pointed.computeIfAbsent(id, k -> new ArrayList<>());
        if (!pts.isEmpty()) {
            BlockPos last = pts.get(pts.size() - 1);
            if (last.x() == aimed.x() && last.y() == aimed.y() && last.z() == aimed.z()) {
                msg(player, NamedTextColor.GRAY, "polyfill.wand.waypoint_dup");
                return;
            }
        }
        pts.add(aimed);
        msg(player, NamedTextColor.GREEN, "polyfill.wand.waypoint_added",
                pts.size(), aimed.x(), aimed.y(), aimed.z());
        // Re-plan + preview the whole route so far.
        if (pts.size() >= 2)
            previewPointed(player, world, pts);
        else
            showPreview(player, world, List.of(new ConveyorPath.Step(
                    aimed.x(), aimed.y(), aimed.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
    }

    private void previewPointed(org.bukkit.entity.Player player, org.bukkit.World world, List<BlockPos> pts) {
        ConveyorRoute.Result plan = planPointed(world, pts);
        if (!plan.isValid()) {
            clearPreview(player);
            msg(player, NamedTextColor.YELLOW, "polyfill.wand.route_pending");
            msg(player, NamedTextColor.YELLOW, plan.errorKey(), plan.errorArgs());
            return;
        }
        showPreview(player, world, plan.steps());
    }

    private void commitPointed(org.bukkit.entity.Player player, org.bukkit.World world, ItemStack hand,
            BlockDefinition def) {
        UUID id = player.getUniqueId();
        List<BlockPos> pts = pointed.get(id);
        if (pts == null || pts.size() < 2) {
            msg(player, NamedTextColor.RED, "polyfill.wand.need_waypoints");
            return;
        }
        ConveyorRoute.Result plan = planPointed(world, pts);
        if (!plan.isValid()) {
            msg(player, NamedTextColor.RED, "polyfill.wand.route_failed");
            msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        pointed.remove(id);
        placeRoute(player, world, hand, def, plan.steps());
    }

    /** Plan a pointed route, treating already-existing belts/routers along the path as blocked. */
    private ConveyorRoute.Result planPointed(org.bukkit.World world, List<BlockPos> pts) {
        // Splitter/merger integration: if the first/last waypoint sits ON a router block, snap
        // it to the adjacent belt cell so the belt ends/starts right next to the router. The
        // belt's runtime hand-off (ConveyorBlockEntity.exitCandidates / ConveyorRouting.receiverAt,
        // which both check ±1 Y) then bridges a flat OR a single up/down slope into/out of the
        // router automatically — no special corner block needed.
        List<BlockPos> route = new ArrayList<>(pts);
        if (route.size() >= 2) {
            BlockPos snappedFirst = snapRouterEndpoint(world, route.get(0), route.get(1));
            if (snappedFirst != null)
                route.set(0, snappedFirst);
            int n = route.size();
            BlockPos snappedLast = snapRouterEndpoint(world, route.get(n - 1), route.get(n - 2));
            if (snappedLast != null)
                route.set(n - 1, snappedLast);
        }
        List<ConveyorRoute.Waypoint> wps = new ArrayList<>(route.size());
        for (BlockPos p : route)
            wps.add(new ConveyorRoute.Waypoint(p.x(), p.y(), p.z()));
        return ConveyorRoute.plan(wps, (x, y, z) -> cellFree(world, x, y, z));
    }

    /**
     * If {@code endpoint} is a splitter/merger router, return the cell one step toward
     * {@code toward} on the horizontal plane (so the belt sits adjacent to the router and
     * hands off at runtime). Returns null when it isn't a router.
     */
    private BlockPos snapRouterEndpoint(org.bukkit.World world, BlockPos endpoint, BlockPos toward) {
        if (!isRouter(world, endpoint))
            return null;
        int dx = Integer.signum(toward.x() - endpoint.x());
        int dz = Integer.signum(toward.z() - endpoint.z());
        if (dx != 0 && dz != 0) {
            // Diagonal: pick the dominant axis so the snapped cell stays orthogonal.
            if (Math.abs(toward.x() - endpoint.x()) >= Math.abs(toward.z() - endpoint.z()))
                dz = 0;
            else
                dx = 0;
        }
        if (dx == 0 && dz == 0)
            return null;
        return new BlockPos(endpoint.x() + dx, endpoint.y(), endpoint.z() + dz);
    }

    /** True when the block at {@code pos} is a conveyor splitter/merger router. */
    private boolean isRouter(org.bukkit.World world, BlockPos pos) {
        try {
            CEWorld w = new BukkitWorld(world).storageWorld();
            if (w == null)
                return false;
            var be = w.getBlockEntityAtIfLoaded(pos);
            return be != null && be.controller instanceof AbstractRouterBlockEntity;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * A cell is usable for a belt when the block is air/replaceable or a top slab.
     * Existing belts/routers/solid blocks are NOT free (the route must avoid them).
     */
    private boolean cellFree(org.bukkit.World world, int x, int y, int z) {
        Block b = world.getBlockAt(x, y, z);
        if (ConveyorBlockEntity.isReplaceable(b))
            return true;
        return isTopSlab(b);
    }

    private static boolean isTopSlab(Block b) {
        try {
            if (b.getBlockData() instanceof org.bukkit.block.data.type.Slab slab)
                return slab.getType() == org.bukkit.block.data.type.Slab.Type.TOP;
        } catch (Throwable ignored) {
        }
        return false;
    }

    // ------------------------------------------------------------- placement

    /** Place the planned step list (shared by both modes), linking prevPos end-to-end. */
    private void placeRoute(org.bukkit.entity.Player player, org.bukkit.World bukkitWorld, ItemStack hand,
            BlockDefinition def, List<ConveyorPath.Step> steps) {
        clearPreview(player);
        CEWorld world = new BukkitWorld(bukkitWorld).storageWorld();
        if (world == null) {
            msg(player, NamedTextColor.RED, "polyfill.wand.no_world");
            return;
        }
        // Validate every cell up front so we never place a partial broken line.
        for (ConveyorPath.Step s : steps) {
            Block block = bukkitWorld.getBlockAt(s.x, s.y, s.z);
            if (!ConveyorBlockEntity.isReplaceable(block) && !isTopSlab(block)) {
                msg(player, NamedTextColor.RED, "polyfill.wand.place_blocked", s.x, s.y, s.z);
                return;
            }
        }

        boolean creative = player.getGameMode() == org.bukkit.GameMode.CREATIVE;
        int budget = creative ? Integer.MAX_VALUE : hand.getAmount();
        if (budget <= 0) {
            msg(player, NamedTextColor.RED, "polyfill.wand.out_of_items");
            return;
        }

        BlockPos prev = null;
        int placedCount = 0;
        for (ConveyorPath.Step s : steps) {
            if (placedCount >= budget) {
                msg(player, NamedTextColor.YELLOW, "polyfill.wand.not_enough", placedCount, steps.size());
                break;
            }
            Direction facing = travelDirection(s.stepX, s.stepZ);
            ImmutableBlockState newState = stateWith(def.defaultState(), facing, s.slope, s.part);
            Location loc = new Location(bukkitWorld, s.x, s.y, s.z);
            boolean placed = CraftEngineBlocks.place(loc, newState, UpdateFlags.UPDATE_ALL, false);
            if (!placed)
                continue;
            placedCount++;
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
        msg(player, NamedTextColor.GREEN, "polyfill.wand.placed", placedCount);
    }

    /** RIGHT-click extend of an existing belt end/start (unchanged from the original wand). */
    private void extendBelt(PlayerInteractEvent event, Block clicked, ItemStack hand) {
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
            msg(event.getPlayer(), NamedTextColor.RED, "polyfill.wand.extend_failed");
        }
    }

    // ------------------------------------------------------------- preview

    /** Replace the player's preview with displays for the given planned steps. */
    private void showPreview(org.bukkit.entity.Player player, org.bukkit.World bukkitWorld,
            List<ConveyorPath.Step> steps) {
        clearPreview(player);
        CEWorld world = new BukkitWorld(bukkitWorld).storageWorld();
        if (world == null)
            return;
        List<Player> viewers = new ArrayList<>();
        Player cePlayer = cePlayer(player);
        if (cePlayer != null)
            viewers.add(cePlayer);
        if (viewers.isEmpty())
            return;

        List<ConveyorPreviewDisplay> list = new ArrayList<>(steps.size());
        for (ConveyorPath.Step s : steps) {
            ConveyorPreviewDisplay d = new ConveyorPreviewDisplay();
            org.bukkit.inventory.ItemStack model = previewItem(s.slope);
            if (model == null)
                continue;
            d.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(model));
            Direction facing = travelDirection(s.stepX, s.stepZ);
            d.setRotation(previewRotation(facing, s.slope));
            d.setScale(1f, 1f, 1f);
            // Centre the block model on the cell.
            d.render(viewers, s.x + 0.5, s.y + 0.5, s.z + 0.5);
            list.add(d);
        }
        previews.put(player.getUniqueId(), list);
    }

    /** Yaw the preview model to match the belt facing (item-display LeftRotation). */
    private static org.joml.Quaternionf previewRotation(Direction facing, ConveyorSlope slope) {
        float yaw;
        switch (facing) {
            case SOUTH: yaw = 0f; break;
            case WEST: yaw = (float) Math.toRadians(90); break;
            case NORTH: yaw = (float) Math.toRadians(180); break;
            case EAST: yaw = (float) Math.toRadians(270); break;
            default: yaw = 0f;
        }
        // The slope preview models face the wrong way along the E/W axis: flip 180° for
        // up/down slopes when the belt runs west or east.
        if (slope != ConveyorSlope.FLAT && (facing == Direction.WEST || facing == Direction.EAST))
            yaw += (float) Math.PI;
        return new org.joml.Quaternionf().rotateY(yaw);
    }

    /** The render-only preview item for a slope geometry. */
    private static org.bukkit.inventory.ItemStack previewItem(ConveyorSlope slope) {
        String idName = slope == ConveyorSlope.UP ? "cv_preview_up"
                : slope == ConveyorSlope.DOWN ? "cv_preview_down" : "cv_preview_flat";
        try {
            var d = CraftEngineItems.byId(Key.of("cml", idName));
            if (d != null)
                return d.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Despawn + forget the player's preview displays. */
    private void clearPreview(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        List<ConveyorPreviewDisplay> list = previews.remove(id);
        if (list == null || list.isEmpty())
            return;
        Player cePlayer = cePlayer(player);
        List<Player> viewers = cePlayer != null ? List.of(cePlayer) : List.of();
        for (ConveyorPreviewDisplay d : list)
            d.despawnAll(viewers);
    }

    /** Clear both the pending selection and the preview for a player. */
    private void clearSelection(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        magicA.remove(id);
        pointed.remove(id);
        lastAim.remove(id);
        clearPreview(player);
    }

    /** Resolve the CraftEngine {@link Player} for a Bukkit player (for direct packet sends). */
    private static Player cePlayer(org.bukkit.entity.Player player) {
        try {
            UUID want = player.getUniqueId();
            for (Player p : net.momirealms.craftengine.core.plugin.CraftEngine.instance()
                    .networkManager().onlineUsers()) {
                Object pp = p.platformPlayer();
                if (pp instanceof org.bukkit.entity.Player b && b.getUniqueId().equals(want))
                    return p;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    // ------------------------------------------------------------- i18n chat

    /**
     * Send a client-translatable wand message. The {@code key} resolves against the
     * resource-pack lang ({@code polyfill.wand.*}); integer {@code args} fill the
     * {@code %s} placeholders so coords/counts/indices localise too. The whole line
     * is tinted {@code color} (keeping the original §a/§c/§e/§7 intent).
     */
    private static void msg(org.bukkit.entity.Player player, NamedTextColor color, String key, int... args) {
        Component c = Component.translatable(key, intArgs(args)).color(color);
        player.sendMessage(c);
    }

    /** Build the translatable args list from integers (each becomes a literal Component). */
    private static List<Component> intArgs(int[] args) {
        List<Component> list = new ArrayList<>(args.length);
        for (int v : args)
            list.add(Component.text(v));
        return list;
    }

    // ------------------------------------------------------------- helpers

    private static boolean isConveyorBlock(BlockDefinition def) {
        if (def == null)
            return false;
        return isConveyorState(def.defaultState());
    }

    private static boolean isConveyorState(ImmutableBlockState state) {
        if (state == null)
            return false;
        Object b = state.behavior();
        if (b instanceof ConveyorBehavior)
            return true;
        if (b instanceof net.momirealms.craftengine.bukkit.block.behavior.DualBlockBehavior dual)
            return dual.getFirst(ConveyorBehavior.class) != null;
        if (b instanceof net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior comp)
            return comp.getFirst(ConveyorBehavior.class) != null;
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
