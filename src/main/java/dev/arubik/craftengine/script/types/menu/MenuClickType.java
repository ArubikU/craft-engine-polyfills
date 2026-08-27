package dev.arubik.craftengine.script.types.menu;

import dev.arubik.craftengine.menu.MenuClickInvocation;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

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
            .propertyTyped("slot", TypeCodecs.DOUBLE, (MenuClickInvocation inv) -> (double) inv.slot())
            .propertyTyped("click_type", TypeCodecs.STRING, (MenuClickInvocation inv) -> inv.clickType())
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MenuClickInvocation inv, String key) -> inv.get(key))
            .methodTyped1("has", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MenuClickInvocation inv, String key) -> inv.data().containsKey(key));
    }

    public static ScriptValue wrap(MenuClickInvocation invocation) {
        if (invocation == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("MenuClick", invocation);
    }
}
