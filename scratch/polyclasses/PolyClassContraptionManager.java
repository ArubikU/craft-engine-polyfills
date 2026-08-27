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
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler3
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler5
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassContraptionManager {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler3 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.TypedMethodHandler0 h$4;
    private static volatile PolyType.MethodHandler m$5;
    private static volatile PolyType.TypedMethodHandler1 h$6;
    private static volatile PolyType.MethodHandler m$7;
    private static volatile PolyType.TypedMethodHandler5 h$8;
    private static volatile PolyType.MethodHandler m$9;
    private static volatile PolyType.TypedMethodHandler2 h$10;
    private static volatile PolyType.MethodHandler m$11;
    private static volatile PolyType.TypedMethodHandler1 h$12;
    private static volatile PolyType.MethodHandler m$13;
    private static volatile PolyType.PropertyHandler p$14;
    private static volatile PolyType.PropertyHandler p$15;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler3)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"create_bearing", (String)"RRS:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"create_bearing");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"disassemble", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"disassemble");
        h$4 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"kill_all", (String)":D");
        m$5 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"kill_all");
        h$6 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"get", (String)"S:R");
        m$7 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"get");
        h$8 = (PolyType.TypedMethodHandler5)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"create", (String)"RDDDS:R");
        m$9 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"create");
        h$10 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"create_at", (String)"RS:R");
        m$11 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"create_at");
        h$12 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ContraptionManager", (String)"kill", (String)"R:Z");
        m$13 = PolyClassRuntime.resolveMethodHandler((String)"ContraptionManager", (String)"kill");
        p$14 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionManager", (String)"all_contraptions");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"ContraptionManager", (String)"count");
    }

    public ScriptValue tm$0_create_bearing(ScriptValue scriptValue, ScriptValue scriptValue2, String string) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)scriptValue, (Object)scriptValue2, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"create_bearing", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, scriptValue2, ScriptValue.of((String)string)});
    }

    public ScriptValue um$1_create_bearing(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"create_bearing", (Object)this.instance, (List)list);
    }

    public boolean tm$2_disassemble(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"disassemble", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_disassemble(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"disassemble", (Object)this.instance, (List)list);
    }

    public double tm$4_kill_all() {
        if (h$4 != null) {
            return (Double)h$4.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"kill_all", (Object)this.instance, (ScriptValue[])new ScriptValue[0]).asNum();
    }

    public ScriptValue um$5_kill_all(List list) {
        if (m$5 != null) {
            return m$5.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"kill_all", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$6_get(String string) {
        if (h$6 != null) {
            return (ScriptValue)h$6.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$7_get(List list) {
        if (m$7 != null) {
            return m$7.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"get", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$8_create(ScriptValue scriptValue, double d, double d2, double d3, String string) {
        if (h$8 != null) {
            return (ScriptValue)h$8.call(this.instance, (Object)scriptValue, (Object)d, (Object)d2, (Object)d3, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"create", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((double)d), ScriptValue.of((double)d2), ScriptValue.of((double)d3), ScriptValue.of((String)string)});
    }

    public ScriptValue um$9_create(List list) {
        if (m$9 != null) {
            return m$9.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"create", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$10_create_at(ScriptValue scriptValue, String string) {
        if (h$10 != null) {
            return (ScriptValue)h$10.call(this.instance, (Object)scriptValue, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"create_at", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue, ScriptValue.of((String)string)});
    }

    public ScriptValue um$11_create_at(List list) {
        if (m$11 != null) {
            return m$11.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"create_at", (Object)this.instance, (List)list);
    }

    public boolean tm$12_kill(ScriptValue scriptValue) {
        if (h$12 != null) {
            return (Boolean)h$12.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"ContraptionManager", (String)"kill", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$13_kill(List list) {
        if (m$13 != null) {
            return m$13.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContraptionManager", (String)"kill", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$14_all_contraptions() {
        if (p$14 != null) {
            return p$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionManager", (String)"all_contraptions", (Object)this.instance);
    }

    public ScriptValue pg$15_count() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContraptionManager", (String)"count", (Object)this.instance);
    }

    public PolyClassContraptionManager(Object object) {
        this.instance = object;
    }

    public static PolyClassContraptionManager of(Object object) {
        return new PolyClassContraptionManager(object);
    }

    public static PolyClassContraptionManager ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ContraptionManager")) {
            return new PolyClassContraptionManager(object);
        }
        return null;
    }
}
