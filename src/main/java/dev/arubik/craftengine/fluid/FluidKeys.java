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

    // Cooldown por bloque para limitar frecuencia por tipo de fluido (ticks)
    public static final TypedKey<Integer> FLUID_TICK_COOLDOWN = TypedKey.of("craftengine", "fluid_tick_cd", PersistentDataType.INTEGER);

    // Cooldown independiente para operaciones de recolección desde bloques del mundo
    public static final TypedKey<Integer> FLUID_BLOCK_COOLDOWN = TypedKey.of("craftengine", "fluid_block_cd", PersistentDataType.INTEGER);

    // Cooldown independiente para operaciones de I/O con carriers (push y pull)
    public static final TypedKey<Integer> FLUID_IO_COOLDOWN = TypedKey.of("craftengine", "fluid_io_cd", PersistentDataType.INTEGER);

}
