package dev.arubik.craftengine.chainery;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.util.NbtType;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * The block entity marking one END of a {@link Chain} (CHAINERY — "2 bloques de cadena que son block
 * entity que marcan el punto de inicio y fin y un uuid"). Persists the chain ids that terminate here and
 * which end each is, so both endpoints know the span they belong to and the span survives save/load and
 * contraption capture (the ids ride in the container bytes, exactly like every other persistent block
 * entity here).
 *
 * <p><b>Multiple chains per anchor (fix — "el hecho de encadenar cadenas falla con los contraptions ...
 * solo el ultimo chain funciona bien y el resto se bugea y desaparece al armar el contraption").</b> A
 * shared/junction anchor cell hosts up to 4 chains (see {@code ChainRegistry.BY_ENDPOINT}), but this block
 * entity used to persist a SINGLE {@code chainId} that each new {@link #bind} overwrote — so at a junction
 * only the last-bound chain was visible to {@code ChainEngine.scanContraptions}, and every other chain there
 * lost its captured endpoint and got orphaned/severed the moment the contraption assembled. It now keeps a
 * LIST of {@link Binding}s ({@code bind} adds, {@code unbind} removes), so a daisy-chain's shared anchors
 * carry every chain through capture, not just the newest.
 *
 * <p><b>Break vs assembly.</b> {@link #onRemove()} fires for BOTH a genuine mining/explosion and a
 * contraption capture pulling this cell into a hologram. The chains must be severed only in the former:
 * assembly should carry the endpoints (and their chains) along. It disambiguates with
 * {@link ContraptionCapture#isRemovingForCapture()} — the same static flag every capture-aware block entity
 * here uses — so "se rompe de manera normal (minando o explotando) no al ensamblar" holds.
 */
public class ChainBlockEntity extends PersistentBlockEntity {

    /** Legacy single-binding keys (pre multi-chain). Still READ for back-compat, then migrated into {@link #CHAIN_BINDINGS}. */
    private static final NamespacedKey CHAIN_ID = NamespacedKey.fromString("polyfills:chain_id");
    private static final NamespacedKey CHAIN_ROLE = NamespacedKey.fromString("polyfills:chain_role");
    /** Multi-binding store: {@code "uuid:role"} entries joined by {@code ';'} — see class javadoc. */
    private static final NamespacedKey CHAIN_BINDINGS = NamespacedKey.fromString("polyfills:chain_bindings");

    /** One chain terminating at this cell: its id and which end ({@code role} 0 = A, 1 = B). */
    public record Binding(UUID chainId, int role) {
    }

    public ChainBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    /** Every chain terminating at this cell (empty if unbound). Migrates a legacy single binding on first read. */
    public List<Binding> bindings() {
        List<Binding> out = new ArrayList<>();
        String s = get(CHAIN_BINDINGS, NbtType.STRING);
        if (s != null && !s.isEmpty()) {
            for (String part : s.split(";")) {
                int c = part.lastIndexOf(':');
                if (c <= 0 || c + 1 >= part.length()) {
                    continue;
                }
                try {
                    UUID id = UUID.fromString(part.substring(0, c));
                    int role = part.charAt(c + 1) == '1' ? 1 : 0;
                    out.add(new Binding(id, role));
                } catch (IllegalArgumentException bad) {
                    // skip a malformed entry, keep the rest
                }
            }
            return out;
        }
        // Legacy fallback: a block entity saved before the multi-binding store existed.
        UUID legacy = legacyChainId();
        if (legacy != null) {
            out.add(new Binding(legacy, legacyRole()));
        }
        return out;
    }

    /** Binds {@code chainId} (end {@code role}: 0 = A, 1 = B) to this cell, keeping any chains already bound here. */
    public void bind(UUID chainId, int role) {
        List<Binding> list = bindings();
        for (Binding b : list) {
            if (b.chainId().equals(chainId)) {
                return; // already bound — don't duplicate
            }
        }
        list.add(new Binding(chainId, role));
        write(list);
    }

    /** Removes {@code chainId} from this cell; returns how many chains still terminate here afterwards. */
    public int unbind(UUID chainId) {
        List<Binding> list = bindings();
        list.removeIf(b -> b.chainId().equals(chainId));
        write(list);
        return list.size();
    }

    private void write(List<Binding> list) {
        StringBuilder sb = new StringBuilder();
        for (Binding b : list) {
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(b.chainId()).append(':').append(b.role());
        }
        set(CHAIN_BINDINGS, NbtType.STRING, sb.toString());
    }

    /** First chain bound here, or null — kept for the single-anchor-click path ({@code ChaineryInteractListener}). */
    public UUID getChainId() {
        List<Binding> list = bindings();
        return list.isEmpty() ? null : list.get(0).chainId();
    }

    /** Role of the first binding (0 = A, 1 = B). Defaults to 0. */
    public int getRole() {
        List<Binding> list = bindings();
        return list.isEmpty() ? 0 : list.get(0).role();
    }

    private UUID legacyChainId() {
        String s = get(CHAIN_ID, NbtType.STRING);
        if (s == null) {
            return null;
        }
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException bad) {
            return null;
        }
    }

    private int legacyRole() {
        Byte r = get(CHAIN_ROLE, NbtType.BYTE);
        return r == null ? 0 : (r == 0 ? 0 : 1);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        List<Binding> list = bindings();
        if (list.isEmpty()) {
            return;
        }
        // Assembly is moving this endpoint into a contraption — the chains ride along, do NOT sever them.
        if (ContraptionCapture.isRemovingForCapture()) {
            return;
        }
        // A real mine/explosion — sever EVERY chain anchored here (a junction breaks all of its spans).
        for (Binding b : list) {
            ChainEngine.onEndpointBroken(b.chainId());
        }
    }
}
