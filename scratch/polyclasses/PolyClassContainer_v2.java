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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassContainer_v2 {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler1 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler2 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler0 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler2 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.TypedMethodHandler2 h$14;
    private static volatile PolyType.MethodHandler m$15;
    private static volatile PolyType.PropertyHandler p$16;
    private static volatile PolyType.TypedPropertyHandler tp$17;
    private static volatile PolyType.PropertyHandler p$18;
    private static volatile PolyType.TypedPropertyHandler tp$19;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"get_item", (String)"D:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"get_item");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"pull", (String)"D:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"pull");
        h$4 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"has_room", (String)"R:Z");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"has_room");
        h$6 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"remove_item", (String)"DD:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"remove_item");
        h$8 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"clear", (String)":Z");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"clear");
        h$10 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"set_item", (String)"DR:Z");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"set_item");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"push", (String)"R:R");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"push");
        h$14 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"Container", (String)"pull_item", (String)"RD:R");
        m$15 = PolyClassRuntime.resolveMethodHandler((String)"Container", (String)"pull_item");
        p$16 = PolyClassRuntime.resolvePropertyHandler((String)"Container", (String)"size");
        tp$17 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Container", (String)"size", (String)"D");
        p$18 = PolyClassRuntime.resolvePropertyHandler((String)"Container", (String)"is_empty");
        tp$19 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Container", (String)"is_empty", (String)"Z");
    }

    public ScriptValue tm$0_get_item(double d) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"get_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$1_get_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"get_item", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_pull(double d) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"pull", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$3_pull(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"pull", (Object)this.instance, (List)list);
    }

    public boolean tm$4_has_room(ScriptValue scriptValue) {
        if (h$4 != null) {
            return (Boolean)h$4.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"has_room", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$5_has_room(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"has_room", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_remove_item(double d, double d2) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)d, (Object)d2);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"remove_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), ScriptValue.of((double)d2)});
    }

    public ScriptValue um$7_remove_item(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"remove_item", (Object)this.instance, (List)list);
    }

    public boolean tm$8_clear() {
        if (h$8 != null) {
            return (Boolean)h$8.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"clear", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asBool();
    }

    public ScriptValue um$9_clear(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"clear", (Object)this.instance, (List)list);
    }

    public boolean tm$10_set_item(double d, ScriptValue scriptValue) {
        if (h$10 != null) {
            return (Boolean)h$10.call(this.instance, (Object)d, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"set_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d), scriptValue}).asBool();
    }

    public ScriptValue um$11_set_item(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"set_item", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$12_push(ScriptValue scriptValue) {
        if (h$12 != null) {
            return (ScriptValue)h$12.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"push", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue});
    }

    public ScriptValue um$13_push(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"push", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$14_pull_item(ScriptValue scriptValue, double d) {
        if (h$14 != null) {
            return (ScriptValue)h$14.call(this.instance, (Object)scriptValue, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"Container", (String)"pull_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d)});
    }

    public ScriptValue um$15_pull_item(List list) {
        if (m$15 != null) {
            return m$15.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Container", (String)"pull_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$16_size() {
        if (p$16 != null) {
            return p$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Container", (String)"size", (Object)this.instance);
    }

    public double tg$17_size() {
        if (tp$17 != null) {
            return (Double)tp$17.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Container", (String)"size", (Object)this.instance).asNum();
    }

    public ScriptValue pg$18_is_empty() {
        if (p$18 != null) {
            return p$18.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Container", (String)"is_empty", (Object)this.instance);
    }

    public boolean tg$19_is_empty() {
        if (tp$19 != null) {
            return (Boolean)tp$19.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Container", (String)"is_empty", (Object)this.instance).asBool();
    }

    public PolyClassContainer_v2(Object object) {
        this.instance = object;
    }

    public static PolyClassContainer_v2 of(Object object) {
        return new PolyClassContainer_v2(object);
    }

    public static PolyClassContainer_v2 ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Container")) {
            return new PolyClassContainer_v2(object);
        }
        return null;
    }

    public static PolyClassContainer_v2 ofVar(ScriptContext scriptContext, String string) {
        return PolyClassContainer_v2.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
