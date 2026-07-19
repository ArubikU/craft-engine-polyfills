package dev.arubik.craftengine.chainery;

import java.util.UUID;

import org.bukkit.NamespacedKey;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.contraption.ContraptionCapture;
import dev.arubik.craftengine.util.NbtType;
import net.momirealms.craftengine.core.block.entity.BlockEntity;

/**
 * The block entity marking one END of a {@link Chain} (CHAINERY — "2 bloques de cadena que son block
 * entity que marcan el punto de inicio y fin y un uuid"). Persists the chain's {@link #getChainId() id} and
 * which end it is, so both endpoints know the span they belong to and the span survives save/load and
 * contraption capture (the id rides in the container bytes, exactly like every other persistent block
 * entity here).
 *
 * <p><b>Break vs assembly.</b> {@link #onRemove()} fires for BOTH a genuine mining/explosion and a
 * contraption capture pulling this cell into a hologram. The chain must be severed only in the former:
 * assembly should carry the endpoint (and its chain) along. It disambiguates with
 * {@link ContraptionCapture#isRemovingForCapture()} — the same static flag every capture-aware block entity
 * here uses — so "se rompe de manera normal (minando o explotando) no al ensamblar" holds.
 */
public class ChainBlockEntity extends PersistentBlockEntity {

    private static final NamespacedKey CHAIN_ID = NamespacedKey.fromString("polyfills:chain_id");
    private static final NamespacedKey CHAIN_ROLE = NamespacedKey.fromString("polyfills:chain_role");

    public ChainBlockEntity(BlockEntity blockEntity) {
        super(blockEntity);
    }

    /** Binds this endpoint to its chain. {@code role} 0 = end A, 1 = end B (purely informational). */
    public void bind(UUID chainId, int role) {
        set(CHAIN_ID, NbtType.STRING, chainId.toString());
        set(CHAIN_ROLE, NbtType.BYTE, (byte) role);
    }

    /** The chain this endpoint belongs to, or null if unbound / not yet written. */
    public UUID getChainId() {
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

    @Override
    public void onRemove() {
        super.onRemove();
        UUID chainId = getChainId();
        if (chainId == null) {
            return;
        }
        // Assembly is moving this endpoint into a contraption — the chain rides along, do NOT sever it.
        if (ContraptionCapture.isRemovingForCapture()) {
            return;
        }
        // A real mine/explosion — sever the whole chain (drops items, removes the far endpoint, clears render).
        ChainEngine.onEndpointBroken(chainId);
    }
}
