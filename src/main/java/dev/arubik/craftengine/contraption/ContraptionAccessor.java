/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Container
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.arubik.craftengine.contraption;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ContraptionAccessor {
    private ContraptionAccessor() {
    }

    public static float hardnessAt(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getDestroySpeed((BlockGetter)level, pos);
    }

    public static boolean isAir(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }

    public static void breakBlockAndCollect(ServerLevel level, BlockPos pos, List<ItemStack> into) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        into.addAll(Block.getDrops((BlockState)state, (ServerLevel)level, (BlockPos)pos, (BlockEntity)blockEntity));
        level.removeBlock(pos, false);
    }

    public static Container realContainerAt(ServerLevel realLevel, BlockPos realPos) {
        try {
            return HopperBlockEntity.getContainerAt((Level)realLevel, (BlockPos)realPos);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean transferItem(Container from, Direction fromFace, Container to, Direction toFace) {
        if (from == null || to == null) {
            return false;
        }
        for (int sourceSlot : ContraptionAccessor.facesFor(from, fromFace)) {
            ItemStack one;
            int destSlot;
            WorldlyContainer wc;
            ItemStack src = from.getItem(sourceSlot);
            if (src.isEmpty() || from instanceof WorldlyContainer && !(wc = (WorldlyContainer)from).canTakeItemThroughFace(sourceSlot, src, fromFace) || (destSlot = ContraptionAccessor.findInsertSlot(to, toFace, src)) < 0 || (one = from.removeItem(sourceSlot, 1)).isEmpty()) continue;
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

    private static int findInsertSlot(Container to, Direction face, ItemStack src) {
        for (int slot : ContraptionAccessor.facesFor(to, face)) {
            WorldlyContainer wc;
            if (to instanceof WorldlyContainer && !(wc = (WorldlyContainer)to).canPlaceItemThroughFace(slot, src, face) || !to.canPlaceItem(slot, src)) continue;
            ItemStack cur = to.getItem(slot);
            if (cur.isEmpty()) {
                return slot;
            }
            if (!ItemStack.isSameItemSameComponents((ItemStack)cur, (ItemStack)src)) continue;
            int max = Math.min(cur.getMaxStackSize(), to.getMaxStackSize());
            if (cur.getCount() >= max) continue;
            return slot;
        }
        return -1;
    }

    private static int[] facesFor(Container container, Direction face) {
        if (container instanceof WorldlyContainer) {
            WorldlyContainer wc = (WorldlyContainer)container;
            return wc.getSlotsForFace(face);
        }
        int[] all = new int[container.getContainerSize()];
        for (int i = 0; i < all.length; ++i) {
            all[i] = i;
        }
        return all;
    }
}

