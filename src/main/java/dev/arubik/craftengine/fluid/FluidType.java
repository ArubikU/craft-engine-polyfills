package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public enum FluidType {
    WATER(1000),
    LAVA(1000),
    SLIME(250),
    EXPERIENCE(1),
    EMPTY(0);

    public final int unit;
    FluidType(int unit){ this.unit = unit; }

    public static final int MB_PER_BUCKET = 1000;

    // ---- Helpers unidad ----
    public int mbPerFullBlock(){
        switch(this){
            case WATER: case LAVA: return MB_PER_BUCKET;
            case SLIME: return 4 * unit; // 4*250=1000
            case EXPERIENCE: return unit; // 1:1
            default: return 0;
        }
    }
    public int unitMb(){ return unit; }
    public boolean isEmpty(){ return this==EMPTY; }

    // --- Recolección básica ---
    public static FluidStack collect(BlockPos pos, Level level){
        FluidState fs = level.getFluidState(pos);
        if(!fs.isEmpty()){
            FluidType t=null;
            if(fs.is(net.minecraft.world.level.material.Fluids.WATER) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)) t=WATER;
            else if(fs.is(net.minecraft.world.level.material.Fluids.LAVA) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) t=LAVA;
            if(t!=null){
                int full=t.mbPerFullBlock();
                if(fs.isSource()){
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, full,0);
                } else {
                    int amount=(full * fs.getAmount())/8; // proporcional
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, amount,0);
                }
            }
        } else {
            if(level.getBlockState(pos).getBlock() instanceof SlimeBlock){
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                return new FluidStack(SLIME, SLIME.mbPerFullBlock(),0);
            }
            var orbs= level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos));
            if(!orbs.isEmpty()){
                int total=0; for(var orb: orbs){ total+=orb.getValue(); orb.discard(); }
                return new FluidStack(EXPERIENCE, total * EXPERIENCE.unitMb(),0);
            }
        }
        return new FluidStack(EMPTY,0,0);
    }

    public static FluidStack collect(BlockPos pos, Level level, FluidType expected){
        if(expected==null || expected.isEmpty()) return new FluidStack(EMPTY,0,0);
        FluidState fs= level.getFluidState(pos);
        if(!fs.isEmpty()){
            if(matches(expected, fs)) return collect(pos, level);
            return new FluidStack(EMPTY,0,0);
        }
        if(expected==SLIME && level.getBlockState(pos).getBlock() instanceof SlimeBlock){
            level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
            return new FluidStack(SLIME, SLIME.mbPerFullBlock(),0);
        }
        if(expected==EXPERIENCE){
            var orbs= level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos));
            if(!orbs.isEmpty()){ int total=0; for(var orb: orbs){ total+=orb.getValue(); orb.discard(); } return new FluidStack(EXPERIENCE,total * EXPERIENCE.unitMb(),0);} }
        return new FluidStack(EMPTY,0,0);
    }

    public static FluidStack collect(BlockPos pos, Level level, int radius){
        FluidStack collected=new FluidStack(EMPTY,0,0);
        boolean found;
        do {
            found=false;
            for(int dx=-radius; dx<=radius; dx++)
                for(int dy=-radius; dy<=radius; dy++)
                    for(int dz=-radius; dz<=radius; dz++){
                        BlockPos p= pos.offset(dx,dy,dz);
                        FluidState fs= level.getFluidState(p);
                        if(!fs.isEmpty()){
                            if(fs.isSource()){
                                FluidStack got= collect(p, level);
                                if(!got.isEmpty()){ collected.addAmount(got.getAmount()); found=true; }
                            }
                            continue;
                        }
                        if(level.getBlockState(p).getBlock() instanceof SlimeBlock){
                            level.setBlock(p, Blocks.AIR.defaultBlockState(),3);
                            collected.addAmount(SLIME.mbPerFullBlock());
                            found=true;
                        }
                    }
        } while(found);
        return collected;
    }

    public static FluidStack collect(BlockPos pos, Level level, int radius, FluidType expected){
        if(expected==null || expected.isEmpty()) return new FluidStack(EMPTY,0,0);
        FluidStack collected=new FluidStack(EMPTY,0,0);
        for(int dx=-radius; dx<=radius; dx++)
            for(int dy=-radius; dy<=radius; dy++)
                for(int dz=-radius; dz<=radius; dz++){
                    BlockPos p= pos.offset(dx,dy,dz);
                    FluidState fs= level.getFluidState(p);
                    if(!fs.isEmpty()){
                        if(fs.isSource() && matches(expected, fs)){
                            FluidStack got= collect(p, level, expected);
                            if(!got.isEmpty()) collected.addAmount(got.getAmount());
                        }
                        continue;
                    }
                    if(expected==SLIME && level.getBlockState(p).getBlock() instanceof SlimeBlock){
                        level.setBlock(p, Blocks.AIR.defaultBlockState(),3);
                        collected.addAmount(SLIME.mbPerFullBlock());
                    }
                }
        return collected;
    }

    // -------- Overloads con límite maxMb --------
    // Nota: Para bloques fuente (agua/lava/slime) solo se colecta si cabe completo (no se hace colección parcial para evitar pérdida de fluido).
    public static FluidStack collectLimited(BlockPos pos, Level level, int maxMb){
        if(maxMb <= 0) return new FluidStack(EMPTY,0,0);
        FluidState fs = level.getFluidState(pos);
        if(!fs.isEmpty()){
            FluidType t = null;
            if(fs.is(net.minecraft.world.level.material.Fluids.WATER) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)) t=WATER;
            else if(fs.is(net.minecraft.world.level.material.Fluids.LAVA) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) t=LAVA;
            if(t!=null){
                int full = t.mbPerFullBlock();
                if(fs.isSource()){
                    if(full > maxMb) return new FluidStack(EMPTY,0,0); // no cabe
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, full,0);
                } else {
                    int amount=(full * fs.getAmount())/8;
                    if(amount > maxMb) amount = maxMb; // flowing: permitimos cap porque ya es parcial
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, amount,0);
                }
            }
        } else {
            if(level.getBlockState(pos).getBlock() instanceof SlimeBlock){
                int full = SLIME.mbPerFullBlock();
                if(full > maxMb) return new FluidStack(EMPTY,0,0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                return new FluidStack(SLIME, full,0);
            }
            var orbs= level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos));
            if(!orbs.isEmpty()){
                int total=0; for(var orb: orbs){ total+=orb.getValue(); }
                int mb = total * EXPERIENCE.unitMb();
                if(mb > maxMb) return new FluidStack(EMPTY,0,0); // no fraccionamos XP
                // si cabe, entonces ya descartamos orbes y retornamos
                for(var orb: orbs) orb.discard();
                return new FluidStack(EXPERIENCE, mb,0);
            }
        }
        return new FluidStack(EMPTY,0,0);
    }

    public static FluidStack collect(BlockPos pos, Level level, FluidType expected, int maxMb){
        if(expected==null || expected.isEmpty() || maxMb<=0) return new FluidStack(EMPTY,0,0);
        FluidState fs = level.getFluidState(pos);
        if(!fs.isEmpty()){
            if(!matches(expected, fs)) return new FluidStack(EMPTY,0,0);
            return collectLimited(pos, level, maxMb); // reutiliza lógica de límite
        }
        if(expected==SLIME && level.getBlockState(pos).getBlock() instanceof SlimeBlock) return collectLimited(pos, level, maxMb);
        if(expected==EXPERIENCE) return collectLimited(pos, level, maxMb);
        return new FluidStack(EMPTY,0,0);
    }

    // Overload helper para expected + límite (envuelve collectLimited simple)
    public static FluidStack collectLimited(BlockPos pos, Level level, FluidType expected, int maxMb){
        return collect(BlockPos.of(pos.asLong()), level, expected, maxMb); // reusa lógica; copia defensiva trivial
    }

    public static FluidStack collect(BlockPos pos, Level level, int radius, int maxMb){
        if(maxMb<=0) return new FluidStack(EMPTY,0,0);
        FluidStack collected = new FluidStack(EMPTY,0,0);
        int remaining = maxMb;
        boolean found;
        do {
            found=false;
            for(int dx=-radius; dx<=radius && remaining>0; dx++)
                for(int dy=-radius; dy<=radius && remaining>0; dy++)
                    for(int dz=-radius; dz<=radius && remaining>0; dz++){
                        BlockPos p= pos.offset(dx,dy,dz);
                        FluidState fs= level.getFluidState(p);
                        if(!fs.isEmpty()){
                            if(fs.isSource()){
                                FluidStack got= collectLimited(p, level, remaining);
                                if(!got.isEmpty()){ collected.addAmount(got.getAmount()); remaining-=got.getAmount(); found=true; }
                            }
                            continue;
                        }
                        if(level.getBlockState(p).getBlock() instanceof SlimeBlock){
                            int full = SLIME.mbPerFullBlock();
                            if(full <= remaining){
                                level.setBlock(p, Blocks.AIR.defaultBlockState(),3);
                                collected.addAmount(full); remaining-=full; found=true;
                            }
                        }
                    }
        } while(found && remaining>0);
        return collected;
    }

    public static FluidStack collect(BlockPos pos, Level level, int radius, FluidType expected, int maxMb){
        if(expected==null || expected.isEmpty() || maxMb<=0) return new FluidStack(EMPTY,0,0);
        FluidStack collected=new FluidStack(EMPTY,0,0);
        int remaining = maxMb;
        for(int dx=-radius; dx<=radius && remaining>0; dx++)
            for(int dy=-radius; dy<=radius && remaining>0; dy++)
                for(int dz=-radius; dz<=radius && remaining>0; dz++){
                    BlockPos p= pos.offset(dx,dy,dz);
                    FluidState fs= level.getFluidState(p);
                    if(!fs.isEmpty()){
                        if(fs.isSource() && matches(expected, fs)){
                            FluidStack got= collectLimited(p, level, expected, remaining);
                            if(!got.isEmpty()){ collected.addAmount(got.getAmount()); remaining-=got.getAmount(); }
                        }
                        continue;
                    }
                    if(expected==SLIME && level.getBlockState(p).getBlock() instanceof SlimeBlock){
                        int full = SLIME.mbPerFullBlock();
                        if(full <= remaining){
                            level.setBlock(p, Blocks.AIR.defaultBlockState(),3);
                            collected.addAmount(full); remaining-=full;
                        }
                    }
                }
        return collected;
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level){
        if(stack==null || stack.isEmpty()) return false;
        FluidType t= stack.getType();
        int needed= t.mbPerFullBlock();
        if(needed<=0 || stack.getAmount()<needed) return false;
        if(!level.getFluidState(pos).isEmpty() || !level.getBlockState(pos).isAir()) return false;
        if(t==WATER || t==LAVA){
            var f = (t==WATER)? net.minecraft.world.level.material.Fluids.WATER : net.minecraft.world.level.material.Fluids.LAVA;
            level.setBlock(pos, f.defaultFluidState().createLegacyBlock(),3);
        } else if(t==SLIME){
            level.setBlock(pos, Blocks.SLIME_BLOCK.defaultBlockState(),3);
        } else {
            return false; // EXPERIENCE no colocable
        }
        stack.removeAmount(needed);
        return true;
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level, int radius){
        if(stack==null || stack.isEmpty()) return false;
        FluidType t= stack.getType();
        int perBlock= t.mbPerFullBlock();
        if(perBlock<=0) return false;
        int maxBlocks= stack.getAmount()/perBlock; if(maxBlocks<=0) return false;
        boolean placed=false; int blocksPlaced=0;
        for(int dx=-radius; dx<=radius; dx++)
            for(int dy=-radius; dy<=radius; dy++)
                for(int dz=-radius; dz<=radius; dz++){
                    if(blocksPlaced>=maxBlocks) return placed;
                    BlockPos p= pos.offset(dx,dy,dz);
                    if(!level.getFluidState(p).isEmpty() || !level.getBlockState(p).isAir()) continue;
                    if(t==WATER || t==LAVA){
                        var f = (t==WATER)? net.minecraft.world.level.material.Fluids.WATER : net.minecraft.world.level.material.Fluids.LAVA;
                        level.setBlock(p, f.defaultFluidState().createLegacyBlock(),3);
                    } else if(t==SLIME){
                        level.setBlock(p, Blocks.SLIME_BLOCK.defaultBlockState(),3);
                    } else continue;
                    blocksPlaced++; placed=true;
                }
        stack.removeAmount(blocksPlaced * perBlock);
        return placed;
    }

    public boolean isSourceBlock(FluidState state){
        if(this==EMPTY) return false;
        if(this==WATER) return state.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER) || state.is(net.minecraft.world.level.material.Fluids.WATER);
        if(this==LAVA) return state.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA) || state.is(net.minecraft.world.level.material.Fluids.LAVA);
        return false;
    }
    public static boolean matches(FluidType t, FluidState state){
        if(t==null || state==null || state.isEmpty()) return false;
        return (t==WATER && (state.is(net.minecraft.world.level.material.Fluids.WATER) || state.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)))
            || (t==LAVA && (state.is(net.minecraft.world.level.material.Fluids.LAVA) || state.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)));
    }

    // Carrier helpers
    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack){
        if(carrier==null || stack==null || stack.isEmpty()) return 0; return carrier.insertFluid(level,pos,stack);
    }
    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb, java.util.function.Consumer<FluidStack> drained){
        if(carrier==null || mb<=0) return 0; return carrier.extractFluid(level,pos,mb,drained);
    }

    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack){
        if(stack==null || stack.isEmpty()) return Pair.of(FluidStack.EMPTY, stack);
        if(stack.getItem()== Items.WATER_BUCKET) return Pair.of(new FluidStack(WATER, WATER.mbPerFullBlock(),0), new ItemStack(Items.BUCKET));
        if(stack.getItem()== Items.LAVA_BUCKET) return Pair.of(new FluidStack(LAVA, LAVA.mbPerFullBlock(),0), new ItemStack(Items.BUCKET));
        if(stack.getItem()== Items.SLIME_BLOCK) return Pair.of(new FluidStack(SLIME, SLIME.mbPerFullBlock(),0), new ItemStack(Items.AIR));
        return Pair.of(FluidStack.EMPTY, stack);
    }
}
