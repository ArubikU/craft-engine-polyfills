package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.machine.MachineType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A one-off dump of the PolyClass generated for the REAL {@link MachineType}, for inspecting what
 * the JIT actually builds for a large production type (see {@code scratch/polyclasses-real}).
 *
 * <p><b>Skipped unless {@code -Dpolyclass.dumpReal=true} is set, and it must be run ALONE.</b>
 * {@link PolyTypeRegistry} is process-global: registering the real MachineType REPLACES the small
 * stand-in "Machine" that several other test classes register in their own {@code @BeforeAll},
 * handing their fake instances to handlers that cast to {@code MachineType.MachineRef}. That breaks
 * unrelated tests depending on class execution order — which is exactly why the routine coverage
 * guarantee lives in {@link RealPolyClassGenerationTest} against a synthetic type instead.
 *
 * <pre>
 * ./gradlew.bat test --tests "*RealMachinePolyClassDumpTest" \
 *     -Dpolyclass.dumpReal=true -PpolyclassDump=&lt;dir&gt;
 * </pre>
 *
 * The {@code --tests} filter is what keeps it safe: no other test class runs, so nothing else has
 * registered a "Machine" to clobber.
 */
class RealMachinePolyClassDumpTest {

    @Test
    void dumpRealMachinePolyClass() {
        assumeTrue(Boolean.getBoolean("polyclass.dumpReal"),
                "opt-in only: pollutes the global registry, must be run alone (see class doc)");

        // Machine inherits from Block and PolyTypeRegistry requires parents first, but the full
        // ScriptBootstrap.init() needs a live Bukkit server. Registration only installs lambdas, so
        // stubbing the parent is enough; only CALLING most handlers would need a server.
        if (!PolyTypeRegistry.has("Block")) PolyTypeRegistry.define("Block");
        MachineType.register();

        PolyType machine = PolyTypeRegistry.get("Machine");
        assertNotNull(machine);
        PolyClassGenerator.buildAll();
        PolyClassGenerator.GeneratedPolyClass generated = PolyClassGenerator.getOrGenerate("Machine");
        assertNotNull(generated);

        java.util.Set<String> covered = new java.util.LinkedHashSet<>(generated.typedMethods().keySet());
        covered.addAll(generated.untypedMethods().keySet());
        for (String m : machine.allMethodNames()) {
            assertTrue(covered.contains(m), "method '" + m + "' got no generated method");
        }
        for (String p : machine.allPropertyNames()) {
            assertTrue(generated.properties().containsKey(p), "property '" + p + "' got no accessor");
        }
        System.out.println("[PolyClass dump] " + generated.internalName()
                + " typed=" + generated.typedMethods().size()
                + " untyped=" + generated.untypedMethods().size()
                + " properties=" + generated.properties().size());
    }
}
