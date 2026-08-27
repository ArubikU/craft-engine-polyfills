package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;

import java.util.List;

/**
 * {@link WidgetVisual} for {@link dev.arubik.craftengine.virtualui.model.Widget.ScrollbarWidget} —
 * composed from a {@link TextVisual} rendering a generated track string (a row of box-drawing
 * characters with the thumb position highlighted), rather than a second display entity, keeping
 * one widget = one packet entity for every widget kind uniformly.
 */
public final class ScrollbarVisual implements WidgetVisual {

    private static final int SEGMENTS = 12;

    private final TextVisual delegate;
    private final double value;

    public ScrollbarVisual(double value, HologramLineConfig style, Quaternionf rotation) {
        this.value = Math.max(0.0, Math.min(1.0, value));
        HologramLineConfig withBar = (style != null ? style : HologramLineConfig.text("", 0, 0, 0, 0.8f))
                .withText(track(this.value));
        this.delegate = new TextVisual(withBar, rotation);
    }

    private static String track(double value) {
        int thumb = (int) Math.round(value * (SEGMENTS - 1));
        StringBuilder sb = new StringBuilder("<gray>[");
        for (int i = 0; i < SEGMENTS; i++) {
            sb.append(i == thumb ? "<yellow>▰</yellow>" : "▱");
        }
        sb.append("<gray>]");
        return sb.toString();
    }

    @Override
    public EntityType<?> entityType() { return delegate.entityType(); }

    @Override
    public List<Object> buildMetadata() { return delegate.buildMetadata(); }

    @Override
    public boolean sameAs(WidgetVisual other) {
        return other instanceof ScrollbarVisual s && Math.abs(value - s.value) < 1.0e-3 && delegate.sameAs(s.delegate);
    }
}
