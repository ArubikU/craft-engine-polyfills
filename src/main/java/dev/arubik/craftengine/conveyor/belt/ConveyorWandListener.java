package dev.arubik.craftengine.conveyor.belt;

import dev.arubik.craftengine.conveyor.routing.AbstractRouterBlockEntity;
import dev.arubik.craftengine.conveyor.belt.ConveyorBehavior;
import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.conveyor.belt.ConveyorPart;
import dev.arubik.craftengine.conveyor.routing.ConveyorPath;
import dev.arubik.craftengine.conveyor.belt.ConveyorPreviewDisplay;
import dev.arubik.craftengine.conveyor.routing.ConveyorRoute;
import dev.arubik.craftengine.conveyor.belt.ConveyorSlope;
import dev.arubik.craftengine.util.CeWorlds;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.block.behavior.DualBlockBehavior;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.joml.Quaternionf;

public class ConveyorWandListener
implements Listener {
    private final Map<UUID, Mode> modes = new HashMap<UUID, Mode>();
    private final Map<UUID, BlockPos> magicA = new HashMap<UUID, BlockPos>();
    private final Map<UUID, List<BlockPos>> pointed = new HashMap<UUID, List<BlockPos>>();
    private final Map<UUID, List<ConveyorPreviewDisplay>> previews = new HashMap<UUID, List<ConveyorPreviewDisplay>>();
    private final Map<UUID, BlockPos> lastAim = new HashMap<UUID, BlockPos>();

    public void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tickAim, 5L, 4L);
    }

    private void tickAim() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            Block looked;
            UUID id = player.getUniqueId();
            BlockPos a = this.magicA.get(id);
            if (a == null) {
                this.lastAim.remove(id);
                continue;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            Key handId = CraftEngineItems.getCustomItemId((ItemStack)hand);
            if (handId == null || !ConveyorWandListener.isConveyorBlock(CraftEngineBlocks.byId((Key)handId)) || (looked = player.getTargetBlockExact(48)) == null) continue;
            BlockFace face = player.getTargetBlockFace(48);
            Block target = face != null ? looked.getRelative(face) : looked;
            BlockPos aimed = new BlockPos(target.getX(), target.getY(), target.getZ());
            BlockPos prevAim = this.lastAim.get(id);
            if (prevAim != null && prevAim.x() == aimed.x() && prevAim.y() == aimed.y() && prevAim.z() == aimed.z()) continue;
            this.lastAim.put(id, aimed);
            ConveyorRoute.Result plan = this.planTwoPoint(player.getWorld(), a, aimed);
            if (plan.isValid()) {
                this.showPreview(player, player.getWorld(), plan.steps());
                continue;
            }
            this.showPreview(player, player.getWorld(), List.of(new ConveyorPath.Step(a.x(), a.y(), a.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        boolean emptySelection;
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        org.bukkit.entity.Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) {
            return;
        }
        Key handId = CraftEngineItems.getCustomItemId((ItemStack)hand);
        if (handId == null) {
            return;
        }
        BlockDefinition conveyorDef = CraftEngineBlocks.byId((Key)handId);
        if (!ConveyorWandListener.isConveyorBlock(conveyorDef)) {
            return;
        }
        event.setCancelled(true);
        UUID id = player.getUniqueId();
        Mode mode = this.modes.getOrDefault(id, Mode.MAGIC);
        boolean sneaking = player.isSneaking();
        BlockFace face = event.getBlockFace();
        Block target = face != null ? clicked.getRelative(face) : clicked;
        BlockPos aimed = new BlockPos(target.getX(), target.getY(), target.getZ());
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (sneaking) {
                this.clearSelection(player);
                ConveyorWandListener.msg(player, NamedTextColor.GRAY, "polyfill.wand.cleared", new int[0]);
                return;
            }
            if (mode == Mode.POINTED) {
                this.commitPointed(player, clicked.getWorld(), hand, conveyorDef);
                return;
            }
            this.extendBelt(event, clicked, hand);
            return;
        }
        boolean bl = emptySelection = mode == Mode.MAGIC && this.magicA.get(id) == null || mode == Mode.POINTED && this.pointed.getOrDefault(id, List.of()).isEmpty();
        if (sneaking && emptySelection) {
            Mode next = mode == Mode.MAGIC ? Mode.POINTED : Mode.MAGIC;
            this.modes.put(id, next);
            this.clearSelection(player);
            if (next == Mode.POINTED) {
                ConveyorWandListener.msg(player, NamedTextColor.AQUA, "polyfill.wand.mode_pointed", new int[0]);
            } else {
                ConveyorWandListener.msg(player, NamedTextColor.AQUA, "polyfill.wand.mode_magic", new int[0]);
            }
            return;
        }
        if (mode == Mode.MAGIC) {
            this.leftClickMagic(player, clicked.getWorld(), hand, conveyorDef, aimed);
        } else {
            this.leftClickPointed(player, clicked.getWorld(), aimed);
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Key id;
        org.bukkit.entity.Player player = event.getPlayer();
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        Key key = id = next != null ? CraftEngineItems.getCustomItemId((ItemStack)next) : null;
        if (id == null || !ConveyorWandListener.isConveyorBlock(CraftEngineBlocks.byId((Key)id))) {
            this.clearSelection(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.clearSelection(event.getPlayer());
        UUID id = event.getPlayer().getUniqueId();
        this.magicA.remove(id);
        this.pointed.remove(id);
        this.previews.remove(id);
    }

    private void leftClickMagic(org.bukkit.entity.Player player, World world, ItemStack hand, BlockDefinition def, BlockPos aimed) {
        UUID id = player.getUniqueId();
        BlockPos a = this.magicA.get(id);
        if (a == null) {
            this.magicA.put(id, aimed);
            this.showPreview(player, world, List.of(new ConveyorPath.Step(aimed.x(), aimed.y(), aimed.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
            ConveyorWandListener.msg(player, NamedTextColor.GREEN, "polyfill.wand.point_a", new int[0]);
            return;
        }
        this.magicA.remove(id);
        ConveyorRoute.Result plan = this.planTwoPoint(world, a, aimed);
        if (!plan.isValid()) {
            this.clearPreview(player);
            ConveyorWandListener.msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        this.placeRoute(player, world, hand, def, plan.steps());
    }

    private ConveyorRoute.Result planTwoPoint(World world, BlockPos a, BlockPos b) {
        ArrayList<BlockPos> pts = new ArrayList<BlockPos>(2);
        pts.add(a);
        pts.add(b);
        return this.planPointed(world, pts);
    }

    private void leftClickPointed(org.bukkit.entity.Player player, World world, BlockPos aimed) {
        BlockPos last;
        UUID id = player.getUniqueId();
        List pts = this.pointed.computeIfAbsent(id, k -> new ArrayList());
        if (!pts.isEmpty() && (last = (BlockPos)pts.get(pts.size() - 1)).x() == aimed.x() && last.y() == aimed.y() && last.z() == aimed.z()) {
            ConveyorWandListener.msg(player, NamedTextColor.GRAY, "polyfill.wand.waypoint_dup", new int[0]);
            return;
        }
        pts.add(aimed);
        ConveyorWandListener.msg(player, NamedTextColor.GREEN, "polyfill.wand.waypoint_added", pts.size(), aimed.x(), aimed.y(), aimed.z());
        if (pts.size() >= 2) {
            this.previewPointed(player, world, pts);
        } else {
            this.showPreview(player, world, List.of(new ConveyorPath.Step(aimed.x(), aimed.y(), aimed.z(), 0, 1, ConveyorSlope.FLAT, ConveyorPart.START)));
        }
    }

    private void previewPointed(org.bukkit.entity.Player player, World world, List<BlockPos> pts) {
        ConveyorRoute.Result plan = this.planPointed(world, pts);
        if (!plan.isValid()) {
            this.clearPreview(player);
            ConveyorWandListener.msg(player, NamedTextColor.YELLOW, "polyfill.wand.route_pending", new int[0]);
            ConveyorWandListener.msg(player, NamedTextColor.YELLOW, plan.errorKey(), plan.errorArgs());
            return;
        }
        this.showPreview(player, world, plan.steps());
    }

    private void commitPointed(org.bukkit.entity.Player player, World world, ItemStack hand, BlockDefinition def) {
        UUID id = player.getUniqueId();
        List<BlockPos> pts = this.pointed.get(id);
        if (pts == null || pts.size() < 2) {
            ConveyorWandListener.msg(player, NamedTextColor.RED, "polyfill.wand.need_waypoints", new int[0]);
            return;
        }
        ConveyorRoute.Result plan = this.planPointed(world, pts);
        if (!plan.isValid()) {
            ConveyorWandListener.msg(player, NamedTextColor.RED, "polyfill.wand.route_failed", new int[0]);
            ConveyorWandListener.msg(player, NamedTextColor.RED, plan.errorKey(), plan.errorArgs());
            return;
        }
        this.pointed.remove(id);
        this.placeRoute(player, world, hand, def, plan.steps());
    }

    private ConveyorRoute.Result planPointed(World world, List<BlockPos> pts) {
        ArrayList<BlockPos> route = new ArrayList<BlockPos>(pts);
        if (route.size() >= 2) {
            int n;
            BlockPos snappedLast;
            BlockPos snappedFirst = this.snapRouterEndpoint(world, (BlockPos)route.get(0), (BlockPos)route.get(1));
            if (snappedFirst != null) {
                route.set(0, snappedFirst);
            }
            if ((snappedLast = this.snapRouterEndpoint(world, (BlockPos)route.get((n = route.size()) - 1), (BlockPos)route.get(n - 2))) != null) {
                route.set(n - 1, snappedLast);
            }
        }
        ArrayList<ConveyorRoute.Waypoint> wps = new ArrayList<ConveyorRoute.Waypoint>(route.size());
        for (BlockPos p : route) {
            wps.add(new ConveyorRoute.Waypoint(p.x(), p.y(), p.z()));
        }
        return ConveyorRoute.plan(wps, (x, y, z) -> this.cellFree(world, x, y, z));
    }

    private BlockPos snapRouterEndpoint(World world, BlockPos endpoint, BlockPos toward) {
        if (!this.isRouter(world, endpoint)) {
            return null;
        }
        int dx = Integer.signum(toward.x() - endpoint.x());
        int dz = Integer.signum(toward.z() - endpoint.z());
        if (dx != 0 && dz != 0) {
            if (Math.abs(toward.x() - endpoint.x()) >= Math.abs(toward.z() - endpoint.z())) {
                dz = 0;
            } else {
                dx = 0;
            }
        }
        if (dx == 0 && dz == 0) {
            return null;
        }
        return new BlockPos(endpoint.x() + dx, endpoint.y(), endpoint.z() + dz);
    }

    private boolean isRouter(World world, BlockPos pos) {
        try {
            CEWorld w = CeWorlds.of(world).storageWorld();
            if (w == null) {
                return false;
            }
            BlockEntity be = w.getBlockEntityAtIfLoaded(pos);
            return be != null && be.controller instanceof AbstractRouterBlockEntity;
        }
        catch (Throwable t) {
            return false;
        }
    }

    private boolean cellFree(World world, int x, int y, int z) {
        Block b = world.getBlockAt(x, y, z);
        if (ConveyorBlockEntity.isReplaceable(b)) {
            return true;
        }
        return ConveyorWandListener.isTopSlab(b);
    }

    private static boolean isTopSlab(Block b) {
        try {
            BlockData blockData = b.getBlockData();
            if (blockData instanceof Slab) {
                Slab slab = (Slab)blockData;
                return slab.getType() == Slab.Type.TOP;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private void placeRoute(org.bukkit.entity.Player player, World bukkitWorld, ItemStack hand, BlockDefinition def, List<ConveyorPath.Step> steps) {
        int budget;
        this.clearPreview(player);
        CEWorld world = CeWorlds.of(bukkitWorld).storageWorld();
        if (world == null) {
            ConveyorWandListener.msg(player, NamedTextColor.RED, "polyfill.wand.no_world", new int[0]);
            return;
        }
        for (ConveyorPath.Step s : steps) {
            Block block = bukkitWorld.getBlockAt(s.x, s.y, s.z);
            if (ConveyorBlockEntity.isReplaceable(block) || ConveyorWandListener.isTopSlab(block)) continue;
            ConveyorWandListener.msg(player, NamedTextColor.RED, "polyfill.wand.place_blocked", s.x, s.y, s.z);
            return;
        }
        boolean creative = player.getGameMode() == GameMode.CREATIVE;
        int n = budget = creative ? Integer.MAX_VALUE : hand.getAmount();
        if (budget <= 0) {
            ConveyorWandListener.msg(player, NamedTextColor.RED, "polyfill.wand.out_of_items", new int[0]);
            return;
        }
        BlockPos prev = null;
        int placedCount = 0;
        for (ConveyorPath.Step s : steps) {
            if (placedCount >= budget) {
                ConveyorWandListener.msg(player, NamedTextColor.YELLOW, "polyfill.wand.not_enough", placedCount, steps.size());
                break;
            }
            Direction facing = ConveyorWandListener.travelDirection(s.stepX, s.stepZ);
            Location loc = new Location(bukkitWorld, (double)s.x, (double)s.y, (double)s.z);
            ImmutableBlockState newState = ConveyorWandListener.stateWith(def.defaultState(), facing, s.slope, s.part);
            boolean placed = CraftEngineBlocks.place((Location)loc, (ImmutableBlockState)newState, (int)3, (boolean)false);
            if (!placed) continue;
            ++placedCount;
            BlockPos here = new BlockPos(s.x, s.y, s.z);
            ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(world, here);
            if (seg != null) {
                seg.clear();
                if (prev != null) {
                    seg.setPrevPos(prev);
                }
            }
            prev = here;
        }
        if (!creative && placedCount > 0) {
            hand.setAmount(hand.getAmount() - placedCount);
        }
        ConveyorWandListener.msg(player, NamedTextColor.GREEN, "polyfill.wand.placed", placedCount);
    }

    private void extendBelt(PlayerInteractEvent event, Block clicked, ItemStack hand) {
        ImmutableBlockState clickedState = CraftEngineBlocks.getCustomBlockState((Block)clicked);
        if (!ConveyorWandListener.isConveyorState(clickedState)) {
            return;
        }
        CEWorld w = CeWorlds.of(clicked.getWorld()).storageWorld();
        if (w == null) {
            return;
        }
        BlockPos cp = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        ConveyorBlockEntity seg = ConveyorBlockEntity.conveyorAt(w, cp);
        if (seg == null) {
            return;
        }
        boolean grew = seg.part() == ConveyorPart.START ? seg.extendStart(w, cp, seg.facing(), seg.slope()) : seg.extend(w, cp, seg.facing(), seg.slope());
        if (grew) {
            org.bukkit.entity.Player pl = event.getPlayer();
            if (pl.getGameMode() != GameMode.CREATIVE) {
                ItemStack h = pl.getInventory().getItemInMainHand();
                h.setAmount(h.getAmount() - 1);
            }
        } else {
            ConveyorWandListener.msg(event.getPlayer(), NamedTextColor.RED, "polyfill.wand.extend_failed", new int[0]);
        }
    }

    private void showPreview(org.bukkit.entity.Player player, World bukkitWorld, List<ConveyorPath.Step> steps) {
        this.clearPreview(player);
        CEWorld world = CeWorlds.of(bukkitWorld).storageWorld();
        if (world == null) {
            return;
        }
        ArrayList<Player> viewers = new ArrayList<Player>();
        Player cePlayer = ConveyorWandListener.cePlayer(player);
        if (cePlayer != null) {
            viewers.add(cePlayer);
        }
        if (viewers.isEmpty()) {
            return;
        }
        ArrayList<ConveyorPreviewDisplay> list = new ArrayList<ConveyorPreviewDisplay>(steps.size());
        for (ConveyorPath.Step s : steps) {
            ConveyorPreviewDisplay d = new ConveyorPreviewDisplay();
            ItemStack model = ConveyorWandListener.previewItem(s.slope);
            if (model == null) continue;
            d.setNmsItem(CraftItemStack.asNMSCopy((ItemStack)model));
            Direction facing = ConveyorWandListener.travelDirection(s.stepX, s.stepZ);
            d.setRotation(ConveyorWandListener.previewRotation(facing, s.slope));
            d.setScale(1.0f, 1.0f, 1.0f);
            d.render(viewers, (double)s.x + 0.5, (double)s.y + 0.5, (double)s.z + 0.5);
            list.add(d);
        }
        this.previews.put(player.getUniqueId(), list);
    }

    private static Quaternionf previewRotation(Direction facing, ConveyorSlope slope) {
        float yaw = switch (facing) {
            case Direction.SOUTH -> 0.0f;
            case Direction.WEST -> (float)Math.toRadians(90.0);
            case Direction.NORTH -> (float)Math.toRadians(180.0);
            case Direction.EAST -> (float)Math.toRadians(270.0);
            default -> 0.0f;
        };
        if (slope != ConveyorSlope.FLAT && (facing == Direction.WEST || facing == Direction.EAST)) {
            yaw += (float)Math.PI;
        }
        return new Quaternionf().rotateY(yaw);
    }

    private static ItemStack previewItem(ConveyorSlope slope) {
        String idName = slope == ConveyorSlope.UP ? "cv_preview_up" : (slope == ConveyorSlope.DOWN ? "cv_preview_down" : "cv_preview_flat");
        try {
            BukkitItemDefinition d = CraftEngineItems.byId((Key)Key.of((String)"cml", (String)idName));
            if (d != null) {
                return d.buildBukkitItem();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private void clearPreview(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        List<ConveyorPreviewDisplay> list = this.previews.remove(id);
        if (list == null || list.isEmpty()) {
            return;
        }
        Player cePlayer = ConveyorWandListener.cePlayer(player);
        List<Player> viewers = cePlayer != null ? List.of(cePlayer) : List.of();
        for (ConveyorPreviewDisplay d : list) {
            d.despawnAll(viewers);
        }
    }

    private void clearSelection(org.bukkit.entity.Player player) {
        UUID id = player.getUniqueId();
        this.magicA.remove(id);
        this.pointed.remove(id);
        this.lastAim.remove(id);
        this.clearPreview(player);
    }

    private static Player cePlayer(org.bukkit.entity.Player player) {
        try {
            UUID want = player.getUniqueId();
            for (Player p : CraftEngine.instance().networkManager().onlineUsers()) {
                org.bukkit.entity.Player b;
                Object pp = p.platformPlayer();
                if (!(pp instanceof org.bukkit.entity.Player) || !(b = (org.bukkit.entity.Player)pp).getUniqueId().equals(want)) continue;
                return p;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static void msg(org.bukkit.entity.Player player, NamedTextColor color, String key, int ... args) {
        Component c = Component.translatable((String)key, ConveyorWandListener.intArgs(args)).color((TextColor)color);
        player.sendMessage(c);
    }

    private static List<Component> intArgs(int[] args) {
        ArrayList<Component> list = new ArrayList<Component>(args.length);
        for (int v : args) {
            list.add((Component)Component.text((int)v));
        }
        return list;
    }

    private static boolean isConveyorBlock(BlockDefinition def) {
        if (def == null) {
            return false;
        }
        return ConveyorWandListener.isConveyorState(def.defaultState());
    }

    private static boolean isConveyorState(ImmutableBlockState state) {
        if (state == null) {
            return false;
        }
        BlockBehavior b = state.behavior();
        if (b instanceof ConveyorBehavior) {
            return true;
        }
        if (b instanceof DualBlockBehavior) {
            DualBlockBehavior dual = (DualBlockBehavior)b;
            return dual.getFirst(ConveyorBehavior.class) != null;
        }
        if (b instanceof CompositeBlockBehavior) {
            CompositeBlockBehavior comp = (CompositeBlockBehavior)b;
            return comp.getFirst(ConveyorBehavior.class) != null;
        }
        return false;
    }

    private static Direction travelDirection(int stepX, int stepZ) {
        if (stepX > 0) {
            return Direction.EAST;
        }
        if (stepX < 0) {
            return Direction.WEST;
        }
        if (stepZ > 0) {
            return Direction.SOUTH;
        }
        return Direction.NORTH;
    }

    private static ImmutableBlockState stateWith(ImmutableBlockState base, Direction facing, ConveyorSlope slope, ConveyorPart part) {
        ImmutableBlockState s = base;
        s = ConveyorWandListener.withEnum(s, "facing", facing.name());
        s = ConveyorWandListener.withEnum(s, "slope", slope.name());
        s = ConveyorWandListener.withEnum(s, "part", part.name());
        return s;
    }

    private static ImmutableBlockState withEnum(ImmutableBlockState state, String prop, String valueName) {
        Property p = state.getProperty(prop);
        if (p == null) {
            return state;
        }
        try {
            Comparable value = p.valueByName(valueName.toLowerCase());
            if (value == null) {
                value = p.valueByName(valueName);
            }
            if (value == null) {
                return state;
            }
            return ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, value);
        }
        catch (Throwable t) {
            return state;
        }
    }

    public static enum Mode {
        MAGIC,
        POINTED;

    }
}

