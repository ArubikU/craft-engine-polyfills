package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.minecraft.core.Direction;
import dev.arubik.craftengine.util.DirectionType;

/**
 * Behavior que define qué caras/lados de este bloque pueden conectarse con
 * otros bloques.
 * Soporta redirección de caras conectables basada en la orientación del bloque.
 * 
 * Configuración esperada (ejemplo en el archivo del bloque):
 * behaviors:
 * - id: craftengine:connectable
 * arguments:
 * faces: ["north", "south", "east", "west", "up", "down"]
 * # o simplemente:
 * faces: "all" # para todas las direcciones
 * # o:
 * faces: "horizontal" # solo north, south, east, west
 * # o:
 * faces: "vertical" # solo up, down
 * 
 * # Configuración opcional para redirección de direcciones:
 * horizontal-direction-property: "facing" # o "horizontal_facing"
 * vertical-direction-property: "vertical_facing" # opcional
 *
 * Cuando se especifica una propiedad de dirección horizontal, el behavior
 * redireccionará las caras conectables basándose en la orientación actual del
 * bloque.
 * Por defecto, la dirección horizontal base es NORTH y la vertical es UP.
 *
 * ConnectedBlockBehavior usará esta información para determinar qué propiedades
 * de conexión crear y actualizar.
 */
