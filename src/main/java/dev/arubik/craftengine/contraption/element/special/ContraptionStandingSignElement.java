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

    private static int rotation16(BlockState bs) {
        return bs.hasProperty(BlockStateProperties.ROTATION_16)
                ? bs.getValue(BlockStateProperties.ROTATION_16) : 0;
    }
}
