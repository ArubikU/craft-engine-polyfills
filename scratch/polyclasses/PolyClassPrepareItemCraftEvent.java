/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassPrepareItemCraftEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PrepareItemCraftEvent", (String)"set_result", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PrepareItemCraftEvent", (String)"set_result");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PrepareItemCraftEvent", (String)"result");
    }

    public boolean tm$0_set_result(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PrepareItemCraftEvent", (String)"set_result", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_result(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PrepareItemCraftEvent", (String)"set_result", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_result() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PrepareItemCraftEvent", (String)"result", (Object)this.instance);
    }

    public PolyClassPrepareItemCraftEvent(Object object) {
        super(object);
    }

    public static PolyClassPrepareItemCraftEvent of(Object object) {
        return new PolyClassPrepareItemCraftEvent(object);
    }
}
