package dev.arubik.craftengine.rotation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A kinetic network: group of machines sharing speed and SU budget.
 * Similar to Create mod's KineticNetwork.
 *
 * Negative SU = generator (provides capacity).
 * Positive SU = consumer (demands capacity).
 * When totalStress > totalCapacity → overStressed → all members run at speed 0.
 */
public final class RpmNetwork {

    private static final Map<Long, RpmNetwork> NETWORKS = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GEN = new AtomicLong(1L);

    public static RpmNetwork create() {
        long id = ID_GEN.getAndIncrement();
        RpmNetwork net = new RpmNetwork(id);
        NETWORKS.put(id, net);
        return net;
    }

    public static RpmNetwork get(long id) { return id == 0L ? null : NETWORKS.get(id); }
    public static void remove(long id) { NETWORKS.remove(id); }
    public static void clear() { NETWORKS.clear(); ID_GEN.set(1L); }

    // ---- Per-network state --------------------------------------------------

    private final long id;
    /** Members → their SU value (negative = generates, positive = consumes) */
    private final Map<KineticMember, Float> members = new ConcurrentHashMap<>();
    /**
     * The generating members only, mirroring Create's separate {@code sources} map.
     * Lets "is anything driving this network?" be answered without touching the member map,
     * which on a large build is the difference between a set lookup and a full scan.
     */
    private final Set<KineticMember> sources = ConcurrentHashMap.newKeySet();
    // Accumulated in double: these are updated incrementally on a very hot path, and float32
    // rounding compounds enough over a few thousand updates to shift the overstress threshold.
    private double totalCapacity = 0d;
    private double totalStress   = 0d;
    private boolean overStressed = false;
    /** Updates since the totals were last rebuilt exactly; see {@link #applyDelta}. */
    private int driftTicks = 0;
    private boolean dissolved = false;
    private boolean dirty = false;
    /** The last value actually pushed to members, so a flag that flaps and returns costs nothing. */
    private boolean lastNotifiedOverstress = false;
    private static final int DRIFT_RESYNC = 1024;

    private RpmNetwork(long id) { this.id = id; }

    public long id()               { return id; }
    public boolean isOverStressed(){ return overStressed; }
    /** Whether anything in this network still generates. Zero means nothing is driving it. */
    public boolean hasCapacity()   { return totalCapacity > 0d; }
    /** How many members are generating. O(1) — no member scan. */
    public int sourceCount()       { return sources.size(); }
    /** Whether any member is generating. O(1). */
    public boolean hasSource()     { return !sources.isEmpty(); }
    public float totalCapacity()   { return (float) totalCapacity; }
    public float totalStress()     { return (float) totalStress; }
    public int size()              { return members.size(); }
    public Set<KineticMember> members() { return members.keySet(); }

    public void addMember(KineticMember be, float su) {
        Float prev = members.put(be, su);
        trackSource(be, su);
        if (prev != null) applyDelta(prev, su); else applyDelta(0f, su);
        refreshOverstress();
    }

    /**
     * Removes one member. O(1) — the rest of the network is left alone.
     *
     * <p>This used to {@link #dissolve()} on every removal, on the theory that losing a member
     * might have split the network in two. That made breaking a single shaft reset the
     * {@code networkId} of every other member and force the whole thing to re-form, which is
     * exactly the cost that made large builds lag.
     *
     * <p>It is also unnecessary, because a split now heals itself:
     * <ul>
     *   <li>A member that lost its power finds nothing on its next pull, winds down and calls
     *       {@code leaveNetwork} on its own (see the relay-death rule in
     *       {@code DataMachineBlockEntity.pullRotationalPower}).</li>
     *   <li>A member still driven by some source follows that source: {@code syncNetworkWithSource}
     *       re-joins whichever network its provider belongs to, so each half of a split ends up
     *       on its own source's network within a tick or two.</li>
     *   <li>Anything left behind is dropped by the pruning pass in {@link #resum()}.</li>
     * </ul>
     *
     * <p>The trade-off is that for those one or two ticks a straggler can still be counted in a
     * network it is no longer physically part of. That skews a stress total briefly; it does not
     * strand anything, and it converges without touching every member.
     */
    public void removeMember(KineticMember be) {
        Float prev = members.remove(be);
        sources.remove(be);
        if (prev != null) applyDelta(prev.floatValue(), 0f);
        if (members.isEmpty()) {
            NETWORKS.remove(id);
            return;
        }
        refreshOverstress();
    }

    /**
     * Dissolve this network: clear all members and remove from registry.
     * Members get networkId=0 so they re-join (or form new networks) on the next tick
     * when relay_to is called. Overstress is cleared for all members.
     */
    public void dissolve() {
        if (dissolved) return;   // a burst of removals must not re-walk the members repeatedly
        dissolved = true;
        List<KineticMember> snapshot = new ArrayList<>(members.keySet());
        members.clear();
        sources.clear();
        totalCapacity = 0d;
        totalStress = 0d;
        NETWORKS.remove(id);
        for (KineticMember m : snapshot) {
            m.setRpmNetworkId(0L);
            try { m.onNetworkOverstressChanged(false); } catch (Throwable ignored) {}
        }
    }

