package dev.arubik.craftengine.machine.render.formula;

import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * {@link PolyClass} exposed as {@code "Contraption"} in the context when a machine lives
 * inside a contraption.
 *
 * <p>Returns {@link PolyValue#NULL} for all properties when the machine is NOT in a
 * contraption (graceful no-op). When IN a contraption, exposes orientation, position,
 * block count, and scale data from the {@link ContraptionLevel} hosting the machine.</p>
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code Contraption.is_contraption} — true when inside a contraption</li>
 *   <li>{@code Contraption.block_count}    — number of blocks in the structure</li>
 *   <li>{@code Contraption.yaw}            — current yaw in degrees</li>
 *   <li>{@code Contraption.pitch}          — current pitch in degrees</li>
 *   <li>{@code Contraption.roll}           — current roll in degrees</li>
 *   <li>{@code Contraption.scale}          — current scale factor</li>
 *   <li>{@code Contraption.speed}          — TODO: from ContraptionState velocity</li>
 *   <li>{@code Contraption.x/y/z}          — real-world position of the structure origin</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code Contraption.is_moving()}    — TODO: from state velocity != 0</li>
 *   <li>{@code Contraption.has_riders()}   — TODO: check seated riders</li>
 * </ul>
 *
 * <h3>Example usage in renderer JSON</h3>
 * <pre>{@code
 * { "type": "particle", "particle": "CRIT", "when": "Contraption.is_contraption && Contraption.speed > 5" }
 * { "type": "text_display", "text": "'Speed: ' + Contraption.speed", "when": "Contraption.is_contraption" }
 * }</pre>
 */
public final class ContraptionClass implements PolyClass {

    private final ContraptionLevel level; // null if not in contraption

    public ContraptionClass(ContraptionLevel level) {
        this.level = level;
    }

    /**
     * Returns a null-safe instance. Properties return NULL when level is null or
     * when the provided serverLevel is not a ContraptionLevel.
     */
    public static ContraptionClass of(Object serverLevel) {
        if (serverLevel instanceof ContraptionLevel cl) return new ContraptionClass(cl);
        return new ContraptionClass(null);
    }

    @Override
    public PolyValue get(String property) {
        if (level == null) return PolyValue.NULL;
        return switch (property) {
            case "is_contraption" -> PolyValue.of(true);
            case "block_count"    -> PolyValue.of(level.blockCount());
            case "yaw"            -> PolyValue.of(Math.toDegrees(level.realYawRadians()));
            case "pitch"          -> PolyValue.of(Math.toDegrees(level.realPitchRadians()));
            case "roll"           -> PolyValue.of(Math.toDegrees(level.realRollRadians()));
            case "scale"          -> PolyValue.of(level.realScaleFactor());
            case "speed" -> {
                // Linear velocity magnitude from physics body
                try {
                    var entity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (entity != null) {
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        if (body != null) yield PolyValue.of(body.body.linearVelocity.length());
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.of(0);
            }
            case "rider_count" -> {
                try {
                    var entity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (entity != null) yield PolyValue.of(entity.state().seatedRiders().size());
                } catch (Throwable ignored) {}
                yield PolyValue.of(0);
            }
            case "is_held" -> {
                try {
                    var entity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (entity != null) yield PolyValue.of(PhysicsWorld.isHeld(entity.state().id()));
                } catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }
            // Returns the entity this contraption is attached to (minecart, happy ghast, etc.)
            // or NULL for block-anchored contraptions.
            case "anchor_entity" -> {
                try {
                    var cEntity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (cEntity != null) {
                        java.util.UUID anchorId = cEntity.state().anchorEntityId();
                        if (anchorId != null && level.realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                            net.minecraft.world.entity.Entity anchor = rl.getEntity(anchorId);
                            if (anchor != null) yield new PolyValue.Obj(new EntityClass(anchor));
                        }
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.NULL;
            }
            case "has_anchor_entity" -> {
                try {
                    var cEntity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (cEntity != null) yield PolyValue.of(cEntity.state().anchorEntityId() != null);
                } catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }
            case "x"              -> {
                try { yield PolyValue.of(level.realWorldPositionOf(new BlockPos(0, 0, 0)).x); }
                catch (Throwable ignored) { yield PolyValue.NULL; }
            }
            case "y"              -> {
                try { yield PolyValue.of(level.realWorldPositionOf(new BlockPos(0, 0, 0)).y); }
                catch (Throwable ignored) { yield PolyValue.NULL; }
            }
            case "z"              -> {
                try { yield PolyValue.of(level.realWorldPositionOf(new BlockPos(0, 0, 0)).z); }
                catch (Throwable ignored) { yield PolyValue.NULL; }
            }
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (level == null) return PolyValue.of(false);
        return switch (method) {
            case "is_moving" -> {
                try {
                    var entity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (entity != null) {
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        yield PolyValue.of(body != null && body.body.linearVelocity.lengthSquared() > 0.001);
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }
            case "has_riders" -> {
                try {
                    var entity = ContraptionWorlds.entityOf(level).orElse(null);
                    if (entity != null) yield PolyValue.of(!entity.state().seatedRiders().isEmpty());
                } catch (Throwable ignored) {}
                yield PolyValue.of(false);
            }
            default -> get(method);
        };
    }
}
