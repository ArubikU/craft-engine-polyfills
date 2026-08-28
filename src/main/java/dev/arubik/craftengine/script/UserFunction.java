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
            // over(), not copyFrom(): the parameters sit on top of the caller's context, which
            // sits on top of the defining scope. That is what these two lines always meant; copying
            // was just how it was said, and it copied every variable in both contexts on every
            // single call — 3.16% of server wall time in a profile with a player online.
            ScriptContext.Builder fb = ScriptContext.builder().over(definingCtx).over(callerCtx);
            for (int i = 0; i < params.size(); i++) {
                fb.val(params.get(i), i < args.size() ? args.get(i) : ScriptValue.NULL);
            }
            // Layered as well: the body's writes land in resultB's OWN map and never reach fb,
            // which is exactly the independence the copy here used to provide. The "callerCtx"
            // argument to executor is unused by ScriptProgram's only implementation (it runs off
            // resultB), so it costs nothing to hand it the same view.
            ScriptContext.Builder resultB = ScriptContext.builder().over(fb.peek());
            executor.accept(fb.peek(), resultB);
            // peek(), not build(), for the same reason the two above are: resultB is dead after
            // this line, so build()'s defensive copy of BOTH maps existed only to read one key out
            // of it and throw the context away. On a real server that showed up as
            // LinkedHashMap.<init>/putMapEntries dominating every script call in the profile.
            // peek() is a live view over the same maps — identical read semantics, including the
            // TRACKED_VARS bookkeeping getVar does — with nothing copied.
            return resultB.peek().getVar("__return__");
        } finally {
            depth[0]--;
            callStack.poll();
        }
    }
}
