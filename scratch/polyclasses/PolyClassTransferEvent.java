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

public class PolyClassTransferEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"mode");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"TransferEvent", (String)"mode", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"payload");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"transfer_type");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"TransferEvent", (String)"transfer_type", (String)"S");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"TransferEvent", (String)"direction");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"TransferEvent", (String)"direction", (String)"S");
    }

    public ScriptValue pg$0_mode() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"mode", (Object)this.instance);
    }

    public String tg$1_mode() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"mode", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_payload() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"payload", (Object)this.instance);
    }

    public ScriptValue pg$3_transfer_type() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"transfer_type", (Object)this.instance);
    }

    public String tg$4_transfer_type() {
        if (tp$4 != null) {
            return (String)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"transfer_type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$5_direction() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"direction", (Object)this.instance);
    }

    public String tg$6_direction() {
        if (tp$6 != null) {
            return (String)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TransferEvent", (String)"direction", (Object)this.instance).asStr();
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

    public static PolyClassTransferEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassTransferEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
