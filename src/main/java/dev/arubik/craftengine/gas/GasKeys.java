package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.TypedKey;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class GasKeys {

    private GasKeys() {
    }

    public static final CustomDataType<GasStack, byte[]> GAS_DATA_TYPE = new CustomDataType<>(
            PersistentDataType.BYTE_ARRAY,
            (complex) -> {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(baos)) {
                    // Persist the type by NAME, not ordinal (reordering GasType would remap stored gas).
                    dos.writeUTF(complex.getType() == null ? "" : complex.getType().name());
                    dos.writeInt(complex.getAmount());
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to serialize GasStack", e);
                }
            },
            (primitive) -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(primitive);
                        DataInputStream dis = new DataInputStream(bais)) {
                    GasType type;
                    try {
                        String name = dis.readUTF();
                        type = name.isEmpty() ? null : GasType.valueOf(name);
                    } catch (Throwable unknownOrLegacy) {
                        return GasStack.EMPTY;
                    }
                    int amount = dis.readInt();
                    return new GasStack(type, amount);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to deserialize GasStack", e);
                }
            });

    // Default key if needed, but we encourage typed keys per tank
    public static final TypedKey<GasStack> GAS = TypedKey.of("craftengine", "gas", GAS_DATA_TYPE);

    public static final TypedKey<String> TRANSFER_HISTORY = TypedKey.of("craftengine", "transfer_history",
            PersistentDataType.STRING);
    public static final TypedKey<Integer> GAS_IO_COOLDOWN = TypedKey.of("craftengine", "gas_io_cooldown",
            PersistentDataType.INTEGER);
    public static final TypedKey<Integer> GAS_BLOCK_COOLDOWN = TypedKey.of("craftengine", "gas_block_cooldown",
            PersistentDataType.INTEGER);

    /**
     * Written by a formed + steam-fed Pressurizer Well core to its own block-entity data (1 = active).
     * A gas pump reads it on the cal block directly below the well core to raise its vein's pump limit.
     */
    public static final TypedKey<Integer> WELL_ACTIVE = TypedKey.of("craftengine", "pressurizer_well_active",
            PersistentDataType.INTEGER);
    /** Pump limit a single active Pressurizer Well grants to its anchored vein. */
    public static final int WELL_PUMP_LIMIT = 5;
}
