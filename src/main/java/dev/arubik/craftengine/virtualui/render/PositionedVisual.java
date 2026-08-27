package dev.arubik.craftengine.virtualui.render;

/**
 * One {@link WidgetVisual} plus its own small offset from the OWNING widget's anchor position —
 * lets a single widget render as more than one packet entity (an icon sitting next to its text
 * label, a scrollbar's moving thumb next to a static track, ...) while every OTHER part of the
 * engine (hit-testing, hover state, the click system) still only ever deals with the widget as
 * one interactive unit. {@code dx}/{@code dy}/{@code dz} are in the same "world units at camera
 * distance" space as everything else, relative to the widget's own {@code offsetX/Y/Z}.
 */
public record PositionedVisual(WidgetVisual visual, double dx, double dy, double dz) {

    public static PositionedVisual at(WidgetVisual visual, double dx, double dy, double dz) {
        return new PositionedVisual(visual, dx, dy, dz);
    }

    public static PositionedVisual centered(WidgetVisual visual) {
        return new PositionedVisual(visual, 0, 0, 0);
    }
}
