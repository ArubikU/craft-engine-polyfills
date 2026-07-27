package dev.arubik.craftengine.machine.recipe;

import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemOutput implements RecipeOutput {
    private final ItemStack stack;
    private final float chance;
    /**
     * How many to produce, rolled per craft. A recipe that wants "2 to 4 nuggets"
     * says so with a vanilla number provider instead of needing a separate chance
     * output per possible count.
     */
    private final dev.arubik.craftengine.data.Amount count;

    public ItemOutput(ItemStack stack, float chance) {
        this(stack, chance, dev.arubik.craftengine.data.Amount.of(stack.getCount()));
    }

    public ItemOutput(ItemStack stack, float chance, dev.arubik.craftengine.data.Amount count) {
        this.stack = stack;
        this.chance = chance;
        this.count = count;
    }

    public ItemOutput(ItemStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void dispense(Level level, AbstractMachineBlockEntity machine) {
        // Respect the drop chance: a secondary output below 1.0 only sometimes drops.
        if (chance < 1.0f && java.util.concurrent.ThreadLocalRandom.current().nextFloat() >= chance) {
            return;
        }
        int rolled = count.roll();
        if (rolled <= 0)
            return;
        ItemStack out = stack.copy();
        out.setCount(rolled);
        machine.addOutput(out);
    }

    public ItemStack getItem() {
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public Object getOutput() {
        return stack;
    }

    @Override
    public float getChance() {
        return chance;
    }
}
