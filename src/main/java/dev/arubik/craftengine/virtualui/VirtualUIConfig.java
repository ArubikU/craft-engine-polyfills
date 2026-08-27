package dev.arubik.craftengine.virtualui;

import dev.arubik.craftengine.virtualui.model.CursorStateConfig;
import dev.arubik.craftengine.virtualui.model.HologramLineConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Global VirtualUI engine tuning ({@code virtualui.yml}) — cursor sensitivity, camera distance,
 * the cursor glyph's own appearance. Deliberately holds NO UI content (no buttons, no holograms,
 * no groups) — every VirtualUI screen is built entirely by a script via {@code VirtualUI}, never
 * by editing a config file. Singleton, reloadable via {@link #load}.
 */
public final class VirtualUIConfig {

    private static VirtualUIConfig INSTANCE = new VirtualUIConfig();

    private double sensitivityX = 0.1;
    private double sensitivityY = 0.1;
    private double maxOffsetX = 5.0;
    private double maxOffsetY = 3.0;
    private double cursorDistance = 2.0;
    private HologramLineConfig cursorDisplay = HologramLineConfig.text("<white>+", 0, 0, 0, 0.8f);
    private double cameraDistance = 10.0;
    private boolean playerInvisibleOnStart = true;
    /** Total allowed head-turn (degrees) around wherever the player was looking the instant a
     *  screen opened, split per axis since yaw (full circle available) and pitch (naturally capped
     *  at ±90 by the client itself) don't need the same range — the client's real rotation is never
     *  frozen (see {@code VirtualUIMovementPacketListener}, which lets rotation packets through
     *  untouched by default), but exceeding either deviation gets that axis clamped back to the
     *  edge of its own cone. 0 disables the clamp entirely for that axis. */
    private double maxYawDegrees = 125.0;
    private double maxPitchDegrees = 125.0;
    /** How much closer than {@code cursor.distance} the cursor glyph/click-marker/tooltip render —
     *  guarantees they're always in front of every widget (see {@code CURSOR_FRONT_MARGIN}'s old
     *  doc in VirtualUICameraSystem, now here). */
    private double cursorFrontMargin = 0.5;
    /** World-unit width/height of the invisible {@code EntityType.INTERACTION} click-detection
     *  marker (see {@code CameraSession#clickMarkerEntityId()}'s doc) — bigger is more forgiving
     *  against any tiny basis mismatch, costs nothing since it's invisible. */
    private double clickMarkerSize = 3.0;
    /** Vanilla or CraftEngine item id (e.g. an invisible/transparent custom item) CLIENT-ONLY
     *  swapped into the player's held hotbar+offhand slots while a VirtualUI is open, so their real
     *  held item never visually clutters the view — {@code null}/blank disables this (real gamemode
     *  and inventory are never touched either way). See {@code VirtualUICameraSystem#show}. */
    private String hideHeldItemAs = null;

    // --- predictive cursor (port of 's cursor-prediction config) ---
    private boolean predictionEnabled = true;
    private double predictionStrength = 0.12;   // how many ticks of velocity to extrapolate ahead
    private double predictionSmoothing = 0.35;  // exponential-smoothing alpha applied to velocity

    // --- cursor-state provider (port of 's cursor: normal/hover/hold/scroll_*/processing)
    // ---
    // named alternate cursor appearances, switched automatically by VirtualUICameraSystem based on
    // hover/drag context (or forced via VirtualUI.set_cursor_state) — see CursorStateConfig.
    private final Map<String, CursorStateConfig> cursorStates = new HashMap<>();

    private VirtualUIConfig() {}

    public static VirtualUIConfig get() { return INSTANCE; }

    public static void load(File dataFolder, ClassLoader classLoader) {
        VirtualUIConfig cfg = new VirtualUIConfig();
        YamlConfiguration main = loadYaml(new File(dataFolder, "virtualui.yml"), "virtualui.yml", classLoader);
        cfg.sensitivityX = main.getDouble("cursor.sensitivity_x", 0.1);
        cfg.sensitivityY = main.getDouble("cursor.sensitivity_y", 0.1);
        cfg.maxOffsetX = main.getDouble("cursor.max_offset_x", 5.0);
        cfg.maxOffsetY = main.getDouble("cursor.max_offset_y", 3.0);
        cfg.cursorDistance = main.getDouble("cursor.distance", 2.0);
        cfg.cameraDistance = main.getDouble("camera.distance", 10.0);
        cfg.playerInvisibleOnStart = main.getBoolean("camera.player_invisible_on_start", true);
        // "max_rotation_degrees" (old, single-value key) still works as a fallback default for
        // BOTH axes when the new split keys aren't set, so existing configs don't silently reset.
        double legacyBoth = main.getDouble("camera.max_rotation_degrees", 125.0);
        cfg.maxYawDegrees = main.getDouble("camera.max_yaw_degrees", legacyBoth);
        cfg.maxPitchDegrees = main.getDouble("camera.max_pitch_degrees", legacyBoth);
        cfg.cursorFrontMargin = main.getDouble("cursor.front_margin", 0.5);
        cfg.clickMarkerSize = main.getDouble("cursor.click_marker_size", 3.0);
        cfg.hideHeldItemAs = main.getString("camera.hide_held_item_as", null);
        cfg.predictionEnabled = main.getBoolean("cursor.prediction.enabled", true);
        cfg.predictionStrength = main.getDouble("cursor.prediction.strength", 0.12);
        cfg.predictionSmoothing = main.getDouble("cursor.prediction.smoothing", 0.35);
        var displaySec = main.getConfigurationSection("cursor.display");
        if (displaySec != null) {
            cfg.cursorDisplay = new HologramLineConfig(
                    displaySec.getString("text", "<white>+"), 0, 0, 0,
                    (float) displaySec.getDouble("scale", 0.8), 0, 0, 0, true,
                    displaySec.getString("alignment", "CENTER"),
                    displaySec.getInt("line_width", 200),
                    Math.min(254, displaySec.getInt("opacity", 254)),
                    displaySec.getString("billboard", "CENTER"),
                    displaySec.getBoolean("see_through", false),
                    displaySec.getBoolean("shadow", false),
                    displaySec.getInt("background_r", 0), displaySec.getInt("background_g", 0),
                    displaySec.getInt("background_b", 0), displaySec.getInt("background_a", 0),
                    displaySec.getInt("brightness_block", 15), displaySec.getInt("brightness_sky", 15));
        }
        var statesSec = main.getConfigurationSection("cursor.states");
        if (statesSec != null) {
            for (String name : statesSec.getKeys(false)) {
                ConfigurationSection stateSec = statesSec.getConfigurationSection(name);
                if (stateSec == null) continue;
                cfg.cursorStates.put(name, readCursorState(stateSec));
            }
        }
        INSTANCE = cfg;
    }

    /** A cursor state is either {@code item: <id>} or the same text-hologram fields as
     *  {@code cursor.display} (text/scale/alignment/...); {@code item} wins if both are present. */
    private static CursorStateConfig readCursorState(ConfigurationSection sec) {
        String itemId = sec.getString("item", null);
        if (itemId != null && !itemId.isBlank()) {
            return new CursorStateConfig(null, itemId);
        }
        HologramLineConfig text = new HologramLineConfig(
                sec.getString("text", "<white>+"), 0, 0, 0,
                (float) sec.getDouble("scale", 0.8), 0, 0, 0, true,
                sec.getString("alignment", "CENTER"),
                sec.getInt("line_width", 200),
                Math.min(254, sec.getInt("opacity", 254)),
                sec.getString("billboard", "CENTER"),
                sec.getBoolean("see_through", false),
                sec.getBoolean("shadow", false),
                sec.getInt("background_r", 0), sec.getInt("background_g", 0),
                sec.getInt("background_b", 0), sec.getInt("background_a", 0),
                sec.getInt("brightness_block", 15), sec.getInt("brightness_sky", 15));
        return new CursorStateConfig(text, null);
    }

    private static YamlConfiguration loadYaml(File file, String resourceName, ClassLoader classLoader) {
        if (file.exists()) return YamlConfiguration.loadConfiguration(file);
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            return is != null
                    ? YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8))
                    : new YamlConfiguration();
        } catch (Throwable t) {
            return new YamlConfiguration();
        }
    }

    public double sensitivityX() { return sensitivityX; }
    public double sensitivityY() { return sensitivityY; }
    public double maxOffsetX() { return maxOffsetX; }
    public double maxOffsetY() { return maxOffsetY; }
    public double cursorDistance() { return cursorDistance; }
    public HologramLineConfig cursorDisplay() { return cursorDisplay; }
    public double cameraDistance() { return cameraDistance; }
    public boolean playerInvisibleOnStart() { return playerInvisibleOnStart; }
    public double maxYawDegrees() { return maxYawDegrees; }
    public double maxPitchDegrees() { return maxPitchDegrees; }
    public double cursorFrontMargin() { return cursorFrontMargin; }
    public double clickMarkerSize() { return clickMarkerSize; }
    public String hideHeldItemAs() { return hideHeldItemAs; }
    public boolean predictionEnabled() { return predictionEnabled; }
    public double predictionStrength() { return predictionStrength; }
    public double predictionSmoothing() { return predictionSmoothing; }

    /** The configured appearance for named cursor state {@code name} (e.g. {@code "hover"},
     *  {@code "scroll_vertical"}, {@code "processing"}), or {@code null} if not configured —
     *  callers fall back to {@code "normal"} then the engine's built-in default glyph. */
    public CursorStateConfig cursorState(String name) { return name == null ? null : cursorStates.get(name); }
}
