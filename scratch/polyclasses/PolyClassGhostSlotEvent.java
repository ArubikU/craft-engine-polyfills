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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassGhostSlotEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"click_type");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"GhostSlotEvent", (String)"click_type", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"slot");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"GhostSlotEvent", (String)"slot", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"GhostSlotEvent", (String)"clicked_id");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"GhostSlotEvent", (String)"clicked_id", (String)"S");
    }

    public ScriptValue pg$0_click_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"click_type", (Object)this.instance);
    }

    public String tg$1_click_type() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"click_type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_slot() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"slot", (Object)this.instance);
    }

    public double tg$3_slot() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"slot", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_clicked_id() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"clicked_id", (Object)this.instance);
    }

    public String tg$5_clicked_id() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"GhostSlotEvent", (String)"clicked_id", (Object)this.instance).asStr();
    }

    public PolyClassGhostSlotEvent(Object object) {
        super(object);
    }

    public static PolyClassGhostSlotEvent of(Object object) {
        return new PolyClassGhostSlotEvent(object);
    }

    public static PolyClassGhostSlotEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("GhostSlotEvent")) {
            return new PolyClassGhostSlotEvent(object);
        }
        return null;
    }

    public static PolyClassGhostSlotEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassGhostSlotEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
