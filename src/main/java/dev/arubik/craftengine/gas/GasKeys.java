/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class GasKeys {
    public static final CustomDataType<GasStack, byte[]> GAS_DATA_TYPE = new CustomDataType<GasStack, byte[]>(NbtType.BYTE_ARRAY, complex -> {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            byte[] byArray;
            try (DataOutputStream dos = new DataOutputStream(baos);){
                dos.writeUTF(complex.getType() == null ? "" : complex.getType().name());
                dos.writeInt(complex.getAmount());
                byArray = baos.toByteArray();
            }
            return byArray;
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to serialize GasStack", e);
        }
    }, primitive -> {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[])primitive);
            try {
                GasType type;
                DataInputStream dis = new DataInputStream(bais);
                try {
                    try {
                        String name = dis.readUTF();
                        type = name.isEmpty() ? null : GasType.valueOf(name);
                    }
                    catch (Throwable unknownOrLegacy) {
                        GasStack gasStack = GasStack.EMPTY;
                        dis.close();
                        bais.close();
                        return gasStack;
                    }
                }
                catch (Throwable throwable) {
                    try {
                        dis.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                int amount = dis.readInt();
                GasStack gasStack = new GasStack(type, amount);
                dis.close();
                return gasStack;
            }
            finally {
                try {
                    bais.close();
                }
                catch (Throwable throwable) {
                    Throwable throwable3;
                    throwable3.addSuppressed(throwable);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to deserialize GasStack", e);
        }
    });
    public static final TypedKey<GasStack> GAS = TypedKey.of("craftengine", "gas", GAS_DATA_TYPE);
    public static final TypedKey<String> TRANSFER_HISTORY = TypedKey.of("craftengine", "transfer_history", NbtType.STRING);
    public static final TypedKey<Integer> GAS_IO_COOLDOWN = TypedKey.of("craftengine", "gas_io_cooldown", NbtType.INTEGER);
    public static final TypedKey<Integer> GAS_BLOCK_COOLDOWN = TypedKey.of("craftengine", "gas_block_cooldown", NbtType.INTEGER);
    public static final TypedKey<Integer> WELL_ACTIVE = TypedKey.of("craftengine", "pressurizer_well_active", NbtType.INTEGER);
    public static final int WELL_PUMP_LIMIT = 5;

    private GasKeys() {
    }
}

