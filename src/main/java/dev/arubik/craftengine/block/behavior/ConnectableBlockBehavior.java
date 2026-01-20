package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

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

    private final List<Direction> connectableFaces;
    public final net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty;
    public final net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty;
    protected final dev.arubik.craftengine.multiblock.IOConfiguration defaultIOConfig;

    public ConnectableBlockBehavior(CustomBlock block, List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        this(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty,
                new dev.arubik.craftengine.multiblock.IOConfiguration.Open());
    }

    public ConnectableBlockBehavior(CustomBlock block, List<Direction> connectableFaces,
            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
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
        if (customState.owner().value() != this.customBlock)
            return originalDirection;

        // Manejar redirección horizontal (local -> mundo)
        if (horizontalDirectionProperty != null) {
            try {
                // Obtener la propiedad de dirección horizontal
                HorizontalDirection directionProperty = customState.get(horizontalDirectionProperty);
                // La dirección por defecto es NORTH, calcular el offset
                int rotationSteps = getRotationSteps(HorizontalDirection.NORTH, directionProperty);
                return rotateDirection(originalDirection, rotationSteps);
            } catch (Exception ignored) {
                // Si hay algún error en la conversión, usar la dirección original
            }
        }

        // Manejar redirección para bloques full directional (local -> mundo)
        if (verticalDirectionProperty != null) {
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .get(verticalDirectionProperty);
                // Convert CE Direction to Minecraft Direction for logic
                if (directionProperty != null) {
                    Direction mineDir = Direction.valueOf(directionProperty.name());
                    return redirectFullDirectional(originalDirection, mineDir);
                }
            } catch (Exception ignored) {
                // Si hay algún error en la conversión, usar la dirección original
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
    protected net.minecraft.core.Direction toLocalDirection(net.minecraft.core.Direction worldDirection,
            BlockState blockState) {
        if (blockState == null)
            return worldDirection;

        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isEmpty())
            return worldDirection;
        ImmutableBlockState customState = customStateOpt.get();
        if (customState.owner().value() != this.customBlock)
            return worldDirection;

        // Horizontal: aplicar rotación inversa
        if (horizontalDirectionProperty != null) {
            try {
                HorizontalDirection directionProperty = customState.get(horizontalDirectionProperty);
                int rotationSteps = getRotationSteps(HorizontalDirection.NORTH, directionProperty);
                int inverse = (4 - (rotationSteps % 4)) % 4;
                return rotateDirection(worldDirection, inverse);
            } catch (Exception ignored) {
            }
        }

        // Full directional: aplicar mapeo inverso
        if (verticalDirectionProperty != null) {
            try {
                net.momirealms.craftengine.core.util.Direction directionProperty = customState
                        .get(verticalDirectionProperty);
                if (directionProperty != null) {
                    Direction mineDir = Direction.valueOf(directionProperty.name());
                    return inverseRedirectFullDirectional(worldDirection, mineDir);
                }
            } catch (Exception ignored) {
            }
        }
        return worldDirection;
    }

    /**
     * Calcula los pasos de rotación necesarios entre dos direcciones horizontales.
     */
    private int getRotationSteps(HorizontalDirection from, HorizontalDirection to) {
        HorizontalDirection[] directions = {
                HorizontalDirection.NORTH, HorizontalDirection.EAST,
                HorizontalDirection.SOUTH, HorizontalDirection.WEST
        };

        int fromIndex = -1, toIndex = -1;
        for (int i = 0; i < directions.length; i++) {
            if (directions[i] == from)
                fromIndex = i;
            if (directions[i] == to)
                toIndex = i;
        }

        if (fromIndex == -1 || toIndex == -1)
            return 0;

        return (toIndex - fromIndex + 4) % 4;
    }

    /**
     * Rota una dirección un número específico de pasos en sentido horario.
     */
    private net.minecraft.core.Direction rotateDirection(net.minecraft.core.Direction direction, int steps) {
        if (steps == 0)
            return direction;

        // Solo rotar direcciones horizontales
        return switch (direction) {
            case NORTH -> switch (steps % 4) {
                case 1 -> Direction.EAST;
                case 2 -> Direction.SOUTH;
                case 3 -> Direction.WEST;
                default -> Direction.NORTH;
            };
            case EAST -> switch (steps % 4) {
                case 1 -> Direction.SOUTH;
                case 2 -> Direction.WEST;
                case 3 -> Direction.NORTH;
                default -> Direction.EAST;
            };
            case SOUTH -> switch (steps % 4) {
                case 1 -> Direction.WEST;
                case 2 -> Direction.NORTH;
                case 3 -> Direction.EAST;
                default -> Direction.SOUTH;
            };
            case WEST -> switch (steps % 4) {
                case 1 -> Direction.NORTH;
                case 2 -> Direction.EAST;
                case 3 -> Direction.SOUTH;
                default -> Direction.WEST;
            };
            // Las direcciones verticales no se rotan
            case UP, DOWN -> direction;
        };
    }

    private Direction redirectFullDirectional(Direction originalDirection, Direction blockFacing) {
        // Si el bloque está orientado hacia arriba o abajo, no rotar
        switch (blockFacing) {
            case UP:
                return originalDirection;
            case DOWN:
                return switch (originalDirection) {
                    case UP -> Direction.DOWN;
                    case DOWN -> Direction.UP;
                    case NORTH -> Direction.SOUTH;
                    case SOUTH -> Direction.NORTH;
                    case EAST -> Direction.WEST;
                    case WEST -> Direction.EAST;
                };
            case NORTH:
                switch (originalDirection) {
                    case UP -> {
                        return Direction.NORTH;
                    }
                    case DOWN -> {
                        return Direction.SOUTH;
                    }
                    case NORTH -> {
                        return Direction.DOWN;
                    }
                    case SOUTH -> {
                        return Direction.UP;
                    }
                    case EAST -> {
                        return Direction.EAST;
                    }
                    case WEST -> {
                        return Direction.WEST;
                    }
                }
                break;
            case SOUTH:
                switch (originalDirection) {
                    case UP -> {
                        return Direction.SOUTH;
                    }
                    case DOWN -> {
                        return Direction.NORTH;
                    }
                    case NORTH -> {
                        return Direction.UP;
                    }
                    case SOUTH -> {
                        return Direction.DOWN;
                    }
                    case EAST -> {
                        return Direction.WEST;
                    }
                    case WEST -> {
                        return Direction.EAST;
                    }
                }
                break;
            case EAST:
                switch (originalDirection) {
                    case UP -> {
                        return Direction.EAST;
                    }
                    case DOWN -> {
                        return Direction.WEST;
                    }
                    case NORTH -> {
                        return Direction.NORTH;
                    }
                    case SOUTH -> {
                        return Direction.SOUTH;
                    }
                    case EAST -> {
                        return Direction.DOWN;
                    }
                    case WEST -> {
                        return Direction.UP;
                    }
                }
                break;
            case WEST:
                switch (originalDirection) {
                    case UP -> {
                        return Direction.WEST;
                    }
                    case DOWN -> {
                        return Direction.EAST;
                    }
                    case NORTH -> {
                        return Direction.NORTH;
                    }
                    case SOUTH -> {
                        return Direction.SOUTH;
                    }
                    case EAST -> {
                        return Direction.UP;
                    }
                    case WEST -> {
                        return Direction.DOWN;
                    }
                }
                break;

            default:
                return originalDirection;
        }
        return originalDirection;
    }

    private Direction inverseRedirectFullDirectional(Direction worldDirection, Direction blockFacing) {
        switch (blockFacing) {
            case UP:
                return worldDirection; // identidad
            case DOWN:
                return switch (worldDirection) {
                    case NORTH -> Direction.UP;
                    case SOUTH -> Direction.DOWN;
                    case DOWN -> Direction.NORTH;
                    case UP -> Direction.SOUTH;
                    case EAST -> Direction.WEST;
                    case WEST -> Direction.EAST;
                };
            case NORTH:
                // Inversa del caso NORTH en redirectFullDirectional
                switch (worldDirection) {
                    case NORTH -> {
                        return Direction.UP;
                    }
                    case SOUTH -> {
                        return Direction.DOWN;
                    }
                    case DOWN -> {
                        return Direction.NORTH;
                    }
                    case UP -> {
                        return Direction.SOUTH;
                    }
                    case EAST -> {
                        return Direction.EAST;
                    }
                    case WEST -> {
                        return Direction.WEST;
                    }
                }
                break;
            case SOUTH:
                switch (worldDirection) {
                    case SOUTH -> {
                        return Direction.UP;
                    }
                    case NORTH -> {
                        return Direction.DOWN;
                    }
                    case UP -> {
                        return Direction.NORTH;
                    }
                    case DOWN -> {
                        return Direction.SOUTH;
                    }
                    case WEST -> {
                        return Direction.EAST;
                    }
                    case EAST -> {
                        return Direction.WEST;
                    }
                }
                break;
            case EAST:
                switch (worldDirection) {
                    case EAST -> {
                        return Direction.UP;
                    }
                    case WEST -> {
                        return Direction.DOWN;
                    }
                    case NORTH -> {
                        return Direction.NORTH;
                    }
                    case SOUTH -> {
                        return Direction.SOUTH;
                    }
                    case DOWN -> {
                        return Direction.EAST;
                    }
                    case UP -> {
                        return Direction.WEST;
                    }
                }
                break;
            case WEST:
                switch (worldDirection) {
                    case WEST -> {
                        return Direction.UP;
                    }
                    case EAST -> {
                        return Direction.DOWN;
                    }
                    case NORTH -> {
                        return Direction.NORTH;
                    }
                    case SOUTH -> {
                        return Direction.SOUTH;
                    }
                    case UP -> {
                        return Direction.EAST;
                    }
                    case DOWN -> {
                        return Direction.WEST;
                    }
                }
                break;
            default:
                return worldDirection;
        }
        return worldDirection;
    }

    /**
     * Devuelve una instancia existente de ConnectableBlockBehavior para un bloque,
     * si existe.
     */
    public static Optional<ConnectableBlockBehavior> from(CustomBlock block) {
        if (block == null)
            return Optional.empty();
        // Iterar sobre behaviors directamente desde el customBlock
        // (Asumiendo que existe alguna forma de acceder a behaviors)
        return Optional.empty(); // Temporal hasta encontrar el método correcto
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
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

            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> hProp = null;
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block
                            .getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
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
            // Add more types if necessary or custom parsing logic

            return new ConnectableBlockBehavior(block, faces, hProp, vProp, ioConfig);
        }
    }
}