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

public class PolyClassItemActionEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"clicked_block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"amount");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ItemActionEvent", (String)"amount", (String)"D");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"other_entity");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"ItemActionEvent", (String)"clicked_face");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ItemActionEvent", (String)"clicked_face", (String)"S");
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

    public double tg$2_amount() {
        if (tp$2 != null) {
            return (Double)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"amount", (Object)this.instance).asNum();
    }

    public ScriptValue pg$3_other_entity() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"other_entity", (Object)this.instance);
    }

    public ScriptValue pg$4_clicked_face() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"clicked_face", (Object)this.instance);
    }

    public String tg$5_clicked_face() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ItemActionEvent", (String)"clicked_face", (Object)this.instance).asStr();
    }

    public PolyClassItemActionEvent(Object object) {
        super(object);
    }

    public static PolyClassItemActionEvent of(Object object) {
        return new PolyClassItemActionEvent(object);
    }

    public static PolyClassItemActionEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ItemActionEvent")) {
            return new PolyClassItemActionEvent(object);
        }
        return null;
    }
}
