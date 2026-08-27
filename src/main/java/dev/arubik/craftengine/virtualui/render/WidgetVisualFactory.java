package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.model.Widget;
import dev.arubik.craftengine.virtualui.model.WidgetRenderContext;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;

import java.util.List;

/** Builds the current-tick render PARTS for one widget instance (almost always exactly one — see
 *  {@link PositionedVisual}). Registered per {@link Widget} subtype in
 *  {@link WidgetVisualRegistry} — this is the plug point a brand-new widget kind implements to
 *  render, without touching any existing widget's code. {@code hovered} is whether the cursor is
 *  currently over this widget — a factory is free to ignore it (most built-in kinds apply a small
 *  highlight scale/color bump; see {@link WidgetVisualRegistry}'s own registrations for the
 *  convention). {@code player}/{@code renderCtx} are what a dynamic (script-ref) text/item field
 *  resolves against — see {@code dev.arubik.craftengine.virtualui.DynamicFieldResolver}. */
@FunctionalInterface
public interface WidgetVisualFactory<W extends Widget> {
    List<PositionedVisual> create(W widget, Quaternionf currentRotation, boolean hovered, Player player, WidgetRenderContext renderCtx);
}
