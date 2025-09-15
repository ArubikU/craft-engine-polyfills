package dev.arubik.craftengine.item;

import dev.arubik.craftengine.item.behavior.*;
import dev.arubik.craftengine.util.RegistryUtils;
import net.momirealms.craftengine.bukkit.item.behavior.BlockItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.util.Key;

public class ItemBehaviors {
    public static final Key POLYFILL_FAKE_ENCHANTMENT = Key.of("polyfills:fake_enchantment");
    public static final Key POLYFILL_FAKE_THORNS_LEVEL = Key.of("polyfills:fake_thorns_level");
    public static void register() {
        RegistryUtils.registerItemBehavior(POLYFILL_FAKE_ENCHANTMENT, EnchantmentUpgrade.FACTORY);
        RegistryUtils.registerItemBehavior(POLYFILL_FAKE_THORNS_LEVEL, FakeThornsLevel.FACTORY);
    }
}
