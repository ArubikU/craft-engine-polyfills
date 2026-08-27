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

public class PolyClassBlockPistonExtendEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"length");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"block");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"direction");
    }

    public ScriptValue pg$0_length() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"length", (Object)this.instance);
    }

    public ScriptValue pg$1_block() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$2_direction() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"direction", (Object)this.instance);
    }

    public PolyClassBlockPistonExtendEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockPistonExtendEvent of(Object object) {
        return new PolyClassBlockPistonExtendEvent(object);
    }

    public static PolyClassBlockPistonExtendEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockPistonExtendEvent")) {
            return new PolyClassBlockPistonExtendEvent(object);
        }
        return null;
    }
}
