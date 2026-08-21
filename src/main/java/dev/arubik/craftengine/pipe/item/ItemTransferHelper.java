package dev.arubik.craftengine.pipe.item;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder;

/**
 * Finds "any storage block holder" at a position — a custom machine/storage block exposing
 * {@link WorldlyContainerHolder} (CraftEngine's own hopper-compat hook, already implemented by
 * {@code MachineBlockBehavior}), or a vanilla container (chest, barrel, hopper, furnace...). This
 * is how the item pipe (v1) connects to arbitrary storage without a new capability interface —
 * items already have one: vanilla {@link Container}.
 */
public final class ItemTransferHelper {

    private ItemTransferHelper() {
    }

    public static Optional<Container> getContainer(Level level, BlockPos pos) {
        if (level == null || pos == null || !level.hasChunkAt(pos))
            return Optional.empty();
        BlockState state = level.getBlockState(pos);
        Optional<ImmutableBlockState> custom = BlockStateUtils.getOptionalCustomBlockState(state);
        if (custom.isPresent()) {
            BlockBehavior beh = custom.get().behavior();
            WorldlyContainerHolder holder = beh instanceof WorldlyContainerHolder w ? w
                    : (beh == null ? null : beh.getFirst(WorldlyContainerHolder.class));
            if (holder != null) {
                Object result = holder.getContainer(beh, new Object[] { state, level, pos });
                if (result instanceof Container c)
                    return Optional.of(c);
            }
            return Optional.empty(); // a custom block with no container capability isn't a storage holder
        }
        var be = level.getBlockEntity(pos);
        if (be instanceof Container c)
            return Optional.of(c);
        return Optional.empty();
    }
}
