package dev.arubik.craftengine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.level.block.state.BlockState;

public class CustomDataType<T, P> {

    public static CustomDataType<BlockState, String> BLOCK_STATE_TYPE = new CustomDataType<>(
            NbtType.STRING,
            blockState -> {
                try {
                    BlockData data = dev.arubik.craftengine.util.MNms.INSTANCE.method$CraftBlockData$fromData(blockState);
                    return data.getAsString();
                } catch (Exception exception) {
                    return "minecraft:air";
                }
            },
            str -> {
                try {
                    BlockData data = Bukkit.createBlockData(str);
                    return (BlockState) BlockStateUtils.blockDataToBlockState(data);
                } catch (Exception exception) {
                    return null;
                }
            });

    public static CustomDataType<UUID, String> UUID_TYPE = new CustomDataType<UUID, String>(
            NbtType.STRING,
            java.util.UUID::toString,
            java.util.UUID::fromString);

    public static CustomDataType<BlockPos, String> BLOCK_POS_TYPE = new CustomDataType<BlockPos, String>(
            NbtType.STRING,
            pos -> pos.getX() + "," + pos.getY() + "," + pos.getZ(),
            str -> {
                String[] parts = str.split(",");
                return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            });
    public static CustomDataType<Location, String> LOCATION_TYPE = new CustomDataType<Location, String>(
            NbtType.STRING,
            loc -> loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ(),
            str -> {
                String[] parts = str.split(",");
                return new Location(org.bukkit.Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            });

    /** Server-global registry lookup for {@code ItemStack.CODEC} — vanilla item/component registries
     * don't vary per-world, so this static fallback is fine off the main tick thread too. */
    private static net.minecraft.core.HolderLookup.Provider itemCodecRegistries() {
        return ((org.bukkit.craftbukkit.CraftServer) Bukkit.getServer()).getServer().registryAccess();
    }

    private static byte[] compress(net.minecraft.nbt.CompoundTag root) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            net.minecraft.nbt.NbtIo.writeCompressed(root, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static net.minecraft.nbt.CompoundTag decompress(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0)
                return new net.minecraft.nbt.CompoundTag();
            return net.minecraft.nbt.NbtIo.readCompressed(new ByteArrayInputStream(bytes),
                    net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        } catch (Exception e) {
            return new net.minecraft.nbt.CompoundTag();
        }
    }

    /** Single NMS {@link net.minecraft.world.item.ItemStack}, serialized via its own vanilla CODEC
     * (survives item-component/NBT changes across versions far better than Bukkit serialization). */
    public static final CustomDataType<net.minecraft.world.item.ItemStack, byte[]> ITEM_CODEC_TYPE = new CustomDataType<>(
            NbtType.BYTE_ARRAY,
            stack -> {
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
                if (stack != null && !stack.isEmpty()) {
                    net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, stack).result()
                            .ifPresent(t -> root.put("i", t));
                }
                return compress(root);
            },
            bytes -> {
                net.minecraft.nbt.CompoundTag root = decompress(bytes);
                net.minecraft.nbt.Tag it = root.get("i");
                if (it == null)
                    return net.minecraft.world.item.ItemStack.EMPTY;
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                return net.minecraft.world.item.ItemStack.CODEC.parse(ops, it).result()
                        .orElse(net.minecraft.world.item.ItemStack.EMPTY);
            });

    /** Fixed-size array of NMS ItemStacks (index = slot), codec-serialized like {@link #ITEM_CODEC_TYPE}. */
    public static final CustomDataType<net.minecraft.world.item.ItemStack[], byte[]> ITEM_ARRAY_CODEC_TYPE = new CustomDataType<>(
            NbtType.BYTE_ARRAY,
            stacks -> {
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
                root.putInt("n", stacks.length);
                for (int i = 0; i < stacks.length; i++) {
                    net.minecraft.world.item.ItemStack s = stacks[i];
                    if (s != null && !s.isEmpty()) {
                        final int idx = i;
                        net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, s).result()
                                .ifPresent(t -> root.put("i" + idx, t));
                    }
                }
                return compress(root);
            },
            bytes -> {
                net.minecraft.nbt.CompoundTag root = decompress(bytes);
                int n = root.getInt("n").orElse(0);
                net.minecraft.world.item.ItemStack[] stacks = new net.minecraft.world.item.ItemStack[n];
                java.util.Arrays.fill(stacks, net.minecraft.world.item.ItemStack.EMPTY);
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                for (int i = 0; i < n; i++) {
                    net.minecraft.nbt.Tag it = root.get("i" + i);
                    if (it != null) {
                        final int idx = i;
                        net.minecraft.world.item.ItemStack.CODEC.parse(ops, it).result()
                                .ifPresent(s -> stacks[idx] = s);
                    }
                }
                return stacks;
            });

    public static final CustomDataType<List<ItemStackWithSlot>, byte[]> ITEM_STACK_WITH_SLOT_LIST_TYPE = new CustomDataType<List<ItemStackWithSlot>, byte[]>(
            NbtType.BYTE_ARRAY,
            (itemStacks) -> {
                
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
                for (int i = 0; i < itemStacks.size(); i++) {
                    ItemStackWithSlot stackWithSlot = itemStacks.get(i);
                    net.minecraft.world.item.ItemStack stack = stackWithSlot.stack();
                    if (stack != null && !stack.isEmpty()) {
                        final int idx = i;
                        net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, stack).result()
                                .ifPresent(t -> root.put("i" + idx, t));
                        root.putInt("s" + idx, stackWithSlot.slot());
                    }
                }
                root.putInt("n", itemStacks.size());
                return compress(root);
            },
            bytes -> {
                net.minecraft.nbt.CompoundTag root = decompress(bytes);
                int n = root.getInt("n").orElse(0);
                List<ItemStackWithSlot> itemStacks = new ArrayList<>();
                net.minecraft.resources.RegistryOps<net.minecraft.nbt.Tag> ops = net.minecraft.resources.RegistryOps
                        .create(net.minecraft.nbt.NbtOps.INSTANCE, itemCodecRegistries());
                for (int i = 0; i < n; i++) {
                    net.minecraft.nbt.Tag it = root.get("i" + i);
                    if (it != null) {
                        final int idx = i;
                        net.minecraft.world.item.ItemStack.CODEC.parse(ops, it).result()
                                .ifPresent(s -> {
                                    int slot = root.getInt("s" + idx).orElse(-1);
                                    itemStacks.add(new ItemStackWithSlot(slot,s));
                                });
                    }
                }
                return itemStacks;
            });

    private final NbtType baseType;
    private final Function<T, P> serializer;
    private final Function<P, T> deserializer;

    public CustomDataType(NbtType baseType, Function<T, P> serializer, Function<P, T> deserializer) {
        this.baseType = baseType;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public NbtType getBaseType() {
        return baseType;
    }

    public Function<T, P> getSerializer() {
        return serializer;
    }

    public Function<P, T> getDeserializer() {
        return deserializer;
    }
}
