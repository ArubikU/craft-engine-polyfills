/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassItemActionEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"clicked_block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"amount");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"other_entity");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"clicked_face");
    }

    public ScriptValue pg$0_clicked_block() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"clicked_block", (Object)this.instance);
    }

    public ScriptValue pg$1_amount() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"amount", (Object)this.instance);
    }

    public ScriptValue pg$2_other_entity() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"other_entity", (Object)this.instance);
    }

    public ScriptValue pg$3_clicked_face() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"clicked_face", (Object)this.instance);
    }

    public PolyClassItemActionEvent(Object object) {
        super(object);
    }

    public static PolyClassItemActionEvent of(Object object) {
        return new PolyClassItemActionEvent(object);
    }
}
