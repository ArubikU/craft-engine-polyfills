package dev.arubik.craftengine.fluid;

import java.util.List;

import dev.arubik.craftengine.fluid.behavior.PumpBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class FluidCollector {

    /**
     * Delay específico para recolectar desde bloques (permite diferenciar de I/O)
     */
    public static int blockCollectDelay(FluidType t) {
        if (t == null)
            return 1;
        switch (t) {
            case LAVA:
                return 12;
            case SLIME:
                return 10;
            case HONEY:
                return 8;
            case POWDER_SNOW:
                return 6;
            case WATER:
                return 4;
            case MILK:
                return 2;
            case EXPERIENCE:
                return 1;
            case EMPTY:
            default:
                return 1;
        }
    }

    /**
     * Recolecta en un solo bloque hasta maxMb, sin bucles infinitos
     */
    public static FluidStack collectAt(BlockPos pos, Level level, int maxMb, FluidType preferred) {
        if (level == null || pos == null || maxMb <= 0)
            return new FluidStack(FluidType.EMPTY, 0, 0);
        FluidState fs = level.getFluidState(pos);
        if (!fs.isEmpty()) {
            FluidType t = null;
            if (fs.is(net.minecraft.world.level.material.Fluids.WATER)
                    || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER))
                t = FluidType.WATER;
            else if (fs.is(net.minecraft.world.level.material.Fluids.LAVA)
                    || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA))
                t = FluidType.LAVA;
            // Respetar preferencia: si hay preferido y no coincide, no recolectar ni
            // eliminar
            if (t != null && preferred != null && preferred != FluidType.EMPTY && t != preferred) {
                return new FluidStack(FluidType.EMPTY, 0, 0);
            }
            if (t != null) {
                int full = t.mbPerFullBlock();
                if (fs.isSource()) {
                    if (full > maxMb)
                        return new FluidStack(FluidType.EMPTY, 0, 0);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return new FluidStack(t, full, 0);
                } else {
                    int amount = (full * fs.getAmount()) / 8; // proporcional a nivel (1..7)
                    if (amount > maxMb)
                        amount = maxMb;
                    if (amount <= 0)
                        return new FluidStack(FluidType.EMPTY, 0, 0);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return new FluidStack(t, amount, 0);
                }
            }
        } else {
            // Bloques materiales
            if (level.getBlockState(pos).getBlock() instanceof SlimeBlock) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.SLIME)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                int full = FluidType.SLIME.mbPerFullBlock();
                if (full > maxMb)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.SLIME, full, 0);
            }
            if (level.getBlockState(pos).is(Blocks.HONEY_BLOCK)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.HONEY)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                int full = FluidType.HONEY.mbPerFullBlock();
                if (full > maxMb)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.HONEY, full, 0);
            }
            if (level.getBlockState(pos).is(Blocks.POWDER_SNOW)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.POWDER_SNOW)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                int full = FluidType.POWDER_SNOW.mbPerFullBlock();
                if (full > maxMb)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.POWDER_SNOW, full, 0);
            }
            // Orbes de experiencia en el bloque
            List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos));
            if (!orbs.isEmpty()) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.EXPERIENCE)
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                int stored = 0;
                int capacity = maxMb;
                for (var orb : orbs) {
                    if (stored >= capacity)
                        break;
                    int value = orb.getValue();
                    int mb = value * FluidType.EXPERIENCE.unitMb();
                    int remaining = capacity - stored;
                    if (mb <= remaining) {
                        stored += mb;
                        orb.discard();
                    } else {
                        int maxXpFit = remaining / FluidType.EXPERIENCE.unitMb();
                        if (maxXpFit > 0) {
                            stored += maxXpFit * FluidType.EXPERIENCE.unitMb();
                            orb.setValue(value - maxXpFit);
                        }
                        break;
                    }
                }
                if (stored > 0)
                    return new FluidStack(FluidType.EXPERIENCE, stored, 0);
            }
        }
        return new FluidStack(FluidType.EMPTY, 0, 0);
    }

    public static FluidType getFluidTypeAt(BlockPos pos, Level level) {
        FluidState fs = level.getFluidState(pos);
        if (!fs.isEmpty()) {
            if (fs.is(net.minecraft.world.level.material.Fluids.WATER)
                    || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER))
                return FluidType.WATER;
            if (fs.is(net.minecraft.world.level.material.Fluids.LAVA)
                    || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA))
                return FluidType.LAVA;
        } else {
            if (level.getBlockState(pos).getBlock() instanceof SlimeBlock)
                return FluidType.SLIME;
            if (level.getBlockState(pos).is(Blocks.POWDER_SNOW))
                return FluidType.POWDER_SNOW;
            var orbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos));
            if (!orbs.isEmpty())
                return FluidType.EXPERIENCE;
            if (level.getBlockState(pos).is(Blocks.HONEY_BLOCK))
                return FluidType.HONEY;
        }
        return FluidType.EMPTY;
    }

    /**
     * Recolecta en un área hasta maxMb, respetando el tipo base del bloque inicial
     */
    public static FluidStack collectArea(BlockPos pos, Level level, int radius, int maxMb, FluidType preferred) {
        if (level == null || pos == null || radius < 0 || maxMb <= 0)
            return new FluidStack(FluidType.EMPTY, 0, 0);
        FluidType base = getFluidTypeAt(pos, level);
        if (base == FluidType.EMPTY)
            return new FluidStack(FluidType.EMPTY, 0, 0);
        // Si existe preferencia no vacía, recolectar SOLO ese tipo; si no, recolectar
        // el tipo base
        FluidType targetType = (preferred != null && preferred != FluidType.EMPTY) ? preferred : base;
        FluidStack collected = new FluidStack(targetType, 0, 0);
        int remaining = maxMb;

        // Limite de iteraciones para evitar lag (max 27 bloques per tick)
        int maxIterations = 27;
        int iterations = 0;

        outerLoop: for (int dx = -radius; dx <= radius && remaining > 0; dx++)
            for (int dy = -radius; dy <= radius && remaining > 0; dy++)
                for (int dz = -radius; dz <= radius && remaining > 0; dz++) {
                    if (iterations++ >= maxIterations)
                        break outerLoop; // Evitar lag

                    BlockPos p = pos.offset(dx, dy, dz);

                    // si el bloque encima es un carrier, no recolectar
                    ImmutableBlockState ibs = BlockStateUtils
                            .getOptionalCustomBlockState(level.getBlockState(p.offset(0, 1, 0))).orElse(null);
                    if (ibs != null && ibs.behavior().getAs(PumpBehavior.class).isPresent())
                        continue;

                    // Evitar quitar el bloque central dos veces cuando sea flowing que refluye
                    if (p.equals(pos))
                        continue;

                    if (targetType == FluidType.WATER || targetType == FluidType.LAVA) {
                        FluidState st = level.getFluidState(p);
                        if (!st.isEmpty() && st.isSource() && FluidType.matches(targetType, st)) {
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) {
                                collected.addAmount(got.getAmount());
                                remaining -= got.getAmount();
                            }
                        }
                    } else if (targetType == FluidType.SLIME) {
                        if (level.getBlockState(p).getBlock() instanceof SlimeBlock) {
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) {
                                collected.addAmount(got.getAmount());
                                remaining -= got.getAmount();
                            }
                        }
                    } else if (targetType == FluidType.HONEY) {
                        if (level.getBlockState(p).is(Blocks.HONEY_BLOCK)) {
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) {
                                collected.addAmount(got.getAmount());
                                remaining -= got.getAmount();
                            }
                        }
                    } else if (targetType == FluidType.POWDER_SNOW) {
                        if (level.getBlockState(p).is(Blocks.POWDER_SNOW)) {
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) {
                                collected.addAmount(got.getAmount());
                                remaining -= got.getAmount();
                            }
                        }
                    } else if (targetType == FluidType.EXPERIENCE) {
                        // XP: recoger orbes dentro del AABB del bloque p (rápido, sin bucles)
                        FluidStack got = collectAt(p, level, remaining, targetType);
                        if (!got.isEmpty()) {
                            collected.addAmount(got.getAmount());
                            remaining -= got.getAmount();
                        }
                    }
                }
        return collected;
    }
}
