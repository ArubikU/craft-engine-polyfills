package dev.arubik.craftengine.contraption.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.craftbukkit.CraftWorld;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Makes an {@link ItemEntity} that spawns INSIDE a {@link ContraptionLevel} (a captured
 * hopper/funnel/crafter ejecting output, or a captured block breaking) actually pickupable by
 * a real player standing in the real world, instead of merely being cosmetically mirrored by
 * {@link ContraptionEntityMirrorSwarm} (packet-only, no hitbox/collision, purely visual).
 *
 * <p><b>Approach — real mirrored pickup entity, not an interaction-bridge.</b> This project's
 * established pattern (see {@code ContraptionInteractionListener}'s class javadoc) is to
 * dispatch through real vanilla mechanics wherever one is reachable, rather than reimplement
 * them by hand. A real entity gets vanilla item-pickup range/cooldown, the pickup sound/
 * animation, hopper vacuum-up, and merge/despawn-timer behavior "for free" once it exists —
 * reimplementing proximity-based pickup (option (b) considered in the task) would mean hand-
 * rolling all of that instead. So: for every live {@link ItemEntity} found inside a
 * {@link ContraptionLevel}, this swarm spawns and maintains a REAL {@link ItemEntity} in the
 * real world (in {@link ContraptionLevel#realLevel()}, the actual world/dimension the
 * contraption currently occupies), teleported every tick to
 * {@link ContraptionLevel#realWorldPositionOf(Vec3)} of the internal item's live position —
 * exactly the same bridging method every other render swarm in this package already uses. The
 * mirror has no gravity/no physics of its own (it's a pure position-follower); the INTERNAL
 * item inside {@link ContraptionLevel} keeps all of its own real physics/AI untouched, so
 * anything already scanning for it there (a captured hopper/funnel/fan's own
 * {@code getEntitiesOfClass(ItemEntity.class, ...)} adjacency scan — see
 * {@code ConveyorBlockEntity}/{@code FanMachineBlockEntity}) keeps working completely
 * unmodified; this swarm doesn't touch or remove the internal item on its own.
 *
 * <p><b>Pickup direction (real player picks it up)</b>: when a real player picks up the real
 * mirror entity via completely normal vanilla mechanics, {@code EntityPickupItemEvent} fires
 * for it — {@link ContraptionItemPickupListener} (co-located in this package) listens for
 * that, resolves back to the internal source item via {@link #internalOwnerOf}, and discards
 * the internal item so it doesn't keep existing (double-pickup prevention) in
 * {@link ContraptionLevel}. If a hopper/funnel/fan sucks up the INTERNAL item first instead
 * (the reverse direction — inside picks it back up before a real player does), this swarm's own
 * per-tick cleanup pass (the "present" tracking below, same shape as
 * {@link ContraptionEntityMirrorSwarm#render}) notices the internal item is gone next tick and
 * discards the now-orphaned real-world mirror to match — no double-existence either way.
 *
 * <p><b>No feedback loop with {@link ContraptionEntityMirrorSwarm}</b>: that swarm only ever
 * scans {@code level.getAllEntities()} — i.e. entities physically inside the
 * {@link ContraptionLevel} instance itself. This swarm's real mirror entities live in a
 * completely different {@link Level} instance ({@link ContraptionLevel#realLevel()}), so they
 * are structurally never visited by that scan; no exclusion tag is needed.
 *
 * <p><b>Real-world collision.</b> Because the mirror is a hard-teleported, no-gravity,
 * zero-velocity position-follower, it previously had ZERO of vanilla's own collision
 * resolution applied to it — it would happily clip through/into a real wall or floor
 * whenever the internal item's translated real-world position landed inside a real block
 * (the {@link ContraptionLevel} itself is a void dimension outside the captured footprint,
 * so the INTERNAL item never sees that obstruction). Every tick, before teleporting the
 * mirror to its new target position, {@link #targetIsObstructed} checks the target position
 * against the REAL world's own collision via {@link Level#noCollision(Entity, AABB)} (the
 * same vanilla helper {@code Entity#move} itself uses to test "can something occupy this
 * box"). If the target is obstructed, the mirror simply is NOT teleported that tick — it
 * stays parked at its last valid real-world position instead of being dragged into/through
 * the obstruction. This is option (a) from the task: simpler and safer than hunting for the
 * nearest free position along the path, and it matches the intuitive "the item stops at the
 * wall" behavior. The INTERNAL item's own physics/collision inside {@link ContraptionLevel}
 * is completely untouched by this — it keeps moving/resting exactly as it already did.
 */
public final class ContraptionItemPickupSwarm {

    /** internal ItemEntity UUID (inside ContraptionLevel) -> real-world mirror ItemEntity UUID. */
    private final Map<UUID, UUID> mirrors = new HashMap<>();

    /** real-world mirror UUID -> internal source UUID — reverse lookup for the pickup listener. */
    private static final Map<UUID, UUID> MIRROR_TO_SOURCE = new java.util.concurrent.ConcurrentHashMap<>();
    /** internal source UUID -> owning ContraptionLevel — so the pickup listener can discard it. */
    private static final Map<UUID, ContraptionLevel> SOURCE_LEVEL = new java.util.concurrent.ConcurrentHashMap<>();

    /** Called by {@link ContraptionItemPickupListener} once a real mirror is actually picked up. */
    static UUID sourceOf(UUID mirrorUuid) {
        return MIRROR_TO_SOURCE.get(mirrorUuid);
    }

    static ContraptionLevel levelOf(UUID sourceUuid) {
        return SOURCE_LEVEL.get(sourceUuid);
    }

    /** True if {@code mirrorUuid} is one of this swarm's own real-world pickup mirrors (any contraption). */
    public static boolean isMirror(UUID mirrorUuid) {
        return MIRROR_TO_SOURCE.containsKey(mirrorUuid);
    }

    /** Discards the tracked mirror for {@code sourceUuid} without going through the normal per-tick diff (used once the source is confirmed gone). */
    static void forgetSource(UUID sourceUuid) {
        SOURCE_LEVEL.remove(sourceUuid);
    }

    /** Maintains real pickupable mirrors for every {@link ItemEntity} currently inside {@code level}; call once per contraption per tick. */
    public void tick(ContraptionLevel level) {
        if (level == null) {
            return;
        }
        Level realLevel = level.realLevel();
        if (!(realLevel instanceof ServerLevel realServerLevel)) {
            return;
        }

        Set<UUID> present = new HashSet<>();
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof ItemEntity internal) || internal.isRemoved()) {
                continue;
            }
            UUID sourceId = internal.getUUID();
            present.add(sourceId);

            Vec3 realPos = level.realWorldPositionOf(internal.position());
            UUID mirrorId = mirrors.get(sourceId);
            ItemEntity mirror = mirrorId != null ? findByUuid(realServerLevel, mirrorId) : null;

            if (mirror == null || mirror.isRemoved()) {
                mirror = spawnMirror(realServerLevel, internal, realPos);
                if (mirror == null) {
                    // target spot is obstructed in the real world and no fallback was free
                    // either — skip spawning this tick, retry next tick once it clears.
                    continue;
                }
                mirrors.put(sourceId, mirror.getUUID());
                MIRROR_TO_SOURCE.put(mirror.getUUID(), sourceId);
                SOURCE_LEVEL.put(sourceId, level);
            } else {
                // keep the mirror's carried stack in sync (e.g. a hopper partially draining the internal stack)
                if (!ItemEntity.areMergable(mirror.getItem(), internal.getItem()) || mirror.getItem().getCount() != internal.getItem().getCount()) {
                    mirror.setItem(internal.getItem().copy());
                }
                // Only move the mirror into its new target position if that spot is actually
                // free in the REAL world — a real wall/floor occupying the translated position
                // must stop the mirror instead of letting it clip straight through (see class
                // javadoc "Real-world collision"). If obstructed, leave the mirror parked at
                // its last valid real-world position for this tick.
                if (!targetIsObstructed(realServerLevel, mirror, realPos)) {
                    mirror.snapTo(realPos.x, realPos.y, realPos.z, mirror.getYRot(), mirror.getXRot());
                }
                mirror.setDeltaMovement(Vec3.ZERO);
            }
        }

        // internal item gone (picked up internally by a hopper/funnel, merged, or otherwise
        // removed) — discard the now-orphaned real-world mirror so it doesn't double-exist.
        mirrors.entrySet().removeIf(e -> {
            if (present.contains(e.getKey())) {
                return false;
            }
            ItemEntity mirror = findByUuid(realServerLevel, e.getValue());
            if (mirror != null) {
                mirror.discard();
            }
            MIRROR_TO_SOURCE.remove(e.getValue());
            SOURCE_LEVEL.remove(e.getKey());
            return true;
        });
    }

    /**
     * Releases every real mirror this instance owns back into normal existence (contraption
     * disassembled/removed) — the internal source item inside {@link ContraptionLevel} is about
     * to be destroyed along with the whole level, but the mirror is already a REAL {@link ItemEntity}
     * sitting in the real world, so it must NOT be discarded here (that would silently delete the
     * item — "al borrar el contraption las entidades se muevan bien al mundo original"). Instead
     * it just stops being a position-follower and becomes a normal, permanent, physics-driven
     * item exactly where it currently sits — gravity back on, real pickup delay restored. Being a
     * genuine entity in a real, persisted world, it also survives a server restart on its own via
     * vanilla's normal chunk-save, with no extra work needed here.
     */
    public void despawnAll() {
        for (Map.Entry<UUID, UUID> e : mirrors.entrySet()) {
            SOURCE_LEVEL.remove(e.getKey());
            UUID mirrorId = e.getValue();
            MIRROR_TO_SOURCE.remove(mirrorId);
            for (org.bukkit.World w : org.bukkit.Bukkit.getWorlds()) {
                ServerLevel sl = ((CraftWorld) w).getHandle();
                ItemEntity mirror = findByUuid(sl, mirrorId);
                if (mirror != null) {
                    mirror.setNoGravity(false);
                    mirror.setPickUpDelay(10);
                    break;
                }
            }
        }
        mirrors.clear();
    }

    public int mirrorCount() {
        return mirrors.size();
    }

    /**
     * Spawns the real mirror at {@code realPos}, unless that spot is obstructed by a real
     * block — spawning INSIDE a real wall/floor would be just as wrong as teleporting into
     * one mid-flight. If {@code realPos} is blocked, falls back to the internal item's own
     * un-translated (capture-relative) position, on the theory that a spot valid enough for
     * the internal item inside {@link ContraptionLevel} is very likely still within the real
     * world's bounds most of the time; if even that fallback is obstructed, this returns
     * {@code null} and the caller simply retries next tick instead of forcing a bad spawn.
     */
    private static ItemEntity spawnMirror(ServerLevel realServerLevel, ItemEntity internal, Vec3 realPos) {
        ItemEntity mirror = new ItemEntity(realServerLevel, realPos.x, realPos.y, realPos.z, internal.getItem().copy());
        if (!realServerLevel.noCollision(mirror, mirror.getBoundingBox())) {
            // target spot is embedded in a real block — fall back to the internal item's own
            // un-translated (capture-relative) position; a spot valid for the internal item
            // inside ContraptionLevel is very likely still within the real world's bounds.
            Vec3 fallback = internal.position();
            mirror.setPos(fallback.x, fallback.y, fallback.z);
            if (!realServerLevel.noCollision(mirror, mirror.getBoundingBox())) {
                return null; // genuinely stuck both ways — skip this tick, retry next tick
            }
        }

        mirror.setDeltaMovement(Vec3.ZERO);
        mirror.setNoGravity(true);
        mirror.setPickUpDelay(0);
        realServerLevel.addFreshEntity(mirror);
        return mirror;
    }

    /**
     * True if teleporting {@code mirror} to {@code target} would embed it inside a real-world
     * block. Uses the mirror's own bounding box translated to {@code target} and
     * {@link Level#noCollision(Entity, AABB)} — the same vanilla check {@code Entity#move}
     * itself relies on — excluding the mirror's own hitbox from the query.
     */
    private static boolean targetIsObstructed(ServerLevel realServerLevel, ItemEntity mirror, Vec3 target) {
        AABB targetBox = mirror.getBoundingBox().move(target.subtract(mirror.position()));
        return !realServerLevel.noCollision(mirror, targetBox);
    }

    private static ItemEntity findByUuid(ServerLevel level, UUID uuid) {
        Entity e = level.getEntity(uuid);
        return e instanceof ItemEntity ie ? ie : null;
    }
}
