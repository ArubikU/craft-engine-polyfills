package dev.arubik.craftengine;

import java.util.Properties;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import dev.arubik.craftengine.block.BlockBehaviors;
import dev.arubik.craftengine.item.ItemBehaviors;
import dev.arubik.craftengine.item.ItemListener;
import dev.arubik.craftengine.util.BlockContainer;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.DataHolders;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.momirealms.craftengine.core.plugin.CraftEngine;

public final class CraftEnginePolyfills extends JavaPlugin {
    private static CraftEnginePolyfills instance;

    @Override
    public void onLoad() {
        instance = this;
        dev.arubik.craftengine.property.Properties.register();
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        initPlugin();
        getLogger().info("CraftEngine Polyfills Loaded");
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        ItemListener.register(this);
        CustomBlockData.registerListener(this);
        BlockContainer.ensureListenerRegistered(this);
        // cepolyfill command
        // sub command data get <block_pos>
        CepCommand cepCommand = new CepCommand();
        getCommand("cepolyfill").setExecutor(cepCommand);
        getCommand("cepolyfill").setTabCompleter(cepCommand);
        getLogger().info("CraftEngine Polyfills Enabled");


    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        DataHolders.INSTANCE.removeHolders();
        getLogger().info("CraftEngine Polyfills Disabled");
    }

    private void initPlugin() {
        BlockBehaviors.register();
        ItemBehaviors.register();
    }

    public static CraftEnginePolyfills instance() {
        return instance;
    }

    public static void log(String message) {
        CraftEngine.instance().logger().info(message);
    }
}
