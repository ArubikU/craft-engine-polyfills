package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.ContraptionContainerView;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.MassModel;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public final class ContraptionType {

    /** How many ticks a script-reported move() /teleport() stays "recent" for
     *  is_moving()/speed's fallback — generous enough to survive any reasonable action_interval
     *  (the driving script may only call move() every few ticks) without misreporting "moving"
     *  long after the contraption actually stopped. */
    private static final long SCRIPT_MOVE_GRACE_TICKS = 20;

    private ContraptionType() {}

    public static void register() {
        PolyTypeRegistry.define("Contraption")
            .property("is_contraption", obj -> ScriptValue.of(obj != null))
            .property("block_count",    obj -> ScriptValue.of(cl(obj).blockCount()))
            // Total mass of every block in the structure (WeightBlockBehavior.weightOf per block,
            // same figure the physics solver uses for inertia) — for a script that wants to scale
            // stress/su cost with how much the contraption is actually made of, e.g. a rotational
            // bearing motor charging su by weight instead of by a fixed rate.
            .property("weight", obj -> {
                try { return ScriptValue.of(MassModel.of(cl(obj)).totalMass()); }
                catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            .property("yaw",   obj -> ScriptValue.of(Math.toDegrees(cl(obj).realYawRadians())))
            .property("pitch", obj -> ScriptValue.of(Math.toDegrees(cl(obj).realPitchRadians())))
            .property("roll",  obj -> ScriptValue.of(Math.toDegrees(cl(obj).realRollRadians())))
            .property("scale", obj -> ScriptValue.of(cl(obj).realScaleFactor()))
            // Three fallback sources, in order:
            //   1. a real PhysicsWorld body's velocity (linear/vehicle/phys bearing types);
            //   2. ContraptionState#lastDeltaX/Y/Z — the velocity ContraptionEngine#stepKinematics
            //      already accumulates every tick from EVERY attached MovementBehavior's own
            //      velocityThisTick(), physics or not. This is what covers a minecart-bearing
            //      contraption: MinecartFollowBehavior has no physics body, it just samples the
            //      real minecart entity's position each tick and reports the delta as its
            //      velocityThisTick() — stepKinematics already folds that into lastDelta today, this
            //      was simply never read from here before;
            //   3. a recently-reported script move() /teleport() (see ContraptionState#
            //      reportScriptMove) — covers a "machine_contraption" elevator/piston bearing that
            //      has no physics body AND no attached MovementBehavior at all, driving its own
            //      position directly from its own action_script instead.
            // Without ALL three, a drill riding a minecart or a script-driven lift always saw 0/
            // false here regardless of how fast it was actually moving, identical to the
            // contraption.rpm gap set_spin() had for the rotational case.
            .property("speed", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        if (PhysicsWorld.isHeld(entity.state().id())) return ScriptValue.of(0.0);
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        if (body != null) return ScriptValue.of(body.body.linearVelocity.length());
                        var st = entity.state();
                        double dx = st.lastDeltaX(), dy = st.lastDeltaY(), dz = st.lastDeltaZ();
                        double behaviorSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (behaviorSpeed > 0.001) return ScriptValue.of(behaviorSpeed);
                        long now = net.minecraft.server.MinecraftServer.getServer().getTickCount();
                        if (st.scriptMoveRecent(now, SCRIPT_MOVE_GRACE_TICKS)) {
                            double sx = st.lastScriptMoveSpeedX(), sy = st.lastScriptMoveSpeedY(), sz = st.lastScriptMoveSpeedZ();
                            return ScriptValue.of(Math.sqrt(sx * sx + sy * sy + sz * sz));
                        }
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("rider_count", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(entity.state().seatedRiders().size());
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("is_held", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(PhysicsWorld.isHeld(entity.state().id()));
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .property("x", obj -> {
                try { return ScriptValue.of(cl(obj).realWorldPositionOf(new BlockPos(0, 0, 0)).x); }
                catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            .property("y", obj -> {
                try { return ScriptValue.of(cl(obj).realWorldPositionOf(new BlockPos(0, 0, 0)).y); }
                catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            .property("z", obj -> {
                try { return ScriptValue.of(cl(obj).realWorldPositionOf(new BlockPos(0, 0, 0)).z); }
                catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            .property("anchor_entity", obj -> {
                try {
                    var cEntity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (cEntity != null) {
                        java.util.UUID anchorId = cEntity.state().anchorEntityId();
                        if (anchorId != null && cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                            net.minecraft.world.entity.Entity anchor = rl.getEntity(anchorId);
                            if (anchor != null) return EntityType.wrap(anchor);
                        }
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            .property("has_anchor_entity", obj -> {
                try {
                    var cEntity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (cEntity != null) return ScriptValue.of(cEntity.state().anchorEntityId() != null);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // See Contraption.speed's javadoc above for the three fallback sources (physics body,
            // ContraptionState#lastDelta from ANY attached MovementBehavior — e.g. a minecart
            // bearing's MinecartFollowBehavior — and a recently-reported script move()/teleport()).
            .method("is_moving", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        if (PhysicsWorld.isHeld(entity.state().id())) return ScriptValue.of(false);
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        if (body != null) return ScriptValue.of(body.body.linearVelocity.lengthSquared() > 0.001);
                        var st = entity.state();
                        double dx = st.lastDeltaX(), dy = st.lastDeltaY(), dz = st.lastDeltaZ();
                        if (dx * dx + dy * dy + dz * dz > 0.001 * 0.001) return ScriptValue.of(true);
                        return ScriptValue.of(st.scriptMoveRecent(
                                net.minecraft.server.MinecraftServer.getServer().getTickCount(), SCRIPT_MOVE_GRACE_TICKS));
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("has_riders", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(!entity.state().seatedRiders().isEmpty());
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .property("uuid", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(entity.state().id().toString());
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            .property("real_world", obj -> {
                if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl)
                    return WorldType.wrap(rl);
                return ScriptValue.NULL;
            })
            .property("contraption_world", obj -> ScriptValue.ofObj("ContraptionWorld", cl(obj)))
            // Combined pushable STORAGE+OUTPUT container across the whole contraption — see
            // ContraptionContainerView's javadoc. Built fresh every access (cheap), so a script
            // calling e.g. Machine.contraption.container.push(item) every tick always sees the
            // contraption's current blocks/contents, not a stale snapshot from assembly time.
            .property("container", obj -> ScriptValue.ofObj("ContraptionContainer", ContraptionContainerView.build(cl(obj))))
            // --- Motion control ---
            // Both teleport() and move() below no-op (but still return true, i.e. "acknowledged")
            // while Contraption.hold() is active — the same reasoning as set_spin()'s held check:
            // a "machine_contraption" elevator/piston-style bearing driven by a script calling
            // move()/teleport() every tick toward a target position (e.g. an initial-pos/final-pos
            // lift) has no physics body either, so without this, hold() would have zero effect on
            // it too and a drill riding along would sail straight through its target the same way
            // it did for a rotating one before this fix.
            .method("teleport", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    if (PhysicsWorld.isHeld(entity.state().id())) return ScriptValue.of(true);
                    var state = entity.state();
                    double oldX = state.x(), oldY = state.y(), oldZ = state.z();
                    double x = args.get(0).asNum(), y = args.get(1).asNum(), z = args.get(2).asNum();
                    double yaw = args.size() >= 4 ? Math.toRadians(args.get(3).asNum()) : cl(obj).realYawRadians();
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                        entity.teleport(rl.getWorld(), x, y, z, yaw);
                        // See Contraption.speed/is_moving() below — this is what lets those detect
                        // a script-driven (no physics body) contraption sliding via repeated
                        // teleport() calls, not just a real PhysicsWorld body's velocity.
                        state.reportScriptMove(net.minecraft.server.MinecraftServer.getServer().getTickCount(),
                                x - oldX, y - oldY, z - oldZ);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("move", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    if (PhysicsWorld.isHeld(entity.state().id())) return ScriptValue.of(true);
                    net.minecraft.world.phys.Vec3 origin = cl(obj).realWorldPositionOf(new BlockPos(0,0,0));
                    double dx = args.get(0).asNum(), dy = args.get(1).asNum(), dz = args.get(2).asNum();
                    double nx = origin.x + dx, ny = origin.y + dy, nz = origin.z + dz;
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                        entity.teleport(rl.getWorld(), nx, ny, nz, cl(obj).realYawRadians());
                        // See teleport() above and Contraption.speed/is_moving() below.
                        entity.state().reportScriptMove(net.minecraft.server.MinecraftServer.getServer().getTickCount(), dx, dy, dz);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("set_velocity", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.setLinearVelocity(entity.state().id(),
                        new Vector3d(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("apply_impulse", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.applyThrustCentral(entity.state().id(),
                        new Vector3d(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("set_yaw", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setYawRadians(Math.toRadians(args.get(0).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // yaw/pitch/roll were readable but only yaw was settable, so a script could see a
            // contraption's full orientation and change just one third of it. These complete the pair.
            .method("set_pitch", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setPitchRadians(Math.toRadians(args.get(0).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("set_roll", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setRollRadians(Math.toRadians(args.get(0).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            /** set_rotation(yaw, pitch, roll) in degrees — the whole orientation in one call. */
            .method("set_rotation", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setYawRadians(Math.toRadians(args.get(0).asNum()));
                    entity.state().setPitchRadians(Math.toRadians(args.get(1).asNum()));
                    entity.state().setRollRadians(Math.toRadians(args.get(2).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            /** Advances the orientation by a delta in degrees — the usual way to spin something. */
            .method("rotate_by", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    var st = entity.state();
                    st.setYawRadians(st.yawRadians() + Math.toRadians(args.get(0).asNum()));
                    st.setPitchRadians(st.pitchRadians() + Math.toRadians(args.get(1).asNum()));
                    st.setRollRadians(st.rollRadians() + Math.toRadians(args.get(2).asNum()));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("set_yaw_rate", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.setYawRate(entity.state().id(), args.get(0).asNum());
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // hold(key) / release(key) — REGISTERS/un-registers `key` as a reason the contraption
            // must stay still, rather than a single shared on/off flag. Two blocks riding the same
            // contraption (two drills on one rotating arm, say) each call hold() independently while
            // they're mid-cut; with a plain boolean, whichever one finished and called release()
            // FIRST would resume the whole contraption out from under the other one still cutting.
            // With a per-key registry (PhysicsWorld#hold/#release), the contraption only actually
            // resumes once EVERY registered key has released — see PhysicsWorld#isHeld. `key` should
            // be something stable and unique per calling block, e.g. Machine.pos as a drill script
            // would pass; the same key is safe to hold() again while already held (re-registration,
            // not a second independent hold needing two releases).
            .method("hold", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.hold(entity.state().id(), holderKey(args.get(0)));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("release", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.release(entity.state().id(), holderKey(args.get(0)));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // set_spin(axis, rpm) — turn the contraption about a world axis at a given RPM.
            //
            // Drives the contraption's OWN rotation state directly (state.setYawRadians/
            // setPitchRadians/setRollRadians), the exact mechanism RotationalBearingBehavior/
            // WindmillBearingBehavior already use for a script/behavior-driven bearing with no
            // physics body — NOT PhysicsWorld.setAngularVelocity, which only affects a REAL
            // physics-vehicle entry (linear/vehicle/phys bearing types via attachDefaultBehavior)
            // and silently no-ops (entry.physBody == null) for anything else, including
            // "machine_contraption" (create_bearing's default type), which is why a script-driven
            // bearing calling this used to visibly do nothing at all.
            .method("set_spin", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    var state = entity.state();
                    // Scale by how many REAL ticks actually elapsed since the last call, not a
                    // flat "one tick" per call — a script gated by action_interval (windmill.pf,
                    // rotational_bearing.pf: every 4 ticks) only calls this once every N ticks, and
                    // without this the contraption under-rotates by a factor of N versus the exact
                    // same RPM applied every tick (as RotationalBearingBehavior/set_rpm_output's own
                    // "1 RPM = 0.3 deg/tick" convention assumes — see windmill.pf/gearbox renderers'
                    // matching "tick() * rpm * 0.3" formula, which IS per-real-tick).
                    long now = net.minecraft.server.MinecraftServer.getServer().getTickCount();
                    long last = state.lastSetSpinTick();
                    long elapsedTicks = last == Long.MIN_VALUE ? 1L : Math.max(1L, Math.min(now - last, 100L));
                    state.setLastSetSpinTick(now);
                    // A held contraption (Contraption.hold(), e.g. a drill pausing mid-break so it
                    // doesn't sweep past its target) must actually stop turning. hold()/release()
                    // only ever wrote to PhysicsWorld's held-set before this check existed — that's
                    // read by the physics step for a real physics-body contraption, but a
                    // "machine_contraption" bearing (create_bearing's default type, e.g.
                    // rotational_bearing.pf) has NO physics body at all; set_spin() IS its entire
                    // rotation mechanism, called unconditionally every tick regardless of hold
                    // state, so the contraption kept spinning straight through a hold with zero
                    // effect. Checking it here — the actual place rotation gets applied for this
                    // bearing type — is what makes hold() real for it. Time isn't lost: elapsedTicks
                    // was already stamped above, so releasing doesn't cause a catch-up jump; it
                    // just resumes from wherever it left off.
                    // Also stamp globalRpm with the reported rate — the same field
                    // RotationalBearingBehavior/WindmillBearingBehavior (the OTHER, physics-less-
                    // but-attached-MovementBehavior route) already keep current via
                    // state.setGlobalRpm() every tick. A "machine_contraption" bearing (create_bearing's
                    // default type, e.g. rotational_bearing.pf) has NO attached MovementBehavior at
                    // all — set_spin() IS its entire rotation mechanism — so without this,
                    // contraption.rpm/get_rpm() silently stayed 0 forever for it, and anything reading
                    // "is this contraption spinning" off contraption.rpm (rather than polling yaw
                    // itself) always saw false. Stamped even while held, below, so a reader still
                    // sees "this is meant to be spinning at N rpm" rather than a stale/zero value.
                    state.setGlobalRpm((float) args.get(1).asNum());
                    if (PhysicsWorld.isHeld(entity.state().id())) {
                        return ScriptValue.of(true);
                    }
                    // 1 RPM = one turn per 60s = 2*PI rad / 1200 ticks.
                    double radiansPerTick = args.get(1).asNum() * (2.0 * Math.PI / 1200.0) * elapsedTicks;
                    switch (args.get(0).asStr().trim().toLowerCase(java.util.Locale.ROOT)) {
                        case "x" -> state.setPitchRadians(state.pitchRadians() + radiansPerTick);
                        case "z" -> state.setRollRadians(state.rollRadians() + radiansPerTick);
                        default -> state.setYawRadians(state.yawRadians() + radiansPerTick);
                    }
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // set_angular_velocity(x, y, z) — drives a REAL physics body (radians per tick about
            // each world axis simultaneously) — for an actual physics-vehicle contraption, unlike
            // set_spin above which is for a script/behavior-driven bearing with no physics body.
            .method("set_angular_velocity", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.setAngularVelocity(entity.state().id(),
                            args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum());
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("set_rpm", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setGlobalRpm((float) args.get(0).asNum());
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("get_rpm", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(entity.state().globalRpm());
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .method("set_scale", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    entity.state().setScale(args.get(0).asNum());
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // report_su(su) — no-op on contraption; SU is reported by the bearing machine script
            // Exists so windmill.pf can call contraption.report_su(su) without errors
            .method("report_su", (obj, args) -> ScriptValue.of(true))
            // Hard force-remove — despawns/disposes but does NOT restore blocks. See
            // ContraptionManagerType#disassemble's javadoc for when to use which.
            .method("kill", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) { dev.arubik.craftengine.contraption.ContraptionKill.kill(entity); return ScriptValue.of(true); }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // The real "return this structure to the world" operation — same primitive the
            // hammer-disassemble listener uses (BearingHammerListener). Restores every block
            // (rotation-snapped), glue edges, and furniture to their resting positions, then
            // despawns/disposes the contraption.
            .method("disassemble", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    if (!(cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl)) return ScriptValue.of(false);
                    dev.arubik.craftengine.contraption.assembly.ContraptionAssembler.disassemble(rl.getWorld(), entity);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .property("rpm", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) return ScriptValue.of(entity.state().globalRpm());
                } catch (Throwable ignored) {}
                return ScriptValue.of(0.0);
            })
            .property("velocity", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        if (body != null) return VectorType.wrap(body.body.linearVelocity.x, body.body.linearVelocity.y, body.body.linearVelocity.z);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // real_direction(dx,dy,dz) → the real-world direction NAME ("north".."down") that a
            // LOCAL direction (e.g. Machine.facing_dx/dy/dz) currently points to once the
            // contraption's live rotation — yaw, pitch, AND roll, not yaw alone — is applied. NULL
            // if any of the three isn't currently within ~1° of a multiple of 90°, same requirement
            // Create's own Portable Storage Interface has (getCurrentFacingIfValid): a spinning or
            // off-axis contraption can't make a valid facing-to-facing connection.
            //
            // Deliberately does NOT go through ContraptionWorlds#realDirectionOf/#isGridAligned —
            // those ALSO require the contraption's anchor POSITION to sit within 0.05 of an integer
            // block coordinate, a requirement that has nothing to do with rotation and silently
            // returns empty for most contraptions (an anchor offset by even a fractional amount,
            // which is completely normal) even when perfectly cardinal-aligned, and only ever
            // checked yaw anyway — a contraption tipped on its side (rolled/pitched 90°) has a
            // perfectly well-defined facing too, just not one yaw alone can express.
            .method("real_direction", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                try {
                    double yaw = ContraptionMath.snapYawToCardinal(cl(obj).realYawRadians());
                    double pitch = ContraptionMath.snapYawToCardinal(cl(obj).realPitchRadians());
                    double roll = ContraptionMath.snapYawToCardinal(cl(obj).realRollRadians());
                    // Widened from 0.02 rad (~1.1°) to 0.12 rad (~6.9°): a contraption spinning fast
                    // enough can rotate several degrees between action_script ticks, so a too-tight
                    // window meant it was frequently sampled just past "aligned" and never matched at
                    // all — this still rejects anything visibly off-axis while giving fast movers a
                    // real chance of being caught mid-tick.
                    if (angleDiff(cl(obj).realYawRadians(), yaw) > 0.12
                            || angleDiff(cl(obj).realPitchRadians(), pitch) > 0.12
                            || angleDiff(cl(obj).realRollRadians(), roll) > 0.12) {
                        return ScriptValue.NULL;
                    }

                    int dx = (int) args.get(0).asNum(), dy = (int) args.get(1).asNum(), dz = (int) args.get(2).asNum();
                    net.minecraft.core.Direction local = net.minecraft.core.Direction.getNearest(dx, dy, dz, net.minecraft.core.Direction.NORTH);
                    net.minecraft.world.phys.Vec3 rotated = ContraptionMath.rotateYawPitchRoll(
                            new net.minecraft.world.phys.Vec3(local.getStepX(), local.getStepY(), local.getStepZ()), yaw, pitch, roll);
                    int rx = (int) Math.round(rotated.x), ry = (int) Math.round(rotated.y), rz = (int) Math.round(rotated.z);
                    for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                        if (d.getStepX() == rx && d.getStepY() == ry && d.getStepZ() == rz) return ScriptValue.of(d.getName());
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // get_block(lx,ly,lz) → BlockType at local contraption coords.
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                BlockPos bp = new BlockPos(x, y, z);
                try { cl(obj).ensureChunkReady(bp); } catch (Throwable ignored) {}
                return BlockType.wrap(cl(obj).serverLevel(), bp);
            })
            // blocks() → Array of BlockType for all local positions with a real (non-air) block.
            // This is what windmill.pf (and anything counting/inspecting a contraption's own
            // contents) actually calls — it lives HERE on Contraption, not on ContraptionWorld
            // below (a distinct PolyType, reached via contraption.contraption_world instead). It
            // used to only exist on ContraptionWorld, so `contraption.blocks()` resolved to no
            // method at all and silently returned NULL — indistinguishable from a real empty
            // result once passed through len(), which answers 0 for NULL same as for []. Also
            // forces each position's virtual chunk ready before reading it (see ensureChunkReady's
            // other callers, ContraptionInteractionListener/ContraptionFurnitureCapture) since
            // nothing else keeps them loaded for a periodic action_script to query later.
            .method("blocks", (obj, args) -> {
                try {
                    java.util.Set<BlockPos> positions = cl(obj).localPositions();
                    net.minecraft.server.level.ServerLevel fakeLevel = cl(obj).serverLevel();
                    java.util.List<ScriptValue> list = new java.util.ArrayList<>(positions.size());
                    for (BlockPos bp : positions) {
                        try { cl(obj).ensureChunkReady(bp); } catch (Throwable ignored) {}
                        if (!cl(obj).getBlockState(bp).isAir())
                            list.add(BlockType.wrap(fakeLevel, bp));
                    }
                    return new ScriptValue.Array(list);
                } catch (Throwable ignored) { return new ScriptValue.Array(java.util.List.of()); }
            })
            // entities() → Array of EntityType for entities inside the contraption's own level.
            .method("entities", (obj, args) -> {
                try {
                    net.minecraft.server.level.ServerLevel fakeLevel = cl(obj).serverLevel();
                    net.minecraft.world.phys.AABB huge = new net.minecraft.world.phys.AABB(-30000000, -512, -30000000, 30000000, 512, 30000000);
                    java.util.List<net.minecraft.world.entity.Entity> ents =
                        fakeLevel.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, huge, e -> true);
                    return EntityType.wrapList(ents);
                } catch (Throwable ignored) { return new ScriptValue.Array(java.util.List.of()); }
            });

        // ContraptionWorld extends World — full contraption-level API
        PolyTypeRegistry.define("ContraptionWorld", "World")
            .property("is_contraption",  obj -> ScriptValue.of(true))
            .property("block_count",     obj -> ScriptValue.of(cl(obj).blockCount()))
            .property("yaw",   obj -> ScriptValue.of(Math.toDegrees(cl(obj).realYawRadians())))
            .property("pitch", obj -> ScriptValue.of(Math.toDegrees(cl(obj).realPitchRadians())))
            .property("roll",  obj -> ScriptValue.of(Math.toDegrees(cl(obj).realRollRadians())))
            .property("scale", obj -> ScriptValue.of(cl(obj).realScaleFactor()))
            .property("real_world", obj -> {
                if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) return WorldType.wrap(rl);
                return ScriptValue.NULL;
            })
            .property("container", obj -> ScriptValue.ofObj("ContraptionContainer", ContraptionContainerView.build(cl(obj))))
            // real_pos(x,y,z) → Vec3 in real-world coords
            .method("real_pos", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                net.minecraft.world.phys.Vec3 rp = cl(obj).realWorldPositionOf(
                    new net.minecraft.world.phys.Vec3(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                return VectorType.wrap(rp.x, rp.y, rp.z);
            })
            // real_block(lx,ly,lz) → BlockType at projected real-world pos. lx/ly/lz are treated as
            // a BLOCK (corner) coordinate, like get_block()/blocks() use — but the +0.5 centering
            // below before rotating is NOT optional: every other renderPosition() caller in this
            // codebase that projects a whole block (ContraptionMachineRendererElement,
            // ContraptionBlockElement, ...) explicitly centers first, because rotating a raw CORNER
            // coordinate about the local origin and then flooring can land in the wrong cell the
            // moment yaw isn't an exact multiple of 90° — i.e. any tick a rotational_bearing-driven
            // contraption is actually spinning, which is the entire point of calling this. Rotating
            // the CENTER instead keeps the floor stable (this was the actual cause of a
            // contraption-riding drill always seeing its target as air: this method had never been
            // exercised by any real caller before that feature). Math.floor via BlockPos.containing
            // (not a raw (int) cast, which truncates toward zero and is wrong for negative inputs)
            // then recovers the containing block from that centered, rotated point.
            .method("real_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                try {
                    net.minecraft.world.phys.Vec3 rp = cl(obj).realWorldPositionOf(
                        new net.minecraft.world.phys.Vec3(
                            args.get(0).asNum() + 0.5, args.get(1).asNum() + 0.5, args.get(2).asNum() + 0.5));
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl)
                        return BlockType.wrap(rl, BlockPos.containing(rp.x, rp.y, rp.z));
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // local_block(rx,ry,rz) → BlockType at the LOCAL contraption position that
            // corresponds to real-world (rx,ry,rz) right now — the exact inverse of real_block(),
            // using the same centered-then-floored convention (so real_block(local_block(p)) round-
            // trips onto the same block p came from). This is what lets something OUTSIDE a
            // contraption (a stationary Portable Storage Interface, say) answer "is the real block
            // directly in front of me currently PART of this contraption" precisely — accounting for
            // its current position AND rotation — instead of a rough is-it-nearby distance check.
            .method("local_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                try {
                    net.minecraft.world.phys.Vec3 bearing = cl(obj).realWorldPositionOf(BlockPos.ZERO);
                    net.minecraft.world.phys.Vec3 lp = ContraptionMath.realToLocal(
                        new net.minecraft.world.phys.Vec3(
                            args.get(0).asNum() + 0.5, args.get(1).asNum() + 0.5, args.get(2).asNum() + 0.5),
                        bearing, cl(obj).realYawRadians(), cl(obj).realPitchRadians(), cl(obj).realRollRadians(), cl(obj).realScaleFactor());
                    BlockPos bp = BlockPos.containing(lp.x, lp.y, lp.z);
                    cl(obj).ensureChunkReady(bp);
                    return BlockType.wrap(cl(obj).serverLevel(), bp);
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // get_block(lx,ly,lz) → BlockType at local contraption coords
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int)args.get(0).asNum(), y = (int)args.get(1).asNum(), z = (int)args.get(2).asNum();
                BlockPos bp = new BlockPos(x, y, z);
                try { cl(obj).ensureChunkReady(bp); } catch (Throwable ignored) {}
                return BlockType.wrap(cl(obj).serverLevel(), bp);
            })
            // blocks() → Array of BlockType for all local positions. Used to read the contraption's
            // own contents (e.g. windmill.pf counting sails) — was silently always empty a few
            // ticks after assembly: nothing outside a player interacting with a contraption block
            // (ContraptionInteractionListener) or furniture capture ever called ensureChunkReady on
            // its virtual chunks, so by the time a periodic action_script queried them they'd gone
            // unloaded and getBlockState quietly answered air for every position. Force each
            // position's chunk ready before reading it, same as those other two callers already do.
            .method("blocks", (obj, args) -> {
                try {
                    java.util.Set<BlockPos> positions = cl(obj).localPositions();
                    net.minecraft.server.level.ServerLevel fakeLevel = cl(obj).serverLevel();
                    java.util.List<ScriptValue> list = new java.util.ArrayList<>(positions.size());
                    for (BlockPos bp : positions) {
                        try { cl(obj).ensureChunkReady(bp); } catch (Throwable ignored) {}
                        if (!cl(obj).getBlockState(bp).isAir())
                            list.add(BlockType.wrap(fakeLevel, bp));
                    }
                    return new ScriptValue.Array(list);
                } catch (Throwable ignored) { return new ScriptValue.Array(java.util.List.of()); }
            })
            // entities() → Array of EntityType for entities inside contraption
            .method("entities", (obj, args) -> {
                try {
                    net.minecraft.server.level.ServerLevel fakeLevel = cl(obj).serverLevel();
                    net.minecraft.world.phys.AABB huge = new net.minecraft.world.phys.AABB(-30000000, -512, -30000000, 30000000, 512, 30000000);
                    java.util.List<net.minecraft.world.entity.Entity> ents =
                        fakeLevel.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, huge, e -> true);
                    return EntityType.wrapList(ents);
                } catch (Throwable ignored) { return new ScriptValue.Array(java.util.List.of()); }
            })
            // play_sound — forwards to real world at projected position
            .method("play_sound", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                try {
                    net.minecraft.world.phys.Vec3 rp = cl(obj).realWorldPositionOf(
                        new net.minecraft.world.phys.Vec3(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                    String sound = args.get(3).asStr();
                    float vol = args.size() >= 5 ? (float)args.get(4).asNum() : 1.0f;
                    float pitch = args.size() >= 6 ? (float)args.get(5).asNum() : 1.0f;
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                        var event = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.Identifier.parse(sound));
                        rl.playSeededSound(null, rp.x, rp.y, rp.z, net.minecraft.core.Holder.direct(event),
                            net.minecraft.sounds.SoundSource.BLOCKS, vol, pitch, 0L);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            });

        // ContraptionContainer — the combined pushable STORAGE+OUTPUT view returned by
        // Contraption.container / ContraptionWorld.container (see ContraptionContainerView). This
        // is what a belt-fed processing machine (sawmill, harvester, ...) riding a moving
        // contraption uses to redirect its drops into the contraption's own storage instead of
        // dropping them into the world, and what the Portable Storage Interface mirrors against.
        // All actual get/set/push/has_room behavior lives on the generic "Container" base type
        // (ContainerType) — nothing contraption-specific needed here beyond the subtype name itself
        // (kept for future contraption-only additions and clearer script-side type checks).
        PolyTypeRegistry.define("ContraptionContainer", "Container");
    }

    /** Null-safe: returns NULL if not a ContraptionLevel. */
    public static ScriptValue wrap(Object serverLevel) {
        if (serverLevel instanceof ContraptionLevel cl) return ScriptValue.ofObj("Contraption", cl);
        return ScriptValue.NULL;
    }

    private static ContraptionLevel cl(Object obj) { return (ContraptionLevel) obj; }

    /** Normalizes a hold()/release() key argument into a stable String for
     *  PhysicsWorld#hold/#release's registry — ScriptValue.asStr() is only well-defined for a
     *  primitive (string/number/bool), so a caller MUST pass one of those (e.g.
     *  {@code Machine.x + "," + Machine.y + "," + Machine.z}), not a raw object like Machine.pos
     *  (a Vector), which would coerce to the same "?" for every caller and collapse every holder
     *  onto one key. */
    private static String holderKey(ScriptValue v) {
        return v.asStr();
    }

    /** Smallest absolute angular distance between two radian angles, wrapped into [-PI, PI] first —
     *  used by real_direction() to check each of yaw/pitch/roll against its own 90°-snapped value. */
    private static double angleDiff(double a, double b) {
        double diff = a - b;
        while (diff > Math.PI) diff -= 2.0 * Math.PI;
        while (diff < -Math.PI) diff += 2.0 * Math.PI;
        return Math.abs(diff);
    }
}
