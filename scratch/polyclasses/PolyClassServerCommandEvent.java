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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassServerCommandEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"ServerCommandEvent", (String)"set_command", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"ServerCommandEvent", (String)"set_command");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ServerCommandEvent", (String)"sender_name");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ServerCommandEvent", (String)"sender_name", (String)"S");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"ServerCommandEvent", (String)"command");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ServerCommandEvent", (String)"command", (String)"S");
    }

    public boolean tm$0_set_command(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"ServerCommandEvent", (String)"set_command", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_set_command(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"ServerCommandEvent", (String)"set_command", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_sender_name() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ServerCommandEvent", (String)"sender_name", (Object)this.instance);
    }

    public String tg$3_sender_name() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ServerCommandEvent", (String)"sender_name", (Object)this.instance).asStr();
    }

    public ScriptValue pg$4_command() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ServerCommandEvent", (String)"command", (Object)this.instance);
    }

    public String tg$5_command() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ServerCommandEvent", (String)"command", (Object)this.instance).asStr();
    }

    public PolyClassServerCommandEvent(Object object) {
        super(object);
    }

    public static PolyClassServerCommandEvent of(Object object) {
        return new PolyClassServerCommandEvent(object);
    }

    public static PolyClassServerCommandEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ServerCommandEvent")) {
            return new PolyClassServerCommandEvent(object);
        }
        return null;
    }
}
