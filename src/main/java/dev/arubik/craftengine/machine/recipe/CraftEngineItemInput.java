package dev.arubik.craftengine.machine.recipe;

import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

public class CraftEngineItemInput implements RecipeInput {

    private final String itemId;
    private final int amount;

    public CraftEngineItemInput(String itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        if (stack.getCount() < amount)
            return false;

        var ceItem = CraftEngineItems.byItemStack(stack.asBukkitMirror());
        if (ceItem == null)
            return false;

        return ceItem.id().toString().equals(itemId);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }
}
