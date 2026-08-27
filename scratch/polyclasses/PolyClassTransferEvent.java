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

public class PolyClassTransferEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"mode");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"payload");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"transfer_type");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"direction");
    }

    public ScriptValue pg$0_mode() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"mode", (Object)this.instance);
    }

    public ScriptValue pg$1_payload() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"payload", (Object)this.instance);
    }

    public ScriptValue pg$2_transfer_type() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"transfer_type", (Object)this.instance);
    }

    public ScriptValue pg$3_direction() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"direction", (Object)this.instance);
    }

    public PolyClassTransferEvent(Object object) {
        super(object);
    }

    public static PolyClassTransferEvent of(Object object) {
        return new PolyClassTransferEvent(object);
    }

    public static PolyClassTransferEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("TransferEvent")) {
            return new PolyClassTransferEvent(object);
        }
        return null;
    }
}
