package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.DynamicFieldResolver;
import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import dev.arubik.craftengine.virtualui.model.Widget;
import dev.arubik.craftengine.virtualui.model.WidgetRenderContext;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Open/closed extension point mapping a {@link Widget} subtype to the {@link WidgetVisualFactory}
 * that renders it — same "named registry, extend without touching the core" shape as
 * {@code PolyTypeRegistry}. Ships with a factory for every built-in widget kind; a new widget kind
 * just calls {@link #register(Class, WidgetVisualFactory)} once at startup and every existing call
 * site (the renderer, the click system) picks it up automatically — nothing else in this package
 * needs to know that kind exists.
 *
 * <p>Every built-in interactive kind applies a small uniform hover highlight (~12% scale bump) —
 * not baked into {@link WidgetVisual} itself, just each factory choosing to react to the
 * {@code hovered} flag it's handed; a custom widget kind is free to ignore it entirely. Every
 * text/item field also goes through {@link DynamicFieldResolver}/{@link ItemResolution} here, so
 * a script ref or a directly-built {@code ScriptValue.Item} works identically across every kind —
 * see {@code dev.arubik.craftengine.script.types.util.VirtualUIWidgetContextTypes} for the
 * per-kind context type each dynamic field is evaluated against.
 */
public final class WidgetVisualRegistry {

    private static final Map<Class<? extends Widget>, WidgetVisualFactory<?>> FACTORIES = new LinkedHashMap<>();
    private static final float HOVER_SCALE_BUMP = 1.12f;

    private WidgetVisualRegistry() {}

    public static <W extends Widget> void register(Class<W> widgetType, WidgetVisualFactory<W> factory) {
        FACTORIES.put(widgetType, factory);
    }

    @SuppressWarnings("unchecked")
    public static List<PositionedVisual> build(Widget widget, Quaternionf currentRotation, boolean hovered,
                                                Player player, WidgetRenderContext ctx) {
        WidgetVisualFactory<Widget> factory = (WidgetVisualFactory<Widget>) FACTORIES.get(widget.getClass());
        if (factory == null) return List.of();
        return factory.create(widget, currentRotation, hovered, player, ctx);
    }

    private static float bump(float scale, boolean hovered) { return hovered ? scale * HOVER_SCALE_BUMP : scale; }

    private static List<PositionedVisual> one(WidgetVisual visual) { return List.of(PositionedVisual.centered(visual)); }

    private static HologramLineConfig resolveHologram(HologramLineConfig h, Player player, WidgetRenderContext ctx, String typeName) {
        String resolved = DynamicFieldResolver.resolveText(h.text(), player, ctx, typeName);
        return resolved.equals(h.text()) ? h : h.withText(resolved);
    }

    private static ItemStack resolveItem(dev.arubik.craftengine.virtualui.model.ItemSource source,
                                          Player player, WidgetRenderContext ctx, String typeName) {
        return ItemResolution.resolve(source, player, ctx, typeName);
    }

    static {
        register(Widget.ButtonWidget.class, (w, rot, hovered, player, ctx) -> {
            HologramLineConfig text = resolveHologram(w.hologram(), player, ctx, "VUIButton");
            TextVisual textVisual = new TextVisual(hovered ? text.withScaleBump(HOVER_SCALE_BUMP) : text, rot);
            if (w.icon() == null || w.icon().isEmpty()) return one(textVisual);
            ItemStack iconStack = resolveItem(w.icon(), player, ctx, "VUIButton");
            ItemStackVisual icon = new ItemStackVisual(iconStack, bump(w.iconScale(), hovered), rot);
            return List.of(PositionedVisual.centered(textVisual), PositionedVisual.at(icon, w.iconOffsetX(), w.iconOffsetY(), 0));
        });
        register(Widget.LabelWidget.class, (w, rot, hovered, player, ctx) ->
                one(new TextVisual(resolveHologram(w.hologram(), player, ctx, "VUIText"), rot)));
        register(Widget.ImageWidget.class, (w, rot, hovered, player, ctx) ->
                one(new ItemStackVisual(resolveItem(w.item(), player, ctx, "VUIImage"),
                        bump((float) Math.max(w.width(), w.height()), hovered), rot)));
        register(Widget.IconWidget.class, (w, rot, hovered, player, ctx) ->
                one(new ItemStackVisual(resolveItem(w.item(), player, ctx, "VUIIcon"), bump(w.scale(), hovered), rot)));
        register(Widget.ItemWidget.class, (w, rot, hovered, player, ctx) ->
                one(new ItemStackVisual(resolveItem(w.item(), player, ctx, "VUIItem"), bump(w.scale(), hovered), rot)));
        register(Widget.SlotWidget.class, (w, rot, hovered, player, ctx) ->
                one(new ItemStackVisual(resolveItem(w.item(), player, ctx, "VUISlot"),
                        bump((float) Math.max(w.width(), w.height()), hovered), rot)));
        register(Widget.PlayerRenderWidget.class, (w, rot, hovered, player, ctx) ->
                one(new PlayerHeadVisual(w.targetPlayer(), bump(w.scale(), hovered), rot)));
        register(Widget.BlockWidget.class, (w, rot, hovered, player, ctx) -> {
            String blockId = DynamicFieldResolver.resolveText(w.blockId(), player, ctx, "VUIBlock");
            return one(new BlockIdVisual(blockId, bump(w.scale(), hovered), rot));
        });
        // Thumb rendering preference: tracker item icon > custom thumb text (plain MiniMessage,
        // same dynamic ${expr}/.pf: pipeline as any button/label) > generated box-drawing bar
        // (see Widget.ScrollbarWidget's doc). Whichever is picked, it's this one widget's sole
        // rendered entity — the track/groove background is the script's own responsibility to
        // place (a label/image widget behind this one).
        register(Widget.ScrollbarWidget.class, (w, rot, hovered, player, ctx) -> {
            if (w.tracker() != null && !w.tracker().isEmpty()) {
                return one(new ItemStackVisual(resolveItem(w.tracker(), player, ctx, "VUIScrollbar"), bump(0.5f, hovered), rot));
            }
            if (w.thumbText() != null && !w.thumbText().isBlank()) {
                HologramLineConfig style = scrollbarStyle(w, hovered)
                        .withText(DynamicFieldResolver.resolveText(w.thumbText(), player, ctx, "VUIScrollbar"));
                return one(new TextVisual(style, rot));
            }
            return one(new ScrollbarVisual(w.value(), scrollbarStyle(w, hovered), rot));
        });
        // Non-interactive fill gauge — tracker icon (if set) renders centered as a static needle/
        // indicator overlay (Create's GaugeIcon doesn't slide either, just re-textures per state);
        // otherwise falls back to the same generated bar shape as ScrollbarVisual, fully lit up to
        // "value" instead of a single moving thumb.
        register(Widget.ProgressWidget.class, (w, rot, hovered, player, ctx) -> {
            if (w.tracker() != null && !w.tracker().isEmpty()) {
                return one(new ItemStackVisual(resolveItem(w.tracker(), player, ctx, "VUIProgress"), bump(0.6f, hovered), rot));
            }
            HologramLineConfig style = HologramLineConfig.text("", w.offsetX(), w.offsetY(), w.offsetZ(), bump(0.8f, hovered));
            return one(new ProgressVisual(w.value(), style, rot));
        });
        register(Widget.ToggleWidget.class, (w, rot, hovered, player, ctx) -> {
            HologramLineConfig label = w.value() ? w.onLabel() : w.offLabel();
            HologramLineConfig text = resolveHologram(label, player, ctx, "VUIToggle");
            TextVisual textVisual = new TextVisual(hovered ? text.withScaleBump(HOVER_SCALE_BUMP) : text, rot);
            dev.arubik.craftengine.virtualui.model.ItemSource icon = w.value() ? w.onIcon() : w.offIcon();
            if (icon == null || icon.isEmpty()) return one(textVisual);
            ItemStack iconStack = resolveItem(icon, player, ctx, "VUIToggle");
            ItemStackVisual iconVisual = new ItemStackVisual(iconStack, bump(w.iconScale(), hovered), rot);
            return List.of(PositionedVisual.centered(textVisual), PositionedVisual.at(iconVisual, w.iconOffsetX(), w.iconOffsetY(), 0));
        });
        register(Widget.SelectWidget.class, (w, rot, hovered, player, ctx) -> {
            HologramLineConfig text = resolveHologram(w.template(), player, ctx, "VUISelect");
            return one(new TextVisual(hovered ? text.withScaleBump(HOVER_SCALE_BUMP) : text, rot));
        });
    }

    private static HologramLineConfig scrollbarStyle(Widget.ScrollbarWidget w, boolean hovered) {
        float scale = bump(0.8f, hovered);
        return HologramLineConfig.text("", w.offsetX(), w.offsetY(), w.offsetZ(), scale);
    }
}
