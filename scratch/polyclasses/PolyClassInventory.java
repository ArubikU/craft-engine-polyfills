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
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassInventory {
    protected final Object instance;
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.TypedMethodHandler1 h$1;
    private static volatile PolyType.MethodHandler m$2;
    private static volatile PolyType.TypedMethodHandler1 h$3;
    private static volatile PolyType.MethodHandler m$4;
    private static volatile PolyType.TypedMethodHandler1 h$5;
    private static volatile PolyType.MethodHandler m$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"Inventory", (String)"slots");
        h$1 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Inventory", (String)"count_of", (String)"S:D");
        m$2 = PolyClassRuntime.resolveMethodHandler((String)"Inventory", (String)"count_of");
        h$3 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Inventory", (String)"slot", (String)"D:R");
        m$4 = PolyClassRuntime.resolveMethodHandler((String)"Inventory", (String)"slot");
        h$5 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Inventory", (String)"has", (String)"S:Z");
        m$6 = PolyClassRuntime.resolveMethodHandler((String)"Inventory", (String)"has");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Inventory", (String)"size");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Inventory", (String)"contents");
    }

    public ScriptValue um$0_slots(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Inventory", (String)"slots", (Object)this.instance, (List)list);
    }

    public double tm$1_count_of(String string) {
        if (h$1 != null) {
            return (Double)h$1.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Inventory", (String)"count_of", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asNum();
    }

    public ScriptValue um$2_count_of(List list) {
        if (m$2 != null) {
            return m$2.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Inventory", (String)"count_of", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$3_slot(double d) {
        if (h$3 != null) {
            return (ScriptValue)h$3.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Inventory", (String)"slot", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$4_slot(List list) {
        if (m$4 != null) {
            return m$4.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Inventory", (String)"slot", (Object)this.instance, (List)list);
    }

    public boolean tm$5_has(String string) {
        if (h$5 != null) {
            return (Boolean)h$5.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"Inventory", (String)"has", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$6_has(List list) {
        if (m$6 != null) {
            return m$6.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Inventory", (String)"has", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$7_size() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Inventory", (String)"size", (Object)this.instance);
    }

    public ScriptValue pg$8_contents() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Inventory", (String)"contents", (Object)this.instance);
    }

    public PolyClassInventory(Object object) {
        this.instance = object;
    }

    public static PolyClassInventory of(Object object) {
        return new PolyClassInventory(object);
    }

    public static PolyClassInventory ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Inventory")) {
            return new PolyClassInventory(object);
        }
        return null;
    }
}
