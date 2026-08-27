/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
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
import java.util.List;

public class PolyClassPlayerRespawnEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerRespawnEvent", (String)"set_respawn_location", (String)"R:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerRespawnEvent", (String)"set_respawn_location");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerRespawnEvent", (String)"respawn_location");
    }

    public boolean tm$0_set_respawn_location(ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"PlayerRespawnEvent", (String)"set_respawn_location", (Object)this.instance, (ScriptValue[])new ScriptValue[]{scriptValue}).asBool();
    }

    public ScriptValue um$1_set_respawn_location(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerRespawnEvent", (String)"set_respawn_location", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_respawn_location() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerRespawnEvent", (String)"respawn_location", (Object)this.instance);
    }

    public PolyClassPlayerRespawnEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerRespawnEvent of(Object object) {
        return new PolyClassPlayerRespawnEvent(object);
    }

    public static PolyClassPlayerRespawnEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerRespawnEvent")) {
            return new PolyClassPlayerRespawnEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerRespawnEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerRespawnEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
