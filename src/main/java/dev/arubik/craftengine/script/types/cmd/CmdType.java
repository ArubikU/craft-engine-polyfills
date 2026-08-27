package dev.arubik.craftengine.script.types.cmd;

import dev.arubik.craftengine.cmd.CmdInvocation;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

/**
 * {@code Cmd} — bound in every data-driven command's {@code execute} script (see {@code
 * dev.arubik.craftengine.cmd.CmdRegistry}). {@code Cmd.arg("name")} reads a parsed argument by
 * the name declared in the command's JSON; {@code Player}/{@code Server} (already bound
 * alongside it) cover per-player and global persistent state, so this type stays a thin,
 * per-invocation read-only view rather than growing its own storage.
 */
public final class CmdType {

    private CmdType() {}

    public static void register() {
        PolyTypeRegistry.define("Cmd")
            .property("name", obj -> ScriptValue.of(inv(obj).commandName()))
            .property("sender_name", obj -> ScriptValue.of(inv(obj).senderName()))
            .property("is_player", obj -> ScriptValue.of(inv(obj).isPlayer()))
            .method("arg", (obj, args) -> args.isEmpty() ? ScriptValue.NULL : inv(obj).arg(args.get(0).asStr()))
            .method("has_arg", (obj, args) -> ScriptValue.of(!args.isEmpty() && inv(obj).args().containsKey(args.get(0).asStr())))
            // open_page(name) — reopens one of THIS command's own declarative "pages" (see
            // CmdPageDef) for the current sender, e.g. a pagination Prev/Next button's click
            // script bumping a page-offset flag then calling this to redraw the same menu.
            .method("open_page", (obj, args) ->
                ScriptValue.of(!args.isEmpty() && inv(obj).openPage(args.get(0).asStr())));
    }

    public static ScriptValue wrap(CmdInvocation invocation) {
        if (invocation == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Cmd", invocation);
    }

    private static CmdInvocation inv(Object obj) { return (CmdInvocation) obj; }
}
