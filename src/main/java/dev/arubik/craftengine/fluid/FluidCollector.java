/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LayeredCauldronBlock
 *  net.minecraft.world.level.block.SlimeBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.fluid.behavior.PumpBehavior;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public class FluidCollector {
    public static int blockCollectDelay(FluidType t) {
        return FluidType.blockCollectDelay(t);
    }

    public static FluidStack collectAt(BlockPos pos, Level level, int maxMb, FluidType preferred) {
        if (level == null || pos == null || maxMb <= 0) {
            return new FluidStack(FluidType.EMPTY, 0, 0);
        }
        FluidState fs = level.getFluidState(pos);
        if (!fs.isEmpty()) {
            FluidType t = null;
            if (fs.is((Fluid)Fluids.WATER) || fs.is((Fluid)Fluids.FLOWING_WATER)) {
                t = FluidType.WATER;
            } else if (fs.is((Fluid)Fluids.LAVA) || fs.is((Fluid)Fluids.FLOWING_LAVA)) {
                t = FluidType.LAVA;
            }
            if (t != null && preferred != null && preferred != FluidType.EMPTY && t != preferred) {
                return new FluidStack(FluidType.EMPTY, 0, 0);
            }
            if (t != null) {
                int full = t.mbPerFullBlock();
                if (fs.isSource()) {
                    if (full > maxMb) {
                        return new FluidStack(FluidType.EMPTY, 0, 0);
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return new FluidStack(t, full, 0);
                }
                int amount = full * fs.getAmount() / 8;
                if (amount > maxMb) {
                    amount = maxMb;
                }
                if (amount <= 0) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(t, amount, 0);
            }
        } else {
            BlockState cs = level.getBlockState(pos);
            if (cs.is(Blocks.WATER_CAULDRON)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.WATER) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int levelMb = Math.max(1, FluidType.WATER.mbPerFullBlock() / 3);
                int lvl = (Integer)cs.getValue((Property)LayeredCauldronBlock.LEVEL);
                int takeLevels = Math.min(lvl, maxMb / levelMb);
                if (takeLevels <= 0) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int newLvl = lvl - takeLevels;
                level.setBlock(pos, newLvl <= 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)cs.setValue((Property)LayeredCauldronBlock.LEVEL, (Comparable)Integer.valueOf(newLvl)), 3);
                return new FluidStack(FluidType.WATER, takeLevels * levelMb, 0);
            }
            if (cs.is(Blocks.LAVA_CAULDRON)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.LAVA) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int full = FluidType.LAVA.mbPerFullBlock();
                if (full > maxMb) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                return new FluidStack(FluidType.LAVA, full, 0);
            }
            if (cs.getBlock() instanceof SlimeBlock) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.SLIME) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int full = FluidType.SLIME.mbPerFullBlock();
                if (full > maxMb) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.SLIME, full, 0);
            }
            if (cs.is(Blocks.HONEY_BLOCK)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.HONEY) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int full = FluidType.HONEY.mbPerFullBlock();
                if (full > maxMb) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.HONEY, full, 0);
            }
            if (cs.is(Blocks.POWDER_SNOW)) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.POWDER_SNOW) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int full = FluidType.POWDER_SNOW.mbPerFullBlock();
                if (full > maxMb) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return new FluidStack(FluidType.POWDER_SNOW, full, 0);
            }
            @SuppressWarnings("unchecked")
            List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos));
            if (!orbs.isEmpty()) {
                if (preferred != null && preferred != FluidType.EMPTY && preferred != FluidType.EXPERIENCE) {
                    return new FluidStack(FluidType.EMPTY, 0, 0);
                }
                int stored = 0;
                int capacity = maxMb;
                for (ExperienceOrb orb : orbs) {
                    int remaining;
                    if (stored >= capacity) break;
                    int value = orb.getValue();
                    int mb = value * FluidType.EXPERIENCE.unitMb();
                    if (mb <= (remaining = capacity - stored)) {
                        stored += mb;
                        orb.discard();
                        continue;
                    }
                    int maxXpFit = remaining / FluidType.EXPERIENCE.unitMb();
                    if (maxXpFit <= 0) break;
                    stored += maxXpFit * FluidType.EXPERIENCE.unitMb();
                    orb.setValue(value - maxXpFit);
                    break;
                }
                if (stored > 0) {
                    return new FluidStack(FluidType.EXPERIENCE, stored, 0);
                }
            }
        }
        return new FluidStack(FluidType.EMPTY, 0, 0);
    }

    public static FluidType getFluidTypeAt(BlockPos pos, Level level) {
        FluidState fs = level.getFluidState(pos);
        if (!fs.isEmpty()) {
            if (fs.is((Fluid)Fluids.WATER) || fs.is((Fluid)Fluids.FLOWING_WATER)) {
                return FluidType.WATER;
            }
            if (fs.is((Fluid)Fluids.LAVA) || fs.is((Fluid)Fluids.FLOWING_LAVA)) {
                return FluidType.LAVA;
            }
        } else {
            BlockState cs = level.getBlockState(pos);
            if (cs.is(Blocks.WATER_CAULDRON)) {
                return FluidType.WATER;
            }
            if (cs.is(Blocks.LAVA_CAULDRON)) {
                return FluidType.LAVA;
            }
            if (cs.getBlock() instanceof SlimeBlock) {
                return FluidType.SLIME;
            }
            if (cs.is(Blocks.POWDER_SNOW)) {
                return FluidType.POWDER_SNOW;
            }
            @SuppressWarnings("unchecked")
            List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos));
            if (!orbs.isEmpty()) {
                return FluidType.EXPERIENCE;
            }
            if (cs.is(Blocks.HONEY_BLOCK)) {
                return FluidType.HONEY;
            }
        }
        return FluidType.EMPTY;
    }

    public static FluidStack collectArea(BlockPos pos, Level level, int radius, int maxMb, FluidType preferred) {
        if (level == null || pos == null || radius < 0 || maxMb <= 0) {
            return new FluidStack(FluidType.EMPTY, 0, 0);
        }
        FluidType base = FluidCollector.getFluidTypeAt(pos, level);
        if (base == FluidType.EMPTY) {
            return new FluidStack(FluidType.EMPTY, 0, 0);
        }
        FluidType targetType = preferred != null && preferred != FluidType.EMPTY ? preferred : base;
        FluidStack collected = new FluidStack(targetType, 0, 0);
        int remaining = maxMb;
        int maxIterations = 27;
        int iterations = 0;
        block0: for (int dx = -radius; dx <= radius && remaining > 0; ++dx) {
            for (int dy = -radius; dy <= radius && remaining > 0; ++dy) {
                for (int dz = -radius; dz <= radius && remaining > 0; ++dz) {
                    FluidStack got;
                    if (iterations++ >= maxIterations) break block0;
                    BlockPos p = pos.offset(dx, dy, dz);
                    ImmutableBlockState ibs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(p.offset(0, 1, 0))).orElse(null);
                    if (ibs != null && ibs.behavior().getFirst(PumpBehavior.class) != null) continue;
                    if (targetType == FluidType.WATER || targetType == FluidType.LAVA) {
                        FluidStack got2;
                        FluidState st = level.getFluidState(p);
                        if (st.isEmpty() || !st.isSource() || !FluidType.matches(targetType, st) || (got2 = FluidCollector.collectAt(p, level, remaining, targetType)).isEmpty()) continue;
                        collected.addAmount(got2.getAmount());
                        remaining -= got2.getAmount();
                        continue;
                    }
                    if (targetType == FluidType.SLIME) {
                        if (!(level.getBlockState(p).getBlock() instanceof SlimeBlock) || (got = FluidCollector.collectAt(p, level, remaining, targetType)).isEmpty()) continue;
                        collected.addAmount(got.getAmount());
                        remaining -= got.getAmount();
                        continue;
                    }
                    if (targetType == FluidType.HONEY) {
                        if (!level.getBlockState(p).is(Blocks.HONEY_BLOCK) || (got = FluidCollector.collectAt(p, level, remaining, targetType)).isEmpty()) continue;
                        collected.addAmount(got.getAmount());
                        remaining -= got.getAmount();
                        continue;
                    }
                    if (targetType == FluidType.POWDER_SNOW) {
                        if (!level.getBlockState(p).is(Blocks.POWDER_SNOW) || (got = FluidCollector.collectAt(p, level, remaining, targetType)).isEmpty()) continue;
                        collected.addAmount(got.getAmount());
                        remaining -= got.getAmount();
                        continue;
                    }
                    if (targetType != FluidType.EXPERIENCE || (got = FluidCollector.collectAt(p, level, remaining, targetType)).isEmpty()) continue;
                    collected.addAmount(got.getAmount());
                    remaining -= got.getAmount();
                }
            }
        }
        return collected;
    }
}

