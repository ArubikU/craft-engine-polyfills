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

    @Override
    protected double textOutwardOffset(boolean back) {
        // Wall sign board: Z 0-2/16 against wall, text front at 2/16, back at 0/16
        // From block center 8/16: front = 2/16 - 8/16 = -6/16, back = 0 - 8/16 = -8/16 + epsilon
        return back ? (-8.0/16.0 + 0.005) : (-6.0/16.0);
    }
}
