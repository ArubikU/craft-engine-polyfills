package dev.arubik.craftengine.contraption.explosive;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joml.Vector3d;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.physics.PhysBody;
import dev.arubik.craftengine.contraption.physics.XpbdSolver;

/**
 * <b>A contraption carrying TNT that hits something hard enough detonates in the real world.</b>
 *
 * <h2>Impact speed, not fall speed</h2>
 * The trigger is the contact manifold's own relative normal speed — {@link PhysBody#maxImpactSpeed()},
 * which {@link XpbdSolver} records for every contact it generates. That is deliberately not a
 * "how fast was it falling" check: the solver produces the same number for a body slamming into the
 * ground, into a wall, and into another contraption, so ramming a cliff face at speed detonates exactly
 * like dropping from the sky does, and neither case needs its own code path. A body driven gently along
 * the ground is in permanent contact and never trips it, because contact is not impact — closing speed is.
 *
 * <h2>Where the explosion happens</h2>
 * Not in the solver. {@link XpbdSolver} writes a number onto the body and nothing else; this class is
 * called by {@code PhysicsWorld} AFTER the step completes, on the server main thread, and is the only
 * place that touches the world. The blast is placed at the contact point itself, in real-world
 * coordinates — the body's position is already the real world's frame — so the crater appears where the
 * structure actually struck, not at its center of mass.
 *
 * <h2>The contraption is consumed by its own blast</h2>
 * Detonating and then continuing to exist made the contraption an infinite bomb: the structure was still
 * sitting there, still carrying its TNT, ready to detonate again on the next impact. So a contraption that
 * goes off is torn down — despawned and unregistered, WITHOUT restoring its cells into the world, because
 * those blocks were just blown up. Its persisted record is deleted too, or the thing would come back intact
 * on the next restart.
 */
public final class ContraptionImpactDetonator {

    private ContraptionImpactDetonator() {
    }

    /**
     * Closing speed, in blocks per tick, at or above which a TNT-carrying contraption detonates.
     *
     * <p>Sized against {@link XpbdSolver#GRAVITY} ({@code -0.04} blocks/tick²), under which a free fall
     * of {@code h} blocks reaches {@code sqrt(2·0.04·h)}: {@code 0.8} is the speed of a roughly 8-block
     * drop (16 blocks/second). Chosen because everything below it is ordinary operation and everything
     * above it is unmistakably a crash — a contraption stepping off a ledge (~1 block, {@code 0.28}), a
     * minecart-borne one at track speed ({@code ~0.4}), or a player-driven one being shoved around all sit
     * far under, while a genuine drop from height or a full-speed ram sits clearly over. It is also well
     * under the solver's ~{@code 2.0} terminal speed ({@code GRAVITY / LINEAR_DAMPING}), so a long fall
     * detonates on the way down to the ground rather than needing to have maxed out first.
     */
    public static final double IMPACT_SPEED_THRESHOLD = 0.8;

    /** Blast power for a single TNT aboard — vanilla {@code PrimedTnt}'s own {@code explosionPower}. */
    public static final float BASE_POWER = 4.0f;

    /** Each TNT beyond the first adds this much power, so a bigger payload is a visibly bigger bang. */
    public static final float POWER_PER_EXTRA_TNT = 1.0f;

    /**
     * Ceiling on {@link #BASE_POWER} + {@link #POWER_PER_EXTRA_TNT} scaling, reached at 7 TNT. One
     * detonation stands in for the whole payload rather than chaining, so without a cap a freight
     * contraption packed with TNT would resolve to an explosion tens of blocks across — a lag spike and a
     * griefing tool rather than a game mechanic. {@code 10.0} is already well past a charged creeper
     * ({@code 6.0}) and reads unmistakably as "the whole thing went up".
     */
    public static final float MAX_POWER = 10.0f;

    /**
     * Ticks before the same contraption may detonate again. In practice the impact itself is
     * self-limiting — the solver stops the body, so the next tick's closing speed is near zero — but a
     * body that lands in the crater it just made, or grinds along at speed, could otherwise re-trigger on
     * consecutive ticks and fire a burst of explosions for one crash. Two seconds is long enough that one
     * impact reads as one blast, short enough that a second, genuinely separate crash still detonates.
     */
    public static final int REARM_TICKS = 40;

    /** Game tick of each contraption's last detonation — see {@link #REARM_TICKS}. */
    private static final Map<UUID, Long> LAST_DETONATION = new HashMap<>();

