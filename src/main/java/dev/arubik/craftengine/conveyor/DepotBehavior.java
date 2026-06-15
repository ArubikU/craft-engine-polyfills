package dev.arubik.craftengine.conveyor;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Block behavior backing a {@link DepotBlockEntity} ({@code polyfills:depot}).
 *
 * <p>Implements {@link WorldlyContainerHolder} so the engine's hopper bridge calls
 * {@link #getContainer} and talks to the depot's native CraftEngine container — the same
 * one belts, right-click, the comparator and the on-top renderer use. Right-click is a
 * GUI-less put/take.</p>
 */
public class DepotBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior
        implements EntityBlock, WorldlyContainerHolder {

    public static final Key FACTORY_KEY = Key.of("polyfills:depot");
    public static final Factory FACTORY = new Factory();

    public static final int DEFAULT_SIZE = 27;

    private final int size;
    private final String title;
    private int controllerId;

    public DepotBehavior(BlockDefinition block, int size, String title) {
        super(block);
        this.size = Math.max(1, Math.min(size, 54));
        this.title = title != null ? title : "Depot";
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new DepotBlockEntity(blockEntity, size, title);
    }

    /** Resolve the depot controller at a world+pos (shared by all hooks). */
    private static DepotBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            CEWorld world = new BukkitWorld(((ServerLevel) levelObj).getWorld()).storageWorld();
            if (world == null)
                return null;
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && be.controller instanceof DepotBlockEntity d)
                return d;
        } catch (Throwable ignored) {
        }
        return null;
    }

    // ---------------- hopper bridge (WorldlyContainerHolder) ----------------

    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        DepotBlockEntity d = controllerAt(args[1], args[2]);
        if (d == null)
            return null;
        // CE's injected CraftEngineBlock.getContainer casts the result to an NMS
        // WorldlyContainer, so wrap our CE container into the NMS delegating container.
        return net.momirealms.craftengine.bukkit.nms.FastNMS.INSTANCE.createContainer(d.container());
    }

    // ---------------- comparator ----------------

    @Override
    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        DepotBlockEntity d = controllerAt(args[1], args[2]);
        return d == null ? 0 : d.analogSignal();
    }

    @Override
    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    // ---------------- right-click: put / take (no GUI) ----------------

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = ((CraftWorld) ((BukkitWorld) context.getLevel()).platformWorld()).getHandle();
            Object posHandle = LocationUtils.toBlockPos(context.getClickedPos());
            if (!(posHandle instanceof BlockPos))
                return InteractionResult.PASS;
            DepotBlockEntity d = controllerAt(level, posHandle);
            if (d == null)
                return InteractionResult.PASS;
            if (!(context.getPlayer() instanceof BukkitServerPlayer p)
                    || !(p.platformPlayer() instanceof Player player))
                return InteractionResult.PASS;

            org.bukkit.inventory.PlayerInventory inv = player.getInventory();
            org.bukkit.inventory.ItemStack hand = inv.getItemInMainHand();
            boolean handEmpty = hand == null || hand.getType().isAir();

            if (handEmpty) {
                org.bukkit.inventory.ItemStack out = d.takeOne();
                if (out == null)
                    return InteractionResult.PASS;
                for (org.bukkit.inventory.ItemStack left : inv.addItem(out).values())
                    player.getWorld().dropItem(player.getLocation(), left);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
            inv.setItemInMainHand(d.putSome(hand));
            return InteractionResult.SUCCESS_AND_CANCEL;
        } catch (Throwable ignored) {
        }
        return InteractionResult.PASS;
    }

    // ---------------- break: drop + clear ----------------

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos,
            net.minecraft.world.level.block.state.BlockState oldState, Boolean movedByPiston) {
        DepotBlockEntity d = controllerAt(level, nmsPos);
        if (d != null)
            d.dropAll();
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            int size = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("size", DEFAULT_SIZE), "size");
            String title = (String) arguments.getOrDefault("title", "Depot");
            return new DepotBehavior(block, size, title);
        }
    }
}
