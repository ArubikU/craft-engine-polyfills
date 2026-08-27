package dev.arubik.craftengine.virtualui.render;

import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.Objects;

/** Renders an already-resolved NMS {@link ItemStack} — the {@link ItemSource}-aware counterpart
 *  to {@link ItemIdVisual} (which only ever takes a bare id string). Resolution (CraftEngine
 *  registry, vanilla fallback, a literal script-built stack, or a dynamic script ref) happens
 *  once per render tick in {@code WidgetVisualRegistry} via {@code ItemResolution#resolve}; this
 *  class just displays whatever it's handed. */
public final class ItemStackVisual extends AbstractItemVisual {

    private final ItemStack stack;

    public ItemStackVisual(ItemStack stack, float scale, Quaternionf rotation) {
        super(scale, rotation);
        this.stack = stack;
    }

    @Override
    protected ItemStack resolveItemStack() { return stack; }

    @Override
    protected boolean sameItemAs(AbstractItemVisual other) {
        return other instanceof ItemStackVisual o && Objects.equals(stack, o.stack);
    }
}
