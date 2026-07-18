package dev.arubik.craftengine.contraption;

/**
 * Per-phase timing/count instrumentation for {@link ContraptionEngine#tickAll()} — the measurement
 * side of the contraption CPU budget, surfaced by {@code /cep contraption perf}.
 *
 * <p><b>Off by default, and free while off.</b> Every call site is guarded by {@link #enabled()}, a
 * single volatile boolean read, so an un-instrumented server pays one predictable-branch read per
 * phase per tick and never calls {@code System.nanoTime} (the expensive part — a real syscall-ish
 * counter read) nor touches the accumulators at all. Instrumentation that costs TPS cannot be used to
 * chase TPS, so this is deliberately opt-in rather than always-on.
 *
 * <p><b>Rolling average, not a running total.</b> Samples are accumulated per tick into
 * {@link Window}, which reports the mean over the last {@link Window#SIZE} ticks. A running total
 * since-enable would be dominated by whatever the server was doing minutes ago; a single tick's
 * sample is pure noise (GC, chunk loads). The mean over ~5 seconds is what actually tracks a change.
 *
 * <p>Main-thread only — the engine tick is the sole writer, so the accumulators are deliberately
 * plain fields with no synchronization. Only {@link #ENABLED} is volatile (a command thread flips it).
 */
public final class ContraptionPerf {

    private ContraptionPerf() {
    }

    /** Phases of one {@link ContraptionEngine#tickAll()}, in the order the engine runs them. */
    public enum Phase {
        /** {@code PhysicsWorld.stepAll} — the whole-world rigid-body solve. */
        PHYSICS,
        /** Every contraption's {@code MovementBehavior#tick} plus the stall gate. */
        BEHAVIORS,
        /** {@code refreshLocalPositions} + every swarm's {@code rebuild}. */
        REBUILD,
        /** Every swarm's {@code render} — the packet-emitting per-cell-per-viewer work. */
        RENDER,
        /** Rider/entity carry and bystander/entity pushback (the real-world AABB scans). */
        CARRY
    }

    private static volatile boolean ENABLED = false;

    private static final Window[] WINDOWS = new Window[Phase.values().length];
    private static final long[] PENDING = new long[Phase.values().length];
    private static final Window TOTAL = new Window();

    static {
        for (int i = 0; i < WINDOWS.length; i++) {
            WINDOWS[i] = new Window();
        }
    }

    /** Per-tick contraption counts, reset at the start of each {@link ContraptionEngine#tickAll()}. */
    private static int countRendered;
    private static int countSkippedUnloaded;
    private static int countSkippedNoViewers;
    private static int countCells;
    private static final Window RENDERED = new Window();
    private static final Window SKIPPED_UNLOADED = new Window();
    private static final Window SKIPPED_NO_VIEWERS = new Window();
    private static final Window CELLS = new Window();

    public static boolean enabled() {
        return ENABLED;
    }

    /** Flips instrumentation on/off, clearing every window so the next report reflects only the new run. */
    public static void setEnabled(boolean enabled) {
        if (enabled && !ENABLED) {
            reset();
        }
        ENABLED = enabled;
    }

    private static void reset() {
        for (Window w : WINDOWS) {
            w.clear();
        }
        TOTAL.clear();
        RENDERED.clear();
        SKIPPED_UNLOADED.clear();
        SKIPPED_NO_VIEWERS.clear();
        CELLS.clear();
        java.util.Arrays.fill(PENDING, 0L);
    }

    /**
     * Opens a phase sample. Returns the start timestamp to hand back to {@link #end(Phase, long)}.
     * Callers must guard on {@link #enabled()} — this does not re-check, so that a disabled server
     * never reaches {@code System.nanoTime} at all.
     */
    public static long begin() {
        return System.nanoTime();
    }

    /** Closes a phase sample opened by {@link #begin()}, accumulating into this tick's pending total. */
    public static void end(Phase phase, long startNanos) {
        PENDING[phase.ordinal()] += System.nanoTime() - startNanos;
    }

    /** Clears the per-tick accumulators. Called at the top of {@link ContraptionEngine#tickAll()}. */
    public static void tickStart() {
        java.util.Arrays.fill(PENDING, 0L);
        countRendered = 0;
        countSkippedUnloaded = 0;
        countSkippedNoViewers = 0;
        countCells = 0;
    }

    /** Commits this tick's accumulators into the rolling windows. Called at the end of {@code tickAll}. */
    public static void tickEnd() {
        long total = 0;
        for (int i = 0; i < PENDING.length; i++) {
            WINDOWS[i].add(PENDING[i]);
            total += PENDING[i];
        }
        TOTAL.add(total);
        RENDERED.add(countRendered);
        SKIPPED_UNLOADED.add(countSkippedUnloaded);
        SKIPPED_NO_VIEWERS.add(countSkippedNoViewers);
        CELLS.add(countCells);
    }

    /** Records a contraption reaching the render gate, with however many cells it currently projects. */
    public static void countContraption(int cells) {
        countCells += cells;
    }

    public static void countRendered() {
        countRendered++;
    }

    public static void countSkippedUnloaded() {
        countSkippedUnloaded++;
    }

    public static void countSkippedNoViewers() {
        countSkippedNoViewers++;
    }

    /** Mean milliseconds spent in {@code phase} over the sampling window. */
    public static double millis(Phase phase) {
        return WINDOWS[phase.ordinal()].mean() / 1.0e6;
    }

    /** Mean milliseconds spent across every instrumented phase over the sampling window. */
    public static double totalMillis() {
        return TOTAL.mean() / 1.0e6;
    }

    public static double meanRendered() {
        return RENDERED.mean();
    }

    public static double meanSkippedUnloaded() {
        return SKIPPED_UNLOADED.mean();
    }

    public static double meanSkippedNoViewers() {
        return SKIPPED_NO_VIEWERS.mean();
    }

    public static double meanCells() {
        return CELLS.mean();
    }

    /** Ticks sampled so far, capped at {@link Window#SIZE} — lets a report say "not warm yet". */
    public static int sampleCount() {
        return TOTAL.count();
    }

    /**
     * Fixed-capacity rolling mean over the last {@link #SIZE} samples — a circular buffer with a
     * running sum, so {@link #add} and {@link #mean} are both O(1) and allocation-free.
     *
     * <p>Package-private rather than nested-private purely so the arithmetic can be unit-tested
     * directly; it is pure (no clock, no Bukkit) and has no other callers.
     */
    static final class Window {

        /** ~5 seconds at 20 TPS — long enough to average out a GC pause, short enough to react to a change. */
        static final int SIZE = 100;

        private final long[] samples = new long[SIZE];
        private int next;
        private int filled;
        private long sum;

        void add(long sample) {
            if (filled == SIZE) {
                sum -= samples[next];
            } else {
                filled++;
            }
            samples[next] = sample;
            sum += sample;
            next = (next + 1) % SIZE;
        }

        double mean() {
            return filled == 0 ? 0.0 : (double) sum / filled;
        }

        int count() {
            return filled;
        }

        void clear() {
            java.util.Arrays.fill(samples, 0L);
            next = 0;
            filled = 0;
            sum = 0;
        }
    }
}
