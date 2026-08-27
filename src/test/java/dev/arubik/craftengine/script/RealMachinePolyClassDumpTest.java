package dev.arubik.craftengine.script;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Dumps what the JIT actually produces, for inspection — every generated PolyClass, plus every
 * shipped {@code .pf} script compiled against the REAL registered types.
 *
 * <p><b>Skipped unless {@code -PpolyclassDumpReal=true}, and it must be run ALONE.</b>
 * {@link PolyTypeRegistry} is process-global: registering the real types REPLACES the small
 * stand-in "Machine" (and friends) that several other test classes register in their own
 * {@code @BeforeAll}, handing their fake instances to handlers that cast to the real ref types.
 * That breaks unrelated tests by execution order — which is why the routine coverage guarantee
 * lives in {@link RealPolyClassGenerationTest} against a synthetic type instead.
 *
 * <pre>
 * ./gradlew.bat test --tests "*RealMachinePolyClassDumpTest" \
 *     -PpolyclassDumpReal=true -PpolyclassDump=&lt;dir&gt;
 * </pre>
 *
 * The {@code --tests} filter is what keeps it safe: no other test class runs, so nothing else has
 * registered types to clobber.
 *
 * <p>Scripts are compiled under their path relative to {@code scripts/}, so the generated classes
 * land in packages mirroring the script tree ({@code kinetics/generators/windmill} becomes
 * {@code ...script.gen.kinetics.generators.Windmill}) — which is what makes the dumped packages and
 * imports readable as a set.
 */
class RealMachinePolyClassDumpTest {

    private static final Path SCRIPTS = Path.of("src/main/resources/scripts");

    @Test
    void dumpEverything() throws Exception {
        assumeTrue(Boolean.getBoolean("polyclass.dumpReal"),
                "opt-in only: pollutes the global registry, must be run alone (see class doc)");
        String dir = System.getProperty("craftengine.polyclass.dump");
        assumeTrue(dir != null && !dir.isBlank(), "needs -PpolyclassDump=<dir>");

        // Register as much of the real type set as runs headless. ScriptBootstrap.init() reaches
        // Bukkit somewhere in the middle (event wiring), so it may throw partway — everything
        // registered before that point still stands, and that is what the dump specialises against.
        // Without real types registered, dotMethodCall can't specialise and the dumps would show
        // generic dispatch everywhere, which would misrepresent what the motor does in production.
        try {
            ScriptBootstrap.init();
        } catch (Throwable t) {
            System.out.println("[dump] ScriptBootstrap.init() stopped early (expected headless): " + t);
        }
        // init() aborts at the first registration that touches Bukkit, so everything AFTER that
        // point is missing — including Machine, which is most of what the kinetics scripts call.
        // Re-run the important ones individually so one Bukkit-dependent registration doesn't
        // silently make the whole dump unrepresentative (it would show generic dispatch everywhere
        // and look like the JIT wasn't specialising at all).
        tryRegister("Machine", () -> dev.arubik.craftengine.script.types.machine.MachineType.register());
        tryRegister("Redstone", () -> dev.arubik.craftengine.script.types.machine.RedstoneType.register());
        tryRegister("Io", () -> dev.arubik.craftengine.script.types.machine.IoType.register());
        tryRegister("Layout", () -> dev.arubik.craftengine.script.types.machine.LayoutType.register());
        tryRegister("Belt", () -> dev.arubik.craftengine.script.types.machine.BeltType.register());
        tryRegister("Pipe", () -> dev.arubik.craftengine.script.types.machine.PipeType.register());
        tryRegister("Workbench", () -> dev.arubik.craftengine.script.types.machine.WorkbenchType.register());
        tryRegister("Recipe", () -> dev.arubik.craftengine.script.types.machine.RecipeType.register());
        tryRegister("Animation", () -> dev.arubik.craftengine.script.types.machine.AnimationType.register());
        tryRegister("Server", () -> dev.arubik.craftengine.script.types.world.ServerType.register());
        tryRegister("Inventory", () -> dev.arubik.craftengine.script.types.resource.InventoryType.register());
        tryRegister("FluidTanks", () -> dev.arubik.craftengine.script.types.resource.FluidTanksType.register());
        tryRegister("Upgrades", () -> dev.arubik.craftengine.script.types.resource.UpgradesType.register());
        tryRegister("MultiBlock", () -> dev.arubik.craftengine.script.types.machine.MultiBlockType.register());
        tryRegister("Bars", () -> dev.arubik.craftengine.script.types.machine.BarsType.register());
        tryRegister("Network", () -> dev.arubik.craftengine.script.types.machine.NetworkType.register());

        int typeCount = PolyTypeRegistry.typeNames().size();
        System.out.println("[dump] registered types: " + typeCount);
        assertTrue(typeCount > 0, "no types registered — the dump would be meaningless");

        PolyClassGenerator.buildAll();

        PolyType machine = PolyTypeRegistry.get("Machine");
        if (machine != null) {
            PolyClassGenerator.GeneratedPolyClass g = PolyClassGenerator.getOrGenerate("Machine");
            assertNotNull(g);
            java.util.Set<String> covered = new java.util.LinkedHashSet<>(g.typedMethods().keySet());
            covered.addAll(g.untypedMethods().keySet());
            for (String m : machine.allMethodNames()) {
                assertTrue(covered.contains(m), "method '" + m + "' got no generated method");
            }
            System.out.println("[dump] " + g.internalName() + " typed=" + g.typedMethods().size()
                    + " untyped=" + g.untypedMethods().size() + " properties=" + g.properties().size());
        }

        dumpAllScripts(Path.of(dir).resolve("scripts"));
    }

