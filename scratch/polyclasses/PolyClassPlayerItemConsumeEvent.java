/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassPlayerItemConsumeEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerItemConsumeEvent", (String)"set_item", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerItemConsumeEvent", (String)"set_item");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerItemConsumeEvent", (String)"item");
    }

    public boolean tm$0_set_item(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PlayerItemConsumeEvent", (String)"set_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerItemConsumeEvent", (String)"set_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_item() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerItemConsumeEvent", (String)"item", (Object)this.instance);
    }

    public PolyClassPlayerItemConsumeEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerItemConsumeEvent of(Object object) {
        return new PolyClassPlayerItemConsumeEvent(object);
    }

    public static PolyClassPlayerItemConsumeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerItemConsumeEvent")) {
            return new PolyClassPlayerItemConsumeEvent(object);
        }
        return null;
    }
}
