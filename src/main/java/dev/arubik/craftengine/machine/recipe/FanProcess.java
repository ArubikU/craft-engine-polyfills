package dev.arubik.craftengine.machine.recipe;

/**
 * The processing FAMILY a fan airflow column can run, decided by the GAS tier × the PROCESS BLOCK in the
 * airflow (Create-style). Full map (gas × block):
 * <ul>
 *   <li>{@link #SMELTING} — FIRE/soul-less fire + STEAM (vanilla furnace).</li>
 *   <li>{@link #COOKING} — SOUL_FIRE + STEAM (vanilla smoker — food).</li>
 *   <li>{@link #BLASTING} — lava, OR any fire-family + HEAVY_STEAM (vanilla blast furnace — ores).</li>
 *   <li>{@link #WASHING} — water + STEAM/HEAVY_STEAM ("splashing"); custom {@code fan} recipes only.</li>
 *   <li>{@link #FREEZING} — water + NITROGEN; custom {@code fan} recipes only.</li>
 *   <li>{@link #NONE} — no process block (or nitrogen over fire): the fan only blows.</li>
 * </ul>
 */
public enum FanProcess {
    NONE, SMELTING, COOKING, BLASTING, WASHING, FREEZING;

    public static FanProcess parse(String s) {
        if (s == null)
            return NONE;
        return switch (s.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "smelting", "smelt", "fire" -> SMELTING;
            case "cooking", "cook", "smoking", "smoker", "soul_fire", "soulfire" -> COOKING;
            case "blasting", "blast", "lava" -> BLASTING;
            case "washing", "wash", "splashing", "water" -> WASHING;
            case "freezing", "freeze", "ice", "frost" -> FREEZING;
            default -> NONE;
        };
    }
}