    /**
     * Records a member's SU. O(1): the running totals are adjusted by the delta rather than
     * re-summed.
     *
     * <p>This is the hottest path in the kinetic system — every generator and every consumer
     * script calls it on every action tick. Re-summing all members here made the cost O(N per
     * reporter), i.e. O(N&sup2;) per tick for the whole network, which is what made large builds
     * lag. A settled network now costs nothing at all: an unchanged value returns immediately,
     * before the map write.
     */
    public void updateMemberSu(KineticMember be, float su) {
        Float prev = members.get(be);
        if (prev == null) return;
        if (prev.floatValue() == su) return;   // steady state: nothing to do, and no boxing
        // Write the map BEFORE adjusting the totals: applyDelta may trigger the periodic exact
        // rebuild, and that reads the map. Adjusting first meant the rebuild saw the stale value
        // and threw away the delta that had just been applied.
        members.put(be, su);
        trackSource(be, su);
        applyDelta(prev.floatValue(), su);
        refreshOverstress();
    }

    /** Tracks whether a member currently generates, keeping the sources view in step. */
    private void trackSource(KineticMember be, float su) {
        if (su < 0f) sources.add(be); else sources.remove(be);
    }

    /** Moves the running totals from {@code oldSu} to {@code newSu}. */
    private void applyDelta(float oldSu, float newSu) {
        if (oldSu < 0f) totalCapacity += oldSu; else totalStress -= oldSu;
        if (newSu < 0f) totalCapacity -= newSu; else totalStress += newSu;
        // Doubles make drift negligible, but a periodic exact rebuild also prunes any
        // bookkeeping that went out of step. Once per DRIFT_RESYNC updates, not per update.
        if (++driftTicks >= DRIFT_RESYNC) resum();
    }

    /** Rebuilds the totals exactly. O(n); only called to clear accumulated float drift. */
    private void resum() {
        double cap = 0d, stress = 0d;
        // Prune while summing, the way Create drops block entities that are no longer at their
        // position. A member that was broken or unloaded without calling leaveNetwork would
        // otherwise be held here forever — a slow leak that also skews capacity and stress.
        for (Iterator<Map.Entry<KineticMember, Float>> it = members.entrySet().iterator(); it.hasNext();) {
            Map.Entry<KineticMember, Float> e = it.next();
            KineticMember m = e.getKey();
            if (m.rpmNetworkId() != this.id) {
                it.remove();
                sources.remove(m);
                continue;
            }
            float s = e.getValue();
            if (s < 0f) cap += -s;
            else stress += s;
        }
        this.totalCapacity = cap;
        this.totalStress = stress;
        this.driftTicks = 0;
    }

    /**
     * Recomputes the overstress flag from the running totals. O(1) unless the flag actually
     * flips, in which case members are notified — a rare event, not a per-tick cost.
     */
    private void refreshOverstress() {
        if (totalCapacity < 0d) totalCapacity = 0d;
        if (totalStress < 0d) totalStress = 0d;
        boolean wasOver = this.overStressed;
        // A network with load but no generator is stalled, not healthy. The old
        // `stress > cap && cap > 0f` let a zero-capacity network read as fine, so pulling the
        // motor out left the consumers believing everything was still turning.
        this.overStressed = totalCapacity <= 0d ? totalStress > 0d : totalStress > totalCapacity;
        if (wasOver != this.overStressed) {
            // Do NOT notify here. Mirrors Create's networkDirty flag: several members reporting in
            // the same tick can flip this back and forth, and walking every member on each flip is
            // the one remaining O(n) cost on the hot path. flush() settles it once per tick, so a
            // flag that flaps and returns costs nothing at all.
            this.dirty = true;
            NETWORKS_DIRTY.add(this.id);
        }
    }

    /** Networks whose overstress flag moved since the last flush. */
    private static final Set<Long> NETWORKS_DIRTY = ConcurrentHashMap.newKeySet();

    /**
     * Pushes any settled overstress change to the members of every dirty network.
     *
     * <p>Called once per tick from the plugin's scheduler, the way Create's TorquePropagator
     * drains its dirty set, so a burst of changes within one tick collapses into a single walk —
     * or into none, when the flag ends the tick where it started.
     */
    public static void flushAll() {
        if (NETWORKS_DIRTY.isEmpty()) return;
        for (Long id : new ArrayList<>(NETWORKS_DIRTY)) {
            NETWORKS_DIRTY.remove(id);
            RpmNetwork net = NETWORKS.get(id);
            if (net != null) net.flush();
        }
    }

    /** Notifies this network's members if the flag genuinely settled somewhere new. */
    public void flush() {
        if (!dirty) return;
        dirty = false;
        if (overStressed == lastNotifiedOverstress) return;   // flapped and came back: no-op
        lastNotifiedOverstress = overStressed;
        for (KineticMember be : new ArrayList<>(members.keySet())) {
            try { be.onNetworkOverstressChanged(this.overStressed); } catch (Throwable ignored) {}
        }
    }

    /** Exact rebuild of totals and overstress. For tests and diagnostics. */
    public void recalculate() {
        resum();
        refreshOverstress();
    }
}
