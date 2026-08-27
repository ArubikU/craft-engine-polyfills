package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.ContraptionKill;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.type.MachineContraptionType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ContraptionManager — global singleton type.
 * In scripts: ContraptionManager.all_contraptions, ContraptionManager.get("uuid"), etc.
 *
 * Instance object is just a sentinel Object (singleton pattern).
 */
public final class ContraptionManagerType {

    /** Singleton sentinel instance */
    public static final Object INSTANCE = new Object();

    private ContraptionManagerType() {}

    public static void register() {
        PolyTypeRegistry.define("ContraptionManager")
            // --- Query ---
            .property("all_contraptions", obj -> {
                List<ScriptValue> result = new ArrayList<>();
                for (ContraptionEntity entity : ContraptionManager.all()) {
                    if (entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl)
                        result.add(ContraptionType.wrap(cl));
                }
                return new ScriptValue.Array(result);
            })
            .property("count", obj -> ScriptValue.of(ContraptionManager.all().size()))
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String idStr) -> {
                    try {
                        UUID id = UUID.fromString(idStr);
                        ContraptionEntity entity = ContraptionManager.get(id);
                        if (entity != null && entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl)
                            return ContraptionType.wrap(cl);
                    } catch (Throwable ignored) {}
                    return ScriptValue.NULL;
                })
            // --- Creation ---
            // create(world, x, y, z[, type]) — NOT migrated: the trailing `type` argument is
            // genuinely optional, checked via args.size() >= 5 inside the body — a typed handler
            // only receives its fixed-arity decoded arguments, not the original args list/size, so
            // that conditional read can't be expressed (same reasoning as ContraptionType's
            // teleport()/play_sound() NOT-migrated notes). Left untyped.
            .method("create", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.NULL;
                try {
                    ScriptValue worldVal = args.get(0);
                    if (!(worldVal instanceof ScriptValue.Obj wo) || !(wo.instance() instanceof ServerLevel level)) return ScriptValue.NULL;
                    int x = (int) args.get(1).asNum(), y = (int) args.get(2).asNum(), z = (int) args.get(3).asNum();
                    BlockPos pos = new BlockPos(x, y, z);
                    Key type = args.size() >= 5 ? Key.of(args.get(4).asStr()) : MachineContraptionType.KEY;
                    org.bukkit.World bukkit = ((CraftWorld) level.getWorld());
                    ContraptionEntity entity = ContraptionAssembler.assemble(bukkit, pos, type);
                    if (entity == null) return ScriptValue.NULL;
                    return entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl
                            ? ContraptionType.wrap(cl) : ScriptValue.NULL;
                } catch (Throwable e) { return ScriptValue.NULL; }
            })
            // create_at(block_value) — assemble at a Block script value
            // create_bearing(seed, pivot [, type]) — assemble the structure grown from `seed`,
            // pivoting on `pivot`, which is left standing in the world.
            //
            // create_at below uses one block as both the growth origin AND the anchor. For a
            // bearing those are different blocks: the structure it carries is in front of it, and
            // the bearing itself must stay put. Because the structure is found through the glue
            // graph, gluing a windmill to its bearing pulled the bearing into the contraption —
            // the block was then removed from the world, so the contraption was anchored to air,
            // never moved, and left the build looking like it had simply vanished.
            // NOT migrated: trailing `type` argument is optional (args.size() >= 3) — same reason
            // as create() above. Left untyped.
            .method("create_bearing", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.NULL;
                try {
                    ScriptValue sv = args.get(0);
                    ScriptValue pv = args.get(1);
                    if (!(sv instanceof ScriptValue.Obj so) || !(so.instance() instanceof BlockType.BlockRef seed))
                        return ScriptValue.NULL;
                    if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof BlockType.BlockRef pivot))
                        return ScriptValue.NULL;
                    Key type = args.size() >= 3 ? Key.of(args.get(2).asStr()) : MachineContraptionType.KEY;
                    org.bukkit.World bukkit = ((CraftWorld) seed.level().getWorld());
                    ContraptionEntity entity = ContraptionAssembler.assembleFrom(
                            bukkit, pivot.pos(), seed.pos(), type, 0.0, 0.0, null);
                    if (entity == null) return ScriptValue.NULL;
                    return entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl
                            ? ContraptionType.wrap(cl) : ScriptValue.NULL;
                } catch (Throwable e) { return ScriptValue.NULL; }
            })
            // NOT migrated: trailing `type` argument is optional (args.size() >= 2) — same reason
            // as create() above. Left untyped.
            .method("create_at", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                try {
                    ScriptValue bv = args.get(0);
                    if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef ref)) return ScriptValue.NULL;
                    Key type = args.size() >= 2 ? Key.of(args.get(1).asStr()) : MachineContraptionType.KEY;
                    org.bukkit.World bukkit = ((CraftWorld) ref.level().getWorld());
                    ContraptionEntity entity = ContraptionAssembler.assemble(bukkit, ref.pos(), type);
                    if (entity == null) return ScriptValue.NULL;
                    return entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl
                            ? ContraptionType.wrap(cl) : ScriptValue.NULL;
                } catch (Throwable e) { return ScriptValue.NULL; }
            })
            // --- Removal ---
            // kill(id) — hard force-remove: despawns displays, drops the manager/physics entries,
            // disposes the contraption level. Does NOT put its blocks back anywhere — for a
            // structure you genuinely want gone (an exploit cleanup, an admin command), not the
            // normal "player disassembles their machine" case, which wants disassemble() below.
            // `id` arg stays TypeCodecs.RAW: the body itself dispatches dynamically on whether the
            // passed ScriptValue is an Obj (a Contraption instance) or a Str (a uuid string) — same
            // reasoning as EntityType#teleport_to's RAW arg.
            .methodTyped1("kill", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue cv) -> {
                    try {
                        if (cv instanceof ScriptValue.Obj co && co.instance() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
                            var entity = ContraptionWorlds.entityOf(cl).orElse(null);
                            if (entity != null) { ContraptionKill.kill(entity); return true; }
                        } else if (cv instanceof ScriptValue.Str s) {
                            UUID id = UUID.fromString(s.value());
                            ContraptionEntity e = ContraptionManager.get(id);
                            if (e != null) { ContraptionKill.kill(e); return true; }
                        }
                    } catch (Throwable ignored) {}
                    return false;
                })
            // killAll() returns an int (count killed) — ScriptValue.of(int) widens to a Num
            // (double), not a Bool, so the return codec is TypeCodecs.DOUBLE, not BOOL.
            .methodTyped0("kill_all", TypeCodecs.DOUBLE, (Object obj) -> (double) ContraptionKill.killAll())
            // disassemble(id) — the REAL "return this structure to the world" operation Create-style
            // bearings use (same primitive the hammer-disassemble listener calls): restores every
            // block (rotation-snapped to the nearest quarter turn), glue edges, and furniture back
            // into the real world at their resting positions, THEN despawns/disposes the
            // contraption. `kill()` alone (what windmill_interact.pf used to call) only does the
            // despawn/dispose half — the blocks were never coming back, which is why disassembling
            // looked like nothing happened.
            // `id` arg stays TypeCodecs.RAW: same Obj-vs-Str dynamic dispatch as kill() above.
            .methodTyped1("disassemble", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue cv) -> {
                    try {
                        ContraptionEntity entity = null;
                        ServerLevel level = null;
                        if (cv instanceof ScriptValue.Obj co && co.instance() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
                            entity = ContraptionWorlds.entityOf(cl).orElse(null);
                            if (entity != null && cl.realLevel() instanceof ServerLevel rl) level = rl;
                        } else if (cv instanceof ScriptValue.Str s) {
                            UUID id = UUID.fromString(s.value());
                            entity = ContraptionManager.get(id);
                            if (entity != null && entity.state().level() != null
                                    && entity.state().level().realLevel() instanceof ServerLevel rl) level = rl;
                        }
                        if (entity == null || level == null) return false;
                        ContraptionAssembler.disassemble(level.getWorld(), entity);
                        return true;
                    } catch (Throwable ignored) {}
                    return false;
                });
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("ContraptionManager", INSTANCE);
    }
}
