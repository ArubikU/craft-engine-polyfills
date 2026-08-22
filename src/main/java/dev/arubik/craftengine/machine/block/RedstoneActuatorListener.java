package dev.arubik.craftengine.machine.block;

import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import net.minecraft.server.level.ServerPlayer;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Fires a machine's {@code on_redstone_actuator} script when a player physically triggers a
 * vanilla pressure plate, lever, or button adjacent to that machine — unlike a command block's
 * {@code @p} nearest-player guess, Bukkit already hands us the exact triggering player, so that's
 * bound as "Player" in the script the same way on_interact does.
 */
public final class RedstoneActuatorListener implements Listener {

    private static final BlockFace[] NEIGHBORS = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!isActuator(event.getAction(), block)) return;

        CEWorld world;
        try {
            world = CeWorlds.of(block.getWorld()).storageWorld();
        } catch (Throwable t) {
            return;
        }
        if (world == null) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        for (BlockFace face : NEIGHBORS) {
            Block neighbor = block.getRelative(face);
            BlockPos cePos = new BlockPos(neighbor.getX(), neighbor.getY(), neighbor.getZ());
            BlockEntity be = world.getBlockEntityAtIfLoaded(cePos);
            if (be == null) continue;
            BlockEntityController controller = be.controller;
            if (!(controller instanceof DataMachineBlockEntity dm)) continue;
            if (dm.definition() == null || dm.definition().redstoneActuatorScript() == null) continue;
            dm.runInteractScript(dm.definition().redstoneActuatorScript(), nmsPlayer);
        }
    }

    private static boolean isActuator(Action action, Block block) {
        String name = block.getType().name();
        if (action == Action.PHYSICAL) return name.endsWith("_PRESSURE_PLATE") || name.equals("PRESSURE_PLATE");
        if (action == Action.RIGHT_CLICK_BLOCK) return name.contains("LEVER") || name.endsWith("_BUTTON");
        return false;
    }
}
