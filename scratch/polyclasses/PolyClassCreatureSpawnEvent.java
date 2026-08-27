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

public class PolyClassCreatureSpawnEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"CreatureSpawnEvent", (String)"reason");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"CreatureSpawnEvent", (String)"reason", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"CreatureSpawnEvent", (String)"entity");
    }

    public ScriptValue pg$0_reason() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"CreatureSpawnEvent", (String)"reason", (Object)this.instance);
    }

    public String tg$1_reason() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"CreatureSpawnEvent", (String)"reason", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_entity() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"CreatureSpawnEvent", (String)"entity", (Object)this.instance);
    }

    public PolyClassCreatureSpawnEvent(Object object) {
        super(object);
    }

    public static PolyClassCreatureSpawnEvent of(Object object) {
        return new PolyClassCreatureSpawnEvent(object);
    }

    public static PolyClassCreatureSpawnEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("CreatureSpawnEvent")) {
            return new PolyClassCreatureSpawnEvent(object);
        }
        return null;
    }

    public static PolyClassCreatureSpawnEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassCreatureSpawnEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
