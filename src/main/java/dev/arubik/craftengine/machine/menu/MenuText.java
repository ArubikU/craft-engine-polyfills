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

    // ---- shared upgrade-menu UI (transparent filler + custom icons + panel image) ----

    /** Invisible, tooltip-less filler so only the UI image shows in background slots. */
    public static ItemStack emptyFiller() {
        ItemStack s = null;
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems
                    .byId(net.momirealms.craftengine.core.util.Key.of("cml", "gui_empty"));
            if (def != null)
                s = def.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        if (s == null || s.getType() == Material.AIR)
            s = new ItemStack(Material.PAPER);
        ItemMeta m = s.getItemMeta();
        if (m != null) {
            try {
                m.setHideTooltip(true);
            } catch (Throwable ignored) {
            }
            m.displayName(Component.empty());
            s.setItemMeta(m);
        }
        return s;
    }

    /** The "back" navigation icon (cml:return_icon, falls back to an arrow). */
    public static ItemStack backIcon() {
        return iconItem(net.momirealms.craftengine.core.util.Key.of("cml", "return_icon"), Material.ARROW,
                tr("polyfill.ui.back", NamedTextColor.YELLOW));
    }

    /** A locked-slot / locked-feature icon (cml:locked_icon, falls back to a barrier). */
    public static ItemStack lockedIcon(Component name, Component... lore) {
        return iconItem(net.momirealms.craftengine.core.util.Key.of("cml", "locked_icon"), Material.BARRIER, name, lore);
    }

    /**
     * Build an inventory-title {@link Component} that renders a CraftEngine GUI image glyph,
     * resolved dynamically by image id (e.g. {@code cml:copper_furnace_gui}). The image is nudged
     * left by {@code shiftPx} pixels (a negative value moves it left) using CraftEngine's own
     * negative-space offset glyphs.
     *
     * <p>Implementation notes (verified against the decompiled CraftEngine 26 source):</p>
     * <ul>
     *   <li>{@code CraftEngineImages.byId(Key)} resolves the image; {@code Image.miniMessageAt(0,0)}
     *       returns a MiniMessage string of the form {@code <font:minecraft:internal>X</font>}.</li>
     *   <li>The {@code <shift:>} tag is NOT usable: {@code AdventureHelper.customMiniMessage()} is built
     *       with {@code TagResolver.empty()} (no tags at all — not even {@code <font>}). CraftEngine
     *       instead renders offsets as REAL negative-space glyphs via
     *       {@code FontManager.createMiniMessageOffsets(px)}. We use that to build the left-nudge,
     *       then deserialize the combined string with the STANDARD {@code MiniMessage.miniMessage()}
     *       (which supports the {@code <font>} tag both strings use).</li>
     * </ul>
     *
     * @return a kyori {@link Component}, or {@code null} on any failure (unknown image, etc.) so the
     *         caller falls back to its existing text title.
     */
    public static Component imageTitle(String imageId, int shiftPx) {
        try {
            var img = net.momirealms.craftengine.bukkit.api.CraftEngineImages
                    .byId(net.momirealms.craftengine.core.util.Key.of(imageId));
            if (img == null)
                return null; // image not (yet) defined -> caller uses its text title
            // miniMessageAt(0,0) returns "<font:ns:path>X</font>" — a plain String, parseable by the
            // NATIVE kyori MiniMessage (which supports <font>). We avoid CraftEngine's shaded adventure
            // types entirely so this compiles against the public API.
            String imageMm = img.miniMessageAt(0, 0);
            // Force WHITE so the glyph renders at full colour (titles default to black otherwise).
            Component out = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(imageMm)
                    .colorIfAbsent(NamedTextColor.WHITE);
            // Left nudge via CraftEngine's internal negative-space glyphs (internal:neg_N = U+F800+(N-1),
            // font minecraft:default; neg_8 = U+F807). shiftPx<0 moves the image left.
            int px = Math.max(0, Math.min(16, -shiftPx));
            if (px > 0) {
                Component neg = Component.text(String.valueOf((char) (0xF800 + px - 1)))
                        .font(net.kyori.adventure.key.Key.key("minecraft", "default"));
                out = neg.append(out);
            }
            return noI(out);
        } catch (Throwable ignored) {
            return null; // any failure -> caller uses its text title
        }
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

    /** {@code <label>: <valueComponent>} — use when the value is itself a (translatable) component. */
    public static Component kv(String key, NamedTextColor keyColor, Component value, NamedTextColor valColor) {
        return tr(key, keyColor).append(lit(": ", valColor)).append(value.colorIfAbsent(valColor));
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

    /**
     * Like {@link #icon(Material, Component, Component...)} but the base is a CraftEngine
     * custom item (so the model/texture come from the resource pack). Falls back to
     * {@code fallback} if the custom id is unknown.
     */
    public static ItemStack iconItem(net.momirealms.craftengine.core.util.Key id, Material fallback,
            Component name, Component... lore) {
        ItemStack s = null;
        try {
            var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(id);
            if (def != null)
                s = def.buildBukkitItem();
        } catch (Throwable ignored) {
        }
        if (s == null || s.getType() == Material.AIR)
            s = new ItemStack(fallback);
        ItemMeta m = s.getItemMeta();
        if (m != null) {
            m.displayName(noI(name));
            if (lore.length > 0) {
                List<Component> ls = new ArrayList<>();
                for (Component c : lore)
                    ls.add(noI(c));
                m.lore(ls);
            }
            s.setItemMeta(m);
        }
        return s;
    }
}
