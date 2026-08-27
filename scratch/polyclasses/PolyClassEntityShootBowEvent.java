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

public class PolyClassEntityShootBowEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"EntityShootBowEvent", (String)"set_consume_item", (String)"Z:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"EntityShootBowEvent", (String)"set_consume_item");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"EntityShootBowEvent", (String)"consume_item");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityShootBowEvent", (String)"consume_item", (String)"Z");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"EntityShootBowEvent", (String)"force");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"EntityShootBowEvent", (String)"force", (String)"D");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"EntityShootBowEvent", (String)"projectile");
    }

    public boolean tm$0_set_consume_item(boolean bl) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"EntityShootBowEvent", (String)"set_consume_item", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$1_set_consume_item(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"EntityShootBowEvent", (String)"set_consume_item", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_consume_item() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityShootBowEvent", (String)"consume_item", (Object)this.instance);
    }

    public boolean tg$3_consume_item() {
        if (tp$3 != null) {
            return (Boolean)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityShootBowEvent", (String)"consume_item", (Object)this.instance).asBool();
    }

    public ScriptValue pg$4_force() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityShootBowEvent", (String)"force", (Object)this.instance);
    }

    public double tg$5_force() {
        if (tp$5 != null) {
            return (Double)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityShootBowEvent", (String)"force", (Object)this.instance).asNum();
    }

    public ScriptValue pg$6_projectile() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"EntityShootBowEvent", (String)"projectile", (Object)this.instance);
    }

    public PolyClassEntityShootBowEvent(Object object) {
        super(object);
    }

    public static PolyClassEntityShootBowEvent of(Object object) {
        return new PolyClassEntityShootBowEvent(object);
    }

    public static PolyClassEntityShootBowEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("EntityShootBowEvent")) {
            return new PolyClassEntityShootBowEvent(object);
        }
        return null;
    }

    public static PolyClassEntityShootBowEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassEntityShootBowEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
