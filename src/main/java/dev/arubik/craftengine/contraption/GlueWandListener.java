package dev.arubik.craftengine.contraption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.joml.Vector3f;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.util.Key;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Glue WAND item tool (CONTRAPTIONS.md §1 "Structure detection") — WorldEdit-style two-corner
 * AREA glue: right-click ONE corner block (sets pos1), right-click a SECOND block ANYWHERE
 * (sets pos2 and immediately glues the whole axis-aligned box between them into one connected
 * structure). Sneak+right-click cancels a pending pos1 instead of completing it. Each completed
 * area-glue spends one use of the wand's 192-use durability; it breaks on the use that empties it.
 *
 * <p>This replaces the earlier click-and-drag Super-Glue-style prototype: that needed a
 * scheduled task to detect "still holding right-click" via repeat-fired
 * {@link PlayerInteractEvent}s (the same trick {@code ConveyorWandListener}/
 * {@code PipeWandListener} use for their live aim-tracking preview), but the box-select flow
 * below is a plain two-click action and needs no hold/drag detection at all. The ONE scheduled
 * task this class still runs ({@link #start}) is unrelated to gesture detection — it is a
 * passive "show already-glued faces near your crosshair" indicator (Create's persistent
 * translucent glue overlay), refreshed every couple ticks for any player holding the wand.
 *
 * <p><b>Visual mechanism history.</b> This indicator has now gone through two different
 * renderings:
 * <ol>
 *   <li><b>Particle wireframe (original).</b> A sparse {@link org.bukkit.Particle#DUST}
 *   wireframe hugging the 12 edges of each glued block's real {@link Block#getBoundingBox()},
 *   sent via {@code World#spawnParticle} every 2 ticks for every player holding the wand. It was
 *   later refined (2026-07-02 follow-up — "que el outline siga el outline real de los bloques,
 *   prueba con el bounding box, aparte solo de bloques reales no consideres el aire") to hug the
 *   block's actual collision shape instead of always a flat 1x1x1 cube, and to skip AIR (a stale
 *   {@code GlueGraph} node whose block was broken by a player after being glued).</li>
 *   <li><b>Packet-only slime-block {@code BLOCK_DISPLAY} overlay (2026-07-02, THIS version —
 *   "optimiza las particulas del glue, ahorita salen muchas y sobrecargan el cliente ...
 *   renderizar un slime block encima del bloque pegado").</b> The particle wireframe was
 *   reported to overload the client: {@code EDGE_SAMPLES=3} points per edge × 12 edges = 40
 *   {@code DUST} particles PER GLUED BLOCK, resent every 2 ticks, for potentially dozens of
 *   blocks within {@link #PASSIVE_RADIUS} — both the raw packet volume (one
 *   {@code ClientboundLevelParticlesPacket} per particle call) and the client-side particle
 *   renderer cost (each particle needs its own render-thread lifecycle: spawn, animate, fade,
 *   despawn) scale linearly with block count, unlike a single entity's metadata. Replaced with
 *   ONE packet-only {@code minecraft:block_display} entity per glued block instead, rendering an
 *   actual {@code minecraft:slime_block} model scaled to {@link #OVERLAY_SCALE} (slightly larger
 *   than the real block, so it reads as a thin visible "shell" just outside the real surface
 *   without z-fighting) — same {@code MNms}-based raw-NMS packet idiom
 *   ({@code ClientboundAddEntityPacket}/{@code ClientboundSetEntityDataPacket}/
 *   {@code ClientboundEntityPositionSyncPacket}/{@code ClientboundRemoveEntitiesPacket}) already
 *   established by {@code ContraptionDisplaySwarm}/{@code ConveyorItemDisplay} elsewhere in this
 *   codebase, just with much simpler per-player bookkeeping local to this class (see
 *   {@link #indicatorsByPlayer}) since this is a passive player-facing indicator, not a captured
 *   contraption cell needing the full swarm-registry machinery. A single entity + one metadata
 *   packet (only resent when something actually changes) replaces 40 particle packets resent
 *   every 2 ticks regardless — a large reduction in both packet count and client render cost for
 *   the exact same "which blocks are glued near my crosshair" information.</li>
 * </ol>
 */
public class GlueWandListener implements Listener {

    /** The glue item's CraftEngine custom-item id. */
    public static final Key SLIME_GLUE = Key.of("cml", "slime_glue");

    /** Max raycast distance for both corner-picking and the passive preview. */
    private static final int REACH = 6;

    /** How far (in blocks, Chebyshev) around the looked-at structure to show passive glued-face markers. */
    private static final int PASSIVE_RADIUS = 6;

    /** Fixed durability cap for the glue wand: 192 area-glues before it breaks. */
    private static final int MAX_USES = 192;

    /** Per-player pending pos1 (first corner, awaiting pos2 to complete the box). */
    private final Map<UUID, BlockPos> pos1 = new HashMap<>();

    /**
     * Per-player currently-shown slime-block indicator entities, keyed by the glued
     * {@link BlockPos} they represent — see {@link #showPassiveGluedFaces}. Simple bookkeeping
     * local to this class (no swarm-registry machinery needed, unlike the contraption render
     * swarms): every tick, diff the freshly BFS'd set of in-range glued blocks against whatever
     * was shown to this player last tick, spawn indicators for newly-in-range blocks, despawn
     * ones that fell out of range, and leave unchanged ones alone.
     */
    private final Map<UUID, Map<BlockPos, SlimeIndicator>> indicatorsByPlayer = new HashMap<>();

    public GlueWandListener() {
    }

    /**
     * Start the passive glued-face preview timer: every 2 ticks, for each player holding the
     * glue wand, raytrace their crosshair and show a packet-only slime-block overlay on every
     * glued block near the looked-at structure (see class javadoc, "Visual mechanism history").
     * Called once at registration, mirroring {@code ConveyorWandListener#start}/
     * {@code PipeWandListener#start}.
     */
    public void start(org.bukkit.plugin.Plugin plugin) {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::tickPassivePreview, 5L, 2L);
    }

    private void tickPassivePreview() {
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            Key handId = (hand == null || hand.getType().isAir()) ? null : CraftEngineItems.getCustomItemId(hand);
            if (handId == null || !handId.equals(SLIME_GLUE)) {
                // No longer holding the wand (or logged off mid-hold) — despawn whatever
                // indicators were left showing for them (see #showPassiveGluedFaces javadoc;
                // unlike the old particle version, these are real packet-only entities that must
                // be explicitly cleaned up rather than simply stopping emission).
                clearIndicatorsFor(player);
                continue;
            }
            showPassiveGluedFaces(player);
        }
    }

    /** Despawns and forgets every slime-block indicator currently shown to {@code player}. */
    private void clearIndicatorsFor(Player player) {
        Map<BlockPos, SlimeIndicator> shown = indicatorsByPlayer.remove(player.getUniqueId());
        if (shown != null) {
            for (SlimeIndicator indicator : shown.values()) {
                indicator.despawn(player);
            }
        }
    }

    // ------------------------------------------------------------------ events

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND)
            return;
        Block clicked = event.getClickedBlock();
        if (clicked == null)
            return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir())
            return;
        Key handId = CraftEngineItems.getCustomItemId(hand);
        if (handId == null || !handId.equals(SLIME_GLUE))
            return; // cheap early-out: not the glue item -> do nothing

        event.setCancelled(true);
        UUID id = player.getUniqueId();
        UUID worldId = clicked.getWorld().getUID();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());

        if (player.isSneaking()) {
            if (pos1.remove(id) != null)
                player.sendActionBar(Component.text("Glue selection cleared.", NamedTextColor.GRAY));
            return;
        }

        BlockPos first = pos1.get(id);
        if (first == null) {
            pos1.put(id, pos);
            player.sendActionBar(Component.text(
                    "Corner 1 set at " + pos.getX() + "," + pos.getY() + "," + pos.getZ()
                            + " — right-click a second block to glue the area between them.",
                    NamedTextColor.AQUA));
            return;
        }

        if (first.equals(pos)) {
            // Same block clicked twice: treat as a fresh pos1 (matches the old anchor-reset
            // edge case) rather than a degenerate 1-block "area".
            pos1.put(id, pos);
            player.sendActionBar(Component.text(
                    "That's the same block — corner 1 reset there. Right-click a different block to glue.",
                    NamedTextColor.YELLOW));
            return;
        }

        pos1.remove(id);
        int glued = glueBox(worldId, first, pos);
        int size = GlueRegistry.structureAt(worldId, pos).size();
        spendUse(player);
        clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_SLIME_BLOCK_PLACE, 0.7f, 1.2f);
        player.sendActionBar(Component.text(
                "Glued " + glued + " block(s) in the box — structure now has " + size + " block(s).",
                NamedTextColor.GREEN));
    }

    /** Switching the held slot away from the glue item clears the pending pos1. */
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack next = player.getInventory().getItem(event.getNewSlot());
        Key id = next != null ? CraftEngineItems.getCustomItemId(next) : null;
        if (id == null || !id.equals(SLIME_GLUE))
            pos1.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pos1.remove(event.getPlayer().getUniqueId());
        // No despawn packets needed — the player is disconnecting, so their client won't see
        // anything anyway; just drop the bookkeeping so it doesn't leak.
        indicatorsByPlayer.remove(event.getPlayer().getUniqueId());
    }

    // ------------------------------------------------------------------ box glue

    /**
     * Glue every block in the axis-aligned box between {@code a} and {@code b} (inclusive on
     * both ends) into ONE connected structure. Doesn't need every possible adjacent pair — just
     * enough edges (each cell glued to its "next" neighbor along X, then Y, then Z) so
     * {@link GlueGraph#connectedComponents()} sees the whole box as a single component. Returns
     * the number of blocks (cells) covered by the box.
     */
    private int glueBox(UUID worldId, BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX()), maxX = Math.max(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY()), maxY = Math.max(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ()), maxZ = Math.max(a.getZ(), b.getZ());

        GlueGraph graph = GlueRegistry.graphFor(worldId);
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos here = new BlockPos(x, y, z);
                    graph.addNode(here);
                    count++;
                    if (x < maxX)
                        graph.glue(here, new BlockPos(x + 1, y, z));
                    if (y < maxY)
                        graph.glue(here, new BlockPos(x, y + 1, z));
                    if (z < maxZ)
                        graph.glue(here, new BlockPos(x, y, z + 1));
                }
            }
        }
        return count;
    }

    // ------------------------------------------------------------------ durability

    /**
     * Spend one use of the glue wand's 192-use durability (skipped in creative), breaking the
     * item on the use that empties it — mirrors {@code HammerAssembleListener#damageHammer}'s
     * vanilla-{@link Damageable} convention, but with a fixed {@link #MAX_USES} cap instead of
     * relying on {@link Damageable#hasMaxDamage()} (the custom item's own model may not define
     * vanilla max-damage metadata, so the cap is enforced here regardless).
     */
    private static void spendUse(Player player) {
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof Damageable dm))
            return;
        int dmg = dm.getDamage() + 1;
        if (dmg >= MAX_USES) {
            stack.setAmount(stack.getAmount() - 1);
            player.getInventory().setItemInMainHand(stack.getAmount() <= 0 ? null : stack);
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        } else {
            dm.setDamage(dmg);
            stack.setItemMeta(dm);
            player.getInventory().setItemInMainHand(stack);
        }
    }

    // ------------------------------------------------------------------ passive preview

    /**
     * Scale factor applied to each slime-block overlay entity (2026-07-02 — see class javadoc,
     * "Visual mechanism history"). Slightly larger than 1.0 so the fake block's faces sit just
     * OUTSIDE the real glued block's own surface, reading as a thin visible "shell" hugging the
     * block rather than z-fighting/flickering against it at an identical 1:1 size.
     */
    private static final float OVERLAY_SCALE = 1.02f;

    /**
     * Not-selecting passive indicator (Create's persistent translucent glue-face overlay): find
     * the structure the player is currently looking at (if any), BFS the glued blocks within
     * {@link #PASSIVE_RADIUS} of it (unchanged targeting/triggering logic from the old particle
     * version), and show a packet-only slime-block {@code BLOCK_DISPLAY} overlay on each —
     * spawning new ones, moving/refreshing ones still in range, and despawning ones that fell out
     * of range, diffed against what was shown to this player last tick (see
     * {@link #indicatorsByPlayer}).
     */
    private void showPassiveGluedFaces(Player player) {
        Map<BlockPos, SlimeIndicator> shown = indicatorsByPlayer.computeIfAbsent(player.getUniqueId(),
                k -> new HashMap<>());

        Block looked = player.getTargetBlockExact(REACH);
        Set<BlockPos> wanted;
        World world;
        if (looked == null || looked.getType().isAir()) {
            wanted = Set.of();
            world = null;
        } else {
            world = looked.getWorld();
            UUID worldId = world.getUID();
            GlueGraph graph = GlueRegistry.graphFor(worldId);
            BlockPos center = new BlockPos(looked.getX(), looked.getY(), looked.getZ());
            wanted = graph.hasNode(center) ? bfsWithinRadius(graph, center, PASSIVE_RADIUS) : Set.of();
        }

        // Despawn indicators for blocks no longer in range (or no structure looked at at all).
        java.util.Iterator<Map.Entry<BlockPos, SlimeIndicator>> it = shown.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, SlimeIndicator> e = it.next();
            if (!wanted.contains(e.getKey())) {
                e.getValue().despawn(player);
                it.remove();
            }
        }

        // Spawn/refresh indicators for every currently-wanted glued block.
        for (BlockPos node : wanted) {
            Block real = world.getBlockAt(node.getX(), node.getY(), node.getZ());
            if (real.getType().isAir()) {
                continue; // stale GlueGraph node whose block was broken since being glued — nothing to show
            }
            org.bukkit.util.BoundingBox box = real.getBoundingBox();
            if (box.getWidthX() <= 0 || box.getHeight() <= 0 || box.getWidthZ() <= 0) {
                continue; // no real collision shape (e.g. tripwire, some plants) — nothing to overlay
            }
            SlimeIndicator indicator = shown.computeIfAbsent(node, n -> new SlimeIndicator());
            indicator.render(player, box);
        }
    }

    /** Small BFS cap so the passive preview never walks an entire huge contraption every 2 ticks. */
    private static Set<BlockPos> bfsWithinRadius(GlueGraph graph, BlockPos start, int radius) {
        Set<BlockPos> visited = new HashSet<>();
        java.util.Deque<BlockPos> queue = new java.util.ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            if (chebyshev(start, cur) >= radius)
                continue;
            for (BlockPos next : graph.neighbors(cur)) {
                if (visited.add(next))
                    queue.add(next);
            }
        }
        return visited;
    }

    private static int chebyshev(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()),
                Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }

    /**
     * One packet-only {@code minecraft:block_display} entity rendering a
     * {@code minecraft:slime_block} model, used as the passive glued-face overlay for a single
     * glued block (see class javadoc, "Visual mechanism history"). Sized/positioned every
     * {@link #render} call from the real block's CURRENT {@link org.bukkit.util.BoundingBox} —
     * same convention {@code ContraptionDisplaySwarm}'s cells use for their captured
     * {@link BlockState} (always re-derived, never baked once) — scaled up by
     * {@link #OVERLAY_SCALE} and centered on the real box so it reads as a thin shell hugging the
     * block's actual (possibly non-full-cube, e.g. slab/stair) shape rather than a floating
     * generic 1x1x1 cube. Talks directly to ONE Bukkit {@link Player} at a time (this indicator
     * is per-viewer already, unlike the multi-viewer contraption swarms) via the CE player
     * wrapper ({@link CePlayers#resolveOne}) needed for {@code MNms}'s packet-send methods.
     */
    private static final class SlimeIndicator {
        private static final BlockState SLIME_BLOCK_STATE = Blocks.SLIME_BLOCK.defaultBlockState();

        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private final Object despawnPacket;
        private boolean spawned = false;

        // Last-sent position + scale, so #render only resends what actually changed.
        private double lastX = Double.NaN, lastY = Double.NaN, lastZ = Double.NaN;
        private float lastScaleX = -1f, lastScaleY = -1f, lastScaleZ = -1f;

        SlimeIndicator() {
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        /**
         * (Re)shows this indicator for {@code player} at {@code box}'s current extents, scaled up
         * by {@link #OVERLAY_SCALE} and centered on the real box (so a non-cube shape like a slab
         * gets a thin slab-shaped shell, not a full-cube one). A real {@code BLOCK_DISPLAY}'s
         * position is its model's min corner (see {@code ContraptionDisplaySwarm}'s own javadoc)
         * — the scaled box's min corner is derived by shrinking the box symmetrically around its
         * own center by the scale factor, same as vanilla's own {@code Scale} transform pivots
         * around the entity's local origin... but since a {@code BLOCK_DISPLAY}'s untransformed
         * model already spans exactly the full [0,1] cube of its own bounding box, a plain
         * {@code Scale} on the DEFAULT model would grow away from the position corner, not the
         * block's true center. To keep the overlay visually centered on the real (possibly
         * smaller-than-1x1x1) shape rather than ballooning toward +X/+Y/+Z, the entity's position
         * is nudged by half the size delta on each axis so growth is symmetric.
         */
        void render(Player player, org.bukkit.util.BoundingBox box) {
            net.momirealms.craftengine.core.entity.player.Player ce = CePlayers.resolveOne(player);
            if (ce == null) {
                return;
            }
            double sx = box.getWidthX(), sy = box.getHeight(), sz = box.getWidthZ();
            double grownX = sx * OVERLAY_SCALE, grownY = sy * OVERLAY_SCALE, grownZ = sz * OVERLAY_SCALE;
            double x = box.getMinX() - (grownX - sx) / 2.0;
            double y = box.getMinY() - (grownY - sy) / 2.0;
            double z = box.getMinZ() - (grownZ - sz) / 2.0;

            float fx = (float) grownX, fy = (float) grownY, fz = (float) grownZ;
            if (!spawned) {
                spawn(ce, x, y, z, fx, fy, fz);
                return;
            }
            boolean moved = x != lastX || y != lastY || z != lastZ;
            boolean scaleChanged = fx != lastScaleX || fy != lastScaleY || fz != lastScaleZ;
            if (moved) {
                ce.sendPacket(MNms.INSTANCE
                        .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
                lastX = x;
                lastY = y;
                lastZ = z;
            }
            if (scaleChanged) {
                // The real block's shape changed (e.g. a slab replaced in place by a full block)
                // — resend metadata with the new scale. Rare in practice, but a stale scale would
                // otherwise leave the overlay visibly mismatched with the real block's new shape.
                ce.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId,
                        metadata(fx, fy, fz)), false);
                lastScaleX = fx;
                lastScaleY = fy;
                lastScaleZ = fz;
            }
        }

        private void spawn(net.momirealms.craftengine.core.entity.player.Player ce, double x, double y, double z,
                float scaleX, float scaleY, float scaleZ) {
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, 0f, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0);
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId,
                    metadata(scaleX, scaleY, scaleZ));
            ce.sendPackets(List.of(addPacket, dataPacket), false);
            spawned = true;
            lastX = x;
            lastY = y;
            lastZ = z;
            lastScaleX = scaleX;
            lastScaleY = scaleY;
            lastScaleZ = scaleZ;
        }

        private List<Object> metadata(float scaleX, float scaleY, float scaleZ) {
            List<Object> values = new ArrayList<>();
            DisplayData.BlockDisplayData.BlockState.addEntityData(SLIME_BLOCK_STATE, values);
            DisplayData.Scale.addEntityData(new Vector3f(scaleX, scaleY, scaleZ), values);
            // Full-bright: this is a UI-style indicator overlay, not a real lit block — it should
            // read clearly regardless of the real block's own ambient lighting.
            DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
            DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
            return values;
        }

        void despawn(Player player) {
            if (!spawned) {
                return;
            }
            net.momirealms.craftengine.core.entity.player.Player ce = CePlayers.resolveOne(player);
            if (ce != null) {
                ce.sendPacket(despawnPacket, false);
            }
            spawned = false;
        }
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
