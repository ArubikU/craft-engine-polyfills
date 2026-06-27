package dev.arubik.craftengine.block.behavior;

import java.util.Map;

import dev.arubik.craftengine.block.entity.StorageBlockEntity;
import dev.arubik.craftengine.util.SoundMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
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
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Storage block — backed by {@link StorageBlockEntity} using CraftEngine's NATIVE block-entity NBT
 * storage (the same system as the custom crafter), replacing the old {@code BlockContainer} which used
 * the Bukkit PDC chunk store. Opens a vanilla {@link ChestMenu} and exposes a {@link Container} bridge
 * for hoppers/comparators. (Piston-move support is added via a piston listener.)
 */
public class StorageBlockBehavior extends BukkitBlockBehavior implements EntityBlock, WorldlyContainerHolder {

    public static final Factory FACTORY = new Factory();

    private final int size;
    private final int maxStack;
    private final String title;
    private final SoundMap soundMap;

    public StorageBlockBehavior(BlockDefinition customBlock, int size, int maxStack, String title, SoundMap soundMap) {
        super(customBlock);
        this.size = Math.max(9, Math.min(size, 54));
        this.maxStack = Math.max(1, Math.min(maxStack, 99));
        this.title = (title != null) ? title : "Storage";
        this.soundMap = soundMap;
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        StorageBlockEntity be = new StorageBlockEntity(blockEntity);
        be.configure(this.size, this.maxStack);
        return be;
    }

    private static StorageBlockEntity at(Level level, BlockPos pos) {
        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
        return be != null && be.controller instanceof StorageBlockEntity s ? s : null;
    }

    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        try {
            StorageBlockEntity s = at((Level) args[1], (BlockPos) args[2]);
            if (s instanceof Container)
                return s;
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        StorageBlockEntity s = at((Level) args[1], (BlockPos) args[2]);
        return s == null ? 0 : s.getRedstoneSignal();
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = (ServerLevel) ((BukkitWorld) context.getLevel()).minecraftWorld();
            BlockPos pos = new BlockPos(context.getClickedPos().x(), context.getClickedPos().y(),
                    context.getClickedPos().z());
            StorageBlockEntity be = at(level, pos);
            if (be == null)
                return InteractionResult.PASS;
            int rows = Math.max(1, Math.min(be.getContainerSize() / 9, 6));
            MenuType<ChestMenu> type = switch (rows) {
                case 1 -> MenuType.GENERIC_9x1;
                case 2 -> MenuType.GENERIC_9x2;
                case 4 -> MenuType.GENERIC_9x4;
                case 5 -> MenuType.GENERIC_9x5;
                case 6 -> MenuType.GENERIC_9x6;
                default -> MenuType.GENERIC_9x3;
            };
            net.minecraft.server.level.ServerPlayer sp = (net.minecraft.server.level.ServerPlayer) context.getPlayer()
                    .serverPlayer();
            MenuProvider provider = new SimpleMenuProvider(
                    (id, inv, p) -> new ChestMenu(type, id, inv, be, rows),
                    net.minecraft.network.chat.Component.literal(this.title));
            sp.openMenu(provider);
            return InteractionResult.SUCCESS_AND_CANCEL;
        } catch (Throwable t) {
            return InteractionResult.PASS;
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            int size = (int) arguments.getOrDefault("size", 27);
            int maxStack = (int) arguments.getOrDefault("max-stack-size", 99);
            String title = (String) arguments.getOrDefault("title", "Storage");
            Object soundMapObj = arguments.get("sound-map");
            SoundMap soundMap = (soundMapObj instanceof Map) ? SoundMap.fromMap((Map<String, Object>) soundMapObj) : null;
            return new StorageBlockBehavior(block, size, maxStack, title, soundMap);
        }
    }
}
