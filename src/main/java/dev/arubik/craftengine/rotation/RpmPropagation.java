package dev.arubik.craftengine.rotation;

/**
 * The rules that decide how rotational power moves from one kinetic block to the next.
 *
 * <p>They live here, as pure functions over plain values, because they used to be inlined in the
 * middle of {@code DataMachineBlockEntity.pullRotationalPower} — a couple of hundred lines of NMS
 * that no test could reach. Every rule below encodes a specific failure that reached the game:
 *
 * <ul>
 *   <li>{@link #providerDistance} — a relay that re-emits its input via {@code set_rpm_output} is
 *       flagged {@code rpmSourceActive}, and treating that as "distance 0 source" made every
 *       downstream block look like an origin. Its upstream would then pull from its own output,
 *       read the already-inverted value, and the conflict rule below would destroy the block. This
 *       is why a gearbox blew up as soon as one of its faces drove something.</li>
 *   <li>{@link #relayGraceTicks} — a cogwheel driven by a perpendicular cogwheel receives its
 *       value from {@code relay_to}, not from its own pull: cogs mesh across their axis, while
 *       their declared rpm input faces run along it. Letting the fruitless pull zero that value
 *       each tick made the block alternate between powered and unpowered, which showed up as the
 *       model flickering and as chains of stacked cogs never connecting.</li>
 *   <li>{@link #isConflict} — two different speeds on one shaft is a build error, but only when
 *       both really are different upstream feeds.</li>
 * </ul>
 */
public final class RpmPropagation {

    /**
     * How many ticks a relayed value stays valid after the SOURCE last produced it.
     *
     * <p>Freshness belongs to the source, not to each hop. An earlier design gave every hop its
     * own countdown that decayed along the chain, which failed in two ways the moment builds got
     * real: each hop only died after its own countdown expired, so a four-cogwheel train took
     * about forty ticks to wind down; and any hop that PULLS (a shaft, a gearbox) always has a
     * spent countdown, so it handed out a full fresh budget and the chain never died at all.
     *
     * <p>With a stamp there is no per-hop budget: every block in a chain carries the same tick
     * number, so when the source stops they all expire together, within this many ticks, however
     * long the chain is.
     *
     * <p>Must exceed the slowest relaying block's {@code action_interval} (cogwheels use 2) or a
     * running chain would flicker between refreshes.
     */
    public static final int SOURCE_MAX_AGE = 8;

    /** Speed difference below which two feeds count as the same shaft. */
    public static final float CONFLICT_EPSILON = 0.01f;

    private RpmPropagation() {}

    /**
     * The effective distance-from-source of a neighbouring provider.
     *
     * @param providerIsSource  the provider is currently emitting ({@code rpmSourceActive})
     * @param providerIsRelay   the provider declares an rpm INPUT face, i.e. it re-emits rather
     *                          than originates
     * @param providerStoredDistance the provider's own recorded distance
     *
     * <p>Only a genuine origin — emitting and with no input of its own — is distance 0. Reading the
     * stored distance alone made a freshly started motor invisible for a tick, because its action
     * script only stamps distance 0 at the END of its tick and block-entity order is arbitrary;
     * treating <em>any</em> emitting block as 0 broke the opposite case described in the class
     * javadoc. Both need the relay flag to tell them apart.
     */
    public static int providerDistance(boolean providerIsSource, boolean providerIsRelay,
                                       int providerStoredDistance) {
        if (providerIsSource && !providerIsRelay) return 0;
        return providerStoredDistance;
    }

    /**
     * May we take power from a provider at {@code providerDist}, given our own {@code myDist}?
     *
     * <p>Strictly-closer-only. Equality is refused too: that is what stops two blocks at the same
     * distance from feeding each other in a loop.
     */
    public static boolean canPullFrom(int providerDist, int myDist) {
        return providerDist < myDist;
    }

    /** Our distance after taking power from a provider at {@code bestProviderDist}. */
    public static int distanceAfterPull(int bestProviderDist) {
        return bestProviderDist < Integer.MAX_VALUE ? bestProviderDist + 1 : Integer.MAX_VALUE;
    }

    /**
     * Is a value stamped at {@code sourceTick} still valid at {@code now}?
     *
     * <p>{@code sourceTick < 0} means "never stamped", i.e. no source ever produced this value.
     */
    public static boolean isFresh(long sourceTick, long now) {
        if (sourceTick < 0L) return false;
        long age = now - sourceTick;
        return age >= 0L && age <= SOURCE_MAX_AGE;
    }

    /**
     * Should this block run its own neighbour scan this tick?
     *
     * @param kinetics   the machine participates in the kinetic network at all
     * @param isRelay    it declares an rpm input face
     * @param isEmitting it is currently emitting ({@code rpmSourceActive})
     *
     * <p>A pure origin never pulls — it makes its own power. A relay always pulls, even while it
     * re-emits, so it notices its source stopping. The stamp, not a skipped scan, is what keeps a
     * freshly relayed value from being clobbered by a scan that legitimately finds nothing.
     */
    public static boolean shouldPull(boolean kinetics, boolean isRelay, boolean isEmitting) {
        if (!kinetics) return false;
        return !isEmitting || isRelay;
    }

