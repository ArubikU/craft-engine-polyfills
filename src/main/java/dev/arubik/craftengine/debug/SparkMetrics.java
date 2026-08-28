package dev.arubik.craftengine.debug;

import java.lang.reflect.Method;

/**
 * Reads what spark's API actually exposes, for the header of a profile report.
 *
 * <h2>What spark's API can and cannot do</h2>
 * The intent was to get the profile itself from spark. It cannot be done, and that is not a guess:
 * the {@code spark-api} jar shipped with this server contains exactly {@code Spark},
 * {@code SparkProvider}, {@code GarbageCollector}, {@code PlaceholderResolver} and the
 * {@code statistic} package. There is no profiler, sampler or dump type in it. spark's own profiler
 * is driven by its command and its results are UPLOADED — the server's {@code plugins/spark/
 * activity.json} records every past run as {@code "type": "url"}, pointing at spark.lucko.me.
 * Nothing lands on disk for something unattended to read.
 *
 * <p>So the sampling in these reports is {@link ThreadSampler}'s, and spark supplies what it is
 * genuinely good for: TPS and, more usefully, MSPT. A sampler reports shares of WALL time, which
 * says nothing about whether a tick is in budget; MSPT says exactly that. Reading them together is
 * how "30% of wall time in the machine tick" becomes "and the tick costs 12ms, so that is 3.6ms".
 *
 * <p>Reflective because spark is provided by the server, not by this plugin: the API is on the
 * server's classpath as a library, and a server without it should lose the header, not the report.
 */
public final class SparkMetrics {

    private SparkMetrics() {}

    /** A human-readable block for the report header, or a line saying why there is none. */
    public static String describe() {
        try {
            Class<?> providerClass = Class.forName("me.lucko.spark.api.SparkProvider");
            Object spark = providerClass.getMethod("get").invoke(null);
            if (spark == null) return "spark    : present but not initialised\n";

            StringBuilder sb = new StringBuilder();
            sb.append("spark    : ").append(tps(spark)).append('\n');
            String mspt = mspt(spark);
            if (mspt != null) sb.append("mspt     : ").append(mspt).append('\n');
            return sb.toString();
        } catch (ClassNotFoundException notInstalled) {
            return "spark    : not installed (TPS/MSPT unavailable)\n";
        } catch (Throwable t) {
            return "spark    : unavailable (" + t.getClass().getSimpleName() + ")\n";
        }
    }

    private static String tps(Object spark) throws Exception {
        Object stat = spark.getClass().getMethod("tps").invoke(spark);
        if (stat == null) return "no TPS statistic";
        Class<?> window = Class.forName("me.lucko.spark.api.statistic.StatisticWindow$TicksPerSecond");
        return "TPS " + fmt(poll(stat, enumOf(window, "SECONDS_10")))
                + " (10s), " + fmt(poll(stat, enumOf(window, "MINUTES_1"))) + " (1m)";
    }

    /**
     * MSPT is the number that matters for optimisation: how long a tick actually takes. Anything
     * under 50ms is in budget, however alarming a wall-time percentage looks.
     */
    private static String mspt(Object spark) {
        try {
            Object stat = spark.getClass().getMethod("mspt").invoke(spark);
            if (stat == null) return null;
            Class<?> window = Class.forName("me.lucko.spark.api.statistic.StatisticWindow$MillisPerTick");
            Object info = poll(stat, enumOf(window, "MINUTES_1"));
            if (info == null) return null;
            return "mean " + fmt(call(info, "mean")) + "ms, 95th " + fmt(call(info, "percentile95th"))
                    + "ms, max " + fmt(call(info, "max")) + "ms (1m) - budget is 50ms";
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Calls {@code poll(window)} found BY NAME AND ARITY rather than by signature.
     *
     * <p>Signature lookup does not work here and the reason is worth recording: spark declares
     * {@code GenericStatistic<V, W extends Enum<W> & StatisticWindow>}, so {@code poll}'s parameter
     * erases to {@code Enum}, not to {@code StatisticWindow}. Asking for
     * {@code getMethod("poll", StatisticWindow.class)} therefore throws NoSuchMethodException —
     * which is exactly what the first version of this class did, and it reported
     * "spark: unavailable" while spark was working perfectly.
     */
    private static Object poll(Object stat, Object window) throws Exception {
        for (java.lang.reflect.Method m : stat.getClass().getMethods()) {
            if (m.getName().equals("poll") && m.getParameterCount() == 1) {
                m.setAccessible(true);
                return m.invoke(stat, window);
            }
        }
        throw new NoSuchMethodException("poll(window) on " + stat.getClass().getName());
    }

    private static Object call(Object target, String name) throws Exception {
        Method m = target.getClass().getMethod(name);
        m.setAccessible(true);
        return m.invoke(target);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object enumOf(Class<?> type, String name) {
        return Enum.valueOf((Class<Enum>) type, name);
    }

    private static String fmt(Object d) {
        return d instanceof Number n ? String.format(java.util.Locale.ROOT, "%.2f", n.doubleValue())
                                     : String.valueOf(d);
    }
}
