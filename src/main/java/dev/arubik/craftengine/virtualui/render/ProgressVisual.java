package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;

import java.util.List;

/**
 * {@link WidgetVisual} for {@link dev.arubik.craftengine.virtualui.model.Widget.ProgressWidget} —
 * a continuous fill bar (every filled segment lit, unlike {@link ScrollbarVisual}'s single moving
 * thumb), used when the widget has no {@code tracker} item icon set. Same one-entity-per-widget
 * shape as every other text-backed visual here.
 */
public final class ProgressVisual implements WidgetVisual {

    private static final int SEGMENTS = 12;

    private final TextVisual delegate;
    private final double value;

    public ProgressVisual(double value, HologramLineConfig style, Quaternionf rotation) {
        this.value = Math.max(0.0, Math.min(1.0, value));
        HologramLineConfig withBar = (style != null ? style : HologramLineConfig.text("", 0, 0, 0, 0.8f))
                .withText(bar(this.value));
        this.delegate = new TextVisual(withBar, rotation);
    }

    private static String bar(double value) {
        int filled = (int) Math.round(value * SEGMENTS);
        StringBuilder sb = new StringBuilder("<gray>[<green>");
        for (int i = 0; i < SEGMENTS; i++) {
            if (i == filled) sb.append("</green><dark_gray>");
            sb.append("▰");
        }
        sb.append("</dark_gray><gray>]");
        return sb.toString();
    }

    @Override
    public EntityType<?> entityType() { return delegate.entityType(); }

    @Override
    public List<Object> buildMetadata() { return delegate.buildMetadata(); }

    @Override
    public boolean sameAs(WidgetVisual other) {
        return other instanceof ProgressVisual p && Math.abs(value - p.value) < 1.0e-3 && delegate.sameAs(p.delegate);
    }
}
