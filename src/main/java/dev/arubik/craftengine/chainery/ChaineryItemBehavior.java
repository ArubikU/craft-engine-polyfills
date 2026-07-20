package dev.arubik.craftengine.chainery;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.arubik.craftengine.item.behavior.ExtendedItemBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * Item behavior that draws a {@link Chain} between two right-clicked points (CHAINERY — "al hacer click
 * derecho en 2 puntos creará una cadena"). First click stores point A; the second computes the span, and if
 * the player holds enough links (one per block of distance, within the material's {@code maxBlocks}) it
 * consumes that many, places a chain block at each end, and registers the rendered span. Sneak-right-click
 * cancels a pending first point.
 */
public class ChaineryItemBehavior extends ExtendedItemBehavior {

    private record Pending(UUID worldId, BlockPos pos, org.joml.Vector3d offset, boolean reuse) {
    }

    /** Straight distance between the two ATTACH points (anchor centre + its hitbox offset). */
    private static double attachDistance(BlockPos a, org.joml.Vector3d oa, BlockPos b, org.joml.Vector3d ob) {
        double ax = a.getX() + 0.5 + (oa == null ? 0 : oa.x);
        double ay = a.getY() + 0.5 + (oa == null ? 0 : oa.y);
        double az = a.getZ() + 0.5 + (oa == null ? 0 : oa.z);
        double bx = b.getX() + 0.5 + (ob == null ? 0 : ob.x);
        double by = b.getY() + 0.5 + (ob == null ? 0 : ob.y);
        double bz = b.getZ() + 0.5 + (ob == null ? 0 : ob.z);
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * The attach offset from the anchor cell's centre to the exact hook point on the clicked block's REAL
     * collision box (the face-centre of that box on the clicked side) — so the chain hangs off a slab's top, a
     * fence post, a stair step, etc., not a flat cell face. Falls back to the flat face if the box is degenerate.
     */
    private static org.joml.Vector3d attachOffset(Block clicked, BlockFace face) {
        Block anchor = clicked.getRelative(face);
        double acx = anchor.getX() + 0.5, acy = anchor.getY() + 0.5, acz = anchor.getZ() + 0.5;
        org.bukkit.util.BoundingBox bb = clicked.getBoundingBox();
        double px, py, pz;
        if (bb == null || bb.getVolume() <= 0.0) {
            px = clicked.getX() + 0.5;
            py = clicked.getY() + 0.5;
            pz = clicked.getZ() + 0.5;
        } else {
            px = bb.getCenterX();
            py = bb.getCenterY();
            pz = bb.getCenterZ();
            switch (face) {
                case UP -> py = bb.getMaxY();
                case DOWN -> py = bb.getMinY();
                case NORTH -> pz = bb.getMinZ();
                case SOUTH -> pz = bb.getMaxZ();
                case EAST -> px = bb.getMaxX();
                case WEST -> px = bb.getMinX();
                default -> {
                }
            }
        }
        return new org.joml.Vector3d(px - acx, py - acy, pz - acz);
    }

    /** Per-player first point awaiting its second click. */
    private static final Map<UUID, Pending> PENDING = new ConcurrentHashMap<>();

    private final ChainMaterial material;

    public ChaineryItemBehavior(ChainMaterial material) {
        this.material = material;
    }

    @Override
    public ItemStack onRightClick(ItemStack stack, Object... args) {
        if (args.length < 1 || !(args[0] instanceof PlayerInteractEvent event)) {
            return stack;
        }
        Block clicked = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Player player = event.getPlayer();
        if (clicked == null || face == null || player == null) {
            return stack; // air click — nothing to anchor to
        }
        event.setCancelled(true); // never let a stray block-place fire from the anchor gesture

        // If the clicked block is ALREADY an anchor, REUSE it (share the anchor); else place a new anchor in the
        // adjacent air. A shared anchor can host up to 4 chains.
        net.minecraft.world.level.Level nms =
                ((org.bukkit.craftbukkit.CraftWorld) clicked.getWorld()).getHandle();
        BlockPos clickedPos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        boolean reuse = ChaineryBlockBehavior.getAt(nms, clickedPos) != null;
        World world;
        BlockPos pos;
        org.joml.Vector3d attach;
        if (reuse) {
            world = clicked.getWorld();
            pos = clickedPos;
            // Inherit the offset an existing chain already uses here, so all chains on a shared anchor connect at
            // the SAME point (not one at the centre and one on the hitbox).
            attach = ChainRegistry.offsetAt(world.getUID(), pos);
            if (ChainRegistry.countAt(world.getUID(), pos) >= 4) {
                player.sendActionBar(net.kyori.adventure.text.Component.text("§cEste anclaje ya tiene 4 cadenas"));
                return stack;
            }
        } else {
            Block target = clicked.getRelative(face);
            if (!target.getType().isAir() && !target.isReplaceable()) {
                player.sendActionBar(net.kyori.adventure.text.Component.text("§cNo hay espacio para anclar la cadena aquí"));
                return stack;
            }
            world = target.getWorld();
            pos = new BlockPos(target.getX(), target.getY(), target.getZ());
            attach = attachOffset(clicked, face); // hook onto the clicked block's real hitbox
        }

        if (player.isSneaking()) {
            PENDING.remove(player.getUniqueId());
            player.sendActionBar(net.kyori.adventure.text.Component.text("§7Selección de cadena cancelada"));
            return stack;
        }

        Pending first = PENDING.get(player.getUniqueId());
        if (first == null || !first.worldId().equals(world.getUID())) {
            PENDING.put(player.getUniqueId(), new Pending(world.getUID(), pos, attach, reuse));
            player.sendActionBar(net.kyori.adventure.text.Component.text("§aPunto A fijado — click derecho en el punto B"));
            return stack;
        }

        // Second point: validate span + stock, then build the chain.
        PENDING.remove(player.getUniqueId());
        if (first.pos().equals(pos)) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cLos dos puntos no pueden ser el mismo"));
            return stack;
        }
        // No duplicate edge / loop: two anchors can't be joined by two chains at once.
        if (ChainRegistry.existsBetween(world.getUID(), first.pos(), pos)) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cYa existe una cadena entre estos anclajes"));
            return stack;
        }
        // The second anchor may also be a reused one — respect the 4-chain cap there too.
        if (reuse && ChainRegistry.countAt(world.getUID(), pos) >= 4) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cEste anclaje ya tiene 4 cadenas"));
            return stack;
        }
        // Measure the REAL span between the two ATTACH points (block centre + half-block toward the stuck face),
        // not block centre to block centre — else the face offsets (up to +1 block total) leave the rope
        // stretched on creation and the fixed-size link models gap at the ends until you extend it.
        int distance = (int) Math.round(attachDistance(first.pos(), first.offset(), pos, attach));
        distance = Math.max(1, distance);
        if (distance > material.maxBlocks()) {
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§cDemasiado lejos: " + distance + " > máximo " + material.maxBlocks()));
            return stack;
        }
        // The linker is a reusable TOOL — it consumes the chain LINKS (cml:iron_chain) from the player's
        // inventory, one per block, not itself.
        int have = countChainItems(player, material.linkItem());
        if (have < distance) {
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§cNecesitas " + distance + " cadenas (tienes " + have + ")"));
            return stack;
        }

        BlockDefinition def = CraftEngineBlocks.byId(Key.of(material.anchorBlock()));
        if (def == null) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cBloque ancla inválido: " + material.anchorBlock()));
            return stack;
        }
        // Only place a NEW anchor block; a reused endpoint already has its anchor in the world.
        if ((!first.reuse() && !place(world, first.pos(), def)) || (!reuse && !place(world, pos, def))) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cNo se pudo colocar el ancla de la cadena"));
            return stack;
        }

        ChainEngine.create(world, first.pos(), pos, first.offset(), attach, material, distance);
        removeChainItems(player, material.linkItem(), distance);
        player.sendActionBar(net.kyori.adventure.text.Component.text(
                "§aCadena creada (" + distance + " bloques)"));
        return stack;
    }

    /**
     * Whether {@code it} is the configured chain link — matches a CraftEngine custom id (e.g. {@code
     * cml:iron_chain}) OR a plain vanilla item by its namespaced key (e.g. {@code minecraft:chain}), so the
     * link can be either a CE item or a vanilla one depending on config.
     */
    public static boolean isLink(org.bukkit.inventory.ItemStack it, String linkId) {
        if (it == null || it.getType().isAir()) {
            return false;
        }
        Key ce = CraftEngineItems.getCustomItemId(it);
        if (ce != null) {
            return ce.toString().equals(linkId);
        }
        org.bukkit.NamespacedKey mk = it.getType().getKey();
        return mk.toString().equals(linkId);
    }

    /** Total count of the chain-link item across the player's inventory. */
    private static int countChainItems(Player player, String itemId) {
        int total = 0;
        for (org.bukkit.inventory.ItemStack it : player.getInventory().getContents()) {
            if (isLink(it, itemId)) {
                total += it.getAmount();
            }
        }
        return total;
    }

    /** Removes {@code amount} of the chain-link item from the player's inventory (assumes enough present). */
    private static void removeChainItems(Player player, String itemId, int amount) {
        org.bukkit.inventory.ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && amount > 0; i++) {
            org.bukkit.inventory.ItemStack it = contents[i];
            if (!isLink(it, itemId)) {
                continue;
            }
            int take = Math.min(amount, it.getAmount());
            it.setAmount(it.getAmount() - take);
            amount -= take;
        }
        player.updateInventory();
    }

    private static boolean place(World world, BlockPos pos, BlockDefinition def) {
        try {
            ImmutableBlockState state = def.defaultState();
            Location loc = new Location(world, pos.getX(), pos.getY(), pos.getZ());
            return CraftEngineBlocks.place(loc, state, UpdateFlags.UPDATE_ALL, false);
        } catch (Throwable t) {
            return false;
        }
    }

    public static class Factory implements ItemBehaviorFactory<ItemBehavior> {
        @Override
        public ItemBehavior create(Pack pack, Path path, Key id, ConfigSection arguments) {
            return new ChaineryItemBehavior(ChainMaterial.fromConfig(arguments));
        }
    }

    public static final Factory FACTORY = new Factory();
}
