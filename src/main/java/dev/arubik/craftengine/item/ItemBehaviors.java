/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.item;

import dev.arubik.craftengine.chainery.ChaineryItemBehavior;
import dev.arubik.craftengine.contraption.glue.GlueItemBehavior;
import dev.arubik.craftengine.item.behavior.EnchantmentUpgrade;
import dev.arubik.craftengine.item.behavior.FakeThornsLevel;
import dev.arubik.craftengine.util.RegistryUtils;
import net.momirealms.craftengine.core.util.Key;

public class ItemBehaviors {
    public static final Key POLYFILL_FAKE_ENCHANTMENT = Key.of((String)"polyfills:fake_enchantment");
    public static final Key POLYFILL_FAKE_THORNS_LEVEL = Key.of((String)"polyfills:fake_thorns_level");
    public static final Key POLYFILL_CHAINERY = Key.of((String)"polyfills:chainery");
    public static final Key POLYFILL_GLUE = Key.of((String)"polyfills:glue");
    /** One generic item behavior for every {@code items/*.json} definition — the item-side
     *  twin of {@code polyfills:data_machine} (see {@code DataItemBehavior}). */
    public static final Key POLYFILL_DATA_ITEM = Key.of((String)"polyfills:data_item");

    public static void register() {
        RegistryUtils.registerItemBehavior(POLYFILL_FAKE_ENCHANTMENT, EnchantmentUpgrade.FACTORY);
        RegistryUtils.registerItemBehavior(POLYFILL_FAKE_THORNS_LEVEL, FakeThornsLevel.FACTORY);
        RegistryUtils.registerItemBehavior(POLYFILL_CHAINERY, ChaineryItemBehavior.FACTORY);
        RegistryUtils.registerItemBehavior(POLYFILL_GLUE, GlueItemBehavior.FACTORY);
        RegistryUtils.registerItemBehavior(POLYFILL_DATA_ITEM,
                dev.arubik.craftengine.item.behavior.DataItemBehavior.FACTORY);
    }
}

