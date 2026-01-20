package dev.arubik.craftengine.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FluidPlacer {

    public static boolean place(FluidStack stack, BlockPos pos, Level level) {
        if (stack == null || stack.isEmpty())
            return false;
        FluidType t = stack.getType();
        int needed = t.mbPerFullBlock();
        if (needed <= 0 || stack.getAmount() < needed)
            return false;
        if (!level.getFluidState(pos).isEmpty() || !level.getBlockState(pos).isAir())
            return false;
        if (t == FluidType.WATER || t == FluidType.LAVA) {
            if (t == FluidType.WATER && level.dimensionType().hasCeiling())
                return false;
            var f = (t == FluidType.WATER) ? net.minecraft.world.level.material.Fluids.WATER
                    : net.minecraft.world.level.material.Fluids.LAVA;
            level.setBlock(pos, f.defaultFluidState().createLegacyBlock(), 3);
        } else if (t == FluidType.SLIME) {
            level.setBlock(pos, Blocks.SLIME_BLOCK.defaultBlockState(), 3);
        } else if (t == FluidType.HONEY) {
            level.setBlock(pos, Blocks.HONEY_BLOCK.defaultBlockState(), 3);
        } else if (t == FluidType.POWDER_SNOW) {
            level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 3);
        } else {
            return false; // EXPERIENCE and MILK not placeable
        }
        stack.removeAmount(needed);
        return true;
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level, int radius) {
        if (stack == null || stack.isEmpty())
            return false;
        FluidType t = stack.getType();
        int perBlock = t.mbPerFullBlock();
        if (perBlock <= 0)
            return false;
        int maxBlocks = stack.getAmount() / perBlock;
        if (maxBlocks <= 0)
            return false;
        boolean placed = false;
        int blocksPlaced = 0;
        for (int dx = -radius; dx <= radius; dx++)
            for (int dy = -radius; dy <= radius; dy++)
                for (int dz = -radius; dz <= radius; dz++) {
                    if (blocksPlaced >= maxBlocks)
                        return placed;
                    BlockPos p = pos.offset(dx, dy, dz);
                    if (!level.getFluidState(p).isEmpty() || !level.getBlockState(p).isAir())
                        continue;
                    if (t == FluidType.WATER || t == FluidType.LAVA) {
                        var f = (t == FluidType.WATER) ? net.minecraft.world.level.material.Fluids.WATER
                                : net.minecraft.world.level.material.Fluids.LAVA;
                        level.setBlock(p, f.defaultFluidState().createLegacyBlock(), 3);
                    } else if (t == FluidType.SLIME) {
                        level.setBlock(p, Blocks.SLIME_BLOCK.defaultBlockState(), 3);
                    } else if (t == FluidType.POWDER_SNOW) {
                        level.setBlock(p, Blocks.POWDER_SNOW.defaultBlockState(), 3);
                    } else
                        continue;
                    blocksPlaced++;
                    placed = true;
                }
        stack.removeAmount(blocksPlaced * perBlock);
        return placed;
    }

    /**
     * Intenta dispersar un fluido en un bloque de aire de manera inteligente.
     * Para XP: genera partículas de experiencia
     * Para líquidos colocables: intenta colocar el bloque
     * 
     * @param stack El fluido a dispersar
     * @param pos   La posición del bloque de aire
     * @param level El mundo
     * @return La cantidad de fluido consumido
     */
    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        if (stack == null || stack.isEmpty() || level.isClientSide())
            return 0;
        if (!level.getBlockState(pos).isAir())
            return 0;

        FluidType type = stack.getType();
        int amount = stack.getAmount();

        if (type == FluidType.EXPERIENCE) {
            // drop max 7 orbs of 1-20 XP each, consuming fluid
            int consumed = 0;
            while (consumed < amount && consumed < 250) { // limit max 100mb per call to avoid long loops
                int xpValue = 1 + FluidType.RANDOM.nextInt(50);
                int xpMb = xpValue * FluidType.EXPERIENCE.unitMb();
                if (consumed + xpMb > amount)
                    break; // no cabe más
                FluidType.spawnXpOrb(level, pos, xpValue);
                consumed += xpMb;
            }
            return consumed;
        } else if (type == FluidType.WATER || type == FluidType.LAVA || type == FluidType.SLIME
                || type == FluidType.POWDER_SNOW) {
            // Intentar colocar bloque si hay suficiente fluido
            int needed = type.mbPerFullBlock();
            if (amount >= needed) {
                if (place(stack, pos, level)) {
                    return needed;
                }
            }
        }

        return 0;
    }
}
