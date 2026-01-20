package dev.arubik.craftengine.gas;

public enum GasType {
    EMPTY("Empty"),
    STEAM("Steam"),
    HEAVY_STEAM("Heavy Steam");

    private final String displayName;

    GasType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Vent this gas into the air.
     * Spawns particles and returns true if vented (always true for non-empty gas).
     */
    public boolean vent(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, int amount) {
        if (isEmpty())
            return false;

        // Spawn particles (Cloud for now)
        // We use server level to spawn particles
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    Math.max(1, amount / 50), // Count based on amount
                    0.2, 0.2, 0.2, 0.05); // Spread and speed
        }
        return true;
    }
}
