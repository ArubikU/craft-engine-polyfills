package dev.arubik.craftengine.util;

import java.io.IOException;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;

public class ItemStackWithSlotAdaptor extends TypeAdapter<ItemStackWithSlot> {

    @Override
    public void write(JsonWriter writer, ItemStackWithSlot value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Slot", net.minecraft.nbt.IntTag.valueOf(value.slot()));
        nbt.store("item",ItemStack.CODEC,value.stack());
        writer.value(nbt.toString());
    }

    @Override
    public ItemStackWithSlot read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String tag = reader.nextString();
        CompoundTag nbt;
        try {
            nbt = TagParser.parseCompoundFully(tag);
        } catch (CommandSyntaxException e) {
            return new ItemStackWithSlot(0,ItemStack.EMPTY);
        }
        int slot = nbt.getIntOr("Slot", 0);
        ItemStack stack = nbt.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        return new ItemStackWithSlot(slot,stack);
    }

}