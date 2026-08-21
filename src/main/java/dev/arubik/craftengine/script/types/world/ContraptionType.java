package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public final class ContraptionType {

    private ContraptionType() {}

    public static void register() {
        PolyTypeRegistry.define("Contraption")
            .property("is_contraption", obj -> ScriptValue.of(obj != null))
            .property("block_count",    obj -> ScriptValue.of(cl(obj).blockCount()))
            .property("yaw",   obj -> ScriptValue.of(Math.toDegrees(cl(obj).realYawRadians())))
            .property("pitch", obj -> ScriptValue.of(Math.toDegrees(cl(obj).realPitchRadians())))
            .property("roll",  obj -> ScriptValue.of(Math.toDegrees(cl(obj).realRollRadians())))
            .property("scale", obj -> ScriptValue.of(cl(obj).realScaleFactor()))
            .property("speed", obj -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        if (body != null) return ScriptValue.of(body.body.linearVelocity.length());
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
            .method("is_moving", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        var body = PhysicsWorld.bodyOf(entity.state().id());
                        return ScriptValue.of(body != null && body.body.linearVelocity.lengthSquared() > 0.001);
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
            .method("disassemble", (obj, args) -> {
                // TODO: Call ContraptionKill or equivalent when API is stable
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) {
                        dev.arubik.craftengine.contraption.ContraptionKill.kill(entity);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                return BlockType.wrap((net.minecraft.server.level.ServerLevel)(Object)cl(obj), new BlockPos(x, y, z));
            })
            // --- Motion control ---
            .method("teleport", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    double x = args.get(0).asNum(), y = args.get(1).asNum(), z = args.get(2).asNum();
                    double yaw = args.size() >= 4 ? Math.toRadians(args.get(3).asNum()) : cl(obj).realYawRadians();
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                        entity.teleport(rl.getWorld(), x, y, z, yaw);
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
                    net.minecraft.world.phys.Vec3 origin = cl(obj).realWorldPositionOf(new BlockPos(0,0,0));
                    double nx = origin.x + args.get(0).asNum(), ny = origin.y + args.get(1).asNum(), nz = origin.z + args.get(2).asNum();
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl) {
                        entity.teleport(rl.getWorld(), nx, ny, nz, cl(obj).realYawRadians());
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
            .method("hold", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.setHeld(entity.state().id(), true);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("release", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    PhysicsWorld.setHeld(entity.state().id(), false);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // set_spin(axis, rpm) — turn the contraption about a world axis at a given RPM.
            //
            // This is the one that actually MOVES it. set_rpm below only records a number on the
            // contraption's state; set_yaw_rate only ever drove the Y axis, so a bearing mounted on
            // a wall — a windmill, which has to turn like a wheel about X or Z — could not be
            // rotated at all.
            .method("set_spin", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity == null) return ScriptValue.of(false);
                    // 1 RPM = one turn per 60s = 2*PI rad / 1200 ticks.
                    double omega = args.get(1).asNum() * (2.0 * Math.PI / 1200.0);
                    double x = 0, y = 0, z = 0;
                    switch (args.get(0).asStr().trim().toLowerCase(java.util.Locale.ROOT)) {
                        case "x" -> x = omega;
                        case "z" -> z = omega;
                        default -> y = omega;
                    }
                    PhysicsWorld.setAngularVelocity(entity.state().id(), x, y, z);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // set_angular_velocity(x, y, z) — the same thing in radians per tick.
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
            .method("kill", (obj, args) -> {
                try {
                    var entity = ContraptionWorlds.entityOf(cl(obj)).orElse(null);
                    if (entity != null) { dev.arubik.craftengine.contraption.ContraptionKill.kill(entity); return ScriptValue.of(true); }
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
            // real_pos(x,y,z) → Vec3 in real-world coords
            .method("real_pos", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                net.minecraft.world.phys.Vec3 rp = cl(obj).realWorldPositionOf(
                    new net.minecraft.world.phys.Vec3(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                return VectorType.wrap(rp.x, rp.y, rp.z);
            })
            // real_block(lx,ly,lz) → BlockType at projected real-world pos
            .method("real_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                try {
                    net.minecraft.world.phys.Vec3 rp = cl(obj).realWorldPositionOf(
                        new net.minecraft.world.phys.Vec3(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                    if (cl(obj).realLevel() instanceof net.minecraft.server.level.ServerLevel rl)
                        return BlockType.wrap(rl, new BlockPos((int)rp.x, (int)rp.y, (int)rp.z));
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // get_block(lx,ly,lz) → BlockType at local contraption coords
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int)args.get(0).asNum(), y = (int)args.get(1).asNum(), z = (int)args.get(2).asNum();
                return BlockType.wrap((net.minecraft.server.level.ServerLevel)(Object)cl(obj), new BlockPos(x, y, z));
            })
            // blocks() → Array of BlockType for all local positions
            .method("blocks", (obj, args) -> {
                try {
                    java.util.Set<BlockPos> positions = cl(obj).localPositions();
                    net.minecraft.server.level.ServerLevel fakeLevel = (net.minecraft.server.level.ServerLevel)(Object)cl(obj);
                    java.util.List<ScriptValue> list = new java.util.ArrayList<>(positions.size());
                    for (BlockPos bp : positions) {
                        if (!fakeLevel.getBlockState(bp).isAir())
                            list.add(BlockType.wrap(fakeLevel, bp));
                    }
                    return new ScriptValue.Array(list);
                } catch (Throwable ignored) { return new ScriptValue.Array(java.util.List.of()); }
            })
            // entities() → Array of EntityType for entities inside contraption
            .method("entities", (obj, args) -> {
                try {
                    net.minecraft.server.level.ServerLevel fakeLevel = (net.minecraft.server.level.ServerLevel)(Object)cl(obj);
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
    }

    /** Null-safe: returns NULL if not a ContraptionLevel. */
    public static ScriptValue wrap(Object serverLevel) {
        if (serverLevel instanceof ContraptionLevel cl) return ScriptValue.ofObj("Contraption", cl);
        return ScriptValue.NULL;
    }

    private static ContraptionLevel cl(Object obj) { return (ContraptionLevel) obj; }
}
