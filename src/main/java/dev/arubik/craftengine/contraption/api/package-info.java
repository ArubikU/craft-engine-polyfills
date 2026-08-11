/**
 * Public API for creating custom contraptions, motors, and physics behaviors.
 *
 * <h2>Core Interfaces</h2>
 * <ul>
 *   <li>{@link dev.arubik.craftengine.contraption.api.ContraptionType} — custom contraption types (submarines, airships)</li>
 *   <li>{@link dev.arubik.craftengine.contraption.api.ContraptionTickable} — blocks that tick inside contraptions</li>
 *   <li>{@link dev.arubik.craftengine.contraption.api.PowerSource} — motors and power generators</li>
 *   <li>{@link dev.arubik.craftengine.contraption.api.PowerConsumer} — behaviors that consume power</li>
 *   <li>{@link dev.arubik.craftengine.contraption.api.MultiblockMember} — multiblock structure detection</li>
 *   <li>{@link dev.arubik.craftengine.contraption.api.ContraptionHitboxProvider} — custom collision shapes</li>
 * </ul>
 *
 * <h2>Registries</h2>
 * <ul>
 *   <li>{@link dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry} — register custom types</li>
 * </ul>
 *
 * <h2>Events</h2>
 * See {@link dev.arubik.craftengine.contraption.event} package for:
 * <ul>
 *   <li>ContraptionAssembleEvent — before assembly</li>
 *   <li>ContraptionAssembledEvent — after assembly</li>
 *   <li>ContraptionDisassembleEvent — before disassembly</li>
 *   <li>ContraptionDisassembledEvent — after disassembly</li>
 *   <li>ContraptionSpawnEvent — entity spawned</li>
 *   <li>ContraptionMoveEvent — contraption moved</li>
 *   <li>ContraptionInteractEvent — player interaction</li>
 *   <li>ContraptionBlockBreakEvent — block broken inside</li>
 *   <li>ContraptionBlockPlaceEvent — block placed inside</li>
 * </ul>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * // 1. Define custom type
 * public class SubmarineType implements ContraptionType {
 *     public ContraptionEntity createEntity(Level level, ContraptionState state) {
 *         return new SubmarineEntity(level, state);
 *     }
 * }
 *
 * // 2. Register in plugin onEnable
 * ContraptionTypeRegistry.register(Key.of("myplugin", "submarine"), new SubmarineType());
 *
 * // 3. Reference in bearing block config
 * behavior:
 *   type: polyfills:bearing_block
 *   contraption_type: myplugin:submarine
 * }</pre>
 *
 * @see dev.arubik.craftengine.contraption.event
 */
package dev.arubik.craftengine.contraption.api;
