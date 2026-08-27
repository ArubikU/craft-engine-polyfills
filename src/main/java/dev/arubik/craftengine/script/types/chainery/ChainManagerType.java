package dev.arubik.craftengine.script.types.chainery;

import dev.arubik.craftengine.chainery.Chain;
import dev.arubik.craftengine.chainery.ChainEngine;
import dev.arubik.craftengine.chainery.ChainMaterial;
import dev.arubik.craftengine.chainery.ChainRegistry;
import dev.arubik.craftengine.chainery.ChaineryBlockBehavior;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.util.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * "ChainManager" — script singleton over the CHAINERY engine ({@link ChainEngine} / {@link
 * ChainRegistry}), the script-side counterpart of {@code ContraptionManager}/{@code Server}. Lets a
 * {@code .pf} script create, break, and query chains directly instead of only through the physical
 * click-two-points item flow — e.g. a motor/mechanism script wiring up a custom direct chain link.
 */
public final class ChainManagerType {

    public static final Object INSTANCE = new Object();

    private ChainManagerType() {}

    public static void register() {
        PolyTypeRegistry.define("ChainManager")
            // create_chain(World, ax, ay, az, bx, by, bz, anchor_block_id?, link_item_id?) -> Chain.
            // Places the anchor block (default cml:chain_anchor, or anchor_block_id if given) at either
            // endpoint that isn't already one, then registers the span — mirrors ChaineryItemBehavior's
            // click-two-points flow minus the inventory/item cost. link_item_id only affects what the
            // rope renders as / drops on break — pass it whenever anchor_block_id is a real, configured
            // id (the hardcoded ChainMaterial.DEFAULT ids aren't configured in every pack).
            // Migrated to methodTypedOpt9 (9 slots: World + 6 coords + 2 optional trailing ids).
            // Every slot defaults to null, which is the "argument absent" sentinel — no codec used
            // here can decode a PRESENT argument to Java null (DOUBLE is primitive-backed, RAW is
            // identity over an args element). Since arguments are positional, null-checking the LAST
            // required slot (bz, index 6) is exactly the original's `args.size() < 7`, and it is
            // re-checked at the top of the body before any block is placed.
            //   * The World slot stays RAW and is decoded by worldOf(...) inside the body — that
            //     helper does real unwrapping (ScriptValue.Obj -> ServerLevel), not a cast, and no
            //     scalar codec can express it. Replaces the old worldArg(args, 0), now removed.
            //   * The two trailing id slots also stay RAW, NOT STRING: the original tested
            //     `args.get(i) != ScriptValue.NULL` before taking .asStr(), and asStr() on a NULL
            //     ScriptValue yields the literal string "null" (non-blank!) — a STRING slot would
            //     therefore accept an explicitly-passed null as a real anchor/link id. `arg != null`
            //     reproduces args.size() > i; the ScriptValue.NULL/isBlank tests are kept verbatim.
            .methodTypedOpt9("create_chain",
                TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null, TypeCodecs.DOUBLE, (Double) null,
                TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.RAW, (ScriptValue) null,
                TypeCodecs.RAW,
                (Object obj, ScriptValue worldVal, Double ax, Double ay, Double az,
                 Double bx, Double by, Double bz, ScriptValue anchorArg, ScriptValue linkArg) -> {
                ServerLevel level = worldOf(worldVal);
                if (level == null || bz == null) return ScriptValue.NULL;
                BlockPos a = new BlockPos((int) (double) ax, (int) (double) ay, (int) (double) az);
                BlockPos b = new BlockPos((int) (double) bx, (int) (double) by, (int) (double) bz);
                org.bukkit.World world = level.getWorld();
                String anchorId = anchorArg != null && anchorArg != ScriptValue.NULL && !anchorArg.asStr().isBlank() ? anchorArg.asStr() : ChainMaterial.DEFAULT.anchorBlock();
                String linkId = linkArg != null && linkArg != ScriptValue.NULL && !linkArg.asStr().isBlank() ? linkArg.asStr() : ChainMaterial.DEFAULT.linkItem();
                ChainMaterial mat = new ChainMaterial(anchorId, linkId, ChainMaterial.DEFAULT.maxBlocks(),
                    ChainMaterial.DEFAULT.stretch(), ChainMaterial.DEFAULT.maxTension(), ChainMaterial.DEFAULT.pull());
                if (!ensureAnchor(world, level, a, mat) || !ensureAnchor(world, level, b, mat)) return ScriptValue.NULL;
                int blocks = Math.max(1, (int) Math.round(Math.sqrt(a.distSqr(b))));
                if (blocks > mat.maxBlocks()) return ScriptValue.NULL;
                Chain chain = ChainEngine.create(world, a, b, null, null, mat, blocks);
                return ChainType.wrap(chain);
                })
            // break_chain(id, drop_items?) -> bool
            // methodTypedOpt2: drop_items defaults to false when absent (exactly what
            // `args.size() > 1 && args.get(1).asBool()` meant). `id` gets an "" default rather than
            // an onMissingArgs short-circuit, which reproduces the old no-args behaviour exactly:
            // lookup("") can't parse as a UUID, so it returns null and the body returns false
            // without touching anything — lookup is pure, so running it costs nothing observable.
            .methodTypedOpt2("break_chain", TypeCodecs.STRING, "", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (Object obj, String id, Boolean drop) -> {
                    Chain chain = lookup(id);
                    if (chain == null) return false;
                    ChainEngine.breakChain(chain, drop);
                    return true;
                })
            // get(id) -> Chain or null
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String id) -> ChainType.wrap(lookup(id)))
            // chain_at(World, x, y, z) -> Chain or null (any one chain anchored there). The World
            // arg stays TypeCodecs.RAW and is decoded the same way worldArg(args, i) does — a plain
            // STRING/DOUBLE/BOOL codec can't express "unwrap a ScriptValue.Obj holding a
            // ServerLevel, or null" — decode logic is inlined below rather than kept as a
            // List<ScriptValue>-based helper, since a typed handler no longer has the raw args list.
            .methodTyped4("chain_at", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    ServerLevel level = worldOf(worldVal);
                    if (level == null) return ScriptValue.NULL;
                    BlockPos pos = new BlockPos((int) (double) x, (int) (double) y, (int) (double) z);
                    return ChainType.wrap(ChainRegistry.at(level.getWorld().getUID(), pos));
                })
            // chains_at(World, x, y, z) -> Array<Chain> (every chain anchored there — an anchor can host several)
            .methodTyped4("chains_at", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.RAW, new ScriptValue.Array(List.of()),
                (Object obj, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    ServerLevel level = worldOf(worldVal);
                    if (level == null) return new ScriptValue.Array(List.of());
                    BlockPos pos = new BlockPos((int) (double) x, (int) (double) y, (int) (double) z);
                    List<ScriptValue> out = new ArrayList<>();
                    for (Chain c : ChainRegistry.chainsAt(level.getWorld().getUID(), pos)) out.add(ChainType.wrap(c));
                    return new ScriptValue.Array(out);
                })
            // count_at(World, x, y, z) -> int
            .methodTyped4("count_at", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.DOUBLE, 0.0,
                (Object obj, ScriptValue worldVal, Double x, Double y, Double z) -> {
                    ServerLevel level = worldOf(worldVal);
                    if (level == null) return 0.0;
                    BlockPos pos = new BlockPos((int) (double) x, (int) (double) y, (int) (double) z);
                    return (double) ChainRegistry.countAt(level.getWorld().getUID(), pos);
                })
            // all() -> Array<Chain> — every live chain, any world
            .methodTyped0("all", TypeCodecs.RAW,
                (Object obj) -> {
                    List<ScriptValue> out = new ArrayList<>();
                    for (Chain c : ChainRegistry.all()) out.add(ChainType.wrap(c));
                    return new ScriptValue.Array(out);
                });
    }

    private static Chain lookup(String idStr) {
        try {
            return ChainRegistry.get(UUID.fromString(idStr));
        } catch (Throwable t) {
            return null;
        }
    }

    /** Decodes a wrapped World script value to a ServerLevel — real unwrapping, not a cast, which
     *  is why every World argument above stays a TypeCodecs.RAW slot and passes through here inside
     *  the handler rather than being decoded by a codec. (Replaced the old List-based
     *  worldArg(args, i), which had no raw args list left to read once create_chain became typed.) */
    private static ServerLevel worldOf(ScriptValue value) {
        return value instanceof ScriptValue.Obj o && o.instance() instanceof ServerLevel lvl ? lvl : null;
    }

    /** Places {@code mat}'s anchor block at {@code pos} unless one is already there. */
    private static boolean ensureAnchor(org.bukkit.World world, ServerLevel level, BlockPos pos, ChainMaterial mat) {
        if (ChaineryBlockBehavior.getAt(level, pos) != null) return true;
        BlockDefinition def = CraftEngineBlocks.byId(Key.of(mat.anchorBlock()));
        if (def == null) return false;
        try {
            org.bukkit.Location loc = new org.bukkit.Location(world, pos.getX(), pos.getY(), pos.getZ());
            return CraftEngineBlocks.place(loc, def.defaultState(), UpdateFlags.UPDATE_ALL, false);
        } catch (Throwable t) {
            return false;
        }
    }

    public static ScriptValue wrap() {
        return ScriptValue.ofObj("ChainManager", INSTANCE);
    }
}
