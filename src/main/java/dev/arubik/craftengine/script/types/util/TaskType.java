package dev.arubik.craftengine.script.types.util;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.tasks.TaskInvocation;

/**
 * {@code Task} — bound in a {@code TaskManager}-fired script when it was scheduled with a data
 * map (see {@code TaskManagerType#schedule}/{@code #repeat}): {@code Task.get("key")} reads a
 * value stashed at schedule time, {@code Task.id} is the handle string {@code
 * TaskManager.cancel(...)} takes (useful for a repeating task to cancel itself from inside its own
 * callback once some condition is met).
 */
public final class TaskType {

    private TaskType() {}

    public static void register() {
        PolyTypeRegistry.define("Task")
            .propertyTyped("id", TypeCodecs.STRING, (TaskInvocation inv) -> inv.id())
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (TaskInvocation inv, String key) -> inv.get(key))
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (TaskInvocation inv, String key) -> inv.data().containsKey(key));
    }

    public static ScriptValue wrap(TaskInvocation invocation) {
        if (invocation == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Task", invocation);
    }
}
