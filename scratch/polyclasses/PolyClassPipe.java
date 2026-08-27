/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPipe {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"pos");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"x");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"y");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_pipe");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"z");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"block");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_gas_pipe");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Pipe", (String)"is_fluid_pipe");
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

    public ScriptValue pg$2_y() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"y", (Object)this.instance);
    }

    public ScriptValue pg$3_is_pipe() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_pipe", (Object)this.instance);
    }

    public ScriptValue pg$4_z() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"z", (Object)this.instance);
    }

    public ScriptValue pg$5_block() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$6_is_gas_pipe() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_gas_pipe", (Object)this.instance);
    }

    public ScriptValue pg$7_is_fluid_pipe() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Pipe", (String)"is_fluid_pipe", (Object)this.instance);
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
}
