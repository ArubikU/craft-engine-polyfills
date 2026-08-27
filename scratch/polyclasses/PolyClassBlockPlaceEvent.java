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

public class PolyClassBlockPlaceEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"can_build");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockPlaceEvent", (String)"can_build", (String)"Z");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"block_placed_against");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"block");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPlaceEvent", (String)"player");
    }

    public ScriptValue pg$0_can_build() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"can_build", (Object)this.instance);
    }

    public boolean tg$1_can_build() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"can_build", (Object)this.instance).asBool();
    }

    public ScriptValue pg$2_block_placed_against() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"block_placed_against", (Object)this.instance);
    }

    public ScriptValue pg$3_block() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$4_player() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPlaceEvent", (String)"player", (Object)this.instance);
    }

    public PolyClassBlockPlaceEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockPlaceEvent of(Object object) {
        return new PolyClassBlockPlaceEvent(object);
    }

    public static PolyClassBlockPlaceEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockPlaceEvent")) {
            return new PolyClassBlockPlaceEvent(object);
        }
        return null;
    }
}
