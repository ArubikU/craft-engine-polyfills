package dev.arubik.craftengine.debug;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A self-contained sampling profiler for one thread, writing a report to a file.
 *
 * <h2>Why the sampling is not spark's</h2>
 * The server runs spark, and getting the profile FROM spark was the intent. It cannot be done, and
 * that is established rather than assumed: the {@code spark-api} jar on this server's classpath
 * contains exactly {@code Spark}, {@code SparkProvider}, {@code GarbageCollector},
 * {@code PlaceholderResolver} and the {@code statistic} package — no profiler, sampler or dump type
 * anywhere in it. spark's own profiler is driven by its command, and its results are UPLOADED:
 * {@code plugins/spark/activity.json} records every past run as {@code "type": "url"} pointing at
 * spark.lucko.me. Nothing reaches disk for something unattended to read.
 *
 * <p>What spark IS used for here is {@link SparkMetrics}, in the header of every report: TPS and
 * MSPT. That is not a consolation prize — a sampler reports shares of WALL time, which cannot tell
 * you whether a tick is in budget, and MSPT can. The two are only useful together.
 *
 * <h2>What it measures</h2>
 * It samples the target thread's stack at a fixed interval and aggregates the samples into a call
 * tree. A node's sample count over the total is the share of wall time that thread spent with that
 * frame on its stack — the same statistical sampling spark does, and it inherits the same caveats:
 * a method that runs for less than one interval may not appear at all, and the attribution is only
 * as good as the sample count. Ten seconds at 5ms is 2000 samples, which is enough to rank the tick
 * but not to trust a difference of one or two samples.
 *
 * <p>It deliberately does NOT stop the world, allocate inside the sampling loop beyond the stack
 * trace itself, or touch the sampled thread. {@code Thread#getStackTrace} on a running thread pauses
 * it briefly at a safepoint, which is why the interval is milliseconds rather than microseconds.
 */
public final class ThreadSampler {

    private ThreadSampler() {}

    private static final AtomicBoolean RUNNING = new AtomicBoolean();

    /** One node of the aggregated call tree. */
    private static final class Node {
        final String frame;
        final Map<String, Node> children = new HashMap<>();
        int samples;      // samples with this frame anywhere on the stack
        int selfSamples;  // samples with this frame on TOP of the stack

        Node(String frame) { this.frame = frame; }

        Node child(String f) { return children.computeIfAbsent(f, Node::new); }
    }

    /**
     * Samples {@code target} for {@code durationMs} and writes the report to {@code out}.
     *
     * <p>Runs on its own daemon thread and returns immediately; {@code onDone} is called with the
     * report path, or with null if it could not be written. Refuses to start a second concurrent
     * run — two samplers would each pause the target and report a distorted picture of the other.
     */
    public static boolean start(Thread target, long durationMs, long intervalMs, Path out,
                                 java.util.function.Consumer<Path> onDone) {
        if (target == null) return false;
        if (!RUNNING.compareAndSet(false, true)) return false;

        Thread sampler = new Thread(() -> {
            Node root = new Node("<root>");
            int taken = 0, missed = 0;
            long deadline = System.nanoTime() + durationMs * 1_000_000L;
            try {
                while (System.nanoTime() < deadline) {
                    StackTraceElement[] stack = target.getStackTrace();
                    if (stack.length == 0) {
                        missed++; // parked or between safepoints — nothing to attribute
                    } else {
                        record(root, stack);
                        taken++;
                    }
                    try { Thread.sleep(intervalMs); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
                Path written = write(root, out, target.getName(), taken, missed, durationMs, intervalMs);
                if (onDone != null) onDone.accept(written);
            } catch (Throwable t) {
                if (onDone != null) onDone.accept(null);
            } finally {
                RUNNING.set(false);
            }
        }, "cep-sampler");
        sampler.setDaemon(true);
        sampler.setPriority(Thread.MAX_PRIORITY); // keep the interval honest under load
        sampler.start();
        return true;
    }

    public static boolean isRunning() { return RUNNING.get(); }

    /** Walks one stack outermost-first, so the tree reads caller -> callee. */
    private static void record(Node root, StackTraceElement[] stack) {
        root.samples++;
        Node cur = root;
        for (int i = stack.length - 1; i >= 0; i--) {
            cur = cur.child(frameOf(stack[i]));
            cur.samples++;
        }
        cur.selfSamples++;
    }

    private static String frameOf(StackTraceElement e) {
        String cls = e.getClassName();
        // Lambda and generated-class suffixes make every sample a distinct frame and shatter the
        // tree into unaggregatable noise. The declaring method is what a reader needs.
        int gen = cls.indexOf("$$Lambda");
        if (gen > 0) cls = cls.substring(0, gen);
        return cls + '.' + e.getMethodName() + "()";
    }

    private static Path write(Node root, Path out, String threadName, int taken, int missed,
                              long durationMs, long intervalMs) throws IOException {
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        double msPerSample = taken > 0 ? (double) durationMs / taken : 0;
        try (Writer w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            w.write("# CraftEnginePolyfill sampling profile\n");
            w.write("thread    : " + threadName + "\n");
            w.write("duration  : " + durationMs + "ms at " + intervalMs + "ms intervals\n");
            w.write("samples   : " + taken + " taken, " + missed + " missed (thread idle/parked)\n");
            w.write("weight    : 1 sample ~ " + String.format(java.util.Locale.ROOT, "%.2f", msPerSample) + "ms\n");
            // From spark's API — the one thing it genuinely offers here. The percentages below
            // are shares of WALL time and say nothing about whether a tick is in budget; MSPT does.
            w.write(SparkMetrics.describe());
            w.write("\n# Tree: <total%> <self%> <est ms> frame — a frame's total includes its callees.\n");
            w.write("# Frames under 0.5% of samples are omitted; sort by SELF to find real work.\n\n");
            writeNode(w, root, root.samples, msPerSample, 0);

            w.write("\n\n# Flat, by SELF time — where the thread actually was.\n\n");
            List<Node> flat = new ArrayList<>();
            flatten(root, flat);
            Map<String, int[]> bySelf = new HashMap<>();
            for (Node n : flat) bySelf.computeIfAbsent(n.frame, k -> new int[1])[0] += n.selfSamples;
            bySelf.entrySet().stream()
                    .filter(e -> e.getValue()[0] > 0)
                    .sorted(Comparator.comparingInt((Map.Entry<String, int[]> e) -> e.getValue()[0]).reversed())
                    .limit(60)
                    .forEach(e -> {
                        int s = e.getValue()[0];
                        try {
                            w.write(String.format(java.util.Locale.ROOT, "%6.2f%%  %8.2fms  %s%n",
                                    100.0 * s / Math.max(1, root.samples), s * msPerSample, e.getKey()));
                        } catch (IOException ignored) { }
                    });
        }
        return out;
    }

    private static void flatten(Node n, List<Node> out) {
        out.add(n);
        for (Node c : n.children.values()) flatten(c, out);
    }

    private static void writeNode(Writer w, Node n, int total, double msPerSample, int depth)
            throws IOException {
        if (depth > 0) {
            double share = 100.0 * n.samples / Math.max(1, total);
            if (share < 0.5) return;
            w.write(String.format(java.util.Locale.ROOT, "%6.2f%% %6.2f%% %8.2fms %s%s%n",
                    share, 100.0 * n.selfSamples / Math.max(1, total), n.samples * msPerSample,
                    "  ".repeat(depth - 1), n.frame));
        }
        List<Node> kids = new ArrayList<>(n.children.values());
        kids.sort(Comparator.comparingInt((Node c) -> c.samples).reversed());
        for (Node c : kids) writeNode(w, c, total, msPerSample, depth + 1);
    }
}
