package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.ContraptionKill;
import dev.arubik.craftengine.contraption.ContraptionWorlds;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.type.MachineContraptionType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
            .method("get", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                try {
                    UUID id = UUID.fromString(args.get(0).asStr());
                    ContraptionEntity entity = ContraptionManager.get(id);
                    if (entity != null && entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl)
                        return ContraptionType.wrap(cl);
                } catch (Throwable ignored) {}
                return ScriptValue.NULL;
            })
            // --- Creation ---
            // create(world, x, y, z) — assemble a MachineContraptionType at given block pos
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
            .method("kill", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    ScriptValue cv = args.get(0);
                    if (cv instanceof ScriptValue.Obj co && co.instance() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
                        var entity = ContraptionWorlds.entityOf(cl).orElse(null);
                        if (entity != null) { ContraptionKill.kill(entity); return ScriptValue.of(true); }
                    } else if (cv instanceof ScriptValue.Str s) {
                        UUID id = UUID.fromString(s.value());
                        ContraptionEntity e = ContraptionManager.get(id);
                        if (e != null) { ContraptionKill.kill(e); return ScriptValue.of(true); }
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            .method("kill_all", (obj, args) -> ScriptValue.of(ContraptionKill.killAll()))
            // disassemble(id) — the REAL "return this structure to the world" operation Create-style
            // bearings use (same primitive the hammer-disassemble listener calls): restores every
            // block (rotation-snapped to the nearest quarter turn), glue edges, and furniture back
            // into the real world at their resting positions, THEN despawns/disposes the
            // contraption. `kill()` alone (what windmill_interact.pf used to call) only does the
            // despawn/dispose half — the blocks were never coming back, which is why disassembling
            // looked like nothing happened.
            .method("disassemble", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    ScriptValue cv = args.get(0);
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
                    if (entity == null || level == null) return ScriptValue.of(false);
                    ContraptionAssembler.disassemble(level.getWorld(), entity);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            });
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("ContraptionManager", INSTANCE);
    }
}
