package dev.arubik.craftengine.debug;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-phase and per-machine-type tick accounting.
 *
 * <p>A sampling profile says "RendererManager.tick is 9.71% of wall time", which is where to look
 * but not what to do: it cannot say whether that is one pathological machine or four hundred cheap
 * ones, nor which machine TYPE, nor how it compares to the tick budget. This measures exactly that —
 * ms per phase per tick, and ms each / ms per tick per machine type.
 *
 * <h2>Cost when off</h2>
 * Every instrumentation point is {@code long t = MachineProfiler.begin();} … {@code end(PHASE, t)},
 * and {@code begin} returns 0 without calling {@code nanoTime} unless profiling is on. So the cost
 * of leaving these calls in place is one volatile read and a predictable branch — which matters,
 * because they sit inside the per-machine per-tick path they exist to measure.
 *
 * <p>When ON it calls {@code nanoTime} twice per phase per machine per tick. That is genuinely
 * expensive and will itself show up in a sampling profile; the in-game panel this feeds says
 * "profiler is active, expect degraded performance" for the same reason.
 *
 * <h2>Loaded vs ticking</h2>
 * Both are counted, because they answer different questions and the gap between them IS the
 * finding: 321 conveyor belts loaded and 0 ticking is a very different situation from 27 machines
 * doing all the work. Loaded is maintained by the block entities themselves as they are created and
 * removed; ticking is whatever actually ran during the window.
 */
public final class MachineProfiler {

    private MachineProfiler() {}

    /** The phases a machine tick divides into — one per gating flag, plus the shared overhead. */
    public enum Phase {
        RENDERERS("renderers"), SCRIPTS("scripts"), RECIPES("recipes"), KINETICS("kinetics"),
        IO_PULL("io_pull"), ANIMATIONS("animations"), REDSTONE("redstone"), UI("ui_tick"),
        CONTEXT("context"), OTHER("other");

        public final String label;
        Phase(String label) { this.label = label; }
    }

    private static final Phase[] PHASES = Phase.values();

    /** Read on every instrumentation point, so it is volatile and nothing else. */
    private static volatile boolean enabled;

    // Written only from the server thread while enabled, so plain longs are correct and cheap.
    private static final long[] phaseNanos = new long[PHASES.length];
    private static final Map<String, long[]> perType = new LinkedHashMap<>(); // id -> {nanos, ticks}
    private static long windowStartNanos;
    private static int serverTicks;

    /** id -> how many are loaded right now. Concurrent: block entities load off the server thread. */
    private static final Map<String, Integer> loadedByType = new ConcurrentHashMap<>();
    private static final Map<String, String> kindByType = new ConcurrentHashMap<>();

    // ---- instrumentation ---------------------------------------------------

    /** Zero when off — which is what keeps these call sites free outside a profiling window. */
    public static long begin() {
        return enabled ? System.nanoTime() : 0L;
    }

    public static void end(Phase phase, long begun) {
        if (begun == 0L) return;
        phaseNanos[phase.ordinal()] += System.nanoTime() - begun;
    }

    /** One machine's whole tick, attributed to its type. */
    public static void endMachine(String typeId, long begun) {
        if (begun == 0L || typeId == null) return;
        long[] cell = perType.computeIfAbsent(typeId, k -> new long[2]);
        cell[0] += System.nanoTime() - begun;
        cell[1]++;
    }

    public static void countServerTick() {
        if (enabled) serverTicks++;
    }

    // ---- loaded/unloaded ---------------------------------------------------

    public static void onLoaded(String typeId, String kind) {
        if (typeId == null) return;
        loadedByType.merge(typeId, 1, Integer::sum);
        kindByType.putIfAbsent(typeId, kind == null ? "classic" : kind);
    }

    public static void onUnloaded(String typeId) {
        if (typeId == null) return;
        loadedByType.computeIfPresent(typeId, (k, v) -> v <= 1 ? null : v - 1);
    }

    // ---- control -----------------------------------------------------------

    public static synchronized void start() {
        java.util.Arrays.fill(phaseNanos, 0L);
        perType.clear();
        serverTicks = 0;
        windowStartNanos = System.nanoTime();
        enabled = true;
    }

    public static synchronized void stop() { enabled = false; }

    public static boolean isEnabled() { return enabled; }

    // ---- reporting ---------------------------------------------------------

    public record TypeRow(String id, String kind, int loaded, int ticking, double msEach, double msPerTick) {}

