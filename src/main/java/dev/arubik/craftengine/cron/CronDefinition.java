package dev.arubik.craftengine.cron;

/**
 * One data-driven cron job loaded from {@code plugins/CraftEnginePolyfill/crons/*.json} — a server
 * owner schedules a {@code .pf} script to run periodically without any Java.
 *
 * <p>Exactly one of {@code cron}/{@code everySeconds} drives a job, because the two trigger
 * mechanisms serve different needs:
 * <ul>
 * <li>{@code cron} — a standard 5-field cron expression (minute, hour, day-of-month, month,
 * day-of-week; no seconds/year fields — see {@link CronExpression}). Calendar-aligned scheduling:
 * "every day at 03:00", "every 5 minutes on the clock", "the 1st of the month". Granularity bottoms
 * out at one minute, which is the natural unit for anything tied to wall-clock time.</li>
 * <li>{@code everySeconds} — a simple fixed-interval counter (see {@link CronScheduler}). For jobs
 * that don't care about calendar alignment and want a tighter interval than a minute (e.g. "every
 * 15 seconds"), where a full cron expression would be overkill.</li>
 * </ul>
 *
 * @param cron         a standard 5-field cron expression, or {@code null} if this job uses
 *                     {@code everySeconds} instead
 * @param everySeconds fixed interval in seconds, or {@code 0} if this job uses {@code cron} instead
 */
public record CronDefinition(String id, String cron, int everySeconds, String script, boolean enabled) {}
