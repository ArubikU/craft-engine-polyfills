package dev.arubik.craftengine.item.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;

public class ExtendedItemBehavior extends ItemBehavior {

    public ItemStack onAttackEntity(ItemStack stack, Object... args) { return stack; }
    public ItemStack onInteractEntity(ItemStack stack, Object... args) { return stack; }
    public ItemStack onDrop(ItemStack stack, Object... args) { return stack; }
    public ItemStack onPickup(ItemStack stack, Object... args) { return stack; }
    public ItemStack onRender(Entity holder, ItemStack stack, int slot) { return stack; }
    public ItemStack onDeath(ItemStack stack, Object... args) { return stack; }
    public ItemStack onUse(ItemStack stack, Object... args) { return stack; }
    public ItemStack onLeftClick(ItemStack stack, Object... args) { return stack; }
    public ItemStack onRightClick(ItemStack stack, Object... args) { return stack; }
    public ItemStack onConsume(ItemStack stack, Object... args) { return stack; }
    public ItemStack onBreakBlock(ItemStack stack, Object... args) { return stack; }
    public ItemStack onPlaceBlock(ItemStack stack, Object... args) { return stack; }
    public ItemStack onEquip(ItemStack stack, Object... args) { return stack; }
    public ItemStack onUnequip(ItemStack stack, Object... args) { return stack; }
    public ItemStack onDamageTaken(ItemStack stack, Object... args) { return stack; }
    public ItemStack onSlotChange(ItemStack stack, Object... args) { return stack; }
    
}
