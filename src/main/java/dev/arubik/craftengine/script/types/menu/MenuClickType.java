package dev.arubik.craftengine.script.types.menu;

import dev.arubik.craftengine.menu.MenuClickInvocation;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

/**
 * {@code MenuClick} — bound in a {@link dev.arubik.craftengine.menu.ScriptMenu} click script (see
 * {@code dev.arubik.craftengine.menu.ScriptMenuListener}). {@code MenuClick.slot}/{@code
 * .click_type} identify what was clicked; {@code MenuClick.get("key")} reads whatever data map was
 * attached to that slot via {@code MenuBuilder.set_item}'s optional {@code click_data} argument.
 */
public final class MenuClickType {

    private MenuClickType() {}

    public static void register() {
        PolyTypeRegistry.define("MenuClick")
            .property("slot", obj -> ScriptValue.of(inv(obj).slot()))
            .property("click_type", obj -> ScriptValue.of(inv(obj).clickType()))
            .method("get", (obj, args) -> args.isEmpty() ? ScriptValue.NULL : inv(obj).get(args.get(0).asStr()))
            .method("has", (obj, args) -> ScriptValue.of(!args.isEmpty() && inv(obj).data().containsKey(args.get(0).asStr())));
    }

    public static ScriptValue wrap(MenuClickInvocation invocation) {
        if (invocation == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("MenuClick", invocation);
    }

    private static MenuClickInvocation inv(Object obj) { return (MenuClickInvocation) obj; }
}