    /**
     * Detonates {@code state}'s contraption if this tick's impact was hard enough and it is carrying TNT.
     * Called once per PhysContraption per tick, after the solver has written its resolved transform back.
     *
     * @param body the stepped body, holding this tick's impact record
     * @param state the contraption the body belongs to
     * @param realLevel the real world the body lives in — where the blast lands
     */
    public static void afterStep(PhysBody body, ContraptionState state, ServerLevel realLevel) {
        if (body == null) {
            return;
        }
        afterStep(body.maxImpactSpeed(), body.impactPoint(), state, realLevel);
    }

    /**
     * Impact-values overload for the async physics driver (2026-07-17). On the off-thread physics path the
     * game thread must NOT read {@link PhysBody#maxImpactSpeed()}/{@link PhysBody#impactPoint()} directly —
     * the physics thread mutates them inside a step — so it passes the PUBLISHED values here instead.
     */
    public static void afterStep(double maxImpactSpeed, Vector3d point, ContraptionState state, ServerLevel realLevel) {
        if (state == null || realLevel == null) {
            return;
        }
        if (maxImpactSpeed < IMPACT_SPEED_THRESHOLD) {
            return;
        }
        if (point == null) {
            return;
        }
        int tnt = ContraptionExplosives.countTnt(state.level());
        if (tnt <= 0) {
            return; // nothing aboard to go off — a crash is just a crash
        }
        if (!rearmed(state.id(), realLevel.getGameTime())) {
            return;
        }
        float power = Math.min(MAX_POWER, BASE_POWER + POWER_PER_EXTRA_TNT * (tnt - 1));
        // Leave the blast-resistant cells behind as real falling debris BEFORE tearing the structure down
        // (2026-07-17 — "los no rompibles como obsi se quedan flotando"). Breakable cells are simply consumed
        // by the blast below, exactly as a real explosion would eat them; obsidian and its kind survive and
        // tumble out. Spawned before destroy() so the cells are still readable, and before explode() so the
        // debris entities are already out of the block grid the blast carves.
        Vec3 blastCenter = new Vec3(point.x, point.y, point.z);
        ContraptionExplosives.spawnBlastSurvivors(state, realLevel, blastCenter, power);
        // Destroy the contraption BEFORE the blast, not after: the explosion is what removes its
        // cells from existence, and tearing down first means the crater is carved into a world the
        // structure has already left rather than one it is still standing in.
        destroy(state);
        realLevel.explode(null, point.x, point.y, point.z, power, Level.ExplosionInteraction.TNT);
    }

    /**
     * Tears the detonated contraption down without restoring a single cell — its blocks went up with
     * the blast.
     *
     * <p>Deliberately not {@code ContraptionAssembler#disassemble}, which exists to put a contraption's
     * blocks BACK into the world: using it here would have the bomb helpfully rebuild itself as real
     * blocks inside its own crater.
     */
    private static void destroy(ContraptionState state) {
        ContraptionEntity entity = ContraptionManager.get(state.id());
        if (entity == null) {
            return;
        }
        try {
            // Despawn to the world's real players, never an empty list: a swarm's despawnAll iterates
            // the viewers it is HANDED and then clears its own records, so an empty list sends zero
            // despawn packets while still forgetting the entities existed — stranding the blown-up
            // structure's fake blocks on every client that could see it.
            net.minecraft.server.MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            net.minecraft.server.level.ServerLevel serverLevel = server.getLevel(state.worldId());
            org.bukkit.World bukkitWorld = serverLevel != null ? serverLevel.getWorld() : null;
            entity.despawn(bukkitWorld == null ? java.util.List.of()
                    : dev.arubik.craftengine.contraption.player.CePlayers.resolve(bukkitWorld.getPlayers()));
            ContraptionManager.remove(state.id());
            dev.arubik.craftengine.contraption.physics.PhysicsWorld.remove(state.id());
            dev.arubik.craftengine.contraption.persistence.BlockAnchoredContraptionStore.delete(state.id());
            dev.arubik.craftengine.contraption.listener.BearingHammerListener.forgetAssembled(state.id());
            var level = state.level();
            if (level != null) {
                level.dispose();
            }
        } catch (Throwable t) {
            // A teardown failure must not swallow the blast — the explosion is the visible effect the
            // player is owed, and a leaked contraption is the lesser bug.
            t.printStackTrace();
        }
    }

    /**
     * Whether {@code id} may detonate at {@code now}, recording the detonation if so. Entries for
     * contraptions that no longer exist are dropped here rather than by a lifecycle hook: the map only
     * ever holds contraptions that actually blew something up, so the sweep is over a handful of keys.
     */
    private static boolean rearmed(UUID id, long now) {
        LAST_DETONATION.keySet().removeIf(known -> ContraptionManager.get(known) == null);
        Long last = LAST_DETONATION.get(id);
        if (last != null && now - last < REARM_TICKS) {
            return false;
        }
        LAST_DETONATION.put(id, now);
        return true;
    }
}
