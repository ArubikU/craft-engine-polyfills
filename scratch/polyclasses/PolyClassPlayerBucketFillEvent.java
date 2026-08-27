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

public class PolyClassPlayerBucketFillEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBucketFillEvent", (String)"block_clicked");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBucketFillEvent", (String)"item_stack");
    }

    public ScriptValue pg$0_block_clicked() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBucketFillEvent", (String)"block_clicked", (Object)this.instance);
    }

    public ScriptValue pg$1_item_stack() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBucketFillEvent", (String)"item_stack", (Object)this.instance);
    }

    public PolyClassPlayerBucketFillEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerBucketFillEvent of(Object object) {
        return new PolyClassPlayerBucketFillEvent(object);
    }
}
