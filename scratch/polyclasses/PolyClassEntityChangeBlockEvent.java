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

public class PolyClassEntityChangeBlockEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"EntityChangeBlockEvent", (String)"block");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"EntityChangeBlockEvent", (String)"to");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityChangeBlockEvent", (String)"to", (String)"S");
    }

    public ScriptValue pg$0_block() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityChangeBlockEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$1_to() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityChangeBlockEvent", (String)"to", (Object)this.instance);
    }

    public String tg$2_to() {
        if (tp$2 != null) {
            return (String)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityChangeBlockEvent", (String)"to", (Object)this.instance).asStr();
    }

    public PolyClassEntityChangeBlockEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityChangeBlockEvent of(Object object) {
        return new PolyClassEntityChangeBlockEvent(object);
    }

    public static PolyClassEntityChangeBlockEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityChangeBlockEvent")) {
            return new PolyClassEntityChangeBlockEvent(object);
        }
        return null;
    }
}
