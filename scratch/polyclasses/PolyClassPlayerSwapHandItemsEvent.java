/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassPlayerSwapHandItemsEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerSwapHandItemsEvent", (String)"set_main_hand_item", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerSwapHandItemsEvent", (String)"set_main_hand_item");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerSwapHandItemsEvent", (String)"set_off_hand_item", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"PlayerSwapHandItemsEvent", (String)"set_off_hand_item");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerSwapHandItemsEvent", (String)"off_hand_item");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerSwapHandItemsEvent", (String)"main_hand_item");
    }

    public boolean tm$0_set_main_hand_item(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PlayerSwapHandItemsEvent", (String)"set_main_hand_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_main_hand_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerSwapHandItemsEvent", (String)"set_main_hand_item", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_off_hand_item(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PlayerSwapHandItemsEvent", (String)"set_off_hand_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_set_off_hand_item(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerSwapHandItemsEvent", (String)"set_off_hand_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_off_hand_item() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerSwapHandItemsEvent", (String)"off_hand_item", (Object)this.instance);
    }

    public ScriptValue pg$5_main_hand_item() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerSwapHandItemsEvent", (String)"main_hand_item", (Object)this.instance);
    }

    public PolyClassPlayerSwapHandItemsEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerSwapHandItemsEvent of(Object object) {
        return new PolyClassPlayerSwapHandItemsEvent(object);
    }

    public static PolyClassPlayerSwapHandItemsEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerSwapHandItemsEvent")) {
            return new PolyClassPlayerSwapHandItemsEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerSwapHandItemsEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerSwapHandItemsEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
