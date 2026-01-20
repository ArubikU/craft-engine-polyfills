package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class FluidItemConverter {

    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return Pair.of(FluidStack.EMPTY, stack);
        if (stack.getItem() == Items.WATER_BUCKET)
            return Pair.of(new FluidStack(FluidType.WATER, FluidType.WATER.mbPerFullBlock(), 0),
                    new ItemStack(Items.BUCKET));
        if (stack.getItem() == Items.LAVA_BUCKET)
            return Pair.of(new FluidStack(FluidType.LAVA, FluidType.LAVA.mbPerFullBlock(), 0),
                    new ItemStack(Items.BUCKET));
        if (stack.getItem() == Items.MILK_BUCKET)
            return Pair.of(new FluidStack(FluidType.MILK, FluidType.MILK.mbPerFullBlock(), 0),
                    new ItemStack(Items.BUCKET));
        if (stack.getItem() == Items.POWDER_SNOW_BUCKET)
            return Pair.of(new FluidStack(FluidType.POWDER_SNOW, FluidType.POWDER_SNOW.mbPerFullBlock(), 0),
                    new ItemStack(Items.BUCKET));
        if (stack.getItem() == Items.SLIME_BLOCK)
            return Pair.of(new FluidStack(FluidType.SLIME, FluidType.SLIME.mbPerFullBlock(), 0),
                    new ItemStack(Items.AIR));
        if (stack.getItem() == Items.EXPERIENCE_BOTTLE)
            return Pair.of(
                    new FluidStack(FluidType.EXPERIENCE, 3 + FluidType.RANDOM.nextInt(5) + FluidType.RANDOM.nextInt(5),
                            0),
                    new ItemStack(Items.GLASS_BOTTLE));
        if (stack.getItem().components().has(DataComponents.POTION_CONTENTS)) {
            var contents = stack.getItem().components().get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER)) {
                return Pair.of(new FluidStack(FluidType.WATER, FluidType.WATER.mbPerFullBlock() / 4, 0),
                        new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        if (stack.getItem() == Items.HONEY_BOTTLE)
            return Pair.of(new FluidStack(FluidType.HONEY, FluidType.HONEY.unitMb(), 0),
                    new ItemStack(Items.GLASS_BOTTLE));
        if (stack.getItem() == Items.SLIME_BALL)
            return Pair.of(new FluidStack(FluidType.SLIME, FluidType.SLIME.unit, 0), new ItemStack(Items.AIR));
        return Pair.of(FluidStack.EMPTY, stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid,
            int requestedAmount) {
        if (container == null || container.isEmpty() || fluid == null || fluid.isEmpty() || requestedAmount <= 0)
            return Pair.of(ItemStack.EMPTY, fluid);

        FluidType type = fluid.getType();
        int available = fluid.getAmount();

        // Bucket containers
        if (container.getItem() == Items.BUCKET) {
            if (available >= type.mbPerFullBlock()) {
                if (type == FluidType.WATER)
                    return Pair.of(new ItemStack(Items.WATER_BUCKET),
                            new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if (type == FluidType.LAVA)
                    return Pair.of(new ItemStack(Items.LAVA_BUCKET),
                            new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if (type == FluidType.MILK)
                    return Pair.of(new ItemStack(Items.MILK_BUCKET),
                            new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
                if (type == FluidType.POWDER_SNOW)
                    return Pair.of(new ItemStack(Items.POWDER_SNOW_BUCKET),
                            new FluidStack(type, available - type.mbPerFullBlock(), fluid.getPressure()));
            }
        }

        // Glass bottle containers
        if (container.getItem() == Items.GLASS_BOTTLE) {
            if (type == FluidType.EXPERIENCE && available >= 10) {
                int taken = Math.min(requestedAmount, available);
                taken = Math.min(taken, 10); // Experience bottle takes 10mb
                return Pair.of(new ItemStack(Items.EXPERIENCE_BOTTLE),
                        new FluidStack(type, available - taken, fluid.getPressure()));
            }
            if (type == FluidType.WATER && available >= 250) {
                int taken = Math.min(requestedAmount, available);
                taken = Math.min(taken, 250); // Water potion takes 250mb (1/4 bucket)
                return Pair.of(PotionContents.createItemStack(Items.POTION, Potions.WATER),
                        new FluidStack(type, available - taken, fluid.getPressure()));
            }
        }

        return Pair.of(ItemStack.EMPTY, fluid);
    }
}
