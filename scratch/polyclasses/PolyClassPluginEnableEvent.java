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

public class PolyClassPluginEnableEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PluginEnableEvent", (String)"plugin_name");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PluginEnableEvent", (String)"plugin_name", (String)"S");
    }

    public ScriptValue pg$0_plugin_name() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PluginEnableEvent", (String)"plugin_name", (Object)this.instance);
    }

    public String tg$1_plugin_name() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PluginEnableEvent", (String)"plugin_name", (Object)this.instance).asStr();
    }

    public PolyClassPluginEnableEvent(Object object) {
        super(object);
    }

    public static PolyClassPluginEnableEvent of(Object object) {
        return new PolyClassPluginEnableEvent(object);
    }

    public static PolyClassPluginEnableEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PluginEnableEvent")) {
            return new PolyClassPluginEnableEvent(object);
        }
        return null;
    }

    public static PolyClassPluginEnableEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPluginEnableEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
