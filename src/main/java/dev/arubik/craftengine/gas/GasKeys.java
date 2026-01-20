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
                    dos.writeInt(complex.getType() == null ? -1 : complex.getType().ordinal());
                    dos.writeInt(complex.getAmount());
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to serialize GasStack", e);
                }
            },
            (primitive) -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(primitive);
                        DataInputStream dis = new DataInputStream(bais)) {
                    int ordinal = dis.readInt();
                    GasType type = (ordinal == -1) ? null : GasType.values()[ordinal];
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
}
