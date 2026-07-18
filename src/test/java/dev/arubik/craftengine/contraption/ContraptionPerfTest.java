package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ContraptionPerf.Window}, the rolling-average accumulator behind
 * {@code /cep contraption perf} — pure arithmetic, no clock and no Bukkit.
 */
class ContraptionPerfTest {

    @Test
    void emptyWindowReportsZeroRatherThanDividingByZero() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        assertEquals(0.0, w.mean());
        assertEquals(0, w.count());
    }

    @Test
    void meanAveragesOverSamplesSeenSoFarNotOverFullCapacity() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        w.add(10);
        w.add(20);
        // A partially-filled window must divide by 2, not by SIZE — otherwise every reading is
        // near-zero until the window fills and the report is unusable for its first few seconds.
        assertEquals(15.0, w.mean());
        assertEquals(2, w.count());
    }

    @Test
    void windowEvictsOldestOnceFullSoTheMeanTracksRecentTicksOnly() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        for (int i = 0; i < ContraptionPerf.Window.SIZE; i++) {
            w.add(100);
        }
        assertEquals(100.0, w.mean());
        assertEquals(ContraptionPerf.Window.SIZE, w.count());

        // Overwrite the whole window with a new value: the mean must end up entirely on the new
        // value, proving the running sum evicts rather than accumulating forever.
        for (int i = 0; i < ContraptionPerf.Window.SIZE; i++) {
            w.add(200);
        }
        assertEquals(200.0, w.mean());
        assertEquals(ContraptionPerf.Window.SIZE, w.count());
    }

    @Test
    void countSaturatesAtCapacity() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        for (int i = 0; i < ContraptionPerf.Window.SIZE * 3; i++) {
            w.add(1);
        }
        assertEquals(ContraptionPerf.Window.SIZE, w.count());
        assertEquals(1.0, w.mean());
    }

    @Test
    void meanIsAPlainAverageAcrossAFullWrapAround() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        // Fill, then push exactly one more so the buffer wraps: the evicted sample is the first.
        for (int i = 1; i <= ContraptionPerf.Window.SIZE; i++) {
            w.add(i);
        }
        long expectedSum = 0;
        for (int i = 2; i <= ContraptionPerf.Window.SIZE + 1; i++) {
            expectedSum += i;
        }
        w.add(ContraptionPerf.Window.SIZE + 1);
        assertEquals((double) expectedSum / ContraptionPerf.Window.SIZE, w.mean(), 1.0e-9);
    }

    @Test
    void clearResetsSumAndCountSoAReArmStartsFresh() {
        ContraptionPerf.Window w = new ContraptionPerf.Window();
        for (int i = 0; i < 10; i++) {
            w.add(500);
        }
        w.clear();
        assertEquals(0, w.count());
        assertEquals(0.0, w.mean());
        // A stale sample must not survive the clear and pollute the next reading.
        w.add(1);
        assertEquals(1.0, w.mean());
    }

    @Test
    void instrumentationIsOffByDefaultSoAnUnaskedServerPaysNothing() {
        // The whole point of the gate: nobody has asked for profiling, so no nanoTime is ever called.
        assertFalse(ContraptionPerf.enabled());
    }

    @Test
    void setEnabledTogglesTheGate() {
        try {
            ContraptionPerf.setEnabled(true);
            assertTrue(ContraptionPerf.enabled());
            ContraptionPerf.setEnabled(false);
            assertFalse(ContraptionPerf.enabled());
        } finally {
            ContraptionPerf.setEnabled(false);
        }
    }
}
