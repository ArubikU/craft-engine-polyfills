package dev.arubik.craftengine.contraption;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
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

    // ---- item I/O across the contraption/real-world boundary (roadmap item #6) ----
    //
    // A captured hopper ticks inside the hidden ContraptionLevel; the real chest it should feed
    // lives in realLevel. Bridging one item between the two independently-ticking levels is a real
    // world interaction on the contraption's behalf, so — per this class's contract (CONTRAPTIONS.md
    // §1 "World interaction boundary") — the two primitives that actually read/mutate the real world
    // (resolve a real container, move one item) live HERE, main-thread only. ContraptionHopperBridge
    // computes WHICH real cell to hit (via ContraptionWorlds.realBlockNeighbor) and calls down into
    // these; it never touches the real world itself.

    /**
     * The vanilla-resolved {@link Container} at real-world {@code realPos}, or {@code null} if that
     * cell holds no container. Delegates to {@link HopperBlockEntity#getContainerAt} — the single
     * choke point vanilla's own hopper suck/eject routes through — so this picks up plain vanilla
     * containers (chest, barrel, double chest), worldly ones, and CraftEngine custom storages that
     * expose a {@code WorldlyContainerHolder} (e.g. the depot), exactly matching
     * {@code FunnelBlockEntity#containerAt}'s resolution. Fail-open: any lookup error yields
     * {@code null} (treated as "nothing adjacent"), never an exception into the tick loop.
     */
    public static Container realContainerAt(ServerLevel realLevel, BlockPos realPos) {
        try {
            return HopperBlockEntity.getContainerAt(realLevel, realPos);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * <b>Dupe-safe single-item move</b> between two {@link Container}s — the primitive the hopper I/O
     * bridge uses to shuttle one item between a captured hopper (in the mini-dimension) and a real
     * chest (in {@code realLevel}), in either direction. Moves at most ONE item and returns whether it
     * did.
     *
     * <p><b>Why it cannot dupe or void.</b> Rather than remove-then-insert-with-rollback (which briefly
     * holds an item outside any container and risks losing it if the insert throws), this
     * <em>peeks</em> first: it finds a source slot holding a movable item AND a destination slot that
     * can accept one of it BEFORE mutating anything. Only once both are known feasible does it
     * {@link Container#removeItem remove} exactly one from the source and place it into the pre-chosen
     * destination slot. Because both levels tick on the same main thread and nothing runs between the
     * peek and the two mutations, the peeked feasibility still holds at mutation time — so the item is
     * never held in limbo, never duplicated (it leaves the source in the same synchronous step it
     * enters the destination), and never voided (the destination slot was confirmed to have room).
     *
     * <p>{@code fromFace}/{@code toFace} are the real-world faces the item leaves the source / enters
     * the destination through, honoured for {@link WorldlyContainer}s exactly as vanilla's hopper does
     * ({@code getSlotsForFace} + {@code canTakeItemThroughFace}/{@code canPlaceItemThroughFace}); a
     * plain {@link Container} ignores them (all slots eligible). Main-thread only.
     */
    public static boolean transferItem(Container from, Direction fromFace, Container to, Direction toFace) {
        if (from == null || to == null) {
            return false;
        }
        for (int sourceSlot : facesFor(from, fromFace)) {
            ItemStack src = from.getItem(sourceSlot);
            if (src.isEmpty()) {
                continue;
            }
            if (from instanceof WorldlyContainer wc && !wc.canTakeItemThroughFace(sourceSlot, src, fromFace)) {
                continue;
            }
            int destSlot = findInsertSlot(to, toFace, src);
            if (destSlot < 0) {
                continue; // this item can't land anywhere in `to` — try the next source slot
            }
            ItemStack one = from.removeItem(sourceSlot, 1);
            if (one.isEmpty()) {
                continue; // defensive: nothing actually came out (should not happen on main thread)
            }
            ItemStack cur = to.getItem(destSlot);
            if (cur.isEmpty()) {
                to.setItem(destSlot, one);
            } else {
                cur.grow(one.getCount());
            }
            from.setChanged();
            to.setChanged();
            return true;
        }
        return false;
    }

    /**
     * First slot of {@code to} (respecting {@code face} for a {@link WorldlyContainer}) that can accept
     * one more of {@code src} — an empty valid slot, or a matching partial stack below its max — or
     * {@code -1} if none. Peek only: mutates nothing (see {@link #transferItem}'s dupe-safety note).
     */
    private static int findInsertSlot(Container to, Direction face, ItemStack src) {
        for (int slot : facesFor(to, face)) {
            if (to instanceof WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, src, face)) {
                continue;
            }
            if (!to.canPlaceItem(slot, src)) {
                continue;
            }
            ItemStack cur = to.getItem(slot);
            if (cur.isEmpty()) {
                return slot;
            }
            if (ItemStack.isSameItemSameComponents(cur, src)) {
                int max = Math.min(cur.getMaxStackSize(), to.getMaxStackSize());
                if (cur.getCount() < max) {
                    return slot;
                }
            }
        }
        return -1;
    }

    /** Slots reachable through {@code face} (worldly-aware), or every slot for a plain container — mirrors {@code FunnelBlockEntity#facesFor}. */
    private static int[] facesFor(Container container, Direction face) {
        if (container instanceof WorldlyContainer wc) {
            return wc.getSlotsForFace(face);
        }
        int[] all = new int[container.getContainerSize()];
        for (int i = 0; i < all.length; i++) {
            all[i] = i;
        }
        return all;
    }
}
