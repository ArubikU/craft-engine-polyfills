package dev.arubik.craftengine.contraption.element.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class ContraptionHangingSignElement extends ContraptionSignElement {

    private final boolean isCeiling;

    public ContraptionHangingSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag,
                                          boolean isCeiling) {
        super(localPos, blockState, beTag, isCeiling ? rotation16(blockState) * 22.5f : 0f);
        this.isCeiling = isCeiling;
    }

    @Override
    protected float getTextEntityYaw(float contraptionYaw, boolean back) {
        float base = isCeiling
                ? rotation16(blockState()) * 22.5f
                : blockState().getValue(WallHangingSignBlock.FACING).toYRot();
        return base + (back ? 180f : 0f) + contraptionYaw;
    }

    @Override
    protected Direction getFacing() {
        if (!isCeiling) return blockState().getValue(WallHangingSignBlock.FACING);
        return Direction.fromYRot(rotation16(blockState()) * 22.5f);
    }

    @Override
    protected float textYCenter() {
        // Hanging sign board: chains at top, board at Y 0-12/16, text center ≈ 6/16 = 0.375
        return 0.375f;
    }

    @Override
    protected double textOutwardOffset(boolean back) {
        // Hanging sign board: same 2-pixel thickness, front at 9/16, back at 7/16 from facing center
        return back ? (7.0/16.0 - 8.0/16.0 - 0.005) : (9.0/16.0 - 8.0/16.0);
    }

    private static int rotation16(BlockState bs) {
        return bs.hasProperty(BlockStateProperties.ROTATION_16)
                ? bs.getValue(BlockStateProperties.ROTATION_16) : 0;
    }
}
