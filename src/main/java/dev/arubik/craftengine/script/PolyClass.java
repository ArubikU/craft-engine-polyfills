package dev.arubik.craftengine.script;

import java.util.List;

/**
 * A named object that can be addressed in script expressions as
 * {@code ClassName.property} or {@code ClassName.method(args)}.
 */
public interface PolyClass {
    ScriptValue get(String property);
    ScriptValue call(String method, List<ScriptValue> args);
}
