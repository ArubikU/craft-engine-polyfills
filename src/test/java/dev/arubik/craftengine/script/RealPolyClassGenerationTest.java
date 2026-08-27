package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.machine.MachineType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Generates the PolyClass for the REAL {@link MachineType} — not the small stand-in "Machine" the
 * behavioural script tests (WindmillScriptTest, WaterWheelScriptTest, ...) each register for their
 * own purposes.
 *
 * <p>This exists because those stand-ins are what a plain test-run dump captures, which makes the
 * dumped {@code PC_Machine} look like the generator dropped almost everything. It didn't — the
 * stand-in genuinely only has a handful of members. This test pins that the generator handles the
 * real thing: every one of {@code MachineType}'s ~120 registrations gets a generated Java method,
 * none silently skipped, and the class still verifies and loads.
 */
class RealPolyClassGenerationTest {

    @Test
    void everyMemberOfTheRealMachineTypeGetsAGeneratedMethod() {
        // Machine inherits from Block, and PolyTypeRegistry requires parents first — but the full
        // ScriptBootstrap.init() needs a live Bukkit server, so stub the parent and register the
        // real MachineType directly. Registration only installs lambdas, so it needs no server;
        // only CALLING most of those handlers would.
        if (!PolyTypeRegistry.has("Block")) PolyTypeRegistry.define("Block");
        MachineType.register();
        PolyType machine = PolyTypeRegistry.get("Machine");
        assertNotNull(machine, "the real MachineType should have registered");

        int methodCount = machine.allMethodNames().size();
        int propCount = machine.allPropertyNames().size();
        assertTrue(methodCount + propCount > 50,
                "sanity: the real MachineType should be large, got " + methodCount + " methods + "
                        + propCount + " properties");

        PolyClassGenerator.GeneratedPolyClass generated = PolyClassGenerator.getOrGenerate("Machine");
        assertNotNull(generated, "the real MachineType must generate a PolyClass");

        // EVERY registered member must have a generated Java method — typed (native signature) or
        // untyped (erased shim). A member missing from both maps would silently fall back to
        // memberCall forever, which is the exact "the JIT quietly dropped things" failure this
        // guards against.
        for (String m : machine.allMethodNames()) {
            assertTrue(generated.typedMethods().containsKey(m) || generated.untypedMethods().containsKey(m),
                    "method '" + m + "' got no generated method on the PolyClass");
        }
        for (String p : machine.allPropertyNames()) {
            assertTrue(generated.properties().containsKey(p),
                    "property '" + p + "' got no generated accessor on the PolyClass");
        }

        assertEquals(methodCount, generated.typedMethods().size() + generated.untypedMethods().size(),
                "every method should be accounted for exactly once");
        assertEquals(propCount, generated.properties().size());

        // The generated class actually loaded and its handler fields resolved (generate() invokes
        // refresh() before returning), so a large real type doesn't blow any classfile limit.
        assertTrue(generated.typedMethods().size() > 0,
                "the real MachineType has methodTypedN registrations, so some should be natively typed");
    }
}
