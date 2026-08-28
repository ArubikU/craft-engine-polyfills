package dev.arubik.craftengine.debug;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the two complementary halves of a memory snapshot.
 *
 * <p><b>The .hprof</b> is the real heap dump — every object, every reference — and the thing to
 * open in a heap analyser when a leak has to be chased to its retaining path. It is binary and
 * large, so it is not something an agent reads.
 *
 * <p><b>The .txt histogram</b> is what makes this useful without a human: one line per class with
 * instance count and retained bytes, biggest first. That is enough to see WHAT is being allocated —
 * which is the question optimisation work actually asks — even though it says nothing about what
 * holds it. The two are written together because they answer different questions and the pair costs
 * one pause.
 *
 * <p>Both come from the JVM's own diagnostic MBeans rather than a bundled agent, so there is no
 * dependency and nothing to keep in sync with the JVM version. Dumping pauses the server for as
 * long as it takes to walk the heap — seconds on a large one — so this is deliberately a command
 * someone runs, never something on a timer.
 */
public final class HeapDump {

    private HeapDump() {}

    /**
     * Dumps live objects to {@code hprof} and their class histogram to {@code histogram}.
     * Returns null on success, or a message describing what failed.
     */
    public static String dump(Path hprof, Path histogram) {
        try {
            if (hprof.getParent() != null) Files.createDirectories(hprof.getParent());
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            // The histogram first: it is the small, readable one, so a failure to write the big
            // binary dump afterwards still leaves something usable behind.
            try {
                Object out = server.invoke(
                        new ObjectName("com.sun.management:type=DiagnosticCommand"),
                        "gcClassHistogram", new Object[]{new String[0]}, new String[]{String[].class.getName()});
                Files.writeString(histogram, String.valueOf(out), StandardCharsets.UTF_8);
            } catch (Throwable t) {
                Files.writeString(histogram, "class histogram unavailable: " + t, StandardCharsets.UTF_8);
            }

            com.sun.management.HotSpotDiagnosticMXBean hotspot =
                    ManagementFactory.newPlatformMXBeanProxy(server,
                            "com.sun.management:type=HotSpotDiagnostic",
                            com.sun.management.HotSpotDiagnosticMXBean.class);
            Files.deleteIfExists(hprof); // dumpHeap refuses to overwrite
            hotspot.dumpHeap(hprof.toAbsolutePath().toString(), true /* live objects only */);
            return null;
        } catch (Throwable t) {
            return t.getClass().getSimpleName() + ": " + t.getMessage();
        }
    }
}
