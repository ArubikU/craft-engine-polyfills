package dev.arubik.craftengine.script.types.cmd;

import dev.arubik.craftengine.cmd.CmdInvocation;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

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
            .propertyTyped("name", TypeCodecs.STRING, (CmdInvocation inv) -> inv.commandName())
            .propertyTyped("sender_name", TypeCodecs.STRING, (CmdInvocation inv) -> inv.senderName())
            .propertyTyped("is_player", TypeCodecs.BOOL, (CmdInvocation inv) -> inv.isPlayer())
            .methodTyped1("arg", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (CmdInvocation obj, String key) -> obj.arg(key))
            .methodTyped1("has_arg", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (CmdInvocation obj, String key) -> obj.args().containsKey(key))
            // open_page(name) — reopens one of THIS command's own declarative "pages" (see
            // CmdPageDef) for the current sender, e.g. a pagination Prev/Next button's click
            // script bumping a page-offset flag then calling this to redraw the same menu.
            .methodTyped1("open_page", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (CmdInvocation obj, String name) -> obj.openPage(name));
    }

    public static ScriptValue wrap(CmdInvocation invocation) {
        if (invocation == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("Cmd", invocation);
    }
}
