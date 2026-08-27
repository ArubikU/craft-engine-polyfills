package dev.arubik.craftengine.virtualui.render;

import net.minecraft.world.entity.EntityType;

import java.util.List;

/**
 * The "how does this widget kind actually render" strategy — the extension point that keeps
 * {@link dev.arubik.craftengine.virtualui.VirtualUIWidgetRenderer} closed for modification (packet
 * spawn/move/despawn mechanics never change) while open for extension (a brand-new widget kind
 * plugs in its own {@link WidgetVisual} implementation + a {@link WidgetVisualFactory} registration,
 * touching no existing class).
 */
public interface WidgetVisual {

    /** Which display entity type this visual needs (text_display, item_display, ...). Must be
     *  stable for the lifetime of one renderer instance — a widget that needs to change entity
     *  type dynamically should be re-created, not mutated. */
    EntityType<?> entityType();

    /** Builds this tick's full {@code SynchedEntityData} value list for the display entity. */
    List<Object> buildMetadata();

    /** Cheap equality so the renderer only resends metadata packets when something actually
     *  changed — mirrors every other packet-display class in this codebase's dirty-tracking. */
    boolean sameAs(WidgetVisual other);
}
