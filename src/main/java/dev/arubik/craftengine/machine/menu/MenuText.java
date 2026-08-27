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

    /** Denominations this project's bundled negative/positive-space title glyphs come in — see
     *  {@code testserver/plugins/CraftEngine/resources/internal/configuration/offset_chars.yml}
     *  ({@code internal:neg_1..neg_16}/{@code neg_32/48/64/128/256}, mirrored by {@code pos_*}).
     *  Largest-first so {@link #spaceRun} always greedily reaches for the biggest glyph that still
     *  fits, minimizing how many characters get chained together for a given magnitude. */
    private static final int[] SHIFT_DENOMS = {256, 128, 64, 48, 32, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    /** The single character for one glyph of the given magnitude (1..16, or 32/48/64/128/256) and
     *  direction — {@code neg_*} run 0xF800..0xF80F then 0xF810..0xF814 for the "big" sizes,
     *  {@code pos_*} the same shape starting at 0xF830/0xF840. */
    private static char shiftChar(int magnitude, boolean negative) {
        if (magnitude <= 16) return (char) ((negative ? 0xF800 : 0xF830) + magnitude - 1);
        int bigIndex = switch (magnitude) { case 32 -> 0; case 48 -> 1; case 64 -> 2; case 128 -> 3; case 256 -> 4; default -> throw new IllegalArgumentException("no glyph for magnitude " + magnitude); };
        return (char) ((negative ? 0xF810 : 0xF840) + bigIndex);
    }

    /** Builds an arbitrary-magnitude cursor shift by greedily chaining {@link #SHIFT_DENOMS}
     *  glyphs (e.g. 129 → one neg_128 + one neg_1) — NOT capped to a single character's 16px max,
     *  unlike the old implementation this replaced. {@code shiftPx < 0} moves the cursor left
     *  ({@code neg_*} glyphs, the common case — matches Nexo's {@code %nexo_shift_-n%}); {@code
     *  shiftPx > 0} moves it right ({@code pos_*}). */
    private static Component spaceRun(int shiftPx) {
        if (shiftPx == 0) return Component.empty();
        boolean negative = shiftPx < 0;
        int remaining = Math.abs(shiftPx);
        net.kyori.adventure.key.Key font = net.kyori.adventure.key.Key.key("minecraft", "default");
        Component out = null;
        for (int d : SHIFT_DENOMS) {
            while (remaining >= d) {
                Component seg = Component.text(String.valueOf(shiftChar(d, negative))).font(font);
                out = out == null ? seg : out.append(seg);
                remaining -= d;
            }
        }
        return out == null ? Component.empty() : out;
    }

    /** A bare cursor shift with NO image glyph — lets a title move the cursor mid-string (between
     *  two plain text/image segments) the way Nexo's bare {@code %nexo_shift_n%} placeholder does,
     *  instead of only ever being able to shift right before an {@code Images.from(...)} glyph. */
    public static Component shiftOnly(int shiftPx) {
        return spaceRun(shiftPx);
    }

    public static Component imageTitle(String imageId, int shiftPx) {
        try {
            Image img = CraftEngineImages.byId((Key)Key.of((String)imageId));
            if (img == null) {
                return null;
            }
            String imageMm = img.miniMessageAt(0, 0);
            Component out = MiniMessage.miniMessage().deserialize(imageMm).colorIfAbsent((TextColor)NamedTextColor.WHITE);
            out = spaceRun(shiftPx).append(out);
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
        return MenuText.applyNameLore(s, name, lore);
    }

    /**
     * Sibling of {@link #iconItem(Key, Material, Component, Component...)} that starts from an
     * ALREADY-BUILT {@code ItemStack} (e.g. a real player-skin head from a script's {@code
     * Item.create(...).with_profile(...)}) instead of resolving a fresh one from a {@code Key} —
     * preserves everything already on the stack (skin profile, custom model data, enchants, ...)
     * while still applying the same name/lore/tooltip-hiding treatment as the {@code Key}-based
     * overload, so a generator-supplied full item behaves identically once displayed. See {@code
     * MachineDefinition.ButtonSpec#customIcon()} / {@code PageDef.StaticSlot#customIcon()}.
     */
    public static ItemStack iconItem(ItemStack baseStack, Component name, Component ... lore) {
        ItemStack s = baseStack != null ? baseStack.clone() : new ItemStack(Material.PAPER);
        return MenuText.applyNameLore(s, name, lore);
    }

    /** Shared name/lore application for both {@code iconItem} overloads above. */
    private static ItemStack applyNameLore(ItemStack s, Component name, Component ... lore) {
        ItemMeta m = s.getItemMeta();
        if (m != null) {
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

