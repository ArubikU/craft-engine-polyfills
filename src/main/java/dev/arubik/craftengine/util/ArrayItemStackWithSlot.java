package dev.arubik.craftengine.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;

public class ArrayItemStackWithSlot {
    public static final List<ItemStackWithSlot> EMPTY = List.of();
    public static final List<ItemStackWithSlot> from(ItemStack[] stacks) {
        List<ItemStackWithSlot> list = new ArrayList<>();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if(stack == null) continue;
            if (!stack.isEmpty()) {
                list.add(new ItemStackWithSlot( i, stack));
            }
        }
        return list;
    }

}
