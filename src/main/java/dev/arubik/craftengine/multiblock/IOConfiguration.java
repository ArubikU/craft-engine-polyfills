package dev.arubik.craftengine.multiblock;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Direction;

/**
 * Defines I/O configuration for a MultiBlock part, specifying which types
 * of resources can be input/output from which directions.
 */
public interface IOConfiguration {

    /**
     * Check if this part accepts input of the given type from the given direction
     */
    boolean acceptsInput(IOType type, Direction dir);

    /**
     * Check if this part provides output of the given type to the given direction
     */
    boolean providesOutput(IOType type, Direction dir);

    /**
     * Check if there is any connection capability (input or output) on the given
     * direction
     * for any IOType.
     * 
     * @param dir The direction to check
     * @return true if any IOType accepts input or provides output on this direction
     */
    default boolean canConnect(Direction dir) {
        for (IOType type : IOType.values()) {
            if (acceptsInput(type, dir) || providesOutput(type, dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all input types accepted from any direction
     */
    default Set<IOType> getInputTypes() {
        Set<IOType> types = EnumSet.noneOf(IOType.class);
        for (IOType type : IOType.values()) {
            for (Direction dir : Direction.values()) {
                if (acceptsInput(type, dir)) {
                    types.add(type);
                    break;
                }
            }
        }
        return types;
    }

    /**
     * Get all output types provided to any direction
     */
    default Set<IOType> getOutputTypes() {
        Set<IOType> types = EnumSet.noneOf(IOType.class);
        for (IOType type : IOType.values()) {
            for (Direction dir : Direction.values()) {
                if (providesOutput(type, dir)) {
                    types.add(type);
                    break;
                }
            }
        }
        return types;
    }

    /**
     * Get the target slot (tank index) for this IO type and direction.
     * Returns -1 if no specific slot is configured (default behavior).
     * 
     * @param type The IO type
     * @param dir  The direction
     * @return Slot index or -1
     */
    default int getTargetSlot(IOType type, Direction dir) {
        return -1;
    }

    default int getTargetSlot(IOType type) {
        for (Direction dir : Direction.values()) {
            int slot = getTargetSlot(type, dir);
            if (slot != -1)
                return slot;
        }
        return -1;
    }

    /**
     * Get the slots (item slots or tank indices) for this configuration
     * based on type and role.
     * 
     * @param type The resource type (ITEM, FLUID, GAS)
     * @param role The role (INPUT, OUTPUT, FUEL)
     * @return Array of slot indices
     */
    default int[] getSlots(IOType type, IORole role) {
        return new int[0];
    }

    /**
     * Role of the IO slot configuration
     */
    enum IORole {
        INPUT,
        OUTPUT,
        FUEL
    }

    /**
     * Resource type that can be transferred
     */
    enum IOType {
        ITEM, // Items (default stack size)
        FLUID, // Fluids in mB (default 1000 mB/tick)
        GAS, // Gas in mB (default 1000 mB/tick)
        ENERGY, // Energy (default 1000 FE/tick)
        XP, // Experience points (default 100/tick)
        REDSTONE;
    }

    /**
     * Simple implementation that allows configuration via builder pattern
     */
    class Simple implements IOConfiguration {
        private final Map<Direction, Set<IOType>> inputs = new HashMap<>();
        private final Map<Direction, Set<IOType>> outputs = new HashMap<>();
        private final Map<IOType, Map<Direction, Integer>> inputSlots = new EnumMap<>(IOType.class);
        private final Map<IOType, Map<Direction, Integer>> outputSlots = new EnumMap<>(IOType.class);

        // Map<IOType, Map<IORole, int[]>>
        private final Map<IOType, Map<IORole, int[]>> configuredSlots = new EnumMap<>(IOType.class);

        public Simple() {
            // Initialize empty sets for all directions
            for (Direction dir : Direction.values()) {
                inputs.put(dir, EnumSet.noneOf(IOType.class));
                outputs.put(dir, EnumSet.noneOf(IOType.class));
            }
        }

        /**
         * Add input type from specific direction
         */
        public Simple addInput(IOType type, Direction dir) {
            inputs.get(dir).add(type);
            return this;
        }

        /**
         * Add input type from all directions
         */
        public Simple addInput(IOType type, Direction... directions) {
            if (directions.length == 0) {
                // All directions
                for (Direction dir : Direction.values()) {
                    inputs.get(dir).add(type);
                }
            } else {
                for (Direction dir : directions) {
                    inputs.get(dir).add(type);
                }
            }
            return this;
        }

        /**
         * Add output type to specific direction
         */
        public Simple addOutput(IOType type, Direction dir) {
            outputs.get(dir).add(type);
            return this;
        }

        /**
         * Add output type to all directions
         */
        public Simple addOutput(IOType type, Direction... directions) {
            if (directions.length == 0) {
                // All directions
                for (Direction dir : Direction.values()) {
                    outputs.get(dir).add(type);
                }
            } else {
                for (Direction dir : directions) {
                    outputs.get(dir).add(type);
                }
            }
            return this;
        }

        /**
         * Set the target slot for a specific input
         */
        public Simple withInputSlot(IOType type, int slot, Direction dir) {
            inputSlots.computeIfAbsent(type, k -> new HashMap<>()).put(dir, slot);
            return this;
        }

        /**
         * Set the target slot for a specific output
         */
        public Simple withOutputSlot(IOType type, int slot, Direction dir) {
            outputSlots.computeIfAbsent(type, k -> new HashMap<>()).put(dir, slot);
            return this;
        }

        public Simple setSlots(IOType type, IORole role, int... slots) {
            configuredSlots.computeIfAbsent(type, k -> new EnumMap<>(IORole.class)).put(role, slots);
            return this;
        }

        @Override
        public int getTargetSlot(IOType type, Direction dir) {
            if (acceptsInput(type, dir)) {
                return inputSlots.getOrDefault(type, Map.of()).getOrDefault(dir, -1);
            }
            if (providesOutput(type, dir)) {
                return outputSlots.getOrDefault(type, Map.of()).getOrDefault(dir, -1);
            }
            return -1;
        }

        @Override
        public boolean acceptsInput(IOType type, Direction dir) {
            return inputs.getOrDefault(dir, EnumSet.noneOf(IOType.class)).contains(type);
        }

        @Override
        public boolean providesOutput(IOType type, Direction dir) {
            return outputs.getOrDefault(dir, EnumSet.noneOf(IOType.class)).contains(type);
        }

        @Override
        public int[] getSlots(IOType type, IORole role) {
            return configuredSlots.getOrDefault(type, Map.of()).getOrDefault(role, new int[0]);
        }
    }

    /**
     * Open configuration - accepts all inputs and provides all outputs from all
     * directions
     */
    class Open implements IOConfiguration {
        @Override
        public boolean acceptsInput(IOType type, Direction dir) {
            return true;
        }

        @Override
        public boolean providesOutput(IOType type, Direction dir) {
            return true;
        }
    }

    /**
     * Closed configuration - no inputs or outputs
     */
    class Closed implements IOConfiguration {
        @Override
        public boolean acceptsInput(IOType type, Direction dir) {
            return false;
        }

        @Override
        public boolean providesOutput(IOType type, Direction dir) {
            return false;
        }
    }

    /**
     * Configuration with custom transfer rates per direction and type
     * Useful for pipes, pumps, valves with specific flow rates
     */
    class WithTransferRate implements IOConfiguration {
        private final Map<Direction, Map<IOType, Integer>> inputRates = new HashMap<>();
        private final Map<Direction, Map<IOType, Integer>> outputRates = new HashMap<>();

        public WithTransferRate() {
            for (Direction dir : Direction.values()) {
                inputRates.put(dir, new EnumMap<>(IOType.class));
                outputRates.put(dir, new EnumMap<>(IOType.class));
            }
        }

        /**
         * Set input rate for a specific type and direction
         * 
         * @param type        IO type
         * @param dir         Direction
         * @param ratePerTick Transfer rate (items/tick, mB/tick, etc.)
         */
        public WithTransferRate setInputRate(IOType type, Direction dir, int ratePerTick) {
            inputRates.get(dir).put(type, ratePerTick);
            return this;
        }

        /**
         * Set output rate for a specific type and direction
         */
        public WithTransferRate setOutputRate(IOType type, Direction dir, int ratePerTick) {
            outputRates.get(dir).put(type, ratePerTick);
            return this;
        }

        /**
         * Set same rate for all directions (common for pipes/tanks)
         */
        public WithTransferRate setAllInputRate(IOType type, int ratePerTick) {
            for (Direction dir : Direction.values()) {
                inputRates.get(dir).put(type, ratePerTick);
            }
            return this;
        }

        public WithTransferRate setAllOutputRate(IOType type, int ratePerTick) {
            for (Direction dir : Direction.values()) {
                outputRates.get(dir).put(type, ratePerTick);
            }
            return this;
        }

        @Override
        public boolean acceptsInput(IOType type, Direction dir) {
            return inputRates.get(dir).containsKey(type);
        }

        @Override
        public boolean providesOutput(IOType type, Direction dir) {
            return outputRates.get(dir).containsKey(type);
        }

    }

    /**
     * Directional IO configuration using relative directions
     * Transforms relative directions (FRONT, BACK, etc.) to world directions based
     * on facing
     */
    class RelativeIO implements IOConfiguration {
        private final Map<IOType, Set<RelativeDirection>> inputs = new EnumMap<>(IOType.class);
        private final Map<IOType, Set<RelativeDirection>> outputs = new EnumMap<>(IOType.class);
        private Direction facing; // Changed to full Direction to support both types

        public RelativeIO() {
            for (IOType type : IOType.values()) {
                inputs.put(type, EnumSet.noneOf(RelativeDirection.class));
                outputs.put(type, EnumSet.noneOf(RelativeDirection.class));
            }
        }

        /**
         * Set the facing direction (supports both horizontal and full direction)
         */
        public RelativeIO withFacing(Direction facing) {
            this.facing = facing;
            return this;
        }

        /**
         * Add input for a specific type from a relative direction
         */
        public RelativeIO addInput(IOType type, RelativeDirection direction) {
            inputs.get(type).add(direction);
            return this;
        }

        /**
         * Add output for a specific type to a relative direction
         */
        public RelativeIO addOutput(IOType type, RelativeDirection direction) {
            outputs.get(type).add(direction);
            return this;
        }

        @Override
        public boolean acceptsInput(IOType type, Direction direction) {
            if (facing == null)
                return false;

            Set<RelativeDirection> relDirs = inputs.get(type);
            if (relDirs == null || relDirs.isEmpty())
                return false;

            // Check each relative direction
            for (RelativeDirection relDir : relDirs) {
                Direction worldDir = DirectionalIOHelper.getWorldDirection(relDir, facing);
                if (worldDir == direction) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean providesOutput(IOType type, Direction direction) {
            if (facing == null)
                return false;

            Set<RelativeDirection> relDirs = outputs.get(type);
            if (relDirs == null || relDirs.isEmpty())
                return false;

            // Check each relative direction
            for (RelativeDirection relDir : relDirs) {
                Direction worldDir = DirectionalIOHelper.getWorldDirection(relDir, facing);
                if (worldDir == direction) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Relative direction configuration with custom transfer rates
     * Combines RelativeDirection support with per-direction transfer rate
     * customization
     * Perfect for directional machines with specific flow rates
     */
    class WithTransferRateRelative implements IOConfiguration {
        private final Map<IOType, Map<RelativeDirection, Integer>> inputRates = new EnumMap<>(IOType.class);
        private final Map<IOType, Map<RelativeDirection, Integer>> outputRates = new EnumMap<>(IOType.class);
        private Direction facing;

        public WithTransferRateRelative() {
            for (IOType type : IOType.values()) {
                inputRates.put(type, new EnumMap<>(RelativeDirection.class));
                outputRates.put(type, new EnumMap<>(RelativeDirection.class));
            }
        }

        /**
         * Set the facing direction (supports both horizontal and full direction)
         */
        public WithTransferRateRelative withFacing(Direction facing) {
            this.facing = facing;
            return this;
        }

        /**
         * Set the facing direction from HorizontalDirection (convenience method)
         */
        public WithTransferRateRelative withFacing(net.momirealms.craftengine.core.util.HorizontalDirection facing) {
            this.facing = DirectionalIOHelper.fromHorizontalDirection(facing);
            return this;
        }

        /**
         * Set input rate for a relative direction
         * 
         * @param type        IO type
         * @param relDir      Relative direction (FRONT, BACK, LEFT, RIGHT, UP, DOWN)
         * @param ratePerTick Transfer rate
         */
        public WithTransferRateRelative setInputRate(IOType type, RelativeDirection relDir, int ratePerTick) {
            inputRates.get(type).put(relDir, ratePerTick);
            return this;
        }

        /**
         * Set output rate for a relative direction
         */
        public WithTransferRateRelative setOutputRate(IOType type, RelativeDirection relDir, int ratePerTick) {
            outputRates.get(type).put(relDir, ratePerTick);
            return this;
        }

        /**
         * Set same input rate for all horizontal directions (FRONT, BACK, LEFT, RIGHT)
         */
        public WithTransferRateRelative setAllHorizontalInputRate(IOType type, int ratePerTick) {
            inputRates.get(type).put(RelativeDirection.FRONT, ratePerTick);
            inputRates.get(type).put(RelativeDirection.BACK, ratePerTick);
            inputRates.get(type).put(RelativeDirection.LEFT, ratePerTick);
            inputRates.get(type).put(RelativeDirection.RIGHT, ratePerTick);
            return this;
        }

        /**
         * Set same output rate for all horizontal directions
         */
        public WithTransferRateRelative setAllHorizontalOutputRate(IOType type, int ratePerTick) {
            outputRates.get(type).put(RelativeDirection.FRONT, ratePerTick);
            outputRates.get(type).put(RelativeDirection.BACK, ratePerTick);
            outputRates.get(type).put(RelativeDirection.LEFT, ratePerTick);
            outputRates.get(type).put(RelativeDirection.RIGHT, ratePerTick);
            return this;
        }

        /**
         * Set same rate for ALL directions (including UP/DOWN)
         */
        public WithTransferRateRelative setAllInputRate(IOType type, int ratePerTick) {
            for (RelativeDirection relDir : RelativeDirection.values()) {
                inputRates.get(type).put(relDir, ratePerTick);
            }
            return this;
        }

        public WithTransferRateRelative setAllOutputRate(IOType type, int ratePerTick) {
            for (RelativeDirection relDir : RelativeDirection.values()) {
                outputRates.get(type).put(relDir, ratePerTick);
            }
            return this;
        }

        @Override
        public boolean acceptsInput(IOType type, Direction direction) {
            if (facing == null)
                return false;

            Map<RelativeDirection, Integer> rates = inputRates.get(type);
            if (rates == null || rates.isEmpty())
                return false;

            // Check each relative direction
            for (RelativeDirection relDir : rates.keySet()) {
                Direction worldDir = DirectionalIOHelper.getWorldDirection(relDir, facing);
                if (worldDir == direction) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean providesOutput(IOType type, Direction direction) {
            if (facing == null)
                return false;

            Map<RelativeDirection, Integer> rates = outputRates.get(type);
            if (rates == null || rates.isEmpty())
                return false;

            // Check each relative direction
            for (RelativeDirection relDir : rates.keySet()) {
                Direction worldDir = DirectionalIOHelper.getWorldDirection(relDir, facing);
                if (worldDir == direction) {
                    return true;
                }
            }
            return false;
        }

    }
}
