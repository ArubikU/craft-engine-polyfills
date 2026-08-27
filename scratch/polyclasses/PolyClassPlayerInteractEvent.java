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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPlayerInteractEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerInteractEvent", (String)"clicked_block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerInteractEvent", (String)"item");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerInteractEvent", (String)"action");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerInteractEvent", (String)"hand");
    }

    public ScriptValue pg$0_clicked_block() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerInteractEvent", (String)"clicked_block", (Object)this.instance);
    }

    public ScriptValue pg$1_item() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerInteractEvent", (String)"item", (Object)this.instance);
    }

    public ScriptValue pg$2_action() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerInteractEvent", (String)"action", (Object)this.instance);
    }

    public ScriptValue pg$3_hand() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerInteractEvent", (String)"hand", (Object)this.instance);
    }

    public PolyClassPlayerInteractEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerInteractEvent of(Object object) {
        return new PolyClassPlayerInteractEvent(object);
    }

    public static PolyClassPlayerInteractEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerInteractEvent")) {
            return new PolyClassPlayerInteractEvent(object);
        }
        return null;
    }
}
