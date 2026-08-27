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

public class PolyClassFurnaceSmeltEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"FurnaceSmeltEvent", (String)"set_result", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"FurnaceSmeltEvent", (String)"set_result");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceSmeltEvent", (String)"result");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceSmeltEvent", (String)"source");
    }

    public boolean tm$0_set_result(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"FurnaceSmeltEvent", (String)"set_result", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_result(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"FurnaceSmeltEvent", (String)"set_result", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_result() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceSmeltEvent", (String)"result", (Object)this.instance);
    }

    public ScriptValue pg$3_source() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceSmeltEvent", (String)"source", (Object)this.instance);
    }

    public PolyClassFurnaceSmeltEvent(Object object) {
        super(object);
    }

    public static PolyClassFurnaceSmeltEvent of(Object object) {
        return new PolyClassFurnaceSmeltEvent(object);
    }

    public static PolyClassFurnaceSmeltEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("FurnaceSmeltEvent")) {
            return new PolyClassFurnaceSmeltEvent(object);
        }
        return null;
    }

    public static PolyClassFurnaceSmeltEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassFurnaceSmeltEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
