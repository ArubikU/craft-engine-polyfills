/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$BlockDisplayData
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory
 *  net.momirealms.craftengine.core.pack.Pack
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.Damageable
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.BoundingBox
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.glue;

import dev.arubik.craftengine.contraption.glue.GlueGraph;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.item.behavior.ExtendedItemBehavior;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.joml.Vector3f;

public class GlueItemBehavior
extends ExtendedItemBehavior {
    private static final Map<UUID, BlockPos> PENDING;
    private static final Map<UUID, Map<BlockPos, SlimeIndicator>> INDICATORS;
    private static boolean systemStarted;
    private static final int REACH = 6;
    private static final int PASSIVE_RADIUS = 6;
    private static final int DEFAULT_MAX_USES = 192;
    private static final float OVERLAY_SCALE = 1.02f;
    private final boolean consume;
    private final int maxUses;
    public static final Factory FACTORY;
    private static final AtomicInteger ENTITY_COUNTER;

    public static void startSystem(Plugin plugin) {
        if (systemStarted) {
            return;
        }
        systemStarted = true;
        plugin.getServer().getPluginManager().registerEvents((Listener)new SystemListener(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, GlueItemBehavior::tickPassivePreview, 5L, 2L);
    }

    private static boolean holdsGlueItem(org.bukkit.entity.Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) {
            return false;
        }
        BukkitItemDefinition ceItem = CraftEngineItems.byItemStack((ItemStack)hand);
        if (ceItem == null) {
            return false;
        }
        ArrayList found = new ArrayList(1);
        ceItem.behavior().let(GlueItemBehavior.class, found::add);
        return !found.isEmpty();
    }

    private static void tickPassivePreview() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (!GlueItemBehavior.holdsGlueItem(player)) {
                GlueItemBehavior.clearIndicators(player);
                continue;
            }
            GlueItemBehavior.showOverlay(player);
        }
    }

    private static void clearIndicators(org.bukkit.entity.Player player) {
        Map<BlockPos, SlimeIndicator> shown = INDICATORS.remove(player.getUniqueId());
        if (shown != null) {
            shown.values().forEach(i -> i.despawn(player));
        }
    }

    /**
     * Draws the glue preview for one player.
     *
     * <p>Three separate things need showing, and only the last of them used to be drawn:
     * <ul>
     *   <li>PENDING — the first block of a pair, after one click. It stays lit wherever the player
     *       then looks, because otherwise nothing tells them a selection is in progress at all.</li>
     *   <li>HOVER — the block under the cursor, so it is clear what the next click would take.</li>
     *   <li>GLUED — the cluster the cursor is over, which is what the old preview showed.</li>
     * </ul>
     * Each uses a different block and scale so they read apart at a glance: honey for the pending
     * pick, slime for anything glued or about to be.
     */
    private static void showOverlay(org.bukkit.entity.Player player) {
        Map<BlockPos, SlimeIndicator> shown = INDICATORS.computeIfAbsent(player.getUniqueId(), k -> new HashMap());
        Map<BlockPos, Highlight> wanted = new HashMap<BlockPos, Highlight>();

        World world = player.getWorld();
        ResourceKey worldId = ((CraftWorld)world).getHandle().dimension();
        GlueGraph graph = GlueRegistry.graphFor((ResourceKey<Level>)worldId);

        // The cluster under the cursor, and the cursor's own block as the next candidate.
        Block looked = player.getTargetBlockExact(6);
        if (looked != null && !looked.getType().isAir()) {
            BlockPos center = new BlockPos(looked.getX(), looked.getY(), looked.getZ());
            if (graph.hasNode(center)) {
                for (BlockPos node : GlueItemBehavior.bfs(graph, center, 6)) {
                    wanted.put(node, Highlight.GLUED);
                }
            }
            wanted.putIfAbsent(center, Highlight.HOVER);
        }

        // The half-made pair outranks both: it must stay visible while the player looks around
        // for its partner.
        BlockPos pending = PENDING.get(player.getUniqueId());
        if (pending != null) {
            wanted.put(pending, Highlight.PENDING);
        }

        // Drop anything no longer wanted, and anything whose highlight changed — an indicator's
        // block and scale are baked in when it spawns, so a change means a fresh entity.
        Iterator<Map.Entry<BlockPos, SlimeIndicator>> it = shown.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, SlimeIndicator> e = it.next();
            Highlight now = wanted.get(e.getKey());
            if (now != null && now == ((SlimeIndicator)e.getValue()).kind) continue;
            ((SlimeIndicator)e.getValue()).despawn(player);
            it.remove();
        }

        for (Map.Entry<BlockPos, Highlight> entry : wanted.entrySet()) {
            BoundingBox box;
            BlockPos node = entry.getKey();
            Block real = world.getBlockAt(node.getX(), node.getY(), node.getZ());
            if (real.getType().isAir() || (box = real.getBoundingBox()).getWidthX() <= 0.0
                    || box.getHeight() <= 0.0 || box.getWidthZ() <= 0.0) continue;
            shown.computeIfAbsent(node, n -> new SlimeIndicator(entry.getValue())).render(player, box);
        }
    }

    private static Set<BlockPos> bfs(GlueGraph graph, BlockPos start, int radius) {
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            BlockPos cur = (BlockPos)queue.poll();
            if (GlueItemBehavior.chebyshev(start, cur) >= radius) continue;
            for (BlockPos next : graph.neighbors(cur)) {
                if (!visited.add(next)) continue;
                queue.add(next);
            }
        }
        return visited;
    }

    private static int chebyshev(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()), Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }

    private static int glueBox(ResourceKey<Level> worldId, BlockPos a, BlockPos b) {
        int x0 = Math.min(a.getX(), b.getX());
        int x1 = Math.max(a.getX(), b.getX());
        int y0 = Math.min(a.getY(), b.getY());
        int y1 = Math.max(a.getY(), b.getY());
        int z0 = Math.min(a.getZ(), b.getZ());
        int z1 = Math.max(a.getZ(), b.getZ());
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        int count = 0;
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                for (int z = z0; z <= z1; ++z) {
                    BlockPos here = new BlockPos(x, y, z);
                    graph.addNode(here);
                    ++count;
                    if (x < x1) {
                        graph.glue(here, new BlockPos(x + 1, y, z));
                    }
                    if (y < y1) {
                        graph.glue(here, new BlockPos(x, y + 1, z));
                    }
                    if (z >= z1) continue;
                    graph.glue(here, new BlockPos(x, y, z + 1));
                }
            }
        }
        return count;
    }

    public GlueItemBehavior(boolean consume, int maxUses) {
        this.consume = consume;
        this.maxUses = maxUses;
    }

    @Override
    public net.minecraft.world.item.ItemStack onRightClick(net.minecraft.world.item.ItemStack stack, Object ... args) {
        Object object;
        if (args.length < 1 || !((object = args[0]) instanceof PlayerInteractEvent)) {
            return stack;
        }
        PlayerInteractEvent event = (PlayerInteractEvent)object;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return stack;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return stack;
        }
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return stack;
        }
        org.bukkit.entity.Player player = event.getPlayer();
        event.setCancelled(true);
        try {
            ResourceKey worldId = ((CraftWorld)clicked.getWorld()).getHandle().dimension();
            BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
            UUID id = player.getUniqueId();
            if (player.isSneaking()) {
                if (PENDING.remove(id) != null) {
                    player.sendActionBar((Component)Component.text((String)"Glue selection cleared.", (TextColor)NamedTextColor.GRAY));
                }
                return stack;
            }
            BlockPos first = PENDING.get(id);
            if (first == null) {
                PENDING.put(id, pos);
                player.sendActionBar((Component)Component.text((String)("Corner 1: " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " \u2014 right-click second block to glue area."), (TextColor)NamedTextColor.AQUA));
                return stack;
            }
            if (first.equals(pos)) {
                PENDING.put(id, pos);
                player.sendActionBar((Component)Component.text((String)"Same block \u2014 corner 1 reset.", (TextColor)NamedTextColor.YELLOW));
                return stack;
            }
            PENDING.remove(id);
            int glued = GlueItemBehavior.glueBox((ResourceKey<Level>)worldId, first, pos);
            int size = GlueRegistry.structureAt((ResourceKey<Level>)worldId, pos).size();
            if (this.consume) {
                ItemStack bStack = stack.asBukkitMirror();
                if (player.getGameMode() != GameMode.CREATIVE) {
                    bStack.setAmount(bStack.getAmount() - 1);
                    player.getInventory().setItemInMainHand(bStack.getAmount() <= 0 ? null : bStack);
                }
            } else {
                GlueItemBehavior.spendDurability(player, this.maxUses);
            }
            clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_SLIME_BLOCK_PLACE, 0.7f, 1.2f);
            player.sendActionBar((Component)Component.text((String)("Glued " + glued + " block(s) \u2014 structure: " + size + " block(s)."), (TextColor)NamedTextColor.GREEN));
        }
        catch (Throwable t) {
            player.sendMessage(Component.text((String)("[Glue] Error: " + String.valueOf(t))).color((TextColor)NamedTextColor.RED));
            t.printStackTrace();
        }
        return stack;
    }

    private static void spendDurability(org.bukkit.entity.Player player, int maxUses) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof Damageable)) {
            return;
        }
        Damageable dm = (Damageable)meta;
        int dmg = dm.getDamage() + 1;
        if (dmg >= maxUses) {
            stack.setAmount(stack.getAmount() - 1);
            player.getInventory().setItemInMainHand(stack.getAmount() <= 0 ? null : stack);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        } else {
            dm.setDamage(dmg);
            stack.setItemMeta((ItemMeta)dm);
            player.getInventory().setItemInMainHand(stack);
        }
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger c;
        PENDING = new HashMap<UUID, BlockPos>();
        INDICATORS = new HashMap<UUID, Map<BlockPos, SlimeIndicator>>();
        systemStarted = false;
        FACTORY = new Factory();
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            c = (AtomicInteger)f.get(null);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = c;
    }

    public static final class SystemListener
    implements Listener {
        @EventHandler
        public void onItemHeld(PlayerItemHeldEvent event) {
            BukkitItemDefinition ceItem;
            org.bukkit.entity.Player player = event.getPlayer();
            ItemStack next = player.getInventory().getItem(event.getNewSlot());
            BukkitItemDefinition bukkitItemDefinition = ceItem = next != null ? CraftEngineItems.byItemStack((ItemStack)next) : null;
            if (ceItem == null) {
                PENDING.remove(player.getUniqueId());
                return;
            }
            ArrayList found = new ArrayList(1);
            ceItem.behavior().let(GlueItemBehavior.class, found::add);
            if (found.isEmpty()) {
                PENDING.remove(player.getUniqueId());
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            UUID id = event.getPlayer().getUniqueId();
            PENDING.remove(id);
            INDICATORS.remove(id);
        }

        /**
         * Sneak + left-click with glue in hand detaches that block from its structure.
         *
         * <p>Gluing was one-way: the only way to undo it was to break the block, which is exactly
         * what a player does NOT want when they just meant to re-shape a contraption. The event is
         * cancelled so the block survives — the glue comes off, the wall stays up.
         *
         * <p>Runs at HIGHEST with ignoreCancelled=false so it still fires where protection plugins
         * would deny the break: removing glue is not breaking anything.
         */
        @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
        public void onUnglue(PlayerInteractEvent event) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
                return;
            }
            org.bukkit.entity.Player player = event.getPlayer();
            if (!player.isSneaking() || !GlueItemBehavior.holdsGlueItem(player)) {
                return;
            }
            Block clicked = event.getClickedBlock();
            if (clicked == null) {
                return;
            }
            // Cancel regardless of whether anything was glued here: with glue in hand and sneaking,
            // the player is asking to edit glue, never to start mining.
            event.setCancelled(true);

            ResourceKey worldId = ((CraftWorld) clicked.getWorld()).getHandle().dimension();
            BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
            GlueGraph graph = GlueRegistry.graphFor((ResourceKey<Level>) worldId);
            if (!graph.hasNode(pos)) {
                player.sendActionBar((Component) Component.text((String) "Nothing glued here.",
                        (TextColor) NamedTextColor.GRAY));
                return;
            }

            int detached = graph.neighbors(pos).size();
            graph.removeNode(pos);
            // Drop this player's cached indicators so the overlay redraws without the stale block.
            GlueItemBehavior.clearIndicators(player);
            clicked.getWorld().playSound(clicked.getLocation(), Sound.BLOCK_SLIME_BLOCK_BREAK, 0.7f, 0.8f);
            player.sendActionBar((Component) Component.text(
                    (String) ("Unglued \u2014 " + detached + " connection(s) removed."),
                    (TextColor) NamedTextColor.YELLOW));
        }
    }

    /** What an indicator is telling the player. */
    private enum Highlight {
        /** Already glued: part of the cluster under the cursor. */
        GLUED(Blocks.SLIME_BLOCK.defaultBlockState(), 1.02f),
        /** The first block of a pending pair, waiting for its partner. */
        PENDING(Blocks.HONEY_BLOCK.defaultBlockState(), 1.06f),
        /** The block under the cursor, offered as the next selection. */
        HOVER(Blocks.SLIME_BLOCK.defaultBlockState(), 1.04f);

        final BlockState state;
        final float scale;

        Highlight(BlockState state, float scale) {
            this.state = state;
            this.scale = scale;
        }
    }

    private static final class SlimeIndicator {
        private final Highlight kind;
        private final int entityId = GlueItemBehavior.nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPkt;
        private boolean spawned = false;
        private double lastX = Double.NaN;
        private double lastY;
        private double lastZ;
        private float lastSX = -1.0f;
        private float lastSY;
        private float lastSZ;

        SlimeIndicator(Highlight kind) {
            this.kind = kind;
            this.despawnPkt = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void render(org.bukkit.entity.Player player, BoundingBox box) {
            Player ce = CePlayers.resolveOne(player);
            if (ce == null) {
                return;
            }
            double sx = box.getWidthX();
            double sy = box.getHeight();
            double sz = box.getWidthZ();
            double gx = sx * (double)this.kind.scale;
            double gy = sy * (double)this.kind.scale;
            double gz = sz * (double)this.kind.scale;
            double x = box.getMinX() - (gx - sx) / 2.0;
            double y = box.getMinY() - (gy - sy) / 2.0;
            double z = box.getMinZ() - (gz - sz) / 2.0;
            float fx = (float)gx;
            float fy = (float)gy;
            float fz = (float)gz;
            if (!this.spawned) {
                Object addPkt = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0.0);
                Object dataPkt = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, SlimeIndicator.meta(this.kind, fx, fy, fz));
                ce.sendPackets(List.of(addPkt, dataPkt), false);
                this.spawned = true;
                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
                this.lastSX = fx;
                this.lastSY = fy;
                this.lastSZ = fz;
                return;
            }
            if (x != this.lastX || y != this.lastY || z != this.lastZ) {
                ce.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, x, y, z, 0.0f, 0.0f, false), false);
                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
            }
            if (fx != this.lastSX || fy != this.lastSY || fz != this.lastSZ) {
                ce.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, SlimeIndicator.meta(this.kind, fx, fy, fz)), false);
                this.lastSX = fx;
                this.lastSY = fy;
                this.lastSZ = fz;
            }
        }

        void despawn(org.bukkit.entity.Player player) {
            if (!this.spawned) {
                return;
            }
            Player ce = CePlayers.resolveOne(player);
            if (ce != null) {
                ce.sendPacket(this.despawnPkt, false);
            }
            this.spawned = false;
        }

        private static List<Object> meta(Highlight kind, float sx, float sy, float sz) {
            ArrayList<Object> v = new ArrayList<Object>();
            DisplayData.BlockDisplayData.BlockState.addEntityData(kind.state, v);
            DisplayData.Scale.addEntityData(new Vector3f(sx, sy, sz), v);
            DisplayData.BrightnessOverride.addEntityData(0xF000F0, v);
            DisplayData.PosRotInterpolationDuration.addEntityData(2, v);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, v);
            return v;
        }
    }

    public static final class Factory
    implements ItemBehaviorFactory<GlueItemBehavior> {
        public GlueItemBehavior create(Pack pack, Path path, Key id, ConfigSection config) {
            boolean consume = config == null || config.getBoolean("consume", true);
            int maxUses = config == null ? 192 : config.getInt("max_uses", 192);
            return new GlueItemBehavior(consume, maxUses);
        }
    }
}

