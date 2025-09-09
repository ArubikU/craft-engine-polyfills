package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.util.CustomDataType;
import dev.arubik.craftengine.util.TypedKey;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class FluidKeys {

    private FluidKeys() {}

    public static final TypedKey<FluidStack> FLUID = TypedKey.of("craftengine", "fluid", new CustomDataType<>(
            PersistentDataType.BYTE_ARRAY,
            (complex) -> {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     DataOutputStream dos = new DataOutputStream(baos)) {
                    dos.writeInt(complex.getType().ordinal());
                    dos.writeInt(complex.getAmount());
                    dos.writeInt(complex.getPressure());
                    return baos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to serialize FluidStack", e);
                }
            },
            (primitive) -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(primitive);
                     DataInputStream dis = new DataInputStream(bais)) {
                    FluidType type = FluidType.values()[dis.readInt()];
                    int amount = dis.readInt();
                    int pressure = dis.readInt();
                    return new FluidStack(type, amount, pressure);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to deserialize FluidStack", e);
                }
            }
    ));

}
