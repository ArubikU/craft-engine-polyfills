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

    private record Pending(UUID worldId, BlockPos pos) {
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

        // Anchor cell = the air just off the clicked face (like hanging a chain off a block).
        Block target = clicked.getRelative(face);
        if (!target.getType().isAir() && !target.isReplaceable()) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cNo hay espacio para anclar la cadena aquí"));
            return stack;
        }
        World world = target.getWorld();
        BlockPos pos = new BlockPos(target.getX(), target.getY(), target.getZ());

        if (player.isSneaking()) {
            PENDING.remove(player.getUniqueId());
            player.sendActionBar(net.kyori.adventure.text.Component.text("§7Selección de cadena cancelada"));
            return stack;
        }

        Pending first = PENDING.get(player.getUniqueId());
        if (first == null || !first.worldId().equals(world.getUID())) {
            PENDING.put(player.getUniqueId(), new Pending(world.getUID(), pos));
            player.sendActionBar(net.kyori.adventure.text.Component.text("§aPunto A fijado — click derecho en el punto B"));
            return stack;
        }

        // Second point: validate span + stock, then build the chain.
        PENDING.remove(player.getUniqueId());
        if (first.pos().equals(pos)) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cLos dos puntos no pueden ser el mismo"));
            return stack;
        }
        int distance = (int) Math.round(Math.sqrt(first.pos().distSqr(pos)));
        distance = Math.max(1, distance);
        if (distance > material.maxBlocks()) {
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§cDemasiado lejos: " + distance + " > máximo " + material.maxBlocks()));
            return stack;
        }
        if (stack.getCount() < distance) {
            player.sendActionBar(net.kyori.adventure.text.Component.text(
                    "§cNecesitas " + distance + " cadenas (tienes " + stack.getCount() + ")"));
            return stack;
        }

        BlockDefinition def = CraftEngineBlocks.byId(Key.of(material.blockId()));
        if (def == null) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cBloque de cadena inválido: " + material.blockId()));
            return stack;
        }
        if (!place(world, first.pos(), def) || !place(world, pos, def)) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cNo se pudo colocar el bloque de cadena"));
            return stack;
        }

        ChainEngine.create(world, first.pos(), pos, material, distance);
        stack.shrink(distance);
        player.sendActionBar(net.kyori.adventure.text.Component.text(
                "§aCadena creada (" + distance + " bloques)"));
        return stack;
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
