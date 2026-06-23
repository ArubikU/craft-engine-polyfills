package dev.arubik.craftengine.machine.recipe;

/**
 * The processing FAMILY a fan airflow column can run, decided by the PROCESS BLOCK sitting in the
 * airflow (Create-style):
 * <ul>
 *   <li>{@link #SMELTING} — fire / soul_fire / lit campfire + STEAM gas (vanilla furnace fallback).</li>
 *   <li>{@link #BLASTING} — lava / lava cauldron, OR fire-family + HEAVY_STEAM gas (vanilla blasting
 *       fallback).</li>
 *   <li>{@link #WASHING} — water / water cauldron ("splashing"); custom {@code fan} recipes only.</li>
 *   <li>{@link #NONE} — no process block: the fan only blows.</li>
 * </ul>
 */
public enum FanProcess {
    NONE, SMELTING, BLASTING, WASHING, FREEZING;

    public static FanProcess parse(String s) {
        if (s == null)
            return NONE;
        return switch (s.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "smelting", "smelt", "fire" -> SMELTING;
            case "blasting", "blast", "lava" -> BLASTING;
            case "washing", "wash", "splashing", "water" -> WASHING;
            case "freezing", "freeze", "ice", "frost" -> FREEZING;
            default -> NONE;
        };
    }
}
