/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassPlayerExpChangeEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerExpChangeEvent", (String)"set_amount", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerExpChangeEvent", (String)"set_amount");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerExpChangeEvent", (String)"amount");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerExpChangeEvent", (String)"amount", (String)"D");
    }

    public boolean tm$0_set_amount(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"PlayerExpChangeEvent", (String)"set_amount", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_amount(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerExpChangeEvent", (String)"set_amount", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_amount() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerExpChangeEvent", (String)"amount", (Object)this.instance);
    }

    public double tg$3_amount() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerExpChangeEvent", (String)"amount", (Object)this.instance).asNum();
    }

    public PolyClassPlayerExpChangeEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerExpChangeEvent of(Object object) {
        return new PolyClassPlayerExpChangeEvent(object);
    }

    public static PolyClassPlayerExpChangeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerExpChangeEvent")) {
            return new PolyClassPlayerExpChangeEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerExpChangeEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerExpChangeEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
