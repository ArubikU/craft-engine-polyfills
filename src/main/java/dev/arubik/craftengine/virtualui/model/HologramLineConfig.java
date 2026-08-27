package dev.arubik.craftengine.virtualui.model;

/**
 * Full text-display styling for one hologram line/widget label — a direct port of 's
 * hologram line schema (text, offset, scale, rotation, billboard, alignment, line width, opacity,
 * see-through, shadow, background color, brightness). Immutable; widgets that need to mutate
 * their text create a new instance via {@link #withText}.
 */
public record HologramLineConfig(
        String text,
        double offsetX, double offsetY, double offsetZ,
        float scale,
        float rotationX, float rotationY, float rotationZ,
        boolean visible,
        String alignment,      // CENTER, LEFT, RIGHT
        int lineWidth,
        int opacity,           // 0-254
        String billboard,      // CENTER, HORIZONTAL, VERTICAL, FIXED
        boolean seeThrough,
        boolean shadow,
        int backgroundR, int backgroundG, int backgroundB, int backgroundA,
        int brightnessBlock, int brightnessSky
) {

    public static HologramLineConfig text(String text, double offsetX, double offsetY, double offsetZ, float scale) {
        return new HologramLineConfig(text, offsetX, offsetY, offsetZ, scale, 0f, 0f, 0f, true,
                "CENTER", 200, 254, "CENTER", false, true, 0, 0, 0, 0, 15, 15);
    }

    public HologramLineConfig withText(String newText) {
        return new HologramLineConfig(newText, offsetX, offsetY, offsetZ, scale, rotationX, rotationY, rotationZ,
                visible, alignment, lineWidth, opacity, billboard, seeThrough, shadow,
                backgroundR, backgroundG, backgroundB, backgroundA, brightnessBlock, brightnessSky);
    }

    public HologramLineConfig withVisible(boolean v) {
        return new HologramLineConfig(text, offsetX, offsetY, offsetZ, scale, rotationX, rotationY, rotationZ,
                v, alignment, lineWidth, opacity, billboard, seeThrough, shadow,
                backgroundR, backgroundG, backgroundB, backgroundA, brightnessBlock, brightnessSky);
    }

    /** Multiplies {@code scale} by {@code factor} — used for the built-in hover highlight (see
     *  {@code WidgetVisualRegistry}); leaves every other field untouched. */
    public HologramLineConfig withScaleBump(float factor) {
        return new HologramLineConfig(text, offsetX, offsetY, offsetZ, scale * factor, rotationX, rotationY, rotationZ,
                visible, alignment, lineWidth, opacity, billboard, seeThrough, shadow,
                backgroundR, backgroundG, backgroundB, backgroundA, brightnessBlock, brightnessSky);
    }

    public HologramLineConfig withOffset(double x, double y, double z) {
        return new HologramLineConfig(text, x, y, z, scale, rotationX, rotationY, rotationZ,
                visible, alignment, lineWidth, opacity, billboard, seeThrough, shadow,
                backgroundR, backgroundG, backgroundB, backgroundA, brightnessBlock, brightnessSky);
    }

    /** Packed ARGB background color for {@code DisplayData.TextDisplayData.BackgroundColor}. */
    public int packedBackgroundColor() {
        return (backgroundA << 24) | (backgroundR << 16) | (backgroundG << 8) | backgroundB;
    }
}
