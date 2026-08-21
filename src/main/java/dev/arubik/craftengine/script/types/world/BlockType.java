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
            .property("is_air", obj -> ScriptValue.of(ref(obj).state().isAir()))
            .property("hardness", obj -> {
                BlockRef r = ref(obj);
                return ScriptValue.of(r.state().getDestroySpeed(r.level(), r.pos()));
            })
            .property("light_level", obj -> ScriptValue.of(ref(obj).state().getLightEmission()))
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
            .property("world", obj -> WorldType.wrap(ref(obj).level()))
            .property("location", obj -> LocationType.wrap(ref(obj).level(), ref(obj).pos().getX(), ref(obj).pos().getY(), ref(obj).pos().getZ()))
            // block_state — full BlockState wrapper (CE + vanilla properties, CE priority)
            .property("block_state", obj -> BlockStateType.wrap(ref(obj).state()))
            .method("get_state", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String propName = args.get(0).asStr();
                BlockState bs = ref(obj).state();
                for (Property<?> prop : bs.getProperties()) {
                    if (prop.getName().equals(propName)) {
                        return ScriptValue.of(bs.getValue(prop).toString());
                    }
                }
                return ScriptValue.NULL;
            })
            .method("set_state", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                String propName = args.get(0).asStr();
                String value = args.get(1).asStr();
                BlockRef r = ref(obj);
                BlockState bs = r.state();
                for (Property<?> prop : bs.getProperties()) {
                    if (prop.getName().equals(propName)) {
                        BlockState newState = applyProp(bs, prop, value);
                        if (newState != null) {
                            r.level().setBlock(r.pos(), newState, 3);
                            return ScriptValue.of(true);
                        }
                    }
                }
                return ScriptValue.of(false);
            })
            // property(name) — alias for get_state
            // property(name) — reads CE custom OR vanilla block state property
            .method("property", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                String val = readProperty(ref(obj).state(), args.get(0).asStr());
                return val != null ? ScriptValue.of(val) : ScriptValue.NULL;
            })
            // has_property(name) — checks both CE and vanilla property systems
            .method("has_property", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(hasProperty(ref(obj).state(), args.get(0).asStr()));
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
            // break_and_drop() → Array<Item> — breaks block and returns drops
            .method("break_and_drop", (obj, args) -> {
                BlockRef r = ref(obj);
                try {
                    BlockState bs = r.state();
                    if (bs.isAir()) return new ScriptValue.Array(java.util.List.of());
                    var drops = net.minecraft.world.level.block.Block.getDrops(bs, r.level(), r.pos(), r.level().getBlockEntity(r.pos()));
                    r.level().removeBlock(r.pos(), false);
                    java.util.List<ScriptValue> result = new java.util.ArrayList<>(drops.size());
                    for (var drop : drops) result.add(ScriptValue.ofItem(drop));
                    return new ScriptValue.Array(result);
                } catch (Throwable e) { return new ScriptValue.Array(java.util.List.of()); }
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
        // Machine.set_state() a silent no-op for every CE-only property — that is why no
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
