package dev.arubik.craftengine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Shared conversion and config-coercion helpers for the polyfill.
 *
 * <p>Two groups:</p>
 * <ul>
 *   <li><b>Direction / NMS conversion</b> — {@link #oppositeDirection}, {@link #fromDirection},
 *       {@link #toBlockHitResult}, {@link #getRelativeBlockPos}, {@link #fromPos} bridge between
 *       craft-engine's {@code core.util.Direction}/{@code core.world} types and Mojang-mapped NMS.</li>
 *   <li><b>Config coercion</b> — the {@code getAs*} helpers replace craft-engine's removed
 *       {@code ResourceConfigUtils.getAs*}. Each accepts a raw config value ({@code Object}, typically
 *       from {@code ConfigSection.getOrDefault}) and coerces it: {@code getAsStringList} returns an
 *       empty list for {@code null} and wraps a scalar as a singleton; the scalar {@code getAsBoolean/
 *       Int/Double/Float} accept {@link Number}/{@link String} and otherwise throw
 *       {@link IllegalArgumentException} naming the offending key. {@code requireNonNullOrThrow} and
 *       {@code requireNonEmptyStringOrThrow} validate presence.</li>
 * </ul>
 */
public class Utils {

    /**
     * The six directions, shared.
     *
     * <p>{@code Direction.values()} clones its array on every call — that is what an enum's
     * generated {@code values()} does — and this plugin walks the six faces inside per-tick loops
     * for every machine, pipe and energy node on the server. The clones showed up on the server
     * thread as 0.40% of its time in {@code getEnumConstantsShared}, allocating six-element arrays
     * nothing ever writes to.
     *
     * <p>Never mutate it. It is shared by every caller precisely so that no copy is made.
     */
    public static final net.minecraft.core.Direction[] DIRECTIONS = net.minecraft.core.Direction.values();


    public static Direction oppositeDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.SOUTH;
            case EAST -> Direction.WEST;
            case SOUTH -> Direction.NORTH;
            case WEST -> Direction.EAST;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
        };
    }

    public static Direction fromDirection(net.momirealms.craftengine.core.util.Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.NORTH;
            case EAST -> Direction.EAST;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }

    public static BlockHitResult toBlockHitResult(net.momirealms.craftengine.core.world.Vec3d hitVec,
            net.momirealms.craftengine.core.util.Direction direction,
            net.momirealms.craftengine.core.world.Vec3i blockPos) {
        return new BlockHitResult(
                new net.minecraft.world.phys.Vec3(hitVec.x(), hitVec.y(), hitVec.z()),
                fromDirection(direction),
                new net.minecraft.core.BlockPos(blockPos.x(), blockPos.y(), blockPos.z()),
                false);
    }

    public static BlockPos getRelativeBlockPos(net.momirealms.craftengine.core.util.Direction direction,
            BlockPos pos, Level level) {
        return switch (direction) {
            case NORTH -> pos.offset(0, 0, -1);
            case EAST -> pos.offset(1, 0, 0);
            case SOUTH -> pos.offset(0, 0, 1);
            case WEST -> pos.offset(-1, 0, 0);
            case UP -> pos.offset(0, 1, 0);
            case DOWN -> pos.offset(0, -1, 0);
        };
    }

    public static BlockPos getRelativeBlockPos(Direction direction,
            BlockPos pos, Level level) {
        return switch (direction) {
            case NORTH -> pos.offset(0, 0, -1);
            case EAST -> pos.offset(1, 0, 0);
            case SOUTH -> pos.offset(0, 0, 1);
            case WEST -> pos.offset(-1, 0, 0);
            case UP -> pos.offset(0, 1, 0);
            case DOWN -> pos.offset(0, -1, 0);
        };
    }

    public static BlockPos fromPos(net.momirealms.craftengine.core.world.BlockPos pos) {
        return BlockPos.of(pos.asLong());
    }

    // ---- Config value helpers (replace removed dev.arubik.craftengine.util.Utils/MiscUtils.getAs*) ----
    public static java.util.List<String> getAsStringList(Object value) {
        if (value == null) return java.util.List.of();
        if (value instanceof java.util.List<?> list) {
            java.util.List<String> out = new java.util.ArrayList<>(list.size());
            for (Object o : list) out.add(String.valueOf(o));
            return out;
        }
        return java.util.List.of(String.valueOf(value));
    }

    public static boolean getAsBoolean(Object value, String name) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s);
        if (value instanceof Number n) return n.intValue() != 0;
        throw new IllegalArgumentException("Expected boolean for '" + name + "' but got " + value);
    }

    public static int getAsInt(Object value, String name) {
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) return Integer.parseInt(s.trim());
        throw new IllegalArgumentException("Expected int for '" + name + "' but got " + value);
    }

    public static double getAsDouble(Object value, String name) {
        if (value instanceof Number n) return n.doubleValue();
        if (value instanceof String s) return Double.parseDouble(s.trim());
        throw new IllegalArgumentException("Expected double for '" + name + "' but got " + value);
    }

    public static float getAsFloat(Object value, String name) {
        if (value instanceof Number n) return n.floatValue();
        if (value instanceof String s) return Float.parseFloat(s.trim());
        throw new IllegalArgumentException("Expected float for '" + name + "' but got " + value);
    }

    public static <T> T requireNonNullOrThrow(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }

    public static String requireNonEmptyStringOrThrow(Object value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        String s = String.valueOf(value);
        if (s.isEmpty()) throw new IllegalArgumentException(message);
        return s;
    }

    public static org.joml.Vector3f getAsVector3f(Object value, String name) {
        if (value == null) throw new IllegalArgumentException("Expected vector3f for '" + name + "' but got null");
        if (value instanceof Number n) { // uniform scalar -> (v,v,v)
            float f = n.floatValue();
            return new org.joml.Vector3f(f, f, f);
        }
        if (value instanceof java.util.List<?> list) {
            if (list.size() == 1) {
                float f = getAsFloat(list.get(0), name);
                return new org.joml.Vector3f(f, f, f);
            }
            if (list.size() == 3) {
                return new org.joml.Vector3f(
                        getAsFloat(list.get(0), name),
                        getAsFloat(list.get(1), name),
                        getAsFloat(list.get(2), name));
            }
            throw new IllegalArgumentException("Expected 1 or 3 components for vector3f '" + name + "' but got " + list.size());
        }
        if (value instanceof String s) {
            String[] parts = s.split("[,\\s]+");
            if (parts.length == 1) {
                float f = Float.parseFloat(parts[0].trim());
                return new org.joml.Vector3f(f, f, f);
            }
            if (parts.length == 3) {
                return new org.joml.Vector3f(
                        Float.parseFloat(parts[0].trim()),
                        Float.parseFloat(parts[1].trim()),
                        Float.parseFloat(parts[2].trim()));
            }
            throw new IllegalArgumentException("Expected \"x,y,z\" or \"v\" for vector3f '" + name + "' but got '" + s + "'");
        }
        throw new IllegalArgumentException("Expected vector3f for '" + name + "' but got " + value);
    }

    public static <E extends Enum<E>> E getAsEnum(Object value, Class<E> type, E defaultValue) {
        if (value == null) return defaultValue;
        if (type.isInstance(value)) return type.cast(value);
        String s = String.valueOf(value).trim();
        if (s.isEmpty()) return defaultValue;
        try {
            return Enum.valueOf(type, s.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            for (E c : type.getEnumConstants()) {
                if (c.name().equalsIgnoreCase(s)) return c;
            }
            throw new IllegalArgumentException("Invalid value '" + s + "' for enum " + type.getSimpleName()
                    + "; expected one of " + java.util.Arrays.toString(type.getEnumConstants()));
        }
    }
}
