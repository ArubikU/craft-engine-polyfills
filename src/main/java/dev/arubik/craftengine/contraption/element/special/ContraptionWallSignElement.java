package dev.arubik.craftengine.contraption.element.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ContraptionWallSignElement extends ContraptionSignElement {

    public ContraptionWallSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, 0f);
    }

    @Override
    protected float getTextEntityYaw(float contraptionYaw, boolean back) {
        float base = blockState().getValue(WallSignBlock.FACING).toYRot();
        return base + (back ? 180f : 0f) + contraptionYaw;
    }

    @Override
    protected Direction getFacing() {
        return blockState().getValue(WallSignBlock.FACING);
    }
}
