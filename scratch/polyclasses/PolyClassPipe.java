/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassPipe {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.TypedPropertyHandler tp$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.PropertyHandler p$10;
    private static volatile PolyType.TypedPropertyHandler tp$11;
    private static volatile PolyType.PropertyHandler p$12;
    private static volatile PolyType.TypedPropertyHandler tp$13;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"pos");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"x");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"x", (String)"D");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"y");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"y", (String)"D");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_pipe");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"is_pipe", (String)"Z");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"z");
        tp$8 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"z", (String)"D");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"block");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_gas_pipe");
        tp$11 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"is_gas_pipe", (String)"Z");
        p$12 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_fluid_pipe");
        tp$13 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Pipe", (String)"is_fluid_pipe", (String)"Z");
    }

    public ScriptValue pg$0_pos() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"pos", (Object)this.instance);
    }

    public ScriptValue pg$1_x() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"x", (Object)this.instance);
    }

    public double tg$2_x() {
        if (tp$2 != null) {
            return (Double)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"x", (Object)this.instance).asNum();
    }

    public ScriptValue pg$3_y() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"y", (Object)this.instance);
    }

    public double tg$4_y() {
        if (tp$4 != null) {
            return (Double)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"y", (Object)this.instance).asNum();
    }

    public ScriptValue pg$5_is_pipe() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_pipe", (Object)this.instance);
    }

    public boolean tg$6_is_pipe() {
        if (tp$6 != null) {
            return (Boolean)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_pipe", (Object)this.instance).asBool();
    }

    public ScriptValue pg$7_z() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"z", (Object)this.instance);
    }

    public double tg$8_z() {
        if (tp$8 != null) {
            return (Double)tp$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"z", (Object)this.instance).asNum();
    }

    public ScriptValue pg$9_block() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$10_is_gas_pipe() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_gas_pipe", (Object)this.instance);
    }

    public boolean tg$11_is_gas_pipe() {
        if (tp$11 != null) {
            return (Boolean)tp$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_gas_pipe", (Object)this.instance).asBool();
    }

    public ScriptValue pg$12_is_fluid_pipe() {
        if (p$12 != null) {
            return p$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_fluid_pipe", (Object)this.instance);
    }

    public boolean tg$13_is_fluid_pipe() {
        if (tp$13 != null) {
            return (Boolean)tp$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_fluid_pipe", (Object)this.instance).asBool();
    }

    public PolyClassPipe(Object object) {
        this.instance = object;
    }

    public static PolyClassPipe of(Object object) {
        return new PolyClassPipe(object);
    }

    public static PolyClassPipe ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Pipe")) {
            return new PolyClassPipe(object);
        }
        return null;
    }

    public static PolyClassPipe ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPipe.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
