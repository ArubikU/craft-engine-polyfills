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
import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassContainerMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ContainerMetadata", (String)"get_item", (String)"D:R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ContainerMetadata", (String)"get_item");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ContainerMetadata", (String)"size");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContainerMetadata", (String)"size", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"ContainerMetadata", (String)"items");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"ContainerMetadata", (String)"is_empty");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ContainerMetadata", (String)"is_empty", (String)"Z");
    }

    public ScriptValue tm$0_get_item(double d) {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"ContainerMetadata", (String)"get_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)});
    }

    public ScriptValue um$1_get_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ContainerMetadata", (String)"get_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_size() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContainerMetadata", (String)"size", (Object)this.instance);
    }

    public double tg$3_size() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContainerMetadata", (String)"size", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_items() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContainerMetadata", (String)"items", (Object)this.instance);
    }

    public ScriptValue pg$5_is_empty() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContainerMetadata", (String)"is_empty", (Object)this.instance);
    }

    public boolean tg$6_is_empty() {
        if (tp$6 != null) {
            return (Boolean)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ContainerMetadata", (String)"is_empty", (Object)this.instance).asBool();
    }

    public PolyClassContainerMetadata(Object object) {
        super(object);
    }

    public static PolyClassContainerMetadata of(Object object) {
        return new PolyClassContainerMetadata(object);
    }

    public static PolyClassContainerMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ContainerMetadata")) {
            return new PolyClassContainerMetadata(object);
        }
        return null;
    }
}
