package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * "Glue" singleton — reads the super-glue graph from scripts, as {@code Glue}.
 *
 * <p>Glue decides what a bearing carries: {@code ContraptionAssembler} grows a structure by walking
 * this graph. Until now a script had no way to look at it, so the only way to find out what a
 * bearing WOULD pick up was to assemble it and see. That is exactly what the windmill did — it
 * assembled, counted the sails it got, and killed the contraption when there were too few, which
 * removes the blocks from the world before the check ever runs. Being able to ask first is what
 * makes a non-destructive precondition possible.
 *
 * <pre>
 *   if !Glue.is_glued(block) { ... }                  # nothing attached to it
 *   for b in Glue.structure(block) { ... }            # every block glued to it, transitively
 *   count = Glue.size(block)
 * </pre>
 */
public final class GlueType {

    /** Singleton sentinel instance — see {@code ContraptionManagerType.INSTANCE} for the same
     *  pattern. Every method below reads its real target from an explicit block argument, not
     *  from this object, so any non-null instance works; it exists only so {@code Glue.*(...)}
     *  has something to resolve {@code Glue} to. */
    public static final Object INSTANCE = new Object();

    private GlueType() {}

    /** The block a script argument refers to, or null. */
    private static BlockType.BlockRef blockRef(ScriptValue v) {
        return v instanceof ScriptValue.Obj o && o.instance() instanceof BlockType.BlockRef r ? r : null;
    }

    @SuppressWarnings("unchecked")
    private static Set<BlockPos> structure(BlockType.BlockRef r) {
        ServerLevel level = r.level();
        return GlueRegistry.structureAt((ResourceKey<Level>) level.dimension(), r.pos());
    }

    private static boolean glued(BlockType.BlockRef r) {
        ServerLevel level = r.level();
        @SuppressWarnings("unchecked")
        ResourceKey<Level> id = (ResourceKey<Level>) level.dimension();
        return GlueRegistry.graphFor(id).hasNode(r.pos());
    }

    public static void register() {
        PolyTypeRegistry.define("Glue")
            // The instance (obj/Object below) is the Glue singleton sentinel and is never read —
            // every method resolves its real target from the block argument instead (see class
            // javadoc). Argument codec is RAW: blockRef(...) does more than a plain asXxx()
            // coercion (it type-checks/unwraps a ScriptValue.Obj into a BlockType.BlockRef), so the
            // helper is still called explicitly inside each body per rule 2. The "missing arg ->
            // null" branch each body used to hand-check is now the framework's own onMissingArgs
            // short-circuit (arity 1, false/empty-Array/0.0 respectively) — a wrong-TYPE argument
            // (present but not a BlockRef) still falls through to blockRef(...) returning null
            // exactly as before.
            /** Is anything glued to this block? A lone block is not a structure. */
            .methodTyped1("is_glued", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue arg) -> {
                    BlockType.BlockRef r = blockRef(arg);
                    return r != null && glued(r);
                })
            /**
             * Every block glued to this one, transitively — the exact set a bearing would carry.
             * An unglued block yields just itself, matching GlueRegistry.structureAt.
             */
            // Return codec declares the element type: every element is a Block, so the handler
            // returns the real List<BlockType.BlockRef> and the codec does the ofObj("Block", ...)
            // wrapping BlockType.wrap used to do by hand (level/pos are both non-null here, so
            // wrap's NULL branch was unreachable and the encoding is identical).
            .methodTyped1("structure", TypeCodecs.RAW,
                TypeCodecs.listOf("Block", BlockType.BlockRef.class), List.of(),
                (Object obj, ScriptValue arg) -> {
                    BlockType.BlockRef r = blockRef(arg);
                    List<BlockType.BlockRef> out = new ArrayList<>();
                    if (r == null) return out;
                    for (BlockPos p : structure(r)) out.add(new BlockType.BlockRef(r.level(), p));
                    return out;
                })
            /** How many blocks that structure holds. Cheaper than materialising it. */
            .methodTyped1("size", TypeCodecs.RAW, TypeCodecs.DOUBLE, 0.0,
                (Object obj, ScriptValue arg) -> {
                    BlockType.BlockRef r = blockRef(arg);
                    return r == null ? 0.0 : (double) structure(r).size();
                })
            /**
             * How many blocks in the structure have an id containing {@code needle} — the count a
             * bearing needs before deciding whether assembling is worth it.
             */
            .methodTyped2("count", TypeCodecs.RAW, TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (Object obj, ScriptValue blockArg, String needle) -> {
                    BlockType.BlockRef r = blockRef(blockArg);
                    if (r == null) return 0.0;
                    int n = 0;
                    for (BlockPos p : structure(r)) {
                        String id = BlockType.customBlockId(r.level().getBlockState(p));
                        if (id == null) {
                            id = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                                    .getKey(r.level().getBlockState(p).getBlock()).toString();
                        }
                        if (id.contains(needle)) n++;
                    }
                    return (double) n;
                });
    }
}
