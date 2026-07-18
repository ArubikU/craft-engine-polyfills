package dev.arubik.craftengine.contraption.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.SeatBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.seat.SeatConfig;
import net.momirealms.craftengine.core.util.Direction;

/**
 * Discovers every sittable seat declared by a CAPTURED BLOCK — i.e. by CraftEngine's own
 * {@code seat_block} block behavior ({@link SeatBlockBehavior}) — inside a contraption's hidden
 * {@link ContraptionLevel}, and reports it as a plain local-space {@link BlockSeat} record for
 * {@link ContraptionFurnitureSwarm} to turn into a real {@code SeatSlot}. Scans the SAME cell set
 * ({@link ContraptionLevel#localPositions()}) {@link ContraptionDisplaySwarm} already iterates, so the two
 * can never disagree about which blocks a contraption actually contains.
 *
 * <p><b>Why this exists (2026-07-16 — "los bloques con seat_block no se pueden usar dentro del
 * contraption").</b> Before this class, {@code ContraptionSeatListener} only ever scanned
 * {@code furnitureSwarm().seatSlots()}, which was populated exclusively from captured FURNITURE
 * ({@code FurnitureVariant#hitBoxConfigs()} -&gt; {@code SeatConfig}). A CraftEngine BLOCK can declare seats
 * too — e.g. {@code testserver}'s {@code default:sleeper_sofa}, a {@code seat_block} behavior with
 * {@code seats: - 0,0,0} on a {@code white_bed[facing=west,occupied=false,part=foot]} carrier — and those
 * were simply invisible to the seat listener: capturing a sofa into a contraption silently made it
 * unsittable.
 *
 * <p><b>Why the NATIVE path cannot be reused, at all.</b> CraftEngine's own entry point is
 * {@code SeatBlockBehavior#useWithoutItem} -&gt; {@code SeatBlockEntityController#spawnSeat(player)} -&gt;
 * {@code BukkitSeat#spawnSeatEntityForPlayer}, which spawns the real seat entity into
 * {@code blockEntity.world} — for a captured block that is the hidden {@link ContraptionLevel}
 * mini-dimension, where the player is NOT and can never be (see {@code ContraptionLevel}'s own javadoc,
 * "Real vs. fake positions": nothing in that level is tracked by, visible to, or mountable by a real-world
 * player). Calling {@code spawnSeat} would therefore mount the player onto an entity in the wrong world —
 * it is not a matter of arranging the call correctly, the native path is structurally inapplicable to a
 * captured block. So this class does what every other captured-object system in this package does: read
 * CraftEngine's own real config off the live object inside the fake level, and re-emit it ourselves in real
 * -world space — here, as seat slots fed into the exact same {@code seatSlots()} list the listener already
 * reads, so sit/stand/carry/dismount all work unchanged with no listener logic added.
 *
 * <p><b>Rotation convention.</b> Copied from CraftEngine, not invented — see {@link #facingYawDegrees} for
 * the {@code facing} -&gt; yaw mapping (lifted from {@code SeatBlockEntityController#spawnSeat}) and
 * {@link ContraptionSeatMath} for the offset math (lifted from {@code BukkitSeat#calculateSeatLocation},
 * shared with the furniture seat path so the two can't diverge).
 */
public final class ContraptionBlockSeats {

    private ContraptionBlockSeats() {
    }

    /**
     * One seat a captured block declares, already resolved into the bearing's yaw-0 local basis — the same
     * space {@code ContraptionFurniture#localOffset()} lives in, ready to feed a {@code SeatSlot}.
     * {@code limitPlayerRotation} is carried through from the {@link SeatConfig} verbatim (CraftEngine uses
     * it to pick a rotation-clamping seat entity type); {@code local} deliberately does NOT include
     * {@code ContraptionSeatMount}'s {@code +0.6} seat height, which that class applies itself.
     */
    public record BlockSeat(Vec3 local, float yawOffsetDegrees, boolean limitPlayerRotation) {
    }

    /**
     * Every {@code seat_block} seat declared by any block currently captured in {@code level}, in the
     * bearing's yaw-0 local basis.
     *
     * <p>Deliberately best-effort per cell: a single unreadable/mid-migration block state must never blank
     * every other seat on the contraption (same defensive convention {@link ContraptionFurnitureSwarm#rebuild}
     * already uses for a malformed furniture definition), and a level with no CraftEngine behind it at all
     * (the pure-kinematics/registry unit-test path) must degrade to "no block seats", never to a thrown
     * exception inside the per-tick rebuild.
     */
    public static List<BlockSeat> scan(ContraptionLevel level) {
        List<BlockSeat> out = new ArrayList<>();
        if (level == null) {
            return out; // null-tolerant unit-test path — see ContraptionState's javadoc
        }
        for (BlockPos local : level.localPositions()) {
            try {
                SeatBlockBehavior seat = seatBehaviorAt(level, local);
                if (seat == null || seat.seats == null || seat.seats.length == 0) {
                    continue;
                }
                ImmutableBlockState state = customStateAt(level, local);
                float facingYaw = facingYawDegrees(seat, state);
                // CraftEngine's SeatBlockEntityController#spawnSeat builds its seat source position as
                // (pos.x + 0.5, pos.y, pos.z + 0.5) — the block's horizontal CENTRE at its own base, NOT
                // its min corner. A captured cell's local offset here is corner-anchored exactly like a
                // real BlockPos (that is the convention every swarm in this package shares — see
                // ContraptionMath#PIVOT_XZ), so the same +0.5/+0.5 centring has to be applied before the
                // seat offset is added, or every block seat would sit half a block off toward -X/-Z.
                Vec3 source = new Vec3(local.getX() + 0.5, local.getY(), local.getZ() + 0.5);
                for (SeatConfig cfg : seat.seats) {
                    if (cfg == null || cfg.position() == null) {
                        continue;
                    }
                    Vec3 offset = ContraptionSeatMath.seatOffset(cfg.position(), facingYaw);
                    out.add(new BlockSeat(source.add(offset),
                            ContraptionSeatMath.seatYawDegrees(cfg, facingYaw),
                            cfg.limitPlayerRotation()));
                }
            } catch (Throwable ignored) {
                // One malformed/unreadable captured block shouldn't blank every seat on the contraption.
            }
        }
        return out;
    }

