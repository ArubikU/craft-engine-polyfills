package dev.arubik.craftengine.fluid;

import java.util.Random;


import com.mojang.datafixers.util.Pair;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.fluid.behavior.PumpBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public enum FluidType {
    WATER(1000),
    LAVA(1000),
    SLIME(111),
    EXPERIENCE(1),
    POWDER_SNOW(1000),
    MILK(1000),
    EMPTY(0),
    HONEY(250);

    public final int unit;
    FluidType(int unit){ this.unit = unit; }
    // Delay específico para recolectar desde bloques (permite diferenciar de I/O)
    public static int blockCollectDelay(FluidType t){
        if (t == null) return 1;
        switch (t){
            case LAVA: return 12; 
            case SLIME: return 10;
            case HONEY: return 8;
            case POWDER_SNOW: return 6;
            case WATER: return 4;
            case MILK: return 2;
            case EXPERIENCE: return 1;
            case EMPTY: default: return 1;
        }
    }

    // Delay específico para I/O con carriers (push/pull)
    public static int carrierIODelay(FluidType t){
        if (t == null) return 1;
        switch (t){
            case LAVA: return 8;         // mover lava entre carriers es menos costoso que recolectarla de mundo
            case SLIME: return 8;
            case HONEY: return 8;
            case POWDER_SNOW: return 4;
            case WATER: return 2;        // rápido
            case MILK: return 2;
            case EXPERIENCE: return 1;
            case EMPTY: default: return 1;
        }
    }

    public static final int MB_PER_BUCKET = 1000;

    // ---- Helpers unidad ----
    public int mbPerFullBlock(){
        switch(this){
            case WATER: case LAVA: case POWDER_SNOW: case MILK: return MB_PER_BUCKET;
            case SLIME: return 9 * unit; // 9*250=2250
            case EXPERIENCE: return unit; // 1:1
            case HONEY: return 4 * unit; // 4*250=1000
            default: return 0;
        }
    }
    public int unitMb(){ return unit; }
    public boolean isEmpty(){ return this==EMPTY; }

    // --- Recolección simplificada (2 funciones públicas) ---
    // 1) Recolecta en un solo bloque hasta maxMb, sin bucles infinitos
    public static FluidStack collectAt(BlockPos pos, Level level, int maxMb, FluidType preferred){
        if (level == null || pos == null || maxMb <= 0) return new FluidStack(EMPTY,0,0);
        FluidState fs = level.getFluidState(pos);
        if(!fs.isEmpty()){
            FluidType t=null;
            if(fs.is(net.minecraft.world.level.material.Fluids.WATER) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)) t=WATER;
            else if(fs.is(net.minecraft.world.level.material.Fluids.LAVA) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) t=LAVA;
            // Respetar preferencia: si hay preferido y no coincide, no recolectar ni eliminar
            if (t != null && preferred != null && preferred != EMPTY && t != preferred) {
                return new FluidStack(EMPTY,0,0);
            }
            if(t!=null){
                int full=t.mbPerFullBlock();
                if(fs.isSource()){
                    if(full > maxMb) return new FluidStack(EMPTY,0,0);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, full,0);
                } else {
                    int amount=(full * fs.getAmount())/8; // proporcional a nivel (1..7)
                    if (amount > maxMb) amount = maxMb;
                    if (amount <= 0) return new FluidStack(EMPTY,0,0);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                    return new FluidStack(t, amount,0);
                }
            }
        } else {
            // Bloques materiales
            if(level.getBlockState(pos).getBlock() instanceof SlimeBlock){
                if (preferred != null && preferred != EMPTY && preferred != SLIME) return new FluidStack(EMPTY,0,0);
                int full = SLIME.mbPerFullBlock();
                if (full > maxMb) return new FluidStack(EMPTY,0,0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                return new FluidStack(SLIME, full,0);
            }
            if(level.getBlockState(pos).is(Blocks.HONEY_BLOCK)){
                if (preferred != null && preferred != EMPTY && preferred != HONEY) return new FluidStack(EMPTY,0,0);
                int full = HONEY.mbPerFullBlock();
                if (full > maxMb) return new FluidStack(EMPTY,0,0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                return new FluidStack(HONEY, full,0);
            }
            if(level.getBlockState(pos).is(Blocks.POWDER_SNOW)){
                if (preferred != null && preferred != EMPTY && preferred != POWDER_SNOW) return new FluidStack(EMPTY,0,0);
                int full = POWDER_SNOW.mbPerFullBlock();
                if (full > maxMb) return new FluidStack(EMPTY,0,0);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(),3);
                return new FluidStack(POWDER_SNOW, full,0);
            }
            // Orbes de experiencia en el bloque
            var orbs= level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos));
            if(!orbs.isEmpty()){
                if (preferred != null && preferred != EMPTY && preferred != EXPERIENCE) return new FluidStack(EMPTY,0,0);
                int stored = 0;
                int capacity = maxMb;
                for (var orb : orbs) {
                    if (stored >= capacity) break;
                    int value = orb.getValue();
                    int mb = value * EXPERIENCE.unitMb();
                    int remaining = capacity - stored;
                    if (mb <= remaining) {
                        stored += mb;
                        orb.discard();
                    } else {
                        int maxXpFit = remaining / EXPERIENCE.unitMb();
                        if (maxXpFit > 0) {
                            stored += maxXpFit * EXPERIENCE.unitMb();
                            orb.setValue(value - maxXpFit);
                        }
                        break;
                    }
                }
                if (stored > 0) return new FluidStack(EXPERIENCE, stored, 0);
            }
        }
        return new FluidStack(EMPTY,0,0);
    }

    public static FluidType getFluidTypeAt(BlockPos pos, Level level){
        FluidState fs = level.getFluidState(pos);
        if(!fs.isEmpty()){
            if(fs.is(net.minecraft.world.level.material.Fluids.WATER) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_WATER)) return WATER;
            if(fs.is(net.minecraft.world.level.material.Fluids.LAVA) || fs.is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) return LAVA;
        } else {
            if(level.getBlockState(pos).getBlock() instanceof SlimeBlock) return SLIME;
            if(level.getBlockState(pos).is(Blocks.POWDER_SNOW)) return POWDER_SNOW;
            var orbs= level.getEntitiesOfClass(ExperienceOrb.class,new AABB(pos));
            if(!orbs.isEmpty()) return EXPERIENCE;
            if(level.getBlockState(pos).is(Blocks.HONEY_BLOCK)) return HONEY;
        }
        return EMPTY;
    }

    // 2) Recolecta en un área hasta maxMb, respetando el tipo base del bloque inicial
    public static FluidStack collectArea(BlockPos pos, Level level, int radius, int maxMb, FluidType preferred){
        if (level == null || pos == null || radius < 0 || maxMb <= 0) return new FluidStack(EMPTY,0,0);
        FluidType base = getFluidTypeAt(pos, level);
        if (base == EMPTY) return new FluidStack(EMPTY,0,0);
        // Si existe preferencia no vacía, recolectar SOLO ese tipo; si no, recolectar el tipo base
        FluidType targetType = (preferred != null && preferred != EMPTY) ? preferred : base;
        FluidStack collected = new FluidStack(targetType, 0, 0);
        int remaining = maxMb;
        for(int dx=-radius; dx<=radius && remaining>0; dx++)
            for(int dy=-radius; dy<=radius && remaining>0; dy++)
                for(int dz=-radius; dz<=radius && remaining>0; dz++){
                    BlockPos p = pos.offset(dx,dy,dz);

                    //si el bloque ensima es un carrier, no recolectar
                    ImmutableBlockState ibs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(p.offset(0,1,0))).orElse(null);
                    if (ibs != null && ibs.behavior().getAs(PumpBehavior.class).isPresent()) continue;

                    // Evitar quitar el bloque central dos veces cuando sea flowing que refluye
                    if (p.equals(pos)) continue;

                    if (targetType == WATER || targetType == LAVA) {
                        FluidState st = level.getFluidState(p);
                        if (!st.isEmpty() && st.isSource() && matches(targetType, st)){
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) { collected.addAmount(got.getAmount()); remaining -= got.getAmount(); }
                        }
                    } else if (targetType == SLIME) {
                        if (level.getBlockState(p).getBlock() instanceof SlimeBlock){
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) { collected.addAmount(got.getAmount()); remaining -= got.getAmount(); }
                        }
                    } else if (targetType == HONEY) {
                        if (level.getBlockState(p).is(Blocks.HONEY_BLOCK)){
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) { collected.addAmount(got.getAmount()); remaining -= got.getAmount(); }
                        }
                    } else if (targetType == POWDER_SNOW) {
                        if (level.getBlockState(p).is(Blocks.POWDER_SNOW)){
                            FluidStack got = collectAt(p, level, remaining, targetType);
                            if (!got.isEmpty()) { collected.addAmount(got.getAmount()); remaining -= got.getAmount(); }
                        }
                    } else if (targetType == EXPERIENCE) {
                        // XP: recoger orbes dentro del AABB del bloque p (rápido, sin bucles)
                        FluidStack got = collectAt(p, level, remaining, targetType);
                        if (!got.isEmpty()) { collected.addAmount(got.getAmount()); remaining -= got.getAmount(); }
                    }
                }
        return collected;
    }

    // (El resto de overloads antiguos se han eliminado a favor de estas 2 funciones)

    public static boolean place(FluidStack stack, BlockPos pos, Level level){
        if(stack==null || stack.isEmpty()) return false;
        FluidType t= stack.getType();
        int needed= t.mbPerFullBlock();
        if(needed<=0 || stack.getAmount()<needed) return false;
        if(!level.getFluidState(pos).isEmpty() || !level.getBlockState(pos).isAir()) return false;
        if(t==WATER || t==LAVA){
            if(t==LAVA && level.dimensionType().ultraWarm()) return false; // no colocar lava en el Nether
            var f = (t==WATER)? net.minecraft.world.level.material.Fluids.WATER : net.minecraft.world.level.material.Fluids.LAVA;
            level.setBlock(pos, f.defaultFluidState().createLegacyBlock(),3);
        } else if(t==SLIME){
            level.setBlock(pos, Blocks.SLIME_BLOCK.defaultBlockState(),3);
        } else if(t==HONEY){
            level.setBlock(pos, Blocks.HONEY_BLOCK.defaultBlockState(),3);
        }
        else if(t==POWDER_SNOW){
            level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(),3);
        } else {
            return false; // EXPERIENCE and MILK not placeable
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
                    } else if(t==POWDER_SNOW){
                        level.setBlock(p, Blocks.POWDER_SNOW.defaultBlockState(),3);
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
        if(stack.getItem()== Items.MILK_BUCKET) return Pair.of(new FluidStack(MILK, MILK.mbPerFullBlock(),0), new ItemStack(Items.BUCKET));
        if(stack.getItem()== Items.POWDER_SNOW_BUCKET) return Pair.of(new FluidStack(POWDER_SNOW, POWDER_SNOW.mbPerFullBlock(),0), new ItemStack(Items.BUCKET));
        if(stack.getItem()== Items.SLIME_BLOCK) return Pair.of(new FluidStack(SLIME, SLIME.mbPerFullBlock(),0), new ItemStack(Items.AIR));
        if(stack.getItem()==Items.EXPERIENCE_BOTTLE) return Pair.of(new FluidStack(EXPERIENCE, 3+RANDOM.nextInt(5)+RANDOM.nextInt(5),0), new ItemStack(Items.GLASS_BOTTLE));
        if(stack.getItem().components().has(DataComponents.POTION_CONTENTS)){
            var contents = stack.getItem().components().get(DataComponents.POTION_CONTENTS);
            if(contents != null && contents.is(Potions.WATER)){
                return Pair.of(new FluidStack(WATER, WATER.mbPerFullBlock()/4,0), new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        if(stack.getItem()== Items.HONEY_BOTTLE) return Pair.of(new FluidStack(HONEY, HONEY.unitMb(),0), new ItemStack(Items.GLASS_BOTTLE));
        if(stack.getItem()==Items.SLIME_BALL) return Pair.of(new FluidStack(SLIME, SLIME.unit,0), new ItemStack(Items.AIR));
        return Pair.of(FluidStack.EMPTY, stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid, int requestedAmount){
        if(container==null || container.isEmpty() || fluid==null || fluid.isEmpty() || requestedAmount <= 0) 
            return Pair.of(ItemStack.EMPTY, fluid);
        
        FluidType type = fluid.getType();
        int available = fluid.getAmount();
        
        // Bucket containers
        if(container.getItem() == Items.BUCKET){
            if(available >= type.mbPerFullBlock()){
                if(type == WATER) return Pair.of(new ItemStack(Items.WATER_BUCKET), new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if(type == LAVA) return Pair.of(new ItemStack(Items.LAVA_BUCKET), new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if(type == MILK) return Pair.of(new ItemStack(Items.MILK_BUCKET), new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if(type == POWDER_SNOW) return Pair.of(new ItemStack(Items.POWDER_SNOW_BUCKET), new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
            }
        }
        
        // Glass bottle containers
        if(container.getItem() == Items.GLASS_BOTTLE){
            if(type == EXPERIENCE && available >= 10){
                int taken = Math.min(requestedAmount, available);
                taken = Math.min(taken, 10); // Experience bottle takes 10mb
                return Pair.of(new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(type, available - taken, fluid.getPressure()));
            }
            if(type == WATER && available >= 250){
                int taken = Math.min(requestedAmount, available);
                taken = Math.min(taken, 250); // Water potion takes 250mb (1/4 bucket)
                return Pair.of(PotionContents.createItemStack(Items.POTION, Potions.WATER), new FluidStack(type, available - taken, fluid.getPressure()));
            }
        }
        
        return Pair.of(ItemStack.EMPTY, fluid);
    }

    /**
     * Reacción de un fluido con el ítem del jugador: consume parte del fluido y transforma el ítem.
     * Devuelve el nuevo Fluido restante y una lista de ítems generados (por ejemplo, cubos o botellas).
     */
    public static Pair<FluidStack, java.util.List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem){
        java.util.List<ItemStack> outputs = new java.util.ArrayList<>();
        if (fluid==null || fluid.isEmpty() || playerItem==null || playerItem.isEmpty())
            return Pair.of(fluid, outputs);

        if (fluid.getType() == LAVA && playerItem.getItem() == Items.WATER_BUCKET) {
            if (fluid.getAmount() >= MB_PER_BUCKET) {
                outputs.add(new ItemStack(Items.BUCKET));
                outputs.add(new ItemStack(Blocks.OBSIDIAN.asItem()));
                return Pair.of(new FluidStack(LAVA, fluid.getAmount() - MB_PER_BUCKET, fluid.getPressure()), outputs);
            }
            return Pair.of(fluid, outputs);
        }
        if (fluid.getType() == WATER && playerItem.getItem() == Items.LAVA_BUCKET) {
            if (fluid.getAmount() >= MB_PER_BUCKET) {
                outputs.add(new ItemStack(Items.BUCKET));
                outputs.add(new ItemStack(Blocks.OBSIDIAN.asItem()));
                return Pair.of(new FluidStack(WATER, fluid.getAmount() - MB_PER_BUCKET, fluid.getPressure()), outputs);
            }
            return Pair.of(fluid, outputs);
        }

        // Por ahora, no hay otras reacciones definidas.
        return Pair.of(fluid, outputs);
    }

    /**
     * Intenta dispersar un fluido en un bloque de aire de manera inteligente.
     * Para XP: genera partículas de experiencia
     * Para líquidos colocables: intenta colocar el bloque
     * @param stack El fluido a dispersar
     * @param pos La posición del bloque de aire
     * @param level El mundo
     * @return La cantidad de fluido consumido
     */
    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        if (stack == null || stack.isEmpty() || level.isClientSide()) return 0;
        if (!level.getBlockState(pos).isAir()) return 0;
        
        FluidType type = stack.getType();
        int amount = stack.getAmount();
        
        if (type == EXPERIENCE) {
            //drop max 7 orbs of 1-20 XP each, consuming fluid
            int consumed = 0;
            while (consumed < amount && consumed < 100) { // limit max 100mb per call to avoid long loops
                int xpValue = 1 + RANDOM.nextInt(20);
                int xpMb = xpValue * EXPERIENCE.unitMb();
                if (consumed + xpMb > amount) break; // no cabe más
                spawnXpOrb(level, pos, xpValue);
                consumed += xpMb;
            }
            return consumed;
        } else if (type == WATER || type == LAVA || type == SLIME || type == POWDER_SNOW) {
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

    public static final Random  RANDOM = new Random();

    @SuppressWarnings("deprecation")
    private static void spawnXpOrb(Level level, BlockPos pos, int xp) {
        ExperienceOrb orb = new ExperienceOrb(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xp);
        level.addFreshEntity(orb);
    }
}
