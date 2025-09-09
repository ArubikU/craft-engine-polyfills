package dev.arubik.craftengine;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.util.ArgumentList;
import dev.arubik.craftengine.util.CustomBlockData;
import dev.arubik.craftengine.util.ArgumentList.XAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.YAxisCoordinate;
import dev.arubik.craftengine.util.ArgumentList.ZAxisCoordinate;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiFunction;

public class CepCommand implements CommandExecutor, TabCompleter {

    private final Map<ArgumentList, BiFunction<CommandSender, Object[], Boolean>> cases = new HashMap<>();

    public CepCommand() {
        // Ejemplo: /cepolyfill data get <string> <int>
        cases.put(new ArgumentList("data^","get^", XAxisCoordinate.class,YAxisCoordinate.class,ZAxisCoordinate.class), (sender, parsed) -> {

            int x = (Integer) parsed[2];
            int y = (Integer) parsed[3];
            int z = (Integer) parsed[4];
            if (sender instanceof Player player){
                Block block = player.getWorld().getBlockAt(x,y,z);
                CustomBlockData data = CustomBlockData.from(block);
                JsonObject json = new JsonObject();
                for (var key : data.getKeys()) {
                    Object value = data.get(key, data.getDataType(key));
                    if(value != null){
                        json.addProperty(key.getKey().toString(), String.valueOf(value));
                    }
                }
                sender.sendMessage(MiniMessage.miniMessage().deserialize(JsonFormatter.toMiniMessage(json)));
            }
            return true;
        });
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        for (Map.Entry<ArgumentList, BiFunction<CommandSender, Object[], Boolean>> e : cases.entrySet()) {
            if (e.getKey().matches(args,sender)) {
                Object[] parsed = e.getKey().parse(args,sender);
                return e.getValue().apply(sender, parsed);
            }
        }
        sender.sendMessage("No matching argument signature for: " + Arrays.toString(args));
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        int index = args.length - 1;
        String current = args[args.length - 1];

        for (var entry : cases.keySet()) {
            if (index < entry.types.length) {
                suggestions.addAll(entry.suggest(index, current, sender));
            }
        }
        return suggestions;
    }
}