    /**
     * The {@link SeatBlockBehavior} the CraftEngine block captured at {@code local} declares, or null if that
     * cell isn't a CraftEngine block at all (the overwhelmingly common case — a plain vanilla block) or
     * declares no {@code seat_block} behavior.
     *
     * <p>Routed through {@code BlockBehavior#getFirst(Class)} rather than an {@code instanceof} on
     * {@code state.behavior()} directly: a CraftEngine block with more than one behavior (the sofa has FOUR
     * — {@code sofa_block}, {@code bouncing_block}, {@code seat_block}, {@code tint_source_block}) carries a
     * {@code CompositeBlockBehavior}, whose own {@code getFirst} walks the composed array; the base
     * {@code BlockBehavior#getFirst} handles the single-behavior case by returning {@code this} when it
     * matches. So this one call covers both shapes, which is exactly why CraftEngine exposes it.
     */
    private static SeatBlockBehavior seatBehaviorAt(ContraptionLevel level, BlockPos local) {
        ImmutableBlockState state = customStateAt(level, local);
        if (state == null) {
            return null;
        }
        BlockBehavior behavior = state.behavior();
        return behavior == null ? null : behavior.getFirst(SeatBlockBehavior.class);
    }

    /**
     * The CraftEngine {@link ImmutableBlockState} backing the captured cell at {@code local}, or null for a
     * plain vanilla block. Resolved from the live NMS {@code BlockState} inside the hidden level via
     * {@code BlockStateUtils.getOptionalCustomBlockState} — the same vanilla-state-id -&gt; custom-state
     * lookup this project's own block behaviors already use, and correct here for the same reason it is
     * there: a CraftEngine block IS a real vanilla carrier state, wherever it lives.
     */
    private static ImmutableBlockState customStateAt(ContraptionLevel level, BlockPos local) {
        Optional<ImmutableBlockState> custom =
                BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(local));
        return custom.orElse(null);
    }

    /**
     * The seat source's yaw (degrees) for a block, derived from its {@code facing} property — copied
     * VERBATIM from CraftEngine's own {@code SeatBlockEntityController#spawnSeat}, which is the exact code
     * that would have produced this value had the block been sat on in the real world:
     * <pre>
     *   SOUTH -&gt; 180, WEST -&gt; 270, EAST -&gt; 90, everything else (DOWN/UP/NORTH) -&gt; 0
     * </pre>
     * (Confirmed against the real {@code craft-engine.jar}: that method's {@code switch} over
     * {@code Direction} maps {@code DOWN}/{@code UP}/{@code NORTH} to the same {@code 0} arm. It is
     * deliberately NOT {@code Direction#getYaw}, which uses a different convention — copying the seat
     * controller's own hardcoded mapping is the whole point, so a sofa's seat lands where CraftEngine would
     * put it rather than where a plausible-looking re-derivation would.)
     *
     * <p>A block with no {@code facing} property at all yields {@code 0}, matching CraftEngine's own
     * null-property branch — and {@link SeatBlockBehavior#directionProperty} is precisely that property:
     * its factory resolves it once as {@code getOptionalProperty(definition, "facing", Direction.class)},
     * the same lookup the controller does by name per-use. {@code default:sleeper_sofa} is exactly this
     * case: its state is a fixed {@code white_bed[facing=west,...]} carrier with no CraftEngine properties
     * declared, so CraftEngine itself seats at yaw 0 there regardless of the vanilla carrier's own
     * {@code facing=west} — and so do we.
     */
    static float facingYawDegrees(SeatBlockBehavior seat, ImmutableBlockState state) {
        Property<Direction> property = seat.directionProperty;
        if (property == null || state == null) {
            return 0f;
        }
        Direction direction = state.get(property);
        if (direction == null) {
            return 0f;
        }
        return switch (direction) {
            case SOUTH -> 180f;
            case WEST -> 270f;
            case EAST -> 90f;
            default -> 0f; // DOWN/UP/NORTH — same arm CraftEngine's own switch collapses them into
        };
    }
}
