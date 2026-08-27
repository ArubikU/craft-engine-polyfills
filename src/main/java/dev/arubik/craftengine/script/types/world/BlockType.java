package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.primitive.MapType;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
            // Every property below keeps `Object` as its instance parameter and calls ref(obj)
            // inside the body: ref() is NOT a plain cast (it also converts a MachineRef, since
            // Machine inherits these members from Block — see ref()'s doc), so that conversion must
            // not be delegated to propertyTyped's generic cast.
            .propertyTyped("id", TypeCodecs.STRING, (Object obj) -> {
                BlockState bs = ref(obj).state();
                // A CraftEngine custom block reports its CE id ("cml:crate_acacia"), not the
                // vanilla block it is backed by. The backing id is useless for identification:
                // every CE block sharing a base block would compare equal to every other.
                String ce = customBlockId(bs);
                if (ce != null) return ce;
                return BuiltInRegistries.BLOCK.getKey(bs.getBlock()).toString();
            })
            /** The vanilla block backing this position, regardless of any CE block on top of it. */
            .propertyTyped("vanilla_id", TypeCodecs.STRING, (Object obj) ->
                    BuiltInRegistries.BLOCK.getKey(ref(obj).state().getBlock()).toString())
            .propertyTyped("is_custom", TypeCodecs.BOOL, (Object obj) -> customBlockId(ref(obj).state()) != null)
            /** Whether super glue attaches this block to anything — see the Glue singleton. */
            .propertyTyped("is_glued", TypeCodecs.BOOL, (Object obj) -> {
                try {
                    BlockRef r = ref(obj);
                    return dev.arubik.craftengine.contraption.glue.GlueRegistry
                            .graphFor(r.level().dimension()).hasNode(r.pos());
                } catch (Throwable ignored) { return false; }
            })
            /** Every block glued to this one — what a bearing anchored here would carry. */
            // Migrated to methodTyped0 — instance kept as Object (not BlockRef) because ref(obj)
            // does more than a plain cast (it also accepts a MachineRef, since Machine inherits
            // this method from Block — see ref()'s doc), so that conversion must stay inside the
            // body rather than being delegated to methodTypedN's generic cast.
            // Return codec declares the element type: every element is an Obj("Block", BlockRef),
            // so the handler returns a real List<BlockRef> and TypeCodecs.listOf wraps it.
            .methodTyped0("glue_structure", TypeCodecs.listOf("Block", BlockRef.class), (Object obj) -> {
                java.util.List<BlockRef> out = new java.util.ArrayList<>();
                try {
                    BlockRef r = ref(obj);
                    for (net.minecraft.core.BlockPos p : dev.arubik.craftengine.contraption.glue.GlueRegistry
                            .structureAt(r.level().dimension(), r.pos())) {
                        out.add(new BlockRef(r.level(), p));
                    }
                } catch (Throwable ignored) {}
                return out;
            })
            .propertyTyped("is_air", TypeCodecs.BOOL, (Object obj) -> ref(obj).state().isAir())
            // Whether this block is a storage endpoint (vanilla container OR a custom block
            // exposing WorldlyContainerHolder) as opposed to another pipe segment or plain air —
            // lets a pipe's own panel script tell "this face touches a chest" from "this face
            // touches another pipe" (see item_pipe_panel.pf's cycle_mode: pipe-to-pipe should only
            // ever be Disabled/Both, the Input/Output split only means something toward a container).
            .propertyTyped("is_container", TypeCodecs.BOOL, (Object obj) -> {
                BlockRef r = ref(obj);
                return dev.arubik.craftengine.pipe.item.ItemTransferHelper
                        .getContainer(r.level(), r.pos()).isPresent();
            })
            .propertyTyped("hardness", TypeCodecs.DOUBLE, (Object obj) -> {
                BlockRef r = ref(obj);
                return (double) r.state().getDestroySpeed(r.level(), r.pos());
            })
            .propertyTyped("light_level", TypeCodecs.DOUBLE, (Object obj) -> (double) ref(obj).state().getLightEmission())
            // The biome id AT this position ("minecraft:plains", ...) — a position-dependent
            // lookup, so it lives here (where a level+pos are already on hand) rather than on
            // Registry.biomes, which is deliberately just an id list with no coordinate concept.
            .propertyTyped("biome", TypeCodecs.STRING, (Object obj) -> {
                BlockRef r = ref(obj);
                try {
                    var holder = r.level().getBiome(r.pos());
                    return holder.unwrapKey()
                            .map(k -> k.identifier().toString())
                            .orElse("unknown");
                } catch (Throwable ignored) { return "unknown"; }
            })
            // polyType("Vector", Vector3d) — VectorType.wrap(x,y,z) is exactly
            // ScriptValue.ofObj("Vector", new Vector3d(x,y,z)).
            .propertyTyped("pos", TypeCodecs.polyType("Vector", org.joml.Vector3d.class), (Object obj) -> {
                BlockPos p = ref(obj).pos();
                return new org.joml.Vector3d(p.getX(), p.getY(), p.getZ());
            })
            .propertyTyped("redstone", TypeCodecs.DOUBLE, (Object obj) -> {
                BlockRef r = ref(obj);
                return (double) r.level().getBestNeighborSignal(r.pos());
            })
            .propertyTyped("powered", TypeCodecs.BOOL, (Object obj) -> {
                BlockRef r = ref(obj);
                return r.level().getBestNeighborSignal(r.pos()) > 0;
            })
            // get_metadata — the real block entity at this position (sign text, container
            // contents, skull owner, banner pattern, ...) as a type-appropriate script object,
            // e.g. block.get_metadata.front.lines[0]. NULL when there's no block entity here,
            // or its kind isn't modeled — see BlockMetadataType.
            // RAW: metadataOf picks a DIFFERENT BlockMetadata child PolyType per block entity
            // ("SignMetadata", "ContainerMetadata", ...) and returns NULL for an unmodeled one, so
            // no single polyType name describes it.
            .propertyTyped("get_metadata", TypeCodecs.RAW, (Object obj) -> BlockMetadataType.metadataOf(ref(obj).level(), ref(obj).pos()))
            // metadata_type — which single BlockMetadata child type get_metadata would return
            // (e.g. "SignMetadata"), or NULL — for branching on a block's kind without paying for
            // the full metadata object first.
            // RAW: metadataTypeOf itself returns a ScriptValue that is a Str on one branch and NULL
            // on two others — STRING would need the helper's own return type changed.
            .propertyTyped("metadata_type", TypeCodecs.RAW, (Object obj) -> BlockMetadataType.metadataTypeOf(ref(obj).level(), ref(obj).pos()))
            // metadata_types — every modeled type name applicable to this block, most specific
            // first, as an Array (empty when nothing is modeled for it).
            // RAW, not listOf: the elements are plain type-NAME strings, not Objs of any PolyType —
            // a list codec would silently drop every one of them.
            .propertyTyped("metadata_types", TypeCodecs.RAW, (Object obj) -> BlockMetadataType.metadataTypesOf(ref(obj).level(), ref(obj).pos()))
            // machine — this block wrapped as a "Machine" (see MachineType), for a script that
            // found some OTHER machine block (via real_block, block_at, facing_block, ...) and
            // wants to read/write its typed data or call its Machine.* methods directly. NULL if
            // there's no data-driven machine block entity actually here.
            // polyType("Machine", MachineRef): MachineType.wrap is exactly
            // ScriptValue.ofObj("Machine", new MachineRef(level, pos, facing, be)) with no null
            // handling of its own, and PolyCodec encodes a null instance to NULL — matching the
            // old "no machine block entity here" branch.
            .propertyTyped("machine", TypeCodecs.polyType("Machine",
                    dev.arubik.craftengine.script.types.machine.MachineType.MachineRef.class), (Object obj) -> {
                BlockRef r = ref(obj);
                var be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                if (be != null && be.controller instanceof dev.arubik.craftengine.block.entity.PersistentBlockEntity pbe) {
                    return new dev.arubik.craftengine.script.types.machine.MachineType.MachineRef(r.level(), r.pos(), "north", pbe);
                }
                return null;
            })
            // polyType("World", ServerLevel) — identical to WorldType.wrap (ofObj for non-null,
            // NULL for null).
            .propertyTyped("world", TypeCodecs.polyType("World", ServerLevel.class), (Object obj) -> ref(obj).level())
            // RAW, not polyType("Location", ...): LocationType.wrap is not a bare ofObj — for a
            // ContraptionLevel it first projects the local coordinates into the real world and may
            // re-target the LocationRef at a different level. Typing this would mean duplicating
            // that projection here.
            .propertyTyped("location", TypeCodecs.RAW, (Object obj) ->
                    LocationType.wrap(ref(obj).level(), ref(obj).pos().getX(), ref(obj).pos().getY(), ref(obj).pos().getZ()))
            // block_state — full BlockState wrapper (CE + vanilla properties, CE priority)
            // RAW, not polyType("BlockState", ...): BlockStateType.wrap also resolves the CE
            // ImmutableBlockState to build its BlockStateRef, which polyType's encode can't do.
            .propertyTyped("block_state", TypeCodecs.RAW, (Object obj) -> BlockStateType.wrap(ref(obj).state()))
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
            // Migrated to methodTyped2 (see glue_structure's note on why instance stays Object).
            .methodTyped2("set_property", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String propName, String value) -> {
                BlockRef r = ref(obj);
                BlockState oldState = r.state();
                BlockState newState = writeProperty(oldState, propName, value);
                if (newState == null) return false;
                r.level().setBlock(r.pos(), newState, 3);
                // Overwrite (not just invalidate) PROPERTY_CACHE's entry for this exact key with
                // the FRESH post-write value, stamped with the current tick — otherwise a same-tick
                // property(name) read right after this write would either see the STALE pre-write
                // value (if left untouched) or pay for a full recompute on its next read (if merely
                // removed); writing the known-correct value straight through keeps that next read a
                // cache hit too.
                overwritePropertyCache(r.level(), r.pos(), propName, newState);
                fireOnPropertyChange(r, oldState, newState);
                return true;
            })
            // property(name) — reads CE custom OR vanilla block state property. Cached within the
            // SAME server tick (see PROPERTY_CACHE/readPropertyThisTick) — a machine's own action
            // script and its renderer entries commonly read the exact same property several times
            // per tick.
            // Migrated to methodTyped1 (see glue_structure's note on why instance stays Object).
            .methodTyped1("property", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String propName) -> {
                BlockRef r = ref(obj);
                String val = readPropertyThisTick(r.level(), r.pos(), r.state(), propName);
                return val != null ? ScriptValue.of(val) : ScriptValue.NULL;
            })
            // has_property(name) — checks both CE and vanilla property systems (same per-tick cache)
            // Migrated to methodTyped1.
            .methodTyped1("has_property", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String propName) -> {
                BlockRef r = ref(obj);
                return readPropertyThisTick(r.level(), r.pos(), r.state(), propName) != null;
            })
            // cycle_prop(name) — advance property to its next possible value
            // Migrated to methodTyped1.
            .methodTyped1("cycle_prop", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String propName) -> {
                BlockRef r = ref(obj);
                BlockState bs = r.state();
                for (Property<?> prop : bs.getProperties()) {
                    if (prop.getName().equals(propName)) {
                        BlockState newState = cycleProp(bs, prop);
                        if (newState != null) {
                            r.level().setBlock(r.pos(), newState, 3);
                            overwritePropertyCache(r.level(), r.pos(), propName, newState);
                            return true;
                        }
                    }
                }
                return false;
            })
            // combined_light — max of block+sky light at this position
            .propertyTyped("combined_light", TypeCodecs.DOUBLE, (Object obj) -> (double) ref(obj).level().getMaxLocalRawBrightness(ref(obj).pos()))
            .propertyTyped("block_light",    TypeCodecs.DOUBLE, (Object obj) -> (double) ref(obj).level().getBrightness(LightLayer.BLOCK, ref(obj).pos()))
            .propertyTyped("sky_light",      TypeCodecs.DOUBLE, (Object obj) -> (double) ref(obj).level().getBrightness(LightLayer.SKY, ref(obj).pos()))
            // apply_bone_meal() → bool — grows the plant/crop
            // Migrated to methodTyped0.
            .methodTyped0("apply_bone_meal", TypeCodecs.BOOL, (Object obj) -> {
                BlockRef r = ref(obj);
                try {
                    BlockState bs = r.state();
                    if (bs.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock g
                            && g.isValidBonemealTarget(r.level(), r.pos(), bs)) {
                        g.performBonemeal((net.minecraft.server.level.ServerLevel) r.level(), r.level().random, r.pos(), bs);
                        return true;
                    }
                } catch (Throwable ignored) {}
                return false;
            })
            // break_and_drop() → Array<Item> — breaks block, awards its normal XP orb (if any), and
            // returns drops. The one true "break this block" implementation — Machine.break_block
            // used to hand-roll a near-identical copy (missing the XP award) for breaking a block
            // OTHER than the machine's own; since the caller already holds that Block value (from
            // Machine.facing_block, World.get_block, ...), it just calls this directly now.
            // Migrated to methodTyped0.
            // NOT typed with listOf: elements are ScriptValue.Item (ScriptValue.ofItem), a distinct
            // ScriptValue variant, not an Obj of any PolyType — listOf would re-box them as Obj.
            .methodTyped0("break_and_drop", TypeCodecs.RAW, (Object obj) -> {
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
            // Migrated to methodTyped1 — item arg kept as TypeCodecs.RAW since it's an ScriptValue
            // union (Item or Obj-wrapped ItemStack), not one fixed native type.
            .methodTyped1("place_from_item", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue itemArg) -> {
                BlockRef r = ref(obj);
                if (!r.state().isAir()) return false;
                net.minecraft.world.item.ItemStack stack;
                if (itemArg instanceof ScriptValue.Item i) stack = i.stack();
                else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof net.minecraft.world.item.ItemStack is) stack = is;
                else return false;
                if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.BlockItem bi)) return false;
                r.level().setBlock(r.pos(), bi.getBlock().defaultBlockState(), 3);
                stack.shrink(1);
                return true;
            })
            // play_sound(soundId, vol?, pitch?)
            // --- Facing / direction methods (CE + vanilla aware) ---
            // facing(fallback?) — reads "facing" from CE custom OR vanilla block state
            // Migrated to methodTypedOpt1: the single arg is an optional fallback that's only
            // consulted when no facing property was found, so the body must still run on a no-arg
            // call (methodTyped1's onMissingArgs would short-circuit BEFORE that lookup and wrongly
            // return its constant for a block that DOES have a facing) — exactly what
            // methodTypedOptN expresses. Arg stays TypeCodecs.RAW: it's returned verbatim, so it
            // must not be coerced to any one native type. Its default IS the old missing-arg
            // result (ScriptValue.NULL), so no sentinel is needed here.
            // Instance stays Object (see glue_structure's note on ref()).
            .methodTypedOpt1("facing", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.RAW,
                (Object obj, ScriptValue fallback) -> {
                String val = readProperty(ref(obj).state(), "facing");
                if (val == null) val = readProperty(ref(obj).state(), "horizontal_facing");
                if (val != null) return ScriptValue.of(val);
                return fallback;
            })
            // relative(direction) — block adjacent in given direction ("north","south","east","west","up","down")
            // Migrated to methodTyped1.
            .methodTyped1("relative", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, String dirName) -> {
                BlockRef r = ref(obj);
                net.minecraft.core.Direction dir = net.minecraft.core.Direction.byName(dirName);
                if (dir == null) return ScriptValue.NULL;
                return wrap(r.level(), r.pos().relative(dir));
            })
            // offset(dx,dy,dz) — block at relative offset
            // Migrated to methodTyped3.
            .methodTyped3("offset", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, Double dxArg, Double dyArg, Double dzArg) -> {
                BlockRef r = ref(obj);
                int dx = dxArg.intValue(), dy = dyArg.intValue(), dz = dzArg.intValue();
                return wrap(r.level(), r.pos().offset(dx, dy, dz));
            })
            // facing_block — block this block faces, using CE or vanilla "facing" property
            // polyType("Block", BlockRef): wrap(level,pos) is ofObj("Block", new BlockRef(level,pos))
            // guarded on null level/pos — neither can be null on this path (r comes from a live
            // BlockRef, and relative() never returns null) — and PolyCodec encodes null to NULL,
            // matching the no-facing branch.
            .propertyTyped("facing_block", TypeCodecs.polyType("Block", BlockRef.class), (Object obj) -> {
                BlockRef r = ref(obj);
                net.minecraft.core.Direction dir = readFacingDirection(r.state());
                return dir != null ? new BlockRef(r.level(), r.pos().relative(dir)) : null;
            })
            // face_blocks — map of direction → adjacent block for all 6 faces
            // RAW: a Map value, not a list or a scalar — MapType.wrap boxes it as Obj("Map", ...).
            .propertyTyped("face_blocks", TypeCodecs.RAW, (Object obj) -> {
                BlockRef r = ref(obj);
                java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    map.put(dir.getName(), wrap(r.level(), r.pos().relative(dir)));
                }
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
            })
            // is_player_looking(playerArg) — true if a player is looking at this block
            // Migrated to methodTyped1 — player arg kept as TypeCodecs.RAW (needs an instanceof
            // narrowing to a wrapped Player, not a fixed native type).
            .methodTyped1("is_player_looking", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Object obj, ScriptValue pv) -> {
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return false;
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (hit instanceof net.minecraft.world.phys.BlockHitResult bhr) {
                    return bhr.getBlockPos().equals(r.pos());
                }
                return false;
            })
            // hit_face(player) → face relative to THIS block's own facing
            // "front","back","left","right","top","bottom" — or absolute if block has no facing
            // Migrated to methodTyped1 — player arg kept as TypeCodecs.RAW (same reason as
            // is_player_looking above).
            .methodTyped1("hit_face", TypeCodecs.RAW, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue pv) -> {
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
            // Migrated to methodTyped1 — player arg kept as TypeCodecs.RAW (same reason as
            // is_player_looking above).
            .methodTyped1("hit_uv", TypeCodecs.RAW, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, ScriptValue pv) -> {
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
            // Migrated to methodTyped6 (per coordinator update — arity 4-7 now supported). Player
            // arg stays TypeCodecs.RAW (needs an instanceof narrowing, not a fixed native type);
            // faceFilter is a string, the four UV bounds are doubles.
            .methodTyped6("hit_in_area", TypeCodecs.RAW, TypeCodecs.STRING,
                TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (Object obj, ScriptValue pv, String faceFilter, Double u0, Double v0, Double u1, Double v1) -> {
                if (!(pv instanceof ScriptValue.Obj po) || !(po.instance() instanceof net.minecraft.world.entity.player.Player player))
                    return false;
                BlockRef r = ref(obj);
                var hit = player.pick(5.0, 1.0f, false);
                if (!(hit instanceof net.minecraft.world.phys.BlockHitResult bhr) || !bhr.getBlockPos().equals(r.pos()))
                    return false;
                if (!faceFilter.isEmpty() && !bhr.getDirection().getName().equals(faceFilter))
                    return false;
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
                return u >= u0 && u <= u1 && v >= v0 && v <= v1;
            })
            // entities(radius?) — all entities near this block
            // Migrated to methodTypedOpt1: radius is optional WITH a real default (4) used to
            // compute an actual result when omitted, not an early-return sentinel — precisely the
            // methodTypedOptN shape (methodTyped1's onMissingArgs would short-circuit instead).
            // NOT typed with listOf: EntityType.wrap picks the PolyType name PER ENTITY
            // ("Player"/"Animal"/"Mob"/...), so this is a mixed-PolyType array.
            .methodTypedOpt1("entities", TypeCodecs.DOUBLE, 4.0, TypeCodecs.RAW,
                (Object obj, Double radiusArg) -> {
                BlockRef r = ref(obj);
                double radius = radiusArg;
                net.minecraft.world.phys.Vec3 center = net.minecraft.world.phys.Vec3.atCenterOf(r.pos());
                net.minecraft.world.phys.AABB box = net.minecraft.world.phys.AABB.ofSize(center, radius*2, radius*2, radius*2);
                java.util.List<net.minecraft.world.entity.Entity> found = r.level().getEntities((net.minecraft.world.entity.Entity)null, box, e -> true);
                java.util.List<ScriptValue> result = new java.util.ArrayList<>(found.size());
                for (var e : found) result.add(dev.arubik.craftengine.script.types.entity.EntityType.wrap(e));
                return new ScriptValue.Array(result);
            })
            // replace(blockId) — set this block to given id
            // Migrated to methodTyped1.
            .methodTyped1("replace", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Object obj, String id) -> {
                BlockRef r = ref(obj);
                try {
                    net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.parse(id.contains(":") ? id : "minecraft:" + id);
                    net.minecraft.world.level.block.Block block = (net.minecraft.world.level.block.Block) BuiltInRegistries.BLOCK.getValue(loc);
                    if (block == null) return false;
                    r.level().setBlock(r.pos(), block.defaultBlockState(), 3);
                    return true;
                } catch (Throwable e) { return false; }
            })
            // place_custom(id[, props_map]) -> bool. Places a CraftEngine CUSTOM block (not a
            // vanilla one — see replace(id) for that) at this position, optionally setting
            // blockstate properties from a map of property-name -> value-name strings (e.g.
            // make_map("facing", "north")) — the generic primitive a "place a different custom
            // block depending on which face was clicked" item script needs (see
            // ItemActionEvent.clicked_block/clicked_face).
            // Migrated to methodTypedOpt2: the optional 2nd arg (props map) must leave the 1-arg
            // call working, which methodTyped2's onMissingArgs would break — methodTypedOptN always
            // runs the body instead. The map slot stays TypeCodecs.RAW (it's iterated as a raw Map
            // after an instanceof narrowing, not one fixed native type) with a Java `null` default,
            // and `id` likewise takes a null default as an "argument was absent" sentinel so the
            // original `args.isEmpty()` early return is reproduced EXACTLY — a decoded STRING is
            // never null (ScriptValue.of(String) maps null to NULL, so asStr() always returns a
            // real string), so null can only mean "not passed". An absent map is null and simply
            // fails the same instanceof check a present-but-non-map argument does.
            .methodTypedOpt2("place_custom", TypeCodecs.STRING, (String) null,
                TypeCodecs.RAW, (ScriptValue) null, TypeCodecs.BOOL,
                (Object obj, String idArg, ScriptValue propsArg) -> {
                if (idArg == null) return false;
                BlockRef r = ref(obj);
                try {
                    net.momirealms.craftengine.core.util.Key id =
                            net.momirealms.craftengine.core.util.Key.of(idArg);
                    net.momirealms.craftengine.core.block.BlockDefinition def =
                            net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.byId(id);
                    if (def == null) return false;
                    net.momirealms.craftengine.core.block.ImmutableBlockState state = def.defaultState();
                    if (propsArg instanceof ScriptValue.Obj mo
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
                    return net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.place(loc, state, 3, false);
                } catch (Throwable e) { return false; }
            })
            // ---- Fluid properties ----
            // has_fluid → true if the block has any non-empty fluid (water, lava, etc.)
            .propertyTyped("has_fluid", TypeCodecs.BOOL, (Object obj) -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return !fs.isEmpty();
                } catch (Throwable ignored) { return false; }
            })
            // is_water → true if the fluid at this position is water (flowing or source)
            .propertyTyped("is_water", TypeCodecs.BOOL, (Object obj) -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return !fs.isEmpty()
                        && fs.getType() instanceof net.minecraft.world.level.material.WaterFluid;
                } catch (Throwable ignored) { return false; }
            })
            // is_source_fluid → true if the fluid at this position is a source block
            .propertyTyped("is_source_fluid", TypeCodecs.BOOL, (Object obj) -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    return !fs.isEmpty() && fs.isSource();
                } catch (Throwable ignored) { return false; }
            })
            // fluid_flow → Vec3 of the flow direction (x,y,z). Zero vector if no fluid or source.
            // polyType("Vector", Vector3d) — see `pos` above; the zero-vector fallbacks become a
            // real new Vector3d(0,0,0), exactly what VectorType.wrap(0,0,0) built.
            .propertyTyped("fluid_flow", TypeCodecs.polyType("Vector", org.joml.Vector3d.class), (Object obj) -> {
                try {
                    BlockRef r = ref(obj);
                    net.minecraft.world.level.material.FluidState fs = r.level().getFluidState(r.pos());
                    if (fs.isEmpty()) return new org.joml.Vector3d(0, 0, 0);
                    net.minecraft.world.phys.Vec3 flow = fs.getFlow(r.level(), r.pos());
                    return new org.joml.Vector3d(flow.x, flow.y, flow.z);
                } catch (Throwable ignored) { return new org.joml.Vector3d(0, 0, 0); }
            })
            // Migrated to methodTypedOpt3: only soundId is required — vol/pitch are optional
            // trailing args each with their own real default (1.0f) and the body still runs when
            // they're omitted, which is methodTypedOptN's shape (methodTyped3's onMissingArgs would
            // break the valid 1-arg call). soundId takes a Java `null` default as an "argument was
            // absent" sentinel so the original `args.isEmpty()` early return is reproduced EXACTLY
            // (a decoded STRING is never null — see place_custom's note above).
            .methodTypedOpt3("play_sound", TypeCodecs.STRING, (String) null,
                TypeCodecs.DOUBLE, 1.0, TypeCodecs.DOUBLE, 1.0, TypeCodecs.BOOL,
                (Object obj, String soundId, Double volArg, Double pitchArg) -> {
                if (soundId == null) return false;
                try {
                    BlockRef r = ref(obj);
                    float vol   = volArg.floatValue();
                    float pitch = pitchArg.floatValue();
                    Identifier id = Identifier.tryParse(soundId.contains(":") ? soundId : "minecraft:" + soundId);
                    if (id == null) return false;
                    r.level().playSeededSound(null,
                        r.pos().getX() + 0.5, r.pos().getY() + 0.5, r.pos().getZ() + 0.5,
                        Holder.direct(SoundEvent.createVariableRangeEvent(id)),
                        SoundSource.BLOCKS, vol, pitch, 0L);
                    return true;
                } catch (Throwable ignored) { return false; }
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
