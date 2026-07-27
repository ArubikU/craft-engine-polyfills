package dev.arubik.craftengine.pipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.fluid.behavior.PipeBehavior;
import dev.arubik.craftengine.gas.behavior.GasPipeBehavior;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

/**
 * Placement WAND for the copper & iron(steel) pipes, mirroring the conveyor belt wand but in its
 * own files. The pipe BLOCK ITEM is itself the wand: holding {@code cml:copper_pipe}
 * ({@link PipeBehavior}, fluid) or {@code cml:iron_pipe} ({@link GasPipeBehavior}, gas) activates it.
 *
 * <p>Pipes are simpler to place than belts — they auto-connect in all 6 directions via
 * {@link ConnectedBlockBehavior} (64 variants resolve themselves), so a route is just an ordered
 * list of cells; routing is full 3D. Four modes (SNEAK+LEFT cycles, SNEAK+RIGHT clears):</p>
 * <ul>
 *   <li><b>NORMAL</b> — right-click a face → place ONE pipe at the adjacent cell (consume 1).</li>
 *   <li><b>STRAIGHT</b> — left-click A, B → an axis-aligned line (validates alignment).</li>
 *   <li><b>POINTED</b> — left-click waypoints, right-click builds the route through them.</li>
 *   <li><b>MAGIC</b> — left-click A, then aim/click B → auto-route A→B in 3D through air.</li>
 * </ul>
 *
 * <p>The live glowing preview ({@link PipePreviewDisplay}) shows each route cell as a translucent
 * pipe whose connection variant is computed with the SAME rule the real placed block uses, so the
 * preview matches the final result exactly — including connections to surrounding existing blocks
 * (other pipes, tanks, machines) via {@link ConnectedBlockBehavior#shouldConnect}.</p>
 */
public class PipeWandListener implements Listener {

    /** Wand mode, persisted per-player. */
    public enum Mode {
        NORMAL, STRAIGHT, POINTED, MAGIC
    }

    // The pipe kinds this wand can place used to be a private enum pairing a hardcoded
    // block id with a hardcoded preview-item prefix. Both now come from the data-driven
    // PipeType registry, so a pack can add a tier without touching this class.

    private final Map<UUID, Mode> modes = new HashMap<>();
    /** STRAIGHT/MAGIC: pending point A awaiting B. */
    private final Map<UUID, int[]> pointA = new HashMap<>();
    /** POINTED: accumulated ordered waypoints. */
    private final Map<UUID, List<int[]>> pointed = new HashMap<>();
    /** Live preview displays shown to each player. */
    private final Map<UUID, List<PipePreviewDisplay>> previews = new HashMap<>();
    /** The aimed cell last previewed per player (refresh only on change). */
    private final Map<UUID, int[]> lastAim = new HashMap<>();

    public PipeWandListener() {
    }

