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
        return getContainer(level, pos, null, null);
    }

    /**
     * Same resolution as {@link #getContainer(Level, BlockPos)}, but also tells an "on_get_container"
     * script hook (if the target machine declares one) WHO is asking — {@code accessorPos} (e.g. a
     * funnel/hopper/pipe segment's own position) and, if known, which face of the target it's
     * reaching in from. Both are optional context, not a filter: a hook that ignores them (or a
     * plain script Machine.container caller with no accessor at all) behaves exactly as before.
     */
    public static Optional<Container> getContainer(Level level, BlockPos pos,
            BlockPos accessorPos, net.minecraft.core.Direction accessorFace) {
        if (level == null || pos == null || !level.hasChunkAt(pos))
            return Optional.empty();
        BlockState state = level.getBlockState(pos);
        Optional<ImmutableBlockState> custom = BlockStateUtils.getOptionalCustomBlockState(state);
        if (custom.isPresent()) {
            // A machine may redirect what container it exposes here (e.g. a Portable Storage
            // Interface mirroring a linked contraption's own combined container instead of an
            // inventory of its own) via an "on_get_container" script hook — see
            // AbstractMachineBlockEntity#resolveContainerOverride. Checked BEFORE the normal
            // WorldlyContainerHolder/direct-Container resolution below so every caller through this
            // one choke point (funnels, hoppers, pipes, Machine.container) sees the same redirect.
            var pbe = dev.arubik.craftengine.block.entity.PersistentBlockEntity.getIfLoaded(level, pos);
            if (pbe instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine) {
                Optional<Container> override = machine.resolveContainerOverride(accessorPos, accessorFace);
                if (override.isPresent()) return override;
            }
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