    /**
     * Gearbox output sign, derived from which face is actually being driven.
     *
     * <p>Ported from Create's {@code RotationPropagator.getAxisModifier}:
     * <pre>
     *   direction.getAxis() == source.getAxis()
     *       ? direction == source ? 1 : -1
     *       : direction.getAxisDirection() == source.getAxisDirection() ? -1 : 1
     * </pre>
     *
     * <p>So the shaft <em>opposite</em> the driven one is REVERSED — a gearbox is a pair of meshed
     * bevel gears, not a straight coupling — and a perpendicular face depends on whether the two
     * faces point along the same axis direction. Neither half can be expressed with static
     * {@code output_same}/{@code output_inverted} lists, because the split moves with the input:
     * gearbox_h hardcoded north/south as "same", which was wrong for both the collinear case and
     * for any build driven from east or west.
     *
     * @param outAxis  axis of the output face (0=X, 1=Y, 2=Z)
     * @param outSign  axis direction of the output face (+1 for EAST/UP/SOUTH, -1 otherwise)
     * @param inAxis   axis of the driven face
     * @param inSign   axis direction of the driven face
     * @return {@code +1} to keep the direction, {@code -1} to reverse it
     */
    public static float gearboxModifier(int outAxis, int outSign, int inAxis, int inSign) {
        if (outAxis == inAxis) {
            // Same face = back toward the source (no-op); opposite face = reversed.
            return outSign == inSign ? 1f : -1f;
        }
        return outSign == inSign ? -1f : 1f;
    }

    /**
     * The longest a settled block may go between neighbour scans, in ticks.
     *
     * <p>Kept small on purpose: this is also the worst-case delay before a block notices its
     * source changing SPEED, which is the one change that does not fire a neighbour update.
     * Four ticks is a fifth of a second — invisible in play, and it removes three quarters of the
     * neighbour scanning on a build that is just running.
     */
    public static final int MAX_PULL_BACKOFF = 4;

    /**
     * How often a block should re-scan its neighbours, given how long its pull result has been
     * unchanged.
     *
     * <p>Topology changes — a block placed, broken or rotated — already force an immediate
     * re-pull through {@code MachineBlockBehavior}'s neighbour-changed hook calling
     * {@code invalidateRpm}, so a stable block re-scanning six neighbours every single tick is
     * pure waste. This is the biggest per-tick cost in the kinetic system, ahead of the network
     * arithmetic, because every scan does six block-entity lookups.
     */
    public static int pullInterval(int stablePulls) {
        if (stablePulls < 8) return 1;
        if (stablePulls < 24) return 2;
        return MAX_PULL_BACKOFF;
    }

    /**
     * Whether a block is due to re-scan its neighbours.
     *
     * <p>A POWERED block always scans. Backing off a running chain looked like a free win and was
     * not: each hop only copies its source's stamp when it scans, so a backed-off relay lets that
     * stamp age by up to a whole interval, and the staleness compounds hop by hop until a long
     * chain expires before it can ever establish itself. Cogwheels stopped connecting entirely.
     *
     * <p>Idle machinery is where the saving actually is — a base full of unpowered kinetic blocks
     * each probing six neighbours every tick — and an idle block has no stamp to keep fresh.
     */
    public static boolean pullDueThisTick(int stablePulls, long ticksAlive, boolean powered) {
        if (powered) return true;
        int interval = pullInterval(stablePulls);
        return interval <= 1 || (ticksAlive % interval) == 0L;
    }

    /** Applies a provider's declared output inversion to the value we read from it. */
    public static float applyInversion(float raw, boolean providerInvertsThisFace) {
        return providerInvertsThisFace ? -raw : raw;
    }

    /**
     * How long a conflict must persist before it is treated as a build error worth breaking.
     *
     * <p>Two feeds disagreeing for a tick or two is normal: changing a motor's speed reaches the
     * blocks around it over several ticks, so during the transition one neighbour still carries
     * the old value while another already has the new one. Breaking on the first disagreement
     * destroyed a perfectly good pair of cogwheels every time the player adjusted the RPM.
     */
    public static final int CONFLICT_TICKS_BEFORE_BREAK = 5;

    /**
     * Do two feeds genuinely conflict?
     *
     * <p>Only when both carry power and differ. A zero on either side is simply "nothing from that
     * side" and must never destroy a build.
     */
    public static boolean isConflict(float delivered, float candidate) {
        if (delivered == 0f || candidate == 0f) return false;
        return Math.abs(delivered - candidate) > CONFLICT_EPSILON;
    }

    /**
     * Do two feeds conflict, given which source each came from?
     *
     * <p>Feeds carrying the same source stamp came from the SAME generator by different routes,
     * so a difference between them is a speed change still spreading through the build — never a
     * conflict. Only genuinely independent sources fighting over one shaft can be one.
     */
    public static boolean isConflict(float delivered, float candidate, long stampA, long stampB) {
        if (stampA >= 0L && stampA == stampB) return false;
        return isConflict(delivered, candidate);
    }

    /** Should a conflict that has now held for {@code ticks} break the build? */
    public static boolean conflictShouldBreak(int ticks) {
        return ticks >= CONFLICT_TICKS_BEFORE_BREAK;
    }
}