    /** Start the live aim-tracking task for the MAGIC preview (mirrors the conveyor wand). */
    public void start(org.bukkit.plugin.Plugin plugin) {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::tickAim, 5L, 4L);
    }

    private void tickAim() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            int[] a = pointA.get(id);
            if (a == null) {
                lastAim.remove(id);
                continue;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            PipeType kind = pipeKind(hand);
            if (kind == null)
                continue;
            if (modes.getOrDefault(id, Mode.NORMAL) != Mode.MAGIC)
                continue;
            Block looked = player.getTargetBlockExact(48);
            if (looked == null)
                continue;
            org.bukkit.block.BlockFace face = player.getTargetBlockFace(48);
            Block target = face != null ? looked.getRelative(face) : looked;
            int[] aimed = { target.getX(), target.getY(), target.getZ() };
            int[] prev = lastAim.get(id);
            if (prev != null && prev[0] == aimed[0] && prev[1] == aimed[1] && prev[2] == aimed[2])
                continue;
            lastAim.put(id, aimed);
            PipeRoute.Result plan = PipeRoute.magic(wp(a), wp(aimed), validator(player.getWorld(), kind));
            if (plan.isValid())
                showPreview(player, player.getWorld(), kind, plan.cells());
            else
                showPreview(player, player.getWorld(), kind,
                        List.of(new PipeRoute.Cell(a[0], a[1], a[2])));
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
        PipeType kind = pipeKind(hand);
        if (kind == null)
            return;

        UUID id = player.getUniqueId();
        Mode mode = modes.getOrDefault(id, Mode.NORMAL);
        boolean sneaking = player.isSneaking();

        org.bukkit.block.BlockFace face = event.getBlockFace();
        Block target = face != null ? clicked.getRelative(face) : clicked;
        int[] aimed = { target.getX(), target.getY(), target.getZ() };

        // sneak+left = cycle mode (any mode, including NORMAL).
        // sneak+right = clear selection IN A WAND MODE; in NORMAL it must fall through to vanilla
        // sneak-placement (placing on a container without opening it) — no intercept, no "cleared" spam.
        if (sneaking) {
            if (action == Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);
                Mode next = cycle(mode);
                modes.put(id, next);
                clearSelection(player);
                announceMode(player, next);
                return;
            }
            // sneak + right
            if (mode == Mode.NORMAL)
                return; // vanilla sneak-place
            event.setCancelled(true);
            clearSelection(player);
            msg(player, NamedTextColor.GRAY, "polyfill.pipewand.cleared");
            return;
        }

        // NORMAL mode = vanilla placement: do NOT intercept the click at all (no cancel), so the pipe
        // places exactly like a normal block. The wand only takes over in STRAIGHT/POINTED/MAGIC.
        if (mode == Mode.NORMAL)
            return;

        event.setCancelled(true);

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (mode == Mode.POINTED)
                commitPointed(player, clicked.getWorld(), kind, hand);
            return; // STRAIGHT/MAGIC: build on left-click
        }

        // LEFT-click.
        switch (mode) {
            case STRAIGHT -> leftClickTwoPoint(player, clicked.getWorld(), kind, hand, aimed, false);
            case MAGIC -> leftClickTwoPoint(player, clicked.getWorld(), kind, hand, aimed, true);
            case POINTED -> leftClickPointed(player, clicked.getWorld(), kind, aimed);
            default -> { /* NORMAL handled above */ }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        if (pipeKind(next) == null)
            clearSelection(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearSelection(event.getPlayer());
        UUID id = event.getPlayer().getUniqueId();
        pointA.remove(id);
        pointed.remove(id);
        previews.remove(id);
    }

    private static Mode cycle(Mode m) {
        return switch (m) {
            case NORMAL -> Mode.STRAIGHT;
            case STRAIGHT -> Mode.POINTED;
            case POINTED -> Mode.MAGIC;
            case MAGIC -> Mode.NORMAL;
        };
    }

    private void announceMode(org.bukkit.entity.Player player, Mode m) {
        String key = switch (m) {
            case NORMAL -> "polyfill.pipewand.mode_normal";
            case STRAIGHT -> "polyfill.pipewand.mode_straight";
            case POINTED -> "polyfill.pipewand.mode_pointed";
            case MAGIC -> "polyfill.pipewand.mode_magic";
        };
        msg(player, NamedTextColor.AQUA, key);
    }

    // ------------------------------------------------------------- STRAIGHT / MAGIC

    private void leftClickTwoPoint(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            ItemStack hand, int[] aimed, boolean magic) {
        UUID id = player.getUniqueId();
        int[] a = pointA.get(id);
        if (a == null) {
            pointA.put(id, aimed);
            showPreview(player, world, kind, List.of(new PipeRoute.Cell(aimed[0], aimed[1], aimed[2])));
            msg(player, NamedTextColor.GREEN, "polyfill.pipewand.point_a");
            return;
        }
        pointA.remove(id);
        lastAim.remove(id);
        PipeRoute.Result plan = magic
                ? PipeRoute.magic(wp(a), wp(aimed), validator(world, kind))
                : PipeRoute.straight(wp(a), wp(aimed), validator(world, kind));
        if (!plan.isValid()) {
            clearPreview(player);
            msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        placeRoute(player, world, kind, hand, plan.cells());
    }

    // ----------------------------------------------------------------- POINTED

    private void leftClickPointed(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            int[] aimed) {
        UUID id = player.getUniqueId();
        List<int[]> pts = pointed.computeIfAbsent(id, k -> new ArrayList<>());
        if (!pts.isEmpty()) {
            int[] last = pts.get(pts.size() - 1);
            if (last[0] == aimed[0] && last[1] == aimed[1] && last[2] == aimed[2]) {
                msg(player, NamedTextColor.GRAY, "polyfill.pipewand.waypoint_dup");
                return;
            }
        }
        pts.add(aimed);
        msg(player, NamedTextColor.GREEN, "polyfill.pipewand.waypoint_added",
                pts.size(), aimed[0], aimed[1], aimed[2]);
        if (pts.size() >= 2) {
            PipeRoute.Result plan = PipeRoute.pointed(toWaypoints(pts), validator(world, kind));
            if (plan.isValid())
                showPreview(player, world, kind, plan.cells());
            else {
                clearPreview(player);
                msg(player, NamedTextColor.YELLOW, plan.errorKey(), plan.errorArgs());
            }
        } else {
            showPreview(player, world, kind, List.of(new PipeRoute.Cell(aimed[0], aimed[1], aimed[2])));
        }
    }

    private void commitPointed(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            ItemStack hand) {
        UUID id = player.getUniqueId();
        List<int[]> pts = pointed.get(id);
        if (pts == null || pts.size() < 2) {
            msg(player, NamedTextColor.RED, "polyfill.pipewand.need_waypoints");
            return;
        }
        PipeRoute.Result plan = PipeRoute.pointed(toWaypoints(pts), validator(world, kind));
        if (!plan.isValid()) {
            msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        pointed.remove(id);
        placeRoute(player, world, kind, hand, plan.cells());
    }

    // ------------------------------------------------------------- placement

    /** NORMAL mode: place a single pipe at the aimed cell. */
    private void placeOne(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            ItemStack hand, int[] cell) {
        placeRoute(player, world, kind, hand, List.of(new PipeRoute.Cell(cell[0], cell[1], cell[2])));
    }

    /**
     * Place the planned cells as {@code cml:copper_pipe}/{@code cml:iron_pipe} default-state blocks;
     * {@link ConnectedBlockBehavior} auto-resolves each block's connection variant on place. Consumes
     * one pipe per cell from the held stack (creative is free); out of pipes → place what we can,
     * cut, and report. Existing same-type pipe cells are skipped (already a pipe), not re-placed.
     */
    private void placeRoute(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            ItemStack hand, List<PipeRoute.Cell> cells) {
        clearPreview(player);
        BlockDefinition def = CraftEngineBlocks.byId(kind.blockId());
        if (def == null) {
            msg(player, NamedTextColor.RED, "polyfill.pipewand.no_world");
            return;
        }
        // Validate every NEW cell up front so we never place a partial broken line. Cells that already
        // hold a same-type pipe are allowed (they're skipped at placement).
        for (PipeRoute.Cell c : cells) {
            Block block = world.getBlockAt(c.x, c.y, c.z);
            if (isExistingPipe(world, kind, c.x, c.y, c.z))
                continue;
            if (!isReplaceable(block, kind)) {
                msg(player, NamedTextColor.RED, "polyfill.pipewand.place_blocked", c.x, c.y, c.z);
                return;
            }
        }

        boolean creative = player.getGameMode() == org.bukkit.GameMode.CREATIVE;
        int budget = creative ? Integer.MAX_VALUE : hand.getAmount();
        if (budget <= 0) {
            msg(player, NamedTextColor.RED, "polyfill.pipewand.out_of_items");
            return;
        }

        ImmutableBlockState state = def.defaultState();
        int placed = 0;
        int needed = 0;
        for (PipeRoute.Cell c : cells) {
            if (isExistingPipe(world, kind, c.x, c.y, c.z))
                continue; // already a pipe here — connects automatically, costs nothing
            needed++;
            if (placed >= budget) {
                msg(player, NamedTextColor.YELLOW, "polyfill.pipewand.not_enough", placed, needed);
                break;
            }
            Location loc = new Location(world, c.x, c.y, c.z);
            boolean ok = CraftEngineBlocks.place(loc, state, UpdateFlags.UPDATE_ALL, false);
            if (!ok)
                continue;
            placed++;
            try {
                net.minecraft.world.level.Level nmsLevel = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
                net.minecraft.core.BlockPos nmsPos = new net.minecraft.core.BlockPos(c.x, c.y, c.z);
                dev.arubik.craftengine.block.entity.PersistentBlockEntity.executeAt(nmsLevel, nmsPos,
                        dev.arubik.craftengine.block.entity.PersistentBlockEntity::clear);
            } catch (Throwable ignored) {
            }
        }
        if (!creative && placed > 0)
            hand.setAmount(hand.getAmount() - placed);
        msg(player, NamedTextColor.GREEN, "polyfill.pipewand.placed", placed);
    }

    // ------------------------------------------------------------- preview

    /**
     * Replace the player's preview with one glowing translucent pipe per cell, each showing the
     * connection variant computed by the SAME rule the placed block uses ({@link #maskFor}).
     */
    private void showPreview(org.bukkit.entity.Player player, org.bukkit.World world, PipeType kind,
            List<PipeRoute.Cell> cells) {
        clearPreview(player);
        Player cePlayer = cePlayer(player);
        if (cePlayer == null)
            return;
        List<Player> viewers = List.of(cePlayer);

        Set<Long> routeSet = new HashSet<>();
        for (PipeRoute.Cell c : cells)
            routeSet.add(pack(c.x, c.y, c.z));

        ConnectedBlockBehavior conn = connectedBehavior(kind);

        List<PipePreviewDisplay> list = new ArrayList<>(cells.size());
        for (PipeRoute.Cell c : cells) {
            String mask = maskFor(world, kind, conn, c, routeSet);
            ItemStack model = previewItem(kind, mask);
            if (model == null)
                continue;
            PipePreviewDisplay d = new PipePreviewDisplay();
            d.setNmsItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(model));
            float[] off = kind.previewOffset();
            float sc = kind.previewScale();
            d.setScale(sc, sc, sc);
            d.render(viewers, c.x + off[0], c.y + off[1], c.z + off[2]);
            list.add(d);
        }
        previews.put(player.getUniqueId(), list);
    }

    /**
     * Compute the 6-bit connection mask for a route cell, in the IDENTICAL char order the block
     * config uses (s,w,n,e,u,d; c=connected, n=none) so the preview variant == the placed variant.
     * A direction is "connected" if EITHER the neighbour cell is part of the planned route, OR the
     * placed pipe would connect to the existing world block there per the real connection logic
     * ({@link ConnectedBlockBehavior#shouldConnect}, evaluated against the live world).
     */
    private String maskFor(org.bukkit.World world, PipeType kind, ConnectedBlockBehavior conn,
            PipeRoute.Cell c, Set<Long> routeSet) {
        net.minecraft.world.level.Level level = conn != null ? nmsLevel(world) : null;
        net.minecraft.core.BlockPos pos = level != null ? new net.minecraft.core.BlockPos(c.x, c.y, c.z) : null;
        // Char order MUST match the block config's variant->appearance mapping. It defaults
        // to south, west, north, east, up, down, and a pipe kind whose pack spells its
        // variants differently overrides it via `mask_order` in pipe_types/*.json.
        net.minecraft.core.Direction[] order = maskOrder(kind);
        StringBuilder sb = new StringBuilder(6);
        for (net.minecraft.core.Direction dir : order) {
            boolean connected = false;
            int nx = c.x + dir.getStepX(), ny = c.y + dir.getStepY(), nz = c.z + dir.getStepZ();
            if (routeSet.contains(pack(nx, ny, nz))) {
                connected = true; // neighbour is part of the planned route
            } else if (conn != null && level != null) {
                try {
                    connected = conn.shouldConnect(dir, pos, level); // real connection rule vs world
                } catch (Throwable ignored) {
                    connected = false;
                }
            }
            sb.append(connected ? 'c' : 'n');
        }
        return sb.toString();
    }

    /**
     * The six directions a pipe kind's connection mask spells, in pack order. Falls
     * back to the standard s,w,n,e,u,d if a data file names a direction that does not
     * exist, since a wrong-length or misspelled order would silently show the wrong
     * elbow rather than fail.
     */
    private static net.minecraft.core.Direction[] maskOrder(PipeType kind) {
        java.util.List<String> names = kind.maskOrder();
        net.minecraft.core.Direction[] order = new net.minecraft.core.Direction[6];
        for (int i = 0; i < 6; i++) {
            net.minecraft.core.Direction dir = i < names.size()
                    ? net.minecraft.core.Direction.byName(names.get(i).toLowerCase(java.util.Locale.ROOT))
                    : null;
            if (dir == null)
                return DEFAULT_MASK_ORDER;
            order[i] = dir;
        }
        return order;
    }

    private static final net.minecraft.core.Direction[] DEFAULT_MASK_ORDER = {
            net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST,
            net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.EAST,
            net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN
    };

    /** The render-only preview item for a pipe kind + 6-char mask. */
    private static ItemStack previewItem(PipeType kind, String mask) {
        try {
            var d = CraftEngineItems.byId(Key.of(kind.previewNamespace(), kind.previewPrefix() + "_" + mask));
            if (d != null)
                return d.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        return null;
    }

    private void clearPreview(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        List<PipePreviewDisplay> list = previews.remove(id);
        if (list == null || list.isEmpty())
            return;
        Player cePlayer = cePlayer(player);
        List<Player> viewers = cePlayer != null ? List.of(cePlayer) : List.of();
        for (PipePreviewDisplay d : list)
            d.despawnAll(viewers);
    }

    private void clearSelection(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        pointA.remove(id);
        pointed.remove(id);
        lastAim.remove(id);
        clearPreview(player);
    }

    // ------------------------------------------------------------- helpers

    /** The route validator: a cell is buildable ONLY when air/replaceable — the route never overlaps a
     *  placed block (existing pipes/machines are connected-to at the route's edges, not tunneled through). */
    private PipeRoute.CellValidator validator(org.bukkit.World world, PipeType kind) {
        return (x, y, z) -> isReplaceable(world.getBlockAt(x, y, z), kind);
    }

    private static PipeRoute.Waypoint wp(int[] a) {
        return new PipeRoute.Waypoint(a[0], a[1], a[2]);
    }

    private static List<PipeRoute.Waypoint> toWaypoints(List<int[]> pts) {
        List<PipeRoute.Waypoint> out = new ArrayList<>(pts.size());
        for (int[] p : pts)
            out.add(new PipeRoute.Waypoint(p[0], p[1], p[2]));
        return out;
    }

    /** True when the held item is a pipe block; returns which transport kind, else null. */
    private static PipeType pipeKind(ItemStack item) {
        if (item == null)
            return null;
        Key handId = CraftEngineItems.getCustomItemId(item);
        if (handId == null)
            return null;
        // The registry is keyed by the block a pipe kind places, so a data-defined tier
        // is recognised here with no code change. The behavior-class sniff below is only
        // a fallback for a pipe block that has no PipeType entry yet.
        PipeType byBlock = PipeType.byBlockId(handId);
        if (byBlock != null)
            return byBlock;
        BlockDefinition def = CraftEngineBlocks.byId(handId);
        if (def == null)
            return null;
        Object beh = behaviorOf(def);
        if (isOrWraps(beh, GasPipeBehavior.class))
            return PipeType.STEEL;
        if (isOrWraps(beh, PipeBehavior.class))
            return PipeType.COPPER;
        return null;
    }

    /** The ConnectedBlockBehavior of a pipe kind's block (for the real connection rule). */
    private static ConnectedBlockBehavior connectedBehavior(PipeType kind) {
        BlockDefinition def = CraftEngineBlocks.byId(kind.blockId());
        if (def == null)
            return null;
        Object beh = behaviorOf(def);
        if (beh instanceof ConnectedBlockBehavior cb)
            return cb;
        if (beh instanceof net.momirealms.craftengine.core.block.behavior.BlockBehavior bb) {
            try {
                return bb.getFirst(ConnectedBlockBehavior.class);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Object behaviorOf(BlockDefinition def) {
        ImmutableBlockState s = def.defaultState();
        return s == null ? null : s.behavior();
    }

    private static boolean isOrWraps(Object beh, Class<?> type) {
        if (beh == null)
            return false;
        if (type.isInstance(beh))
            return true;
        if (beh instanceof net.momirealms.craftengine.core.block.behavior.BlockBehavior bb) {
            try {
                return bb.getFirst(type) != null;
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    private boolean isExistingPipe(org.bukkit.World world, PipeType kind, int x, int y, int z) {
        try {
            ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(world.getBlockAt(x, y, z));
            if (state == null)
                return false;
            Class<?> want = kind.resource() == PipeType.Resource.GAS ? GasPipeBehavior.class
                    : PipeBehavior.class;
            return isOrWraps(state.behavior(), want);
        } catch (Throwable t) {
            return false;
        }
    }

    /** Whether a route may be carved through this block, per the pipe kind's data. */
    private static boolean isReplaceable(Block block, PipeType kind) {
        if (block == null)
            return false;
        org.bukkit.NamespacedKey key = block.getType().getKey();
        String id = key.getNamespace() + ":" + key.getKey();
        for (String allowed : kind.replaceableBlocks())
            if (allowed.equalsIgnoreCase(id))
                return true;
        return false;
    }

    private static net.minecraft.world.level.Level nmsLevel(org.bukkit.World world) {
        try {
            return ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
        } catch (Throwable t) {
            return null;
        }
    }

    private static long pack(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
    }

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

    private static void msg(org.bukkit.entity.Player player, NamedTextColor color, String key, int... args) {
        Component c = Component.translatable(key, intArgs(args)).color(color);
        player.sendMessage(c);
    }

    private static List<Component> intArgs(int[] args) {
        List<Component> list = new ArrayList<>(args.length);
        for (int v : args)
            list.add(Component.text(v));
        return list;
    }
}
