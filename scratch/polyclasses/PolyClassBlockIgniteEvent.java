/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassBlockIgniteEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockIgniteEvent", (String)"ignition_source_entity");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockIgniteEvent", (String)"cause");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockIgniteEvent", (String)"cause", (String)"S");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockIgniteEvent", (String)"block");
    }

    public ScriptValue pg$0_ignition_source_entity() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockIgniteEvent", (String)"ignition_source_entity", (Object)this.instance);
    }

    public ScriptValue pg$1_cause() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockIgniteEvent", (String)"cause", (Object)this.instance);
    }

    public String tg$2_cause() {
        if (tp$2 != null) {
            return (String)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockIgniteEvent", (String)"cause", (Object)this.instance).asStr();
    }

    public ScriptValue pg$3_block() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockIgniteEvent", (String)"block", (Object)this.instance);
    }

    public PolyClassBlockIgniteEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockIgniteEvent of(Object object) {
        return new PolyClassBlockIgniteEvent(object);
    }

    public static PolyClassBlockIgniteEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockIgniteEvent")) {
            return new PolyClassBlockIgniteEvent(object);
        }
        return null;
    }
}
