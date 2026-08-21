package dev.arubik.craftengine.rotation;

/** Gas fuel specification for a motor: rpm output, SU capacity, gas per tick. */
public record GasSpec(float rpm, float su, int gasPerTick) {}
