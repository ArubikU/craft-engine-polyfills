package dev.arubik.craftengine.script.event;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.CraftItemEvent;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fires instead of {@link InventoryClickWrapper} when the click is specifically on a crafting
 * table's result slot — everything {@code InventoryClickEvent} has (via inheriting
 * {@link InventoryClickWrapper}: {@link #slot()}, {@link #whoClicked()}, ...) plus
 * {@link #recipeResult()}. A script could use this to grant bonus XP whenever a player crafts a
 * specific recipe, comparing {@link #recipeResult()}'s item id against a configured list.
 */
public final class CraftItemWrapper extends InventoryClickWrapper {
    public CraftItemWrapper(CraftItemEvent raw) {
        super("CraftItemEvent", raw);
    }

    @Override
    public CraftItemEvent raw() { return (CraftItemEvent) super.raw(); }

    /** The item this recipe produces (same for every click on this slot until the recipe changes). */
    public ScriptValue recipeResult() {
        return ScriptValue.ofItem(CraftItemStack.asNMSCopy(raw().getRecipe().getResult()));
    }
}
