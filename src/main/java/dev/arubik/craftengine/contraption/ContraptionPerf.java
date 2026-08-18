/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption;

import java.util.Arrays;

public final class ContraptionPerf {
    private static volatile boolean ENABLED = false;
    private static final Window[] WINDOWS = new Window[Phase.values().length];
    private static final long[] PENDING = new long[Phase.values().length];
    private static final Window TOTAL = new Window();
    private static int countRendered;
    private static int countSkippedUnloaded;
    private static int countSkippedNoViewers;
    private static int countCells;
    private static final Window RENDERED;
    private static final Window SKIPPED_UNLOADED;
    private static final Window SKIPPED_NO_VIEWERS;
    private static final Window CELLS;

    private ContraptionPerf() {
    }

    public static boolean enabled() {
        return ENABLED;
    }

    public static void setEnabled(boolean enabled) {
        if (enabled && !ENABLED) {
            ContraptionPerf.reset();
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
        Arrays.fill(PENDING, 0L);
    }

    public static long begin() {
        return System.nanoTime();
    }

    public static void end(Phase phase, long startNanos) {
        int n = phase.ordinal();
        PENDING[n] = PENDING[n] + (System.nanoTime() - startNanos);
    }

    public static void tickStart() {
        Arrays.fill(PENDING, 0L);
        countRendered = 0;
        countSkippedUnloaded = 0;
        countSkippedNoViewers = 0;
        countCells = 0;
    }

    public static void tickEnd() {
        long total = 0L;
        for (int i = 0; i < PENDING.length; ++i) {
            WINDOWS[i].add(PENDING[i]);
            total += PENDING[i];
        }
        TOTAL.add(total);
        RENDERED.add(countRendered);
        SKIPPED_UNLOADED.add(countSkippedUnloaded);
        SKIPPED_NO_VIEWERS.add(countSkippedNoViewers);
        CELLS.add(countCells);
    }

    public static void countContraption(int cells) {
        countCells += cells;
    }

    public static void countRendered() {
        ++countRendered;
    }

    public static void countSkippedUnloaded() {
        ++countSkippedUnloaded;
    }

    public static void countSkippedNoViewers() {
        ++countSkippedNoViewers;
    }

    public static double millis(Phase phase) {
        return WINDOWS[phase.ordinal()].mean() / 1000000.0;
    }

    public static double totalMillis() {
        return TOTAL.mean() / 1000000.0;
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

    public static int sampleCount() {
        return TOTAL.count();
    }

    static {
        for (int i = 0; i < WINDOWS.length; ++i) {
            ContraptionPerf.WINDOWS[i] = new Window();
        }
        RENDERED = new Window();
        SKIPPED_UNLOADED = new Window();
        SKIPPED_NO_VIEWERS = new Window();
        CELLS = new Window();
    }

    static final class Window {
        static final int SIZE = 100;
        private final long[] samples = new long[100];
        private int next;
        private int filled;
        private long sum;

        Window() {
        }

        void add(long sample) {
            if (this.filled == 100) {
                this.sum -= this.samples[this.next];
            } else {
                ++this.filled;
            }
            this.samples[this.next] = sample;
            this.sum += sample;
            this.next = (this.next + 1) % 100;
        }

        double mean() {
            return this.filled == 0 ? 0.0 : (double)this.sum / (double)this.filled;
        }

        int count() {
            return this.filled;
        }

        void clear() {
            Arrays.fill(this.samples, 0L);
            this.next = 0;
            this.filled = 0;
            this.sum = 0L;
        }
    }

    public static enum Phase {
        PHYSICS,
        BEHAVIORS,
        REBUILD,
        RENDER,
        CARRY;

    }
}

