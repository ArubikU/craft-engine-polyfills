package dev.arubik.craftengine.virtualui.render;

import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link WidgetVisual} for a {@code text_display} — full  hologram-line styling
 * (alignment, line width, opacity, see-through, shadow, background color, billboard, brightness).
 * The three fields CraftEngine's {@code DisplayData} doesn't wrap directly (style flags, opacity,
 * line width) are set via the same targeted reflection into {@code Display.TextDisplay}'s static
 * {@code EntityDataAccessor} fields this codebase already uses (see
 * {@code ContraptionMachineRendererElement#buildMeta}).
 */
public final class TextVisual implements WidgetVisual {

    private final HologramLineConfig config;
    private final Quaternionf rotation;

    public TextVisual(HologramLineConfig config, Quaternionf rotation) {
        this.config = config;
        this.rotation = rotation != null ? rotation : new Quaternionf();
    }

    @Override
    public EntityType<?> entityType() { return EntityType.TEXT_DISPLAY; }

    @Override
    public List<Object> buildMetadata() {
        List<Object> values = new ArrayList<>();
        DisplayData.TextDisplayData.Text.addEntityData(parseText(config != null ? config.text() : ""), values);
        float s = config != null ? config.scale() : 1f;
        DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
        DisplayData.LeftRotation.addEntityData(rotation, values);
        try {
            DisplayData.BillboardConstraints.addEntityData(
                    billboardByte(config != null ? config.billboard() : "CENTER"), values);
        } catch (Throwable ignored) {}
        try {
            DisplayData.TextDisplayData.BackgroundColor.addEntityData(
                    config != null ? config.packedBackgroundColor() : 0, values);
        } catch (Throwable ignored) {}
        addReflectedTextField("DATA_STYLE_FLAGS", (byte) (
                (config != null && config.shadow() ? 1 : 0) | (config != null && config.seeThrough() ? 2 : 0)), values);
        addReflectedTextField("DATA_TEXT_OPACITY", (byte) (config != null ? config.opacity() : 254), values);
        addReflectedTextField("DATA_LINE_WIDTH", config != null ? config.lineWidth() : 200, values);
        int bl = config != null ? config.brightnessBlock() : 15;
        int sl = config != null ? config.brightnessSky() : 15;
        DisplayData.BrightnessOverride.addEntityData((bl << 4) | (sl << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(2, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    @Override
    public boolean sameAs(WidgetVisual other) {
        return other instanceof TextVisual t
                && java.util.Objects.equals(config, t.config)
                && rotation.equals(t.rotation, 1.0e-4f);
    }

    private static void addReflectedTextField(String fieldName, Object value, List<Object> values) {
        try {
            Field f = Display.TextDisplay.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            EntityDataAccessor<Object> acc = (EntityDataAccessor<Object>) f.get(null);
            values.add(SynchedEntityData.DataValue.create(acc, value));
        } catch (Throwable ignored) {}
    }

    /** MiniMessage is this project's text format of choice — {@code <green><bold>...} tags parse
     *  first; legacy {@code &}/{@code §} codes (still used by a couple of Java-side call sites,
     *  e.g. CepCommand's built-in sample) fall back automatically so neither convention silently
     *  shows raw markup. Bad syntax degrades to plain literal text rather than showing nothing. */
    private static net.minecraft.network.chat.Component parseText(String text) {
        String s = text != null ? text : "";
        try {
            net.kyori.adventure.text.Component adv = (s.contains("<") && s.contains(">"))
                    ? MiniMessage.miniMessage().deserialize(s)
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(s.replace('§', '&'));
            return PaperAdventure.asVanilla(adv);
        } catch (Throwable ignored) {
            return net.minecraft.network.chat.Component.literal(s);
        }
    }

    private static byte billboardByte(String name) {
        return switch (name == null ? "CENTER" : name.toUpperCase(java.util.Locale.ROOT)) {
            case "FIXED" -> (byte) 0;
            case "VERTICAL" -> (byte) 1;
            case "HORIZONTAL" -> (byte) 2;
            default -> (byte) 3; // CENTER
        };
    }
}
