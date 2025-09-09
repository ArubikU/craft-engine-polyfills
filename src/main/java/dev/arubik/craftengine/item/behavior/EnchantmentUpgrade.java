package dev.arubik.craftengine.item.behavior;

import java.nio.file.Path;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;

import dev.arubik.craftengine.util.MinecraftComponent;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.util.Key;

public class EnchantmentUpgrade extends ExtendedItemBehavior {
    public static final EnchantmentUpgradeFactory FACTORY = new EnchantmentUpgradeFactory();

    private final Map<Holder<Enchantment>, Integer> enchantments;
    public EnchantmentUpgrade(Map<Holder<Enchantment>, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public ItemStack onRender(Entity holder, ItemStack stack, int slot) {
        ItemEnchantments  enchants = stack.getComponents().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchants);
        ItemLore components = stack.getComponents().getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        boolean enchanted = false;
        for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {
            int[] currentLevelArr = {0};
            mutable.removeIf(e -> {
                Bukkit.getConsoleSender().sendMessage(e.value().description().getString());
                Bukkit.getConsoleSender().sendMessage(entry.getKey().value().description().getString());

                if(e.value().description().getString().equals(entry.getKey().value().description().getString())){
                    currentLevelArr[0] = mutable.getLevel(e);
                    return true;
                }else{
                    return false;
                }
            });
            int currentLevel = currentLevelArr[0];
            if(currentLevel+entry.getValue() <=0) continue;
            MinecraftComponent enchName = MinecraftComponent.fromMinecraft(Enchantment.getFullname(entry.getKey(), currentLevel).copy());
            MinecraftComponent extraLevel ;
            if(currentLevel==0){
                extraLevel = MinecraftComponent.fromString("<green>(+"+entry.getValue()+")</green>");
            }else{
                extraLevel = MinecraftComponent.fromString("<green> (+"+entry.getValue()+")</green>");
            }
            components = components.withLineAdded(enchName.append(extraLevel).decorate(TextDecoration.ITALIC.withState(false)).toMinecraft());
            enchanted = true;
        }
        
        enchanted = enchanted || !enchantments.isEmpty();
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, enchanted);
        stack.set(DataComponents.ENCHANTMENTS,mutable.toImmutable());
        stack.set(DataComponents.LORE,components);
        return stack;
    }

    public static class EnchantmentUpgradeFactory implements ItemBehaviorFactory {

        @Override
        public ItemBehavior create(Pack arg0, Path arg1, Key arg2, Map<String, Object> arguments) {
            if(arguments.containsKey("enchantments")){
                Object enchantsObj = arguments.get("enchantments");
                if (!(enchantsObj instanceof Map<?, ?> enchantsRaw)) {
                    throw new IllegalArgumentException("'enchantments' must be a Map<String, Integer>");
                }
                Map<String, Integer> enchants = new java.util.HashMap<>();
                for (Map.Entry<?, ?> entry : enchantsRaw.entrySet()) {
                    if (!(entry.getKey() instanceof String key) || !(entry.getValue() instanceof Integer value)) {
                        throw new IllegalArgumentException("Invalid enchantment entry: " + entry);
                    }
                    enchants.put(key, value);
                }
                Map<Holder<Enchantment>, Integer> enchantments = enchants.entrySet().stream().collect(
                    java.util.stream.Collectors.toMap(
                        e -> {
                            NamespacedKey key = NamespacedKey.fromString(e.getKey());
                            if(key == null) throw new IllegalArgumentException("Invalid enchantment key: "+e.getKey());
                            Enchantment ench = ((CraftEnchantment)io.papermc.paper.registry.RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(key)).getHandle();
                            return Holder.direct(ench);
                        },
                        Map.Entry::getValue
                    )
                );
                return new EnchantmentUpgrade(enchantments);
            }
            throw new UnsupportedOperationException("Unimplemented method 'create'");
        }
    }
}
