/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class FluidKeys {
    public static final CustomDataType<FluidStack, byte[]> FLUID_DATA_TYPE = new CustomDataType<FluidStack, byte[]>(NbtType.BYTE_ARRAY, complex -> {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            byte[] byArray;
            try (DataOutputStream dos = new DataOutputStream(baos);){
                dos.writeUTF(complex.getType().name());
                dos.writeInt(complex.getAmount());
                dos.writeInt(complex.getPressure());
                byArray = baos.toByteArray();
            }
            return byArray;
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to serialize FluidStack", e);
        }
    }, primitive -> {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[])primitive);
            try {
                FluidType type;
                DataInputStream dis = new DataInputStream(bais);
                try {
                    try {
                        type = FluidType.valueOf(dis.readUTF());
                    }
                    catch (Throwable unknownOrLegacy) {
                        FluidStack fluidStack = FluidStack.EMPTY;
                        dis.close();
                        bais.close();
                        return fluidStack;
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
                int pressure = dis.readInt();
                FluidStack fluidStack = new FluidStack(type, amount, pressure);
                dis.close();
                return fluidStack;
            }
            finally {
                try {
                    bais.close();
                }
                catch (Throwable throwable) {
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to deserialize FluidStack", e);
        }
    });
    public static final TypedKey<FluidStack> FLUID = TypedKey.of("craftengine", "fluid", FLUID_DATA_TYPE);
    public static final TypedKey<Integer> FLUID_TICK_COOLDOWN = TypedKey.of("craftengine", "fluid_tick_cd", NbtType.INTEGER);
    public static final TypedKey<Integer> FLUID_BLOCK_COOLDOWN = TypedKey.of("craftengine", "fluid_block_cd", NbtType.INTEGER);
    public static final TypedKey<Integer> FLUID_IO_COOLDOWN = TypedKey.of("craftengine", "fluid_io_cd", NbtType.INTEGER);
    public static final TypedKey<String> TRANSFER_HISTORY = TypedKey.of("craftengine", "fluid_history", NbtType.STRING);

    private FluidKeys() {
    }
}

