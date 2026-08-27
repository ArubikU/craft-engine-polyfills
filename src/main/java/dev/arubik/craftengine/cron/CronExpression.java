package dev.arubik.craftengine.cron;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.BitSet;

/**
 * A minimal standard 5-field cron matcher: minute, hour, day-of-month, month, day-of-week.
 * Deliberately no seconds field (crons here fire at minute granularity — see
 * {@link CronDefinition}) and no year field (standard cron doesn't have one either).
 *
 * <p>Per-field syntax supported: {@code *} (any), a bare integer, a comma-separated list
 * (e.g. {@code 1,15,30}), a range ({@code 1-5}), and a step ({@code * /N} or {@code 1-30/N}).
 */
public final class CronExpression {

    private final BitSet minutes;
    private final BitSet hours;
    private final BitSet daysOfMonth;
    private final BitSet months;
    private final BitSet daysOfWeek;

    private CronExpression(BitSet minutes, BitSet hours, BitSet daysOfMonth, BitSet months, BitSet daysOfWeek) {
        this.minutes = minutes;
        this.hours = hours;
        this.daysOfMonth = daysOfMonth;
        this.months = months;
        this.daysOfWeek = daysOfWeek;
    }

    /**
     * Parses a standard 5-field cron expression. Throws {@link IllegalArgumentException} with a
     * message naming the offending field on malformed input.
     */
    public static CronExpression parse(String expr) {
        if (expr == null || expr.isBlank())
            throw new IllegalArgumentException("cron expression is empty");
        String[] fields = expr.trim().split("\\s+");
        if (fields.length != 5)
            throw new IllegalArgumentException(
                    "cron expression must have exactly 5 fields (minute hour day-of-month month day-of-week), found "
                            + fields.length);
        BitSet minute = parseField(fields[0], 0, 59, "minute");
        BitSet hour = parseField(fields[1], 0, 23, "hour");
        BitSet dom = parseField(fields[2], 1, 31, "day-of-month");
        BitSet month = parseField(fields[3], 1, 12, "month");
        BitSet dow = parseField(fields[4], 0, 6, "day-of-week");
        return new CronExpression(minute, hour, dom, month, dow);
    }

    /** Parses one field (comma-separated list of bare/range/step terms) into a bit set of valid values. */
    private static BitSet parseField(String field, int min, int max, String fieldName) {
        BitSet bits = new BitSet(max + 1);
        for (String term : field.split(",")) {
            parseTerm(term, min, max, fieldName, bits);
        }
        return bits;
    }

    private static void parseTerm(String term, int min, int max, String fieldName, BitSet bits) {
        if (term.isEmpty())
            throw new IllegalArgumentException(fieldName + ": empty term in '" + term + "'");

        int step = 1;
        String rangePart = term;
        int slash = term.indexOf('/');
        if (slash >= 0) {
            rangePart = term.substring(0, slash);
            try {
                step = Integer.parseInt(term.substring(slash + 1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + ": invalid step in '" + term + "'");
            }
            if (step <= 0)
                throw new IllegalArgumentException(fieldName + ": step must be positive in '" + term + "'");
        }

        int rangeStart;
        int rangeEnd;
        if (rangePart.equals("*")) {
            rangeStart = min;
            rangeEnd = max;
        } else if (rangePart.contains("-")) {
            String[] parts = rangePart.split("-", 2);
            try {
                rangeStart = Integer.parseInt(parts[0]);
                rangeEnd = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + ": invalid range in '" + term + "'");
            }
        } else {
            try {
                rangeStart = rangeEnd = Integer.parseInt(rangePart);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + ": invalid value '" + term + "'");
            }
        }

        if (rangeStart < min || rangeEnd > max || rangeStart > rangeEnd)
            throw new IllegalArgumentException(
                    fieldName + ": value out of range in '" + term + "' (expected [" + min + ", " + max + "])");

        for (int v = rangeStart; v <= rangeEnd; v += step) {
            bits.set(v);
        }
    }

    /** True when every field of {@code time} matches this expression. */
    public boolean matches(ZonedDateTime time) {
        if (!minutes.get(time.getMinute())) return false;
        if (!hours.get(time.getHour())) return false;
        if (!daysOfMonth.get(time.getDayOfMonth())) return false;
        if (!months.get(time.getMonthValue())) return false;
        return daysOfWeek.get(toCronDayOfWeek(time.getDayOfWeek()));
    }

    /** java.time.DayOfWeek is 1=Monday..7=Sunday; standard cron is 0=Sunday..6=Saturday. */
    private static int toCronDayOfWeek(DayOfWeek dow) {
        return dow == DayOfWeek.SUNDAY ? 0 : dow.getValue();
    }

    // Sanity checks (manual, no test file per task scope):
    // parse("*/5 * * * *").matches(time at minute 10)  -> true  (10 % 5 == 0)
    // parse("*/5 * * * *").matches(time at minute 12)  -> false
    // parse("0 3 * * *").matches(time at 03:00)         -> true
    // parse("0 3 * * *").matches(time at 04:00)         -> false
    // parse("* * * * 0").matches(a Sunday)               -> true
}
