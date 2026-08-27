/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBlockFadeEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockFadeEvent", (String)"new_state");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockFadeEvent", (String)"new_state", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockFadeEvent", (String)"block");
    }

    public ScriptValue pg$0_new_state() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockFadeEvent", (String)"new_state", (Object)this.instance);
    }

    public String tg$1_new_state() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockFadeEvent", (String)"new_state", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_block() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockFadeEvent", (String)"block", (Object)this.instance);
    }

    public PolyClassBlockFadeEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockFadeEvent of(Object object) {
        return new PolyClassBlockFadeEvent(object);
    }

    public static PolyClassBlockFadeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockFadeEvent")) {
            return new PolyClassBlockFadeEvent(object);
        }
        return null;
    }

    public static PolyClassBlockFadeEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlockFadeEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
