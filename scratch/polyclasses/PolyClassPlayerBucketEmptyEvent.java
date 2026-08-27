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

public class PolyClassPlayerBucketEmptyEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBucketEmptyEvent", (String)"block_clicked");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBucketEmptyEvent", (String)"item_stack");
    }

    public ScriptValue pg$0_block_clicked() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBucketEmptyEvent", (String)"block_clicked", (Object)this.instance);
    }

    public ScriptValue pg$1_item_stack() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBucketEmptyEvent", (String)"item_stack", (Object)this.instance);
    }

    public PolyClassPlayerBucketEmptyEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerBucketEmptyEvent of(Object object) {
        return new PolyClassPlayerBucketEmptyEvent(object);
    }

    public static PolyClassPlayerBucketEmptyEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerBucketEmptyEvent")) {
            return new PolyClassPlayerBucketEmptyEvent(object);
        }
        return null;
    }
}
