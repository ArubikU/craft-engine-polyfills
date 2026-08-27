/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPluginEnableEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PluginEnableEvent", (String)"plugin_name");
    }

    public ScriptValue pg$0_plugin_name() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PluginEnableEvent", (String)"plugin_name", (Object)this.instance);
    }

    public PolyClassPluginEnableEvent(Object object) {
        super(object);
    }

    public static PolyClassPluginEnableEvent of(Object object) {
        return new PolyClassPluginEnableEvent(object);
    }
}
