package dev.arubik.craftengine.machine.recipe.condition;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockBelowCondition implements RecipeCondition {
    private final Material requiredBlock;

    public BlockBelowCondition(Material requiredBlock) {
        this.requiredBlock = requiredBlock;
    }

    @Override
    public boolean test(net.minecraft.world.level.Level level, AbstractMachineBlockEntity machine) {
        if (level == null)
            return false;
        // BlockPos is NMS BlockPos, we need to get Bukkit Block or use NMS Level
        // AbstractMachineBlockEntity.getMachinePos() returns NMS BlockPos

        // Using NMS directly for performance

        net.minecraft.world.level.block.state.BlockState state = level
                .getBlockState(machine.getMachinePos().below());
        // Convert NMS Block to Bukkit Material?
        // Easier: Get Bukkit Block via World
        // level.getWorld().getBlockAt(...)

        // Let's use Bukkit API if possible for Material check, or NMS Block check
        // NMS: state.is(Blocks.MAGMA_BLOCK)

        // But we store Material (Bukkit). So we need Bukkit check.
        Block block = level.getWorld().getBlockAt(
                machine.getMachinePos().getX(),
                machine.getMachinePos().getY() - 1,
                machine.getMachinePos().getZ());

        return block.getType() == requiredBlock;
    }

    @Override
    public String getDescription() {
        return "Requires " + requiredBlock.name() + " below";
    }
}
