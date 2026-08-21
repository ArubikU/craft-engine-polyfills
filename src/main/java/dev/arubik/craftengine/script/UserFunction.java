package dev.arubik.craftengine.script;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * A user-defined function declared with {@code def name(params) { body }} in a .pf script.
 * Stored as {@code ScriptValue.Obj("__func__", UserFunction)} in the script context.
 *
 * The {@code executor} is a lambda created by ScriptProgram that runs the body
 * given a prepared ScriptContext.Builder, handling ReturnSignal internally and
 * writing {@code __return__} to the builder on return.
 */
public record UserFunction(String name, List<String> params, BiConsumer<ScriptContext, ScriptContext.Builder> executor) {
    public static final String TYPE = "__func__";

    private static final int MAX_DEPTH = 64;
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[]{0});
    private static final ThreadLocal<java.util.Deque<String>> CALL_STACK =
        ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /**
     * Call this function with the given arguments under the given context.
     * Returns the value of {@code __return__} or NULL.
     * Throws if call depth exceeds MAX_DEPTH to prevent infinite-recursion hangs.
     */
    public ScriptValue call(List<ScriptValue> args, ScriptContext callerCtx) {
        int[] depth = DEPTH.get();
        java.util.Deque<String> callStack = CALL_STACK.get();
        if (depth[0] >= MAX_DEPTH) {
            java.util.logging.Logger.getLogger("CraftEnginePolyfills")
                .warning("[Script] Call depth limit (" + MAX_DEPTH + ") in '" + name
                    + "'. Stack: " + callStack);
            return ScriptValue.NULL;
        }
        depth[0]++;
        callStack.push(name);
        try {
            ScriptContext.Builder fb = ScriptContext.builder().copyFrom(callerCtx);
            for (int i = 0; i < params.size(); i++) {
                fb.val(params.get(i), i < args.size() ? args.get(i) : ScriptValue.NULL);
            }
            ScriptContext.Builder resultB = ScriptContext.builder().copyFrom(fb.build());
            executor.accept(fb.build(), resultB);
            return resultB.build().getVar("__return__");
        } finally {
            depth[0]--;
            callStack.poll();
        }
    }
}
