/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassInventoryClickEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.PropertyHandler p$10;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"InventoryClickEvent", (String)"set_current_item", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"InventoryClickEvent", (String)"set_current_item");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"InventoryClickEvent", (String)"set_cursor", (String)"R:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"InventoryClickEvent", (String)"set_cursor");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"cursor");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"click_type");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"current_item");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"raw_slot");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"action");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"slot");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"InventoryClickEvent", (String)"who_clicked");
    }

    public boolean tm$0_set_current_item(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"InventoryClickEvent", (String)"set_current_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_current_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"InventoryClickEvent", (String)"set_current_item", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_cursor(ScriptValue scriptValue) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"InventoryClickEvent", (String)"set_cursor", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$3_set_cursor(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"InventoryClickEvent", (String)"set_cursor", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_cursor() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"cursor", (Object)this.instance);
    }

    public ScriptValue pg$5_click_type() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"click_type", (Object)this.instance);
    }

    public ScriptValue pg$6_current_item() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"current_item", (Object)this.instance);
    }

    public ScriptValue pg$7_raw_slot() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"raw_slot", (Object)this.instance);
    }

    public ScriptValue pg$8_action() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"action", (Object)this.instance);
    }

    public ScriptValue pg$9_slot() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"slot", (Object)this.instance);
    }

    public ScriptValue pg$10_who_clicked() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"InventoryClickEvent", (String)"who_clicked", (Object)this.instance);
    }

    public PolyClassInventoryClickEvent(Object object) {
        super(object);
    }

    public static PolyClassInventoryClickEvent of(Object object) {
        return new PolyClassInventoryClickEvent(object);
    }
}
