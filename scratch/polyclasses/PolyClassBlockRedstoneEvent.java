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

public class PolyClassBlockRedstoneEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockRedstoneEvent", (String)"set_new_current", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BlockRedstoneEvent", (String)"set_new_current");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"new_current");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockRedstoneEvent", (String)"new_current", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"block");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"BlockRedstoneEvent", (String)"old_current");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockRedstoneEvent", (String)"old_current", (String)"D");
    }

    public boolean tm$0_set_new_current(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"BlockRedstoneEvent", (String)"set_new_current", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_new_current(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockRedstoneEvent", (String)"set_new_current", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_new_current() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"new_current", (Object)this.instance);
    }

    public double tg$3_new_current() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"new_current", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_block() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$5_old_current() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"old_current", (Object)this.instance);
    }

    public double tg$6_old_current() {
        if (tp$6 != null) {
            return (Double)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockRedstoneEvent", (String)"old_current", (Object)this.instance).asNum();
    }

    public PolyClassBlockRedstoneEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockRedstoneEvent of(Object object) {
        return new PolyClassBlockRedstoneEvent(object);
    }

    public static PolyClassBlockRedstoneEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockRedstoneEvent")) {
            return new PolyClassBlockRedstoneEvent(object);
        }
        return null;
    }
}