    private static void tryRegister(String label, Runnable register) {
        if (PolyTypeRegistry.has(label)) return; // init() already got this far
        try {
            register.run();
        } catch (Throwable t) {
            System.out.println("[dump] " + label + " could not register headless: " + t);
        }
    }

    /** Compiles every shipped {@code .pf} and writes the generated class, mirroring the script tree
     *  so packages/imports can be read as a set. Reports which scripts the JIT declined, since that
     *  is itself the interesting signal — a declined script is one the compiler could not fully
     *  represent, and the list of them is the honest map of what the motor still doesn't cover. */
    private static void dumpAllScripts(Path outRoot) throws Exception {
        List<Path> scripts = new ArrayList<>();
        try (var walk = Files.walk(SCRIPTS)) {
            walk.filter(p -> p.toString().endsWith(".pf")).sorted().forEach(scripts::add);
        }
        Files.createDirectories(outRoot);

        int compiled = 0, declined = 0, failed = 0, defs = 0;
        List<String> declinedNames = new ArrayList<>();
        for (Path p : scripts) {
            String rel = SCRIPTS.relativize(p).toString().replace('\\', '/');
            String origin = rel.substring(0, rel.length() - ".pf".length());
            try {
                ScriptProgram prog = ScriptProgram.parse(origin, Files.readString(p), Logger.getLogger("dump"));
                ScriptClassCompiler.Compiled c =
                        ScriptClassCompiler.tryCompile(origin, prog.statementsForCompiler());
                if (c == null) {
                    declined++;
                    declinedNames.add(origin);
                    continue;
                }
                Path out = outRoot.resolve(origin + ".class");
                Files.createDirectories(out.getParent());
                Files.write(out, c.classBytes());
                compiled++;
                defs += c.methodsByDefName().size();
            } catch (Throwable t) {
                failed++;
                System.out.println("[dump] " + origin + " FAILED: " + t);
            }
        }
        System.out.println("[dump] scripts: " + scripts.size() + " total, " + compiled + " compiled ("
                + defs + " defs), " + declined + " declined by the JIT, " + failed + " errored");
        declinedNames.sort(Comparator.naturalOrder());
        for (String n : declinedNames) System.out.println("[dump]   declined: " + n);
    }
}
