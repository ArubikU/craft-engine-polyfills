package dev.arubik.craftengine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CustomDataType<T, P> {

    public static String encode(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(item);
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (Exception exception) {
            return "";
        }
    }

    public static ItemStack decode(String base64) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(base64));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (Exception exception) {
            return null;
        }
    }

    public static CustomDataType<UUID, String> UUID_TYPE = new CustomDataType<UUID, String>(
            PersistentDataType.STRING,
            java.util.UUID::toString,
            java.util.UUID::fromString);

    public static CustomDataType<BlockPos, String> BLOCK_POS_TYPE = new CustomDataType<BlockPos, String>(
            PersistentDataType.STRING,
            pos -> pos.getX() + "," + pos.getY() + "," + pos.getZ(),
            str -> {
                String[] parts = str.split(",");
                return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            });
    public static CustomDataType<Location, String> LOCATION_TYPE = new CustomDataType<Location, String>(
            PersistentDataType.STRING,
            loc -> loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ(),
            str -> {
                String[] parts = str.split(",");
                return new Location(org.bukkit.Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            });

    public static final Gson gson = new GsonBuilder().registerTypeAdapter(ItemStackWithSlot.class, new ItemStackWithSlotAdaptor()).create();
    public static final CustomDataType<List<ItemStackWithSlot>, String> ITEM_STACK_WITH_SLOT_LIST_TYPE = new CustomDataType<List<ItemStackWithSlot>, String>(
            PersistentDataType.STRING,
            (itemStacks) -> {
                try {
                    JsonArray jsonObject = new JsonArray();
                    itemStacks.forEach(itemStackWithSlot -> {
                        JsonElement itemJson = gson.toJsonTree(itemStackWithSlot);
                        jsonObject.add(itemJson);
                    });
                    return jsonObject.toString();
                } catch (Throwable e) {
                    Bukkit.getConsoleSender().sendMessage("Error serializing ItemStackWithSlot list: " + e.getMessage());
                    return "[]";
                }
            },
            str -> {
                try {
                    JsonArray jsonArray = gson.fromJson(str, JsonArray.class);
                    List<ItemStackWithSlot> itemStacks = new ArrayList<>();
                    for (JsonElement element : jsonArray) {
                        ItemStackWithSlot itemStackWithSlot = gson.fromJson(element, ItemStackWithSlot.class);
                        itemStacks.add(itemStackWithSlot);
                    }
                    return itemStacks;
                } catch (Throwable e) {
                    return List.of();
                }
            });

    private final PersistentDataType<?, P> baseType;
    private Function<T, P> serializer;
    private BiFunction<T, PersistentDataContainer, P> containerSerializer;
    private final Function<P, T> deserializer;

    public CustomDataType(PersistentDataType<?, P> baseType, Function<T, P> serializer, Function<P, T> deserializer) {
        this.baseType = baseType;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.containerSerializer = null;
    }

    public CustomDataType(PersistentDataType<?, P> baseType,
            BiFunction<T, PersistentDataContainer, P> containerSerializer, Function<P, T> deserializer) {
        this.baseType = baseType;
        this.serializer = null;
        this.containerSerializer = containerSerializer;
        this.deserializer = deserializer;
    }

    public PersistentDataType<?, P> getBaseType() {
        return baseType;
    }

    public Function<T, P> getSerializer() {
        return serializer;
    }

    public BiFunction<T, PersistentDataContainer, P> getContainerSerializer() {
        return containerSerializer;
    }

    public boolean isContainerSerializer() {
        return containerSerializer != null;
    }

    public Function<P, T> getDeserializer() {
        return deserializer;
    }
}
