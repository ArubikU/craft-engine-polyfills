package dev.arubik.craftengine.contraption;

/**
 * Immutable, per-contraption tuning for how the {@code ContraptionHitboxSwarm} resolves entity
 * collision with the moving structure (the packet-only, server-resolved push/carry math in
 * {@link render.ContraptionHitboxSwarm} — see that class's javadoc and CONTRAPTIONS.md §5 Phase 4).
 * Attached to {@link ContraptionState} (field + getter/setter, defaulting to {@link #DEFAULT}) so
 * each contraption can tune its own collision response without touching the swarm's collision MATH.
 *
 * <p><b>Every default reproduces the pre-settings behavior EXACTLY</b>, so a contraption left on
 * {@link #DEFAULT} behaves identically to before this class existed:
 * <ul>
 *   <li>{@link #pushStrength}/{@link #pushUpStrength} default to {@code 1.0} — the implicit unit
 *   multiplier the swarm's pushback vectors were already applied at.</li>
 *   <li>{@link #pushUpEnabled} defaults to {@code false} — the swarm had no step-up path at all
 *   before, so the whole {@link #maxStepUpHeight}-gated "lift over a lip" branch stays dormant
 *   until a caller opts in.</li>
 *   <li>{@link #carryEntities} defaults to {@code false} — a non-rider entity caught in the
 *   structure's path is shoved out sideways, exactly as before.</li>
 * </ul>
 * {@link #maxStepUpHeight}'s default ({@code 0.6}, vanilla's own auto-step-up height) is inert
 * while {@link #pushUpEnabled} is {@code false}, so it never affects the default behavior either.
 *
 * <p>Build a tuned instance with {@link #builder()} (all fields optional, each defaulting to the
 * {@link #DEFAULT} value); instances are immutable, so a builder result is safe to share.
 */
public final class ContraptionPushSettings {

    /**
     * Whether the opt-in "push up / step-up" path is active (see
     * {@code ContraptionHitboxSwarm}'s pushback resolution). When {@code false} (the default) a
     * solid part meeting an entity only ever shoves it back/blocks it, exactly as before this
     * setting existed. When {@code true}, a horizontal shove against a lip no taller than
     * {@link #maxStepUpHeight} is turned into an upward lift so the entity steps up over the lip
     * instead of being shoved back.
     */
    public final boolean pushUpEnabled;

    /**
     * How tall a lip (blocks, measured from the entity's feet to the solid's top face) the
     * contraption may lift an entity UP over — instead of shoving it back — when
     * {@link #pushUpEnabled} is {@code true}. Ignored entirely while {@link #pushUpEnabled} is
     * {@code false}. Defaults to {@code 0.6}, matching vanilla's own auto-step-up height.
     */
    public final double maxStepUpHeight;

    /**
     * Multiplier on the horizontal pushback vector applied to an entity shoved out of the
     * structure's path. Defaults to {@code 1.0} — the implicit unit scale the swarm's pushback
     * was already applied at, so the default is a no-op.
     */
    public final double pushStrength;

    /**
     * Multiplier on the upward lift applied by the {@link #pushUpEnabled} step-up path. Defaults
     * to {@code 1.0}; ignored while {@link #pushUpEnabled} is {@code false}.
     */
    public final double pushUpStrength;

    /**
     * Whether a non-rider entity caught in the structure's path is carried UP onto the step
     * (when a step-up lip within {@link #maxStepUpHeight} exists) rather than shoved back
     * sideways. Defaults to {@code false} — the pre-settings behavior, where such an entity is
     * always shoved out horizontally. Only affects non-player entities (a bystander player is
     * always shoved, never silently lifted).
     */
    public final boolean carryEntities;

    /** Behavior-preserving defaults — see this class's javadoc for why each reproduces today's behavior. */
    public static final ContraptionPushSettings DEFAULT = builder().build();

    private ContraptionPushSettings(Builder b) {
        this.pushUpEnabled = b.pushUpEnabled;
        this.maxStepUpHeight = b.maxStepUpHeight;
        this.pushStrength = b.pushStrength;
        this.pushUpStrength = b.pushUpStrength;
        this.carryEntities = b.carryEntities;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Mutable builder for {@link ContraptionPushSettings}; every field defaults to the {@link #DEFAULT} value. */
    public static final class Builder {
        private boolean pushUpEnabled = false;
        private double maxStepUpHeight = 0.6;
        private double pushStrength = 1.0;
        private double pushUpStrength = 1.0;
        private boolean carryEntities = false;

        public Builder pushUpEnabled(boolean pushUpEnabled) {
            this.pushUpEnabled = pushUpEnabled;
            return this;
        }

        public Builder maxStepUpHeight(double maxStepUpHeight) {
            this.maxStepUpHeight = maxStepUpHeight;
            return this;
        }

        public Builder pushStrength(double pushStrength) {
            this.pushStrength = pushStrength;
            return this;
        }

        public Builder pushUpStrength(double pushUpStrength) {
            this.pushUpStrength = pushUpStrength;
            return this;
        }

        public Builder carryEntities(boolean carryEntities) {
            this.carryEntities = carryEntities;
            return this;
        }

        public ContraptionPushSettings build() {
            return new ContraptionPushSettings(this);
        }
    }
}
