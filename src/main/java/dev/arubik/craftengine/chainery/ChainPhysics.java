package dev.arubik.craftengine.chainery;

/**
 * The rope constraint at the heart of chain-tethered physics (CHAINERY phase 2 — "si son 2 phys ambos
 * estarán unidos por una cuerda ... cierto otro parámetro máxima tensión si se supera se rompe la cadena").
 * Pure math, no engine coupling, so it is unit-testable in isolation and can then be driven from
 * {@code ChainEngine.tickAll} against live {@code PhysicsWorld} bodies via {@code applyThrust}.
 *
 * <p>A chain is an <b>inextensible</b> link: it does nothing while slack (endpoints closer than its span) and
 * only bites once stretched past it, pulling the two ends back together — never pushing. Iron ({@code
 * stretch == 0}) holds an exact length; a springier rope ({@code stretch > 0}) is allowed to overshoot by
 * that fraction before it resists. The corrective impulse it would apply IS the tension, so the same number
 * decides both the pull and whether the chain snaps.
 */
public final class ChainPhysics {

    private ChainPhysics() {
    }

    /** The outcome of resolving one rope link for one tick. */
    public record RopeResult(double impulse, boolean broke) {
        public static final RopeResult SLACK = new RopeResult(0.0, false);
    }

    /**
     * Resolves a rope link between two endpoints along their connecting axis.
     *
     * @param dist         current distance between the endpoints (blocks)
     * @param maxLen       the chain's taut span (blocks) — its rest length, {@code chain.blocks}
     * @param stretch      elastic give 0..1: extra fraction the rope may lengthen before it resists
     * @param separating   relative velocity ALONG the axis, positive when the ends are pulling apart
     * @param invMassSum   sum of the two endpoints' inverse masses along the axis (a static block anchor
     *                     contributes 0 — infinite mass, so all correction lands on the moving body)
     * @param pull         correction stiffness 0..1 — how hard it yanks the overshoot back per tick
     * @param maxTension   snap threshold in impulse units; {@code <= 0} means unbreakable
     * @param dt           tick length
     * @return the impulse to apply along the axis (0 when slack) and whether the tension broke the chain
     */
    public static RopeResult resolve(double dist, double maxLen, double stretch, double separating,
            double invMassSum, double pull, double maxTension, double dt) {
        double effectiveMax = maxLen * (1.0 + Math.max(0.0, stretch));
        if (dist <= effectiveMax || invMassSum <= 1.0e-9) {
            return RopeResult.SLACK;
        }
        double overshoot = dist - effectiveMax;
        // Remove the separating velocity AND pull the overshoot back over the next tick (Baumgarte),
        // scaled by stiffness. A rope can only pull, so the impulse is floored at 0.
        double targetDeltaV = separating + Math.max(0.0, pull) * overshoot / Math.max(1.0e-6, dt);
        double impulse = targetDeltaV / invMassSum;
        if (impulse <= 0.0) {
            return RopeResult.SLACK;
        }
        // Break on RELATIVE over-stretch, not an absolute impulse: a chain snaps once it's stretched past its
        // natural length by more than MAX_STRETCH (so the minimum length for a span scales WITH the span — a
        // 5.4-block diagonal can't be held by 1 link). maxTension just enables/disables breaking.
        boolean broke = maxTension > 0.0 && dist > effectiveMax * (1.0 + MAX_STRETCH);
        return new RopeResult(impulse, broke);
    }

    /** Max fractional over-stretch before a chain snaps (0.8 = up to 1.8x its natural length). */
    public static final double MAX_STRETCH = 0.8;
}
