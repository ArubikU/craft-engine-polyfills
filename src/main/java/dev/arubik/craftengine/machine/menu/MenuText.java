/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.key.Key
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  net.kyori.adventure.text.minimessage.MiniMessage
 *  net.momirealms.craftengine.bukkit.api.CraftEngineImages
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.item.BukkitItemDefinition
 *  net.momirealms.craftengine.core.font.Image
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.machine.menu;

import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.craftengine.bukkit.api.CraftEngineImages;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.font.Image;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MenuText {
    private MenuText() {
    }

    public static Component noI(Component c) {
        return c.decoration(TextDecoration.ITALIC, false);
    }

    public static ItemStack emptyFiller() {
        ItemMeta m;
        ItemStack s = null;
        try {
            BukkitItemDefinition def = CraftEngineItems.byId((Key)Key.of((String)"cml", (String)"gui_empty"));
            if (def != null) {
                s = def.buildBukkitItem();
            }
        }
        catch (Throwable def) {
            // empty catch block
        }
        if (s == null || s.getType() == Material.AIR) {
            s = new ItemStack(Material.PAPER);
        }
        if ((m = s.getItemMeta()) != null) {
            try {
                m.setHideTooltip(true);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            m.displayName((Component)Component.empty());
            s.setItemMeta(m);
        }
        return s;
    }

    public static ItemStack backIcon() {
        return MenuText.iconItem(Key.of((String)"cml", (String)"return_icon"), Material.ARROW, MenuText.tr("polyfill.ui.back", NamedTextColor.YELLOW), new Component[0]);
    }

    public static ItemStack lockedIcon(Component name, Component ... lore) {
        return MenuText.iconItem(Key.of((String)"cml", (String)"locked_icon"), Material.BARRIER, name, lore);
    }

    public static Component imageTitle(String imageId, int shiftPx) {
        try {
            Image img = CraftEngineImages.byId((Key)Key.of((String)imageId));
            if (img == null) {
                return null;
            }
            String imageMm = img.miniMessageAt(0, 0);
            Component out = MiniMessage.miniMessage().deserialize(imageMm).colorIfAbsent((TextColor)NamedTextColor.WHITE);
            int px = Math.max(0, Math.min(16, -shiftPx));
            if (px > 0) {
                Component neg = Component.text((String)String.valueOf((char)(63488 + px - 1))).font(net.kyori.adventure.key.Key.key((String)"minecraft", (String)"default"));
                out = neg.append(out);
            }
            return MenuText.noI(out);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    public static Component tr(String key, NamedTextColor color) {
        return Component.translatable((String)key).color((TextColor)color);
    }

    public static Component textOrTranslatable(String value, NamedTextColor color) {
        if (value == null) {
            return Component.empty();
        }
        String key = MenuText.normalizedI18nKey(value);
        if (key != null) {
            return MenuText.tr(key, color);
        }
        return MenuText.lit(value, color);
    }

    public static String normalizedI18nKey(String value) {
        if (value == null) {
            return null;
        }
        String key = value.trim();
        if (key.isEmpty()) {
            return null;
        }
        if (key.startsWith("<lang:") && key.endsWith(">")) {
            key = key.substring(6, key.length() - 1).trim();
        } else if (key.startsWith("lang:")) {
            key = key.substring(5).trim();
        }
        if (key.isEmpty() || key.indexOf(32) >= 0) {
            return null;
        }
        if (key.contains(".") || key.contains(":")) {
            return key;
        }
        return null;
    }

    public static boolean isI18nKey(String value) {
        return MenuText.normalizedI18nKey(value) != null;
    }

    public static Component lit(String s, NamedTextColor color) {
        return Component.text((String)s, (TextColor)color);
    }

    public static Component kv(String key, NamedTextColor keyColor, String value, NamedTextColor valColor) {
        return MenuText.tr(key, keyColor).append(MenuText.lit(": " + value, valColor));
    }

    public static Component kv(String key, NamedTextColor keyColor, Component value, NamedTextColor valColor) {
        return MenuText.tr(key, keyColor).append(MenuText.lit(": ", valColor)).append(value.colorIfAbsent((TextColor)valColor));
    }

    public static ItemStack icon(Material mat, Component name, Component ... lore) {
        ItemStack s = new ItemStack(mat);
        ItemMeta m = s.getItemMeta();
        m.displayName(MenuText.noI(name));
        if (lore.length > 0) {
            ArrayList<Component> ls = new ArrayList<Component>();
            for (Component c : lore) {
                ls.add(MenuText.noI(c));
            }
            m.lore(ls);
        }
        s.setItemMeta(m);
        return s;
    }

    public static ItemStack iconItem(Key id, Material fallback, Component name, Component ... lore) {
        ItemMeta m;
        ItemStack s = null;
        try {
            BukkitItemDefinition def = CraftEngineItems.byId((Key)id);
            if (def != null) {
                s = def.buildBukkitItem();
            }
        }
        catch (Throwable def) {
            // empty catch block
        }
        if (s == null || s.getType() == Material.AIR) {
            s = new ItemStack(fallback);
        }
        if ((m = s.getItemMeta()) != null) {
            m.displayName(MenuText.noI(name));
            if (lore.length > 0) {
                ArrayList<Component> ls = new ArrayList<Component>();
                for (Component c : lore) {
                    ls.add(MenuText.noI(c));
                }
                m.lore(ls);
            }
            s.setItemMeta(m);
        }
        return s;
    }
}

