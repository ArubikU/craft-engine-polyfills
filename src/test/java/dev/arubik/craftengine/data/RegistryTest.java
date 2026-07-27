package dev.arubik.craftengine.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.core.util.Key;

/**
 * The freeze contract is the whole point of {@link Registry}: content may only be
 * registered while the load phase is open, so a plugin that tries to add a fluid
 * at runtime fails loudly instead of adding state that silently vanishes on the
 * next reload.
 */
class RegistryTest {

    private static Key key(String path) {
        return Key.of("test", path);
    }

    @Test
    void registersAndReadsBack() {
        Registry<String> registry = Registries.create("t_basic");
        registry.register(key("a"), "alpha");
        registry.register(key("b"), "beta");

        assertEquals("alpha", registry.get(key("a")));
        assertEquals(2, registry.size());
        assertTrue(registry.contains(key("b")));
        assertNull(registry.get(key("missing")));
    }

    @Test
    void keepsInsertionOrder() {
        Registry<String> registry = Registries.create("t_order");
        for (String path : List.of("z", "m", "a"))
            registry.register(key(path), path);

        assertEquals(List.of(key("z"), key("m"), key("a")), registry.keys());
        assertEquals(List.of("z", "m", "a"), registry.values());
    }

    @Test
    void reRegisteringReplacesWithoutDuplicatingTheKey() {
        Registry<String> registry = Registries.create("t_replace");
        registry.register(key("a"), "first");
        registry.register(key("a"), "second");

        assertEquals("second", registry.get(key("a")));
        assertEquals(1, registry.size(), "replacing a binding must not duplicate its key");
        assertEquals(List.of(key("a")), registry.keys());
    }

    @Test
    void writingAfterFreezeThrows() {
        Registry<String> registry = Registries.create("t_freeze");
        registry.register(key("a"), "alpha");
        Registries.freezeAll();

        assertTrue(registry.isFrozen());
        assertThrows(FrozenRegistryException.class, () -> registry.register(key("b"), "beta"));
        assertThrows(FrozenRegistryException.class, () -> registry.unregister(key("a")));
        assertThrows(FrozenRegistryException.class, registry::clear);
        // Reads keep working while frozen — that is the point of freezing.
        assertEquals("alpha", registry.get(key("a")));
    }

    @Test
    void getOrThrowNamesTheMissingEntry() {
        Registry<String> registry = Registries.create("t_throw");
        registry.register(key("known"), "v");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> registry.getOrThrow(key("unknown")));
        assertTrue(error.getMessage().contains("unknown"), error.getMessage());
    }

    @Test
    void rejectsNullIdAndValue() {
        Registry<String> registry = Registries.create("t_null");
        assertThrows(IllegalArgumentException.class, () -> registry.register(null, "v"));
        assertThrows(IllegalArgumentException.class, () -> registry.register(key("a"), null));
    }

    @Test
    void reloadClearsNormalRegistriesButKeepsIdentityOnes() {
        Registry<String> rebuilt = Registries.create("t_rebuilt", true);
        Registry<StringBuilder> identity = Registries.create("t_identity", false);

        rebuilt.register(key("a"), "old");
        StringBuilder stable = identity.register(key("a"), new StringBuilder("old"));

        AtomicInteger runs = new AtomicInteger();
        Registries.addLoader("t_loader", Registries.PHASE_TYPES, () -> {
            runs.incrementAndGet();
            // A rebuilt registry starts empty each cycle; an identity one keeps its
            // entries so live references stay valid, and the loader mutates in place.
            assertEquals(0, rebuilt.size());
            assertEquals(1, identity.size());
            rebuilt.register(key("a"), "new");
            identity.get(key("a")).setLength(0);
            identity.get(key("a")).append("new");
        });

        Registries.reload();

        assertEquals(1, runs.get());
        assertEquals("new", rebuilt.get(key("a")));
        assertSame(stable, identity.get(key("a")), "identity entries must survive a reload");
        assertEquals("new", identity.get(key("a")).toString());
        assertTrue(Registries.isFrozen(), "reload must leave everything frozen");
    }

    @Test
    void aFailingLoaderDoesNotAbortTheCycle() {
        Registry<String> registry = Registries.create("t_resilient");
        Registries.addLoader("t_bad", Registries.PHASE_TYPES, () -> {
            throw new IllegalStateException("boom");
        });
        Registries.addLoader("t_good", Registries.PHASE_RECIPES, () -> registry.register(key("ok"), "v"));

        Registries.reload();

        assertEquals("v", registry.get(key("ok")), "a later loader must still run");
        assertTrue(Registries.isFrozen());
    }

    @Test
    void loadersRunInPhaseOrder() {
        StringBuilder order = new StringBuilder();
        Registries.addLoader("t_recipes", Registries.PHASE_RECIPES, () -> order.append("R"));
        Registries.addLoader("t_types", Registries.PHASE_TYPES, () -> order.append("T"));
        Registries.addLoader("t_defs", Registries.PHASE_DEFINITIONS, () -> order.append("D"));

        Registries.reload();

        // Types before definitions before recipes, whatever order they were declared in.
        assertEquals("TDR", order.toString().replaceAll("[^TDR]", ""));
    }

    @Test
    void duplicateRegistryNameIsRejected() {
        Registries.create("t_dup");
        assertThrows(IllegalStateException.class, () -> Registries.create("t_dup"));
    }

    @Test
    void emptyRegistryReportsEmpty() {
        Registry<String> registry = Registries.create("t_empty");
        assertTrue(registry.isEmpty());
        assertFalse(registry.contains(key("a")));
        assertEquals(List.of(), registry.values());
    }
}
