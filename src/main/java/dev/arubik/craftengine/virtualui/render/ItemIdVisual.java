package dev.arubik.craftengine.virtualui.render;

import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.Objects;

/** Renders an item id — backs {@code ImageWidget}/{@code IconWidget}/{@code ItemWidget}/
 *  {@code SlotWidget}. Resolves through CraftEngine's own item registry FIRST (so a widget can
 *  show a CraftEngine custom item, e.g. {@code "cml:crate_acacia"}), falling back to vanilla —
 *  see {@link ItemResolution}. */
public final class ItemIdVisual extends AbstractItemVisual {

    private final String itemId;

    public ItemIdVisual(String itemId, float scale, Quaternionf rotation) {
        super(scale, rotation);
        this.itemId = itemId;
    }

    @Override
    protected ItemStack resolveItemStack() {
        return ItemResolution.resolve(itemId);
    }

    @Override
    protected boolean sameItemAs(AbstractItemVisual other) {
        return other instanceof ItemIdVisual o && Objects.equals(itemId, o.itemId);
    }
}
