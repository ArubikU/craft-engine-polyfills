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
import dev.arubik.craftengine.script.PolyClassEntity_v3;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassItemEntity
extends PolyClassEntity_v3 {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ItemEntity", (String)"set_item", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ItemEntity", (String)"set_item");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ItemEntity", (String)"item");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ItemEntity", (String)"pickup_delay");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ItemEntity", (String)"pickup_delay", (String)"D");
    }

    public boolean tm$0_set_item(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"ItemEntity", (String)"set_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ItemEntity", (String)"set_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_item() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemEntity", (String)"item", (Object)this.instance);
    }

    public ScriptValue pg$3_pickup_delay() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemEntity", (String)"pickup_delay", (Object)this.instance);
    }

    public double tg$4_pickup_delay() {
        if (tp$4 != null) {
            return (Double)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemEntity", (String)"pickup_delay", (Object)this.instance).asNum();
    }

    public PolyClassItemEntity(Object object) {
        super(object);
    }

    public static PolyClassItemEntity of(Object object) {
        return new PolyClassItemEntity(object);
    }

    public static PolyClassItemEntity ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ItemEntity")) {
            return new PolyClassItemEntity(object);
        }
        return null;
    }

    public static PolyClassItemEntity ofVar(ScriptContext scriptContext, String string) {
        return PolyClassItemEntity.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
