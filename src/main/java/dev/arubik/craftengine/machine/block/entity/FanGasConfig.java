package dev.arubik.craftengine.machine.block.entity;

/**
 * Per-gas-type tuning for the {@link FanMachineBlockEntity}. One config object per {@code GasType}
 * (instead of a separate map per knob), so adding a future gas type — or a new per-gas knob — is a
 * one-line change. Config form:
 *
 * <pre>
 * gases:
 *   steam:        { push_strength: 0.1,  push_limit: 5, particle: CLOUD }
 *   heavy_steam:  { push_strength: 0.16, push_limit: 7, particle: SMOKE }
 * </pre>
 *
 * @param pushStrength push velocity in BLOCKS per tick
 * @param pushLimit    max airflow distance (cells) — generation/overclock extend it on top
 * @param particle     airflow particle for this gas
 */
public record FanGasConfig(double pushStrength, int pushLimit, org.bukkit.Particle particle) {
}