    public record Report(int serverTicks, double windowSeconds, Map<String, Double> phaseMsPerTick,
                          double totalMsPerTick, List<TypeRow> types) {}

    /**
     * The report as MiniMessage lines, laid out as the in-game profiler HUD lays it out: phases
     * first (what kind of work), then counts (how much of it there is), then cost per type (which
     * machine to go and look at).
     */
    public static synchronized List<String> render() {
        Report r = report();
        List<String> out = new ArrayList<>();
        out.add("<dark_gray><st>                                        </st>");
        out.add("<gold>Machines ms/tick <gray>(" + r.serverTicks() + " ticks, "
                + String.format(java.util.Locale.ROOT, "%.1f", r.windowSeconds()) + "s)");
        StringBuilder phaseLine = new StringBuilder("  ");
        for (Map.Entry<String, Double> e : r.phaseMsPerTick().entrySet()) {
            phaseLine.append("<aqua>").append(e.getKey()).append(" <white>")
                     .append(String.format(java.util.Locale.ROOT, "%.3f", e.getValue())).append("  ");
        }
        out.add(phaseLine.toString());
        out.add("  <yellow>total <white>" + String.format(java.util.Locale.ROOT, "%.3f", r.totalMsPerTick())
                + " ms/tick");

        out.add("<gold>Machines by type <gray>(loaded / ticking)");
        int shown = 0, loadedAll = 0, tickingAll = 0;
        for (TypeRow t : r.types()) { loadedAll += t.loaded(); tickingAll += t.ticking(); }
        out.add("  <white>all machines<gray>: <white>" + loadedAll + "<gray> loaded, <white>"
                + tickingAll + "<gray> ticking");
        List<TypeRow> byLoaded = new ArrayList<>(r.types());
        byLoaded.sort(Comparator.comparingInt(TypeRow::loaded).reversed());
        for (TypeRow t : byLoaded) {
            if (t.loaded() == 0 && t.ticking() == 0) continue;
            if (shown++ >= 15) break;
            out.add("  <gray>" + t.id() + " <dark_gray>[" + t.kind() + "]<gray>: <white>"
                    + t.loaded() + "<gray> loaded, <white>" + t.ticking() + "<gray> ticking");
        }

        out.add("<gold>Machine cost <gray>(ms each / ms per tick)");
        shown = 0;
        for (TypeRow t : r.types()) {
            if (t.msPerTick() <= 0) continue;
            if (shown++ >= 15) break;
            out.add("  <gray>" + t.id() + ": <white>"
                    + String.format(java.util.Locale.ROOT, "%.3f", t.msEach()) + "<gray> ms each, <white>"
                    + String.format(java.util.Locale.ROOT, "%.3f", t.msPerTick()) + "<gray> ms/tick");
        }
        if (shown == 0) out.add("  <dark_gray>no machine ticked during the window");
        out.add("<dark_gray><st>                                        </st>");
        return out;
    }

    public static synchronized Report report() {
        int ticks = Math.max(1, serverTicks);
        double windowSec = (System.nanoTime() - windowStartNanos) / 1_000_000_000.0;

        Map<String, Double> phases = new LinkedHashMap<>();
        double total = 0;
        for (Phase p : PHASES) {
            double msPerTick = phaseNanos[p.ordinal()] / 1_000_000.0 / ticks;
            if (msPerTick > 0) phases.put(p.label, msPerTick);
            total += msPerTick;
        }

        List<TypeRow> rows = new ArrayList<>();
        java.util.Set<String> ids = new java.util.LinkedHashSet<>(loadedByType.keySet());
        ids.addAll(perType.keySet());
        for (String id : ids) {
            long[] cell = perType.get(id);
            long nanos = cell == null ? 0 : cell[0];
            long invocations = cell == null ? 0 : cell[1];
            // "ticking" is how many DISTINCT machines ran, approximated by invocations per server
            // tick — a machine that ticked every tick counts once, which is what the panel means.
            int ticking = (int) Math.round(invocations / (double) ticks);
            rows.add(new TypeRow(id, kindByType.getOrDefault(id, "classic"),
                    loadedByType.getOrDefault(id, 0), ticking,
                    invocations == 0 ? 0 : nanos / 1_000_000.0 / invocations,
                    nanos / 1_000_000.0 / ticks));
        }
        rows.sort(Comparator.comparingDouble(TypeRow::msPerTick).reversed());
        return new Report(serverTicks, windowSec, phases, total, rows);
    }
}
