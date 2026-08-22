package dev.arubik.craftengine.script.types.chainery;

import dev.arubik.craftengine.chainery.Chain;
import dev.arubik.craftengine.chainery.ChainEngine;
import dev.arubik.craftengine.chainery.ChainMaterial;
import dev.arubik.craftengine.chainery.ChainRegistry;
import dev.arubik.craftengine.chainery.ChaineryBlockBehavior;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
            .method("create_chain", (obj, args) -> {
                ServerLevel level = worldArg(args, 0);
                if (level == null || args.size() < 7) return ScriptValue.NULL;
                BlockPos a = new BlockPos((int) args.get(1).asNum(), (int) args.get(2).asNum(), (int) args.get(3).asNum());
                BlockPos b = new BlockPos((int) args.get(4).asNum(), (int) args.get(5).asNum(), (int) args.get(6).asNum());
                org.bukkit.World world = level.getWorld();
                String anchorId = args.size() > 7 && args.get(7) != ScriptValue.NULL && !args.get(7).asStr().isBlank() ? args.get(7).asStr() : ChainMaterial.DEFAULT.anchorBlock();
                String linkId = args.size() > 8 && args.get(8) != ScriptValue.NULL && !args.get(8).asStr().isBlank() ? args.get(8).asStr() : ChainMaterial.DEFAULT.linkItem();
                ChainMaterial mat = new ChainMaterial(anchorId, linkId, ChainMaterial.DEFAULT.maxBlocks(),
                    ChainMaterial.DEFAULT.stretch(), ChainMaterial.DEFAULT.maxTension(), ChainMaterial.DEFAULT.pull());
                if (!ensureAnchor(world, level, a, mat) || !ensureAnchor(world, level, b, mat)) return ScriptValue.NULL;
                int blocks = Math.max(1, (int) Math.round(Math.sqrt(a.distSqr(b))));
                if (blocks > mat.maxBlocks()) return ScriptValue.NULL;
                Chain chain = ChainEngine.create(world, a, b, null, null, mat, blocks);
                return ChainType.wrap(chain);
            })
            // break_chain(id, drop_items?) -> bool
            .method("break_chain", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                Chain chain = lookup(args.get(0).asStr());
                if (chain == null) return ScriptValue.of(false);
                boolean drop = args.size() > 1 && args.get(1).asBool();
                ChainEngine.breakChain(chain, drop);
                return ScriptValue.of(true);
            })
            // get(id) -> Chain or null
            .method("get", (obj, args) ->
                args.isEmpty() ? ScriptValue.NULL : ChainType.wrap(lookup(args.get(0).asStr())))
            // chain_at(World, x, y, z) -> Chain or null (any one chain anchored there)
            .method("chain_at", (obj, args) -> {
                ServerLevel level = worldArg(args, 0);
                if (level == null || args.size() < 4) return ScriptValue.NULL;
                BlockPos pos = new BlockPos((int) args.get(1).asNum(), (int) args.get(2).asNum(), (int) args.get(3).asNum());
                return ChainType.wrap(ChainRegistry.at(level.getWorld().getUID(), pos));
            })
            // chains_at(World, x, y, z) -> Array<Chain> (every chain anchored there — an anchor can host several)
            .method("chains_at", (obj, args) -> {
                ServerLevel level = worldArg(args, 0);
                if (level == null || args.size() < 4) return new ScriptValue.Array(List.of());
                BlockPos pos = new BlockPos((int) args.get(1).asNum(), (int) args.get(2).asNum(), (int) args.get(3).asNum());
                List<ScriptValue> out = new ArrayList<>();
                for (Chain c : ChainRegistry.chainsAt(level.getWorld().getUID(), pos)) out.add(ChainType.wrap(c));
                return new ScriptValue.Array(out);
            })
            // count_at(World, x, y, z) -> int
            .method("count_at", (obj, args) -> {
                ServerLevel level = worldArg(args, 0);
                if (level == null || args.size() < 4) return ScriptValue.of(0);
                BlockPos pos = new BlockPos((int) args.get(1).asNum(), (int) args.get(2).asNum(), (int) args.get(3).asNum());
                return ScriptValue.of(ChainRegistry.countAt(level.getWorld().getUID(), pos));
            })
            // all() -> Array<Chain> — every live chain, any world
            .method("all", (obj, args) -> {
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

    private static ServerLevel worldArg(List<ScriptValue> args, int i) {
        if (i >= args.size()) return null;
        return args.get(i) instanceof ScriptValue.Obj o && o.instance() instanceof ServerLevel lvl ? lvl : null;
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
