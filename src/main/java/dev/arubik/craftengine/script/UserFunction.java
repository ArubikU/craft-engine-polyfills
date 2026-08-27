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
public record UserFunction(String name, List<String> params, ScriptContext definingCtx,
                            BiConsumer<ScriptContext, ScriptContext.Builder> executor) {
    public static final String TYPE = "__func__";

    private static final int MAX_DEPTH = 64;
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[]{0});
    private static final ThreadLocal<java.util.Deque<String>> CALL_STACK =
        ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /**
     * Call this function with the given arguments under the given context.
     * Returns the value of {@code __return__} or NULL.
     * Throws if call depth exceeds MAX_DEPTH to prevent infinite-recursion hangs.
     *
     * <p>Resolves free names LEXICALLY, not dynamically: the execution scope starts from this
     * function's own DEFINING context (every sibling def/const/import visible at the point this
     * function was declared in its own file — snapshotted once, at definition time), with the
     * caller's live bindings (Machine, Player, event, ...) layered on top. Without definingCtx, a
     * helper function used only internally by another function (never itself re-exported by name
     * through every import chain up to whatever top-level script ends up calling it) would resolve
     * to nothing the moment it's invoked from a DIFFERENT file than the one that imported it —
     * silently, since calling an unresolved name here just yields NULL rather than throwing.</p>
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
            ScriptContext.Builder fb = ScriptContext.builder();
            if (definingCtx != null) fb.copyFrom(definingCtx);
            fb.copyFrom(callerCtx);
            for (int i = 0; i < params.size(); i++) {
                fb.val(params.get(i), i < args.size() ? args.get(i) : ScriptValue.NULL);
            }
            // peek(), not build() — fb isn't touched again after this point (both reads below are
            // synchronous), so there's no reason to pay for a full defensive copy either time.
            // resultB.copyFrom still gives IT a real independent map of its own (it gets mutated by
            // executor.accept below); the "callerCtx" arg passed to executor is currently unused by
            // ScriptProgram's only executor implementation (it runs entirely off resultB instead),
            // so building a whole second defensive copy just for that was pure waste.
            ScriptContext.Builder resultB = ScriptContext.builder().copyFrom(fb.peek());
            executor.accept(fb.peek(), resultB);
            return resultB.build().getVar("__return__");
        } finally {
            depth[0]--;
            callStack.poll();
        }
    }
}
