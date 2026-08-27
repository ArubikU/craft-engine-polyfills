package dev.arubik.craftengine.tasks;

import java.util.Map;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * The {@code Task} object bound in a {@link TaskManagerRegistry}-fired script — carries whatever
 * {@code data} map the scheduling call attached (via {@code make_map(...)}, see {@code
 * TaskManagerType.schedule/repeat}), so a deferred task can carry a LIVE value straight through
 * (a {@code Player}/{@code Entity} object, not just a re-resolvable name/UUID string) instead of
 * every script having to stash state in {@code Player.*_flag}/{@code Server.*_flag} and look it
 * back up by hand when the task fires.
 */
public record TaskInvocation(String id, Map<String, ScriptValue> data) {

    public ScriptValue get(String key) {
        return data.getOrDefault(key, ScriptValue.NULL);
    }
}
