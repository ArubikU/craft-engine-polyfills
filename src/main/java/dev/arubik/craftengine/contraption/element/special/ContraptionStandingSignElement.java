package dev.arubik.craftengine.contraption.element.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class ContraptionStandingSignElement extends ContraptionSignElement {

    public ContraptionStandingSignElement(BlockPos localPos, BlockState blockState, CompoundTag beTag) {
        super(localPos, blockState, beTag, rotation16(blockState) * 22.5f);
    }

    @Override
    protected float getTextEntityYaw(float contraptionYaw, boolean back) {
        float base = rotation16(blockState()) * 22.5f;
        return base + (back ? 180f : 0f) + contraptionYaw;
    }

    @Override
    protected Direction getFacing() {
        return Direction.fromYRot(rotation16(blockState()) * 22.5f);
    }

    @Override
    protected double textOutwardOffset(boolean back) {
        // Standing sign board: Z 7-9/16 centered at block center, text at 9/16 front, 7/16 back
        return back ? (7.0/16.0 - 8.0/16.0 - 0.005) : (9.0/16.0 - 8.0/16.0);
    }

    private static int rotation16(BlockState bs) {
        return bs.hasProperty(BlockStateProperties.ROTATION_16)
                ? bs.getValue(BlockStateProperties.ROTATION_16) : 0;
    }
}
