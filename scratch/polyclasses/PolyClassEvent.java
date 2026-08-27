/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassEvent {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.TypedPropertyHandler tp$7;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Event", (String)"cancel", (String)":Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Event", (String)"cancel");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Event", (String)"set_cancelled", (String)"Z:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Event", (String)"set_cancelled");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Event", (String)"cancelled");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Event", (String)"cancelled", (String)"Z");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Event", (String)"type");
        tp$7 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Event", (String)"type", (String)"S");
    }

    public boolean tm$0_cancel() {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Event", (String)"cancel", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$1_cancel(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Event", (String)"cancel", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_cancelled(boolean bl) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"Event", (String)"set_cancelled", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$3_set_cancelled(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Event", (String)"set_cancelled", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_cancelled() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"cancelled", (Object)this.instance);
    }

    public boolean tg$5_cancelled() {
        if (tp$5 != null) {
            return (Boolean)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"cancelled", (Object)this.instance).asBool();
    }

    public ScriptValue pg$6_type() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"type", (Object)this.instance);
    }

    public String tg$7_type() {
        if (tp$7 != null) {
            return (String)tp$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Event", (String)"type", (Object)this.instance).asStr();
    }

    public PolyClassEvent(Object object) {
        this.instance = object;
    }

    public static PolyClassEvent of(Object object) {
        return new PolyClassEvent(object);
    }

    public static PolyClassEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Event")) {
            return new PolyClassEvent(object);
        }
        return null;
    }
}
