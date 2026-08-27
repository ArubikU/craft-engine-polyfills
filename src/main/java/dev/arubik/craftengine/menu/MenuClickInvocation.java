package dev.arubik.craftengine.menu;

import java.util.Map;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * The {@code MenuClick} object bound in a {@link ScriptMenu} click script — carries which slot was
 * clicked, the Bukkit click-type name (same lowercase convention as the machine-menu button system,
 * e.g. {@code "left"}/{@code "shift_right"}/{@code "drop"}), and whatever data map was attached to
 * that slot via {@code MenuBuilder.set_item}'s optional trailing {@code click_data} argument —
 * mirrors {@code dev.arubik.craftengine.tasks.TaskInvocation}'s "optional trailing data map" shape.
 */
public record MenuClickInvocation(int slot, String clickType, Map<String, ScriptValue> data) {

    public ScriptValue get(String key) {
        return data.getOrDefault(key, ScriptValue.NULL);
    }
}
