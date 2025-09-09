package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

/**
 * Behavior que define qué caras/lados de este bloque pueden conectarse con otros bloques.
 * Soporta redirección de caras conectables basada en la orientación del bloque.
 * 
 * Configuración esperada (ejemplo en el archivo del bloque):
 *  behaviors:
 *    - id: craftengine:connectable
 *      arguments:
 *        faces: ["north", "south", "east", "west", "up", "down"]
 *        # o simplemente:
 *        faces: "all"  # para todas las direcciones
 *        # o:
 *        faces: "horizontal"  # solo north, south, east, west
 *        # o:
 *        faces: "vertical"  # solo up, down
 *        
 *        # Configuración opcional para redirección de direcciones:
 *        horizontal-direction-property: "facing"  # o "horizontal_facing"
 *        vertical-direction-property: "vertical_facing"  # opcional
 *
 * Cuando se especifica una propiedad de dirección horizontal, el behavior
 * redireccionará las caras conectables basándose en la orientación actual del bloque.
 * Por defecto, la dirección horizontal base es NORTH y la vertical es UP.
 *
 * ConnectedBlockBehavior usará esta información para determinar qué propiedades
 * de conexión crear y actualizar.
 */
public class ConnectableBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    private final List<Direction> connectableFaces;
    public final net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty;
    public final net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> verticalDirectionProperty;

    public ConnectableBlockBehavior(CustomBlock block, List<Direction> connectableFaces,
                                    net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
                                    net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> verticalDirectionProperty) {
        super(block);
        this.connectableFaces = connectableFaces;
        this.horizontalDirectionProperty = horizontalDirectionProperty;
        this.verticalDirectionProperty = verticalDirectionProperty;
    }

    /**
     * Verifica si una dirección específica puede conectarse.
     * @param direction La dirección a verificar
     * @return true si esta cara puede conectarse
     */
    public boolean canConnectTo(Direction direction) {
        return connectableFaces.contains(direction);
    }

    /**
     * Verifica si una dirección específica puede conectarse, teniendo en cuenta
     * la orientación actual del bloque para redireccionar las caras.
     * @param direction La dirección a verificar
     * @param blockState El estado del bloque para obtener su orientación actual
     * @return true si esta cara puede conectarse
     */
    public boolean canConnectTo(Direction direction, BlockState blockState) {
        Direction redirectedDirection = redirectDirection(direction, blockState);
        return connectableFaces.contains(redirectedDirection);
    }

    /**
     * Obtiene todas las caras conectables de este bloque.
     */
    public List<Direction> getConnectableFaces() {
        return new ArrayList<>(connectableFaces);
    }

    /**
     * Redirecciona una dirección basándose en la orientación actual del bloque.
     * Toma en cuenta las propiedades de dirección horizontal y vertical configuradas.
     * 
     * @param originalDirection La dirección original a redireccionar
     * @param blockState El estado del bloque para obtener sus propiedades
     * @return La dirección redirecionada basada en la orientación del bloque
     */
    private Direction redirectDirection(Direction originalDirection, BlockState blockState) {
        if (blockState == null) return originalDirection;
        
        // Obtener el estado customizado del bloque
        Optional<ImmutableBlockState> customStateOpt = BlockStateUtils.getOptionalCustomBlockState(blockState);
        if (customStateOpt.isEmpty()) return originalDirection;
        
        ImmutableBlockState customState = customStateOpt.get();
        
        // Verificar que sea nuestro bloque
        if (customState.owner().value() != this.customBlock) return originalDirection;
        
        // Manejar redirección horizontal
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
        
        // Manejar redirección para bloques full directional
        if (verticalDirectionProperty != null) {
            try {
                Direction directionProperty = customState.get(verticalDirectionProperty);
                return redirectFullDirectional(originalDirection, directionProperty);
            } catch (Exception ignored) {
                // Si hay algún error en la conversión, usar la dirección original
            }
        }
        
        return originalDirection;
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
            if (directions[i] == from) fromIndex = i;
            if (directions[i] == to) toIndex = i;
        }
        
        if (fromIndex == -1 || toIndex == -1) return 0;
        
        return (toIndex - fromIndex + 4) % 4;
    }

    /**
     * Rota una dirección un número específico de pasos en sentido horario.
     */
    private Direction rotateDirection(Direction direction, int steps) {
        if (steps == 0) return direction;
        
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
                return originalDirection.opposite();
            case NORTH:
                switch (originalDirection) {
                    case UP -> { return Direction.NORTH; }
                    case DOWN -> { return Direction.SOUTH; }
                    case NORTH -> { return Direction.DOWN; }
                    case SOUTH -> { return Direction.UP; }
                    case EAST -> { return Direction.EAST; }
                    case WEST -> { return Direction.WEST; }
                }
                break;
            case SOUTH:
                switch (originalDirection) {
                    case UP -> { return Direction.SOUTH; }
                    case DOWN -> { return Direction.NORTH; }
                    case NORTH -> { return Direction.UP; }
                    case SOUTH -> { return Direction.DOWN; }
                    case EAST -> { return Direction.WEST; }
                    case WEST -> { return Direction.EAST; }
                }
                break;
            case EAST:
                switch (originalDirection) {
                    case UP -> { return Direction.EAST; }
                    case DOWN -> { return Direction.WEST; }
                    case NORTH -> { return Direction.NORTH; }
                    case SOUTH -> { return Direction.SOUTH; }
                    case EAST -> { return Direction.DOWN; }
                    case WEST -> { return Direction.UP; }
                }
                break;
            case WEST:
                switch (originalDirection) {
                    case UP -> { return Direction.WEST; }
                    case DOWN -> { return Direction.EAST; }
                    case NORTH -> { return Direction.NORTH; }
                    case SOUTH -> { return Direction.SOUTH; }
                    case EAST -> { return Direction.UP; }
                    case WEST -> { return Direction.DOWN; }
                }
                break;

            default:
                return originalDirection;
        }
        return originalDirection;
    }

    /**
     * Devuelve una instancia existente de ConnectableBlockBehavior para un bloque, si existe.
     */
    public static Optional<ConnectableBlockBehavior> from(CustomBlock block) {
        if (block == null) return Optional.empty();
        // Iterar sobre behaviors directamente desde el customBlock
        // (Asumiendo que existe alguna forma de acceder a behaviors)
        return Optional.empty(); // Temporal hasta encontrar el método correcto
    }

    public static class Factory implements BlockBehaviorFactory {
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
            net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block.getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            
            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<Direction>) block.getProperty(verticalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            return new ConnectableBlockBehavior(block, faces, hProp, vProp);
        }
    }
}