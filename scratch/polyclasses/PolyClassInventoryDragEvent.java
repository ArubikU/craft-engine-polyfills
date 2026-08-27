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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassInventoryDragEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"InventoryDragEvent", (String)"set_cursor", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"InventoryDragEvent", (String)"set_cursor");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryDragEvent", (String)"cursor");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryDragEvent", (String)"old_cursor");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryDragEvent", (String)"who_clicked");
    }

    public boolean tm$0_set_cursor(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"InventoryDragEvent", (String)"set_cursor", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_cursor(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"InventoryDragEvent", (String)"set_cursor", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_cursor() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryDragEvent", (String)"cursor", (Object)this.instance);
    }

    public ScriptValue pg$3_old_cursor() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryDragEvent", (String)"old_cursor", (Object)this.instance);
    }

    public ScriptValue pg$4_who_clicked() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryDragEvent", (String)"who_clicked", (Object)this.instance);
    }

    public PolyClassInventoryDragEvent(Object object) {
        super(object);
    }

    public static PolyClassInventoryDragEvent of(Object object) {
        return new PolyClassInventoryDragEvent(object);
    }

    public static PolyClassInventoryDragEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("InventoryDragEvent")) {
            return new PolyClassInventoryDragEvent(object);
        }
        return null;
    }
}
