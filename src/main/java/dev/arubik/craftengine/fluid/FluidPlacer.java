/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LayeredCauldronBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluids
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;

public class FluidPlacer {
    public static boolean place(FluidStack stack, BlockPos pos, Level level) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        FluidType t = stack.getType();
        BlockState cs = level.getBlockState(pos);
        if (t == FluidType.WATER && (cs.is(Blocks.CAULDRON) || cs.is(Blocks.WATER_CAULDRON))) {
            int cur;
            int levelMb = Math.max(1, FluidType.WATER.mbPerFullBlock() / 3);
            int n = cur = cs.is(Blocks.WATER_CAULDRON) ? (Integer)cs.getValue((Property)LayeredCauldronBlock.LEVEL) : 0;
            if (cur >= 3) {
                return false;
            }
            int addLevels = Math.min(3 - cur, stack.getAmount() / levelMb);
            if (addLevels <= 0) {
                return false;
            }
            level.setBlock(pos, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue((Property)LayeredCauldronBlock.LEVEL, (Comparable)Integer.valueOf(cur + addLevels)), 3);
            stack.removeAmount(addLevels * levelMb);
            return true;
        }
        if (t == FluidType.LAVA && cs.is(Blocks.CAULDRON)) {
            int full = FluidType.LAVA.mbPerFullBlock();
            if (stack.getAmount() < full) {
                return false;
            }
            level.setBlock(pos, Blocks.LAVA_CAULDRON.defaultBlockState(), 3);
            stack.removeAmount(full);
            return true;
        }
        int needed = t.mbPerFullBlock();
        if (needed <= 0 || stack.getAmount() < needed) {
            return false;
        }
        if (!level.getFluidState(pos).isEmpty() || !level.getBlockState(pos).isAir()) {
            return false;
        }
        if (t == FluidType.WATER || t == FluidType.LAVA) {
            if (t == FluidType.WATER && level.dimensionType().hasCeiling()) {
                return false;
            }
            FlowingFluid f = t == FluidType.WATER ? Fluids.WATER : Fluids.LAVA;
            level.setBlock(pos, f.defaultFluidState().createLegacyBlock(), 3);
        } else if (t == FluidType.SLIME) {
            level.setBlock(pos, Blocks.SLIME_BLOCK.defaultBlockState(), 3);
        } else if (t == FluidType.HONEY) {
            level.setBlock(pos, Blocks.HONEY_BLOCK.defaultBlockState(), 3);
        } else if (t == FluidType.POWDER_SNOW) {
            level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
        } else {
            return false;
        }
        stack.removeAmount(needed);
        return true;
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level, int radius) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        FluidType t = stack.getType();
        int perBlock = t.mbPerFullBlock();
        if (perBlock <= 0) {
            return false;
        }
        int maxBlocks = stack.getAmount() / perBlock;
        if (maxBlocks <= 0) {
            return false;
        }
        boolean placed = false;
        int blocksPlaced = 0;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    if (blocksPlaced >= maxBlocks) {
                        return placed;
                    }
                    BlockPos p = pos.offset(dx, dy, dz);
                    if (!level.getFluidState(p).isEmpty() || !level.getBlockState(p).isAir()) continue;
                    if (t == FluidType.WATER || t == FluidType.LAVA) {
                        FlowingFluid f = t == FluidType.WATER ? Fluids.WATER : Fluids.LAVA;
                        level.setBlock(p, f.defaultFluidState().createLegacyBlock(), 3);
                    } else if (t == FluidType.SLIME) {
                        level.setBlock(p, Blocks.SLIME_BLOCK.defaultBlockState(), 3);
                    } else {
                        if (t != FluidType.POWDER_SNOW) continue;
                        level.setBlock(p, Blocks.POWDER_SNOW.defaultBlockState(), 3);
                    }
                    ++blocksPlaced;
                    placed = true;
                }
            }
        }
        stack.removeAmount(blocksPlaced * perBlock);
        return placed;
    }

    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        int needed;
        if (stack == null || stack.isEmpty() || level.isClientSide()) {
            return 0;
        }
        if (!level.getBlockState(pos).isAir()) {
            return 0;
        }
        FluidType type = stack.getType();
        int amount = stack.getAmount();
        if (type == FluidType.EXPERIENCE) {
            int xpValue;
            int consumed;
            int xpMb;
            for (consumed = 0; consumed < amount && consumed < 250 && consumed + (xpMb = (xpValue = 1 + FluidType.RANDOM.nextInt(50)) * FluidType.EXPERIENCE.unitMb()) <= amount; consumed += xpMb) {
                FluidType.spawnXpOrb(level, pos, xpValue);
            }
            return consumed;
        }
        if ((type == FluidType.WATER || type == FluidType.LAVA || type == FluidType.SLIME || type == FluidType.POWDER_SNOW) && amount >= (needed = type.mbPerFullBlock()) && FluidPlacer.place(stack, pos, level)) {
            return needed;
        }
        return 0;
    }
}

