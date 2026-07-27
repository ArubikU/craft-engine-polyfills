package dev.arubik.craftengine.data;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.momirealms.craftengine.core.util.Key;

/**
 * A public, keyed, insertion-ordered registry that is writable only while the
 * data-load phase is open and read-only ("frozen") afterwards.
 *
 * <p>
 * Registries are created through {@link Registries#create(String)} so the
 * central manager can seal and reopen every one of them in lockstep with the
 * plugin's load/reload cycle. Addons may register their own entries from a
 * {@code CraftEngineReloadEvent} listener with a lower priority than ours;
 * anything later throws {@link FrozenRegistryException}.
 *
 * <p>
 * Reads are lock-free and safe from any thread. Writes are expected to happen
 * on the main thread during load, but are still guarded so a stray async
 * registration cannot corrupt the map.
 */
public final class Registry<T> implements Iterable<Registry.Entry<T>> {

    /** A single {@code id -> value} binding, kept in registration order. */
    public record Entry<T>(Key id, T value) {
    }

    private final String name;
    private final boolean clearOnReload;
    private final Map<Key, T> byId = new ConcurrentHashMap<>();
    /** Insertion order, so iteration and dumps are deterministic. */
    private final List<Key> order = new CopyOnWriteArrayList<>();
    private volatile boolean frozen;

    Registry(String name, boolean clearOnReload) {
        this.name = name;
        this.clearOnReload = clearOnReload;
    }

    public String name() {
        return name;
    }

    /**
     * Whether {@link Registries#reload()} empties this registry before re-running
     * the loaders.
     *
     * <p>
     * {@code false} for registries whose entries are <em>identities</em> that live
     * world state and save files point at — a fluid type, a gas type, a pipe kind.
     * Yanking those on reload would leave dangling references in loaded chunks, so
     * instead the entries survive and their loader overwrites each entry's
     * properties in place.
     */
    public boolean clearsOnReload() {
        return clearOnReload;
    }

    // ---------------------------------------------------------------- writing

    /**
     * Binds {@code id} to {@code value}, replacing any previous binding.
     *
     * @return {@code value}, so callers can register and keep a reference in one
     *         expression
     * @throws FrozenRegistryException if the load phase has already closed
     */
    public T register(Key id, T value) {
        if (frozen)
            throw new FrozenRegistryException(name, id);
        if (id == null)
            throw new IllegalArgumentException("Registry '" + name + "': id must not be null");
        if (value == null)
            throw new IllegalArgumentException("Registry '" + name + "': value for '" + id + "' must not be null");
        synchronized (this) {
            if (frozen)
                throw new FrozenRegistryException(name, id);
            if (byId.put(id, value) == null)
                order.add(id);
        }
        return value;
    }

    /** Registers only if {@code id} is still free. Returns the effective value. */
    public T registerIfAbsent(Key id, T value) {
        T existing = byId.get(id);
        return existing != null ? existing : register(id, value);
    }

    /** Removes a binding. Load-phase only. */
    public boolean unregister(Key id) {
        if (frozen)
            throw new FrozenRegistryException(name, id);
        synchronized (this) {
            if (frozen)
                throw new FrozenRegistryException(name, id);
            if (byId.remove(id) == null)
                return false;
            order.remove(id);
            return true;
        }
    }

    /** Drops every binding. Load-phase only; used at the start of a reload. */
    public void clear() {
        if (frozen)
            throw new FrozenRegistryException(name, "*");
        synchronized (this) {
            if (frozen)
                throw new FrozenRegistryException(name, "*");
            byId.clear();
            order.clear();
        }
    }

    // ---------------------------------------------------------------- reading

    public T get(Key id) {
        return id == null ? null : byId.get(id);
    }

    public Optional<T> find(Key id) {
        return Optional.ofNullable(get(id));
    }

    public T getOrThrow(Key id) {
        T value = get(id);
        if (value == null)
            throw new IllegalArgumentException(
                    "Registry '" + name + "' has no entry '" + id + "'. Known: " + keys());
        return value;
    }

    public T getOrDefault(Key id, T fallback) {
        T value = get(id);
        return value != null ? value : fallback;
    }

    public boolean contains(Key id) {
        return id != null && byId.containsKey(id);
    }

    public int size() {
        return byId.size();
    }

    public boolean isEmpty() {
        return byId.isEmpty();
    }

    /** Registered ids, in registration order. */
    public List<Key> keys() {
        return Collections.unmodifiableList(order);
    }

    /** Registered values, in registration order. */
    public List<T> values() {
        return order.stream().map(byId::get).filter(java.util.Objects::nonNull).toList();
    }

    /** Live read-only view keyed by id. */
    public Map<Key, T> asMap() {
        return Collections.unmodifiableMap(byId);
    }

    @Override
    public Iterator<Entry<T>> iterator() {
        Iterator<Key> ids = order.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return ids.hasNext();
            }

            @Override
            public Entry<T> next() {
                Key id = ids.next();
                return new Entry<>(id, byId.get(id));
            }
        };
    }

    // --------------------------------------------------------------- freezing

    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Seals the registry. Called by {@link Registries#freezeAll()} once every
     * loader has run; there is deliberately no public way to reopen one.
     */
    void freeze() {
        this.frozen = true;
    }

    /** Reopens the registry for a reload. Package-private on purpose. */
    void thaw() {
        this.frozen = false;
    }

    @Override
    public String toString() {
        return "Registry[" + name + ", " + byId.size() + " entries, " + (frozen ? "frozen" : "open") + "]";
    }

    /** Convenience for code that only needs the id set as a {@link Set}. */
    public Set<Key> idSet() {
        return Collections.unmodifiableSet(byId.keySet());
    }
}