public class ConnectableBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    protected List<Direction> connectableFaces;
    public final net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty;
    public final net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty;
    public final dev.arubik.craftengine.multiblock.IOConfiguration defaultIOConfig;

    public ConnectableBlockBehavior(BlockDefinition block, List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        this(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty,
                new dev.arubik.craftengine.multiblock.IOConfiguration.Open());
    }

    public ConnectableBlockBehavior(BlockDefinition block, List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig) {
        super(block);
        this.connectableFaces = connectableFaces;
        this.horizontalDirectionProperty = horizontalDirectionProperty;
        this.verticalDirectionProperty = verticalDirectionProperty;
        this.defaultIOConfig = ioConfig;
    }

    /**
     * Verifica si una dirección específica puede conectarse.
     * Versión "World-aware" para permitir lógicas complejas (ej. MultiBlock IO).
     * 
     * @param level     El nivel
     * @param pos       La posición del bloque
     * @param direction La dirección a verificar
     * @return true si esta cara puede conectarse
     */
    public boolean canConnectTo(Level level, BlockPos pos, net.minecraft.core.Direction direction) {
        // Fast paths that skip the blockstate lookup + toLocalDirection (which dominated the gas-engine
        // tick — for a property-less pipe it threw+caught an IllegalArgumentException on every call):
        //   - connects on all 6 faces  -> any direction is fine.
        //   - no connectable faces     -> never connects.
        if (connectableFaces.size() >= 6)
            return true;
        if (connectableFaces.isEmpty())
            return false;
        BlockState blockState = level.getBlockState(pos);
        Direction localDirection = toLocalDirection(direction, blockState);
        return connectableFaces.contains(localDirection);
    }

    /**
     * Obtiene todas las caras conectables de este bloque.
     */
    public List<Direction> getConnectableFaces() {
        return new ArrayList<>(connectableFaces);
    }

    public DirectionType getDirectionType() {
        if (verticalDirectionProperty != null)
            return DirectionType.FULL;
        return DirectionType.HORIZONTAL;
    }

    /**
     * Redirecciona una dirección basándose en la orientación actual del bloque.
     * Toma en cuenta las propiedades de dirección horizontal y vertical
     * configuradas.
     * 
     * @param originalDirection La dirección original a redireccionar
     * @param blockState        El estado del bloque para obtener sus propiedades
     * @return La dirección redirecionada basada en la orientación del bloque
     */
    // Local -> Mundo: usa esto cuando el propio bloque quiere actuar hacia una
    // dirección
    protected Direction redirectDirection(Direction originalDirection, BlockState blockState) {
        if (blockState == null)
            return originalDirection;

        // Obtener el estado customizado del bloque
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isEmpty())
            return originalDirection;

        ImmutableBlockState customState = customStateOpt.get();

        // Verificar que sea nuestro bloque
        if (customState.owner().value() != this.block())
            return originalDirection;

        // Explicit dispatch based on DirectionType (null-guard before get() — see toLocalDirection note).
        if (getDirectionType() == DirectionType.FULL) {
            if (verticalDirectionProperty == null)
                return originalDirection;
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .getNullable(verticalDirectionProperty);
                if (directionProperty != null) {
                    Direction mineDir = Direction.valueOf(directionProperty.name());
                    return dev.arubik.craftengine.multiblock.DirectionalIOHelper.getVerticalWorldDirection(
                            dev.arubik.craftengine.multiblock.DirectionalIOHelper.fromDirection(originalDirection),
                            mineDir);
                }
            } catch (Exception ignored) {
            }
        } else {
            if (horizontalDirectionProperty == null)
                return originalDirection;
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .getNullable(horizontalDirectionProperty);
                if (directionProperty != null) {
                    return dev.arubik.craftengine.multiblock.DirectionalIOHelper.getHorizontalWorldDirection(
                            dev.arubik.craftengine.multiblock.DirectionalIOHelper.fromDirection(originalDirection),
                            directionProperty);
                }
            } catch (Exception ignored) {
            }
        }

        return originalDirection;
    }

    /**
     * Obtiene la configuración de entrada/salida para este bloque.
     * Puede ser sobrescrito por subclases para lógica dinámica (ej. multiblocks).
     * 
     * @param level El nivel
     * @param pos   La posición del bloque
     * @return La configuración IO
     */
    public dev.arubik.craftengine.multiblock.IOConfiguration getIOConfiguration(net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos) {
        return defaultIOConfig;
    }

    // Mundo -> Local: usa esto para chequear si un vecino puede conectar a este
    // bloque
    public net.minecraft.core.Direction toLocalDirection(net.minecraft.core.Direction worldDirection,
            BlockState blockState) {
        if (blockState == null)
            return worldDirection;

        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isEmpty())
            return worldDirection;
        ImmutableBlockState customState = customStateOpt.get();
        if (customState.owner().value() != this.block())
            return worldDirection;

        // Explicit dispatch based on DirectionType. NOTE: guard the property for null BEFORE calling get()
        // — get(null) throws IllegalArgumentException, and building that exception (stack trace) on every
        // call for property-less blocks (pipes/tanks) was the single biggest server-thread cost.
        if (getDirectionType() == DirectionType.FULL) {
            if (verticalDirectionProperty == null)
                return worldDirection;
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .getNullable(verticalDirectionProperty);
                if (directionProperty != null) {
                    Direction mineDir = Direction.valueOf(directionProperty.name());
                    return dev.arubik.craftengine.multiblock.DirectionalIOHelper.getVerticalLocalDirection(
                            worldDirection,
                            mineDir);
                }
            } catch (Exception ignored) {
            }
        } else {
            if (horizontalDirectionProperty == null)
                return worldDirection;
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .getNullable(horizontalDirectionProperty);
                if (directionProperty != null) {
                    return dev.arubik.craftengine.multiblock.DirectionalIOHelper.getHorizontalLocalDirection(
                            worldDirection,
                            directionProperty);
                }
            } catch (Exception ignored) {
            }
        }
        return worldDirection;
    }

    public Direction toDirection(BlockState blockState) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(blockState).get();
        net.momirealms.craftengine.core.util.Direction horizontalDirection = customState.getNullable(horizontalDirectionProperty);
        net.momirealms.craftengine.core.util.Direction verticalDirection = customState
                .getNullable(verticalDirectionProperty);
        if (verticalDirection != null) {
            return switch (verticalDirection) {
                case NORTH -> Direction.NORTH;
                case SOUTH -> Direction.SOUTH;
                case EAST -> Direction.EAST;
                case WEST -> Direction.WEST;
                case UP -> Direction.UP;
                case DOWN -> Direction.DOWN;
                default -> Direction.NORTH;
            };
        }
        if (horizontalDirection != null) {
            return switch (horizontalDirection) {
                case NORTH -> Direction.NORTH;
                case SOUTH -> Direction.SOUTH;
                case EAST -> Direction.EAST;
                case WEST -> Direction.WEST;
                default -> Direction.NORTH;
            };
        }
        return Direction.NORTH;
    }

    /**
     * Devuelve una instancia existente de ConnectableBlockBehavior para un bloque,
     * si existe.
     */
    public static Optional<ConnectableBlockBehavior> from(BlockDefinition block) {
        if (block == null)
            return Optional.empty();
        // Iterar sobre behaviors directamente desde el customBlock
        // (Asumiendo que existe alguna forma de acceder a behaviors)
        return Optional.empty(); // Temporal hasta encontrar el método correcto
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            List<Direction> faces = new ArrayList<>();

            Object facesArg = arguments.getOrDefault("faces", "all");

            if (facesArg instanceof String faceStr) {
                switch (faceStr.toLowerCase()) {
                    case "all" -> {
                        faces.add(Direction.NORTH);
                        faces.add(Direction.SOUTH);
                        faces.add(Direction.EAST);
                        faces.add(Direction.WEST);
                        faces.add(Direction.UP);
                        faces.add(Direction.DOWN);
                    }
                    case "horizontal" -> {
                        faces.add(Direction.NORTH);
                        faces.add(Direction.SOUTH);
                        faces.add(Direction.EAST);
                        faces.add(Direction.WEST);
                    }
                    case "vertical" -> {
                        faces.add(Direction.UP);
                        faces.add(Direction.DOWN);
                    }
                    default -> {
                        // Intentar parsear como dirección individual
                        try {
                            faces.add(Direction.valueOf(faceStr.toUpperCase()));
                        } catch (IllegalArgumentException ignored) {
                            // Si falla, usar todas por defecto
                            faces.add(Direction.NORTH);
                            faces.add(Direction.SOUTH);
                            faces.add(Direction.EAST);
                            faces.add(Direction.WEST);
                            faces.add(Direction.UP);
                            faces.add(Direction.DOWN);
                        }
                    }
                }
            } else if (facesArg instanceof List<?> faceList) {
                for (Object face : faceList) {
                    try {
                        faces.add(Direction.valueOf(face.toString().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                        // Ignorar direcciones inválidas
                    }
                }
            }

            // Si no se especificaron caras válidas, usar todas por defecto
            if (faces.isEmpty()) {
                faces.add(Direction.NORTH);
                faces.add(Direction.SOUTH);
                faces.add(Direction.EAST);
                faces.add(Direction.WEST);
                faces.add(Direction.UP);
                faces.add(Direction.DOWN);
            }

            // Leer las propiedades de dirección desde los argumentos
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;

            Object horizontalProp = arguments.get("horizontal-direction-property");
            if (horizontalProp instanceof String) {
                horizontalDirectionProperty = (String) horizontalProp;
            }

            Object verticalProp = arguments.get("vertical-direction-property");
            if (verticalProp instanceof String) {
                verticalDirectionProperty = (String) verticalProp;
            }

            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }

            // Parse IO configuration
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig = new dev.arubik.craftengine.multiblock.IOConfiguration.Open();
            String ioType = (String) arguments.get("io"); // e.g., "open", "closed", etc.
            if ("closed".equalsIgnoreCase(ioType)) {
                ioConfig = new dev.arubik.craftengine.multiblock.IOConfiguration.Closed();
            }

            return new ConnectableBlockBehavior(block, faces, hProp, vProp, ioConfig);
        }
    }
}