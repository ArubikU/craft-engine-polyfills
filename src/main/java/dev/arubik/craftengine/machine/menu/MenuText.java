package dev.arubik.craftengine.machine.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Shared helpers for building i18n (client-translated) machine-menu icons. All text
 * is a {@link Component#translatable(String)} resolved from the resource-pack lang, so
 * machine UIs change with the client language.
 */
public final class MenuText {

    private MenuText() {
    }

    /** Strip the default italic that inventory item names/lore get. */
    public static Component noI(Component c) {
        return c.decoration(TextDecoration.ITALIC, false);
    }

    /** Translatable label in {@code color}. */
    public static Component tr(String key, NamedTextColor color) {
        return Component.translatable(key).color(color);
    }

    /** Plain literal (numbers / separators). */
    public static Component lit(String s, NamedTextColor color) {
        return Component.text(s, color);
    }

    /** {@code <label>: <value>} as one component. */
    public static Component kv(String key, NamedTextColor keyColor, String value, NamedTextColor valColor) {
        return tr(key, keyColor).append(lit(": " + value, valColor));
    }

    public static ItemStack icon(Material mat, Component name, Component... lore) {
        ItemStack s = new ItemStack(mat);
        ItemMeta m = s.getItemMeta();
        m.displayName(noI(name));
        if (lore.length > 0) {
            List<Component> ls = new ArrayList<>();
            for (Component c : lore)
                ls.add(noI(c));
            m.lore(ls);
        }
        s.setItemMeta(m);
        return s;
    }
}
