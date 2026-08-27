package dev.arubik.craftengine.virtualui.render;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Template-method base for every {@code item_display}-backed {@link WidgetVisual}
 * ({@link ItemIdVisual}, {@link PlayerHeadVisual}, and any future kind — a "block preview" or
 * "custom-model image" visual only needs to extend this and implement {@link #resolveItemStack()}
 * + {@link #sameItemAs(AbstractItemVisual)}, inheriting scale/rotation/brightness/interpolation
 * handling for free).
 */
public abstract class AbstractItemVisual implements WidgetVisual {

    protected final float scale;
    protected final Quaternionf rotation;

    protected AbstractItemVisual(float scale, Quaternionf rotation) {
        this.scale = scale;
        // Item displays with CENTER billboarding face the flat "generated" icon's BACK toward the
        // viewer by default (a documented quirk of billboard + LeftRotation interacting) — bake in a
        // 180-degree yaw flip so the icon reads face-on instead of mirrored/backwards.
        this.rotation = (rotation != null ? new Quaternionf(rotation) : new Quaternionf())
                .rotateY((float) Math.PI);
    }

    @Override
    public final EntityType<?> entityType() { return EntityType.ITEM_DISPLAY; }

    @Override
    public final List<Object> buildMetadata() {
        List<Object> values = new ArrayList<>();
        ItemStack stack = resolveItemStack();
        if (stack != null) {
            try { DisplayData.ItemDisplayData.ItemStack.addEntityData(stack, values); } catch (Throwable ignored) {}
        }
        DisplayData.Scale.addEntityData(new Vector3f(scale, scale, scale), values);
        DisplayData.LeftRotation.addEntityData(rotation, values);
        // Item displays default to BILLBOARD_FIXED (no auto-facing) — a flat "generated" icon quad
        // then only looks right from the one world-axis it happens to face, and edge-on (paper-thin)
        // from 90 degrees off. Every widget/cursor icon here is meant to read as a flat UI sprite
        // that always faces the viewer, so force CENTER billboarding, same as TextVisual already does.
        try { DisplayData.BillboardConstraints.addEntityData((byte) 3, values); } catch (Throwable ignored) {}
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    @Override
    public final boolean sameAs(WidgetVisual other) {
        return other instanceof AbstractItemVisual v
                && Math.abs(scale - v.scale) < 1.0e-4f
                && rotation.equals(v.rotation, 1.0e-4f)
                && getClass() == v.getClass()
                && sameItemAs(v);
    }

    /** Resolves the NMS {@link ItemStack} to show right now. May return {@code null} (renders empty). */
    protected abstract ItemStack resolveItemStack();

    /** Subclass-specific equality of "what item" (excluding scale/rotation, already compared). */
    protected abstract boolean sameItemAs(AbstractItemVisual other);
}
