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

public class PolyClassBlockBreakEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockBreakEvent", (String)"set_drop_items", (String)"Z:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BlockBreakEvent", (String)"set_drop_items");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BlockBreakEvent", (String)"set_exp_to_drop", (String)"D:Z");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"BlockBreakEvent", (String)"set_exp_to_drop");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBreakEvent", (String)"drop_items");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBreakEvent", (String)"block");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBreakEvent", (String)"player");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"BlockBreakEvent", (String)"exp_to_drop");
    }

    public boolean tm$0_set_drop_items(boolean bl) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"BlockBreakEvent", (String)"set_drop_items", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$1_set_drop_items(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockBreakEvent", (String)"set_drop_items", (Object)this.instance, (List)list);
    }

    public boolean tm$2_set_exp_to_drop(double d) {
        if (h$2 != null) {
            return (Boolean)h$2.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"BlockBreakEvent", (String)"set_exp_to_drop", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$3_set_exp_to_drop(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BlockBreakEvent", (String)"set_exp_to_drop", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_drop_items() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBreakEvent", (String)"drop_items", (Object)this.instance);
    }

    public ScriptValue pg$5_block() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBreakEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$6_player() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBreakEvent", (String)"player", (Object)this.instance);
    }

    public ScriptValue pg$7_exp_to_drop() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockBreakEvent", (String)"exp_to_drop", (Object)this.instance);
    }

    public PolyClassBlockBreakEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockBreakEvent of(Object object) {
        return new PolyClassBlockBreakEvent(object);
    }
}
