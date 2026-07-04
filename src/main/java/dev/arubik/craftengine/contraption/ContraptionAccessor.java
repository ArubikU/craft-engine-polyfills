package dev.arubik.craftengine.contraption;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The ONLY class allowed to touch the real Overworld on behalf of a contraption
 * (CONTRAPTIONS.md §1 "World interaction boundary") — always synchronously, main-thread
 * only. An earlier idea (simulate on a background thread, queue world mutations back to
 * main thread) was explicitly rejected: Bukkit/Paper's thread-safety model makes async
 * world mutation a non-starter.
 */
public final class ContraptionAccessor {

    private ContraptionAccessor() {
    }

    /** Vanilla mining hardness (seconds-ish unit vanilla itself uses for break speed) at a world position. */
    public static float hardnessAt(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getDestroySpeed(level, pos);
    }

    public static boolean isAir(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }

    /**
     * Breaks the real block at {@code pos}, collecting its drops into {@code into} instead
     * of spawning real dropped-item entities — "drops go straight into the contraption's
     * own virtual inventory — never spawned as real dropped-item entities" (CONTRAPTIONS.md
     * §1 "Miners specifically"). No tool/fortune context: the miner isn't a player holding
     * an item, so this uses the no-tool {@code Block.getDrops} overload (vanilla's own
     * default-drop-table path, same as a bare-hand-equivalent break).
     */
    public static void breakBlockAndCollect(ServerLevel level, BlockPos pos, List<ItemStack> into) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        into.addAll(Block.getDrops(state, level, pos, blockEntity));
        level.removeBlock(pos, false);
    }
}
