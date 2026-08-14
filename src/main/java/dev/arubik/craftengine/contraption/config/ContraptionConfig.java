package dev.arubik.craftengine.contraption.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ContraptionConfig {

    private static ContraptionConfig INSTANCE = new ContraptionConfig();

    private String itemFrameModel = "";

    // --- entity culling ---
    private boolean entityCullingEnabled  = true;
    private double  entityCullingDistance = 64.0;
    private boolean entityCullingFrustum     = false; // off by default
    private double  entityCullingFovDegrees  = 120.0;
    private double  entityCullingNearBypass  = 16.0;
    private double  entityCullingExpansion   = 8.0;   // expand target sphere by this many blocks

    // --- fluid render ---
    private boolean fluidRenderEnabled   = true;
    private String  waterFluidTypeId     = "polyfills:water";
    private String  lavaFluidTypeId      = "polyfills:lava";
    private double  lavaDamage           = 4.0;
    private boolean renderWaterlogged    = true;
    private boolean renderFlowingFluid   = true;
    private boolean renderSourceFluid    = true;
    private boolean simulateFluidFlow    = false;

    /** Resolved from fluid type JSON files after loading. */
    private String waterItemPattern = "";
    private String lavaItemPattern  = "";

    private ContraptionConfig() {}

    public static ContraptionConfig get() { return INSTANCE; }

    public static void load(File dataFolder, ClassLoader classLoader) {
        INSTANCE = new ContraptionConfig();
        File file = new File(dataFolder, "contraptions.yml");
        YamlConfiguration cfg;
        if (file.exists()) {
            cfg = YamlConfiguration.loadConfiguration(file);
        } else {
            try (InputStream is = classLoader.getResourceAsStream("contraptions.yml")) {
                cfg = is != null
                        ? YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8))
                        : new YamlConfiguration();
            } catch (Throwable t) {
                cfg = new YamlConfiguration();
            }
        }
        INSTANCE.itemFrameModel         = cfg.getString("item_frame.model", "").trim();
        INSTANCE.entityCullingEnabled   = cfg.getBoolean("entity_culling.enabled", true);
        INSTANCE.entityCullingDistance  = cfg.getDouble("entity_culling.view_distance", 64.0);
        INSTANCE.entityCullingFrustum      = cfg.getBoolean("entity_culling.frustum.enabled", false);
        INSTANCE.entityCullingFovDegrees   = cfg.getDouble("entity_culling.frustum.fov", 120.0);
        INSTANCE.entityCullingNearBypass   = cfg.getDouble("entity_culling.frustum.near_bypass", 16.0);
        INSTANCE.entityCullingExpansion    = cfg.getDouble("entity_culling.frustum.expansion", 8.0);
        INSTANCE.fluidRenderEnabled     = cfg.getBoolean("fluid_render.enabled", true);
        INSTANCE.waterFluidTypeId     = cfg.getString("fluid_render.water", "polyfills:water").trim();
        INSTANCE.lavaFluidTypeId      = cfg.getString("fluid_render.lava",  "polyfills:lava").trim();
        INSTANCE.lavaDamage           = cfg.getDouble("fluid_render.lava_damage", 4.0);
        INSTANCE.renderWaterlogged    = cfg.getBoolean("fluid_render.render_waterlogged", true);
        INSTANCE.renderFlowingFluid   = cfg.getBoolean("fluid_render.render_flowing", true);
        INSTANCE.renderSourceFluid    = cfg.getBoolean("fluid_render.render_source", true);
        INSTANCE.simulateFluidFlow    = cfg.getBoolean("fluid_render.simulate_flow", false);
    }

    /**
     * Called after fluid types are loaded — resolves contraption_item_pattern from each fluid type JSON.
     */
    public static void resolveFluidPatterns(File dataFolder) {
        INSTANCE.waterItemPattern = readPattern(dataFolder, INSTANCE.waterFluidTypeId);
        INSTANCE.lavaItemPattern  = readPattern(dataFolder, INSTANCE.lavaFluidTypeId);
    }

    private static String readPattern(File dataFolder, String typeId) {
        if (typeId.isEmpty()) return "";
        try {
            String name = typeId.contains(":") ? typeId.substring(typeId.indexOf(':') + 1) : typeId;
            File f = new File(dataFolder, "fluid_types/" + name + ".json");
            if (!f.exists()) return readPatternFromResource(name);
            String content = new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            return extractJsonField(content, "contraption_item_pattern");
        } catch (Throwable ignored) { return ""; }
    }

    private static String readPatternFromResource(String name) {
        try (InputStream is = ContraptionConfig.class.getClassLoader()
                .getResourceAsStream("fluid_types/" + name + ".json")) {
            if (is == null) return "";
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return extractJsonField(content, "contraption_item_pattern");
        } catch (Throwable ignored) { return ""; }
    }

    private static String extractJsonField(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx + search.length());
        if (colon < 0) return "";
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return "";
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return "";
        return json.substring(q1 + 1, q2);
    }

    /** Returns item ID for the given CE fluid level (1-16). Empty = not configured. */
    public String waterItemId(int ceLevel) {
        return waterItemPattern.isEmpty() ? "" : waterItemPattern.replace("{level}", String.valueOf(ceLevel));
    }

    public String lavaItemId(int ceLevel) {
        return lavaItemPattern.isEmpty() ? "" : lavaItemPattern.replace("{level}", String.valueOf(ceLevel));
    }

    public boolean fluidRenderEnabled()  { return fluidRenderEnabled; }
    public boolean renderWater()         { return fluidRenderEnabled && !waterFluidTypeId.isEmpty() && !waterItemPattern.isEmpty(); }
    public boolean renderLava()          { return fluidRenderEnabled && !lavaFluidTypeId.isEmpty()  && !lavaItemPattern.isEmpty(); }
    public boolean renderWaterlogged()   { return renderWaterlogged; }
    public boolean renderFlowingFluid()  { return renderFlowingFluid; }
    public boolean renderSourceFluid()   { return renderSourceFluid; }
    public boolean simulateFluidFlow()   { return simulateFluidFlow; }

    public boolean entityCullingEnabled()  { return entityCullingEnabled; }
    public double  entityCullingDistance() { return entityCullingDistance; }
    public boolean entityCullingFrustumEnabled() { return entityCullingFrustum; }
    public double  entityCullingFovDegrees()      { return entityCullingFovDegrees; }
    public double  entityCullingNearBypass()      { return entityCullingNearBypass; }
    public double  entityCullingExpansion()       { return entityCullingExpansion; }

    public String itemFrameModel()        { return itemFrameModel; }
    public boolean useCustomItemFrame()   { return !itemFrameModel.isEmpty(); }
    public double lavaDamage()            { return lavaDamage; }
}
