package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.ArrayList;

public class FluidReactions {

    /**
     * Reacción de un fluido con el ítem del jugador: consume parte del fluido y
     * transforma el ítem.
     * Devuelve el nuevo Fluido restante y una lista de ítems generados (por
     * ejemplo, cubos o botellas).
     */
    public static Pair<FluidStack, List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        List<ItemStack> outputs = new ArrayList<>();
        if (fluid == null || fluid.isEmpty() || playerItem == null || playerItem.isEmpty())
            return Pair.of(fluid, outputs);

        if (fluid.getType() == FluidType.LAVA && playerItem.getItem() == Items.WATER_BUCKET) {
            if (fluid.getAmount() >= FluidType.MB_PER_BUCKET) {
                outputs.add(new ItemStack(Items.BUCKET));
                outputs.add(new ItemStack(Blocks.OBSIDIAN.asItem()));
                return Pair.of(new FluidStack(FluidType.LAVA, fluid.getAmount() - FluidType.MB_PER_BUCKET,
                        fluid.getPressure()), outputs);
            }
            return Pair.of(fluid, outputs);
        }
        if (fluid.getType() == FluidType.WATER && playerItem.getItem() == Items.LAVA_BUCKET) {
            if (fluid.getAmount() >= FluidType.MB_PER_BUCKET) {
                outputs.add(new ItemStack(Items.BUCKET));
                outputs.add(new ItemStack(Blocks.OBSIDIAN.asItem()));
                return Pair.of(new FluidStack(FluidType.WATER, fluid.getAmount() - FluidType.MB_PER_BUCKET,
                        fluid.getPressure()), outputs);
            }
            return Pair.of(fluid, outputs);
        }

        // Por ahora, no hay otras reacciones definidas.
        return Pair.of(fluid, outputs);
    }
}
