package dev.arubik.craftengine.machine.render.formula;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;

/**
 * PolyClass exposing block state properties + block entity NBT data.
 * Accessible as "Block" in PolyContext.
 *
 * Properties:
 *   id          — "minecraft:stone" or CE custom block id
 *   is_air      — boolean
 *   hardness    — float
 *   light_level — emitted light (0-15)
 *   redstone    — redstone signal received (0-15)
 *   powered     — redstone > 0
 *   any block state property by name (e.g. "facing", "powered", "lit", "age")
 *
 * Methods:
 *   nbt("path.to.key")       — reads block entity NBT by dot-path
 *   has_property("name")     — bool: block state has this property
 *   property("name")         — value of block state property as string/number
 */
public final class BlockClass extends PolyValue.Obj {

    final ServerLevel level;
    final BlockPos pos;
    private BlockState cachedState;
    private CompoundTag cachedNbt;
    private boolean nbtResolved;

    private BlockClass(ServerLevel level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public static BlockClass of(ServerLevel level, BlockPos pos) {
        return new BlockClass(level, pos);
    }

    private BlockState state() {
        if (cachedState == null && level != null) {
            try { cachedState = level.getBlockState(pos); } catch (Throwable ignored) {}
        }
        return cachedState;
    }

    private CompoundTag nbt() {
        if (!nbtResolved && level != null) {
            nbtResolved = true;
            try {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) cachedNbt = be.saveWithFullMetadata(level.registryAccess());
            } catch (Throwable ignored) {}
        }
        return cachedNbt;
    }

    @Override
    public PolyValue get(String property) {
        BlockState bs = state();
        if (bs == null) return PolyValue.NULL;

        return switch (property) {
            case "id" -> {
                try {
                    var ceState = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                            .getOptionalCustomBlockState(bs);
                    if (ceState.isPresent()) {
                        yield PolyValue.of(ceState.get().owner().value().id().toString());
                    }
                } catch (Throwable ignored) {}
                yield PolyValue.of(bs.getBlock().builtInRegistryHolder().key().toString());
            }
            case "is_air"      -> PolyValue.of(bs.isAir());
            case "hardness"    -> PolyValue.of(bs.getDestroySpeed(level, pos));
            case "light_level" -> PolyValue.of(bs.getLightEmission());
            case "redstone"    -> PolyValue.of(level.getBestNeighborSignal(pos));
            case "powered"     -> PolyValue.of(level.getBestNeighborSignal(pos) > 0);
            case "has_be", "has_block_entity" -> PolyValue.of(bs.hasBlockEntity());
            case "location" -> new PolyValue.Obj(LocationClass.forLevel(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            case "world"    -> new PolyValue.Obj(WorldClass.forLevel(level));
            case "inventory" -> {
                try {
                    var be = level.getBlockEntity(pos);
                    if (be instanceof net.minecraft.world.Container c)
                        yield new PolyValue.Obj(new InventoryClass(
                                new org.bukkit.craftbukkit.inventory.CraftInventory(c)));
                } catch (Throwable ignored) {}
                yield PolyValue.NULL;
            }
            default -> {
                // Try as block state property
                Property<?> prop = null;
                for (Property<?> p : bs.getProperties()) {
                    if (p.getName().equals(property)) { prop = p; break; }
                }
                if (prop != null) {
                    Comparable<?> val = bs.getValue(prop);
                    if (val instanceof Boolean b) yield PolyValue.of(b);
                    if (val instanceof Number n) yield PolyValue.of(n.doubleValue());
                    yield PolyValue.of(val.toString());
                }
                yield PolyValue.NULL;
            }
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {
            case "nbt" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                CompoundTag root = nbt();
                if (root == null) yield PolyValue.NULL;
                yield resolveNbtPath(root, args.get(0).asStr());
            }
            case "has_property" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                BlockState bs = state();
                if (bs == null) yield PolyValue.of(false);
                String name = args.get(0).asStr();
                yield PolyValue.of(bs.getProperties().stream().anyMatch(p -> p.getName().equals(name)));
            }
            case "property" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                yield get(args.get(0).asStr());
            }
            default -> get(method);
        };
    }

    private static PolyValue resolveNbtPath(CompoundTag root, String path) {
        String[] parts = path.split("\\.");
        Tag current = root;
        for (String part : parts) {
            if (current instanceof CompoundTag ct) {
                if (!ct.contains(part)) return PolyValue.NULL;
                current = ct.get(part);
            } else {
                return PolyValue.NULL;
            }
        }
        if (current == null) return PolyValue.NULL;
        if (current instanceof NumericTag nt) return PolyValue.of(nt.doubleValue());
        if (current instanceof StringTag st) return PolyValue.of(st.value());
        if (current instanceof CompoundTag) return PolyValue.of("[compound]");
        return PolyValue.of(current.toString());
    }
}
