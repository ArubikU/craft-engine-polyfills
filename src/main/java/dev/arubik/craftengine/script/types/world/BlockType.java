package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.primitive.MapType;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Block type — represents a block state at a position.
 * Instance object: BlockRef record holding level + pos.
 */
public final class BlockType {

    public record BlockRef(ServerLevel level, BlockPos pos) {
        public BlockState state() { return level.getBlockState(pos); }
    }

    private BlockType() {}

    /** Per-(dimension, position, property-name) cache for {@link #readProperty}, valid only within
     *  the SAME server tick it was computed on — a machine's own action script and its renderer
     *  entries commonly read the exact same {@code Machine.block.property(...)}/{@code
     *  Block.property(...)} several times within one tick (once for a "when" gate, again for a
     *  value formula, again from a helper .pf function...), and {@code readProperty} itself isn't
     *  free (CE-custom-state-first, vanilla-fallback, a linear property scan). Bounded (simple LRU
     *  via access-order {@link LinkedHashMap}) so this can't grow unbounded over server uptime as
     *  machines are placed/broken at ever-changing positions. Only ever touched from the main
     *  server thread (block state / script evaluation is never concurrent here), so a plain
     *  {@code LinkedHashMap} is safe — no need for a concurrent map. */
    private record PropCacheKey(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim, long pos, String prop) {}
    private record PropCacheEntry(int tick, String value) {}
    private static final int PROP_CACHE_MAX = 2048;
    private static final java.util.LinkedHashMap<PropCacheKey, PropCacheEntry> PROPERTY_CACHE =
        new java.util.LinkedHashMap<>(256, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<PropCacheKey, PropCacheEntry> eldest) {
                return size() > PROP_CACHE_MAX;
            }
        };

    /** Same as {@link #readProperty(BlockState, String)} but cached per-tick against a known
     *  level+pos — see {@link #PROPERTY_CACHE}'s own doc for why. Falls back to the uncached read
     *  (still correct, just not deduplicated) if the current tick can't be determined. */
    public static String readPropertyThisTick(ServerLevel level, BlockPos pos, BlockState bs, String propName) {
        if (level == null || pos == null) return readProperty(bs, propName);
        int tick;
        try { tick = level.getServer().getTickCount(); } catch (Throwable ignored) { return readProperty(bs, propName); }
        PropCacheKey key = new PropCacheKey(level.dimension(), pos.asLong(), propName);
        PropCacheEntry cached = PROPERTY_CACHE.get(key);
        if (cached != null && cached.tick() == tick) return cached.value();
        String val = readProperty(bs, propName);
        PROPERTY_CACHE.put(key, new PropCacheEntry(tick, val));
        return val;
    }

    /** Called by every in-tick write path (set_property, cycle_prop) right after mutating the
     *  block — writes the FRESH value straight into {@link #PROPERTY_CACHE} (stamped with the
     *  current tick) instead of merely dropping the stale entry, so a same-tick read right after
     *  a write is still a cache hit instead of paying for a recompute. */
    private static void overwritePropertyCache(ServerLevel level, BlockPos pos, String propName, BlockState newState) {
        if (level == null || pos == null) return;
        int tick;
        try { tick = level.getServer().getTickCount(); } catch (Throwable ignored) { return; }
        PropCacheKey key = new PropCacheKey(level.dimension(), pos.asLong(), propName);
        PROPERTY_CACHE.put(key, new PropCacheEntry(tick, readProperty(newState, propName)));
    }

    /** Awards vanilla's normal block-break XP orb for a {@link net.minecraft.world.level.block.DropExperienceBlock}
     *  (ores, etc.) — no-op for anything else. Shared by every block-breaking entry point
     *  (break_and_drop here, Machine.tick_break's progressive break) so they can't drift out of
     *  sync on which blocks award XP. ItemStack.EMPTY as the "tool" is fine: DropExperienceBlock's
     *  own getExpDrop only reads the tool to check for Silk Touch, and a script-driven break has no
     *  Silk-Touch-enchanted tool to honor anyway (same assumption Block.getDrops itself makes by
     *  passing no tool either). */
    public static void spawnExpDrop(ServerLevel level, BlockPos pos, BlockState state) {
        try {
            if (state.getBlock() instanceof net.minecraft.world.level.block.DropExperienceBlock deb) {
                int xp = deb.getExpDrop(state, level, pos, net.minecraft.world.item.ItemStack.EMPTY, true);
                if (xp > 0) {
                    net.minecraft.world.entity.ExperienceOrb.award(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), xp);
                }
            }
        } catch (Throwable ignored) {}
    }

    public static void register() {
        PolyTypeRegistry.define("Block")
            .property("id", obj -> {
                BlockState bs = ref(obj).state();
                // A CraftEngine custom block reports its CE id ("cml:crate_acacia"), not the
                // vanilla block it is backed by. The backing id is useless for identification:
                // every CE block sharing a base block would compare equal to every other.
                String ce = customBlockId(bs);
                if (ce != null) return ScriptValue.of(ce);
                return ScriptValue.of(BuiltInRegistries.BLOCK.getKey(bs.getBlock()).toString());
            })
            /** The vanilla block backing this position, regardless of any CE block on top of it. */
            .property("vanilla_id", obj -> ScriptValue.of(
                    BuiltInRegistries.BLOCK.getKey(ref(obj).state().getBlock()).toString()))
            .property("is_custom", obj -> ScriptValue.of(customBlockId(ref(obj).state()) != null))
            /** Whether super glue attaches this block to anything — see the Glue singleton. */
            .property("is_glued", obj -> {
                try {
                    BlockRef r = ref(obj);
                    return ScriptValue.of(dev.arubik.craftengine.contraption.glue.GlueRegistry
                            .graphFor(r.level().dimension()).hasNode(r.pos()));
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            /** Every block glued to this one — what a bearing anchored here would carry. */
            .method("glue_structure", (obj, args) -> {
                java.util.List<ScriptValue> out = new java.util.ArrayList<>();
                try {
                    BlockRef r = ref(obj);
                    for (net.minecraft.core.BlockPos p : dev.arubik.craftengine.contraption.glue.GlueRegistry
                            .structureAt(r.level().dimension(), r.pos())) {
                        out.add(wrap(r.level(), p));
                    }
                } catch (Throwable ignored) {}
                return new ScriptValue.Array(out);
            })
            .property("is_air", obj -> ScriptValue.of(ref(obj).state().isAir()))
            // Whether this block is a storage endpoint (vanilla container OR a custom block
            // exposing WorldlyContainerHolder) as opposed to another pipe segment or plain air —
            // lets a pipe's own panel script tell "this face touches a chest" from "this face
            // touches another pipe" (see item_pipe_panel.pf's cycle_mode: pipe-to-pipe should only
            // ever be Disabled/Both, the Input/Output split only means something toward a container).
            .property("is_container", obj -> {
                BlockRef r = ref(obj);
                return ScriptValue.of(dev.arubik.craftengine.pipe.item.ItemTransferHelper
                        .getContainer(r.level(), r.pos()).isPresent());
            })
            .property("hardness", obj -> {
                BlockRef r = ref(obj);
                return ScriptValue.of(r.state().getDestroySpeed(r.level(), r.pos()));
            })
            .property("light_level", obj -> ScriptValue.of(ref(obj).state().getLightEmission()))
            // The biome id AT this position ("minecraft:plains", ...) — a position-dependent
            // lookup, so it lives here (where a level+pos are already on hand) rather than on
            // Registry.biomes, which is deliberately just an id list with no coordinate concept.
            .property("biome", obj -> {
                BlockRef r = ref(obj);
                try {
                    var holder = r.level().getBiome(r.pos());
                    return ScriptValue.of(holder.unwrapKey()
                            .map(k -> k.identifier().toString())
                            .orElse("unknown"));
                } catch (Throwable ignored) { return ScriptValue.of("unknown"); }
            })
            .property("pos", obj -> {
                BlockPos p = ref(obj).pos();
                return VectorType.wrap(p.getX(), p.getY(), p.getZ());
            })
            .property("redstone", obj -> {
                BlockRef r = ref(obj);
                return ScriptValue.of(r.level().getBestNeighborSignal(r.pos()));
            })
            .property("powered", obj -> {
                BlockRef r = ref(obj);
                return ScriptValue.of(r.level().getBestNeighborSignal(r.pos()) > 0);
            })
            // get_metadata — the real block entity at this position (sign text, container
            // contents, skull owner, banner pattern, ...) as a type-appropriate script object,
            // e.g. block.get_metadata.front.lines[0]. NULL when there's no block entity here,
            // or its kind isn't modeled — see BlockMetadataType.
            .property("get_metadata", obj -> BlockMetadataType.metadataOf(ref(obj).level(), ref(obj).pos()))
            // metadata_type — which single BlockMetadata child type get_metadata would return
            // (e.g. "SignMetadata"), or NULL — for branching on a block's kind without paying for
            // the full metadata object first.
            .property("metadata_type", obj -> BlockMetadataType.metadataTypeOf(ref(obj).level(), ref(obj).pos()))
            // metadata_types — every modeled type name applicable to this block, most specific
            // first, as an Array (empty when nothing is modeled for it).
            .property("metadata_types", obj -> BlockMetadataType.metadataTypesOf(ref(obj).level(), ref(obj).pos()))
            // machine — this block wrapped as a "Machine" (see MachineType), for a script that
            // found some OTHER machine block (via real_block, block_at, facing_block, ...) and
            // wants to read/write its typed data or call its Machine.* methods directly. NULL if
            // there's no data-driven machine block entity actually here.
            .property("machine", obj -> {
                BlockRef r = ref(obj);
                var be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                if (be != null && be.controller instanceof dev.arubik.craftengine.block.entity.PersistentBlockEntity pbe) {
                    return dev.arubik.craftengine.script.types.machine.MachineType.wrap(r.level(), r.pos(), "north", pbe);
                }
                return ScriptValue.NULL;
            })
            .property("world", obj -> WorldType.wrap(ref(obj).level()))
            .property("location", obj -> LocationType.wrap(ref(obj).level(), ref(obj).pos().getX(), ref(obj).pos().getY(), ref(obj).pos().getZ()))
            // block_state — full BlockState wrapper (CE + vanilla properties, CE priority)
            .property("block_state", obj -> BlockStateType.wrap(ref(obj).state()))
            // CE-aware (readProperty checks the CraftEngine custom block state FIRST, falling back
            // to vanilla) — a disguised custom block's OWN properties (e.g. the saw's "facing"/
            // "face") live on its ImmutableBlockState, not the raw NMS state its disguise reports
            // (mangrove_leaves[distance=...] for the saw), so reading bs.getProperties() directly
            // here always returned NULL for exactly the properties a script most wants to read on a
            // custom block. Previously only correct by coincidence for a genuinely-vanilla block
            // (real leaves' own "distance", as used by the saw's tree-search).
            // set_property(name, value) — CE-aware (writeProperty tries vanilla first, then the
            // CraftEngine custom block state, same as property()'s own CE-first read). If this
            // position's block entity is a machine with an on_property_change hook configured, that
            // hook fires afterward — this is the ONE place a property write happens for every caller
            // (the old Machine.set_state was a near-duplicate of this exact mutation and has been
            // removed; Machine inherits this method from Block instead, per Block.ref()'s MachineRef
            // conversion, so Machine.set_property(...) still works, it just isn't a separate
            // implementation anymore). Named to match property()/has_property()/cycle_prop() — the
            // old "state" family (get_state/set_state) has been fully retired in favour of this one.
            .method("set_property", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                String propName = args.get(0).asStr();
                String value = args.get(1).asStr();
                BlockRef r = ref(obj);
                BlockState oldState = r.state();
                BlockState newState = writeProperty(oldState, propName, value);
                if (newState == null) return ScriptValue.of(false);
                r.level().setBlock(r.pos(), newState, 3);
                // Overwrite (not just invalidate) PROPERTY_CACHE's entry for this exact key with
                // the FRESH post-write value, stamped with the current tick — otherwise a same-tick
                // property(name) read right after this write would either see the STALE pre-write
                // value (if left untouched) or pay for a full recompute on its next read (if merely
                // removed); writing the known-correct value straight through keeps that next read a
                // cache hit too.
                overwritePropertyCache(r.level(), r.pos(), propName, newState);
                fireOnPropertyChange(r, oldState, newState);
                return ScriptValue.of(true);
            })
            // property(name) — reads CE custom OR vanilla block state property. Cached within the
            // SAME server tick (see PROPERTY_CACHE/readPropertyThisTick) — a machine's own action
            // script and its renderer entries commonly read the exact same property several times
            // per tick.
            .method("property", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                BlockRef r = ref(obj);
                String val = readPropertyThisTick(r.level(), r.pos(), r.state(), args.get(0).asStr());
                return val != null ? ScriptValue.of(val) : ScriptValue.NULL;
            })
            // has_property(name) — checks both CE and vanilla property systems (same per-tick cache)
            .method("has_property", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                BlockRef r = ref(obj);
                return ScriptValue.of(readPropertyThisTick(r.level(), r.pos(), r.state(), args.get(0).asStr()) != null);
            })
            // cycle_prop(name) — advance property to its next possible value
            .method("cycle_prop", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                String propName = args.get(0).asStr();
                BlockRef r = ref(obj);
                BlockState bs = r.state();
                for (Property<?> prop : bs.getProperties()) {
                    if (prop.getName().equals(propName)) {
                        BlockState newState = cycleProp(bs, prop);
                        if (newState != null) {
                            r.level().setBlock(r.pos(), newState, 3);
                            overwritePropertyCache(r.level(), r.pos(), propName, newState);
                            return ScriptValue.of(true);
                        }
                    }
                }
                return ScriptValue.of(false);
            })
            // combined_light — max of block+sky light at this position
            .property("combined_light", obj -> ScriptValue.of(ref(obj).level().getMaxLocalRawBrightness(ref(obj).pos())))
            .property("block_light",    obj -> ScriptValue.of(ref(obj).level().getBrightness(LightLayer.BLOCK, ref(obj).pos())))
            .property("sky_light",      obj -> ScriptValue.of(ref(obj).level().getBrightness(LightLayer.SKY, ref(obj).pos())))
            // apply_bone_meal() → bool — grows the plant/crop
            .method("apply_bone_meal", (obj, args) -> {
                BlockRef r = ref(obj);
                try {
                    BlockState bs = r.state();
                    if (bs.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock g
                            && g.isValidBonemealTarget(r.level(), r.pos(), bs)) {
                        g.performBonemeal((net.minecraft.server.level.ServerLevel) r.level(), r.level().random, r.pos(), bs);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // break_and_drop() → Array<Item> — breaks block, awards its normal XP orb (if any), and
            // returns drops. The one true "break this block" implementation — Machine.break_block
            // used to hand-roll a near-identical copy (missing the XP award) for breaking a block
            // OTHER than the machine's own; since the caller already holds that Block value (from
            // Machine.facing_block, World.get_block, ...), it just calls this directly now.
            .method("break_and_drop", (obj, args) -> {
                BlockRef r = ref(obj);
                try {
                    BlockState bs = r.state();
                    if (bs.isAir()) return new ScriptValue.Array(java.util.List.of());
                    var drops = net.minecraft.world.level.block.Block.getDrops(bs, r.level(), r.pos(), r.level().getBlockEntity(r.pos()));
                    r.level().destroyBlock(r.pos(), false);
                    spawnExpDrop(r.level(), r.pos(), bs);
                    java.util.List<ScriptValue> result = new java.util.ArrayList<>(drops.size());
                    for (var drop : drops) result.add(ScriptValue.ofItem(drop));
                    return new ScriptValue.Array(result);
                } catch (Throwable e) { return new ScriptValue.Array(java.util.List.of()); }
            })
            // place_from_item(item) → bool — places the block backing `item` (must be a BlockItem)
            // here IF this position is currently air, shrinking `item` by 1 on success. The item-
            // driven counterpart to replace(id): replace() unconditionally overwrites with a bare
            // block id, this only ever fills empty space and consumes the placed item, matching what
            // a dispenser/placer machine actually needs.
            .method("place_from_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                BlockRef r = ref(obj);
                if (!r.state().isAir()) return ScriptValue.of(false);
                ScriptValue itemArg = args.get(0);
                net.minecraft.world.item.ItemStack stack;
                if (itemArg instanceof ScriptValue.Item i) stack = i.stack();
                else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.item.ItemStack is) stack = is;
                else return ScriptValue.of(false);
                if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.BlockItem bi)) return ScriptValue.of(false);
                r.level().setBlock(r.pos(), bi.getBlock().defaultBlockState(), 3);
                stack.shrink(1);
                return ScriptValue.of(true);
            })
            // play_sound(soundId, vol?, pitch?)
            // --- Facing / direction methods (CE + vanilla aware) ---
            // facing(fallback?) — reads "facing" from CE custom OR vanilla block state
            .method("facing", (obj, args) -> {
                String val = readProperty(ref(obj).state(), "facing");
                if (val == null) val = readProperty(ref(obj).state(), "horizontal_facing");
                if (val != null) return ScriptValue.of(val);
                return args.isEmpty() ? ScriptValue.NULL : args.get(0);
            })
            // relative(direction) — block adjacent in given direction ("north","south","east","west","up","down")
            .method("relative", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                BlockRef r = ref(obj);
                net.minecraft.core.Direction dir = net.minecraft.core.Direction.byName(args.get(0).asStr());
                if (dir == null) return ScriptValue.NULL;
                return wrap(r.level(), r.pos().relative(dir));
            })
            // offset(dx,dy,dz) — block at relative offset
            .method("offset", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                BlockRef r = ref(obj);
                int dx = (int) args.get(0).asNum(), dy = (int) args.get(1).asNum(), dz = (int) args.get(2).asNum();
                return wrap(r.level(), r.pos().offset(dx, dy, dz));
            })
            // facing_block — block this block faces, using CE or vanilla "facing" property
            .property("facing_block", obj -> {
                BlockRef r = ref(obj);
                net.minecraft.core.Direction dir = readFacingDirection(r.state());
                return dir != null ? wrap(r.level(), r.pos().relative(dir)) : ScriptValue.NULL;
            })
            // face_blocks — map of direction → adjacent block for all 6 faces
            .property("face_blocks", obj -> {
                BlockRef r = ref(obj);
                java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    map.put(dir.getName(), wrap(r.level(), r.pos().relative(dir)));
                }
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
            })
            // is_player_looking(playerArg) — true if a player is looking at this block
            .method("is_player_looking", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                ScriptValue pv = args.get(0);
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return ScriptValue.of(false);
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (hit instanceof net.minecraft.world.phys.BlockHitResult bhr) {
                    return ScriptValue.of(bhr.getBlockPos().equals(r.pos()));
                }
                return ScriptValue.of(false);
            })
            // hit_face(player) → face relative to THIS block's own facing
            // "front","back","left","right","top","bottom" — or absolute if block has no facing
            .method("hit_face", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                ScriptValue pv = args.get(0);
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return ScriptValue.NULL;
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr) || !bhr.getBlockPos().equals(r.pos()))
                    return ScriptValue.NULL;
                net.minecraft.core.Direction hitDir = bhr.getDirection();
                // If block has a facing, express hit face relative to it
                net.minecraft.core.Direction blockFacing = readFacingDirection(r.state());
                if (blockFacing == null || blockFacing.getAxis().isVertical()) {
                    // No horizontal facing — return absolute
                    return ScriptValue.of(hitDir.getName());
                }
                if (hitDir == net.minecraft.core.Direction.UP) return ScriptValue.of("top");
                if (hitDir == net.minecraft.core.Direction.DOWN) return ScriptValue.of("bottom");
                if (hitDir == blockFacing) return ScriptValue.of("front");
                if (hitDir == blockFacing.getOpposite()) return ScriptValue.of("back");
                if (hitDir == blockFacing.getCounterClockWise(net.minecraft.core.Direction.Axis.Y)) return ScriptValue.of("left");
                if (hitDir == blockFacing.getClockWise(net.minecraft.core.Direction.Axis.Y)) return ScriptValue.of("right");
                return ScriptValue.of(hitDir.getName());
            })
            // hit_uv(player) → {u: 0..1, v: 0..1} within the hit face (u=horizontal, v=vertical on face)
            // Useful for detecting sub-areas: hit_uv returns u,v in [0,1] within the hit face
            .method("hit_uv", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                ScriptValue pv = args.get(0);
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return ScriptValue.NULL;
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr) || !bhr.getBlockPos().equals(r.pos()))
                    return ScriptValue.NULL;
                net.minecraft.world.phys.Vec3 loc = bhr.getLocation();
                net.minecraft.core.Direction face = bhr.getDirection();
                // Project hit point onto face-local UV coords [0,1]
                double u, v;
                double lx = loc.x - r.pos().getX(), ly = loc.y - r.pos().getY(), lz = loc.z - r.pos().getZ();
                switch (face) {
                    case UP    -> { u = lx; v = 1 - lz; }
                    case DOWN  -> { u = lx; v = lz; }
                    case NORTH -> { u = 1 - lx; v = 1 - ly; }
                    case SOUTH -> { u = lx; v = 1 - ly; }
                    case WEST  -> { u = lz; v = 1 - ly; }
                    case EAST  -> { u = 1 - lz; v = 1 - ly; }
                    default    -> { u = 0; v = 0; }
                }
                java.util.LinkedHashMap<String, ScriptValue> m = new java.util.LinkedHashMap<>();
                m.put("u", ScriptValue.of(u));
                m.put("v", ScriptValue.of(v));
                m.put("face", ScriptValue.of(face.getName()));
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(m);
            })
            // hit_in_area(player, face, u0, v0, u1, v1) → bool
            // True if player looks at this block's given face AND the hit point is within [u0,v0]..[u1,v1]
            .method("hit_in_area", (obj, args) -> {
                if (args.size() < 6) return ScriptValue.of(false);
                ScriptValue pv = args.get(0);
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return ScriptValue.of(false);
                String faceFilter = args.get(1).asStr();
                double u0 = args.get(2).asNum(), v0 = args.get(3).asNum();
                double u1 = args.get(4).asNum(), v1 = args.get(5).asNum();
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr) || !bhr.getBlockPos().equals(r.pos()))
                    return ScriptValue.of(false);
                if (!faceFilter.isEmpty() && !bhr.getDirection().getName().equals(faceFilter))
                    return ScriptValue.of(false);
                net.minecraft.world.phys.Vec3 loc = bhr.getLocation();
                net.minecraft.core.Direction face = bhr.getDirection();
                double lx = loc.x - r.pos().getX(), ly = loc.y - r.pos().getY(), lz = loc.z - r.pos().getZ();
                double u, v;
                switch (face) {
                    case UP    -> { u = lx; v = 1 - lz; }
                    case DOWN  -> { u = lx; v = lz; }
                    case NORTH -> { u = 1 - lx; v = 1 - ly; }
                    case SOUTH -> { u = lx; v = 1 - ly; }
                    case WEST  -> { u = lz; v = 1 - ly; }
                    case EAST  -> { u = 1 - lz; v = 1 - ly; }
                    default    -> { u = 0; v = 0; }
                }
                return ScriptValue.of(u >= u0 && u <= u1 && v >= v0 && v <= v1);
            })
            // entities(radius?) — all entities near this block
            .method("entities", (obj, args) -> {
                BlockRef r = ref(obj);
                double radius = args.isEmpty() ? 4 : args.get(0).asNum();
                net.minecraft.world.phys.Vec3 center = net.minecraft.world.phys.Vec3.atCenterOf(r.pos());
                net.minecraft.world.phys.AABB box = net.minecraft.world.phys.AABB.ofSize(center, radius*2, radius*2, radius*2);
                java.util.List<net.minecraft.world.entity.Entity> found = r.level().getEntities((net.minecraft.world.entity.Entity)null, box, e -> true);
                java.util.List<ScriptValue> result = new java.util.ArrayList<>(found.size());
                for (var e : found) result.add(dev.arubik.craftengine.script.types.entity.EntityType.wrap(e));
                return new ScriptValue.Array(result);
            })
            // replace(blockId) — set this block to given id
            .method("replace", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                BlockRef r = ref(obj);
                String id = args.get(0).asStr();
                try {
                    net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.parse(id.contains(":") ? id : "minecraft:" + id);
                    net.minecraft.world.level.block.Block block = (net.minecraft.world.level.block.Block) BuiltInRegistries.BLOCK.getValue(loc);
                    if (block == null) return ScriptValue.of(false);
                    r.level().setBlock(r.pos(), block.defaultBlockState(), 3);
                    return ScriptValue.of(true);
                } catch (Throwable e) { return ScriptValue.of(false); }
            })
            // place_custom(id[, props_map]) -> bool. Places a CraftEngine CUSTOM block (not a
            // vanilla one — see replace(id) for that) at this position, optionally setting
            // blockstate properties from a map of property-name -> value-name strings (e.g.
            // make_map("facing", "north")) — the generic primitive a "place a different custom
            // block depending on which face was clicked" item script needs (see
            // ItemActionEvent.clicked_block/clicked_face).
            .method("place_custom", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                BlockRef r = ref(obj);
                try {
                    net.momirealms.craftengine.core.util.Key id =
                            net.momirealms.craftengine.core.util.Key.of(args.get(0).asStr());
                    net.momirealms.craftengine.core.block.BlockDefinition def =
                            net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.byId(id);
                    if (def == null) return ScriptValue.of(false);
                    net.momirealms.craftengine.core.block.ImmutableBlockState state = def.defaultState();
                    if (args.size() >= 2 && args.get(1) instanceof ScriptValue.Obj mo
                            && mo.instance() instanceof java.util.Map<?, ?> map) {
                        for (var entry : map.entrySet()) {
                            String propName = String.valueOf(entry.getKey());
                            Object rawValue = entry.getValue();
                            String valueName = rawValue instanceof ScriptValue sv ? sv.asStr() : String.valueOf(rawValue);
                            net.momirealms.craftengine.core.block.property.Property prop = state.getProperty(propName);
                            if (prop == null) continue;
                            Comparable value = prop.valueByName(valueName.toLowerCase(java.util.Locale.ROOT));
                            if (value != null)
                                state = net.momirealms.craftengine.core.block.ImmutableBlockState.with(state, prop, value);
                        }
                    }
                    org.bukkit.World world = r.level().getWorld();
                    org.bukkit.Location loc = new org.bukkit.Location(world, r.pos().getX(), r.pos().getY(), r.pos().getZ());
                    boolean placed = net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(loc, state, 3, false);
                    return ScriptValue.of(placed);
                } catch (Throwable e) { return ScriptValue.of(false); }
            })
            // ---- Fluid properties ----
            // has_fluid → true if the block has any non-empty fluid (water, lava, etc.)
            .property("has_fluid", obj -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return ScriptValue.of(!fs.isEmpty());
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // is_water → true if the fluid at this position is water (flowing or source)
            .property("is_water", obj -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return ScriptValue.of(!fs.isEmpty()
                        && fs.getType() instanceof net.minecraft.world.level.material.WaterFluid);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // is_source_fluid → true if the fluid at this position is a source block
            .property("is_source_fluid", obj -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return ScriptValue.of(!fs.isEmpty() && fs.isSource());
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // fluid_flow → Vec3 of the flow direction (x,y,z). Zero vector if no fluid or source.
            .property("fluid_flow", obj -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    if (fs.isEmpty()) return VectorType.wrap(0, 0, 0);
                    net.minecraft.world.phys.Vec3 flow = fs.getFlow(r.level(), r.pos());
                    return VectorType.wrap(flow.x, flow.y, flow.z);
                } catch (Throwable ignored) { return VectorType.wrap(0, 0, 0); }
            })
            .method("play_sound", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                try {
                    BlockRef r = ref(obj);
                    String soundId = args.get(0).asStr();
                    float vol   = args.size() >= 2 ? (float) args.get(1).asNum() : 1.0f;
                    float pitch = args.size() >= 3 ? (float) args.get(2).asNum() : 1.0f;
                    Identifier id = Identifier.tryParse(soundId.contains(":") ? soundId : "minecraft:" + soundId);
                    if (id == null) return ScriptValue.of(false);
                    r.level().playSeededSound(null,
                        r.pos().getX() + 0.5, r.pos().getY() + 0.5, r.pos().getZ() + 0.5,
                        Holder.direct(SoundEvent.createVariableRangeEvent(id)),
                        SoundSource.BLOCKS, vol, pitch, 0L);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            });
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Block", new BlockRef(level, pos));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState cycleProp(BlockState state, Property prop) {
        Object[] vals = prop.getPossibleValues().toArray();
        Object current = state.getValue(prop);
        for (int i = 0; i < vals.length; i++) {
            if (vals[i].equals(current)) {
                return state.setValue(prop, (Comparable) vals[(i + 1) % vals.length]);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState applyProp(BlockState state, Property prop, String value) {
        var parsed = prop.getValue(value);
        if (parsed.isPresent()) {
            return state.setValue(prop, (Comparable) parsed.get());
        }
        return null;
    }

    /** Fires a machine's on_property_change hook after a set_property write, if this position's
     *  block entity is a machine with one configured — moved here from the old (now-removed)
     *  Machine.set_state so every set_property caller, Machine or plain Block, gets the same
     *  behaviour uniformly. No-op for anything that isn't a DataMachineBlockEntity. */
    private static void fireOnPropertyChange(BlockRef r, BlockState oldState, BlockState newState) {
        try {
            var be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
            if (be == null || !(be.controller instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return;
            if (dm.definition() == null || dm.definition().onPropertyChangeScript() == null) return;
            dev.arubik.craftengine.script.ScriptContext baseCtx = dm.buildScriptContext();
            if (baseCtx == null) return;
            dev.arubik.craftengine.script.ScriptContext ctx = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(baseCtx)
                .typed("prevState", dev.arubik.craftengine.script.types.world.BlockStateType.wrap(oldState))
                .typed("newState",  dev.arubik.craftengine.script.types.world.BlockStateType.wrap(newState))
                .build();
            dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(dm.definition().onPropertyChangeScript());
            if (call != null) call.execute(ctx);
        } catch (Throwable ignored) {}
    }

    public static BlockRef ref(Object obj) {
        if (obj instanceof dev.arubik.craftengine.script.types.machine.MachineType.MachineRef m)
            return new BlockRef(m.level(), m.pos());
        return (BlockRef) obj;
    }

    // ---- CE + NMS dual property helpers ----------------------------------------

    /**
     * Read a named property from a block — checks CE custom block state first,
     * falls back to vanilla NMS BlockState properties.
     * Returns null if property not found on either system.
     */
    @SuppressWarnings("unchecked")
    /** CE custom block id at this state, or null when the block is plain vanilla. */
    public static String customBlockId(BlockState bs) {
        try {
            var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(bs).orElse(null);
            if (cs == null) return null;
            net.momirealms.craftengine.core.util.Key id = cs.owner().value().id();
            return id != null ? id.toString() : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String readProperty(BlockState bs, String propName) {
        // CE custom block state
        try {
            var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null) {
                net.momirealms.craftengine.core.block.property.Property<?> ceProp =
                    ((net.momirealms.craftengine.core.block.BlockDefinition) cs.owner().value()).getProperty(propName);
                if (ceProp != null) return String.valueOf(cs.get(ceProp)).toLowerCase(java.util.Locale.ROOT);
            }
        } catch (Throwable ignored) {}
        // Vanilla NMS
        for (Property<?> prop : bs.getProperties()) {
            if (prop.getName().equals(propName)) return bs.getValue(prop).toString();
        }
        return null;
    }

    /** True if the block has the named property in either CE or vanilla. */
    public static boolean hasProperty(BlockState bs, String propName) {
        return readProperty(bs, propName) != null;
    }

    /**
     * Set a named property — tries CE custom block state first, falls back to vanilla.
     * Returns new BlockState or null if failed.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BlockState writeProperty(BlockState bs, String propName, String value) {
        // Try vanilla first (CE custom blocks often also have vanilla mirrored props)
        for (Property prop : bs.getProperties()) {
            if (prop.getName().equals(propName)) {
                BlockState result = applyProp(bs, prop, value);
                if (result != null) return result;
            }
        }
        // CE custom property. This used to return null outright, which made
        // Machine.set_property() a silent no-op for every CE-only property — that is why no
        // kinetic block (shaft, cogwheels, gearboxes, drill, hand crank, water wheels,
        // windmill) ever reached activated=true: their NMS backing block has no such
        // property, only the CE state does. Mirrors readProperty's CE branch.
        try {
            var cs = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null) {
                net.momirealms.craftengine.core.block.property.Property ceProp =
                    ((net.momirealms.craftengine.core.block.BlockDefinition) cs.owner().value()).getProperty(propName);
                if (ceProp != null) {
                    Comparable<?> val = ceProp.valueByName(value);
                    if (val == null) {
                        // Accept "1"/"0" and mixed case for booleans, like the vanilla path does.
                        val = ceProp.valueByName(value.toLowerCase(java.util.Locale.ROOT));
                    }
                    if (val != null) {
                        var ns = net.momirealms.craftengine.core.block.ImmutableBlockState
                                .with(cs, ceProp, val);
                        if (ns != null && ns.customBlockState() != null) {
                            return (BlockState) ns.customBlockState().minecraftState();
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /**
     * Read the "facing" direction from a block — checks both CE and vanilla,
     * tries common property names: "facing", "horizontal_facing".
     */
    public static net.minecraft.core.Direction readFacingDirection(BlockState bs) {
        for (String name : new String[]{"facing", "horizontal_facing"}) {
            String val = readProperty(bs, name);
            if (val != null) {
                net.minecraft.core.Direction dir = net.minecraft.core.Direction.byName(val);
                if (dir != null) return dir;
            }
        }
        return null;
    }
}
